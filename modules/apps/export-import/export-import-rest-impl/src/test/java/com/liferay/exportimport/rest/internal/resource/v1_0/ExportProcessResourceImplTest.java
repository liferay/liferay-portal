/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Gabor Komaromi
 */
public class ExportProcessResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetContentDispositionHeaderValueEncodesCRLF() {
		String headerValue =
			ExportProcessResourceImpl.getContentDispositionHeaderValue(
				"pwn\r\nX-Injected: yes\r\n");

		Assert.assertFalse(headerValue.contains("\r"));
		Assert.assertFalse(headerValue.contains("\n"));
		Assert.assertFalse(headerValue.contains("X-Injected: yes"));
		Assert.assertEquals(
			"attachment; filename*=UTF-8''pwn%0D%0AX-Injected%3A%20yes%0D%0A",
			headerValue);
	}

	@Test
	public void testGetContentDispositionHeaderValuePreservesReadableName() {
		Assert.assertEquals(
			"attachment; filename*=UTF-8''My%20Site.lar",
			ExportProcessResourceImpl.getContentDispositionHeaderValue(
				"My Site.lar"));
	}

}