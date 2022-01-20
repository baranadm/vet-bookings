package pl.baranowski.dev.validator;


import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NipValidatorTest {

	NipValidator underTest;
	@BeforeEach
	void setUp() throws Exception {
		underTest= new NipValidator();
	}

	@Test
	void isValid_trueForValidNip() {
		assert(underTest.isValid("1181328620", null));
	}

	@Test
	void isValid_falseWhenNull() {
		assertFalse(underTest.isValid(null, null));
	}

	@Test
	void isValid_falseWhenEmpty() {
		assertFalse(underTest.isValid("", null));
	}

	@Test
	void isValid_falseWhenTooShort() {
		assertFalse(underTest.isValid("181328620", null));
	}

	@Test
	void isValid_falseWhenTooLong() {
		assertFalse(underTest.isValid("11181328620", null));
	}
	
	@Test
	void isValid_falseWhenNotDigits() {
		assertFalse(underTest.isValid("1a81328620", null));
	}
	
	@Test
	void isValid_falseWhenControllSumIncorrect() {
		assertFalse(underTest.isValid("1181328621", null));
	}

}
