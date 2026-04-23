
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // (غيّر هذه المتغيرات لتناسب إعدادات قاعدة بياناتك - مثال MySQL)
    private static final String URL = "jdbc:mysql://localhost:3306/transport_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * تُرجع كائن اتصال جديد بقاعدة البيانات.
     * 
     * @return Connection
     * @throws SQLException إذا فشل الاتصال.
     */
    public static Connection getConnection() throws SQLException {
        // يتم تحميل Driver تلقائيًا في إصدارات JDBC الحديثة
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * وظيفة مساعدة لإغلاق موارد قاعدة البيانات.
     */
    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                // يمكنك تسجيل الخطأ هنا
                e.printStackTrace();
            }
        }
    }
}