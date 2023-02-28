public class Person {

    //Attributes

    DatabaseManager databaseManager;
    private final String jobType;
    private String name;
    private String telNumber;
    private String emailAddress;
    private String physicalAddress;
    private final String tableName;

    // Constructor for the Person class
    public Person(String jobType, String name, String telNumber, String emailAddress,
                  String physicalAddress) {
        this.jobType = jobType;
        this.name = name;
        this.telNumber = telNumber;
        this.emailAddress = emailAddress;
        this.physicalAddress = physicalAddress;
        databaseManager = DatabaseManager.getInstance();
        tableName = jobType + "s";
    }

    // methods used
    public String getName() {
        return name;
    }

    public String getJobType() {
        return jobType;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }
    public void setTelNumber(String newTelNumber) {
        databaseManager.updatePerson(tableName, name, "Tel", newTelNumber);
        this.telNumber = newTelNumber;
    }

    public void setEmailAddress(String newEmailAddress) {
        databaseManager.updatePerson(tableName, name, "Email", newEmailAddress);
        this.emailAddress = newEmailAddress;
    }
    public void setName(String newName) {
        databaseManager.updatePersonName(tableName, name, this, newName);
        this.name = newName;
    }
    public void setPhysicalAddress(String newAddress) {
        databaseManager.updatePerson(tableName, name, "Address", newAddress);
        this.physicalAddress = newAddress;
    }
}