/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseFieldsetTag;
import com.liferay.taglib.ui.IconHelpTag;
import com.liferay.taglib.ui.MessageTag;
import com.liferay.taglib.util.InlineUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class FieldsetTag extends BaseFieldsetTag {

	@Override
	protected String getEndPage() {
		return "/html/taglib/aui/fieldset/end.jsp";
	}

	@Override
	protected String getStartPage() {
		return "/html/taglib/aui/fieldset/start.jsp";
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div></fieldset>");

		return EVAL_PAGE;
	}

	@Override
	protected int processStartTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<fieldset class=\"fieldset ");
		jspWriter.write(GetterUtil.getString(getCssClass()));
		jspWriter.write("\" ");

		String id = getId();

		if (id != null) {
			jspWriter.write("id=\"");
			jspWriter.write(id);
			jspWriter.write("\" ");
		}

		jspWriter.write(
			InlineUtil.buildDynamicAttributes(getDynamicAttributes()));
		jspWriter.write(
			"><legend class=\"fieldset-legend\"><span class=\"legend\">");

		String label = getLabel();

		if (label != null) {
			MessageTag messageTag = new MessageTag();

			messageTag.setKey(label);
			messageTag.setLocalizeKey(getLocalizeLabel());

			messageTag.doTag(pageContext);

			String helpMessage = getHelpMessage();

			if (helpMessage != null) {
				IconHelpTag iconHelpTag = new IconHelpTag();

				iconHelpTag.setMessage(helpMessage);

				iconHelpTag.doTag(pageContext);
			}
		}

		jspWriter.write("</span></legend>");

		if (getColumn()) {
			jspWriter.write("<div class=\"row\">");
		}
		else {
			jspWriter.write("<div class=\"\">");
		}

		return EVAL_BODY_INCLUDE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(getId()) && Validator.isNotNull(getLabel()) &&
			getCollapsible()) {

			String id = PortalUtil.getUniqueElementId(
				httpServletRequest, _getNamespace(),
				AUIUtil.normalizeId(getLabel()));

			setId(_getNamespace() + id);
		}

		super.setAttributes(httpServletRequest);
	}

	private String _getNamespace() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse != null) {
			return portletResponse.getNamespace();
		}

		return StringPool.BLANK;
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

}