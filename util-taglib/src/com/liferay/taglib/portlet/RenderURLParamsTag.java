/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.taglib.servlet.PipingPageContext;

import jakarta.portlet.PortletURL;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class RenderURLParamsTag extends TagSupport {

	public static String doTag(PortletURL portletURL, PageContext pageContext)
		throws Exception {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		_doTag(
			portletURL, null,
			new PipingPageContext(pageContext, unsyncStringWriter));

		return unsyncStringWriter.toString();
	}

	public static String doTag(String varImpl, PageContext pageContext)
		throws Exception {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		_doTag(
			null, varImpl,
			new PipingPageContext(pageContext, unsyncStringWriter));

		return unsyncStringWriter.toString();
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			_doTag(_portletURL, _varImpl, pageContext);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setVarImpl(String varImpl) {
		_varImpl = varImpl;
	}

	private static void _doTag(
			PortletURL portletURL, String varImpl, PageContext pageContext)
		throws Exception {

		if (portletURL == null) {
			portletURL = (PortletURL)pageContext.getAttribute(varImpl);
		}

		if (portletURL != null) {
			_writeParamsString(portletURL, pageContext);
		}
	}

	private static void _writeParamsString(
			PortletURL portletURL, PageContext pageContext)
		throws Exception {

		String url = portletURL.toString();

		JspWriter jspWriter = pageContext.getOut();

		String[] parameters = StringUtil.split(
			HttpComponentsUtil.getQueryString(url), CharPool.AMPERSAND);

		for (String parameter : parameters) {
			if (parameter.length() > 0) {
				String[] kvp = StringUtil.split(parameter, CharPool.EQUAL);

				if (ArrayUtil.isNotEmpty(kvp)) {
					String key = kvp[0];

					String value = StringPool.BLANK;

					if (kvp.length > 1) {
						value = kvp[1];
					}

					value = HttpComponentsUtil.decodeURL(value);

					jspWriter.write("<input name=\"");
					jspWriter.write(key);
					jspWriter.write("\" type=\"hidden\" value=\"");
					jspWriter.write(HtmlUtil.escapeAttribute(value));
					jspWriter.write("\" />");
				}
			}
		}
	}

	private PortletURL _portletURL;
	private String _varImpl;

}