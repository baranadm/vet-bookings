package pl.baranowski.dev.repository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.baranowski.dev.entity.AnimalType;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class AnimalTypeRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    AnimalTypeRepository underTest;

    @Test
    void findOneByName_shouldFindAnimalType() {
        //given
        entityManager.persist(new AnimalType("Kot"));
        //when
        boolean isPresent = underTest.findOneByName("Kot").isPresent();
        assert isPresent;
    }

    //should store

    //

    @Test
    void findByName() {
    }
}