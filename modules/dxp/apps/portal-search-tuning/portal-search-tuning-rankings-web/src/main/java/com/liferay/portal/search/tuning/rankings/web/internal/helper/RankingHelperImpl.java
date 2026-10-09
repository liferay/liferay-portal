/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.helper;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.tuning.rankings.helper.RankingHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(service = RankingHelper.class)
public class RankingHelperImpl implements RankingHelper {

	public static final String JOURNAL_ARTICLE_DOCUMENT_PREFIX =
		"com.liferay.journal.model.JournalArticle_PORTLET_";

	@Override
	public String getDocumentId(String documentId) {
		if (!documentId.startsWith(JOURNAL_ARTICLE_DOCUMENT_PREFIX)) {
			return documentId;
		}

		String[] parts = StringUtil.split(
			documentId, JOURNAL_ARTICLE_DOCUMENT_PREFIX);

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchJournalArticle(
				Long.valueOf(parts[1]));

		if (journalArticle == null) {
			return StringPool.BLANK;
		}

		JournalArticle latestJournalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				journalArticle.getResourcePrimKey());

		return JOURNAL_ARTICLE_DOCUMENT_PREFIX + latestJournalArticle.getId();
	}

	@Override
	public Collection<String> getQueryStrings(
		String queryString, List<String> aliases) {

		Set<String> queryStrings = new LinkedHashSet<>();

		if (!Validator.isBlank(queryString)) {
			queryStrings.add(queryString);
		}

		for (String alias : aliases) {
			if (Validator.isBlank(alias)) {
				continue;
			}

			queryStrings.add(alias);
		}

		return ListUtil.sort(new ArrayList<>(queryStrings));
	}

	@Override
	public List<String> translateDocumentIds(List<String> documentIds) {
		return TransformUtil.transform(
			documentIds,
			documentId -> {
				String id = getDocumentId(documentId);

				if (!Validator.isBlank(id)) {
					return id;
				}

				return null;
			});
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

}