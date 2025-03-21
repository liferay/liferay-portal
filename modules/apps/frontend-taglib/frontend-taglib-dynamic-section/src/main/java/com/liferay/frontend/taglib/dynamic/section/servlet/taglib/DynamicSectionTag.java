/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.dynamic.section.servlet.taglib;

import com.liferay.frontend.taglib.dynamic.section.DynamicSection;
import com.liferay.frontend.taglib.dynamic.section.DynamicSectionReplace;
import com.liferay.frontend.taglib.dynamic.section.internal.util.DynamicSectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.taglib.BaseBodyTagSupport;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.util.List;

/**
 * @author Matthew Tambara
 */
public class DynamicSectionTag extends BaseBodyTagSupport implements BodyTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter jspWriter = pageContext.getOut();

			if (_dynamicSectionReplace != null) {
				jspWriter.write(_dynamicSectionReplace.replace(pageContext));
			}
			else if (_dynamicSections != null) {
				ServletRequest servletRequest = pageContext.getRequest();

				String key = _PREFIX.concat(_name);

				StringBundler sb = getBodyContentAsStringBundler();

				StringBundler originalBodySB = new StringBundler(sb.index());

				originalBodySB.append(sb);

				servletRequest.setAttribute(key, originalBodySB);

				for (DynamicSection dynamicSection : _dynamicSections) {
					sb = dynamicSection.modify(sb, pageContext);
				}

				servletRequest.removeAttribute(key);

				sb.writeTo(jspWriter);
			}

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			_dynamicSectionReplace = null;
			_dynamicSections = null;
			_name = null;
		}
	}

	@Override
	public int doStartTag() {
		_dynamicSectionReplace = DynamicSectionUtil.getReplace(_name);

		if (_dynamicSectionReplace != null) {
			return SKIP_BODY;
		}

		List<DynamicSection> dynamicSections = DynamicSectionUtil.getServices(
			_name);

		if ((dynamicSections != null) && !dynamicSections.isEmpty()) {
			_dynamicSections = dynamicSections;

			return EVAL_BODY_BUFFERED;
		}

		return EVAL_BODY_INCLUDE;
	}

	public void setName(String name) {
		_name = name;
	}

	private static final String _PREFIX =
		DynamicSectionTag.class.getName() + "#";

	private DynamicSectionReplace _dynamicSectionReplace;
	private List<DynamicSection> _dynamicSections;
	private String _name;

}