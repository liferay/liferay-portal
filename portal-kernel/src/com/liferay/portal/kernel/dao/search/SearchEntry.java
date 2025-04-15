/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

/**
 * @author Raymond Augé
 */
public interface SearchEntry {

	public static final String DEFAULT_ALIGN = StringPool.BLANK;

	public static final int DEFAULT_COLSPAN = 1;

	public static final String DEFAULT_CSS_CLASS = StringPool.BLANK;

	public static final String DEFAULT_VALIGN = StringPool.BLANK;

	public String getAlign();

	public int getColspan();

	public String getCssClass();

	public int getIndex();

	public String getValign();

	public boolean isTruncate();

	public void print(
			Writer writer, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	public void setAlign(String align);

	public void setColspan(int colspan);

	public void setCssClass(String cssClass);

	public void setIndex(int index);

	public void setTruncate(boolean truncate);

	public void setValign(String valign);

}