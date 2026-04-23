# 🎨 دليل استخدام FlatLaf في مشروع AissaGo

## ✅ الحالة الحالية

تم تثبيت وتفعيل مكتبة **FlatLaf 3.7** بنجاح في المشروع!

## 📁 الملفات المهمة

### 1. نقطة الدخول الرئيسية
**الملف**: `src/aissagoapp/AissaGoApp.java`

هذا هو المكان الذي يتم فيه تفعيل FlatLaf لكامل التطبيق:

```java
public static void main(String[] args) {
    try {
        // تفعيل الثيم الفاتح
        FlatLightLaf.setup();
        
        // تخصيصات إضافية
        UIManager.put("Button.arc", 10);        // زوايا مستديرة للأزرار
        UIManager.put("Component.arc", 10);     // زوايا مستديرة للمكونات
        UIManager.put("TextComponent.arc", 10); // زوايا مستديرة للحقول
    } catch (Exception ex) {
        System.err.println("خطأ في تفعيل FlatLaf: " + ex.getMessage());
    }
    
    SwingUtilities.invokeLater(() -> {
        new LoginFrame().setVisible(true);
    });
}
```

### 2. المكتبة
**الموقع**: `lib/flatlaf-3.7.jar`

### 3. الإعدادات
**الملف**: `nbproject/project.properties`
- السطر 37: `file.reference.flatlaf-3.7.jar=lib/flatlaf-3.7.jar`
- السطر 42: المكتبة مضافة إلى classpath

## 🎨 تغيير الثيم

### الثيم الفاتح (الحالي)
```java
FlatLightLaf.setup();
```

### الثيم الداكن
لتفعيل الوضع الليلي، غيّر في `AissaGoApp.java`:

```java
// استبدل:
FlatLightLaf.setup();

// بـ:
FlatDarkLaf.setup();
```

ولا تنسَ إضافة الاستيراد:
```java
import com.formdev.flatlaf.FlatDarkLaf;
```

## 🔧 التخصيصات المتاحة

### الزوايا المستديرة
```java
UIManager.put("Button.arc", 15);           // أزرار أكثر استدارة
UIManager.put("Component.arc", 15);        // مكونات أكثر استدارة
UIManager.put("TextComponent.arc", 15);    // حقول أكثر استدارة
```

### الألوان
```java
UIManager.put("Button.background", new Color(63, 81, 181));
UIManager.put("Button.foreground", Color.WHITE);
```

### الهوامش
```java
UIManager.put("Button.margin", new Insets(10, 20, 10, 20));
```

## 📚 ملفات إضافية

1. **`FLATLAF_GUIDE.md`** - دليل شامل بالفرنسية
2. **`src/view/examples/FlatLafExamplePanel.java`** - أمثلة عملية للمكونات

## ✨ المزايا

- ✅ واجهة عصرية ومهنية
- ✅ دعم الوضع الفاتح والداكن
- ✅ تطبيق تلقائي على جميع المكونات
- ✅ سهولة التخصيص
- ✅ أداء ممتاز

## 🚀 كيفية التشغيل

1. افتح المشروع في NetBeans
2. شغّل الملف الرئيسي: `AissaGoApp.java`
3. ستظهر واجهة تسجيل الدخول بتصميم FlatLaf الحديث

## 🎯 الملفات المستفيدة من FlatLaf

جميع الملفات في مجلد `view/` تستفيد تلقائيًا من FlatLaf:
- ✅ LoginFrame.java
- ✅ MainFrame.java
- ✅ RegistrationFrame.java
- ✅ PaymentDialog.java
- ✅ ProfileDialog.java
- ✅ وجميع المكونات الأخرى

## 📝 ملاحظات مهمة

1. **لا تحتاج** لاستيراد FlatLaf في كل ملف
2. التفعيل يتم **مرة واحدة** في `AissaGoApp.java`
3. جميع المكونات **ترث** التصميم تلقائيًا
4. يمكنك **تخصيص** مكونات فردية باستخدام `putClientProperty()`

## 🔗 روابط مفيدة

- الموقع الرسمي: https://www.formdev.com/flatlaf/
- GitHub: https://github.com/JFormDesigner/FlatLaf
- التوثيق: https://www.formdev.com/flatlaf/themes/

---

## ⚠️ حل تحذيرات Java 21+

### المشكلة
إذا كنت تستخدم Java 21 أو أحدث، قد تظهر تحذيرات:
```
WARNING: java.lang.System::load has been called by com.formdev.flatlaf.util.NativeLibrary
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning
```

### الحل المطبق ✅
تم تحديث `nbproject/project.properties`:
```properties
run.jvmargs=--enable-native-access=ALL-UNNAMED
```

### التوثيق الكامل
- 📄 دليل مفصل بالعربية: `FLATLAF_WARNING_FIX_AR.md`
- 📄 دليل بالفرنسية: `FLATLAF_WARNING_FIX.md`

---

**تم التحديث**: 2025-12-15
**الإصدار**: FlatLaf 3.7
**الحالة**: ✅ مفعّل وجاهز للاستخدام
