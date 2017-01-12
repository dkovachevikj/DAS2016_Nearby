package com.dmgremlins.nearby;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nacev on 08.01.2017.
 */
public class TestConnection{

    protected Activity activity;
    public ArrayList<Review> reviews;
    private boolean flag;

    public TestConnection(Activity activity){
        flag=false;
        this.activity=activity;
        reviews=new ArrayList<Review>();
    }
private class WorkerThread extends AsyncTask<String,Void,Void> {

    private ProgressDialog dialog;
    @Override
    protected void onPreExecute() {
        reviews = new ArrayList<Review>();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {

        Log.d("GetInfoByName",params[0]);
        ArrayList<Review> reviews = new ArrayList<Review>();
        int i = 0;
        String connectionUrl = "jdbc:jtds:sqlserver://sqlservermihail.database.windows.net:1433/DB_DAS;user=mihailnacev@sqlservermihail;password=ABCabc123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
        // Declare the JDBC objects.
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try

        {
            // Establish the connection.
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(connectionUrl);

            // Create and execute an SQL statement that returns some data.
            String SQL = "SELECT * FROM REVIEWS";
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                reviews.add(new Review(rs.getString(2), rs.getFloat(3), rs.getString(4)));
                Log.d(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4),"LINE");
            }
            flag=true;
        }

        // Handle any errors that may have occurred.
        catch (Exception e)

        {
            e.printStackTrace();
        } finally

        {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (con != null) try {
                con.close();
            } catch (Exception e) {
            }
        }
        Intent intent = new Intent();
        intent.setAction("ReviewsFilledWithInformation");
        intent.putExtra("Reviews",reviews);
        activity.getApplicationContext().sendBroadcast(intent);
        Log.d("Reviews","Broadcast sent");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
    public void getReviews(){
        new WorkerThread().execute("1234");
    }
}

