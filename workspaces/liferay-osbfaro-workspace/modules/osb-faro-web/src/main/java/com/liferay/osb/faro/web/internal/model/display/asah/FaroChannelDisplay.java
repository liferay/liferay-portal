/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.asah;

import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.credentials.DummyCredentials;
import com.liferay.osb.faro.engine.client.model.credentials.TokenCredentials;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * @author André Miranda
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class FaroChannelDisplay {

	public FaroChannelDisplay() {
	}

	public FaroChannelDisplay(Channel channel, FaroChannel faroChannel) {
		this(faroChannel);

		_groupsCount = 0;

		for (Map<String, Object> dataSource : channel.getDataSources()) {
			List<String> groupIds = (List)dataSource.get("groupIds");

			_groupsCount += groupIds.size();
		}

		Map<String, Object> embeddedResources = channel.getEmbeddedResources();

		if (MapUtil.isNotEmpty(embeddedResources)) {
			for (Map<String, Object> dataSource :
					(List<Map<String, Object>>)embeddedResources.get(
						"data-sources")) {

				Map<String, Object> credentials =
					(Map<String, Object>)dataSource.get("credentials");

				if (StringUtil.equals(
						String.valueOf(credentials.get("type")),
						DummyCredentials.TYPE) ||
					StringUtil.equals(
						String.valueOf(credentials.get("type")),
						TokenCredentials.TYPE)) {

					_tokenAuth = true;

					break;
				}
			}
		}
	}

	public FaroChannelDisplay(FaroChannel faroChannel) {
		_createTime = faroChannel.getCreateTime();
		_id = faroChannel.getChannelId();
		_name = faroChannel.getName();
		_permissionType = faroChannel.getPermissionType();
	}

	private long _createTime;
	private int _groupsCount;
	private String _id;
	private String _name;
	private int _permissionType;
	private boolean _tokenAuth;

}