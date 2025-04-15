/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BodyContentWrapper;
import com.liferay.portal.kernel.util.ServerDetector;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-13878.
 * </p>
 *
 * @author Shuyang Zhou
 */
public class BaseBodyTagSupport extends TagSupport {

	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	public void doInitBody() throws JspException {
	}

	@Override
	public int doStartTag() throws JspException {
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	public BodyContent getBodyContent() {
		return bodyContent;
	}

	public StringBundler getBodyContentAsStringBundler() {
		if (!(this instanceof BodyTag)) {
			Class<?> clazz = getClass();

			throw new RuntimeException(
				clazz.getName() + " must implement " + BodyTag.class.getName());
		}

		BodyContent bodyContent = getBodyContent();

		if (bodyContent instanceof BodyContentWrapper) {
			BodyContentWrapper bodyContentWrapper =
				(BodyContentWrapper)bodyContent;

			return bodyContentWrapper.getStringBundler();
		}

		if (bodyContent == null) {
			return new StringBundler();
		}

		if (ServerDetector.isTomcat() && _log.isWarnEnabled()) {
			_log.warn(
				"BodyContent is not BodyContentWrapper. Check " +
					"JspFactorySwapper.");
		}

		String bodyContentString = bodyContent.getString();

		if (bodyContentString == null) {
			return new StringBundler();
		}

		return new StringBundler(bodyContentString);
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest)pageContext.getRequest();
	}

	@Override
	public void release() {
		bodyContent = null;

		super.release();
	}

	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

	public void writeBodyContent(Writer writer) throws IOException {
		StringBundler sb = getBodyContentAsStringBundler();

		sb.writeTo(writer);
	}

	protected BodyContent bodyContent;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseBodyTagSupport.class);

}