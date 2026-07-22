/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.model.listener.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class DLFileEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, ServiceContextTestUtil.getServiceContext());
		_group = DSRTestUtil.getOrAddGroup();
		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());
	}

	@Test
	public void testOnBeforeCreate() throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToDSRRooms_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		DLFolder dlFolder =
			_dlFolderLocalService.getDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				group.getGroupId());

		DLFileEntry dlFileEntry = _addFileEntry(dlFolder.getFolderId(), group);

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				DLFileEntry.class.getName()),
			ResourceAction::getActionId, String.class);
		Role role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.OWNER);

		_assertHasResourcePermissions(
			actionIds, dlFileEntry.getFileEntryId(),
			Arrays.asList(
				ArrayUtil.filter(
					actionIds,
					actionId -> !Objects.equals(
						actionId, ActionKeys.PERMISSIONS))),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		_assertHasResourcePermissions(
			actionIds, dlFileEntry.getFileEntryId(),
			Arrays.asList(
				ActionKeys.ADD_DISCUSSION, ActionKeys.DOWNLOAD,
				ActionKeys.SUBSCRIBE, ActionKeys.VIEW),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_OWNER);

		_assertHasResourcePermissions(
			actionIds, dlFileEntry.getFileEntryId(), Arrays.asList(actionIds),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), "DSR Content Contributor");

		_assertHasResourcePermissions(
			actionIds, dlFileEntry.getFileEntryId(),
			Arrays.asList(
				ActionKeys.ADD_DISCUSSION, ActionKeys.DOWNLOAD,
				ActionKeys.SUBSCRIBE, ActionKeys.UPDATE, ActionKeys.VIEW),
			role.getRoleId());

		dlFileEntry = _addFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, group);

		_assertHasResourcePermissions(
			actionIds, dlFileEntry.getFileEntryId(), Arrays.asList(),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		_assertHasResourcePermissions(
			actionIds, dlFileEntry.getFileEntryId(), Arrays.asList(),
			role.getRoleId());
	}

	private DLFileEntry _addFileEntry(long folderId, Group group)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _dlFileEntryLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				group.getGroupId(), folderId, RandomTestUtil.randomString(),
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, null,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
				null, null,
				new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
				TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
				serviceContext);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _assertHasResourcePermissions(
			String[] actionIds, long dlFileEntryId, List<String> roleActionIds,
			long roleId)
		throws Exception {

		for (String actionId : actionIds) {
			if (roleActionIds.contains(actionId)) {
				Assert.assertTrue(
					_resourcePermissionLocalService.hasResourcePermission(
						TestPropsValues.getCompanyId(),
						DLFileEntry.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(dlFileEntryId), roleId, actionId));
			}
			else {
				Assert.assertFalse(
					_resourcePermissionLocalService.hasResourcePermission(
						TestPropsValues.getCompanyId(),
						DLFileEntry.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(dlFileEntryId), roleId, actionId));
			}
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}