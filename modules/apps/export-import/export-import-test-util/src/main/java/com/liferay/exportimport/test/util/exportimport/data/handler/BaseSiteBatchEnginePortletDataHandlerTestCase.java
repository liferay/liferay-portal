/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.exportimport.data.handler;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;

import java.io.File;

import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

/**
 * @author Alberto Javier Moreno Lage
 */
public abstract class BaseSiteBatchEnginePortletDataHandlerTestCase<T>
	extends BaseBatchEnginePortletDataHandlerTestCase<T> {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_group = addGroup();
		_targetGroup = addGroup();
		_user = TestPropsValues.getUser();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());
		_targetLayout = LayoutTestUtil.addTypePortletLayout(
			_targetGroup.getGroupId());
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(_group);
		GroupTestUtil.deleteGroup(_targetGroup);
	}

	protected Group addGroup() throws Exception {
		return GroupTestUtil.addGroup();
	}

	@Override
	protected void exportImport(
			Date endDate, Map<String, String[]> parameterMap, Date startDate)
		throws Exception {

		parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).putAll(
			parameterMap
		).build();

		ExportImportConfiguration exportImportConfiguration =
			addExportImportConfiguration(
				endDate,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportPortletSettingsMap(
						_user, _layout.getPlid(), _layout.getGroupId(),
						portletId, parameterMap, StringPool.BLANK),
				startDate,
				ExportImportConfigurationConstants.TYPE_PUBLISH_PORTLET_LOCAL);

		File larFile = _exportImportLocalService.exportPortletInfoAsFile(
			exportImportConfiguration);

		try {
			exportImportConfiguration = updateExportImportConfiguration(
				exportImportConfiguration,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportPortletSettingsMap(
						_user, _targetLayout.getPlid(),
						_targetLayout.getGroupId(), portletId, parameterMap),
				_targetLayout.getGroupId(), _user);

			_exportImportLocalService.importPortletDataDeletions(
				exportImportConfiguration, larFile);

			_exportImportLocalService.importPortletInfo(
				exportImportConfiguration, larFile);
		}
		finally {
			FileUtil.delete(larFile);
		}
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Group getTargetGroup() {
		return _targetGroup;
	}

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	private Group _group;
	private Layout _layout;
	private Group _targetGroup;
	private Layout _targetLayout;
	private User _user;

}