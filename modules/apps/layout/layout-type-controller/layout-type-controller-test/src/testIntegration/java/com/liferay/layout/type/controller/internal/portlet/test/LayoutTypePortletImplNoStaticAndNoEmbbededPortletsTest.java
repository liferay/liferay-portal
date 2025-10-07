/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class LayoutTypePortletImplNoStaticAndNoEmbbededPortletsTest
	extends BaseLayoutTypePortletImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testShouldReturnFalseIfANonlayoutCacheableRootPortletIsInstalled()
		throws Exception {

		Portlet noncacheablePortlet = PortletLocalServiceUtil.getPortletById(
			PortletKeys.LOGIN);

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout,
			noncacheablePortlet.getPortletId(), "column-1",
			new HashMap<String, String[]>());

		String cacheablePortletId = PortletProviderUtil.getPortletId(
			"com.liferay.journal.model.JournalArticle",
			PortletProvider.Action.ADD);

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, cacheablePortletId, "column-1",
			new HashMap<String, String[]>());

		Assert.assertFalse(layoutTypePortlet.isCacheable());
	}

	@Test
	public void testShouldReturnTrueIfInstalledRootPortletsAreLayoutCacheable()
		throws Exception {

		String[] layoutStaticPortletsAll =
			PropsValues.LAYOUT_STATIC_PORTLETS_ALL;

		PropsUtil.set(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, "");

		try {
			String cacheablePortletId = PortletProviderUtil.getPortletId(
				"com.liferay.journal.model.JournalArticle",
				PortletProvider.Action.ADD);

			LayoutTestUtil.addPortletToLayout(
				TestPropsValues.getUserId(), layout, cacheablePortletId,
				"column-1", new HashMap<String, String[]>());

			Assert.assertTrue(layoutTypePortlet.isCacheable());
		}
		finally {
			PropsUtil.set(
				PropsKeys.LAYOUT_STATIC_PORTLETS_ALL,
				StringUtil.merge(layoutStaticPortletsAll, StringPool.COMMA));
		}
	}

}