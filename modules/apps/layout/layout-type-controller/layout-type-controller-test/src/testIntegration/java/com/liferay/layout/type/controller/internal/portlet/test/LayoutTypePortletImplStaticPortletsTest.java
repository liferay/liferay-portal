/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.ReloadURLClassLoader;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class LayoutTypePortletImplStaticPortletsTest
	extends BaseLayoutTypePortletImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldReturnFalseIfThereIsANonlayoutCacheableRootPortlet()
		throws Exception {

		String[] layoutStaticPortletsAll =
			PropsValues.LAYOUT_STATIC_PORTLETS_ALL;

		PropsValues.LAYOUT_STATIC_PORTLETS_ALL = StringPool.EMPTY_ARRAY;

		try {
			Portlet noncacheablePortlet =
				PortletLocalServiceUtil.getPortletById(PortletKeys.LOGIN);

			String cacheablePortletId = PortletProviderUtil.getPortletId(
				"com.liferay.journal.model.JournalArticle",
				PortletProvider.Action.ADD);

			PropsUtil.set(
				PropsKeys.LAYOUT_STATIC_PORTLETS_ALL,
				noncacheablePortlet.getPortletId() + "," + cacheablePortletId);

			LayoutTypePortlet reloadedLayoutTypePortlet =
				_reloadLayoutTypePortlet();

			Assert.assertFalse(reloadedLayoutTypePortlet.isCacheable());
		}
		finally {
			PropsValues.LAYOUT_STATIC_PORTLETS_ALL = layoutStaticPortletsAll;
		}
	}

	@Test
	public void testShouldReturnTrueIfAllRootPortletsAreLayoutCacheable()
		throws Exception {

		String[] layoutStaticPortletsAll =
			PropsValues.LAYOUT_STATIC_PORTLETS_ALL;

		PropsUtil.set(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, "");

		try {
			String cacheablePortletId = PortletProviderUtil.getPortletId(
				"com.liferay.journal.model.JournalArticle",
				PortletProvider.Action.ADD);

			PropsUtil.set(
				PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, cacheablePortletId);

			LayoutTypePortlet reloadedLayoutTypePortlet =
				_reloadLayoutTypePortlet();

			Assert.assertTrue(reloadedLayoutTypePortlet.isCacheable());
		}
		finally {
			PropsUtil.set(
				PropsKeys.LAYOUT_STATIC_PORTLETS_ALL,
				StringUtil.merge(layoutStaticPortletsAll, StringPool.COMMA));
		}
	}

	private LayoutTypePortlet _reloadLayoutTypePortlet() throws Exception {
		Class<? extends LayoutTypePortlet> clazz =
			(Class<LayoutTypePortlet>)layoutTypePortlet.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Class<?> anonymousInnerClass = classLoader.loadClass(
			clazz.getName() + "$1");

		ClassLoader reloadURLClassLoader = new ReloadURLClassLoader(
			clazz, anonymousInnerClass);

		Class<? extends LayoutTypePortlet> reloadedClass =
			(Class<? extends LayoutTypePortlet>)reloadURLClassLoader.loadClass(
				clazz.getName());

		Constructor<? extends LayoutTypePortlet> constructor =
			reloadedClass.getConstructor(
				Layout.class, LayoutTypeController.class,
				LayoutTypeAccessPolicy.class);

		return constructor.newInstance(
			layoutTypePortlet.getLayout(),
			layoutTypePortlet.getLayoutTypeController(),
			layoutTypePortlet.getLayoutTypeAccessPolicy());
	}

}