/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseNavItemTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getAnchorCssClass() {
		return _anchorCssClass;
	}

	public java.lang.Object getAnchorData() {
		return _anchorData;
	}

	public java.lang.String getAnchorId() {
		return _anchorId;
	}

	public java.lang.String getAriaLabel() {
		return _ariaLabel;
	}

	public java.lang.String getAriaRole() {
		return _ariaRole;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.lang.Object getData() {
		return _data;
	}

	public boolean getDropdown() {
		return _dropdown;
	}

	public java.lang.Object getHref() {
		return _href;
	}

	public java.lang.String getIconCssClass() {
		return _iconCssClass;
	}

	public java.lang.String getIconSrc() {
		return _iconSrc;
	}

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getLabel() {
		return _label;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public boolean getSelected() {
		return _selected;
	}

	public java.lang.String getState() {
		return _state;
	}

	public java.lang.String getTarget() {
		return _target;
	}

	public java.lang.String getTitle() {
		return _title;
	}

	public boolean getToggle() {
		return _toggle;
	}

	public boolean getToggleTouch() {
		return _toggleTouch;
	}

	public boolean getUseDialog() {
		return _useDialog;
	}

	public boolean getWrapDropDownMenu() {
		return _wrapDropDownMenu;
	}

	public void setAnchorCssClass(java.lang.String anchorCssClass) {
		_anchorCssClass = anchorCssClass;
	}

	public void setAnchorData(java.lang.Object anchorData) {
		_anchorData = anchorData;
	}

	public void setAnchorId(java.lang.String anchorId) {
		_anchorId = anchorId;
	}

	public void setAriaLabel(java.lang.String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setAriaRole(java.lang.String ariaRole) {
		_ariaRole = ariaRole;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.lang.Object data) {
		_data = data;
	}

	public void setDropdown(boolean dropdown) {
		_dropdown = dropdown;
	}

	public void setHref(java.lang.Object href) {
		_href = href;
	}

	public void setIconCssClass(java.lang.String iconCssClass) {
		_iconCssClass = iconCssClass;
	}

	public void setIconSrc(java.lang.String iconSrc) {
		_iconSrc = iconSrc;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setLabel(java.lang.String label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	public void setState(java.lang.String state) {
		_state = state;
	}

	public void setTarget(java.lang.String target) {
		_target = target;
	}

	public void setTitle(java.lang.String title) {
		_title = title;
	}

	public void setToggle(boolean toggle) {
		_toggle = toggle;
	}

	public void setToggleTouch(boolean toggleTouch) {
		_toggleTouch = toggleTouch;
	}

	public void setUseDialog(boolean useDialog) {
		_useDialog = useDialog;
	}

	public void setWrapDropDownMenu(boolean wrapDropDownMenu) {
		_wrapDropDownMenu = wrapDropDownMenu;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_anchorCssClass = null;
		_anchorData = null;
		_anchorId = null;
		_ariaLabel = null;
		_ariaRole = null;
		_cssClass = null;
		_data = null;
		_dropdown = false;
		_href = "javascript:void(0);";
		_iconCssClass = null;
		_iconSrc = null;
		_id = null;
		_label = null;
		_localizeLabel = true;
		_selected = false;
		_state = null;
		_target = null;
		_title = null;
		_toggle = false;
		_toggleTouch = true;
		_useDialog = false;
		_wrapDropDownMenu = true;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "anchorCssClass", _anchorCssClass);
		setNamespacedAttribute(request, "anchorData", _anchorData);
		setNamespacedAttribute(request, "anchorId", _anchorId);
		setNamespacedAttribute(request, "ariaLabel", _ariaLabel);
		setNamespacedAttribute(request, "ariaRole", _ariaRole);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "dropdown", _dropdown);
		setNamespacedAttribute(request, "href", _href);
		setNamespacedAttribute(request, "iconCssClass", _iconCssClass);
		setNamespacedAttribute(request, "iconSrc", _iconSrc);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(request, "selected", _selected);
		setNamespacedAttribute(request, "state", _state);
		setNamespacedAttribute(request, "target", _target);
		setNamespacedAttribute(request, "title", _title);
		setNamespacedAttribute(request, "toggle", _toggle);
		setNamespacedAttribute(request, "toggleTouch", _toggleTouch);
		setNamespacedAttribute(request, "useDialog", _useDialog);
		setNamespacedAttribute(request, "wrapDropDownMenu", _wrapDropDownMenu);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:nav-item:";

	private static final String _END_PAGE =
		"/html/taglib/aui/nav_item/end.jsp";

	private java.lang.String _anchorCssClass = null;
	private java.lang.Object _anchorData = null;
	private java.lang.String _anchorId = null;
	private java.lang.String _ariaLabel = null;
	private java.lang.String _ariaRole = null;
	private java.lang.String _cssClass = null;
	private java.lang.Object _data = null;
	private boolean _dropdown = false;
	private java.lang.Object _href = "javascript:void(0);";
	private java.lang.String _iconCssClass = null;
	private java.lang.String _iconSrc = null;
	private java.lang.String _id = null;
	private java.lang.String _label = null;
	private boolean _localizeLabel = true;
	private boolean _selected = false;
	private java.lang.String _state = null;
	private java.lang.String _target = null;
	private java.lang.String _title = null;
	private boolean _toggle = false;
	private boolean _toggleTouch = true;
	private boolean _useDialog = false;
	private boolean _wrapDropDownMenu = true;

}