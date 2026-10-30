/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.StyleBook;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.resource.v1_0.StyleBookResource;
import com.liferay.headless.admin.site.client.scope.Scope;
import com.liferay.headless.admin.site.resource.v1_0.test.util.DesignLibraryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ProblemExceptionTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;
import com.liferay.style.book.util.StyleBookUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 * @author Thiago Buarque
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class StyleBookResourceTest extends BaseStyleBookResourceTestCase {

	@Override
	@Test
	public void testDeleteSiteStyleBook() throws Exception {
		super.testDeleteSiteStyleBook();

		_testDeleteSiteStyleBookForNonexistingStyleBook();
		_testDeleteSiteStyleBookWithDesignLibraryExternalReferenceCode();
	}

	@Override
	@Test
	public void testGetDesignLibraryStyleBooksPage() throws Exception {
		super.testGetDesignLibraryStyleBooksPage();

		_testGetDesignLibraryStyleBooksPageWithSiteExternalReferenceCode();
		_testGetDesignLibraryStyleBooksPageWithSortName();
	}

	@Override
	@Test
	public void testGetSitePageSpecificationStyleBooksPage() throws Exception {
		super.testGetSitePageSpecificationStyleBooksPage();

		_styleBookEntryLocalService.deleteStyleBookEntries(
			testGroup.getGroupId());

		_testGetSitePageSpecificationStyleBooksPage();
		_testGetSitePageSpecificationStyleBooksPageWithoutPermission();
		_testGetSitePageSpecificationStyleBooksPageWithLayoutTypes();
		_testGetSitePageSpecificationStyleBooksPageWithSearch();
		_testGetSitePageSpecificationStyleBooksPageWithUnknownPageSpecification();
	}

	@Override
	@Test
	public void testGetSiteStyleBook() throws Exception {
		super.testGetSiteStyleBook();

		_testGetSiteStyleBookForNonexistingStyleBook();
		_testGetSiteStyleBookWithDesignLibraryExternalReferenceCode();
	}

	@Override
	@Test
	public void testGetSiteStyleBooksPage() throws Exception {
		super.testGetSiteStyleBooksPage();

		_testGetSiteStyleBooksPageWithDefaultSort();
		_testGetSiteStyleBooksPageWithDesignLibraryExternalReferenceCode();
		_testGetSiteStyleBooksPageWithSearch();
		_testGetSiteStyleBooksPageWithSortName();
		_testGetSiteStyleBooksPageWithStaleDocument();
	}

	@Override
	@Test
	public void testPatchDesignLibraryStyleBook() throws Exception {
		_testPatchDesignLibraryStyleBook();
		_testPatchDesignLibraryStyleBookForNonexistingStyleBook();
		_testPatchDesignLibraryStyleBookWithoutFrontendTokenDefinition();
	}

	@Override
	@Test
	public void testPatchSiteStyleBook() throws Exception {
		_testPatchSiteStyleBook();
		_testPatchSiteStyleBookForNonexistingStyleBook();
		_testPatchSiteStyleBookWithoutFrontendTokenDefinition();
	}

	@Override
	@Test
	public void testPostSiteStyleBook() throws Exception {
		super.testPostSiteStyleBook();

		_testPostSiteStyleBookWithBlankThemeId();
		_testPostSiteStyleBookWithDesignLibraryExternalReferenceCode();
		_testPostSiteStyleBookWithDuplicateExternalReferenceCode();
		_testPostSiteStyleBookWithDuplicateFrontendToken();
		_testPostSiteStyleBookWithDuplicateKey();
		_testPostSiteStyleBookWithDuplicateName();
		_testPostSiteStyleBookWithFrontendTokenDefinition();
		_testPostSiteStyleBookWithInvalidFrontendTokenDefinition();
		_testPostSiteStyleBookWithInvalidFrontendTokensValues();
		_testPostSiteStyleBookWithMalformedFrontendTokensValues();
		_testPostSiteStyleBookWithNullDefaultStyleBook();
	}

	@Override
	@Test
	public void testPutDesignLibraryStyleBook() throws Exception {
		super.testPutDesignLibraryStyleBook();

		_testPutDesignLibraryStyleBookWithFrontendTokenDefinition();
		_testPutDesignLibraryStyleBookWithoutFrontendTokenDefinition();
	}

	@Override
	@Test
	public void testPutSiteStyleBook() throws Exception {
		super.testPutSiteStyleBook();

		_testPutSiteStyleBookWithDesignLibraryExternalReferenceCode();
		_testPutSiteStyleBookWithFrontendTokenDefinition();
		_testPutSiteStyleBookWithInvalidFrontendTokenDefinition();
		_testPutSiteStyleBookWithNullDefaultStyleBook();
		_testPutSiteStyleBookWithoutFrontendTokenDefinition();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"frontendTokenDefinition", "key", "name", "themeId"
		};
	}

	@Override
	protected StyleBook randomStyleBook() throws Exception {
		StyleBook styleBook = super.randomStyleBook();

		String frontendTokenName = RandomTestUtil.randomString();

		styleBook.setFrontendTokenDefinition(
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				frontendTokenName));
		styleBook.setFrontendTokensValues(
			JSONUtil.put(
				frontendTokenName,
				JSONUtil.put("value", RandomTestUtil.randomString())
			).toString());

		return styleBook;
	}

	@Override
	protected StyleBook testDeleteDesignLibraryStyleBook_addStyleBook()
		throws Exception {

		return styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());
	}

	@Override
	protected String
			testDeleteDesignLibraryStyleBook_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	@Override
	protected StyleBook testGetDesignLibraryStyleBook_addStyleBook()
		throws Exception {

		return styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());
	}

	@Override
	protected String
			testGetDesignLibraryStyleBook_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	@Override
	protected StyleBook testGetDesignLibraryStyleBooksPage_addStyleBook(
			String designLibraryExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		return styleBookResource.postDesignLibraryStyleBook(
			designLibraryExternalReferenceCode, styleBook);
	}

	@Override
	protected String
			testGetDesignLibraryStyleBooksPage_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	@Override
	protected String
			testGetDesignLibraryStyleBooksPage_getIrrelevantDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getIrrelevantDesignLibraryExternalReferenceCode();
	}

	@Override
	protected StyleBook testGetSitePageSpecificationStyleBooksPage_addStyleBook(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		styleBook.setThemeId(_getPageSpecificationThemeId());

		return styleBookResource.postSiteStyleBook(
			siteExternalReferenceCode, styleBook);
	}

	@Override
	protected String
			testGetSitePageSpecificationStyleBooksPage_getPageSpecificationExternalReferenceCode()
		throws Exception {

		Layout layout = _getPageSpecificationLayout();

		return layout.getExternalReferenceCode();
	}

	@Override
	protected StyleBook testPatchDesignLibraryStyleBook_addStyleBook()
		throws Exception {

		return styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());
	}

	@Override
	protected StyleBook testPostDesignLibraryStyleBook_addStyleBook(
			StyleBook styleBook)
		throws Exception {

		return styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), styleBook);
	}

	@Override
	protected StyleBook testPostSiteStyleBook_addStyleBook(StyleBook styleBook)
		throws Exception {

		return styleBookResource.postSiteStyleBook(
			testGroup.getExternalReferenceCode(), styleBook);
	}

	@Override
	protected StyleBook testPutDesignLibraryStyleBook_addStyleBook()
		throws Exception {

		return styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());
	}

	@Override
	protected String
			testPutDesignLibraryStyleBook_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	private Group _addConnectedDesignLibraryGroup() throws Exception {
		Group group = DesignLibraryTestUtil.addDesignLibraryGroup(
			testGroup.getGroupId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			group.getClassPK(), testGroup.getGroupId());

		return group;
	}

	private StyleBookEntry _addStyleBookEntry(
			long groupId, String themeId, String name)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), groupId, false, null, null, name,
			null, themeId,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private void _assertFrontendTokens(
			StyleBook actualStyleBook, StyleBook expectedStyleBook)
		throws Exception {

		Assert.assertEquals(
			expectedStyleBook.getFrontendTokenDefinition(),
			actualStyleBook.getFrontendTokenDefinition());

		JSONObject actualFrontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				actualStyleBook.getFrontendTokensValues());
		JSONObject expectedFrontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				expectedStyleBook.getFrontendTokensValues());

		for (String key : expectedFrontendTokensValuesJSONObject.keySet()) {
			if (!key.startsWith(
					StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM)) {

				key =
					StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM +
						StringPool.COLON + key;
			}

			Assert.assertTrue(actualFrontendTokensValuesJSONObject.has(key));
		}
	}

	private void _assertNames(
		List<String> expectedNames, Page<StyleBook> page) {

		List<String> actualNames = TransformUtil.transform(
			page.getItems(),
			styleBook -> {
				if (!expectedNames.contains(styleBook.getName())) {
					return null;
				}

				return styleBook.getName();
			});

		Assert.assertEquals(expectedNames, actualNames);
	}

	private void _assertTotalCount(
		String externalReferenceCode, Page<StyleBook> page, long totalCount) {

		Assert.assertEquals(
			externalReferenceCode, totalCount, page.getTotalCount());
	}

	private String _getDesignLibraryExternalReferenceCode() throws Exception {
		if (_designLibraryGroup == null) {
			_designLibraryGroup = DesignLibraryTestUtil.addDesignLibraryGroup(
				testGroup.getGroupId());
		}

		return _designLibraryGroup.getExternalReferenceCode();
	}

	private String _getIrrelevantDesignLibraryExternalReferenceCode()
		throws Exception {

		if (_irrelevantDesignLibraryGroup == null) {
			_irrelevantDesignLibraryGroup =
				DesignLibraryTestUtil.addDesignLibraryGroup(
					testGroup.getGroupId());
		}

		return _irrelevantDesignLibraryGroup.getExternalReferenceCode();
	}

	private Layout _getPageSpecificationLayout() throws Exception {
		if (_pageSpecificationLayout == null) {
			_pageSpecificationLayout = LayoutTestUtil.addTypeContentLayout(
				testGroup);
		}

		return _pageSpecificationLayout;
	}

	private String _getPageSpecificationThemeId() throws Exception {
		Layout layout = _getPageSpecificationLayout();

		Theme theme = layout.getTheme();

		return theme.getThemeId();
	}

	private void _testDeleteSiteStyleBookForNonexistingStyleBook()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.deleteSiteStyleBook(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testDeleteSiteStyleBookWithDesignLibraryExternalReferenceCode()
		throws Exception {

		StyleBook styleBook = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() -> styleBookResource.deleteSiteStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				styleBook.getExternalReferenceCode()));

		assertEquals(
			styleBook,
			styleBookResource.getDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				styleBook.getExternalReferenceCode()));
	}

	private void _testGetDesignLibraryStyleBooksPageWithSiteExternalReferenceCode()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.getDesignLibraryStyleBooksPage(
				testGroup.getExternalReferenceCode(), null, null, null,
				Pagination.of(1, 10), null));
	}

	private void _testGetDesignLibraryStyleBooksPageWithSortName()
		throws Exception {

		StyleBook styleBook1 = randomStyleBook();

		styleBook1.setName("aaa" + RandomTestUtil.randomString());

		styleBook1 = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), styleBook1);

		StyleBook styleBook2 = randomStyleBook();

		styleBook2.setName("bbb" + RandomTestUtil.randomString());

		styleBook2 = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), styleBook2);

		_assertNames(
			Arrays.asList(styleBook1.getName(), styleBook2.getName()),
			styleBookResource.getDesignLibraryStyleBooksPage(
				_getDesignLibraryExternalReferenceCode(), null, null, null,
				Pagination.of(1, 100), "name:asc"));
		_assertNames(
			Arrays.asList(styleBook2.getName(), styleBook1.getName()),
			styleBookResource.getDesignLibraryStyleBooksPage(
				_getDesignLibraryExternalReferenceCode(), null, null, null,
				Pagination.of(1, 100), "name:desc"));
	}

	private void _testGetSitePageSpecificationStyleBooksPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		Theme theme = layout.getTheme();

		String themeId = theme.getThemeId();

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			testGroup.getGroupId(), themeId, "Site Style Book");

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		StyleBookEntry designLibraryStyleBookEntry = _addStyleBookEntry(
			designLibraryGroup.getGroupId(), themeId,
			"Design Library Style Book");

		_addStyleBookEntry(
			designLibraryGroup.getGroupId(), RandomTestUtil.randomString(),
			"Other Theme Style Book");

		StyleBookResource styleBookResource = StyleBookResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "scope.key,scope.label"
		).build();

		Page<StyleBook> page =
			styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), null, Pagination.of(1, 50));

		Assert.assertEquals(3, page.getTotalCount());

		List<StyleBook> items = new ArrayList<>(page.getItems());

		StyleBook styleFromThemeLibraryStyleBook = items.get(0);

		Assert.assertEquals(
			"Styles from Classic Theme",
			styleFromThemeLibraryStyleBook.getName());

		StyleBook designLibraryStyleBook = items.get(1);

		Assert.assertEquals(
			designLibraryStyleBookEntry.getName(),
			designLibraryStyleBook.getName());

		Scope scope = designLibraryStyleBook.getScope();

		Assert.assertEquals("AssetLibrary", scope.getTypeAsString());
		Assert.assertEquals(
			designLibraryGroup.getExternalReferenceCode(),
			scope.getExternalReferenceCode());
		Assert.assertEquals(
			designLibraryGroup.getName("en-US"), scope.getLabel());

		StyleBook siteStyleBook = items.get(2);

		Assert.assertEquals(
			siteStyleBookEntry.getName(), siteStyleBook.getName());

		scope = siteStyleBook.getScope();

		Assert.assertNull(scope);
	}

	private void _testGetSitePageSpecificationStyleBooksPageWithLayout(
			StyleBookEntry designLibraryStyleBookEntry,
			String pageSpecificationExternalReferenceCode,
			StyleBookEntry siteStyleBookEntry)
		throws Exception {

		List<String> names = new ArrayList<>();

		Page<StyleBook> page =
			styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode, null,
				Pagination.of(1, 50));

		for (StyleBook styleBook : page.getItems()) {
			names.add(styleBook.getName());
		}

		Assert.assertTrue(
			names.toString(),
			names.contains(designLibraryStyleBookEntry.getName()));
		Assert.assertTrue(
			names.toString(), names.contains(siteStyleBookEntry.getName()));
	}

	private void _testGetSitePageSpecificationStyleBooksPageWithLayoutTypes()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		String themeId = _getPageSpecificationThemeId();

		StyleBookEntry designLibraryStyleBookEntry = _addStyleBookEntry(
			designLibraryGroup.getGroupId(), themeId,
			RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout basicLayoutPageTemplateEntryLayout =
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext);

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			testGroup.getGroupId(), themeId, RandomTestUtil.randomString());

		_testGetSitePageSpecificationStyleBooksPageWithLayout(
			designLibraryStyleBookEntry,
			basicLayoutPageTemplateEntryLayout.getExternalReferenceCode(),
			siteStyleBookEntry);

		Layout displayPageLayoutPageTemplateEntryLayout =
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext);

		_testGetSitePageSpecificationStyleBooksPageWithLayout(
			designLibraryStyleBookEntry,
			displayPageLayoutPageTemplateEntryLayout.getExternalReferenceCode(),
			siteStyleBookEntry);

		Layout layoutUtilityPageEntryLayout =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext);

		_testGetSitePageSpecificationStyleBooksPageWithLayout(
			designLibraryStyleBookEntry,
			layoutUtilityPageEntryLayout.getExternalReferenceCode(),
			siteStyleBookEntry);

		Layout masterLayoutPageTemplateEntryLayout =
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext);

		_testGetSitePageSpecificationStyleBooksPageWithLayout(
			designLibraryStyleBookEntry,
			masterLayoutPageTemplateEntryLayout.getExternalReferenceCode(),
			siteStyleBookEntry);

		Layout typePortletLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup);

		_testGetSitePageSpecificationStyleBooksPageWithLayout(
			designLibraryStyleBookEntry,
			typePortletLayout.getExternalReferenceCode(), siteStyleBookEntry);

		LayoutPageTemplateEntry widgetPageLayoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		_testGetSitePageSpecificationStyleBooksPageWithLayout(
			designLibraryStyleBookEntry,
			widgetPageLayoutPageTemplateEntry.getExternalReferenceCode(),
			siteStyleBookEntry);
	}

	private void _testGetSitePageSpecificationStyleBooksPageWithSearch()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		Theme theme = layout.getTheme();

		String themeId = theme.getThemeId();

		String name1 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		_addStyleBookEntry(testGroup.getGroupId(), themeId, name1);
		_addStyleBookEntry(testGroup.getGroupId(), themeId, name2);

		StyleBookEntry styleFromThemeStyleBookEntry =
			StyleBookUtil.getStyleFromThemeStyleBookEntry(
				layout, LocaleUtil.getDefault());

		String styleFromThemeStyleBookEntryName =
			styleFromThemeStyleBookEntry.getName();

		Page<StyleBook> page =
			styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(),
				styleFromThemeStyleBookEntryName, Pagination.of(1, 50));

		List<StyleBook> items = new ArrayList<>(page.getItems());

		Assert.assertFalse(items.toString(), items.isEmpty());

		StyleBook styleFromThemeStyleBook = items.get(0);

		Assert.assertEquals(
			items.toString(), styleFromThemeStyleBookEntryName,
			styleFromThemeStyleBook.getName());

		page = styleBookResource.getSitePageSpecificationStyleBooksPage(
			testGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode(), name1, Pagination.of(1, 50));

		List<String> names = new ArrayList<>();

		for (StyleBook styleBook : page.getItems()) {
			names.add(styleBook.getName());
		}

		Assert.assertTrue(names.toString(), names.contains(name1));
		Assert.assertFalse(names.toString(), names.contains(name2));
		Assert.assertFalse(
			names.toString(), names.contains(styleFromThemeStyleBookEntryName));
	}

	private void _testGetSitePageSpecificationStyleBooksPageWithUnknownPageSpecification()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), null, Pagination.of(1, 10)));
	}

	private void _testGetSitePageSpecificationStyleBooksPageWithoutPermission()
		throws Exception {

		User user = UserTestUtil.addGroupUser(
			testGroup, RoleConstants.SITE_MEMBER);

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		StyleBookResource styleBookResource = StyleBookResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10)));
	}

	private void _testGetSiteStyleBookForNonexistingStyleBook()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.getSiteStyleBook(
				testGetSiteStyleBook_getSiteExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testGetSiteStyleBookWithDesignLibraryExternalReferenceCode()
		throws Exception {

		StyleBook styleBook = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() -> styleBookResource.getSiteStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				styleBook.getExternalReferenceCode()));
	}

	private void _testGetSiteStyleBooksPageWithDefaultSort() throws Exception {
		StyleBook styleBook1 = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());
		StyleBook styleBook2 = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		_assertNames(
			Arrays.asList(styleBook2.getName(), styleBook1.getName()),
			styleBookResource.getSiteStyleBooksPage(
				testGroup.getExternalReferenceCode(), null, null, null,
				Pagination.of(1, 100), null));
	}

	private void _testGetSiteStyleBooksPageWithDesignLibraryExternalReferenceCode()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() -> styleBookResource.getSiteStyleBooksPage(
				_getDesignLibraryExternalReferenceCode(), null, null, null,
				Pagination.of(1, 10), null));
	}

	private void _testGetSiteStyleBooksPageWithSearch() throws Exception {
		StyleBook styleBook = randomStyleBook();

		String prefix = StringUtil.toLowerCase(RandomTestUtil.randomString());

		styleBook.setName(prefix + " " + RandomTestUtil.randomString());

		styleBook = testPostSiteStyleBook_addStyleBook(styleBook);

		String filterString =
			"externalReferenceCode eq '" +
				styleBook.getExternalReferenceCode() + "'";

		_assertTotalCount(
			styleBook.getExternalReferenceCode(),
			styleBookResource.getSiteStyleBooksPage(
				testGroup.getExternalReferenceCode(), prefix.substring(0, 4),
				null, filterString, Pagination.of(1, 10), null),
			1);
		_assertTotalCount(
			styleBook.getExternalReferenceCode(),
			styleBookResource.getSiteStyleBooksPage(
				testGroup.getExternalReferenceCode(), prefix.substring(4), null,
				filterString, Pagination.of(1, 10), null),
			0);
	}

	private void _testGetSiteStyleBooksPageWithSortName() throws Exception {
		StyleBook styleBook1 = randomStyleBook();

		styleBook1.setName("aaa" + RandomTestUtil.randomString());

		styleBook1 = testPostSiteStyleBook_addStyleBook(styleBook1);

		StyleBook styleBook2 = randomStyleBook();

		styleBook2.setName("bbb" + RandomTestUtil.randomString());

		styleBook2 = testPostSiteStyleBook_addStyleBook(styleBook2);

		_assertNames(
			Arrays.asList(styleBook1.getName(), styleBook2.getName()),
			styleBookResource.getSiteStyleBooksPage(
				testGroup.getExternalReferenceCode(), null, null, null,
				Pagination.of(1, 100), "name:asc"));
		_assertNames(
			Arrays.asList(styleBook2.getName(), styleBook1.getName()),
			styleBookResource.getSiteStyleBooksPage(
				testGroup.getExternalReferenceCode(), null, null, null,
				Pagination.of(1, 100), "name:desc"));
	}

	private void _testGetSiteStyleBooksPageWithStaleDocument()
		throws Exception {

		StyleBook styleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.getStyleBookEntry(styleBook.getId());

		_styleBookEntryLocalService.deleteStyleBookEntry(styleBookEntry);

		Indexer<StyleBookEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(StyleBookEntry.class);

		indexer.reindex(styleBookEntry);

		Page<StyleBook> page = styleBookResource.getSiteStyleBooksPage(
			testGroup.getExternalReferenceCode(), null, null,
			"externalReferenceCode eq '" +
				styleBook.getExternalReferenceCode() + "'",
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		Collection<StyleBook> styleBooks = page.getItems();

		Assert.assertEquals(styleBooks.toString(), 0, styleBooks.size());
	}

	private void _testPatchDesignLibraryStyleBook() throws Exception {
		StyleBook postStyleBook =
			testPatchDesignLibraryStyleBook_addStyleBook();

		StyleBook randomPatchStyleBook = randomPatchStyleBook();

		StyleBook patchStyleBook =
			styleBookResource.patchDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				postStyleBook.getExternalReferenceCode(), randomPatchStyleBook);

		StyleBook expectedPatchStyleBook = postStyleBook.clone();

		BeanTestUtil.copyProperties(
			randomPatchStyleBook, expectedPatchStyleBook);

		StyleBook getStyleBook = styleBookResource.getDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(),
			patchStyleBook.getExternalReferenceCode());

		assertEquals(expectedPatchStyleBook, getStyleBook);
		assertValid(getStyleBook);
	}

	private void _testPatchDesignLibraryStyleBookForNonexistingStyleBook()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.patchDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				RandomTestUtil.randomString(), randomPatchStyleBook()));
	}

	private void _testPatchDesignLibraryStyleBookWithoutFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());

		StyleBook randomPatchStyleBook = randomPatchStyleBook();

		randomPatchStyleBook.setFrontendTokenDefinition(() -> null);

		StyleBook patchStyleBook =
			styleBookResource.patchDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				postStyleBook.getExternalReferenceCode(), randomPatchStyleBook);

		Assert.assertEquals(
			postStyleBook.getFrontendTokenDefinition(),
			patchStyleBook.getFrontendTokenDefinition());
	}

	private void _testPatchSiteStyleBook() throws Exception {
		StyleBook postStyleBook = testPatchSiteStyleBook_addStyleBook();

		StyleBook randomPatchStyleBook = randomPatchStyleBook();

		StyleBook patchStyleBook = styleBookResource.patchSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomPatchStyleBook);

		StyleBook expectedPatchStyleBook = postStyleBook.clone();

		BeanTestUtil.copyProperties(
			randomPatchStyleBook, expectedPatchStyleBook);

		StyleBook getStyleBook = styleBookResource.getSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			patchStyleBook.getExternalReferenceCode());

		assertEquals(expectedPatchStyleBook, getStyleBook);
		assertValid(getStyleBook);
	}

	private void _testPatchSiteStyleBookForNonexistingStyleBook()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> styleBookResource.patchSiteStyleBook(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomPatchStyleBook()));
	}

	private void _testPatchSiteStyleBookWithoutFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomPatchStyleBook = randomPatchStyleBook();

		randomPatchStyleBook.setFrontendTokenDefinition(() -> null);

		StyleBook patchStyleBook = styleBookResource.patchSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomPatchStyleBook);

		Assert.assertEquals(
			postStyleBook.getFrontendTokenDefinition(),
			patchStyleBook.getFrontendTokenDefinition());
	}

	private void _testPostSiteStyleBookWithBlankThemeId() throws Exception {
		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setThemeId(StringPool.BLANK);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "Theme ID must not be null",
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithDesignLibraryExternalReferenceCode()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() -> styleBookResource.postSiteStyleBook(
				_getDesignLibraryExternalReferenceCode(), randomStyleBook()));
	}

	private void _testPostSiteStyleBookWithDuplicateExternalReferenceCode()
		throws Exception {

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setExternalReferenceCode(
			postStyleBook.getExternalReferenceCode());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			_language.get(
				LocaleUtil.getDefault(),
				"this-external-reference-code-is-already-in-use"),
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithDuplicateFrontendToken()
		throws Exception {

		String frontendTokenName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			JSONFactoryUtil.createJSONObject(
				FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
					frontendTokenName));

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		frontendTokenCategoriesJSONArray.put(
			frontendTokenCategoriesJSONArray.getJSONObject(0));

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokenDefinition(
			frontendTokenDefinitionJSONObject.toString());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Frontend token \"" + frontendTokenName +
				"\" is defined more than once",
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithDuplicateKey() throws Exception {
		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setKey(postStyleBook.getKey());

		ProblemExceptionTestUtil.assertProblemException(
			"CONFLICT", "A style book with the same key already exists",
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithDuplicateName() throws Exception {
		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setName(postStyleBook.getName());

		ProblemExceptionTestUtil.assertProblemException(
			"CONFLICT", "A style book with the same name already exists",
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithFrontendTokenDefinition()
		throws Exception {

		StyleBook randomStyleBook = randomStyleBook();

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook);

		_assertFrontendTokens(postStyleBook, randomStyleBook);

		StyleBook getStyleBook = styleBookResource.getSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode());

		_assertFrontendTokens(getStyleBook, randomStyleBook);

		StyleBook postDesignLibraryStyleBook =
			styleBookResource.postDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(), getStyleBook);

		_assertFrontendTokens(postDesignLibraryStyleBook, randomStyleBook);

		Assert.assertEquals(
			getStyleBook.getFrontendTokensValues(),
			postDesignLibraryStyleBook.getFrontendTokensValues());
	}

	private void _testPostSiteStyleBookWithInvalidFrontendTokenDefinition()
		throws Exception {

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokenDefinition(
			JSONUtil.put(
				"frontendTokenCategories", RandomTestUtil.randomString()
			).toString());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "Unable to parse frontend token definition",
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithInvalidFrontendTokensValues()
		throws Exception {

		String frontendTokenName = RandomTestUtil.randomString();

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokensValues(
			JSONUtil.put(
				frontendTokenName,
				JSONUtil.put(
					"value",
					StringPool.LESS_THAN + RandomTestUtil.randomString())
			).toString());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"Frontend token value \"", randomStyleBook.getThemeId(),
				StringPool.COLON, frontendTokenName,
				"\" contains invalid characters"),
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithMalformedFrontendTokensValues()
		throws Exception {

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokensValues(RandomTestUtil.randomString());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "Unable to parse frontend tokens values",
			() -> testPostSiteStyleBook_addStyleBook(randomStyleBook));
	}

	private void _testPostSiteStyleBookWithNullDefaultStyleBook()
		throws Exception {

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setDefaultStyleBook(() -> null);

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook);

		Assert.assertFalse(postStyleBook.getDefaultStyleBook());
	}

	private void _testPutDesignLibraryStyleBookWithFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		StyleBook putStyleBook = styleBookResource.putDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomStyleBook);

		_assertFrontendTokens(putStyleBook, randomStyleBook);
		_assertFrontendTokens(
			styleBookResource.getDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				putStyleBook.getExternalReferenceCode()),
			randomStyleBook);
	}

	private void _testPutDesignLibraryStyleBookWithoutFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokenDefinition(() -> null);

		StyleBook putStyleBook = styleBookResource.putDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomStyleBook);

		Assert.assertTrue(
			Validator.isNull(putStyleBook.getFrontendTokenDefinition()));
	}

	private void _testPutSiteStyleBookWithDesignLibraryExternalReferenceCode()
		throws Exception {

		StyleBook styleBook = styleBookResource.postDesignLibraryStyleBook(
			_getDesignLibraryExternalReferenceCode(), randomStyleBook());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() -> styleBookResource.putSiteStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				styleBook.getExternalReferenceCode(), randomStyleBook()));
	}

	private void _testPutSiteStyleBookWithFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		StyleBook putStyleBook = styleBookResource.putSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomStyleBook);

		_assertFrontendTokens(putStyleBook, randomStyleBook);
		_assertFrontendTokens(
			styleBookResource.getSiteStyleBook(
				testGroup.getExternalReferenceCode(),
				putStyleBook.getExternalReferenceCode()),
			randomStyleBook);
	}

	private void _testPutSiteStyleBookWithInvalidFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokenDefinition(
			JSONUtil.put(
				"frontendTokenCategories", RandomTestUtil.randomString()
			).toString());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "Unable to parse frontend token definition",
			() -> styleBookResource.putSiteStyleBook(
				testGroup.getExternalReferenceCode(),
				postStyleBook.getExternalReferenceCode(), randomStyleBook));
	}

	private void _testPutSiteStyleBookWithNullDefaultStyleBook()
		throws Exception {

		StyleBook randomStyleBook1 = randomStyleBook();

		randomStyleBook1.setDefaultStyleBook(Boolean.FALSE);

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook1);

		StyleBook randomStyleBook2 = randomStyleBook();

		randomStyleBook2.setDefaultStyleBook(() -> null);

		StyleBook putStyleBook = styleBookResource.putSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomStyleBook2);

		Assert.assertFalse(putStyleBook.getDefaultStyleBook());
	}

	private void _testPutSiteStyleBookWithoutFrontendTokenDefinition()
		throws Exception {

		StyleBook postStyleBook = testPostSiteStyleBook_addStyleBook(
			randomStyleBook());

		StyleBook randomStyleBook = randomStyleBook();

		randomStyleBook.setFrontendTokenDefinition(() -> null);

		StyleBook putStyleBook = styleBookResource.putSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode(), randomStyleBook);

		Assert.assertTrue(
			Validator.isNull(putStyleBook.getFrontendTokenDefinition()));
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	private Group _designLibraryGroup;
	private Group _irrelevantDesignLibraryGroup;

	@Inject
	private Language _language;

	private Layout _pageSpecificationLayout;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}