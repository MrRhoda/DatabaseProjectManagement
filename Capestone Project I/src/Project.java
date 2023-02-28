import java.time.LocalDate;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.format.DateTimeFormatter;

public class Project {

    DatabaseManager databaseManager;

    //Attributes for Project class
    private final Person architect;
    private final Person contractor;
    private final Person customer;

    private String projectNumber;
    private final String name;
    private String buildingType;
    private String address;
    private String erfNumber;
    private String deadline;
    private final float totalProjectFees;
    private float amountPaidToDate;
    private boolean isFinalised;
    private String completionDate;



    // Constructor for Project class
    public Project(String projectNumber, String name, String buildingType,
                   String address, String erfNumber, float totalProjectFees,
                   float amountPaidToDate, String deadline, Person architect,
                   Person contractor, Person customer) {

        this.projectNumber = projectNumber;
        this.name = name;
        this.buildingType = buildingType;
        this.address = address;
        this.erfNumber = erfNumber;
        this.totalProjectFees = totalProjectFees;
        this.amountPaidToDate = amountPaidToDate;
        this.deadline = deadline;
        this.architect = architect;
        this.contractor = contractor;
        this.customer = customer;
        databaseManager = DatabaseManager.getInstance();
    }

    // methods used
    public String getName() {

        return name;
    }

    public Person getContractor() {

        return contractor;
    }

    public void setAmountPaidToDate(float newAmount) {
        databaseManager.updateProject(projectNumber,"PaidToDate", newAmount);
        this.amountPaidToDate = newAmount;
    }

    public void setDeadline(String newDeadline) {
        databaseManager.updateProject(projectNumber, "DeadLine", newDeadline);
        this.deadline = newDeadline;
    }

    public String getProjectNumber() {
        return projectNumber;}

    public boolean getIsFinalised() {
        return isFinalised; }

    public String getDeadline() {
        return deadline; }

    public Person getArchitect() {
        return architect; }

    public Person getCustomer() {
        return customer; }

    public String getBuildingType() {
        return buildingType; }

    public String getAddress() {
        return address; }

    public String getErfNumber() {
        return erfNumber; }

    public String getCompletionDate() {
        return completionDate; }

    public float getTotalProjectFees() {
        return totalProjectFees; }

    public float getAmountPaidToDate() {
        return amountPaidToDate; }

    public void setProjectNumber(String number){
        databaseManager.updateProject(projectNumber,"Number", number);
        this.projectNumber = number; }

    public void setCompletionDate(String completionDate) {
        databaseManager.updateProject(projectNumber, "CompletionDate", completionDate);
        this.completionDate = completionDate; }

    public void setCompletionDateOnly(String completionDate) {
        this.completionDate = completionDate;
    }

    public void setFinalised(boolean finalised) {
        String isFinal;
        if (finalised) {
            isFinal = "true";
        } else {
            isFinal = "false";
        }
        databaseManager.updateProject(projectNumber,"Finalised", isFinal);
        this.isFinalised = finalised; }

    public void setFinalisedOnly(boolean finalised) {
        this.isFinalised = finalised;
    }

    public void setBuildingType(String buildingType) {
        databaseManager.updateProject(projectNumber,"BuildingType", buildingType);
        this.buildingType = buildingType; }

    public void setAddress(String address) {
        databaseManager.updateAddress(this.address, erfNumber, address);
        this.address = address; }

    public void setErfNumber(String erfNumber) {
        databaseManager.updateERFNumber(address, erfNumber);
        this.erfNumber = erfNumber; }

    public void finaliseProject() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        setCompletionDate(dateFormatter.format(LocalDate.now()));
        if(getTotalProjectFees() - getAmountPaidToDate() > 0) {
            generateInvoice();
        }
        setFinalised(true);
        System.out.println("Project Completed.");
    }


    // We generate the invoice in a way to be saved to a text.file
    public void generateInvoice() {
        String invoiceDirectory = "invoices";
        float amountToBePaid = getTotalProjectFees() - getAmountPaidToDate();
        FileWriter fileWriter = null;
        if(amountToBePaid > 0) {

            // We construct the invoice
            String invoice = "Customer: " + getCustomer().getName();
            invoice += "\nTel number: " + getCustomer().getTelNumber();
            invoice += "\nEmail: " + getCustomer().getEmailAddress();
            invoice += "\nCompletion Date: " + getCompletionDate();
            invoice += "\n\nTotal amount to be paid: R" + amountToBePaid;
            System.out.println(invoice);
            try {
               // File is created, and we save the invoice to the file
               new File(invoiceDirectory).mkdirs();
               invoiceDirectory += "\\";
               invoiceDirectory += name + ".txt";
               File invoiceFile = new File(invoiceDirectory);
               fileWriter = new FileWriter(invoiceFile);
               fileWriter.write(invoice + "\n");

            }  catch (NullPointerException | IOException e) {
               System.out.println("There is a file error");
               e.printStackTrace();
            }  finally {
                if(fileWriter != null) {
                    try {
                      fileWriter.close();
                    } catch (IOException e) {
                        System.out.println("The invoice file error will be closed");
                    }
                }
            }
        }
    }

    // Printing the data to console in a readable format
    public String toString() {
        return  ("****************************************************"
                + "\nProject name: " + name
                + "\nProject number: " + projectNumber
                + "\nBuilding type: " + getBuildingType()
                + "\nAddress: " + getAddress()
                + "\nERF Number: " + getErfNumber()
                + "\nDue date: " + deadline
                + "\nArchitect: " + getArchitect().getName()
                + "\n\tTel number:" + getArchitect().getTelNumber()
                + "\n\tEmail:" + getArchitect().getEmailAddress()
                + "\nContractor: " + getContractor().getName()
                + "\n\tTel number:" + getContractor().getTelNumber()
                + "\n\tEmail:" + getContractor().getEmailAddress()
                + "\nCustomer: " + getCustomer().getName()
                + "\n\tTel number:" + getCustomer().getTelNumber()
                + "\n\tEmail:" + getCustomer().getEmailAddress()
                + "\nTotal project fee: R" + getTotalProjectFees()
                + "\nTotal paid to date: R" + getAmountPaidToDate()
                + "\nProject finalised: " + isFinalised
                + "\nCompletion date: " + getCompletionDate()
                + "\n****************************************************");
    }
}
