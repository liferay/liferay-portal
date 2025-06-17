/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.VersionableContentDashboardItem;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemActionProviderRegistry;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class JournalArticleContentDashboardItemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testGetAssetCategories() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			Collections.singletonList(assetCategory),
			contentDashboardItem.getAssetCategories());
	}

	@Test
	public void testGetAssetCategoriesByAssetVocabulary() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			Collections.singletonList(assetCategory),
			contentDashboardItem.getAssetCategories(
				assetCategory.getVocabularyId()));
	}

	@Test
	public void testGetAssetCategoriesByAssetVocabularyWithEmptyAssetCategories()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			Collections.emptyList(),
			contentDashboardItem.getAssetCategories(
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetCategoriesWithNoAssetCategoriesInAssetVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			Collections.emptyList(),
			contentDashboardItem.getAssetCategories(
				assetVocabulary.getVocabularyId()));
	}

	@Test
	public void testGetAssetTags() throws Exception {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		_serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			Collections.singletonList(assetTag),
			contentDashboardItem.getAssetTags());
	}

	@Test
	public void testGetAvailableLocales() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			new HashMap<>(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, LocaleUtil.getSiteDefault(), null, false, false,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		List<Locale> availableLocales =
			contentDashboardItem.getAvailableLocales();

		Assert.assertEquals(
			availableLocales.toString(), 2, availableLocales.size());
		Assert.assertTrue(availableLocales.contains(LocaleUtil.SPAIN));
		Assert.assertTrue(availableLocales.contains(LocaleUtil.US));
	}

	@Test
	public void testGetContentDashboardItemVersionActions() throws Exception {
		TestJournalArticleContentDashboardItemVersionAction
			testJournalArticleContentDashboardItemVersionAction =
				new TestJournalArticleContentDashboardItemVersionAction();

		TestJournalArticleContentDashboardItemVersionActionProvider
			testJournalArticleContentDashboardItemVersionActionProvider =
				new TestJournalArticleContentDashboardItemVersionActionProvider(
					true, testJournalArticleContentDashboardItemVersionAction);

		ServiceRegistration<ContentDashboardItemVersionActionProvider>
			serviceRegistration = _getServiceRegistration(
				testJournalArticleContentDashboardItemVersionActionProvider);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_serviceContext);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			VersionableContentDashboardItem versionableContentDashboardItem =
				(VersionableContentDashboardItem)contentDashboardItem;

			HttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

			List<ContentDashboardItemVersion> contentDashboardItemVersions =
				versionableContentDashboardItem.
					getAllContentDashboardItemVersions(mockHttpServletRequest);

			Assert.assertEquals(
				contentDashboardItemVersions.toString(), 1,
				contentDashboardItemVersions.size());

			ContentDashboardItemVersion contentDashboardItemVersion =
				contentDashboardItemVersions.get(0);

			List<ContentDashboardItemVersionAction>
				contentDashboardItemVersionActions =
					contentDashboardItemVersion.
						getContentDashboardItemVersionActions();

			Assert.assertTrue(
				contentDashboardItemVersionActions.contains(
					testJournalArticleContentDashboardItemVersionAction));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetCreateDate() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getCreateDate(),
			contentDashboardItem.getCreateDate());
	}

	@Test
	public void testGetDefaultContentDashboardItemAction() throws Exception {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			_addAssetDisplayPageEntry(journalArticle);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

			ContentDashboardItemAction contentDashboardItemAction =
				contentDashboardItem.getDefaultContentDashboardItemAction(
					mockHttpServletRequest);

			ContentDashboardItemAction expectedContentDashboardItemAction =
				_getContentDashboardItemAction(
					journalArticle, mockHttpServletRequest,
					ContentDashboardItemAction.Type.VIEW);

			Assert.assertEquals(
				expectedContentDashboardItemAction.getURL(),
				contentDashboardItemAction.getURL());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetDefaultContentDashboardItemActionWithApprovedAndDraftStatusAndNotOwnerUser()
		throws Exception {

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			JournalArticle journalArticle =
				JournalTestUtil.addArticleWithWorkflow(
					_group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), true);

			journalArticle = updateArticleStatus(
				journalArticle, WorkflowConstants.STATUS_APPROVED,
				journalArticle.getUserId());

			updateArticleStatus(
				journalArticle, WorkflowConstants.STATUS_DRAFT,
				journalArticle.getUserId());

			_addAssetDisplayPageEntry(journalArticle);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			ContentDashboardItemAction contentDashboardItemAction =
				contentDashboardItem.getDefaultContentDashboardItemAction(
					mockHttpServletRequest);

			ContentDashboardItemAction expectedContentDashboardItemAction =
				_getContentDashboardItemAction(
					journalArticle, mockHttpServletRequest,
					ContentDashboardItemAction.Type.VIEW);

			Assert.assertEquals(
				expectedContentDashboardItemAction.getURL(),
				contentDashboardItemAction.getURL());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetDefaultContentDashboardItemActionWithDraftStatusAndOwnerUser()
		throws Exception {

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, _getLiferayPortletConfig());
		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));
		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			JournalArticle journalArticle =
				JournalTestUtil.addArticleWithWorkflow(
					_group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), true);

			updateArticleStatus(
				journalArticle, WorkflowConstants.STATUS_DRAFT,
				TestPropsValues.getUserId());

			_addAssetDisplayPageEntry(journalArticle);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			ContentDashboardItemAction contentDashboardItemAction =
				contentDashboardItem.getDefaultContentDashboardItemAction(
					mockHttpServletRequest);

			ContentDashboardItemAction expectedContentDashboardItemAction =
				_getContentDashboardItemAction(
					journalArticle, mockHttpServletRequest,
					ContentDashboardItemAction.Type.EDIT);

			Assert.assertEquals(
				HttpComponentsUtil.removeParameter(
					expectedContentDashboardItemAction.getURL(), "p_p_auth"),
				HttpComponentsUtil.removeParameter(
					contentDashboardItemAction.getURL(), "p_p_auth"));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetDescription() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "Description"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, LocaleUtil.getSiteDefault(), null, false, false,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getDescription(LocaleUtil.US),
			contentDashboardItem.getDescription(LocaleUtil.US));
	}

	@Test
	public void testGetInfoItemReference() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		InfoItemReference infoItemReference = new InfoItemReference(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			infoItemReference, contentDashboardItem.getInfoItemReference());
	}

	@Test
	public void testGetLatestContentDashboardItemVersions() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);

		journalArticle = updateArticleStatus(
			journalArticle, WorkflowConstants.STATUS_APPROVED,
			journalArticle.getUserId());

		JournalArticle latestJournalArticle = updateArticleStatus(
			journalArticle, WorkflowConstants.STATUS_DRAFT,
			journalArticle.getUserId());

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			contentDashboardItem.getLatestContentDashboardItemVersions(
				LocaleUtil.US);

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 2,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.US, "approved"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals(
			String.valueOf(journalArticle.getVersion()),
			contentDashboardItemVersion.getVersion());

		contentDashboardItemVersion = contentDashboardItemVersions.get(1);

		Assert.assertEquals(
			_language.get(LocaleUtil.US, "draft"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals(
			String.valueOf(latestJournalArticle.getVersion()),
			contentDashboardItemVersion.getVersion());
	}

	@Test
	public void testGetModifiedDate() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getModifiedDate(),
			contentDashboardItem.getModifiedDate());
	}

	@Test
	public void testGetScopeName() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			_group.getDescriptiveName(LocaleUtil.US),
			contentDashboardItem.getScopeName(LocaleUtil.US));
	}

	@Test
	public void testGetSpecificInformationList() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		List<ContentDashboardItem.SpecificInformation<?>> specificInformations =
			contentDashboardItem.getSpecificInformationList(LocaleUtil.US);

		Assert.assertEquals(
			specificInformations.toString(), 3, specificInformations.size());

		ContentDashboardItem.SpecificInformation<?>
			displayDateSpecificInformation = null;

		for (ContentDashboardItem.SpecificInformation<?> specificInformation :
				specificInformations) {

			if (Objects.equals(specificInformation.getKey(), "display-date")) {
				displayDateSpecificInformation = specificInformation;

				break;
			}
		}

		Assert.assertNotNull(
			"display-date not found", displayDateSpecificInformation);

		Assert.assertEquals(
			journalArticle.getDisplayDate(),
			displayDateSpecificInformation.getValue());

		ContentDashboardItem.SpecificInformation<?>
			expirationDateSpecificInformation = null;

		for (ContentDashboardItem.SpecificInformation<?> specificInformation :
				specificInformations) {

			if (Objects.equals(
					specificInformation.getKey(), "expiration-date")) {

				expirationDateSpecificInformation = specificInformation;

				break;
			}
		}

		Assert.assertNotNull(
			"expiration-date not found", expirationDateSpecificInformation);

		Assert.assertEquals(
			journalArticle.getExpirationDate(),
			expirationDateSpecificInformation.getValue());

		ContentDashboardItem.SpecificInformation<?>
			reviewDateSpecificInformation = null;

		for (ContentDashboardItem.SpecificInformation<?> specificInformation :
				specificInformations) {

			if (Objects.equals(specificInformation.getKey(), "review-date")) {
				reviewDateSpecificInformation = specificInformation;

				break;
			}
		}

		Assert.assertNotNull(
			"review-date not found", reviewDateSpecificInformation);
		Assert.assertEquals(
			journalArticle.getReviewDate(),
			reviewDateSpecificInformation.getValue());
	}

	@Test
	public void testGetSubtype() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		ContentDashboardItemSubtype contentDashboardItemSubtype =
			contentDashboardItem.getContentDashboardItemSubtype();

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		Assert.assertEquals(
			ddmStructure.getName(LocaleUtil.US),
			contentDashboardItemSubtype.getLabel(LocaleUtil.US));
	}

	@Test
	public void testGetTitle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			contentDashboardItem.getTitle(LocaleUtil.US));
	}

	@Test
	public void testGetTypeLabel() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			ResourceActionsUtil.getModelResource(
				LocaleUtil.US, JournalArticle.class.getName()),
			contentDashboardItem.getTypeLabel(LocaleUtil.US));
	}

	@Test
	public void testGetUserId() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getUserId(), contentDashboardItem.getUserId());
	}

	@Test
	public void testGetUserIdWithLatestApprovedJournalArticle()
		throws Exception {

		User user = UserTestUtil.addUser();

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			JournalArticle latestJournalArticle = JournalTestUtil.updateArticle(
				user.getUserId(), journalArticle, journalArticle.getTitleMap(),
				journalArticle.getContent(), false, false, _serviceContext);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			Assert.assertEquals(
				latestJournalArticle.getStatusByUserId(),
				contentDashboardItem.getUserId());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetUserName() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			journalArticle.getUserId(), contentDashboardItem.getUserId());
	}

	@Test
	public void testGetUserNameWithLatestApprovedJournalArticle()
		throws Exception {

		User user = UserTestUtil.addUser();

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			JournalArticle latestJournalArticle = JournalTestUtil.updateArticle(
				user.getUserId(), journalArticle, journalArticle.getTitleMap(),
				journalArticle.getContent(), false, false, _serviceContext);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			Assert.assertEquals(
				latestJournalArticle.getStatusByUserName(),
				contentDashboardItem.getUserName());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetVersions() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		VersionableContentDashboardItem versionableContentDashboardItem =
			(VersionableContentDashboardItem)contentDashboardItem;

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.
				getLatestContentDashboardItemVersions(LocaleUtil.US);

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 1,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.US, WorkflowConstants.LABEL_APPROVED),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals("success", contentDashboardItemVersion.getStyle());
	}

	@Test
	public void testGetVersionsWithApprovedVersion() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);

		journalArticle = updateArticleStatus(
			journalArticle, WorkflowConstants.STATUS_APPROVED,
			journalArticle.getUserId());

		updateArticleStatus(
			journalArticle, WorkflowConstants.STATUS_DRAFT,
			journalArticle.getUserId());

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		VersionableContentDashboardItem versionableContentDashboardItem =
			(VersionableContentDashboardItem)contentDashboardItem;

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.
				getLatestContentDashboardItemVersions(LocaleUtil.US);

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 2,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion1 =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.US, WorkflowConstants.LABEL_APPROVED),
			contentDashboardItemVersion1.getLabel());
		Assert.assertEquals("success", contentDashboardItemVersion1.getStyle());

		ContentDashboardItemVersion contentDashboardItemVersion2 =
			contentDashboardItemVersions.get(1);

		Assert.assertEquals(
			_language.get(LocaleUtil.US, WorkflowConstants.LABEL_DRAFT),
			contentDashboardItemVersion2.getLabel());
		Assert.assertEquals(
			"secondary", contentDashboardItemVersion2.getStyle());
	}

	@Test
	public void testGetVisibleContentDashboardItemVersionActions()
		throws Exception {

		TestJournalArticleContentDashboardItemVersionAction
			testJournalArticleContentDashboardItemVersionAction =
				new TestJournalArticleContentDashboardItemVersionAction();

		TestJournalArticleContentDashboardItemVersionActionProvider
			testJournalArticleContentDashboardItemVersionActionProvider =
				new TestJournalArticleContentDashboardItemVersionActionProvider(
					false, testJournalArticleContentDashboardItemVersionAction);

		ServiceRegistration<ContentDashboardItemVersionActionProvider>
			serviceRegistration = _getServiceRegistration(
				testJournalArticleContentDashboardItemVersionActionProvider);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_serviceContext);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			VersionableContentDashboardItem versionableContentDashboardItem =
				(VersionableContentDashboardItem)contentDashboardItem;

			HttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

			List<ContentDashboardItemVersion> contentDashboardItemVersions =
				versionableContentDashboardItem.
					getAllContentDashboardItemVersions(mockHttpServletRequest);

			Assert.assertEquals(
				contentDashboardItemVersions.toString(), 1,
				contentDashboardItemVersions.size());

			ContentDashboardItemVersion contentDashboardItemVersion =
				contentDashboardItemVersions.get(0);

			List<ContentDashboardItemVersionAction>
				contentDashboardItemVersionActions =
					contentDashboardItemVersion.
						getContentDashboardItemVersionActions();

			Assert.assertFalse(
				contentDashboardItemVersionActions.contains(
					testJournalArticleContentDashboardItemVersionAction));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testIsViewable() throws Exception {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			_addAssetDisplayPageEntry(journalArticle);

			Assert.assertTrue(
				contentDashboardItem.isViewable(mockHttpServletRequest));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testIsViewableWithNoAssetDisplayPageEntry() throws Exception {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			ContentDashboardItem contentDashboardItem =
				_contentDashboardItemFactory.create(
					journalArticle.getResourcePrimKey());

			Assert.assertFalse(
				contentDashboardItem.isViewable(mockHttpServletRequest));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testViewVersionURL() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(
				journalArticle.getResourcePrimKey());

		VersionableContentDashboardItem versionableContentDashboardItem =
			(VersionableContentDashboardItem)contentDashboardItem;

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		String viewVersionsURL =
			versionableContentDashboardItem.getViewVersionsURL(
				mockHttpServletRequest);

		Assert.assertNotNull(viewVersionsURL);
		Assert.assertTrue(
			viewVersionsURL.contains(
				"articleId=" + journalArticle.getArticleId()));
		Assert.assertTrue(
			viewVersionsURL.contains(
				"mvcPath=" + HtmlUtil.escapeURL("/view_article_history.jsp")));
	}

	protected JournalArticle updateArticleStatus(
			JournalArticle article, int status, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		if (status == WorkflowConstants.STATUS_DRAFT) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}
		else {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		return JournalTestUtil.updateArticle(
			userId, article, article.getTitleMap(), article.getContent(), false,
			true, serviceContext);
	}

	private void _addAssetDisplayPageEntry(JournalArticle journalArticle)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				journalArticle.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			journalArticle.getUserId(), journalArticle.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_DEFAULT, _serviceContext);
	}

	private ContentDashboardItemAction _getContentDashboardItemAction(
			JournalArticle journalArticle,
			HttpServletRequest httpServletRequest,
			ContentDashboardItemAction.Type type)
		throws Exception {

		ContentDashboardItemActionProvider contentDashboardItemActionProvider =
			_contentDashboardItemActionProviderRegistry.
				getContentDashboardItemActionProvider(
					JournalArticle.class.getName(), type);

		return contentDashboardItemActionProvider.getContentDashboardItemAction(
			journalArticle, httpServletRequest);
	}

	private LiferayPortletConfig _getLiferayPortletConfig() {
		Portlet portlet = _portletLocalService.getPortletById(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);

		return (LiferayPortletConfig)PortletConfigFactoryUtil.create(
			portlet, null);
	}

	private ServiceRegistration<ContentDashboardItemVersionActionProvider>
		_getServiceRegistration(
			TestJournalArticleContentDashboardItemVersionActionProvider
				testJournalArticleContentDashboardItemVersionActionProvider) {

		Bundle bundle = FrameworkUtil.getBundle(
			JournalArticleContentDashboardItemTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			ContentDashboardItemVersionActionProvider.class,
			testJournalArticleContentDashboardItemVersionActionProvider, null);
	}

	private ThemeDisplay _getThemeDisplay(Locale locale) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		themeDisplay.setLocale(locale);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ContentDashboardItemActionProviderRegistry
		_contentDashboardItemActionProviderRegistry;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.journal.internal.item.JournalArticleContentDashboardItemFactory"
	)
	private ContentDashboardItemFactory _contentDashboardItemFactory;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

	private static class TestJournalArticleContentDashboardItemVersionAction
		implements ContentDashboardItemVersionAction {

		@Override
		public String getIcon() {
			return null;
		}

		@Override
		public String getLabel(Locale locale) {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getURL() {
			return null;
		}

	}

	private static class
		TestJournalArticleContentDashboardItemVersionActionProvider
			implements ContentDashboardItemVersionActionProvider
				<JournalArticle> {

		public TestJournalArticleContentDashboardItemVersionActionProvider(
			boolean showContentDashboardItemVersionAction,
			TestJournalArticleContentDashboardItemVersionAction
				testJournalArticleContentDashboardItemVersionAction) {

			_showContentDashboardItemVersionAction =
				showContentDashboardItemVersionAction;
			_testJournalArticleContentDashboardItemVersionAction =
				testJournalArticleContentDashboardItemVersionAction;
		}

		@Override
		public ContentDashboardItemVersionAction
			getContentDashboardItemVersionAction(
				JournalArticle journalArticle,
				HttpServletRequest httpServletRequest) {

			return _testJournalArticleContentDashboardItemVersionAction;
		}

		@Override
		public boolean isShow(
			JournalArticle journalArticle,
			HttpServletRequest httpServletRequest) {

			return _showContentDashboardItemVersionAction;
		}

		private final boolean _showContentDashboardItemVersionAction;
		private final TestJournalArticleContentDashboardItemVersionAction
			_testJournalArticleContentDashboardItemVersionAction;

	}

}