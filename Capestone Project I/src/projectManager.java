import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.sql.*;

public class projectManager {

    // Class array variable to store all projects

     //List of Project objects that stores all projects

    static List<Project> allProjects = new ArrayList<>();

    //List of Project objects that stores all incomplete projects
    static List<Project> incompleteProjects = new ArrayList<>();

    //List of Project objects that stores all overdue projects
    static List<Project> overDueProjects = new ArrayList<>();

    // Database object
    static DatabaseManager databaseManager;

    //Main loop allows user to ass the project manager menu.

    public static void main(String[] args) {
        // Objects
        Scanner input = new Scanner(System.in);

        databaseManager = DatabaseManager.getInstance();

        // Variables
        String menuOption = " ";

        // we retrieve projects

        try {
            databaseManager.signIn("root", "MufasaxZuko94155");
        } catch (SQLException e) {
            System.out.println("Could not connect to database.");
        }

        allProjects = databaseManager.retrieveProjects();

        while(!menuOption.equals("0")) {

            int selectedProject;

            drawLine();
            System.out.println("""
              Project Manager
              1: View All Projects
              2: View Incomplete Projects
              3: View Late Projects
              4: Search for Project
              5: Add Project
              0: Quit""");

            menuOption = input.next();
            input.nextLine();

            // View all projects
            switch (menuOption) {
                case "1":

                    while (true) {

                        while (true) {
                            try {

                                drawLine();
                                System.out.println("Select a project(Enter '0'to return to menu):");

                                for (int i = 0; i < allProjects.size(); i++) {
                                    if (allProjects.get(i) != null) {
                                        System.out.println((i + 1) + ": " + allProjects.get(i).getName());
                                    }
                                }
                                System.out.println();
                                selectedProject = Integer.parseInt(input.nextLine()) - 1;
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Option not selected!");
                            }
                        }
                        if (selectedProject == -1) {
                            break;
                        }

                        processProject(input, allProjects.get(selectedProject));
                    }
                    break;
                case "2":
                    // View Incomplete Tasks
                    while (true) {
                        while (true) {
                            try {
                                int index = 1;
                                drawLine();
                                System.out.println("All Incomplete Projects: \n");
                                System.out.println("Select a project(Enter '0'to return to menu):");
                                for (Project project : allProjects) {
                                    if (project != null && !project.getIsFinalised()) {
                                        System.out.println(index + " " + project.getName());
                                        incompleteProjects.add(project);
                                        index++;
                                    }
                                }
                                System.out.println();
                                selectedProject = Integer.parseInt(input.nextLine()) - 1;
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Option not selected!");
                            }
                        }
                        if (selectedProject == -1) {
                            break;
                        }
                        processProject(input, incompleteProjects.get(selectedProject));
                    }
                    break;
                case "3":
                    // View Overdue Tasks
                    while (true) {
                        while (true) {
                            try {
                                int index = 1;
                                drawLine();
                                System.out.println("All Overdue Projects: \n");
                                System.out.println("Select a project(Type '0'to return to menu):");
                                for (Project project : allProjects) {
                                    if (project != null
                                            && Date.valueOf(LocalDate.now()).after(
                                            Date.valueOf(project.getDeadline()))) {
                                        System.out.println(index + " " + project.getName());
                                        overDueProjects.add(project);
                                        index++;
                                    }
                                }
                                System.out.println();
                                selectedProject = Integer.parseInt(input.nextLine()) - 1;
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Option not selected!");
                            }
                        }
                        if (selectedProject == -1) {
                            break;
                        }
                        processProject(input, overDueProjects.get(selectedProject));
                    }
                    break;
                case "4":
                    // Search for project using Name or Number
                    Project searchedProject = searchProject(input, allProjects);
                    if (searchedProject != null) {
                        processProject(input, searchedProject);
                    } else {
                        System.out.println("Project not Found!");
                    }

                    break;
                case "5":
                    // Add project
                    allProjects.add(createProject());
                    System.out.println("Project added: " + allProjects.get(allProjects.size() - 1).getName());
                    break;
            }
        }
        input.close();
    }

    /**
     *
     * The method retrieves all the projects and there data
     * from a text file and reads it to the allProjects
     * array.
     *
     * @since version 1.00
     */

    // Method to search for project
    private static Project searchProject(Scanner input, List<Project> allProjects) {
        System.out.println("Select a project name or number: ");
        String userInput = input.nextLine();
        // We check if the project exists and return the project
        for (Project project : allProjects) {
            if (project != null && (userInput.equals(project.getName())
                    || userInput.equals(project.getProjectNumber()))) {
                return project;
            }
        }
        return null;
    }

