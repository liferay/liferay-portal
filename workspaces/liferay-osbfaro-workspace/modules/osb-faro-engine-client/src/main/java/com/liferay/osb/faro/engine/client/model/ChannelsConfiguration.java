/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Nilton Vieira
 */
public class ChannelsConfiguration {

	public List<Channel> getChannels() {
		return _channels;
	}

	public boolean isEnableAllChannels() {
		return _enableAllChannels;
	}

	public void setChannels(List<Channel> channels) {
		_channels = channels;
	}

	public void setEnableAllChannels(boolean enableAllChannels) {
		_enableAllChannels = enableAllChannels;
	}

	public static class Channel {

		public String getChannelId() {
			return _channelId;
		}

		public Set<Long> getGroupIds() {
			return _groupIds;
		}

		public Boolean isEnabled() {
			return _enabled;
		}

		public void setChannelId(String channelId) {
			_channelId = channelId;
		}

		public void setEnabled(Boolean enabled) {
			_enabled = enabled;
		}

		public void setGroupIds(Set<Long> groupIds) {
			_groupIds = groupIds;
		}

		private String _channelId;
		private Boolean _enabled;
		private Set<Long> _groupIds;

	}

	private List<Channel> _channels = new ArrayList<>();
	private boolean _enableAllChannels;

}