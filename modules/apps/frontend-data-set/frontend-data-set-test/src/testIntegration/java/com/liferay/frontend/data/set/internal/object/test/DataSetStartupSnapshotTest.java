/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.object.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juanjo Fernandez
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-34594"), @FeatureFlag("LPS-164563")}
)
@RunWith(Arquillian.class)
public class DataSetStartupSnapshotTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(DataSetStartupSnapshotTest.class);

		_dataSetSnapshotObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", TestPropsValues.getCompanyId());

		Assert.assertNotNull(_dataSetSnapshotObjectDefinition);

		_dataSetStartupSnapshotObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_STARTUP_SNAPSHOT",
					TestPropsValues.getCompanyId());

		Assert.assertNotNull(_dataSetStartupSnapshotObjectDefinition);
	}

	@Test
	public void testDeleteDataSetSnapshotCascadesToStartupSnapshot()
		throws Exception {

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			RandomTestUtil.randomString());

		ObjectEntry dataSetStartupSnapshotObjectEntry = _saveStartupSnapshot(
			RandomTestUtil.randomString(),
			dataSetSnapshotObjectEntry.getObjectEntryId());

		_objectEntryLocalService.deleteObjectEntry(
			dataSetSnapshotObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				dataSetStartupSnapshotObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testSetDataSetStartupSnapshotReplacesExisting()
		throws Exception {

		String fdsName = RandomTestUtil.randomString();

		ObjectEntry dataSetSnapshotObjectEntry1 =
			_addDataSetSnapshotObjectEntry(fdsName);
		ObjectEntry dataSetSnapshotObjectEntry2 =
			_addDataSetSnapshotObjectEntry(fdsName);

		_saveStartupSnapshot(
			fdsName, dataSetSnapshotObjectEntry1.getObjectEntryId());

		ObjectEntry dataSetStartupSnapshotObjectEntry = _saveStartupSnapshot(
			fdsName, dataSetSnapshotObjectEntry2.getObjectEntryId());

		Map<String, Serializable> values =
			dataSetStartupSnapshotObjectEntry.getValues();

		Assert.assertEquals(
			dataSetSnapshotObjectEntry2.getObjectEntryId(),
			GetterUtil.getLong(
				values.get(_DATA_SET_SNAPSHOT_ID_OBJECT_FIELD_NAME)));
	}

	@Test
	public void testStartupSnapshotExternalReferenceCodeResolvesSnapshot()
		throws Exception {

		String fdsName = RandomTestUtil.randomString();

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			fdsName);

		ObjectEntry dataSetStartupSnapshotObjectEntry = _saveStartupSnapshot(
			fdsName, dataSetSnapshotObjectEntry.getObjectEntryId());

		Map<String, Serializable> values =
			dataSetStartupSnapshotObjectEntry.getValues();

		ObjectEntry resolvedDataSetSnapshotObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getLong(
					values.get(_DATA_SET_SNAPSHOT_ID_OBJECT_FIELD_NAME)));

		Assert.assertEquals(
			dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			resolvedDataSetSnapshotObjectEntry.getExternalReferenceCode());
	}

	private ObjectEntry _addDataSetSnapshotObjectEntry(String fdsName)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_dataSetSnapshotObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"fdsName", fdsName
			).put(
				"label", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	private String _getStartupSnapshotExternalReferenceCode(String fdsName)
		throws Exception {

		User user = TestPropsValues.getUser();

		return user.getExternalReferenceCode() + StringPool.UNDERLINE + fdsName;
	}

	private ObjectEntry _saveStartupSnapshot(
			String fdsName, long dataSetSnapshotObjectEntryId)
		throws Exception {

		String externalReferenceCode = _getStartupSnapshotExternalReferenceCode(
			fdsName);

		ObjectEntry dataSetStartupSnapshotObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, 0,
				_dataSetStartupSnapshotObjectDefinition.
					getObjectDefinitionId());

		if (dataSetStartupSnapshotObjectEntry != null) {
			_objectEntryLocalService.deleteObjectEntry(
				dataSetStartupSnapshotObjectEntry.getObjectEntryId());
		}

		return _objectEntryLocalService.addOrUpdateObjectEntry(
			externalReferenceCode, 0, TestPropsValues.getUserId(),
			_dataSetStartupSnapshotObjectDefinition.getObjectDefinitionId(), 0,
			HashMapBuilder.<String, Serializable>put(
				_DATA_SET_SNAPSHOT_ID_OBJECT_FIELD_NAME,
				dataSetSnapshotObjectEntryId
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	private static final String _DATA_SET_SNAPSHOT_ID_OBJECT_FIELD_NAME =
		"r_dataSetSnapshotToStartupSnapshots_l_dataSetSnapshotId";

	private ObjectDefinition _dataSetSnapshotObjectDefinition;
	private ObjectDefinition _dataSetStartupSnapshotObjectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}