/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.util.ServicesProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.taglib.util.AttributesTagSupport;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.Map;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
public class StylesheetTag extends AttributesTagSupport {

	@Override
	public int doEndTag() throws JspException {
		Map<String, Bundle> bundleMap = ServicesProvider.getBundleMap();

		Bundle bundle = bundleMap.get(_bundle);

		if (bundle == null) {
			throw new JspException("Unable to find bundle " + _bundle);
		}

		AbsolutePortalURLBuilderFactory absolutePortalURLBuilderFactory =
			ServicesProvider.getAbsolutePortalURLBuilderFactory();

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		OutputData outputData = _getOutputData(httpServletRequest);

		StringBundler sb = new StringBundler(5);

		sb.append("<link href=\"");
		sb.append(
			absolutePortalURLBuilder.forBundleStylesheet(
				bundle, _css
			).build());
		sb.append(StringPool.QUOTE);
		sb.append(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		sb.append(" rel=\"stylesheet\" type=\"text/css\"></link>");

		outputData.addDataSB(_getOutputKey(), WebKeys.PAGE_TOP, sb);

		return EVAL_PAGE;
	}

	public void setBundle(String bundle) {
		_bundle = bundle;
	}

	public void setCss(String css) {
		_css = css;
	}

	private OutputData _getOutputData(ServletRequest servletRequest) {
		OutputData outputData = (OutputData)servletRequest.getAttribute(
			WebKeys.OUTPUT_DATA);

		if (outputData == null) {
			outputData = new OutputData();

			servletRequest.setAttribute(WebKeys.OUTPUT_DATA, outputData);
		}

		return outputData;
	}

	private String _getOutputKey() {
		return _bundle + StringPool.SLASH + _css;
	}

	private String _bundle;
	private String _css;

}