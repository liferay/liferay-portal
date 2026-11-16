/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal.servlet.taglib;

import com.liferay.frontend.js.bundle.config.extender.internal.JSBundleConfigRegistryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

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

		JSBundleConfigRegistryUtil.appendJavaScript(sb, jsConfigs);

		sb.append("</script>");

		return sb.toString();
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

	private volatile ObjectValuePair<Long, String> _objectValuePair =
		new ObjectValuePair<>(0L, null);

}