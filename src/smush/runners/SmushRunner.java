package smush.runners;

import com.abhyrama.smushit.SmushImages;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.io.FileUtils;
import semblance.results.ErrorResult;
import semblance.results.IResult;
import semblance.runners.MultiThreadRunner;
import semblance.runners.Runner;
import static semblance.runners.Runner.callRunnerSequence;

/**
 * Optimises JPEG and PNG files as part of a CI System
 *
 * https://github.com/depsypher/pngtastic
 * https://code.google.com/p/pngtastic/downloads/list
 *
 * https://github.com/abhirama/smushit
 *
 * @author kyleb2
 */
public class SmushRunner extends MultiThreadRunner {

    public static final String KEY_IMAGE_DIR = "dir";
    public static final String KEY_USE_WEB_SERVICE = "useSmushItService";

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
        String imageDir = (String) getConfigValue(KEY_IMAGE_DIR, "./");
        Map<String, File> files = FileUtils.listFiles(imageDir);
        for (File image : files.values()) {
            String path = image.getAbsolutePath();
            if (path.endsWith(".png")) {
                queue.add(new PngRunner(image));
            }
        }
        return queue;
    }

    @Override
    public List<IResult> call() throws Exception, Error {
        results = super.call();
        Boolean useSmushItService = (Boolean) getConfigValue(KEY_USE_WEB_SERVICE, false);
        String imageDir = (String) getConfigValue(KEY_IMAGE_DIR, "./");
        if (useSmushItService) {
            Set<String> extensions = new HashSet<String>();
            extensions.add("jpg");
            extensions.add("jpeg");
            extensions.add("png");
            extensions.add("gif");
            SmushImages smush = new SmushImages(imageDir, extensions);
            smush.setVerbose(false);
            smush.setDryRun(false);
            try {
                smush.smush();
            } catch (Exception ex) {
                Logger.getLogger(SmushRunner.class.getName()).log(Level.SEVERE, null, ex);
                results.add(new ErrorResult("Error using SmushIt service", ex.getMessage()));
            }
        }
        return results;
    }

}
