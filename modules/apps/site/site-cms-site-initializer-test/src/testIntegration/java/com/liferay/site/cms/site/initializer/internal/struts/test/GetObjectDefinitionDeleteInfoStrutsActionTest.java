/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class GetObjectDefinitionDeleteInfoStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-116122")
	public void testExecute() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getObjectDefinitionDeleteInfoStrutsAction.execute(
			_getMockHttpServletRequest(
				objectDefinition, TestPropsValues.getUser()),
			mockHttpServletResponse);

		JSONObject deletionInfoJSONObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertFalse(
			deletionInfoJSONObject.getBoolean("hasObjectRelationship"));
		Assert.assertEquals(
			0, deletionInfoJSONObject.getInt("objectEntriesCount"));
		Assert.assertEquals(
			objectDefinition.getStatus(),
			deletionInfoJSONObject.getInt("status"));

		User user = UserTestUtil.addUser();

		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _getObjectDefinitionDeleteInfoStrutsAction.execute(
					_getMockHttpServletRequest(objectDefinition, user),
					new MockHttpServletResponse()));

		Assert.assertArrayEquals(
			new String[] {ActionKeys.DELETE}, mustHavePermission.actionId);
		Assert.assertEquals(
			objectDefinition.getObjectDefinitionId(),
			mustHavePermission.resourceId);
		Assert.assertEquals(
			ObjectDefinition.class.getName(), mustHavePermission.resourceName);
		Assert.assertEquals(user.getUserId(), mustHavePermission.userId);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"objectDefinitionId",
			String.valueOf(objectDefinition.getObjectDefinitionId()));

		return mockHttpServletRequest;
	}

	@Inject(filter = "path=/cms/get_object_definition_deletion_info")
	private StrutsAction _getObjectDefinitionDeleteInfoStrutsAction;

	@Inject
	private JSONFactory _jsonFactory;

}