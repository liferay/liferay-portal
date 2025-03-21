/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.util.DropdownItemListUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;

import jakarta.servlet.jsp.JspException;

import java.util.List;
import java.util.Map;

/**
 * @author Chema Balsas
 */
public class DropdownMenuTag extends ButtonTag {

	@Override
	public int doEndTag() throws JspException {
		if (_empty) {
			return EVAL_PAGE;
		}

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		_empty = DropdownItemListUtil.isEmpty(_dropdownItems);

		if (_empty) {
			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public List<DropdownItem> getDropdownItems() {
		return _dropdownItems;
	}

	public Map<String, String> getMenuProps() {
		return _menuProps;
	}

	public void setDropdownItems(List<DropdownItem> dropdownItems) {
		_dropdownItems = dropdownItems;
	}

	public void setMenuProps(Map<String, String> menuProps) {
		_menuProps = menuProps;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_buttonType = null;
		_dropdownItems = null;
		_empty = null;
		_menuProps = null;
	}

	@Override
	protected String getHydratedModuleName() {
		if (DropdownItemListUtil.isEmpty(_dropdownItems)) {
			return null;
		}

		return "{DropdownMenu} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("items", _dropdownItems);
		props.put("menuProps", _menuProps);

		return super.prepareProps(props);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:dropdown-menu:";

	private String _buttonType;
	private List<DropdownItem> _dropdownItems;
	private Boolean _empty;
	private Map<String, String> _menuProps;

}