/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.net.URI;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class EditOAuth2ApplicationTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		_assertEscapedOnce(
			_getEditOAuth2ApplicationPageBodyString(_oAuth2ApplicationId),
			_clientCredentialUser.getScreenName());

		_assertEscapedOnce(
			_getEditOAuth2ApplicationPageBodyString(0), _user.getScreenName());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new EditOAuth2ApplicationTestPreparatorBundleActivator();
	}

	private void _assertEscapedOnce(String bodyString, String userName) {
		Matcher matcher = _clientCredentialUserNamePattern.matcher(bodyString);

		Assert.assertTrue(matcher.find());

		Assert.assertEquals(
			HtmlUtil.escapeAttribute(userName), matcher.group(1));
	}

	private String _getEditOAuth2ApplicationPageBodyString(
			long oAuth2ApplicationId)
		throws Exception {

		String portletNamespace = PortalUtil.getPortletNamespace(_PORTLET_ID);

		URI uri = new URI(
			PortalUtil.getControlPanelFullURL(
				TestPropsValues.getGroupId(), _PORTLET_ID,
				HashMapBuilder.put(
					portletNamespace + "mvcRenderCommandName",
					new String[] {"/oauth2_provider/update_oauth2_application"}
				).put(
					portletNamespace + "oAuth2ApplicationId",
					new String[] {String.valueOf(oAuth2ApplicationId)}
				).build()));

		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path(uri.getPath());

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			uri.getRawQuery());

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			webTarget = webTarget.queryParam(
				entry.getKey(), (Object[])entry.getValue());
		}

		Function<WebTarget, Invocation.Builder> invocationBuilderFunction =
			getAuthenticatedInvocationBuilderFunction(
				_user.getEmailAddress(), _PASSWORD, null);

		Invocation.Builder invocationBuilder = invocationBuilderFunction.apply(
			webTarget);

		Response response = invocationBuilder.get();

		Assert.assertEquals(
			Response.Status.OK.getStatusCode(), response.getStatus());

		return response.readEntity(String.class);
	}

	private static final String _PASSWORD = RandomTestUtil.randomString();

	private static final String _PORTLET_ID =
		"com_liferay_oauth2_provider_web_internal_portlet_OAuth2AdminPortlet";

	private static final Pattern _clientCredentialUserNamePattern =
		Pattern.compile("_clientCredentialUserName\"[^>]*?value=\"([^\"]*)\"");

	private User _clientCredentialUser;
	private long _oAuth2ApplicationId;
	private User _user;

	private class EditOAuth2ApplicationTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_clientCredentialUser = _addUser(companyId);
			_user = _addUser(companyId);

			OAuth2Application oAuth2Application = createOAuth2Application(
				companyId, _clientCredentialUser, RandomTestUtil.randomString(),
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				Collections.singletonList("everything"));

			_oAuth2ApplicationId = oAuth2Application.getOAuth2ApplicationId();

			Role role = RoleLocalServiceUtil.getRole(
				companyId, RoleConstants.ADMINISTRATOR);

			UserLocalServiceUtil.addRoleUser(role.getRoleId(), _user);
		}

		private User _addUser(long companyId) throws Exception {
			User user = UserTestUtil.addUser(
				CompanyLocalServiceUtil.getCompany(companyId), _PASSWORD);

			autoCloseables.add(
				() -> UserLocalServiceUtil.deleteUser(user.getUserId()));

			user.setScreenName(
				RandomTestUtil.randomString() + StringPool.APOSTROPHE);

			return UserLocalServiceUtil.updateUser(user);
		}

	}

}