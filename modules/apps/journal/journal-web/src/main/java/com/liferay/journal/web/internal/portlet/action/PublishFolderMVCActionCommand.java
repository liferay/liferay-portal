/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.web.internal.util.JournalUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zoltan Csaszi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/publish_folder"
	},
	service = MVCActionCommand.class
)
public class PublishFolderMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long folderId = ParamUtil.getLong(actionRequest, "folderId");

		Changeset.Builder builder = Changeset.create();

		Changeset changeset = builder.addStagedModel(
			() -> _journalFolderLocalService.fetchJournalFolder(folderId)
		).addStagedModelHierarchy(
			() -> _journalFolderLocalService.fetchJournalFolder(folderId),
			journalFolder -> _getFoldersAndArticles(journalFolder)
		).build();

		_exportImportChangesetMVCActionCommandHelper.publish(
			actionRequest, actionResponse, changeset);
	}

	private List<StagedModel> _getFoldersAndArticles(
		JournalFolder journalFolder) {

		if (journalFolder == null) {
			return Collections.emptyList();
		}

		List<StagedModel> stagedModels = new ArrayList<>();

		try {
			List<Object> childObjects =
				_journalFolderLocalService.getFoldersAndArticles(
					journalFolder.getGroupId(), journalFolder.getFolderId());

			for (Object childObject : childObjects) {
				if (childObject instanceof JournalFolder) {
					stagedModels.add((JournalFolder)childObject);
				}
				else if (childObject instanceof JournalArticle) {
					JournalArticle journalArticle = (JournalArticle)childObject;

					boolean includeVersionHistory =
						JournalUtil.isIncludeVersionHistory();

					StagedModelDataHandler<JournalArticle>
						stagedModelDataHandler = _getStagedModelDataHandler();

					List<JournalArticle> journalArticles = new ArrayList<>();

					if (includeVersionHistory) {
						journalArticles.addAll(
							_journalArticleLocalService.getArticles(
								journalArticle.getGroupId(),
								journalArticle.getArticleId()));
					}
					else {
						journalArticles.add(
							_journalArticleLocalService.getArticle(
								journalArticle.getGroupId(),
								journalArticle.getArticleId()));
					}

					for (JournalArticle curJournalArticle : journalArticles) {
						if (ArrayUtil.contains(
								stagedModelDataHandler.getExportableStatuses(),
								curJournalArticle.getStatus())) {

							stagedModels.add(curJournalArticle);
						}
					}
				}
				else {
					stagedModels.add((StagedModel)childObject);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get folders and articles for folder " +
						journalFolder.getFolderId(),
					portalException);
			}
		}

		return stagedModels;
	}

	private StagedModelDataHandler<JournalArticle>
		_getStagedModelDataHandler() {

		return (StagedModelDataHandler<JournalArticle>)
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				JournalArticle.class.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishFolderMVCActionCommand.class);

	@Reference
	private ExportImportChangesetMVCActionCommandHelper
		_exportImportChangesetMVCActionCommandHelper;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

}