/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.item;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "launch.entry.content.resolver.class.name=com.liferay.journal.model.JournalArticle",
	service = LaunchEntryContentResolver.class
)
public class JournalArticleLaunchEntryContentResolver
	implements LaunchEntryContentResolver {

	@Override
	public LaunchEntryContent resolve(
			long classPK, String classVersion, Locale locale)
		throws PortalException {

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getArticlesByResourcePrimKey(classPK);

		JournalArticle journalArticle = null;

		double version = GetterUtil.getDouble(classVersion);

		for (JournalArticle curJournalArticle : journalArticles) {
			if (curJournalArticle.getVersion() == version) {
				journalArticle = curJournalArticle;

				break;
			}
		}

		if (journalArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No JournalArticle exists with the key {resourcePrimKey=",
					classPK, ", version=", classVersion, "}"));
		}

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		return new LaunchEntryContent(
			journalArticle.getGroupId(), journalArticle.getModifiedDate(),
			journalArticle.getStatus(), journalArticle.getTitle(locale),
			ddmStructure.getName(locale), journalArticle.getUserName());
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

}