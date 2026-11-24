/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.sidebar.panel.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sandro Chinea
 */
@RunWith(Arquillian.class)
public class CommentsContentPageEditorSidebarPanelTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-117394")
	public void testIsVisible() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertTrue(
			_commentsContentPageEditorSidebarPanel.isVisible(
				PermissionThreadLocal.getPermissionChecker(),
				draftLayout.getPlid(), -1));

		Layout launchDraftLayout = LayoutTestUtil.addLaunchDraftLayout(
			RandomTestUtil.randomLong(), layout);

		Assert.assertFalse(
			_commentsContentPageEditorSidebarPanel.isVisible(
				PermissionThreadLocal.getPermissionChecker(),
				launchDraftLayout.getPlid(), -1));
	}

	@Inject(
		filter = "component.name=com.liferay.layout.content.page.editor.web.internal.sidebar.panel.CommentsContentPageEditorSidebarPanel"
	)
	private ContentPageEditorSidebarPanel
		_commentsContentPageEditorSidebarPanel;

	@DeleteAfterTestRun
	private Group _group;

}