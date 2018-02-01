package com.line2linecoatings.api.dao;

import com.line2linecoatings.api.tracking.models.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        String query = String.format("SELECT * FROM Employee WHERE id='%d'", id);

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            employee = new Employee();
            employee.setId(rs.getInt("id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
        }

        rs.close();
        st.close();
        conn.close();
        return employee;
    }

    public boolean removeEmployee(int id) throws Exception {
        log.info("Start of removeEmployee in DAO with id " + id);
        boolean removed;

        removed = removeFromTableById("Employee", id);

        log.info("End of removeEmployee in DAO with id " + id);
        return removed;
    }

    public List<Station> getAllStations() throws Exception {
        log.info("Start of getAllStations in DAO");
        List<Station> stations = new ArrayList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM Station";

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Station station = new Station();
            station.setId(rs.getInt("id"));
            station.setName(rs.getString("name"));
            stations.add(station);
        }

        rs.close();
        st.close();
        conn.close();
        return stations;
    }

    public Station getStation(int id) throws Exception {
        log.info("Start of getStation in DAO with id " + id);
        Connection conn = createConnection();
        Station station = null;

        String query = "SELECT * FROM Station WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            station = new Station();
            station.setId(rs.getInt("id"));
            station.setName(rs.getString("name"));
        }
        rs.close();
        preparedStatement.close();
        conn.close();
        return station;
    }

    public List<CostCenter> getAllCostCenters() throws Exception {
        log.info("Start of getAllCostCenters in DAO");
        List<CostCenter> costCenters = new ArrayList<>();

        Connection conn = createConnection();

        String query = "SELECT * FROM CostCenter";

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            CostCenter costCenter = new CostCenter();
            costCenter.setId(rs.getInt("id"));
            costCenter.setName(rs.getString("name"));
            costCenters.add(costCenter);
        }

        rs.close();
        st.close();
        conn.close();
        log.info("End of getAllCostCenters in DAO");
        return costCenters;
    }

    public CostCenter getCostCenter(int id) throws Exception {
        log.info("Start of getCostCenter in DAO with id " + id);
        Connection conn = createConnection();
        CostCenter costCenter = null;

        String query = "SELECT * FROM Station WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            costCenter = new CostCenter();
            costCenter.setId(rs.getInt("id"));
            costCenter.setName(rs.getString("name"));
        }
        rs.close();
        preparedStatement.close();
        conn.close();
        log.info("End of getCostCenter in DAO with id " + id);
        return costCenter;
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
