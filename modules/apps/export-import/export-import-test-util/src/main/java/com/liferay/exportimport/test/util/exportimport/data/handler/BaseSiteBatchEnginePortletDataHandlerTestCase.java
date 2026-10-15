/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.exportimport.data.handler;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

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
		group = addGroup();
		targetGroup = addGroup();
		targetUser = TestPropsValues.getUser();

		_layout = LayoutTestUtil.addTypePortletLayout(group.getGroupId());
		_targetLayout = LayoutTestUtil.addTypePortletLayout(
			targetGroup.getGroupId());

		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(targetGroup);
		GroupTestUtil.deleteGroup(group);
	}

	protected Group addGroup() throws Exception {
		return GroupTestUtil.addGroup();
	}

	@Override
	protected void exportImport(
			Map<String, String[]> parameterMap, Date startDate, Date endDate)
		throws Exception {

		parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).putAll(
			parameterMap
		).build();

		ExportImportConfiguration exportImportConfiguration =
			addExportConfiguration(
				ExportImportConfigurationConstants.TYPE_PUBLISH_PORTLET_LOCAL,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportPortletSettingsMap(
						targetUser, _layout.getPlid(), _layout.getGroupId(),
						portletId, parameterMap, StringPool.BLANK),
				startDate, endDate);

		File larFile = ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);

		try {
			exportImportConfiguration = updateImportConfiguration(
				exportImportConfiguration, targetUser,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportPortletSettingsMap(
						targetUser, _targetLayout.getPlid(),
						_targetLayout.getGroupId(), portletId, parameterMap),
				_targetLayout.getGroupId());

			ExportImportLocalServiceUtil.importPortletDataDeletions(
				exportImportConfiguration, larFile);

			ExportImportLocalServiceUtil.importPortletInfo(
				exportImportConfiguration, larFile);
		}
		finally {
			FileUtil.delete(larFile);
		}
	}

	private Layout _layout;
	private Layout _targetLayout;

}