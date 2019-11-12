package org.icde.juriedu.search;

import org.icde.juriedu.model.Entry;

import java.util.List;

public class OutputObject {

    private List<Entry> result;

    private String requestId;

    public List<Entry> getResult() {
        return result;
    }

    public String getRequestId() {
        return requestId;
    }

    public OutputObject setResult(List<Entry> result) {
        this.result = result;
        return this;
    }

    public OutputObject setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
