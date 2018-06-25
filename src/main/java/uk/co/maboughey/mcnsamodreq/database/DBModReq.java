package uk.co.maboughey.mcnsamodreq.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.type.ModRequest;
import uk.co.maboughey.mcnsamodreq.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBModReq {
    public static List<ModRequest> getRequests(int status, UUID uuid) {
        List<ModRequest> output = new ArrayList<ModRequest>();

        try {
            Connection connection = DatabaseManager.getConnection();
            String sql = "";
            if (uuid == null) {
                sql= "SELECT * FROM modReq WHERE status=? OR (status=1 AND responder=\""+uuid.toString()+"\")";
            }
            else if (Bukkit.getPlayer(uuid).hasPermission("modreq.admin"))
                sql= "SELECT * FROM modReq WHERE status=? OR (status=1 AND responder=\""+uuid.toString()+"\")";
            else
                sql= "SELECT * FROM modReq WHERE status=? AND escalated=0";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, status);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                ModRequest request = new ModRequest();
                request.id = results.getInt("id");
                request.user = UUID.fromString(results.getString("user"));
                request.message = results.getString("message");
                request.status = results.getInt("status");
                request.response = results.getString("response");
                request.server = results.getString("server");
                request.date = results.getDate("date");
                request.escalated = results.getBoolean("escalated");

                //Handle null responder field
                String responder = results.getString("responder");
                if (responder == null) {
                    request.responder = null;
                }
                else {
                    request.responder = UUID.fromString(results.getString("responder"));
                }

                //Location info
                Double pos_x = results.getDouble("pos_x");
                Double pos_y = results.getDouble("pos_y");
                Double pos_z = results.getDouble("pos_z");
                float rot_x = results.getFloat("rot_x");
                float rot_y = results.getFloat("rot_y");
                String world = results.getString("world");

                request.setLocation(pos_x, pos_y, pos_z, rot_x, rot_y, world);

                output.add(request);
            }

        }
        catch (SQLException e) {
            Log.error("SQL Error retrieving requests: "+e.getMessage());
        }

        return output;
    }
    public static List<ModRequest> getUsersRequests(UUID uuid, int status) {
        List<ModRequest> output = new ArrayList<ModRequest>();

        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM modReq WHERE user=? AND status=? ORDER BY status ASC, id DESC");
            statement.setString(1, uuid.toString());
            statement.setInt(2, status);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                ModRequest request = new ModRequest();
                request.id = results.getInt("id");
                request.user = UUID.fromString(results.getString("user"));
                request.message = results.getString("message");
                request.status = results.getInt("status");
                request.response = results.getString("response");
                request.server = results.getString("server");
                request.date = results.getDate("date");
                request.escalated = results.getBoolean("escalated");

                //Handle null responder field
                String responder = results.getString("responder");
                if (responder == null) {
                    request.responder = null;
                }
                else {
                    request.responder = UUID.fromString(results.getString("responder"));
                }

                //Location info
                Double pos_x = results.getDouble("pos_x");
                Double pos_y = results.getDouble("pos_y");
                Double pos_z = results.getDouble("pos_z");
                float rot_x = results.getFloat("rot_x");
                float rot_y = results.getFloat("rot_y");
                String world = results.getString("world");

                request.setLocation(pos_x, pos_y, pos_z, rot_x, rot_y, world);

                output.add(request);
            }

        }
        catch (SQLException e) {
            Log.error("SQL Error retrieving requests: "+e.getMessage());
        }

        return output;
    }
    public static ModRequest getRequest(int id) {
        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM modReq WHERE id=?");
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                ModRequest request = new ModRequest();
                request.id = results.getInt("id");
                request.user = UUID.fromString(results.getString("user"));
                request.message = results.getString("message");
                request.status = results.getInt("status");
                request.response = results.getString("response");
                request.server = results.getString("server");
                request.escalated = results.getBoolean("escalated");

                //Handle null responder field
                String responder = results.getString("responder");
                if (responder == null) {
                    request.responder = null;
                }
                else {
                    request.responder = UUID.fromString(results.getString("responder"));
                }

                //Location info
                Double pos_x = results.getDouble("pos_x");
                Double pos_y = results.getDouble("pos_y");
                Double pos_z = results.getDouble("pos_z");
                float rot_x = results.getFloat("rot_x");
                float rot_y = results.getFloat("rot_y");
                String world = results.getString("world");

                request.setLocation(pos_x, pos_y, pos_z, rot_x, rot_y, world);

                return request;
            }
        }
        catch (SQLException e) {
            Log.error("SQL Error retrieving requests: "+e.getMessage());
        }
        return null;
    }
    public static int getCount(int status, UUID uuid) {
        int count = 0;

        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(id) FROM modReq WHERE status=? AND user=?");
            statement.setInt(1, status);
            statement.setString(2, uuid.toString());
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                count = results.getInt("COUNT(id)");
            }
        }
        catch (SQLException e){
            Log.error("Sql error getting count "+e.getMessage());
        }
        return count;
    }
    public static int getModCount(int status, Player player) {
        int count = 0;

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = null;

            if (status == 0) {
                if (player.hasPermission("modreq.admin"))
                    statement = connection.prepareStatement("SELECT COUNT(id) FROM modReq WHERE status=?");
                else
                    statement = connection.prepareStatement("SELECT COUNT(id) FROM modReq WHERE status=? AND escalated=0");
            }
            else if (status == 1){
                if (player.hasPermission("modreq.admin"))
                    statement = connection.prepareStatement("SELECT COUNT(id) FROM modReq WHERE status=? AND responder=?");
                else
                    statement = connection.prepareStatement("SELECT COUNT(id) FROM modReq WHERE status=? AND responder=? AND escalated=0");
                statement.setString(2, player.getUniqueId().toString());
            }
            statement.setInt(1, status);

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                count = results.getInt("COUNT(id)");
            }
        }
        catch (SQLException e){
           Log.error("Sql error getting count "+e.getMessage());
        }
        return count;
    }
    public static void saveNewRequest(ModRequest request) {
        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO modReq (user, message, server, pos_x, pos_y, pos_z, world, " +
                    "rot_x, rot_y, date, escalated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, request.user.toString());
            statement.setString(2, request.message);
            statement.setString(3, request.server);
            statement.setDouble(4, request.location.getX());
            statement.setDouble(5, request.location.getY());
            statement.setDouble(6, request.location.getZ());
            statement.setString(7, request.location.getWorld().getUID().toString());
            statement.setDouble(8, request.location.getPitch());
            statement.setDouble(9, request.location.getYaw());
            statement.setDate(10, request.date);
            statement.setBoolean(11, request.escalated);

            statement.executeUpdate();
        }
        catch (SQLException e){
            Log.error("Sql error saving new mod request "+e.getMessage());
            Log.error(e.getSQLState());
            Log.error(e.getLocalizedMessage());
        }
    }
    public static void updateRequestDone(ModRequest request){
        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE modReq SET status=?, responder=?, response=? WHERE id=?");

            statement.setInt(1, request.status);
            if (request.responder == null)
                statement.setString(2, null);
            else
                statement.setString(2, request.responder.toString());
            statement.setString(3, request.response);
            statement.setInt(4, request.id);

            statement.executeUpdate();
        }
        catch (SQLException e){
            Log.error("Sql error updating mod request (Done) "+e.getMessage());
        }
    }
    public static void updateRequestClaimed(ModRequest request) {
        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE modReq SET status=?, responder=? WHERE id=?");

            statement.setInt(1, request.status);
            if (request.responder == null)
                statement.setString(2, null);
            else
                statement.setString(2, request.responder.toString());
            statement.setInt(3, request.id);

            statement.executeUpdate();
        }
        catch (SQLException e){
            Log.error("Sql error updating mod request (Claimed) "+e.getMessage());
        }
    }
    public static void updateRequestRead(ModRequest request) {
        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE modReq SET status=? WHERE id=?");

            statement.setInt(1, request.status);
            statement.setInt(2, request.id);

            statement.executeUpdate();
        }
        catch (SQLException e){
            Log.error("Sql error updating mod request (Completed) "+e.getMessage());
        }
    }
    public static void updateRequestEscalation(ModRequest request) {
        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE modReq SET escalated=? WHERE id=?");

            statement.setBoolean(1, request.escalated);
            statement.setInt(2, request.id);

            statement.executeUpdate();
        }
        catch (SQLException e){
            Log.error("Sql error updating mod request (Completed) "+e.getMessage());
        }
    }
    public static List<ModRequest> getRequestsEscalated() {
        List<ModRequest> output = new ArrayList<ModRequest>();

        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM modReq WHERE escalated=1");
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                ModRequest request = new ModRequest();
                request.id = results.getInt("id");
                request.user = UUID.fromString(results.getString("user"));
                request.message = results.getString("message");
                request.status = results.getInt("status");
                request.response = results.getString("response");
                request.server = results.getString("server");
                request.date = results.getDate("date");

                //Handle null responder field
                String responder = results.getString("responder");
                if (responder == null) {
                    request.responder = null;
                }
                else {
                    request.responder = UUID.fromString(results.getString("responder"));
                }

                //Location info
                Double pos_x = results.getDouble("pos_x");
                Double pos_y = results.getDouble("pos_y");
                Double pos_z = results.getDouble("pos_z");
                float rot_x = results.getFloat("rot_x");
                float rot_y = results.getFloat("rot_y");
                String world = results.getString("world");

                request.setLocation(pos_x, pos_y, pos_z, rot_x, rot_y, world);

                output.add(request);
            }

        }
        catch (SQLException e) {
            Log.error("SQL Error retrieving escalated requests: "+e.getMessage());
        }

        return output;
    }
    public static List<ModRequest> getAdminRequests() {
        List<ModRequest> output = new ArrayList<ModRequest>();

        try {
            Connection connection = DatabaseManager.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM modReq WHERE status=0 AND escalated=1");
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                ModRequest request = new ModRequest();
                request.id = results.getInt("id");
                request.user = UUID.fromString(results.getString("user"));
                request.message = results.getString("message");
                request.status = results.getInt("status");
                request.response = results.getString("response");
                request.server = results.getString("server");
                request.date = results.getDate("date");
                request.escalated = results.getBoolean("escalated");

                //Handle null responder field
                String responder = results.getString("responder");
                if (responder == null) {
                    request.responder = null;
                }
                else {
                    request.responder = UUID.fromString(results.getString("responder"));
                }

                //Location info
                Double pos_x = results.getDouble("pos_x");
                Double pos_y = results.getDouble("pos_y");
                Double pos_z = results.getDouble("pos_z");
                float rot_x = results.getFloat("rot_x");
                float rot_y = results.getFloat("rot_y");
                String world = results.getString("world");

                request.setLocation(pos_x, pos_y, pos_z, rot_x, rot_y, world);

                output.add(request);
            }

        }
        catch (SQLException e) {
            Log.error("SQL Error retrieving requests: "+e.getMessage());
        }

        return output;
    }

}
