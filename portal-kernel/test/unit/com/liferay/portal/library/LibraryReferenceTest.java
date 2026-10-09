/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.library;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Matthew Tambara
 * @author Shuyang Zhou
 * @author Andrea Di Giorgi
 */
public class LibraryReferenceTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_portalPath = Paths.get(System.getProperty("user.dir"));

		_initGitIgnoreJars();
		_initLibJars(LIB_DIR_NAME);
		_initLibJars(LIB_PRE_DIR_NAME);
		_initModuleSourceDirs();

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		_initEclipse(documentBuilder);
		_initIntelliJ(documentBuilder);
		_initNetBeans(documentBuilder);
		_initVersionsJars(documentBuilder, _VERSIONS_FILE_NAME, _versionsJars);
		_initVersionsJars(
			documentBuilder, _VERSIONS_EXT_FILE_NAME, _versionsExtJars);
	}

	@Test
	public void testEclipseJarsInLib() {
		testNonexistentJarReferences(_eclipseJars, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testEclipseSourceDirsInModules() {
		testNonexistentModuleSourceDirReferences(
			_eclipseModuleSourceDirs, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testIntelliJLibPreModules() {
		StringBundler sb = new StringBundler();

		for (Map.Entry<String, List<String>> entry :
				_intelliJModuleSourceModules.entrySet()) {

			List<String> modules = entry.getValue();

			List<String> missingModules = new ArrayList<>();

			for (String moduleSourceDir : _moduleSourceDirs) {
				int y = moduleSourceDir.indexOf(_SRC_JAVA_DIR_NAME);

				int x = moduleSourceDir.lastIndexOf(CharPool.SLASH, y - 2);

				String moduleName = moduleSourceDir.substring(x + 1, y - 1);

				if (modules.contains(moduleName)) {
					continue;
				}

				missingModules.add(moduleName);
			}

			if (missingModules.isEmpty()) {
				continue;
			}

			sb.append(entry.getKey());
			sb.append(" is missing orderEntry elements for modules: ");
			sb.append(missingModules);
			sb.append(StringPool.NEW_LINE);
		}

		Assert.assertEquals(sb.toString(), 0, sb.index());
	}

	@Test
	public void testLibDependencyJarsInGitIgnore() {
		testMissingJarReferences(_gitIgnoreJars, _GIT_IGNORE_FILE_NAME);
	}

	@Test
	public void testLibDependencyJarsInVersionsExt() {
		for (String jar : _libDependencyJars) {
			if (_excludeJars.contains(jar)) {
				continue;
			}

			Assert.assertTrue(
				_VERSIONS_EXT_FILE_NAME + " is missing a reference to " + jar,
				_versionsExtJars.contains(jar));

			String libDependencyJarsVersion = _libDependencyJarsVersions.get(
				jar);

			Assert.assertNotNull(
				jar + " does not have a version", libDependencyJarsVersion);

			String versionsJarsVersion = _versionsJarsVersions.get(jar);

			Assert.assertEquals(
				StringBundler.concat(
					"Wrong version for ", jar, " in ", _VERSIONS_EXT_FILE_NAME),
				_normalizeVersion(libDependencyJarsVersion),
				versionsJarsVersion);
		}
	}

	@Test
	public void testLibJarsInEclipse() {
		testMissingJarReferences(_eclipseJars, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testLibJarsInNetBeans() {
		testMissingJarReferences(_netBeansJars, _NETBEANS_PROPERTIES_FILE_NAME);
	}

	@Test
	public void testLibJarsInVersions() {
		testMissingJarReferences(_versionsJars, _VERSIONS_FILE_NAME);
	}

	@Test
	public void testModulesSourceDirsInEclipse() {
		testMissingModuleSourceDirReferences(
			_eclipseModuleSourceDirs, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testModulesSourceDirsInNetBeans() {
		testMissingModuleSourceDirReferences(
			_netBeansModuleSourceDirs, _NETBEANS_XML_FILE_NAME);
	}

	@Test
	public void testNetBeansJarsInLib() {
		testNonexistentJarReferences(
			_netBeansJars, _NETBEANS_PROPERTIES_FILE_NAME);
	}

	@Test
	public void testNetBeansSourceDirsInModules() {
		testNonexistentModuleSourceDirReferences(
			_netBeansModuleSourceDirs, _NETBEANS_XML_FILE_NAME);
	}

	@Test
	public void testVersionsExtJarsInLib() {
		for (String jar : _versionsExtJars) {
			if (jar.indexOf(CharPool.EXCLAMATION) != -1) {
				continue;
			}

			Assert.assertTrue(
				_VERSIONS_EXT_FILE_NAME + " has a nonexistent reference to " +
					jar,
				_libDependencyJars.contains(jar));
		}
	}

	@Test
	public void testVersionsJarsInLib() {
		testNonexistentJarReferences(_versionsJars, _VERSIONS_FILE_NAME);
	}

	protected void testMissingJarReferences(Set<String> jars, String fileName) {
		Set<String> libJars = _libJars;

		if (fileName.equals(_GIT_IGNORE_FILE_NAME)) {
			libJars = _libDependencyJars;
		}

		for (String jar : libJars) {
			if ((fileName.equals(_ECLIPSE_FILE_NAME) ||
				 fileName.equals(_NETBEANS_PROPERTIES_FILE_NAME)) &&
				_ideExcludeJars.contains(jar)) {

				continue;
			}

			if (fileName.equals(_VERSIONS_FILE_NAME) &&
				(_excludeJars.contains(jar) ||
				 _libDependencyJars.contains(jar))) {

				continue;
			}

			String referenceJar = jar;

			if (fileName.equals(_GIT_IGNORE_FILE_NAME)) {
				referenceJar = referenceJar.substring(LIB_DIR_NAME.length());
			}

			Assert.assertTrue(
				fileName + " is missing a reference to " + referenceJar,
				jars.contains(jar));
		}
	}

	protected void testMissingModuleSourceDirReferences(
		Set<String> dirs, String fileName) {

		for (String dir : _moduleSourceDirs) {
			Assert.assertTrue(
				fileName + " is missing a reference to " + dir,
				dirs.contains(dir));
		}
	}

	protected void testNonexistentJarReferences(
		Set<String> jars, String fileName) {

		for (String jar : jars) {
			if (fileName.equals(_VERSIONS_FILE_NAME)) {
				Assert.assertFalse(
					fileName + " has a forbidden reference to " + jar,
					_libDependencyJars.contains(jar));
			}

			Assert.assertTrue(
				fileName + " has a nonexistent reference to " + jar,
				_libJars.contains(jar));
		}
	}

	protected void testNonexistentModuleSourceDirReferences(
		Set<String> dirs, String fileName) {

		for (String dir : dirs) {
			Assert.assertTrue(
				fileName + " has a nonexistent reference to " + dir,
				_moduleSourceDirs.contains(dir));
		}
	}

	protected static final String LIB_DIR_NAME = "lib";

	protected static final String LIB_PRE_DIR_NAME = "tmp/lib-pre";

	private static void _initEclipse(DocumentBuilder documentBuilder)
		throws Exception {

		Document document = documentBuilder.parse(new File(_ECLIPSE_FILE_NAME));

		NodeList nodeList = document.getElementsByTagName("classpathentry");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			NamedNodeMap namedNodeMap = node.getAttributes();

			Node kindNode = namedNodeMap.getNamedItem("kind");
			Node pathNode = namedNodeMap.getNamedItem("path");

			String kind = kindNode.getNodeValue();
			String path = pathNode.getNodeValue();

			if (kind.equals(LIB_DIR_NAME)) {
				_eclipseJars.add(path);
			}
			else if (kind.equals("src")) {
				if (path.startsWith(_MODULES_DIR_NAME + CharPool.SLASH)) {
					_eclipseModuleSourceDirs.add(path);
				}
			}
		}
	}

	private static void _initGitIgnoreJars() throws Exception {
		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new FileReader(new File(_GIT_IGNORE_FILE_NAME)))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				_gitIgnoreJars.add(LIB_DIR_NAME + line);
			}
		}
	}

	private static void _initIntelliJ(DocumentBuilder documentBuilder)
		throws Exception {

		for (String fileName : _intelliJFileNames) {
			Document document = documentBuilder.parse(new File(fileName));

			NodeList nodeList = document.getElementsByTagName("orderEntry");

			List<String> intelliJModuleSourceModules = new ArrayList<>();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element)nodeList.item(i);

				if (!Objects.equals(element.getAttribute("type"), "module")) {
					continue;
				}

				intelliJModuleSourceModules.add(
					element.getAttribute("module-name"));
			}

			_intelliJModuleSourceModules.put(
				fileName, intelliJModuleSourceModules);
		}
	}

	private static void _initLibJars(String dirName) throws Exception {
		Path libDirPath = Paths.get(dirName);

		_readLines(_excludeJars, libDirPath.resolve("versions-ignore.txt"));
		_readLines(_ideExcludeJars, libDirPath.resolve("ide-ignore.txt"));

		Files.walkFileTree(
			libDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path path = dirPath.resolve("dependencies.properties");

					if (Files.notExists(path)) {
						return FileVisitResult.CONTINUE;
					}

					Properties properties;

					try (InputStream inputStream = Files.newInputStream(path)) {
						properties = PropertiesUtil.load(
							inputStream, StringPool.UTF8);
					}

					String dirPathString = dirPath.toString();

					if (File.separatorChar != CharPool.SLASH) {
						dirPathString = StringUtil.replace(
							dirPathString, File.separatorChar, CharPool.SLASH);
					}

					dirPathString += CharPool.SLASH;

					for (String fileTitle : properties.stringPropertyNames()) {
						String jar = dirPathString + fileTitle + ".jar";

						_libDependencyJars.add(jar);
						_libJars.add(jar);

						String dependency = properties.getProperty(fileTitle);

						if (Validator.isNull(dependency)) {
							continue;
						}

						String[] dependencyArray = StringUtil.split(
							dependency, CharPool.COLON);

						if (dependencyArray.length < 3) {
							continue;
						}

						String version = dependencyArray[2];

						_libDependencyJarsVersions.put(jar, version);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					String pathString = path.toString();

					if (pathString.endsWith(".jar")) {
						if (File.separatorChar != CharPool.SLASH) {
							pathString = StringUtil.replace(
								pathString, File.separatorChar, CharPool.SLASH);
						}

						_libJars.add(pathString);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _initModuleSourceDirs() throws Exception {
		Files.walkFileTree(
			_portalPath.resolve(_MODULES_DIR_NAME),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (!dirName.endsWith("-compat") &&
						!dirName.endsWith("-test-util") &&
						Files.exists(dirPath.resolve(".lfrbuild-portal-pre"))) {

						Path sourceDirPath = dirPath.resolve(
							_SRC_JAVA_DIR_NAME);

						String sourceDir = String.valueOf(
							_portalPath.relativize(sourceDirPath));

						if (File.separatorChar != CharPool.SLASH) {
							sourceDir = StringUtil.replace(
								sourceDir, File.separatorChar, CharPool.SLASH);
						}

						_moduleSourceDirs.add(sourceDir);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _initNetBeans(DocumentBuilder documentBuilder)
		throws Exception {

		Document document = documentBuilder.parse(
			new File(_NETBEANS_XML_FILE_NAME));

		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(
				Paths.get(_NETBEANS_PROPERTIES_FILE_NAME))) {

			properties.load(inputStream);
		}

		Collections.addAll(
			_netBeansJars,
			StringUtil.split(properties.getProperty("javac.classpath"), ':'));

		NodeList nodeList = document.getElementsByTagName("source-folder");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);

			NodeList locationNodeList = element.getElementsByTagName(
				"location");

			Node locationNode = locationNodeList.item(0);

			String location = locationNode.getTextContent();

			if (!location.startsWith(_MODULES_DIR_NAME + CharPool.SLASH) ||
				!location.endsWith(CharPool.SLASH + _SRC_JAVA_DIR_NAME)) {

				continue;
			}

			_netBeansModuleSourceDirs.add(location);
		}
	}

	private static void _initVersionsJars(
			DocumentBuilder documentBuilder, String fileName, Set<String> jars)
		throws Exception {

		Document document = documentBuilder.parse(new File(fileName));

		NodeList nodeList = document.getElementsByTagName("library");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			NodeList childNodeList = node.getChildNodes();

			String jar = null;
			String version = null;

			for (int j = 0; j < childNodeList.getLength(); j++) {
				Node childNode = childNodeList.item(j);

				if (Objects.equals(childNode.getNodeName(), "file-name")) {
					jar = childNode.getTextContent();
				}
				else if (Objects.equals(childNode.getNodeName(), "version")) {
					version = childNode.getTextContent();
				}
			}

			if (Validator.isNull(jar)) {
				continue;
			}

			jars.add(jar);

			if (Validator.isNull(version)) {
				continue;
			}

			_versionsJarsVersions.put(jar, version);
		}
	}

	private static void _readLines(Set<String> lines, Path path)
		throws Exception {

		if (Files.notExists(path)) {
			return;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(path.toFile()))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (Validator.isNull(line)) {
					continue;
				}

				lines.add(line);
			}
		}
	}

	private String _normalizeVersion(String version) {
		Matcher matcher = _versionPattern.matcher(version);

		Assert.assertTrue(
			_versionPattern.pattern() + " does not match " + version,
			matcher.matches());

		String numericVersion = matcher.group(1);

		String suffix = matcher.group(4);

		if (suffix.startsWith("RELEASE")) {
			suffix = suffix.substring("RELEASE".length());
		}

		if (Validator.isNull(numericVersion)) {
			return suffix;
		}

		if (Validator.isNull(suffix)) {
			return numericVersion;
		}

		String upperCaseSuffix = StringUtil.toUpperCase(suffix);

		for (String normalizedVersionSuffix : _normalizedVersionSuffixes) {
			if (upperCaseSuffix.startsWith(normalizedVersionSuffix)) {
				return StringBundler.concat(
					numericVersion, StringPool.SPACE, upperCaseSuffix);
			}
		}

		return version;
	}

	private static final String _ECLIPSE_FILE_NAME = ".classpath";

	private static final String _GIT_IGNORE_FILE_NAME =
		LIB_DIR_NAME + "/.gitignore";

	private static final String _MODULES_DIR_NAME = "modules";

	private static final String _NETBEANS_PROPERTIES_FILE_NAME =
		"nbproject/project.properties";

	private static final String _NETBEANS_XML_FILE_NAME =
		"nbproject/project.xml";

	private static final String _SRC_JAVA_DIR_NAME = "src/main/java";

	private static final String _VERSIONS_EXT_FILE_NAME =
		LIB_DIR_NAME + "/versions-ext.xml";

	private static final String _VERSIONS_FILE_NAME =
		LIB_DIR_NAME + "/versions.xml";

	private static final Set<String> _eclipseJars = new HashSet<>();
	private static final Set<String> _eclipseModuleSourceDirs = new HashSet<>();
	private static final Set<String> _excludeJars = new HashSet<>();
	private static final Set<String> _gitIgnoreJars = new HashSet<>();
	private static final Set<String> _ideExcludeJars = new HashSet<>();
	private static final List<String> _intelliJFileNames = Arrays.asList(
		"portal-impl/portal-impl.iml", "portal-kernel/portal-kernel.iml",
		"portal-test/portal-test.iml", "portal-web/portal-web.iml",
		"util-bridges/util-bridges.iml", "util-java/util-java.iml",
		"util-slf4j/util-slf4j.iml", "util-taglib/util-taglib.iml");
	private static final Map<String, List<String>>
		_intelliJModuleSourceModules = new HashMap<>();
	private static final Set<String> _libDependencyJars = new HashSet<>();
	private static final Map<String, String> _libDependencyJarsVersions =
		new HashMap<>();
	private static final Set<String> _libJars = new HashSet<>();
	private static final Set<String> _moduleSourceDirs = new HashSet<>();
	private static final Set<String> _netBeansJars = new HashSet<>();
	private static final Set<String> _netBeansModuleSourceDirs =
		new HashSet<>();
	private static final List<String> _normalizedVersionSuffixes =
		Arrays.asList("FCS", "GA", "RC");
	private static Path _portalPath;
	private static final Pattern _versionPattern = Pattern.compile(
		"(\\d+(\\.\\d+)+)?(\\W)?(.*)");
	private static final Set<String> _versionsExtJars = new HashSet<>();
	private static final Set<String> _versionsJars = new HashSet<>();
	private static final Map<String, String> _versionsJarsVersions =
		new HashMap<>();

}