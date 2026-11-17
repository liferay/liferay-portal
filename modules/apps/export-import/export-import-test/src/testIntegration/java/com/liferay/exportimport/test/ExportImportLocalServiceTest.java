/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.io.File;

import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ExportImportLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_controlPanelGroup = GroupLocalServiceUtil.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CONTROL_PANEL);

		_expandoTable = ExpandoTableLocalServiceUtil.addTable(
			TestPropsValues.getCompanyId(), User.class.getName(),
			RandomTestUtil.randomString());

		ExpandoColumnLocalServiceUtil.addColumn(
			_expandoTable.getTableId(), RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);
	}

	@Test
	@TestInfo("LPD-115712")
	public void testImportPortletInfo() throws Exception {
		_testImportPortletInfoIntoCompanyGroup();
		_testImportPortletInfoIntoControlPanelGroup();
		_testImportPortletInfoIntoSiteGroup();
	}

	private File _exportPortletInfo(String portletId) throws Exception {
		User user = TestPropsValues.getUser();

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							user,
							PortalUtil.getControlPanelPlid(
								TestPropsValues.getCompanyId()),
							_controlPanelGroup.getGroupId(), portletId,
							_getParameterMap(portletId),
							RandomTestUtil.randomString()));

		return ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);
	}

	private Map<String, String[]> _getParameterMap(String portletId) {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA + "_" + portletId,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private void _importPortletInfo(Group group, String portletId)
		throws Exception {

		User user = TestPropsValues.getUser();

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							user,
							PortalUtil.getControlPanelPlid(
								TestPropsValues.getCompanyId()),
							group.getGroupId(), portletId,
							_getParameterMap(portletId)));

		ExportImportLocalServiceUtil.importPortletInfo(
			exportImportConfiguration, _exportPortletInfo(portletId));
	}

	private void _testImportPortletInfoIntoCompanyGroup() throws Exception {
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group group = stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		_importPortletInfo(group, PortletKeys.EXPANDO);
		_importPortletInfo(group, PortletKeys.PASSWORD_POLICIES_ADMIN);
	}

	private void _testImportPortletInfoIntoControlPanelGroup()
		throws Exception {

		_importPortletInfo(_controlPanelGroup, PortletKeys.EXPANDO);
		_importPortletInfo(
			_controlPanelGroup, PortletKeys.PASSWORD_POLICIES_ADMIN);
	}

	private void _testImportPortletInfoIntoSiteGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try {
			AssertUtils.assertFailure(
				PortletDataException.class,
				"Unable to import a company scoped element into a site group",
				() -> _importPortletInfo(group, PortletKeys.EXPANDO));
			AssertUtils.assertFailure(
				PortletDataException.class,
				"Unable to import a company scoped element into a site group",
				() -> _importPortletInfo(
					group, PortletKeys.PASSWORD_POLICIES_ADMIN));
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group);
		}
	}

	private Group _controlPanelGroup;

	@DeleteAfterTestRun
	private ExpandoTable _expandoTable;

}