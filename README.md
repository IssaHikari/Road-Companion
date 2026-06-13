<!-- ===== BANNER ===== -->
<p align="center">
  <img src="images/road_companion_banner.png" alt="Road-Companion Banner" width="100%"/>
</p>

<p align="center">
  <img src="images/logoapp.avif" alt="Road-Companion Logo" width="120"/>
</p>

<h1 align="center">🚗 ROAD-COMPANION</h1>

<p align="center">
  <strong>Carpooling Platform · Java Desktop App</strong><br/>
  <em>智能拼车平台 · Java 桌面应用程序</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java_SE-17+-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java SE"/>
  <img src="https://img.shields.io/badge/Java_Swing-Desktop_UI-6C63FF?style=for-the-badge&logo=java&logoColor=white" alt="Java Swing"/>
  <img src="https://img.shields.io/badge/FlatLaf-3.7-43E6FC?style=for-the-badge" alt="FlatLaf 3.7"/>
  <img src="https://img.shields.io/badge/MySQL-8.0+-0096C8?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/i18n-EN_|_AR_|_FR-FFD700?style=for-the-badge&logo=googletranslate&logoColor=white" alt="i18n"/>
  <img src="https://img.shields.io/badge/Academic-v1.0.0-FF6584?style=for-the-badge&logo=graduation-cap&logoColor=white" alt="Academic v1.0.0"/>
</p>

<p align="center"><em>Travel smart. Travel together.</em></p>

---

## 📖 About the Project

<img align="right" src="images/reze_character.png" alt="Reze — Project Mascot" width="260"/>

**Road-Companion** is a full-featured desktop carpooling platform built entirely in Java. It connects *drivers* who have empty seats with *passengers* traveling the same route — reducing travel costs, cutting fuel expenses, and easing traffic congestion.

Designed as a complete academic project for the **University of Béjaïa (2024–2025)**, it demonstrates real-world MVC architecture, multi-role authentication, database-driven persistence, and a polished modern UI.

> 智能拼车平台 — Road-Companion 是一个完整的桌面拼车平台，完全用 Java 构建。它将有空座位的司机与同路乘客连接起来，旨在降低出行成本、分摊燃油费用并缓解交通拥堵。本项目作为贝贾亚大学 2024–2025 学年完整学术项目，展示了真实的 MVC 架构、多角色认证、数据库持久化和精致的现代 UI。

<br clear="right"/>

---

## ⚡ Core Features

| | Feature | Description |
|---|---|---|
| 🔐 | **Multi-Role Authentication** | Separate accounts for Drivers, Passengers, and Admins with role-specific dashboards and permissions. |
| ✅ | **Driver Verification** | Drivers must upload their license and vehicle details. Admins review and approve/reject before they can post trips. |
| 🔍 | **Flexible Trip Search** | Search available trips by departure location, destination, and travel date with real-time results. |
| ⭐ | **Ratings & Reviews** | After every trip, drivers and passengers rate each other to ensure safety and build community trust. |
| 🏆 | **Leaderboard** | Displays the most active or highest-rated drivers and passengers to gamify the experience. |
| 💳 | **Mock Payment System** | A realistic credit-card payment dialog simulates booking confirmation and electronic payment flow. |
| 🔔 | **Notification System** | Real-time alerts when bookings are confirmed, cancelled, or account status is updated. |
| 🌍 | **Multi-Language Support** | Full i18n with Arabic, English, and French — switchable dynamically via `.properties` files. |

---

## 👥 User Roles

### 🚗 Driver — *司机*
- ✅ Post and manage trips
- ✅ Review booking requests
- ✅ Upload vehicle documents
- ✅ Rate passengers
- ✅ View earnings summary

### 🧳 Passenger — *乘客*
- ✅ Search available trips
- ✅ Book seats & pay
- ✅ Manage reservations
- ✅ Rate drivers
- ✅ View booking history

### 🛡 Admin — *管理员*
- ✅ Verify driver documents
- ✅ Approve/reject accounts
- ✅ Monitor all users
- ✅ Manage reports
- ✅ View platform stats

---

## 🛠 Tech Stack

| Technology | Purpose |
|---|---|
| ☕ **Java SE 17+** | Core language — JDK 17 or later |
| 🖥 **Java Swing** | Desktop GUI framework |
| ✨ **FlatLaf 3.7** | Modern Look & Feel theming |
| 🗄 **MySQL 8.0+** | Relational database backend |
| 🔌 **JDBC** | Database connectivity layer |
| 🌍 **i18n (.properties)** | Internationalization (EN / AR / FR) |

---

## 🏗 Architecture

The project follows a clean **MVC (Model-View-Controller)** architecture:

