/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.internal.portlet.test;

import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;

import org.junit.After;
import org.junit.Before;

/**
 * @author Manuel de la Peña
 */
public abstract class BaseLayoutTypePortletImplTestCase {

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		layout = LayoutTestUtil.addTypePortletLayout(_group, false);

		layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();
	}

	@After
	public void tearDown() {
		StringBundler sb = new StringBundler(
			_LAYOUT_STATIC_PORTLETS_ALL.length);

		for (String layoutStaticPortlet : _LAYOUT_STATIC_PORTLETS_ALL) {
			sb.append(layoutStaticPortlet);
		}

		PropsUtil.set(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, sb.toString());
	}

	protected static PortletPreferences addLayoutPortletPreferences(
			Layout layout, Portlet portlet)
		throws Exception {

		return PortletPreferencesLocalServiceUtil.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			portlet.getPortletId(), portlet, null);
	}

	protected Layout layout;
	protected LayoutTypePortlet layoutTypePortlet;

	private static final String[] _LAYOUT_STATIC_PORTLETS_ALL =
		PropsValues.LAYOUT_STATIC_PORTLETS_ALL;

	@DeleteAfterTestRun
	private Group _group;

}