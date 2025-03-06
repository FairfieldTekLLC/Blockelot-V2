//using System;
//using System.Collections.Generic;
//using System.Data.SqlClient;
//using ServerSite.DataContracts.Rpg;

//namespace ServerSite.WorldEdit.Data
//{
//    public class Rpg
//    {
//        public static string Blockelot_DbConnString =
//            "Server=fftsql02.winterleaf.local; database=Blockelot; UID=sa; password=Redshoe1!;";

//        public static PlayerInfoResponse GetPlayer(string uuid)
//        {
//            var response = PlayerLoad(uuid);
//            if (response != null)
//                return response;
//            PlayerCreate(uuid);
//            response = PlayerLoad(uuid);
//            return response;
//        }

//        public static MobExpResponse MobExp()
//        {
//            var response = new MobExpResponse();
//            var records = new List<MobExp>();
//            using (var conn = new SqlConnection(Blockelot_DbConnString))
//            {
//                conn.Open();
//                using var cmd = conn.CreateCommand();
//                cmd.CommandText = "select name,exp from [dbo].[mobexp] order by name";
//                using var rdr = cmd.ExecuteReader();
//                while (rdr.Read())
//                    records.Add(new MobExp {Name = rdr.GetString(0), Exp = rdr.GetInt32(1)});
//            }

//            response.Mobs = records.ToArray();
//            return response;
//        }

//        private static void PlayerCreate(string uuid)
//        {
//            using var conn = new SqlConnection(Blockelot_DbConnString);
//            conn.Open();
//            using var cmd = conn.CreateCommand();
//            cmd.CommandText =
//                "insert into [dbo].[players] ([Uuid], [HpCur], [Exp], [Lvl],[fkClassId],inventoryjson,armorjson,enderjson) values (@uuid, @hpcur,@exp,@lvl,1,'','','')";
//            cmd.Parameters.AddWithValue("@uuid", uuid);
//            cmd.Parameters.AddWithValue("@hpcur", Starting.HpCur);
//            cmd.Parameters.AddWithValue("@exp", Starting.Exp);
//            cmd.Parameters.AddWithValue("@lvl", Starting.Lvl);
//            cmd.ExecuteNonQuery();
//        }

//        private static PlayerInfoResponse PlayerLoad(string uuid)
//        {
//            PlayerInfoResponse response = null;
//            using (var conn = new SqlConnection(Blockelot_DbConnString))
//            {
//                conn.Open();
//                using var cmd = conn.CreateCommand();
//                cmd.CommandText = @"
//                                        select 
//                                                p.[pkPlayerId]
//		                                        , p.[Uuid]
//		                                        , p.[HpCur]
//		                                        , p.[Exp]
//		                                        , p.[Lvl]
//		                                        ,p.[fkClassId]
//		                                        ,c.[Name]
//		                                        ,e.[ExpToLevel]
//												,s.[STR]
//												,s.[STA]
//												,s.[DEX]
//												,s.[WIS]
//												,s.[CHA]
//												,s.[AC]
//                                                ,p.inventoryJson
//                                                ,p.armorJson
//                                                ,p.enderjson
												
//                                        from 
//	                                        Players p 
//		                                        inner join Classes c on p.fkClassId = c.pkClassId 
//		                                        inner join ClassLevel e on p.fkClassId = e.fkClassId and e.Lvl = p.Lvl 
//												inner join ClassStat s on p.fkClassId = s.fkClassId
//                                        where 
//	                                        p.[Uuid] = @Uuid
//                                        ";
//                cmd.Parameters.AddWithValue("@Uuid", uuid);
//                using var rdr = cmd.ExecuteReader();
//                if (rdr.Read())
//                    response = new PlayerInfoResponse
//                    {
//                        Id = rdr.GetInt32(0),
//                        UUID = rdr.GetString(1),
//                        HpCur = rdr.GetInt32(2),
//                        Exp = rdr.GetInt32(3),
//                        Lvl = rdr.GetInt32(4),
//                        ClassId = rdr.GetInt32(5),
//                        ClassName = rdr.GetString(6),
//                        ExpToLevel = rdr.GetInt32(7),
//                        Str = rdr.GetInt32(8),
//                        Sta = rdr.GetInt32(9),
//                        Dex = rdr.GetInt32(10),
//                        Wis = rdr.GetInt32(11),
//                        Cha = rdr.GetInt32(12),
//                        Ac = rdr.GetInt32(13),
//                        InvCont = rdr.GetString(14),
//                        InvArm = rdr.GetString(15),
//                        InvEnd = rdr.GetString(16)
//                    };
//            }

//            return response;
//        }

//        public static PlayerInfoSaveResponse PlayerSave(PlayerInfoResponse save)
//        {
//            try
//            {
//                using (var conn = new SqlConnection(Blockelot_DbConnString))
//                {
//                    conn.Open();
//                    using var cmd = conn.CreateCommand();
//                    cmd.CommandText = @"update [dbo].[Players]
//set
//	[HpCur]=@hpcur,
//	[Exp]=@exp,
//	[lvl]=@lvl,
//    [inventoryjson] = @inventoryjson,
//    [armorjson] = @armorjson,
//    [EnderJson] = @enderjson
//where
//	[pkPlayerId] = @id";
//                    cmd.Parameters.AddWithValue("@hpcur", save.HpCur);
//                    cmd.Parameters.AddWithValue("@exp", save.Exp);
//                    cmd.Parameters.AddWithValue("@lvl", save.Lvl);
//                    cmd.Parameters.AddWithValue("@id", save.Id);
//                    cmd.Parameters.AddWithValue("@inventoryjson", save.InvCont);
//                    cmd.Parameters.AddWithValue("@armorjson", save.InvArm);
//                    cmd.Parameters.AddWithValue("@enderjson", save.InvEnd ?? "");
//                    cmd.ExecuteNonQuery();
//                }

//                var t = PlayerLoad(save.UUID);

//                return new PlayerInfoSaveResponse {Success = true, ExpToLevel = t.ExpToLevel};
//            }
//            catch (Exception e)
//            {
//            }

//            return new PlayerInfoSaveResponse {Success = false};
//        }

//        public static class Starting
//        {
//            public static int Exp = 0;
//            public static int HpCur = 20;
//            public static int HpMax = 20;
//            public static int Lvl = 1;
//        }
//    }
//}