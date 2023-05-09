package de.trustable.ca3s.acmeproxy.service.dto;


import java.io.Serializable;
import java.util.Arrays;

public class RemoteRequestProxyConfigView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String requestProxyUrl;

    private Boolean active;

    private String[] acmeRealmArr;


    public RemoteRequestProxyConfigView(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestProxyUrl() {
        return requestProxyUrl;
    }

    public void setRequestProxyUrl(String requestProxyUrl) {
        this.requestProxyUrl = requestProxyUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String[] getAcmeRealmArr() {
        return acmeRealmArr;
    }

    public void setAcmeRealmArr(String[] acmeRealmArr) {
        this.acmeRealmArr = acmeRealmArr;
    }

    @Override
    public String toString() {
        return "RemoteRequestProxyConfigView{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", requestProxyUrl='" + requestProxyUrl + '\'' +
            ", active=" + active +
            ", acmeRealmArr=" + Arrays.toString(acmeRealmArr) +
            '}';
    }
}
