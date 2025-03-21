/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.comment.internal.search;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.result.SearchResultContributor;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author André de Oliveira
 */
@Component(service = SearchResultContributor.class)
public class MBMessageCommentSearchResultContributor
	implements SearchResultContributor {

	@Override
	public void addRelatedModel(
			SearchResult searchResult, Document document, Locale locale,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		long entryClassPK = GetterUtil.getLong(
			document.get(Field.ENTRY_CLASS_PK));

		MBMessage mbMessage = _mbMessageLocalService.getMessage(entryClassPK);

		Comment comment = _commentManager.fetchComment(
			mbMessage.getMessageId());

		Summary summary = new Summary(null, mbMessage.getBody());

		summary.setEscape(false);

		searchResult.addComment(comment, summary);
	}

	@Override
	public String getEntryClassName() {
		return MBMessage.class.getName();
	}

	@Reference
	private CommentManager _commentManager;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

}