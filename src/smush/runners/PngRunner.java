/*
 * Copyright (C) 2014 kyleb2
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package smush.runners;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import semblance.results.ErrorResult;
import semblance.results.IResult;
import semblance.results.PassResult;
import semblance.runners.Runner;

/**
 *
 * @author kyleb2
 */
public class PngRunner extends Runner implements Callable<List<IResult>> {

    private File fileToOptimize;

    public PngRunner(Map config) {
        super(config);
    }

    public PngRunner(File fileToOptimize) {
        super("");
        this.fileToOptimize = fileToOptimize;
    }

    @Override
    public List<IResult> call() throws Exception {
        results = new ArrayList<IResult>();
        File toDir = new File(fileToOptimize.getParent());
        Boolean removeGamma = true;
        Integer compressionLevel = null; // try all (brute force)
        String compressor = null; // alternative compressor path
        String logLevel = "error";
        long start = System.currentTimeMillis();

        PngOptimizer optimizer = new PngOptimizer(logLevel);
        optimizer.setCompressor(compressor);

        try {
            toDir.mkdirs();
            PngImage image = new PngImage(fileToOptimize.getAbsolutePath());
            optimizer.optimize(image, fileToOptimize.getAbsolutePath(), removeGamma, compressionLevel);
        } catch (Exception ex) {
            results.add(new ErrorResult(fileToOptimize.getAbsolutePath(), ex.getMessage()));
        }
        results.add(new PassResult(String.format("Processed %s in %d milliseconds, saving %d bytes", fileToOptimize.getAbsolutePath(), optimizer.getStats().size(), System.currentTimeMillis() - start, optimizer.getTotalSavings())));

        return results;
    }

}
