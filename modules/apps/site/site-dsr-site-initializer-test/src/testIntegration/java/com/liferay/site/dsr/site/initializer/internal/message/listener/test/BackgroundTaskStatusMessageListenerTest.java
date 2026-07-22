/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.message.listener.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class BackgroundTaskStatusMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DSRTestUtil.getOrAddGroup();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, ServiceContextTestUtil.getServiceContext());
		_layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					"L_DSR_LAYOUT_SET_PROTOTYPE",
					TestPropsValues.getCompanyId());
		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());
	}

	@Test
	public void testReceive() throws Exception {
		boolean active = CacheRegistryUtil.isActive();

		try {
			CacheRegistryUtil.setActive(false);

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"name", "A" + RandomTestUtil.randomString()
				).put(
					"r_accountToDSRRooms_accountEntryId",
					_accountEntry.getAccountEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Group group = _layoutSetPrototype.getGroup();

			Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
				DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_SELLER,
				TestPropsValues.getCompanyId());

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(group.getDefaultPrivatePlid()),
					role.getRoleId(), ActionKeys.VIEW));

			group = _groupLocalService.fetchGroup(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

			Assert.assertFalse(
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(group.getDefaultPublicPlid()),
					role.getRoleId(), ActionKeys.VIEW));

			DLFolder dlFolder =
				_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
					DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
					group.getGroupId());

			_assertHasResourcePermission(
				ActionKeys.ADD_DOCUMENT, true,
				String.valueOf(dlFolder.getFolderId()),
				_roleLocalService.fetchRoleByExternalReferenceCode(
					DSRRoleConstants.
						EXTERNAL_REFERENCE_CODE_DSR_CONTENT_CONTRIBUTOR,
					TestPropsValues.getCompanyId()));
			_assertHasResourcePermission(
				ActionKeys.ADD_DOCUMENT, true,
				String.valueOf(dlFolder.getFolderId()),
				_roleLocalService.fetchRoleByExternalReferenceCode(
					DSRRoleConstants.
						EXTERNAL_REFERENCE_CODE_DSR_ROOM_COLLABORATOR,
					TestPropsValues.getCompanyId()));
			_assertHasResourcePermission(
				ActionKeys.ADD_DOCUMENT, false,
				String.valueOf(dlFolder.getFolderId()),
				_roleLocalService.fetchRole(
					TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER));
		}
		finally {
			CacheRegistryUtil.setActive(active);
		}
	}

	private void _assertHasResourcePermission(
			String actionId, boolean expected, String primKey, Role role)
		throws Exception {

		Assert.assertEquals(
			expected,
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, primKey, role.getRoleId(),
				actionId));
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private LayoutSetPrototype _layoutSetPrototype;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}