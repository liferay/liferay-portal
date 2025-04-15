/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.web.internal.util.JournalUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Szimko
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/publish_articles"
	},
	service = MVCActionCommand.class
)
public class PublishArticlesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		String articleId = ParamUtil.getString(actionRequest, "articleId");

		if (Validator.isNotNull(articleId)) {
			Changeset changeset = _createChangeset(
				groupId, ListUtil.fromArray(articleId));

			_exportImportChangesetMVCActionCommandHelper.publish(
				actionRequest, actionResponse, changeset);

			return;
		}

		String[] articleIds = ParamUtil.getStringValues(
			actionRequest, "rowIdsJournalArticle");

		Changeset changeset = _createChangeset(
			groupId, ListUtil.fromArray(articleIds));

		_exportImportChangesetMVCActionCommandHelper.publish(
			actionRequest, actionResponse, changeset);
	}

	private Changeset _createChangeset(long groupId, List<String> articleIds) {
		Changeset.Builder builder = Changeset.create();

		for (String articleId : articleIds) {
			JournalArticle journalArticle = _fetchArticle(groupId, articleId);

			builder.addStagedModel(
				() -> journalArticle
			).addMultipleStagedModel(
				() -> _getJournalArticleVersionStagedModels(journalArticle)
			);
		}

		return builder.build();
	}

	private JournalArticle _fetchArticle(long groupId, String articleId) {
		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(groupId, articleId);

		StagedModelDataHandler<JournalArticle> stagedModelDataHandler =
			_getStagedModelDataHandler();

		try {
			JournalArticle latestApprovedJournalArticle =
				_journalArticleLocalService.getArticle(
					journalArticle.getGroupId(), journalArticle.getArticleId());

			if (ArrayUtil.contains(
					stagedModelDataHandler.getExportableStatuses(),
					latestApprovedJournalArticle.getStatus())) {

				return latestApprovedJournalArticle;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get journal article by group ", groupId,
						" and article ID ", articleId),
					portalException);
			}
		}

		return null;
	}

	private List<StagedModel> _getJournalArticleVersionStagedModels(
		JournalArticle journalArticle) {

		boolean includeVersionHistory = JournalUtil.isIncludeVersionHistory();

		if (!includeVersionHistory) {
			return Collections.emptyList();
		}

		StagedModelDataHandler<JournalArticle> stagedModelDataHandler =
			_getStagedModelDataHandler();

		List<JournalArticle> journalArticles = new ArrayList<>();

		journalArticles.addAll(
			_journalArticleLocalService.getArticles(
				journalArticle.getGroupId(), journalArticle.getArticleId()));

		return TransformUtil.transform(
			journalArticles,
			curJournalArticle -> {
				if (ArrayUtil.contains(
						stagedModelDataHandler.getExportableStatuses(),
						curJournalArticle.getStatus())) {

					return curJournalArticle;
				}

				return null;
			});
	}

	private StagedModelDataHandler<JournalArticle>
		_getStagedModelDataHandler() {

		return (StagedModelDataHandler<JournalArticle>)
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				JournalArticle.class.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishArticlesMVCActionCommand.class);

	@Reference
	private ExportImportChangesetMVCActionCommandHelper
		_exportImportChangesetMVCActionCommandHelper;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

}