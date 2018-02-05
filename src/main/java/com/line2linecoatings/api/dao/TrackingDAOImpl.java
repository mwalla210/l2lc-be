package com.line2linecoatings.api.dao;

import com.line2linecoatings.api.tracking.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

public class TrackingDAOImpl {
    public static final Log log = LogFactory.getLog(TrackingDAOImpl.class);

    public Address insertAddress(Address address) throws Exception {
        String query = "SELECT id FROM Address WHERE street=? AND city=? LIMIT 1;";
        Connection conn = createConnection();

        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, address.street);
        preparedStatement.setString(2, address.city);
        ResultSet rs = preparedStatement.executeQuery();

        if (!rs.next()) {
            log.info("Inserting new address in DAO");
            preparedStatement.close();
            rs.close();

            query = "INSERT INTO Address (street, city, state, country, zip) VALUES (?, ?, ?, ?, ?)";

            preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, address.street);
            preparedStatement.setString(2, address.city);
            preparedStatement.setString(3, address.state);
            preparedStatement.setString(4, address.country);
            preparedStatement.setString(5, address.zip);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    address.setId(generatedKeys.getInt(1));
                    log.info("Address created with id " + address.id);
                    generatedKeys.close();
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        } else {
            address.setId(rs.getInt(1));
        }

        preparedStatement.close();
        conn.close();
        rs.close();
        return address;
    }

    public Address getAddressById(int id) throws Exception {
        log.info("Start of getAddressById in DAO");

        Address address = null;
        Connection conn = createConnection();
        String query = "SELECT * FROM Address WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            address = new Address();
            address.setId(rs.getInt("id"));
            address.setStreet(rs.getString("street"));
            address.setCity(rs.getString("city"));
            address.setCountry(rs.getString("country"));
            address.setZip(rs.getString("zip"));
        }

        rs.close();
        conn.close();
        return address;
    }

    public Address updateAddress(Address address) throws Exception {
        log.info("Start of updateAddress in DAO");
        Connection conn = createConnection();

        // see if this address exists
        String query = "SELECT (id) FROM Address WHERE street = ? AND city = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, address.getStreet());
        preparedStatement.setString(2, address.getCity());

        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            // address exists, check references
            int id = rs.getInt("id");
            preparedStatement.close();
            rs.close();

            query = "SELECT COUNT(*) as rowcount FROM Customer " +
                    "WHERE shipping_addr_id = ? OR billing_addr_id = ?";

            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);

            rs = preparedStatement.executeQuery();

            if (!rs.next()) {
                throw new SQLException("This should be impossible");
            }
            int refCount = rs.getInt("rowcount");

            preparedStatement.close();
            rs.close();

            if (refCount <= 1) {
                // one customer references this, safe to update it
                query = "UPDATE Address " +
                        "SET street = ?, city = ?, state = ?, country = ?, zip = ?" +
                        "WHERE id = ?";

                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, address.getStreet());
                preparedStatement.setString(2, address.getCity());
                preparedStatement.setString(3, address.getState());
                preparedStatement.setString(4, address.getCountry());
                preparedStatement.setString(5, address.getZip());
                preparedStatement.setInt(6, id);
                preparedStatement.executeUpdate();

                address.setId(id);
                return address;
            }
        }

        // address does not exist or is referenced, insert a new row
        return insertAddress(address);
    }

    public Customer createCustomer(Customer customer) throws Exception {
        log.info("Start of CreateCustomer in DAO");

        customer.setBillingAddr(insertAddress(customer.billingAddr));
        customer.setShippingAddr(insertAddress(customer.shippingAddr));

        String query = "INSERT INTO Customer (name, email, website, shipping_addr_id, billing_addr_id, is_past_due, phone) values (?, ?, ?, ?, ?, ?, ?);";
        Connection conn = createConnection();

        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getEmail());
        preparedStatement.setString(3, customer.getWebsite());
        preparedStatement.setInt(4, customer.getShippingAddr().getId());
        preparedStatement.setInt(5, customer.getBillingAddr().getId());
        preparedStatement.setBoolean(6, customer.getPastDue());
        preparedStatement.setString(7, customer.getPhoneNumber());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating customer failed, no rows affected.");
        }

        try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
            if (rs.next()) {
                customer.setId(rs.getInt(1));
                log.info("Customer created with id " + customer.getId());
                rs.close();
            } else {
                throw new SQLException("Creating customer failed, no id obtained.");
            }
        }

        preparedStatement.close();
        conn.close();
        return customer;
    }

    public Customer getCustomerById(int id) throws Exception {
        log.info("Start of getCustomerById in DAO");

        Customer customer = null;
        Connection conn = createConnection();
        String query = "SELECT * FROM Customer WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();

        if(rs.next()) {
            customer = new Customer();
            customer.setId(rs.getInt("id"));
            customer.setName(rs.getString("name"));
            customer.setEmail(rs.getString("email"));
            customer.setWebsite(rs.getString("website"));
            customer.setPastDue(rs.getBoolean("is_past_due"));
            customer.setPhoneNumber(rs.getString("phone"));
            customer.setShippingAddr(getAddressById(rs.getInt("shipping_addr_id")));

            int billingAddrId = rs.getInt("billing_addr_id");
            if (billingAddrId == customer.getShippingAddr().getId()) {
                customer.setBillingAddr(customer.getShippingAddr());
            } else {
                customer.setShippingAddr(getAddressById(billingAddrId));
            }

            log.info("customer found with id " + customer.getId());
        }

        rs.close();
        conn.close();
        return customer;
    }

    public Customer updateCustomer(int id, Customer customer) throws Exception {
        log.info("Start of updateCustomer in DAO");

        customer.setShippingAddr(updateAddress(customer.getShippingAddr()));
        customer.setBillingAddr(updateAddress(customer.getBillingAddr()));

        Connection conn = createConnection();

        String query = "UPDATE Customer " +
                        "SET name = ?, email = ?, website = ?, shipping_addr_id = ?, " +
                        "billing_addr_id = ?, is_past_due = ?, phone = ?" +
                        "WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getEmail());
        preparedStatement.setString(3, customer.getWebsite());
        preparedStatement.setInt(4, customer.getShippingAddr().getId());
        preparedStatement.setInt(5, customer.getBillingAddr().getId());
        preparedStatement.setBoolean(6, customer.getPastDue());
        preparedStatement.setString(7, customer.getPhoneNumber());
        preparedStatement.setInt(8, id);

        preparedStatement.executeUpdate();

        customer.setId(id);

        preparedStatement.close();
        conn.close();

        log.info("End of updateCustomer in DAO");
        return customer;
    }

    public Page getCustomerPage(int limit, int offset) throws Exception {
        log.info("Start of getCustomerPage in DAO");

        Connection conn = createConnection();
        Page customerPage = new Page();
        List<Customer> customers = new ArrayList<>();

        String query = "Select * FROM Customer ORDER BY id DESC LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, limit);
        preparedStatement.setInt(2, offset);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Customer customer = new Customer();
            customer.setId(rs.getInt("id"));
            customer.setName(rs.getString("name"));
            customer.setEmail(rs.getString("email"));
            customer.setWebsite(rs.getString("website"));
            customer.setPastDue(rs.getBoolean("is_past_due"));
            customer.setPhoneNumber(rs.getString("phone"));
            customer.setShippingAddr(getAddressById(rs.getInt("shipping_addr_id")));

            int billingAddrId = rs.getInt("billing_addr_id");
            if (billingAddrId == customer.getShippingAddr().getId()) {
                customer.setBillingAddr(customer.getShippingAddr());
            } else {
                customer.setShippingAddr(getAddressById(billingAddrId));
            }
            customers.add(customer);
        }
        customerPage.setLimit(limit);
        customerPage.setOffset(offset);
        customerPage.setItems(customers);
        log.info("End of getCustomerPage in DAO");
        return customerPage;
    }

    public Employee createEmployee(Employee employee) throws Exception {
        log.info("Start of createEmployee in DAO");
        Connection conn = createConnection();

        String query = "INSERT INTO employee (first_name, last_name) VALUES (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, employee.getFirstName());
        preparedStatement.setString(2, employee.getLastName());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating employee failed, no rows affected.");
        }

        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                employee.setId(generatedKeys.getInt(1));
                log.info("Employee Created with id " + employee.getId());
                generatedKeys.close();
            }
            else {
                throw new SQLException("Creating employee failed, no ID obtained.");
            }
        }
        preparedStatement.close();
        conn.close();
        log.info("End of createEmployee in DAO");
        return employee;
    }

    public Employee updateEmployee(int id, Employee employee) throws Exception {
        log.info("Start of createEmployee in DAO with id " + id);
        Connection conn = createConnection();

        String query = "UPDATE Employee " +
                "SET first_name = ?, last_name = ? "+
                "WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, employee.getFirstName());
        preparedStatement.setString(2, employee.getLastName());
        preparedStatement.setInt(3, id);

        preparedStatement.executeUpdate();

        employee.setId(id);

        preparedStatement.close();
        conn.close();
        log.info("End of createEmployee in DAO with id " + id);
        return employee;
    }

    public Employee getEmployeeById(int id) throws Exception {
        Employee employee = null;

        Connection conn = createConnection();

        String query = "SELECT * FROM Employee WHERE id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            employee = new Employee();
            employee.setId(rs.getInt("id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
        }

        rs.close();
        preparedStatement.close();
        conn.close();
        return employee;
    }

    public Page getEmployeePage(int limit, int offset) throws Exception {
        log.info("Start of getEmployeePage in DAO");

        Connection conn = createConnection();
        Page employeePage = new Page();
        List<Employee> employees = new ArrayList<>();

        String query = "Select * FROM Employee ORDER BY id DESC LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, limit);
        preparedStatement.setInt(2, offset);

        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {
            Employee employee = new Employee();
            employee.setId(rs.getInt("id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
            employees.add(employee);
        }

        rs.close();
        preparedStatement.close();
        conn.close();

        employeePage.setLimit(limit);
        employeePage.setOffset(offset);
        employeePage.setItems(employees);
        return employeePage;
    }

    public boolean removeEmployee(int id) throws Exception {
        log.info("Start of removeEmployee in DAO with id " + id);
        boolean removed;

        removed = removeFromTableById("Employee", id);

        log.info("End of removeEmployee in DAO with id " + id);
        return removed;
    }

    public List<String> getAllStations() throws Exception {
        log.info("Start of getAllStations in DAO");
        List<String> stations = new ArrayList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM Station";

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            stations.add(rs.getString("name"));
        }

        rs.close();
        st.close();
        conn.close();
        return stations;
    }

    public List<String> getCostCentersEnum() throws Exception {
        log.info("Start of getCostCentersEnum in DAO");
        List<String> costCenters = new ArrayList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM CostCenter ORDER BY Id";

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            costCenters.add(rs.getString("name"));
        }

        rs.close();
        st.close();
        conn.close();
        log.info("End of getCostCentersEnum in DAO");
        return costCenters;
    }

    public User createUser(User user) throws Exception {
        log.info("Start of createUser in DAO");

        Integer stationId = null;
        if (StringUtils.isNotEmpty(user.getStation())) {
            stationId = findStationId(user.getStation());
        }
        Connection conn = createConnection();

        String query = "INSERT INTO Login (username, password, is_admin, station_id) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setBoolean(3, user.isAdmin());

        if (stationId != null) {
            preparedStatement.setInt(4, stationId);
        } else {
            preparedStatement.setNull(4, Types.INTEGER);
        }

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
                log.info("User Created with id " + user.getId());
                generatedKeys.close();
            } else {
                throw new SQLException("Creating User failed, no ID obtained.");
            }
        }

        preparedStatement.close();
        conn.close();
        log.info("End of createUser in DAO");
        return user;
    }

    private int findStationId(String station) throws Exception{
        Connection conn = createConnection();

        String query = "SELECT * FROM Station WHERE name=?";

        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, station);

        ResultSet rs = preparedStatement.executeQuery();
        int stationId;
        if (rs.next()) {
            stationId = rs.getInt("id");
        } else {
            throw new SQLException("Invalid name for stations");
        }

        rs.close();
        preparedStatement.close();
        conn.close();
        return stationId;
    }

    private String findStationName(int id) throws Exception {
        Connection conn = createConnection();

        String query = "SELECT * FROM Station WHERE id=?";

        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();
        String stationName;
        if (rs.next()) {
            stationName = rs.getString("name");
        } else {
            throw new SQLException("Invalid id for stations");
        }

        rs.close();
        preparedStatement.close();
        conn.close();
        return stationName;
    }
    public User getUser(int id) throws Exception {
        log.info("Start of getUser in DAO with id " + id);

        User user = null;

        Connection conn = createConnection();

        String query = "SELECT * FROM Login WHERE id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();
        Integer stationId = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setAdmin(rs.getBoolean("is_admin"));
            stationId = rs.getInt("station_id");
            if (rs.wasNull()) {
                stationId = null;
            }
        }

        rs.close();
        preparedStatement.close();
        conn.close();

        if (stationId != null) {
            user.setStation(findStationName(stationId));
        }
        log.info("End of getUser in DAO with id " + id);
        return user;
    }

    public User login(User user) throws Exception {
        log.info("Start of login in DAO");
        User loginUser = null;

        Connection conn = createConnection();

        String query = "SELECT * FROM Login WHERE username = ? AND password = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            loginUser = new User();
            loginUser.setId(rs.getInt("id"));
            loginUser.setUsername(rs.getString("username"));
            loginUser.setStation(findStationName(rs.getInt("station_id")));
            loginUser.setAdmin(rs.getBoolean("is_admin"));
        }
        rs.close();
        preparedStatement.close();
        conn.close();
        log.info("End of login in DAO");
        return loginUser;

    }

    public boolean doesUsernameExist(String username) throws Exception {
        log.info("Start of doesUsernameExist in DAO");
        boolean exists = false;

        Connection conn = createConnection();

        String query = "SELECT * FROM Login WHERE username = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            exists = true;
        }
        log.info("End of doesUsernameExist in DAO");
        return exists;
    }

    public Set<String> getProjectStatusEnum() throws Exception {
        Set<String> statuses = new HashSet<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM ProjectStatus";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            statuses.add(rs.getString("title"));
        }
        rs.close();
        stm.close();
        conn.close();
        return statuses;
    }

    public List<JobType> getJobTypeEnum() throws Exception {
        List<JobType> jobTypes = new ArrayList<>();

        Connection conn = createConnection();
        Statement stm = conn.createStatement();

        String query = "SELECT * FROM JobType ORDER BY id";

        ResultSet rs = stm.executeQuery(query);
        List<String> costCenters = this.getCostCentersEnum();

        while (rs.next()) {
            JobType jobType = new JobType();
            jobType.setJobType(rs.getString("title"));
            jobType.setCostCenter(costCenters.get(rs.getInt("cost_center_id")-1));
            jobTypes.add(jobType);
        }

        rs.close();
        stm.close();
        conn.close();
        return jobTypes;
    }

    public Set<String> getPriorityEnum() throws Exception {
        Set<String> priorities = new HashSet<>();
        Connection conn = createConnection();
        Statement stm = conn.createStatement();

        String query = "SELECT * FROM Priority";

        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            priorities.add(rs.getString("name"));
        }

        rs.close();
        stm.close();
        conn.close();
        return priorities;
    }

    public Project createProject(Project project) throws Exception {
        log.info("Start of createProject in DAO");
        int jobTypeId = findJobTypeId(project.getJobType());
        int projectStatusId = findProjectStatusId(project.getProjectStatus());
        Integer priorityId = null;
        if (StringUtils.isNotEmpty(project.getPriority())) {
            priorityId = findPriorityId(project.getPriority());
        }
        Connection conn = createConnection();
        String query = "INSERT INTO "+
                "Project (job_type_id, customer_id, project_status_id," +
                " created, title, description, priority, part_count, ref_name)" +
                " VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, jobTypeId);
        if (project.getCustomerId() != null) {
            preparedStatement.setInt(2, project.getCustomerId());
        }
        preparedStatement.setInt(3, projectStatusId);
        preparedStatement.setDate(4, new java.sql.Date(project.getCreated().getTime()));
        preparedStatement.setString(5, project.getTitle());
        preparedStatement.setString(6, project.getDescription());
        if (priorityId != null) {
            preparedStatement.setInt(7, priorityId);
        }
        if (project.getPartCount() != null) {
            preparedStatement.setInt(8, project.getPartCount());
        }
        preparedStatement.setString(9, project.getRefName());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating project failed, no rows affected");
        }

        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                project.setId(generatedKeys.getInt(1));
                log.info("Project Created with id " + project.getId());
                generatedKeys.close();
            } else {
                throw new SQLException("Creating Project failed, no ID obtained");
            }
        }

        preparedStatement.close();
        conn.close();
        log.info("End of createProject in DAO");
        return project;
    }

    public Project getProject(int id) throws Exception {
        log.info("Start of getProject in DAO with id " + id);
        Connection conn = createConnection();
        String query = "SELECT * FROM Project WHERE id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        Project project = null;

        int jobTypeid;
        int projectStatusId;
        Integer priorityId;

        if (rs.next()) {
            project = new Project();
            jobTypeid = rs.getInt("job_type_id");
            projectStatusId = rs.getInt("project_status_id");
            priorityId = rs.getInt("priority");
            if (rs.wasNull()) {
                priorityId = null;
            }

            project.setId(rs.getInt("id"));
            project.setCustomerId(rs.getInt("customer_id"));
            if (rs.wasNull()) {
                project.setCustomerId(null);
            }
            project.setCreated(rs.getTime("created"));
            project.setFinished(rs.getTime("finished"));
            project.setTitle(rs.getString("title"));
            project.setDescription(rs.getString("description"));
            project.setPartCount(rs.getInt("part_count"));
            if (rs.wasNull()) {
                project.setPartCount(null);
            }
            project.setRefName(rs.getString("ref_name"));
            rs.close();
            preparedStatement.close();
            conn.close();

            JobType jobType = getJobTypeById(jobTypeid);
            project.setJobType(jobType.getJobType());
            project.setCostCenter(jobType.getCostCenter());
            project.setProjectStatus(getProjectStatusById(projectStatusId));
            project.setPriority(getPriorityById(priorityId));
        }
        log.info("End of getProject in DAO with id " + id);
        return project;
    }

    private JobType getJobTypeById(int id) throws Exception {
        List<JobType> jobTypes = getJobTypeEnum();
        if (id > jobTypes.size()) {
            throw new SQLException("Invalid Job Type id");
        }

        return jobTypes.get(id-1);
    }

    private String getPriorityById(int id) throws Exception {
        Connection conn = createConnection();
        String query = "SELECT * FROM Priority WHERE id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();

        String priority;
        if (rs.next()) {
            priority = rs.getString("name");
        } else {
            throw new SQLException("Invalid Priority Id");
        }
        return priority;
    }

    private String getProjectStatusById(int id) throws Exception {
        Connection conn = createConnection();
        String query = "SELECT * FROM ProjectStatus WHERE id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();

        String projectStatus;
        if (rs.next()) {
            projectStatus = rs.getString("title");
        } else {
            throw new SQLException("Invalid Project Status Id");
        }
        return projectStatus;
    }

    private int findJobTypeId(String jobTypeTitle) throws Exception {
        Connection conn = createConnection();

        String query = "SELECT * FROM JobType WHERE title=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, jobTypeTitle);
        ResultSet rs = preparedStatement.executeQuery();

        int jobTypeId;
        if (rs.next()) {
            jobTypeId = rs.getInt("id");
        } else {
            throw new SQLException("Invalid Job Type Title");
        }
        rs.close();
        preparedStatement.close();
        conn.close();
        return jobTypeId;
    }

    private int findProjectStatusId(String title) throws Exception {
        Connection conn = createConnection();

        String query = "SELECT * FROM ProjectStatus WHERE title=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, title);
        ResultSet rs = preparedStatement.executeQuery();

        int projectStatusId;
        if (rs.next()) {
            projectStatusId = rs.getInt("id");
        } else {
            throw new SQLException("Invalid Project Status Id");
        }
        rs.close();
        preparedStatement.close();
        conn.close();
        return projectStatusId;
    }

    private int findPriorityId(String priority) throws Exception {
        Connection conn = createConnection();

        String query = "SELECT * FROM Priority WHERE name=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, priority);
        ResultSet rs = preparedStatement.executeQuery();

        int priorityId;
        if (rs.next()) {
            priorityId = rs.getInt("id");
        } else {
            throw new SQLException("Invalid Priority id");
        }
        rs.close();
        preparedStatement.close();
        conn.close();
        return priorityId;
    }
    private boolean removeFromTableById(String table, int id) throws Exception {
        log.info("Start of removeFromTableById with table "  + table + " and id " + id);
        int count;
        boolean removed;

        Connection conn = createConnection();

        String query = "DELETE FROM ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, table);
        preparedStatement.setInt(2, id);

        count = preparedStatement.executeUpdate();
        removed = count==1?true:false;

        preparedStatement.close();
        conn.close();
        log.info("End of removeFromTableById with table "  + table + " and id " + id);
        return removed;
    }

    private Connection createConnection() throws Exception{
        log.info("Start of createConnection");
        Connection conn = null;
        Context context = new InitialContext();
        DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/sqlite");
        conn = ds.getConnection();
        log.info("End of createConnection");
        return conn;
    }
}
