/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.service.FaroChannelLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
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
							faroChannel.getChannelId(), _getFaroChannelId()));
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

	private String _getFaroChannelId() {
		if (_faroChannelId != null) {
			return _faroChannelId;
		}

		FaroChannel faroChannel = getFaroChannel();

		if (faroChannel == null) {
			return null;
		}

		_faroChannelId = faroChannel.getChannelId();

		return _faroChannelId;
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

	private FaroChannel _faroChannel;
	private String _faroChannelId;
	private List<FaroChannel> _faroChannels;
	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}