package main.unipi.iot;

public class DBManager {
//TODO da fare

    private static String MySqlIp;
    private static int MySqlPort;
    private static String MySqlUsername;
    private static String MySqlPassword;
    private static String MySqlName;

    //TODO set password,ip,name,username and port

    private Connection getConnection() throws SQLException {
            return DriverManager.getConnection("jdbc:mysql://"+ MySqlIp + ":" + MySqlPort +
                            "/" + MySqlName + "?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET",
                    MySqlUsername, MySqlPassword);
        }

    public void insert(String tableName, int nArg,String[] attrNames,String[] values){
        
        //preparo la stringa in formato INSERT INTO table name (attr1,attr2,...,attrn) VALUES (v1,v2,....,vn)
        String statementString="INSERT INTO "+tableName+" (";

        for(int i=0;i<nArg;i++){
            statementString=statementString+attrNames[i];
            if(i<nArg-1){
                statementString+=", ";
            }
        }
        statementString+=") VALUES (";
        for(int i=0;i<nArg;i++){
                statementString=statementString+values[i];
                if(i<nArg-1){
                    statementString+=", ";
                }
            }
        statementString+=")";
        try (
                    Connection connection = getConnection();
                    PreparedStatement statement = connection.prepareStatement(statementString);
            )
            {
                statement.executeUpdate();
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
            }
    }
    
}
