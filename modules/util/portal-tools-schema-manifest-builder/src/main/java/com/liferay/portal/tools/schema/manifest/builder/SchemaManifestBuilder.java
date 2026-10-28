/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.schema.manifest.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ArgumentsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luis Ortiz
 */
public class SchemaManifestBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String baseDirName = GetterUtil.getString(
			arguments.get("schema.manifest.base.dir"), ".");
		String manifestFileName = GetterUtil.getString(
			arguments.get("schema.manifest.file"),
			"src/main/resources/META-INF/schema/manifest.json");
		String module = arguments.get("schema.manifest.module");

		try {
			SchemaManifestBuilder schemaManifestBuilder =
				new SchemaManifestBuilder(
					baseDirName, manifestFileName, module);

			schemaManifestBuilder.build();
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public SchemaManifestBuilder(
		String baseDirName, String manifestFileName, String module) {

		_module = module;

		_baseDir = new File(baseDirName);

		_manifestFile = new File(_baseDir, manifestFileName);
	}

	public void build() throws IOException {
		String module = _module;

		if (Validator.isNull(module)) {
			module = getBundleSymbolicName(new File(_baseDir, "bnd.bnd"));
		}

		write(_manifestFile, getManifestJSONObject(module));
	}

	protected String getBundleSymbolicName(File bndFile) throws IOException {
		if (!bndFile.exists()) {
			throw new IllegalArgumentException(
				"Unable to find " + bndFile.getAbsolutePath());
		}

		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(bndFile.toPath())) {
			properties.load(inputStream);
		}

		String bundleSymbolicName = properties.getProperty(
			"Bundle-SymbolicName");

		if (Validator.isNull(bundleSymbolicName)) {
			throw new IllegalArgumentException(
				"Unable to find \"Bundle-SymbolicName\" in " +
					bndFile.getAbsolutePath());
		}

		return bundleSymbolicName;
	}

	protected JSONObject getManifestJSONObject(String module) {
		JSONObject manifestJSONObject = new JSONObjectImpl();

		return manifestJSONObject.put(
			"description",
			StringBundler.concat(
				"Database changes manifest of one Liferay module, generated ",
				"by Liferay Schema Manifest Builder. Do not edit it by hand. ",
				"Top level keys: 'module' is the module's ",
				"Bundle-SymbolicName, or 'portal' for portal core; ",
				"'timestamp' is when the content last changed.")
		).put(
			"module", module
		);
	}

	protected String getTimestamp(String content) {
		Matcher matcher = _timestampPattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	protected void write(File manifestFile, JSONObject manifestJSONObject)
		throws IOException {

		Path manifestPath = manifestFile.toPath();

		if (manifestFile.exists()) {
			String content = StringUtil.replace(
				new String(
					Files.readAllBytes(manifestPath), StandardCharsets.UTF_8),
				StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);

			String timestamp = getTimestamp(content);

			if (timestamp != null) {
				manifestJSONObject.put("timestamp", timestamp);

				if (content.equals(JSONUtil.toString(manifestJSONObject))) {
					return;
				}
			}
		}

		Instant instant = Instant.now();

		manifestJSONObject.put(
			"timestamp",
			String.valueOf(instant.truncatedTo(ChronoUnit.SECONDS)));

		Files.createDirectories(manifestPath.getParent());

		String json = JSONUtil.toString(manifestJSONObject);

		Files.write(manifestPath, json.getBytes(StandardCharsets.UTF_8));

		System.out.println("Writing " + manifestFile);
	}

	private static final Pattern _timestampPattern = Pattern.compile(
		"^\t\"timestamp\": \"([^\"]*)\"", Pattern.MULTILINE);

	private final File _baseDir;
	private final File _manifestFile;
	private final String _module;

}