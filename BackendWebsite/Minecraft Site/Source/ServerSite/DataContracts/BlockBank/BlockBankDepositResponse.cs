namespace ServerSite.DataContracts.BlockBank;

public class BlockBankDepositResponse
{
    public string Uuid { get; set; }
    public string Auth { get; set; }
    public bool Success { get; set; }
}