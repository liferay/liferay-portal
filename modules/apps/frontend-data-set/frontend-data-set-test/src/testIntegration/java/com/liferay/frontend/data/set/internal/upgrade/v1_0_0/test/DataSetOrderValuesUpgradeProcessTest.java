/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.upgrade.v1_0_0.test;

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
import com.liferay.portal.kernel.util.StringUtil;
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
 * @author Daniel Sanz
 */
@FeatureFlag("LPS-164563")
@RunWith(Arquillian.class)
public class DataSetOrderValuesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			DataSetOrderValuesUpgradeProcessTest.class);
	}

	@Test
	public void testUpgrade() throws Exception {
		_dataSetObjectEntry = _createDataSetObjectEntry();

		Map<String, Serializable> values = _dataSetObjectEntry.getValues();

		Map<String, String> expectedERCs = HashMapBuilder.put(
			"creationActionsOrder",
			_createRelatedObjectEntries(
				values, "L_DATA_SET_ACTION", "creationActionsOrder",
				HashMapBuilder.<String, Serializable>put(
					"r_dataSetToDataSetActions_l_dataSetId",
					_dataSetObjectEntry.getObjectEntryId()
				).put(
					"target", "target"
				).put(
					"type", "creation"
				).build())
		).put(
			"filtersOrder",
			_createRelatedObjectEntries(
				values, "L_DATA_SET_DATE_FILTER", "filtersOrder",
				HashMapBuilder.<String, Serializable>put(
					"fieldName", "fieldName"
				).put(
					"r_dataSetToDataSetDateFilters_l_dataSetId",
					_dataSetObjectEntry.getObjectEntryId()
				).build())
		).put(
			"itemActionsOrder",
			_createRelatedObjectEntries(
				values, "L_DATA_SET_ACTION", "itemActionsOrder",
				HashMapBuilder.<String, Serializable>put(
					"r_dataSetToDataSetActions_l_dataSetId",
					_dataSetObjectEntry.getObjectEntryId()
				).put(
					"target", "target"
				).put(
					"type", "item"
				).build())
		).put(
			"sortsOrder",
			_createRelatedObjectEntries(
				values, "L_DATA_SET_SORT", "sortsOrder",
				HashMapBuilder.<String, Serializable>put(
					"fieldName", "fieldName"
				).put(
					"orderType", "asc"
				).put(
					"r_dataSetToDataSetSorts_l_dataSetId",
					_dataSetObjectEntry.getObjectEntryId()
				).build())
		).put(
			"tableSectionsOrder",
			_createRelatedObjectEntries(
				values, "L_DATA_SET_TABLE_SECTION", "tableSectionsOrder",
				HashMapBuilder.<String, Serializable>put(
					"fieldName", "fieldName"
				).put(
					"r_dataSetToDataSetTableSections_l_dataSetId",
					_dataSetObjectEntry.getObjectEntryId()
				).put(
					"type", "type"
				).build())
		).build();

		_objectEntryLocalService.updateObjectEntry(
			_dataSetObjectEntry.getUserId(),
			_dataSetObjectEntry.getObjectEntryId(),
			_dataSetObjectEntry.getObjectEntryFolderId(), values,
			new ServiceContext());

		_runUpgrade();

		_dataSetObjectEntry = _objectEntryLocalService.getObjectEntry(
			_dataSetObjectEntry.getObjectEntryId());

		values = _dataSetObjectEntry.getValues();

		for (Map.Entry<String, String> entry : expectedERCs.entrySet()) {
			Assert.assertEquals(entry.getValue(), values.get(entry.getKey()));
		}
	}

	private ObjectEntry _createDataSetObjectEntry() throws Exception {
		ObjectDefinition dataSetObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", TestPropsValues.getCompanyId());

		Assert.assertNotNull(dataSetObjectDefinition);

		return _createObjectEntry(
			dataSetObjectDefinition.getObjectDefinitionId(),
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
			).build());
	}

	private ObjectEntry _createObjectEntry(
			long objectDefinitionId, Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(), objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, new ServiceContext());
	}

	private String _createRelatedObjectEntries(
			Map<String, Serializable> dataSetObjectEntryValues,
			String objectDefinitionExternalReferenceCode, String propertyName,
			Map<String, Serializable> relatedObjectEntryValues)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		ObjectEntry objectEntry1 = _createObjectEntry(
			objectDefinition.getObjectDefinitionId(), relatedObjectEntryValues);
		ObjectEntry objectEntry2 = _createObjectEntry(
			objectDefinition.getObjectDefinitionId(), relatedObjectEntryValues);

		dataSetObjectEntryValues.put(
			propertyName,
			StringUtil.merge(
				new Long[] {
					objectEntry1.getObjectEntryId(),
					objectEntry2.getObjectEntryId()
				}));

		return StringUtil.merge(
			new String[] {
				objectEntry1.getExternalReferenceCode(),
				objectEntry2.getExternalReferenceCode()
			});
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.frontend.data.set.internal.upgrade.v1_0_0." +
				"DataSetOrderValuesUpgradeProcess");

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