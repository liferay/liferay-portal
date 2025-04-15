/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.content.LayoutContentProvider;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.constants.LayoutPortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
public class LayoutLocalServiceCopyLayoutContentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@Test
	public void testCopyAssetCategoryIdsAndAssetTagNames() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		AssetTag assetTag = AssetTestUtil.addTag(_group.getGroupId());

		_layoutLocalService.updateAsset(
			sourceLayout.getUserId(), sourceLayout,
			new long[] {assetCategory.getCategoryId()},
			new String[] {assetTag.getName()});

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				Layout.class.getName(), targetLayout.getPlid());

		List<AssetTag> assetTags = _assetTagLocalService.getTags(
			Layout.class.getName(), targetLayout.getPlid());

		Assert.assertEquals(assetCategory, assetCategories.get(0));
		Assert.assertEquals(assetTag, assetTags.get(0));
	}

	@Test
	public void testCopyContentLayoutStructure() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypeContentLayout(_group);

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				sourceLayout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			containerLayoutStructureItem.getItemId(), 0);

		fragmentEntryLink = _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
			defaultSegmentsExperienceId, sourceLayout.getPlid(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
			FragmentConstants.TYPE_COMPONENT, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			containerLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				sourceLayout.getGroupId(), sourceLayout.getPlid(),
				defaultSegmentsExperienceId, layoutStructure.toString());

		Layout targetLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			ListUtil.isNotEmpty(
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					_group.getGroupId(), sourceLayout.getPlid())));

		Assert.assertFalse(
			ListUtil.isNotEmpty(
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					_group.getGroupId(), targetLayout.getPlid())));

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		Assert.assertNotNull(
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					targetLayout.getGroupId(), targetLayout.getPlid()));

		Assert.assertTrue(
			ListUtil.isNotEmpty(
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					_group.getGroupId(), targetLayout.getPlid())));
	}

	@Test
	public void testCopyContentLayoutStructureWithSegmentsExperiences()
		throws Exception {

		Layout targetLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout sourceLayout = targetLayout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), sourceLayout.getPlid());

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), sourceLayout.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperience1.getSegmentsExperienceId(),
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), sourceLayout.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperience2.getSegmentsExperienceId(),
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		_assertSegmentsExperiences(sourceLayout, targetLayout, 3);

		_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
			segmentsExperience2.getSegmentsExperienceId(), 1);

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		_assertSegmentsExperiences(sourceLayout, targetLayout, 3);
	}

	@Test
	public void testCopyContentLayoutWithExpandoValue() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypeContentLayout(_group);

		ExpandoTable expandoTable = _expandoTableLocalService.addDefaultTable(
			TestPropsValues.getCompanyId(), Layout.class.getName());

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		_expandoValueLocalService.addValue(
			TestPropsValues.getCompanyId(),
			PortalUtil.getClassName(expandoTable.getClassNameId()),
			expandoTable.getName(), expandoColumn.getName(),
			sourceLayout.getPlid(), RandomTestUtil.randomString());

		Layout targetLayout = LayoutTestUtil.addTypeContentLayout(_group);

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);
		}
	}

	@Test
	public void testCopyFragmentEntryLinksAndKeepTheSameFragmentEntryLinkId()
		throws Exception {

		Layout targetLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout sourceLayout = targetLayout.fetchDraftLayout();

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				sourceLayout.getPlid());

		FragmentEntryLink fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		LayoutStructureItem fragmentStyledLayoutStructureItem1 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink1.getFragmentEntryLinkId(),
				containerLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink2.getFragmentEntryLinkId(),
			containerLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink3 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink3.getFragmentEntryLinkId(),
			containerLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink4 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		LayoutStructureItem fragmentStyledLayoutStructureItem4 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink4.getFragmentEntryLinkId(),
				containerLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				sourceLayout.getGroupId(), sourceLayout.getPlid(),
				defaultSegmentsExperienceId, layoutStructure.toString());

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		List<FragmentEntryLink> firstCopyFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), targetLayout.getPlid());

		long[] firstCopyFragmentEntryLinkIds =
			_assertFragmentEntryLinksAndGetOriginalFragmentEntryLinkIds(
				firstCopyFragmentEntryLinks,
				ListUtil.fromArray(
					fragmentEntryLink1, fragmentEntryLink2, fragmentEntryLink3,
					fragmentEntryLink4));

		_fragmentEntryLinkLocalService.updateDeleted(
			TestPropsValues.getUserId(),
			fragmentEntryLink1.getFragmentEntryLinkId(), true);

		layoutStructure.deleteLayoutStructureItem(
			fragmentStyledLayoutStructureItem1.getItemId());

		_fragmentEntryLinkLocalService.updateDeleted(
			TestPropsValues.getUserId(),
			fragmentEntryLink4.getFragmentEntryLinkId(), true);

		layoutStructure.deleteLayoutStructureItem(
			fragmentStyledLayoutStructureItem4.getItemId());

		FragmentEntryLink fragmentEntryLink5 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink5.getFragmentEntryLinkId(),
			containerLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink6 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, sourceLayout.getUserId(), sourceLayout.getGroupId(), 0, 0,
				defaultSegmentsExperienceId, sourceLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink6.getFragmentEntryLinkId(),
			containerLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				sourceLayout.getGroupId(), sourceLayout.getPlid(),
				defaultSegmentsExperienceId, layoutStructure.toString());

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		List<FragmentEntryLink> secondCopyFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), targetLayout.getPlid());

		long[] secondCopyFragmentEntryLinkIds =
			_assertFragmentEntryLinksAndGetOriginalFragmentEntryLinkIds(
				secondCopyFragmentEntryLinks,
				ListUtil.fromArray(
					fragmentEntryLink2, fragmentEntryLink3, fragmentEntryLink5,
					fragmentEntryLink6));

		Assert.assertFalse(
			ArrayUtil.contains(
				secondCopyFragmentEntryLinkIds,
				fragmentEntryLink1.getFragmentEntryLinkId()));
		Assert.assertFalse(
			ArrayUtil.contains(
				secondCopyFragmentEntryLinkIds,
				fragmentEntryLink4.getFragmentEntryLinkId()));

		Set<Long> updatedFragmentEntryLinkIds = SetUtil.intersect(
			firstCopyFragmentEntryLinkIds, secondCopyFragmentEntryLinkIds);

		Assert.assertEquals(
			updatedFragmentEntryLinkIds.toString(), 2,
			updatedFragmentEntryLinkIds.size());

		Assert.assertTrue(
			ArrayUtil.containsAll(
				TransformUtil.transformToLongArray(
					secondCopyFragmentEntryLinks,
					FragmentEntryLinkModel::getOriginalFragmentEntryLinkId),
				new long[] {
					fragmentEntryLink2.getFragmentEntryLinkId(),
					fragmentEntryLink3.getFragmentEntryLinkId()
				}));
	}

	@Test
	@TestInfo("LPS-159512")
	public void testCopyLayoutClassedModelUsages() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			sourceLayout.getPlid(), new ServiceContext());

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(targetLayout.getPlid());

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), 1,
			layoutClassedModelUsages.size());

		sourceLayout = LayoutTestUtil.addTypeContentLayout(_group);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			sourceLayout.getPlid(), new ServiceContext());

		targetLayout = LayoutTestUtil.addTypeContentLayout(_group);

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(targetLayout.getPlid());

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), 1,
			layoutClassedModelUsages.size());
	}

	@Test
	public void testCopyLayoutContentUpdateAndPublishDraftWithinPublication()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Layout draftLayout = layout.fetchDraftLayout();

			Assert.assertNotNull(draftLayout);

			CTCollection ctCollection =
				_ctCollectionLocalService.addCTCollection(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId(), 0,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				String content = RandomTestUtil.randomString();

				_layoutLocalService.copyLayoutContent(
					_addFragmentEntryLinkAndGetLayout(content, draftLayout),
					layout);

				_assertLayoutContent(
					content, _portal.getSiteDefaultLocale(_group), 1,
					layout.getPlid());
			}
		}
	}

	@Test
	public void testCopyLayoutContentWithPublication() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			Layout displayPageTemplateLayout = _addDisplayPageTemplateLayout();

			Map<Long, String> layoutPlidMap = _addContentLayouts();

			Locale locale = _portal.getSiteDefaultLocale(_group);

			CTCollection ctCollection =
				_ctCollectionLocalService.addCTCollection(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId(), 0,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

			_entityCache.clearCache();
			_multiVMPool.clear();

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				try (LoggingTimer loggingTimer = new LoggingTimer()) {
					_layoutLocalService.copyLayoutContent(
						displayPageTemplateLayout.fetchDraftLayout(),
						displayPageTemplateLayout);
				}

				_assertLayoutContent(layoutPlidMap, locale);
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				_assertLayoutContent(layoutPlidMap, locale);
			}
		}
	}

	@Test
	public void testCopyLayoutDefaultSegmentsExperience() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout targetLayout = LayoutTestUtil.addTypeContentLayout(_group);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					targetLayout.getGroupId(), targetLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		FragmentEntryLink widgetFragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, targetLayout.getUserId(), _group.getGroupId(), 0, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(targetLayout.getPlid()),
				targetLayout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				JSONUtil.put(
					"instanceid", StringUtil.randomString()
				).put(
					"portletId", LayoutPortletKeys.LAYOUT_TEST_PORTLET
				).toString(),
				StringPool.BLANK, 0, StringPool.BLANK,
				FragmentConstants.TYPE_PORTLET, _serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			widgetFragmentEntryLink.getFragmentEntryLinkId(),
			layoutStructure.getMainItemId(), 0);

		String resourceName = PortletIdCodec.decodePortletName(
			LayoutPortletKeys.LAYOUT_TEST_PORTLET);

		String resourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			targetLayout.getPlid(), LayoutPortletKeys.LAYOUT_TEST_PORTLET);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			targetLayout.getCompanyId(), resourceName,
			ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey,
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				targetLayout.getCompanyId(), resourceName,
				ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);

		Assert.assertFalse(resourcePermissions.isEmpty());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_layoutLocalService.copyLayoutContent(
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				sourceLayout.getPlid()),
			sourceLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				targetLayout.getPlid()),
			targetLayout);

		resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				targetLayout.getCompanyId(), resourceName,
				ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);

		Assert.assertTrue(resourcePermissions.isEmpty());
	}

	@Test
	public void testCopyLayoutIcon() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		BufferedImage bufferedImage = new BufferedImage(
			1, 1, BufferedImage.TYPE_INT_RGB);

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

		byteArrayOutputStream.flush();

		sourceLayout = LayoutLocalServiceUtil.updateIconImage(
			sourceLayout.getPlid(), byteArrayOutputStream.toByteArray());

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		Assert.assertTrue(sourceLayout.isIconImage());
		Assert.assertFalse(targetLayout.isIconImage());

		Assert.assertNotEquals(
			sourceLayout.getIconImageId(), targetLayout.getIconImageId());

		targetLayout = _layoutLocalService.copyLayoutContent(
			sourceLayout, targetLayout);

		Assert.assertTrue(sourceLayout.isIconImage());
		Assert.assertTrue(targetLayout.isIconImage());

		Image sourceImage = _imageLocalService.getImage(
			sourceLayout.getIconImageId());

		Image targetImage = _imageLocalService.getImage(
			sourceLayout.getIconImageId());

		Assert.assertNotEquals(
			sourceImage.getTextObj(), targetImage.getTextObj());
	}

	@Test
	public void testCopyLayoutLookAndFeel() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		sourceLayout.setThemeId("l1-theme");
		sourceLayout.setCss("l1-css");

		sourceLayout = LayoutLocalServiceUtil.updateLayout(sourceLayout);

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		Assert.assertNotEquals(
			sourceLayout.getThemeId(), targetLayout.getThemeId());

		Assert.assertNotEquals(sourceLayout.getCss(), targetLayout.getCss());

		targetLayout = _layoutLocalService.copyLayoutContent(
			sourceLayout, targetLayout);

		Assert.assertEquals(
			sourceLayout.getThemeId(), targetLayout.getThemeId());

		Assert.assertEquals(sourceLayout.getCss(), targetLayout.getCss());
	}

	@Test
	public void testCopyLayoutPortletPreferences() throws Exception {
		String portletId = LayoutPortletKeys.LAYOUT_TEST_PORTLET;

		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), "column-1=" + portletId);

		PortletPreferences sourcePortletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				sourceLayout, portletId,
				"<portlet-preferences><preference><name>layout</name><value>1" +
					"</value></preference></portlet-preferences>");

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		PortletPreferences targetPortletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				targetLayout, portletId);

		Assert.assertNotEquals(
			PortletPreferencesFactoryUtil.toXML(targetPortletPreferences),
			PortletPreferencesFactoryUtil.toXML(sourcePortletPreferences));

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		targetPortletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				targetLayout, portletId);

		Assert.assertEquals(
			PortletPreferencesFactoryUtil.toXML(sourcePortletPreferences),
			PortletPreferencesFactoryUtil.toXML(targetPortletPreferences));
	}

	@Test
	public void testCopyLayoutSEOEntryWithLayoutSEOEntryCustomMetaTags()
		throws Exception {

		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			sourceLayout.getUserId(), sourceLayout.getGroupId(),
			sourceLayout.isPrivateLayout(), sourceLayout.getLayoutId(), false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), sourceLayout.getGroupId(), false,
			sourceLayout.getLayoutId(),
			Arrays.asList(
				new LayoutSEOEntryCustomMetaTagProperty(
					Collections.singletonMap(
						LocaleUtil.getSiteDefault(), "content1"),
					"property1"),
				new LayoutSEOEntryCustomMetaTagProperty(
					Collections.singletonMap(
						LocaleUtil.getSiteDefault(), "content2"),
					"property2")),
			ServiceContextTestUtil.getServiceContext(
				sourceLayout.getGroupId(), TestPropsValues.getUserId()));

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		LayoutSEOEntry targetLayoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				targetLayout.getGroupId(), targetLayout.isPrivateLayout(),
				targetLayout.getLayoutId());

		List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags =
			_layoutSEOEntryLocalService.getLayoutSEOEntryCustomMetaTags(
				targetLayoutSEOEntry.getGroupId(),
				targetLayoutSEOEntry.getLayoutSEOEntryId());

		Assert.assertFalse(layoutSEOEntryCustomMetaTags.isEmpty());
		Assert.assertEquals(
			layoutSEOEntryCustomMetaTags.toString(), 2,
			layoutSEOEntryCustomMetaTags.size());

		LayoutSEOEntryCustomMetaTag firstLayoutSEOEntryCustomMetaTag =
			layoutSEOEntryCustomMetaTags.get(0);

		Assert.assertEquals(
			"property1", firstLayoutSEOEntryCustomMetaTag.getProperty());
		Assert.assertEquals(
			"content1",
			firstLayoutSEOEntryCustomMetaTag.getContent(
				LocaleUtil.getSiteDefault()));

		LayoutSEOEntryCustomMetaTag secondLayoutSEOEntryCustomMetaTag =
			layoutSEOEntryCustomMetaTags.get(1);

		Assert.assertEquals(
			"property2", secondLayoutSEOEntryCustomMetaTag.getProperty());
		Assert.assertEquals(
			"content2",
			secondLayoutSEOEntryCustomMetaTag.getContent(
				LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testCopyMasterLayoutContentWithOrphanPortletPreferences()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				draftLayout, LayoutPortletKeys.LAYOUT_TEST_PORTLET);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		String portletId1 = PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));

		_assertPortletPreferences(1, portletId1);

		String portletId2 = _addPortletToLayout(
			draftLayout, LayoutPortletKeys.LAYOUT_TEST_PORTLET);

		_assertPortletPreferences(1, portletId2);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertPortletPreferences(2, portletId1);
		_assertPortletPreferences(2, portletId2);

		Map<Long, String> layoutPlidMap = _addContentLayouts(layout.getPlid());

		for (Long plid : layoutPlidMap.keySet()) {
			ContentLayoutTestUtil.getRenderLayoutHTML(
				_layoutLocalService.getLayout(plid),
				_layoutServiceContextHelper, _layoutStructureProvider,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(plid));
		}

		int count = layoutPlidMap.size() + 3;

		_assertPortletPreferences(count, portletId1);
		_assertPortletPreferences(count, portletId2);

		String name = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		_addPortletPreferenceValue(name, draftLayout, portletId2, value);

		_entityCache.clearCache();
		_multiVMPool.clear();

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			ContentLayoutTestUtil.markItemForDeletionFromLayout(
				processAddPortletJSONObject.getString("addedItemId"),
				draftLayout, portletId1);

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);
		}

		_assertPortletPreferences(0, portletId1);
		_assertPortletPreferences(count, portletId2);
		_assertPortletPreferences(value, name, portletId2, draftLayout, layout);
	}

	@Test
	public void testCopyTypeSettings() throws Exception {
		Layout sourceLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				"lfr-theme:regular:show-footer", Boolean.TRUE.toString()
			).put(
				"lfr-theme:regular:show-header", Boolean.TRUE.toString()
			).buildString());

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		UnicodeProperties targetUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				targetLayout.getTypeSettings()
			).build();

		Assert.assertNull(
			targetUnicodeProperties.getProperty(
				"lfr-theme:regular:show-footer"));
		Assert.assertNull(
			targetUnicodeProperties.getProperty(
				"lfr-theme:regular:show-header"));

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);

		targetLayout = _layoutLocalService.fetchLayout(targetLayout.getPlid());

		targetUnicodeProperties.fastLoad(targetLayout.getTypeSettings());

		Assert.assertEquals(
			Boolean.TRUE.toString(),
			targetUnicodeProperties.getProperty(
				"lfr-theme:regular:show-footer"));
		Assert.assertEquals(
			Boolean.TRUE.toString(),
			targetUnicodeProperties.getProperty(
				"lfr-theme:regular:show-header"));
	}

	private Map<Long, String> _addContentLayouts() throws Exception {
		return _addContentLayouts(0);
	}

	private Map<Long, String> _addContentLayouts(long masterLayoutPlid)
		throws Exception {

		Map<Long, String> map = new HashMap<>();

		FragmentEntry fragmentEntry = _addFragmentEntry();

		Locale locale = _portal.getSiteDefaultLocale(_group);

		String languageId = LocaleUtil.toLanguageId(locale);

		for (int i = 0; i < _NUMBER_PAGES; i++) {
			Layout layout = LayoutTestUtil.addTypeContentLayout(
				_group, false, false, masterLayoutPlid);

			Layout draftLayout = layout.fetchDraftLayout();

			Assert.assertNotNull(draftLayout);

			List<String> list = new ArrayList<>();

			for (int j = 0; j < _NUMBER_FRAGMENT_ENTRY_LINKS; j++) {
				String elementText = RandomTestUtil.randomString();

				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put(
							"element-text",
							JSONUtil.put(languageId, elementText))
					).toString(),
					fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
					fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), draftLayout,
					fragmentEntry.getFragmentEntryKey(),
					fragmentEntry.getType(), null, 0,
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(
							draftLayout.getPlid()));

				list.add(elementText);
			}

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			String content = _getLayoutContent(layout, locale);

			for (String text : list) {
				Assert.assertTrue(
					StringUtil.contains(content, text, StringPool.BLANK));
			}

			map.put(layout.getPlid(), content);
		}

		return map;
	}

	private Layout _addDisplayPageTemplateLayout() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		FragmentEntry fragmentEntry = _addFragmentEntry();

		for (int i = 0; i < _NUMBER_FRAGMENT_ENTRY_LINKS; i++) {
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put("mappedField", "AssetCategory_name"))
				).toString(),
				fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));
		}

		Assert.assertEquals(
			_NUMBER_FRAGMENT_ENTRY_LINKS,
			_fragmentEntryLinkLocalService.getFragmentEntryLinksCountByPlid(
				_group.getGroupId(), draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		Assert.assertEquals(
			_NUMBER_FRAGMENT_ENTRY_LINKS,
			_fragmentEntryLinkLocalService.getFragmentEntryLinksCountByPlid(
				_group.getGroupId(), layout.getPlid()));

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		String html =
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>";

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), null, html, null, false, null, null,
			0, false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private Layout _addFragmentEntryLinkAndGetLayout(
			String elementText, Layout layout)
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry();

		Locale locale = _portal.getSiteDefaultLocale(_group);

		String languageId = LocaleUtil.toLanguageId(locale);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text", JSONUtil.put(languageId, elementText))
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), null, 0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private void _addPortletPreferenceValue(
			String name, Layout layout, String portletId, String value)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(layout, portletId, null);

		portletPreferences.setValue(name, value);

		portletPreferences.store();

		_assertPortletPreferences(value, name, portletId, layout);
	}

	private String _addPortletToLayout(Layout layout, String portletId)
		throws Exception {

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(layout, portletId);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private long[] _assertFragmentEntryLinksAndGetOriginalFragmentEntryLinkIds(
		List<FragmentEntryLink> copiedFragmentEntryLinks,
		List<FragmentEntryLink> sourceFragmentEntryLinks) {

		Map<Long, FragmentEntryLink> originalFragmentEntryLinkIdMap =
			new HashMap<Long, FragmentEntryLink>() {
				{
					for (FragmentEntryLink fragmentEntryLink :
							copiedFragmentEntryLinks) {

						put(
							fragmentEntryLink.getOriginalFragmentEntryLinkId(),
							fragmentEntryLink);
					}
				}
			};

		long[] originalFragmentEntryLinkIds = ArrayUtil.toLongArray(
			originalFragmentEntryLinkIdMap.keySet());

		Assert.assertEquals(
			Arrays.toString(originalFragmentEntryLinkIds),
			sourceFragmentEntryLinks.size(),
			originalFragmentEntryLinkIds.length);
		Assert.assertTrue(
			ArrayUtil.containsAll(
				originalFragmentEntryLinkIds,
				TransformUtil.transformToLongArray(
					sourceFragmentEntryLinks,
					fragmentEntryLink ->
						fragmentEntryLink.getFragmentEntryLinkId())));

		for (FragmentEntryLink sourceFragmentEntryLink :
				sourceFragmentEntryLinks) {

			FragmentEntryLink copiedFragmentEntryLink =
				originalFragmentEntryLinkIdMap.get(
					sourceFragmentEntryLink.getFragmentEntryLinkId());

			Assert.assertNotNull(copiedFragmentEntryLink);
			Assert.assertEquals(
				sourceFragmentEntryLink.getConfiguration(),
				copiedFragmentEntryLink.getConfiguration());
			Assert.assertEquals(
				sourceFragmentEntryLink.getCss(),
				copiedFragmentEntryLink.getCss());
			Assert.assertEquals(
				sourceFragmentEntryLink.getEditableValues(),
				copiedFragmentEntryLink.getEditableValues());
			Assert.assertEquals(
				sourceFragmentEntryLink.getHtml(),
				copiedFragmentEntryLink.getHtml());
			Assert.assertEquals(
				sourceFragmentEntryLink.getJs(),
				copiedFragmentEntryLink.getJs());
			Assert.assertEquals(
				sourceFragmentEntryLink.getLastPropagationDate(),
				copiedFragmentEntryLink.getLastPropagationDate());
		}

		return originalFragmentEntryLinkIds;
	}

	private void _assertLayoutContent(
			Map<Long, String> layoutPlidMap, Locale locale)
		throws Exception {

		for (Map.Entry<Long, String> entry : layoutPlidMap.entrySet()) {
			_assertLayoutContent(
				entry.getValue(), locale, _NUMBER_FRAGMENT_ENTRY_LINKS,
				entry.getKey());
		}
	}

	private void _assertLayoutContent(
			String content, Locale locale, int numberFragmentEntryLinks,
			long plid)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(_group.getGroupId(), plid);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(
				SegmentsExperienceConstants.KEY_DEFAULT));

		Map<Long, LayoutStructureItem> fragmentEntryLinkIdMap =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			fragmentEntryLinkIdMap.toString(), numberFragmentEntryLinks,
			fragmentEntryLinkIdMap.size());

		for (long fragmentEntryLinkId : fragmentEntryLinkIdMap.keySet()) {
			Assert.assertNotNull(
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLinkId));
		}

		Assert.assertEquals(
			content,
			_getLayoutContent(_layoutLocalService.getLayout(plid), locale));
	}

	private void _assertPortletPreferences(long count, String portletId) {
		List<com.liferay.portal.kernel.model.PortletPreferences>
			portletPreferences =
				_portletPreferencesLocalService.
					getPortletPreferencesByPortletId(portletId);

		Assert.assertEquals(
			portletPreferences.toString(), count, portletPreferences.size());
	}

	private void _assertPortletPreferences(
		String expectedValue, String name, String portletId,
		Layout... layouts) {

		for (Layout layout : layouts) {
			PortletPreferences portletPreferences =
				_portletPreferencesFactory.getPortletSetup(
					layout, portletId, null);

			Assert.assertEquals(
				expectedValue, portletPreferences.getValue(name, null));
		}
	}

	private void _assertSegmentsExperiences(
		Layout sourceLayout, Layout targetLayout, int totalCount) {

		Assert.assertEquals(
			totalCount,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), sourceLayout.getPlid()));
		Assert.assertEquals(
			totalCount,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), targetLayout.getPlid()));

		for (SegmentsExperience sourceSegmentsExperience :
				_segmentsExperienceLocalService.getSegmentsExperiences(
					_group.getGroupId(), sourceLayout.getPlid())) {

			SegmentsExperience targetSegmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					_group.getGroupId(),
					sourceSegmentsExperience.getSegmentsExperienceKey(),
					targetLayout.getPlid());

			Assert.assertEquals(
				targetSegmentsExperience.getPriority(),
				sourceSegmentsExperience.getPriority());
		}
	}

	private String _getLayoutContent(Layout layout, Locale locale)
		throws Exception {

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			return _layoutContentProvider.getLayoutContent(
				themeDisplay.getRequest(), themeDisplay.getResponse(), layout,
				locale);
		}
	}

	private static final int _NUMBER_FRAGMENT_ENTRY_LINKS = 10;

	private static final int _NUMBER_PAGES = 10;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ImageLocalService _imageLocalService;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutContentProvider _layoutContentProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "jakarta.portlet.name=" + LayoutPortletKeys.LAYOUT_TEST_PORTLET
	)
	private final Portlet _portlet = null;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}