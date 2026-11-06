/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.display.context.factory.test;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.ai.creator.openai.display.context.factory.AICreatorOpenAIMenuItemFactory;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Veronica Gonzalez
 */
@FeatureFlag("LPD-93786")
@RunWith(Arquillian.class)
public class AICreatorOpenAIMenuItemFactoryTest {

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
		_originalImageModel =
			_aiCreatorOpenAIConfigurationManager.
				getAICreatorOpenAICompanyImageModel(_group.getCompanyId());
		_originalImageSizes =
			_aiCreatorOpenAIConfigurationManager.
				getAICreatorOpenAICompanyImageSizes(_group.getCompanyId());
		_originalOpenAIImageGenerationEnabled =
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorOpenAIImageGenerationCompanyEnabled(
					_group.getCompanyId());

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				RandomTestUtil.randomString(), _group.getCompanyId(), true,
				true, StringPool.BLANK, StringPool.BLANK);
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAIGroupConfiguration(
				RandomTestUtil.randomString(), _group.getGroupId(), true, true,
				StringPool.BLANK, StringPool.BLANK);
	}

	@After
	public void tearDown() throws Exception {
		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				_originalAPIKey, _group.getCompanyId(), _originalChatGPTEnabled,
				_originalOpenAIImageGenerationEnabled, _originalImageModel,
				_originalImageSizes);

		_groupLocalService.deleteGroup(_group);
	}

	@Test
	public void testCreateAICreatorCreateImageMenuItem() throws Exception {
		Assert.assertTrue(
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorOpenAIImageGenerationCompanyEnabled(
					_group.getCompanyId()));
		Assert.assertTrue(
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorOpenAIImageGenerationGroupEnabled(
					_group.getCompanyId(), _group.getGroupId()));
		Assert.assertNotNull(
			_aiCreatorOpenAIMenuItemFactory.createAICreatorCreateImageMenuItem(
				_group.getGroupId(), 0, 0, _getThemeDisplay()));
	}

	@FeatureFlag(enable = false, value = "LPD-93786")
	@Test
	public void testCreateAICreatorCreateImageMenuItemWhenFeatureFlagIsDisabled()
		throws Exception {

		Assert.assertFalse(
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorOpenAIImageGenerationCompanyEnabled(
					_group.getCompanyId()));
		Assert.assertFalse(
			_aiCreatorOpenAIConfigurationManager.
				isAICreatorOpenAIImageGenerationGroupEnabled(
					_group.getCompanyId(), _group.getGroupId()));
		Assert.assertNull(
			_aiCreatorOpenAIMenuItemFactory.createAICreatorCreateImageMenuItem(
				_group.getGroupId(), 0, 0, _getThemeDisplay()));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(mockHttpServletRequest);

		return themeDisplay;
	}

	@Inject
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	@Inject
	private AICreatorOpenAIMenuItemFactory _aiCreatorOpenAIMenuItemFactory;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private String _originalAPIKey;
	private boolean _originalChatGPTEnabled;
	private String _originalImageModel;
	private String _originalImageSizes;
	private boolean _originalOpenAIImageGenerationEnabled;

}