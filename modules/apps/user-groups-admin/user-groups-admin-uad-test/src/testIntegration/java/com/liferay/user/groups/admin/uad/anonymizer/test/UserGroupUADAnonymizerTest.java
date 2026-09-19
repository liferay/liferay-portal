/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.uad.anonymizer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.test.util.BaseUADAnonymizerTestCase;
import com.liferay.user.groups.admin.uad.test.UserGroupUADTestHelper;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class UserGroupUADAnonymizerTest
	extends BaseUADAnonymizerTestCase<UserGroup> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		_userGroupUADTestHelper.cleanUpDependencies(_userGroups);
	}

	@Override
	protected UserGroup addBaseModel(long userId) throws Exception {
		return addBaseModel(userId, true);
	}

	@Override
	protected UserGroup addBaseModel(long userId, boolean deleteAfterTestRun)
		throws Exception {

		UserGroup userGroup = _userGroupUADTestHelper.addUserGroup(userId);

		if (deleteAfterTestRun) {
			_userGroups.add(userGroup);
		}

		return userGroup;
	}

	@Override
	protected void deleteBaseModels(List<UserGroup> baseModels)
		throws Exception {

		_userGroupUADTestHelper.cleanUpDependencies(baseModels);
	}

	@Override
	protected UADAnonymizer<UserGroup> getUADAnonymizer() {
		return _uadAnonymizer;
	}

	@Override
	protected boolean isBaseModelAutoAnonymized(long baseModelPK, User user)
		throws Exception {

		UserGroup userGroup = _userGroupLocalService.getUserGroup(baseModelPK);

		String userName = userGroup.getUserName();

		if ((userGroup.getUserId() != user.getUserId()) &&
			!userName.equals(user.getFullName())) {

			return true;
		}

		return false;
	}

	@Override
	protected boolean isBaseModelDeleted(long baseModelPK) {
		if (_userGroupLocalService.fetchUserGroup(baseModelPK) == null) {
			return true;
		}

		return false;
	}

	@Inject(
		filter = "component.name=com.liferay.user.groups.admin.uad.anonymizer.UserGroupUADAnonymizer"
	)
	private UADAnonymizer<UserGroup> _uadAnonymizer;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupUADTestHelper _userGroupUADTestHelper;

	@DeleteAfterTestRun
	private final List<UserGroup> _userGroups = new ArrayList<>();

}