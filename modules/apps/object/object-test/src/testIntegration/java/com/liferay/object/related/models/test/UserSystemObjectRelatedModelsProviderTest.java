/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.related.models.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class UserSystemObjectRelatedModelsProviderTest
	extends BaseSystemObjectRelatedModelsProviderTestCase {

	@Test
	public void testSystemObjectEntry1toMObjectRelatedModelsWithNameTitleObjectField()
		throws Exception {

		ObjectDefinition objectDefinition = getSystemObjectDefinition();

		long originalTitleObjectFieldId =
			objectDefinition.getTitleObjectFieldId();

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "name");

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		_searchByLastName = true;

		testSystemObjectEntry1toMObjectRelatedModels();

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			originalTitleObjectFieldId);
	}

	@Test
	public void testSystemObjectEntryMtoMObjectRelatedModelsWithNameTitleObjectField()
		throws Exception {

		ObjectDefinition objectDefinition = getSystemObjectDefinition();

		long originalTitleObjectFieldId =
			objectDefinition.getTitleObjectFieldId();

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "name");

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		testSystemObjectEntryMtoMObjectRelatedModels();

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			originalTitleObjectFieldId);
	}

	@Override
	protected long[] addBaseModels(int count) throws Exception {
		long[] userIds = new long[count];

		for (int i = 0; i < count; i++) {
			User user = UserTestUtil.addUser();

			userIds[i] = user.getUserId();
		}

		return userIds;
	}

	@Override
	protected void assertFailure(long primaryKey) {
		AssertUtils.assertFailure(
			NoSuchUserException.class,
			"No User exists with the primary key " + primaryKey,
			() -> _userLocalService.getUser(primaryKey));
	}

	@Override
	protected void deleteBaseModel(long primaryKey) throws Exception {
		_userLocalService.deleteUser(primaryKey);
	}

	@Override
	protected Object fetchBaseModel(long primaryKey) {
		return _userLocalService.fetchUser(primaryKey);
	}

	@Override
	protected String getName(long primaryKey) throws Exception {
		User user = _userLocalService.getUser(primaryKey);

		if (_searchByLastName) {
			return user.getLastName();
		}

		return user.getFirstName();
	}

	@Override
	protected ObjectDefinition getSystemObjectDefinition() throws Exception {
		return _objectDefinitionLocalService.fetchObjectDefinitionByClassName(
			TestPropsValues.getCompanyId(), User.class.getName());
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private boolean _searchByLastName;

	@Inject
	private UserLocalService _userLocalService;

}