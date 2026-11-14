/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroChannelLocalServiceUtil;
import com.liferay.osb.faro.service.FaroPreferencesLocalServiceUtil;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class ChannelSelectorDisplayContext {

	public ChannelSelectorDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems() throws PortalException {
		DropdownItemList dropdownItemList = new DropdownItemList();

		String layoutFullURL = PortalUtil.getLayoutFullURL(
			_themeDisplay.getLayout(), _themeDisplay);

		for (FaroChannel faroChannel : _getFaroChannels()) {
			dropdownItemList.add(
				dropdownItem -> {
					dropdownItem.setActive(
						Objects.equals(
							faroChannel.getChannelId(), getFaroChannelId()));
					dropdownItem.setHref(
						HttpComponentsUtil.addParameter(
							layoutFullURL, "channelId",
							faroChannel.getChannelId()));
					dropdownItem.setLabel(faroChannel.getName());
				});
		}

		return dropdownItemList;
	}

	public FaroChannel getFaroChannel() {
		if (_faroChannel != null) {
			return _faroChannel;
		}

		List<FaroChannel> faroChannels = _getFaroChannels();

		if (faroChannels.isEmpty()) {
			return null;
		}

		String channelId = ParamUtil.getString(
			_httpServletRequest, "channelId");

		if (Validator.isNotNull(channelId)) {
			SessionClicks.put(
				_httpServletRequest,
				_KEY_SELECTED_CHANNEL_ID + _themeDisplay.getScopeGroupId(),
				channelId);
		}
		else {
			channelId = SessionClicks.get(
				_httpServletRequest,
				_KEY_SELECTED_CHANNEL_ID + _themeDisplay.getScopeGroupId(),
				null);
		}

		if (Validator.isNull(channelId)) {
			FaroPreferences faroPreferences =
				FaroPreferencesLocalServiceUtil.fetchFaroPreferences(
					_themeDisplay.getScopeGroupId(), _themeDisplay.getUserId());

			if (faroPreferences != null) {
				try {
					WorkspacePreferences workspacePreferences =
						JSONUtil.readValue(
							faroPreferences.getPreferences(),
							WorkspacePreferences.class);

					channelId = workspacePreferences.getDefaultChannelId();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		FaroChannel faroChannel = FaroChannelLocalServiceUtil.fetchFaroChannel(
			channelId, _themeDisplay.getScopeGroupId());

		if ((faroChannel != null) && faroChannels.contains(faroChannel)) {
			_faroChannel = faroChannel;
		}
		else {
			_faroChannel = faroChannels.get(0);
		}

		return _faroChannel;
	}

	public String getFaroChannelId() {
		FaroChannel faroChannel = getFaroChannel();

		if (faroChannel == null) {
			return StringPool.BLANK;
		}

		return faroChannel.getChannelId();
	}

	private List<FaroChannel> _getFaroChannels() {
		if (_faroChannels != null) {
			return _faroChannels;
		}

		_faroChannels = FaroChannelLocalServiceUtil.search(
			_themeDisplay.getScopeGroupId(), null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return _faroChannels;
	}

	private static final String _KEY_SELECTED_CHANNEL_ID =
		"com.liferay.osb.faro.web_selectedChannelId_";

	private static final Log _log = LogFactoryUtil.getLog(
		ChannelSelectorDisplayContext.class);

	private FaroChannel _faroChannel;
	private List<FaroChannel> _faroChannels;
	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}