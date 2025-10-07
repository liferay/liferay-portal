/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Matthew Tambara
 */
@PortalProps(properties = "testClassKey=testClassValue")
public class InitializeKernelUtilTestRuleTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCalendarFactoryUtil() {
		Assert.assertNotNull(CalendarFactoryUtil.getCalendar());
	}

	@Test
	public void testFileUtil() {
		Assert.assertNotNull(FileUtil.getFile());
	}

	@Test
	public void testJSONFactoryUtil() {
		Assert.assertNotNull(JSONFactoryUtil.getJSONFactory());
	}

	@Test
	public void testProps() {
		Assert.assertEquals("testClassValue", PropsUtil.get("testClassKey"));
	}

	@PortalProps(properties = "testMethodKey=testMethodValue")
	@Test
	public void testPropsMethodAnnotation() {
		Assert.assertEquals("testClassValue", PropsUtil.get("testClassKey"));

		Assert.assertEquals("testMethodValue", PropsUtil.get("testMethodKey"));
	}

}