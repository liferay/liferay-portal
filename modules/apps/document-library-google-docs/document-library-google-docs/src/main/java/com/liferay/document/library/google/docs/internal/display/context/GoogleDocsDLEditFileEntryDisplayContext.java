/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.display.context;

import com.liferay.document.library.display.context.BaseDLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLFilePicker;
import com.liferay.document.library.google.docs.internal.helper.GoogleDocsMetadataHelper;
import com.liferay.document.library.google.docs.internal.util.constants.GoogleDocsConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

/**
 * @author Iván Zaera
 */
public class GoogleDocsDLEditFileEntryDisplayContext
	extends BaseDLEditFileEntryDisplayContext {

	public GoogleDocsDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DLFileEntryType dlFileEntryType) {

		super(
			_UUID, parentDLEditFileEntryDisplayContext, httpServletRequest,
			httpServletResponse, dlFileEntryType);
	}

	public GoogleDocsDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileEntry fileEntry,
		GoogleDocsMetadataHelper googleDocsMetadataHelper) {

		super(
			_UUID, parentDLEditFileEntryDisplayContext, httpServletRequest,
			httpServletResponse, fileEntry);

		_googleDocsMetadataHelper = googleDocsMetadataHelper;
	}

	@Override
	public DLFilePicker getDLFilePicker(String onFilePickCallback)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return new GoogleDocsDLFilePicker(
			_googleDocsMetadataHelper, portletDisplay.getNamespace(),
			onFilePickCallback, themeDisplay);
	}

	@Override
	public long getMaximumUploadSize() {
		return 0;
	}

	@Override
	public boolean isCancelCheckoutDocumentButtonVisible() {
		return false;
	}

	@Override
	public boolean isCheckinButtonVisible() {
		return false;
	}

	@Override
	public boolean isCheckoutDocumentButtonVisible() {
		return false;
	}

	@Override
	public boolean isDDMStructureVisible(DDMStructure ddmStructure)
		throws PortalException {

		String ddmStructureKey = ddmStructure.getStructureKey();

		if (ddmStructureKey.equals(
				GoogleDocsConstants.DDM_STRUCTURE_KEY_GOOGLE_DOCS)) {

			return false;
		}

		return super.isDDMStructureVisible(ddmStructure);
	}

	@Override
	public boolean isVersionInfoVisible() {
		return false;
	}

	private static final UUID _UUID = UUID.fromString(
		"62BE5287-BEA3-4E3F-9731-15B1B901380D");

	private GoogleDocsMetadataHelper _googleDocsMetadataHelper;

}