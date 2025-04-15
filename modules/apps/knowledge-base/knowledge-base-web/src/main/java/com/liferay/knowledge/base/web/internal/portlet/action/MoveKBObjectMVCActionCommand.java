/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.service.KBFolderService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SEARCH,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION,
		"mvc.command.name=/knowledge_base/move_kb_object"
	},
	service = MVCActionCommand.class
)
public class MoveKBObjectMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		boolean dragAndDrop = ParamUtil.getBoolean(
			actionRequest, "dragAndDrop");
		long parentResourceClassNameId = ParamUtil.getLong(
			actionRequest, "parentResourceClassNameId",
			_portal.getClassNameId(KBFolderConstants.getClassName()));
		long parentResourcePrimKey = ParamUtil.getLong(
			actionRequest, "parentResourcePrimKey",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		long resourceClassNameId = ParamUtil.getLong(
			actionRequest, "resourceClassNameId");
		long resourcePrimKey = ParamUtil.getLong(
			actionRequest, "resourcePrimKey");
		int position = ParamUtil.getInteger(actionRequest, "position");
		double priority = ParamUtil.getDouble(actionRequest, "priority");

		if (ParamUtil.getBoolean(actionRequest, "forceLock")) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_kbArticleService.forceLockKBArticle(
				themeDisplay.getScopeGroupId(), resourcePrimKey);
		}

		try {
			long kbArticleClassNameId = _portal.getClassNameId(
				KBArticleConstants.getClassName());

			if (resourceClassNameId == kbArticleClassNameId) {
				if (!dragAndDrop) {
					_kbArticleService.moveKBArticle(
						resourcePrimKey, parentResourceClassNameId,
						parentResourcePrimKey, priority);
				}
				else {
					KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
						resourcePrimKey, WorkflowConstants.STATUS_ANY);

					if ((kbArticle.getParentResourcePrimKey() !=
							parentResourcePrimKey) ||
						(position != -1)) {

						priority = _getPriority(
							kbArticle, parentResourcePrimKey, position);

						_kbArticleService.moveKBArticle(
							resourcePrimKey, parentResourceClassNameId,
							parentResourcePrimKey, priority);
					}
				}
			}
			else {
				if (!dragAndDrop) {
					_kbFolderService.moveKBFolder(
						resourcePrimKey, parentResourcePrimKey);
				}
				else {
					if (parentResourceClassNameId == kbArticleClassNameId) {
						_errorMessage(
							actionRequest, actionResponse,
							_language.get(
								_portal.getHttpServletRequest(actionRequest),
								"folders-cannot-be-moved-into-articles"));

						return;
					}

					KBFolder kbFolder = _kbFolderService.getKBFolder(
						resourcePrimKey);

					if (kbFolder.getParentKBFolderId() !=
							parentResourcePrimKey) {

						_kbFolderService.moveKBFolder(
							resourcePrimKey, parentResourcePrimKey);
					}
				}
			}

			if (dragAndDrop) {
				hideDefaultSuccessMessage(actionRequest);

				JSONObject jsonObject = JSONUtil.put("success", Boolean.TRUE);

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse, jsonObject);
			}
		}
		catch (LockedKBArticleException lockedKBArticleException) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"actionLabel",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						Constants.MOVE)
				).put(
					"actionURL",
					KnowledgeBaseUtil.getKBArticleMoveURL(
						_portal.getLiferayPortletResponse(actionResponse),
						false, true, parentResourceClassNameId,
						parentResourcePrimKey, position, priority,
						KnowledgeBaseUtil.getRedirect(actionRequest),
						resourceClassNameId, resourcePrimKey)
				).put(
					"lockException", Boolean.TRUE
				).put(
					"success", Boolean.FALSE
				).put(
					"userName", lockedKBArticleException.getUserName()
				));
		}
		catch (PortalException portalException) {
			if (!dragAndDrop) {
				throw portalException;
			}

			_errorMessage(
				actionRequest, actionResponse,
				_language.get(
					_portal.getHttpServletRequest(actionRequest),
					"your-request-failed-to-complete"));
		}
	}

	private void _errorMessage(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String message)
		throws IOException {

		hideDefaultErrorMessage(actionRequest);

		JSONObject jsonObject = JSONUtil.put("errorMessage", message);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private double _getNearestPriority(
		double nextKBArticlePriority, double previousKBArticlePriority) {

		int ceil = (int)Math.ceil(nextKBArticlePriority);
		int floor = (int)Math.floor(nextKBArticlePriority);

		if ((ceil == floor) &&
			((nextKBArticlePriority - 1) > previousKBArticlePriority)) {

			return nextKBArticlePriority - 1;
		}
		else if ((ceil != floor) && (floor > previousKBArticlePriority)) {
			return floor;
		}

		return (previousKBArticlePriority + nextKBArticlePriority) / 2;
	}

	private double _getPriority(
			KBArticle kbArticle, long parentResourcePrimKey, int position)
		throws PortalException {

		int kbFoldersCount = _kbFolderService.getKBFoldersCount(
			kbArticle.getGroupId(), parentResourcePrimKey);

		position = position - kbFoldersCount;

		List<KBArticle> kbArticles = _kbArticleService.getKBArticles(
			kbArticle.getGroupId(), parentResourcePrimKey,
			WorkflowConstants.STATUS_ANY, position - 1, position + 1,
			KBArticlePriorityComparator.getInstance(true));

		if (ListUtil.isEmpty(kbArticles)) {
			return kbArticle.getPriority();
		}

		KBArticle nextKBArticle = kbArticles.get(kbArticles.size() - 1);

		if (position == 0) {
			return _getNearestPriority(nextKBArticle.getPriority(), 0);
		}
		else if (kbArticles.size() == 1) {
			return _getNearestPriority(
				nextKBArticle.getPriority() + 2, nextKBArticle.getPriority());
		}

		KBArticle previousKBArticle = kbArticles.get(kbArticles.size() - 2);

		return _getNearestPriority(
			nextKBArticle.getPriority(), previousKBArticle.getPriority());
	}

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private KBFolderService _kbFolderService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}