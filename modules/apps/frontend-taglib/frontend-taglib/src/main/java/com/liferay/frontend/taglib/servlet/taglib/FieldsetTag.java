/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class FieldsetTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public boolean isCollapsed() {
		return _collapsed;
	}

	public boolean isCollapsible() {
		return _collapsible;
	}

	public boolean isColumn() {
		return _column;
	}

	public boolean isDeprecated() {
		return _deprecated;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isLocalizeLabel() {
		return _localizeLabel;
	}

	public void setCollapsed(boolean collapsed) {
		_collapsed = collapsed;
	}

	public void setCollapsible(boolean collapsible) {
		_collapsible = collapsible;
	}

	public void setColumn(boolean column) {
		_column = column;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDeprecated(boolean deprecated) {
		_deprecated = deprecated;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_collapsed = false;
		_collapsible = false;
		_column = false;
		_cssClass = null;
		_deprecated = false;
		_disabled = false;
		_helpMessage = null;
		_id = null;
		_label = null;
		_localizeLabel = true;
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
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(_id)) {
			String id = StringPool.BLANK;

			if (Validator.isNotNull(_label)) {
				id = PortalUtil.getUniqueElementId(
					httpServletRequest, _getNamespace(),
					AUIUtil.normalizeId(_label));
			}
			else {
				id = StringUtil.randomId();
			}

			setId(_getNamespace() + id);
		}

		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:collapsed", String.valueOf(_collapsed));
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:collapsible",
			String.valueOf(_collapsible));
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:column", String.valueOf(_column));
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:deprecated",
			String.valueOf(_deprecated));
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:disabled", String.valueOf(_disabled));
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:helpMessage", _helpMessage);
		httpServletRequest.setAttribute("liferay-frontend:fieldset:id", _id);
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:label", _label);
		httpServletRequest.setAttribute(
			"liferay-frontend:fieldset:localizeLabel",
			String.valueOf(_localizeLabel));
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

	private static final String _ATTRIBUTE_NAMESPACE =
		"liferay-frontend:fieldset:";

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _END_PAGE = "/fieldset/end.jsp";

	private static final String _START_PAGE = "/fieldset/start.jsp";

	private boolean _collapsed;
	private boolean _collapsible;
	private boolean _column;
	private String _cssClass;
	private boolean _deprecated;
	private boolean _disabled;
	private String _helpMessage;
	private String _id;
	private String _label;
	private boolean _localizeLabel = true;

}