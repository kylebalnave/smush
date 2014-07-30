package smush.runners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import semblance.io.FileUtils;
import semblance.runners.MultiThreadRunner;
import semblance.runners.Runner;
import static semblance.runners.Runner.callRunnerSequence;

/**
 * https://github.com/depsypher/pngtastic
 * https://code.google.com/p/pngtastic/downloads/list
 *
 * @author kyleb2
 */
public class SmushRunner extends MultiThreadRunner {
    
    public static final String KEY_IMAGE_DIR = "dir";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        callRunnerSequence(SmushRunner.class, args);
    }

    public SmushRunner(Map config) {
        super(config);
    }

    public SmushRunner(String configUrlOrFilePath) {
        super(configUrlOrFilePath);
    }

    @Override
    protected List<Runner> getRunnerCollection() {
        List<Runner> queue = new ArrayList<Runner>();
        String imageDir = (String) getConfigValue(KEY_IMAGE_DIR, "./images");
        Map<String, File> files = FileUtils.listFiles(imageDir);
        for(File image : files.values()) {
            String path = image.getAbsolutePath();
            if(path.endsWith(".png")) {
                queue.add(new PngRunner(image));
            }
        }
        return queue;
    }

}
