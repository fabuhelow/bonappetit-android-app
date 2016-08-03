package com.github.clboettcher.bonappetit.app.service.staffmembers;

public class StaffMembersUpdateFailedEvent {
    private Throwable throwable;
    private Integer httpCode;
    private String httpMessage;

    public StaffMembersUpdateFailedEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public StaffMembersUpdateFailedEvent(Integer httpCode, String httpMessage) {
        this.httpCode = httpCode;
        this.httpMessage = httpMessage;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StaffMembersUpdateFailedEvent{");
        sb.append("throwable=").append(throwable);
        sb.append(", httpCode=").append(httpCode);
        sb.append(", httpMessage='").append(httpMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
