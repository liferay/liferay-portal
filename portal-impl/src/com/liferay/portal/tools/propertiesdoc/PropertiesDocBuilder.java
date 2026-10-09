/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.propertiesdoc;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.freemarker.FreeMarkerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.util.FileImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jesse Rao
 * @author James Hinkey
 */
public class PropertiesDocBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		try {
			new PropertiesDocBuilder(arguments);
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public PropertiesDocBuilder(Map<String, String> arguments)
		throws IOException {

		String propertiesFileName = GetterUtil.getString(
			arguments.get("properties.file"));

		System.out.println("Converting " + propertiesFileName + " to HTML");

		File propertiesFile = new File(propertiesFileName);

		List<PropertiesSection> propertiesSections = getPropertiesSections(
			propertiesFile);

		if (propertiesSections == null) {
			return;
		}

		int pos = propertiesFileName.lastIndexOf(StringPool.SLASH);

		if (pos != -1) {
			propertiesFileName = propertiesFileName.substring(pos + 1);
		}

		Map<String, Object> context = HashMapBuilder.<String, Object>put(
			"pageTitle", GetterUtil.getString(arguments.get("properties.title"))
		).put(
			"propertiesFileName", propertiesFileName
		).put(
			"sections", propertiesSections
		).put(
			"toc", GetterUtil.getBoolean(arguments.get("properties.toc"))
		).build();

		try {
			StringBundler sb = new StringBundler(4);

			String propertiesDestDirName = GetterUtil.getString(
				arguments.get("properties.dest.dir"));

			sb.append(propertiesDestDirName);

			sb.append(StringPool.SLASH);
			sb.append(propertiesFileName);
			sb.append(".html");

			String propertiesHTMLFileName = sb.toString();

			File propertiesHTMLFile = new File(propertiesHTMLFileName);

			System.out.println("Writing " + propertiesHTMLFile);

			Charset charset = Charset.forName("UTF-8");

			Writer writer = new OutputStreamWriter(
				new FileOutputStream(propertiesHTMLFile), charset.newEncoder());

			try {
				FreeMarkerUtil.process(
					"com/liferay/portal/tools/propertiesdoc/dependencies" +
						"/properties.ftl",
					context, writer);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			writer.flush();
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	protected void addPropertyComment(
		List<PropertyComment> propertyComments, String comment) {

		if (Validator.isNotNull(comment)) {
			PropertyComment propertyComment = new PropertyComment(comment);

			propertyComments.add(propertyComment);
		}
	}

	protected List<String> extractComments(String[] lines) {
		List<String> comments = new ArrayList<>();

		StringBundler sb = new StringBundler();

		for (String line : lines) {
			String trimmedLine = line.trim();

			if (trimmedLine.startsWith("## ")) {
				trimmedLine = trimmedLine.substring(2);

				sb.append(trimmedLine.trim());
			}

			if ((trimmedLine.length() >= 3) || (sb.index() == 0)) {
				continue;
			}

			comments.add(sb.toString());

			sb = new StringBundler();
		}

		return comments;
	}

	protected String extractDefaultProperties(String[] lines) {
		StringBundler sb = new StringBundler();

		boolean previousLineIsDefaultProperty = false;

		for (String line : lines) {
			if (!previousLineIsDefaultProperty) {
				if (!line.startsWith("#") && !line.startsWith(INDENT + "#")) {
					previousLineIsDefaultProperty = true;

					sb.append(line);
					sb.append(StringPool.NEW_LINE);
				}
			}
			else {
				if (line.startsWith("#") || line.startsWith(INDENT + "#")) {
					previousLineIsDefaultProperty = false;

					continue;
				}

				sb.append(line);
				sb.append(StringPool.NEW_LINE);
			}
		}

		return sb.toString();
	}

	protected String extractExampleProperties(String[] lines) {
		StringBundler sb = new StringBundler();

		boolean previousLineIsExample = false;

		for (String line : lines) {
			String trimmedLine = line.trim();

			if (!previousLineIsExample) {
				if (line.startsWith(INDENT + "# ") || trimmedLine.equals("#")) {
					continue;
				}

				if (line.startsWith(INDENT + "#")) {
					previousLineIsExample = true;

					String exampleProperty =
						StringUtil.replaceFirst(line, '#', StringPool.BLANK) +
							StringPool.NEW_LINE;

					sb.append(exampleProperty);
				}
			}
			else {
				if (!trimmedLine.startsWith("#")) {
					previousLineIsExample = false;

					continue;
				}

				String exampleProperty =
					line.replaceFirst("#", StringPool.BLANK) +
						StringPool.NEW_LINE;

				sb.append(exampleProperty);
			}
		}

		return sb.toString();
	}

	protected List<PropertyComment> extractPropertyComments(String[] lines) {
		List<PropertyComment> propertyComments = new ArrayList<>();

		StringBundler sb = new StringBundler();

		boolean previousLineIsPreformatted = false;

		for (String line : lines) {
			line = StringUtil.trimTrailing(line);

			if (line.startsWith(DOUBLE_INDENT + "#")) {
				break;
			}

			String trimmedLine = line.trim();

			if (trimmedLine.startsWith("# " + INDENT)) {
				if (previousLineIsPreformatted) {
					sb.append(
						StringUtil.replaceFirst(
							trimmedLine, '#', StringPool.BLANK));
				}
				else {
					addPropertyComment(propertyComments, sb.toString());

					sb = new StringBundler();

					sb.append(
						StringUtil.replaceFirst(
							trimmedLine, '#', StringPool.BLANK));
				}

				sb.append(StringPool.NEW_LINE);

				previousLineIsPreformatted = true;
			}
			else if (trimmedLine.startsWith("# ")) {
				if (previousLineIsPreformatted) {
					addPropertyComment(propertyComments, sb.toString());

					sb = new StringBundler();

					trimmedLine = StringUtil.replaceFirst(
						trimmedLine, '#', StringPool.BLANK);

					sb.append(trimmedLine.trim());
				}
				else {
					if (sb.length() > 0) {
						sb.append(StringPool.SPACE);
					}

					line = StringUtil.replaceFirst(line, '#', StringPool.BLANK);

					sb.append(line.trim());
				}

				sb.append(StringPool.NEW_LINE);

				previousLineIsPreformatted = false;
			}
			else if (trimmedLine.startsWith("#") &&
					 (trimmedLine.length() < 2)) {

				addPropertyComment(propertyComments, sb.toString());

				sb = new StringBundler();
			}
			else {
				addPropertyComment(propertyComments, sb.toString());

				break;
			}
		}

		return propertyComments;
	}

	protected String extractTitle(String[] lines) {
		if ((lines == null) || (lines.length <= 1)) {
			return null;
		}

		String title = lines[1];

		title = StringUtil.replaceFirst(title, "##", StringPool.BLANK);

		return title.trim();
	}

	protected int getLineCount(String sectionString) {
		String[] lines = sectionString.split("\r\n|\r|\n");

		return lines.length;
	}

	protected List<PropertiesSection> getPropertiesSections(File propertiesFile)
		throws IOException {

		String content = _fileImpl.read(propertiesFile);

		String[] sections = content.split("\n\n");

		List<PropertiesSection> propertiesSections = new ArrayList<>(
			sections.length);

		for (String section : sections) {
			section = StringUtil.trimLeading(section, CharPool.SPACE);

			PropertiesSection propertiesSection = new PropertiesSection(
				section);

			String[] lines = section.split(StringPool.NEW_LINE);

			if (section.startsWith("##")) {
				int lineCount = getLineCount(section);

				if (lineCount == 3) {
					propertiesSection.setTitle(extractTitle(lines));

					propertiesSections.add(propertiesSection);
				}
				else if (lineCount > 3) {
					propertiesSection.setComments(extractComments(lines));

					propertiesSections.add(propertiesSection);
				}
				else {
					System.out.println(
						"Properties section should consist of 3 or more " +
							"lines:\n##\n## Comments\n##");

					return null;
				}
			}
			else {
				propertiesSection.setDefaultProperties(
					extractDefaultProperties(lines));
				propertiesSection.setExampleProperties(
					extractExampleProperties(lines));
				propertiesSection.setPropertyComments(
					extractPropertyComments(lines));

				propertiesSections.add(propertiesSection);
			}
		}

		return propertiesSections;
	}

	protected static final String DOUBLE_INDENT =
		PropertiesDocBuilder.INDENT + PropertiesDocBuilder.INDENT;

	protected static final String INDENT = StringPool.FOUR_SPACES;

	private static final Log _log = LogFactoryUtil.getLog(
		PropertiesDocBuilder.class);

	private static final FileImpl _fileImpl = FileImpl.getInstance();

}