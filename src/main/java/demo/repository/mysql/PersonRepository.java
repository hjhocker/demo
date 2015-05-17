package demo.repository.mysql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.domain.mysql.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

	public List<Person> findAll();
}