    // Method used to edit a selected project
    private static void processProject(Scanner input, Project project) {
        while (project != null) {
            // Allow user to edit information of the selected task.
            drawLine();
            System.out.println(project);
            drawLine();
            System.out.println("Edit the project "
                    + project.getName()
                    + "\n1: Due date."
                    + "\n2: Total paid to date."
                    + "\n3: Number "
                    + "\n4: ERF Number"
                    + "\n5: Building type "
                    + "\n6: Address"
                    + "\n7: Contractors contact details."
                    + "\n8: Architect contact details."
                    + "\n9: Customer contact details."
                    + "\nfinal : Finalise project."
                    + "\n0: Go back.");
            String userInput = input.next().toLowerCase();
            input.nextLine();

            switch (userInput) {
                case "1" -> project.setDeadline(changeDueDate(input));
                case "2" -> changeAmountPaid(input, project);
                case "3" -> {
                    System.out.println("Enter a new project number: \n");
                    project.setProjectNumber(input.next());
                    input.nextLine();
                }
                case "4" -> {
                    System.out.println("Enter a new ERF number: \n");
                    project.setErfNumber(input.next());
                    input.nextLine();
                }
                case "5" -> {
                    System.out.println("Enter a new Building type: \n");
                    project.setBuildingType(input.next());
                    input.nextLine();
                }
                case "6" -> {
                    System.out.println("Enter a new address:");
                    String address = input.nextLine();
                    project.setAddress(input.next());
                }
                case "7" -> changePersonContacts(input, project.getContractor());
                case "8" -> changePersonContacts(input, project.getArchitect());
                case "9" -> changePersonContacts(input, project.getCustomer());
                case "final" -> {
                    project.finaliseProject();
                    userInput = "0";
                }
            }
            if (userInput.equals("0")) {
                break;
            }

        }
    }

    // Method used to edit contact information

    private static void changePersonContacts(Scanner input, Person person) {

        while (true) {
            drawLine();
            System.out.println("""
          What would you like to edit?
          1: Name
          2: Tel number
          3: Email Address
          4: Physical Address
          0: Go back""");
            String userChoice = input.nextLine();
            drawLine();

            if(userChoice.equals("0")) {
                break;
            }

            switch(userChoice) {
                case "1" -> {
                    System.out.println("Enter new name:");
                    String name = input.next();
                    input.nextLine();
                    person.setName(name);
                }
                case "2" -> {
                    System.out.println("Enter new phone number:");
                    String number = input.next();
                    input.nextLine();
                    person.setTelNumber(number);
                }
                case "3" -> {
                    System.out.println("Enter new Email address:");
                    String email = input.next();
                    input.nextLine();
                    person.setEmailAddress(email);
                }
                case "4" -> {
                    System.out.println("Enter new physical address:");
                    String address = input.next();
                    input.nextLine();
                    person.setPhysicalAddress(address);
                }
            }
            System.out.println(person.getJobType() + " details updated!");
        }
    }

    // Method used to edit amount paid
    private static void changeAmountPaid(Scanner input, Project project) {

        while (true) {
            try {
                System.out.println("Enter new total amount paid:");
                float amount = Float.parseFloat(input.nextLine());
                project.setAmountPaidToDate(amount);
                drawLine();
                System.out.println("Amount paid to date updated!");
                break;
            } catch (NullPointerException | NumberFormatException e) {
                System.out.println("Only enter numbers.");
            }
        }
    }

    // Method used to edit deadline

    private static String changeDueDate(Scanner input) {

        LocalDate deadline;
        String[] dateArray;
        String dateStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (true) {
            int year;
            int month;
            int day;

            System.out.println("Enter the project deadline(YYYY-MM-DD): ");
            dateStr = input.nextLine();
            dateStr = dateStr.strip();
            dateStr = dateStr.replace(" ", "-");
            dateStr = dateStr.replace("/", "-");
            dateArray = dateStr.split("-");

            try {
                year = Integer.parseInt(dateArray[0]);
                month = Integer.parseInt(dateArray[1]);
                day = Integer.parseInt(dateArray[2]);
                deadline = LocalDate.of(year, month, day);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Date format is incorrect.");
            }
        }
        return formatter.format(deadline);
    }

    // Method to create a project

