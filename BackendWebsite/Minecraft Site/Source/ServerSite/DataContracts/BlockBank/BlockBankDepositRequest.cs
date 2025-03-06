namespace ServerSite.DataContracts.BlockBank
{
    public class BlockBankDepositRequest
    {
        public string Wid { get; set; }
        public string Uuid { get; set; }

        public string Auth { get; set; }

        public BlockBankInventoryItem[] ToDeposit { get; set; }
    }
}