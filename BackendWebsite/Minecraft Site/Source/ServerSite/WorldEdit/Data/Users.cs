//  Fairfield Tek L.L.C.
//  Copyright (c) 2016, Fairfield Tek L.L.C.
//  
//  
// THIS SOFTWARE IS PROVIDED BY WINTERLEAF ENTERTAINMENT LLC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL WINTERLEAF ENTERTAINMENT LLC BE LIABLE FOR ANY DIRECT, INDIRECT, 
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
// DAMAGE. 
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using MySqlConnector;
using Newtonsoft.Json;
using ServerSite.DataContracts.BlockBank;
using ServerSite.DataContracts.WorldEdit;
using Z.BulkOperations;
//using SqlCommand = MySql.Data.MySqlClient.SqlCommand;
//using SqlConnection = MySql.Data.MySqlClient.SqlConnection;
//using SqlDataReader = MySql.Data.MySqlClient.SqlDataReader;

namespace ServerSite.WorldEdit.Data
{
    public static class Users
    {
        public static string FixGuid(this Guid guid)
        {
            return $"{{{guid.ToString().ToUpper()}}}";
        }

        private static readonly int TimeOut = 120000;

        public static async Task<string> AuthUser(string uuid)
        {
            await using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };

            conn.Open();

            string lastAuth = GetNewAuth();

            string emailAddress = GetEmail(uuid);
            if (emailAddress == null)
                return "";

            int id = GetUserId(uuid);

            if (id == -1)
                return "";

            await using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "update Users set LastAuth = @Auth, LastAuthDt = @LastAuthDt WHERE PkUserId = @UserId;";
                cmd.Parameters.AddWithValue("@Auth", lastAuth);
                cmd.Parameters.AddWithValue("@UserId", id);
                cmd.Parameters.AddWithValue("@LastAuthDt", DateTime.Now);
                cmd.ExecuteNonQuery();
            }

            //await EmailMessages.SendAuthCode(emailAddress, lastAuth);

