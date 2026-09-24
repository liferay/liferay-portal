/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.knowledge.base.service.KBCommentService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
		"mvc.command.name=/knowledge_base/update_kb_comment"
	},
	service = MVCActionCommand.class
)
public class UpdateKBCommentMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return;
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String content = ParamUtil.getString(actionRequest, "content");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			KBComment.class.getName(), actionRequest);

		if (cmd.equals(Constants.ADD)) {
			long classPK = ParamUtil.getLong(actionRequest, "classPK");

			_kbArticleModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), classPK, ActionKeys.VIEW);

			_kbCommentLocalService.addKBComment(
				themeDisplay.getUserId(),
				_portal.getClassNameId(KBArticle.class), classPK, content,
				serviceContext);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			long kbCommentId = ParamUtil.getLong(actionRequest, "kbCommentId");

			KBComment kbComment = _kbCommentLocalService.getKBComment(
				kbCommentId);

			int status = ParamUtil.getInteger(
				actionRequest, "status", KBCommentConstants.STATUS_ANY);

			if (status == KBCommentConstants.STATUS_ANY) {
				_kbCommentService.updateKBComment(
					kbCommentId, kbComment.getClassNameId(),
					kbComment.getClassPK(), content, serviceContext);
			}
			else {
				_kbCommentService.updateKBComment(
					kbCommentId, kbComment.getClassNameId(),
					kbComment.getClassPK(), content, status, serviceContext);
			}
		}

		SessionMessages.add(actionRequest, "suggestionSaved");
	}

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference
	private KBCommentLocalService _kbCommentLocalService;

	@Reference
	private KBCommentService _kbCommentService;

	@Reference
	private Portal _portal;

}