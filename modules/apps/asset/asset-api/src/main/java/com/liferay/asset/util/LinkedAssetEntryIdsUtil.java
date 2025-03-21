/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.util;

import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lourdes Fernández Besada
 */
public class LinkedAssetEntryIdsUtil {

	public static void addLinkedAssetEntryId(
		HttpServletRequest httpServletRequest, long assetEntryId) {

		Set<Long> linkedAssetEntryIds =
			(Set<Long>)httpServletRequest.getAttribute(
				WebKeys.LINKED_ASSET_ENTRY_IDS);

		if (linkedAssetEntryIds == null) {
			linkedAssetEntryIds = new HashSet<>();
		}

		linkedAssetEntryIds.add(assetEntryId);

		httpServletRequest.setAttribute(
			WebKeys.LINKED_ASSET_ENTRY_IDS, linkedAssetEntryIds);
	}

	public static void addLinkedAssetEntryId(
		PortletRequest portletRequest, long assetEntryId) {

		Set<Long> linkedAssetEntryIds = (Set<Long>)portletRequest.getAttribute(
			WebKeys.LINKED_ASSET_ENTRY_IDS);

		if (linkedAssetEntryIds == null) {
			linkedAssetEntryIds = new HashSet<>();
		}

		linkedAssetEntryIds.add(assetEntryId);

		portletRequest.setAttribute(
			WebKeys.LINKED_ASSET_ENTRY_IDS, linkedAssetEntryIds);
	}

	public static void replaceLinkedAssetEntryId(
		HttpServletRequest httpServletRequest, long oldAssetEntryId,
		long newAssetEntryId) {

		Set<Long> linkedAssetEntryIds =
			(Set<Long>)httpServletRequest.getAttribute(
				WebKeys.LINKED_ASSET_ENTRY_IDS);

		if (linkedAssetEntryIds == null) {
			linkedAssetEntryIds = new HashSet<>();
		}

		linkedAssetEntryIds.remove(oldAssetEntryId);

		linkedAssetEntryIds.add(newAssetEntryId);

		httpServletRequest.setAttribute(
			WebKeys.LINKED_ASSET_ENTRY_IDS, linkedAssetEntryIds);
	}

}