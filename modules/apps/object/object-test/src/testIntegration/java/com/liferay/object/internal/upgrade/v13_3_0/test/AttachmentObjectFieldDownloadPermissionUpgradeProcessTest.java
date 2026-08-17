/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_3_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class AttachmentObjectFieldDownloadPermissionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		_testUpgradeWithDraftObjectDefinition();
		_testUpgradeWithPublishedObjectDefinition();
		_testUpgradeWithUnmodifiableSystemObjectDefinition();
	}

	private DLFileEntry _addDLFileEntry() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			TempFileEntryUtil.getTempFileName("image.jpg"),
			ContentTypes.APPLICATION_TEXT, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()), 67, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		return _dlFileEntryLocalService.getFileEntry(
			fileEntry.getFileEntryId());
	}

	private void _assertDownloadPLOEntryValues(
			String actionId, long companyId, ObjectField objectField)
		throws Exception {

		for (Locale locale : _language.getCompanyAvailableLocales(companyId)) {
			PLOEntry ploEntry = _ploEntryLocalService.fetchPLOEntry(
				companyId, "action." + actionId,
				LocaleUtil.toLanguageId(locale));

			Assert.assertEquals(
				_language.format(
					locale, "download-x", objectField.getLabel(locale)),
				ploEntry.getValue());
		}
	}

	private void _assertDownloadResourceAction(
		String actionId, String className) {

		Assert.assertNotNull(
			_resourceActionLocalService.fetchResourceAction(
				className, actionId));
	}

	private void _assertHasResourcePermissions(
			String actionId, String className, long companyId, String primKey)
		throws Exception {

		Assert.assertFalse(
			_hasResourcePermission(
				actionId, className, companyId, primKey, RoleConstants.GUEST));
		Assert.assertFalse(
			_hasResourcePermission(
				actionId, className, companyId, primKey, RoleConstants.USER));
		Assert.assertTrue(
			_hasResourcePermission(
				actionId, className, companyId, primKey,
				RoleConstants.POWER_USER));
	}

	private void _assertNoDownloadPLOEntries(String actionId, long companyId) {
		for (Locale locale : _language.getCompanyAvailableLocales(companyId)) {
			Assert.assertNull(
				_ploEntryLocalService.fetchPLOEntry(
					companyId, "action." + actionId,
					LocaleUtil.toLanguageId(locale)));
		}
	}

	private ObjectField _createAttachmentObjectField() {
		return ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
			RandomTestUtil.randomString(), StringUtil.randomId(),
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"jpg, jpeg, png, svg, txt"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					"100"
				).build()),
			false);
	}

	private UpgradeProcess _getUpgradeProcess() throws Exception {
		return UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);
	}

	private boolean _hasResourcePermission(
			String actionId, String className, long companyId, String primKey,
			String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(companyId, roleName);

		return _resourcePermissionLocalService.hasResourcePermission(
			companyId, className, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			role.getRoleId(), actionId);
	}

	private void _setResourcePermissions(
			String[] actionIds, String className, long companyId,
			String primKey, String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(companyId, roleName);

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, className, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			role.getRoleId(), actionIds);
	}

	private void _testUpgradeWithDraftObjectDefinition() throws Exception {
		ObjectField objectField = _createAttachmentObjectField();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(objectField));

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectDefinition.getStatus());

		String actionId = objectField.getAttachmentDownloadActionKey();

		UpgradeProcess upgradeProcess = _getUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();

		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				objectDefinition.getClassName(), actionId));

		_assertNoDownloadPLOEntries(actionId, objectDefinition.getCompanyId());
	}

	private void _testUpgradeWithPublishedObjectDefinition() throws Exception {
		ObjectField objectField = _createAttachmentObjectField();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField));

		ObjectEntry objectEntry =
			_objectEntryLocalService.addOrUpdateObjectEntry(
				RandomTestUtil.randomString(), 0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					objectField.getName(),
					() -> {
						DLFileEntry dlFileEntry = _addDLFileEntry();

						return String.valueOf(dlFileEntry.getFileEntryId());
					}
				).build(),
				ServiceContextTestUtil.getServiceContext());

		String className = objectDefinition.getClassName();
		long companyId = objectDefinition.getCompanyId();

		String primKey = String.valueOf(objectEntry.getObjectEntryId());

		_setResourcePermissions(
			new String[] {ActionKeys.UPDATE}, className, companyId, primKey,
			RoleConstants.USER);
		_setResourcePermissions(
			new String[] {ActionKeys.VIEW}, className, companyId, primKey,
			RoleConstants.POWER_USER);

		String actionId = objectField.getAttachmentDownloadActionKey();

		Assert.assertFalse(
			_hasResourcePermission(
				actionId, className, companyId, primKey,
				RoleConstants.POWER_USER));

		_resourceActionLocalService.deleteResourceAction(
			_resourceActionLocalService.getResourceAction(className, actionId));

		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				className, actionId));

		for (Locale locale : _language.getCompanyAvailableLocales(companyId)) {
			_ploEntryLocalService.deletePLOEntry(
				companyId, "action." + actionId,
				LocaleUtil.toLanguageId(locale));
		}

		UpgradeProcess upgradeProcess = _getUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();

		_assertDownloadPLOEntryValues(actionId, companyId, objectField);
		_assertDownloadResourceAction(actionId, className);
		_assertHasResourcePermissions(actionId, className, companyId, primKey);

		upgradeProcess.upgrade();

		_entityCache.clearCache();

		_assertDownloadPLOEntryValues(actionId, companyId, objectField);
		_assertDownloadResourceAction(actionId, className);
		_assertHasResourcePermissions(actionId, className, companyId, primKey);
	}

	private void _testUpgradeWithUnmodifiableSystemObjectDefinition()
		throws Exception {

		ObjectDefinition accountEntryObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(),
				AccountEntry.class.getSimpleName());

		Assert.assertTrue(
			accountEntryObjectDefinition.isUnmodifiableSystemObject());

		ObjectField objectField = _createAttachmentObjectField();

		objectField.setUserId(TestPropsValues.getUserId());
		objectField.setObjectDefinitionId(
			accountEntryObjectDefinition.getObjectDefinitionId());

		objectField = ObjectFieldUtil.addCustomObjectField(objectField);

		String actionId = objectField.getAttachmentDownloadActionKey();

		UpgradeProcess upgradeProcess = _getUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();

		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				accountEntryObjectDefinition.getClassName(), actionId));

		_assertNoDownloadPLOEntries(
			actionId, accountEntryObjectDefinition.getCompanyId());

		_objectFieldLocalService.deleteObjectField(
			objectField.getObjectFieldId());
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v13_3_0." +
			"AttachmentObjectFieldDownloadPermissionUpgradeProcess";

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private Language _language;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}