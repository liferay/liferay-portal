/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseNavBarSearchTag;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.NavigationBarTag}
 */
@Deprecated
public class NavBarSearchTag extends BaseNavBarSearchTag {

	@Override
	public int doStartTag() throws JspException {
		NavBarTag navBarTag = (NavBarTag)findAncestorWithClass(
			this, NavBarTag.class);

		if (navBarTag != null) {
			StringBundler sb = navBarTag.getResponsiveButtonsSB();

			sb.append("<a class=\"btn navbar-btn navbar-toggle");

			if (_hasSearchResults()) {
				sb.append(" hide");
			}

			sb.append("\" id=\"");
			sb.append(_getNamespacedId());
			sb.append("NavbarBtn\" data-navId=\"");
			sb.append(_getNamespacedId());
			sb.append("\" tabindex=\"0\">");
			sb.append("<i class=\"icon-search\"></i></a>");
		}

		return super.doStartTag();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_namespacedId = null;
	}

	@Override
	protected String getEndPage() {
		return "/html/taglib/aui/nav_bar_search/end.jsp";
	}

	protected String getMarkupView() {
		return null;
	}

	@Override
	protected String getStartPage() {
		return "/html/taglib/aui/nav_bar_search/start.jsp";
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div></div>");

		return EVAL_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		setNamespacedAttribute(httpServletRequest, "id", _getNamespacedId());
		setNamespacedAttribute(
			httpServletRequest, "searchResults", _hasSearchResults());
	}

	private String _getNamespacedId() {
		if (Validator.isNotNull(_namespacedId)) {
			return _namespacedId;
		}

		_namespacedId = getId();

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		if (Validator.isNull(_namespacedId)) {
			_namespacedId = PortalUtil.getUniqueElementId(
				httpServletRequest, StringPool.BLANK,
				AUIUtil.normalizeId("navBar"));
		}

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse != null) {
			_namespacedId = portletResponse.getNamespace() + _namespacedId;
		}

		return _namespacedId;
	}

	private boolean _hasSearchResults() {
		SearchContainer<?> searchContainer = getSearchContainer();

		if (searchContainer == null) {
			return false;
		}

		DisplayTerms displayTerms = searchContainer.getDisplayTerms();

		String keywords = displayTerms.getKeywords();

		if (displayTerms.isAdvancedSearch() ||
			!keywords.equals(StringPool.BLANK)) {

			return true;
		}

		return false;
	}

	private String _namespacedId;

}