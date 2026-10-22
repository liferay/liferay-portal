/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.model.DSRecipientViewDefinition;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ProxyUtil;
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
		Object dsHttp = ReflectionTestUtil.getFieldValue(
			_dsRecipientViewDefinitionManager, "_dsHttp");

		String url = "https://" + RandomTestUtil.randomString();

		Http http = (Http)ReflectionTestUtil.getAndSetFieldValue(
			dsHttp, "_http",
			ProxyUtil.newProxyInstance(
				Http.class.getClassLoader(), new Class<?>[] {Http.class},
				(proxy, method, arguments) -> {
					String json = JSONUtil.put(
						"url", url
					).toString();

					return json.getBytes();
				}));

		try {
			Assert.assertEquals(
				url,
				_dsRecipientViewDefinitionManager.addDSRecipientViewDefinition(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
					new DSRecipientViewDefinition()));

			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Assert.assertThrows(
				PrincipalException.MustBeCompanyAdmin.class,
				() ->
					_dsRecipientViewDefinitionManager.
						addDSRecipientViewDefinition(
							TestPropsValues.getCompanyId(),
							TestPropsValues.getGroupId(),
							RandomTestUtil.randomString(),
							new DSRecipientViewDefinition()));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_dsRecipientViewDefinitionManager.
						addDSRecipientViewDefinition(
							TestPropsValues.getCompanyId(),
							TestPropsValues.getGroupId(),
							RandomTestUtil.randomString(),
							new DSRequestRecipient(
								HashMapBuilder.<String, Serializable>put(
									"r_userToDSRequestRecipients_userId",
									TestPropsValues.getUserId()
								).build()),
							"http://" + RandomTestUtil.randomString()));
			Assert.assertEquals(
				url,
				_dsRecipientViewDefinitionManager.addDSRecipientViewDefinition(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
					new DSRequestRecipient(
						HashMapBuilder.<String, Serializable>put(
							"r_userToDSRequestRecipients_userId",
							user.getUserId()
						).build()),
					"http://" + RandomTestUtil.randomString()));
		}
		finally {
			ReflectionTestUtil.setFieldValue(dsHttp, "_http", http);
		}
	}

	@Inject
	private DSRecipientViewDefinitionManager _dsRecipientViewDefinitionManager;

}