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
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageOutputStream;
import semblance.results.ErrorResult;
import semblance.results.IResult;
import semblance.results.PassResult;
import semblance.runners.Runner;

/**
 *
 * @author kyleb2
 */
public class JpgRunner extends Runner implements Callable<List<IResult>> {

    private File fileToOptimize;

    public JpgRunner(Map config) {
        super(config);
    }

    public JpgRunner(File fileToOptimize) {
        super("");
        this.fileToOptimize = fileToOptimize;
    }

    // This method accepts quality levels between 0 (lowest) and 1 (highest) and simply converts
    // it to a range between 0 and 256; this is not a correct conversion algorithm.
    // However, a proper alternative is a lot more complicated.
    // This should do until the bug is fixed.
    public float setCompressionQuality(float quality) {
        if (quality < 0.0F || quality > 1.0F) {
            throw new IllegalArgumentException("Quality out-of-bounds!");
        }
        return 256 - (quality * 256);
    }

    @Override
    public List<IResult> call() throws Exception {
        results = new ArrayList<IResult>();
        File toDir = new File(fileToOptimize.getParent());
        long start = System.currentTimeMillis();
        try {
            toDir.mkdirs();
            BufferedImage bufferedImage = ImageIO.read(fileToOptimize);
            Iterator writers = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter imageWriter = (ImageWriter) writers.next();

            JPEGImageWriteParam params = new JPEGImageWriteParam(null);
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality((float) 1.0);
            // create tables

            //JPEGQTable[] qTables = {JPEGQTable.K1Luminance, JPEGQTable.K2Chrominance};
            //JPEGHuffmanTable[] dcTables = {JPEGHuffmanTable.StdDCLuminance, JPEGHuffmanTable.StdDCChrominance};
            //JPEGHuffmanTable[] acTables = {JPEGHuffmanTable.StdACLuminance, JPEGHuffmanTable.StdACChrominance};
            //params.setEncodeTables(qTables, dcTables, acTables);
           // IIOMetadata streamMetadata = ImageIO.getImageReaders(fileToOptimize).next().getImageMetadata(0);
            //System.out.println(ImageIO.getImageReaders(fileToOptimize).hasNext());

            // set other params
            params.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_DISABLED);
            params.setDestinationType(new ImageTypeSpecifier(IndexColorModel.getRGBdefault(), IndexColorModel.getRGBdefault().createCompatibleSampleModel(16, 16)));
            params.setOptimizeHuffmanTables(true);

            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(fileToOptimize);
            imageWriter.setOutput(imageOutputStream);
            imageWriter.write(null, new IIOImage(bufferedImage, null, null), params);
            imageOutputStream.close();
            imageWriter.dispose();
        } catch (Exception ex) {
            results.add(new ErrorResult(fileToOptimize.getAbsolutePath(), ex.getMessage(), ex.getMessage()));
        }
        results.add(new PassResult(String.format("Processed %s in %d milliseconds", fileToOptimize.getAbsolutePath(), System.currentTimeMillis() - start)));

        return results;
    }

}
