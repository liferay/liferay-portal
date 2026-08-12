/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.ChannelDataSource;

import java.util.List;
import java.util.Map;

/**
 * @author Marcos Martins
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class ChannelDataSourceDisplay {

	public ChannelDataSourceDisplay() {
	}

	public ChannelDataSourceDisplay(
		Channel channel, ChannelDataSource channelDataSource) {

		this(channelDataSource);

		for (Map<String, Object> dataSource : channel.getDataSources()) {
			List<String> groupIds = (List<String>)dataSource.get("groupIds");

			_groupsCount += groupIds.size();

			Boolean individualEnabled = (Boolean)dataSource.get(
				"individualEnabled");

			if (individualEnabled) {
				_individualDataSourcesCount += 1;
			}
		}
	}

	public ChannelDataSourceDisplay(ChannelDataSource channelDataSource) {
		_channelId = channelDataSource.getChannelId();
		_enabled = channelDataSource.isEnabled();
		_name = channelDataSource.getName();
	}

	private String _channelId;
	private boolean _enabled;
	private int _groupsCount;
	private int _individualDataSourcesCount;
	private String _name;

}