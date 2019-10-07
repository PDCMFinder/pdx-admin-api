package org.pdxfinder.config.jwt;


import java.util.List;
import java.util.Map;

public class JwtUser {
    private String userName;
    private long id;
    private String role;
    private Map<String, Object> organization;
    private List<Map> previledges;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, Object> getOrganization() {
        return organization;
    }

    public void setOrganization(Map<String, Object> organization) {
        this.organization = organization;
    }

    public List<Map> getPreviledges() {
        return previledges;
    }

    public void setPreviledges(List<Map> previledges) {
        this.previledges = previledges;
    }


}
