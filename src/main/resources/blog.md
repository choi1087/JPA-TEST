# Spring Data JPA 사용
## Spring Data JPA를 사용하는 이유
- JPQL을 직접 작성하지 않고 객체를 통해 동작하기 때문에 유지보수, 재사용성 측면에서 장점
- CRUD 처리를 위한 공통 인터페이스 제공


## JPQL vs Spring Data JPA
### JPA 및 데이터베이스 h2 설정
- application.yml
  
        spring:
            datasource:
                driver-class-name: org.h2.Driver
                url: jdbc:h2:tcp://localhost/~/test
                username: sa
            h2:
                console:
                enabled: true
                path: /h2-console
                settings:
                    web-allow-others: true
                    trace: false
            
            jpa:
                database: H2
                database-platform: org.hibernate.dialect.H2Dialect
                show-sql: true
                hibernate:
                ddl-auto: create
                properties:
                hibernate:
                    dialect: org.hibernate.dialect.H2Dialect
                defer-datasource-initialization: true

- pom.xml
  
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-jpa</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>

            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
                <version>2.1.210</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>

### 비교를 위해 필요한 사전 데이터
- PersonEntity
  
        @NoArgsConstructor
        public class Person {

            @Id            
            private Long id;            
            private String name;
            private int age;

            @Embedded
            private Address address;

            @Builder
            public Person(Long id, String name, Address address, int age){
                this.id = id;
                this.name = name;
                this.age = age;
                this.address = address;
            }
        }

        public class Address {
            private String city;
            private String street;

            @Builder
            public Address(String city, String street){
                this.city = city;
                this.street = street;
            }
        }
- JPQL 사용하여 repository를 구현한 코드

        @Repository
        public class PersonRepositoryUsingJPQL {

            private final EntityManager em;

            PersonRepositoryUsingJPQL(EntityManager em) {
                this.em = em;
            }

            public void flush() {
                em.flush();
            }

            public void save(Person person) {
                em.persist(person);
            }

            public Person findOne(Long id) {
                return em.find(Person.class, id);
            }

            public List<Person> findAll() {
                return em.createQuery("select p from Person p", Person.class)
                        .getResultList();
            }            
        }
- Type check 불가능, 컴파일 시점에 에러를 확인할 수 없음.
  
        public Person findByName(String name) {
            return (Person) em.createQuery("select p from Person p where p.name=:namee")
                    .setParameter("name", name)
                    .getSingleResult();
        }
- 위의 p.name=:namee 와 같은 오타는 컴파일 시점에서 확인할 수 없고, 해당 로직이 실행된 후에 오류를 발견할 수 있음.

- Spring Data JPA를 통해 repository를 구현한 코드
  
        public interface PersonRepositoryUsingDataJpa extends JpaRepository<Person, Long>{
                   
        }
- Spring Data JPA가 제공해주는 공통 메소드가 존재하기 때문에 비교적 코드양이 줄고 구현, 수정 등의 면에서 용이
- Spring Data JPA가 제공하는 공통 메소드 관련 링크: https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html


## 쿼리 메소드 사용
- 공통 메소드가 제공해주지 않는 부분에 대해 keyword와 메소드 이름을 통해 쿼리 생성
- find"OOO"By"OOO"
- get"OOO"By"OOO"
- count"OOO"By"OOO"
- 쿼리 메소드 관련 링크: https://docs.spring.io/spring-data/jpa/docs/1.8.0.RELEASE/reference/html/#jpa.query-methods.query-creation
- 쿼리 메소드 예시 코드
  
        public interface PersonRepositoryUsingDataJpa extends JpaRepository<Person, Long>{
            Person findByName(String name);

            //Person findByAddressCity(String city);
            Person findByAddress_City(String city);
        }

- Address의 city를 통해 entity 정보를 받아오는 쿼리 메소드를 만들 때, findByAddressCity가 아닌 _로 구분하여 findByAddress_City 와 같은 방식으로 쿼리 메소드를 만듦.

## Return Type
1. Optional
   - NPE(NullPointerException)을 방지할 수 있는 타입. 
   - Optional<'T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스, null을 참조하더라도 NPE가 발생하지 않도록 함.
   - Optional 활용
     - optional.empty(): 빈 객체 확인
     - optional.orElse(): optional 안의 값이 null이든 아니든 항상 호출
     - optional.orElseGet(): optional 안의 값이 null일 경우에만 호출
     - optional.orElseThrow(): optional 안의 값이 null일 경우, 예외 처리

2. Sort
   - Spring Data JPA에서 정렬된 형태로 데이터를 받아오는 방법 2가지 존재
   - 메소드에 OrderBy를 붙여줌, 메소드명으로 정렬기준을 만들 수 있는 장점이 있지만, 다수의 정렬 기준을 만들기는 어려움이 있음
  
            List<Person> findAllByOrderByAgeAsc();

    - Sort 클래스를 인자로 받음, 다수의 정렬기준이 필요한 경우 활용하기 좋은 방법

            List<Person> findAll(Sort sort);

3. Page
   - Pageable를 구현한 PageRequest 객체를 쿼리 메소드의 인자로 받아 사용 가능한 데이터의 총 개수 및 전체 페이지를 설정할 수 있음
   - count 쿼리를 실행함으로써 전체 페이지, 데이터 개수 확인 가능
   - PageRequest 객체를 통해 정렬 기준을 설정할 수 있지만, 단순 정렬 기능만 구현할 경우, Sort를 통해 정렬하는 것이 좋음
   - PageRequest.of(int page, int size) -> 페이지 번호(0부터 시작), 페이지당 데이터의 수
   - PageReuqest.of(int page, int size, Sort sort) -> 페이지 번호, 페이지당 데이터의 수, 정렬 방향(오름, 내림차순)
   - PageRequest.of(int page, int size, Sort.Direction, String ....) -> 페이지 번호, 페이지당 데이터의 수, 정렬 방향(오름, 내림차순), 정렬 기준 컬럼


## 전체 테스트 코드
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
