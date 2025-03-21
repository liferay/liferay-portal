/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class CheckboxTag extends BaseCssTag {

	public long getDeletions() {
		return _deletions;
	}

	public String getDescription() {
		return _description;
	}

	public String getId() {
		return _id;
	}

	public long getItems() {
		return _items;
	}

	public String getLabel() {
		return _label;
	}

	public String getName() {
		return _name;
	}

	public String getPopover() {
		return _popover;
	}

	public String getSuggestion() {
		return _suggestion;
	}

	@Override
	public String getTagNameForCssPath() {
		return "checkbox";
	}

	public String getWarning() {
		return _warning;
	}

	public boolean isChecked() {
		return _checked;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public void setChecked(boolean checked) {
		_checked = checked;
	}

	public void setDeletions(long deletions) {
		_deletions = deletions;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setItems(long items) {
		_items = items;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPopover(String popover) {
		_popover = popover;
	}

	public void setSuggestion(String suggestion) {
		_suggestion = suggestion;
	}

	public void setWarning(String warning) {
		_warning = warning;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_checked = false;
		_deletions = 0;
		_description = StringPool.BLANK;
		_disabled = false;
		_id = StringPool.BLANK;
		_items = 0;
		_label = StringPool.BLANK;
		_name = StringPool.BLANK;
		_popover = StringPool.BLANK;
		_suggestion = StringPool.BLANK;
		_warning = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:checked", _checked);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:deletions", _deletions);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:description", _description);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:disabled", _disabled);
		httpServletRequest.setAttribute("liferay-staging:checkbox:id", _id);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:items", _items);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:label", _label);
		httpServletRequest.setAttribute("liferay-staging:checkbox:name", _name);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:popover", _popover);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:suggestion", _suggestion);
		httpServletRequest.setAttribute(
			"liferay-staging:checkbox:warning", _warning);
	}

	private static final String _PAGE = "/checkbox/aui/page.jsp";

	private boolean _checked;
	private long _deletions;
	private String _description = StringPool.BLANK;
	private boolean _disabled;
	private String _id = StringPool.BLANK;
	private long _items;
	private String _label = StringPool.BLANK;
	private String _name = StringPool.BLANK;
	private String _popover = StringPool.BLANK;
	private String _suggestion = StringPool.BLANK;
	private String _warning = StringPool.BLANK;

}