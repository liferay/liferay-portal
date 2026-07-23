/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.asset.library.client.dto.v1_0.UserGroup;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.resource.v1_0.UserGroupResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class UserGroupResourceTest extends BaseUserGroupResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_testUserGroup = _addUserGroup();
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() {
	}

	@Override
	@Test
	public void testGetAssetLibraryUserGroupsPage() throws Exception {
		super.testGetAssetLibraryUserGroupsPage();

		Page<UserGroup> adminUserUserGroupPage =
			userGroupResource.getAssetLibraryUserGroupsPage(
				testGetAssetLibraryUserGroupsPage_getAssetLibraryExternalReferenceCode(),
				StringPool.BLANK, StringPool.BLANK, Pagination.of(1, 0),
				StringPool.BLANK);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {testDepotEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		_userLocalService.addGroupUser(testDepotEntry.getGroupId(), user);

		UserGroupResource userUserGroupResource = UserGroupResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		Page<UserGroup> userUserGroupPage =
			userUserGroupResource.getAssetLibraryUserGroupsPage(
				testGetAssetLibraryUserGroupsPage_getAssetLibraryExternalReferenceCode(),
				StringPool.BLANK, StringPool.BLANK, Pagination.of(1, 0),
				StringPool.BLANK);

		Assert.assertEquals(
			adminUserUserGroupPage.getTotalCount(),
			userUserGroupPage.getTotalCount());
	}

	@Override
	protected Collection<EntityField> getEntityFields() {
		return Collections.emptyList();
	}

	@Override
	protected UserGroup randomIrrelevantUserGroup() throws Exception {
		return _addUserGroup();
	}

	@Override
	protected UserGroup randomUserGroup() throws Exception {
		return _addUserGroup();
	}

	@Override
	protected UserGroup testDeleteAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntryGroup.getExternalReferenceCode(),
			_testUserGroup.getExternalReferenceCode());
	}

	@Override
	protected String
			testDeleteAssetLibraryUserGroup_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup testGetAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntryGroup.getExternalReferenceCode(),
			_testUserGroup.getExternalReferenceCode());
	}

	@Override
	protected String
			testGetAssetLibraryUserGroup_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup testGetAssetLibraryUserGroupsPage_addUserGroup(
			String assetLibraryExternalReferenceCode, UserGroup userGroup)
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			assetLibraryExternalReferenceCode,
			userGroup.getExternalReferenceCode());
	}

	@Override
	protected String
			testGetAssetLibraryUserGroupsPage_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup testPutAssetLibraryUserGroup_addUserGroup() {
		return _testUserGroup;
	}

	@Override
	protected String
			testPutAssetLibraryUserGroup_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	private UserGroup _addUserGroup() throws Exception {
		com.liferay.portal.kernel.model.UserGroup userGroup =
			_userGroupLocalService.addUserGroup(
				null, TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		_userGroups.add(userGroup);

		return new UserGroup() {
			{
				externalReferenceCode = userGroup.getExternalReferenceCode();
				id = userGroup.getUserGroupId();
				name = userGroup.getName();
			}
		};
	}

	private UserGroup _testUserGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@DeleteAfterTestRun
	private List<com.liferay.portal.kernel.model.UserGroup> _userGroups =
		new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

}