import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// This class contains the Manager for the database of the program. All database manipulation will be done through this class.

public class DatabaseManager {

    // Database object for use of singleton pattern.

    private static DatabaseManager manager = null;


     //Connection object for connecting to database.

    private Connection connection = null;

    // Statement object to query statements to database.

    private Statement statement = null;

    // Integer value to retrieve total rows edited from database.

    private int rowsAffected;

    //Return current instance of Database Manager.

    public static DatabaseManager getInstance() {
        if (manager == null) {
            manager = new DatabaseManager();
        }
        return manager;
    }

    // This method signs the user into the database.

    public void signIn(String username, String password) throws SQLException {

        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/poiseprojects_db?useSSL=false",
                "root",
                "MufasaxZuko94155");
        statement = connection.createStatement();
        System.out.println("User logged into database.");
    }

    //This method retrieves all the projects from the projects tables in the database and return it in a list.

    public List<Project> retrieveProjects() {
        // Initialise all variables and object to retrieve values.
        List<Project> projectsList = new ArrayList<>();
        Person contractor = null;
        Person architect = null;
        Person customer = null;

        List<String> number = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> buildingType = new ArrayList<>();
        List<String> address = new ArrayList<>();
        List<Float> totalFee = new ArrayList<>();
        List<Float> paidToDate = new ArrayList<>();
        List<String> deadline = new ArrayList<>();
        List<String> completionDate = new ArrayList<>();
        List<String> contractorName = new ArrayList<>();
        List<String> architectName = new ArrayList<>();
        List<String> customerName = new ArrayList<>();
        List<String> isFinalised = new ArrayList<>();

        try {
            // Retrieve all data from projects table and read to their respective lists.

            ResultSet results = statement.executeQuery("SELECT * FROM projects");
            int index = 0;
            while (!results.isClosed() && results.next()) {

                number.add(index, results.getString("number"));
                name.add(index, results.getString("Name"));
                buildingType.add(index, results.getString("BuildingType"));
                address.add(index, results.getString("Address"));
                totalFee.add(index, results.getFloat("TotalFee"));
                paidToDate.add(index, results.getFloat("PaidToDate"));
                deadline.add(index, String.valueOf(results.getDate("DeadLine")));
                completionDate.add(index, String.valueOf(results.getDate("CompletionDate")));
                contractorName.add(index, results.getString("ContractorName"));
                architectName.add(index, results.getString("ArchitectName"));
                customerName.add(index, results.getString("CustomerName"));
                isFinalised.add(index, results.getString("Finalised"));
                index++;
            }
            results.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        // For each project retrieve Person information from their respective tables.
        try{
            for(int x =0; x < number.size(); x++) {
                ResultSet personQuery = null;
                for (int i = 0; i < 3; i++) {
                    switch (i) {
                        case 0 -> personQuery = statement.executeQuery(
                                String.format("SELECT * FROM Contractors WHERE name = '%s'", contractorName.get(x)));
                        case 1 -> personQuery = statement.executeQuery(
                                String.format("SELECT * FROM Architects WHERE name = '%s'", architectName.get(x)));
                        case 2 -> personQuery = statement.executeQuery(
                                String.format("SELECT * FROM Customers WHERE name = '%s'", customerName.get(x)));
                    }
                    String telNum = "";
                    String email = "";
                    String personAddress = "";
                    if (personQuery.next()) {
                        telNum = personQuery.getString("Tel");
                        email = personQuery.getString("Email");
                        personAddress = personQuery.getString("Address");
                    }
                    switch (i) {
                        case 0 -> contractor = new Person("Contractor", contractorName.get(x), telNum, email, personAddress);
                        case 1 -> architect = new Person("Architect", architectName.get(x), telNum, email, personAddress);
                        case 2 -> customer = new Person("Customer", customerName.get(x), telNum, email, personAddress);
                    }
                }
                personQuery.close();

                // Retrieve ERFNumber from ERFNumber table.
                String erfNumber = "";
                ResultSet erfNumbers = statement.executeQuery(
                        String.format("SELECT * FROM ERFNumbers WHERE Address = '%s'", address.get(x)));
                if (erfNumbers.next()) {
                    erfNumber = erfNumbers.getString("ERFNumber");
                }
                erfNumbers.close();

                // Create project object and add to project list.
                projectsList.add(new Project(number.get(x), name.get(x), buildingType.get(x),
                        address.get(x), erfNumber, totalFee.get(x), paidToDate.get(x), deadline.get(x),
                        contractor, architect, customer));


                /* Add the rest of the project information
                 *  that is not being added with constructor.*/
                projectsList.get(x).setFinalisedOnly(Boolean.parseBoolean(isFinalised.get(x)));
                if(completionDate.get(x) != null) {
                    projectsList.get(x).setCompletionDateOnly(completionDate.get(x));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error! Error reading projects from database.");
        }
        return projectsList;
    }

    // This method adds a new project to the projects table in the database.

    public void addProject(String number, String name, String buildingType, String Address,
                           float totalFees, float paidToDate, String deadline,
                           String completionDate, String contractor, String architect,
                           String customer) {

        try {
            String prepStatement = "insert into projects (Number, Name, BuildingType, Address, TotalFee, PaidToDate, DeadLine, CompletionDate, ContractorName, ArchitectName, CustomerName) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement1 = connection.prepareStatement(prepStatement);
            statement1.setString(1, number);
            statement1.setString(2, name);
            statement1.setString(3, buildingType);
            statement1.setString(4, Address);
            statement1.setFloat(5, totalFees);
            statement1.setFloat(6, paidToDate);
            statement1.setString(7, deadline);
            statement1.setString(8, completionDate);
            statement1.setString(9, contractor);
            statement1.setString(10, architect);
            statement1.setString(11, customer);
            statement1.execute();
            System.out.println("Project added.");
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            System.out.println("Project already exists.");
        } catch (SQLException e) {
            System.out.println("Error adding project!");
        }
    }

    // This method allow you to update string values of a project(identified by number) in the projects table.

    public void updateProject(String number, String column, String newValue) {
        try {
            String statementStr = "UPDATE projects SET " + column + " = ? WHERE Number = ?";
            PreparedStatement prepStatement = connection.prepareStatement(statementStr);
            prepStatement.setString(1, newValue);
            prepStatement.setString(2, number);
            prepStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This method allow you to update float values of a project(identified by number) in the projects table.

    public void updateProject(String number, String column, float newValue) {
        try {
            String statementStr = "UPDATE projects SET " + column + " = ? WHERE Number = ?";
            PreparedStatement prepStatement = connection.prepareStatement(statementStr);
            prepStatement.setFloat(1, newValue);
            prepStatement.setString(2, number);
            prepStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This method updates a Person objects name in its respective table in the database.

    public void updatePersonName(String table, String name, Person person, String newValue) {
        try {
            String statementStr = "";
            // Create statement for table to be edited.
            switch (table) {
                case "Architects" -> {
                    addArchitect(newValue, person.getTelNumber(), person.getEmailAddress(), person.getPhysicalAddress());
                    statementStr = "UPDATE Projects SET ArchitectName = ? WHERE ArchitectName = ?";
                }
                case "Contractors" -> {
                    addContractor(newValue, person.getTelNumber(), person.getEmailAddress(), person.getPhysicalAddress());
                    statementStr = "UPDATE Projects SET ContractorName = ? WHERE ContractorName = ?";
                }
                case "Customer" -> {
                    addCustomer(newValue, person.getTelNumber(), person.getEmailAddress(), person.getPhysicalAddress());
                    statementStr = "UPDATE Projects SET CustomerName = ? WHERE CustomerName = ?";
                }
            }
            PreparedStatement prepStatement = connection.prepareStatement(statementStr);
            prepStatement.setString(1, newValue);
            prepStatement.setString(2, name);
            prepStatement.execute();
            statement.executeUpdate("delete from " + table + " where name = '" + name + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This method updates a column in the Person objects respective table in the database.

    public void updatePerson(String table, String name, String column, String newValue) {
        try {
            String statementStr = "UPDATE " + table + " SET " + column + " = ? WHERE Name = ?";
            PreparedStatement prepStatement = connection.prepareStatement(statementStr);
            prepStatement.setString(1, newValue);
            prepStatement.setString(2, name);
            prepStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This method add a Contractor to the customer table in the database.


    public void addContractor(String name, String telNum, String email, String physicalAddress) throws SQLException {
        statement.executeUpdate(String.format("INSERT INTO Contractors VALUES ('%s', '%s', '%s', '%s')",
                name, telNum, email, physicalAddress));
    }

    // This method add an Architect to the customer table in the database.

    public void addArchitect(String name, String telNum, String email, String physicalAddress) throws SQLException {
        statement.executeUpdate(String.format("INSERT INTO Architects VALUES ('%s', '%s', '%s', '%s')",
                name, telNum, email, physicalAddress));
    }

    // This method add a Customer to the customer table in the database.

    public void addCustomer(String name, String telNum, String email, String physicalAddress) throws SQLException {
        statement.executeUpdate(String.format("INSERT INTO Customers VALUES ('%s', '%s', '%s', '%s')",
                name, telNum, email, physicalAddress));
    }

    /**
     *
     * This method updates an address in the ERFNumbers table.
     *
     * @param address Address to update
     * @param erfNumber erfNumber of address
     * @param newValue new Address to set old Address to
     */
    public void updateAddress(String address, String erfNumber, String newValue) {

        try {
            addERFNumber(newValue,erfNumber);
            String statementStr = "UPDATE Projects SET Address = ? WHERE Address = ?";
            PreparedStatement prepStatement = connection.prepareStatement(statementStr);
            prepStatement.setString(1, newValue);
            prepStatement.setString(2, address);
            prepStatement.execute();
            statement.executeUpdate("delete from ERFNumbers where Address = '" + address + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method update a current ERFNumber to a new one.
     *
     * @param address Address of erfNumber ot be changed
     * @param newValue new ErfNumber to set current one to
     */
    public void updateERFNumber(String address, String newValue) {

        try {
            String statementStr = "UPDATE ERFNumbers SET ERFNumbers = ? WHERE Address = ?";
            PreparedStatement prepStatement = connection.prepareStatement(statementStr);
            prepStatement.setString(1, newValue);
            prepStatement.setString(2, address);
            prepStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method adds a new ERFNumber ot the ERFNumber table in the database.
     *
     * @param address Address of erf number ot change
     * @param erfNumber new Erf number
     */
    public void addERFNumber(String address, String erfNumber) throws SQLException {
        statement.executeUpdate(String.format("INSERT INTO ERFNumbers VALUES ('%s', '%s')",
                address, erfNumber));
    }
}