/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.diff.exception.CompareVersionsException;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.util.JournalHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/compare_versions"
	},
	service = MVCResourceCommand.class
)
public class CompareVersionsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(resourceResponse);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(resourceRequest, "groupId");
		String articleId = ParamUtil.getString(resourceRequest, "articleId");
		double sourceVersion = ParamUtil.getDouble(
			resourceRequest, "filterSourceVersion");
		double targetVersion = ParamUtil.getDouble(
			resourceRequest, "filterTargetVersion");
		String languageId = ParamUtil.getString(resourceRequest, "languageId");

		StringBundler sb = new StringBundler(3);

		try {
			sb.append("<div class=\"taglib-diff-html\">");
			sb.append(
				_journalHelper.diffHtml(
					groupId, articleId, sourceVersion, targetVersion,
					languageId,
					new PortletRequestModel(resourceRequest, resourceResponse),
					themeDisplay));
			sb.append("</div>");
		}
		catch (CompareVersionsException compareVersionsException) {
			sb.append("<div class=\"alert alert-info\">");
			sb.append(
				_language.format(
					httpServletRequest, "unable-to-render-version-x",
					compareVersionsException.getVersion()));
			sb.append("</div>");
		}
		catch (Exception exception) {
			try {
				_portal.sendError(
					exception, httpServletRequest, httpServletResponse);
			}
			catch (ServletException servletException) {
				if (_log.isDebugEnabled()) {
					_log.debug(servletException);
				}
			}

			sb.append("<div class=\"alert alert-info\">");
			sb.append(
				_language.get(
					httpServletRequest, "these-versions-are-not-comparable"));
			sb.append("</div>");
		}

		ServletResponseUtil.write(httpServletResponse, sb.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompareVersionsMVCResourceCommand.class);

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}