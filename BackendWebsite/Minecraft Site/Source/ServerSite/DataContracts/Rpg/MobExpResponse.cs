namespace ServerSite.DataContracts.Rpg
{
    public class MobExp
    {
        public int Exp { get; set; }
        public string Name { get; set; }
    }

    public class MobExpResponse
    {
        public MobExp[] Mobs { get; set; }
    }
}