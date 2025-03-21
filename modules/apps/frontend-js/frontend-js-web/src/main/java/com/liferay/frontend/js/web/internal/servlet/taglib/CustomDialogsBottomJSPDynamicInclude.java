/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.taglib;

import com.liferay.frontend.js.web.internal.configuration.CustomDialogsSettingsConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Diego Nascimento
 */
@Component(
	configurationPid = "com.liferay.frontend.js.web.internal.configuration.CustomDialogsSettingsConfiguration",
	service = DynamicInclude.class
)
public class CustomDialogsBottomJSPDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = themeDisplay.getScopeGroup();

			CustomDialogsSettingsConfiguration
				customDialogsSettingsConfiguration =
					_configurationProvider.getGroupConfiguration(
						CustomDialogsSettingsConfiguration.class,
						group.getGroupId());

			ScriptData scriptData = new ScriptData();

			scriptData.append(
				_portal.getPortletId(httpServletRequest),
				new JSFragment(
					StringBundler.concat(
						"Liferay.CustomDialogs = {enabled: ",
						customDialogsSettingsConfiguration.enabled(), "};")));

			scriptData.writeTo(httpServletResponse.getWriter());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomDialogsBottomJSPDynamicInclude.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}