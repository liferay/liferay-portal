/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.service.KBCommentServiceUtil;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
public class KBSuggestionListDisplayContext {

	public KBSuggestionListDisplayContext(
		HttpServletRequest httpServletRequest, KBArticle kbArticle) {

		_httpServletRequest = httpServletRequest;
		_kbArticle = kbArticle;

		_groupId = kbArticle.getGroupId();
		_navigation = ParamUtil.getString(
			httpServletRequest, "navigation", "all");
	}

	public KBSuggestionListDisplayContext(
		HttpServletRequest httpServletRequest, long groupId) {

		_httpServletRequest = httpServletRequest;
		_groupId = groupId;

		_navigation = ParamUtil.getString(
			httpServletRequest, "navigation", "all");
	}

	public int getCompletedKBCommentsCount() throws PortalException {
		return getKBCommentsCount(KBCommentConstants.STATUS_COMPLETED);
	}

	public String getEmptyResultsMessage() {
		if (_navigation.equals("new")) {
			return "there-are-no-new-suggestions";
		}

		if (_navigation.equals("in-progress")) {
			return "there-are-no-suggestions-in-progress";
		}

		if (_navigation.equals("resolved")) {
			return "there-are-no-resolved-suggestions";
		}

		return "there-are-no-suggestions";
	}

	public long getGroupId() {
		return _groupId;
	}

	public int getInProgressKBCommentsCount() throws PortalException {
		return getKBCommentsCount(KBCommentConstants.STATUS_IN_PROGRESS);
	}

	public int getKBCommentsCount(int status) throws PortalException {
		if (_kbArticle == null) {
			return KBCommentServiceUtil.getKBCommentsCount(_groupId, status);
		}

		return KBCommentServiceUtil.getKBCommentsCount(
			_groupId, KBArticleConstants.getClassName(),
			_kbArticle.getResourcePrimKey(), status);
	}

	public int getNewKBCommentsCount() throws PortalException {
		return getKBCommentsCount(KBCommentConstants.STATUS_NEW);
	}

	public boolean isShowKBArticleTitle() {
		if (_kbArticle == null) {
			return true;
		}

		return false;
	}

	public void populateResultsAndTotal(
			SearchContainer<KBComment> searchContainer)
		throws PortalException {

		int status = _getStatus();

		if (_kbArticle == null) {
			if (status == KBCommentConstants.STATUS_ANY) {
				searchContainer.setResultsAndTotal(
					() -> KBCommentServiceUtil.getKBComments(
						_groupId, searchContainer.getStart(),
						searchContainer.getEnd(),
						KBUtil.getKBCommentOrderByComparator(
							searchContainer.getOrderByCol(),
							searchContainer.getOrderByType())),
					KBCommentServiceUtil.getKBCommentsCount(_groupId));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> KBCommentServiceUtil.getKBComments(
						_groupId, status, searchContainer.getStart(),
						searchContainer.getEnd(),
						KBUtil.getKBCommentOrderByComparator(
							searchContainer.getOrderByCol(),
							searchContainer.getOrderByType())),
					getKBCommentsCount(status));
			}
		}
		else {
			if (status == KBCommentConstants.STATUS_ANY) {
				searchContainer.setResultsAndTotal(
					() -> KBCommentServiceUtil.getKBComments(
						_groupId, KBArticleConstants.getClassName(),
						_kbArticle.getResourcePrimKey(),
						searchContainer.getStart(), searchContainer.getEnd(),
						KBUtil.getKBCommentOrderByComparator(
							searchContainer.getOrderByCol(),
							searchContainer.getOrderByType())),
					KBCommentServiceUtil.getKBCommentsCount(
						_groupId, KBArticleConstants.getClassName(),
						_kbArticle.getResourcePrimKey()));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> KBCommentServiceUtil.getKBComments(
						_groupId, KBArticleConstants.getClassName(),
						_kbArticle.getResourcePrimKey(), status,
						searchContainer.getStart(), searchContainer.getEnd(),
						KBUtil.getKBCommentOrderByComparator(
							searchContainer.getOrderByCol(),
							searchContainer.getOrderByType())),
					getKBCommentsCount(status));
			}
		}
	}

	private int _getStatus() {
		if (_navigation.equals("new")) {
			return KBCommentConstants.STATUS_NEW;
		}

		if (_navigation.equals("in-progress")) {
			return KBCommentConstants.STATUS_IN_PROGRESS;
		}

		if (_navigation.equals("resolved")) {
			return KBCommentConstants.STATUS_COMPLETED;
		}

		return KBCommentConstants.STATUS_ANY;
	}

	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private KBArticle _kbArticle;
	private final String _navigation;

}