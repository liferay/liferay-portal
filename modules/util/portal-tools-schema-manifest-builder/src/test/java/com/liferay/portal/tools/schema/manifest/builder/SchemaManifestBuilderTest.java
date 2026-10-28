/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.schema.manifest.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.time.Instant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Luis Ortiz
 */
public class SchemaManifestBuilderTest {

	@Before
	public void setUp() {
		_baseDir = temporaryFolder.getRoot();

		_manifestFile = new File(_baseDir, _MANIFEST_FILE_NAME);

		_schemaManifestBuilder = new SchemaManifestBuilder(
			_baseDir.getAbsolutePath(), _MANIFEST_FILE_NAME, null);
	}

	@Test
	public void testBuild() throws Exception {
		String module = RandomTestUtil.randomString();

		_writeBndFile(module);

		_schemaManifestBuilder.build();

		String timestamp = _assertManifest(module);

		Instant.parse(timestamp);

		String randomTimestamp = String.valueOf(
			Instant.ofEpochSecond(RandomTestUtil.randomInt()));

		_writeManifest(
			StringUtil.replace(
				_read(_manifestFile), timestamp, randomTimestamp));

		_schemaManifestBuilder.build();

		Assert.assertEquals(randomTimestamp, _assertManifest(module));

		_writeManifest("{}");

		_schemaManifestBuilder.build();

		Assert.assertNotEquals(randomTimestamp, _assertManifest(module));

		module = RandomTestUtil.randomString();

		_writeBndFile(module);

		_schemaManifestBuilder.build();

		_assertManifest(module);
	}

	@Test
	public void testBuildWithCRLFLineEndings() throws Exception {
		_writeBndFile(RandomTestUtil.randomString());

		_schemaManifestBuilder.build();

		String content = StringUtil.replace(_read(_manifestFile), "\n", "\r\n");

		_writeManifest(content);

		_schemaManifestBuilder.build();

		Assert.assertEquals(content, _read(_manifestFile));
	}

	@Test
	public void testBuildWithModule() throws Exception {
		_writeBndFile(RandomTestUtil.randomString());

		String module = RandomTestUtil.randomString();

		SchemaManifestBuilder schemaManifestBuilder = new SchemaManifestBuilder(
			_baseDir.getAbsolutePath(), _MANIFEST_FILE_NAME, module);

		schemaManifestBuilder.build();

		_assertManifest(module);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildWithoutBndFile() throws Exception {
		_schemaManifestBuilder.build();
	}

	@Test
	public void testGetBundleSymbolicName() throws Exception {
		String bundleSymbolicName = RandomTestUtil.randomString();

		_writeBndFile(bundleSymbolicName);

		Assert.assertEquals(
			bundleSymbolicName,
			_schemaManifestBuilder.getBundleSymbolicName(
				new File(_baseDir, "bnd.bnd")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetBundleSymbolicNameWithoutBndFile() throws Exception {
		_schemaManifestBuilder.getBundleSymbolicName(
			new File(_baseDir, "bnd.bnd"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetBundleSymbolicNameWithoutBundleSymbolicName()
		throws Exception {

		File bndFile = new File(_baseDir, "bnd.bnd");

		Files.write(
			bndFile.toPath(),
			StringBundler.concat(
				"Bundle-Name: ", RandomTestUtil.randomString(), "\n"
			).getBytes(
				StandardCharsets.UTF_8
			));

		_schemaManifestBuilder.getBundleSymbolicName(bndFile);
	}

	@Test
	public void testGetManifestJSONObject() {
		String module = RandomTestUtil.randomString();

		JSONObject manifestJSONObject =
			_schemaManifestBuilder.getManifestJSONObject(module);

		Assert.assertEquals(
			_DESCRIPTION, manifestJSONObject.get("description"));
		Assert.assertEquals(module, manifestJSONObject.get("module"));
		Assert.assertFalse(manifestJSONObject.has("timestamp"));
	}

	@Test
	public void testGetTimestamp() {
		String timestamp = String.valueOf(
			Instant.ofEpochSecond(RandomTestUtil.randomInt()));

		Assert.assertEquals(
			timestamp,
			_schemaManifestBuilder.getTimestamp(
				StringBundler.concat(
					"{\n\t\"module\": \"", RandomTestUtil.randomString(),
					"\",\n\t\"timestamp\": \"", timestamp, "\"\n}")));
	}

	@Test
	public void testGetTimestampWithoutTimestamp() {
		Assert.assertNull(
			_schemaManifestBuilder.getTimestamp(
				StringBundler.concat(
					"{\n\t\"module\": \"", RandomTestUtil.randomString(),
					"\"\n}")));
	}

	@Test
	public void testWrite() throws Exception {
		String module = RandomTestUtil.randomString();

		_schemaManifestBuilder.write(
			_manifestFile,
			_schemaManifestBuilder.getManifestJSONObject(module));

		String content = _read(_manifestFile);

		String timestamp = _assertManifest(module);

		Instant.parse(timestamp);

		_schemaManifestBuilder.write(
			_manifestFile,
			_schemaManifestBuilder.getManifestJSONObject(module));

		Assert.assertEquals(content, _read(_manifestFile));
	}

	@Test
	public void testWriteWithChangedContent() throws Exception {
		String timestamp = String.valueOf(
			Instant.ofEpochSecond(RandomTestUtil.randomInt()));

		JSONObject manifestJSONObject =
			_schemaManifestBuilder.getManifestJSONObject(
				RandomTestUtil.randomString());

		manifestJSONObject.put("timestamp", timestamp);

		Files.createDirectories(
			_manifestFile.toPath(
			).getParent());

		_writeManifest(JSONUtil.toString(manifestJSONObject));

		String module = RandomTestUtil.randomString();

		_schemaManifestBuilder.write(
			_manifestFile,
			_schemaManifestBuilder.getManifestJSONObject(module));

		Assert.assertNotEquals(timestamp, _assertManifest(module));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _assertManifest(String module) throws Exception {
		String content = _read(_manifestFile);

		Matcher matcher = _timestampPattern.matcher(content);

		Assert.assertTrue(matcher.find());

		String timestamp = matcher.group(1);

		Assert.assertEquals(
			StringBundler.concat(
				"{\n\t\"description\": \"", _DESCRIPTION,
				"\",\n\t\"module\": \"", module, "\",\n\t\"timestamp\": \"",
				timestamp, "\"\n}"),
			content);

		return timestamp;
	}

	private String _read(File file) throws Exception {
		return new String(
			Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}

	private void _writeBndFile(String bundleSymbolicName) throws Exception {
		String content = StringBundler.concat(
			"Bundle-Name: ", RandomTestUtil.randomString(),
			"\nBundle-SymbolicName: ", bundleSymbolicName, "\n");

		File bndFile = new File(_baseDir, "bnd.bnd");

		Files.write(bndFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	private void _writeManifest(String content) throws Exception {
		Files.write(
			_manifestFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	private static final String _DESCRIPTION = StringBundler.concat(
		"Database changes manifest of one Liferay module, generated by ",
		"Liferay Schema Manifest Builder. Do not edit it by hand. Top level ",
		"keys: 'module' is the module's Bundle-SymbolicName, or 'portal' for ",
		"portal core; 'timestamp' is when the content last changed.");

	private static final String _MANIFEST_FILE_NAME =
		"src/main/resources/META-INF/schema/manifest.json";

	private static final Pattern _timestampPattern = Pattern.compile(
		"\"timestamp\": \"([^\"]*)\"");

	private File _baseDir;
	private File _manifestFile;
	private SchemaManifestBuilder _schemaManifestBuilder;

}