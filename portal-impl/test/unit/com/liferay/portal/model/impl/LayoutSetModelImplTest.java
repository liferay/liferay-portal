/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.NavigableMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class LayoutSetModelImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCopyCacheFields() {
		LayoutSetImpl sourceLayoutSetImpl = new LayoutSetImpl();

		String companyFallbackVirtualHostname = RandomTestUtil.randomString();

		sourceLayoutSetImpl.setCompanyFallbackVirtualHostname(
			companyFallbackVirtualHostname);

		sourceLayoutSetImpl.setMvccVersion(2);

		NavigableMap<String, String> virtualHostnames = TreeMapBuilder.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		).build();

		sourceLayoutSetImpl.setVirtualHostnames(virtualHostnames);

		LayoutSetImpl layoutSetImpl = new LayoutSetImpl();

		layoutSetImpl.setMvccVersion(1);

		layoutSetImpl.copyCacheFields(sourceLayoutSetImpl);

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				layoutSetImpl, "_companyFallbackVirtualHostname"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				layoutSetImpl, "_virtualHostnames"));

		layoutSetImpl.setMvccVersion(2);

		layoutSetImpl.copyCacheFields(sourceLayoutSetImpl);

		Assert.assertEquals(
			companyFallbackVirtualHostname,
			layoutSetImpl.getCompanyFallbackVirtualHostname());
		Assert.assertSame(
			virtualHostnames, layoutSetImpl.getVirtualHostnames());
	}

	@Test
	public void testCopyCacheFieldsWithUncomputedSource() {
		LayoutSetImpl sourceLayoutSetImpl = new LayoutSetImpl();

		sourceLayoutSetImpl.setMvccVersion(1);

		LayoutSetImpl layoutSetImpl = new LayoutSetImpl();

		layoutSetImpl.setMvccVersion(1);

		layoutSetImpl.copyCacheFields(sourceLayoutSetImpl);

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				layoutSetImpl, "_companyFallbackVirtualHostname"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				layoutSetImpl, "_virtualHostnames"));

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				sourceLayoutSetImpl, "_companyFallbackVirtualHostname"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				sourceLayoutSetImpl, "_virtualHostnames"));
	}

}