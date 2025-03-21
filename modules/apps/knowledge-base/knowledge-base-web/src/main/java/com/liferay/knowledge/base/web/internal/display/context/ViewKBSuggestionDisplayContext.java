/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBCommentServiceUtil;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.knowledge.base.web.internal.display.context.helper.KBArticleURLHelper;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBArticlePermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBCommentPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Adolfo Pérez
 */
public class ViewKBSuggestionDisplayContext {

	public ViewKBSuggestionDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, String rootPortletId) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_rootPortletId = rootPortletId;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public boolean canTransitionToNextStatus() throws PortalException {
		if (_hasUpdateKBArticlePermission() &&
			(_getNextStatus() != KBCommentConstants.STATUS_NONE)) {

			return true;
		}

		return false;
	}

	public boolean canTransitionToPreviousStatus() throws PortalException {
		if (_hasUpdateKBArticlePermission() &&
			(_getPreviousStatus() != KBCommentConstants.STATUS_NONE)) {

			return true;
		}

		return false;
	}

	public String getContainerCssClass() {
		if (_rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_ADMIN)) {
			return "container-form-lg";
		}

		if (_rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_ARTICLE)) {
			return StringPool.BLANK;
		}

		return "container-view";
	}

	public String getDeleteKBCommentURL() throws PortalException {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/knowledge_base/delete_kb_comment"
		).setRedirect(
			getRedirect()
		).setParameter(
			"kbCommentId", getKBCommentId()
		).buildString();
	}

	public String getKBArticleTitle() throws PortalException {
		KBArticle kbArticle = _getKBArticle();

		return kbArticle.getTitle();
	}

	public String getKBArticleURL() throws PortalException {
		KBArticle kbArticle = _getKBArticle();

		KBArticleURLHelper kbArticleURLHelper = new KBArticleURLHelper(
			_renderRequest, _renderResponse);

		PortletURL viewKBArticleURL =
			kbArticleURLHelper.createViewWithRedirectURL(
				kbArticle, PortalUtil.getCurrentURL(_httpServletRequest));

		return viewKBArticleURL.toString();
	}

	public String getKBCommentContent() throws PortalException {
		KBComment kbComment = _getKBComment();

		return kbComment.getContent();
	}

	public long getKBCommentId() throws PortalException {
		KBComment kbComment = _getKBComment();

		return kbComment.getKbCommentId();
	}

	public String getKBCommentStatusLabel() throws PortalException {
		KBComment kbComment = _getKBComment();

		return KBUtil.getStatusLabel(kbComment.getStatus());
	}

	public String getKBCommentTitle() throws PortalException {
		KBComment kbComment = _getKBComment();

		return StringUtil.shorten(kbComment.getContent(), 100);
	}

	public long getKBCommentUserId() throws PortalException {
		KBComment kbComment = _getKBComment();

		return kbComment.getUserId();
	}

	public String getModifiedDateLabel() throws PortalException {
		KBComment kbComment = _getKBComment();

		Date modifiedDate = kbComment.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - modifiedDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "x-suggested-x-ago",
			new String[] {kbComment.getUserName(), modifiedDateDescription});
	}

	public String getNextStatusTransitionLabel() throws PortalException {
		return KBUtil.getStatusTransitionLabel(_getNextStatus());
	}

	public String getNextStatusTransitionURL() throws PortalException {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/knowledge_base/update_kb_comment_status"
		).setRedirect(
			PortalUtil.getCurrentURL(_renderRequest)
		).setParameter(
			"kbCommentId", getKBCommentId()
		).setParameter(
			"kbCommentStatus", _getNextStatus()
		).buildString();
	}

	public String getPreviousStatusTransitionLabel() throws PortalException {
		return KBUtil.getStatusTransitionLabel(_getPreviousStatus());
	}

	public String getPreviousStatusTransitionURL() throws PortalException {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/knowledge_base/update_kb_comment_status"
		).setRedirect(
			PortalUtil.getCurrentURL(_renderRequest)
		).setParameter(
			"kbCommentId", getKBCommentId()
		).setParameter(
			"kbCommentStatus", _getPreviousStatus()
		).buildString();
	}

	public String getRedirect() {
		return PortalUtil.escapeRedirect(
			ParamUtil.getString(
				_httpServletRequest, "redirect",
				PortalUtil.getCurrentURL(_renderRequest)));
	}

	public boolean hasDeleteKBCommentPermission() throws PortalException {
		return KBCommentPermission.contains(
			_themeDisplay.getPermissionChecker(), _getKBComment(),
			KBActionKeys.DELETE);
	}

	public boolean isKBCommentActionsVisible() throws PortalException {
		if (_hasUpdateKBArticlePermission() || hasDeleteKBCommentPermission()) {
			return true;
		}

		return false;
	}

	private KBArticle _getKBArticle() throws PortalException {
		if (_kbArticle != null) {
			return _kbArticle;
		}

		KBComment kbComment = _getKBComment();

		_kbArticle = KBArticleServiceUtil.getLatestKBArticle(
			kbComment.getClassPK(), WorkflowConstants.STATUS_ANY);

		return _kbArticle;
	}

	private KBComment _getKBComment() throws PortalException {
		if (_kbComment != null) {
			return _kbComment;
		}

		_kbComment = KBCommentServiceUtil.getKBComment(
			ParamUtil.getLong(_httpServletRequest, "kbCommentId"));

		return _kbComment;
	}

	private int _getNextStatus() throws PortalException {
		KBComment kbComment = _getKBComment();

		return KBUtil.getNextStatus(kbComment.getStatus());
	}

	private int _getPreviousStatus() throws PortalException {
		KBComment kbComment = _getKBComment();

		return KBUtil.getPreviousStatus(kbComment.getStatus());
	}

	private boolean _hasUpdateKBArticlePermission() throws PortalException {
		return KBArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), _getKBArticle(),
			KBActionKeys.UPDATE);
	}

	private final HttpServletRequest _httpServletRequest;
	private KBArticle _kbArticle;
	private KBComment _kbComment;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final String _rootPortletId;
	private final ThemeDisplay _themeDisplay;

}