/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.lar.BaseCompanyStagedModelDataHandlerTestCase;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author David Mendez Gonzalez
 */
@RunWith(Arquillian.class)
public class UserGroupStagedModelDataHandlerTest
	extends BaseCompanyStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_userGroup != null) {
			_userGroup =
				UserGroupLocalServiceUtil.fetchUserGroupByUuidAndCompanyId(
					_userGroup.getUuid(), _userGroup.getCompanyId());
		}
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		_userGroup = UserGroupTestUtil.addUserGroup();

		return _userGroup;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return UserGroupLocalServiceUtil.getUserGroupByUuidAndCompanyId(
			uuid, group.getCompanyId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return UserGroup.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		// super.validateImportedStagedModel(stagedModel, importedStagedModel);

		UserGroup userGroup = (UserGroup)stagedModel;
		UserGroup importedUserGroup = (UserGroup)importedStagedModel;

		Assert.assertEquals(userGroup.getName(), importedUserGroup.getName());
		Assert.assertEquals(
			userGroup.getDescription(), importedUserGroup.getDescription());
		Assert.assertEquals(
			userGroup.isAddedByLDAPImport(),
			importedUserGroup.isAddedByLDAPImport());
	}

	@DeleteAfterTestRun
	private UserGroup _userGroup;

}