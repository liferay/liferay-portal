/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@RunWith(Arquillian.class)
public class LayoutLocalServiceWrapperTest {

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
	public void testCopyLayoutContent() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		List<LayoutContentVersion> layoutContentVersions =
			_testCopyLayoutContent(1, layout);

		LayoutContentVersion layoutContentVersion = layoutContentVersions.get(
			0);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			layoutContentVersion.getStatus());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			draftLayout.getPlid(), layoutContentVersion.getPlid());
	}

	private List<LayoutContentVersion> _testCopyLayoutContent(
			int count, Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		List<LayoutContentVersion> layoutContentVersions1 =
			_layoutContentVersionLocalService.getLayoutContentVersions(
				draftLayout.getPlid());

		_layoutLocalService.copyLayoutContent(draftLayout, layout);

		List<LayoutContentVersion> layoutContentVersions2 =
			_layoutContentVersionLocalService.getLayoutContentVersions(
				draftLayout.getPlid());

		Assert.assertEquals(
			layoutContentVersions2.toString(),
			layoutContentVersions1.size() + count,
			layoutContentVersions2.size());

		return layoutContentVersions2;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

}