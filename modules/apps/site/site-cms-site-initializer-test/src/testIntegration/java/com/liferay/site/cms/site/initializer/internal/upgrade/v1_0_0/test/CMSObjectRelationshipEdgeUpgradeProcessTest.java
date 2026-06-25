/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class CMSObjectRelationshipEdgeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		CMSTestUtil.getOrAddGroup(
			CMSObjectRelationshipEdgeUpgradeProcessTest.class);

		_cmsContentStructuresObjectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());
		_cmsFileTypesObjectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				TestPropsValues.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			TreeTestUtil.unbind(
				objectDefinition.getObjectDefinitionId(),
				_objectRelationshipLocalService);
		}

		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}

		_objectDefinitions.clear();
	}

	@Test
	public void testUpgradeObjectDefinitions() throws Exception {
		ObjectDefinition contentObjectDefinition1 = _addCMSObjectDefinition(
			_cmsContentStructuresObjectFolder);
		ObjectDefinition contentObjectDefinition2 = _addCMSObjectDefinition(
			_cmsContentStructuresObjectFolder);

		ObjectRelationship contentObjectRelationship1 = _addObjectRelationship(
			contentObjectDefinition2, contentObjectDefinition1);

		ObjectDefinition contentObjectDefinition3 = _addCMSObjectDefinition(
			_cmsContentStructuresObjectFolder);

		ObjectRelationship contentObjectRelationship2 = _addObjectRelationship(
			contentObjectDefinition3, contentObjectDefinition2);

		ObjectDefinition customObjectDefinition1 = _addObjectDefinition();
		ObjectDefinition customObjectDefinition2 = _addObjectDefinition();

		ObjectRelationship customObjectRelationship1 = _addObjectRelationship(
			customObjectDefinition2, customObjectDefinition1);

		ObjectDefinition customObjectDefinition3 = _addObjectDefinition();

		ObjectRelationship customObjectRelationship2 = _addObjectRelationship(
			customObjectDefinition3, customObjectDefinition2);

		ObjectDefinition fileObjectDefinition1 = _addCMSObjectDefinition(
			_cmsFileTypesObjectFolder);
		ObjectDefinition fileObjectDefinition2 = _addCMSObjectDefinition(
			_cmsFileTypesObjectFolder);

		ObjectRelationship fileObjectRelationship1 = _addObjectRelationship(
			fileObjectDefinition2, fileObjectDefinition1);

		ObjectDefinition fileObjectDefinition3 = _addCMSObjectDefinition(
			_cmsFileTypesObjectFolder);

		ObjectRelationship fileObjectRelationship2 = _addObjectRelationship(
			fileObjectDefinition3, fileObjectDefinition2);

		_runUpgrade();

		_assertObjectRelationshipEdge(
			true, contentObjectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, contentObjectRelationship2.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			false, customObjectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			false, customObjectRelationship2.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, fileObjectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, fileObjectRelationship2.getObjectRelationshipId());
	}

	@Test
	public void testUpgradeObjectEntries() throws Exception {
		Locale locale = LocaleUtil.fromLanguageId(
			UpgradeProcessUtil.getDefaultLanguageId(
				TestPropsValues.getCompanyId()));

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				locale, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				locale, RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		long depotGroupId = depotEntry.getGroupId();

		ObjectDefinition rootObjectDefinition = _addCMSObjectDefinition();
		ObjectDefinition childObjectDefinition = _addCMSObjectDefinition();
		ObjectDefinition grandchildObjectDefinition = _addCMSObjectDefinition();

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			childObjectDefinition, rootObjectDefinition);
		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			grandchildObjectDefinition, childObjectDefinition);

		ObjectEntry rootObjectEntry = _addObjectEntry(
			depotGroupId, rootObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", RandomTestUtil.randomString()
			).build());

		long rootObjectEntryId = rootObjectEntry.getObjectEntryId();

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship1.getObjectFieldId2());

		ObjectEntry childObjectEntry = _addObjectEntry(
			depotGroupId, childObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", RandomTestUtil.randomString()
			).put(
				objectField.getName(), rootObjectEntryId
			).build());

		long childObjectEntryId = childObjectEntry.getObjectEntryId();

		objectField = _objectFieldLocalService.getObjectField(
			objectRelationship2.getObjectFieldId2());

		ObjectEntry grandchildObjectEntry = _addObjectEntry(
			depotGroupId, grandchildObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", RandomTestUtil.randomString()
			).put(
				objectField.getName(), childObjectEntryId
			).build());

		_runUpgrade();

		_assertObjectRelationshipEdge(
			true, objectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, objectRelationship2.getObjectRelationshipId());

		childObjectEntry = _objectEntryLocalService.getObjectEntry(
			childObjectEntryId);

		Assert.assertEquals(
			rootObjectEntryId, childObjectEntry.getRootObjectEntryId());

		grandchildObjectEntry = _objectEntryLocalService.getObjectEntry(
			grandchildObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			rootObjectEntryId, grandchildObjectEntry.getRootObjectEntryId());
	}

	@Test
	public void testUpgradeObjectRelationshipsCascadeDeletionType()
		throws Exception {

		_testUpgradeObjectRelationshipsCascadeDeletionType();
	}

	@Test
	public void testUpgradeObjectRelationshipsDisassociateDeletionType()
		throws Exception {

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();
		ObjectDefinition cascadeObjectDefinition = _addCMSObjectDefinition();
		ObjectDefinition disassociateObjectDefinition =
			_addCMSObjectDefinition();

		ObjectRelationship cascadeObjectRelationship = _addObjectRelationship(
			cascadeObjectDefinition, objectDefinition);
		ObjectRelationship disassociateObjectRelationship =
			_addObjectRelationship(
				disassociateObjectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				objectDefinition);

		_runUpgrade();

		_assertObjectRelationshipEdge(
			true, cascadeObjectRelationship.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			false, disassociateObjectRelationship.getObjectRelationshipId());
	}

	@Test
	public void testUpgradeWithDepthGreaterThanAllowed() throws Exception {
		ObjectDefinition[] objectDefinitions = new ObjectDefinition[6];

		for (int i = 0; i < objectDefinitions.length; i++) {
			objectDefinitions[i] = _addCMSObjectDefinition();
		}

		for (int i = 0; i < (objectDefinitions.length - 1); i++) {
			_addObjectRelationship(
				objectDefinitions[i + 1], objectDefinitions[i]);
		}

		try {
			_runUpgrade();

			Assert.fail(
				"Expected an UpgradeException for exceeding the maximum " +
					"nesting depth");
		}
		catch (UpgradeException upgradeException) {
			String message = upgradeException.getMessage();

			Assert.assertTrue(
				message, message.contains("maximum nesting depth"));

			for (ObjectDefinition objectDefinition : objectDefinitions) {
				Assert.assertTrue(
					message, message.contains(objectDefinition.getShortName()));
			}
		}
	}

	@Test
	public void testUpgradeWithExistingEdgeObjectRelationships()
		throws Exception {

		ObjectDefinition rootObjectDefinition = _addCMSObjectDefinition();
		ObjectDefinition childObjectDefinition = _addCMSObjectDefinition();
		ObjectDefinition grandchildObjectDefinition = _addCMSObjectDefinition();

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			childObjectDefinition, rootObjectDefinition);
		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			grandchildObjectDefinition, childObjectDefinition);

		_runUpgrade();

		_assertObjectRelationshipEdge(
			true, objectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, objectRelationship2.getObjectRelationshipId());

		long rootObjectDefinitionId =
			rootObjectDefinition.getObjectDefinitionId();

		_assertRootObjectDefinitionIds(
			rootObjectDefinitionId, childObjectDefinition);
		_assertRootObjectDefinitionIds(
			rootObjectDefinitionId, grandchildObjectDefinition);

		long[] childRootObjectDefinitionIds = _getRootObjectDefinitionIds(
			childObjectDefinition);
		long[] grandchildRootObjectDefinitionIds = _getRootObjectDefinitionIds(
			grandchildObjectDefinition);

		_runUpgrade();

		_assertObjectRelationshipEdge(
			true, objectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, objectRelationship2.getObjectRelationshipId());

		Assert.assertArrayEquals(
			childRootObjectDefinitionIds,
			_getRootObjectDefinitionIds(childObjectDefinition));
		Assert.assertArrayEquals(
			grandchildRootObjectDefinitionIds,
			_getRootObjectDefinitionIds(grandchildObjectDefinition));
	}

	@Test
	public void testUpgradeWithObjectRelationshipsCycle() throws Exception {
		ObjectDefinition objectDefinition1 = _addCMSObjectDefinition();
		ObjectDefinition objectDefinition2 = _addCMSObjectDefinition();

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			objectDefinition2, objectDefinition1);
		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			objectDefinition1, objectDefinition2);

		try {
			_runUpgrade();

			Assert.fail(
				"Expected an UpgradeException for a cycle in the CMS object " +
					"relationships");
		}
		catch (UpgradeException upgradeException) {
			String message = upgradeException.getMessage();

			Assert.assertTrue(message, message.contains("form a cycle"));
			Assert.assertTrue(
				message, message.contains(objectRelationship1.getName()));
			Assert.assertTrue(
				message, message.contains(objectRelationship2.getName()));
		}
	}

	private ObjectDefinition _addCMSObjectDefinition() throws Exception {
		return _addCMSObjectDefinition(_cmsContentStructuresObjectFolder);
	}

	private ObjectDefinition _addCMSObjectDefinition(ObjectFolder objectFolder)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, false, false, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).userId(
						TestPropsValues.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"able"
					).build()),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS, "true");

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).userId(
						TestPropsValues.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"able"
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private ObjectEntry _addObjectEntry(
			long groupId, long objectDefinitionId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(), objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, ServiceContextTestUtil.getServiceContext());
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition childObjectDefinition,
			ObjectDefinition parentObjectDefinition)
		throws Exception {

		return ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, parentObjectDefinition,
			childObjectDefinition);
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition childObjectDefinition, String deletionType,
			ObjectDefinition parentObjectDefinition)
		throws Exception {

		return ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, parentObjectDefinition,
			childObjectDefinition, deletionType);
	}

	private void _assertObjectRelationshipEdge(
			boolean expected, long objectRelationshipId)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		Assert.assertEquals(expected, objectRelationship.isEdge());
	}

	private void _assertRootObjectDefinitionIds(
			long expectedRootObjectDefinitionId,
			ObjectDefinition objectDefinition)
		throws Exception {

		objectDefinition = _objectDefinitionLocalService.getObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		Assert.assertTrue(
			ArrayUtil.contains(
				objectDefinition.getRootObjectDefinitionIds(),
				expectedRootObjectDefinitionId));
	}

	private long[] _getRootObjectDefinitionIds(
			ObjectDefinition objectDefinition)
		throws Exception {

		objectDefinition = _objectDefinitionLocalService.getObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		return objectDefinition.getRootObjectDefinitionIds();
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private void _testUpgradeObjectRelationshipsCascadeDeletionType()
		throws Exception {

		ObjectDefinition objectDefinition1 = _addCMSObjectDefinition();
		ObjectDefinition objectDefinition2 = _addCMSObjectDefinition();
		ObjectDefinition objectDefinition3 = _addCMSObjectDefinition();
		ObjectDefinition objectDefinition4 = _addCMSObjectDefinition();

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			objectDefinition2, objectDefinition1);
		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			objectDefinition3, objectDefinition1);
		ObjectRelationship objectRelationship3 = _addObjectRelationship(
			objectDefinition4, objectDefinition2);
		ObjectRelationship objectRelationship4 = _addObjectRelationship(
			objectDefinition4, objectDefinition3);

		_runUpgrade();

		_assertObjectRelationshipEdge(
			true, objectRelationship1.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, objectRelationship2.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, objectRelationship3.getObjectRelationshipId());
		_assertObjectRelationshipEdge(
			true, objectRelationship4.getObjectRelationshipId());

		long objectDefinitionId1 = objectDefinition1.getObjectDefinitionId();

		_assertRootObjectDefinitionIds(objectDefinitionId1, objectDefinition2);
		_assertRootObjectDefinitionIds(objectDefinitionId1, objectDefinition3);
		_assertRootObjectDefinitionIds(objectDefinitionId1, objectDefinition4);
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0." +
			"CMSObjectRelationshipEdgeUpgradeProcess";

	private ObjectFolder _cmsContentStructuresObjectFolder;
	private ObjectFolder _cmsFileTypesObjectFolder;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}