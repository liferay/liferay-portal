/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.TabsPanelTag}
 */
@Deprecated
public class SectionTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			_tabsTag = (TabsTag)findAncestorWithClass(this, TabsTag.class);

			if (_tabsTag == null) {
				throw new JspException();
			}

			HttpServletRequest httpServletRequest =
				(HttpServletRequest)pageContext.getRequest();

			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			String namespace = StringPool.BLANK;

			if (portletResponse != null) {
				namespace = portletResponse.getNamespace();
			}

			String sectionParam = _tabsTag.getParam();
			String sectionName = _tabsTag.getSectionName();
			_sectionSelected = Boolean.valueOf(_tabsTag.getSectionSelected());
			String sectionScroll = namespace + sectionParam + "TabsScroll";

			String sectionRedirectParams = StringBundler.concat(
				"&scroll=", sectionScroll, "&", sectionParam, "=", sectionName);

			_tabsTag.incrementSection();

			httpServletRequest.setAttribute("liferay-ui:section:data", _data);
			httpServletRequest.setAttribute(
				"liferay-ui:section:name", sectionName);
			httpServletRequest.setAttribute(
				"liferay-ui:section:param", sectionParam);
			httpServletRequest.setAttribute(
				"liferay-ui:section:scroll", sectionScroll);
			httpServletRequest.setAttribute(
				"liferay-ui:section:selected", _sectionSelected);

			pageContext.setAttribute("sectionName", sectionName);
			pageContext.setAttribute("sectionParam", sectionParam);
			pageContext.setAttribute(
				"sectionRedirectParams", sectionRedirectParams);
			pageContext.setAttribute("sectionScroll", sectionScroll);
			pageContext.setAttribute("sectionSelected", _sectionSelected);

			include(getStartPage(), true);

			if (!_tabsTag.isRefresh() || _sectionSelected.booleanValue()) {
				return EVAL_BODY_INCLUDE;
			}

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_data = null;
		_sectionSelected = Boolean.FALSE;
		_tabsTag = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

	private static final String _END_PAGE = "/html/taglib/ui/section/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/ui/section/start.jsp";

	private Map<String, Object> _data;
	private Boolean _sectionSelected = Boolean.FALSE;
	private TabsTag _tabsTag;

}