/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search.result;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchResult;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author André de Oliveira
 */
public interface SearchResultTranslator {

	public List<SearchResult> translate(
		Hits hits, Locale locale, PortletRequest portletRequest,
		PortletResponse portletResponse);

}