/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.ListTypeServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Igor Spasic
 */
public class JSONServiceActionTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetArgumentValue() throws Exception {
		JSONServiceAction jsonServiceAction = new JSONServiceAction();

		String[] parameters = {
			"companyId", "name", "excludedNames", "title", "description",
			"types", "subtype", "excludedTeamRoleId", "teamGroupId", "start",
			"end"
		};

		Object[] methodAndParameterTypes =
			jsonServiceAction.getMethodAndParameterTypes(
				RoleServiceUtil.class, "getGroupRolesAndTeamRoles", parameters,
				new String[0]);

		Method method = (Method)methodAndParameterTypes[0];
		Type[] parameterTypes = (Type[])methodAndParameterTypes[1];

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("excludedNames", "[]");

		Object value = jsonServiceAction.getArgValue(
			mockHttpServletRequest, RoleServiceUtil.class, method.getName(),
			parameters[2], parameterTypes[2]);

		Assert.assertEquals("[]", value.toString());

		mockHttpServletRequest.setParameter(
			"excludedNames",
			"{\"class\": " +
				"\"com.liferay.portal.kernel.dao.orm.EntityCacheUtil\"}");

		value = jsonServiceAction.getArgValue(
			mockHttpServletRequest, RoleServiceUtil.class, method.getName(),
			parameters[2], parameterTypes[2]);

		Assert.assertEquals(
			"{class=com.liferay.portal.kernel.dao.orm.EntityCacheUtil}",
			value.toString());
	}

	@Test
	public void testGetArgumentWithArrayValue() throws Exception {
		JSONServiceAction jsonServiceAction = new JSONServiceAction();

		String[] parameters = {"roleId", "groupIds"};

		Object[] methodAndParameterTypes =
			jsonServiceAction.getMethodAndParameterTypes(
				GroupServiceUtil.class, "addRoleGroups", parameters,
				new String[0]);

		Method method = (Method)methodAndParameterTypes[0];
		Type[] parameterTypes = (Type[])methodAndParameterTypes[1];

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(parameters[0], "11111");
		mockHttpServletRequest.setParameter(parameters[1], "11111,22222,33333");

		Object value = jsonServiceAction.getArgValue(
			mockHttpServletRequest, GroupServiceUtil.class, method.getName(),
			parameters[1], parameterTypes[1]);

		Class<?> clazz = value.getClass();

		Assert.assertTrue(clazz.isArray());

		long[] arrayValue = (long[])value;

		Assert.assertEquals(Arrays.toString(arrayValue), 3, arrayValue.length);
	}

	@Test
	public void testGetMethodAndParameterTypes() throws Exception {
		JSONServiceAction jsonServiceAction = new JSONServiceAction();

		Assert.assertEquals(
			ListTypeServiceUtil.class.getMethod(
				"getListTypes", long.class, String.class),
			jsonServiceAction.getMethodAndParameterTypes(
				ListTypeServiceUtil.class, "getListTypes",
				new String[] {"companyId", "type"}, new String[0])[0]);
		Assert.assertNull(
			jsonServiceAction.getMethodAndParameterTypes(
				ListTypeServiceUtil.class, "getService", new String[0],
				new String[0]));
		Assert.assertNull(
			jsonServiceAction.getMethodAndParameterTypes(
				ListTypeServiceUtil.class, "hashCode", new String[0],
				new String[0]));
		Assert.assertNull(
			jsonServiceAction.getMethodAndParameterTypes(
				ListTypeServiceUtil.class, "setService",
				new String[] {"service"}, new String[0]));
		Assert.assertNull(
			jsonServiceAction.getMethodAndParameterTypes(
				ListTypeServiceUtil.class, "toString", new String[0],
				new String[0]));
	}

}