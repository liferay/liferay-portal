/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.content.dashboard.web.test.util.ContentDashboardTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.MutablePortletParameters;
import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletParameters;
import jakarta.portlet.ResourceURL;

import java.io.ByteArrayOutputStream;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Yurena Cabrera
 */
@RunWith(Arquillian.class)
public class GetContentDashboardItemInfoMVCResourceCommandTest {

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

		_initCategoryAndVocabulary();
	}

	@Test
	public void testGetFileEntrySpecificFields() throws Exception {
		JSONObject jsonObject = _serveResource(
			_createContentDashboardFileItem());

		Assert.assertNotNull(jsonObject);
		_assertSpecificFieldsJSONArrayTitles(
			Arrays.asList(
				_language.get(LocaleUtil.US, "size"),
				_language.get(LocaleUtil.US, "resolution"),
				_language.get(LocaleUtil.US, "content-dashboard-aspect-ratio"),
				_language.get(LocaleUtil.US, "extension"),
				_language.get(LocaleUtil.US, "file-name"),
				_language.get(LocaleUtil.US, "latest-version-url"),
				_language.get(LocaleUtil.US, "web-dav-url")),
			jsonObject.getJSONArray("specificFields"));
	}

	@Test
	public void testGetJournalArticleSpecificFields() throws Exception {
		JSONObject jsonObject = _serveResource(
			_createContentDashboardJournalArticleItem());

		Assert.assertNotNull(jsonObject);
		_assertSpecificFieldsJSONArrayTitles(
			Arrays.asList(
				_language.get(LocaleUtil.US, "display-date"),
				_language.get(LocaleUtil.US, "expiration-date"),
				_language.get(LocaleUtil.US, "review-date")),
			jsonObject.getJSONArray("specificFields"));
	}

	@Test
	public void testServeResource() throws Exception {
		ContentDashboardItem<?> contentDashboardItem =
			_createContentDashboardFileItem();

		JSONObject jsonObject = _serveResource(contentDashboardItem);

		JSONObject vocabulariesJSONObject = jsonObject.getJSONObject(
			"vocabularies");

		for (AssetCategory assetCategory :
				contentDashboardItem.getAssetCategories()) {

			JSONObject vocabularyDataJSONObject =
				vocabulariesJSONObject.getJSONObject(
					String.valueOf(assetCategory.getVocabularyId()));

			JSONArray categoriesJSONArray =
				vocabularyDataJSONObject.getJSONArray("categories");

			Assert.assertEquals(1, categoriesJSONArray.length());
			Assert.assertEquals(
				assetCategory.getTitle(LocaleUtil.getSiteDefault()),
				categoriesJSONArray.getString(0));
		}

		InfoItemReference infoItemReference =
			contentDashboardItem.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		Assert.assertTrue(
			infoItemIdentifier instanceof ClassPKInfoItemIdentifier);

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		Assert.assertEquals(
			infoItemReference.getClassName(),
			jsonObject.getString("className"));
		Assert.assertEquals(
			classPKInfoItemIdentifier.getClassPK(),
			jsonObject.getLong("classPK"), 0);

		Assert.assertEquals(
			contentDashboardItem.getDescription(LocaleUtil.US),
			jsonObject.getString("description"));
		Assert.assertNotNull(jsonObject.getString("fetchSharingButtonURL"));
		Assert.assertNotNull(
			jsonObject.getString("fetchSharingCollaboratorsURL"));

		JSONArray tagsJSONArray = jsonObject.getJSONArray("tags");

		Assert.assertEquals(
			JSONUtil.putAll(
				ListUtil.toArray(
					contentDashboardItem.getAssetTags(), AssetTag.NAME_ACCESSOR)
			).toString(),
			tagsJSONArray.toString());

		Assert.assertEquals(
			contentDashboardItem.getTitle(LocaleUtil.US),
			jsonObject.getString("title"));

		ContentDashboardItemSubtype<?> contentDashboardItemSubtype =
			contentDashboardItem.getContentDashboardItemSubtype();

		Assert.assertEquals(
			contentDashboardItemSubtype.getLabel(LocaleUtil.US),
			jsonObject.getString("subType"));

		List<ContentDashboardItem.SpecificInformation<?>> specificInformations =
			contentDashboardItem.getSpecificInformationList(LocaleUtil.US);

		Assert.assertEquals(
			String.valueOf(specificInformations), 7,
			specificInformations.size());

		JSONArray specificFieldsJSONArray = jsonObject.getJSONArray(
			"specificFields");

		for (int i = 0; i < specificInformations.size(); i++) {
			ContentDashboardItem.SpecificInformation<?> specificInformation =
				specificInformations.get(i);

			JSONObject specificFieldJSONObject =
				specificFieldsJSONArray.getJSONObject(i);

			JSONObject specificInformationJSONObject =
				specificInformation.toJSONObject(
					LanguageUtil.getLanguage(), LocaleUtil.US);

			Assert.assertEquals(
				specificFieldJSONObject.getString("title"),
				specificInformationJSONObject.getString("title"));
			Assert.assertEquals(
				specificFieldJSONObject.getString("value"),
				specificInformationJSONObject.getString("value"));
		}

		JSONObject userJSONObject = jsonObject.getJSONObject("user");

		Assert.assertEquals(
			contentDashboardItem.getUserName(),
			userJSONObject.getString("name"));
		Assert.assertEquals(
			contentDashboardItem.getUserId(), userJSONObject.getLong("userId"));

		_assertContentDashboardItemLatestVersions(
			contentDashboardItem, jsonObject);
	}

	@Test
	public void testServeResourceWithoutSharingButtonAction() throws Exception {
		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(),
						"com.liferay.sharing.internal.configuration." +
							"SharingGroupConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			JSONObject jsonObject = _serveResource(
				_createContentDashboardFileItem());

			Assert.assertEquals(
				StringPool.BLANK,
				jsonObject.getString("fetchSharingButtonURL"));
		}
	}

	@Test
	public void testServeResourceWithoutSharingCollaboratorsAction()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(),
						"com.liferay.sharing.internal.configuration." +
							"SharingGroupConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			JSONObject jsonObject = _serveResource(
				_createContentDashboardFileItem());

			Assert.assertEquals(
				StringPool.BLANK,
				jsonObject.getString("fetchSharingCollaboratorsURL"));
		}
	}

	@Test
	public void testServeResourceWithoutSubtype() throws Exception {
		JSONObject jsonObject = _serveResource(
			_createContentDashboardBlogItem());

		Assert.assertEquals(StringPool.BLANK, jsonObject.getString("subType"));
	}

	@Test
	public void testServeResourceWithoutUser() throws Exception {
		JSONObject jsonObject = _serveResource(
			_createContentDashboardFileItem());

		User user = TestPropsValues.getUser();

		JSONObject userJSONObject = jsonObject.getJSONObject("user");

		Assert.assertEquals(
			user.getFullName(), userJSONObject.getString("name"));
		Assert.assertEquals(user.getUserId(), userJSONObject.getLong("userId"));

		Assert.assertEquals(StringPool.BLANK, userJSONObject.getString("url"));
	}

	private void _assertContentDashboardItemLatestVersions(
		ContentDashboardItem<?> contentDashboardItem, JSONObject jsonObject) {

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			contentDashboardItem.getLatestContentDashboardItemVersions(
				LocaleUtil.US);

		JSONArray actualJSONArray = jsonObject.getJSONArray("latestVersions");

		for (int i = 0; i < actualJSONArray.length(); i++) {
			JSONObject actualJSONObject = actualJSONArray.getJSONObject(i);

			ContentDashboardItemVersion contentDashboardItemVersion =
				contentDashboardItemVersions.get(i);

			JSONObject expectedJSONObject =
				contentDashboardItemVersion.toJSONObject();

			Assert.assertEquals(
				expectedJSONObject.getString("statusLabel"),
				actualJSONObject.getString("statusLabel"));
			Assert.assertEquals(
				expectedJSONObject.getString("statusStyle"),
				actualJSONObject.getString("statusStyle"));
			Assert.assertEquals(
				expectedJSONObject.getString("version"),
				actualJSONObject.getString("version"));
		}
	}

	private void _assertSpecificFieldsJSONArrayTitles(
		List<String> expectedTitles, JSONArray specificFieldsJSONArray) {

		Assert.assertEquals(
			expectedTitles.size(), specificFieldsJSONArray.length());

		for (int i = 0; i < expectedTitles.size(); i++) {
			JSONObject specificFieldJSONObject =
				specificFieldsJSONArray.getJSONObject(i);

			Assert.assertEquals(
				expectedTitles.get(i), specificFieldJSONObject.get("title"));
		}
	}

	private ContentDashboardItem<?> _createContentDashboardBlogItem()
		throws Exception {

		Calendar displayDateCalendar = CalendarFactoryUtil.getCalendar(
			2022, 1, 20);

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), displayDateCalendar.getTime(),
			_serviceContext);

		return _contentDashboardBlogsItemFactory.create(
			blogsEntry.getPrimaryKey());
	}

	private ContentDashboardItem<?> _createContentDashboardFileItem()
		throws Exception {

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			"Site", TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "fileName.pdf",
			"application/pdf", new byte[0], new Date(150000),
			new Date(System.currentTimeMillis() + Time.MINUTE),
			new Date(150000), _serviceContext);

		fileEntry = _dlAppLocalService.updateFileEntry(
			fileEntry.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), fileEntry.getMimeType(),
			fileEntry.getTitle(), StringUtil.randomString(),
			fileEntry.getDescription(), RandomTestUtil.randomString(),
			DLVersionNumberIncrease.MINOR, fileEntry.getContentStream(),
			fileEntry.getSize(), fileEntry.getDisplayDate(),
			fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
			_serviceContext);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());

		_dlFileEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), dlFileEntry,
			dlFileEntry.getLatestFileVersion(true),
			WorkflowConstants.STATUS_EXPIRED, _serviceContext,
			Collections.emptyMap());

		return _contentDashboardFileItemFactory.create(
			fileEntry.getPrimaryKey());
	}

	private ContentDashboardItem<?> _createContentDashboardJournalArticleItem()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _contentDashboardJournalItemFactory.create(
			journalArticle.getResourcePrimKey());
	}

	private MockLiferayResourceRequest _getMockLiferayPortletResourceRequest(
			InfoItemReference infoItemReference)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(WebKeys.LOCALE, LocaleUtil.US);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = ContentDashboardTestUtil.getThemeDisplay(
			_group);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayResourceRequest.setParameter(
			"className", infoItemReference.getClassName());

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		Assert.assertTrue(
			infoItemIdentifier instanceof ClassPKInfoItemIdentifier);

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(classPKInfoItemIdentifier.getClassPK()));

		return mockLiferayResourceRequest;
	}

	private void _initCategoryAndVocabulary() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});
		_serviceContext.setAssetTagNames(new String[] {"tag1"});
	}

	private JSONObject _serveResource(
			ContentDashboardItem<?> contentDashboardItem)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayPortletResourceRequest(
				contentDashboardItem.getInfoItemReference());

		TestMockLiferayResourceResponse mockLiferayResourceResponse =
			new TestMockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());
	}

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.blogs.internal.item.BlogsEntryContentDashboardItemFactory"
	)
	private ContentDashboardItemFactory<?> _contentDashboardBlogsItemFactory;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.document.library.internal.item.FileEntryContentDashboardItemFactory"
	)
	private ContentDashboardItemFactory<?> _contentDashboardFileItemFactory;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.journal.internal.item.JournalArticleContentDashboardItemFactory"
	)
	private ContentDashboardItemFactory<?> _contentDashboardJournalItemFactory;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject(
		filter = "mvc.command.name=/content_dashboard/get_content_dashboard_item_info"
	)
	private MVCResourceCommand _mvcResourceCommand;

	private ServiceContext _serviceContext;

	private static class TestLiferayPortletURL extends MockLiferayPortletURL {

		@Override
		public MutableResourceParameters getResourceParameters() {
			return new MutableResourceParameters() {

				@Override
				public MutablePortletParameters add(
					PortletParameters portletParameters) {

					return null;
				}

				@Override
				public void clear() {
				}

				@Override
				public MutableResourceParameters clone() {
					return null;
				}

				@Override
				public Set<String> getNames() {
					return null;
				}

				@Override
				public String getValue(String s) {
					return null;
				}

				@Override
				public String[] getValues(String s) {
					return new String[0];
				}

				@Override
				public boolean isEmpty() {
					return false;
				}

				@Override
				public boolean removeParameter(String s) {
					return false;
				}

				@Override
				public MutablePortletParameters set(
					PortletParameters portletParameters) {

					return null;
				}

				@Override
				public String setValue(String s, String s1) {
					return null;
				}

				@Override
				public String[] setValues(String s, String... strings) {
					return new String[0];
				}

				@Override
				public int size() {
					return 0;
				}

			};
		}

	}

	private static class TestMockLiferayResourceResponse
		extends MockLiferayResourceResponse {

		@Override
		public ResourceURL createResourceURL() {
			return new TestLiferayPortletURL();
		}

	}

}