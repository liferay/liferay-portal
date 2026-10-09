/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.upgrade.table.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ArgumentsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 */
public class UpgradeTableBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String baseDirName = GetterUtil.getString(
			arguments.get("upgrade.base.dir"),
			UpgradeTableBuilderArgs.BASE_DIR_NAME);
		boolean osgiModule = GetterUtil.getBoolean(
			arguments.get("upgrade.osgi.module"),
			UpgradeTableBuilderArgs.OSGI_MODULE);
		String releaseInfoVersion = arguments.get(
			"upgrade.release.info.version");
		String upgradeTableDirName = arguments.get("upgrade.table.dir");

		try {
			new UpgradeTableBuilder(
				baseDirName, osgiModule, releaseInfoVersion,
				upgradeTableDirName);
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public UpgradeTableBuilder(
			String baseDirName, boolean osgiModule, String releaseInfoVersion,
			String upgradeTableDirName)
		throws Exception {

		_baseDirName = baseDirName;

		_osgiModule = osgiModule;

		if (_osgiModule) {
			_releaseInfoVersion = null;
		}
		else {
			_releaseInfoVersion = releaseInfoVersion;
		}

		_upgradeTableDirName = upgradeTableDirName;

		FileSystem fileSystem = FileSystems.getDefault();

		final PathMatcher pathMatcher = fileSystem.getPathMatcher(
			"glob:**/upgrade/v**/util/*Table.java");

		final AtomicBoolean tableFilesFound = new AtomicBoolean();

		Files.walkFileTree(
			Paths.get(baseDirName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (pathMatcher.matches(path)) {
						tableFilesFound.set(true);

						_buildUpgradeTable(path);
					}

					return FileVisitResult.CONTINUE;
				}

			});

		if (!tableFilesFound.get()) {
			System.out.println(
				"No files matching the pattern \"" + _baseDirName +
					"/**/upgrade/v**/util/*Table.java\" have been found.");
		}
	}

	private void _buildUpgradeTable(Path path) throws IOException {
		String pathString = path.toString();

		pathString = StringUtil.replace(pathString, '\\', '/');

		String upgradeFileVersion = pathString.replaceFirst(
			".*/upgrade/v(.+)/util.*", "$1");

		upgradeFileVersion = StringUtil.replace(upgradeFileVersion, '_', '.');

		if (upgradeFileVersion.contains("to")) {
			upgradeFileVersion = upgradeFileVersion.replaceFirst(
				".+\\.to\\.(.+)", "$1");
		}

		String fileName = String.valueOf(path.getFileName());

		String tableName = fileName.substring(0, fileName.length() - 10);

		String upgradeFileName = tableName + "ModelImpl.java";

		Path upgradeFilePath = Paths.get(
			_upgradeTableDirName, upgradeFileVersion, upgradeFileName);

		if (Files.notExists(upgradeFilePath)) {
			if (!_isRelevantUpgradePackage(upgradeFileVersion)) {
				return;
			}

			upgradeFilePath = _getUpgradeFilePath(upgradeFileName);

			if (upgradeFilePath == null) {
				throw new IOException(
					StringBundler.concat(
						"Verify the file name for ", fileName, " because ",
						upgradeFileName, " does not exist"));
			}
		}

		String content = _read(path);

		String packagePath = _getPackagePath(content);

		if (packagePath == null) {
			throw new IOException("Provide a package in " + fileName);
		}

		String className = fileName.substring(0, fileName.length() - 5);

		String upgradeFileContent = _read(upgradeFilePath);

		if (_getVersion() >= 74) {
			content = _getContent(
				packagePath, className, upgradeFileContent,
				_getAuthor(content));
		}
		else {
			content = _getOldContent(
				packagePath, className, upgradeFileContent, _getAuthor(content),
				_getAddIndexes(
					_getIndexesFilePath(upgradeFileVersion), tableName));
		}

		Files.write(path, content.getBytes(StandardCharsets.UTF_8));
	}

	private List<Path> _findFiles(
			String baseDirName, String pattern, final int limit)
		throws IOException {

		final List<Path> paths = new ArrayList<>();

		FileSystem fileSystem = FileSystems.getDefault();

		final PathMatcher pathMatcher = fileSystem.getPathMatcher(
			"glob:" + pattern);

		Files.walkFileTree(
			Paths.get(baseDirName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					if (pathMatcher.matches(filePath)) {
						paths.add(filePath);

						if (paths.size() == limit) {
							return FileVisitResult.TERMINATE;
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return paths;
	}

	private String[] _getAddIndexes(Path indexesFilePath, String tableName)
		throws IOException {

		List<String> addIndexes = new ArrayList<>();

		try (BufferedReader bufferedReader = Files.newBufferedReader(
				indexesFilePath, StandardCharsets.UTF_8)) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (!line.contains(" on " + tableName + " (") &&
					!line.contains(" on " + tableName + "_ (")) {

					continue;
				}

				String sql = line.trim();

				if (sql.endsWith(";")) {
					sql = sql.substring(0, sql.length() - 1);
				}

				addIndexes.add(sql);
			}
		}

		return addIndexes.toArray(new String[0]);
	}

	private String _getAuthor(String content) {
		int x = content.indexOf("* @author ");

		if (x != -1) {
			int y = content.indexOf("*", x + 1);

			if (y != -1) {
				return StringUtil.trim(content.substring(x + 10, y));
			}
		}

		return _AUTHOR;
	}

	private String _getContent(
			String packagePath, String className, String content, String author)
		throws IOException {

		StringBundler sb = new StringBundler(34);

		sb.append(_getCopyright());
		sb.append("\n\npackage ");
		sb.append(packagePath);
		sb.append(";\n\n");
		sb.append(
			"import com.liferay.portal.kernel.upgrade.UpgradeProcess;\n\n");
		sb.append("/**\n");
		sb.append(" * @author ");
		sb.append(author);
		sb.append("\n");
		sb.append(" * @generated\n");
		sb.append(" * @see ");
		sb.append(UpgradeTableBuilder.class.getName());
		sb.append("\n");
		sb.append(" */\n");
		sb.append("public class ");
		sb.append(className);
		sb.append(" {\n\n");
		sb.append("\tpublic static UpgradeProcess create() {\n");
		sb.append("\t\treturn new UpgradeProcess() {\n\n");
		sb.append("\t\t\t@Override\n");
		sb.append("\t\t\tprotected void doUpgrade() throws Exception {\n");
		sb.append("\t\t\t\tif (!hasTable(_TABLE_NAME)) {\n");
		sb.append("\t\t\t\t\trunSQL(_TABLE_SQL_CREATE);\n");
		sb.append("\t\t\t\t}\n");
		sb.append("\t\t\t}\n\n");
		sb.append("\t\t};\n");
		sb.append("\t}\n\n");
		sb.append("\tprivate static final String _TABLE_NAME =");

		int x = content.indexOf("public static final String TABLE_NAME =");

		if (x == -1) {
			x = content.indexOf("public static String TABLE_NAME =");
		}

		sb.append(
			content.substring(
				content.indexOf("=", x) + 1, content.indexOf(";", x)));

		sb.append(";\n\n");
		sb.append("\tprivate static final String _TABLE_SQL_CREATE =");

		int y = content.indexOf(
			"public static final String TABLE_SQL_CREATE =");

		if (y == -1) {
			y = content.lastIndexOf("public static String TABLE_SQL_CREATE =");
		}

		sb.append(
			content.substring(
				content.indexOf("=", y) + 1, content.indexOf(";", y)));

		sb.append(";\n\n");
		sb.append("}");

		return sb.toString();
	}

	private String _getCopyright() throws IOException {
		Path path = Paths.get(_baseDirName);

		path = path.toAbsolutePath();

		while (path != null) {
			Path copyrightFilePath = path.resolve("copyright.txt");

			if (Files.exists(copyrightFilePath)) {
				return _read(copyrightFilePath);
			}

			path = path.getParent();
		}

		return null;
	}

	private Path _getIndexesFilePath(String upgradeFileVersion)
		throws IOException {

		Path indexesFilePath = null;

		if (_osgiModule) {
			List<Path> paths = _findFiles(
				_baseDirName, "**/sql/indexes.sql", 1);

			if (!paths.isEmpty()) {
				indexesFilePath = paths.get(0);
			}
		}
		else {
			indexesFilePath = Paths.get(
				_upgradeTableDirName, upgradeFileVersion, "indexes.sql");

			if (Files.notExists(indexesFilePath)) {
				indexesFilePath = Paths.get(_baseDirName, "../sql/indexes.sql");
			}
		}

		return indexesFilePath;
	}

	private String _getOldContent(
			String packagePath, String className, String content, String author,
			String[] addIndexes)
		throws IOException {

		int x = content.indexOf("public static final String TABLE_NAME =");

		if (x == -1) {
			x = content.indexOf("public static String TABLE_NAME =");
		}

		int y = content.indexOf("public static final String TABLE_SQL_DROP =");

		if (y == -1) {
			y = content.indexOf("public static String TABLE_SQL_DROP =");
		}

		y = content.indexOf(";", y);

		content = content.substring(x, y + 1);

		content = StringUtil.removeSubstring(content, "\t");
		content = StringUtil.replace(content, "{ \"", "{\"");
		content = StringUtil.replace(content, "new Integer(Types.", "Types.");
		content = StringUtil.replace(content, ") }", "}");
		content = StringUtil.replace(content, " }", "}");

		while (content.contains("\n\n")) {
			content = StringUtil.replace(content, "\n\n", "\n");
		}

		StringBundler sb = new StringBundler();

		sb.append(_getCopyright());

		sb.append("\n\npackage ");
		sb.append(packagePath);
		sb.append(";\n\n");
		sb.append("import java.sql.Types;\n\n");

		if (content.contains("TABLE_COLUMNS_MAP")) {
			sb.append("import java.util.HashMap;\n");
			sb.append("import java.util.Map;\n\n");
		}

		sb.append("/**\n");
		sb.append(" * @author\t  ");
		sb.append(author);
		sb.append("\n");
		sb.append(" * @generated\n");
		sb.append(" */\n");
		sb.append("public class ");
		sb.append(className);
		sb.append(" {\n\n");

		String[] lines = content.split("\\n");

		for (String line : lines) {
			if (line.startsWith("public static") || line.startsWith("};")) {
				sb.append("\t");
			}
			else if (line.startsWith("{\"")) {
				sb.append("\t\t");
			}

			sb.append(line);
			sb.append("\n");

			if (!line.endsWith(";")) {
				continue;
			}

			sb.append("\n");
		}

		sb.append("\tpublic static final String[] TABLE_SQL_ADD_INDEXES = {\n");

		for (int i = 0; i < addIndexes.length; i++) {
			String addIndex = addIndexes[i];

			sb.append("\t\t\"");
			sb.append(addIndex);
			sb.append("\"");

			if ((i + 1) < addIndexes.length) {
				sb.append(",");
			}

			sb.append("\n");
		}

		sb.append("\t};\n\n");
		sb.append("}");

		return sb.toString();
	}

	private String _getPackagePath(String content) {
		Matcher matcher = _packagePathPattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private String _getSchemaVersion() throws IOException {
		Properties properties = new Properties();

		Path path = Paths.get(_baseDirName, "bnd.bnd");

		try (InputStream inputStream = Files.newInputStream(path)) {
			properties.load(inputStream);
		}

		return properties.getProperty("Liferay-Require-SchemaVersion");
	}

	private Path _getUpgradeFilePath(String fileName) throws IOException {
		List<Path> paths = _findFiles(_baseDirName, "**/" + fileName, 1);

		if (paths.isEmpty()) {
			return null;
		}

		return paths.get(0);
	}

	private int _getVersion() throws IOException {
		Path path = null;

		if (_osgiModule) {
			path = Paths.get(_baseDirName, "service.xml");
		}
		else {
			Path[] paths = new Path[1];

			Files.walkFileTree(
				Paths.get(_baseDirName),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (path.endsWith("service.xml")) {
							paths[0] = path;

							return FileVisitResult.TERMINATE;
						}

						return FileVisitResult.CONTINUE;
					}

				});

			path = paths[0];
		}

		String content = _read(path);

		int index = content.indexOf("http://www.liferay.com/dtd/");

		String url = content.substring(
			content.indexOf(index), content.indexOf("\">", index));

		Matcher matcher = _dtdVersionPattern.matcher(url);

		if (matcher.matches()) {
			String version = StringUtil.removeSubstring(matcher.group(1), "_");

			return Integer.valueOf(version.substring(0, 2));
		}

		throw new IOException("Unable to get Liferay version from " + path);
	}

	private boolean _isRelevantUpgradePackage(String upgradeFileVersion)
		throws IOException {

		String currentVersion = null;

		if (_osgiModule) {
			currentVersion = _getSchemaVersion();
		}
		else {
			currentVersion = _releaseInfoVersion.substring(0, 3);
		}

		return upgradeFileVersion.startsWith(currentVersion);
	}

	private String _read(Path path) throws IOException {
		String s = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

		return StringUtil.replace(s, "\r\n", "\n");
	}

	private static final String _AUTHOR = "Brian Wing Shun Chan";

	private static final Pattern _dtdVersionPattern = Pattern.compile(
		".*service-builder_([^\\.]+)\\.dtd");
	private static final Pattern _packagePathPattern = Pattern.compile(
		"package (.+?);");

	private final String _baseDirName;
	private final boolean _osgiModule;
	private final String _releaseInfoVersion;
	private final String _upgradeTableDirName;

}