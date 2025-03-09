namespace ServerSite.DataContracts.WorldEdit;

public class SchematicDataDownloadResponse


{
    public bool IsAuthorized { get; set; }

    public string Auth { get; set; }

    public string Message { get; set; }

    public string Uuid { get; set; }

    public bool WasSuccessful { get; set; }

    public string DirectoryPath { get; set; }

    public string FileName { get; set; }

    public PaletteEntry[] BlockDataPalette { get; set; }

    public PaletteEntry[] BlockTypePalette { get; set; }

    public PaletteEntry[] BlockInvePalette { get; set; }

    public int[] Blocks { get; set; }

    public int TotalNumberOfBlocks { get; set; }
}