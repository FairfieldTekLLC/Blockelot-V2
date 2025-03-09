namespace ServerSite.DataContracts.WorldEdit;

public class SchematicDataResponse
{
    public int SchematicId { get; set; }

    public bool IsAuthorized { get; set; }

    public string Auth { get; set; }

    public string Message { get; set; }

    public string Uuid { get; set; }

    public bool WasSuccessful { get; set; }
}