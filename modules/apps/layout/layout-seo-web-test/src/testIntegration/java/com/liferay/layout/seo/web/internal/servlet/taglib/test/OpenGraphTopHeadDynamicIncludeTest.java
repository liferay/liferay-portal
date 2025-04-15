/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.ImageInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.type.WebImage;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.junit.After;
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
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alicia García
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class OpenGraphTopHeadDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout.setDescriptionMap(RandomTestUtil.randomLocaleStringMap());

		_layout = _layoutLocalService.updateLayout(_layout);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_serviceContext.setRequest(_getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testIncludeCustomCanonicalURL() throws Exception {
		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(
				LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()),
				"http://example.com"),
			true, Collections.emptyMap(), Collections.emptyMap(), 0, false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_serviceContext.getRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "og:url", "http://example.com");
	}

	@Test
	public void testIncludeCustomDescription() throws Exception {
		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"), true,
			Collections.singletonMap(LocaleUtil.US, "customDescription"),
			Collections.emptyMap(), 0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_serviceContext.getRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "og:description", "customDescription");
	}

	@Test
	public void testIncludeCustomMetaTags() throws Exception {
		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			_layout.getUserId(), _layout.getGroupId(),
			_layout.isPrivateLayout(), _layout.getLayoutId(), false,
			Collections.emptyMap(), false, Collections.emptyMap(),
			Collections.emptyMap(), 0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(),
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
				_layout.getGroupId(), TestPropsValues.getUserId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "property1", "content1");
		_assertMetaTag(document, "property2", "content2");
	}

	@Test
	public void testIncludeCustomMetaTagsWithDefaultValueForOtherLocale()
		throws Exception {

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			_layout.getUserId(), _layout.getGroupId(),
			_layout.isPrivateLayout(), _layout.getLayoutId(), false,
			Collections.emptyMap(), false, Collections.emptyMap(),
			Collections.emptyMap(), 0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(),
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
				_layout.getGroupId(), TestPropsValues.getUserId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(LocaleUtil.SPAIN), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "property1", "content1");
		_assertMetaTag(document, "property2", "content2");
	}

	@Test
	public void testIncludeCustomMetaTagsWithEmptyValues() throws Exception {
		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			_layout.getUserId(), _layout.getGroupId(),
			_layout.isPrivateLayout(), _layout.getLayoutId(), false,
			Collections.emptyMap(), false, Collections.emptyMap(),
			Collections.emptyMap(), 0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(),
			Collections.singletonList(
				new LayoutSEOEntryCustomMetaTagProperty(
					Collections.singletonMap(
						LocaleUtil.getSiteDefault(), StringPool.BLANK),
					"custom property")),
			ServiceContextTestUtil.getServiceContext(
				_layout.getGroupId(), TestPropsValues.getUserId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(LocaleUtil.SPAIN), mockHttpServletResponse,
			RandomTestUtil.randomString());

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertFalse(content.contains("custom content"));
		Assert.assertFalse(content.contains("custom property"));
	}

	@Test
	public void testIncludeCustomMetaTagsWithSpecialCharacters()
		throws Exception {

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			_layout.getUserId(), _layout.getGroupId(),
			_layout.isPrivateLayout(), _layout.getLayoutId(), false,
			Collections.emptyMap(), false, Collections.emptyMap(),
			Collections.emptyMap(), 0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(),
			Collections.singletonList(
				new LayoutSEOEntryCustomMetaTagProperty(
					Collections.singletonMap(
						LocaleUtil.getSiteDefault(), "& content"),
					"& property")),
			ServiceContextTestUtil.getServiceContext(
				_layout.getGroupId(), TestPropsValues.getUserId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "& property", "& content");
	}

	@Test
	public void testIncludeCustomTitle() throws Exception {
		_assertIncludeTitleAndDescription(null, "Heló");
	}

	@Test
	public void testIncludeCustomTitleAndDescription() throws Exception {
		_assertIncludeTitleAndDescription(
			"description@#$%^&*()~`1234567890", "@#$%^&*()~`1234567890title");
	}

	@Test
	public void testIncludeCustomTitleAndDescriptionForArabicTranslation()
		throws Exception {

		Locale locale = LocaleUtil.fromLanguageId("ar_SA");
		String description = "الوصف العربي";
		String title = "العنوان بالعربية";

		_assertIncludeTitleAndDescription(
			description, title, locale,
			HashMapBuilder.put(
				locale, description
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				locale, title
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build());
	}

	@Test
	public void testIncludeCustomTitleAndDescriptionForUntranslatedLanguage()
		throws Exception {

		String description = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();
		Locale locale = LocaleUtil.fromLanguageId("ar_SA");

		_assertIncludeTitleAndDescription(
			description, title, locale,
			HashMapBuilder.put(
				locale, StringPool.BLANK
			).put(
				LocaleUtil.US, description
			).build(),
			HashMapBuilder.put(
				locale, StringPool.BLANK
			).put(
				LocaleUtil.US, title
			).build());
	}

	@Test
	public void testIncludeDefaultMappedTitleAndDescription() throws Exception {
		_layout.setType(LayoutConstants.TYPE_ASSET_DISPLAY);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithMockInfoItem(
			_serviceContext.getRequest(),
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_serviceContext.getRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true));

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "og:description", "defaultMappedDescription");
		_assertMetaTag(document, "og:title", "defaultMappedTitle");
	}

	@Test
	public void testIncludeDefaultTitleAndDescription() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_serviceContext.getRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_assertMetaTag(
			document, "og:description", _layout.getDescription(LocaleUtil.US));
		_assertMetaTag(
			document, "og:title",
			StringBundler.concat(
				_layout.getName(LocaleUtil.US), " - ",
				_group.getDescriptiveName(LocaleUtil.US), " - ",
				company.getName()));
	}

	@Test
	public void testIncludeImageAltNoImage() throws Exception {
		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(),
			Collections.singletonMap(LocaleUtil.US, "Image alternative text"),
			0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoOpenGraphMetaProperty(document, "og:image:alt");
	}

	@Test
	public void testIncludeIncompleteCustomMetaTags() throws Exception {
		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			_layout.getUserId(), _layout.getGroupId(),
			_layout.isPrivateLayout(), _layout.getLayoutId(), false,
			Collections.emptyMap(), false, Collections.emptyMap(),
			Collections.emptyMap(), 0, false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(),
			Arrays.asList(
				new LayoutSEOEntryCustomMetaTagProperty(
					Collections.singletonMap(
						LocaleUtil.getSiteDefault(), "content1"),
					"property1"),
				new LayoutSEOEntryCustomMetaTagProperty(
					Collections.singletonMap(
						LocaleUtil.getSiteDefault(), StringPool.BLANK),
					"property2")),
			ServiceContextTestUtil.getServiceContext(
				_layout.getGroupId(), TestPropsValues.getUserId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "property1", "content1");
		_assertNoOpenGraphMetaProperty(document, "property2");
		_assertNoOpenGraphMetaContent(document, "content3");
	}

	@Test
	public void testIncludeInternalServerErrorResponseStatus()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setStatus(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoLinkElements(document, "alternate");
		_assertNoLinkElements(document, "canonical");
	}

	@Test
	public void testIncludeLayoutOpenGraphImage() throws Exception {
		FileEntry layoutOpenGraphImageFileEntry = _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(), Collections.emptyMap(),
			layoutOpenGraphImageFileEntry.getFileEntryId(), false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image",
			_dlurlHelper.getImagePreviewURL(
				layoutOpenGraphImageFileEntry, _getThemeDisplay()));
	}

	@Test
	public void testIncludeLayoutOpenGraphImageNoImageAlt() throws Exception {
		FileEntry layoutOpenGraphImageFileEntry = _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(), Collections.emptyMap(),
			layoutOpenGraphImageFileEntry.getFileEntryId(), false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image",
			_dlurlHelper.getImagePreviewURL(
				layoutOpenGraphImageFileEntry, _getThemeDisplay()));

		_assertNoOpenGraphMetaProperty(document, "og:image:alt");
	}

	@Test
	public void testIncludeLayoutOpenGraphImageWhenBothDefined()
		throws Exception {

		FileEntry layoutOpenGraphImageFileEntry = _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(), Collections.emptyMap(),
			layoutOpenGraphImageFileEntry.getFileEntryId(), false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileEntry siteOpenGraphImageFileEntry = _addImageFileEntry(
			"image_site.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			TestPropsValues.getUserId(), _layout.getGroupId(), true,
			Collections.emptyMap(),
			siteOpenGraphImageFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image",
			_dlurlHelper.getImagePreviewURL(
				layoutOpenGraphImageFileEntry, _getThemeDisplay()));
	}

	@Test
	public void testIncludeLayoutOpenGraphImageWhenBothDefinedLayoutImageAlt()
		throws Exception {

		FileEntry imageFileEntry = _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(),
			Collections.singletonMap(
				LocaleUtil.US, "Layout image alternative text"),
			imageFileEntry.getFileEntryId(), false, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			TestPropsValues.getUserId(), _layout.getGroupId(), true,
			Collections.singletonMap(
				LocaleUtil.US, "Site Image alternative text"),
			imageFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image",
			_dlurlHelper.getImagePreviewURL(
				imageFileEntry, _getThemeDisplay()));

		_assertMetaTag(
			document, "og:image:alt", "Layout image alternative text");
	}

	@Test
	public void testIncludeLayoutOpenGraphImageWithImageAlt() throws Exception {
		FileEntry layoutOpenGraphImageFileEntry = _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(),
			Collections.singletonMap(LocaleUtil.US, "Image alternative text"),
			layoutOpenGraphImageFileEntry.getFileEntryId(), false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image",
			_dlurlHelper.getImagePreviewURL(
				layoutOpenGraphImageFileEntry, _getThemeDisplay()));

		_assertMetaTag(document, "og:image:alt", "Image alternative text");
	}

	@Test
	public void testIncludeLink() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTag(
			document, _language.getAvailableLocales(_group.getGroupId()));
		_assertCanonicalLinkTag(
			document,
			PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout));
	}

	@Test
	public void testIncludeLinkAssetDisplayPage() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FileEntry fileEntry = _getFileEntry();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getAssetDisplayPageHttpServletRequest(fileEntry),
				mockHttpServletResponse, RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTagAssetDisplayPage(
			document, fileEntry,
			_language.getAvailableLocales(_group.getGroupId()));
		_assertCanonicalLinkTag(
			document,
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())),
				_getThemeDisplay()));
	}

	@Test
	public void testIncludeLinkAssetDisplayPageLayoutTranslatedLanguagesCompanyEnabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FileEntry fileEntry = _getFileEntry();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getAssetDisplayPageHttpServletRequest(fileEntry),
					mockHttpServletResponse, RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTagAssetDisplayPage(
			document, fileEntry,
			_getAvailableLocalesLayoutTranslatedLanguages());
		_assertCanonicalLinkTag(
			document,
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())),
				_getThemeDisplay()));
	}

	@Test
	public void testIncludeLinkAssetDisplayPageLayoutTranslatedLanguagesContentLayout()
		throws Exception {

		_layout.setType(LayoutConstants.TYPE_CONTENT);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FileEntry fileEntry = _getFileEntry();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getAssetDisplayPageHttpServletRequest(fileEntry),
				mockHttpServletResponse, RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTagAssetDisplayPage(
			document, fileEntry,
			_getAvailableLocalesLayoutTranslatedLanguages());
		_assertCanonicalLinkTag(
			document,
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())),
				_getThemeDisplay()));
	}

	@Test
	public void testIncludeLinkAssetDisplayPageLayoutTranslatedLanguagesOpenGraphDisabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FileEntry fileEntry = _getFileEntry();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getAssetDisplayPageHttpServletRequest(fileEntry),
					mockHttpServletResponse, RandomTestUtil.randomString()),
				false, false),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTagAssetDisplayPage(
			document, fileEntry,
			_language.getAvailableLocales(_group.getGroupId()));
		_assertCanonicalLinkTag(
			document,
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())),
				_getThemeDisplay()));
	}

	@Test
	public void testIncludeLinkAssetDisplayPageLayoutTranslatedLanguagesSiteEnabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FileEntry fileEntry = _getFileEntry();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getAssetDisplayPageHttpServletRequest(fileEntry),
					mockHttpServletResponse, RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertCanonicalLinkTag(
			document,
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())),
				_getThemeDisplay()));
		_assertAlternateLinkTagAssetDisplayPage(
			document, fileEntry,
			_getAvailableLocalesLayoutTranslatedLanguages());
	}

	@Test
	public void testIncludeLinkLayoutTranslatedLanguages() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertCanonicalLinkTag(
			document,
			PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout));
	}

	@Test
	public void testIncludeLinkLayoutTranslatedLanguagesContentLayoutCompanyEnabled()
		throws Exception {

		_layout.setType(LayoutConstants.TYPE_CONTENT);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertCanonicalLinkTag(
			document,
			PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout));
		_assertAlternateLinkTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
	}

	@Test
	public void testIncludeLinkLayoutTranslatedLanguagesContentLayoutSiteEnabled()
		throws Exception {

		_layout.setType(LayoutConstants.TYPE_CONTENT);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertCanonicalLinkTag(
			document,
			PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout));
	}

	@Test
	public void testIncludeLinkLayoutTranslatedLanguagesOpenGraphDisabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, false),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLinkTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertCanonicalLinkTag(
			document,
			PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout));
	}

	@Test
	public void testIncludeLocales() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _language.getAvailableLocales(_group.getGroupId()));
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesCompanyEnabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			true, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesContentLayoutCompanyEnabled()
		throws Exception {

		_layout.setType(LayoutConstants.TYPE_CONTENT);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			true, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());

		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesContentLayoutSiteEnabled()
		throws Exception {

		_layout.setType(LayoutConstants.TYPE_CONTENT);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesEnableAndDisable()
		throws Exception {

		_layout.setType(LayoutConstants.TYPE_CONTENT);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			true, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());

		mockHttpServletResponse.reset();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		document = Jsoup.parse(mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _language.getAvailableLocales(_group.getGroupId()));
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesOpenGraphDisabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, false),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoOpenGraphMetaTagElements(document);
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesPortletLayout()
		throws Exception {

		_layout.setTitle(RandomTestUtil.randomString(), LocaleUtil.SPAIN);
		_layout.setType(LayoutConstants.TYPE_PORTLET);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			true, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());

		Set<Locale> locales = new HashSet<>();

		for (String availableLanguageId : _layout.getAvailableLanguageIds()) {
			locales.add(LocaleUtil.fromLanguageId(availableLanguageId));
		}

		_assertAlternateLocalesTag(document, locales);
	}

	@Test
	public void testIncludeLocalesLayoutTranslatedLanguagesSiteEnabled()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOGroupConfiguration(
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					_getHttpServletRequest(), mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true),
			true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertAlternateLocalesTag(
			document, _getAvailableLocalesLayoutTranslatedLanguages());
		_assertMetaTag(document, "og:locale", _group.getDefaultLanguageId());
	}

	@Test
	public void testIncludeMappedImage() throws Exception {
		_layout.setType(LayoutConstants.TYPE_ASSET_DISPLAY);

		UnicodeProperties typeSettingsUnicodeProperties =
			_layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			"mapped-openGraphImage", "mappedImageFieldName");
		typeSettingsUnicodeProperties.put(
			"mapped-openGraphImageAlt", "mappedImageAltFieldName");

		_layout = _layoutLocalService.updateLayout(_layout);

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithMockInfoItem(
			httpServletRequest,
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					httpServletRequest, mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true));

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "og:image", "http://localhost:8080/imageURL");
		_assertMetaTag(document, "og:image:alt", "mappedImageAlt");
		_assertMetaTag(
			document, "og:image:url", "http://localhost:8080/imageURL");
	}

	@Test
	public void testIncludeMappedTitleAndDescription() throws Exception {
		_layout.setType(LayoutConstants.TYPE_ASSET_DISPLAY);

		UnicodeProperties typeSettingsUnicodeProperties =
			_layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			"mapped-openGraphDescription", "mappedDescriptionFieldName");
		typeSettingsUnicodeProperties.put(
			"mapped-openGraphTitle", "mappedTitleFieldName");

		_layout = _layoutLocalService.updateLayout(_layout);

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithMockInfoItem(
			httpServletRequest,
			() -> _testWithLayoutSEOCompanyConfiguration(
				() -> _dynamicInclude.include(
					httpServletRequest, mockHttpServletResponse,
					RandomTestUtil.randomString()),
				false, true));

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:description", "mappedDescriptionFieldName");
		_assertMetaTag(document, "og:title", "mappedTitleFieldName");
	}

	@Test
	public void testIncludeNotFoundResponseStatus() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoLinkElements(document, "alternate");
		_assertNoLinkElements(document, "canonical");
	}

	@Test
	public void testIncludeOpenGraphNotEnabled() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, false);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertLinkElements(document);

		_assertNoOpenGraphMetaTagElements(document);
	}

	@Test
	public void testIncludeSecureURL() throws Exception {
		FileEntry layoutOpenGraphImageFileEntry = _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(), Collections.emptyMap(),
			layoutOpenGraphImageFileEntry.getFileEntryId(), false,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image:secure_url",
			_dlurlHelper.getImagePreviewURL(
				layoutOpenGraphImageFileEntry, _getThemeDisplay()));
	}

	@Test
	public void testIncludeSiteName() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:site_name",
			_group.getDescriptiveName(
				LocaleUtil.fromLanguageId(_group.getDefaultLanguageId())));
	}

	@Test
	public void testIncludeSiteOpenGraphImage() throws Exception {
		FileEntry siteOpenGraphImageFileEntry = _addImageFileEntry(
			"image_site.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			TestPropsValues.getUserId(), _layout.getGroupId(), true,
			Collections.emptyMap(),
			siteOpenGraphImageFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:image",
			_dlurlHelper.getImagePreviewURL(
				siteOpenGraphImageFileEntry, _getThemeDisplay()));
	}

	@Test
	public void testIncludeSiteOpenGraphImageWhenOpenGraphDisabled()
		throws Exception {

		FileEntry siteOpenGraphImageFileEntry = _addImageFileEntry(
			"image_site.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			Collections.emptyMap(),
			siteOpenGraphImageFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoMetaTag(document, "og:image");
	}

	@Test
	public void testIncludeType() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(document, "og:type", "website");
	}

	@Test
	public void testIncludeUrl() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertMetaTag(
			document, "og:url",
			PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout));
	}

	@Test
	public void testMetaTagValuesAreEscaped() throws Exception {
		String xssContent = "'\"><img src=x onerror=alert()>";

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			false, Collections.emptyMap(), Collections.emptyMap(), 0, true,
			Collections.singletonMap(LocaleUtil.US, xssContent),
			_serviceContext);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			content.contains(HtmlUtil.escapeAttribute(xssContent)));
	}

	@Test
	public void testPrivateLayout() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_layout.setPrivateLayout(true);

		_dynamicInclude.include(
			_getHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoLinkElements(document, "alternate");
		_assertNoLinkElements(document, "canonical");
	}

	@Test
	public void testSignedIn() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_getSignedInHttpServletRequest(), mockHttpServletResponse,
			RandomTestUtil.randomString());

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoLinkElements(document, "alternate");
		_assertNoLinkElements(document, "canonical");
	}

	@Test
	public void testStatusErrorPage() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setStatus(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		_assertNoLinkElements(document, "alternate");
		_assertNoLinkElements(document, "canonical");
	}

	private FileEntry _addImageFileEntry(
			String fileName, ServiceContext serviceContext)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null, serviceContext);
	}

	private void _assertAlternateLinkTag(Document document, Set<Locale> locales)
		throws Exception {

		Elements alternateLinkElements = document.select(
			"link[rel='alternate']");

		Assert.assertNotNull(alternateLinkElements);

		for (Locale locale : locales) {
			Elements localeAlternateLinkElements = alternateLinkElements.select(
				"[hrefLang='" + LocaleUtil.toW3cLanguageId(locale) + "']");

			Assert.assertEquals(1, localeAlternateLinkElements.size());

			Element localeAlternateLinkElement =
				localeAlternateLinkElements.get(0);

			Assert.assertEquals(
				PortalUtil.getAlternateURL(
					PortalUtil.getCanonicalURL("", _getThemeDisplay(), _layout),
					_getThemeDisplay(), locale, _layout),
				localeAlternateLinkElement.attr("href"));
		}
	}

	private void _assertAlternateLinkTagAssetDisplayPage(
			Document document, FileEntry fileEntry, Set<Locale> locales)
		throws Exception {

		Elements alternateLinkElements = document.select(
			"link[rel='alternate']");

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (Locale locale : locales) {
			Elements localeAlternateLinkElements = alternateLinkElements.select(
				"[hrefLang='" + LocaleUtil.toW3cLanguageId(locale) + "']");

			Assert.assertEquals(1, localeAlternateLinkElements.size());

			Element localeAlternateLinkElement =
				localeAlternateLinkElements.get(0);

			Assert.assertEquals(
				_portal.getAlternateURL(
					_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
						new InfoItemReference(
							FileEntry.class.getName(),
							new ClassPKInfoItemIdentifier(
								fileEntry.getFileEntryId())),
						locale, themeDisplay),
					themeDisplay, locale, _layout),
				localeAlternateLinkElement.attr("href"));
		}
	}

	private void _assertAlternateLocalesTag(
		Document document, Set<Locale> locales) {

		Elements elements = document.select(
			"meta[property='og:locale:alternate']");

		Assert.assertNotNull(elements);
		Assert.assertEquals(
			locales.toString(), elements.size(), locales.size());

		for (Locale locale : locales) {
			Elements element = elements.select(
				"[content='" + LocaleUtil.toLanguageId(locale) + "']");

			Assert.assertEquals(1, element.size());
		}
	}

	private void _assertCanonicalLinkTag(Document document, String href) {
		Elements elements = document.select("link[rel='canonical']");

		Assert.assertNotNull(elements);
		Assert.assertEquals(1, elements.size());

		Element element = elements.get(0);

		Assert.assertEquals(href, element.attr("href"));
	}

	private void _assertIncludeTitleAndDescription(
			String description, String title)
		throws Exception {

		_assertIncludeTitleAndDescription(
			description, title,
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()),
			Collections.singletonMap(LocaleUtil.US, description),
			Collections.singletonMap(LocaleUtil.US, title));
	}

	private void _assertIncludeTitleAndDescription(
			String expectedDescription, String expectedTitle, Locale locale,
			Map<Locale, String> descriptionMap, Map<Locale, String> titleMap)
		throws Exception {

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(), true,
			Collections.singletonMap(LocaleUtil.US, "http://example.com"),
			Validator.isNotNull(expectedDescription), descriptionMap,
			Collections.emptyMap(), 0, true, titleMap,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setCharacterEncoding("UTF-8");

		_testWithLayoutSEOCompanyConfiguration(
			() -> _dynamicInclude.include(
				_getHttpServletRequest(locale), mockHttpServletResponse,
				RandomTestUtil.randomString()),
			false, true);

		Document document = Jsoup.parse(
			mockHttpServletResponse.getContentAsString());

		if (Validator.isNotNull(expectedDescription)) {
			_assertMetaTag(document, "og:description", expectedDescription);
		}
		else {
			_assertMetaTag(
				document, "og:description", _layout.getDescription(locale));
		}

		_assertMetaTag(document, "og:title", expectedTitle);
	}

	private void _assertLinkElements(Document document) {
		Elements elements = document.select("link[data-senna-track]");

		Assert.assertNotEquals(0, elements.size());
	}

	private void _assertMetaTag(
		Document document, String property, String content) {

		Elements elements = document.select(
			"meta[property='" + property + "']");

		Assert.assertNotNull(elements);
		Assert.assertEquals(1, elements.size());

		Element element = elements.get(0);

		Assert.assertEquals(content, element.attr("content"));
	}

	private void _assertNoLinkElements(Document document, String rel) {
		Elements elements = document.select("link[rel='" + rel + "']");

		Assert.assertEquals(0, elements.size());
	}

	private void _assertNoMetaTag(Document document, String property) {
		Elements elements = document.select(
			"meta[property='" + property + "']");

		Assert.assertEquals(0, elements.size());
	}

	private void _assertNoOpenGraphMetaContent(
		Document document, String content) {

		Elements elements = document.select("meta[content='" + content + "']");

		Assert.assertEquals(0, elements.size());
	}

	private void _assertNoOpenGraphMetaProperty(
		Document document, String property) {

		Elements elements = document.select(
			"meta[property='" + property + "']");

		Assert.assertEquals(0, elements.size());
	}

	private void _assertNoOpenGraphMetaTagElements(Document document) {
		Elements elements = document.select("meta[property^='og:']");

		Assert.assertEquals(0, elements.size());
	}

	private HttpServletRequest _getAssetDisplayPageHttpServletRequest(
			FileEntry fileEntry)
		throws Exception {

		String className = FileEntry.class.getName();

		long classNameId = _classNameLocalService.getClassNameId(className);

		long classPK = fileEntry.getFileEntryId();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, _group.getCreatorUserId(), _group.getGroupId(), 0, null,
				classNameId, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, 0,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			classPK, layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		InfoItemObjectProvider<?> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		Object infoItem = infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));

		httpServletRequest.setAttribute(InfoDisplayWebKeys.INFO_ITEM, infoItem);

		InfoItemDetailsProvider infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className);

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM_DETAILS,
			infoItemDetailsProvider.getInfoItemDetails(infoItem));

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(className);

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(className, classPK)));

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setRequest(httpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return httpServletRequest;
	}

	private Set<Locale> _getAvailableLocalesLayoutTranslatedLanguages()
		throws Exception {

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		if (infoItemLanguagesProvider == null) {
			return _language.getAvailableLocales(_group.getGroupId());
		}

		Set<Locale> availableLocales = new HashSet<>();

		for (String availableLanguageId :
				infoItemLanguagesProvider.getAvailableLanguageIds(_layout)) {

			availableLocales.add(
				LocaleUtil.fromLanguageId(availableLanguageId));
		}

		availableLocales.add(
			_portal.getSiteDefaultLocale(_layout.getGroupId()));

		return availableLocales;
	}

	private FileEntry _getFileEntry() throws Exception {
		return _addImageFileEntry(
			"image.jpg",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private HttpServletRequest _getHttpServletRequest() throws PortalException {
		return _getHttpServletRequest(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
	}

	private HttpServletRequest _getHttpServletRequest(Locale locale)
		throws PortalException {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(locale);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setRequestURI(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_group.getFriendlyURL() + _layout.getFriendlyURL());

		return mockHttpServletRequest;
	}

	private HttpServletRequest _getSignedInHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getSingedInThemeDisplay());
		mockHttpServletRequest.setRequestURI(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_group.getFriendlyURL() + _layout.getFriendlyURL());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getSingedInThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		themeDisplay.setSignedIn(true);

		return themeDisplay;
	}

	private ThemeDisplay _getThemeDisplay() throws PortalException {
		return _getThemeDisplay(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
	}

	private ThemeDisplay _getThemeDisplay(Locale locale)
		throws PortalException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		themeDisplay.setCompany(company);

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		themeDisplay.setLocale(locale);
		themeDisplay.setPortalDomain("localhost");
		themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSecure(true);
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private void _testWithLayoutSEOCompanyConfiguration(
			UnsafeRunnable<Exception> unsafeRunnable,
			boolean enableLayoutTranslatedLanguages, boolean enableOpenGraph)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_LAYOUT_SEO_COMPANY_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"enableLayoutTranslatedLanguages",
						enableLayoutTranslatedLanguages
					).put(
						"enableOpenGraph", enableOpenGraph
					).build())) {

			unsafeRunnable.run();
		}
	}

	private void _testWithLayoutSEOGroupConfiguration(
			UnsafeRunnable<Exception> unsafeRunnable,
			boolean enableLayoutTranslatedLanguages)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_LAYOUT_SEO_GROUP_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"enableLayoutTranslatedLanguages",
						enableLayoutTranslatedLanguages
					).build())) {

			unsafeRunnable.run();
		}
	}

	private void _testWithMockInfoItem(
			HttpServletRequest httpServletRequest,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			OpenGraphTopHeadDynamicIncludeTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<InfoItemFieldValuesProvider<?>>
			infoItemFieldValuesProviderServiceRegistration =
				bundleContext.registerService(
					(Class<InfoItemFieldValuesProvider<?>>)
						(Class<?>)InfoItemFieldValuesProvider.class,
					new MockInfoItemFieldValuesProvider(), null);

		InfoItemClassDetails infoItemClassDetails = new InfoItemClassDetails(
			MockObject.class.getName());

		InfoItemDetails infoItemDetails = new InfoItemDetails(
			infoItemClassDetails, null);

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM_DETAILS, infoItemDetails);

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			new MockLayoutDisplayPageObjectProvider());

		try {
			unsafeRunnable.run();
		}
		finally {
			infoItemFieldValuesProviderServiceRegistration.unregister();
		}
	}

	private static final String _LAYOUT_SEO_COMPANY_CONFIGURATION_PID =
		"com.liferay.layout.seo.internal.configuration." +
			"LayoutSEOCompanyConfiguration";

	private static final String _LAYOUT_SEO_GROUP_CONFIGURATION_PID =
		"com.liferay.layout.seo.internal.configuration." +
			"LayoutSEOGroupConfiguration";

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlurlHelper;

	@Inject(
		filter = "component.name=com.liferay.layout.seo.web.internal.servlet.taglib.OpenGraphTopHeadDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Inject
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	private static class MockInfoItemFieldValuesProvider
		implements InfoItemFieldValuesProvider<MockObject> {

		@Override
		public InfoItemFieldValues getInfoItemFieldValues(
			MockObject mockObject) {

			return InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"description"
					).build(),
					"<p>defaultMappedDescription</p>")
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"title"
					).build(),
					"defaultMappedTitle")
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"mappedDescriptionFieldName"
					).build(),
					"<p>mappedDescription</p>")
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"mappedTitleFieldName"
					).build(),
					"mappedTitle")
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"mappedTitleFieldName"
					).build(),
					"mappedTitle")
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						ImageInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"mappedImageFieldName"
					).build(),
					new WebImage("/imageURL"))
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"mappedImageAltFieldName"
					).build(),
					"mappedImageAlt")
			).build();
		}

	}

	private static class MockLayoutDisplayPageObjectProvider
		implements LayoutDisplayPageObjectProvider<MockObject> {

		@Override
		public long getClassNameId() {
			return PortalUtil.getClassNameId(MockObject.class);
		}

		@Override
		public long getClassPK() {
			return 0;
		}

		@Override
		public long getClassTypeId() {
			return 0;
		}

		@Override
		public String getDescription(Locale locale) {
			return null;
		}

		@Override
		public MockObject getDisplayObject() {
			return null;
		}

		@Override
		public long getGroupId() {
			return 0;
		}

		@Override
		public String getKeywords(Locale locale) {
			return null;
		}

		@Override
		public String getTitle(Locale locale) {
			return null;
		}

		@Override
		public String getURLTitle(Locale locale) {
			return null;
		}

	}

	private static class MockObject {
	}

}