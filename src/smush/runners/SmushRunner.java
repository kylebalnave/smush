package smush.runners;

import java.util.List;
import java.util.Map;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
