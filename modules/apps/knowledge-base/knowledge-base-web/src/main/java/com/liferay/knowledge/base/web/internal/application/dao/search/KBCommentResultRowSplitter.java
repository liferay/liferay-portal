/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.application.dao.search;

import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.web.internal.display.context.KBSuggestionListDisplayContext;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Sergio González
 */
public class KBCommentResultRowSplitter implements ResultRowSplitter {

	public KBCommentResultRowSplitter(
		KBSuggestionListDisplayContext kbSuggestionListDisplayContext,
		ResourceBundle resourceBundle) {

		this(kbSuggestionListDisplayContext, resourceBundle, "desc");
	}

	public KBCommentResultRowSplitter(
		KBSuggestionListDisplayContext kbSuggestionListDisplayContext,
		ResourceBundle resourceBundle, String orderByType) {

		_kbSuggestionListDisplayContext = kbSuggestionListDisplayContext;
		_resourceBundle = resourceBundle;
		_orderByType = orderByType;
	}

	@Override
	public List<ResultRowSplitterEntry> split(List<ResultRow> resultRows) {
		List<ResultRowSplitterEntry> resultRowSplitterEntries =
			new ArrayList<>();

		List<ResultRow> newResultRows = new ArrayList<>();
		List<ResultRow> inProgressResultRows = new ArrayList<>();
		List<ResultRow> completedResultRows = new ArrayList<>();

		for (ResultRow resultRow : resultRows) {
			KBComment kbComment = (KBComment)resultRow.getObject();

			if (kbComment.getStatus() == KBCommentConstants.STATUS_NEW) {
				newResultRows.add(resultRow);
			}
			else if (kbComment.getStatus() ==
						KBCommentConstants.STATUS_IN_PROGRESS) {

				inProgressResultRows.add(resultRow);
			}
			else {
				completedResultRows.add(resultRow);
			}
		}

		if (!newResultRows.isEmpty()) {
			resultRowSplitterEntries.add(
				new ResultRowSplitterEntry(
					_getNewKBCommentsLabel(), newResultRows));
		}

		if (!inProgressResultRows.isEmpty()) {
			resultRowSplitterEntries.add(
				new ResultRowSplitterEntry(
					_getInProgressKBCommentsLabel(), inProgressResultRows));
		}

		if (!completedResultRows.isEmpty()) {
			resultRowSplitterEntries.add(
				new ResultRowSplitterEntry(
					_getCompletedKBCommentsLabel(), completedResultRows));
		}

		if (_orderByType.equals("asc")) {
			Collections.reverse(resultRowSplitterEntries);
		}

		return resultRowSplitterEntries;
	}

	private String _getCompletedKBCommentsLabel() {
		int completedKBCommentsCount = 0;

		try {
			completedKBCommentsCount =
				_kbSuggestionListDisplayContext.getCompletedKBCommentsCount();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to obtain completed knowledge base comments count " +
					"for group " + _kbSuggestionListDisplayContext.getGroupId(),
				portalException);
		}

		return String.format(
			"%s (%s)", LanguageUtil.get(_resourceBundle, "resolved"),
			completedKBCommentsCount);
	}

	private String _getInProgressKBCommentsLabel() {
		int inProgressKBCommentsCount = 0;

		try {
			inProgressKBCommentsCount =
				_kbSuggestionListDisplayContext.getInProgressKBCommentsCount();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to obtain in progress knowledge base comments count " +
					"for  group " +
						_kbSuggestionListDisplayContext.getGroupId(),
				portalException);
		}

		return String.format(
			"%s (%s)", LanguageUtil.get(_resourceBundle, "in-progress"),
			inProgressKBCommentsCount);
	}

	private String _getNewKBCommentsLabel() {
		int newKBCommentsCount = 0;

		try {
			newKBCommentsCount =
				_kbSuggestionListDisplayContext.getNewKBCommentsCount();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to obtain new knowledge base comments count for " +
					"group " + _kbSuggestionListDisplayContext.getGroupId(),
				portalException);
		}

		return String.format(
			"%s (%s)", LanguageUtil.get(_resourceBundle, "new"),
			newKBCommentsCount);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBCommentResultRowSplitter.class);

	private final KBSuggestionListDisplayContext
		_kbSuggestionListDisplayContext;
	private final String _orderByType;
	private final ResourceBundle _resourceBundle;

}