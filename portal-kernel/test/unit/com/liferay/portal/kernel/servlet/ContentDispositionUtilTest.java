/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniel Raposo
 */
public class ContentDispositionUtilTest {

	@Test
	public void testGetContentDispositionHeaderValueEncodesAccents() {
		Assert.assertEquals(
			"attachment; filename*=UTF-8''Caf%C3%A9%20%C3%B1.lar",
			ContentDispositionUtil.getContentDispositionHeaderValue(
				"Caf\u00e9 \u00f1.lar"));
	}

	@Test
	public void testGetContentDispositionHeaderValueEncodesCRLF() {
		String headerValue =
			ContentDispositionUtil.getContentDispositionHeaderValue(
				"pwn\r\nX-Injected: yes\r\n");

		Assert.assertFalse(headerValue.contains("\r"));
		Assert.assertFalse(headerValue.contains("\n"));
		Assert.assertEquals(
			"attachment; filename*=UTF-8''pwn%0D%0AX-Injected%3A%20yes%0D%0A",
			headerValue);
	}

	@Test
	public void testGetContentDispositionHeaderValueEncodesQuotes() {
		Assert.assertEquals(
			"attachment; filename*=UTF-8''a%22%3B%20filename%3D%22sample.lar",
			ContentDispositionUtil.getContentDispositionHeaderValue(
				"a\"; filename=\"sample.lar"));
	}

	@Test
	public void testGetContentDispositionHeaderValuePreservesReadableName() {
		Assert.assertEquals(
			"attachment; filename=\"My Site.lar\"",
			ContentDispositionUtil.getContentDispositionHeaderValue(
				"My Site.lar"));
	}

	@Test
	public void testGetContentDispositionHeaderValueUsesGivenType() {
		Assert.assertEquals(
			"inline; filename=\"My Site.lar\"",
			ContentDispositionUtil.getContentDispositionHeaderValue(
				HttpHeaders.CONTENT_DISPOSITION_INLINE, "My Site.lar"));
		Assert.assertEquals(
			"inline; filename*=UTF-8''Caf%C3%A9.lar",
			ContentDispositionUtil.getContentDispositionHeaderValue(
				HttpHeaders.CONTENT_DISPOSITION_INLINE, "Caf\u00e9.lar"));
	}

}