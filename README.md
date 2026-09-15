# Employee Management System (Spring Boot REST API)

A Spring Boot REST API for managing employees, departments, and leaves, with Spring Batch CSV import, Redis caching, and file upload support.

## Tech Stack

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA (Hibernate)
- Spring Batch
- Spring Validation
- Spring Boot Actuator
- Springdoc OpenAPI (Swagger UI)
- Redis (caching)
- MySQL
- Lombok

## Project Structure

```text
com.SpringBoot.demo
├── batch          -> Spring Batch config, DTO, processor, writer, listener (CSV employee import)
├── controller     -> REST controllers (Employee, Employee V2, Department, Leave, LeaveBalance, Batch)
├── dto            -> Request/response objects
├── entity         -> JPA entities (Employee, Department, Address, Leave, LeaveBalance, EmployeeDocument, LeaveType, BaseEntity)
├── exception      -> Custom exceptions + GlobalExceptionHandler
├── filter         -> LoggingFilter (logs every request/response)
├── repository     -> Spring Data JPA repositories
├── service        -> Service interfaces and implementations
└── util           -> Entity <-> DTO mappers
```

## Entities

- **Employee** - name, email, phoneNumber, password, leave balances (sick/casual/unpaid). Has a `OneToOne` Address, a `ManyToOne` Department, a `OneToMany` list of Leaves, and a `OneToOne` EmployeeDocument.
- **Address** - street, city, state, pincode.
- **Department** - deptId, deptName.
- **Leave** - fromDate, toDate, leaveType, reason, status. `ManyToOne` to Employee.
- **LeaveBalance** - totalLeaves, usedLeaves, remainingLeaves. `OneToOne` to Employee.
- **EmployeeDocument** - stores an uploaded file's name, stored file name, type, size, and path. `OneToOne` to Employee.
- **BaseEntity** - common createdBy/createdAt/updatedBy/updatedAt audit fields, extended by all entities above.

## API Endpoints

### Employee (v1)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/employee` | Add a new employee |
| GET | `/employee/{id}` | Get an employee by id |
| GET | `/employees` | Get all employees |
| PUT | `/employee/{id}` | Update an employee |
| DELETE | `/employee/{id}` | Delete an employee |

### Employee (v2) - base path `/v2`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/v2/employee` | Add employee (v2 request format) |
| POST | `/v2/addv2employee` | Add employee with a profile image (multipart) |
| GET | `/v2/EmployeeDetails` | Get paginated employee details |
| GET | `/v2/allEmployees/optimized` | Get all employees (optimized query) |
| GET | `/v2/allEmployees/withLeaves` | Get all employees along with their leaves |

### Department
| Method | Endpoint | Description |
|---|---|---|
| PUT | `/employee/{id}/department/{deptId}` | Assign a department to an employee |

### Leave - base path `/api/v1/leave`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/leave/employee/{employeeId}` | Apply for leave |
| GET | `/api/v1/leave/{leaveId}` | Get a leave by id |
| GET | `/api/v1/leave/allLeaves` | Get all leaves |
| GET | `/api/v1/leave/employee/{employeeId}/leaves` | Get all leaves for an employee |
| PUT | `/api/v1/leave/{leaveId}/status` | Approve/reject a leave |
| DELETE | `/api/v1/leave/cancelLeave` | Cancel a pending leave |

### Leave Balance - base path `/api/leaveBalance`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/leaveBalance/add/{employeeId}` | Create a leave balance for an employee |

### Batch (CSV employee import) - base path `/api/batch/employees`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/batch/employees/upload` | Upload a CSV file to bulk-create employees |
| GET | `/api/batch/employees/status/{jobExecutionId}` | Check the status of a batch job |

## Spring Batch - CSV Employee Import

CSV upload header must be:

```text
name,email,phoneNumber,password,street,city,state,pincode,deptId
```

`deptId` must refer to an existing department.

**Upload:**
```text
POST /api/batch/employees/upload
```
Sent as multipart form-data with key `file`. The response contains a `jobExecutionId`.

**Check status:**
```text
GET /api/batch/employees/status/{jobExecutionId}
```

**Batch flow:**
```text
CSV file
   |
   v
FlatFileItemReader
   |
   v
EmployeeItemProcessor
   |
   v
EmployeeItemWriter
   |
   v
MySQL employees + address data
```

Chunk size is 50 records. Spring Batch reads/processes records and writes them as chunks inside transaction boundaries.

Spring Batch metadata tables are initialized automatically:
```properties
spring.batch.job.enabled=false
spring.batch.jdbc.initialize-schema=always
```
The job does not run automatically at application startup; it only starts when the upload endpoint is called.

**Spring Batch 6 import note:** item APIs moved to `org.springframework.batch.infrastructure.item...`. Examples used by this project:
```java
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
```

## Exception Handling

`GlobalExceptionHandler` maps these custom exceptions to HTTP responses:

| Exception | Status |
|---|---|
| EmailAndPhoneDuplicateException | 409 Conflict |
| DuplicateEmailException | 409 Conflict |
| DuplicatePhoneException | 409 Conflict |
| EmployeeNotFoundException | 409 Conflict |
| IdNotFoundException | 404 Not Found |
| InvalidLeaveDateException | 400 Bad Request |
| LeaveNotFoundException | 400 Bad Request |
| LeaveBalanceExistException | 400 Bad Request |
| LeaveApprovedException | 400 Bad Request |
| FailedToAddEmployeeException | 400 Bad Request |

## Logging

`LoggingFilter` logs every incoming request and its response (method, URI, client IP, status, time taken).

## Configuration (`application.properties`)

- MySQL database: `spring_rest_api`
- `spring.jpa.hibernate.ddl-auto=update`
- Multipart file size limit: 5MB
- Redis host/port for caching
- Spring Batch metadata schema auto-initialized; batch jobs only run when the upload API is called
- Actuator endpoints exposed: `health`, `info`, `metrics`

## Running the Project

1. Create a MySQL database named `spring_rest_api`.
2. Update `spring.datasource.username` / `spring.datasource.password` in `application.properties` if needed.
3. Make sure Redis is running on `localhost:6379`.
4. Run `DemoApplication.java`.
5. The app starts on port `8080`.
6. Swagger UI (from springdoc-openapi) is available once the app is running.
7. Actuator health check: `GET /actuator/health`.

## IntelliJ Maven Refresh

If IntelliJ shows red/unresolved imports after opening the project:

1. Open the Maven tool window.
2. Click **Reload All Maven Projects**.
3. Wait until Maven finishes downloading dependencies.
4. Run `clean` and `compile` from Maven if required.
5. If it still shows red imports, use **File -> Invalidate Caches -> Invalidate and Restart**.
