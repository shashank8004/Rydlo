# 🏍️ Rydlo - Bike Rental System

Rydlo is a full-stack bike rental platform connecting bike owners with customers. It provides a seamless experience for browsing, booking, and managing bike rentals with role-based access for Customers, Owners, and Admins.

## 🚀 Key Features

### 👤 Customer
- **Smart Search**: Find bikes by location, city, or pincode with real-time availability checks.
- **Booking System**: Select pickup/drop-off dates, view automated price calculations (including taxes), and book rides.
- **Ride History**: View past and upcoming bookings with status updates.
- **Profile Management**: Update personal details and password.

### 🏢 Bike Owner
- **Fleet Management**: Add, update, and remove bikes from the listing.
- **Dashboard**: View earnings and rental limits.

### 🛡️ Admin
- **User Management**: Oversee all customers and owners.
- **Platform Oversight**: Monitor transactions and system activity.

## 🛠️ Tech Stack

**Frontend:**
- React.js (Vite)
- Tailwind CSS
- Lucide React (Icons)
- Axios (API Integration)
- React Router DOM

**Backend:**
- Java 17+
- Spring Boot 3.x
- Spring Security (JWT Authentication)
- Spring Data JPA (Hibernate)
- MySQL Database
- Maven

## ⚙️ Setup & Installation

### Prerequisites
- Java JDK 17 or higher
- Node.js & npm
- MySQL Server

### 1. Backend Setup
1. Used `git clone` to get the repo (if you haven't already).
2. Configure your database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/rydlo_db
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```
3. Run the application:
   ```bash
   cd Rydlo
   ./mvnw spring-boot:run
   ```

### 2. Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd rydlo-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```

## 🔐 API Security
- **JWT (JSON Web Token)** is used for securing endpoints.
- **BCrypt** is used for password hashing.
- Role-based authorization ensures users only access permitted features.

## 🤝 Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a Pull Request.
