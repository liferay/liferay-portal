/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.model.DSRecipientViewDefinition;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@RunWith(Arquillian.class)
public class DSRecipientViewDefinitionManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddDSRecipientViewDefinition() throws Exception {
		User user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		Assert.assertThrows(
			PrincipalException.MustBeCompanyAdmin.class,
			() ->
				_dsRecipientViewDefinitionManager.addDSRecipientViewDefinition(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
					new DSRecipientViewDefinition()));

		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() ->
				_dsRecipientViewDefinitionManager.addDSRecipientViewDefinition(
					TestPropsValues.getCompanyId(),
					RandomTestUtil.randomString(),
					new DSRequestRecipient(
						HashMapBuilder.<String, Serializable>put(
							"r_userToDSRequestRecipients_userId",
							TestPropsValues.getUserId()
						).build()),
					TestPropsValues.getGroupId(),
					"http://" + RandomTestUtil.randomString()));
	}

	@Inject
	private DSRecipientViewDefinitionManager _dsRecipientViewDefinitionManager;

}