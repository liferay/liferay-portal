/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.BaseBodyTagSupport;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.PanelTag}
 */
@Deprecated
public class PanelTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		if (Validator.isNull(_id)) {
			_id = StringUtil.randomId();
		}

		if (Validator.isNull(_parentId)) {
			BaseBodyTagSupport baseBodyTagSupport =
				(BaseBodyTagSupport)findAncestorWithClass(
					this, BaseBodyTagSupport.class);

			if (baseBodyTagSupport instanceof PanelContainerTag) {
				PanelContainerTag panelContainerTag =
					(PanelContainerTag)baseBodyTagSupport;

				_accordion = panelContainerTag.isAccordion();
				_parentId = panelContainerTag.getId();
			}
		}

		httpServletRequest.setAttribute(
			"liferay-ui:panel:accordion", String.valueOf(_accordion));
		httpServletRequest.setAttribute(
			"liferay-ui:panel:collapsible", String.valueOf(_collapsible));
		httpServletRequest.setAttribute("liferay-ui:panel:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:panel:defaultState", _defaultState);
		httpServletRequest.setAttribute("liferay-ui:panel:extended", _extended);
		httpServletRequest.setAttribute(
			"liferay-ui:panel:helpMessage", _helpMessage);
		httpServletRequest.setAttribute(
			"liferay-ui:panel:iconCssClass", _iconCssClass);
		httpServletRequest.setAttribute("liferay-ui:panel:id", _id);
		httpServletRequest.setAttribute("liferay-ui:panel:parentId", _parentId);
		httpServletRequest.setAttribute(
			"liferay-ui:panel:persistState", String.valueOf(_persistState));
		httpServletRequest.setAttribute("liferay-ui:panel:state", _state);
		httpServletRequest.setAttribute("liferay-ui:panel:title", _title);

		super.doStartTag();

		return EVAL_BODY_INCLUDE;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getDefaultState() {
		return _defaultState;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getIconCssClass() {
		return _iconCssClass;
	}

	public String getId() {
		return _id;
	}

	public String getMarkupView() {
		return null;
	}

	public String getParentId() {
		return _parentId;
	}

	public String getState() {
		return _state;
	}

	public String getTitle() {
		return _title;
	}

	public boolean isCollapsible() {
		return _collapsible;
	}

	public Boolean isExtended() {
		return _extended;
	}

	public boolean isPersistState() {
		return _persistState;
	}

	public void setCollapsible(boolean collapsible) {
		_collapsible = collapsible;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDefaultState(String defaultState) {
		_defaultState = defaultState;
	}

	public void setEndPage(String endPage) {
		_endPage = endPage;
	}

	public void setExtended(Boolean extended) {
		_extended = extended;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setIconCssClass(String iconCssClass) {
		_iconCssClass = iconCssClass;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMarkupView(String markupView) {
	}

	public void setParentId(String parentId) {
		_parentId = parentId;
	}

	public void setPersistState(boolean persistState) {
		_persistState = persistState;
	}

	public void setStartPage(String startPage) {
		_startPage = startPage;
	}

	public void setState(String state) {
		_state = state;
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_accordion = false;
		_collapsible = true;
		_cssClass = null;
		_defaultState = "open";
		_endPage = null;
		_extended = null;
		_helpMessage = null;
		_iconCssClass = null;
		_id = null;
		_parentId = StringPool.BLANK;
		_persistState = true;
		_startPage = null;
		_state = null;
		_title = null;
	}

	@Override
	protected String getEndPage() {
		if (Validator.isNull(_endPage)) {
			return "/html/taglib/ui/panel/end.jsp";
		}

		return _endPage;
	}

	@Override
	protected String getStartPage() {
		if (Validator.isNull(_startPage)) {
			return "/html/taglib/ui/panel/start.jsp";
		}

		return _startPage;
	}

	private boolean _accordion;
	private boolean _collapsible = true;
	private String _cssClass;
	private String _defaultState = "open";
	private String _endPage;
	private Boolean _extended;
	private String _helpMessage;
	private String _iconCssClass;
	private String _id;
	private String _parentId = StringPool.BLANK;
	private boolean _persistState = true;
	private String _startPage;
	private String _state;
	private String _title;

}