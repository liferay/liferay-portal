/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.walkthrough.web.internal.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.walkthrough.web.internal.configuration.WalkthroughConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matuzalem Teles
 * @author Diego Nascimento
 */
@Component(
	configurationPid = "com.liferay.frontend.js.walkthrough.web.internal.configuration.WalkthroughConfiguration",
	service = DynamicInclude.class
)
public class WalkthroughBottomJSPDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-44091")) {

			return;
		}

		Group group = themeDisplay.getScopeGroup();

		String steps = null;

		try {
			WalkthroughConfiguration walkthroughConfiguration =
				_configurationProvider.getGroupConfiguration(
					WalkthroughConfiguration.class, group.getGroupId());

			if (!walkthroughConfiguration.enabled()) {
				return;
			}

			steps = walkthroughConfiguration.steps();

			if (Validator.isNull(steps)) {
				return;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		ScriptData scriptData = new ScriptData();

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		scriptData.append(
			null,
			new JSFragment(
				"main(" + steps + ");",
				Arrays.asList(
					ESImportUtil.getESImport(
						absolutePortalURLBuilder,
						"{main} from frontend-js-walkthrough-web"))));

		scriptData.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WalkthroughBottomJSPDynamicInclude.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private ConfigurationProvider _configurationProvider;

}