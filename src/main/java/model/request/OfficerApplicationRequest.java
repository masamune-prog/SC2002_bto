package model.request;
import java.util.Map;
public class OfficerApplicationRequest implements Request{
    private String requestID;
    private String projectID;
    private RequestStatus status;
    private String managerID;

    public OfficerApplicationRequest(String ID, String projectID, RequestStatus status, String managerID) {
        this.requestID = ID;
        this.projectID = projectID;
        this.status = status;
        this.managerID = managerID;
    }
    public OfficerApplicationRequest(Map<String, String> map) {
        fromMap(map);
    }
    @Override
    public String getID() {
        return requestID;
    }

    @Override
    public String getProjectID() {
        return projectID;
    }

    @Override
    public RequestStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.OFFICER_REQUEST;
    }

    @Override
    public String getManagerID() {
        return managerID;
    }

    @Override
    public String getDisplayableString() {
        return getSplitter() + "\n" +
                String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", getRequestType()) +
                String.format("| %-18s | %-36s |\n", "Request Status", status.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Manager ID", managerID) +
                getSplitter();
    }
}
