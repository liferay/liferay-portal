/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.helper;

import com.liferay.document.library.repository.external.ExtRepositoryFileEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Ankita Malik
 */
public class SharepointURLHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_sharepointURLHelper = new SharepointURLHelper(
			_SITE_ABSOLUTE_URL, _RESULTS_SOURCE_ID);
	}

	@Test
	public void testGetAddFileURLEncodesApostropheInName() {
		Assert.assertEquals(
			_getFolderURL(
				"/Shared%20Documents",
				"/Files/Add(overwrite=false,url='relatorio%27%27teste.doc')"),
			_sharepointURLHelper.getAddFileURL(
				"/Shared Documents", "relatorio'teste.doc"));
	}

	@Test
	public void testGetAddFileURLEncodesPlusSignInName() {
		Assert.assertEquals(
			_getFolderURL(
				"/Shared%20Documents",
				"/Files/Add(overwrite=false,url='relatorio%2Bteste.doc')"),
			_sharepointURLHelper.getAddFileURL(
				"/Shared Documents", "relatorio+teste.doc"));
	}

	@Test
	public void testGetCheckInFileURLEncodesApostropheInChangeLog() {
		Assert.assertEquals(
			_getFileURL(
				"/Shared%20Documents/relatorio.doc",
				"/CheckIn(comment='Ankita%27%27s%20edit',checkintype=1)"),
			_sharepointURLHelper.getCheckInFileURL(
				"/Shared Documents/relatorio.doc", true, "Ankita's edit"));
	}

	@Test
	public void testGetFileVersionsURLEncodesApostrophe() {
		Assert.assertEquals(
			_getFileURL(
				"/Documentos%20compartidos/E1-apostrophe" +
					"/relatorio%27%27teste.doc",
				"/Versions"),
			_sharepointURLHelper.getFileVersionsURL(
				_getExtRepositoryFileEntry(
					"/Documentos compartidos/E1-apostrophe" +
						"/relatorio'teste.doc")));
	}

	@Test
	public void testGetFileVersionsURLEncodesBothPlusSignAndApostrophe() {
		Assert.assertEquals(
			_getFileURL("/Shared%20Documents/a%2Bb%27%27c.doc", "/Versions"),
			_sharepointURLHelper.getFileVersionsURL(
				_getExtRepositoryFileEntry("/Shared Documents/a+b'c.doc")));
	}

	@Test
	public void testGetFileVersionsURLEncodesPlusSign() {
		Assert.assertEquals(
			_getFileURL(
				"/Documentos%20compartidos/E1-plus/relatorio%2Bteste.doc",
				"/Versions"),
			_sharepointURLHelper.getFileVersionsURL(
				_getExtRepositoryFileEntry(
					"/Documentos compartidos/E1-plus/relatorio+teste.doc")));
	}

	@Test
	public void testGetFileVersionsURLEncodesSpaceAndPreservesSlashes() {
		Assert.assertEquals(
			_getFileURL(
				"/Shared%20Documents/sub%20folder/relatorio.doc", "/Versions"),
			_sharepointURLHelper.getFileVersionsURL(
				_getExtRepositoryFileEntry(
					"/Shared Documents/sub folder/relatorio.doc")));
	}

	@Test
	public void testGetMoveFileURLEncodesPlusSignInTitle() {
		Assert.assertEquals(
			_getFileURL(
				"/Shared%20Documents/a%2Bb.doc",
				"/MoveTo(newurl='/Archive/a%2Bb.doc',flags=1)"),
			_sharepointURLHelper.getMoveFileURL(
				"/Shared Documents/a+b.doc", "/Archive", "a+b.doc"));
	}

	@Test
	public void testGetSearchURLEncodesApostropheInQueryText() {
		Assert.assertEquals(
			StringBundler.concat(
				_SITE_ABSOLUTE_URL,
				"/_api/search/query?QueryText='a%27%27b'&SourceID='",
				_RESULTS_SOURCE_ID, "'&StartRow=0&RowsPerPage=20"),
			_sharepointURLHelper.getSearchURL("a'b", 0, 20));
	}

	private ExtRepositoryFileEntry _getExtRepositoryFileEntry(
		String extRepositoryModelKey) {

		ExtRepositoryFileEntry extRepositoryFileEntry = Mockito.mock(
			ExtRepositoryFileEntry.class);

		Mockito.when(
			extRepositoryFileEntry.getExtRepositoryModelKey()
		).thenReturn(
			extRepositoryModelKey
		);

		return extRepositoryFileEntry;
	}

	private String _getFileURL(String encodedPath, String suffix) {
		return StringBundler.concat(
			_SITE_ABSOLUTE_URL,
			"/_api/web/GetFileByServerRelativePath(decodedUrl='", encodedPath,
			"')", suffix);
	}

	private String _getFolderURL(String encodedPath, String suffix) {
		return StringBundler.concat(
			_SITE_ABSOLUTE_URL,
			"/_api/web/GetFolderByServerRelativePath(decodedUrl='", encodedPath,
			"')", suffix);
	}

	private static final String _RESULTS_SOURCE_ID = "resultsSourceId";

	private static final String _SITE_ABSOLUTE_URL =
		"https://liferaycolab.sharepoint.com";

	private SharepointURLHelper _sharepointURLHelper;

}