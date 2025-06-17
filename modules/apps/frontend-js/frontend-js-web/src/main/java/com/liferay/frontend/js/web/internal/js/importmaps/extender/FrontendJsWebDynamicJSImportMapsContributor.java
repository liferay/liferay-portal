/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.DynamicJSImportMapsContributor;
import com.liferay.frontend.js.web.internal.hashed.files.HashedFilesRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = DynamicJSImportMapsContributor.class)
public class FrontendJsWebDynamicJSImportMapsContributor
	implements DynamicJSImportMapsContributor {

	@Override
	public void writeGlobalImports(
			HttpServletRequest httpServletRequest, Writer writer)
		throws IOException {

		writer.write("\"@liferay/language/\": \"/o/js/language/\"");

		_hashedFilesRegistry.forEach(
			(unhashedFileURI, hashedFileURI) -> {
				if (!unhashedFileURI.endsWith(".js")) {
					return;
				}

				try {
					writer.write(", \"");
					writer.write(unhashedFileURI);
					writer.write("\": \"");
					writer.write(hashedFileURI);
					writer.write(StringPool.QUOTE);
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			});
	}

	@Override
	public void writeScopedImports(
			HttpServletRequest httpServletRequest, Writer writer)
		throws IOException {
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_hashedFilesRegistry = new HashedFilesRegistry(bundleContext);
	}

	@Deactivate
	protected void deactivate() {
		_hashedFilesRegistry.close();

		_hashedFilesRegistry = null;
	}

	private HashedFilesRegistry _hashedFilesRegistry;

	@Reference
	private Portal _portal;

}