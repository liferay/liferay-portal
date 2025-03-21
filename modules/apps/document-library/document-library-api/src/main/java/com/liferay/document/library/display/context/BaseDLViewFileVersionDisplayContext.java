/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Iván Zaera
 */
public class BaseDLViewFileVersionDisplayContext
	extends BaseDLDisplayContext<DLViewFileVersionDisplayContext>
	implements DLViewFileVersionDisplayContext {

	public BaseDLViewFileVersionDisplayContext(
		UUID uuid, DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		super(
			uuid, parentDLDisplayContext, httpServletRequest,
			httpServletResponse);

		this.fileVersion = fileVersion;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		return parentDisplayContext.getActionDropdownItems();
	}

	@Override
	public String getCssClassFileMimeType() {
		return parentDisplayContext.getCssClassFileMimeType();
	}

	@Override
	public DDMFormValues getDDMFormValues(DDMStructure ddmStructure)
		throws PortalException {

		return parentDisplayContext.getDDMFormValues(ddmStructure);
	}

	@Override
	public DDMFormValues getDDMFormValues(long ddmStorageId)
		throws PortalException {

		return parentDisplayContext.getDDMFormValues(ddmStorageId);
	}

	@Override
	public List<DDMStructure> getDDMStructures() throws PortalException {
		return parentDisplayContext.getDDMStructures();
	}

	@Override
	public int getDDMStructuresCount() throws PortalException {
		return parentDisplayContext.getDDMStructuresCount();
	}

	@Override
	public String getDiscussionClassName() {
		return parentDisplayContext.getDiscussionClassName();
	}

	@Override
	public long getDiscussionClassPK() {
		return parentDisplayContext.getDiscussionClassPK();
	}

	@Override
	public String getDiscussionLabel(Locale locale) {
		return parentDisplayContext.getDiscussionLabel(locale);
	}

	@Override
	public String getIconFileMimeType() {
		return parentDisplayContext.getIconFileMimeType();
	}

	@Override
	public boolean hasApprovedVersion() {
		return parentDisplayContext.hasApprovedVersion();
	}

	@Override
	public boolean hasCustomThumbnail() {
		return parentDisplayContext.hasCustomThumbnail();
	}

	@Override
	public boolean hasPreview() {
		return parentDisplayContext.hasPreview();
	}

	@Override
	public boolean isActionsVisible() {
		return parentDisplayContext.isActionsVisible();
	}

	@Override
	public boolean isDownloadLinkVisible() throws PortalException {
		return parentDisplayContext.isDownloadLinkVisible();
	}

	@Override
	public boolean isShared() throws PortalException {
		return parentDisplayContext.isShared();
	}

	@Override
	public boolean isSharingLinkVisible() throws PortalException {
		return parentDisplayContext.isSharingLinkVisible();
	}

	@Override
	public boolean isVersionInfoVisible() throws PortalException {
		return parentDisplayContext.isVersionInfoVisible();
	}

	@Override
	public void renderCustomThumbnail(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		parentDisplayContext.renderCustomThumbnail(
			httpServletRequest, httpServletResponse);
	}

	@Override
	public void renderPreview(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		parentDisplayContext.renderPreview(
			httpServletRequest, httpServletResponse);
	}

	protected FileVersion fileVersion;

}