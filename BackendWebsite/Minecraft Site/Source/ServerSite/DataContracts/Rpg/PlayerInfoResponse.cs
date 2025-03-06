namespace ServerSite.DataContracts.Rpg
{
    public class PlayerInfoResponse
    {
        public int Ac { get; set; }
        public int Cha { get; set; }
        public int ClassId { get; set; }
        public string ClassName { get; set; }
        public int Dex { get; set; }
        public int Exp { get; set; }

        public int ExpToLevel { get; set; }
        public int HpCur { get; set; }
        public int Id { get; set; }
        public string InvArm { get; set; }

        public string InvCont { get; set; }
        public string InvEnd { get; set; }
        public int Lvl { get; set; }
        public int Sta { get; set; }

        public int Str { get; set; }
        public string UUID { get; set; }
        public int Wis { get; set; }
    }
}