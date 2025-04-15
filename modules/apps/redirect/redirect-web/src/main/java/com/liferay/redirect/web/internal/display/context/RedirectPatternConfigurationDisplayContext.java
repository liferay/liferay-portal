/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.display.context;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.redirect.configuration.RedirectPatternConfigurationProvider;
import com.liferay.redirect.constants.RedirectConstants;
import com.liferay.redirect.model.RedirectPatternEntry;
import com.liferay.staging.StagingGroupHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alicia García
 */
public class RedirectPatternConfigurationDisplayContext {

	public RedirectPatternConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RedirectPatternConfigurationProvider
			redirectPatternConfigurationProvider,
		StagingGroupHelper stagingGroupHelper) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_redirectPatternConfigurationProvider =
			redirectPatternConfigurationProvider;
		_stagingGroupHelper = stagingGroupHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getRedirectPatterns() {
		return HashMapBuilder.<String, Object>put(
			"actionUrl", _getRedirectPatternConfigurationURL()
		).put(
			"isStagingEnvironment", isStagingGroup() || isLiveGroup()
		).put(
			"patterns",
			() -> {
				List<Map<String, Object>> list = new ArrayList<>();

				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				List<RedirectPatternEntry> redirectPatternEntries =
					_redirectPatternConfigurationProvider.
						getRedirectPatternEntries(
							themeDisplay.getScopeGroupId());

				redirectPatternEntries.forEach(
					redirectPatternEntry -> list.add(
						HashMapBuilder.<String, Object>put(
							"destinationURL",
							redirectPatternEntry.getDestinationURL()
						).put(
							"pattern",
							String.valueOf(redirectPatternEntry.getPattern())
						).put(
							"userAgent",
							() -> {
								if (redirectPatternEntry.getUserAgent() ==
										null) {

									return RedirectConstants.USER_AGENT_ALL;
								}

								return redirectPatternEntry.getUserAgent();
							}
						).build()));

				return list;
			}
		).put(
			"portletNamespace", _liferayPortletResponse.getNamespace()
		).put(
			"strings",
			HashMapBuilder.put(
				"absoluteURL", PortalUtil.getPortalURL(_httpServletRequest)
			).put(
				"relativeURL",
				PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUT_FRIENDLY_URL
			).build()
		).put(
			"userAgents",
			JSONUtil.putAll(
				JSONUtil.put(
					"label", LanguageUtil.get(_httpServletRequest, "all")
				).put(
					"value", "all"
				)
			).put(
				JSONUtil.put(
					"label", LanguageUtil.get(_httpServletRequest, "bot")
				).put(
					"value", "bot"
				)
			).put(
				JSONUtil.put(
					"label", LanguageUtil.get(_httpServletRequest, "human")
				).put(
					"value", "human"
				)
			)
		).build();
	}

	public boolean isLiveGroup() {
		if (_liveGroup != null) {
			return _liveGroup;
		}

		boolean liveGroup = false;

		if (_stagingGroupHelper.isLiveGroup(_themeDisplay.getScopeGroupId())) {
			liveGroup = true;
		}

		_liveGroup = liveGroup;

		return _liveGroup;
	}

	public boolean isStagingGroup() {
		if (_stagingGroup != null) {
			return _stagingGroup;
		}

		boolean stagingGroup = false;

		if (_stagingGroupHelper.isLocalStagingGroup(
				_themeDisplay.getScopeGroup()) ||
			_stagingGroupHelper.isRemoteStagingGroup(
				_themeDisplay.getScopeGroup())) {

			stagingGroup = true;
		}

		_stagingGroup = stagingGroup;

		return _stagingGroup;
	}

	private String _getRedirectPatternConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/redirect/edit_redirect_patterns"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Boolean _liveGroup;
	private final RedirectPatternConfigurationProvider
		_redirectPatternConfigurationProvider;
	private Boolean _stagingGroup;
	private final StagingGroupHelper _stagingGroupHelper;
	private final ThemeDisplay _themeDisplay;

}