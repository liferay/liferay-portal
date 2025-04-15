/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.exception.NoSuchEntryException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/discard_draft_style_book_entry"
	},
	service = MVCActionCommand.class
)
public class DiscardDraftStyleBookEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long styleBookEntryId = ParamUtil.getLong(
			actionRequest, "styleBookEntryId");

		try {
			StyleBookEntry styleBookEntry =
				_styleBookEntryLocalService.fetchStyleBookEntry(
					styleBookEntryId);

			if (styleBookEntry == null) {
				throw new NoSuchEntryException();
			}

			long draftStyleBookEntryId = styleBookEntryId;

			if (styleBookEntry.isHead()) {
				StyleBookEntry draftStyleBookEntry =
					_styleBookEntryLocalService.getDraft(styleBookEntryId);

				draftStyleBookEntryId =
					draftStyleBookEntry.getStyleBookEntryId();
			}

			_styleBookEntryService.discardDraftStyleBookEntry(
				draftStyleBookEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			SessionErrors.add(actionRequest, PortalException.class);
		}

		if (!SessionErrors.isEmpty(actionRequest)) {
			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DiscardDraftStyleBookEntryMVCActionCommand.class);

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}