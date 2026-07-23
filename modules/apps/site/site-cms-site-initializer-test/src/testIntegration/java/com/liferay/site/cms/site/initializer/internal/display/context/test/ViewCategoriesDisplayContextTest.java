/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
@Sync
public class ViewCategoriesDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		String assetVocabularyTitle = RandomTestUtil.randomString();

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabularyTitle,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), assetVocabularyTitle
			).put(
				LocaleUtil.FRANCE, RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		_assetCategory = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), group.getGroupId(), 0,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).put(
				LocaleUtil.FRANCE, RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), _assetVocabulary.getVocabularyId(), false,
			new String[0],
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	@Test
	@TestInfo("LPD-83779")
	public void testGetBreadcrumbProps() throws Exception {
		_testGetBreadcrumbPropsUsesAssetCategoryTitle();
		_testGetBreadcrumbPropsUsesAssetVocabularyTitle();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			long categoryId, Locale locale, long vocabularyId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLocale(locale);

		mockHttpServletRequest.setParameter(
			"categoryId", String.valueOf(categoryId));

		if (vocabularyId != 0) {
			mockHttpServletRequest.setParameter(
				"vocabularyId", String.valueOf(vocabularyId));
		}

		return mockHttpServletRequest;
	}

	private Object _getViewCategoriesDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_viewCategoriesFragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewCategoriesDisplayContext");
	}

	private void _testGetBreadcrumbPropsUsesAssetCategoryTitle()
		throws Exception {

		HttpServletRequest httpServletRequest = _getMockHttpServletRequest(
			_assetCategory.getCategoryId(), LocaleUtil.FRANCE,
			_assetVocabulary.getVocabularyId());

		Map<String, Object> breadcrumbProps = ReflectionTestUtil.invoke(
			_getViewCategoriesDisplayContext(httpServletRequest),
			"getBreadcrumbProps", new Class<?>[0]);

		JSONArray jsonArray = (JSONArray)breadcrumbProps.get("breadcrumbItems");

		JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);

		Assert.assertEquals(
			_assetCategory.getTitle(LocaleUtil.FRANCE),
			jsonObject.getString("label"));
	}

	private void _testGetBreadcrumbPropsUsesAssetVocabularyTitle()
		throws Exception {

		HttpServletRequest httpServletRequest = _getMockHttpServletRequest(
			0, LocaleUtil.getDefault(), _assetVocabulary.getVocabularyId());

		Map<String, Object> breadcrumbProps = ReflectionTestUtil.invoke(
			_getViewCategoriesDisplayContext(httpServletRequest),
			"getBreadcrumbProps", new Class<?>[0]);

		JSONArray jsonArray = (JSONArray)breadcrumbProps.get("breadcrumbItems");

		JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);

		Assert.assertEquals(
			_assetVocabulary.getTitle(LocaleUtil.getDefault()),
			jsonObject.getString("label"));
	}

	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewCategoriesJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _viewCategoriesFragmentRenderer;

}