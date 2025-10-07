/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(service = GroupItemSelectorProvider.class)
public class AssetLibraryDepotGroupItemSelectorProvider
	extends BaseDepotGroupItemSelectorProvider {

	@Override
	public String getGroupType() {
		return "depot";
	}

	@Override
	protected int getDepotEntryType() {
		return DepotConstants.TYPE_ASSET_LIBRARY;
	}

	@Override
	protected String getEmptyResultsMessageKey() {
		return "no-asset-libraries-were-found";
	}

	@Override
	protected String getLabelKey() {
		return "asset-library";
	}

}