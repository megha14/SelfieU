package com.dailyselfie.model.mediator.webdata;

/**
 * This "Plain Ol' Java Object" (POJO) class represents meta-data of
 * interest downloaded in Json from the Selfie Service via the
 * SelfieServiceProxy.
 */
public class SelfieStatus {
    /**
     * State of the Selfie.
     */
    private SelfieState state;

    /**
     * Constructor that initializes all the fields of interest.
     */
    public SelfieStatus(SelfieState state) {
        super();
        this.state = state;
    }

    public SelfieState getState() {
        return state;
    }
    
    /*
     * Getters and setters to access SelfieStatus.
     */

    public void setState(SelfieState state) {
        this.state = state;
    }

    /**
     * Various fields corresponding to data downloaded in Json from
     * the Selfie WebService.
     */
    public enum SelfieState {
        READY,
        PROCESSING
    }
}
