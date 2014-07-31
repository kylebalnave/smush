package smush.runners;

import com.abhyrama.smushit.SmushImages;
import com.abhyrama.smushit.SmushItResultVo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.results.ErrorResult;
import semblance.results.IResult;
import semblance.results.PassResult;
import semblance.runners.Runner;
import static semblance.runners.Runner.callRunnerSequence;

/**
 * Optimises JPEG and PNG files as part of a CI System Wraps
 * https://github.com/abhirama/smushit to use with Semblance
 *
 * @author kyleb2
 */
public class SmushRunner extends Runner implements Callable<List<IResult>> {

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
    public List<IResult> call() throws Exception {
        results = new ArrayList<IResult>();
        String imageDir = (String) getConfigValue(SmushRunner.KEY_IMAGE_DIR, "./");
        Set<String> extensions = new HashSet<String>();
        extensions.add("jpg");
        extensions.add("jpeg");
        extensions.add("png");
        extensions.add("gif");
        SmushImages smush = new SmushImages(imageDir, extensions);
        smush.setVerbose(false);
        smush.setDryRun(false);
        try {
            long start = System.currentTimeMillis();
            List<SmushItResultVo> smushResults = smush.smush();
            results.add(new PassResult(String.format("SmushIt successfull returned images in %sms", System.currentTimeMillis() - start)));
            for (SmushItResultVo sResult : smushResults) {
                results.add(new PassResult(String.format("SmushIt saved %s%s", sResult.getSavingPercentage(), "%")));
            }
        } catch (IOException ex) {
            Logger.getLogger(SmushRunner.class.getName()).log(Level.SEVERE, null, ex);
            results.add(new ErrorResult("Exception using SmushIt service in " + imageDir, ex.getMessage()));
        }
        return results;
    }

}
