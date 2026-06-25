/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Antonio Ortega
 */
@FeatureFlag("LPS-164563")
@RunWith(Arquillian.class)
public class DataSetShowSearchUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			DataSetShowSearchUpgradeProcessTest.class);
	}

	@Test
	public void testUpgrade() throws Exception {
		_dataSetObjectEntry = _createDataSetObjectEntry();

		Map<String, Serializable> values = _dataSetObjectEntry.getValues();

		values.put("showSearch", Boolean.FALSE);

		_objectEntryLocalService.updateObjectEntry(
			_dataSetObjectEntry.getUserId(),
			_dataSetObjectEntry.getObjectEntryId(),
			_dataSetObjectEntry.getObjectEntryFolderId(), values,
			new ServiceContext());

		_runUpgrade();

		_dataSetObjectEntry = _objectEntryLocalService.getObjectEntry(
			_dataSetObjectEntry.getObjectEntryId());

		values = _dataSetObjectEntry.getValues();

		Assert.assertEquals(Boolean.TRUE, values.get("showSearch"));
	}

	private ObjectEntry _createDataSetObjectEntry() throws Exception {
		ObjectDefinition dataSetObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", TestPropsValues.getCompanyId());

		Assert.assertNotNull(dataSetObjectDefinition);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			dataSetObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"defaultItemsPerPage", 20
			).put(
				"label", "Data Set"
			).put(
				"listOfItemsPerPage", "10, 20, 50"
			).put(
				"restApplication", "restApplication"
			).put(
				"restEndpoint", "restEndpoint"
			).put(
				"restSchema", "restSchema"
			).build(),
			new ServiceContext());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.frontend.data.set.internal.upgrade.v1_1_0." +
				"DataSetShowSearchUpgradeProcess");

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@DeleteAfterTestRun
	private ObjectEntry _dataSetObjectEntry;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.frontend.data.set.internal.upgrade.registry.FrontendDataSetImplUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}