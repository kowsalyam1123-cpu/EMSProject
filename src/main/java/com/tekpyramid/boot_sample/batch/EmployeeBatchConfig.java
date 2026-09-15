package com.tekpyramid.boot_sample.batch;

import com.tekpyramid.boot_sample.dto.EmployeeCsvDto;
import com.tekpyramid.boot_sample.entity.Employee;
import com.tekpyramid.boot_sample.repository.EmployeeRepository;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeBatchConfig {

    // 1. READER
    @Bean
    public FlatFileItemReader<EmployeeCsvDto> employeeReader() {

        return new FlatFileItemReaderBuilder<EmployeeCsvDto>()
                .name("employeeReader")
                .resource(new ClassPathResource("employees.csv"))
                .linesToSkip(1)
                .delimited()
                .names("name", "email", "age")
                .targetType(EmployeeCsvDto.class)
                .build();
    }


    // 2. PROCESSOR
    @Bean
    public ItemProcessor<EmployeeCsvDto, Employee> employeeProcessor(
            EmployeeRepository employeeRepository) {

        return dto -> {

            // Check whether email already exists
            if (employeeRepository.existsByEmail(dto.getEmail())) {

                System.out.println(
                        "Skipping duplicate email: " + dto.getEmail()
                );

                return null;
            }

            Employee employee = new Employee();

            employee.setName(dto.getName());
            employee.setEmail(dto.getEmail());
            employee.setAge(dto.getAge());

            return employee;
        };
    }


    // 3. WRITER
    @Bean
    public ItemWriter<Employee> employeeWriter(
            EmployeeRepository employeeRepository) {

        return chunk -> {

            employeeRepository.saveAll(
                    chunk.getItems()
            );
        };
    }


    // 4. STEP
    @Bean
    public Step employeeStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<EmployeeCsvDto> employeeReader,
            ItemProcessor<EmployeeCsvDto, Employee> employeeProcessor,
            ItemWriter<Employee> employeeWriter) {

        return new StepBuilder(
                "employeeStep",
                jobRepository
        )
                .<EmployeeCsvDto, Employee>chunk(10)
                .transactionManager(transactionManager)
                .reader(employeeReader)
                .processor(employeeProcessor)
                .writer(employeeWriter)
                .build();
    }


    // 5. JOB
    @Bean
    public Job employeeJob(
            JobRepository jobRepository,
            Step employeeStep) {

        return new JobBuilder(
                "employeeJob",
                jobRepository
        )
                .start(employeeStep)
                .build();
    }
}