/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.nested.portlets.layout.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.portlet.PortletManager;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class NestedPortletsPortletManagerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testIsVisibleInContentPage() throws Exception {
		Assert.assertFalse(
			_portletManager.isVisible(
				LayoutTestUtil.addTypeContentLayout(_group)));
	}

	@Test
	public void testIsVisibleInWidgetPage() throws Exception {
		Assert.assertTrue(
			_portletManager.isVisible(
				LayoutTestUtil.addTypePortletLayout(_group)));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "jakarta.portlet.name=" + PortletKeys.NESTED_PORTLETS)
	private PortletManager _portletManager;

}