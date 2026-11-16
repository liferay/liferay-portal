/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal.servlet.taglib;

import com.liferay.frontend.js.bundle.config.extender.internal.JSBundleConfigRegistryUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(
	property = {
		"dynamic.include.key=/html/common/themes/top_js.jspf#resources",
		"service.ranking:Integer=" + Integer.MIN_VALUE
	},
	service = DynamicInclude.class
)
public class JSBundleConfigTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		String nonce = ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
			httpServletRequest);

		if (Validator.isNotNull(nonce)) {
			_writeResponse(httpServletResponse, _getBundleConfig(nonce));

			return;
		}

		if (!_isStale()) {
			_writeResponse(httpServletResponse, _objectValuePair.getValue());

			return;
		}

		String bundleConfig = _getBundleConfig(StringPool.BLANK);

		_objectValuePair = new ObjectValuePair<>(
			JSBundleConfigRegistryUtil.getLastModified(), bundleConfig);

		_writeResponse(httpServletResponse, bundleConfig);
	}

	private void _appendJavaScript(
		StringBundler sb,
		Collection<JSBundleConfigRegistryUtil.JSConfig> jsConfigs) {

		for (JSBundleConfigRegistryUtil.JSConfig jsConfig : jsConfigs) {
			try {
				sb.append("try {var MODULE_MAIN='");

				ServletContext servletContext = jsConfig.getServletContext();

				sb.append(_getModuleMain(jsConfig));
				sb.append("';var MODULE_PATH='");
				sb.append(_portal.getPathProxy());
				sb.append(servletContext.getContextPath());
				sb.append("';");
				sb.append(
					StringUtil.removeSubstring(
						URLUtil.toString(jsConfig.getURL()),
						"//# sourceMappingURL=config.js.map"));
				sb.append("} catch(error) {console.error(error);}");
			}
			catch (Exception exception) {
				_log.error("Unable to open resource", exception);
			}
		}
	}

	private String _getBundleConfig(String nonce) {
		Collection<JSBundleConfigRegistryUtil.JSConfig> jsConfigs =
			JSBundleConfigRegistryUtil.getJSConfigs();

		if (jsConfigs.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		sb.append("<script");
		sb.append(nonce);
		sb.append(" data-senna-track=\"temporary\" type=\"");
		sb.append(ContentTypes.TEXT_JAVASCRIPT);
		sb.append("\">");

		_appendJavaScript(sb, jsConfigs);

		sb.append("</script>");

		return sb.toString();
	}

	private String _getModuleMain(
		JSBundleConfigRegistryUtil.JSConfig jsConfig) {

		try {
			ServletContext servletContext = jsConfig.getServletContext();

			URL url = servletContext.getResource(
				"META-INF/resources/package.json");

			if (url == null) {
				url = servletContext.getResource("package.json");
			}

			if (url == null) {
				return null;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				URLUtil.toString(url));

			String moduleName = jsonObject.getString("name");
			String moduleVersion = jsonObject.getString("version");

			String moduleMain = jsonObject.getString("main");

			if (Validator.isNull(moduleMain)) {
				moduleMain = "index.js";
			}

			return StringBundler.concat(
				moduleName, "@", moduleVersion, "/",
				ModuleNameUtil.toModuleName(moduleMain));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private boolean _isStale() {
		if (JSBundleConfigRegistryUtil.getLastModified() >
				_objectValuePair.getKey()) {

			return true;
		}

		return false;
	}

	private void _writeResponse(
			HttpServletResponse httpServletResponse, String content)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.println(content);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSBundleConfigTopHeadDynamicInclude.class);

	@Reference
	private JSONFactory _jsonFactory;

	private volatile ObjectValuePair<Long, String> _objectValuePair =
		new ObjectValuePair<>(0L, null);

	@Reference
	private Portal _portal;

}