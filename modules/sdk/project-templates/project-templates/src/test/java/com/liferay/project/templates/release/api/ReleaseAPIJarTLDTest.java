/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.release.api;

import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Simon Jiang
 * @author Lawrence Lee
 */
public class ReleaseAPIJarTLDTest implements BaseReleaseAPIJarTestCase {

	public void assertTLDClasses(
			String filterString, Path classesPath, Set<String> classes)
		throws Exception {

		Map<String, Set<String>> tldClassStrings = getTLDClassPaths(
			filterString, classesPath);

		Assert.assertFalse(tldClassStrings.isEmpty());

		Set<Map.Entry<String, Set<String>>> tldEntries =
			tldClassStrings.entrySet();

		StringBuilder sb = new StringBuilder("");

		tldEntries.forEach(
			entry -> {
				Set<String> tldClasses = entry.getValue();

				Stream<String> stream = tldClasses.stream();

				List<String> missingTLDClasses = stream.filter(
					tldClass -> !classes.contains(tldClass)
				).collect(
					Collectors.toList()
				);

				if (!missingTLDClasses.isEmpty()) {
					sb.append(entry.getKey() + "\n");

					missingTLDClasses.forEach(
						tldClass -> sb.append("\t" + tldClass + "\n"));
				}
			});

		Assert.assertTrue(
			"Missing TLD classes:\n" + sb.toString(),
			Objects.equals(sb.toString(), ""));
	}

	public Map<String, Set<String>> getTLDClassPaths(
			String xpathFilterString, Path sourcePath)
		throws Exception {

		Map<String, Set<String>> results = new HashMap<>();

		Files.walkFileTree(
			sourcePath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.endsWith(".tld")) {
						results.computeIfAbsent(
							fileName, key -> new HashSet<String>());

						try (FileInputStream fileInputStream =
								new FileInputStream(path.toFile())) {

							DocumentBuilderFactory builderFactory =
								DocumentBuilderFactory.newInstance();

							DocumentBuilder documentBuilder =
								builderFactory.newDocumentBuilder();

							Document xmlDocument = documentBuilder.parse(
								fileInputStream);

							XPathFactory xPathFactory =
								XPathFactory.newInstance();

							XPath xPath = xPathFactory.newXPath();

							XPathExpression xPathExpression = xPath.compile(
								xpathFilterString);

							NodeList nodeList =
								(NodeList)xPathExpression.evaluate(
									xmlDocument, XPathConstants.NODESET);

							Set<String> paths = results.get(fileName);

							for (int i = nodeList.getLength() - 1; i >= 0;
								 i--) {

								Node childNode = nodeList.item(i);

								String textContent = childNode.getTextContent();

								if (!textContent.startsWith("com.liferay.")) {
									continue;
								}

								paths.add(textContent.replace('.', '/'));
							}
						}
						catch (Exception exception) {
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return results;
	}

	@Test
	public void testReleaseApiJarTLD() throws Exception {
		Assume.assumeNotNull(RELEASE_API_JAR_FILE);

		Path classesDirPath = getClassesDirPath(temporaryFolder);

		Set<String> classFileStrings = getPaths(classesDirPath, ".class");

		Assert.assertFalse(classFileStrings.isEmpty());

		assertTLDClasses(
			"/taglib/tag/tag-class", classesDirPath, classFileStrings);

		assertTLDClasses(
			"/taglib/tag/tei-class", classesDirPath, classFileStrings);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

}