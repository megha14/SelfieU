/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.dailyselfie.utils;


import com.dailyselfie.repository.SelfieRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class provides a simple implementation to store selfie binary
 * data on the file system in a "selfies" folder. The class provides
 * methods for saving selfies and retrieving their binary data.
 *
 * @author jules
 */
public class SelfieFileManager {

    private Path targetDir_ = Paths.get("selfie");

    // The SelfieFileManager.get() method should be used
    // to obtain an instance
    private SelfieFileManager() throws IOException {
        if (!Files.exists(targetDir_)) {
            Files.createDirectories(targetDir_);
        }
    }

    /**
     * This static factory method creates and returns a
     * SelfieFileManager object to the caller. Feel free to customize
     * this method to take parameters, etc. if you want.
     *
     * @return
     * @throws IOException
     */
    public static SelfieFileManager get() throws IOException {
        return new SelfieFileManager();
    }

    // Private helper method for resolving selfie file paths
    public Path getSelfiePath(SelfieRecord v) {
        assert (v != null);

        return targetDir_.resolve("selfie" + v.getId() + ".jpg");
    }

    /**
     * This method returns true if the specified SelfieRecord has binary
     * data stored on the file system.
     *
     * @param v
     * @return
     */
    public boolean hasSelfieData(SelfieRecord v) {
        Path source = getSelfiePath(v);
        return Files.exists(source);
    }

    /**
     * This method copies the binary data for the given SelfieRecord to
     * the provided output stream. The caller is responsible for
     * ensuring that the specified SelfieRecord has binary data associated
     * with it. If not, this method will throw a FileNotFoundException.
     *
     * @param v
     * @param out
     * @throws IOException
     */
    public void copySelfieData(SelfieRecord v, OutputStream out) throws IOException {
        Path source = getSelfiePath(v);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Unable to find the referenced selfie file for selfieId:" + v.getId());
        }
        Files.copy(source, out);
    }

    /**
     * This method reads all of the data in the provided InputStream and stores
     * it on the file system. The data is associated with the SelfieRecord object that
     * is provided by the caller.
     *
     * @param v
     * @param selfieData
     * @throws IOException
     */
    public void saveSelfieData(SelfieRecord v, InputStream selfieData) throws IOException {
        assert (selfieData != null);

        Path target = getSelfiePath(v);
        Files.copy(selfieData, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public File getFile(SelfieRecord v) throws IOException {
        Path source = getSelfiePath(v);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Unable to find the referenced selfie file for selfieId:" + v.getId());
        }
        return new File(String.valueOf(source));
    }

    public void deleteFile(SelfieRecord v) throws IOException {
        Path source = getSelfiePath(v);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Unable to find the referenced selfie file for selfieId:" + v.getId());
        }
        Files.delete(source);
    }
}
