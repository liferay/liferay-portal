/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.cache.FragmentEntryLinkCache;
import com.liferay.fragment.configuration.FragmentJavaScriptConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FragmentEntryFragmentRendererTest {

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

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
		_locale = _portal.getSiteDefaultLocale(_group);
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());
	}

	@Test
	@TestInfo("LPS-146373")
	public void testAddMappedFragmentEntryLinkWithPrefixURL() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addHeadingFragmentEntryLink(
			JSONUtil.put(
				"element-text",
				JSONUtil.put(
					"config",
					JSONUtil.put(
						"href",
						JSONUtil.put(
							LocaleUtil.toLanguageId(
								LocaleUtil.getSiteDefault()),
							"test@liferay.com")
					).put(
						"mapperType", "link"
					).put(
						"prefix", "mailto:"
					)
				).put(
					"defaultValue", "Heading Example"
				)));

		MockHttpServletResponse mockHttpServletResponse =
			_renderFragmentEntryLink(fragmentEntryLink);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("mailto:test@liferay.com"));
	}

	@Test
	public void testAddMappedFragmentEntryLinkWithURL() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addHeadingFragmentEntryLink(
			JSONUtil.put(
				"element-text",
				JSONUtil.put(
					"config",
					JSONUtil.put(
						"href",
						JSONUtil.put(
							LocaleUtil.toLanguageId(
								LocaleUtil.getSiteDefault()),
							"https://liferay.com")
					).put(
						"mapperType", "link"
					)
				).put(
					"defaultValue", "Heading Example"
				)));

		MockHttpServletResponse mockHttpServletResponse =
			_renderFragmentEntryLink(fragmentEntryLink);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("https://liferay.com"));
	}

	@Test
	public void testCacheableFragmentEntryLink() throws Exception {
		FragmentEntry fragmentEntry = _getFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		_renderFragmentEntryLink(fragmentEntryLink);

		String content = _fragmentEntryLinkCache.getFragmentEntryLinkContent(
			fragmentEntryLink, _locale);

		Assert.assertTrue(content.contains(fragmentEntry.getHtml()));
	}

	@Test
	public void testCacheableFragmentEntryLinkNonceAttribute()
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		String nonce = RandomTestUtil.randomString();

		_testRenderWithNonce(fragmentEntryLink, nonce);

		String content = _fragmentEntryLinkCache.getFragmentEntryLinkContent(
			fragmentEntryLink, _locale);

		Assert.assertFalse(content, content.contains(nonce));
		Assert.assertEquals(
			content, 2, StringUtil.count(content, "data-lfr-nonce"));
	}

	@Test
	@TestInfo("LPS-101333")
	public void testCannotExecuteFreeMarkerCodeInHTMLFragment()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addHTMLFragmentEntryLink(
			JSONUtil.put(
				"element-html",
				JSONUtil.put(
					"defaultValue",
					"<div class=\"fragment-html-test\">${test}</div>")));

		MockHttpServletResponse mockHttpServletResponse =
			_renderFragmentEntryLink(fragmentEntryLink);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			content.contains(
				"<div class=\"fragment-html-test\">${test}</div>"));
	}

	@Test
	public void testFragmentEntryLinkJavaScriptVariables() throws Exception {
		FragmentEntry fragmentEntry = _getFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), _read("configuration.json"), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		_renderFragmentEntryLink(fragmentEntryLink);

		String content = _fragmentEntryLinkCache.getFragmentEntryLinkContent(
			fragmentEntryLink, _locale);

		Assert.assertTrue(
			content.contains(
				"fragmentEntryLinkNamespace = '" +
					fragmentEntryLink.getNamespace()));

		Assert.assertTrue(
			content.contains(
				"fragmentNamespace = '" + fragmentEntryLink.getNamespace()));

		Assert.assertTrue(content.contains("\"buttonSize\":\"nm\""));
		Assert.assertTrue(content.contains("\"buttonType\":\"primary\""));
	}

	@Test
	public void testJavaScriptModuleConfiguration() throws Exception {
		FragmentEntry fragmentEntry = _getFragmentEntry(false);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(LocaleUtil.US);

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_fragmentRenderer.render(
			defaultFragmentRendererContext, mockHttpServletRequest,
			mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("type=\"module\""));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentJavaScriptConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"javaScriptModuleEnabled", false
						).build())) {

			mockHttpServletResponse = new MockHttpServletResponse();

			_fragmentRenderer.render(
				defaultFragmentRendererContext, mockHttpServletRequest,
				mockHttpServletResponse);

			content = mockHttpServletResponse.getContentAsString();

			Assert.assertFalse(content.contains("type=\"module\""));
		}
	}

	@Test
	@TestInfo("LPS-118276")
	public void testMapAssetVocabularyToInfoField() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		BlogsEntry blogsEntry = _addBlogsEntry();

		String fieldId = "AssetVocabulary_" + assetVocabulary.getVocabularyId();

		String title = assetCategory.getTitle(LocaleUtil.getDefault());

		_assertRenderFragmentEntryLink(
			title,
			JSONUtil.put(
				"element-text",
				JSONUtil.put(
					"className", BlogsEntry.class.getName()
				).put(
					"classNameId",
					String.valueOf(
						_portal.getClassNameId(BlogsEntry.class.getName()))
				).put(
					"classPK", String.valueOf(blogsEntry.getEntryId())
				).put(
					"classTypeId", "0"
				).put(
					"fieldId", fieldId
				)));

		FileEntry fileEntry = _addFileEntry();

		_assertRenderFragmentEntryLink(
			title,
			JSONUtil.put(
				"element-text",
				JSONUtil.put(
					"className", FileEntry.class.getName()
				).put(
					"classNameId",
					String.valueOf(
						_portal.getClassNameId(FileEntry.class.getName()))
				).put(
					"classPK", String.valueOf(fileEntry.getFileEntryId())
				).put(
					"classTypeId", "0"
				).put(
					"fieldId", fieldId
				)));

		JournalArticle journalArticle = _addJournalArticle();

		_assertRenderFragmentEntryLink(
			title,
			JSONUtil.put(
				"element-text",
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					String.valueOf(
						_portal.getClassNameId(JournalArticle.class.getName()))
				).put(
					"classPK",
					String.valueOf(journalArticle.getResourcePrimKey())
				).put(
					"classTypeId",
					String.valueOf(journalArticle.getDDMStructureId())
				).put(
					"fieldId", fieldId
				)));
	}

	@Test
	public void testNoncacheableFragmentEntryLink() throws Exception {
		FragmentEntry fragmentEntry = _getFragmentEntry(false);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		_renderFragmentEntryLink(fragmentEntryLink);

		Assert.assertNull(
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, _locale));
	}

	@Test
	public void testNoncacheableFragmentEntryLinkNonceAttribute()
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry(false);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		_testRenderWithNonce(fragmentEntryLink, RandomTestUtil.randomString());
	}

	@Test
	@TestInfo("LPD-32054")
	public void testShouldOnlyCacheFragmentEntryLinkInProductionMode()
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, FragmentEntryFragmentRendererTest.class.getSimpleName(), null);

		FragmentEntry fragmentEntry = _getFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(_locale);

		Assert.assertTrue(
			_isFragmentEntryLinkCacheable(
				fragmentEntryLink, defaultFragmentRendererContext));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Assert.assertFalse(
				_isFragmentEntryLinkCacheable(
					fragmentEntryLink, defaultFragmentRendererContext));
		}
		finally {
			_ctCollectionLocalService.deleteCTCollection(ctCollection);
		}

		Assert.assertTrue(
			_isFragmentEntryLinkCacheable(
				fragmentEntryLink, defaultFragmentRendererContext));
	}

	@Test
	@TestInfo("LPD-32929")
	public void testShouldRemoveCacheFragmentEntryLinkOnPublishPublication()
		throws Exception {

		String originalText = RandomTestUtil.randomString();

		FragmentEntryLink fragmentEntryLink = _addHeadingFragmentEntryLink(
			JSONUtil.put(
				"element-text",
				JSONUtil.put(LocaleUtil.toLanguageId(_locale), originalText)));

		_renderFragmentEntryLink(fragmentEntryLink);

		String content = _fragmentEntryLinkCache.getFragmentEntryLinkContent(
			fragmentEntryLink, _locale);

		Assert.assertTrue(content.contains(originalText));

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, FragmentEntryFragmentRendererTest.class.getSimpleName(), null);
		String updatedText = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_renderFragmentEntryLink(
				_fragmentEntryLinkLocalService.updateFragmentEntryLink(
					TestPropsValues.getUserId(),
					fragmentEntryLink.getFragmentEntryLinkId(),
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put(
							"element-text",
							JSONUtil.put(
								LocaleUtil.toLanguageId(_locale), updatedText))
					).toString()));

			String curContent =
				_fragmentEntryLinkCache.getFragmentEntryLinkContent(
					fragmentEntryLink, _locale);

			Assert.assertFalse(curContent.contains(updatedText));
			Assert.assertEquals(content, curContent);

			_fragmentEntryLinkCache.removeFragmentEntryLinkCache(
				fragmentEntryLink.getFragmentEntryLinkId());

			Assert.assertEquals(
				content,
				_fragmentEntryLinkCache.getFragmentEntryLinkContent(
					fragmentEntryLink, _locale));
		}

		Assert.assertEquals(
			content,
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, _locale));

		_ctCollectionService.publishCTCollection(
			TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

		Assert.assertNull(
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, _locale));

		_renderFragmentEntryLink(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));

		String updatedContent =
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, _locale);

		Assert.assertTrue(updatedContent.contains(updatedText));
		Assert.assertNotEquals(content, updatedContent);
	}

	@Test
	public void testUpdateCacheableFragmentEntryLink() throws Exception {
		FragmentEntry fragmentEntry = _getFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		_renderFragmentEntryLink(fragmentEntryLink);

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), "Updated Fragment Entry HTML",
			fragmentEntry.getJs(), fragmentEntry.isCacheable(),
			fragmentEntry.getConfiguration(), fragmentEntry.getIcon(), 0, false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink.getFragmentEntryLinkId());

		_renderFragmentEntryLink(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));

		String content = _fragmentEntryLinkCache.getFragmentEntryLinkContent(
			fragmentEntryLink, _locale);

		Assert.assertTrue(content.contains(fragmentEntry.getHtml()));
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			DateUtil.newDate(System.currentTimeMillis() - Time.DAY), true, true,
			new String[0], StringPool.BLANK, null, null, _serviceContext);
	}

	private FileEntry _addFileEntry() throws Exception {
		return _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + "." + ContentTypes.IMAGE_JPEG,
			MimeTypesUtil.getExtensionContentType(ContentTypes.IMAGE_JPEG),
			new byte[0], null, null, null, _serviceContext);
	}

	private FragmentEntryLink _addHeadingFragmentEntryLink(
			JSONObject jsonObject)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			fragmentEntry.getFragmentEntryId(), _defaultSegmentsExperienceId,
			_layout.getPlid(), fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				jsonObject
			).toString(),
			StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), _serviceContext);
	}

	private FragmentEntryLink _addHTMLFragmentEntryLink(JSONObject jsonObject)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-html");

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			fragmentEntry.getFragmentEntryId(), _defaultSegmentsExperienceId,
			_layout.getPlid(), fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				jsonObject
			).toString(),
			StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), _serviceContext);
	}

	private JournalArticle _addJournalArticle() throws Exception {
		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, StringPool.BLANK,
			true, _serviceContext);
	}

	private void _assertRenderFragmentEntryLink(
			String expected, JSONObject jsonObject)
		throws Exception {

		FragmentEntryLink fileHeadingFragmentEntryLink =
			_addHeadingFragmentEntryLink(jsonObject);

		MockHttpServletResponse mockHttpServletResponse =
			_renderFragmentEntryLink(fileHeadingFragmentEntryLink);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content, content.contains(expected));
	}

	private FragmentEntry _getFragmentEntry(boolean cacheable)
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), ".component{color:blue;}",
			"Fragment Entry HTML", "console.log('test');", cacheable, null,
			null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			TilesUtil.DEFINITION,
			new Definition(StringPool.BLANK, new HashMap<>()));
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setLocale(_locale);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return themeDisplay;
	}

	private boolean _isFragmentEntryLinkCacheable(
		FragmentEntryLink fragmentEntryLink,
		FragmentRendererContext defaultFragmentRendererContext) {

		return ReflectionTestUtil.invoke(
			_fragmentRenderer, "_isCacheable",
			new Class<?>[] {
				FragmentEntryLink.class, FragmentRendererContext.class
			},
			fragmentEntryLink, defaultFragmentRendererContext);
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private MockHttpServletResponse _renderFragmentEntryLink(
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(_locale);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_fragmentRenderer.render(
			defaultFragmentRendererContext, _getHttpServletRequest(),
			mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _testRenderWithNonce(
			FragmentEntryLink fragmentEntryLink, String nonce)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(_locale);

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		httpServletRequest.setAttribute(
			"com.liferay.portal.security.content.security.policy.internal." +
				"ContentSecurityPolicyNonceManager#NONCE",
			nonce);

		_fragmentRenderer.render(
			defaultFragmentRendererContext, httpServletRequest,
			mockHttpServletResponse);

		Document document = Jsoup.parseBodyFragment(
			mockHttpServletResponse.getContentAsString());

		Elements styleElements = document.getElementsByTag("style");

		Element styleElement = styleElements.get(0);

		Assert.assertEquals(nonce, styleElement.attr("nonce"));

		Elements scriptElements = document.getElementsByTag("script");

		Element scriptElement = scriptElements.get(0);

		Assert.assertEquals(nonce, scriptElement.attr("nonce"));
	}

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private CTCollectionService _ctCollectionService;

	private long _defaultSegmentsExperienceId;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkCache _fragmentEntryLinkCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.internal.renderer.FragmentEntryFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private Locale _locale;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}