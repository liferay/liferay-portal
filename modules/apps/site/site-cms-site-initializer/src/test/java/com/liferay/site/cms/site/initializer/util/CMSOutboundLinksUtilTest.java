/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jürgen Kappler
 */
public class CMSOutboundLinksUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetObjectEntryExternalReferenceCodeToken() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			"objectEntryERC_" + externalReferenceCode,
			CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
				externalReferenceCode));
	}

	@Test
	public void testGetObjectEntryIdToken() {
		long objectEntryId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			"objectEntryId_" + objectEntryId,
			CMSOutboundLinksUtil.getObjectEntryIdToken(objectEntryId));
	}

}