    public static Project createProject() {

        // Objects
        Scanner input = new Scanner(System.in);
        Person architect = null;
        Person contractor = null;
        Person customer = null;
        String deadline;

        // Variables
        String projectNumber;
        String name;
        String buildingType;
        String address;
        String erfNumber;
        float totalProjectFees;
        float amountPaidToDate;

        drawLine();
        while (true) {
            boolean exists = false;
            System.out.println("Enter the project number: ");
            projectNumber = input.nextLine();
            for (Project project : allProjects) {
                if (projectNumber.equals(project.getProjectNumber())) {
                    exists = true;
                    System.out.println("Project number already used.");
                    break;
                } else {
                    exists = false;
                }
            }
            if (!exists) {
                break;
            }
        }

        while (true) {
            boolean exists = false;
            System.out.println("Enter the project name: ");
            name = input.nextLine();

            for (Project project : allProjects) {
                if (name.equals(project.getName())) {
                    exists = true;
                    System.out.println("Project name already used.");
                    break;
                } else {
                    exists = false;
                }
            }
            if (!exists) {
                break;
            }
        }

        drawLine();
        System.out.println("Enter the building type: ");
        buildingType = input.nextLine();
        drawLine();
        while (true) {
            try {
                System.out.println("Enter the project address: ");
                address = input.nextLine();
                drawLine();
                System.out.println("Enter the project ERF number: ");
                erfNumber = input.nextLine();
                drawLine();
                databaseManager.addERFNumber(address, erfNumber);
                break;
            } catch (SQLException e) {
                System.out.println("Address already exists.");
            }
        }


        // Basic try/catch check to prevent crashing.
        while (true) {
            try {
                System.out.println("Enter the project total cost: ");
                totalProjectFees = Float.parseFloat(input.nextLine());
                drawLine();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter numbers only!");
            }
        }
        while (true) {
            try {
                System.out.println("Enter the amount paid to date: ");
                amountPaidToDate = Float.parseFloat(input.nextLine());
                drawLine();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter numbers only!");
            }
        }

        deadline = changeDueDate(input);
        drawLine();

        // for loop to build 3 Person objects an Architect, contractor and a customer.
        for(int i = 0; i < 3; i++) {
            switch (i) {
                case 0 -> architect = createPerson("Architect");
                case 1 -> contractor = createPerson("Contractor");
                case 2 -> customer = createPerson("Customer");
            }
        }

        // If no name is entered one is created.
        if(name.equals("")) {
            String[] splitName = customer.getName().split(" ");
            if(splitName.length != 1) {
                name = buildingType + " " + splitName[splitName.length -1];
            } else{
                name = buildingType + " " + splitName[0];
            }
        }

        databaseManager.addProject(projectNumber, name, buildingType, address,
                totalProjectFees, amountPaidToDate, deadline, null,  contractor.getName(),
                architect.getName(), customer.getName());

        return new Project(projectNumber, name, buildingType, address, erfNumber,
                totalProjectFees, amountPaidToDate, deadline, architect,
                contractor, customer);

    }

    // Method used to create person object
    private static Person createPerson(String jobType) {

        Scanner personInput = new Scanner(System.in);

        String personName = "";
        String telNumber = "";
        String emailAddress = "";
        String physicalAddress = "";
        boolean exists = false;

        while (!exists) {
            System.out.println("Enter the name of the " + jobType + ": ");
            personName = personInput.nextLine();
            drawLine();

            while (true) {
                System.out.println("Enter the tel number of the " + jobType + ": ");
                telNumber = personInput.nextLine();
                telNumber = telNumber.replace(" ", "");
                telNumber = telNumber.strip();
                if (telNumber.length() == 10) {
                    drawLine();
                    break;
                } else {
                    System.out.println("A tel number may only have 10 characters.");
                }
            }

            while (true) {
                System.out.println("Enter the email address of the " + jobType + ": ");
                emailAddress = personInput.nextLine();
                if (!emailAddress.contains("@")) {
                    System.out.println("Email format incorrect. No @ symbol detected.");
                } else {
                    drawLine();
                    break;
                }
            }

            System.out.println("Enter the physical address of the " + jobType + ": ");
            physicalAddress = personInput.nextLine();
            drawLine();

            switch (jobType.toLowerCase()) {
                case "contractor" -> {
                    try {
                        databaseManager.addContractor(personName, telNumber, emailAddress, physicalAddress);
                        exists = true;
                    } catch (SQLException e) {
                        System.out.println("Error adding contractor. Try using a different name.");
                    }
                }
                case "architect" -> {
                    try {
                        databaseManager.addArchitect(personName, telNumber, emailAddress, physicalAddress);
                        exists = true;
                    } catch (SQLException e) {
                        System.out.println("Error adding architect. Try using a different name.");
                    }
                }
                case "customer" -> {
                    try {
                        databaseManager.addCustomer(personName, telNumber, emailAddress, physicalAddress);
                        exists = true;
                    } catch (SQLException e) {
                        System.out.println("Error adding customer. Try using a different name.");
                    }
                }

            }
        }
        return new Person(jobType, personName, telNumber, emailAddress,
                physicalAddress);
    }

    // Method to draw line to console
    public static void drawLine(){
        for(int i = 0; i < 80; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

}
