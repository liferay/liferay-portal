/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Date;

/**
 * @author Péter Borkuti
 */
public class ProcessDateTag extends IncludeTag {

	public Date getDate() {
		return _date;
	}

	public String getLabelKey() {
		return _labelKey;
	}

	public boolean isListView() {
		return _listView;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public void setLabelKey(String labelKey) {
		_labelKey = labelKey;
	}

	public void setListView(boolean listView) {
		_listView = listView;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_date = null;
		_labelKey = StringPool.BLANK;
		_listView = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:process-date:date", _date);
		httpServletRequest.setAttribute(
			"liferay-staging:process-date:labelKey", _labelKey);
		httpServletRequest.setAttribute(
			"liferay-staging:process-date:listView", _listView);
	}

	private static final String _PAGE = "/process_date/page.jsp";

	private Date _date;
	private String _labelKey = StringPool.BLANK;
	private boolean _listView;

}