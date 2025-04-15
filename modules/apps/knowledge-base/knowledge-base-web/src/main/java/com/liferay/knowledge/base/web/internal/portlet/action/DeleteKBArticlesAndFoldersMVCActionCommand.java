/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.service.KBFolderService;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"mvc.command.name=/knowledge_base/delete_kb_articles_and_folders"
	},
	service = MVCActionCommand.class
)
public class DeleteKBArticlesAndFoldersMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long[] kbArticleResourcePrimKeys = ParamUtil.getLongValues(
			actionRequest, "rowIdsKBArticle");
		long[] kbFolderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsKBFolder");

		if (cmd.equals(Constants.MOVE_TO_TRASH)) {
			_moveToTrash(actionRequest, kbArticleResourcePrimKeys, kbFolderIds);
		}
		else {
			_delete(kbArticleResourcePrimKeys, kbFolderIds);
		}
	}

	private void _delete(long[] kbArticleResourcePrimKeys, long[] kbFolderIds)
		throws Exception {

		for (long kbArticleResourcePrimKey : kbArticleResourcePrimKeys) {
			_kbArticleService.deleteKBArticle(kbArticleResourcePrimKey);
		}

		for (long kbFolderId : kbFolderIds) {
			_kbFolderService.deleteKBFolder(kbFolderId);
		}
	}

	private void _moveToTrash(
			ActionRequest actionRequest, long[] kbArticleResourcePrimKeys,
			long[] kbFolderIds)
		throws Exception {

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long kbArticleResourcePrimKey : kbArticleResourcePrimKeys) {
			trashedModels.add(
				_kbArticleService.moveKBArticleToTrash(
					kbArticleResourcePrimKey));
		}

		for (long kbFolderId : kbFolderIds) {
			trashedModels.add(_kbFolderService.moveKBFolderToTrash(kbFolderId));
		}

		if (!trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}
	}

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private KBFolderService _kbFolderService;

}