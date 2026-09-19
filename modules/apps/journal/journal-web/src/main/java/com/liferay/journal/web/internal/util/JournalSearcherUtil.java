/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Lourdes Fernández Besada
 */
public class JournalSearcherUtil {

	public static SearchResponse searchJournalArticles(
		Consumer<SearchContext> searchContextConsumer) {

		return _search(searchContextConsumer, JournalArticle.class);
	}

	public static SearchResponse searchJournalArticlesAndJournalFolders(
		Consumer<SearchContext> searchContextConsumer) {

		return _search(
			searchContextConsumer, JournalArticle.class, JournalFolder.class);
	}

	public static SearchResponse searchJournalFolders(
		Consumer<SearchContext> searchContextConsumer) {

		return _search(searchContextConsumer, JournalFolder.class);
	}

	public static List<Object> transformJournalArticleAndFolders(
		List<Document> documents) {

		return TransformUtil.transform(
			documents,
			document -> {
				String className = document.get(Field.ENTRY_CLASS_NAME);

				if (className.equals(JournalArticle.class.getName())) {
					JournalArticleLocalService journalArticleLocalService =
						_journalArticleLocalServiceSnapshot.get();

					return journalArticleLocalService.fetchArticle(
						GetterUtil.getLong(document.get(Field.GROUP_ID)),
						GetterUtil.getString(document.get(Field.ARTICLE_ID)),
						GetterUtil.getDouble(document.get(Field.VERSION)));
				}

				JournalFolderLocalService journalFolderLocalService =
					_journalFolderLocalServiceSnapshot.get();

				return journalFolderLocalService.fetchJournalFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));
			});
	}

	public static List<JournalArticle> transformJournalArticles(
		List<Document> documents) {

		return TransformUtil.transform(
			documents,
			document -> {
				JournalArticleLocalService journalArticleLocalService =
					_journalArticleLocalServiceSnapshot.get();

				return journalArticleLocalService.fetchArticle(
					GetterUtil.getLong(document.get(Field.GROUP_ID)),
					GetterUtil.getString(document.get(Field.ARTICLE_ID)),
					GetterUtil.getDouble(document.get(Field.VERSION)));
			});
	}

	private static SearchResponse _search(
		Consumer<SearchContext> searchContextConsumer,
		Class<?>... modelIndexerClasses) {

		Searcher searcher = _searcherSnapshot.get();
		SearchRequestBuilderFactory searchRequestBuilderFactory =
			_searchRequestBuilderFactorySnapshot.get();

		return searcher.search(
			searchRequestBuilderFactory.builder(
			).emptySearchEnabled(
				true
			).modelIndexerClasses(
				modelIndexerClasses
			).withSearchContext(
				searchContextConsumer
			).build());
	}

	private static final Snapshot<JournalArticleLocalService>
		_journalArticleLocalServiceSnapshot = new Snapshot<>(
			JournalSearcherUtil.class, JournalArticleLocalService.class);
	private static final Snapshot<JournalFolderLocalService>
		_journalFolderLocalServiceSnapshot = new Snapshot<>(
			JournalSearcherUtil.class, JournalFolderLocalService.class);
	private static final Snapshot<SearchRequestBuilderFactory>
		_searchRequestBuilderFactorySnapshot = new Snapshot<>(
			JournalSearcherUtil.class, SearchRequestBuilderFactory.class);
	private static final Snapshot<Searcher> _searcherSnapshot = new Snapshot<>(
		JournalSearcherUtil.class, Searcher.class);

}