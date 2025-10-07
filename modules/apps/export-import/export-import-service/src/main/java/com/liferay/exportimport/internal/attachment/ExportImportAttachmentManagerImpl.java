/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.attachment;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.exportimport.internal.lar.PortletDataContextThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = ExportImportAttachmentManager.class)
public class ExportImportAttachmentManagerImpl
	implements ExportImportAttachmentManager {

	@Override
	public String getFileURL(DLFileEntry dlFileEntry) throws Exception {
		PortletDataContext portletDataContext =
			PortletDataContextThreadLocal.getPortletDataContext();

		if ((portletDataContext == null) ||
			(portletDataContext.getZipWriter() == null)) {

			return null;
		}

		try (InputStream inputStream = dlFileEntry.getContentStream()) {
			String fileKey = String.valueOf(dlFileEntry.getFileEntryId());

			portletDataContext.addZipEntry(_getZipPath(fileKey), inputStream);

			return _PROTOCOL + ":" + fileKey;
		}
	}

	@Override
	public URL getURL(String url) throws MalformedURLException {
		PortletDataContext portletDataContext =
			PortletDataContextThreadLocal.getPortletDataContext();

		if (!url.startsWith(_PROTOCOL + ":") || (portletDataContext == null)) {
			return new URL(url);
		}

		return new URL(
			null, url,
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

	private String _getZipPath(String key) {
		return "batch-binaries/" + key;
	}

	private static final String _PROTOCOL = "lar";

}