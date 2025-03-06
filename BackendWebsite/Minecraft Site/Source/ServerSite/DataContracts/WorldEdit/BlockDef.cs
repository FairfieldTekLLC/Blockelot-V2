namespace ServerSite.DataContracts.WorldEdit
{
    public class BlockDef
    {
        public BlockDef()
        {
        }

        public BlockDef(string compressed)
        {
            string[] elements = compressed.Split("|");

            X = int.Parse(elements[0]);
            Y = int.Parse(elements[1]);
            Z = int.Parse(elements[2]);
            BlockTypeIndex = int.Parse(elements[3]);
            BlockDataIndex = int.Parse(elements[4]);
            BlockContentsIndex = int.Parse(elements[5]);
            BlockStorageIndex = int.Parse(elements[6]);
        }

        public int BlockDataIndex { get; set; }

        public int BlockTypeIndex { get; set; }

        public int BlockContentsIndex { get; set; }

        public int BlockStorageIndex { get; set; }

        public int X { get; set; }

        public int Y { get; set; }

        public int Z { get; set; }
    }
}