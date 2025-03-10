/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.jakarta.ee.transformer;

import com.liferay.portal.tools.jakarta.ee.transformer.function.BundleHeaderReplacerBiFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author Shuyang Zhou
 */
public class JarTransformer {

	public static void transform(File file) throws IOException {
		File transformedFile = new File(
			file.getParent(), file.getName() + ".transformed");

		transformedFile.delete();

		try (JarFile jarFile = new JarFile(file)) {
			Manifest manifest = jarFile.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			Map<Object, Object> map =
				BundleHeaderReplacerBiFunction.INSTANCE.apply(
					file.getCanonicalPath(), attributes);

			attributes.clear();

			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				attributes.put(entry.getKey(), entry.getValue());
			}

			try (OutputStream outputStream = new FileOutputStream(
					transformedFile);
				JarOutputStream jarOutputStream = new JarOutputStream(
					outputStream, manifest)) {

				Enumeration<JarEntry> enumeration = jarFile.entries();

				while (enumeration.hasMoreElements()) {
					JarEntry jarEntry = enumeration.nextElement();

					if (Objects.equals(jarEntry.getName(), "META-INF/") ||
						Objects.equals(
							jarEntry.getName(), "META-INF/MANIFEST.MF")) {

						continue;
					}

					jarOutputStream.putNextEntry(jarEntry);

					InputStream inputStream = jarFile.getInputStream(jarEntry);

					inputStream.transferTo(jarOutputStream);
				}
			}
		}

		file.delete();

		transformedFile.renameTo(file);
	}

}