/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.multipart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class MultipartBodyTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetBinaryFile() {
		BinaryFile binaryFile = new BinaryFile(
			"contentType", "fileName", null, 0);

		MultipartBody multipartBody = MultipartBody.of(
			Collections.singletonMap("file", binaryFile), __ -> _objectMapper,
			Collections.emptyMap());

		MatcherAssert.assertThat(
			multipartBody.getBinaryFile("file"), Is.is(binaryFile));
		MatcherAssert.assertThat(
			multipartBody.getBinaryFile("null"),
			Is.is(CoreMatchers.nullValue()));
	}

	@Test
	public void testGetValueAsInstance() throws IOException {

		// With object mapper

		MultipartBody multipartBody = MultipartBody.of(
			Collections.emptyMap(), __ -> _objectMapper,
			Collections.singletonMap(
				"key",
				JSONUtil.put(
					"list", Arrays.asList(1, 2, 3)
				).put(
					"number", 42
				).put(
					"string", "Hello"
				).toString()));

		TestClass testClass = multipartBody.getValueAsInstance(
			"key", TestClass.class);

		MatcherAssert.assertThat(testClass.list, Matchers.contains(1, 2, 3));
		MatcherAssert.assertThat(testClass.number, Is.is(42L));
		MatcherAssert.assertThat(testClass.string, Is.is("Hello"));
		MatcherAssert.assertThat(
			testClass.testClass, Is.is(CoreMatchers.nullValue()));

		try {
			multipartBody.getValueAsInstance("null", TestClass.class);

			throw new AssertionError("Should thrown exception");
		}
		catch (Exception exception) {
			MatcherAssert.assertThat(
				exception,
				Is.is(CoreMatchers.instanceOf(BadRequestException.class)));
			MatcherAssert.assertThat(
				exception.getMessage(),
				Is.is("Missing JSON property with the key: null"));
		}

		// Without object mapper

		multipartBody = MultipartBody.of(
			Collections.emptyMap(), __ -> null,
			Collections.singletonMap("key", "value"));

		try {
			multipartBody.getValueAsInstance("key", TestClass.class);

			throw new AssertionError();
		}
		catch (Exception exception) {
			MatcherAssert.assertThat(
				exception,
				Is.is(
					CoreMatchers.instanceOf(
						InternalServerErrorException.class)));

			String expectedMessage =
				"Unable to get object mapper for class " +
					TestClass.class.getName();

			MatcherAssert.assertThat(
				exception.getMessage(), Is.is(expectedMessage));
		}
	}

	@Test
	public void testGetValueAsNullableInstance() throws IOException {

		// Present optional

		MultipartBody multipartBody = MultipartBody.of(
			Collections.emptyMap(), __ -> _objectMapper,
			Collections.singletonMap(
				"key",
				JSONUtil.put(
					"list", Arrays.asList(1, 2, 3)
				).put(
					"number", 42
				).put(
					"string", "Hello"
				).toString()));

		TestClass testClass = multipartBody.getValueAsNullableInstance(
			"key", TestClass.class);

		MatcherAssert.assertThat(testClass != null, Is.is(true));

		MatcherAssert.assertThat(testClass.list, Matchers.contains(1, 2, 3));
		MatcherAssert.assertThat(testClass.number, Is.is(42L));
		MatcherAssert.assertThat(testClass.string, Is.is("Hello"));
		MatcherAssert.assertThat(
			testClass.testClass, Is.is(CoreMatchers.nullValue()));

		// Null optional

		testClass = multipartBody.getValueAsNullableInstance(
			"null", TestClass.class);

		MatcherAssert.assertThat(testClass != null, Is.is(false));

		// Incorrect JSON

		multipartBody = MultipartBody.of(
			Collections.emptyMap(), __ -> _objectMapper,
			Collections.singletonMap(
				"key",
				JSONUtil.put(
					"number", 42
				).put(
					"string", "Hello"
				).put(
					"wrongKey", Arrays.asList(1, 2, 3)
				).toString()));

		try {
			multipartBody.getValueAsInstance("key", TestClass.class);

			throw new AssertionError();
		}
		catch (Exception exception) {
			MatcherAssert.assertThat(
				exception,
				Is.is(
					CoreMatchers.instanceOf(
						UnrecognizedPropertyException.class)));
		}
	}

	@Test
	public void testGetValueAsString() {
		MultipartBody multipartBody = MultipartBody.of(
			Collections.emptyMap(), __ -> _objectMapper,
			Collections.singletonMap("key", "value"));

		MatcherAssert.assertThat(
			multipartBody.getValueAsString("key"), Is.is("value"));
		MatcherAssert.assertThat(
			multipartBody.getValueAsString("null"),
			Is.is(CoreMatchers.nullValue()));
	}

	public static class TestClass {

		public List<Integer> list;
		public Long number;
		public String string;
		public TestClass testClass;

	}

	private static final ObjectMapper _objectMapper = new ObjectMapper();

}