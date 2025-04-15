/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.struts;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.rss.util.RSSUtil;
import com.liferay.wiki.configuration.WikiGroupServiceOverriddenConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.web.internal.display.context.helper.WikiRequestHelper;
import com.liferay.wiki.web.internal.util.WikiUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(property = "path=/wiki/rss", service = StrutsAction.class)
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

		String rss = StringPool.BLANK;

		long nodeId = ParamUtil.getLong(httpServletRequest, "nodeId");

		if (nodeId <= 0) {
			return rss.getBytes(StringPool.UTF8);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String title = ParamUtil.getString(httpServletRequest, "title");
		int max = ParamUtil.getInteger(
			httpServletRequest, "max", SearchContainer.DEFAULT_DELTA);
		String type = ParamUtil.getString(
			httpServletRequest, "type", RSSUtil.FORMAT_DEFAULT);
		double version = ParamUtil.getDouble(
			httpServletRequest, "version", RSSUtil.VERSION_DEFAULT);
		String displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle", RSSUtil.DISPLAY_STYLE_DEFAULT);
		String feedURL = StringBundler.concat(
			_portal.getLayoutFullURL(
				themeDisplay.getScopeGroupId(), WikiPortletKeys.WIKI),
			Portal.FRIENDLY_URL_SEPARATOR, "wiki/", nodeId);

		String entryURL = feedURL + StringPool.SLASH + title;

		String attachmentURLPrefix = WikiUtil.getAttachmentURLPrefix(
			themeDisplay.getPathMain(), themeDisplay.getPlid(), nodeId, title);

		if (Validator.isNotNull(title)) {
			rss = _wikiPageService.getPagesRSS(
				nodeId, title, max, type, version, displayStyle, feedURL,
				entryURL, attachmentURLPrefix, themeDisplay.getLocale());
		}
		else {
			rss = _wikiPageService.getNodePagesRSS(
				nodeId, max, type, version, displayStyle, feedURL, entryURL,
				attachmentURLPrefix);
		}

		return rss.getBytes(StringPool.UTF8);
	}

	private boolean _isRSSFeedsEnabled(HttpServletRequest httpServletRequest)
		throws Exception {

		WikiRequestHelper wikiRequestHelper = new WikiRequestHelper(
			httpServletRequest);

		WikiGroupServiceOverriddenConfiguration
			wikiGroupServiceOverriddenConfiguration =
				wikiRequestHelper.getWikiGroupServiceOverriddenConfiguration();

		return wikiGroupServiceOverriddenConfiguration.enableRss();
	}

	@Reference
	private Portal _portal;

	@Reference
	private WikiPageService _wikiPageService;

}