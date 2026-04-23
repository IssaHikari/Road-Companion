
package service;

import dao.UserDAO;
import model.User;
import java.sql.SQLException;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * تسجيل مستخدم جديد في النظام بعد التحقق من صحة البيانات.
     * 
     * @param user كائن المستخدم المراد تسجيله.
     * @return User كائن المستخدم المكتمل بعد التسجيل، أو null في حالة الفشل.
     * @throws Exception إذا كانت هناك بيانات مفقودة أو فشل في قاعدة البيانات.
     */
    public User registerUser(User user) throws Exception {

        // --- [قواعد التحقق Business Rules] ---
        if (user.getName() == null || user.getName().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPassword() == null || user.getPassword().length() < 6 ||
                user.getRole() == null || user.getRole().isEmpty()) {

            throw new IllegalArgumentException("يجب ملء جميع الحقول الأساسية وتأكيد كلمة المرور لا تقل عن 6 أحرف.");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("يجب إدخال رقم الهاتف.");
        }
        // يمكنك إضافة التحقق من تنسيق البريد الإلكتروني هنا
        // if (!isValidEmail(user.getEmail())) { ... }

        // التحقق من أن الدور المدخل صحيح
        if (!user.getRole().equals("DRIVER") && !user.getRole().equals("PASSENGER")) {
            throw new IllegalArgumentException("الدور غير صحيح.");
        }

        // --- [تنفيذ عملية الوصول للبيانات] ---
        try {
            // التحقق من عدم وجود مستخدم بنفس البريد الإلكتروني (قاعدة عمل مهمة)
            if (userDAO.isEmailTaken(user.getEmail())) {
                throw new Exception("البريد الإلكتروني مُسجل بالفعل.");
            }

            // يتم إرسال الكائن لطبقة DAO للتسجيل الفعلي
            return userDAO.registerUser(user);

        } catch (SQLException e) {
            // تسجيل الخطأ أو إعادة رفعه كـ Exception موضح أكثر
            e.printStackTrace();
            throw new Exception("فشل في الوصول إلى قاعدة البيانات أثناء التسجيل.");
        }
    }

    // داخل كلاس UserService

    public User authenticateUser(String email, String password) throws Exception {

        // ... (يمكن وضع قواعد عمل هنا قبل الوصول إلى DAO) ...

        try {
            return userDAO.authenticate(email, password);
        } catch (SQLException e) {
            throw new Exception("فشل في الوصول إلى قاعدة البيانات أثناء المصادقة.");
        }
    }

    /**
     * Get user details by ID.
     */
    public User getUserById(int userId) throws Exception {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            throw new Exception("Error fetching user details: " + e.getMessage());
        }
    }

    // ... يمكن إضافة دوال أخرى مثل authenticateUser(email, password), etc.
}