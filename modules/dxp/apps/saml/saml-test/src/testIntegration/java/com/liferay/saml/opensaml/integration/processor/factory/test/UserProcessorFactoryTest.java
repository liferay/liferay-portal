/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.processor.factory.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.UserFieldExpressionHandlerRegistry;
import com.liferay.saml.opensaml.integration.processor.UserProcessor;
import com.liferay.saml.opensaml.integration.processor.factory.UserProcessorFactory;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class UserProcessorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testMembershipUserGroups() throws Exception {
		_user = UserTestUtil.addUser();
		_userGroup = UserGroupTestUtil.addUserGroup();

		_user = _processUser(
			HashMapBuilder.put(
				"membership:userGroups", new String[] {_userGroup.getName()}
			).build());

		Assert.assertEquals(
			1,
			_userGroupLocalService.getUserUserGroupsCount(_user.getUserId()));

		_user = _processUser(Collections.emptyMap());

		Assert.assertEquals(
			1,
			_userGroupLocalService.getUserUserGroupsCount(_user.getUserId()));

		_user = _processUser(
			HashMapBuilder.put(
				"membership:userGroups", new String[0]
			).build());

		Assert.assertEquals(
			0,
			_userGroupLocalService.getUserUserGroupsCount(_user.getUserId()));
	}

	private User _processUser(Map<String, String[]> attributesMap)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_user.getCompanyId(), _user.getGroupId(), _user.getUserId());

		UserProcessor userProcessor = _userProcessorFactory.create(
			_user, _userFieldExpressionHandlerRegistry);

		for (Map.Entry<String, String[]> attribute : attributesMap.entrySet()) {
			userProcessor.setValueArray(
				attribute.getKey(), attribute.getValue());
		}

		return userProcessor.process(serviceContext);
	}

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserFieldExpressionHandlerRegistry
		_userFieldExpressionHandlerRegistry;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserProcessorFactory _userProcessorFactory;

}