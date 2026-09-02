/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
public interface OmniSearchResultProvider {

	public List<OmniSearchResult> getOmniSearchResults(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws PortalException;

}