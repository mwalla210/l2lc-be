package com.line2linecoatings.api.dao;

import com.line2linecoatings.api.tracking.caches.Cache;
import com.line2linecoatings.api.tracking.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class TrackingDAOImpl {
    public static final Log log = LogFactory.getLog(TrackingDAOImpl.class);

    // Address methods

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
        preparedStatement.close();
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
                preparedStatement.close();
                return address;
            }
        }

        // address does not exist or is referenced, insert a new row
        return insertAddress(address);
    }

    // Customer methods

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
                customer.setBillingAddr(getAddressById(billingAddrId));
            }

            log.info("customer found with id " + customer.getId());
        }

        rs.close();
        preparedStatement.close();
        conn.close();
        return customer;
    }

    public Customer updateCustomer(int id, Customer customer) throws Exception {
        log.info("Start of updateCustomer in DAO");

        Connection conn = createConnection();
        ArrayList<String> params = new ArrayList<>();

        if (customer.getName() != null)
            params.add(" name = \"" + customer.getName() + "\"");
        if (customer.getEmail() != null)
            params.add(" email = \"" +  customer.getEmail() + "\"");
        if (customer.getWebsite() != null)
            params.add(" website = \"" + customer.getWebsite() + "\"");
        if (customer.getPastDue() != null)
            params.add(" is_past_due = " + getSqlBoolean(customer.getPastDue()));
        if (customer.getPhoneNumber() != null)
            params.add("phone = \"" + customer.getPhoneNumber() + "\"");
        if (customer.getShippingAddr() != null) {
            customer.setShippingAddr(updateAddress(customer.getShippingAddr()));
            params.add(" shipping_addr_id = " + customer.getShippingAddr().getId());
        }
        if (customer.getBillingAddr() != null) {
            customer.setBillingAddr(updateAddress(customer.getBillingAddr()));
            params.add(" billing_addr_id = " + customer.getBillingAddr().getId());
        }

        // lop off last comma and add where clause
        String query = "UPDATE CUSTOMER SET" + String.join(",", params)  + " WHERE id = " + id;

        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        conn.close();

        log.info("End of updateCustomer in DAO");
        return getCustomerById(id);
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
        rs.close();
        preparedStatement.close();
        conn.close();
        log.info("End of getCustomerPage in DAO");
        return customerPage;
    }

    public boolean removeCustomer(int id) throws Exception {
        log.info("Start of removeCustomer in DAO");
        boolean removed = removeFromTableById("Customer", id);
        log.info("End of removeCustomer in DAO");
        return removed;
    }

    // Employee methods

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

        employeePage.setLimit(limit);
        employeePage.setOffset(offset);
        employeePage.setItems(employees);

        rs.close();
        preparedStatement.close();
        conn.close();

        log.info("End of getEmployeePage in DAO");
        return employeePage;
    }

    public boolean removeEmployee(int id) throws Exception {
        log.info("Start of removeEmployee in DAO with id " + id);
        boolean removed;

        removed = removeFromTableById("Employee", id);

        log.info("End of removeEmployee in DAO with id " + id);
        return removed;
    }

    // User methods

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
        user.setPassword(null);
        log.info("End of createUser in DAO");
        return user;
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

        rs.close();
        preparedStatement.close();
        conn.close();

        log.info("End of doesUsernameExist in DAO");
        return exists;
    }

    // Station Methods

    public List<DBEnumeration> getAllStations() throws Exception {
        log.info("Start of getAllStations in DAO");
        List<DBEnumeration> stations = new LinkedList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM Station";

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Station s = new Station();
            s.setName(rs.getString("name"));
            s.setId(rs.getInt("id"));
            stations.add(s);
        }

        rs.close();
        st.close();
        conn.close();
        return stations;
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

    // CostCenter methods

    public List<DBEnumeration> getAllCostCenters() throws Exception {
        log.info("Start of getCostCentersEnum in DAO");
        List<DBEnumeration> costCenters = new LinkedList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM CostCenter ORDER BY Id";

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            CostCenter c = new CostCenter();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            costCenters.add(c);
        }

        rs.close();
        st.close();
        conn.close();
        log.info("End of getCostCentersEnum in DAO");
        return costCenters;
    }

    // ProjectStatus methods

    public List<DBEnumeration> getAllProjectStatuses() throws Exception {
        List<DBEnumeration> statuses = new LinkedList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM ProjectStatus";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            ProjectStatus p = new ProjectStatus();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("title"));
            statuses.add(p);
        }
        rs.close();
        stm.close();
        conn.close();
        return statuses;
    }

    // JobType methods

    public List<DBEnumeration> getAllJobTypes() throws Exception {
        List<DBEnumeration> jobTypes = new LinkedList<>();

        Connection conn = createConnection();
        Statement stm = conn.createStatement();

        String query = "SELECT * FROM JobType ORDER BY id";

        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            JobType j = new JobType();
            j.setId(rs.getInt("id"));
            j.setName(rs.getString("title"));
            jobTypes.add(j);
        }

        rs.close();
        stm.close();
        conn.close();
        return jobTypes;
    }

    // ProjectPriority methods

    public List<DBEnumeration> getAllProjectPriorities() throws Exception {
        List<DBEnumeration> priorities = new LinkedList<>();
        Connection conn = createConnection();
        Statement stm = conn.createStatement();

        String query = "SELECT * FROM Priority";

        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            ProjectPriority p = new ProjectPriority();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            priorities.add(p);
        }

        rs.close();
        stm.close();
        conn.close();
        return priorities;
    }

    // Project methods

    public Project createProject(Project project) throws Exception {
        log.info("Start of createProject in DAO");
        int jobTypeId = Cache.jobTypeCache.getIdForName(project.getJobType());
        int projectStatusId = Cache.projectStatusCache.getIdForName(project.getProjectStatus());
        int costCenterId = Cache.costCenterCache.getIdForName(project.getCostCenter());
        Integer priorityId = null;

        if (StringUtils.isNotEmpty(project.getPriority())) {
            priorityId = Cache.projectPriorityCache.getIdForName(project.getPriority());
        }

        Connection conn = createConnection();
        String query = "INSERT INTO "+
                "Project (job_type_id, customer_id, project_status_id," +
                " created, title, description, priority, part_count, ref_name, cost_center_id)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, jobTypeId);
        if (project.getCustomer() != null) {
            preparedStatement.setInt(2, project.getCustomer().getId());
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
        preparedStatement.setString(9, project.getRefNumber());
        preparedStatement.setInt(10, costCenterId);
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
        String query = "SELECT P.id as id, P.job_type_id as j_id, P.cost_center_id as c_id, P.project_status_id as s_id, P.priority as pri, P.ref_name as r_name, " +
                "P.customer_id as cust_id, P.created as created, P.finished as finish, P.title as title, P.description as descr, P.part_count as pc, C.name as name " +
                "FROM Project P " +
                "LEFT JOIN Customer C " +
                "ON cust_id = C.id WHERE P.id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        Project project = null;

        int jobTypeId;
        int costCenterId;
        int projectStatusId;
        Integer priorityId;

        if (rs.next()) {
            project = new Project();
            jobTypeId = rs.getInt("j_id");
            costCenterId = rs.getInt("c_id");
            projectStatusId = rs.getInt("s_id");
            priorityId = rs.getInt("pri");
            if (rs.wasNull()) {
                priorityId = null;
            }

            project.setId(rs.getInt("id"));
            int custId = rs.getInt("cust_id");
            if (rs.wasNull()) {
                project.setCustomer(null);
            } else {
                Customer c = new Customer();
                c.setId(custId);
                c.setName(rs.getString("name"));
                project.setCustomer(c);
            }
            project.setCreated(rs.getDate("created"));
            project.setFinished(rs.getDate("finish"));
            project.setTitle(rs.getString("title"));
            project.setDescription(rs.getString("descr"));
            project.setPartCount(rs.getInt("pc"));
            if (rs.wasNull()) {
                project.setPartCount(null);
            }
            project.setRefNumber(rs.getString("r_name"));
            rs.close();
            preparedStatement.close();
            conn.close();

            project.setJobType(Cache.jobTypeCache.getNameForId(jobTypeId));
            project.setCostCenter(Cache.costCenterCache.getNameForId(costCenterId));
            project.setProjectStatus(Cache.projectStatusCache.getNameForId(projectStatusId));

            if (priorityId != null) {
                project.setPriority(Cache.projectPriorityCache.getNameForId(priorityId));
            }
        } else {
            rs.close();
            preparedStatement.close();
            conn.close();
        }
        log.info("End of getProject in DAO with id " + id);
        return project;
    }

    public Page getProjectPage(int limit, int offset) throws Exception {
        log.info("Start of getProjectPage in DAO");
        Connection conn = createConnection();

        Page page = new Page();

        String query = "SELECT P.id as id, P.job_type_id as j_id, P.cost_center_id as c_id, P.project_status_id as s_id, P.priority as pri, P.ref_name as r_name, " +
                "P.customer_id as cust_id, P.created as created, P.finished as finish, P.title as title, P.description as descr, P.part_count as pc, C.name as name " +
                "FROM Project P " +
                "LEFT JOIN Customer C " +
                "ON cust_id = C.id";

        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        List<Integer> jobTypeIds = new ArrayList<>();
        List<Integer> projectStatusIds = new ArrayList<>();
        List<Integer> priorityIds = new ArrayList<>();
        List<Integer> costCenterIds = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        while(rs.next()) {
            Project project = new Project();
            int jobTypeid = rs.getInt("j_id");
            int projectStatusId = rs.getInt("s_id");
            Integer priorityId = rs.getInt("pri");
            int costCenterId = rs.getInt("c_id");
            if (rs.wasNull()) {
                priorityId = null;
            }
            project.setId(rs.getInt("id"));
            int custId = rs.getInt("cust_id");
            if (rs.wasNull()) {
                project.setCustomer(null);
            } else {
                Customer c = new Customer();
                c.setId(custId);
                c.setName(rs.getString("name"));
                project.setCustomer(c);
            }
            project.setCreated(rs.getDate("created"));
            project.setFinished(rs.getDate("finish"));
            project.setTitle(rs.getString("title"));
            project.setDescription(rs.getString("descr"));
            project.setPartCount(rs.getInt("pc"));
            if (rs.wasNull()) {
                project.setPartCount(null);
            }
            project.setRefNumber(rs.getString("r_name"));

            jobTypeIds.add(jobTypeid);
            projectStatusIds.add(projectStatusId);
            priorityIds.add(priorityId);
            costCenterIds.add(costCenterId);
            projects.add(project);
        }
        rs.close();
        stm.close();
        conn.close();

        for (int x = 0; x < projects.size(); x++) {
            Project project = projects.get(x);
            int jobTypeid = jobTypeIds.get(x);
            int projectStatusId = projectStatusIds.get(x);
            int costCenterId = costCenterIds.get(x);
            Integer priorityId = priorityIds.get(x);

            project.setJobType(Cache.jobTypeCache.getNameForId(jobTypeid));
            project.setCostCenter(Cache.costCenterCache.getNameForId(costCenterId));
            project.setProjectStatus(Cache.projectStatusCache.getNameForId(projectStatusId));

            if (priorityId != null) {
                project.setPriority(Cache.projectPriorityCache.getNameForId(priorityId));
            }
        }
        page.setItems(projects);
        page.setOffset(offset);
        page.setLimit(limit);

        log.info("End of getProjectPage in DAO");
        return page;
    }

    public Project updateProject(int id, Project project) throws Exception {

        String query = "UPDATE Project SET ";
        List<String> updates = new ArrayList<>();

        if (StringUtils.isNotEmpty(project.getJobType())) {
            int jobType = Cache.jobTypeCache.getIdForName(project.getJobType());
            updates.add("job_type_id = " + jobType);
        }

        if (StringUtils.isNotEmpty(project.getCostCenter())) {
            int costCenter = Cache.costCenterCache.getIdForName(project.getCostCenter());
            updates.add("cost_center_id = " + costCenter);
        }

        if (project.getCustomer().getId() != null) {
            updates.add("customer_id = " + project.getCustomer().getId());
        }

        if (project.getTitle() != null) {
            updates.add("title = " + '"' + project.getTitle() + '"');
        }

        if (project.getDescription() != null) {
            updates.add("description = " + '"' + project.getDescription() + '"');
        }

        if (project.getPriority() != null) {
            int priority = Cache.projectPriorityCache.getIdForName(project.getPriority());
            updates.add("priority = " + priority);
        }

        if (project.getPartCount() != null) {
            updates.add("part_count = " + project.getPartCount());
        }

        if (project.getRefNumber() != null) {
            updates.add("ref_name = " + '"' + project.getRefNumber() + '"');
        }

        query += String.join(",", updates) + " WHERE id = " + id;
        log.info(query);
        Connection conn = createConnection();
        Statement stm = conn.createStatement();
        stm.executeUpdate(query);
        stm.close();
        conn.close();
        return this.getProject(id);
    }

    public void updateProjectStatus(int id, String status) throws Exception {
        Connection conn = createConnection();
        int projectStatusId = Cache.projectStatusCache.getIdForName(status);
        String query = "UPDATE Project SET project_status_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, projectStatusId);
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        conn.close();
    }

    public void updateProjectStatus(int id, String status, Date finishDate) throws Exception {
        Connection conn = createConnection();
        int projectStatusId = Cache.projectStatusCache.getIdForName(status);
        String query = "UPDATE Project SET project_status_id = ?, finished = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, projectStatusId);
        preparedStatement.setDate(2, new java.sql.Date(finishDate.getTime()));
        preparedStatement.setInt(3, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        conn.close();

    }

    public ProjectTimeEntry createTimeEntry(ProjectTimeEntry projectTimeEntry) throws Exception {
        log.info("Start of createTimeEntry in DAO with id " + projectTimeEntry.getProjectId());

        int stationId = Cache.stationCache.getIdForName(projectTimeEntry.getStation());
        Connection conn = createConnection();
        String query = "INSERT INTO ProjectTimeEntry (project_id, employee_id, station_id, created) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, projectTimeEntry.getProjectId());
        preparedStatement.setInt(2, projectTimeEntry.getEmployeeId());
        preparedStatement.setInt(3, stationId);
        preparedStatement.setDate(4, new java.sql.Date(projectTimeEntry.getCreated().getTime()));

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating time entry failed, no rows affected");
        }

        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                projectTimeEntry.setId(generatedKeys.getInt(1));
                log.info("Time Entry Created with id " + projectTimeEntry.getId());
                generatedKeys.close();
            } else {
                throw new SQLException("Creating Project failed, no ID obtained");
            }
        }

        preparedStatement.close();
        conn.close();
        log.info("End of createTimeEntry in DAO with id " + projectTimeEntry.getProjectId());
        return projectTimeEntry;
    }

    public List<ProjectTimeEntry> getTimeEntries(int projectId) throws Exception {
        log.info("Start of getTimeEntries in DAO with id " + projectId);
        List<ProjectTimeEntry> timeEntries = new ArrayList<>();
        List<Integer> stationIds = new ArrayList<>();

        Connection conn = createConnection();
        String query = "SELECT * FROM ProjectTimeEntry WHERE project_id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, projectId);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            ProjectTimeEntry projectTimeEntry = new ProjectTimeEntry();
            projectTimeEntry.setId(rs.getInt("id"));
            projectTimeEntry.setProjectId(rs.getInt("project_id"));
            projectTimeEntry.setEmployeeId(rs.getInt("employee_id"));
            projectTimeEntry.setCreated(rs.getDate("created"));
            stationIds.add(rs.getInt("station_id"));
            timeEntries.add(projectTimeEntry);
        }

        rs.close();
        preparedStatement.close();
        conn.close();

        // getting station names
        for (int x = 0; x < timeEntries.size(); x++) {
            timeEntries.get(x).setStation(Cache.stationCache.getNameForId(stationIds.get(x)));
        }
        log.info("End of getTimeEntries in DAO with id " + projectId);
        return timeEntries;
    }

    private boolean removeFromTableById(String table, int id) throws Exception {
        log.info("Start of removeFromTableById with table "  + table + " and id " + id);
        int count;
        boolean removed;

        Connection conn = createConnection();

        String query = "DELETE FROM "+ table + " WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

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

    private int getSqlBoolean(Boolean b) {
        if (b) {
            return 1;
        }
        return 0;
    }
}
