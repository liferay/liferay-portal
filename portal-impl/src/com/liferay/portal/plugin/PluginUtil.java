/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.plugin;

import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.model.PluginSetting;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PluginSettingLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class PluginUtil {

	public static <P extends Plugin> List<P> restrictPlugins(
		List<P> plugins, long companyId, long userId) {

		return restrictPlugins(plugins, companyId, 0, userId);
	}

	public static <P extends Plugin> List<P> restrictPlugins(
		List<P> plugins, long companyId, long groupId, long userId) {

		List<P> visiblePlugins = new ArrayList<>(plugins.size());

		for (P plugin : plugins) {
			PluginSetting pluginSetting =
				PluginSettingLocalServiceUtil.getPluginSetting(
					companyId, plugin.getPluginId(), plugin.getPluginType());

			if (!pluginSetting.isActive() ||
				!pluginSetting.hasPermission(userId, groupId)) {

				continue;
			}

			visiblePlugins.add(plugin);
		}

		return visiblePlugins;
	}

	public static <P extends Plugin> List<P> restrictPlugins(
		List<P> plugins, User user) {

		return restrictPlugins(plugins, user.getCompanyId(), user.getUserId());
	}

}