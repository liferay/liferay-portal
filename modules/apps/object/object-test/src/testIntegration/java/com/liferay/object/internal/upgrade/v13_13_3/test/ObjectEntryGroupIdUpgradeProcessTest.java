/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_13_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ObjectEntryGroupIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(), new HashMap<>());

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(), new HashMap<>());

		_objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry2,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				TestPropsValues.getGroupId());

		_moveObjectEntry(objectEntry1, objectEntryFolder);
		_moveObjectEntry(objectEntry2, objectEntryFolder);

		ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			Collections.singletonMap(
				"externalReferenceCode",
				objectEntry2.getExternalReferenceCode()));

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.object.internal.upgrade.v13_13_3." +
				"ObjectEntryGroupIdUpgradeProcess");

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();

		_assertObjectEntry(
			objectEntry1.getExternalReferenceCode(), objectEntry1);

		ObjectEntry restoredObjectEntry2 =
			_objectEntryLocalService.getObjectEntry(
				objectEntry2.getObjectEntryId());

		Assert.assertNotEquals(
			objectEntry2.getExternalReferenceCode(),
			restoredObjectEntry2.getExternalReferenceCode());

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			_objectDefinition.getClassName(), objectEntry2.getObjectEntryId());

		Assert.assertEquals(0, trashEntry.getGroupId());
		Assert.assertEquals(
			"0", trashEntry.getTypeSettingsProperty("objectEntryFolderId"));
	}

	@Test
	public void testUpgradeWithFriendlyURLEntries() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectDefinition.setFriendlyURLSeparator(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		ObjectEntry objectEntry2 = _addObjectEntry(
			RandomTestUtil.randomString());

		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry3 = _addObjectEntry(title);

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				TestPropsValues.getGroupId());

		_moveObjectEntry(objectEntry2, objectEntryFolder);

		_updateObjectEntry(objectEntry2, objectEntryFolder, title);

		FriendlyURLEntry friendlyURLEntry3 = _getMainFriendlyURLEntry(
			objectEntry3);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.object.internal.upgrade.v13_13_3." +
				"ObjectEntryGroupIdUpgradeProcess");

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();

		_assertMainFriendlyURLEntry(
			friendlyURLEntry3.getUrlTitle() + "-1", objectEntry2);
	}

	private ObjectEntry _addObjectEntry(String title) throws Exception {
		return ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", title
			).build());
	}

	private void _assertMainFriendlyURLEntry(
			String expectedUrlTitle, ObjectEntry objectEntry)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry = _getMainFriendlyURLEntry(
			objectEntry);

		Company company = _companyLocalService.getCompany(
			objectEntry.getCompanyId());

		Assert.assertEquals(
			company.getGroupId(), friendlyURLEntry.getGroupId());

		Assert.assertEquals(expectedUrlTitle, friendlyURLEntry.getUrlTitle());

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				TestPropsValues.getGroupId(),
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

		Assert.assertEquals(
			friendlyURLEntries.toString(), 0, friendlyURLEntries.size());
	}

	private void _assertObjectEntry(
			String expectedExternalReferenceCode, ObjectEntry objectEntry)
		throws Exception {

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			expectedExternalReferenceCode,
			objectEntry.getExternalReferenceCode());
		Assert.assertEquals(0, objectEntry.getGroupId());
		Assert.assertEquals(0, objectEntry.getObjectEntryFolderId());
		Assert.assertEquals("/", objectEntry.getTreePath());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			_objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Company company = _companyLocalService.getCompany(
			objectEntry.getCompanyId());

		Assert.assertEquals(company.getGroupId(), assetEntry.getGroupId());
	}

	private FriendlyURLEntry _getMainFriendlyURLEntry(ObjectEntry objectEntry)
		throws Exception {

		return _friendlyURLEntryLocalService.getMainFriendlyURLEntry(
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());
	}

	private void _moveObjectEntry(
			ObjectEntry objectEntry, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"update ObjectEntry set groupId = ?, objectEntryFolderId = " +
					"?, treePath = ? where objectEntryId = ?");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"update AssetEntry set groupId = ? where classPK = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"update TrashEntry set groupId = ?, typeSettings = ? where " +
					"classPK = ?")) {

			preparedStatement1.setLong(1, objectEntryFolder.getGroupId());
			preparedStatement1.setLong(
				2, objectEntryFolder.getObjectEntryFolderId());
			preparedStatement1.setString(3, objectEntryFolder.getTreePath());
			preparedStatement1.setLong(4, objectEntry.getObjectEntryId());

			preparedStatement1.executeUpdate();

			preparedStatement2.setLong(1, objectEntryFolder.getGroupId());
			preparedStatement2.setLong(2, objectEntry.getObjectEntryId());

			preparedStatement2.executeUpdate();

			preparedStatement3.setLong(1, objectEntryFolder.getGroupId());
			preparedStatement3.setString(
				2,
				UnicodePropertiesBuilder.put(
					"objectEntryFolderId",
					objectEntryFolder.getObjectEntryFolderId()
				).buildString());
			preparedStatement3.setLong(3, objectEntry.getObjectEntryId());

			preparedStatement3.executeUpdate();
		}

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();
	}

	private void _updateObjectEntry(
			ObjectEntry objectEntry, ObjectEntryFolder objectEntryFolder,
			String title)
		throws Exception {

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntryFolder.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"able", title
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}