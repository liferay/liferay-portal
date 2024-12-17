/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.jakarta.ee.transformer;

import com.liferay.portal.tools.jakarta.ee.transformer.function.TextReplacerBiFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class SourceTransformer {

	public static void main(String[] args) throws IOException {
		List<List<Module>> tieredModules = _tierModules();

		System.out.println("Total tiers : " + tieredModules.size());

		int tier = 0;

		List<Module> modules = tieredModules.get(tier);

		System.out.println(
			"Focus on tier " + tier + " with " + modules.size() + " Modules");

		System.out.println("    build.jakarta.transformer.include.dirs=\\");
		System.out.println("        # Tier " + tier + " \\");

		List<String> dirs = new ArrayList<>();

		for (Module module : modules) {
			String dir = module.getId();

			dir = dir.replace(':', '/');

			dir = dir.substring(1);

			dirs.add(dir);
		}

		for (int i = 0; i < dirs.size(); i++) {
			System.out.print("        " + dirs.get(i));

			if (i < (dirs.size() - 1)) {
				System.out.println(",\\");
			}
			else {
				System.out.println();
			}
		}

		for (String dir : dirs) {
			_transformDir(dir);
		}

		if (false) {
			_transformTopLevelProjects();
		}
	}

	private static Map<String, String> _loadLibMappings() throws IOException {
		Map<String, String> libMappings = new HashMap<>();

		try (InputStream inputStream =
				SourceTransformer.class.getResourceAsStream(
					"dependencies/lib-mapping.properties");
			Reader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader)) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}

				String[] parts = line.split("=");

				libMappings.put(parts[0], parts[1]);
			}
		}

		return libMappings;
	}

	private static List<Module> _scanModules(Path modulesPath, Path subpath)
		throws IOException {

		List<Module> modules = new ArrayList<>();

		Files.walkFileTree(
			subpath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path buildGradlePath = dirPath.resolve("build.gradle");

					if (Files.exists(buildGradlePath) &&
						(Files.exists(dirPath.resolve("bnd.bnd")) ||
						 Files.exists(dirPath.resolve("src")))) {

						String relativePathString = String.valueOf(
							modulesPath.relativize(dirPath));

						Module module = new Module(
							":".concat(relativePathString.replace('/', ':')));

						String content = new String(
							Files.readAllBytes(buildGradlePath));

						Matcher matcher = _projectDependencyPattern.matcher(
							content);

						while (matcher.find()) {
							module.addDependencyId(matcher.group(1));
						}

						modules.add(module);

						Path servicesPath = dirPath.resolve(
							"src/main/resources/META-INF/services");

						if (Files.exists(servicesPath)) {
							try (DirectoryStream<Path> directoryStream =
									Files.newDirectoryStream(servicesPath)) {

								for (Path path : directoryStream) {
									String fileName = String.valueOf(
										path.getFileName());

									String newFileName =
										TextReplacerBiFunction.INSTANCE.apply(
											"SPI#" + path, fileName);

									if (!Objects.equals(
											fileName, newFileName)) {

										Files.move(
											path,
											path.resolveSibling(newFileName));
									}
								}
							}
						}

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return modules;
	}

	private static void _tierFromBottomUp(
		Map<String, Module> moduleMap, List<List<Module>> tieredModules) {

		while (!moduleMap.isEmpty()) {
			List<Module> currentTieredModules = new ArrayList<>();

			Collection<Module> modules = moduleMap.values();

			Iterator<Module> iterator = modules.iterator();

			while (iterator.hasNext()) {
				Module module = iterator.next();

				if (!module.hasDependencies()) {
					iterator.remove();

					currentTieredModules.add(module);
				}
			}

			if (currentTieredModules.isEmpty()) {
				throw new RuntimeException("Circular dependencies detected");
			}

			currentTieredModules.sort(Comparator.comparing(Module::getId));

			tieredModules.add(currentTieredModules);

			for (Module currentTieredModule : currentTieredModules) {
				for (Module module : modules) {
					module.removeDependencyId(currentTieredModule.getId());
				}
			}
		}
	}

	private static void _tierFromTopDown(
		Map<String, Module> moduleMap, List<List<Module>> tieredModules) {

		while (!moduleMap.isEmpty()) {
			List<Module> currentTieredModules = new ArrayList<>();

			Collection<Module> modules = moduleMap.values();

			Iterator<Module> iterator = modules.iterator();

			while (iterator.hasNext()) {
				Module module = iterator.next();

				if (!module.hasDependents()) {
					iterator.remove();

					currentTieredModules.add(module);
				}
			}

			if (currentTieredModules.isEmpty()) {
				throw new RuntimeException("Circular dependencies detected");
			}

			currentTieredModules.sort(Comparator.comparing(Module::getId));

			tieredModules.add(currentTieredModules);

			for (Module currentTieredModule : currentTieredModules) {
				for (Module module : modules) {
					module.removeDependentId(currentTieredModule.getId());
				}
			}
		}
	}

	private static List<List<Module>> _tierModules() throws IOException {
		List<List<Module>> tieredModules = new ArrayList<>();

		Map<String, Module> moduleMap = new HashMap<>();

		Path modulesPath = Paths.get("modules");

		for (String moduleSubfolder : _moduleSubfolders) {
			for (Module module :
					_scanModules(
						modulesPath, modulesPath.resolve(moduleSubfolder))) {

				moduleMap.put(module._id, module);
			}
		}

		for (Module module : moduleMap.values()) {
			for (String dependencyId : module.getDependencyIds()) {
				Module dependencyModule = moduleMap.get(dependencyId);

				if (dependencyModule == null) {
					System.out.println("########" + dependencyId);
				}

				dependencyModule.addDependentId(module.getId());
			}
		}

		for (Module module : moduleMap.values()) {
			module.freeze();
		}

		if (false) {
			_tierFromBottomUp(moduleMap, tieredModules);
		}

		_tierFromTopDown(moduleMap, tieredModules);

		return tieredModules;
	}

	private static void _transformBndBnd(Path bndBndPath) throws IOException {
		Path parentPath = bndBndPath.getParent();

		String content = new String(Files.readAllBytes(bndBndPath), "UTF-8");

		String newContent = content;

		if (Objects.equals(
				String.valueOf(parentPath.getFileName()),
				"oauth2-provider-rest")) {

			newContent = newContent.replace(
				"cxf-rt-rs-extension-providers*",
				"org.apache.cxf.rt.rs.extension.providers*");
			newContent = newContent.replace(
				"cxf-rt-rs-json-basic*", "org.apache.cxf.rt.rs.json.basic*");
			newContent = newContent.replace(
				"cxf-rt-rs-security-jose*",
				"org.apache.cxf.rt.rs.security.jose*");
			newContent = newContent.replace(
				"cxf-rt-rs-security-jose-jaxrs*",
				"org.apache.cxf.rt.rs.security.jose.jaxrs*");
			newContent = newContent.replace(
				"cxf-rt-rs-security-oauth2*",
				"org.apache.cxf.rt.rs.security.oauth2*");
			newContent = newContent.replace(
				"cxf-rt-security*", "org.apache.cxf.rt.security*");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"portal-osgi-web-servlet-jsp-compiler")) {

			newContent = newContent.replace(
				"javax.servlet.jsp.jstl", "jakarta.servlet.jsp.jstl");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"portal-template-freemarker")) {

			newContent = newContent.replace(
				"javax.servlet.jsp.jstl", "jakarta.servlet.jsp.jstl");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"saml-opensaml-integration")) {

			newContent = newContent.replace(
				"opensaml-messaging-impl-*", "org.opensaml.messaging.impl-*");
			newContent = newContent.replace(
				"opensaml-saml-impl-*", "org.opensaml.impl-*");
		}

		if (!Objects.equals(content, newContent)) {
			Files.write(
				bndBndPath, newContent.getBytes("UTF-8"),
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}
	}

	private static void _transformBuildGradle(Path buildGradlePath)
		throws IOException {

		String content = new String(
			Files.readAllBytes(buildGradlePath), "UTF-8");

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _jarDependencyPattern.matcher(content);

		while (matcher.find()) {
			String dependency = matcher.group();

			String moduleGroup = matcher.group(1);
			String moduleName = matcher.group(2);
			String moduleVersion = matcher.group(3);

			String libId = moduleGroup + ":" + moduleName + ":" + moduleVersion;

			String mappedLibIds = _libMappings.get(libId);

			if (mappedLibIds != null) {
				String[] mappedLibIdArray = mappedLibIds.split("[|]");

				StringBuilder dependencySB = new StringBuilder();

				for (int i = 0; i < mappedLibIdArray.length; i++) {
					String mappedLibId = mappedLibIdArray[i];

					String[] parts = mappedLibId.split(":");

					dependencySB.append(
						content.substring(matcher.start(0), matcher.start(1)));
					dependencySB.append(parts[0]);
					dependencySB.append(
						content.substring(matcher.end(1), matcher.start(2)));
					dependencySB.append(parts[1]);
					dependencySB.append(
						content.substring(matcher.end(2), matcher.start(3)));
					dependencySB.append(parts[2]);
					dependencySB.append(
						content.substring(matcher.end(3), matcher.end(0)));

					if (i < (mappedLibIdArray.length - 1)) {
						dependencySB.append('\n');
					}
				}

				dependency = dependencySB.toString();
			}

			matcher.appendReplacement(sb, dependency);
		}

		matcher.appendTail(sb);

		String newContent = sb.toString();

		Path parentPath = buildGradlePath.getParent();

		if (Objects.equals(
				String.valueOf(parentPath.getFileName()),
				"bean-portlet-cdi-extension")) {

			newContent = newContent.replace("javax.mvc-api", "jakarta.mvc-api");
			newContent = newContent.replace(
				"javax\\.mvc-api", "jakarta\\.mvc-api");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"portal-remote-jaxrs-whiteboard")) {

			newContent = newContent.replace("cxf-*", "org.apache.cxf.*");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"portal-remote-jaxrs-whiteboard-jaxb-json")) {

			newContent = newContent.replace(
				"jackson-jaxrs-base-*", "jackson-jakarta-rs-base-*");
			newContent = newContent.replace(
				"jackson-jaxrs-json-provider-*",
				"jackson-jakarta-rs-json-provider-*");
			newContent = newContent.replace(
				"jackson-module-jaxb-annotations-*",
				"jackson-module-jakarta-xmlbind-annotations-*");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"portal-remote-soap-extender-impl")) {

			newContent = newContent.replace(
				"include \"cxf-rt-frontend-jaxws-*.jar\"\n" +
					"	include \"cxf-rt-frontend-simple-*.jar\"\n" +
						"	include \"cxf-rt-ws-policy-*.jar\"",
				"include \"org.apache.cxf.rt.*.jar\"");
		}
		else if (Objects.equals(
					String.valueOf(parentPath.getFileName()),
					"portal-store-s3")) {

			newContent = newContent.replace(
				"javax.annotation.Generated;", "jakarta.annotation.Generated;");
		}

		if (!Objects.equals(content, newContent)) {
			Files.write(
				buildGradlePath, newContent.getBytes("UTF-8"),
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}
	}

	private static void _transformDir(String dir) throws IOException {
		Path dirPath = Paths.get("modules", dir);

		System.err.println("\nScanning dir " + dirPath + "\n");

		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (Objects.equals(
							String.valueOf(dirPath.getFileName()),
							"portal-tools-jakarta-ee-transformer")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					Path bndBndPath = dirPath.resolve("bnd.bnd");
					Path buildGradlePath = dirPath.resolve("build.gradle");

					if (Files.exists(bndBndPath) &&
						Files.exists(buildGradlePath)) {

						_transformBndBnd(bndBndPath);
						_transformBuildGradle(buildGradlePath);
						_transformModule(dirPath);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _transformJavaFiles(Path srcPath, boolean freeMarker)
		throws IOException {

		System.err.println("\t\u21AATransforming java");

		Files.walkFileTree(
			srcPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path fileNamePath = filePath.getFileName();

					String fileName = String.valueOf(fileNamePath);

					if (fileName.endsWith(".java")) {
						String content = new String(
							Files.readAllBytes(filePath), "UTF-8");

						String newContent =
							TextReplacerBiFunction.INSTANCE.apply(
								"JavaSource#" + filePath, content);

						if (freeMarker) {
							newContent = newContent.replace(
								"freemarker.ext.jsp.TaglibFactory",
								"freemarker.ext.jakarta.jsp.TaglibFactory");

							newContent = newContent.replace(
								"freemarker.ext.servlet.",
								"freemarker.ext.jakarta.servlet.");
						}

						if (!Objects.equals(content, newContent)) {
							Files.write(
								filePath, newContent.getBytes("UTF-8"),
								StandardOpenOption.TRUNCATE_EXISTING,
								StandardOpenOption.WRITE);
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _transformJspFiles(Path srcPath) throws IOException {
		System.err.println("\t\u21AATransforming jsp");

		Files.walkFileTree(
			srcPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path fileNamePath = filePath.getFileName();

					String fileName = String.valueOf(fileNamePath);

					if (fileName.endsWith(".jsp") ||
						fileName.endsWith(".jspf")) {

						String content = new String(
							Files.readAllBytes(filePath), "UTF-8");

						String newContent =
							TextReplacerBiFunction.INSTANCE.apply(
								"JspSource", content);

						if (!Objects.equals(content, newContent)) {
							Files.write(
								filePath, newContent.getBytes("UTF-8"),
								StandardOpenOption.TRUNCATE_EXISTING,
								StandardOpenOption.WRITE);
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _transformModule(Path moduleDirPath)
		throws IOException {

		System.err.println("\nTransforming module " + moduleDirPath);

		Path srcPath = moduleDirPath.resolve("src");

		if (Files.exists(srcPath)) {
			boolean freeMarker = false;

			String dirName = String.valueOf(moduleDirPath.getFileName());

			if (dirName.contains("freemarker")) {
				freeMarker = true;
			}

			_transformJavaFiles(srcPath, freeMarker);
			_transformJspFiles(srcPath);
		}
	}

	private static void _transformTopLevelLibs(Path path) throws IOException {
		Properties properties = new Properties();

		try (BufferedReader bufferedReader = Files.newBufferedReader(
				path, Charset.forName("UTF-8"))) {

			properties.load(bufferedReader);
		}

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String mappedLibId = _libMappings.get(
				String.valueOf(entry.getValue()));

			if (mappedLibId != null) {
				entry.setValue(mappedLibId);
			}
		}

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				path, Charset.forName("UTF-8"))) {

			List<String> names = new ArrayList<>(
				properties.stringPropertyNames());

			names.sort(null);

			for (String name : names) {
				bufferedWriter.write(name);
				bufferedWriter.write('=');
				bufferedWriter.write(properties.getProperty(name));
				bufferedWriter.newLine();
			}
		}
	}

	private static void _transformTopLevelProjects() throws IOException {
		for (String topLevelProjectFolder : _topLevelProjectFolders) {
			Path path = Paths.get(topLevelProjectFolder);

			_transformJavaFiles(path, false);
			_transformJspFiles(path);
		}

		_transformTopLevelLibs(
			Paths.get("lib/development/dependencies.properties"));
		_transformTopLevelLibs(Paths.get("lib/portal/dependencies.properties"));
	}

	private static final Pattern _jarDependencyPattern = Pattern.compile(
		"\\w+(?:\\s|\\()+group:\\s*['\"](.+)['\"],\\s*" +
			"name:\\s*['\"](.+)['\"],\\s*(?:transitive:\\s*\\w+,\\s*)?" +
				"version:\\s*['\"](.+)['\"]");
	private static final Map<String, String> _libMappings;
	private static final List<String> _moduleSubfolders = Arrays.asList(
		"apps", "core", "dxp", "test", "util");
	private static final Pattern _projectDependencyPattern = Pattern.compile(
		"project\\(\"(.*)\"\\)");
	private static final List<String> _topLevelProjectFolders = Arrays.asList(
		"portal-impl", "portal-kernel", "portal-test", "portal-web",
		"support-tomcat", "util-bridges", "util-java", "util-slf4j",
		"util-taglib");

	static {
		try {
			_libMappings = _loadLibMappings();
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

	private static class Module {

		public void addDependencyId(String dependencyId) {
			if (_freezed) {
				_workDependencyIds.add(dependencyId);
			}
			else {
				_dependencyIds.add(dependencyId);
			}
		}

		public void addDependentId(String dependentId) {
			if (_freezed) {
				_workDependentIds.add(dependentId);
			}
			else {
				_dependentIds.add(dependentId);
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof Module)) {
				return false;
			}

			Module module = (Module)object;

			return Objects.equals(_id, module._id);
		}

		public void freeze() {
			_workDependencyIds.addAll(_dependencyIds);
			_workDependentIds.addAll(_dependentIds);

			_freezed = true;
		}

		public Set<String> getDependencyIds() {
			if (_freezed) {
				return _workDependencyIds;
			}

			return _dependencyIds;
		}

		public Set<String> getDependenyIds() {
			if (_freezed) {
				return _workDependentIds;
			}

			return _dependentIds;
		}

		public String getId() {
			return _id;
		}

		public boolean hasDependencies() {
			if (_freezed) {
				return !_workDependencyIds.isEmpty();
			}

			return !_dependencyIds.isEmpty();
		}

		public boolean hasDependents() {
			if (_freezed) {
				return !_workDependentIds.isEmpty();
			}

			return !_dependentIds.isEmpty();
		}

		@Override
		public int hashCode() {
			return _id.hashCode();
		}

		public void removeDependencyId(String dependencyId) {
			if (_freezed) {
				_workDependencyIds.remove(dependencyId);
			}
			else {
				_dependencyIds.remove(dependencyId);
			}
		}

		public void removeDependentId(String dependentId) {
			if (_freezed) {
				_workDependentIds.remove(dependentId);
			}
			else {
				_dependentIds.remove(dependentId);
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("{id=");
			sb.append(_id);
			sb.append(", _dependencyIds=");
			sb.append(_workDependencyIds);
			sb.append(", _dependentIds=");
			sb.append(_workDependentIds);
			sb.append("}");

			return sb.toString();
		}

		private Module(String id) {
			_id = id;
		}

		private final Set<String> _dependencyIds = new TreeSet<>();
		private final Set<String> _dependentIds = new TreeSet<>();
		private boolean _freezed;
		private final String _id;
		private final Set<String> _workDependencyIds = new HashSet<>();
		private final Set<String> _workDependentIds = new HashSet<>();

	}

}