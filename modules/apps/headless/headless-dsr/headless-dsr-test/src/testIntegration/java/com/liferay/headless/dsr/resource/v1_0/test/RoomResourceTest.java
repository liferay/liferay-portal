/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.headless.dsr.client.dto.v1_0.Room;
import com.liferay.headless.dsr.client.problem.Problem;
import com.liferay.headless.dsr.client.resource.v1_0.RoomResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class RoomResourceTest extends BaseRoomResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		DSRTestUtil.getOrAddGroup();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, ServiceContextTestUtil.getServiceContext());
		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());
	}

	@Override
	@Test
	public void testPostRoomDuplicate() throws Exception {
		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name",
				StringUtil.toLowerCase("A" + RandomTestUtil.randomString())
			).put(
				"r_accountToDSRRooms_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group1 = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry1.getObjectEntryId());

		DLFolder dlFolder1 =
			_dlFolderLocalService.getDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				group1.getGroupId());

		DLFileEntry dlFileEntry1 = _addFileEntry(
			dlFolder1.getFolderId(), group1);

		Room room = roomResource.postRoomDuplicate(
			objectEntry1.getObjectEntryId(),
			new Room() {
				{
					setFileEntryIds(new Long[] {dlFileEntry1.getFileEntryId()});
					setName(
						StringUtil.toLowerCase(
							"A" + RandomTestUtil.randomString()));
				}
			});

		Assert.assertTrue(room.getGroupId() > 0);
		Assert.assertTrue(room.getId() != objectEntry1.getObjectEntryId());

		DLFolder dlFolder2 =
			_dlFolderLocalService.getDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				room.getGroupId());

		Assert.assertTrue(
			ListUtil.exists(
				_dlFileEntryLocalService.getFileEntries(
					room.getGroupId(), dlFolder2.getFolderId()),
				dlFileEntry2 -> Objects.equals(
					dlFileEntry2.getTitle(), dlFileEntry1.getTitle())));

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[0], ServiceContextTestUtil.getServiceContext());

		objectEntry1 = _objectEntryLocalService.getObjectEntry(
			objectEntry1.getObjectEntryId());

		objectEntry1.setUserId(user.getUserId());

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(objectEntry1);

		RoomResource roomResource = RoomResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		try {
			roomResource.postRoomDuplicate(
				objectEntry1.getObjectEntryId(),
				new Room() {
					{
						setName(
							StringUtil.toLowerCase(
								"A" + RandomTestUtil.randomString()));
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			String message = problemException.getMessage();

			Assert.assertTrue(message, message.contains("Forbidden"));
		}

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), DSRRoleConstants.NAME_DSR_SELLER);

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});

		room = roomResource.postRoomDuplicate(
			objectEntry1.getObjectEntryId(),
			new Room() {
				{
					setName(
						StringUtil.toLowerCase(
							"A" + RandomTestUtil.randomString()));
				}
			});

		Assert.assertTrue(room.getGroupId() > 0);
		Assert.assertTrue(room.getId() != objectEntry1.getObjectEntryId());
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

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}