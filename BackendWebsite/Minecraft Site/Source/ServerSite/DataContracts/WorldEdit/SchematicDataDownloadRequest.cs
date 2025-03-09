namespace ServerSite.DataContracts.WorldEdit;

public class SchematicDataDownloadRequest

{
    private string _currentDirectory;
    public string Uuid { get; set; }

    public string Auth { get; set; }

    public string FileName { get; set; }

    public string CurrentDirectory
    {
        get => _currentDirectory;
        set
        {
            if (!value.Trim().EndsWith("/"))
                _currentDirectory = value.Trim() + "/";
            else
                _currentDirectory = value.Trim();
        }
    }
}