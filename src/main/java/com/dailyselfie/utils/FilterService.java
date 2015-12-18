package com.dailyselfie.utils;

import com.dailyselfie.jhlabs.image.ChromeFilter;
import com.dailyselfie.jhlabs.image.GaussianFilter;
import com.dailyselfie.jhlabs.image.GlowFilter;
import com.dailyselfie.jhlabs.image.GrayscaleFilter;
import com.dailyselfie.jhlabs.image.ImageUtils;
import com.dailyselfie.jhlabs.image.InvertFilter;
import com.dailyselfie.jhlabs.image.PointFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;


@Service
public class FilterService {
    private static final Logger logger = LoggerFactory.getLogger(FilterService.class);


    public FilterService() {

    }


    @Async
    public Future<Void> applyFilters(InputStream inputStream, OutputStream out,
                                     List<String> commandList) throws IOException {
        //Perform the filter tasks...
        BufferedImage src = null;
        //BufferedImage target = null;

        src = ImageIO.read(inputStream);
        src = ImageUtils.convertImageToARGB(src);

        List<String> command = commandList;
        for (int i = 0; i < command.size(); i++) {
            String filterName = command.get(i);
            if (filterName.equals("Grayscale")) {
                PointFilter grayScaleFilter = new GrayscaleFilter();
                src = grayScaleFilter.filter(src, src);

            } else if (filterName.equals("Invert")) {
                PointFilter invertFilter = new InvertFilter();
                src = invertFilter.filter(src, src);
            } else if (filterName.equals("Gaussian")) {
                GaussianFilter gaussian = new GaussianFilter(5);
                src = gaussian.filter(src, src);
            } else if (filterName.equals("Chrome")) {
                ChromeFilter chromeFilter = new ChromeFilter();
                chromeFilter.setAmount(0.3f);
                chromeFilter.setExposure(0.3f);
                src = chromeFilter.filter(src, src);
            } else if (filterName.equals("Glow")) {
                GlowFilter glowFilter = new GlowFilter();
                src = glowFilter.filter(src, src);
            } else {
                logger.info("error");
            }
        }

        ImageIO.write(src, "jpg", out);

        return new AsyncResult<>(null);
    }
}