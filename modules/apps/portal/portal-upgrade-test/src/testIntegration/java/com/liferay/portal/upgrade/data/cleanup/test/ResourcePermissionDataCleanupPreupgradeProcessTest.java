/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.permission.ResourceActionsImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.ResourcePermissionDataCleanupPreupgradeProcess;

import java.io.Serializable;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class ResourcePermissionDataCleanupPreupgradeProcessTest
	extends ResourcePermissionDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();

		Role ownerRole = RoleLocalServiceUtil.getRole(_companyId, "Owner");

		_ownerRoleId = ownerRole.getRoleId();
	}

	@Test
	public void testUpgrade() throws Exception {
		ResourceActionsImpl resourceActionsImpl = new ResourceActionsImpl();

		String compositeClassName = resourceActionsImpl.getCompositeModelName(
			DDMStructure.class.getName(), JournalArticle.class.getName());

		long compositeClassNameId = RandomTestUtil.nextLong();

		_setResourcePermission(
			compositeClassName, String.valueOf(compositeClassNameId));

		Assert.assertTrue(
			_hasResourcePermission(
				compositeClassName, String.valueOf(compositeClassNameId)));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		Assert.assertTrue(
			_hasResourcePermission(
				JournalArticle.class.getName(),
				String.valueOf(journalArticle.getResourcePrimKey())));

		runSQL(
			"delete from JournalArticle where articleId = '" +
				journalArticle.getArticleId() + "'");

		long layoutId = RandomTestUtil.nextLong();

		_setResourcePermission(
			Layout.class.getName(), String.valueOf(layoutId));

		Assert.assertTrue(
			_hasResourcePermission(
				Layout.class.getName(), String.valueOf(layoutId)));

		long ctCollectionId = RandomTestUtil.nextLong();

		_setResourcePermission(
			CTCollection.class.getName(), String.valueOf(ctCollectionId));

		Assert.assertTrue(
			_hasResourcePermission(
				CTCollection.class.getName(), String.valueOf(ctCollectionId)));

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"First Name", "firstName", true)));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(
			_hasResourcePermission(
				objectDefinition.getClassName(),
				String.valueOf(objectEntry.getObjectEntryId())));

		runSQL(
			StringBundler.concat(
				"delete from ", objectDefinition.getDBTableName(), " where ",
				objectDefinition.getPKObjectFieldDBColumnName(), " = ",
				objectEntry.getObjectEntryId()));

		String nonliferayClassName =
			"com.test." + RandomTestUtil.randomString();
		long nonliferayClassNameId = RandomTestUtil.nextLong();

		_resourceActionLocalService.addResourceAction(
			nonliferayClassName, ActionKeys.VIEW, 1L);

		_setResourcePermission(
			nonliferayClassName, String.valueOf(nonliferayClassNameId));

		Assert.assertTrue(
			_hasResourcePermission(
				nonliferayClassName, String.valueOf(nonliferayClassNameId)));

		upgrade();

		CacheRegistryUtil.clear();

		Assert.assertFalse(
			_hasResourcePermission(
				JournalArticle.class.getName(),
				String.valueOf(journalArticle.getResourcePrimKey())));
		Assert.assertFalse(
			_hasResourcePermission(
				Layout.class.getName(), String.valueOf(layoutId)));
		Assert.assertFalse(
			_hasResourcePermission(
				CTCollection.class.getName(), String.valueOf(ctCollectionId)));
		Assert.assertFalse(
			_hasResourcePermission(
				compositeClassName, String.valueOf(compositeClassNameId)));
		Assert.assertTrue(
			_hasResourcePermission(
				nonliferayClassName, String.valueOf(nonliferayClassNameId)));
		Assert.assertFalse(
			_hasResourcePermission(
				objectDefinition.getClassName(),
				String.valueOf(objectEntry.getObjectEntryId())));
	}

	private boolean _hasResourcePermission(String className, String primKey)
		throws Exception {

		return _resourcePermissionLocalService.hasResourcePermission(
			_companyId, className, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			_ownerRoleId, ActionKeys.VIEW);
	}

	private void _setResourcePermission(String className, String primKey)
		throws Exception {

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			_companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(primKey), _ownerRoleId,
			new String[] {ActionKeys.VIEW});
	}

	private long _companyId;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private long _ownerRoleId;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}