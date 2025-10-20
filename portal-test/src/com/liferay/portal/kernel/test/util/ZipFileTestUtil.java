/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.zip.ZipWriter;

import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class ZipFileTestUtil {

	public static InputStream toInputStream(
			String basePath, Bundle bundle, ZipWriter zipWriter)
		throws Exception {

		Enumeration<URL> enumeration = bundle.findEntries(basePath, "*", true);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				String urlPath = url.getPath();

				if (urlPath.endsWith(StringPool.SLASH)) {
					continue;
				}

				String zipPath = urlPath.substring(basePath.length());

				if (zipPath.startsWith(StringPool.SLASH)) {
					zipPath = zipPath.substring(1);
				}

				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(zipPath, inputStream);
				}
			}
		}

		return new FileInputStream(zipWriter.getFile());
	}

}