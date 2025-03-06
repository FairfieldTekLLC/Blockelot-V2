namespace ServerSite.DataContracts.BlockBank
{
    public class BlockBankInventoryResponse
    {
        public string Uuid { get; set; }
        public string Auth { get; set; }
        public bool Success { get; set; }
        public BlockBankInventoryItem[] Items { get; set; }
    }
}