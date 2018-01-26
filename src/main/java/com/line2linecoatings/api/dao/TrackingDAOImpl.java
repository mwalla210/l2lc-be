package com.line2linecoatings.api.dao;

import com.line2linecoatings.api.tracking.models.Address;
import com.line2linecoatings.api.tracking.models.Customer;
import com.line2linecoatings.api.tracking.models.Employee;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;

public class TrackingDAOImpl {
    public static final Log log = LogFactory.getLog(TrackingDAOImpl.class);

    public Address insertAddress(Address address) throws Exception {
        String query = "SELECT id FROM Address WHERE street=? AND city=? LIMIT 1;";
        Connection conn = createConnection();

        PreparedStatement preparedStatement = createConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, address.street);
        preparedStatement.setString(2, address.city);
        ResultSet rs = preparedStatement.executeQuery();

        if (!rs.next()) {
            log.info("Inserting new address in DAO");
            preparedStatement.close();
            rs.close();

            query = "INSERT INTO Address (street, city, state, country, zip) VALUES (?, ?, ?, ?, ?)";

            preparedStatement = createConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, address.street);
            preparedStatement.setString(2, address.city);
            preparedStatement.setString(3, address.state);
            preparedStatement.setString(4, address.country);
            preparedStatement.setString(5, address.zip);

            try (ResultSet generatedKeys = preparedStatement.executeQuery()) {
                if (!generatedKeys.next()) {
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

    public Employee createEmployee(Employee employee) throws Exception {
        log.info("Start of createEmployee in DAO");
        Connection conn = createConnection();

        String query = "INSERT INTO employee (first_name, last_name) VALUES (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, employee.getFirstName());
        preparedStatement.setString(2, employee.getLastName());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                employee.setId(generatedKeys.getInt(1));
                log.info("Employee Created with id " + employee.getId());

            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
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
        log.info("End of createEmployee in DAO with id " + id);
        return employee;
    }

    public Employee getEmployeeById(int id) throws Exception {
        Employee employee = null;

        Connection conn = createConnection();

        String query = String.format("SELECT * FROM Employee WHERE id='%d'", id);

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        employee = mapEmployeeFromResultSet(rs);

        st.close();
        conn.close();
        return employee;
    }

    public boolean removeEmployee(int id) throws Exception {
        log.info("Start of removeEmployee in DAO with id " + id);
        int count;
        boolean removed;

        Connection conn = createConnection();

        String query = "DELETE FROM Employee WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);

        count = preparedStatement.executeUpdate();
        removed = count==1?true:false;
        log.info("End of removeEmployee in DAO with id " + id);
        return removed;
    }

    private Employee mapEmployeeFromResultSet(ResultSet rs) throws Exception {
        Employee employee = null;
        if (rs.next()) {
            employee = new Employee();
            employee.setId(rs.getInt("id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
        }

        return employee;
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
