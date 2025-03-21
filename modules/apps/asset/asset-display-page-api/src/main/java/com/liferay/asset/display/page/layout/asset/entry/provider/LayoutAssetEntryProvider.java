/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.layout.asset.entry.provider;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.model.Layout;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Roberto Díaz
 */
public interface LayoutAssetEntryProvider {

	public AssetEntry getLayoutAssetEntry(
		HttpServletRequest httpServletRequest, Layout layout);

}