/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.filter;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.filter.RenderFilter;

import java.util.Collections;
import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.Constants;

/**
 * @author Shuyang Zhou
 */
public class BeanFilterImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testToDictionary() {
		int ordinal = RandomTestUtil.randomInt();

		BeanFilterImpl beanFilterImpl = new BeanFilterImpl(
			RenderFilter.class, RandomTestUtil.randomString(),
			Collections.emptyMap(), Collections.emptySet(), ordinal,
			Collections.emptySet());

		Dictionary<String, Object> dictionary = beanFilterImpl.toDictionary();

		Assert.assertEquals(
			dictionary.toString(), ordinal,
			dictionary.get(Constants.SERVICE_RANKING));
	}

}