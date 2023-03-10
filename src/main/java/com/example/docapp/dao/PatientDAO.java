package com.example.docapp.dao;

import com.example.docapp.models.Utilisateur;
import com.example.docapp.util.DBUtil;
import com.example.docapp.models.Patient;
import com.example.docapp.util.DateFormatter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Vector;

public class PatientDAO {
    public static int addPatient(Patient patient) {
        PreparedStatement psAddPatient = null;
        ResultSet queryOutput = null;
        int statusCode=0;

        try {
            Connection connection = DBUtil.getConnection();

            psAddPatient = connection.prepareStatement("INSERT INTO patient (first_name,last_name,birth_date,cin,phone,description, join_date) VALUES (?, ?, ?, ?, ?, ?, ?)");
            psAddPatient.setString(1, patient.getFirstName());
            psAddPatient.setString(2, patient.getLastName());
            psAddPatient.setString(3,  patient.getBirthDate());
            psAddPatient.setString(4, patient.getCin());
            psAddPatient.setString(5, patient.getPhoneNumber());
            psAddPatient.setString(6, patient.getDescription());
            psAddPatient.setString(7, LocalDateTime.now().format(DateFormatter.formatter).split(" ")[0]);

            psAddPatient.executeUpdate();

            psAddPatient = connection.prepareStatement("insert into action  (id_utilisateur,action,action_time) values (?,?,?)");
            psAddPatient.setInt(1, Utilisateur.currentUser.getId());
            psAddPatient.setString(2, "Ajout d'un patient");
            psAddPatient.setString(3, LocalDateTime.now().format(DateFormatter.formatter));
            psAddPatient.executeUpdate();

            statusCode=201;
        } catch (SQLException e) {
            statusCode = 400;
            throw new RuntimeException(e);

        } finally {
            if (psAddPatient != null) {
                try {
                    psAddPatient.close();
                } catch (SQLException e) {
                    statusCode = 400;
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return statusCode;
    }

    public static Vector<Patient> searchPatients(String search) {
        Vector<Patient> patients = new Vector<Patient>();
        Patient patient;
        PreparedStatement psLogin = null;
        ResultSet queryOutput = null;

        try {
            Connection connection = DBUtil.getConnection();

            psLogin = connection.prepareStatement("SELECT * FROM patient where first_name like ? or last_name like ? or cin like ? or phone like ? order by id DESC  ");
              psLogin.setString(1, "%"+search+"%");
                psLogin.setString(2, "%"+search+"%");
                psLogin.setString(3, "%"+search+"%");
                psLogin.setString(4, "%"+search+"%");

            queryOutput = psLogin.executeQuery();

            while (queryOutput.next()) {
                patient = new Patient();
                patient.setId(queryOutput.getInt("id"));
                patient.setFirstName(queryOutput.getString("first_name"));
                patient.setLastName(queryOutput.getString("last_name"));
                patient.setBirthDate(queryOutput.getString("birth_date"));
                patient.setCin(queryOutput.getString("cin"));
                patient.setPhoneNumber(queryOutput.getString("phone"));
                patient.setDescription(queryOutput.getString("description"));

                patients.add(patient);
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

        return patients;
    }

    public static int editPatient(Patient patient){
        PreparedStatement psAddP = null;
        ResultSet queryOutput = null;
        int statusCode=0;

        try {
            Connection connection = DBUtil.getConnection();

            psAddP = connection.prepareStatement("UPDATE patient SET first_name = ?, last_name = ?, birth_date = ?, cin = ?, phone = ?,description=? WHERE id = ?");
            psAddP.setString(1, patient.getFirstName());
            psAddP.setString(2, patient.getLastName());
            psAddP.setString(3,  patient.getBirthDate());
            psAddP.setString(4, patient.getCin());
            psAddP.setString(5, patient.getPhoneNumber());
            psAddP.setString(6, patient.getDescription());
            psAddP.setInt(7, patient.getId());

            psAddP.executeUpdate();

            psAddP = connection.prepareStatement("insert into action  (id_utilisateur,action,action_time) values (?,?,?)");
            psAddP.setInt(1, Utilisateur.currentUser.getId());
            psAddP.setString(2, "Modification d'un patient id : "+patient.getId());
            psAddP.setString(3, LocalDateTime.now().format(DateFormatter.formatter));

            psAddP.executeUpdate();
            statusCode=201;
        } catch (SQLException e) {
            statusCode = 400;
            throw new RuntimeException(e);
        } finally {
            if (psAddP != null) {
                try {
                    psAddP.close();
                } catch (SQLException e) {
                    statusCode = 400;
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return statusCode;
    }

    public static int deletePatient(int id){
        PreparedStatement psAddP = null;
        ResultSet queryOutput = null;
        int statusCode=0;

        try {
            Connection connection = DBUtil.getConnection();

            psAddP = connection.prepareStatement("DELETE from patient WHERE id=?");

            psAddP.setInt(1, id);

            psAddP.executeUpdate();

            psAddP = connection.prepareStatement("insert into action  (id_utilisateur,action,action_time) values (?,?,?)");
            psAddP.setInt(1, Utilisateur.currentUser.getId());
            psAddP.setString(2, "Suppression d'un patient id : "+id);
            psAddP.setString(3, LocalDateTime.now().format(DateFormatter.formatter));
            psAddP.executeUpdate();

            statusCode=200;
        } catch (SQLException e) {
            statusCode = 400;
            throw new RuntimeException(e);
        } finally {
            if (psAddP != null) {
                try {
                    psAddP.close();
                } catch (SQLException e) {
                    statusCode = 400;
                    e.printStackTrace();
                }
            }

            DBUtil.stopConnection();
        }

        return statusCode;
    }

    public static Vector <Patient> getRecentPatients(){
        Vector<Patient> patients = new Vector<Patient>();
        Patient patient;
        PreparedStatement psLogin = null;
        ResultSet queryOutput = null;

        try {
            Connection connection = DBUtil.getConnection();
            psLogin = connection.prepareStatement("SELECT * FROM patient order by join_date DESC LIMIT 5");
            queryOutput = psLogin.executeQuery();
            while (queryOutput.next()) {
                patient = new Patient();
                patient.setId(queryOutput.getInt("id"));
                patient.setFirstName(queryOutput.getString("first_name"));
                patient.setLastName(queryOutput.getString("last_name"));
                patient.setBirthDate(queryOutput.getString("birth_date"));
                patient.setCin(queryOutput.getString("cin"));
                patient.setPhoneNumber(queryOutput.getString("phone"));
                patient.setDescription(queryOutput.getString("description"));
                patient.setJoin_date( queryOutput.getString("join_date"));

                patients.add(patient);
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

        return patients;
    }

    public static Vector <Patient> getPatients(){
        Vector<Patient> patients = new Vector<Patient>();
        Patient patient;
        PreparedStatement psLogin = null;
        ResultSet queryOutput = null;

        try {
            Connection connection = DBUtil.getConnection();
            psLogin = connection.prepareStatement("SELECT * FROM patient order by id DESC ");
            queryOutput = psLogin.executeQuery();
            while (queryOutput.next()) {
                patient = new Patient();
                patient.setId(queryOutput.getInt("id"));
                patient.setFirstName(queryOutput.getString("first_name"));
                patient.setLastName(queryOutput.getString("last_name"));
                patient.setBirthDate(queryOutput.getString("birth_date"));
                patient.setCin(queryOutput.getString("cin"));
                patient.setPhoneNumber(queryOutput.getString("phone"));
                patient.setDescription(queryOutput.getString("description"));
                patient.setJoin_date( queryOutput.getString("join_date"));

                patients.add(patient);
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

        return patients;
    }

    public static Patient getPatientByID(String id){

        Patient patient = new Patient();
        PreparedStatement psLogin = null;
        ResultSet queryOutput = null;

        try {
            Connection connection = DBUtil.getConnection();
            psLogin = connection.prepareStatement("SELECT * FROM patient where id = ?");
            psLogin.setString(1, id);
            queryOutput = psLogin.executeQuery();
            if (queryOutput.next()) {
                patient.setId(queryOutput.getInt("id"));
                patient.setFirstName(queryOutput.getString("first_name"));
                patient.setLastName(queryOutput.getString("last_name"));
               patient.setBirthDate(queryOutput.getString("birth_date"));
                patient.setCin(queryOutput.getString("cin"));
                patient.setPhoneNumber(queryOutput.getString("phone"));
                patient.setDescription(queryOutput.getString("description"));
                patient.setJoin_date( queryOutput.getString("join_date"));
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

        return patient;
    }
}
