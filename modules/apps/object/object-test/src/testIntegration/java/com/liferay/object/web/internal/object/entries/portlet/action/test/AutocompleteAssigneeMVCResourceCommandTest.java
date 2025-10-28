/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class AutocompleteAssigneeMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			AutocompleteAssigneeMVCResourceCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		List<ServiceReference<MVCResourceCommand>> serviceReferences =
			new ArrayList<>(
				bundleContext.getServiceReferences(
					MVCResourceCommand.class,
					StringBundler.concat(
						"(&(jakarta.portlet.name=",
						_objectDefinition.getPortletId(),
						")(mvc.command.name=/object_entries",
						"/autocomplete_assignee))")));

		_mvcResourceCommand = bundleContext.getService(
			serviceReferences.get(0));

		_role = RoleTestUtil.addRole("Custom Role", RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(), "John",
			"Doe", new long[0], ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testDoServeResource() throws Exception {
		JSONObject jsonObject = _getJSONObject(RoleConstants.GUEST);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 0, itemsJSONArray.length());

		_assertJSONObject("Role", _getJSONObject("Custom R"));
		_assertJSONObject("Role", _getJSONObject("custom role"));
		_assertJSONObject("User", _getJSONObject("Doe"));
		_assertJSONObject("User", _getJSONObject("John"));
		_assertJSONObject("User", _getJSONObject("John D"));
		_assertJSONObject("User", _getJSONObject("John Doe"));
	}

	private void _assertJSONObject(String expectedType, JSONObject jsonObject)
		throws Exception {

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode",
				() -> {
					if (StringUtil.equals(expectedType, "Role")) {
						return _role.getExternalReferenceCode();
					}

					return _user.getExternalReferenceCode();
				}
			).put(
				"name",
				() -> {
					if (StringUtil.equals(expectedType, "Role")) {
						return _role.getName();
					}

					return _user.getFullName();
				}
			).put(
				"type", expectedType
			).toString(),
			String.valueOf(itemsJSONArray.getJSONObject(0)),
			JSONCompareMode.LENIENT);
	}

	private JSONObject _getJSONObject(String search) throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("search", search);
		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			new ThemeDisplay() {
				{
					setCompany(
						_companyLocalService.getCompany(
							TestPropsValues.getCompanyId()));
				}
			});

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		ReflectionTestUtil.invoke(
			_mvcResourceCommand, "doServeResource",
			new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private MVCResourceCommand _mvcResourceCommand;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

}