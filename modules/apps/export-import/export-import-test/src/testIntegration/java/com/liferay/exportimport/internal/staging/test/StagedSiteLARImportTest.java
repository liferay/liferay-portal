/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

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
	public void testImportLARIntoStagingGroup() throws Exception {
		Group stagingGroup = _stagedGroup.getStagingGroup();

		_importLARIntoStagingGroup();

		List<Layout> layouts = _layoutLocalService.getLayouts(
			stagingGroup.getGroupId(), false);

		String sourceLayoutName = _sourceLayout.getName(LocaleUtil.US);

		Assert.assertTrue(
			layouts.toString(),
			ListUtil.exists(
				layouts,
				layout -> sourceLayoutName.equals(
					layout.getName(LocaleUtil.US))));
	}

	private ExportImportConfiguration _addExportImportConfiguration(
			long groupId, Map<String, Serializable> settingsMap, int type)
		throws Exception {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				type, settingsMap, WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext());
	}

	private void _importLARIntoStagingGroup() throws Exception {
		Group stagingGroup = _stagedGroup.getStagingGroup();

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_addExportImportConfiguration(
				_sourceGroup.getGroupId(),
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportLayoutSettingsMap(
						TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
						false, new long[] {_sourceLayout.getLayoutId()},
						ExportImportConfigurationParameterMapFactoryUtil.
							buildParameterMap(),
						LocaleUtil.US, TimeZoneUtil.GMT),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));

		long backgroundTaskId =
			_exportImportLocalService.importLayoutsInBackground(
				TestPropsValues.getUserId(),
				_addExportImportConfiguration(
					stagingGroup.getGroupId(),
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUserId(),
							stagingGroup.getGroupId(), false, null,
							ExportImportConfigurationParameterMapFactoryUtil.
								buildParameterMap(),
							LocaleUtil.US, TimeZoneUtil.GMT),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT),
				larFile);

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);
	}

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

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