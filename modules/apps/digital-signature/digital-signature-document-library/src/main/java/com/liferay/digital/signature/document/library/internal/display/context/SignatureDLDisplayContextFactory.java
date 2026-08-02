/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.document.library.internal.display.context;

import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.digital.signature.url.SignDSURLProvider;
import com.liferay.document.library.display.context.DLDisplayContextFactory;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = DLDisplayContextFactory.class)
public class SignatureDLDisplayContextFactory
	implements DLDisplayContextFactory {

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DLFileEntryType dlFileEntryType) {

		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileEntry fileEntry) {

		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileShortcut fileShortcut) {

		return parentDLViewFileVersionDisplayContext;
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		if (fileVersion == null) {
			return parentDLViewFileVersionDisplayContext;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) || !themeDisplay.isSignedIn()) {
			return parentDLViewFileVersionDisplayContext;
		}

		Map<Long, String> providerRequestIds =
			(Map<Long, String>)httpServletRequest.getAttribute(
				_PROVIDER_REQUEST_IDS);

		if (providerRequestIds == null) {
			providerRequestIds = _dsRequestManager.getProviderRequestIds(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				Arrays.asList("delivered", "sent"));

			httpServletRequest.setAttribute(
				_PROVIDER_REQUEST_IDS, providerRequestIds);
		}

		if (providerRequestIds.isEmpty()) {
			return parentDLViewFileVersionDisplayContext;
		}

		try {
			FileEntry fileEntry = fileVersion.getFileEntry();

			String providerRequestId = providerRequestIds.get(
				fileEntry.getFileEntryId());

			if (Validator.isNull(providerRequestId)) {
				return parentDLViewFileVersionDisplayContext;
			}

			return new SignatureDLViewFileVersionDisplayContext(
				parentDLViewFileVersionDisplayContext, httpServletRequest,
				httpServletResponse, fileVersion, providerRequestId,
				_signDSURLProvider);
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to create the signature display context for file " +
					"version " + fileVersion,
				portalException);
		}
	}

	private static final String _PROVIDER_REQUEST_IDS =
		SignatureDLDisplayContextFactory.class.getName() +
			"#PROVIDER_REQUEST_IDS";

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private SignDSURLProvider _signDSURLProvider;

}