# Car-Rental-Project


<h1 style="text-align:center;">🚗 <b>Araba Kiralama Platformu</b> 🚗</h1>

<h2>📌 <b>Project Overview</b></h2>
<p>This project is a web-based <b>Car Rental Platform</b> where users can easily browse, filter, and reserve cars. Admins can manage the cars (CRUD operations) via an admin panel, while users can make reservations based on availability.</p>

<hr/>

<h2>✨ <b>Features</b></h2>
<ul>
  <li>User Registration & Login (with Email Verification)</li>
  <li>Role-Based Access Control (Admin & User roles)</li>
  <li>Car Listing & Filtering</li>
  <li>Car Rental & Reservation System</li>
  <li>Admin Panel: Add, Update, Delete Cars</li>
  <li>Email Notifications (Reservation confirmation, reminders)</li>
  <li>Secure Authentication (Spring Security + JWT)</li>
  <li>CRUD Operations</li>
  <li>Mobile-friendly frontend interface</li>
  <li>Dockerized setup for easy deployment</li>
  <li>CI/CD Pipeline for automated testing & deployment</li>
</ul>

<hr/>

<h2>🛠️ <b>Tech Stack</b></h2>

<table>
  <tr>
    <th>Component</th>
    <th>Technologies</th>
  </tr>
  <tr>
    <td>Backend</td>
    <td>Java, Spring Boot, Spring Security, Hibernate</td>
  </tr>
  <tr>
    <td>Frontend</td>
    <td>HTML, CSS, JavaScript</td>
  </tr>
  <tr>
    <td>Database</td>
    <td>PostgreSQL</td>
  </tr>
  <tr>
    <td>Authentication</td>
    <td>JWT + Email Verification</td>
  </tr>
  <tr>
    <td>CI/CD</td>
    <td>GitHub Actions</td>
  </tr>
  <tr>
    <td>Containerization</td>
    <td>Docker</td>
  </tr>
  <tr>
    <td>Testing Tools</td>
    <td>JUnit, Selenium</td>
  </tr>
  <tr>
    <td>Version Control</td>
    <td>Git & GitHub</td>
  </tr>
</table>

<hr/>

<h2>📥 <b>Installation Instructions</b></h2>

<h3>1️⃣ Clone the Repository</h3>
<pre>
git clone https://github.com/ibrahimGDK/Car-Rental-Project.git
cd Car-Rental-Project
</pre>

<h3>2️⃣ Backend Setup</h3>
<pre>
cd backend
./mvnw clean install
./mvnw spring-boot:run
</pre>

<h3>3️⃣ Frontend Setup</h3>
<pre>
cd frontend
# Open HTML file in browser
</pre>

<h3>4️⃣ Docker Usage (Optional)</h3>
<pre>
docker-compose up --build
</pre>

<hr/>

<h2>📡 <b>API Endpoints </b></h2>

<table>
  <tr>
    <th>Endpoint</th>
    <th>Description</th>
    <th>Method</th>
  </tr>
  <tr>
    <td>/api/auth/register</td>
    <td>User Registration</td>
    <td>POST</td>
  </tr>
  <tr>
    <td>/api/auth/login</td>
    <td>User Login</td>
    <td>POST</td>
  </tr>
  <tr>
    <td>/api/cars</td>
    <td>List Cars</td>
    <td>GET</td>
  </tr>
  <tr>
    <td>/api/cars</td>
    <td>Add New Car (Admin Only)</td>
    <td>POST</td>
  </tr>
  <tr>
    <td>/api/reservations</td>
    <td>Make Reservation</td>
    <td>POST</td>
  </tr>
</table>

<hr/>

<h2>👥 <b>Team Members & Responsibilities</b></h2>

<table>
  <tr>
    <th>Name</th>
    <th>Role</th>
  </tr>
  <tr>
    <td>İbrahim</td>
    <td>Backend Developer: Spring Boot, API development, DB integration, Spring Security</td>
  </tr>
  <tr>
    <td>Hakan</td>
    <td>Frontend Developer: HTML, CSS, JavaScript UI development</td>
  </tr>
  <tr>
    <td>Emre</td>
    <td>DevOps & Testing: Docker, CI/CD setup, test automation (JUnit, Selenium)</td>
  </tr>
</table>

<hr/>

<h2>📝 <b>License</b></h2>
<p>This project is developed solely for educational purposes and is not intended for commercial use.</p>

<hr/>

<h2>📨 <b>Contact</b></h2>
<ul>
  <li>İbrahim Ercan Gedik: ibrahimercangedik@ogr.iuc.edu.tr</li>
  <li>Hakan Babur: hakanbabur@ogr.iuc.edu.tr</li>
  <li>Emre Açıl:emreacil@ogr.iuc.edu.tr</li>
</ul>

