/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.term.provider.NotificationTermProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayOutputStream;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class GetObjectFieldNotificationTemplateTermsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", _company.getCompanyId());

		Bundle bundle = FrameworkUtil.getBundle(
			GetObjectFieldNotificationTemplateTermsMVCResourceCommandTest.
				class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			TestNotificationTermProvider.class,
			new TestNotificationTermProvider(),
			HashMapDictionaryBuilder.<String, Object>put(
				"class.name", objectDefinition.getClassName()
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetCommerceOrderObjectFieldNotificationTemplateTerms()
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", _company.getCompanyId());

		mockLiferayResourceRequest.setParameter(
			"objectDefinitionId",
			String.valueOf(objectDefinition.getObjectDefinitionId()));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			byteArrayOutputStream.toString());

		JSONArray jsonArray = (JSONArray)jsonObject.get("terms");

		Iterator<JSONObject> iterator = jsonArray.iterator();

		while (iterator.hasNext()) {
			JSONObject termJSONObject = iterator.next();

			if (!Objects.equals(termJSONObject.get("termLabel"), "test")) {
				continue;
			}

			Assert.assertEquals("[%TEST%]", termJSONObject.get("termName"));
		}
	}

	public static class TestNotificationTermProvider
		implements NotificationTermProvider {

		@Override
		public Map<String, String> getNotificationTerms() {
			return HashMapBuilder.put(
				"test", "[%TEST%]"
			).build();
		}

	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static ServiceRegistration<TestNotificationTermProvider>
		_serviceRegistration;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "mvc.command.name=/notification_templates/get_object_field_notification_template_terms"
	)
	private MVCResourceCommand _mvcResourceCommand;

}