/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.StyleBook;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;

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

	@Test
	public void testGetDesignLibraryStyleBooksPageWhenGroupIsNotDesignLibrary()
		throws Exception {

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
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _designLibraryGroup;
	private Group _irrelevantDesignLibraryGroup;

	@Inject
	private Language _language;

}