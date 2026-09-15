package com.tekpyramid.boot_sample.services;

import com.tekpyramid.boot_sample.dto.EmployeeRequestDto;
import com.tekpyramid.boot_sample.dto.EmployeeRequestV3;
import com.tekpyramid.boot_sample.dto.EmployeeResponseDto;
import com.tekpyramid.boot_sample.dto.EmployeePageResponse;
import com.tekpyramid.boot_sample.dto.ResponseDto;
import com.tekpyramid.boot_sample.entity.Department;
import com.tekpyramid.boot_sample.entity.Employee;
import com.tekpyramid.boot_sample.entity.EmployeeAddress;
import com.tekpyramid.boot_sample.entity.EmployeeDocuments;
import com.tekpyramid.boot_sample.exception.DataNotFoundException;
import com.tekpyramid.boot_sample.exception.DuplicateEmailException;
import com.tekpyramid.boot_sample.exception.TestBusinessException;
import com.tekpyramid.boot_sample.repository.DepartmentRepository;
import com.tekpyramid.boot_sample.repository.EmployeeDocumentsRepository;
import com.tekpyramid.boot_sample.repository.EmployeeRepository;
import com.tekpyramid.boot_sample.util.EmployeeMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final EmployeeDocumentsRepository employeeDocumentsRepository;


    public EmployeeServiceImp(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            EmployeeDocumentsRepository employeeDocumentsRepository) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeDocumentsRepository = employeeDocumentsRepository;
    }


    // =========================================================
    // 1. SAVE EMPLOYEE
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 10
    )
    public EmployeeResponseDto saveEmployee(
            EmployeeRequestDto dto) {

        log.info(
                "Save employee request started. email={}",
                dto.getEmail()
        );


        // Check duplicate email
        if (employeeRepository.existsByEmail(dto.getEmail())) {

            log.warn(
                    "Duplicate employee email found. email={}",
                    dto.getEmail()
            );

            throw new DuplicateEmailException(
                    "Email Id Already Present"
            );
        }


        log.info(
                "Converting employee DTO to entity. email={}",
                dto.getEmail()
        );

        Employee employee =
                EmployeeMapper.dtoToEntity(
                        dto,
                        departmentRepository
                );


        log.info(
                "Saving employee to database. email={}",
                dto.getEmail()
        );

        Employee savedEmployee =
                employeeRepository.save(employee);


        log.info(
                "Employee saved successfully. employeeId={}",
                savedEmployee.getId()
        );


        return EmployeeMapper.entitytoDto(
                savedEmployee
        );
    }


    // =========================================================
    // 2. GET EMPLOYEE BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "employees",
            key = "#id"
    )
    public EmployeeResponseDto getEmployee(int id) {

        log.info(
                "Fetching employee. employeeId={}",
                id
        );


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Employee not found. employeeId={}",
                                    id
                            );

                            return new DataNotFoundException(
                                    "Employee Not Found"
                            );
                        });


        log.info(
                "Employee fetched successfully. employeeId={}",
                id
        );


        return EmployeeMapper.entitytoDto(
                employee
        );
    }


    // =========================================================
    // 3. GET ALL EMPLOYEES - V1
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployeeV1() {

        log.info(
                "Fetching all employees - V1"
        );


        List<Employee> employees =
                employeeRepository.findAll();


        log.info(
                "Employees fetched successfully. count={}",
                employees.size()
        );


        return employees
                .stream()
                .map(EmployeeMapper::entitytoDto)
                .toList();
    }


    // =========================================================
    // 4. GET ALL EMPLOYEES - V2
    // SEARCH + PAGINATION
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public EmployeePageResponse getAllEmployee(
            String search,
            Pageable pageable) {

        log.info(
                "Fetching employees - V2. search={}, page={}, size={}",
                search,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );


        Page<Employee> employeePage;


        if (search == null || search.isBlank()) {

            log.info(
                    "No search value provided. Fetching all employees with pagination."
            );

            employeePage =
                    employeeRepository.findAll(
                            pageable
                    );

        } else {

            log.info(
                    "Searching employees. search={}",
                    search.trim()
            );

            employeePage =
                    employeeRepository
                            .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                    search.trim(),
                                    search.trim(),
                                    pageable
                            );
        }


        log.info(
                "Employee search completed. totalElements={}, totalPages={}",
                employeePage.getTotalElements(),
                employeePage.getTotalPages()
        );


        List<EmployeeResponseDto> content =
                employeePage
                        .getContent()
                        .stream()
                        .map(EmployeeMapper::entitytoDto)
                        .toList();


        EmployeePageResponse response =
                new EmployeePageResponse();


        response.setContent(content);

        response.setPage(
                employeePage.getNumber()
        );

        response.setSize(
                employeePage.getSize()
        );

        response.setTotalElements(
                employeePage.getTotalElements()
        );

        response.setTotalPages(
                employeePage.getTotalPages()
        );


        return response;
    }


    // =========================================================
    // 5. UPDATE EMPLOYEE - PUT
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 10
    )
    @CacheEvict(
            value = "employees",
            key = "#id"
    )
    public EmployeeResponseDto updateEmployee(
            int id,
            EmployeeRequestDto dto) {

        log.info(
                "Update employee request started. employeeId={}",
                id
        );


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Employee not found for update. employeeId={}",
                                    id
                            );

                            return new DataNotFoundException(
                                    "Employee Not Found"
                            );
                        });


        if (employeeRepository.existsByEmailAndIdNot(
                dto.getEmail(),
                id)) {

            log.warn(
                    "Duplicate email during employee update. employeeId={}, email={}",
                    id,
                    dto.getEmail()
            );

            throw new DuplicateEmailException(
                    "Email Id Already Present"
            );
        }


        log.info(
                "Updating employee fields. employeeId={}",
                id
        );


        employee.setName(
                dto.getName()
        );

        employee.setEmail(
                dto.getEmail()
        );

        employee.setAge(
                dto.getAge()
        );


        EmployeeMapper.updateAddress(
                employee,
                dto
        );


        EmployeeMapper.updateDepartment(
                employee,
                dto,
                departmentRepository
        );


        log.info(
                "Saving updated employee. employeeId={}",
                id
        );


        Employee updatedEmployee =
                employeeRepository.save(employee);


        log.info(
                "Employee updated successfully. employeeId={}",
                updatedEmployee.getId()
        );


        return EmployeeMapper.entitytoDto(
                updatedEmployee
        );
    }


    // =========================================================
    // 6. PATCH EMPLOYEE
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 10
    )
    @CacheEvict(
            value = "employees",
            key = "#id"
    )
    public EmployeeResponseDto patchEmployee(
            int id,
            EmployeeRequestDto dto) {

        log.info(
                "Patch employee request started. employeeId={}",
                id
        );


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Employee not found for patch. employeeId={}",
                                    id
                            );

                            return new DataNotFoundException(
                                    "Employee Not Found"
                            );
                        });


        if (dto.getName() != null) {

            log.info(
                    "Updating employee name. employeeId={}",
                    id
            );

            employee.setName(
                    dto.getName()
            );
        }


        if (dto.getEmail() != null) {

            log.info(
                    "Updating employee email. employeeId={}, email={}",
                    id,
                    dto.getEmail()
            );


            if (employeeRepository
                    .existsByEmailAndIdNot(
                            dto.getEmail(),
                            id)) {

                log.warn(
                        "Duplicate email during patch. employeeId={}, email={}",
                        id,
                        dto.getEmail()
                );

                throw new DuplicateEmailException(
                        "Email Id Already Present"
                );
            }


            employee.setEmail(
                    dto.getEmail()
            );
        }


        if (dto.getAge() != null) {

            log.info(
                    "Updating employee age. employeeId={}",
                    id
            );

            employee.setAge(
                    dto.getAge()
            );
        }


        if (dto.getAddress() != null) {

            log.info(
                    "Updating employee address. employeeId={}",
                    id
            );

            EmployeeMapper.updateAddress(
                    employee,
                    dto
            );
        }


        if (dto.getDepartment() != null) {

            log.info(
                    "Updating employee department. employeeId={}",
                    id
            );

            EmployeeMapper.updateDepartment(
                    employee,
                    dto,
                    departmentRepository
            );
        }


        log.info(
                "Saving patched employee. employeeId={}",
                id
        );


        Employee updatedEmployee =
                employeeRepository.save(employee);


        log.info(
                "Employee patched successfully. employeeId={}",
                updatedEmployee.getId()
        );


        return EmployeeMapper.entitytoDto(
                updatedEmployee
        );
    }


    // =========================================================
    // 7. DELETE EMPLOYEE
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 10
    )
    @CacheEvict(
            value = "employees",
            key = "#id"
    )
    public void deleteEmployee(int id) {

        log.info(
                "Delete employee request started. employeeId={}",
                id
        );


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Employee not found for delete. employeeId={}",
                                    id
                            );

                            return new DataNotFoundException(
                                    "Employee Not Found"
                            );
                        });


        employeeRepository.delete(employee);


        log.info(
                "Employee deleted successfully. employeeId={}",
                id
        );
    }


    // =========================================================
    // 8. ASSIGN DEPARTMENT
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 10
    )
    @CacheEvict(
            value = "employees",
            key = "#employeeId"
    )
    public EmployeeResponseDto assignDepartment(
            Integer employeeId,
            Integer departmentId) {

        log.info(
                "Assign department request started. employeeId={}, departmentId={}",
                employeeId,
                departmentId
        );


        Employee employee =
                employeeRepository.findById(
                        employeeId
                ).orElseThrow(() -> {

                    log.warn(
                            "Employee not found. employeeId={}",
                            employeeId
                    );

                    return new DataNotFoundException(
                            "Employee Not Found"
                    );
                });


        Department department =
                departmentRepository.findById(
                        departmentId
                ).orElseThrow(() -> {

                    log.warn(
                            "Department not found. departmentId={}",
                            departmentId
                    );

                    return new DataNotFoundException(
                            "Department Not Found"
                    );
                });


        log.info(
                "Assigning department to employee. employeeId={}, departmentId={}",
                employeeId,
                departmentId
        );


        employee.setDepartment(
                department
        );


        Employee updatedEmployee =
                employeeRepository.save(employee);


        log.info(
                "Department assigned successfully. employeeId={}, departmentId={}",
                employeeId,
                departmentId
        );


        return EmployeeMapper.entitytoDto(
                updatedEmployee
        );
    }


    // =========================================================
    // 9. SAVE EMPLOYEE DETAILS
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 10
    )
    public EmployeeResponseDto saveEmployeeDetails(
            EmployeeRequestDto dto) {

        log.info(
                "Save employee details request started. email={}",
                dto.getEmail()
        );


        EmployeeResponseDto response =
                saveEmployee(dto);


        log.info(
                "Employee details saved successfully. email={}",
                dto.getEmail()
        );


        return response;
    }


    // =========================================================
    // 10. CREATE EMPLOYEE V3
    // EMPLOYEE + ADDRESS + DEPARTMENT + FILE
    // =========================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            timeout = 30
    )
    public ResponseDto createEmployeeV3(
            EmployeeRequestV3 dto,
            MultipartFile file) {

        log.info(
                "Create Employee V3 request started. email={}, department={}",
                dto.getEmail(),
                dto.getDepartmentName()
        );


        // -----------------------------------------------------
        // 1. Create Employee
        // -----------------------------------------------------

        log.info(
                "Creating employee object. name={}, email={}, age={}",
                dto.getName(),
                dto.getEmail(),
                dto.getAge()
        );


        Employee employee =
                new Employee();


        employee.setName(
                dto.getName()
        );

        employee.setEmail(
                dto.getEmail()
        );

        employee.setAge(
                dto.getAge()
        );


        // -----------------------------------------------------
        // 2. Create Address
        // -----------------------------------------------------

        log.info(
                "Creating employee address. city={}, state={}, country={}",
                dto.getCity(),
                dto.getState(),
                dto.getCountry()
        );


        EmployeeAddress address =
                new EmployeeAddress();


        address.setCity(
                dto.getCity()
        );

        address.setState(
                dto.getState()
        );

        address.setCountry(
                dto.getCountry()
        );


        employee.setAddresses(
                address
        );


        // -----------------------------------------------------
        // 3. Find Department
        // -----------------------------------------------------

        log.info(
                "Searching department. departmentName={}",
                dto.getDepartmentName()
        );


        Department department =
                departmentRepository
                        .findByDepartmentName(
                                dto.getDepartmentName()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Department not found. departmentName={}",
                                    dto.getDepartmentName()
                            );

                            return new DataNotFoundException(
                                    "Department not found: "
                                            + dto.getDepartmentName()
                            );
                        });


        log.info(
                "Department found successfully. departmentId={}, departmentName={}",
                department.getId(),
                department.getDepartmentName()
        );


        employee.setDepartment(
                department
        );


        // -----------------------------------------------------
        // 4. Check Duplicate Email
        // -----------------------------------------------------

        log.info(
                "Checking duplicate email. email={}",
                dto.getEmail()
        );


        if (employeeRepository
                .existsByEmail(dto.getEmail())) {

            log.warn(
                    "Duplicate employee email found. email={}",
                    dto.getEmail()
            );

            throw new DuplicateEmailException(
                    "Email Id Already Present"
            );
        }


        // -----------------------------------------------------
        // 5. Save Employee
        // -----------------------------------------------------

        log.info(
                "Saving employee to database. email={}",
                dto.getEmail()
        );


        Employee savedEmployee =
                employeeRepository.save(
                        employee
                );


        log.info(
                "Employee saved successfully. employeeId={}",
                savedEmployee.getId()
        );


        // -----------------------------------------------------
        // 6. Save Uploaded File
        // -----------------------------------------------------

        if (file != null && !file.isEmpty()) {

            log.info(
                    "File upload detected. employeeId={}, fileName={}, contentType={}, size={}",
                    savedEmployee.getId(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize()
            );


            try {

                byte[] fileData =
                        file.getBytes();


                log.info(
                        "File converted to byte array. employeeId={}, bytes={}",
                        savedEmployee.getId(),
                        fileData.length
                );


                EmployeeDocuments document =
                        new EmployeeDocuments();


                document.setDocumentName(
                        file.getOriginalFilename()
                );


                document.setDocumentType(
                        file.getContentType()
                );


                document.setFileName(
                        file.getOriginalFilename()
                );


                document.setFileData(
                        fileData
                );


                document.setEmployee(
                        savedEmployee
                );


                employeeDocumentsRepository.save(
                        document
                );


                log.info(
                        "Employee document saved successfully. employeeId={}, fileName={}",
                        savedEmployee.getId(),
                        file.getOriginalFilename()
                );


            } catch (IOException e) {

                log.error(
                        "Failed to read uploaded file. employeeId={}, fileName={}",
                        savedEmployee.getId(),
                        file.getOriginalFilename(),
                        e
                );


                throw new RuntimeException(
                        "Failed to read uploaded file",
                        e
                );
            }

        } else {

            log.info(
                    "No file uploaded. employeeId={}",
                    savedEmployee.getId()
            );
        }


        // -----------------------------------------------------
        // 7. Return Response
        // -----------------------------------------------------

        log.info(
                "Create Employee V3 completed successfully. employeeId={}",
                savedEmployee.getId()
        );


        return ResponseDto.builder()
                .error(false)
                .message(
                        "Employee created successfully"
                )
                .data(savedEmployee)
                .build();
    }


    // =========================================================
    // 11. TEST NO ROLLBACK
    // =========================================================

    @Transactional(
            noRollbackFor = TestBusinessException.class
    )
    public void testNoRollback() {

        log.info(
                "No rollback test started."
        );


        Employee employee =
                new Employee();


        employee.setName(
                "No Rollback Test"
        );

        employee.setEmail(
                "norollback@test.com"
        );

        employee.setAge(
                25
        );


        employeeRepository.save(employee);


        log.info(
                "Employee saved for no rollback test. employeeId={}",
                employee.getId()
        );


        log.warn(
                "Throwing TestBusinessException intentionally to test noRollbackFor."
        );


        throw new TestBusinessException(
                "Testing noRollbackFor"
        );
    }
}
