/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.StyleBook;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.StyleBookResource;
import com.liferay.headless.admin.site.client.scope.Scope;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.StyleBookUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
	public void testGetDesignLibraryStyleBooksPage() throws Exception {
		super.testGetDesignLibraryStyleBooksPage();

		try {
			styleBookResource.getDesignLibraryStyleBooksPage(
				testGroup.getExternalReferenceCode(), null, null, null,
				Pagination.of(1, 10), null);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	@Override
	@Test
	public void testGetSitePageSpecificationStyleBooksPage() throws Exception {
		super.testGetSitePageSpecificationStyleBooksPage();

		_testGetSitePageSpecificationStyleBooksPage();
		_testGetSitePageSpecificationStyleBooksPageWithoutPermission();
		_testGetSitePageSpecificationStyleBooksPageWithSearch();
		_testGetSitePageSpecificationStyleBooksPageWithUnknownPageSpecification();
	}

	@Override
	@Test
	public void testGetSiteStyleBook() throws Exception {
		super.testGetSiteStyleBook();

		try {
			styleBookResource.getSiteStyleBook(
				testGetSiteStyleBook_getSiteExternalReferenceCode(),
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	@Override
	@Test
	public void testPatchDesignLibraryStyleBook() throws Exception {
		_testPatchDesignLibraryStyleBook();
		_testPatchDesignLibraryStyleBookForNonexistingStyleBook();
	}

	@Override
	@Test
	public void testPatchSiteStyleBook() throws Exception {
		_testPatchSiteStyleBook();
		_testPatchSiteStyleBookForNonexistingStyleBook();
	}

	@Override
	@Test
	public void testPostSiteStyleBook() throws Exception {
		super.testPostSiteStyleBook();

		_testPostSiteStyleBookWithBlankThemeId();
		_testPostSiteStyleBookWithDuplicateExternalReferenceCode();
		_testPostSiteStyleBookWithDuplicateKey();
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
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), testGroup.getGroupId());

		return depotEntry.getGroup();
	}

	private StyleBookEntry _addStyleBookEntry(
			long groupId, String themeId, String name)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), groupId, false, null, name, null,
			themeId,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private String _getDesignLibraryExternalReferenceCode() throws Exception {
		if (_designLibraryGroup == null) {
			DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				DepotConstants.TYPE_DESIGN_LIBRARY,
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

			_designLibraryGroup = depotEntry.getGroup();
		}

		return _designLibraryGroup.getExternalReferenceCode();
	}

	private String _getIrrelevantDesignLibraryExternalReferenceCode()
		throws Exception {

		if (_irrelevantDesignLibraryGroup == null) {
			DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				DepotConstants.TYPE_DESIGN_LIBRARY,
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

			_irrelevantDesignLibraryGroup = depotEntry.getGroup();
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

	private void _testGetSitePageSpecificationStyleBooksPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		Theme theme = layout.getTheme();

		String themeId = theme.getThemeId();

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			testGroup.getGroupId(), themeId, "Site Book");

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		StyleBookEntry designLibraryStyleBookEntry = _addStyleBookEntry(
			designLibraryGroup.getGroupId(), themeId, "Design Library Book");

		_addStyleBookEntry(
			designLibraryGroup.getGroupId(), "other-theme", "Other Theme Book");

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

		List<String> names = new ArrayList<>();

		StyleBook designLibraryStyleBook = null;

		for (StyleBook styleBook : page.getItems()) {
			names.add(styleBook.getName());

			if (Objects.equals(
					styleBook.getExternalReferenceCode(),
					designLibraryStyleBookEntry.getExternalReferenceCode())) {

				designLibraryStyleBook = styleBook;
			}
		}

		Assert.assertTrue(
			names.toString(), names.contains(siteStyleBookEntry.getName()));
		Assert.assertTrue(
			names.toString(), names.contains("Design Library Book"));
		Assert.assertFalse(
			names.toString(), names.contains("Other Theme Book"));

		Assert.assertNotNull(designLibraryStyleBook);

		Scope scope = designLibraryStyleBook.getScope();

		Assert.assertEquals("AssetLibrary", scope.getTypeAsString());
		Assert.assertEquals(
			designLibraryGroup.getExternalReferenceCode(),
			scope.getExternalReferenceCode());
		Assert.assertEquals(
			designLibraryGroup.getName("en-US"), scope.getLabel());
	}

	private void _testGetSitePageSpecificationStyleBooksPageWithoutPermission()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

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

		try {
			styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
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

		try {
			styleBookResource.getSitePageSpecificationStyleBooksPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), null, Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
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

		try {
			styleBookResource.patchDesignLibraryStyleBook(
				_getDesignLibraryExternalReferenceCode(),
				RandomTestUtil.randomString(), randomPatchStyleBook());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
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

		try {
			styleBookResource.patchSiteStyleBook(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomPatchStyleBook());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostSiteStyleBookWithBlankThemeId() throws Exception {
		try {
			StyleBook randomStyleBook = randomStyleBook();

			randomStyleBook.setThemeId(StringPool.BLANK);

			testPostSiteStyleBook_addStyleBook(randomStyleBook);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertEquals(
				"Theme ID must not be null", problemException.getMessage());
		}
	}

	private void _testPostSiteStyleBookWithDuplicateExternalReferenceCode()
		throws Exception {

		try {
			StyleBook randomStyleBook1 = randomStyleBook();

			randomStyleBook1 = testPostSiteStyleBook_addStyleBook(
				randomStyleBook1);

			StyleBook randomStyleBook2 = randomStyleBook();

			randomStyleBook2.setExternalReferenceCode(
				randomStyleBook1.getExternalReferenceCode());

			testPostSiteStyleBook_addStyleBook(randomStyleBook2);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				_language.get(
					LocaleUtil.getDefault(),
					"this-external-reference-code-is-already-in-use"),
				problem.getTitle());
		}
	}

	private void _testPostSiteStyleBookWithDuplicateKey() throws Exception {
		try {
			StyleBook randomStyleBook1 = randomStyleBook();

			randomStyleBook1 = testPostSiteStyleBook_addStyleBook(
				randomStyleBook1);

			StyleBook randomStyleBook2 = randomStyleBook();

			randomStyleBook2.setKey(randomStyleBook1.getKey());

			testPostSiteStyleBook_addStyleBook(randomStyleBook2);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertEquals(
				"A style book with the same key already exists",
				problemException.getMessage());
		}
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

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