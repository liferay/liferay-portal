/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.servlet.taglib;

import com.liferay.cookies.global.privacy.control.GlobalPrivacyControlProvider;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(
	property = "dynamic.include.key=/html/common/themes/bottom.jsp#post",
	service = DynamicInclude.class
)
public class GlobalPrivacyControlBottomJSDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!_globalPrivacyControlProvider.isEnabled(httpServletRequest)) {
			return;
		}

		ScriptData scriptData = new ScriptData();

		scriptData.append(
			GlobalPrivacyControlBottomJSDynamicInclude.class.getName(),
			new JSFragment(
				StringBundler.concat(
					"initGlobalPrivacyControl(",
					_globalPrivacyControlProvider.isSignalActive(
						httpServletRequest),
					");"),
				Arrays.asList(
					ESImportUtil.getESImport(
						_absolutePortalURLBuilderFactory.
							getAbsolutePortalURLBuilder(httpServletRequest),
						"{initGlobalPrivacyControl} from " +
							"cookies-banner-web/index.js"))));

		scriptData.writeTo(httpServletResponse.getWriter());
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private GlobalPrivacyControlProvider _globalPrivacyControlProvider;

}