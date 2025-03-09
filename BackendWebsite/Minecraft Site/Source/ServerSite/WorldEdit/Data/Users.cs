//  Fairfield Tek L.L.C.
//  Copyright (c) 2016, Fairfield Tek L.L.C.


using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Data.SqlClient;
using Newtonsoft.Json;
using ServerSite.DataAccess;
using ServerSite.DataContracts.BlockBank;
using ServerSite.DataContracts.WorldEdit;

namespace ServerSite.WorldEdit.Data;

public static class Users
{
    public static SqlHelper SqlHelper = new(Constants.DataSourceType);


   // private static readonly int TimeOut = 120000;


    public static string FixGuid(this Guid guid)
    {
        return $"{{{guid.ToString().ToUpper()}}}";
    }

    public static Guid AsGuid(this object guid)
    {
        return guid switch
        {
            Guid guid1 => guid1,
            string guid2 => new Guid(guid2),
            _ => throw new Exception("Guid is invalid")
        };
    }

    public static async Task<string> AuthUser(string uuid)
    {
        string lastAuth = GetNewAuth();
        string emailAddress = await GetEmail(uuid);
        if (emailAddress == null)
            return "";
        int id = GetUserId(uuid);
        if (id == -1)
            return "";
        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "update Users set LastAuth = @Auth, LastAuthDt = @LastAuthDt WHERE PkUserId = @UserId;", [
                new KeyValuePair<string, object>("@Auth", lastAuth),
                new KeyValuePair<string, object>("@UserId", id),
                new KeyValuePair<string, object>("@LastAuthDt", DateTime.Now)
            ]);
        //await EmailMessages.SendAuthCode(emailAddress, lastAuth);
        return lastAuth;
    }

    public static async Task<bool> BulkLoad(int schematicId, string data, string rootPath)
    {
        int nextSeq;

        object o = await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select max(Seq) from SchematicData where FkSchematicId = @SID", [
                new KeyValuePair<string, object>("@SID", schematicId)
            ]);

        if (o == null)
            nextSeq = 0;
        else
            nextSeq = (int) o;


        nextSeq += 1;
        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "insert into SchematicData (FkSchematicId, Seq, BlockData ) select @SID, @SEQ, @binary", [
                new KeyValuePair<string, object>("@SID", schematicId),
                new KeyValuePair<string, object>("@SEQ", nextSeq),
                new KeyValuePair<string, object>("@binary", data)
            ]);
        return true;
    }


    public static async Task<Tuple<bool, string>> CreateFolder(int userid, string path, string foldername)
    {
        foldername = foldername.Trim();
        if (!foldername.All(char.IsLetterOrDigit))
            return new Tuple<bool, string>(false, "Only Letters and Numbers are allowed for Directory Names.");
        if (foldername.Length > 50)
            return new Tuple<bool, string>(false, "Max folder name is 50 characters.");

        int parentFolder = await FindFolderFromPath(userid, path);


        int chk = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select count(*) from Directory where DirectoryName = @DirectoryName and FkParentId = @Parent and IsDeleted = 0;",
            [
                new KeyValuePair<string, object>("@DirectoryName", foldername),
                new KeyValuePair<string, object>("@Parent", parentFolder)
            ]);
        if (chk > 0)
            return new Tuple<bool, string>(false, "Folder already exists.");


        chk = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select count(*) from Schematic where Name = @DirectoryName and FkDirectoryId = @Parent and IsDeleted=0;",
            [
                new KeyValuePair<string, object>("@DirectoryName", foldername),
                new KeyValuePair<string, object>("@Parent", parentFolder)
            ]);
        if (chk > 0)
            return new Tuple<bool, string>(false, "Schematic exists with folder name.");


        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "Insert into Directory (DirectoryName,FkParentId,FkUserId, IsDeleted) values (@DirectoryName, @FkParentId, @FkUserId, 0);",
            [
                new KeyValuePair<string, object>("@DirectoryName", foldername),
                new KeyValuePair<string, object>("@FkParentId", parentFolder),
                new KeyValuePair<string, object>("@FkUserId", userid)
            ]);

        return new Tuple<bool, string>(true, "Folder created.");
    }

    public static async Task<Tuple<bool, string>> Delete(int userId, string path, string foldername)
    {
        int sid = -1;
        //int fid = FindFolderFromPath(userId, path);

        try
        {
            sid = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
                "select pkSchematicId from Schematic where Name = @name and fkOwnerId = @Owner and IsDeleted = 0;",
                [
                    new KeyValuePair<string, object>("@name", foldername),
                    new KeyValuePair<string, object>("@Owner", userId)
                ]);
        }
        catch (Exception)
        {
            //
        }


        if (sid != -1)
        {
            await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                "update Schematic set IsDeleted = 1 where PkSchematicId = @Sid", [
                    new KeyValuePair<string, object>("@Sid", sid)
                ]);

            return new Tuple<bool, string>(true, "Deleted File " + foldername + ".");
        }

        int folderToDelete = await FindFolderFromPath(userId, path + "/" + foldername);

        if (folderToDelete == -1)
            return new Tuple<bool, string>(false, "Cannot Find Folder " + foldername + ".");


        int chk = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "Select Count(*) from Schematic where FkDirectoryId = @DirectoryId and IsDeleted = 0;", [
                new KeyValuePair<string, object>("@DirectoryId", folderToDelete)
            ]);
        if (chk > 0)
            return new Tuple<bool, string>(false, "Cannot delete Directory (Schematics Exist).");

        chk = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "Select Count(*) from Directory where FkParentId = @DirectoryId and IsDeleted = 0;", [
                new KeyValuePair<string, object>("@DirectoryId", folderToDelete)
            ]);
        if (chk > 0)
            return new Tuple<bool, string>(false, "Cannot delete Directory (Directories Exist).");

        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "update Directory set IsDeleted = 1 where PkDirectoryId = @Id",
            [
                new KeyValuePair<string, object>("@Id", folderToDelete)
            ]);

        return new Tuple<bool, string>(true, "Deleted Folder " + foldername + ".");
    }

    public static async Task<int> FindFolderFromPath(int userId, string path)
    {
        string[] parts = path.Split("/");

        int rootFolderId = (await GetHomeFolder(userId)).Item1;

        foreach (string part in parts)
        {
            if (part.Equals("..") || string.IsNullOrEmpty(part))
                continue;

            try
            {
                rootFolderId = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
                    "Select PkDirectoryId from Directory where FkParentId = @FkParentId and DirectoryName = @DirectoryName and FkUserId = @UserId and isDeleted = 0",
                    [
                        new KeyValuePair<string, object>("@FkParentId", rootFolderId),
                        new KeyValuePair<string, object>("@DirectoryName", part.Trim()),
                        new KeyValuePair<string, object>("@UserId", userId)
                    ]);
                return rootFolderId;
            }
            catch (Exception)
            {
                return -1;
            }
        }

        return rootFolderId;
    }

    /// <summary>
    ///     Get email address registered to a Minecraft UUID
    /// </summary>
    /// <param name="uuid"></param>
    /// <returns></returns>
    public static async Task<string> GetEmail(string uuid)
    {
        try
        {
            return (string) await SqlHelper.ExecuteScalar(Constants.DbConnString,
                "Select EmailAddress from Users where UniqueId = @UserId;", [
                    new KeyValuePair<string, object>("@UserId", uuid)
                ]);
        }
        catch (Exception)
        {
            //
        }

        return null;
    }

    public static async Task<List<DirectoryElement>> GetFolderContents(int userId, string path)
    {
        int folderId = await FindFolderFromPath(userId, path);


        var result = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "Select DirectoryName from Directory where FkParentId = @FkParentId and FkUserId = @FkUserId and isDeleted =0",
            [
                new KeyValuePair<string, object>("@FkUserId", userId),
                new KeyValuePair<string, object>("@FkParentId", folderId)
            ]);


        var directories = result.Select(dat => new DirectoryElement
            {ElementType = Constants.DirectoryEntry.Directory, BlockCount = 0, Name = (string) dat[0]}).ToList();


        result = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "Select Name, NumberOfBlocks from Schematic Where FkDirectoryId = @FkDirectoryId and isDeleted = 0;",
            [
                new KeyValuePair<string, object>("@FkDirectoryId", folderId)
            ]);


        directories.AddRange(result.Select(dat => new DirectoryElement
            {ElementType = Constants.DirectoryEntry.Schematic, BlockCount = (int) dat[1], Name = (string) dat[0]}));


        return directories;
    }

    public static async Task<Tuple<int, string>> GetHomeFolder(int userId)
    {
        var result = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "select PkDirectoryId, DirectoryName from Directory where FkParentId is NULL and FkUserId = @FkUserId and isDeleted = 0;",
            [
                new KeyValuePair<string, object>("@FkUserId", userId)
            ]);

        return result.Count > 0 ? new Tuple<int, string>((int) result[0][0], (string) result[0][1]) : null;
    }

    private static string GetNewAuth()
    {
        return Guid.NewGuid().ToString("N").Substring(27).ToUpper().Replace("O", "0");
    }


    public static int GetUserId(string uuid)
    {
        object r = SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select PkUserId from Users where UniqueId = @UUID;", [
                new KeyValuePair<string, object>("@UUID", uuid)
            ]);
        if (r == null)
            return -1;
        return (int) r;
    }

    public static async
        Task<Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>>
        LoadSchematic(int userId, string path, string filename)
    {
        int targetFolderId = await FindFolderFromPath(userId, path);
        int schematicId = -1;
        if (targetFolderId == -1)
            return new
                Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>(
                    false, "Target folder does not exist.", "", new List<PaletteEntry>(),
                    new List<PaletteEntry>(), new List<PaletteEntry>(), 0);

        List<PaletteEntry> blockDataPalette = null;
        List<PaletteEntry> blockTypePalette = null;
        List<PaletteEntry> blockInvePalette = null;
        //string name;
        int blockCount = 0;


        var result = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "Select PkSchematicId, BlockDataJson, BlockTypeJson,name,BlockInveJson,NumberOfBlocks  from Schematic where Name=@Name and FkDirectoryId = @FkDirectoryId and IsDeleted = 0 ",
            [
                new KeyValuePair<string, object>("@Name", filename),
                new KeyValuePair<string, object>("@FkDirectoryId", targetFolderId)
            ]);

        if (result.Count > 0)
        {
            schematicId = (int) result[0][0]; //    rdr.GetInt32(0);
            blockDataPalette = JsonConvert.DeserializeObject<List<PaletteEntry>>(
                    (string) result[0][1] //rdr.GetString(1)
                )
                .Where(x => x != null).ToList();
            blockTypePalette = JsonConvert.DeserializeObject<List<PaletteEntry>>(
                    (string) result[0][2] // rdr.GetString(2)
                )
                .Where(x => x != null).ToList();
            blockInvePalette = JsonConvert.DeserializeObject<List<PaletteEntry>>(
                    (string) result[0][4] //rdr.GetString(4)
                )
                .Where(x => x != null).ToList();
            // name = (string)result[0][3];// rdr.GetString(3);
            blockCount = (int) result[0][5]; // rdr.GetInt32(5);
        }


        if (schematicId == -1)
            return new
                Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>(
                    false, "Unknown Filename", "", new List<PaletteEntry>(),
                    new List<PaletteEntry>(), new List<PaletteEntry>(), 0);

        var dat = new StringBuilder();


        result = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "SELECT BlockData from SchematicData where FkSchematicId = @FkSchematicId order by  Seq;", [
                new KeyValuePair<string, object>("@FkSchematicId", schematicId)
            ]);

        foreach (var rows in result) dat.Append((string) rows[0]);

        return new
            Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>(
                true, "Blocks Loaded", dat.ToString(), blockDataPalette, blockTypePalette, blockInvePalette,
                blockCount);
    }


    public static async Task<string> Login(string uuid, string authToken, string wid = null)
    {
        var worldId = Guid.Empty;
        if (!string.IsNullOrEmpty(wid))
            worldId = Guid.Parse(wid);

        if (string.IsNullOrEmpty(authToken)) throw new Exception("AUTH TOKEN BLANK!");

        int id = -1;


        object r = await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select PkUserId from Users where UniqueId=@UniqueId and LastAuth = @Auth;", [
                new KeyValuePair<string, object>("@UniqueId", uuid),
                new KeyValuePair<string, object>("@Auth", authToken)
            ]);
        if (r != null) id = (int) r;

        if (id == -1)
            return "";

        string lastAuth = GetNewAuth();
        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "UPDATE Users SET LastAuth = @Auth, LastAuthDt = @LastAuthDt,Confirmed = 1 WHERE PkUserId = @UserId;", [
                new KeyValuePair<string, object>("@Auth", lastAuth),
                new KeyValuePair<string, object>("@UserId", id),
                new KeyValuePair<string, object>("@LastAuthDt", DateTime.Now)
            ]);


        if (worldId == Guid.Empty)
            return lastAuth;

        bool found = false;
        {
            object o = await SqlHelper.ExecuteScalar(Constants.DbConnString,
                "select LastDt from WorldUsers where fkWorldId = @fkWorldId and fkUserId= @fkUserId", [
                    new KeyValuePair<string, object>("@fkWorldId", worldId.FixGuid()),
                    new KeyValuePair<string, object>("@fkUserId", id)
                ]);

            if (o != null) found = true;
        }
        if (found)
            await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                "Update WorldUsers set LastDt = @Date where fkUserId= @fkUserId and fkWorldId = @fkWorldId", [
                    new KeyValuePair<string, object>("@fkUserId", id),
                    new KeyValuePair<string, object>("@Date", DateTime.Now),
                    new KeyValuePair<string, object>("@fkWorldId", worldId.FixGuid())
                ]);
        else
            await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                "INSERT INTO WorldUsers ( fkWorldId , fkUserId , LastDt ) select @w , @u , getdate();", [
                    new KeyValuePair<string, object>("@w", worldId.FixGuid()),
                    new KeyValuePair<string, object>("@u", id)
                ]);


        return lastAuth;
    }

    public static async Task<string> RegisterNewUser(string uuid, string emailAddress)
    {
        string lastAuth = GetNewAuth();

        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "Insert into Users (UniqueId, EmailAddress, Confirmed,LastAuth) values (@UniqueId,@EmailAddress,0,@Auth);",
            [
                new KeyValuePair<string, object>("@UniqueId", uuid),
                new KeyValuePair<string, object>("@EmailAddress", emailAddress),
                new KeyValuePair<string, object>("@Auth", lastAuth)
            ]);


        int id = GetUserId(uuid);
        if (id == -1)
            return "";

        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "Insert into Directory(DirectoryName, FkParentId, FkUserId,IsDeleted) values (@DirectoryName,null,@FkUserId,0);",
            [
                new KeyValuePair<string, object>("@DirectoryName", ".."),
                new KeyValuePair<string, object>("@FkUserId", id)
            ]);

        return lastAuth;

        //await EmailMessages.SendAuthCode(emailAddress, lastAuth);
    }


    public static async Task<int> CheckIfFileFolderExist(string filename, int targetFolderId)
    {
        int count = (int) await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select count(*) from Directory where DirectoryName = @DirectoryName and FkParentId = @Parent and isDeleted = 0;",
            [
                new KeyValuePair<string, object>("@DirectoryName", filename),
                new KeyValuePair<string, object>("@Parent", targetFolderId)
            ]);

        return count;
    }

    public static async Task<long> CheckIfFileExists(string filename, int targetFolderId)
    {
        object count = await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select count(*) from Schematic where Name = @DirectoryName and FkDirectoryId = @Parent and isDeleted=0;",
            [
                new KeyValuePair<string, object>("@DirectoryName", filename),
                new KeyValuePair<string, object>("@Parent", targetFolderId)
            ]);
        if (count != null)
            return (int) count;
        return 0;
    }


    public static async Task<Tuple<bool, string, int>> SaveSchematic(int userId, string path, string filename,
        SchematicDataRequest schematicDataRequest, string rootPath)
    {
        int schematicId = -1;
        try
        {
            int targetFolderId = await FindFolderFromPath(userId, path);
            if (targetFolderId == -1)
                return new Tuple<bool, string, int>(false, "Target folder does not exist.", -1);


            if (await CheckIfFileFolderExist(filename, targetFolderId) > 0)
                return new Tuple<bool, string, int>(false, "Directory exists with filename already exists.", -1);


            if (await CheckIfFileExists(filename, targetFolderId) > 0)
                return new Tuple<bool, string, int>(false, "Schematic exists with filename.", -1);


            await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                "Insert into Schematic (Name, FkOwnerId, FkDirectoryId, IsDeleted, BlockDataJson, BlockTypeJson,BlockInveJson,NumberOfBlocks) values (@Name, @FkOwnerId, @FkDirectoryId,0,@BlockDataJson,@BlockTypeJson,@BlockInveJson,@NumberOfBlocks);",
                [
                    new KeyValuePair<string, object>("@Name", schematicDataRequest.FileName),
                    new KeyValuePair<string, object>("@FkOwnerId", userId),
                    new KeyValuePair<string, object>("@FkDirectoryId", targetFolderId),
                    new KeyValuePair<string, object>("@BlockDataJson",
                        JsonConvert.SerializeObject(schematicDataRequest.BlockDataPalette)),
                    new KeyValuePair<string, object>("@BlockTypeJson",
                        JsonConvert.SerializeObject(schematicDataRequest.BlockTypePalette)),
                    new KeyValuePair<string, object>("@BlockInveJson",
                        JsonConvert.SerializeObject(schematicDataRequest.BlockInvePalette)),
                    new KeyValuePair<string, object>("@NumberOfBlocks", schematicDataRequest.TotalNumberOfBlocks)
                ]);


            object result = await SqlHelper.ExecuteScalar(Constants.DbConnString,
                "Select PkSchematicId from Schematic where Name = @Name and FkOwnerId=@FkOwnerId and FkDirectoryId = @FkDirectoryId and IsDeleted=0;",
                [
                    new KeyValuePair<string, object>("@Name", schematicDataRequest.FileName),
                    new KeyValuePair<string, object>("@FkOwnerId", userId),
                    new KeyValuePair<string, object>("@FkDirectoryId", targetFolderId)
                ]);

            if (result != null) schematicId = (int) result;


            void LoadEm(object callback)
            {
                _ = BulkLoad(schematicId, schematicDataRequest.Blocks, rootPath).GetAwaiter().GetResult();
            }

            ThreadPool.QueueUserWorkItem(LoadEm);
        }
        catch (Exception e)
        {
            Debug.WriteLine(e);
        }

        return new Tuple<bool, string, int>(true, "", schematicId);
    }


    public static async Task<Guid> RegisterServer(string ipAddress, string latestVersion, string serverName,
        string worldId)
    {
        serverName = serverName.Replace("'", "");

        Guid? wid = null;
        try
        {
            wid = Guid.Parse(worldId);
        }
        catch (Exception )
        {
            //
        }

        if (wid != null)
        {
            object result = await SqlHelper.ExecuteScalar(Constants.DbConnString,
                "select pkWorldId from Worlds where w.pkWorldId = @worldId", [
                    new KeyValuePair<string, object>("@worldId", wid.Value.FixGuid())
                ]);
            if (result != null)
                return wid.Value;
        }

        var ServerId = Guid.Empty;


        var rdr = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "select pkWorldId from Worlds where IPAddress = @ipAddress and ServerName=@serverName", [
                new KeyValuePair<string, object>("@ipAddress", ipAddress.Trim()),
                new KeyValuePair<string, object>("@serverName", serverName)
            ]);
        if (rdr.Count > 0) ServerId = rdr[0][0].AsGuid();

        if (ServerId != Guid.Empty)
            return ServerId;
        ServerId = Guid.NewGuid();
        //New Registration

        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "insert into Worlds ( pkWorldId , IPAddress , LastVersion, ServerName, LastUpdated) select @w , @i, @l, @s, getdate()",
            [
                new KeyValuePair<string, object>("@w", ServerId.FixGuid()),
                new KeyValuePair<string, object>("@i", ipAddress.Trim()),
                new KeyValuePair<string, object>("@l", latestVersion),
                new KeyValuePair<string, object>("@s", serverName)
            ]);

        return ServerId;
    }


    public static async Task<bool> DepositMaterial(int userId, string wid, string material, int count)
    {
        var vWid = Guid.Parse(wid);
        var BlockBankId = await GetBlockBank(userId, vWid);

        var MaterialId = Guid.Empty;


        object result = await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select pkId from BlockBankContents where fkBlockBankId = @BBID and Material = @Material", [
                new KeyValuePair<string, object>("@BBID", BlockBankId.FixGuid()),
                new KeyValuePair<string, object>("Material", material)
            ]);

        if (result != null) MaterialId = result.AsGuid();

        if (MaterialId == Guid.Empty)
        {
            await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                "insert into BlockBankContents (pkId, fkBlockBankId, Material, Amount) values (@MatId, @BBID, @Material,@Amount)",
                [
                    new KeyValuePair<string, object>("@BBID", BlockBankId.FixGuid()),
                    new KeyValuePair<string, object>("@Material", material),
                    new KeyValuePair<string, object>("@MatId", Guid.NewGuid().FixGuid()),
                    new KeyValuePair<string, object>("@Amount", count)
                ]);
            return true;
        }

        int amount = 0;

        result = await SqlHelper.ExecuteScalar(Constants.DbConnString,
            "select Amount from BlockBankContents where pkId = @Id", [
                new KeyValuePair<string, object>("@Id", MaterialId.FixGuid())
            ]);

        if (result != null) amount = (int) result;

        amount = amount + count;

        await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
            "update BlockBankContents set Amount = @amt where pkId = @id", [
                new KeyValuePair<string, object>("@Id", MaterialId.FixGuid()),
                new KeyValuePair<string, object>("@amt", amount)
            ]);

        return true;
    }

    public static async Task<List<BlockBankInventoryItem>> GetBlockBankInventory(int userId, string wid,
        string criteria)
    {
        var vwid = Guid.Parse(wid);

        var result = new List<BlockBankInventoryItem>();
        var blockBankId = await GetBlockBank(userId, vwid);

        List<List<object>> qresult;


        if (criteria == string.Empty)
            qresult = await SqlHelper.ExecuteReader(Constants.DbConnString,
                "select  Material, Amount from BlockBankContents where fkBlockBankId = @BBID", [
                    new KeyValuePair<string, object>("@BBID", blockBankId.FixGuid())
                ]);
        else
            qresult = await SqlHelper.ExecuteReader(Constants.DbConnString,
                "select  Material, Amount from BlockBankContents where fkBlockBankId = @BBID and Material like @Mat", [
                    new KeyValuePair<string, object>("@BBID", blockBankId.FixGuid()),
                    new KeyValuePair<string, object>("@Mat", criteria)
                ]);

        if (qresult.Count <= 0)
            return result;
        result.AddRange(qresult.Select(row => new BlockBankInventoryItem
            {MaterialName = row[0] as string, Count = (int) row[1]}));
        return result;
    }


    public static async Task<Guid> GetBlockBank(int userId, Guid wid)
    {
        var BlockBankId = Guid.Empty;
        using (var con = new SqlConnection())
        {
            object result = await SqlHelper.ExecuteScalar(Constants.DbConnString,
                "select pkBlockBankId from BlockBank where fkUserId = @fkUserId and fkWorldId = @fkWorldId", [
                    new KeyValuePair<string, object>("@fkUserId", userId),
                    new KeyValuePair<string, object>("@fkWorldId", wid.FixGuid())
                ]);
            if (result != null) BlockBankId = result.AsGuid();


            if (BlockBankId != Guid.Empty) return BlockBankId;
            {
                BlockBankId = Guid.NewGuid();

                await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                    "Insert into BlockBank (pkBlockBankId,fkUserId,fkWorldId) select @bb, @u, @w;", [
                        new KeyValuePair<string, object>("@bb", BlockBankId.FixGuid()),
                        new KeyValuePair<string, object>("@u", userId),
                        new KeyValuePair<string, object>("@w", wid.FixGuid())
                    ]);
            }
        }

        return BlockBankId;
    }


    public static async Task<int> WithDrawlMaterial(int userId, string wid, string material, int amount)
    {
        var vwid = Guid.Parse(wid);
        var BlockBankId = await GetBlockBank(userId, vwid);

        var Id = Guid.Empty;
        int available = 0;


        var rdr = await SqlHelper.ExecuteReader(Constants.DbConnString,
            "select pkId,Amount from BlockBankContents where fkBlockBankId = @BBID and Material = @Material", [
                new KeyValuePair<string, object>("@BBID", BlockBankId.FixGuid()),
                new KeyValuePair<string, object>("@Material", material)
            ]);
        if (rdr.Count > 0)
        {
            Id = rdr[0][0].AsGuid();
            available = (int) rdr[0][1];
        }

        int leftOver = 0;

        if (available <= amount)
        {
            leftOver = 0;
            amount = available;
        }
        else
        {
            leftOver = available - amount;
        }

        if (leftOver == 0)
            await SqlHelper.ExecuteNonQuery(Constants.DbConnString, "delete  BlockBankContents where pkId = @ID", [
                new KeyValuePair<string, object>("@ID", Id.FixGuid())
            ]);
        else
            await SqlHelper.ExecuteNonQuery(Constants.DbConnString,
                "update  BlockBankContents set Amount = @amt  where pkId = @ID", [
                    new KeyValuePair<string, object>("@ID", Id.FixGuid()),
                    new KeyValuePair<string, object>("@amt", leftOver)
                ]);

        return amount;
    }
}