/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v12_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@FeatureFlag("LPS-164563")
@RunWith(Arquillian.class)
public class ObjectDefinitionSettingUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			ObjectDefinitionSettingUpgradeProcessTest.class);
	}

	@Test
	public void testUpgrade() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectDefinitionA = ObjectDefinitionTestUtil.publishObjectDefinition();
		_objectDefinitionAA =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		TreeTestUtil.bind(
			_objectDefinitionA.getObjectDefinitionId(),
			_objectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_objectDefinitionSettingLocalService.deleteObjectDefinitionSetting(
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				_objectDefinitionAA.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.
					NAME_ALLOW_STANDALONE_OBJECT_ENTRY));

		_objectDefinitionAB =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		TreeTestUtil.bind(
			_objectDefinitionA.getObjectDefinitionId(),
			_objectDefinitionAB.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				_objectDefinitionAB.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.
					NAME_ALLOW_STANDALONE_OBJECT_ENTRY);

		objectDefinitionSetting.setValue("false");

		_objectDefinitionSettingLocalService.updateObjectDefinitionSetting(
			objectDefinitionSetting);

		_forEachDataSetObjectDefinition(
			objectDefinition -> Assert.assertEquals(
				"false",
				_getAllowStandaloneObjectEntryValue(objectDefinition)));

		Assert.assertNull(
			_getAllowStandaloneObjectEntryValue(_objectDefinition));
		Assert.assertNull(
			_getAllowStandaloneObjectEntryValue(_objectDefinitionA));
		Assert.assertNull(
			_getAllowStandaloneObjectEntryValue(_objectDefinitionAA));
		Assert.assertEquals(
			"false", _getAllowStandaloneObjectEntryValue(_objectDefinitionAB));

		_forEachDataSetObjectDefinition(
			objectDefinition -> {
				ObjectDefinitionSetting dataSetObjectDefinitionSetting =
					_objectDefinitionSettingLocalService.
						fetchObjectDefinitionSetting(
							objectDefinition.getObjectDefinitionId(),
							ObjectDefinitionSettingConstants.
								NAME_ALLOW_STANDALONE_OBJECT_ENTRY);

				if (dataSetObjectDefinitionSetting != null) {
					_objectDefinitionSettingLocalService.
						deleteObjectDefinitionSetting(
							dataSetObjectDefinitionSetting);
				}
			});

		_runUpgrade();

		Assert.assertNull(
			_getAllowStandaloneObjectEntryValue(_objectDefinition));
		Assert.assertNull(
			_getAllowStandaloneObjectEntryValue(_objectDefinitionA));
		Assert.assertEquals(
			"true", _getAllowStandaloneObjectEntryValue(_objectDefinitionAA));
		Assert.assertEquals(
			"false", _getAllowStandaloneObjectEntryValue(_objectDefinitionAB));

		_forEachDataSetObjectDefinition(
			objectDefinition -> Assert.assertEquals(
				"false",
				_getAllowStandaloneObjectEntryValue(objectDefinition)));

		_runUpgrade();

		Assert.assertEquals(
			"true", _getAllowStandaloneObjectEntryValue(_objectDefinitionAA));
		Assert.assertEquals(
			"false", _getAllowStandaloneObjectEntryValue(_objectDefinitionAB));
	}

	private void _forEachDataSetObjectDefinition(
			UnsafeConsumer<ObjectDefinition, Exception> unsafeConsumer)
		throws Exception {

		for (String externalReferenceCode : _DATA_SET_OBJECT_DEFINITION_ERCS) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, TestPropsValues.getCompanyId());

			if (objectDefinition == null) {
				continue;
			}

			unsafeConsumer.accept(objectDefinition);
		}
	}

	private String _getAllowStandaloneObjectEntryValue(
		ObjectDefinition objectDefinition) {

		return ObjectDefinitionSettingUtil.getValue(
			ObjectDefinitionSettingConstants.NAME_ALLOW_STANDALONE_OBJECT_ENTRY,
			_objectDefinitionSettingLocalService.getObjectDefinitionSettings(
				objectDefinition.getObjectDefinitionId()));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_multiVMPool.clear();
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v12_1_0." +
			"ObjectDefinitionSettingUpgradeProcess";

	private static final String[] _DATA_SET_OBJECT_DEFINITION_ERCS = {
		"L_DATA_SET_ACTION", "L_DATA_SET_CARDS_SECTION",
		"L_DATA_SET_CLIENT_EXTENSION_FILTER", "L_DATA_SET_DATE_FILTER",
		"L_DATA_SET_LIST_SECTION", "L_DATA_SET_SELECTION_FILTER",
		"L_DATA_SET_SORT", "L_DATA_SET_TABLE_SECTION"
	};

	@Inject
	private MultiVMPool _multiVMPool;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinitionA;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinitionAA;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinitionAB;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}