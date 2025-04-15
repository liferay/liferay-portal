/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.taglib.internal.util.AssetCategoryUtil;
import com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSelectorTag;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetCategoriesSelectorTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutPrototype = _layoutPrototypeLocalService.addLayoutPrototype(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testGetCategoryIdsTitles() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetCategory assetCategory3 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory3.getCategoryId(), assetCategory2.getCategoryId(),
				assetCategory1.getCategoryId()
			});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		AssetCategoriesSelectorTag assetCategoriesSelectorTag =
			new AssetCategoriesSelectorTag();

		assetCategoriesSelectorTag.setClassName(JournalArticle.class.getName());
		assetCategoriesSelectorTag.setClassPK(
			journalArticle.getResourcePrimKey());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.getDefault());

		ReflectionTestUtil.setFieldValue(
			assetCategoriesSelectorTag, "_httpServletRequest",
			_getMockHttpServletRequest(themeDisplay));

		List<String[]> categoryIdsTitles = ReflectionTestUtil.invoke(
			assetCategoriesSelectorTag, "getCategoryIdsTitles",
			new Class<?>[0]);

		Assert.assertNotNull(categoryIdsTitles);

		Assert.assertEquals(
			categoryIdsTitles.toString(), 2, categoryIdsTitles.size());

		String[] categoryIdsTitle = categoryIdsTitles.get(0);

		Assert.assertEquals(StringPool.BLANK, categoryIdsTitle[0]);
		Assert.assertEquals(StringPool.BLANK, categoryIdsTitle[1]);

		categoryIdsTitle = categoryIdsTitles.get(1);

		Assert.assertEquals(
			StringBundler.concat(
				assetCategory1.getCategoryId(), StringPool.COMMA,
				assetCategory2.getCategoryId(), StringPool.COMMA,
				assetCategory3.getCategoryId()),
			categoryIdsTitle[0]);
		Assert.assertEquals(
			StringBundler.concat(
				assetCategory1.getName(), AssetCategoryUtil.CATEGORY_SEPARATOR,
				assetCategory2.getName(), AssetCategoryUtil.CATEGORY_SEPARATOR,
				assetCategory3.getName()),
			categoryIdsTitle[1]);
	}

	@Test
	public void testGetGroupIdsWithLayoutPrototype() throws Exception {
		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
			WorkflowConstants.STATUS_APPROVED);

		AssetCategoriesSelectorTag assetCategoriesSelectorTag =
			new AssetCategoriesSelectorTag();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		ReflectionTestUtil.setFieldValue(
			assetCategoriesSelectorTag, "_httpServletRequest",
			_getMockHttpServletRequest(themeDisplay));

		long[] groupIds = ReflectionTestUtil.invoke(
			assetCategoriesSelectorTag, "getGroupIds", new Class<?>[0]);

		Assert.assertNotNull(groupIds);

		Assert.assertEquals(_group.getGroupId(), groupIds[0]);
	}

	private HttpServletRequest _getMockHttpServletRequest(
		ThemeDisplay themeDisplay) {

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@DeleteAfterTestRun
	private LayoutPrototype _layoutPrototype;

	@Inject
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

}