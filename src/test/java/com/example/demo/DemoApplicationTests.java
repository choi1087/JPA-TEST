package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Address;
import com.example.demo.entity.Person;
import com.example.demo.repository.PersonRepositoryUsingDataJpa;
import com.example.demo.repository.PersonRepositoryUsingJPQL;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(true)
class DemoApplicationTests {

	@Autowired
	PersonRepositoryUsingJPQL personRepositoryUsingJPQL;

	@Autowired
	PersonRepositoryUsingDataJpa personRepositoryUsingDataJpa;

	@BeforeEach
	public void savePersonData() {

		Person person1 = Person.builder()
				.id(1L)
				.name("Kim")
				.age(20)
				.address(Address.builder()
						.city("Seoul")
						.street("강남대로")
						.build())
				.build();

		Person person2 = Person.builder()
				.id(2L)
				.name("Choi")
				.age(30)
				.address(Address.builder()
						.city("Incheon")
						.street("인하로")
						.build())
				.build();

		Person person3 = Person.builder()
				.id(3L)
				.name("Park")
				.age(10)
				.address(Address.builder()
						.city("Bucheon")
						.street("신흥로")
						.build())
				.build();

		personRepositoryUsingJPQL.save(person1);
		personRepositoryUsingJPQL.save(person2);
		personRepositoryUsingJPQL.save(person3);
	}

	@Test
	@Order(1)
	@DisplayName("JPQL repository를 사용하여 Person 데이터 저장")
	public void findOne1() {

		Long id = 1L;
		String name = "Kim";
		int age = 20;
		String city = "Seoul";
		String street = "강남대로";

		Person findPerson = personRepositoryUsingJPQL.findOne(id);

		Assertions.assertThat(findPerson.getName()).isEqualTo(name);
		Assertions.assertThat(findPerson.getAge()).isEqualTo(age);
		Assertions.assertThat(findPerson.getAddress().getCity()).isEqualTo(city);
		Assertions.assertThat(findPerson.getAddress().getStreet()).isEqualTo(street);

		List<Person> personList = personRepositoryUsingJPQL.findAll();
		Person findFirstPerson = personList.get(0);

		Assertions.assertThat(findFirstPerson.getName()).isEqualTo(name);
		Assertions.assertThat(findFirstPerson.getAddress().getCity()).isEqualTo(city);
		Assertions.assertThat(findFirstPerson.getAddress().getStreet()).isEqualTo(street);
	}

	@Test
	@Order(2)
	@DisplayName("Data JPA repository를 사용하여 Person 데이터 저장")
	public void findOne2() {

		Long id = 1L;
		String name = "Kim";
		int age = 20;
		String city = "Seoul";
		String street = "강남대로";

		Person findPerson = personRepositoryUsingDataJpa.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 인원이 존재하지 않습니다"));

		Assertions.assertThat(findPerson.getName()).isEqualTo(name);
		Assertions.assertThat(findPerson.getAge()).isEqualTo(age);
		Assertions.assertThat(findPerson.getAddress().getCity()).isEqualTo(city);
		Assertions.assertThat(findPerson.getAddress().getStreet()).isEqualTo(street);

		List<Person> personList = personRepositoryUsingDataJpa.findAll();
		Person findFirstPerson = personList.get(0);
		Assertions.assertThat(findFirstPerson.getName()).isEqualTo(name);
		Assertions.assertThat(findFirstPerson.getAddress().getCity()).isEqualTo(city);
		Assertions.assertThat(findFirstPerson.getAddress().getStreet()).isEqualTo(street);
	}

	@Test
	@Order(3)
	@DisplayName("JPQL repository를 사용하여 이름으로 회원 찾기")
	public void findOneByName1() {

		String name = "Kim";
		String city = "Seoul";
		String street = "강남대로";

		Person findPerson = personRepositoryUsingJPQL.findByName(name);
		Assertions.assertThat(findPerson.getAddress().getCity()).isEqualTo(city);
		Assertions.assertThat(findPerson.getAddress().getStreet()).isEqualTo(street);
	}

