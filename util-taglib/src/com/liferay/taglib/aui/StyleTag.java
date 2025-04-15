/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseStyleTag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.io.IOException;

import java.util.Objects;

/**
 * @author Iván Zaera Avellón
 */
public class StyleTag extends BaseStyleTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write("<style");

			String senna = getSenna();

			if (Objects.equals(senna, "off")) {
				_write(jspWriter, "data-senna-off", "true");
			}
			else if (Validator.isNotNull(senna)) {
				_write(jspWriter, "data-senna-track", "senna");
			}

			jspWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					getRequest()));

			String type = getType();

			if (Validator.isNotNull(type)) {
				_write(jspWriter, "type", type);
			}

			jspWriter.write(">");

			StringBundler sb = getBodyContentAsStringBundler();

			jspWriter.write(sb.toString());

			jspWriter.write("</style>");

			return EVAL_PAGE;
		}
		catch (IOException ioException) {
			throw new JspException(ioException);
		}
	}

	private void _write(JspWriter jspWriter, String name, boolean value)
		throws IOException {

		if (value) {
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(name);
		}
	}

	private void _write(JspWriter jspWriter, String name, String value)
		throws IOException {

		if (Validator.isNotNull(value)) {
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(name);
			jspWriter.write("=\"");
			jspWriter.write(value);
			jspWriter.write(StringPool.QUOTE);
		}
	}

}