/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.url;

import com.liferay.adaptive.media.AMURIResolver;
import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.adaptive.media.image.util.AMImageAdaptationUtil;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Daniel Sanz
 */
@Component(service = AMImageURLFactory.class)
public class AMImageURLFactoryImpl implements AMImageURLFactory {

	@Override
	public URI createFileEntryURL(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		if (AMImageAdaptationUtil.isAdaptationDelegated(
				amImageConfigurationEntry, fileVersion.getCompanyId())) {

			return _createDelegatedAdaptationURL(
				amImageConfigurationEntry, fileVersion);
		}

		Date modifiedDate = fileVersion.getModifiedDate();

		String relativeURI = String.format(
			"image/%d/%s/%s?t=%d", fileVersion.getFileEntryId(),
			amImageConfigurationEntry.getUUID(),
			_encode(fileVersion.getFileName()), modifiedDate.getTime());

		return _amURIResolver.resolveURI(URI.create(relativeURI));
	}

	@Override
	public URI createFileVersionURL(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		if (AMImageAdaptationUtil.isAdaptationDelegated(
				amImageConfigurationEntry, fileVersion.getCompanyId())) {

			return _createDelegatedAdaptationURL(
				amImageConfigurationEntry, fileVersion);
		}

		String relativeURI = String.format(
			"image/%d/%d/%s/%s", fileVersion.getFileEntryId(),
			fileVersion.getFileVersionId(), amImageConfigurationEntry.getUUID(),
			_encode(fileVersion.getFileName()));

		return _amURIResolver.resolveURI(URI.create(relativeURI));
	}

	private URI _createDelegatedAdaptationURL(
		AMImageConfigurationEntry amImageConfigurationEntry,
		FileVersion fileVersion) {

		try {
			String previewURL = _dlURLHelper.getPreviewURL(
				fileVersion.getFileEntry(), fileVersion, (ThemeDisplay)null,
				StringPool.BLANK, true, false);

			if (Validator.isBlank(previewURL)) {
				return null;
			}

			if (!HttpComponentsUtil.hasDomain(previewURL)) {
				previewURL =
					_getCDNHost(fileVersion.getCompanyId()) + previewURL;
			}

			Map<String, String> properties =
				amImageConfigurationEntry.getProperties();

			Map<String, String[]> parameterMap =
				HttpComponentsUtil.getParameterMap(
					GetterUtil.getString(
						properties.get("adaptation-url-parameters")));

			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String[] values = entry.getValue();

				previewURL = HttpComponentsUtil.setParameter(
					previewURL, entry.getKey(), values[0]);
			}

			return URI.create(previewURL);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private String _encode(String s) {
		try {
			return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new AMRuntimeException.UnsupportedEncodingException(
				unsupportedEncodingException);
		}
	}

	private String _getCDNHost(long companyId) {
		String cdnHost = _portal.getCDNHostHttps(companyId);

		if (Validator.isBlank(cdnHost)) {
			cdnHost = _portal.getCDNHostHttp(companyId);
		}

		if (Validator.isBlank(cdnHost)) {
			return StringPool.BLANK;
		}

		if (cdnHost.endsWith(StringPool.SLASH)) {
			return cdnHost.substring(0, cdnHost.length() - 1);
		}

		return cdnHost;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageURLFactoryImpl.class);

	@Reference
	private AMURIResolver _amURIResolver;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private Portal _portal;

}