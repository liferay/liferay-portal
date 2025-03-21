/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

/**
 * @author Iván Zaera
 */
public interface DLViewFileVersionDisplayContext extends DLDisplayContext {

	public List<DropdownItem> getActionDropdownItems() throws PortalException;

	public String getCssClassFileMimeType();

	public DDMFormValues getDDMFormValues(DDMStructure ddmStructure)
		throws PortalException;

	public DDMFormValues getDDMFormValues(long ddmStorageId)
		throws PortalException;

	public List<DDMStructure> getDDMStructures() throws PortalException;

	public int getDDMStructuresCount() throws PortalException;

	public String getDiscussionClassName();

	public long getDiscussionClassPK();

	public String getDiscussionLabel(Locale locale);

	public default String getIconFileMimeType() {
		return "document-default";
	}

	public boolean hasApprovedVersion();

	public default boolean hasCustomThumbnail() {
		return false;
	}

	public boolean hasPreview();

	public default boolean isActionsVisible() {
		return false;
	}

	public boolean isDownloadLinkVisible() throws PortalException;

	public default boolean isShared() throws PortalException {
		return false;
	}

	public default boolean isSharingLinkVisible() throws PortalException {
		return false;
	}

	public boolean isVersionInfoVisible() throws PortalException;

	public default void renderCustomThumbnail(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {
	}

	public void renderPreview(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException;

}