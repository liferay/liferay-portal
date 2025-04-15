/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class AutocompleteUserMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_setUpAutocompleteUserMVCResourceCommand();
	}

	@Test
	public void testServeResponse() throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		_users.add(UserTestUtil.addUser());
		_users.add(UserTestUtil.addUser());

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(_getThemeDisplay(user, true)),
			mockLiferayResourceResponse);

		JSONArray jsonArray = _getUsersJSONArray(mockLiferayResourceResponse);

		Assert.assertEquals(_getUsersCount() - 1, jsonArray.length());
		Assert.assertTrue(_containsField(jsonArray, "emailAddress"));
		Assert.assertTrue(_containsField(jsonArray, "fullName"));
	}

	@Test
	public void testServeResponseWithCompanyAdmin() throws Exception {
		_users.add(UserTestUtil.addUser());
		_users.add(UserTestUtil.addUser());

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(
				_getThemeDisplay(TestPropsValues.getUser(), true)),
			mockLiferayResourceResponse);

		JSONArray jsonArray = _getUsersJSONArray(mockLiferayResourceResponse);

		Assert.assertEquals(_getUsersCount() - 1, jsonArray.length());
		Assert.assertTrue(_containsField(jsonArray, "emailAddress"));
		Assert.assertTrue(_containsField(jsonArray, "fullName"));
	}

	@Test
	public void testServeResponseWithEmptyGroupIds() throws Exception {
		User user = UserTestUtil.addUser(new long[0]);

		_users.add(user);

		_users.add(UserTestUtil.addUser());

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(_getThemeDisplay(user, true)),
			mockLiferayResourceResponse);

		JSONArray jsonArray = _getUsersJSONArray(mockLiferayResourceResponse);

		Assert.assertEquals(0, jsonArray.length());
	}

	@Test(expected = PortletException.class)
	public void testServeResponseWithError() throws Exception {
		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(
				_getThemeDisplay(TestPropsValues.getUser(), false)),
			new MockLiferayResourceResponse());
	}

	private boolean _containsField(
		JSONArray fieldValuesJSONArray, String field) {

		for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
			JSONObject jsonObject = fieldValuesJSONArray.getJSONObject(i);

			if (jsonObject.has(field)) {
				return true;
			}
		}

		return false;
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
		ThemeDisplay themeDisplay) {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayResourceRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user, boolean signedIn)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setSignedIn(signedIn);
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private int _getUsersCount() {
		return ListUtil.count(
			UserLocalServiceUtil.getUsers(0, 20),
			user -> !user.isGuestUser() && !user.isServiceAccountUser());
	}

	private JSONArray _getUsersJSONArray(
			MockLiferayResourceResponse mockLiferayResourceResponse)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONArray(
			new String(byteArrayOutputStream.toByteArray()));
	}

	private void _setUpAutocompleteUserMVCResourceCommand() {
		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_userLocalService", _userLocalService);
	}

	@Inject
	private static UserLocalService _userLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "mvc.command.name=/admin/autocomplete_user")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private List<User> _users = new ArrayList<>();

}