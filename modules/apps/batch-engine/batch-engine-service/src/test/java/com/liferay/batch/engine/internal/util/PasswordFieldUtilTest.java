/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Adolfo Pérez
 */
public class PasswordFieldUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPasswordFieldNames() {
		Assert.assertEquals(
			Set.of("credential.secret", "credentials.secret", "password"),
			PasswordFieldUtil.getPasswordFieldNames(TestEntity.class));
		Assert.assertEquals(
			Collections.emptySet(),
			PasswordFieldUtil.getPasswordFieldNames(Object.class));
	}

	@Test
	public void testToString() throws Exception {
		String password = StringUtil.randomString();

		TestEntity testEntity = new TestEntity();

		testEntity.credential = new Credential("login1", password);
		testEntity.credentials = new Credential[] {
			new Credential("login2", password),
			new Credential("login3", password)
		};
		testEntity.name = "login4";
		testEntity.password = password;

		String string = PasswordFieldUtil.toString(testEntity);

		Assert.assertFalse(string, string.contains(password));

		JSONAssert.assertEquals(
			"{\"credential\": {\"login\": \"login1\"}, \"credentials\": " +
				"[{\"login\": \"login2\"}, {\"login\": \"login3\"}], " +
					"\"name\": \"login4\"}",
			string, true);
	}

	@Test
	public void testToStringWithInvalidJSON() {
		TestEntity testEntity = new TestEntity() {

			@Override
			public String toString() {
				return StringUtil.randomString();
			}

		};

		Assert.assertNull(PasswordFieldUtil.toString(testEntity));
	}

	@Test
	public void testToStringWithoutPasswordFields() {
		String password = StringUtil.randomString();

		Object object = new Object() {

			@Override
			public String toString() {
				return "password=" + password;
			}

		};

		Assert.assertEquals(
			"password=" + password, PasswordFieldUtil.toString(object));
	}

	@Schema
	public static class Credential {

		public Credential(String login, String password) {
			this.login = login;
			this.password = password;
		}

		@Schema
		public String getLogin() {
			return login;
		}

		@Schema(format = "password")
		public String getPassword() {
			return password;
		}

		@Override
		public String toString() {
			return String.format(
				"{\"login\": \"%s\", \"secret\": \"%s\"}", login, password);
		}

		protected String login;

		@JsonProperty("secret")
		protected String password;

	}

	@Schema
	public static class TestEntity {

		@Schema
		public Credential getCredential() {
			return credential;
		}

		@Schema
		public Credential[] getCredentials() {
			return credentials;
		}

		@Schema
		public String getName() {
			return name;
		}

		@Schema(format = "password")
		public String getPassword() {
			return password;
		}

		@Override
		public String toString() {
			return String.format(
				"{\"credential\": %s, \"credentials\": [%s, %s], \"name\": " +
					"\"%s\", \"password\": \"%s\"}",
				credential, credentials[0], credentials[1], name, password);
		}

		protected Credential credential;
		protected Credential[] credentials;
		protected String name;
		protected String password;

	}

}