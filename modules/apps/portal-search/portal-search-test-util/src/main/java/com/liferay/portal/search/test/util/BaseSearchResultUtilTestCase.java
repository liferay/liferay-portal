/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util;

import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ClassNameWrapper;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.RelatedSearchResult;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.result.SearchResultTranslator;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalServiceWrapper;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;

import org.osgi.framework.BundleContext;

/**
 * @author André de Oliveira
 */
public abstract class BaseSearchResultUtilTestCase {

	@Before
	public void setUp() throws Exception {
		setUpClassNameLocalService();
		setUpFastDateFormatFactoryUtil();
		setUpSearchResultTranslator();
	}

	protected void assertEmptyCommentRelatedSearchResults(
		SearchResult searchResult) {

		List<RelatedSearchResult<Comment>> commentRelatedSearchResults =
			searchResult.getCommentRelatedSearchResults();

		Assert.assertTrue(
			commentRelatedSearchResults.toString(),
			commentRelatedSearchResults.isEmpty());
	}

	protected void assertEmptyFileEntryRelatedSearchResults(
		SearchResult searchResult) {

		List<RelatedSearchResult<FileEntry>> fileEntryRelatedSearchResults =
			searchResult.getFileEntryRelatedSearchResults();

		Assert.assertTrue(
			fileEntryRelatedSearchResults.toString(),
			fileEntryRelatedSearchResults.isEmpty());
	}

	protected void assertEmptyVersions(SearchResult searchResult) {
		List<String> versions = searchResult.getVersions();

		Assert.assertTrue(versions.toString(), versions.isEmpty());
	}

	protected SearchResult assertOneSearchResult(Document document) {
		List<SearchResult> searchResults = SearchTestUtil.getSearchResults(
			searchResultTranslator, document);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		return searchResults.get(0);
	}

	protected abstract SearchResultTranslator createSearchResultTranslator();

	protected void setUpClassNameLocalService() {
		classNameLocalService = new ClassNameLocalServiceWrapper() {

			@Override
			public ClassName getClassName(long classNameId) {
				if (classNameId !=
						SearchTestUtil.ATTACHMENT_OWNER_CLASS_NAME_ID) {

					return null;
				}

				return new ClassNameWrapper(null) {

					@Override
					public String getClassName() {
						return SearchTestUtil.ATTACHMENT_OWNER_CLASS_NAME;
					}

				};
			}

		};
	}

	protected void setUpFastDateFormatFactoryUtil() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			ProxyFactory.newDummyInstance(FastDateFormatFactory.class));
	}

	protected void setUpSearchResultTranslator() {
		searchResultTranslator = createSearchResultTranslator();
	}

	protected BundleContext bundleContext = SystemBundleUtil.getBundleContext();
	protected ClassNameLocalService classNameLocalService;
	protected SearchResultTranslator searchResultTranslator;

}