package net.javaguides.ems.service;

import lombok.AllArgsConstructor;
import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.mapper.EmployeeMapper;
import net.javaguides.ems.repository.DepartmentRepository;
import net.javaguides.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service //tells the spring container to create spring bean for below class
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private EmployeeRepository employeeRepository; //injected dependency

    private DepartmentRepository departmentRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        //converting the dto to entity
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        //Setting department
        Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department does not exist with id: "+ employeeDto.getDepartmentId()));
        employee.setDepartment(department);
        // now saving the entity to DB using repository
        Employee savedEmployee = employeeRepository.save(employee);
        // now sending savedEmployee back to the client by converting it into dto
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee with given id :" + employeeId + ", does not exist"));
        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();


        return employees.stream().map(EmployeeMapper::mapToEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployeeDto) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found with id: "+ employeeId)
        );

        employee.setFirstName(updatedEmployeeDto.getFirstName());
        employee.setLastName(updatedEmployeeDto.getLastName());
        employee.setEmail(updatedEmployeeDto.getEmail());

        Department department = departmentRepository.findById(updatedEmployeeDto.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department does not exist with id: "+ updatedEmployeeDto.getDepartmentId()));
        employee.setDepartment(department);

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found with id: "+ employeeId)
        );

        employeeRepository.deleteById(employeeId);

    }


}
