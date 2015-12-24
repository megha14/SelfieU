package com.dailyselfie.integration.test;

import com.dailyselfie.client.SecuredRestBuilder;
import com.dailyselfie.client.SelfieSvcApi;
import com.dailyselfie.repository.SelfieRecord;
import com.dailyselfie.repository.SelfieStatus;
import com.dailyselfie.selfie.TestData;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A test for the Asgn2 selfie service
 *
 * @author mitchell
 */
public class SelfieSvcApiTest {

    private final String TEST_URL = "https://192.168.0.8:8443";
    private final String USERNAME1 = "admin@example.com";
    private final String USERNAME2 = "user0@example.com";
    private final String PASSWORD = "pass";
    private final String CLIENT_ID = "mobile";
    private SelfieSvcApi readWriteSelfieSvcUser1 = new SecuredRestBuilder()
            .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + SelfieSvcApi.TOKEN_PATH)
            .setLogLevel(LogLevel.FULL)
            .setUsername(USERNAME1).setPassword(PASSWORD).setClientId(CLIENT_ID)
            .build().create(SelfieSvcApi.class);
    private SelfieSvcApi readWriteSelfieSvcUser2 = new SecuredRestBuilder()
            .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + SelfieSvcApi.TOKEN_PATH)
                    //.setLogLevel(LogLevel.FULL)
            .setUsername(USERNAME2).setPassword(PASSWORD).setClientId(CLIENT_ID)
            .build().create(SelfieSvcApi.class);
    private SelfieRecord selfieRecord = TestData.randomSelfie();
    private File testSelfieData = new File(
            "src/test/java/resources/test.jpg");

    @Test
    public void testAddSelfieMetadata() throws Exception {

        SelfieRecord received = readWriteSelfieSvcUser1.addSelfie(selfieRecord);


        assertTrue(received.getId() > 0);
        assertTrue(received.getDataUrl() != null);
    }

    @Test
    public void testAddGetSelfie() throws Exception {
        readWriteSelfieSvcUser1.addSelfie(selfieRecord);
        Collection<SelfieRecord> stored = readWriteSelfieSvcUser1.getSelfieList();
        assertTrue(stored.contains(selfieRecord));
    }

    @Test
    public void testDenySelfieAddWithoutOAuth() throws Exception {
        ErrorRecorder error = new ErrorRecorder();

        // Create an insecure version of our Rest Adapter that doesn't know how
        // to use OAuth.
        SelfieSvcApi insecureselfieService = new RestAdapter.Builder()
                .setClient(
                        new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
                .setErrorHandler(error).build().create(SelfieSvcApi.class);
        try {
            // This should fail because we haven't logged in!
            insecureselfieService.addSelfie(selfieRecord);

            fail("Yikes, the security setup is horribly broken and didn't require the user to authenticate!!");

        } catch (Exception e) {
            // Ok, our security may have worked, ensure that
            // we got a 401
            assertEquals(HttpStatus.SC_UNAUTHORIZED, error.getError()
                    .getResponse().getStatus());
        }

        // We should NOT get back the selfie that we added above!
        Collection<SelfieRecord> selfies = readWriteSelfieSvcUser1.getSelfieList();
        assertFalse(selfies.contains(selfieRecord));
    }

    @Test
    public void testAddSelfieData() throws Exception {
        SelfieRecord received = readWriteSelfieSvcUser1.addSelfie(selfieRecord);
        SelfieStatus status = readWriteSelfieSvcUser1.setSelfieData(received.getId(),
                new TypedFile(received.getContentType(), testSelfieData));
        assertEquals(SelfieStatus.SelfieState.READY, status.getState());

        Response response = readWriteSelfieSvcUser1.getData(received.getId());
        assertEquals(200, response.getStatus());

        InputStream selfieData = response.getBody().in();
        byte[] originalFile = IOUtils.toByteArray(new FileInputStream(testSelfieData));
        byte[] retrievedFile = IOUtils.toByteArray(selfieData);
        assertTrue(Arrays.equals(originalFile, retrievedFile));
    }

    @Test
    public void testGetNonExistantSelfiesData() throws Exception {

        long nonExistantId = getInvalidSelfieId();

        try {
            Response r = readWriteSelfieSvcUser1.getData(nonExistantId);
            assertEquals(404, r.getStatus());
        } catch (RetrofitError e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    @Test
    public void testAddNonExistantSelfiesData() throws Exception {
        long nonExistantId = getInvalidSelfieId();
        try {
            readWriteSelfieSvcUser1.setSelfieData(nonExistantId, new TypedFile(selfieRecord.getContentType(), testSelfieData));
            fail("The client should receive a 404 error code and throw an exception if an invalid"
                    + " selfieRecord ID is provided in setSelfieData()");
        } catch (RetrofitError e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    @Test
    public void testApplyFilter() throws Exception {

        List<String> filterCommandList = new ArrayList<String>();
        filterCommandList.add("Grayscale");
        filterCommandList.add("Chrome");
//		logger.info("##Auto "+filterCommandList.get(0).getFilterName());
        Response response1 = readWriteSelfieSvcUser1.applyFilters(filterCommandList, new TypedFile("image/jpeg", testSelfieData));
        assertEquals(200, response1.getStatus());
        InputStream selfieData = response1.getBody().in();
        byte[] originalFile = IOUtils.toByteArray(new FileInputStream(testSelfieData));
        byte[] retrievedFile = IOUtils.toByteArray(selfieData);
        assertTrue(!(Arrays.equals(originalFile, retrievedFile)));

    }

    private long getInvalidSelfieId() {
        Set<Long> ids = new HashSet<Long>();
        Collection<SelfieRecord> stored = readWriteSelfieSvcUser1.getSelfieList();
        for (SelfieRecord v : stored) {
            ids.add(v.getId());
        }

        long nonExistantId = Long.MIN_VALUE;
        while (ids.contains(nonExistantId)) {
            nonExistantId++;
        }
        return nonExistantId;
    }

    private class ErrorRecorder implements ErrorHandler {

        private RetrofitError error;

        @Override
        public Throwable handleError(RetrofitError cause) {
            error = cause;
            return error.getCause();
        }

        public RetrofitError getError() {
            return error;
        }
    }

}
