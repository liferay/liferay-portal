/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.result.display.context.builder;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentHelper;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.result.SearchResultContributor;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.internal.summary.SummaryBuilderFactoryImpl;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.internal.util.SearchUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Lino Alves
 * @author André de Oliveira
 */
public class SearchResultSummaryDisplayContextBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_searchResultContributorServiceRegistration =
			bundleContext.registerService(
				SearchResultContributor.class,
				Mockito.mock(SearchResultContributor.class), null);
	}

	@AfterClass
	public static void tearDownClass() {
		if (_searchResultContributorServiceRegistration != null) {
			_searchResultContributorServiceRegistration.unregister();
		}
	}

	@Before
	public void setUp() throws Exception {
		_setUpAssetRenderer();
		_setUpAssetRendererFactory();
		_setUpGroupLocalService();
		_setUpLocaleThreadLocal();
		_setUpUser();
		_setUpUserLocalService();

		themeDisplay = _createThemeDisplay();
	}

	@After
	public void tearDown() {
		searchUtilMockedStatic.close();
	}

	@Test
	public void testClassFieldsWithoutAssetTagsOrCategories() throws Exception {
		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.doReturn(
			portletURL
		).when(
			portletURLFactory
		).getPortletURL();

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(_createDocument());

		Assert.assertEquals(
			className, searchResultSummaryDisplayContext.getClassName());
		Assert.assertEquals(
			classPK, searchResultSummaryDisplayContext.getClassPK());
		Assert.assertEquals(
			portletURL, searchResultSummaryDisplayContext.getPortletURL());
	}

	@Test
	public void testCreationDate() throws Exception {
		Document document = _createDocument();

		_assertCreationDateMissing(document);

		document.addKeyword(Field.CREATE_DATE, "20180425171442");

		_assertCreationDate(document, "Apr 25, 18, 5:14 PM");

		_assertCreationDate(
			document, "25 de abr. de 18 17:14", LocaleUtil.BRAZIL);
		_assertCreationDate(document, "18年4月25日 下午5:14", LocaleUtil.CHINA);
		_assertCreationDate(document, "25.04.18, 17:14", LocaleUtil.GERMANY);
		_assertCreationDate(document, "18. ápr. 25. 17:14", LocaleUtil.HUNGARY);
		_assertCreationDate(document, "25 apr 18, 17:14", LocaleUtil.ITALY);
		_assertCreationDate(document, "18/04/25 17:14", LocaleUtil.JAPAN);
		_assertCreationDate(
			document, "25 apr. 18 17:14", LocaleUtil.NETHERLANDS);
		_assertCreationDate(document, "25 abr 18 17:14", LocaleUtil.SPAIN);
	}

	@Test
	public void testNoStagingLabel() throws Exception {
		_setUpGroup(false);

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(_createDocument());

		Assert.assertEquals(
			_SUMMARY_TITLE,
			searchResultSummaryDisplayContext.getHighlightedTitle());
	}

	@Test
	public void testResultIsTemporarilyUnavailable() throws Exception {
		_ruinAssetRendererFactoryLookup();

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(Mockito.mock(Document.class));

		Assert.assertTrue(
			searchResultSummaryDisplayContext.isTemporarilyUnavailable());
	}

	@Test
	public void testStagingLabel() throws Exception {
		_setUpGroup(true);
		_setUpLanguage("staged");

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(_createDocument());

		Assert.assertEquals(
			_SUMMARY_TITLE + " (staged)",
			searchResultSummaryDisplayContext.getHighlightedTitle());
	}

	@Test
	public void testTagsURLDownloadAndUserPortraitFromResult()
		throws Exception {

		_setUpIndexerRegistry();

		long userId = RandomTestUtil.randomLong();

		_whenAssetEntryLocalServiceFetchEntry(
			_createAssetEntryWithTagsPresent(userId));

		_whenAssetRendererFactoryHasPermission(true);

		String urlDownload = RandomTestUtil.randomString();

		_whenAssetRendererGetURLDownload(assetRenderer, urlDownload);

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(_createDocument());

		_assertAssetRendererURLDownloadVisible(
			searchResultSummaryDisplayContext, urlDownload);
		_assertTagsVisible(classPK, searchResultSummaryDisplayContext);
		_assertUserPortraitVisible(searchResultSummaryDisplayContext, userId);
	}

	@Test
	public void testURLDownloadHiddenFromResult() throws Exception {
		_setUpIndexerRegistry();

		_whenAssetEntryLocalServiceFetchEntry(
			_createAssetEntryWithTagsPresent(RandomTestUtil.randomLong()));
		_whenAssetRendererFactoryHasPermission(false);

		String urlDownload = RandomTestUtil.randomString();

		_whenAssetRendererGetURLDownload(assetRenderer, urlDownload);

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(_createDocument());

		Assert.assertEquals(
			urlDownload,
			searchResultSummaryDisplayContext.getAssetRendererURLDownload());
		Assert.assertFalse(
			searchResultSummaryDisplayContext.
				isAssetRendererURLDownloadVisible());
	}

	@Test
	public void testUserPortraitFromResultButTagsAndURLDownloadFromRoot()
		throws Exception {

		_setUpIndexerRegistry();

		long classPK = RandomTestUtil.randomInt(2, Integer.MAX_VALUE);

		_whenAssetRendererFactoryGetAssetRenderer(assetRenderer, classPK);

		long userId = RandomTestUtil.randomInt(2, Integer.MAX_VALUE);

		_whenAssetEntryLocalServiceFetchEntry(
			_createAssetEntry(userId), classPK);

		long rootClassPK = classPK - 1;

		_whenAssetEntryLocalServiceFetchEntry(
			_createAssetEntryWithTagsPresent(userId - 1), rootClassPK);

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		_whenAssetRendererFactoryGetAssetRenderer(assetRenderer, rootClassPK);

		_whenAssetRendererFactoryHasPermission(true);

		String urlDownload = RandomTestUtil.randomString();

		_whenAssetRendererGetURLDownload(assetRenderer, urlDownload);

		Document document = _createDocument(classPK);

		document.addKeyword(Field.ROOT_ENTRY_CLASS_PK, rootClassPK);

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(document);

		_assertAssetRendererURLDownloadVisible(
			searchResultSummaryDisplayContext, urlDownload);
		_assertTagsVisible(rootClassPK, searchResultSummaryDisplayContext);
		_assertUserPortraitVisible(searchResultSummaryDisplayContext, userId);
	}

	@Test
	public void testViewURL1() throws Exception {
		long classNameId = RandomTestUtil.randomLong();

		_setUpClassNameLocalService(classNameId, RandomTestUtil.randomString());

		_setUpSearchUtilMockedStatic(className, classPK);

		Document document = _createDocument();

		document.addKeyword(Field.CLASS_NAME_ID, classNameId);
		document.addKeyword(Field.CLASS_PK, RandomTestUtil.randomLong());

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(document);

		Assert.assertEquals(
			className + classPK,
			searchResultSummaryDisplayContext.getViewURL());
	}

	@Test
	public void testViewURL2() throws Exception {
		String className = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();

		_setUpClassNameLocalService(classNameId, className);

		long classPK = RandomTestUtil.randomLong();

		_setUpSearchUtilMockedStatic(className, classPK);

		_whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(
			className);

		Document document = _createDocument();

		document.addKeyword(Field.CLASS_NAME_ID, classNameId);
		document.addKeyword(Field.CLASS_PK, classPK);

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(document);

		Assert.assertEquals(
			className + classPK,
			searchResultSummaryDisplayContext.getViewURL());
	}

	protected SearchResultSummaryDisplayContext build(Document document)
		throws Exception {

		SearchResultSummaryDisplayContextBuilder
			searchResultSummaryDisplayContextBuilder =
				_createSearchResultSummaryDisplayContextBuilder();

		searchResultSummaryDisplayContextBuilder.setDocument(document);

		return searchResultSummaryDisplayContextBuilder.build();
	}

	protected AssetEntryLocalService assetEntryLocalService = Mockito.mock(
		AssetEntryLocalService.class);
	protected AssetRenderer<?> assetRenderer = Mockito.mock(
		AssetRenderer.class);
	protected AssetRendererFactory<?> assetRendererFactory = Mockito.mock(
		AssetRendererFactory.class);
	protected AssetRendererFactoryLookup assetRendererFactoryLookup =
		Mockito.mock(AssetRendererFactoryLookup.class);
	protected String className;
	protected ClassNameLocalService classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	protected long classPK;
	protected FastDateFormatFactory fastDateFormatFactory =
		new FastDateFormatFactoryImpl();
	protected Group group = Mockito.mock(Group.class);
	protected GroupLocalService groupLocalService = Mockito.mock(
		GroupLocalService.class);
	protected IndexerRegistry indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	protected Language language = Mockito.mock(Language.class);
	protected Locale locale = LocaleUtil.US;
	protected PermissionChecker permissionChecker = Mockito.mock(
		PermissionChecker.class);
	protected PortletURLFactory portletURLFactory = Mockito.mock(
		PortletURLFactory.class);
	protected MockedStatic<SearchUtil> searchUtilMockedStatic =
		Mockito.mockStatic(SearchUtil.class);
	protected ThemeDisplay themeDisplay;
	protected User user = Mockito.mock(User.class);
	protected UserLocalService userLocalService = Mockito.mock(
		UserLocalService.class);

	private void _assertAssetRendererURLDownloadVisible(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext,
		String urlDownload) {

		Assert.assertTrue(
			searchResultSummaryDisplayContext.
				isAssetRendererURLDownloadVisible());
		Assert.assertEquals(
			urlDownload,
			searchResultSummaryDisplayContext.getAssetRendererURLDownload());
	}

	private void _assertCreationDate(
			Document document, String expectedCreationDateString)
		throws Exception {

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(document);

		Assert.assertEquals(
			expectedCreationDateString,
			searchResultSummaryDisplayContext.getCreationDateString());
		Assert.assertTrue(
			searchResultSummaryDisplayContext.isCreationDateVisible());
	}

	private void _assertCreationDate(
			Document document, String expectedCreationDateString,
			Locale locale1)
		throws Exception {

		locale = locale1;

		_assertCreationDate(document, expectedCreationDateString);
	}

	private void _assertCreationDateMissing(Document document)
		throws Exception {

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			build(document);

		Assert.assertNull(
			searchResultSummaryDisplayContext.getCreationDateString());
		Assert.assertFalse(
			searchResultSummaryDisplayContext.isCreationDateVisible());
	}

	private void _assertTagsVisible(
		long classPK,
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		Assert.assertTrue(
			searchResultSummaryDisplayContext.isAssetCategoriesOrTagsVisible());
		Assert.assertEquals(
			classPK, searchResultSummaryDisplayContext.getClassPK());
	}

	private void _assertUserPortraitVisible(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext,
		long userId) {

		Assert.assertTrue(
			searchResultSummaryDisplayContext.isUserPortraitVisible());
		Assert.assertEquals(
			userId, searchResultSummaryDisplayContext.getAssetEntryUserId());
	}

	private AssetEntry _createAssetEntry(long userId) {
		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.doReturn(
			assetRenderer
		).when(
			assetEntry
		).getAssetRenderer();

		Mockito.doReturn(
			assetRendererFactory
		).when(
			assetEntry
		).getAssetRendererFactory();

		Mockito.doReturn(
			userId
		).when(
			assetEntry
		).getUserId();

		return assetEntry;
	}

	private AssetEntry _createAssetEntryWithTagsPresent(long userId) {
		AssetEntry assetEntry = _createAssetEntry(userId);

		Mockito.doReturn(
			new String[] {RandomTestUtil.randomString()}
		).when(
			assetEntry
		).getTagNames();

		return assetEntry;
	}

	private Document _createDocument() {
		return _createDocument(classPK);
	}

	private Document _createDocument(long classPK) {
		Document document = new DocumentImpl();

		DocumentHelper documentHelper = new DocumentHelper(document);

		documentHelper.setEntryKey(className, classPK);

		return document;
	}

	private Indexer<?> _createIndexer() throws Exception {
		Indexer<?> indexer = Mockito.mock(Indexer.class);

		Mockito.doReturn(
			new Summary(LocaleUtil.US, null, null)
		).when(
			indexer
		).getSummary(
			Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any()
		);

		return indexer;
	}

	private SearchResultSummaryDisplayContextBuilder
		_createSearchResultSummaryDisplayContextBuilder() {

		SearchResultSummaryDisplayContextBuilder
			searchResultSummaryDisplayContextBuilder =
				new SearchResultSummaryDisplayContextBuilder();

		searchResultSummaryDisplayContextBuilder.setAssetEntryLocalService(
			assetEntryLocalService);
		searchResultSummaryDisplayContextBuilder.setAssetRendererFactoryLookup(
			assetRendererFactoryLookup);
		searchResultSummaryDisplayContextBuilder.setClassNameLocalService(
			classNameLocalService);
		searchResultSummaryDisplayContextBuilder.setFastDateFormatFactory(
			fastDateFormatFactory);
		searchResultSummaryDisplayContextBuilder.setGroupLocalService(
			groupLocalService);
		searchResultSummaryDisplayContextBuilder.setIndexerRegistry(
			indexerRegistry);
		searchResultSummaryDisplayContextBuilder.setLanguage(language);
		searchResultSummaryDisplayContextBuilder.setLocale(locale);
		searchResultSummaryDisplayContextBuilder.setPortletURLFactory(
			portletURLFactory);
		searchResultSummaryDisplayContextBuilder.setResourceActions(
			Mockito.mock(ResourceActions.class));
		searchResultSummaryDisplayContextBuilder.setSearchResultPreferences(
			Mockito.mock(SearchResultPreferences.class));
		searchResultSummaryDisplayContextBuilder.setSummaryBuilderFactory(
			new SummaryBuilderFactoryImpl());
		searchResultSummaryDisplayContextBuilder.setThemeDisplay(themeDisplay);
		searchResultSummaryDisplayContextBuilder.setUserLocalService(
			userLocalService);

		return searchResultSummaryDisplayContextBuilder;
	}

	private ThemeDisplay _createThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(Mockito.mock(Company.class));
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setUser(Mockito.mock(User.class));

		return themeDisplay;
	}

	private void _ruinAssetRendererFactoryLookup() {
		Mockito.doThrow(
			RuntimeException.class
		).when(
			assetRendererFactoryLookup
		).getAssetRendererFactoryByClassName(
			Mockito.anyString()
		);
	}

	private void _setUpAssetRenderer() throws Exception {
		Mockito.doReturn(
			_SUMMARY_CONTENT
		).when(
			assetRenderer
		).getSearchSummary(
			Mockito.nullable(Locale.class)
		);

		Mockito.doReturn(
			_SUMMARY_TITLE
		).when(
			assetRenderer
		).getTitle(
			Mockito.nullable(Locale.class)
		);
	}

	private void _setUpAssetRendererFactory() throws Exception {
		classPK = RandomTestUtil.randomLong();

		_whenAssetRendererFactoryGetAssetRenderer(assetRenderer, classPK);

		className = RandomTestUtil.randomString();

		_whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(
			className);
	}

	private void _setUpClassNameLocalService(long classNameId, String value)
		throws Exception {

		ClassName className = Mockito.mock(ClassName.class);

		Mockito.doReturn(
			value
		).when(
			className
		).getClassName();

		Mockito.doReturn(
			className
		).when(
			classNameLocalService
		).getClassName(
			Mockito.eq(classNameId)
		);
	}

	private void _setUpGroup(boolean stagingGroup) throws Exception {
		Mockito.doReturn(
			stagingGroup
		).when(
			group
		).isStagingGroup();
	}

	private void _setUpGroupLocalService() {
		Mockito.doReturn(
			group
		).when(
			groupLocalService
		).fetchGroup(
			Mockito.anyLong()
		);
	}

	private void _setUpIndexerRegistry() throws Exception {
		Mockito.doReturn(
			_createIndexer()
		).when(
			indexerRegistry
		).getIndexer(
			className
		);
	}

	private void _setUpLanguage(String string) {
		Mockito.doReturn(
			string
		).when(
			language
		).get(
			Mockito.nullable(HttpServletRequest.class), Mockito.anyString()
		);
	}

	private void _setUpLocaleThreadLocal() {
		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);
	}

	private void _setUpSearchUtilMockedStatic(String className, long classPK) {
		searchUtilMockedStatic.when(
			() -> SearchUtil.getSearchResultViewURL(
				Mockito.any(), Mockito.any(), Mockito.eq(className),
				Mockito.eq(classPK), Mockito.eq(false), Mockito.isNull())
		).thenReturn(
			className + classPK
		);
	}

	private void _setUpUser() throws Exception {
		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			user
		).getPortraitURL(
			Mockito.any()
		);

		Mockito.doReturn(
			RandomTestUtil.randomLong()
		).when(
			user
		).getPortraitId();
	}

	private void _setUpUserLocalService() {
		Mockito.doReturn(
			user
		).when(
			userLocalService
		).fetchUser(
			Mockito.anyLong()
		);
	}

	private void _whenAssetEntryLocalServiceFetchEntry(AssetEntry assetEntry) {
		_whenAssetEntryLocalServiceFetchEntry(assetEntry, classPK);
	}

	private void _whenAssetEntryLocalServiceFetchEntry(
		AssetEntry assetEntry, long classPK) {

		Mockito.doReturn(
			assetEntry
		).when(
			assetEntryLocalService
		).fetchEntry(
			className, classPK
		);
	}

	private void _whenAssetRendererFactoryGetAssetRenderer(
			AssetRenderer<?> assetRenderer, long classPK)
		throws Exception {

		Mockito.doReturn(
			assetRenderer
		).when(
			assetRendererFactory
		).getAssetRenderer(
			classPK
		);
	}

	private void _whenAssetRendererFactoryHasPermission(boolean hasPermission)
		throws Exception {

		Mockito.doReturn(
			hasPermission
		).when(
			assetRendererFactory
		).hasPermission(
			Mockito.any(), Mockito.anyLong(), Mockito.anyString()
		);
	}

	private void
		_whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(
			String className) {

		Mockito.doReturn(
			assetRendererFactory
		).when(
			assetRendererFactoryLookup
		).getAssetRendererFactoryByClassName(
			className
		);
	}

	private void _whenAssetRendererGetURLDownload(
		AssetRenderer<?> assetRenderer, String urlDownload) {

		Mockito.doReturn(
			urlDownload
		).when(
			assetRenderer
		).getURLDownload(
			themeDisplay
		);
	}

	private static final String _SUMMARY_CONTENT =
		RandomTestUtil.randomString();

	private static final String _SUMMARY_TITLE = RandomTestUtil.randomString();

	private static ServiceRegistration<SearchResultContributor>
		_searchResultContributorServiceRegistration;

}