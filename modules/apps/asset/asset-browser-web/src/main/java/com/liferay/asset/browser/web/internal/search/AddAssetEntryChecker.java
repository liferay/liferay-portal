/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.browser.web.internal.search;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.RenderResponse;

/**
 * @author Jürgen Kappler
 */
public class AddAssetEntryChecker extends EmptyOnClickRowChecker {

	public AddAssetEntryChecker(
		RenderResponse renderResponse, long assetEntryId) {

		super(renderResponse);

		_assetEntryId = assetEntryId;
	}

	@Override
	public boolean isDisabled(Object object) {
		AssetEntry assetEntry = (AssetEntry)object;

		if (assetEntry.getEntryId() == _assetEntryId) {
			return true;
		}

		return super.isDisabled(object);
	}

	private final long _assetEntryId;

}