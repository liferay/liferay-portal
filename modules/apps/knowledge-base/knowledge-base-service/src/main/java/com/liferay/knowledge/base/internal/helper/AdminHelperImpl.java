/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.helper;

import com.liferay.diff.DiffHtml;
import com.liferay.diff.DiffVersion;
import com.liferay.diff.DiffVersionsInfo;
import com.liferay.knowledge.base.internal.util.KBArticleDiffUtil;
import com.liferay.knowledge.base.internal.util.KBSectionEscapeUtil;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.util.AdminHelper;
import com.liferay.knowledge.base.util.comparator.KBArticleVersionComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lance Ji
 */
@Component(service = AdminHelper.class)
public class AdminHelperImpl implements AdminHelper {

	@Override
	public String[] escapeSections(String[] sections) {
		return KBSectionEscapeUtil.escapeSections(sections);
	}

	@Override
	public DiffVersionsInfo getDiffVersionsInfo(
		long groupId, long kbArticleResourcePrimKey, int sourceVersion,
		int targetVersion) {

		double previousVersion = 0;
		double nextVersion = 0;

		List<KBArticle> kbArticles = _kbArticleService.getKBArticleVersions(
			groupId, kbArticleResourcePrimKey,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, KBArticleVersionComparator.getInstance(false));

		for (KBArticle curKBArticle : kbArticles) {
			if ((curKBArticle.getVersion() < sourceVersion) &&
				(curKBArticle.getVersion() > previousVersion)) {

				previousVersion = curKBArticle.getVersion();
			}

			if ((curKBArticle.getVersion() <= targetVersion) ||
				((curKBArticle.getVersion() >= nextVersion) &&
				 (nextVersion != 0))) {

				continue;
			}

			nextVersion = curKBArticle.getVersion();
		}

		List<DiffVersion> diffVersions = new ArrayList<>();

		for (KBArticle curKBArticle : kbArticles) {
			DiffVersion diffVersion = new DiffVersion(
				curKBArticle.getUserId(), curKBArticle.getVersion(),
				curKBArticle.getModifiedDate());

			diffVersions.add(diffVersion);
		}

		return new DiffVersionsInfo(diffVersions, nextVersion, previousVersion);
	}

	@Override
	public String getKBArticleDiff(
			long resourcePrimKey, int sourceVersion, int targetVersion,
			String param)
		throws Exception {

		return KBArticleDiffUtil.getKBArticleDiff(
			version -> _kbArticleService.getKBArticle(resourcePrimKey, version),
			sourceVersion, targetVersion, param, _diffHtml);
	}

	@Override
	public String[] unescapeSections(String sections) {
		return KBSectionEscapeUtil.unescapeSections(sections);
	}

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private KBArticleService _kbArticleService;

}