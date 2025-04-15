/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
@Sync
public class EditContentDashboardConfigurationMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = _companyLocalService.fetchCompany(
			TestPropsValues.getCompanyId());

		_audienceAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				_company.getGroupId(), "audience");
		_stageAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				_company.getGroupId(), "stage");
		_topicAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				_company.getGroupId(), "topic");
	}

	@Test
	public void testGetAvailableVocabularyNames() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				String.valueOf(_audienceAssetVocabulary.getVocabularyId()),
				String.valueOf(_stageAssetVocabulary.getVocabularyId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		JSONArray keyValuePairsJSONArray = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminConfigurationDisplayContext"),
			"getAvailableVocabularyJSONArray", new Class<?>[0]);

		Assert.assertTrue(keyValuePairsJSONArray.length() > 0);

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Topic"
			));
	}

	@Test
	public void testGetAvailableVocabularyNamesWithAudienceAndNonglobalAssetVocabulary()
		throws Exception {

		User testUser = UserTestUtil.addUser();

		Group testGroup = GroupTestUtil.addGroup();

		Company testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_assetVocabularyLocalService.addVocabulary(
			testUser.getUserId(), testGroup.getGroupId(),
			"NonGlobalAssetVocabulary",
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				testUser.getUserId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				String.valueOf(_stageAssetVocabulary.getVocabularyId()),
				String.valueOf(_topicAssetVocabulary.getVocabularyId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		JSONArray keyValuePairsJSONArray = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminConfigurationDisplayContext"),
			"getAvailableVocabularyJSONArray", new Class<?>[0]);

		Assert.assertTrue(keyValuePairsJSONArray.length() > 0);

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Audience"
			));
		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"NonGlobalAssetVocabulary"
			));
	}

	@Test
	public void testGetAvailableVocabularyNamesWithAudienceAssetVocabulary()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				String.valueOf(_audienceAssetVocabulary.getVocabularyId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		JSONArray keyValuePairsJSONArray = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminConfigurationDisplayContext"),
			"getAvailableVocabularyJSONArray", new Class<?>[0]);

		Assert.assertTrue(keyValuePairsJSONArray.length() > 0);

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Stage"
			));
		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Topic"
			));
	}

	@Test
	public void testGetCurrentVocabularyNames() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				String.valueOf(_audienceAssetVocabulary.getVocabularyId()),
				String.valueOf(_stageAssetVocabulary.getVocabularyId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		JSONArray keyValuePairsJSONArray = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminConfigurationDisplayContext"),
			"getCurrentVocabularyJSONArray", new Class<?>[0]);

		Assert.assertEquals(2, keyValuePairsJSONArray.length());

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Audience"
			));
		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Stage"
			));
	}

	@Test
	public void testGetCurrentVocabularyNamesWithAudienceAndNonglobalAssetVocabulary()
		throws Exception {

		User testUser = UserTestUtil.addUser();

		Group testGroup = GroupTestUtil.addGroup();

		Company testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		AssetVocabulary nonglobalAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				testUser.getUserId(), testGroup.getGroupId(),
				"NonGlobalAssetVocabulary",
				ServiceContextTestUtil.getServiceContext(
					testCompany.getCompanyId(), testGroup.getGroupId(),
					testUser.getUserId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				String.valueOf(_audienceAssetVocabulary.getVocabularyId()),
				String.valueOf(nonglobalAssetVocabulary.getVocabularyId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		JSONArray keyValuePairsJSONArray = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminConfigurationDisplayContext"),
			"getCurrentVocabularyJSONArray", new Class<?>[0]);

		Assert.assertEquals(2, keyValuePairsJSONArray.length());

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"NonGlobalAssetVocabulary"
			));

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Audience"
			));
	}

	@Test
	public void testGetCurrentVocabularyNamesWithAudienceAssetVocabulary()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				String.valueOf(_audienceAssetVocabulary.getVocabularyId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		JSONArray keyValuePairsJSONArray = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminConfigurationDisplayContext"),
			"getCurrentVocabularyJSONArray", new Class<?>[0]);

		Assert.assertEquals(1, keyValuePairsJSONArray.length());

		Assert.assertTrue(
			keyValuePairsJSONArray.toString(
			).contains(
				"Audience"
			));
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			String... assetVocabularyIds)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		PortletPreferences portletPreferences =
			mockLiferayPortletRenderRequest.getPreferences();

		portletPreferences.setValues("assetVocabularyIds", assetVocabularyIds);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(LanguageUtil.getLanguageId(_locale));
		themeDisplay.setLocale(_locale);

		return themeDisplay;
	}

	@Inject
	private static AssetVocabularyLocalService _assetVocabularyLocalService;

	private static AssetVocabulary _audienceAssetVocabulary;
	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static final Locale _locale = LocaleUtil.US;
	private static AssetVocabulary _stageAssetVocabulary;
	private static AssetVocabulary _topicAssetVocabulary;

	@Inject(
		filter = "mvc.command.name=/content_dashboard/edit_content_dashboard_configuration",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

}