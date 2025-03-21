/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.base.BaseNavTag;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.NavigationBarTag}
 */
@Deprecated
public class NavTag extends BaseNavTag implements BodyTag {

	@Override
	public int doStartTag() throws JspException {
		NavBarTag navBarTag = (NavBarTag)findAncestorWithClass(
			this, NavBarTag.class);

		if ((navBarTag != null) &&
			(!_calledCollapsibleSetter || getCollapsible())) {

			setCollapsible(true);

			navBarTag.setDataTarget(_getNamespacedId());

			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			StringBundler sb = navBarTag.getResponsiveButtonsSB();

			sb.append("<a class=\"navbar-toggler navbar-toggler-link");

			String cssClass = getCssClass();

			if (Validator.isNotNull(cssClass)) {
				String[] cssClassParts = StringUtil.split(
					cssClass, CharPool.SPACE);

				for (String cssClassPart : cssClassParts) {
					sb.append(StringPool.SPACE);
					sb.append(cssClassPart);
					sb.append("-btn");
				}
			}

			if (_hasSearchResults()) {
				sb.append(" hide");
			}

			sb.append("\" id=\"");
			sb.append(_getNamespacedId());
			sb.append("NavbarBtn\" data-navId=\"");
			sb.append(_getNamespacedId());
			sb.append("\" tabindex=\"0\">");

			String icon = getIcon();

			if (Validator.isNull(icon)) {
				sb.append("<span class=\"navbar-toggler-icon\"></span>");
			}
			else if (icon.equals("user") && themeDisplay.isSignedIn()) {
				try {
					sb.append("<img alt=\"");

					sb.append(
						LanguageUtil.get(
							TagResourceBundleUtil.getResourceBundle(
								pageContext),
							"my-account"));

					sb.append("\" class=\"user-avatar-image\" src=\"");

					User user = themeDisplay.getUser();

					sb.append(user.getPortraitURL(themeDisplay));

					sb.append("\">");
				}
				catch (Exception exception) {
					throw new JspException(exception);
				}
			}
			else {
				sb.append("<i class=\"icon-");
				sb.append(icon);
				sb.append("\"></i>");
			}

			sb.append("</a>");
		}

		return super.doStartTag();
	}

	@Override
	public void setCollapsible(boolean collapsible) {
		super.setCollapsible(collapsible);

		_calledCollapsibleSetter = true;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_calledCollapsibleSetter = false;
		_namespacedId = null;
	}

	protected String getMarkupView() {
		return null;
	}

	@Override
	protected String getPage() {
		return "/html/taglib/aui/nav/page.jsp";
	}

	@Override
	protected int processStartTag() throws Exception {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		setNamespacedAttribute(httpServletRequest, "id", _getNamespacedId());
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
				AUIUtil.normalizeId("navTag"));
		}

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if ((portletResponse != null) && getUseNamespace()) {
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

	private boolean _calledCollapsibleSetter;
	private String _namespacedId;

}