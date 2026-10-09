/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node.internal.util;

import com.liferay.gradle.util.OSDetector;

import groovy.json.JsonSlurper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.logging.Logger;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

/**
 * @author Andrea Di Giorgi
 */
public class FileUtil extends com.liferay.gradle.util.FileUtil {

	public static void createBinDirLinks(Logger logger, File nodeModulesDir)
		throws IOException {

		for (File nodeModulesBinDir : _getNodeModulesBinDirs(nodeModulesDir)) {
			_createBinDirLinks(logger, nodeModulesBinDir);
		}
	}

	public static void deleteSymbolicLinks(Path dirPath) throws IOException {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath)) {

			for (Path path : directoryStream) {
				if (!Files.isSymbolicLink(path)) {
					continue;
				}

				Files.delete(path);
			}
		}
	}

	public static File[] getFiles(
		File dir, final String prefix, final String suffix) {

		return dir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return false;
					}

					String name = file.getName();

					if (!name.startsWith(prefix) || !name.endsWith(suffix)) {
						return false;
					}

					return true;
				}

			});
	}

	public static void removeBinDirLinks(Logger logger, File nodeModulesDir)
		throws IOException {

		for (File nodeModulesBinDir : _getNodeModulesBinDirs(nodeModulesDir)) {
			if (logger.isInfoEnabled()) {
				String message = "Removing binary symbolic links from {}";

				logger.info(message, nodeModulesBinDir.toPath());
			}

			deleteSymbolicLinks(nodeModulesBinDir.toPath());
		}
	}

	public static void syncDir(
		Project project, final File sourceDir, final File targetDir,
		boolean nativeSync) {

		ExecResult execResult = null;

		if (nativeSync) {
			execResult = project.exec(
				new Action<ExecSpec>() {

					@Override
					public void execute(ExecSpec execSpec) {
						if (OSDetector.isWindows()) {
							execSpec.args(
								"/MIR", "/NDL", "/NFL", "/NJH", "/NJS", "/NP",
								sourceDir.getAbsolutePath(),
								targetDir.getAbsolutePath());

							execSpec.setExecutable("robocopy");
						}
						else {
							execSpec.args(
								"--archive", "--delete",
								sourceDir.getAbsolutePath() + File.separator,
								targetDir.getAbsolutePath());

							execSpec.setExecutable("rsync");
						}

						execSpec.setIgnoreExitValue(true);
					}

				});
		}

		if ((execResult != null) && (execResult.getExitValue() == 0)) {
			return;
		}

		project.delete(targetDir);

		try {
			project.copy(
				new Action<CopySpec>() {

					@Override
					public void execute(CopySpec copySpec) {
						copySpec.from(sourceDir);
						copySpec.into(targetDir);
					}

				});
		}
		catch (RuntimeException runtimeException) {
			project.delete(targetDir);

			throw runtimeException;
		}
	}

	public static void write(File file, byte[] bytes) throws IOException {
		File dir = file.getParentFile();

		if (dir != null) {
			Files.createDirectories(dir.toPath());
		}

		Files.write(file.toPath(), bytes);
	}

	private static void _createBinDirLinks(
			Logger logger, File nodeModulesBinDir)
		throws IOException {

		JsonSlurper jsonSlurper = new JsonSlurper();

		Path nodeModulesBinDirPath = nodeModulesBinDir.toPath();
		File nodeModulesDir = nodeModulesBinDir.getParentFile();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				nodeModulesDir.toPath(), _directoryStreamFilter)) {

			for (Path dirPath : directoryStream) {
				Path packageJsonPath = dirPath.resolve("package.json");

				if (Files.notExists(packageJsonPath) &&
					Files.exists(dirPath.resolve("cli"))) {

					dirPath = dirPath.resolve("cli");

					packageJsonPath = dirPath.resolve("package.json");
				}

				if (Files.notExists(packageJsonPath)) {
					continue;
				}

				Map<String, Object> packageJsonMap =
					(Map<String, Object>)jsonSlurper.parse(
						packageJsonPath.toFile());

				Object binObject = packageJsonMap.get("bin");

				if (!(binObject instanceof Map<?, ?>)) {
					continue;
				}

				Map<String, String> binJsonMap = (Map<String, String>)binObject;

				if (binJsonMap.isEmpty()) {
					continue;
				}

				Files.createDirectories(nodeModulesBinDirPath);

				for (Map.Entry<String, String> entry : binJsonMap.entrySet()) {
					String linkFileName = entry.getKey();
					String linkTargetFileName = entry.getValue();

					Path linkPath = nodeModulesBinDirPath.resolve(linkFileName);
					Path linkTargetPath = dirPath.resolve(linkTargetFileName);

					Files.deleteIfExists(linkPath);

					Files.createSymbolicLink(linkPath, linkTargetPath);

					if (logger.isInfoEnabled()) {
						logger.info(
							"Created binary symbolic link {} which targets {}",
							linkPath, linkTargetPath);
					}
				}
			}
		}
	}

	private static Set<File> _getNodeModulesBinDirs(File nodeModulesDir)
		throws IOException {

		final Set<File> nodeModulesBinDirs = new HashSet<>();

		Files.walkFileTree(
			nodeModulesDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.equals(_NODE_MODULES_BIN_DIR_NAME)) {
						nodeModulesBinDirs.add(dirPath.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return nodeModulesBinDirs;
	}

	private static final String _NODE_MODULES_BIN_DIR_NAME = ".bin";

	private static final DirectoryStream.Filter<Path> _directoryStreamFilter =
		new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path path) throws IOException {
				return Files.isDirectory(path);
			}

		};

}