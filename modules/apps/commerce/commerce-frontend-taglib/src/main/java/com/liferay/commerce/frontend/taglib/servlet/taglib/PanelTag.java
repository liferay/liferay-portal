/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Fabio Diego Mastrorilli
 */
public class PanelTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		HttpServletRequest httpServletRequest = getRequest();

		if (Validator.isNull(_spritemap)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_spritemap = themeDisplay.getPathThemeSpritemap();
		}

		String randomNamespace =
			PortalUtil.generateRandomKey(httpServletRequest, "commerce_panel") +
				StringPool.UNDERLINE;

		httpServletRequest.setAttribute(
			"liferay-commerce:panel:actionIcon", _actionIcon);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:actionLabel", _actionLabel);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:actionTargetId", _actionTargetId);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:actionUrl", _actionUrl);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:bodyClasses", _bodyClasses);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:collapsed", _collapsed);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:collapseLabel", _collapseLabel);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:collapseSwitchName", _collapseSwitchName);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:collapsible", _collapsible);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:elementClasses", _elementClasses);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:randomNamespace", randomNamespace);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:showMoreId", _showMoreId);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:showMoreUrl", _showMoreUrl);
		httpServletRequest.setAttribute(
			"liferay-commerce:panel:spritemap", _spritemap);
		httpServletRequest.setAttribute("liferay-commerce:panel:title", _title);

		super.doStartTag();

		return EVAL_BODY_INCLUDE;
	}

	public String getActionIcon() {
		return _actionIcon;
	}

	public String getActionLabel() {
		return _actionLabel;
	}

	public String getActionTargetId() {
		return _actionTargetId;
	}

	public String getActionUrl() {
		return _actionUrl;
	}

	public String getBodyClasses() {
		return _bodyClasses;
	}

	public boolean getCollapsed() {
		return _collapsed;
	}

	public String getCollapseLabel() {
		return _collapseLabel;
	}

	public String getCollapseSwitchName() {
		return _collapseSwitchName;
	}

	public boolean getCollapsible() {
		return _collapsible;
	}

	public String getElementClasses() {
		return _elementClasses;
	}

	public String getShowMoreId() {
		return _showMoreId;
	}

	public String getShowMoreUrl() {
		return _showMoreUrl;
	}

	public String getSpritemap() {
		return _spritemap;
	}

	public String getTitle() {
		return _title;
	}

	public void setActionIcon(String actionIcon) {
		_actionIcon = actionIcon;
	}

	public void setActionLabel(String actionLabel) {
		_actionLabel = actionLabel;
	}

	public void setActionTargetId(String actionTargetId) {
		_actionTargetId = actionTargetId;
	}

	public void setActionUrl(String actionUrl) {
		_actionUrl = actionUrl;
	}

	public void setBodyClasses(String bodyClasses) {
		_bodyClasses = bodyClasses;
	}

	public void setCollapsed(boolean collapsed) {
		_collapsed = collapsed;
	}

	public void setCollapseLabel(String collapseLabel) {
		_collapseLabel = collapseLabel;
	}

	public void setCollapseSwitchName(String collapseSwitchName) {
		_collapseSwitchName = collapseSwitchName;
	}

	public void setCollapsible(boolean collapsible) {
		_collapsible = collapsible;
	}

	public void setElementClasses(String elementClasses) {
		_elementClasses = elementClasses;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowMoreId(String showMoreId) {
		_showMoreId = showMoreId;
	}

	public void setShowMoreUrl(String showMoreUrl) {
		_showMoreUrl = showMoreUrl;
	}

	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_actionIcon = null;
		_actionLabel = null;
		_actionTargetId = null;
		_actionUrl = null;
		_bodyClasses = null;
		_collapsed = false;
		_collapseLabel = null;
		_collapseSwitchName = null;
		_collapsible = false;
		_elementClasses = null;
		_showMoreId = null;
		_showMoreUrl = null;
		_spritemap = null;
		_title = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	private static final String _ATTRIBUTE_NAMESPACE =
		"liferay-commerce:panel:";

	private static final String _END_PAGE = "/panel/end.jsp";

	private static final String _START_PAGE = "/panel/start.jsp";

	private String _actionIcon;
	private String _actionLabel;
	private String _actionTargetId;
	private String _actionUrl;
	private String _bodyClasses;
	private boolean _collapsed;
	private String _collapseLabel;
	private String _collapseSwitchName;
	private boolean _collapsible;
	private String _elementClasses;
	private String _showMoreId;
	private String _showMoreUrl;
	private String _spritemap;
	private String _title;

}