	@Test
	@Order(4)
	@DisplayName("Data JPA repository를 사용하여 이름으로 회원 찾기")
	public void findOneByName2() {

		String name = "Kim";
		String city = "Seoul";
		String street = "강남대로";

		Person findPerson = personRepositoryUsingDataJpa.findByName(name);
		Assertions.assertThat(findPerson.getAddress().getCity()).isEqualTo(city);
		Assertions.assertThat(findPerson.getAddress().getStreet()).isEqualTo(street);
	}

	@Test
	@Order(5)
	@DisplayName("Data JPA repository를 사용하여 city 이름으로 회원 찾기")
	public void findOneByCity() {

		String name = "Kim";
		String city = "Seoul";
		String street = "강남대로";

		Person findPerson = personRepositoryUsingDataJpa.findByAddress_City(city);
		Assertions.assertThat(findPerson.getName()).isEqualTo(name);
		Assertions.assertThat(findPerson.getAddress().getStreet()).isEqualTo(street);
	}

	@Test
	@Order(6)
	@DisplayName("Optioinal 기능")
	public void optionalTest() {
		Optional<Person> findPerson1 = personRepositoryUsingDataJpa.findById(100L);
		Assertions.assertThat(findPerson1).isEmpty();

		Person findPerson2 = personRepositoryUsingDataJpa.findById(1L).orElse(null);
		Assertions.assertThat(findPerson2.getName()).isEqualTo("Kim");

		Person findPerson3 = personRepositoryUsingDataJpa.findById(101L).orElse(null);
		Assertions.assertThat(findPerson3).isNull();

		assertThrows(NullPointerException.class, () -> personRepositoryUsingDataJpa.findById(100L).orElseGet(null));

		assertThrows(IllegalArgumentException.class, () -> personRepositoryUsingDataJpa.findById(100L).orElseThrow(
				() -> new IllegalArgumentException()));
	}

	@Test
	@Order(7)
	@DisplayName("Sort by method name, 나이순 정렬")
	public void sortingTest1() {
		// id: 1L -> age: 20
		// id: 2L -> age: 30
		// id: 3L -> age: 10
		List<Person> findPersonList = personRepositoryUsingDataJpa.findAllByOrderByAgeAsc();
		Assertions.assertThat(findPersonList.get(0).getId()).isEqualTo(3L);
		Assertions.assertThat(findPersonList.get(1).getId()).isEqualTo(1L);
		Assertions.assertThat(findPersonList.get(2).getId()).isEqualTo(2L);
	}

	@Test
	@Order(8)
	@DisplayName("Sort by parameter, 나이순 정렬")
	public void sortingTest2() {
		// id: 1L -> age: 20
		// id: 2L -> age: 30
		// id: 3L -> age: 10
		List<Person> findPersonList = personRepositoryUsingDataJpa.findAll(Sort.by(Sort.Direction.ASC, "age"));
		Assertions.assertThat(findPersonList.get(0).getId()).isEqualTo(3L);
		Assertions.assertThat(findPersonList.get(1).getId()).isEqualTo(1L);
		Assertions.assertThat(findPersonList.get(2).getId()).isEqualTo(2L);
	}

	@Test
	@Order(9)
	@DisplayName("Page 형식의 반환")
	public void pageTest(){

		for(int i=0; i<10; i++){
			Person person = Person.builder()
				.id(100L + i)
				.name("Person" + 10L + i)
				.age(20)
				.address(Address.builder()
						.city("Seoul" + 10L + i)
						.street("강남대로")
						.build())
				.build();
			personRepositoryUsingDataJpa.save(person);
		}

		//위의 @BeforeEach savePersonData() 함수에서 생성된 person 포함, 총 13개
		Integer page = 0;
		Integer size = 5;

		// 페이징처리(페이지번호 0번부터 시작, 페이지당 데이터 5개) 및 정렬(나이 내림차순)
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "age");
		Page<Person> findPerson = personRepositoryUsingDataJpa.findAll(pageRequest);
		Assertions.assertThat(findPerson.getSize()).isEqualTo(5);
		Assertions.assertThat(findPerson.getTotalPages()).isEqualTo(3);
		Assertions.assertThat(findPerson.getTotalElements()).isEqualTo(13);

		Assertions.assertThat(findPerson.getContent().get(0).getAge()).isEqualTo(30);
	}
}