```
┌──────────────────────────────────────────────────────┐
│                    VIEW LAYER                        │
│  LoginFrame · MainFrame · AdminDashboard             │
│  SearchTripPanel · ProfileDialog · PaymentDialog     │
│  LeaderboardPanel · RatingDialog · ...               │
├──────────────────────────────────────────────────────┤
│                  SERVICE LAYER                       │
│  UserService · TripService                           │
│  BookingService · ReportService                      │
├──────────────────────────────────────────────────────┤
│                    DAO LAYER                          │
│  UserDAO · TripDAO · BookingDAO                      │
│  RatingDAO · NotificationDAO · ReportDAO             │
├──────────────────────────────────────────────────────┤
│                   MODEL LAYER                        │
│  User · Trip · Booking                               │
│  Rating · Notification · Report                      │
├──────────────────────────────────────────────────────┤
│                  UTILITY LAYER                       │
│  DBConnection · LanguageManager                      │
│  AlgeriaLocations · IconManager · PasswordUtils      │
└──────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
Road-Companion/
├── src/
│   ├── aissagoapp/           # Main application entry point
│   ├── model/                # Data models (POJOs)
│   │   ├── User.java
│   │   ├── Trip.java
│   │   ├── Booking.java
│   │   ├── Rating.java
│   │   ├── Notification.java
│   │   └── Report.java
│   ├── dao/                  # Data Access Objects (JDBC)
│   │   ├── DBConnection.java
│   │   ├── UserDAO.java
│   │   ├── TripDAO.java
│   │   ├── BookingDAO.java
│   │   ├── RatingDAO.java
│   │   ├── NotificationDAO.java
│   │   └── ReportDAO.java
│   ├── service/              # Business logic layer
│   │   ├── UserService.java
│   │   ├── TripService.java
│   │   ├── BookingService.java
│   │   └── ReportService.java
│   ├── view/                 # Swing UI components
│   │   ├── LoginFrame.java
│   │   ├── RegistrationFrame.java
│   │   ├── MainFrame.java
│   │   ├── SearchTripPanel.java
│   │   ├── CreateTripPanel.java
│   │   ├── DriverTripsPanel.java
│   │   ├── DriverBookingsPanel.java
│   │   ├── PassengerBookingsPanel.java
│   │   ├── LeaderboardPanel.java
│   │   ├── ProfileDialog.java
│   │   ├── PaymentDialog.java
│   │   ├── RatingDialog.java
│   │   ├── TripDetailsDialog.java
│   │   ├── LocationSelectorDialog.java
│   │   ├── admin/
│   │   │   └── AdminDashboard.java
│   │   └── settings/
│   ├── util/                 # Utility classes
│   │   ├── AlgeriaLocations.java
│   │   ├── IconManager.java
│   │   ├── LanguageManager.java
│   │   └── PasswordUtils.java
│   └── i18n/                 # Internationalization
│       ├── messages_en.properties
│       ├── messages_ar.properties
│       └── messages_fr.properties
├── lib/                      # External libraries
│   ├── flatlaf-3.7.jar
│   └── mysql-connector-j-8.3.0.jar
├── images/                   # Application assets
├── icons/                    # UI icons
├── database.sql              # MySQL schema setup
├── build.xml                 # Ant build configuration
└── README.md
```

---

## 🗄 Database Schema

The application uses **MySQL** with the following tables:

| Table | Description |
|---|---|
| `Users` | User accounts with role-based fields (driver verification, vehicle info, avatar, etc.) |
| `Trips_Offered` | Trips posted by verified drivers (origin, destination, date, price, seats) |
| `Bookings` | Passenger bookings linked to trips (payment status, booking status) |
| `Ratings` | Mutual ratings between drivers and passengers (1–5 score + comment) |
| `Notifications` | In-app notifications for booking updates and account events |

To initialize the database:

```sql
mysql -u root -p < database.sql
```

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 17** or later
- **MySQL 8.0+** server running
- **Apache Ant** (optional, for build automation)

### Installation

**1.** Clone the repository:

```bash
git clone https://github.com/IssaHikari/Road-Companion.git
cd Road-Companion
```

**2.** Set up the database:

```bash
mysql -u root -p < database.sql
```

**3.** Configure database connection:

Update the database credentials in `src/dao/DBConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/transport_db";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

**4.** Add libraries to classpath:

Ensure `lib/flatlaf-3.7.jar` and `lib/mysql-connector-j-8.3.0.jar` are included in your project classpath.

**5.** Build and run:

```bash
# Using Ant
ant run

# Or compile and run manually
javac -cp "lib/*" -d bin src/**/*.java
java -cp "bin:lib/*" aissagoapp.Main
```

---

## 🌍 Internationalization

The application supports three languages out of the box:

| Language | Properties File | Direction |
|---|---|---|
| 🇬🇧 English | `messages_en.properties` | LTR |
| 🇫🇷 French | `messages_fr.properties` | LTR |
| 🇸🇦 Arabic | `messages_ar.properties` | RTL |

Languages can be switched dynamically at runtime through the settings panel via the `LanguageManager` utility class.

---

## 🎓 Academic Context

This project was developed as a comprehensive academic project for the **University of Béjaïa, Faculty of Exact Sciences, Department of Computer Science** during the **2024–2025 academic year**.

It demonstrates proficiency in:
- Object-Oriented Programming with Java
- MVC architectural pattern
- Database design and JDBC operations
- Desktop UI development with Java Swing
- Modern UI theming with FlatLaf
- Internationalization and localization
- Multi-role access control systems

---

## 📄 License

This project is developed for academic purposes at the University of Béjaïa.

---

<p align="center">
  <strong>ROAD-COMPANION</strong><br/>
  <em>Smart carpooling for a greener tomorrow 🌱</em><br/>
  <sub>University of Béjaïa · 2024–2025</sub>
</p>
