/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.exception.TrackbackValidationException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.trackback.Trackback;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(
	property = {
		"auth.token.ignore.mvc.action=true",
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_AGGREGATOR,
		"mvc.command.name=/blogs/trackback"
	},
	service = MVCActionCommand.class
)
public class TrackbackMVCActionCommand extends BaseMVCActionCommand {

	public void addTrackback(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			BlogsEntry entry = _getBlogsEntry(actionRequest);

			_validate(entry);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(actionRequest);

			HttpServletRequest originalHttpServletRequest =
				_portal.getOriginalServletRequest(httpServletRequest);

			String excerpt = ParamUtil.getString(
				originalHttpServletRequest, "excerpt");
			String url = ParamUtil.getString(originalHttpServletRequest, "url");
			String blogName = ParamUtil.getString(
				originalHttpServletRequest, "blog_name");
			String title = ParamUtil.getString(
				originalHttpServletRequest, "title");

			_validate(actionRequest, httpServletRequest.getRemoteAddr(), url);

			_trackback.addTrackback(
				entry, themeDisplay, excerpt, url, blogName, title,
				new ServiceContextFunction(actionRequest));
		}
		catch (TrackbackValidationException trackbackValidationException) {
			_sendError(
				actionRequest, actionResponse,
				trackbackValidationException.getMessage());

			return;
		}

		_sendSuccess(actionRequest, actionResponse);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			addTrackback(actionRequest, actionResponse);
		}
		catch (NoSuchEntryException noSuchEntryException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchEntryException);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private BlogsEntry _getBlogsEntry(ActionRequest actionRequest)
		throws Exception {

		try {
			return ActionUtil.getEntry(actionRequest);
		}
		catch (PrincipalException principalException) {
			throw new TrackbackValidationException(
				"Blog entry must have guest view permissions to enable " +
					"trackbacks",
				principalException);
		}
	}

	private boolean _isCommentsEnabled(ActionRequest actionRequest)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				actionRequest);

		if (portletPreferences == null) {
			portletPreferences = actionRequest.getPreferences();
		}

		return GetterUtil.getBoolean(
			portletPreferences.getValue("enableComments", null), true);
	}

	private void _sendError(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String msg)
		throws Exception {

		_sendResponse(actionRequest, actionResponse, msg, false);
	}

	private void _sendResponse(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String msg, boolean success)
		throws Exception {

		StringBundler sb = new StringBundler(7);

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<response>");

		if (success) {
			sb.append("<error>0</error>");
		}
		else {
			sb.append("<error>1</error>");
			sb.append("<message>");
			sb.append(msg);
			sb.append("</message>");
		}

		sb.append("</response>");

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		String s = sb.toString();

		ServletResponseUtil.sendFile(
			httpServletRequest, httpServletResponse, null,
			s.getBytes(StringPool.UTF8), ContentTypes.TEXT_XML_UTF8);
	}

	private void _sendSuccess(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_sendResponse(actionRequest, actionResponse, null, true);
	}

	private void _validate(
			ActionRequest actionRequest, String remoteIP, String url)
		throws Exception {

		if (!_isCommentsEnabled(actionRequest)) {
			throw new TrackbackValidationException("Comments are disabled");
		}

		if (Validator.isNull(url)) {
			throw new TrackbackValidationException(
				"Trackback requires a valid permanent URL");
		}

		String trackbackIP = HttpComponentsUtil.getIpAddress(url);

		if (!remoteIP.equals(trackbackIP)) {
			throw new TrackbackValidationException(
				"Remote IP does not match the trackback URL's IP");
		}
	}

	private void _validate(BlogsEntry entry)
		throws TrackbackValidationException {

		if (!entry.isAllowTrackbacks()) {
			throw new TrackbackValidationException(
				"Trackbacks are not enabled");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TrackbackMVCActionCommand.class);

	@Reference
	private Portal _portal;

	@Reference
	private Trackback _trackback;

}