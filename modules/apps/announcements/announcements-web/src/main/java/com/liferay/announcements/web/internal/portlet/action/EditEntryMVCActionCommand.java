/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.portlet.action;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.kernel.exception.EntryContentException;
import com.liferay.announcements.kernel.exception.EntryDisplayDateException;
import com.liferay.announcements.kernel.exception.EntryExpirationDateException;
import com.liferay.announcements.kernel.exception.EntryTitleException;
import com.liferay.announcements.kernel.exception.EntryURLException;
import com.liferay.announcements.kernel.exception.NoSuchEntryException;
import com.liferay.announcements.kernel.service.AnnouncementsEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ALERTS,
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
		"mvc.command.name=/announcements/edit_entry"
	},
	service = MVCActionCommand.class
)
public class EditEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateEntry(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteEntry(actionRequest);
			}
		}
		catch (EntryContentException | EntryDisplayDateException |
			   EntryExpirationDateException | EntryTitleException |
			   EntryURLException | NoSuchEntryException | PrincipalException
				   exception) {

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	private void _deleteEntry(ActionRequest actionRequest) throws Exception {
		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		long[] deleteEntryIds = null;

		if (entryId > 0) {
			deleteEntryIds = new long[] {entryId};
		}
		else {
			deleteEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIdsAnnouncementsEntry");
		}

		for (long deleteEntryId : deleteEntryIds) {
			_announcementsEntryService.deleteEntry(deleteEntryId);
		}
	}

	private void _updateEntry(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		String[] distributionScopeParts = StringUtil.split(
			ParamUtil.getString(actionRequest, "distributionScope"));

		long classNameId = 0;
		long classPK = 0;

		if (distributionScopeParts.length == 2) {
			classNameId = GetterUtil.getLong(distributionScopeParts[0]);

			if (classNameId > 0) {
				classPK = GetterUtil.getLong(distributionScopeParts[1]);
			}
		}

		String title = ParamUtil.getString(actionRequest, "title");
		String content = ParamUtil.getString(actionRequest, "content");
		String url = ParamUtil.getString(actionRequest, "url");
		String type = ParamUtil.getString(actionRequest, "type");

		Date displayDate = new Date();

		boolean displayImmediately = ParamUtil.getBoolean(
			actionRequest, "displayImmediately");

		if (!displayImmediately) {
			int displayDateMonth = ParamUtil.getInteger(
				actionRequest, "displayDateMonth");
			int displayDateDay = ParamUtil.getInteger(
				actionRequest, "displayDateDay");
			int displayDateYear = ParamUtil.getInteger(
				actionRequest, "displayDateYear");
			int displayDateHour = ParamUtil.getInteger(
				actionRequest, "displayDateHour");
			int displayDateMinute = ParamUtil.getInteger(
				actionRequest, "displayDateMinute");
			int displayDateAmPm = ParamUtil.getInteger(
				actionRequest, "displayDateAmPm");

			if (displayDateAmPm == Calendar.PM) {
				displayDateHour += 12;
			}

			displayDate = _portal.getDate(
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, themeDisplay.getTimeZone(),
				EntryDisplayDateException.class);
		}

		int expirationDateMonth = ParamUtil.getInteger(
			actionRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			actionRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			actionRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			actionRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			actionRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			actionRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		Date expirationDate = _portal.getDate(
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute,
			themeDisplay.getTimeZone(), EntryExpirationDateException.class);

		int priority = ParamUtil.getInteger(actionRequest, "priority");

		if (entryId <= 0) {
			boolean alert = ParamUtil.getBoolean(actionRequest, "alert");

			_announcementsEntryService.addEntry(
				classNameId, classPK, title, content, url, type, displayDate,
				expirationDate, priority, alert);
		}
		else {
			_announcementsEntryService.updateEntry(
				entryId, title, content, url, type, displayDate, expirationDate,
				priority);
		}
	}

	@Reference
	private AnnouncementsEntryService _announcementsEntryService;

	@Reference
	private Portal _portal;

}