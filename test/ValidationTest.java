package test;

/**
 * Test class to demonstrate validation logic
 * This is for documentation purposes only
 */
public class ValidationTest {
    
    /**
     * Test phone number validation
     */
    public static void testPhoneValidation() {
        System.out.println("=== Phone Number Validation Tests ===\n");
        
        String[] validPhones = {
            "0612345678",
            "0512345678", 
            "0712345678",
            "06 12 34 56 78",
            "06-12-34-56-78"
        };
        
        String[] invalidPhones = {
            "0812345678",  // starts with 08
            "061234567",   // only 9 digits
            "06123456789", // 11 digits
            "12345678",    // doesn't start with 06/05/07
            "0612abc678",  // contains letters
            ""             // empty
        };
        
        System.out.println("Valid phone numbers:");
        for (String phone : validPhones) {
            System.out.println("  ✓ " + phone);
        }
        
        System.out.println("\nInvalid phone numbers:");
        for (String phone : invalidPhones) {
            System.out.println("  ✗ " + phone);
        }
    }
    
    /**
     * Test email validation
     */
    public static void testEmailValidation() {
        System.out.println("\n\n=== Email Validation Tests ===\n");
        
        String[] validEmails = {
            "user@gmail.com",
            "ahmed.ali@yahoo.fr",
            "contact@company.dz",
            "test_user@outlook.com",
            "name123@domain.co.uk"
        };
        
        String[] invalidEmails = {
            "usergmail.com",     // missing @
            "user@gmail",        // missing domain extension
            "@gmail.com",        // missing username
            "user@",             // missing domain
            "user @gmail.com",   // space in email
            ""                   // empty
        };
        
        System.out.println("Valid emails:");
        for (String email : validEmails) {
            System.out.println("  ✓ " + email);
        }
        
        System.out.println("\nInvalid emails:");
        for (String email : invalidEmails) {
            System.out.println("  ✗ " + email);
        }
    }
    
    /**
     * Test password confirmation
     */
    public static void testPasswordConfirmation() {
        System.out.println("\n\n=== Password Confirmation Tests ===\n");
        
        System.out.println("Matching passwords:");
        System.out.println("  Password: 'MyPassword123'");
        System.out.println("  Confirm:  'MyPassword123'");
        System.out.println("  Result: ✓ VALID\n");
        
        System.out.println("Non-matching passwords:");
        System.out.println("  Password: 'MyPassword123'");
        System.out.println("  Confirm:  'MyPassword124'");
        System.out.println("  Result: ✗ INVALID - Passwords don't match\n");
        
        System.out.println("  Password: 'MyPassword123'");
        System.out.println("  Confirm:  'mypassword123'");
        System.out.println("  Result: ✗ INVALID - Case sensitive\n");
    }
    
    /**
     * Main method to run all tests
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   AissaGo Registration Validation Tests       ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        testPhoneValidation();
        testEmailValidation();
        testPasswordConfirmation();
        
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║   All validation examples displayed           ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }
}
