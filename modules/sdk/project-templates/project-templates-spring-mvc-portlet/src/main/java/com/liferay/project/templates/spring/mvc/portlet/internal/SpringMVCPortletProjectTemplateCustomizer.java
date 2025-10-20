/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.spring.mvc.portlet.internal;

import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.VersionUtil;

import java.io.File;

import java.nio.file.Path;

import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;

/**
 * @author Neil Griffin
 * @author Lawrence Lee
 */
public class SpringMVCPortletProjectTemplateCustomizer
	implements ProjectTemplateCustomizer {

	@Override
	public String getTemplateName() {
		return "spring-mvc-portlet";
	}

	@Override
	public void onAfterGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs, File destinationDir,
			ArchetypeGenerationResult archetypeGenerationResult)
		throws Exception {

		SpringMVCPortletProjectTemplatesArgsExt ext =
			(SpringMVCPortletProjectTemplatesArgsExt)
				projectTemplatesArgs.getProjectTemplatesArgsExt();

		Path destinationDirPath = destinationDir.toPath();

		Path projectPath = destinationDirPath.resolve(
			projectTemplatesArgs.getName());

		File buildDir = projectPath.toFile();

		File webappDir = new File(buildDir, "src/main/webapp");

		File viewsDir = new File(webappDir, "WEB-INF/views");

		String packageName = projectTemplatesArgs.getPackageName();

		File spring4JavaPkgDir = new File(
			buildDir,
			"src/main/java/" + packageName.replaceAll("[.]", "/") + "/spring4");

		String viewType = ext.getViewType();

		if (viewType.equals("jsp")) {
			FileUtil.deleteFilesByPattern(viewsDir.toPath(), _jspPattern);
		}
		else {
			FileUtil.deleteFilesByPattern(viewsDir.toPath(), _thymeleafPattern);
		}

		String framework = ext.getFramework();

		if (viewType.equals("jsp") || framework.equals("portletmvc4spring")) {
			FileUtil.deleteDir(spring4JavaPkgDir.toPath());
		}

		String liferayVersion = projectTemplatesArgs.getLiferayVersion();

		String minorVersionString = String.valueOf(
			VersionUtil.getMinorVersion(liferayVersion));

		if (VersionUtil.isLiferayQuarterlyVersion(liferayVersion)) {
			minorVersionString = "4";
		}

		File liferayPortletXML = new File(
			webappDir, "WEB-INF/liferay-display.xml");

		File liferayDisplayXML = new File(
			webappDir, "WEB-INF/liferay-portlet.xml");

		FileUtil.replaceString(
			liferayDisplayXML, "7.0", "7." + minorVersionString);
		FileUtil.replaceString(
			liferayDisplayXML, "7_0", "7_" + minorVersionString);

		FileUtil.replaceString(
			liferayPortletXML, "7.0", "7." + minorVersionString);
		FileUtil.replaceString(
			liferayPortletXML, "7_0", "7_" + minorVersionString);
	}

	@Override
	public void onBeforeGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs,
			ArchetypeGenerationRequest archetypeGenerationRequest)
		throws Exception {

		Properties properties = archetypeGenerationRequest.getProperties();

		SpringMVCPortletProjectTemplatesArgsExt
			springMVCPortletProjectTemplatesArgsExt =
				(SpringMVCPortletProjectTemplatesArgsExt)
					projectTemplatesArgs.getProjectTemplatesArgsExt();

		setProperty(
			properties, "framework",
			springMVCPortletProjectTemplatesArgsExt.getFramework());
		setProperty(
			properties, "frameworkDependencies",
			springMVCPortletProjectTemplatesArgsExt.getFrameworkDependencies());
		setProperty(
			properties, "viewType",
			springMVCPortletProjectTemplatesArgsExt.getViewType());

		boolean jakartaCompatible = VersionUtil.isJakartaCompatibleVersion(
			projectTemplatesArgs.getLiferayVersion());

		setProperty(
			properties, "jakartaCompatible", String.valueOf(jakartaCompatible));

		if (jakartaCompatible &&
			Objects.equals(
				springMVCPortletProjectTemplatesArgsExt.getFramework(),
				"springportletmvc")) {

			throw new IllegalArgumentException(
				"Framework springportletmvc is not compatible with Jakarta, " +
					"use portletmvc4spring instead");
		}
	}

	private static final Pattern _jspPattern = Pattern.compile(".*.html");
	private static final Pattern _thymeleafPattern = Pattern.compile(".*.jspx");

}