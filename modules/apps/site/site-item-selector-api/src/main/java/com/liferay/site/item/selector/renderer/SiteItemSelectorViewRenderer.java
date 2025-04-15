/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.renderer;

import com.liferay.site.item.selector.display.context.SitesItemSelectorViewDisplayContext;

import jakarta.servlet.ServletException;

import java.io.IOException;

/**
 * @author Alejandro Tardín
 */
public interface SiteItemSelectorViewRenderer {

	public void renderHTML(
			SitesItemSelectorViewDisplayContext
				sitesItemSelectorViewDisplayContext)
		throws IOException, ServletException;

}