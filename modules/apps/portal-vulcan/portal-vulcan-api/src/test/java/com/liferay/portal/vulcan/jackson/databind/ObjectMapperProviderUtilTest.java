/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jackson.databind;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class ObjectMapperProviderUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test() throws Exception {
		JsonNode jsonNode = _objectMapper.readTree(
			_objectWriter.writeValueAsString(new TestSubclass()));

		Assert.assertEquals(_objectMapper.createObjectNode(), jsonNode);

		jsonNode = _objectMapper.readTree(
			_objectWriter.writeValueAsString(
				new TestSubclass() {
					{
					}
				}));

		Assert.assertEquals(_objectMapper.createObjectNode(), jsonNode);

		jsonNode = _objectMapper.readTree(
			_objectWriter.writeValueAsString(
				new TestSubclass() {
					{
						type = "test";
					}
				}));

		Assert.assertEquals(
			_objectMapper.createObjectNode(
			).put(
				"type", "test"
			),
			jsonNode);
	}

	@Test
	public void testGetBatchEngineObjectMapperWithJSONStringMaxLength() {
		_testJSONStringMaxLength(
			ObjectMapperProviderUtil::getBatchEngineObjectMapper);
	}

	@Test
	public void testGetObjectMapperWithJSONStringMaxLength() {
		_testJSONStringMaxLength(
			() -> {
				ObjectMapperProviderUtil.getBatchEngineObjectMapper();

				return ObjectMapperProviderUtil.getObjectMapper();
			});
	}

	@JsonSubTypes(
		{@JsonSubTypes.Type(name = "test", value = TestSubclass.class)}
	)
	@JsonTypeInfo(
		include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",
		use = JsonTypeInfo.Id.NAME, visible = true
	)
	@XmlRootElement(name = "TestClass")
	public abstract static class TestClass implements Serializable {

		@JsonGetter("type")
		@Valid
		public String getType() {
			return type;
		}

		@JsonProperty(access = JsonProperty.Access.READ_WRITE)
		protected String type;

	}

	public static class TestSubclass extends TestClass {
	}

	private void _assertJSONStringMaxLength(
		int expectedJSONStringMaxLength, ObjectMapper objectMapper) {

		JsonFactory jsonFactory = objectMapper.getFactory();

		StreamReadConstraints streamReadConstraints =
			jsonFactory.streamReadConstraints();

		Assert.assertEquals(
			expectedJSONStringMaxLength,
			streamReadConstraints.getMaxStringLength());
	}

	private void _testJSONStringMaxLength(Supplier<ObjectMapper> supplier) {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"JSON_STRING_MAX_LENGTH", 10)) {

			_assertJSONStringMaxLength(10, supplier.get());
		}

		_assertJSONStringMaxLength(
			PropsValues.JSON_STRING_MAX_LENGTH, supplier.get());
	}

	private final ObjectMapper _objectMapper =
		ObjectMapperProviderUtil.getObjectMapper();
	private final ObjectWriter _objectWriter = _objectMapper.writer();

}