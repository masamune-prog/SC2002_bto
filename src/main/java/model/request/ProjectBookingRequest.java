package model.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectBookingRequest implements Request {
    private String requestID;
    private String projectID;
    private RequestStatus status;
    private String managerID;
    private List<String> officerIDs;
    private String applicantID;
    private String originalRequestID;
    private String roomType;
    private LocalDate bookingDate;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ProjectBookingRequest(String requestID, String projectID, RequestStatus status,
                                 String managerID, List<String> officerIDs, String applicantID,
                                 String originalRequestID, String roomType, LocalDate bookingDate) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.status = status;
        this.managerID = managerID;
        this.officerIDs = officerIDs;
        this.applicantID = applicantID;
        this.originalRequestID = originalRequestID;
        this.roomType = roomType;
        this.bookingDate = bookingDate;
    }

    public ProjectBookingRequest(Map<String, String> map) {
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
        return RequestType.PROJECT_BOOKING_REQUEST;
    }

    @Override
    public String getManagerID() {
        return managerID;
    }

    public List<String> getOfficerIDs() {
        return officerIDs;
    }

    public String getApplicantID() {
        return applicantID;
    }

    public String getOriginalRequestID() {
        return originalRequestID;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    @Override
    public String getDisplayableString() {
        return getSplitter() + "\n" +
                String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", getRequestType()) +
                String.format("| %-18s | %-36s |\n", "Request Status", status.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Manager ID", managerID) +
                String.format("| %-18s | %-27s |\n", "Officer ID", officerIDs) +
                String.format("| %-18s | %-27s |\n", "Applicant ID", applicantID) +
                String.format("| %-18s | %-27s |\n", "Original Request", originalRequestID) +
                String.format("| %-18s | %-27s |\n", "Room Type", roomType) +
                String.format("| %-18s | %-27s |\n", "Booking Date", bookingDate) +
                getSplitter();
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.requestID = map.get("requestID");
        this.projectID = map.get("projectID");
        this.status = map.get("status") != null ? 
                RequestStatus.valueOf(map.get("status")) : RequestStatus.PENDING;
        this.managerID = map.get("managerID");
        this.officerIDs = map.get("officerID") != null ? List.of(map.get("officerID").split(",")) : null;
        this.applicantID = map.get("applicantID");
        this.originalRequestID = map.get("originalRequestID");
        this.roomType = map.get("roomType");
        
        if (map.get("bookingDate") != null) {
            try {
                this.bookingDate = LocalDate.parse(map.get("bookingDate"), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing booking date: " + map.get("bookingDate"));
                this.bookingDate = null;
            }
        } else {
            this.bookingDate = null;
        }
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("requestID", requestID);
        map.put("projectID", projectID);
        map.put("status", status != null ? status.toString() : null);
        map.put("managerID", managerID);
        map.put("officerID", officerIDs != null ? String.join(",", officerIDs) : null);
        map.put("applicantID", applicantID);
        map.put("originalRequestID", originalRequestID);
        map.put("roomType", roomType);
        map.put("bookingDate", bookingDate != null ? bookingDate.format(DATE_FORMATTER) : null);
        map.put("requestType", getRequestType().toString());
        return map;
    }
}