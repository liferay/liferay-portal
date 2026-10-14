/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.constants.SitemapConstants;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = ModelListener.class)
public class JournalArticleModelListener
	extends BaseSitemapModelListener<JournalArticle> {

	@Override
	protected String getAssetTypeKey() {
		return SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT;
	}

	@Override
	protected Set<String> getLayoutUuids(
			JournalArticle originalJournalArticle,
			JournalArticle journalArticle)
		throws PortalException {

		Group group = _groupLocalService.getGroup(journalArticle.getGroupId());

		if (group.isDepot()) {
			return null;
		}

		Set<String> layoutUuids = new HashSet<>();

		JournalArticle previousJournalArticle = originalJournalArticle;

		if (previousJournalArticle == null) {
			previousJournalArticle =
				_journalArticleLocalService.getPreviousApprovedArticle(
					journalArticle);
		}

		if (Validator.isNotNull(previousJournalArticle.getLayoutUuid())) {
			layoutUuids.add(previousJournalArticle.getLayoutUuid());
		}

		if (Validator.isNotNull(journalArticle.getLayoutUuid())) {
			layoutUuids.add(journalArticle.getLayoutUuid());
		}

		Layout layout = _getDisplayPageTemplateLayout(journalArticle);

		if (layout != null) {
			layoutUuids.add(layout.getUuid());
		}

		return layoutUuids;
	}

	private Layout _getDisplayPageTemplateLayout(
		JournalArticle journalArticle) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				journalArticle.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getResourcePrimKey(),
				journalArticle.getDDMStructureId());

		if (layoutPageTemplateEntry == null) {
			return null;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		if ((layout == null) ||
			(layout.getGroupId() != journalArticle.getGroupId())) {

			return null;
		}

		return layout;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}