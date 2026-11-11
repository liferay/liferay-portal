/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.model.listener;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jon Steer
 * @author Raymond Augé
 */
@Component(service = ModelListener.class)
public class JournalArticleModelListener
	extends BaseModelListener<JournalArticle> {

	@Override
	public void onAfterRemove(JournalArticle journalArticle) {
		clearCache(journalArticle);

		int count =
			_journalArticleLocalService.getArticlesCountByResourcePrimKey(
				journalArticle.getResourcePrimKey());

		if (count <= 0) {
			_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsages(
				_portal.getClassNameId(JournalArticle.class),
				journalArticle.getResourcePrimKey());
		}
	}

	@Override
	public void onAfterUpdate(
		JournalArticle originalJournalArticle, JournalArticle journalArticle) {

		clearCache(journalArticle);
	}

	protected void clearCache(JournalArticle journalArticle) {
		if (journalArticle == null) {
			return;
		}

		// Journal content

		_journalContent.clearCache(
			journalArticle.getGroupId(), journalArticle.getArticleId());
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private Portal _portal;

}