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
package com.dailyselfie;

import com.dailyselfie.client.SelfieSvcApi;
import com.dailyselfie.repository.SelfieRecord;
import com.dailyselfie.repository.SelfieRepository;
import com.dailyselfie.repository.SelfieStatus;
import com.dailyselfie.utils.FilterService;
import com.dailyselfie.utils.SelfieFileManager;
import com.dailyselfie.utils.SelfieNotFoundException;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SelfieController {

    private static final AtomicLong CURRENT_ID = new AtomicLong(0L);
    private static final Logger LOGGER = LoggerFactory.getLogger(SelfieController.class);
    @Autowired
    FilterService filterService;
    @Autowired
    private SelfieRepository selfieCollection;

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = SelfieSvcApi.SELFIE_SVC_PATH, method = RequestMethod.GET)
    @ResponseBody
    public Collection<SelfieRecord> getSelfieList(Principal principal) {
        LOGGER.info("###################Selfie List : " + Lists.newArrayList(selfieCollection.findAll()));
        Collection<SelfieRecord> listRecords = Lists.newArrayList(selfieCollection.findAll());
        Collection<SelfieRecord> listRecordsUser = new ArrayList<SelfieRecord>();
        if (listRecords.size() == 0) {
            return listRecords;
        } else {
            for (SelfieRecord record : listRecords) {
                if (record.getUser().equals(principal.getName()))
                    listRecordsUser.add(record);
            }
            return listRecordsUser;
        }


    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = SelfieSvcApi.SELFIE_SVC_PATH, method = RequestMethod.POST)
    @ResponseBody
    public SelfieRecord addSelfie(@RequestBody SelfieRecord v, Principal principal) {

        LOGGER.info("###################Selfie object : " + v.getName() + ", " + v.getContentType());
        String user = principal.getName();
        LOGGER.info("###################User name : " + principal.getName());
        if (v.getId() <= 0) {
            v.setId(CURRENT_ID.incrementAndGet());
        }
        v.setUser(user);
        v.setId(CURRENT_ID.incrementAndGet());
        v.setDataUrl(getDataUrl(v.getId()));

        LOGGER.info("###################Selfie object : " + v.toString());
        return selfieCollection.save(v);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = SelfieSvcApi.SELFIE_DATA_PATH, method = RequestMethod.POST)
    @ResponseBody
    public SelfieStatus setSelfieData(
            @PathVariable(SelfieSvcApi.ID_PARAMETER) long id, Principal p,
            @RequestPart(SelfieSvcApi.DATA_PARAMETER) MultipartFile selfieData) {
        SelfieRecord v = selfieCollection.findOne(id);
        String user = p.getName();
        if (v != null && v.getUser().equals(user)) {
            try {
                SelfieFileManager.get().saveSelfieData(v, selfieData.getInputStream());
                return new SelfieStatus(SelfieStatus.SelfieState.READY);
            } catch (IOException e) {
                LOGGER.error("Error occured when trying to save selfie " + id, e);
                throw new InternalError(e);
            }
        }
        throw new SelfieNotFoundException();
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = SelfieSvcApi.SELFIE_DATA_PATH, method = RequestMethod.GET)
    public void getData(@PathVariable(SelfieSvcApi.ID_PARAMETER) long id, Principal p, HttpServletResponse response) {
        SelfieRecord v = selfieCollection.findOne(id);
        String user = p.getName();
        try {
            if (v != null && SelfieFileManager.get().hasSelfieData(v) && v.getUser().equals(user)) {
                SelfieFileManager.get().copySelfieData(v, response.getOutputStream());
                response.flushBuffer();
                return;
            }
            throw new SelfieNotFoundException();
        } catch (IOException e) {
            LOGGER.error("Error occured when trying to load selfie " + id, e);
            throw new InternalError(e);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = SelfieSvcApi.SELFIE_FILTER_PATH, method = RequestMethod.POST, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public void applyFilters(

            @RequestParam(SelfieSvcApi.FILTER_PARAMETER) List<String> filterCommands,
            @RequestPart(SelfieSvcApi.DATA_PARAMETER) MultipartFile selfieRecord,
            HttpServletResponse response) {
        LOGGER.info("in applyFilters");


        try {
            LOGGER.info(filterCommands.toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Future<Void> task = filterService.applyFilters(selfieRecord.getInputStream(), out, filterCommands);
            response.setContentType("image/jpeg");

            try {
                task.get();

                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().write(out.toByteArray());

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            LOGGER.error("Error occured when trying to save selfie " + e);
            throw new InternalError(e);
        }
        try {
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = SelfieSvcApi.SELFIE_DELETE_PATH, method = RequestMethod.POST)
    @ResponseBody
    public void delete(
            @PathVariable(SelfieSvcApi.ID_PARAMETER) long id, Principal p) {
        SelfieRecord v = selfieCollection.findOne(id);
        String user = p.getName();
        if (v != null && v.getUser().equals(user)) {
            try {
                LOGGER.info("%%%%%%Before " + selfieCollection.toString());

                SelfieFileManager.get().deleteFile(v);
                selfieCollection.delete(v);

                LOGGER.info("%%%%%%After " + selfieCollection.toString());
                return;
            } catch (Exception e) {
                LOGGER.error("Error occured when trying to save selfie " + id, e);
                throw new InternalError(e);
            }
        }
        throw new SelfieNotFoundException();
    }

    private String getDataUrl(long selfieId) {
        return getUrlBaseForLocalServer() + "/selfie/" + selfieId + "/data";
        //return "https://192.168.0.8:8443" + "/selfie/" + selfieId + "/data";
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String base = "https://"
                + request.getServerName()
                + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
        return base;
    }
}
