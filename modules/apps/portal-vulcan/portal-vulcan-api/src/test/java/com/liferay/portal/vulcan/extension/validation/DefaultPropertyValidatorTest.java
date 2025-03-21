/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.extension.validation;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carlos Correa
 */
public class DefaultPropertyValidatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidateBigDecimal() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.BIG_DECIMAL,
			defaultPropertyValidator, RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, new BigDecimal(RandomTestUtil.randomLong()));
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomDouble());
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomFloat());
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomInt());
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomLong());
	}

	@Test
	public void testValidateBoolean() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.BOOLEAN, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testValidateDateTime() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			Collections.singleton(String.class), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.DATE_TIME, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, _dateFormat.format(RandomTestUtil.nextDate()));
	}

	@Test
	public void testValidateDecimal() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.DECIMAL, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomFloat());
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomInt());
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomLong());
	}

	@Test
	public void testValidateDouble() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.DOUBLE, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomDouble());
		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomFloat());
	}

	@Test
	public void testValidateInteger() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.INTEGER, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomInt());
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalidBigDecimal() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.BIG_DECIMAL,
			defaultPropertyValidator, RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomString());
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalidDateTime() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			Collections.singleton(String.class), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.DATE_TIME, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomString());
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalidMultipleElements() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			Collections.singleton(TestInvalidClass.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.MULTIPLE_ELEMENT,
			defaultPropertyValidator, RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition,
			new Map[] {_getTestClassAsMap(), _getTestClassAsMap()});
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalidSingleElement() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			Collections.singleton(TestInvalidClass.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.SINGLE_ELEMENT,
			defaultPropertyValidator, RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, _getTestClassAsMap());
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalidValue() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.BOOLEAN, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomString());
	}

	@Test
	public void testValidateLong() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.LONG, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomInt());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomLong());
	}

	@Test
	public void testValidateMultipleElements() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			Collections.singleton(TestClass.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.MULTIPLE_ELEMENT,
			defaultPropertyValidator, RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition,
			new Map[] {_getTestClassAsMap(), _getTestClassAsMap()});
	}

	@Test(expected = ValidationException.class)
	public void testValidateNullClass() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.BOOLEAN, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testValidateSingleElement() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			Collections.singleton(TestClass.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.SINGLE_ELEMENT,
			defaultPropertyValidator, RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, _getTestClassAsMap());
	}

	@Test
	public void testValidateText() {
		DefaultPropertyValidator defaultPropertyValidator =
			new DefaultPropertyValidator();

		PropertyDefinition propertyDefinition = new PropertyDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			PropertyDefinition.PropertyType.TEXT, defaultPropertyValidator,
			RandomTestUtil.randomBoolean());

		defaultPropertyValidator.validate(
			propertyDefinition, RandomTestUtil.randomString());
	}

	public static class TestClass implements Serializable {

		public String getField1() {
			return _field1;
		}

		public String getField2() {
			return _field2;
		}

		public Long getField3() {
			return _field3;
		}

		public void setField1(String field1) {
			_field1 = field1;
		}

		public void setField2(String field2) {
			_field2 = field2;
		}

		public void setField3(Long field3) {
			_field3 = field3;
		}

		private String _field1;
		private String _field2;
		private Long _field3;

	}

	public static class TestInvalidClass implements Serializable {
	}

	private Map<String, Object> _getTestClassAsMap() {
		return new HashMapBuilder<>().<String, Object>put(
			"field1", RandomTestUtil.randomString()
		).<String, Object>put(
			"field2", RandomTestUtil.randomString()
		).<String, Object>put(
			"field3", RandomTestUtil.randomLong()
		).build();
	}

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss'Z'");

}