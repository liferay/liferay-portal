/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchEntry;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class ResultRow
	implements com.liferay.portal.kernel.dao.search.ResultRow {

	public ResultRow(Object object, long primaryKey, int pos) {
		this(object, String.valueOf(primaryKey), pos);
	}

	public ResultRow(Object object, long primaryKey, int pos, boolean bold) {
		this(object, String.valueOf(primaryKey), pos, bold);
	}

	public ResultRow(Object object, String primaryKey, int pos) {
		this(object, primaryKey, pos, false);
	}

	public ResultRow(Object object, String primaryKey, int pos, boolean bold) {
		this(String.valueOf(pos + 1), object, primaryKey, pos, bold);
	}

	public ResultRow(
		String rowId, Object object, String primaryKey, int pos, boolean bold) {

		this(
			rowId, object, primaryKey, pos, bold, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK);
	}

	public ResultRow(
		String rowId, Object object, String primaryKey, int pos, boolean bold,
		String ariaLabel, String cssClass, String state, String tabIndex) {

		_rowId = rowId;
		_object = object;
		_primaryKey = primaryKey;
		_pos = pos;
		_bold = bold;
		_ariaLabel = ariaLabel;
		_cssClass = cssClass;
		_state = state;
		_tabIndex = tabIndex;

		_searchEntries = new ArrayList<>();
	}

	@Override
	public void addButton(int index, String name, String href) {
		addButton(
			index, SearchEntry.DEFAULT_ALIGN, SearchEntry.DEFAULT_VALIGN,
			SearchEntry.DEFAULT_COLSPAN, name, href);
	}

	@Override
	public void addButton(
		int index, String align, String valign, int colspan, String name,
		String href) {

		if (_restricted) {
			href = null;
		}

		ButtonSearchEntry buttonSearchEntry = new ButtonSearchEntry();

		buttonSearchEntry.setAlign(align);
		buttonSearchEntry.setColspan(colspan);
		buttonSearchEntry.setHref(href);
		buttonSearchEntry.setName(name);
		buttonSearchEntry.setValign(valign);

		_searchEntries.add(index, buttonSearchEntry);
	}

	@Override
	public void addButton(String name, String href) {
		addButton(_searchEntries.size(), name, href);
	}

	@Override
	public void addButton(
		String align, String valign, int colspan, String name, String href) {

		addButton(_searchEntries.size(), align, valign, colspan, name, href);
	}

	@Override
	public void addButton(
		String align, String valign, String name, String href) {

		addButton(
			_searchEntries.size(), align, valign, SearchEntry.DEFAULT_COLSPAN,
			name, href);
	}

	@Override
	public void addDate(Date date) {
		addDate(_searchEntries.size(), date, null);
	}

	@Override
	public void addDate(Date date, PortletURL portletURL) {
		if (portletURL != null) {
			addDate(_searchEntries.size(), date, portletURL.toString());
		}
		else {
			addDate(_searchEntries.size(), date, null);
		}
	}

	@Override
	public void addDate(Date date, String href) {
		addDate(_searchEntries.size(), date, null);
	}

	@Override
	public void addDate(int index, Date date, String href) {
		DateSearchEntry dateSearchEntry = new DateSearchEntry();

		dateSearchEntry.setAlign(SearchEntry.DEFAULT_ALIGN);
		dateSearchEntry.setColspan(SearchEntry.DEFAULT_COLSPAN);
		dateSearchEntry.setDate(date);
		dateSearchEntry.setHref(href);
		dateSearchEntry.setValign(SearchEntry.DEFAULT_VALIGN);

		_searchEntries.add(index, dateSearchEntry);
	}

	@Override
	public void addJSP(
		int index, String path, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		addJSP(
			index, SearchEntry.DEFAULT_ALIGN, SearchEntry.DEFAULT_VALIGN,
			SearchEntry.DEFAULT_COLSPAN, path, servletContext,
			httpServletRequest, httpServletResponse);
	}

	@Override
	public void addJSP(
		int index, String align, String valign, int colspan, String path,
		ServletContext servletContext, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		JSPSearchEntry jspSearchEntry = new JSPSearchEntry();

		jspSearchEntry.setAlign(align);
		jspSearchEntry.setColspan(colspan);
		jspSearchEntry.setPath(path);
		jspSearchEntry.setRequest(httpServletRequest);
		jspSearchEntry.setResponse(httpServletResponse);
		jspSearchEntry.setServletContext(servletContext);
		jspSearchEntry.setValign(valign);

		_searchEntries.add(index, jspSearchEntry);
	}

	@Override
	public void addJSP(
		String path, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		addJSP(
			_searchEntries.size(), path, servletContext, httpServletRequest,
			httpServletResponse);
	}

	@Override
	public void addJSP(
		String align, String valign, int colspan, String path,
		ServletContext servletContext, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		addJSP(
			_searchEntries.size(), align, valign, colspan, path, servletContext,
			httpServletRequest, httpServletResponse);
	}

	@Override
	public void addJSP(
		String path, String cssClass, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		JSPSearchEntry jspSearchEntry = new JSPSearchEntry();

		jspSearchEntry.setAlign(SearchEntry.DEFAULT_ALIGN);
		jspSearchEntry.setColspan(SearchEntry.DEFAULT_COLSPAN);
		jspSearchEntry.setCssClass(cssClass);
		jspSearchEntry.setPath(path);
		jspSearchEntry.setRequest(httpServletRequest);
		jspSearchEntry.setResponse(httpServletResponse);
		jspSearchEntry.setServletContext(servletContext);
		jspSearchEntry.setValign(SearchEntry.DEFAULT_VALIGN);

		_searchEntries.add(_searchEntries.size(), jspSearchEntry);
	}

	@Override
	public void addJSP(
		String align, String valign, String path, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		addJSP(
			_searchEntries.size(), align, valign, SearchEntry.DEFAULT_COLSPAN,
			path, servletContext, httpServletRequest, httpServletResponse);
	}

	@Override
	public void addSearchEntry(int index, SearchEntry searchEntry) {
		_searchEntries.add(index, searchEntry);
	}

	@Override
	public void addSearchEntry(SearchEntry searchEntry) {
		_searchEntries.add(searchEntry);
	}

	@Override
	public void addStatus(int status) {
		addStatus(_searchEntries.size(), status, 0, null, null);
	}

	@Override
	public void addStatus(
		int index, int status, long statusByUserId, Date statusDate,
		String href) {

		StatusSearchEntry statusSearchEntry = new StatusSearchEntry();

		statusSearchEntry.setAlign(SearchEntry.DEFAULT_ALIGN);
		statusSearchEntry.setColspan(SearchEntry.DEFAULT_COLSPAN);
		statusSearchEntry.setHref(href);
		statusSearchEntry.setStatus(status);
		statusSearchEntry.setStatusByUserId(statusByUserId);
		statusSearchEntry.setStatusDate(statusDate);
		statusSearchEntry.setValign(SearchEntry.DEFAULT_VALIGN);

		_searchEntries.add(index, statusSearchEntry);
	}

	@Override
	public void addStatus(
		int index, int status, String href, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		StatusSearchEntry statusSearchEntry = new StatusSearchEntry();

		statusSearchEntry.setAlign(SearchEntry.DEFAULT_ALIGN);
		statusSearchEntry.setColspan(SearchEntry.DEFAULT_COLSPAN);
		statusSearchEntry.setHref(href);
		statusSearchEntry.setRequest(httpServletRequest);
		statusSearchEntry.setResponse(httpServletResponse);
		statusSearchEntry.setServletContext(servletContext);
		statusSearchEntry.setStatus(status);
		statusSearchEntry.setValign(SearchEntry.DEFAULT_VALIGN);

		_searchEntries.add(index, statusSearchEntry);
	}

	@Override
	public void addStatus(int status, long statusByUserId, Date statusDate) {
		addStatus(
			_searchEntries.size(), status, statusByUserId, statusDate, null);
	}

	@Override
	public void addStatus(
		int status, long statusByUserId, Date statusDate,
		PortletURL portletURL) {

		if (portletURL != null) {
			addStatus(
				_searchEntries.size(), status, statusByUserId, statusDate,
				portletURL.toString());
		}
		else {
			addStatus(
				_searchEntries.size(), status, statusByUserId, statusDate,
				null);
		}
	}

	@Override
	public void addStatus(
		int status, long statusByUserId, Date statusDate, String href) {

		addStatus(
			_searchEntries.size(), status, statusByUserId, statusDate, href);
	}

	@Override
	public void addStatus(int status, PortletURL portletURL) {
		if (portletURL != null) {
			addStatus(
				_searchEntries.size(), status, 0, null, portletURL.toString());
		}
		else {
			addStatus(_searchEntries.size(), status, 0, null, null);
		}
	}

	@Override
	public void addStatus(int status, String href) {
		addStatus(_searchEntries.size(), status, 0, null, href);
	}

	@Override
	public void addText(int index, String name) {
		addText(
			index, SearchEntry.DEFAULT_ALIGN, SearchEntry.DEFAULT_VALIGN,
			SearchEntry.DEFAULT_COLSPAN, name);
	}

	@Override
	public void addText(int index, String name, PortletURL portletURL) {
		if (portletURL == null) {
			addText(index, name);
		}
		else {
			addText(index, name, portletURL.toString());
		}
	}

	@Override
	public void addText(int index, String name, String href) {
		addText(
			index, SearchEntry.DEFAULT_ALIGN, SearchEntry.DEFAULT_VALIGN,
			SearchEntry.DEFAULT_COLSPAN, name, href);
	}

	@Override
	public void addText(
		int index, String align, String valign, int colspan, String name) {

		TextSearchEntry textSearchEntry = new TextSearchEntry();

		textSearchEntry.setAlign(align);
		textSearchEntry.setColspan(colspan);
		textSearchEntry.setName(name);
		textSearchEntry.setValign(valign);

		_searchEntries.add(index, textSearchEntry);
	}

	@Override
	public void addText(
		int index, String align, String valign, int colspan, String name,
		PortletURL portletURL) {

		if (portletURL == null) {
			addText(index, align, valign, colspan, name);
		}
		else {
			addText(index, align, valign, colspan, name, portletURL.toString());
		}
	}

	@Override
	public void addText(
		int index, String align, String valign, int colspan, String name,
		String href) {

		if (_restricted) {
			href = null;
		}

		TextSearchEntry textSearchEntry = new TextSearchEntry();

		textSearchEntry.setAlign(align);
		textSearchEntry.setColspan(colspan);
		textSearchEntry.setHref(href);
		textSearchEntry.setName(name);
		textSearchEntry.setValign(valign);

		_searchEntries.add(index, textSearchEntry);
	}

	public void addText(int index, TextSearchEntry searchEntry) {
		if (_restricted) {
			searchEntry.setHref(null);
		}

		_searchEntries.add(index, searchEntry);
	}

	@Override
	public void addText(String name) {
		addText(_searchEntries.size(), name);
	}

	@Override
	public void addText(String name, PortletURL portletURL) {
		if (portletURL == null) {
			addText(name);
		}
		else {
			addText(name, portletURL.toString());
		}
	}

	@Override
	public void addText(String name, String href) {
		addText(_searchEntries.size(), name, href);
	}

	@Override
	public void addText(String align, String valign, int colspan, String name) {
		addText(_searchEntries.size(), align, valign, colspan, name);
	}

	@Override
	public void addText(
		String align, String valign, int colspan, String name,
		PortletURL portletURL) {

		if (portletURL == null) {
			addText(align, valign, colspan, name);
		}
		else {
			addText(align, valign, colspan, name, portletURL.toString());
		}
	}

	@Override
	public void addText(
		String align, String valign, int colspan, String name, String href) {

		addText(_searchEntries.size(), align, valign, colspan, name, href);
	}

	@Override
	public void addText(String align, String valign, String name) {
		addText(
			_searchEntries.size(), align, valign, SearchEntry.DEFAULT_COLSPAN,
			name);
	}

	@Override
	public void addText(
		String align, String valign, String name, PortletURL portletURL) {

		addText(align, valign, SearchEntry.DEFAULT_COLSPAN, name, portletURL);
	}

	@Override
	public void addText(String align, String valign, String name, String href) {
		addText(
			_searchEntries.size(), align, valign, SearchEntry.DEFAULT_COLSPAN,
			name, href);
	}

	public void addText(TextSearchEntry searchEntry) {
		if (_restricted) {
			searchEntry.setHref(null);
		}

		_searchEntries.add(_searchEntries.size(), searchEntry);
	}

	@Override
	public String getAriaLabel() {
		return _ariaLabel;
	}

	@Override
	public String getClassHoverName() {
		return _classHoverName;
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public String getCssClass() {
		return _cssClass;
	}

	@Override
	public Map<String, Object> getData() {
		return _data;
	}

	@Override
	public List<SearchEntry> getEntries() {
		return _searchEntries;
	}

	@Override
	public Object getObject() {
		return _object;
	}

	@Override
	public Object getParameter(String param) {
		if (_params == null) {
			_params = new HashMap<>();
		}

		return _params.get(param);
	}

	@Override
	public int getPos() {
		return _pos;
	}

	@Override
	public String getPrimaryKey() {
		return _primaryKey;
	}

	@Override
	public String getRowId() {
		return _rowId;
	}

	@Override
	public String getState() {
		return _state;
	}

	@Override
	public String getTabIndex() {
		return _tabIndex;
	}

	@Override
	public boolean isBold() {
		return _bold;
	}

	@Override
	public boolean isRestricted() {
		return _restricted;
	}

	@Override
	public boolean isSkip() {
		return _skip;
	}

	@Override
	public void removeSearchEntry(int pos) {
		_searchEntries.remove(pos);
	}

	@Override
	public void setAriaLabel(String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	@Override
	public void setBold(boolean bold) {
		_bold = bold;
	}

	@Override
	public void setClassHoverName(String classHoverName) {
		_classHoverName = classHoverName;
	}

	@Override
	public void setClassName(String className) {
		_className = className;
	}

	@Override
	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	@Override
	public void setData(Map<String, Object> data) {
		_data = data;
	}

	@Override
	public void setObject(Object object) {
		_object = object;
	}

	@Override
	public void setParameter(String param, Object value) {
		if (_params == null) {
			_params = new HashMap<>();
		}

		_params.put(param, value);
	}

	@Override
	public void setPrimaryKey(String primaryKey) {
		_primaryKey = primaryKey;
	}

	@Override
	public void setRestricted(boolean restricted) {
		_restricted = restricted;
	}

	@Override
	public void setRowId(String rowId) {
		_rowId = rowId;
	}

	@Override
	public void setSkip(boolean skip) {
		_skip = skip;
	}

	@Override
	public void setState(String state) {
		_state = state;
	}

	@Override
	public void setTabIndex(String tabIndex) {
		_tabIndex = tabIndex;
	}

	private String _ariaLabel;
	private boolean _bold;
	private String _classHoverName;
	private String _className;
	private String _cssClass;
	private Map<String, Object> _data;
	private Object _object;
	private Map<String, Object> _params;
	private final int _pos;
	private String _primaryKey;
	private boolean _restricted;
	private String _rowId;
	private final List<SearchEntry> _searchEntries;
	private boolean _skip;
	private String _state;
	private String _tabIndex;

}