/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.change.tracking.spi.listener;

import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleTable;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(
	property = "service.ranking:Integer=100", service = CTEventListener.class
)
public class JournalArticleCTSearchEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) {
		List<CTEntry> ctEntries = _ctEntryLocalService.getCTEntries(
			ctCollectionId, _portal.getClassNameId(JournalArticle.class));

		if (ctEntries.isEmpty()) {
			return;
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				List<JournalArticle> journalArticles = null;

				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ctCollectionId)) {

					journalArticles = _journalArticleLocalService.dslQuery(
						DSLQueryFactoryUtil.select(
							JournalArticleTable.INSTANCE
						).from(
							JournalArticleTable.INSTANCE
						).where(
							JournalArticleTable.INSTANCE.ctCollectionId.eq(
								ctCollectionId
							).and(
								JournalArticleTable.INSTANCE.id.in(
									TransformUtil.transformToArray(
										ctEntries,
										ctEntry -> ctEntry.getModelClassPK(),
										Long.class))
							)
						));
				}

				_journalArticleIndexer.reindex(journalArticles);

				return null;
			});
	}

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference(
		target = "(indexer.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private Indexer<JournalArticle> _journalArticleIndexer;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Portal _portal;

}