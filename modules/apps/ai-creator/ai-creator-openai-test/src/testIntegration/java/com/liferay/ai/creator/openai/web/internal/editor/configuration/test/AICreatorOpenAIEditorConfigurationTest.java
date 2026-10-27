/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.editor.configuration.test;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-93786")
@RunWith(Arquillian.class)
public class AICreatorOpenAIEditorConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_originalAPIKey =
			_aiCreatorOpenAIConfigurationManager.
				getAICreatorOpenAICompanyAPIKey(_group.getCompanyId());
		_originalChatGPTEnabled =
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorChatGPTCompanyEnabled(_group.getCompanyId());
		_originalDALLEEnabled =
			_aiCreatorOpenAIConfigurationManager.isAICreatorDALLECompanyEnabled(
				_group.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), _originalAPIKey, _originalChatGPTEnabled,
				_originalDALLEEnabled);

		_groupLocalService.deleteGroup(_group);
	}

	@Test
	public void testAICreatorCKEditor5ConfigCompanyAndGroupEnabledJournalPortlet()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true, true);

		_assertCKEditor5EditorConfigurationConfigJSONObject(
			true, JournalPortletKeys.JOURNAL);
	}

	@Test
	public void testAICreatorCKEditor5ConfigCompanyAndGroupEnabledNoJournalPortlet()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true, true);

		_assertCKEditor5EditorConfigurationConfigJSONObject(
			false, RandomTestUtil.randomString());
	}

	@Test
	public void testAICreatorCKEditor5ConfigCompanyDisabled() throws Exception {
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), StringPool.BLANK, false, false);

		_assertCKEditor5EditorConfigurationConfigJSONObject(
			false, RandomTestUtil.randomString());
	}

	@FeatureFlag(enable = false, value = "LPD-93786")
	@Test
	public void testAICreatorCKEditor5ConfigFeatureFlagDisabled()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true, true);

		_assertCKEditor5EditorConfigurationConfigJSONObject(
			false, JournalPortletKeys.JOURNAL);
	}

	@Test
	public void testAICreatorToolbarCompanyAndGroupEnabledWithAPIKeyInCompany()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), StringPool.BLANK, true, true);

		_assertEditorConfigurationConfigJSONObject(
			true, true, true, JournalPortletKeys.JOURNAL);
	}

	@Test
	public void testAICreatorToolbarCompanyAndGroupEnabledWithAPIKeyInGroup()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), StringPool.BLANK, true, true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true, true);

		_assertEditorConfigurationConfigJSONObject(
			true, true, true, JournalPortletKeys.JOURNAL);
	}

	@Test
	public void testAICreatorToolbarCompanyAndGroupEnabledWithAPIKeyNoJournalPortlet()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true, true);

		_assertEditorConfigurationConfigJSONObject(
			false, false, false, RandomTestUtil.randomString());
	}

	@Test
	public void testAICreatorToolbarCompanyAndGroupEnabledWithoutAPIKey()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), StringPool.BLANK, true, true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), StringPool.BLANK, true, true);

		_assertEditorConfigurationConfigJSONObject(
			false, true, true, JournalPortletKeys.JOURNAL);
	}

	@Test
	public void testAICreatorToolbarCompanyDisabledGroupEnabled()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), StringPool.BLANK, false, false);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true,
				false);

		_assertEditorConfigurationConfigJSONObject(
			false, false, false, JournalPortletKeys.JOURNAL);
	}

	@Test
	public void testAICreatorToolbarCompanyEnabledGroupDisabled()
		throws Exception {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), false,
				false);

		_assertEditorConfigurationConfigJSONObject(
			false, false, false, JournalPortletKeys.JOURNAL);
	}

	@FeatureFlag(enable = false, value = "LPD-93786")
	@Test
	public void testAICreatorToolbarFeatureFlagDisabled() throws Exception {
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_group.getCompanyId(), RandomTestUtil.randomString(), true,
				true);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				_group.getGroupId(), RandomTestUtil.randomString(), true, true);

		Assert.assertFalse(
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorChatGPTCompanyEnabled(_group.getCompanyId()));
		Assert.assertFalse(
			_aiCreatorOpenAIConfigurationManager.isAICreatorChatGPTGroupEnabled(
				_group.getCompanyId(), _group.getGroupId()));
		_assertEditorConfigurationConfigJSONObject(
			true, false, false, JournalPortletKeys.JOURNAL);
	}

	private void _assertCKEditor5EditorConfigurationConfigJSONObject(
			boolean expectedEnabled, String portletId)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(portletId);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				portletId, RandomTestUtil.randomString(), "ckeditor5_classic",
				new HashMap<String, Object>(), themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(
					themeDisplay.getRequest()));

		JSONObject configJSONObject = editorConfiguration.getConfigJSONObject();

		Assert.assertEquals(
			expectedEnabled, configJSONObject.has("aiCreatorOpenAIURL"));
		Assert.assertEquals(
			expectedEnabled, configJSONObject.has("aiCreatorPortletNamespace"));
		Assert.assertEquals(
			expectedEnabled, configJSONObject.has("isAICreatorOpenAIAPIKey"));
		Assert.assertEquals(
			expectedEnabled, configJSONObject.has("showAICreator"));
	}

	private void _assertEditorConfigurationConfigJSONObject(
			boolean expectedAPIKey, boolean expectedAICreatorConfig,
			boolean expectedAICreatorToolbar, String portletId)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(portletId);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				portletId, "rich_text", "ckeditor_classic",
				HashMapBuilder.<String, Object>put(
					"liferay-ui:input-editor:name", "testEditor"
				).build(),
				themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(
					themeDisplay.getRequest()));

		JSONObject configJSONObject = editorConfiguration.getConfigJSONObject();

		Assert.assertEquals(
			expectedAICreatorConfig,
			configJSONObject.has("aiCreatorOpenAIURL"));
		Assert.assertEquals(
			expectedAICreatorConfig,
			configJSONObject.has("aiCreatorPortletNamespace"));
		Assert.assertEquals(
			expectedAICreatorConfig,
			configJSONObject.has("isAICreatorOpenAIAPIKey"));
		Assert.assertEquals(
			expectedAPIKey && expectedAICreatorConfig,
			configJSONObject.getBoolean("isAICreatorOpenAIAPIKey"));
		Assert.assertEquals(
			expectedAICreatorConfig, configJSONObject.has("showAICreator"));

		String extraPlugins = configJSONObject.getString("extraPlugins");

		Assert.assertNotNull(extraPlugins);
		Assert.assertEquals(
			expectedAICreatorToolbar, extraPlugins.contains("aicreator"));

		Assert.assertEquals(
			expectedAICreatorToolbar, _isAICreatorInToolbars(configJSONObject));
	}

	private ThemeDisplay _getThemeDisplay(String portletId) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		themeDisplay.setPpid(portletId);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return themeDisplay;
	}

	private boolean _isAICreator(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONArray toolbarJSONArray = jsonArray.getJSONArray(i);

			if (JSONUtil.hasValue(toolbarJSONArray, "AICreator")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isAICreatorInToolbars(JSONObject configJSONObject) {
		for (String key : configJSONObject.keySet()) {
			if (!key.startsWith("toolbar_")) {
				continue;
			}

			JSONArray jsonArray = configJSONObject.getJSONArray(key);

			Assert.assertFalse(JSONUtil.isEmpty(jsonArray));

			if (!_isAICreator(jsonArray)) {
				return false;
			}
		}

		return true;
	}

	@Inject
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private String _originalAPIKey;
	private boolean _originalChatGPTEnabled;
	private boolean _originalDALLEEnabled;

}