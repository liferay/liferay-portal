/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.headless;

import com.liferay.talend.common.exception.MalformedURLException;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class HeadlessUtilTest {

	@Test
	public void testSanitizeOpenAPIModuleURI() {
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("/headless-liferay/v1.0"));
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("/headless-liferay/v1.0/"));
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("headless-liferay/v1.0"));
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("headless-liferay/v1.0/"));
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("o/headless-liferay/v1.0/"));
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("/o/headless-liferay/v1.0/"));
		Assert.assertEquals(
			"/headless-liferay/v1.0",
			HeadlessUtil.sanitizeOpenAPIModuleURI("/o/headless-liferay/v1.0"));
	}

	@Test
	public void testUpdateWithQueryParameters() {
		String url =
			"https://localhost:1977/o/headless-commerce-admin-catalog/v1.0" +
				"/product";

		Map<String, String> parameters = new HashMap<>();

		parameters.put("archive", "true");
		parameters.put("id", "1977");
		parameters.put("key", "197797");
		parameters.put("subscription", "true");

		String uriString = String.valueOf(
			HeadlessUtil.updateWithQueryParameters(url, parameters));

		Assert.assertTrue(
			"URI has archive query parameter",
			uriString.contains("archive=true"));
		Assert.assertTrue(
			"URI has archive key parameter", uriString.contains("key=197797"));
		Assert.assertTrue(
			"URI has archive subscription parameter",
			uriString.contains("subscription=true"));
	}

	@Test
	public void testValidateOpenAPISpecURL() {
		HeadlessUtil.validateOpenAPISpecURL(
			"http://localhost:8080/o/headless/v1.0/openapi.json");
	}

	@Test
	public void testValidateOpenAPISpecURLExceptions() {
		_assertException(MalformedURLException.class, null);

		_assertException(
			MalformedURLException.class, "http://localhost:8080/o/test/wrong");
	}

	private void _assertException(Class<?> exceptionClass, String url) {
		Class<?> clazz = Exception.class;

		try {
			HeadlessUtil.validateOpenAPISpecURL(url);
		}
		catch (Exception exception) {
			clazz = exception.getClass();
		}

		Assert.assertEquals("Invalid URL " + url, exceptionClass, clazz);
	}

}