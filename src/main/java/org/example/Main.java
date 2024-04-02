package org.example;

import com.google.gson.*;
import org.example.model.Employee;
import org.example.model.PensionPlan;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.Comparator;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = initializeData();
        Main main = new Main();
        main.printEmployeesData(employees);
        main.printMonthlyUpcomingEnrollees(employees);
    }

    public static List<Employee> initializeData(){
        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(1, "Daniel", "Agar", LocalDate.of(2018, 1, 17), 105945.50));
        employees.get(0).setPensionPlan(new PensionPlan("EX1089", LocalDate.of(2023, 1, 17), 100.00));

        employees.add(new Employee(2, "Benard", "Shaw", LocalDate.of(2019, 4, 3), 197750.00));

        employees.add(new Employee(3, "Carly", "Agar", LocalDate.of(2014, 5, 16), 842000.75));
        employees.get(2).setPensionPlan(new PensionPlan("SM2307", LocalDate.of(2019, 11, 4), 1555.50));

        employees.add(new Employee(4, "Wesley", "Schneider", LocalDate.of(2019, 5, 2), 74500.00));

        return employees;
    }

    private void printEmployeesData(List<Employee> employees) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        List<Employee> sortedEmployees = employees.stream()
                .sorted(Comparator.comparing(Employee::getLastName).thenComparing(Employee::getYearlySalary).reversed())
                .collect(Collectors.toList());


        System.out.println("List of all Employees with Pension Plan data: ");
        System.out.println(gson.toJson(sortedEmployees));
        System.out.println();
    }

    private void printMonthlyUpcomingEnrollees(List<Employee> employees) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfNextMonth = currentDate.plusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfNextMonth = firstDayOfNextMonth.withDayOfMonth(firstDayOfNextMonth.lengthOfMonth());


        List<Employee> upcomingEnrollees = employees.stream()
                .filter(emp -> emp.getPensionPlan() == null && emp.getEmploymentDate().plusYears(5).isBefore(lastDayOfNextMonth.plusDays(1)))
                .sorted(Comparator.comparing(Employee::getEmploymentDate))
                .collect(Collectors.toList());

        System.out.println("Monthly Upcoming Enrollees report: ");
        System.out.println(gson.toJson(upcomingEnrollees));
    }


    public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}

