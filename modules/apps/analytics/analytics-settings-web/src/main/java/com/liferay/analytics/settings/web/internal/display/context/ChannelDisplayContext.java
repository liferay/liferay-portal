/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.analytics.settings.web.internal.model.Channel;
import com.liferay.analytics.settings.web.internal.search.ChannelSearch;
import com.liferay.analytics.settings.web.internal.util.AnalyticsSettingsUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * @author André Miranda
 */
public class ChannelDisplayContext {

	public ChannelDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public ChannelSearch getChannelSearch() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!AnalyticsSettingsUtil.isAnalyticsEnabled(
				themeDisplay.getCompanyId())) {

			return null;
		}

		try {
			ChannelSearch channelSearch = new ChannelSearch(
				_renderRequest, getPortletURL());

			HttpResponse httpResponse = AnalyticsSettingsUtil.doGet(
				_getCompanyId(),
				String.format(
					"api/1.0/channels?filter=%s&page=%d&size=%d",
					_getKeywords(), channelSearch.getCur() - 1,
					channelSearch.getDelta()));

			StatusLine statusLine = httpResponse.getStatusLine();

			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();

				throw new PortalException(
					"Request to Analytics Cloud failed: " +
						StringUtil.read(httpEntity.getContent()));
			}

			JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
				EntityUtils.toString(httpResponse.getEntity()));

			JSONObject embeddedJSONObject = responseJSONObject.getJSONObject(
				"_embedded");

			JSONArray channelsJSONArray = embeddedJSONObject.getJSONArray(
				"channels");

			List<Channel> channels = new ArrayList<>();

			for (int i = 0; i < channelsJSONArray.length(); i++) {
				JSONObject channelJSONObject = channelsJSONArray.getJSONObject(
					i);

				channels.add(
					new Channel(
						channelJSONObject.getLong("id"),
						channelJSONObject.getString("name")));
			}

			JSONObject pageJSONObject = responseJSONObject.getJSONObject(
				"page");

			channelSearch.setResultsAndTotal(
				() -> channels, pageJSONObject.getInt("totalElements"));

			return channelSearch;
		}
		catch (Exception exception) {
			_log.error("Unable to get channel search", exception);

			return null;
		}
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/configuration_admin/view_configuration_screen"
		).setParameter(
			"configurationScreenKey", "1-synced-sites"
		).buildPortletURL();
	}

	private long _getCompanyId() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.getCompanyId();
	}

	private String _getKeywords() {
		if (_keywords == null) {
			_keywords = ParamUtil.getString(_renderRequest, "keywords");
		}

		return HtmlUtil.escapeURL(_keywords);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ChannelDisplayContext.class);

	private String _keywords;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}