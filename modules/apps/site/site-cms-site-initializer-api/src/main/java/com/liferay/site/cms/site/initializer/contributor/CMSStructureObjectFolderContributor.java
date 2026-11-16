/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.contributor;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectEntryFolder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public interface CMSStructureObjectFolderContributor {

	public default String getBaseObjectDefinitionExternalReferenceCode() {
		return null;
	}

	public default List<DropdownItem> getBulkActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	public default List<DropdownItem> getCreationMenuDropdownItems(
		HttpServletRequest httpServletRequest,
		ObjectEntryFolder objectEntryFolder) {

		return null;
	}

	public default String getCreationMenuIcon() {
		return null;
	}

	public default Map<String, Object> getEmptyState(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	public default List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	public default String getFDSName() {
		return null;
	}

	public String getLabel();

	public default String getObjectEntryFolderExternalReferenceCode() {
		return null;
	}

	public String getObjectFolderExternalReferenceCode();

	public default String getPropsTransformerModule() {
		return null;
	}

	public Map<String, List<String>> getSystemObjectFieldNames();

	public default boolean isGalleryViewEnabled() {
		return true;
	}

}