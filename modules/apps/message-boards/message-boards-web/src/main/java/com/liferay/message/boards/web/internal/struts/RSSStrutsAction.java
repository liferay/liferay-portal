/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.struts;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.message.boards.web.internal.util.MBRequestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.util.RSSUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(property = "path=/message_boards/rss", service = StrutsAction.class)
public class RSSStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_isRSSFeedsEnabled(httpServletRequest)) {
			_portal.sendRSSFeedsDisabledError(
				httpServletRequest, httpServletResponse);

			return null;
		}

		try {
			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, null,
				_getRSS(httpServletRequest), ContentTypes.TEXT_XML_UTF8);

			return null;
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	private byte[] _getRSS(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long plid = ParamUtil.getLong(httpServletRequest, "plid");

		if (plid == LayoutConstants.DEFAULT_PLID) {
			plid = themeDisplay.getPlid();
		}

		long companyId = ParamUtil.getLong(httpServletRequest, "companyId");
		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");
		long categoryId = ParamUtil.getLong(httpServletRequest, "mbCategoryId");
		long threadId = ParamUtil.getLong(httpServletRequest, "threadId");
		int max = ParamUtil.getInteger(
			httpServletRequest, "max", SearchContainer.DEFAULT_DELTA);
		String type = ParamUtil.getString(
			httpServletRequest, "type", RSSUtil.FORMAT_DEFAULT);
		double version = ParamUtil.getDouble(
			httpServletRequest, "version", RSSUtil.VERSION_DEFAULT);
		String displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle", RSSUtil.DISPLAY_STYLE_DEFAULT);

		String entryURL = StringBundler.concat(
			themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
			"/message_boards/find_message?p_l_id=", plid);

		String rss = StringPool.BLANK;

		if (threadId != 0) {
			String feedURL = StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				"/message_boards/find_thread?p_l_id=", plid, "&threadId=",
				threadId);

			rss = _mbMessageService.getThreadMessagesRSS(
				threadId, WorkflowConstants.STATUS_APPROVED, max, type, version,
				displayStyle, feedURL, entryURL, themeDisplay);
		}
		else if (categoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			String feedURL = StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				"/message_boards/find_category?p_l_id=", plid, "&mbCategoryId=",
				categoryId);

			rss = _mbMessageService.getCategoryMessagesRSS(
				groupId, categoryId, WorkflowConstants.STATUS_APPROVED, max,
				type, version, displayStyle, feedURL, entryURL, themeDisplay);
		}
		else if (groupId != 0) {
			String mvcRenderCommandName = ParamUtil.getString(
				httpServletRequest, "mvcRenderCommandName");

			String feedURL = null;

			if (mvcRenderCommandName.equals(
					"/message_boards/view_recent_posts")) {

				feedURL = StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					"/message_boards/find_recent_posts?p_l_id=", plid);
			}
			else {
				feedURL = StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					"/message_boards/find_category?p_l_id=", plid,
					"&mbCategoryId=", categoryId);
			}

			long userId = ParamUtil.getLong(httpServletRequest, "userId");

			if (userId > 0) {
				rss = _mbMessageService.getGroupMessagesRSS(
					groupId, userId, WorkflowConstants.STATUS_APPROVED, max,
					type, version, displayStyle, feedURL, entryURL,
					themeDisplay);
			}
			else {
				rss = _mbMessageService.getGroupMessagesRSS(
					groupId, WorkflowConstants.STATUS_APPROVED, max, type,
					version, displayStyle, feedURL, entryURL, themeDisplay);
			}
		}
		else if (companyId != 0) {
			String feedURL = StringPool.BLANK;

			rss = _mbMessageService.getCompanyMessagesRSS(
				companyId, WorkflowConstants.STATUS_APPROVED, max, type,
				version, displayStyle, feedURL, entryURL, themeDisplay);
		}

		return rss.getBytes(StringPool.UTF8);
	}

	private boolean _isRSSFeedsEnabled(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		MBGroupServiceSettings mbGroupServiceSettings =
			MBRequestUtil.getMBGroupServiceSettings(
				httpServletRequest, themeDisplay.getSiteGroupId());

		return mbGroupServiceSettings.isEnableRSS();
	}

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private Portal _portal;

}