/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.a11y.web.internal.servlet.taglib;

import com.liferay.frontend.js.a11y.web.internal.configuration.A11yConfiguration;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matuzalem Teles
 */
@Component(
	configurationPid = "com.liferay.frontend.js.a11y.web.internal.configuration.A11yConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = DynamicInclude.class
)
public class A11yBottomJSPDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ScriptData scriptData = new ScriptData();

		List<String> denylist = new ArrayList<>(
			Arrays.asList(
				".a11y-popover", "#a11yContainer", "#yui3-css-stamp",
				".dropdown-menu", ".tooltip", "#tooltipContainer",
				"[id*=senna_surface1-screen]:not([class=flipped])"));

		if (_a11yConfiguration.denylist() != null) {
			Collections.addAll(denylist, _a11yConfiguration.denylist());
		}

		if (!_a11yConfiguration.enableEditors()) {
			denylist.add(".alloy-editor-container");
			denylist.add(".cke");
			denylist.add(".cke_editable");
		}

		if (!_a11yConfiguration.enableControlMenu()) {
			denylist.add(".control-menu");
		}

		if (!_a11yConfiguration.enableGlobalMenu()) {
			denylist.add(".applications-menu-modal");
		}

		if (!_a11yConfiguration.enableProductMenu()) {
			denylist.add(".lfr-product-menu-panel");
		}

		JSONObject propsJSONObject = JSONUtil.put(
			"axeOptions",
			JSONUtil.put(
				"frameWaitTime", _a11yConfiguration.axeCoreFrameWaitTime()
			).put(
				"iframes", _a11yConfiguration.axeCoreIframes()
			).put(
				"performanceTimer", _a11yConfiguration.axeCorePerformanceTimer()
			).put(
				"resultTypes", new String[] {"violations"}
			).put(
				"runOnly", _a11yConfiguration.axeCoreRunOnly()
			)
		).put(
			"denylist", denylist
		).put(
			"mutations",
			JSONUtil.put(
				"any",
				JSONUtil.put(
					"data-restore-title", _STAR
				).put(
					"draggable", _STAR
				).put(
					"id", _STAR
				)
			).put(
				"body", JSONUtil.put("class", _STAR)
			).put(
				"input",
				JSONUtil.put(
					"id", _STAR
				).put(
					"value", _STAR
				)
			)
		).put(
			"portletId", "frontend-js-a11y-web"
		).put(
			"targets", new String[] {_a11yConfiguration.target()}
		);

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		scriptData.append(
			null,
			new JSFragment(
				"main(" + propsJSONObject.toString() + ");",
				Arrays.asList(
					ESImportUtil.getESImport(
						absolutePortalURLBuilder,
						"{main} from frontend-js-a11y-web"))));

		scriptData.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		if (_a11yConfiguration.enable()) {
			dynamicIncludeRegistry.register(
				"/html/common/themes/bottom.jsp#post");
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_a11yConfiguration = ConfigurableUtil.createConfigurable(
			A11yConfiguration.class, properties);
	}

	private static final String[] _STAR = {"*"};

	private volatile A11yConfiguration _a11yConfiguration;

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

}