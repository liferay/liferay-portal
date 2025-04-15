/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.aui.web.internal.servlet.taglib;

import com.liferay.frontend.js.aui.web.internal.configuration.AUIConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.ComboRequestAbsolutePortalURLBuilder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	configurationPid = "com.liferay.frontend.js.aui.web.internal.configuration.AUIConfiguration",
	property = "service.ranking:Integer=" + (Integer.MAX_VALUE - 1),
	service = DynamicInclude.class
)
public class AUITopHeadJSDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isThemeJsFastLoad()) {
			if (themeDisplay.isThemeJsBarebone()) {
				_renderBundleComboURLs(
					httpServletRequest, httpServletResponse, _jsResourcePaths);
			}
			else {
				_renderBundleComboURLs(
					httpServletRequest, httpServletResponse,
					_allJSResourcePaths);
			}
		}
		else {
			if (themeDisplay.isThemeJsBarebone()) {
				_renderBundleURLs(
					httpServletRequest, httpServletResponse, _jsResourcePaths);
			}
			else {
				_renderBundleURLs(
					httpServletRequest, httpServletResponse,
					_allJSResourcePaths);
			}
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		AUIConfiguration auiConfiguration = ConfigurableUtil.createConfigurable(
			AUIConfiguration.class, properties);

		List<String> allJSResourcePaths = new ArrayList<>();
		List<String> jsResourcePaths = new ArrayList<>();

		for (String resourcePath : _FILE_NAMES_AUI_CORE) {
			allJSResourcePaths.add(
				_servletContext.getContextPath() + resourcePath);
			jsResourcePaths.add(
				_servletContext.getContextPath() + resourcePath);
		}

		if (auiConfiguration.enableAUIPreload()) {
			for (String resourcePath : _FILE_NAMES_AUI_PRELOAD) {
				allJSResourcePaths.add(
					_servletContext.getContextPath() + resourcePath);
				jsResourcePaths.add(
					_servletContext.getContextPath() + resourcePath);
			}

			for (String resourcePath : _FILE_NAMES_AUI_PRELOAD_AUTHENTICATED) {
				allJSResourcePaths.add(
					_servletContext.getContextPath() + resourcePath);
			}
		}

		_allJSResourcePaths = allJSResourcePaths;
		_jsResourcePaths = jsResourcePaths;
	}

	private void _renderBundleComboURLs(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, List<String> urls)
		throws IOException {

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		ComboRequestAbsolutePortalURLBuilder
			comboRequestAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forComboRequest();

		comboRequestAbsolutePortalURLBuilder.setTimestamp(
			PortalWebResourcesUtil.getLastModified(
				PortalWebResourceConstants.RESOURCE_TYPE_JS));

		String comboURL = comboRequestAbsolutePortalURLBuilder.build();

		PrintWriter printWriter = httpServletResponse.getWriter();
		StringBundler sb = new StringBundler();

		for (String url : urls) {
			if ((sb.length() + url.length() + 1) >= 2000) {
				_renderScriptURL(
					httpServletRequest, printWriter, sb.toString());

				sb = new StringBundler();
			}

			if (sb.length() == 0) {
				sb.append(comboURL);
			}

			sb.append(StringPool.AMPERSAND);
			sb.append(url);
		}

		if (sb.length() > 0) {
			_renderScriptURL(httpServletRequest, printWriter, sb.toString());
		}
	}

	private void _renderBundleURLs(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, List<String> urls)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		for (String url : urls) {
			_renderScriptURL(httpServletRequest, printWriter, url);
		}
	}

	private void _renderScriptURL(
		HttpServletRequest httpServletRequest, PrintWriter printWriter,
		String url) {

		printWriter.print("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" data-senna-track=\"permanent\" src=\"");
		printWriter.print(url);
		printWriter.println("\" type=\"text/javascript\"></script>");
	}

	private static final String[] _FILE_NAMES_AUI_CORE = {
		"/aui/aui/aui-min.js", "/liferay/modules.js", "/liferay/aui_sandbox.js",
		"/aui/attribute-base/attribute-base-min.js",
		"/aui/attribute-complex/attribute-complex-min.js",
		"/aui/attribute-core/attribute-core-min.js",
		"/aui/attribute-observable/attribute-observable-min.js",
		"/aui/attribute-extras/attribute-extras-min.js",
		"/aui/event-custom-base/event-custom-base-min.js",
		"/aui/event-custom-complex/event-custom-complex-min.js",
		"/aui/oop/oop-min.js", "/aui/aui-base-lang/aui-base-lang-min.js",
		"/liferay/dependency.js", "/liferay/util.js"
	};

	private static final String[] _FILE_NAMES_AUI_PRELOAD = {
		"/aui/aui-base-html5-shiv/aui-base-html5-shiv-min.js",
		"/aui/arraylist-add/arraylist-add-min.js",
		"/aui/arraylist-filter/arraylist-filter-min.js",
		"/aui/arraylist/arraylist-min.js",
		"/aui/array-extras/array-extras-min.js",
		"/aui/array-invoke/array-invoke-min.js",
		"/aui/base-base/base-base-min.js",
		"/aui/base-pluginhost/base-pluginhost-min.js",
		"/aui/classnamemanager/classnamemanager-min.js",
		"/aui/datatype-xml-format/datatype-xml-format-min.js",
		"/aui/datatype-xml-parse/datatype-xml-parse-min.js",
		"/aui/dom-base/dom-base-min.js", "/aui/dom-core/dom-core-min.js",
		"/aui/dom-screen/dom-screen-min.js", "/aui/dom-style/dom-style-min.js",
		"/aui/event-base/event-base-min.js",
		"/aui/event-delegate/event-delegate-min.js",
		"/aui/event-focus/event-focus-min.js",
		"/aui/event-hover/event-hover-min.js",
		"/aui/event-key/event-key-min.js",
		"/aui/event-mouseenter/event-mouseenter-min.js",
		"/aui/event-mousewheel/event-mousewheel-min.js",
		"/aui/event-outside/event-outside-min.js",
		"/aui/event-resize/event-resize-min.js",
		"/aui/event-simulate/event-simulate-min.js",
		"/aui/event-synthetic/event-synthetic-min.js", "/aui/intl/intl-min.js",
		"/aui/io-base/io-base-min.js", "/aui/io-form/io-form-min.js",
		"/aui/io-queue/io-queue-min.js",
		"/aui/io-upload-iframe/io-upload-iframe-min.js",
		"/aui/io-xdr/io-xdr-min.js", "/aui/json-parse/json-parse-min.js",
		"/aui/json-stringify/json-stringify-min.js",
		"/aui/node-base/node-base-min.js", "/aui/node-core/node-core-min.js",
		"/aui/node-event-delegate/node-event-delegate-min.js",
		"/aui/node-event-simulate/node-event-simulate-min.js",
		"/aui/node-focusmanager/node-focusmanager-min.js",
		"/aui/node-pluginhost/node-pluginhost-min.js",
		"/aui/node-screen/node-screen-min.js",
		"/aui/node-style/node-style-min.js", "/aui/plugin/plugin-min.js",
		"/aui/pluginhost-base/pluginhost-base-min.js",
		"/aui/pluginhost-config/pluginhost-config-min.js",
		"/aui/querystring-stringify-simple/querystring-stringify-simple-min.js",
		"/aui/queue-promote/queue-promote-min.js",
		"/aui/selector-css2/selector-css2-min.js",
		"/aui/selector-css3/selector-css3-min.js",
		"/aui/selector-native/selector-native-min.js",
		"/aui/selector/selector-min.js", "/aui/widget-base/widget-base-min.js",
		"/aui/widget-htmlparser/widget-htmlparser-min.js",
		"/aui/widget-skin/widget-skin-min.js",
		"/aui/widget-uievents/widget-uievents-min.js",
		"/aui/yui-throttle/yui-throttle-min.js",
		"/aui/aui-base-core/aui-base-core-min.js",
		"/aui/aui-classnamemanager/aui-classnamemanager-min.js",
		"/aui/aui-component/aui-component-min.js",
		"/aui/aui-debounce/aui-debounce-min.js",
		"/aui/aui-delayed-task-deprecated/aui-delayed-task-deprecated-min.js",
		"/aui/aui-event-base/aui-event-base-min.js",
		"/aui/aui-event-input/aui-event-input-min.js",
		"/aui/aui-form-validator/aui-form-validator-min.js",
		"/aui/aui-node-base/aui-node-base-min.js",
		"/aui/aui-node-html5/aui-node-html5-min.js",
		"/aui/aui-selector/aui-selector-min.js",
		"/aui/aui-timer/aui-timer-min.js", "/liferay/form.js",
		"/liferay/icon.js", "/liferay/menu.js"
	};

	private static final String[] _FILE_NAMES_AUI_PRELOAD_AUTHENTICATED = {
		"/aui/async-queue/async-queue-min.js",
		"/aui/base-build/base-build-min.js", "/aui/cookie/cookie-min.js",
		"/aui/event-touch/event-touch-min.js", "/aui/overlay/overlay-min.js",
		"/aui/querystring-stringify/querystring-stringify-min.js",
		"/aui/widget-child/widget-child-min.js",
		"/aui/widget-position-align/widget-position-align-min.js",
		"/aui/widget-position-constrain/widget-position-constrain-min.js",
		"/aui/widget-position/widget-position-min.js",
		"/aui/widget-stack/widget-stack-min.js",
		"/aui/widget-stdmod/widget-stdmod-min.js",
		"/aui/aui-aria/aui-aria-min.js",
		"/aui/aui-io-plugin-deprecated/aui-io-plugin-deprecated-min.js",
		"/aui/aui-io-request/aui-io-request-min.js",
		"/aui/aui-loading-mask-deprecated/aui-loading-mask-deprecated-min.js",
		"/aui/aui-overlay-base-deprecated/aui-overlay-base-deprecated-min.js",
		"/aui/aui-overlay-context-deprecated" +
			"/aui-overlay-context-deprecated-min.js",
		"/aui/aui-overlay-manager-deprecated" +
			"/aui-overlay-manager-deprecated-min.js",
		"/aui/aui-overlay-mask-deprecated/aui-overlay-mask-deprecated-min.js",
		"/aui/aui-parse-content/aui-parse-content-min.js",
		"/liferay/session.js", "/liferay/deprecated.js"
	};

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private volatile List<String> _allJSResourcePaths = new ArrayList<>();
	private volatile List<String> _jsResourcePaths = new ArrayList<>();

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.js.aui.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}