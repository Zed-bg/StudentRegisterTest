package com.example.studentregistertest.controller;

import com.example.studentregistertest.entity.Student;
import com.example.studentregistertest.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String index(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("newStudent", new Student());
        return "index";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute("newStudent") Student student) {
        studentService.addStudent(student);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editOrAddStudent(@PathVariable Long id, Model model) {
        if (id != null) {
            // Editing existing student
            Student student = studentService.getStudentById(id);
            if (student != null) {
                List<Student> students = studentService.getAllStudents(); // Add this line
                model.addAttribute("students", students); // Add this line
                model.addAttribute("student", student);
            }
        }
        return "index";
    }





    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") Student student) {
        student.setId(id);
        studentService.updateStudent(student);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/";
    }

//    start
@GetMapping("/print/{id}")
public void printStudent(@PathVariable Long id, HttpServletResponse response) throws Exception {
    // Fetch the student
    Student student = studentService.getStudentById(id);

    // Compile JRXML file
    ClassPathResource resource = new ClassPathResource("/studentreport.jrxml");
    Path jrxmlFilePath = Paths.get(resource.getURI());
    JasperCompileManager.compileReportToFile(jrxmlFilePath.toString());

    // Set up parameters
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("studentId", id); // Pass any parameters your report needs

    // Fetch data source (you can use JRDataSource or JRBeanCollectionDataSource)
    // For simplicity, let's assume you're using JRBeanCollectionDataSource
    List<Student> students = List.of(student); // Java 9+ feature
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);

    // Fill the report
    InputStream compiledReportStream = Files.newInputStream(jrxmlFilePath.getParent().resolve("studentreport.jasper"));
    JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReportStream, parameters, dataSource);

    // Set the filename with the student's name
    String filename = StringUtils.isEmpty(student.getName()) ? "student_report.pdf" : student.getName() + "_report.pdf";
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=" + filename);

    // Export the report to PDF and write it to the response output stream
    JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
}
//    end


}
