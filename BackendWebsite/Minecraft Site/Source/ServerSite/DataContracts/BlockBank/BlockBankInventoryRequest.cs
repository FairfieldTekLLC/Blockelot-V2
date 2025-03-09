namespace ServerSite.DataContracts.BlockBank;

public class BlockBankInventoryRequest
{
    public string Wid { get; set; }
    public string Uuid { get; set; }

    public string Auth { get; set; }

    public string SearchCriteria { get; set; }
}