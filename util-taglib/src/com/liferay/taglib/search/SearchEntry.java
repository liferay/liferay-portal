/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class SearchEntry
	implements Cloneable, com.liferay.portal.kernel.dao.search.SearchEntry {

	@Override
	public String getAlign() {
		return _align;
	}

	@Override
	public int getColspan() {
		return _colspan;
	}

	@Override
	public String getCssClass() {
		return _cssClass;
	}

	@Override
	public int getIndex() {
		return _index;
	}

	@Override
	public String getValign() {
		return _valign;
	}

	@Override
	public boolean isTruncate() {
		return _truncate;
	}

	@Override
	public abstract void print(
			Writer writer, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	@Override
	public void setAlign(String align) {
		_align = align;
	}

	@Override
	public void setColspan(int colspan) {
		_colspan = colspan;
	}

	@Override
	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	@Override
	public void setIndex(int index) {
		_index = index;
	}

	@Override
	public void setTruncate(boolean truncate) {
		_truncate = truncate;
	}

	@Override
	public void setValign(String valign) {
		_valign = valign;
	}

	private String _align = DEFAULT_ALIGN;
	private int _colspan = DEFAULT_COLSPAN;
	private String _cssClass = DEFAULT_CSS_CLASS;
	private int _index;
	private boolean _truncate;
	private String _valign = DEFAULT_VALIGN;

}