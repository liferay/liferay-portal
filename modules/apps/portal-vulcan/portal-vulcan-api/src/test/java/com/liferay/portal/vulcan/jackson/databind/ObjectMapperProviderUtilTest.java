/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jackson.databind;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
		ObjectMapper objectMapper = ObjectMapperProviderUtil.getObjectMapper();

		ObjectWriter objectWriter = objectMapper.writer();

		Assert.assertEquals(
			"{\n  \"type\" : \"test\"\n}",
			objectWriter.writeValueAsString(new TestSubclass()));
		Assert.assertEquals(
			"{\n  \"type\" : \"ObjectMapperProviderUtilTest$1\"\n}",
			objectWriter.writeValueAsString(
				new TestSubclass() {
					{
					}
				}));
		Assert.assertEquals(
			"{\n  \"type\" : \"ObjectMapperProviderUtilTest$2\",\n  \"type\" " +
				": \"test\"\n}",
			objectWriter.writeValueAsString(
				new TestSubclass() {
					{
						type = "test";
					}
				}));
	}

	@JsonSubTypes(
		{@JsonSubTypes.Type(name = "test", value = TestSubclass.class)}
	)
	@JsonTypeInfo(
		include = JsonTypeInfo.As.PROPERTY, property = "type",
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

}