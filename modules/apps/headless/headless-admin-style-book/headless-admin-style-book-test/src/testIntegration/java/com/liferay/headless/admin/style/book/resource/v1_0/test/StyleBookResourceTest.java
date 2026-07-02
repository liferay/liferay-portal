/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.style.book.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.style.book.client.dto.v1_0.StyleBook;
import com.liferay.headless.admin.style.book.client.problem.Problem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 * @author Luis Ortiz
 * @author Thiago Buarque
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class StyleBookResourceTest extends BaseStyleBookResourceTestCase {

	@Override
	@Test
	public void testGetAssetLibraryStyleBook() throws Exception {
		super.testGetAssetLibraryStyleBook();

		_testGetAssetLibraryStyleBookExposesActions();
		_testGetAssetLibraryStyleBookDesignLibraryFields();

		try {
			styleBookResource.getAssetLibraryStyleBook(
				testGetAssetLibraryStyleBook_getAssetLibraryExternalReferenceCode(),
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
	public void testGetSiteStyleBook() throws Exception {
		super.testGetSiteStyleBook();

		_testGetSiteStyleBookDesignLibraryFields();

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
	protected StyleBook testDeleteAssetLibraryStyleBook_addStyleBook()
		throws Exception {

		return _addAssetLibraryStyleBook(randomStyleBook());
	}

	@Override
	protected StyleBook testGetAssetLibraryStyleBook_addStyleBook()
		throws Exception {

		return _addAssetLibraryStyleBook(randomStyleBook());
	}

	@Override
	protected StyleBook testGetAssetLibraryStyleBooksPage_addStyleBook(
			String assetLibraryExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			assetLibraryExternalReferenceCode, TestPropsValues.getCompanyId());

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			group.getGroupId(), styleBook);

		return styleBookResource.getAssetLibraryStyleBook(
			assetLibraryExternalReferenceCode,
			styleBookEntry.getExternalReferenceCode());
	}

	@Override
	protected StyleBook testGraphQLAssetLibraryStyleBook_addStyleBook()
		throws Exception {

		return testGetAssetLibraryStyleBook_addStyleBook();
	}

	@Override
	protected StyleBook testPostSiteStyleBook_addStyleBook(StyleBook styleBook)
		throws Exception {

		return styleBookResource.postSiteStyleBook(
			testGroup.getExternalReferenceCode(), styleBook);
	}

	private StyleBook _addAssetLibraryStyleBook(StyleBook styleBook)
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			testDepotEntryGroup.getGroupId(), styleBook);

		return styleBookResource.getAssetLibraryStyleBook(
			testDepotEntryGroup.getExternalReferenceCode(),
			styleBookEntry.getExternalReferenceCode());
	}

	private StyleBookEntry _addStyleBookEntry(long groupId, StyleBook styleBook)
		throws Exception {

		boolean defaultStyleBook = false;

		if ((styleBook.getDefaultStyleBook() != null) &&
			styleBook.getDefaultStyleBook()) {

			defaultStyleBook = true;
		}

		return _styleBookEntryLocalService.addStyleBookEntry(
			styleBook.getExternalReferenceCode(), TestPropsValues.getUserId(),
			groupId, defaultStyleBook, styleBook.getFrontendTokensValues(),
			styleBook.getName(), styleBook.getKey(), styleBook.getThemeId(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private void _testGetAssetLibraryStyleBookDesignLibraryFields()
		throws Exception {

		StyleBook postStyleBook = _addAssetLibraryStyleBook(randomStyleBook());

		StyleBook getStyleBook = styleBookResource.getAssetLibraryStyleBook(
			testDepotEntryGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode());

		Assert.assertEquals(
			testDepotEntryGroup.getDescriptiveName(LocaleUtil.getDefault()),
			getStyleBook.getDesignLibraryName());
		Assert.assertEquals(
			testDepotEntryGroup.getExternalReferenceCode(),
			getStyleBook.getDesignLibraryExternalReferenceCode());
	}

	private void _testGetAssetLibraryStyleBookExposesActions()
		throws Exception {

		StyleBook postStyleBook = testGetAssetLibraryStyleBook_addStyleBook();

		StyleBook getStyleBook = styleBookResource.getAssetLibraryStyleBook(
			testGetAssetLibraryStyleBook_getAssetLibraryExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode());

		Map<String, Map<String, String>> actions = getStyleBook.getActions();

		Assert.assertNotNull(actions);
		Assert.assertTrue(actions.containsKey("delete"));
		Assert.assertTrue(actions.containsKey("get"));
	}

	private void _testGetSiteStyleBookDesignLibraryFields() throws Exception {
		StyleBook postStyleBook = styleBookResource.postSiteStyleBook(
			testGroup.getExternalReferenceCode(), randomStyleBook());

		StyleBook getStyleBook = styleBookResource.getSiteStyleBook(
			testGroup.getExternalReferenceCode(),
			postStyleBook.getExternalReferenceCode());

		Assert.assertNull(getStyleBook.getDesignLibraryName());
		Assert.assertNull(getStyleBook.getDesignLibraryExternalReferenceCode());
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
			Assert.assertEquals(
				"A style book with the same external reference code already " +
					"exists",
				problemException.getMessage());
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
	private GroupLocalService _groupLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}