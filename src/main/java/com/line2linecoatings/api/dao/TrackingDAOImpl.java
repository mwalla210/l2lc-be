package com.line2linecoatings.api.dao;

import com.line2linecoatings.api.tracking.models.Employee;
import com.line2linecoatings.api.tracking.models.Station;
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
