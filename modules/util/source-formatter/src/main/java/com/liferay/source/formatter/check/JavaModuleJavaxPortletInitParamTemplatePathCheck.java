/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.File;
import java.io.FilenameFilter;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class JavaModuleJavaxPortletInitParamTemplatePathCheck
	extends BaseJavaTermCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		String content = javaTerm.getContent();

		if (javaTerm.getParentJavaClass() != null) {
			return content;
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		if (!extendedClassNames.contains("MVCPortlet") ||
			!absolutePath.contains("/src/")) {

			return content;
		}

		String moduleAbsolutePath = absolutePath.substring(
			0, absolutePath.indexOf("/src/"));

		String resourcesAbsolutePath =
			moduleAbsolutePath + "/src/main/resources";

		File dir = new File(resourcesAbsolutePath);

		if (!dir.exists()) {
			return content;
		}

		Matcher matcher = _pattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String group = matcher.group(1);

		if (!group.contains("=")) {
			return content;
		}

		String[] array = group.split("=");

		if (array.length != 2) {
			return content;
		}

		String oldTemplatePath = StringUtil.trim(array[1]);

		if (!oldTemplatePath.equals("/")) {
			return content;
		}

		String newTemplatePath = _getTemplatePath(resourcesAbsolutePath);

		return StringUtil.replaceFirst(
			content, matcher.group(1),
			"jakarta.portlet.init-param.template-path=" + newTemplatePath);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _getTemplatePath(String resourcesAbsolutePath)
		throws Exception {

		File resourcesDir = new File(resourcesAbsolutePath);

		final Set<String> jspDirNames = new TreeSet<>();

		Files.walkFileTree(
			resourcesDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					File dir = dirPath.toFile();

					File[] files = dir.listFiles(
						new FilenameFilter() {

							public boolean accept(File dir, String name) {
								String s = StringUtil.toLowerCase(name);

								return s.endsWith(".jsp");
							}

						});

					if ((files != null) && (files.length > 0)) {
						String path = SourceUtil.getAbsolutePath(dirPath);

						jspDirNames.add(path);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		if (jspDirNames.isEmpty()) {
			File dir = new File(resourcesDir, "META-INF/resources");

			if (dir.exists()) {
				return "/META-INF/resources/";
			}

			return StringPool.FORWARD_SLASH;
		}

		String templatePath = StringPool.BLANK;

		for (String jspDirName : jspDirNames) {
			String path = StringUtil.replaceFirst(
				jspDirName, resourcesAbsolutePath, StringPool.BLANK);

			path = path + StringPool.FORWARD_SLASH;

			if (Validator.isNull(templatePath)) {
				templatePath = path;
			}

			if (!path.contains(templatePath)) {
				int x = templatePath.lastIndexOf(StringPool.SLASH);

				int y = templatePath.lastIndexOf(StringPool.SLASH, x - 1);

				while (y != -1) {
					templatePath = templatePath.substring(0, y + 1);

					if (path.contains(templatePath)) {
						break;
					}

					y = templatePath.lastIndexOf(
						StringPool.FORWARD_SLASH, y - 1);
				}
			}
		}

		return templatePath;
	}

	private static final Pattern _pattern = Pattern.compile(
		"@Component\\([^)]*\"" +
			"(javax\\.portlet\\.init-param\\.template-path[^\"]*)\"[^)]*\\)");

}