package pro.sky.bookMenegers.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pro.sky.bookMenegers.exception.*;
import pro.sky.bookMenegers.model.Employee;

import java.util.stream.Stream;

public class EmployeeServiceTest {

  private final EmployeeService employeeService = new EmployeeService(new ValidatorService());

  @BeforeEach
  public void beforeEach() {
    employeeService.add("Иван", "Иванов", 1, 10_000);
    employeeService.add("Пётр", "Петров", 2, 20_000);
    employeeService.add("Сергей", "Сергеев", 3, 30_000);
  }

  @AfterEach
  public void afterEach() {
    employeeService.getAll()
        .forEach(employee -> employeeService.remove(employee.getName(), employee.getSurname()));
  }

  public static Stream<Arguments> addWithIncorrectNameTestParams() {
    return Stream.of(
        Arguments.of("Иван1"),
        Arguments.of("Иван!"),
        Arguments.of("Иван@")
    );
  }

  public static Stream<Arguments> addWithIncorrectSurnameTestParams() {
    return Stream.of(
        Arguments.of("Иванов1"),
        Arguments.of("Иванов!"),
        Arguments.of("Иванов@")
    );
  }

  @Test
  public void addTest() {
    int beforeCount = employeeService.getAll().size();
    Employee expected = new Employee("Ivan", "Ivanov", 1, 10_000);

    Assertions.assertThat(employeeService.add("Ivan", "Ivanov", 1, 10_000))
        .isEqualTo(expected)
        .isIn(employeeService.getAll());
    Assertions.assertThat(employeeService.getAll()).hasSize(beforeCount + 1);
    Assertions.assertThat(employeeService.find("Ivan", "Ivanov")).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("addWithIncorrectNameTestParams")
  public void addWithIncorrectNameTest(String incorrectName) {
    Assertions.assertThatExceptionOfType(IncorrectNameException.class)
        .isThrownBy(() -> employeeService.add(incorrectName, "Ivanov", 1, 10_0000));
  }

  @ParameterizedTest
  @MethodSource("addWithIncorrectSurnameTestParams")
  public void addWithIncorrectSurnameTest(String incorrectSurname) {
    Assertions.assertThatExceptionOfType(IncorrectSurnameException.class)
        .isThrownBy(() -> employeeService.add("Ivan", incorrectSurname, 1, 10_0000));
  }

  @Test
  public void addWhenAlreadyExistsTest() {
    Assertions.assertThatExceptionOfType(EmployeeAlreadyAddedException.class)
        .isThrownBy(() -> employeeService.add("Иван", "Иванов", 1, 10_000));
  }

  @Test
  public void addWhenStorageIsFullTest() {
    Stream.iterate(1, i -> i + 1)
        .limit(7)
        .map(number -> new Employee(
                "Иван" + ((char) ('а' + number)),
                "Иванов" + ((char) ('а' + number)),
                number,
                10_000 + number
            )
        )
        .forEach(employee ->
            employeeService.add(
                employee.getName(),
                employee.getSurname(),
                employee.getDepartment(),
                employee.getSalary()
            )
        );

    Assertions.assertThatExceptionOfType(EmployeeStorageIsFullException.class)
        .isThrownBy(() -> employeeService.add("Вася", "Пупкин", 1, 10_000));
  }

  @Test
  public void removeTest() {
    int beforeCount = employeeService.getAll().size();
    Employee expected = new Employee("Иван", "Иванов", 1, 10_000);

    Assertions.assertThat(employeeService.remove("Иван", "Иванов"))
        .isEqualTo(expected)
        .isNotIn(employeeService.getAll());
    Assertions.assertThat(employeeService.getAll()).hasSize(beforeCount - 1);
    Assertions.assertThatExceptionOfType(EmployeeNotFoundException.class)
        .isThrownBy(() -> employeeService.find("Иван", "Иванов"));
  }

  @Test
  public void removeWhenNotFoundTest() {
    Assertions.assertThatExceptionOfType(EmployeeNotFoundException.class)
        .isThrownBy(() -> employeeService.find("Вася", "Пупкин"));
  }

  @Test
  public void findTest() {
    int beforeCount = employeeService.getAll().size();
    Employee expected = new Employee("Иван", "Иванов", 1, 10_000);

    Assertions.assertThat(employeeService.find("Иван", "Иванов"))
        .isEqualTo(expected)
        .isIn(employeeService.getAll());
    Assertions.assertThat(employeeService.getAll()).hasSize(beforeCount);
  }

  @Test
  public void findWhenNotFoundTest() {
    Assertions.assertThatExceptionOfType(EmployeeNotFoundException.class)
        .isThrownBy(() -> employeeService.find("Вася", "Пупкин"));
  }

  @Test
  public void getAllTest() {
    Assertions.assertThat(employeeService.getAll())
        .hasSize(3)
        .containsExactlyInAnyOrder(
            new Employee("Пётр", "Петров", 2, 20_000),
            new Employee("Сергей", "Сергеев", 3, 30_000),
            new Employee("Иван", "Иванов", 1, 10_000)
        );
  }

}
