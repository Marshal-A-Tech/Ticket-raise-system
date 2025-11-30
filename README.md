# Ticket Raise System – AWS Deployed Backend (Spring Boot + MySQL)

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3-green)
![AWS EC2](https://img.shields.io/badge/AWS-EC2-orange)
![AWS RDS](https://img.shields.io/badge/AWS-RDS-blueviolet)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![JWT](https://img.shields.io/badge/JWT-Auth-yellow)
![Status](https://img.shields.io/badge/Status-Deployed-success)

A production-style **Ticket Management Backend** deployed on **AWS EC2** with a secure **RDS MySQL** database inside a custom **VPC**.  
Built using **Spring Boot**, **JWT Authentication**, and deployed with **systemd** for auto-start.

---

# 🌐 Live API Documentation
Replace `<EC2-PUBLIC-IP>` with your instance IP:

👉 `http://<EC2-PUBLIC-IP>:8080/swagger-ui/index.html`

---

# 🏗️ Architecture Overview


- EC2 → Public subnet  
- RDS → Private subnet  
- DB accessible **only from EC2 SG**  
- Spring Boot runs via **systemd service**

---

# ⚙️ Tech Stack

### Backend
- Java 17  
- Spring Boot 3  
- Spring Security + JWT  
- Hibernate JPA  
- MySQL  
- Maven  

### AWS
- EC2  
- RDS MySQL  
- VPC  
- Subnets  
- Security Groups  
- Internet Gateway  
- Linux systemd  

---

# 🎯 Key Features

### 🔐 Authentication
- User registration  
- Login with JWT  
- Role-based access:  
  - `ROLE_USER`  
  - `ROLE_TEAMMEMBER`  
  - `ROLE_GATEKEEPER`

### 🎫 Ticket Operations
- Raise ticket  
- Update ticket  
- View user tickets  
- Admin/Gatekeeper endpoints  

### 📘 Swagger Documentation
Interactive API explorer and testing interface.

---

# 📂 Project Structure
Ticket-raise-system/
├── src/main/java/com/itil/
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── config/
│ ├── filter/
│ └── model/
├── src/main/resources/application.properties
├── pom.xml
└── README.md

---

# 💾 Local Setup

### 1️⃣ Clone Project
```bash
2️⃣ Configure Database

Edit application.properties:
git clone https://github.com/Marshal-A-Tech/Ticket-raise-system.git
cd Ticket-raise-system
spring.datasource.url=jdbc:mysql://localhost:3306/ticketdb
spring.datasource.username=root
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update

3️⃣ Build & Run
mvn clean package -DskipTests
java -jar target/itilproject-0.0.1-SNAPSHOT.jar
☁️ AWS Deployment Summary
✔ VPC

Custom VPC 10.0.0.0/16

Public + private subnets

Internet gateway + route tables

✔ RDS (MySQL)

Private subnet

Only EC2 SG allowed on port 3306

DB name: ticketdb

✔ EC2 (Backend Server)

Amazon Linux 2023

Java installed

Project pulled and built

Connected to RDS

✔ systemd Service (Auto Start)

/etc/systemd/system/ticketapp.service:

[Unit]
Description=Ticket App
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/Ticket-raise-system/target
ExecStart=/usr/bin/java -jar itilproject-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
🧪 API Testing (Swagger)
1. Register

POST /api/users/register

2. Login

POST /api/auth/login

3. Authorize

Click Authorize in Swagger →

Bearer <TOKEN>

4. Raise Ticket

POST /api/tickets/{userId}

📸 Screenshots (Add Your Proof)

Create a folder docs/screenshots/ in the repo and attach:

EC2 running

RDS available

VPC created

Security groups

Systemctl status

Journalctl logs

Swagger UI

These serve as deployment proof for recruiters.

🎓 What This Project Demonstrates

Real AWS deployment

VPC networking knowledge

Linux + systemd

Secure RDS connectivity

Spring Boot backend development

JWT security implementation

Debugging & production readiness

This project is a strong portfolio piece for Cloud, DevOps, and Backend roles.

👨‍💻 Author

Marshal A
Cloud & Backend Developer
GitHub: https://github.com/Marshal-A-Tech
