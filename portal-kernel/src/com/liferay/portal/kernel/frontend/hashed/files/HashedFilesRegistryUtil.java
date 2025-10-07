/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.frontend.hashed.files;

import com.liferay.portal.kernel.module.service.Snapshot;

import java.net.URL;

import java.util.function.BiConsumer;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFilesRegistryUtil {

	public static void forEach(BiConsumer<String, String> biConsumer) {
		HashedFilesRegistry hashedFilesRegistry =
			_hashedFilesRegistrySnapshot.get();

		hashedFilesRegistry.forEach(biConsumer);
	}

	public static String getHashedFileURI(String unhashedFileURI) {
		HashedFilesRegistry hashedFilesRegistry =
			_hashedFilesRegistrySnapshot.get();

		return hashedFilesRegistry.getHashedFileURI(unhashedFileURI);
	}

	public static URL getResource(String path) {
		HashedFilesRegistry hashedFilesRegistry =
			_hashedFilesRegistrySnapshot.get();

		return hashedFilesRegistry.getResource(path);
	}

	private static final Snapshot<HashedFilesRegistry>
		_hashedFilesRegistrySnapshot = new Snapshot<>(
			HashedFilesRegistryUtil.class, HashedFilesRegistry.class);

}