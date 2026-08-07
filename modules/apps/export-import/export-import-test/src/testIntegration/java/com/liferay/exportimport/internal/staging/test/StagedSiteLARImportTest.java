/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime León Rosado
 */
@RunWith(Arquillian.class)
public class StagedSiteLARImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_sourceGroup = GroupTestUtil.addGroup();

		_sourceLayout = LayoutTestUtil.addTypePortletLayout(_sourceGroup);

		_stagedGroup = GroupTestUtil.addGroup();

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _stagedGroup, false, false,
			ServiceContextTestUtil.getServiceContext(
				_stagedGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-100541")
	public void testImportLARIntoStagingGroupAndPublish() throws Exception {
		Group stagingGroup = _stagedGroup.getStagingGroup();

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			ExportImportTestUtil.importLayoutsInBackground(
				stagingGroup,
				ExportImportTestUtil.exportLayoutsAsFile(
					_sourceGroup, _sourceLayout)));

		String sourceLayoutName = _sourceLayout.getName(LocaleUtil.US);

		List<String> stagingGroupLayoutNames = _getLayoutNames(
			stagingGroup.getGroupId());

		Assert.assertTrue(
			stagingGroupLayoutNames.toString(),
			stagingGroupLayoutNames.contains(sourceLayoutName));

		List<String> liveGroupLayoutNames = _getLayoutNames(
			_stagedGroup.getGroupId());

		Assert.assertFalse(
			liveGroupLayoutNames.toString(),
			liveGroupLayoutNames.contains(sourceLayoutName));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			StagingUtil.publishLayouts(
				TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				_stagedGroup.getGroupId(), false,
				ExportImportConfigurationParameterMapFactoryUtil.
					buildFullPublishParameterMap()));

		liveGroupLayoutNames = _getLayoutNames(_stagedGroup.getGroupId());

		Assert.assertTrue(
			liveGroupLayoutNames.toString(),
			liveGroupLayoutNames.contains(sourceLayoutName));
	}

	private List<String> _getLayoutNames(long groupId) {
		return TransformUtil.transform(
			_layoutLocalService.getLayouts(groupId, false),
			layout -> layout.getName(LocaleUtil.US));
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Group _sourceGroup;

	private Layout _sourceLayout;

	@DeleteAfterTestRun
	private Group _stagedGroup;

	@Inject
	private StagingLocalService _stagingLocalService;

}