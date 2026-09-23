/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class AddStyleBookEntryFrontendTokenMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = new ServiceContext();

		_serviceContext.setScopeGroupId(_group.getGroupId());
		_serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(mockHttpServletRequest);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddFrontendToken() throws Exception {
		_testAddFrontendTokenWithBlankLabel();
		_testAddFrontendTokenWithDuplicateLabel();
		_testAddFrontendTokenWithType();
		_testAddFrontendTokenWithUTF8Label();
	}

	private JSONObject _addFrontendToken(
			String categoryName, String description, String label,
			StyleBookEntry styleBookEntry, String tokenSetName,
			FrontendToken.Type type)
		throws Exception {

		String defaultValue = RandomTestUtil.randomString();
		String tokenSetDescription = RandomTestUtil.randomString();
		String tokenSetLabel = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_getCustomFrontendTokenDefinitionJSONObject(
				_processAction(
					_getMockLiferayPortletActionRequest(
						categoryName, defaultValue, description, label,
						styleBookEntry.getStyleBookEntryId(),
						tokenSetDescription, tokenSetLabel, tokenSetName,
						type.getValue())));

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		Map<String, JSONObject> frontendTokenCategoryJSONObjects =
			JSONUtil.toJSONObjectMap(frontendTokenCategoriesJSONArray, "name");

		JSONObject frontendTokenCategoryJSONObject =
			frontendTokenCategoryJSONObjects.get(categoryName);

		Assert.assertEquals(
			categoryName, frontendTokenCategoryJSONObject.getString("label"));

		JSONArray frontendTokenSetsJSONArray =
			frontendTokenCategoryJSONObject.getJSONArray("frontendTokenSets");

		Map<String, JSONObject> frontendTokenSetJSONObjects =
			JSONUtil.toJSONObjectMap(frontendTokenSetsJSONArray, "name");

		JSONObject frontendTokenSetJSONObject = frontendTokenSetJSONObjects.get(
			tokenSetName);

		JSONArray frontendTokensJSONArray =
			frontendTokenSetJSONObject.getJSONArray("frontendTokens");

		Assert.assertEquals(1, frontendTokensJSONArray.length());

		return frontendTokensJSONArray.getJSONObject(0);
	}

	private StyleBookEntry _addStyleBookEntry() throws Exception {
		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			StringPool.BLANK, _THEME_ID_CLASSIC, _serviceContext);
	}

	private JSONObject _getCustomFrontendTokenDefinitionJSONObject(
			String responseJSON)
		throws Exception {

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			responseJSON);

		JSONObject customFrontendTokenDefinitionJSONObject =
			responseJSONObject.getJSONObject("customFrontendTokenDefinition");

		Assert.assertEquals(
			StyleBookConstants.CUSTOM_FRONTEND_TOKEN_DEFINITION_ID,
			customFrontendTokenDefinitionJSONObject.getString("id"));
		Assert.assertEquals(
			FrontendTokenDefinitionConstants.PRIORITY_CUSTOM,
			customFrontendTokenDefinitionJSONObject.getInt("priority"));

		return customFrontendTokenDefinitionJSONObject;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
		String categoryName, String defaultValue, String description,
		String label, long styleBookEntryId, String tokenSetDescription,
		String tokenSetLabel, String tokenSetName, String type) {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"categoryName", categoryName);
		mockLiferayPortletActionRequest.addParameter(
			"defaultValue", defaultValue);
		mockLiferayPortletActionRequest.addParameter(
			"description", description);
		mockLiferayPortletActionRequest.addParameter(
			"editorType", FrontendTokenDefinitionUtil.EDITOR_TYPE_DEFAULT);
		mockLiferayPortletActionRequest.addParameter("label", label);
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId", String.valueOf(styleBookEntryId));
		mockLiferayPortletActionRequest.addParameter(
			"tokenSetDescription", tokenSetDescription);
		mockLiferayPortletActionRequest.addParameter(
			"tokenSetLabel", tokenSetLabel);
		mockLiferayPortletActionRequest.addParameter(
			"tokenSetName", tokenSetName);
		mockLiferayPortletActionRequest.addParameter("type", type);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private String _processAction(
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest)
		throws Exception {

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		ReflectionTestUtil.invoke(
			_addStyleBookEntryFrontendTokenMVCActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return mockHttpServletResponse.getContentAsString();
	}

	private void _testAddFrontendTokenWithBlankLabel() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry();

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			_processAction(
				_getMockLiferayPortletActionRequest(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), StringPool.BLANK,
					styleBookEntry.getStyleBookEntryId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					FrontendToken.Type.STRING.getValue())));

		Assert.assertEquals(
			LanguageUtil.get(
				_themeDisplay.getRequest(),
				"please-fill-in-the-required-fields"),
			responseJSONObject.getString("error"));

		Assert.assertNull(
			_styleBookEntryLocalService.fetchDraft(
				styleBookEntry.getStyleBookEntryId()));
	}

	private void _testAddFrontendTokenWithDuplicateLabel() throws Exception {
		String categoryName = RandomTestUtil.randomString();
		String label = RandomTestUtil.randomString();
		String tokenSetName = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry = _addStyleBookEntry();

		_addFrontendToken(
			categoryName, RandomTestUtil.randomString(), label, styleBookEntry,
			tokenSetName, FrontendToken.Type.STRING);

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			_processAction(
				_getMockLiferayPortletActionRequest(
					categoryName, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), label,
					styleBookEntry.getStyleBookEntryId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), tokenSetName,
					FrontendToken.Type.STRING.getValue())));

		Assert.assertEquals(
			LanguageUtil.get(
				_themeDisplay.getRequest(),
				"a-custom-token-with-this-label-already-exists.-please-enter-" +
					"a-different-label"),
			responseJSONObject.getString("error"));
	}

	private void _testAddFrontendTokenWithType() throws Exception {
		for (FrontendToken.Type type : FrontendToken.Type.values()) {
			JSONObject frontendTokenJSONObject = _addFrontendToken(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), _addStyleBookEntry(),
				RandomTestUtil.randomString(), type);

			Assert.assertEquals(
				type.getValue(), frontendTokenJSONObject.getString("type"));
		}
	}

	private void _testAddFrontendTokenWithUTF8Label() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry();

		String categoryName = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();
		String label = "テスト";
		String tokenSetName = RandomTestUtil.randomString();

		JSONObject frontendTokenJSONObject = _addFrontendToken(
			categoryName, description, label, styleBookEntry, tokenSetName,
			FrontendToken.Type.STRING);

		String name = frontendTokenJSONObject.getString("name");

		Assert.assertTrue(name.matches("^token[0-9a-f]{64}$"));

		Assert.assertTrue(frontendTokenJSONObject.has("defaultValue"));

		Assert.assertEquals(
			description, frontendTokenJSONObject.getString("description"));
		Assert.assertFalse(frontendTokenJSONObject.has("editorType"));
		Assert.assertEquals(label, frontendTokenJSONObject.getString("label"));

		JSONArray mappingsJSONArray = frontendTokenJSONObject.getJSONArray(
			"mappings");

		JSONObject mappingJSONObject = mappingsJSONArray.getJSONObject(0);

		Assert.assertEquals(name, mappingJSONObject.getString("value"));

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.getDraft(
				styleBookEntry.getStyleBookEntryId());

		JSONObject persistedFrontendTokenDefinitionJSONObject =
			JSONFactoryUtil.createJSONObject(
				updatedStyleBookEntry.getFrontendTokenDefinition());

		JSONObject persistedFrontendTokenJSONObject =
			persistedFrontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories"
			).getJSONObject(
				0
			).getJSONArray(
				"frontendTokenSets"
			).getJSONObject(
				0
			).getJSONArray(
				"frontendTokens"
			).getJSONObject(
				0
			);

		JSONArray persistedMappingsJSONArray =
			persistedFrontendTokenJSONObject.getJSONArray("mappings");

		Assert.assertEquals(
			label, persistedFrontendTokenJSONObject.getString("label"));

		Assert.assertEquals(
			name,
			persistedMappingsJSONArray.getJSONObject(
				0
			).getString(
				"value"
			));
	}

	private static final String _THEME_ID_CLASSIC = "classic_WAR_classictheme";

	@Inject(
		filter = "mvc.command.name=/style_book/add_style_book_entry_frontend_token"
	)
	private MVCActionCommand _addStyleBookEntryFrontendTokenMVCActionCommand;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private ThemeDisplay _themeDisplay;

}