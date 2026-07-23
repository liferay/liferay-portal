/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.internal.service.test.util.CMSObjectEntryTestUtil;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Attila Bakay
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class TrashEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(_cmsGroup.getGroupId()));

		_updateTrashEntriesMaxAge(_depotEntry.getGroup(), 5);

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())),
			ObjectDefinitionConstants.SCOPE_DEPOT);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);
	}

	@Test
	@TestInfo({"LPD-89104", "LPD-93315"})
	public void testCheckEntriesWithExpiredObjectEntry() throws Exception {
		ObjectEntry expiredObjectEntry = CMSObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _objectDefinition,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
		ObjectEntry objectEntry = CMSObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _objectDefinition,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		CMSObjectEntryTestUtil.moveObjectEntryToTrash(
			_depotEntry.getGroupId(), expiredObjectEntry);

		_expireTrashEntry(expiredObjectEntry);

		CMSObjectEntryTestUtil.moveObjectEntryToTrash(
			_depotEntry.getGroupId(), objectEntry);

		_trashEntryLocalService.checkEntries();

		String className = _objectDefinition.getClassName();

		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				className, expiredObjectEntry.getObjectEntryId()));
		Assert.assertNotNull(
			_trashEntryLocalService.fetchEntry(
				className, objectEntry.getObjectEntryId()));
	}

	@Test
	@TestInfo({"LPD-89104", "LPD-93315"})
	public void testDeleteEntriesForGroup() throws Exception {
		ObjectEntry objectEntry1 = CMSObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _objectDefinition,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
		ObjectEntry objectEntry2 = CMSObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _objectDefinition,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		CMSObjectEntryTestUtil.moveObjectEntryToTrash(
			_depotEntry.getGroupId(), objectEntry1);
		CMSObjectEntryTestUtil.moveObjectEntryToTrash(
			_depotEntry.getGroupId(), objectEntry2);

		String className = _objectDefinition.getClassName();

		Assert.assertNotNull(
			_trashEntryLocalService.fetchEntry(
				className, objectEntry1.getObjectEntryId()));
		Assert.assertNotNull(
			_trashEntryLocalService.fetchEntry(
				className, objectEntry2.getObjectEntryId()));

		_trashEntryLocalService.deleteEntries(_depotEntry.getGroupId(), true);

		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				className, objectEntry1.getObjectEntryId()));
		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				className, objectEntry2.getObjectEntryId()));

		List<TrashEntry> trashEntries = _trashEntryLocalService.getEntries(
			_depotEntry.getGroupId());

		Assert.assertTrue(trashEntries.toString(), trashEntries.isEmpty());
	}

	private void _expireTrashEntry(ObjectEntry objectEntry) throws Exception {
		int maxAgeMinutes = _trashHelper.getMaxAge(_depotEntry.getGroup());

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			_objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Date createDate = trashEntry.getCreateDate();

		trashEntry.setCreateDate(
			new Date(
				createDate.getTime() - (maxAgeMinutes * Time.MINUTE) -
					Time.DAY));

		_trashEntryLocalService.updateTrashEntry(trashEntry);
	}

	private void _updateTrashEntriesMaxAge(Group group, int days) {
		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"trashEntriesMaxAge", String.valueOf(days * 24 * 60));

		group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		_groupLocalService.updateGroup(group);
	}

	private Group _cmsGroup;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

	@Inject
	private TrashHelper _trashHelper;

}