            return lastAuth;
        }

        public static void BulkLoad(int schematicId, string data, string rootPath)
        {
            using var conn = new SqlConnection(Constants.DbConnString);
            int nextSeq = 0;

            conn.Open();
            try
            {
                using var cmd = conn.CreateCommand();
                cmd.CommandText =
                    "select max(Seq) from SchematicData where FkSchematicId = @SID";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@SID", schematicId);
                nextSeq = (int)cmd.ExecuteScalar();
                nextSeq += 1;
            }
            catch (Exception e)
            {
                //Throw Away
            }

            {
                using var cmd = conn.CreateCommand();
                cmd.CommandText =
                    "insert into SchematicData (FkSchematicId, Seq, BlockData ) select @SID, @SEQ, @binary";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@SID", schematicId);
                cmd.Parameters.AddWithValue("@SEQ", nextSeq);
                cmd.Parameters.AddWithValue("@binary", data);
                cmd.ExecuteNonQuery();
            }
        }


        public static Tuple<bool, string> CreateFolder(int userid, string path, string foldername)
        {
            foldername = foldername.Trim();
            if (!foldername.All(char.IsLetterOrDigit))
                return new Tuple<bool, string>(false, "Only Letters and Numbers are allowed for Directory Names.");
            if (foldername.Length > 50)
                return new Tuple<bool, string>(false, "Max folder name is 50 characters.");

            int parentFolder = FindFolderFromPath(userid, path);

            using SqlConnection conn = new SqlConnection();
            conn.ConnectionString = Constants.DbConnString;

            conn.Open();

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "select count(*) from Directory where DirectoryName = @DirectoryName and FkParentId = @Parent and IsDeleted = 0;";
                cmd.Parameters.AddWithValue("@DirectoryName", foldername);
                cmd.Parameters.AddWithValue("@Parent", parentFolder);
                int chk =(int) cmd.ExecuteScalar();
                if (chk > 0)
                    return new Tuple<bool, string>(false, "Folder already exists.");
            }

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "select count(*) from Schematic where Name = @DirectoryName and FkDirectoryId = @Parent and IsDeleted=0;";
                cmd.Parameters.AddWithValue("@DirectoryName", foldername);
                cmd.Parameters.AddWithValue("@Parent", parentFolder);
                int chk = (int)cmd.ExecuteScalar();
                if (chk > 0)
                    return new Tuple<bool, string>(false, "Schematic exists with foldername.");
            }

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "Insert into Directory (DirectoryName,FkParentId,FkUserId, IsDeleted) values (@DirectoryName, @FkParentId, @FkUserId, 0);";
                cmd.Parameters.AddWithValue("@DirectoryName", foldername);
                cmd.Parameters.AddWithValue("@FkParentId", parentFolder);
                cmd.Parameters.AddWithValue("@FkUserId", userid);
                cmd.ExecuteNonQuery();
            }

            return new Tuple<bool, string>(true, "Folder created.");
        }

        public static Tuple<bool, string> Delete(int userId, string path, string foldername)
        {
            int sid = -1;
            using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };

            conn.Open();
            int fid = FindFolderFromPath(userId, path);
            {
                using SqlCommand cmd = conn.CreateCommand();
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "select pkSchematicId from Schematic where Name = @name and fkOwnerId = @Owner and IsDeleted = 0;";
                cmd.CommandTimeout = 99999;
                cmd.Parameters.AddWithValue("@name", foldername);
                cmd.Parameters.AddWithValue("@Owner", userId);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    sid = rdr.GetInt32(0);
            }

            if (sid != -1)
            {
                //using (var cmd = conn.CreateCommand())
                //{
                //    cmd.CommandTimeout = TimeOut;
                //    cmd.CommandText = "delete from SchematicData where FkSchematicId = @Sid";
                //    cmd.Parameters.AddWithValue("@Sid", sid);
                //    cmd.ExecuteNonQuery();
                //}
                //using (var cmd = conn.CreateCommand())
                //{
                //    cmd.CommandTimeout = TimeOut;
                //    cmd.CommandText = "delete from Schematic where PkSchematicId = @Sid";
                //    cmd.Parameters.AddWithValue("@Sid", sid);
                //    cmd.ExecuteNonQuery();
                //}

                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText = "update Schematic set IsDeleted = 1 where PkSchematicId = @Sid";
                    cmd.Parameters.AddWithValue("@Sid", sid);
                    cmd.ExecuteNonQuery();
                }

                return new Tuple<bool, string>(true, "Deleted File " + foldername + ".");
            }

            int folderToDelete = FindFolderFromPath(userId, path + "/" + foldername);

            if (folderToDelete == -1)
                return new Tuple<bool, string>(false, "Cannot Find Folder " + foldername + ".");

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "Select Count(*) from Schematic where FkDirectoryId = @DirectoryId and IsDeleted = 0;";
                cmd.Parameters.AddWithValue("@DirectoryId", folderToDelete);
                long chk = (long)cmd.ExecuteScalar();
                if (chk > 0)
                    return new Tuple<bool, string>(false, "Cannot delete Directory (Schematics Exist).");
            }

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "Select Count(*) from Directory where FkParentId = @DirectoryId and IsDeleted = 0;";
                cmd.Parameters.AddWithValue("@DirectoryId", folderToDelete);
                long chk = (long)cmd.ExecuteScalar();
                if (chk > 0)
                    return new Tuple<bool, string>(false, "Cannot delete Directory (Directories Exist).");
            }

            //using (var cmd = conn.CreateCommand())
            //{
            //    cmd.CommandTimeout = TimeOut;
            //    cmd.CommandText = "Delete from Directory where PkDirectoryId = @Id";
            //    cmd.Parameters.AddWithValue("@Id", folderToDelete);
            //    cmd.ExecuteNonQuery();
            //}
            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText = "update Directory set IsDeleted = 1 where PkDirectoryId = @Id";
                cmd.Parameters.AddWithValue("@Id", folderToDelete);
                cmd.ExecuteNonQuery();
            }

            return new Tuple<bool, string>(true, "Deleted Folder " + foldername + ".");
        }

        public static int FindFolderFromPath(int userId, string path)
        {
            using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };

            conn.Open();
            string[] parts = path.Split("/");

            int rootFolderId = GetHomeFolder(userId).Item1;

            foreach (string part in parts)
            {
                if (part.Equals("..") || string.IsNullOrEmpty(part))
                    continue;

                using SqlCommand cmd = conn.CreateCommand();
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "Select PkDirectoryId from Directory where FkParentId = @FkParentId and DirectoryName = @DirectoryName and FkUserId = @UserId and isDeleted = 0";
                cmd.Parameters.AddWithValue("@FkParentId", rootFolderId);
                cmd.Parameters.AddWithValue("@DirectoryName", part.Trim());
                cmd.Parameters.AddWithValue("@UserId", userId);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    rootFolderId = rdr.GetInt32(0);
                else
                    return -1;
            }

            return rootFolderId;
        }

        /// <summary>
        ///     Get email address registered to a Minecraft UUID
        /// </summary>
        /// <param name="uuid"></param>
        /// <returns></returns>
        public static string GetEmail(string uuid)
        {
            using (SqlConnection conn = new SqlConnection())
            {
                conn.ConnectionString = Constants.DbConnString;
                conn.Open();
                using SqlCommand cmd = conn.CreateCommand();
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText = "Select EmailAddress from Users where UniqueId = @UserId;";
                cmd.Parameters.AddWithValue("@UserId", uuid);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    return rdr.GetString(0);
            }

            return null;
        }

        public static List<DirectoryElement> GetFolderContents(int userId, string path)
        {
            int folderId = FindFolderFromPath(userId, path);

            List<DirectoryElement> directories = new List<DirectoryElement>();

            using (SqlConnection conn = new SqlConnection())
            {
                conn.ConnectionString = Constants.DbConnString;

                conn.Open();

                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText =
                        "Select DirectoryName from Directory where FkParentId = @FkParentId and FkUserId = @FkUserId and isDeleted =0";
                    cmd.Parameters.AddWithValue("@FkUserId", userId);
                    cmd.Parameters.AddWithValue("@FkParentId", folderId);
                    using SqlDataReader rdr = cmd.ExecuteReader();
                    while (rdr.Read())
                    {
                        DirectoryElement de = new DirectoryElement
                        {
                            ElementType = Constants.DirectoryEntry.Directory,
                            BlockCount = 0,
                            Name = rdr.GetString(0)
                        };
                        directories.Add(de);
                    }
                }

                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText =
                        @"Select Name, NumberOfBlocks from Schematic Where FkDirectoryId = @FkDirectoryId and isDeleted = 0;";
                    cmd.Parameters.AddWithValue("@FkDirectoryId", folderId);
                    using SqlDataReader rdr = cmd.ExecuteReader();
                    while (rdr.Read())
                    {
                        DirectoryElement de = new DirectoryElement
                        {
                            Name = rdr.GetString(0),
                            BlockCount = rdr.GetInt32(1),
                            ElementType = Constants.DirectoryEntry.Schematic
                        };
                        directories.Add(de);
                    }
                }
            }

            return directories;
        }

        public static Tuple<int, string> GetHomeFolder(int userId)
        {
            using (SqlConnection conn = new SqlConnection())
            {
                conn.ConnectionString = Constants.DbConnString;
                conn.Open();
                using SqlCommand cmd = conn.CreateCommand();
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "select PkDirectoryId, DirectoryName from Directory where FkParentId is NULL and FkUserId = @FkUserId and isDeleted = 0;";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@FkUserId", userId);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    return new Tuple<int, string>(rdr.GetInt32(0), rdr.GetString(1));
            }

            return null;
        }

        private static string GetNewAuth()
        {
            return Guid.NewGuid().ToString("N").Substring(27).ToUpper().Replace("O", "0");
        }

        public static int GetUserId(string uuid, string authToken)
        {
            int id = -1;
            using (SqlConnection conn = new SqlConnection())
            {
                conn.ConnectionString = Constants.DbConnString;
                conn.Open();
                using SqlCommand cmd = conn.CreateCommand();
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText = "select PkUserId from Users where UniqueId=@UniqueId and Auth = @Auth;";
                cmd.Parameters.AddWithValue("@UniqueId", uuid);
                cmd.Parameters.AddWithValue("@Auth", authToken);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    id = rdr.GetInt32(0);
            }

            return id;
        }

        public static int GetUserId(string uuid)
        {
            int id = -1;
            using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };
            conn.Open();
            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText = "select PkUserId from Users where UniqueId = @UUID;";
                cmd.Parameters.AddWithValue("@UUID", uuid);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    id = rdr.GetInt32(0);
            }

            return id;
        }

        public static Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>
            LoadSchematic(int userId, string path, string filename)
        {
            int targetFolderId = FindFolderFromPath(userId, path);
            int schematicId = -1;
            if (targetFolderId == -1)
                return new
                    Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>(
                        false, "Target folder does not exist.", "", new List<PaletteEntry>(),
                        new List<PaletteEntry>(), new List<PaletteEntry>(), 0);
            using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };

            conn.Open();

            List<PaletteEntry> blockDataPalette = null;
            List<PaletteEntry> blockTypePalette = null;
            List<PaletteEntry> blockInvePalette = null;
            string name;
            int blockCount = 0;
            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandText =
                    "Select PkSchematicId, BlockDataJson, BlockTypeJson,name,BlockInveJson,NumberOfBlocks  from Schematic where Name=@Name and FkDirectoryId = @FkDirectoryId and IsDeleted = 0 ";
                cmd.Parameters.AddWithValue("@Name", filename);
                cmd.Parameters.AddWithValue("@FkDirectoryId", targetFolderId);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                {
                    schematicId = rdr.GetInt32(0);
                    blockDataPalette = JsonConvert.DeserializeObject<List<PaletteEntry>>(rdr.GetString(1))
                        .Where(x => x != null).ToList();
                    blockTypePalette = JsonConvert.DeserializeObject<List<PaletteEntry>>(rdr.GetString(2))
                        .Where(x => x != null).ToList();
                    blockInvePalette = JsonConvert.DeserializeObject<List<PaletteEntry>>(rdr.GetString(4))
                        .Where(x => x != null).ToList();
                    name = rdr.GetString(3);
                    blockCount = rdr.GetInt32(5);
                }
            }

            if (schematicId == -1)
                return new
                    Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>(
                        false, "Unknown Filename", "", new List<PaletteEntry>(),
                        new List<PaletteEntry>(), new List<PaletteEntry>(), 0);

            StringBuilder dat = new StringBuilder();
            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandText =
                    @"SELECT BlockData from SchematicData where FkSchematicId = @FkSchematicId order by  Seq;";
                cmd.Parameters.AddWithValue("@FkSchematicId", schematicId);
                cmd.CommandTimeout = TimeOut;
                using SqlDataReader rdr = cmd.ExecuteReader();
                while (rdr.Read())
                {
                    dat.Append(rdr.GetString(0));
                }
            }





            return new
                Tuple<bool, string, string, List<PaletteEntry>, List<PaletteEntry>, List<PaletteEntry>, int>(
                    true, "Blocks Loaded", dat.ToString(), blockDataPalette, blockTypePalette, blockInvePalette,
                    blockCount);
        }


        public static void RunCommand(SqlCommand cmd)
        {
            using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };
            conn.Open();
            cmd.Connection = conn;
            cmd.ExecuteNonQuery();
        }


        public static string Login(string uuid, string authToken, string wid = null)
        {
            Guid worldId = Guid.Empty;
            if (!string.IsNullOrEmpty(wid))
                worldId = Guid.Parse(wid);

            if (string.IsNullOrEmpty(authToken)) throw new Exception("AUTH TOKEN BLANK!");

            int id = -1;
            using SqlConnection conn = new SqlConnection();
            conn.ConnectionString = Constants.DbConnString;
            conn.Open();
            {
                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText = "select PkUserId from Users where UniqueId=@UniqueId and LastAuth = @Auth;";
                    cmd.Parameters.AddWithValue("@UniqueId", uuid);
                    cmd.Parameters.AddWithValue("@Auth", authToken);
                    using SqlDataReader rdr = cmd.ExecuteReader();
                    if (rdr.Read())
                        id = rdr.GetInt32(0);
                }
            }
            if (id == -1)
                return "";

            string lastAuth = GetNewAuth();
            {
                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText =
                        "UPDATE Users SET LastAuth = @Auth, LastAuthDt = @LastAuthDt,Confirmed = 1 WHERE PkUserId = @UserId;";
                    cmd.Parameters.AddWithValue("@Auth", lastAuth);
                    cmd.Parameters.AddWithValue("@UserId", id);
                    cmd.Parameters.AddWithValue("@LastAuthDt", DateTime.Now);
                    cmd.ExecuteNonQuery();
                }
            }

            if (worldId == Guid.Empty)
                return lastAuth;

            bool found = false;
            {
                using SqlCommand cmd = conn.CreateCommand();
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "select LastDt from WorldUsers where fkWorldId = @fkWorldId and fkUserId= @fkUserId";
                cmd.Parameters.AddWithValue("@fkWorldId", worldId.FixGuid());
                cmd.Parameters.AddWithValue("@fkUserId", id);


                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    found = true;
            }
            if (found)
            {
                SqlCommand c = new SqlCommand
                {
                    CommandText =
                        "Update WorldUsers set LastDt = @Date where fkUserId= @fkUserId and fkWorldId = @fkWorldId",
                    CommandType = CommandType.Text
                };
                c.Parameters.AddWithValue("@fkUserId", id);
                c.Parameters.AddWithValue("@Date", DateTime.Now);
                c.Parameters.AddWithValue("@fkWorldId", worldId.FixGuid());
                RunCommand(c);
            }
            else
            {
                SqlCommand c = new SqlCommand
                {
                    CommandText =
                        "INSERT INTO WorldUsers ( fkWorldId , fkUserId , LastDt ) select @w , @u , getdate();",
                    CommandType = CommandType.Text
                };
                c.Parameters.AddWithValue("@w", worldId.FixGuid());
                c.Parameters.AddWithValue("@u", id);
                RunCommand(c);
            }


            return lastAuth;
        }

        public static string RegisterNewUser(string uuid, string emailAddress)
        {
            using SqlConnection conn = new SqlConnection { ConnectionString = Constants.DbConnString };

            conn.Open();

            string lastAuth = GetNewAuth();

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "Insert into Users (UniqueId, EmailAddress, Confirmed,LastAuth) values (@UniqueId,@EmailAddress,0,@Auth);";
                cmd.Parameters.AddWithValue("@UniqueId", uuid);
                cmd.Parameters.AddWithValue("@EmailAddress", emailAddress);
                cmd.Parameters.AddWithValue("@Auth", lastAuth);
                cmd.ExecuteNonQuery();
            }

            int id = GetUserId(uuid);
            if (id == -1)
                return "";

            using (SqlCommand cmd = conn.CreateCommand())
            {
                cmd.CommandTimeout = TimeOut;
                cmd.CommandText =
                    "Insert into Directory(DirectoryName, FkParentId, FkUserId,IsDeleted) values (@DirectoryName,null,@FkUserId,0); ";
                cmd.Parameters.AddWithValue("@DirectoryName", "..");
                cmd.Parameters.AddWithValue("@FkUserId", id);
                cmd.ExecuteNonQuery();
            }

            return lastAuth;

            //await EmailMessages.SendAuthCode(emailAddress, lastAuth);
        }


        public static int CheckIfFileFolderExist(string filename, int targetFolderId)
        {
            using (SqlConnection conn = new SqlConnection())
            {
                conn.ConnectionString = Constants.DbConnString;

                conn.Open();

                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText =
                        "select count(*) from Directory where DirectoryName = @DirectoryName and FkParentId = @Parent and isDeleted = 0;";
                    cmd.Parameters.AddWithValue("@DirectoryName", filename);
                    cmd.Parameters.AddWithValue("@Parent", targetFolderId);
                    var rdr = cmd.ExecuteReader();
                    long chk = 0;
                    if (rdr.Read())
                    {
                        return rdr.GetInt32(0);
                    }
                }

            }

            return 0;
        }

        public static long CheckIfFileExists(string filename, int targetFolderId)
        {
            using (SqlConnection conn = new SqlConnection())
            {
                conn.ConnectionString = Constants.DbConnString;

                conn.Open();
                using (SqlCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandTimeout = TimeOut;
                    cmd.CommandText =
                        "select count(*) from Schematic where Name = @DirectoryName and FkDirectoryId = @Parent and isDeleted=0;";
                    cmd.Parameters.AddWithValue("@DirectoryName", filename);
                    cmd.Parameters.AddWithValue("@Parent", targetFolderId);
                    var rdr = cmd.ExecuteReader();
                    long chk = 0;
                    if (rdr.Read())
                    {
                        chk = rdr.GetInt32(0);
                    }

                    return chk;
                }
            }
        }



        public static Tuple<bool, string, int> SaveSchematic(int userId, string path, string filename,
                SchematicDataRequest schematicDataRequest, string rootPath)
        {
            int schematicId = -1;
            try
            {
                int targetFolderId = FindFolderFromPath(userId, path);
                if (targetFolderId == -1)
                    return new Tuple<bool, string, int>(false, "Target folder does not exist.", -1);



                if (CheckIfFileFolderExist(filename, targetFolderId) > 0)
                {
                    return new Tuple<bool, string, int>(false, "Directory exists with filename already exists.", -1);
                }



                using (SqlConnection conn = new SqlConnection())
                {
                    conn.ConnectionString = Constants.DbConnString;

                    conn.Open();


                    if (CheckIfFileExists(filename, targetFolderId) > 0)
                        return new Tuple<bool, string, int>(false, "Schematic exists with filename.", -1);


                    using (SqlCommand cmd = conn.CreateCommand())
                    {
                        cmd.CommandTimeout = TimeOut;
                        cmd.CommandText =
                            "Insert into Schematic (Name, FkOwnerId, FkDirectoryId, IsDeleted, BlockDataJson, BlockTypeJson,BlockInveJson,NumberOfBlocks) " +
                            @"values (@Name, @FkOwnerId, @FkDirectoryId,0,@BlockDataJson,@BlockTypeJson,@BlockInveJson,@NumberOfBlocks);";
                        cmd.Parameters.AddWithValue("@Name", schematicDataRequest.FileName);
                        cmd.Parameters.AddWithValue("@FkOwnerId", userId);
                        cmd.Parameters.AddWithValue("@FkDirectoryId", targetFolderId);
                        cmd.Parameters.AddWithValue("@BlockDataJson",
                            JsonConvert.SerializeObject(schematicDataRequest.BlockDataPalette));
                        cmd.Parameters.AddWithValue("@BlockTypeJson",
                            JsonConvert.SerializeObject(schematicDataRequest.BlockTypePalette));
                        cmd.Parameters.AddWithValue("@BlockInveJson",
                            JsonConvert.SerializeObject(schematicDataRequest.BlockInvePalette));
                        cmd.Parameters.AddWithValue("@NumberOfBlocks", schematicDataRequest.TotalNumberOfBlocks);
                        cmd.ExecuteNonQuery();
                    }
                }
                using (SqlConnection conn = new SqlConnection())
                {
                    conn.ConnectionString = Constants.DbConnString;

                    conn.Open();
                    using (SqlCommand cmd = conn.CreateCommand())
                    {
                        cmd.CommandTimeout = TimeOut;
                        cmd.CommandText =
                            "Select PkSchematicId from Schematic where Name = @Name and FkOwnerId=@FkOwnerId and FkDirectoryId = @FkDirectoryId and IsDeleted=0;";
                        cmd.Parameters.AddWithValue("@Name", schematicDataRequest.FileName);
                        cmd.Parameters.AddWithValue("@FkOwnerId", userId);
                        cmd.Parameters.AddWithValue("@FkDirectoryId", targetFolderId);

                        var rdr = cmd.ExecuteReader();
                        if (rdr.Read())
                        {
                            int t = rdr.GetInt32(0);
                            schematicId = (int)t;
                        }


                        //int t = (int)cmd.ExecuteScalar();
                        //schematicId = (int)t;
                    }
                }

                void LoadEm(object callback)
                {
                    BulkLoad(schematicId, schematicDataRequest.Blocks, rootPath);
                }

                ThreadPool.QueueUserWorkItem(new WaitCallback(LoadEm));
            }
            catch (Exception e)
            {
                Debug.WriteLine(e);
            }

            return new Tuple<bool, string, int>(true, "", schematicId);
        }


        public static Guid RegisterServer(string ipAddress, string latestVersion, string serverName, string worldId)
        {
            serverName = serverName.Replace("'", "");

            Guid? wid = null;
            try
            {
                wid = Guid.Parse(worldId);
            }
            catch (Exception e)
            {
            }



            using SqlConnection con = new SqlConnection { ConnectionString = Constants.DbConnString };
            con.Open();


            if (wid != null)
            {
                using SqlCommand cmd = con.CreateCommand();
                cmd.CommandText = "select pkWorldId from Worlds where w.pkWorldId = @worldId";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@worldId", wid.Value.FixGuid());
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    return wid.Value;
            }


            Guid ServerId = Guid.Empty;
            using (SqlCommand cmd = con.CreateCommand())
            {
                cmd.CommandText =
                    "select pkWorldId from Worlds where IPAddress = @ipAddress and ServerName=@serverName";
                cmd.Parameters.AddWithValue("@ipAddress", ipAddress.Trim());
                cmd.Parameters.AddWithValue("@serverName", serverName);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                    ServerId = rdr.GetGuid(0);
            }

            if (ServerId == Guid.Empty)
            {
                ServerId = Guid.NewGuid();
                //New Registration
                using SqlCommand cmd = con.CreateCommand();
                cmd.CommandText =
                    @$"insert into Worlds ( pkWorldId , IPAddress , LastVersion, ServerName, LastUpdated) 
                        select @w , @i, @l, @s, getdate()";
                //VALUES ('{ServerId.FixGuid()}','{ipAddress.Trim()}','{latestVersion}','{serverName}',now());";
                cmd.Parameters.AddWithValue("@w", ServerId.FixGuid());
                cmd.Parameters.AddWithValue("@i", ipAddress.Trim());
                cmd.Parameters.AddWithValue("@l", latestVersion);
                cmd.Parameters.AddWithValue("@s", serverName);
                cmd.ExecuteNonQuery();
            }

            return ServerId;
        }


        public static void DepositMaterial(int userId, string wid, string material, int count)
        {
            Guid vWid = Guid.Parse(wid);
            Guid BlockBankId = GetBlockBank(userId, vWid);

            Guid MaterialId = Guid.Empty;
            using SqlConnection con = new SqlConnection { ConnectionString = Constants.DbConnString };
            con.Open();
            using (SqlCommand cmd = con.CreateCommand())
            {
                cmd.CommandText =
                    "select pkId from BlockBankContents where fkBlockBankId = @BBID and Material = @Material";
                cmd.Parameters.AddWithValue("@BBID", BlockBankId.FixGuid());
                cmd.Parameters.AddWithValue("Material", material);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read()) MaterialId = rdr.GetGuid(0);
            }


            if (MaterialId == Guid.Empty)
            {
                using (SqlCommand cmd = con.CreateCommand())
                {
                    cmd.CommandText =
                        "insert into BlockBankContents (pkId, fkBlockBankId, Material, Amount) values (@MatId, @BBID, @Material,@Amount)";
                    cmd.Parameters.AddWithValue("@BBID", BlockBankId.FixGuid());
                    cmd.Parameters.AddWithValue("Material", material);
                    cmd.Parameters.AddWithValue("@MatId", Guid.NewGuid().FixGuid());
                    cmd.Parameters.AddWithValue("@Amount", count);
                    cmd.ExecuteNonQuery();
                }

                return;
            }

            int amount = 0;
            using (SqlCommand cmd = con.CreateCommand())
            {
                cmd.CommandText =
                    "select Amount from BlockBankContents where pkId = @Id";
                cmd.Parameters.AddWithValue("@Id", MaterialId.FixGuid());
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read()) amount = rdr.GetInt32(0);
            }

            using (SqlCommand cmd = con.CreateCommand())
            {
                cmd.CommandText =
                    "update BlockBankContents set Amount = @amt where pkId = @id";
                cmd.Parameters.AddWithValue("@Id", MaterialId.FixGuid());

                amount = amount + count;
                cmd.Parameters.AddWithValue("@amt", amount);
                cmd.ExecuteNonQuery();
            }
        }

        public static List<BlockBankInventoryItem> GetBlockBankInventory(int userId, string wid)
        {
            Guid vwid = Guid.Parse(wid);

            List<BlockBankInventoryItem> result = new List<BlockBankInventoryItem>();
            Guid blockBankId = GetBlockBank(userId, vwid);

            using (SqlConnection con = new SqlConnection())
            {
                con.ConnectionString = Constants.DbConnString;
                con.Open();
                using SqlCommand cmd = con.CreateCommand();
                cmd.CommandText =
                    "select  Material, Amount from BlockBankContents where fkBlockBankId = @BBID";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@BBID", blockBankId.FixGuid());
                using SqlDataReader rdr = cmd.ExecuteReader();
                while (rdr.Read())
                    result.Add(new BlockBankInventoryItem
                    {
                        MaterialName = rdr.GetString(0),
                        Count = rdr.GetInt32(1)
                    });
            }

            return result;
        }


        public static Guid GetBlockBank(int userId, Guid wid)
        {
            Guid BlockBankId = Guid.Empty;
            using (SqlConnection con = new SqlConnection())
            {
                con.ConnectionString = Constants.DbConnString;
                con.Open();
                using (SqlCommand cmd = con.CreateCommand())
                {
                    cmd.CommandText =
                        "select pkBlockBankId from BlockBank where fkUserId = @fkUserId and fkWorldId = @fkWorldId";
                    cmd.Parameters.AddWithValue("@fkUserId", userId);
                    cmd.Parameters.AddWithValue("@fkWorldId", wid.FixGuid());
                    using SqlDataReader rdr = cmd.ExecuteReader();
                    if (rdr.Read()) BlockBankId = rdr.GetGuid(0);
                }

                if (BlockBankId != Guid.Empty) return BlockBankId;
                {
                    BlockBankId = Guid.NewGuid();

                    using SqlCommand cmd = con.CreateCommand();
                    cmd.CommandText =
                        "Insert into BlockBank (pkBlockBankId,fkUserId,fkWorldId) select @bb, @u, @w;";
                    cmd.Parameters.AddWithValue("@bb", BlockBankId.FixGuid());
                    cmd.Parameters.AddWithValue("@u", userId);
                    cmd.Parameters.AddWithValue("@w", wid.FixGuid());
                    cmd.ExecuteNonQuery();
                }
            }

            return BlockBankId;
        }


        public static int WithDrawlMaterial(int userId, string wid, string material, int amount)
        {
            Guid vwid = Guid.Parse(wid);
            Guid BlockBankId = GetBlockBank(userId, vwid);
            using SqlConnection con = new SqlConnection { ConnectionString = Constants.DbConnString };
            con.Open();
            Guid Id = Guid.Empty;
            int available = 0;

            using (SqlCommand cmd = con.CreateCommand())
            {
                cmd.CommandText =
                    "select pkId,Amount from BlockBankContents where fkBlockBankId = @BBID and Material = @Material";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@BBID", BlockBankId.FixGuid());
                cmd.Parameters.AddWithValue("@Material", material);
                using SqlDataReader rdr = cmd.ExecuteReader();
                if (rdr.Read())
                {
                    Id = rdr.GetGuid(0);
                    available = rdr.GetInt32(1);
                }
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
            {
                using SqlCommand cmd = con.CreateCommand();
                cmd.CommandText =
                    "delete  BlockBankContents where pkId = @ID";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@ID", Id.FixGuid());
                cmd.ExecuteNonQuery();
            }
            else
            {
                using SqlCommand cmd = con.CreateCommand();
                cmd.CommandText =
                    "update  BlockBankContents set Amount = @amt  where pkId = @ID";
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.AddWithValue("@ID", Id.FixGuid());
                cmd.Parameters.AddWithValue("@amt", leftOver);
                cmd.ExecuteNonQuery();
            }

            return amount;
        }
    }
}