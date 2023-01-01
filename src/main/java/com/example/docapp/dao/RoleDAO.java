package com.example.docapp.dao;

import com.example.docapp.models.*;
import com.example.docapp.util.DBUtil;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Vector;

public class RoleDAO {
    public static int addRole(Role role, Vector<Permission> permissions) {
        PreparedStatement psAddRole = null;
        int statusCode=0;

        try {
            Connection connection = DBUtil.getConnection();

            psAddRole = connection.prepareStatement("INSERT INTO role (name) VALUES (?)");
            psAddRole.setString(1, role.getName());
            psAddRole.executeUpdate();

            try (ResultSet generatedKeys = psAddRole.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    role.setId(generatedKeys.getInt(1));

                    for (Permission permission : permissions) {
                        String query = "INSERT INTO permission VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement psAddUserRole = connection.prepareStatement(query);
                        psAddUserRole.setInt(1, role.getId());
                        psAddUserRole.setString(2, permission.getSubject());
                        psAddUserRole.setInt(3, permission.isCanView() ? 1 : 0);
                        psAddUserRole.setInt(4, permission.isCanAdd() ? 1 : 0);
                        psAddUserRole.setInt(5, permission.isCanModify() ? 1 : 0);
                        psAddUserRole.setInt(6, permission.isCanDelete() ? 1 : 0);
                        psAddUserRole.executeUpdate();
                        psAddUserRole.close();
                    }
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            Action action = new Action("Ajout du rôle " + role.getName() + ", son id est " + role.getId(), LocalDateTime.now().toString(), Utilisateur.currentUser.getId());
            ActionDAO.addAction(action);

            statusCode=201;
        } catch (SQLException e) {
            statusCode = 400;
            throw new RuntimeException(e);

        } finally {
            if (psAddRole != null) {
                try {
                    psAddRole.close();
                } catch (SQLException e) {
                    statusCode = 400;
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return statusCode;
    }

    public static int editRole(Role role) {
        PreparedStatement psEditRole = null;
        int statusCode = 0;
        String oldRoleName = role.getName();

        try {
            Connection connection = DBUtil.getConnection();
            
            psEditRole = connection.prepareStatement("UPDATE role SET name = ?");
            psEditRole.setString(1, role.getName());
            psEditRole.executeUpdate();

            Action action = new Action("Modification du rôle " + oldRoleName + " en " + role.getName() + ", son id est " + role.getId(), LocalDateTime.now().toString(), Utilisateur.currentUser.getId());
            ActionDAO.addAction(action);

            statusCode = 201;
        } catch (SQLException e) {
            statusCode = 400;
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (psEditRole != null) {
                try {
                    psEditRole.close();
                } catch (SQLException e) {
                    statusCode = 400;
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return statusCode;
    }

    public static int deleteRole(Role role) {
        PreparedStatement statement = null;
        ResultSet queryOutput = null;
        int statusCode = 0;

        try {
            Connection connection = DBUtil.getConnection();

//            statement = connection.prepareStatement("DELETE FROM permission WHERE id_utilisateur=?");
//            statement.setInt(1, role.getId());
//            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM role WHERE id = ?");
            statement.setInt(1, role.getId());
            statement.executeUpdate();

            Action action = new Action("Suppression du rôle id = " + role.getId(), LocalDateTime.now().toString(), Utilisateur.currentUser.getId());
            ActionDAO.addAction(action);

            statusCode = 200;
        } catch (SQLException e) {
            statusCode = 400;
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    statusCode = 400;
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return statusCode;
    }

    public static Vector<Role> getRoles(){
        Vector<Role> roles = new Vector<Role>();
        Role role;
        PreparedStatement psLogin = null;
        ResultSet queryOutput = null;

        try {
            Connection connection = DBUtil.getConnection();

            psLogin = connection.prepareStatement("SELECT * FROM role");
            queryOutput = psLogin.executeQuery();

            while (queryOutput.next()) {
                role = new Role();
                role.setId(queryOutput.getInt("id"));
                role.setName(queryOutput.getString("name"));

                roles.add(role);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (queryOutput != null) {
                try {
                    queryOutput.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (psLogin != null) {
                try {
                    psLogin.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return roles;
    }
}