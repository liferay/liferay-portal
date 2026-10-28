/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.util.comparator;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author Brian Wulbern
 */
public class PortletIdComparatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	@Test
	public void testCompareWithSameTitle() {
		String title = RandomTestUtil.randomString();

		Mockito.when(
			_portal.getPortletTitle(
				ArgumentMatchers.anyString(),
				ArgumentMatchers.any(Locale.class))
		).thenReturn(
			title
		);

		String portletId1 = RandomTestUtil.randomString();
		String portletId2 = RandomTestUtil.randomString();

		PortletIdComparator portletIdComparator = new PortletIdComparator(
			LocaleUtil.US);

		Assert.assertNotEquals(
			0, portletIdComparator.compare(portletId1, portletId2));

		Map<String, String> map = TreeMapBuilder.<String, String>create(
			portletIdComparator
		).put(
			portletId1, portletId1
		).put(
			portletId2, portletId2
		).build();

		Assert.assertEquals(map.toString(), 2, map.size());
	}

	private final Portal _portal = Mockito.mock(Portal.class);

}