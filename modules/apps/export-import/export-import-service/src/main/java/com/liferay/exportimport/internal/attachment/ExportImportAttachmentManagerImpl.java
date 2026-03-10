/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.attachment;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.exportimport.internal.lar.PortletDataContextThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ExportImportAttachmentManager.class)
public class ExportImportAttachmentManagerImpl
	implements ExportImportAttachmentManager {

	@Override
	public String getFileURL(DLFileEntry dlFileEntry) throws Exception {
		if (dlFileEntry == null) {
			return null;
		}

		return _getURL(
			dlFileEntry.getCompanyId(), dlFileEntry::getContentStream,
			String.valueOf(dlFileEntry.getFileEntryId()),
			() -> _dlurlHelper.getThumbnailSrc(
				_dlAppLocalService.getFileEntry(dlFileEntry.getFileEntryId()),
				null));
	}

	@Override
	public String getImageURL(Image image) throws Exception {
		if (image == null) {
			return null;
		}

		return _getURL(
			image.getCompanyId(),
			() -> _imageLocalService.getImageInputStream(
				image.getCompanyId(), image.getImageId(), image.getType()),
			String.valueOf(image.getImageId()),
			() -> StringBundler.concat(
				_portal.getPathImage(), "/layout_icon?img_id=",
				image.getImageId()));
	}

	@Override
	public URL getURL(String urlString) throws MalformedURLException {
		PortletDataContext portletDataContext =
			PortletDataContextThreadLocal.getPortletDataContext();

		if (!urlString.startsWith(_PROTOCOL + ":") ||
			(portletDataContext == null)) {

			return new URL(urlString);
		}

		return new URL(
			null, urlString,
			new URLStreamHandler() {

				protected URLConnection openConnection(URL url) {
					return new URLConnection(url) {

						public void connect() {
						}

						@Override
						public InputStream getInputStream() {
							return portletDataContext.getZipEntryAsInputStream(
								_getZipPath(url.getPath()));
						}

					};
				}

			});
	}

	private String _getURL(
			long companyId,
			UnsafeSupplier<InputStream, Exception> inputStreamUnsafeSupplier,
			String key, UnsafeSupplier<String, Exception> pathUnsafeSupplier)
		throws Exception {

		PortletDataContext portletDataContext =
			PortletDataContextThreadLocal.getPortletDataContext();

		if ((portletDataContext == null) ||
			(portletDataContext.getZipWriter() == null)) {

			Company company = _companyLocalService.getCompany(companyId);
			boolean secure = _isSecure();

			String portalURL = _portal.getPortalURL(
				company.getVirtualHostname(),
				_portal.getPortalServerPort(secure), secure);

			return portalURL + pathUnsafeSupplier.get();
		}

		try (InputStream inputStream = inputStreamUnsafeSupplier.get()) {
			portletDataContext.addZipEntry(_getZipPath(key), inputStream);

			return _PROTOCOL + ":" + key;
		}
	}

	private String _getZipPath(String key) {
		return "batch-binaries/" + key;
	}

	private boolean _isSecure() {
		if (Objects.equals(
				Http.HTTPS,
				PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL)) ||
			Objects.equals(
				Http.HTTPS, PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL))) {

			return true;
		}

		return false;
	}

	private static final String _PROTOCOL = "lar";

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private Portal _portal;

}