/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.lotame.api;

/**
 *
 * @author Chris
 */
public class Audience {
    protected int id;
    protected String name;
    protected String targetingCode;
    protected long uniques;
    protected long pageViews;
    protected long opportunities;
    
    private Audience() {
        
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTargetingCode() { return targetingCode; }
    public long getUniques() { return uniques; }
    public long getPageViews() { return pageViews; }
    public long getOpportunities() { return opportunities; }
    
    public static class AudienceBuilder {
        Audience audience;
        
        public AudienceBuilder() {
            audience = new Audience();
        }
        
        public Audience build() {
            // TODO: Validate build...
            return audience;
        }
        
        public void setId(int id) { audience.id = id; }
        public void setName(String name) { audience.name = name; }
        public void setTargetingCode(String targetingCode) { audience.targetingCode = targetingCode; }
        public void setUniques(long uniques) { audience.uniques = uniques; }
        public void setPageViews(long pageViews) { audience.pageViews = pageViews; }
        public void setOpportunities(long opportunities) { audience.opportunities = opportunities; }
    }
}
