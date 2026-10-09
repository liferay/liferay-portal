/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.bundle.builder.commands;

import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;

import aQute.lib.io.IO;

import com.beust.jcommander.Parameters;

import com.liferay.osgi.bundle.builder.OSGiBundleBuilderArgs;
import com.liferay.osgi.bundle.builder.internal.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.Map;

/**
 * @author David Truong
 */
@Parameters(
	commandDescription = "Generates an exploded JAR of the OSGi bundle.",
	commandNames = "exploded-jar"
)
public class ExplodedJarCommand extends BaseCommand {

	@Override
	protected void writeOutput(
			Jar jar, OSGiBundleBuilderArgs osgiBundleBuilderArgs)
		throws Exception {

		long lastModified = jar.lastModified();

		File outputDir = osgiBundleBuilderArgs.getOutputFile();

		FileUtil.createDirectories(outputDir);

		Map<String, Resource> resources = jar.getResources();

		for (Map.Entry<String, Resource> entry : resources.entrySet()) {
			File outputFile = IO.getFile(outputDir, entry.getKey());

			Resource resource = entry.getValue();

			if (resource instanceof FileResource) {
				FileResource fileResource = (FileResource)resource;

				if (outputFile.equals(fileResource.getFile())) {
					continue;
				}
			}

			if (outputFile.exists() &&
				(outputFile.lastModified() >= lastModified)) {

				continue;
			}

			FileUtil.createDirectories(outputFile.getParentFile());

			try (OutputStream outputStream = new FileOutputStream(outputFile)) {
				IO.copy(resource.openInputStream(), outputStream);
			}
		}

		File manifestDir = new File(outputDir, "META-INF");

		FileUtil.createDirectories(manifestDir);

		File manifestFile = new File(manifestDir, "MANIFEST.MF");

		try (OutputStream outputStream = new FileOutputStream(manifestFile)) {
			jar.writeManifest(outputStream);
		}
	}

}