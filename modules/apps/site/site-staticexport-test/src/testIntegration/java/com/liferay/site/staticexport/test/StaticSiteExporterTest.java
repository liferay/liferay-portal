/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;
import com.liferay.site.staticexport.DisplayPageStaticSiteExportEntry;
import com.liferay.site.staticexport.LayoutStaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExportReport;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;

import java.io.File;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class StaticSiteExporterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_availableLocales = LanguageUtil.getCompanyAvailableLocales(
			TestPropsValues.getCompanyId());
		_defaultLocale = LocaleUtil.getDefault();

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), _availableLocales, _defaultLocale);
	}

	@Test
	public void testExport() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			_assertStaticSiteExport(layout, staticSiteExport);
		}
	}

	@Test
	public void testExportWithAbsolutePortalURLs() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		int portalServerPort = PortalUtil.getPortalServerPort(false);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<img src=\"//127.0.0.1:", portalServerPort, _ICONS_URL,
				"\"><img src=\"http://localhost:", portalServerPort, _ICONS_URL,
				"\"><img src=\"https://LOCALHOST", _ICONS_URL, "\">"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			StaticSiteExportResource iconsStaticSiteExportResource = null;

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExport.getStaticSiteExportResources()) {

				if (_ICONS_URL.equals(staticSiteExportResource.getURL())) {
					iconsStaticSiteExportResource = staticSiteExportResource;
				}
			}

			Assert.assertNotNull(
				String.valueOf(staticSiteExport.getStaticSiteExportResources()),
				iconsStaticSiteExportResource);

			List<StaticSiteExportEntry> staticSiteExportEntries =
				staticSiteExport.getStaticSiteExportEntries();

			StaticSiteExportEntry staticSiteExportEntry =
				staticSiteExportEntries.get(0);

			String html = staticSiteExportEntry.getHTML();

			Assert.assertEquals(
				html, 3,
				StringUtil.count(
					html,
					StringBundler.concat(
						"<img src=\"/", iconsStaticSiteExportResource.getPath(),
						"\">")));
		}
	}

	@Test
	public void testExportWithExternalURL() throws Exception {
		String imageURL = StringBundler.concat(
			"http://10.0.0.1/", RandomTestUtil.randomString(), ".png");
		String linkURL =
			"https://www.liferay.com/" + RandomTestUtil.randomString();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<a href=\"", linkURL, "\">", RandomTestUtil.randomString(),
				"</a><img src=\"", imageURL, "\" />"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExport.getStaticSiteExportResources()) {

				String url = staticSiteExportResource.getURL();

				Assert.assertNotEquals(imageURL, url);
				Assert.assertNotEquals(linkURL, url);
			}

			StaticSiteExportReport staticSiteExportReport =
				staticSiteExport.getStaticSiteExportReport();

			List<String> resourceFailureURLs = TransformUtil.transform(
				staticSiteExportReport.getResourceFailures(),
				StaticSiteExportReport.Failure::getURL);

			Assert.assertEquals(
				resourceFailureURLs.toString(), List.of(imageURL),
				resourceFailureURLs);

			List<StaticSiteExportEntry> staticSiteExportEntries =
				staticSiteExport.getStaticSiteExportEntries();

			StaticSiteExportEntry staticSiteExportEntry =
				staticSiteExportEntries.get(0);

			String html = staticSiteExportEntry.getHTML();

			Assert.assertThat(
				html, CoreMatchers.containsString("href=\"" + linkURL + "\""));
			Assert.assertThat(
				html, CoreMatchers.containsString("src=\"" + imageURL + "\""));
		}
	}

	@Test
	public void testExportWithLayoutIds() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(
			layout1.fetchDraftLayout(), layout1);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(
			layout2.fetchDraftLayout(), layout2);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(layout2.getLayoutId()),
				Set.of(LocaleUtil.US))) {

			_assertStaticSiteExport(layout2, staticSiteExport);

			for (StaticSiteExportEntry staticSiteExportEntry :
					staticSiteExport.getStaticSiteExportEntries()) {

				Assert.assertNotEquals(
					layout1.getExternalReferenceCode(),
					staticSiteExportEntry.getKey());
			}
		}
	}

	@Test
	public void testExportWithLayoutPageTemplateEntry() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				PortalUtil.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		Layout contentLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = contentLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<a href=\"", PortalUtil.getPathFriendlyURLPublic(),
				_group.getFriendlyURL(), "/w/", journalArticle.getUrlTitle(),
				"\">", RandomTestUtil.randomString(), "</a>"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, contentLayout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			Map<String, StaticSiteExportEntry> staticSiteExportEntries =
				new HashMap<>();

			for (StaticSiteExportEntry staticSiteExportEntry :
					staticSiteExport.getStaticSiteExportEntries()) {

				staticSiteExportEntries.put(
					staticSiteExportEntry.getPath(), staticSiteExportEntry);
			}

			Assert.assertEquals(
				staticSiteExportEntries.toString(), 2,
				staticSiteExportEntries.size());

			String journalArticlePath =
				"en/w/" + journalArticle.getUrlTitle() + ".html";

			DisplayPageStaticSiteExportEntry displayPageStaticSiteExportEntry =
				(DisplayPageStaticSiteExportEntry)staticSiteExportEntries.get(
					journalArticlePath);

			Layout layout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			Assert.assertEquals(
				JournalArticle.class.getName(),
				displayPageStaticSiteExportEntry.getClassName());
			Assert.assertEquals(
				journalArticle.getResourcePrimKey(),
				displayPageStaticSiteExportEntry.getClassPK());
			Assert.assertEquals(
				journalArticle.getExternalReferenceCode(),
				displayPageStaticSiteExportEntry.getExternalReferenceCode());
			Assert.assertEquals(
				layout.getExternalReferenceCode(),
				displayPageStaticSiteExportEntry.
					getLayoutExternalReferenceCode());
			Assert.assertThat(
				displayPageStaticSiteExportEntry.getHTML(),
				CoreMatchers.containsString(
					journalArticle.getTitle(LocaleUtil.US)));

			StaticSiteExportEntry staticSiteExportEntry =
				staticSiteExportEntries.get(
					"en" + contentLayout.getFriendlyURL() + ".html");

			Assert.assertThat(
				staticSiteExportEntry.getHTML(),
				CoreMatchers.containsString(
					"href=\"/" + journalArticlePath + "\""));
		}
	}

	@Test
	public void testExportWithLocales() throws Exception {
		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.US);

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.US);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String englishFriendlyURL = StringBundler.concat(
			StringPool.SLASH,
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			StringPool.SLASH,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		String spanishFriendlyURL = StringBundler.concat(
			StringPool.SLASH,
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			StringPool.SLASH,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		String siteURL =
			PortalUtil.getPathFriendlyURLPublic() + _group.getFriendlyURL();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<a href=\"/es", siteURL, spanishFriendlyURL,
				"\">Spanish</a><a href=\"", siteURL, englishFriendlyURL,
				"\">Unprefixed</a>"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		LayoutTestUtil.updateFriendlyURL(
			layout,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, spanishFriendlyURL
			).put(
				LocaleUtil.US, englishFriendlyURL
			).build());

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.SPAIN, LocaleUtil.US))) {

			Map<Locale, StaticSiteExportEntry> staticSiteExportEntries =
				new HashMap<>();

			for (StaticSiteExportEntry staticSiteExportEntry :
					staticSiteExport.getStaticSiteExportEntries()) {

				staticSiteExportEntries.put(
					staticSiteExportEntry.getLocale(), staticSiteExportEntry);
			}

			Assert.assertEquals(
				staticSiteExportEntries.toString(), 2,
				staticSiteExportEntries.size());

			String englishPath = "en" + englishFriendlyURL + ".html";
			String spanishPath = "es" + spanishFriendlyURL + ".html";

			StaticSiteExportEntry spanishStaticSiteExportEntry =
				staticSiteExportEntries.get(LocaleUtil.SPAIN);

			Assert.assertEquals(
				spanishPath, spanishStaticSiteExportEntry.getPath());

			String spanishHTML = spanishStaticSiteExportEntry.getHTML();

			_assertLinks(
				spanishPath, englishPath, spanishHTML, "en_US", spanishPath);

			Assert.assertThat(
				spanishHTML,
				CoreMatchers.containsString(
					"href=\"/" + spanishPath + "\">Spanish"));
			Assert.assertThat(
				spanishHTML,
				CoreMatchers.containsString(
					"href=\"/" + spanishPath + "\">Unprefixed"));

			StaticSiteExportEntry englishStaticSiteExportEntry =
				staticSiteExportEntries.get(LocaleUtil.US);

			Assert.assertEquals(
				englishPath, englishStaticSiteExportEntry.getPath());

			String englishHTML = englishStaticSiteExportEntry.getHTML();

			_assertLinks(
				englishPath, englishPath, englishHTML, "es_ES", spanishPath);

			Assert.assertThat(
				englishHTML,
				CoreMatchers.containsString(
					"href=\"/" + spanishPath + "\">Spanish"));
			Assert.assertThat(
				englishHTML,
				CoreMatchers.containsString(
					"href=\"/" + englishPath + "\">Unprefixed"));
		}
	}

	@Test
	public void testExportWithPreviewURL() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();

		String fileEntryURL = _getPreviewURL(
			bytes, ContentTypes.IMAGE_PNG,
			RandomTestUtil.randomString() + ".png");

		String stylesheet = StringBundler.concat(
			".", RandomTestUtil.randomString(), " {background: url(",
			fileEntryURL, ");}");

		String stylesheetURL = _getPreviewURL(
			stylesheet.getBytes(StandardCharsets.UTF_8), ContentTypes.TEXT_CSS,
			RandomTestUtil.randomString() + ".css");

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<link href=\"", stylesheetURL, "\" rel=\"stylesheet\" />"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			StaticSiteExportResource fileEntryStaticSiteExportResource = null;
			StaticSiteExportResource stylesheetStaticSiteExportResource = null;

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExport.getStaticSiteExportResources()) {

				String url = staticSiteExportResource.getURL();

				if (url.equals(fileEntryURL)) {
					fileEntryStaticSiteExportResource =
						staticSiteExportResource;
				}
				else if (url.equals(stylesheetURL)) {
					stylesheetStaticSiteExportResource =
						staticSiteExportResource;
				}
			}

			Assert.assertArrayEquals(
				bytes,
				FileUtil.getBytes(fileEntryStaticSiteExportResource.getFile()));

			String path = stylesheetStaticSiteExportResource.getPath();

			Assert.assertTrue(path, path.endsWith(".css"));

			Assert.assertThat(
				FileUtil.read(stylesheetStaticSiteExportResource.getFile()),
				CoreMatchers.containsString(
					"url(/" + fileEntryStaticSiteExportResource.getPath() +
						")"));
		}
	}

	@Test
	public void testExportWithSiteInitializer() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.welcome");

			siteInitializer.initialize(_group.getGroupId());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			List<StaticSiteExportEntry> staticSiteExportEntries =
				staticSiteExport.getStaticSiteExportEntries();

			Assert.assertEquals(
				staticSiteExportEntries.toString(),
				_layoutLocalService.getLayoutsCount(_group.getGroupId(), false),
				staticSiteExportEntries.size());

			for (StaticSiteExportEntry staticSiteExportEntry :
					staticSiteExportEntries) {

				Assert.assertThat(
					staticSiteExportEntry.getHTML(),
					CoreMatchers.containsString(
						"/o/layout-common-styles/main."));
			}

			boolean image = false;

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExport.getStaticSiteExportResources()) {

				String url = staticSiteExportResource.getURL();

				if (url.startsWith("/documents/") ||
					url.startsWith("/o/adaptive-media/")) {

					image = true;
				}

				File file = staticSiteExportResource.getFile();

				Assert.assertTrue(url, file.exists());
			}

			Assert.assertTrue(
				String.valueOf(staticSiteExport.getStaticSiteExportResources()),
				image);

			StaticSiteExportReport staticSiteExportReport =
				staticSiteExport.getStaticSiteExportReport();

			List<StaticSiteExportReport.Failure> layoutFailures =
				staticSiteExportReport.getLayoutFailures();

			Assert.assertTrue(
				layoutFailures.toString(), layoutFailures.isEmpty());

			List<StaticSiteExportReport.Failure> resourceFailures =
				staticSiteExportReport.getResourceFailures();

			Assert.assertTrue(
				resourceFailures.toString(), resourceFailures.isEmpty());
		}
	}

	@Test
	public void testExportWithStylesheet() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();

		String fileEntryURL = _getFileEntryURL(
			bytes, ContentTypes.IMAGE_PNG,
			RandomTestUtil.randomString() + ".png");

		String stylesheet = StringBundler.concat(
			".", RandomTestUtil.randomString(), " {background: url(",
			fileEntryURL, "?v=1#", RandomTestUtil.randomString(), ");}");

		String stylesheetURL = _getFileEntryURL(
			stylesheet.getBytes(StandardCharsets.UTF_8), ContentTypes.TEXT_CSS,
			RandomTestUtil.randomString() + ".css");

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<link href=\"", stylesheetURL,
				"?t=1\" rel=\"stylesheet\" /><img src=\"", fileEntryURL,
				"?v=2\" />"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			StaticSiteExportResource imageStaticSiteExportResource = null;
			StaticSiteExportResource stylesheetImageStaticSiteExportResource =
				null;
			StaticSiteExportResource stylesheetStaticSiteExportResource = null;

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExport.getStaticSiteExportResources()) {

				String url = staticSiteExportResource.getURL();

				if (url.equals(fileEntryURL + "?v=1")) {
					stylesheetImageStaticSiteExportResource =
						staticSiteExportResource;
				}
				else if (url.equals(fileEntryURL + "?v=2")) {
					imageStaticSiteExportResource = staticSiteExportResource;
				}
				else if (url.equals(stylesheetURL + "?t=1")) {
					stylesheetStaticSiteExportResource =
						staticSiteExportResource;
				}
			}

			Assert.assertArrayEquals(
				bytes,
				FileUtil.getBytes(
					stylesheetImageStaticSiteExportResource.getFile()));

			Assert.assertThat(
				FileUtil.read(stylesheetStaticSiteExportResource.getFile()),
				CoreMatchers.containsString(
					"url(/" +
						stylesheetImageStaticSiteExportResource.getPath() +
							"#"));

			List<StaticSiteExportEntry> staticSiteExportEntries =
				staticSiteExport.getStaticSiteExportEntries();

			StaticSiteExportEntry staticSiteExportEntry =
				staticSiteExportEntries.get(0);

			Assert.assertThat(
				staticSiteExportEntry.getHTML(),
				CoreMatchers.containsString(
					"src=\"/" + imageStaticSiteExportResource.getPath() +
						"\""));
		}
	}

	@Test
	public void testExportWithURIFragment() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<a href=\"", PortalUtil.getPathFriendlyURLPublic(),
				_group.getFriendlyURL(), layout.getFriendlyURL(),
				"#section\">Section</a>"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			List<StaticSiteExportEntry> staticSiteExportEntries =
				staticSiteExport.getStaticSiteExportEntries();

			StaticSiteExportEntry staticSiteExportEntry =
				staticSiteExportEntries.get(0);

			Assert.assertThat(
				staticSiteExportEntry.getHTML(),
				CoreMatchers.containsString(
					StringBundler.concat(
						"href=\"/", staticSiteExportEntry.getPath(),
						"#section\"")));
		}
	}

	private FileEntry _addFileEntry(byte[] bytes, String mimeType, String title)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, title, mimeType, title,
			null, StringPool.BLANK, StringPool.BLANK, bytes, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertLinks(
		String canonicalPath, String englishPath, String html,
		String openGraphLocaleAlternate, String spanishPath) {

		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"<link href=\"/" + englishPath +
					"\" hreflang=\"en-US\" rel=\"alternate\">"));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"<link href=\"/" + spanishPath +
					"\" hreflang=\"es-ES\" rel=\"alternate\">"));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"<link href=\"/" + englishPath +
					"\" hreflang=\"x-default\" rel=\"alternate\">"));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"<link href=\"/" + canonicalPath + "\" rel=\"canonical\">"));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"<meta name=\"twitter:url\" content=\"/" + canonicalPath +
					"\">"));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				StringBundler.concat(
					"<meta content=\"", openGraphLocaleAlternate,
					"\" property=\"og:locale:alternate\">")));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"<meta property=\"og:url\" content=\"/" + canonicalPath +
					"\">"));
		Assert.assertThat(
			html,
			CoreMatchers.not(
				CoreMatchers.containsString("hreflang=\"de-DE\"")));
		Assert.assertThat(
			html,
			CoreMatchers.not(
				CoreMatchers.containsString(
					"content=\"de_DE\" property=\"og:locale:alternate\"")));
	}

	private void _assertStaticSiteExport(
		Layout layout, StaticSiteExport staticSiteExport) {

		List<StaticSiteExportEntry> staticSiteExportEntries =
			staticSiteExport.getStaticSiteExportEntries();

		Assert.assertEquals(
			staticSiteExportEntries.toString(), 1,
			staticSiteExportEntries.size());

		LayoutStaticSiteExportEntry layoutStaticSiteExportEntry =
			(LayoutStaticSiteExportEntry)staticSiteExportEntries.get(0);

		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			layoutStaticSiteExportEntry.getExternalReferenceCode());
		Assert.assertEquals(
			LocaleUtil.US, layoutStaticSiteExportEntry.getLocale());
		Assert.assertEquals(
			"en" + layout.getFriendlyURL() + ".html",
			layoutStaticSiteExportEntry.getPath());
		Assert.assertThat(
			layoutStaticSiteExportEntry.getHTML(),
			CoreMatchers.containsString(layout.getName(LocaleUtil.US)));

		List<StaticSiteExportResource> staticSiteExportResources =
			staticSiteExport.getStaticSiteExportResources();

		Assert.assertFalse(staticSiteExportResources.isEmpty());

		boolean auiResource = false;
		boolean bundleResource = false;
		boolean generatedResource = false;
		boolean liferayResource = false;
		boolean stylesheet = false;

		for (StaticSiteExportResource staticSiteExportResource :
				staticSiteExportResources) {

			String path = staticSiteExportResource.getPath();

			Assert.assertFalse(path, path.startsWith(StringPool.SLASH));
			Assert.assertFalse(path, path.contains(StringPool.QUESTION));

			String url = staticSiteExportResource.getURL();

			Assert.assertTrue(url, url.startsWith(StringPool.SLASH));

			File file = staticSiteExportResource.getFile();

			Assert.assertTrue(url, file.exists());

			if (url.contains("/__liferay__/")) {
				bundleResource = true;
			}

			if (url.startsWith("/o/frontend-js-aui-web/aui/")) {
				auiResource = true;
			}

			if (url.startsWith("/o/frontend-js-aui-web/liferay/")) {
				liferayResource = true;
			}

			if (url.contains("/layout-common-styles/")) {
				generatedResource = true;
			}

			String html = layoutStaticSiteExportEntry.getHTML();

			if (url.contains(".css") && html.contains(url)) {
				stylesheet = true;

				Assert.assertThat(
					html, CoreMatchers.containsString(StringPool.SLASH + path));
			}
		}

		Assert.assertTrue(staticSiteExportResources.toString(), auiResource);
		Assert.assertTrue(staticSiteExportResources.toString(), bundleResource);
		Assert.assertTrue(
			staticSiteExportResources.toString(), generatedResource);
		Assert.assertTrue(
			staticSiteExportResources.toString(), liferayResource);
		Assert.assertTrue(staticSiteExportResources.toString(), stylesheet);

		StaticSiteExportReport staticSiteExportReport =
			staticSiteExport.getStaticSiteExportReport();

		List<StaticSiteExportReport.Failure> layoutFailures =
			staticSiteExportReport.getLayoutFailures();

		Assert.assertTrue(layoutFailures.toString(), layoutFailures.isEmpty());

		List<StaticSiteExportReport.Failure> resourceFailures =
			staticSiteExportReport.getResourceFailures();

		Assert.assertTrue(
			resourceFailures.toString(), resourceFailures.isEmpty());
	}

	private String _getFileEntryURL(byte[] bytes, String mimeType, String title)
		throws Exception {

		FileEntry fileEntry = _addFileEntry(bytes, mimeType, title);

		return StringBundler.concat(
			"/documents/", fileEntry.getGroupId(), StringPool.SLASH,
			fileEntry.getFolderId(), StringPool.SLASH, fileEntry.getTitle(),
			StringPool.SLASH, fileEntry.getUuid());
	}

	private String _getPreviewURL(byte[] bytes, String mimeType, String title)
		throws Exception {

		FileEntry fileEntry = _addFileEntry(bytes, mimeType, title);

		return _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, false);
	}

	private static final String _ICONS_URL =
		"/o/classic-theme/images/clay/icons.svg";

	private Set<Locale> _availableLocales;
	private Locale _defaultLocale;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private StaticSiteExporter _staticSiteExporter;

}