/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.asset.library.client.dto.v1_0.UserGroup;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
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
	protected UserGroup
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup()
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntry.getGroupId(), _testUserGroup.getId());
	}

	@Override
	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup testDeleteAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntry.getGroupId(), _testUserGroup.getId());
	}

	@Override
	protected Long testDeleteAssetLibraryUserGroup_getAssetLibraryId() {
		return testDepotEntry.getGroupId();
	}

	@Override
	protected UserGroup
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup()
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntry.getGroupId(), _testUserGroup.getId());
	}

	@Override
	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				String externalReferenceCode, UserGroup userGroup)
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntry.getGroupId(), userGroup.getId());
	}

	@Override
	protected String
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup testGetAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			testDepotEntry.getGroupId(), _testUserGroup.getId());
	}

	@Override
	protected Long testGetAssetLibraryUserGroup_getAssetLibraryId() {
		return testDepotEntry.getGroupId();
	}

	@Override
	protected UserGroup testGetAssetLibraryUserGroupsPage_addUserGroup(
			Long assetLibraryId, UserGroup userGroup)
		throws Exception {

		return userGroupResource.putAssetLibraryUserGroup(
			assetLibraryId, userGroup.getId());
	}

	@Override
	protected UserGroup
		testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup() {

		return _testUserGroup;
	}

	@Override
	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected UserGroup testPutAssetLibraryUserGroup_addUserGroup() {
		return _testUserGroup;
	}

	@Override
	protected Long testPutAssetLibraryUserGroup_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getGroupId();
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

}