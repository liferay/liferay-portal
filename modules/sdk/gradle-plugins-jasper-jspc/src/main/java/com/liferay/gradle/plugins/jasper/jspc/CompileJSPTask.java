/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.jasper.jspc;

import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import java.io.File;

import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class CompileJSPTask extends DefaultTask {

	@TaskAction
	public void compileJSP() {
		FileCollection compileJspClasspath = getCompileJspClasspath();
		FileCollection jspCClasspath = getJspCClasspath();
		Logger logger = getLogger();

		if ((compileJspClasspath == null) || compileJspClasspath.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("Compiling JSP with standard class path");
			}

			CompileJSPUtil.compileJSP(
				_getCompilerClassName(), _getCompleteArgs(),
				jspCClasspath.getAsPath());

			return;
		}

		if (logger.isInfoEnabled()) {
			logger.info("Compiling JSP with custom class path");
		}

		Set<File> files = compileJspClasspath.getFiles();

		Stream<File> stream = files.stream();

		URL[] urls = stream.map(
			File::toURI
		).map(
			uri -> {
				try {
					return uri.toURL();
				}
				catch (MalformedURLException malformedURLException) {
					throw new GradleException(
						malformedURLException.getMessage(),
						malformedURLException);
				}
			}
		).toArray(
			URL[]::new
		);

		try (URLClassLoader urlClassLoader = new URLClassLoader(urls, null)) {
			Class<?> compileJSPUtilClass = Class.forName(
				CompileJSPUtil.class.getName(), true, urlClassLoader);

			Method compileJSPMethod = compileJSPUtilClass.getMethod(
				"compileJSP", String.class, String[].class, String.class);

			compileJSPMethod.invoke(
				null, _getCompilerClassName(), _getCompleteArgs(),
				jspCClasspath.getAsPath());
		}
		catch (Exception exception) {
			throw new GradleException(exception.getMessage(), exception);
		}
	}

	@Classpath
	@Optional
	public FileCollection getCompileJspClasspath() {
		return _compileJspClasspath;
	}

	@OutputDirectory
	public File getDestinationDir() {
		return GradleUtil.toFile(getProject(), _destinationDir);
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	public FileCollection getJSPFiles() {
		Project project = getProject();

		Map<String, Object> args = new HashMap<>();

		args.put("dir", getWebAppDir());

		List<String> excludes = new ArrayList<>(2);

		excludes.add("**/custom_jsps/**/*");
		excludes.add("**/dependencies/**/*");

		args.put("excludes", excludes);

		args.put("include", "**/*.jsp");

		return project.fileTree(args);
	}

	@Classpath
	public FileCollection getJspCClasspath() {
		return _jspCClasspath;
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getWebAppDir() {
		return GradleUtil.toFile(getProject(), _webAppDir);
	}

	@Input
	public boolean isPoolingEnabled() {
		return _poolingEnabled;
	}

	public void setCompileJspClasspath(FileCollection compileJspClasspath) {
		_compileJspClasspath = compileJspClasspath;
	}

	public void setDestinationDir(Object destinationDir) {
		_destinationDir = destinationDir;
	}

	public void setJspCClasspath(FileCollection jspCClasspath) {
		_jspCClasspath = jspCClasspath;
	}

	public void setPoolingEnabled(boolean poolingEnabled) {
		_poolingEnabled = poolingEnabled;
	}

	public void setWebAppDir(Object webAppDir) {
		_webAppDir = webAppDir;
	}

	private String _getCompilerClassName() {
		JavaVersion javaVersion = JavaVersion.current();

		if (!Boolean.getBoolean("build.jakarta.transformer.enabled") ||
			(javaVersion.compareTo(JavaVersion.VERSION_17) < 0)) {

			return null;
		}

		String dirNames = System.getProperty(
			"build.jakarta.transformer.include.dirs");

		if (Validator.isNull(dirNames)) {
			return null;
		}

		Project project = getProject();

		File portalModulesDir = project.getRootDir();

		Path portalModulesPath = portalModulesDir.toPath();

		File projectDir = project.getProjectDir();

		Path projectPath = projectDir.toPath();

		for (String dirName : dirNames.split(",")) {
			if (projectPath.startsWith(portalModulesPath.resolve(dirName))) {
				return JakartaTransformerJDTCompiler.class.getName();
			}
		}

		return null;
	}

	private String[] _getCompleteArgs() {
		return new String[] {
			"-d", FileUtil.getAbsolutePath(getDestinationDir()),
			"-no-strictQuoteEscaping", "-poolingEnabled",
			String.valueOf(isPoolingEnabled()), "-webapp",
			FileUtil.getAbsolutePath(getWebAppDir())
		};
	}

	private FileCollection _compileJspClasspath;
	private Object _destinationDir;
	private FileCollection _jspCClasspath;
	private boolean _poolingEnabled;
	private Object _webAppDir;

}