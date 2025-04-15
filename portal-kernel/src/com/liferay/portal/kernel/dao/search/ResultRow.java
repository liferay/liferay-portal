/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public interface ResultRow {

	public void addButton(int index, String name, String href);

	public void addButton(
		int index, String align, String valign, int colspan, String name,
		String href);

	public void addButton(String name, String href);

	public void addButton(
		String align, String valign, int colspan, String name, String href);

	public void addButton(
		String align, String valign, String name, String href);

	public void addDate(Date date);

	public void addDate(Date date, PortletURL portletURL);

	public void addDate(Date date, String href);

	public void addDate(int index, Date date, String href);

	public void addJSP(
		int index, String path, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addJSP(
		int index, String align, String valign, int colspan, String path,
		ServletContext servletContext, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addJSP(
		String path, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addJSP(
		String align, String valign, int colspan, String path,
		ServletContext servletContext, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addJSP(
		String path, String cssClass, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addJSP(
		String align, String valign, String path, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addSearchEntry(int index, SearchEntry searchEntry);

	public void addSearchEntry(SearchEntry searchEntry);

	public void addStatus(int status);

	public void addStatus(
		int index, int status, long statusByUserId, Date statusDate,
		String href);

	public void addStatus(
		int index, int status, String href, ServletContext servletContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void addStatus(int status, long statusByUserId, Date statusDate);

	public void addStatus(
		int status, long statusByUserId, Date statusDate,
		PortletURL portletURL);

	public void addStatus(
		int status, long statusByUserId, Date statusDate, String href);

	public void addStatus(int status, PortletURL portletURL);

	public void addStatus(int status, String href);

	public void addText(int index, String name);

	public void addText(int index, String name, PortletURL portletURL);

	public void addText(int index, String name, String href);

	public void addText(
		int index, String align, String valign, int colspan, String name);

	public void addText(
		int index, String align, String valign, int colspan, String name,
		PortletURL portletURL);

	public void addText(
		int index, String align, String valign, int colspan, String name,
		String href);

	public void addText(String name);

	public void addText(String name, PortletURL portletURL);

	public void addText(String name, String href);

	public void addText(String align, String valign, int colspan, String name);

	public void addText(
		String align, String valign, int colspan, String name,
		PortletURL portletURL);

	public void addText(
		String align, String valign, int colspan, String name, String href);

	public void addText(String align, String valign, String name);

	public void addText(
		String align, String valign, String name, PortletURL portletURL);

	public void addText(String align, String valign, String name, String href);

	public String getAriaLabel();

	public String getClassHoverName();

	public String getClassName();

	public String getCssClass();

	public Map<String, Object> getData();

	public List<SearchEntry> getEntries();

	public Object getObject();

	public Object getParameter(String param);

	public int getPos();

	public String getPrimaryKey();

	public String getRowId();

	public String getState();

	public String getTabIndex();

	public boolean isBold();

	public boolean isRestricted();

	public boolean isSkip();

	public void removeSearchEntry(int pos);

	public void setAriaLabel(String ariaLabel);

	public void setBold(boolean bold);

	public void setClassHoverName(String classHoverName);

	public void setClassName(String className);

	public void setCssClass(String cssClass);

	public void setData(Map<String, Object> data);

	public void setObject(Object object);

	public void setParameter(String param, Object value);

	public void setPrimaryKey(String primaryKey);

	public void setRestricted(boolean restricted);

	public void setRowId(String rowId);

	public void setSkip(boolean skip);

	public void setState(String state);

	public void setTabIndex(String tabIndex);

}