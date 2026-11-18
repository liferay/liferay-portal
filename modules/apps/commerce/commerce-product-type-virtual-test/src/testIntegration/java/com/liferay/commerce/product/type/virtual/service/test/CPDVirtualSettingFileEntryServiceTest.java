/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPDVirtualSettingFileEntryServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddCPDefinitionVirtualSetting() throws Exception {
		CommerceCatalog commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog1.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog1.getCommerceCatalogId()),
			_role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting1 =
			_addCPDefinitionVirtualSetting(commerceCatalog1);
		FileEntry fileEntry1 = _addFileEntry(commerceCatalog1);

		CommerceCatalog commerceCatalog2 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting2 =
			_addCPDefinitionVirtualSetting(commerceCatalog2);

		FileEntry fileEntry2 = _addFileEntry(GroupTestUtil.addGroup());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_assertMustHavePermission(
				ActionKeys.UPDATE, commerceCatalog2.getCommerceCatalogId(),
				CommerceCatalog.class.getName(),
				() ->
					_cpdVirtualSettingFileEntryService.
						addCPDefinitionVirtualSetting(
							commerceCatalog1.getGroupId(),
							cpDefinitionVirtualSetting1.getClassName(),
							cpDefinitionVirtualSetting1.getClassPK(),
							cpDefinitionVirtualSetting2.
								getCPDefinitionVirtualSettingId(),
							fileEntry1.getFileEntryId(), null,
							RandomTestUtil.randomString()));
			_assertMustHavePermission(
				ActionKeys.VIEW, fileEntry2.getFileEntryId(),
				DLFileEntry.class.getName(),
				() ->
					_cpdVirtualSettingFileEntryService.
						addCPDefinitionVirtualSetting(
							commerceCatalog1.getGroupId(),
							cpDefinitionVirtualSetting1.getClassName(),
							cpDefinitionVirtualSetting1.getClassPK(),
							cpDefinitionVirtualSetting1.
								getCPDefinitionVirtualSettingId(),
							fileEntry2.getFileEntryId(), null,
							RandomTestUtil.randomString()));

			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
				_cpdVirtualSettingFileEntryService.
					addCPDefinitionVirtualSetting(
						commerceCatalog1.getGroupId(),
						cpDefinitionVirtualSetting1.getClassName(),
						cpDefinitionVirtualSetting1.getClassPK(),
						cpDefinitionVirtualSetting1.
							getCPDefinitionVirtualSettingId(),
						fileEntry1.getFileEntryId(), null,
						RandomTestUtil.randomString());

			Assert.assertEquals(
				fileEntry1.getFileEntryId(),
				cpdVirtualSettingFileEntry.getFileEntryId());
		}
	}

	@Test
	public void testDeleteCPDVirtualSettingFileEntry() throws Exception {
		CommerceCatalog commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog1.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog1.getCommerceCatalogId()),
			_role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting1 =
			_addCPDefinitionVirtualSetting(commerceCatalog1);

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry1 =
			_addCPDVirtualSettingFileEntry(
				cpDefinitionVirtualSetting1, _addFileEntry(commerceCatalog1));

		CommerceCatalog commerceCatalog2 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry2 =
			_addCPDVirtualSettingFileEntry(
				_addCPDefinitionVirtualSetting(commerceCatalog2),
				_addFileEntry(commerceCatalog2));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_assertMustHavePermission(
				ActionKeys.UPDATE, commerceCatalog2.getCommerceCatalogId(),
				CommerceCatalog.class.getName(),
				() ->
					_cpdVirtualSettingFileEntryService.
						deleteCPDVirtualSettingFileEntry(
							cpDefinitionVirtualSetting1.getClassName(),
							cpDefinitionVirtualSetting1.getClassPK(),
							cpdVirtualSettingFileEntry2.
								getCPDefinitionVirtualSettingFileEntryId()));

			Assert.assertNotNull(
				_cpdVirtualSettingFileEntryLocalService.
					fetchCPDVirtualSettingFileEntry(
						cpdVirtualSettingFileEntry2.
							getCPDefinitionVirtualSettingFileEntryId()));

			_cpdVirtualSettingFileEntryService.deleteCPDVirtualSettingFileEntry(
				cpDefinitionVirtualSetting1.getClassName(),
				cpDefinitionVirtualSetting1.getClassPK(),
				cpdVirtualSettingFileEntry1.
					getCPDefinitionVirtualSettingFileEntryId());

			Assert.assertNull(
				_cpdVirtualSettingFileEntryLocalService.
					fetchCPDVirtualSettingFileEntry(
						cpdVirtualSettingFileEntry1.
							getCPDefinitionVirtualSettingFileEntryId()));
		}
	}

	@Test
	public void testUpdateCPDefinitionVirtualSetting() throws Exception {
		CommerceCatalog commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog1.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog1.getCommerceCatalogId()),
			_role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_addCPDVirtualSettingFileEntry(
				_addCPDefinitionVirtualSetting(commerceCatalog1),
				_addFileEntry(commerceCatalog1));

		FileEntry fileEntry1 = _addFileEntry(commerceCatalog1);

		FileEntry fileEntry2 = _addFileEntry(GroupTestUtil.addGroup());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_assertMustHavePermission(
				ActionKeys.VIEW, fileEntry2.getFileEntryId(),
				DLFileEntry.class.getName(),
				() ->
					_cpdVirtualSettingFileEntryService.
						updateCPDefinitionVirtualSetting(
							cpdVirtualSettingFileEntry.
								getCPDefinitionVirtualSettingFileEntryId(),
							fileEntry2.getFileEntryId(), null,
							RandomTestUtil.randomString()));

			CPDVirtualSettingFileEntry updatedCPDVirtualSettingFileEntry =
				_cpdVirtualSettingFileEntryService.
					updateCPDefinitionVirtualSetting(
						cpdVirtualSettingFileEntry.
							getCPDefinitionVirtualSettingFileEntryId(),
						fileEntry1.getFileEntryId(), null,
						RandomTestUtil.randomString());

			Assert.assertEquals(
				fileEntry1.getFileEntryId(),
				updatedCPDVirtualSettingFileEntry.getFileEntryId());
		}
	}

	private CPDVirtualSettingFileEntry _addCPDVirtualSettingFileEntry(
			CPDefinitionVirtualSetting cpDefinitionVirtualSetting,
			FileEntry fileEntry)
		throws Exception {

		return _cpdVirtualSettingFileEntryLocalService.
			addCPDVirtualSettingFileEntry(
				TestPropsValues.getUserId(),
				cpDefinitionVirtualSetting.getGroupId(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				fileEntry.getFileEntryId(), null,
				RandomTestUtil.randomString());
	}

	private CPDefinitionVirtualSetting _addCPDefinitionVirtualSetting(
			CommerceCatalog commerceCatalog)
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		return _cpDefinitionVirtualSettingLocalService.
			addCPDefinitionVirtualSetting(
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				0, null, RandomTestUtil.randomInt(),
				RandomTestUtil.randomLong(), RandomTestUtil.randomInt(), false,
				0, null, false, null, 0, false,
				ServiceContextTestUtil.getServiceContext(
					cpDefinition.getGroupId()));
	}

	private FileEntry _addFileEntry(CommerceCatalog commerceCatalog)
		throws Exception {

		return _cpdVirtualSettingFileEntryLocalService.addFileEntry(
			TestPropsValues.getUserId(), commerceCatalog.getGroupId(),
			CommerceCatalog.class.getName(),
			commerceCatalog.getCommerceCatalogId(),
			CPConstants.SERVICE_NAME_PRODUCT,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()),
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN);
	}

	private FileEntry _addFileEntry(Group group) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(group.getGroupId());

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);
	}

	private void _assertMustHavePermission(
		String actionId, long resourceId, String resourceName,
		ThrowingRunnable throwingRunnable) {

		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class, throwingRunnable);

		Assert.assertArrayEquals(
			new String[] {actionId}, mustHavePermission.actionId);
		Assert.assertEquals(resourceId, mustHavePermission.resourceId);
		Assert.assertEquals(resourceName, mustHavePermission.resourceName);
	}

	@Inject
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Inject
	private CPDVirtualSettingFileEntryLocalService
		_cpdVirtualSettingFileEntryLocalService;

	@Inject
	private CPDVirtualSettingFileEntryService
		_cpdVirtualSettingFileEntryService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}