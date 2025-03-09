namespace ServerSite.DataContracts.BlockBank;

public class BlockBankWithdrawlResponse
{
    public string Wid { get; set; }
    public string Uuid { get; set; }
    public string Auth { get; set; }
    public bool Success { get; set; }
    public string Material { get; set; }
    public int Amount { get; set; }
}