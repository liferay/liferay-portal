/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.asset.library.client.dto.v1_0.UserAccount;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlag;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class UserAccountResourceTest extends BaseUserAccountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_testUser = UserTestUtil.addUser();
	}

	@Override
	protected Collection<EntityField> getEntityFields() {
		return Collections.emptyList();
	}

	@Override
	protected UserAccount randomIrrelevantUserAccount() throws Exception {
		User user = UserTestUtil.addUser();

		return userAccountResource.putAssetLibraryUserAccount(
			irrelevantDepotEntry.getGroupId(), user.getUserId());
	}

	@Override
	protected UserAccount randomUserAccount() throws Exception {
		User user = UserTestUtil.addUser();

		return userAccountResource.putAssetLibraryUserAccount(
			testDepotEntry.getGroupId(), user.getUserId());
	}

	@Override
	protected UserAccount
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return _getGroupExternalReferenceCode();
	}

	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_getUserExternalReferenceCode()
		throws Exception {

		return _testUser.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testDeleteAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	@Override
	protected Long testDeleteAssetLibraryUserAccount_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected Long testDeleteAssetLibraryUserAccount_getUserId() {
		return _testUser.getUserId();
	}

	@Override
	protected UserAccount
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return _getGroupExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_getUserExternalReferenceCode()
		throws Exception {

		return _testUser.getExternalReferenceCode();
	}

	protected UserAccount
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		return userAccountResource.putAssetLibraryUserAccount(
			testDepotEntry.getGroupId(), userAccount.getId());
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode()
		throws Exception {

		return _getGroupExternalReferenceCode();
	}

	@Override
	protected UserAccount testGetAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	@Override
	protected Long testGetAssetLibraryUserAccount_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected Long testGetAssetLibraryUserAccount_getUserId() throws Exception {
		return _testUser.getUserId();
	}

	@Override
	protected UserAccount testGetAssetLibraryUserAccountsPage_addUserAccount(
			Long assetLibraryId, UserAccount userAccount)
		throws Exception {

		return userAccountResource.putAssetLibraryUserAccount(
			assetLibraryId, userAccount.getId());
	}

	protected UserAccount
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return _getGroupExternalReferenceCode();
	}

	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode_getUserExternalReferenceCode()
		throws Exception {

		return _testUser.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testPutAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	@Override
	protected Long testPutAssetLibraryUserAccount_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected Long testPutAssetLibraryUserAccount_getUserId() throws Exception {
		return _testUser.getUserId();
	}

	private UserAccount _addUserAccount() throws Exception {
		return userAccountResource.putAssetLibraryUserAccount(
			testDepotEntry.getGroupId(), _testUser.getUserId());
	}

	private String _getGroupExternalReferenceCode() throws Exception {
		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	private User _testUser;

}