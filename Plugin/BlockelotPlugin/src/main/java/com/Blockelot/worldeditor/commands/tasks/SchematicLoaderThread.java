package com.Blockelot.worldeditor.commands.tasks;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.BlockCollection;
import com.Blockelot.worldeditor.container.BlockInfo;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author geev
 */
public class SchematicLoaderThread implements Runnable {

    int[] Numbers = null;
    ArrayList<BlockInfo> Blocks;
    BlockCollection Working;

    @Override
    public void run() {
        try {
            int counter = 0;

            int NumCounter = 0;

            int[] Seven = new int[7];

            for (int num : Numbers) {

                Seven[NumCounter] = num;

                NumCounter++;

                if (NumCounter == 7) {

                    BlockInfo block = BlockInfo.fromXferStream(Seven);

                    block.setBlockCollection(Working);

                    Blocks.add(block);

                    counter++;

                    Seven = new int[7];
                    
                    NumCounter = 0;

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BlockInfo.class.getName()).log(Level.WARNING, null, ex);
            ServerUtil.consoleLog(ex.getLocalizedMessage());
            ServerUtil.consoleLog(ex.getMessage());
            ServerUtil.consoleLog(ex);
        }
    }

    public SchematicLoaderThread(int[] numbers, ArrayList<BlockInfo> blocks, BlockCollection scheme) {
        Working = scheme;
        Numbers = numbers;
        Blocks = blocks;
    }
}
