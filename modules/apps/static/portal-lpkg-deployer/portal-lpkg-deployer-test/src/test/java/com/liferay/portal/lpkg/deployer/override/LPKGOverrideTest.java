/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.override;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.Version;

/**
 * @author Matthew Tambara
 */
public class LPKGOverrideTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOverrideLPKG() throws IOException {
		String liferayHome = SystemProperties.get("liferay.home");

		Assert.assertFalse(
			"Missing system property \"liferay.home\"",
			Validator.isNull(liferayHome));

		File file = new File(liferayHome, "/osgi/marketplace/override");

		for (File subfiles : file.listFiles()) {
			subfiles.delete();
		}

		Map<String, String> overrides = new HashMap<>();

		List<String> lpkgStaticFileNames = _getStaticLPKGFileNames();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				Paths.get(liferayHome, "/osgi/marketplace"), "*.lpkg")) {

			for (Path lpkgPath : directoryStream) {
				try (ZipFile zipFile = new ZipFile(lpkgPath.toFile())) {
					Enumeration<? extends ZipEntry> enumeration =
						zipFile.entries();

					while (enumeration.hasMoreElements()) {
						ZipEntry zipEntry = enumeration.nextElement();

						String name = zipEntry.getName();

						if (!(name.startsWith("com.liferay") &&
							  name.endsWith(".jar")) &&
							!name.endsWith(".war")) {

							continue;
						}

						Matcher matcher = _pattern.matcher(name);

						Assert.assertTrue(
							name + " does not match " + _pattern,
							matcher.matches());

						name = matcher.group(1) + matcher.group(4);

						Path lpkgPathName = lpkgPath.getFileName();

						if (lpkgStaticFileNames.contains(
								lpkgPathName.toString())) {

							Path staticOverridePath = Paths.get(
								liferayHome, "/osgi/static/", name);

							Files.copy(
								zipFile.getInputStream(zipEntry),
								staticOverridePath,
								StandardCopyOption.REPLACE_EXISTING);

							_upgradeModuleVersion(staticOverridePath, null);

							overrides.put(
								"static.".concat(matcher.group(1)), null);
						}
						else {
							Path overridePath = Paths.get(
								file.toString(), name);

							Files.copy(
								zipFile.getInputStream(zipEntry), overridePath,
								StandardCopyOption.REPLACE_EXISTING);

							if (name.endsWith(".war")) {
								String fileName = matcher.group(1);

								fileName = StringUtil.removeSubstring(
									fileName, "-dxp");

								overrides.put("war.".concat(fileName), null);

								continue;
							}

							_upgradeModuleVersion(overridePath, overrides);
						}
					}
				}
			}
		}

		StringBundler sb = new StringBundler(overrides.size() * 4);

		for (Map.Entry<String, String> entry : overrides.entrySet()) {
			sb.append(entry.getKey());
			sb.append(StringPool.COLON);
			sb.append(entry.getValue());
			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		Files.write(
			Paths.get(liferayHome, "/overrides"), Arrays.asList(sb.toString()),
			StandardCharsets.UTF_8, StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

	private List<String> _getStaticLPKGFileNames() {
		String staticLPKGBundleSymbolicNames = SystemProperties.get(
			"static.lpkg.bundle.symbolic.names");

		List<String> staticLPKGBundleSymbolicNameList =
			com.liferay.petra.string.StringUtil.split(
				staticLPKGBundleSymbolicNames);

		String name = ReleaseInfo.getName();

		String lpkgSymbolicNamePrefix = "Liferay ";

		if (name.contains("Community")) {
			lpkgSymbolicNamePrefix = "Liferay CE ";
		}

		for (int i = 0; i < staticLPKGBundleSymbolicNameList.size(); i++) {
			staticLPKGBundleSymbolicNameList.set(
				i,
				StringBundler.concat(
					lpkgSymbolicNamePrefix,
					staticLPKGBundleSymbolicNameList.get(i), ".lpkg"));
		}

		return staticLPKGBundleSymbolicNameList;
	}

	private void _upgradeModuleVersion(Path path, Map<String, String> overrides)
		throws IOException {

		try (FileSystem fileSystem = FileSystems.newFileSystem(path)) {
			Path manifestPath = fileSystem.getPath("META-INF/MANIFEST.MF");

			try (InputStream inputStream = Files.newInputStream(manifestPath);
				UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
					new UnsyncByteArrayOutputStream()) {

				Manifest manifest = new Manifest(inputStream);

				Attributes attributes = manifest.getMainAttributes();

				String versionString = attributes.getValue("Bundle-Version");

				Version version = new Version(versionString);

				version = new Version(
					version.getMajor(), version.getMinor(),
					version.getMicro() + 1, version.getQualifier());

				versionString = version.toString();

				attributes.putValue("Bundle-Version", versionString);

				if (overrides != null) {
					overrides.put(
						attributes.getValue("Bundle-SymbolicName"),
						versionString);
				}

				manifest.write(unsyncByteArrayOutputStream);

				Files.write(
					manifestPath, unsyncByteArrayOutputStream.toByteArray(),
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE);
			}
		}
	}

	private static final Pattern _pattern = Pattern.compile(
		"(.*?)(-\\d+\\.\\d+\\.\\d+)(\\..+)?(\\.[jw]ar)");

}