/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author André de Oliveira
 * @author Hugo Huijser
 */
public class ClassUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		URL.setURLStreamHandlerFactory(
			protocol -> {
				if (protocol.equals("vfs") || protocol.equals("zip") ||
					protocol.equals("bundleresource")) {

					return new URLStreamHandler() {

						protected URLConnection openConnection(URL url) {
							return new URLConnection(url) {

								public void connect() {
									throw new UnsupportedOperationException(
										"protocol not supported");
								}

							};
						}

					};
				}

				return null;
			});
	}

	@Test
	public void testConstructor() {
		new ClassUtil();
	}

	@Test
	public void testGetClassName() {
		Assert.assertEquals(
			"java.lang.Object", ClassUtil.getClassName(new Object()));
		Assert.assertNull(ClassUtil.getClassName(null));
	}

	@Test
	public void testGetClasses() throws Exception {
		_testGetClasses("@Annotation", "Annotation");
		_testGetClasses("@AnnotationClass.Annotation", "AnnotationClass");
		_testGetClasses(" @Annotation", "Annotation");
		_testGetClasses("@Annotation({A}", "A", "Annotation");
		_testGetClasses("@Annotation({A})", "A", "Annotation");
		_testGetClasses("@Annotation({A\n}))", "A", "Annotation");
		_testGetClasses("@Annotation({A.class})", "A", "Annotation");
		_testGetClasses("@Annotation({A.class\n})", "A", "Annotation");
		_testGetClasses(
			"@Annotation({A.class,\nB.class\n})", "A", "B", "Annotation");
		_testGetClasses(
			"@Annotation({A.class,B.class})", "A", "B", "Annotation");
		_testGetClasses(
			"@Annotation({A.class,B.class,C.class})", "A", "B", "C",
			"Annotation");
		_testGetClasses(
			"@AnnotationClass.Annotation({A.class})", "A", "AnnotationClass");
		_testGetClasses(
			"@AnnotationClass.Annotation({A.class,B.class})", "A", "B",
			"AnnotationClass");
		_testGetClasses(
			"@AnnotationClass.Annotation({A.class,B.class,C.class})", "A", "B",
			"C", "AnnotationClass");
		_testGetClasses("class A", "A");
		_testGetClasses("class \u00C0", "\u00C0");
		_testGetClasses("class 0");
		_testGetClasses("class \n");
		_testGetClasses("class A.B.C", "A", "C");
		_testGetClasses("enum A", "A");
		_testGetClasses("interface A", "A");
		_testGetClasses("@interface A", "A");
		_testGetClasses(" @%");
		_testGetClasses("A");
	}

	@Test
	public void testGetClassesWithFile() throws IOException {
		_testGetClassesWithFile("TestClass.java", null);
		_testGetClassesWithFile(
			"TestClass.java", "@Annotation({A.class,B.class} TestClass", "A",
			"B", "Annotation");
		_testGetClassesWithFile(
			"TestClass.txt", "@Annotation({A.class,B.class} TestClass", "A",
			"B", "Annotation", "TestClass");
	}

	@Test
	public void testIsSubclass() {
		Assert.assertTrue(
			"ArrayList should be considered sub class of itself",
			ClassUtil.isSubclass(ArrayList.class, ArrayList.class));
		Assert.assertFalse(
			"ArrayList should not be sub class of null",
			ClassUtil.isSubclass(ArrayList.class, (Class<?>)null));
		Assert.assertFalse(
			"null should not be sub class of any class or interface",
			ClassUtil.isSubclass(null, ArrayList.class));
		Assert.assertTrue(
			"ArrayList should be sub class of abstract class AbstractList",
			ClassUtil.isSubclass(ArrayList.class, AbstractList.class));
		Assert.assertTrue(
			"ArrayList should be sub class of interface List",
			ClassUtil.isSubclass(ArrayList.class, List.class));
		Assert.assertFalse(
			"ArrayList should be sub class of interface Set",
			ClassUtil.isSubclass(ArrayList.class, Set.class));

		Assert.assertTrue(
			"ArrayList should be considered sub class of itself",
			ClassUtil.isSubclass(ArrayList.class, ArrayList.class.getName()));
		Assert.assertFalse(
			"ArrayList should not be sub class of null",
			ClassUtil.isSubclass(ArrayList.class, (String)null));
		Assert.assertFalse(
			"null should not be sub class of any class or interface",
			ClassUtil.isSubclass(null, ArrayList.class.getName()));
		Assert.assertTrue(
			"ArrayList should be sub class of abstract class AbstractList",
			ClassUtil.isSubclass(
				ArrayList.class, AbstractList.class.getName()));
		Assert.assertTrue(
			"ArrayList should be sub class of interface List",
			ClassUtil.isSubclass(ArrayList.class, List.class.getName()));
		Assert.assertFalse(
			"ArrayList should be sub class of interface Set",
			ClassUtil.isSubclass(ArrayList.class, Set.class.getName()));
	}

	private void _testGetClasses(String content, String... expectedClasses)
		throws Exception {

		Set<String> expectedClassNames = new HashSet<>();

		Collections.addAll(expectedClassNames, expectedClasses);

		Set<String> actualClassNames = ClassUtil.getClasses(
			new StringReader(content), null);

		Assert.assertEquals(expectedClassNames, actualClassNames);
	}

	private void _testGetClassesWithFile(
			String fileName, String fileContent, String... expectedClasses)
		throws IOException {

		Path tempDirPath = Files.createTempDirectory(
			ClassUtilTest.class.getName());

		File file = new File(tempDirPath.toFile(), fileName);

		if (fileContent == null) {
			file.createNewFile();
		}
		else {
			Files.write(file.toPath(), Collections.singleton(fileContent));
		}

		Set<String> expectedClassNames = new HashSet<>();

		if (expectedClasses != null) {
			Collections.addAll(expectedClassNames, expectedClasses);
		}

		Set<String> actualClassNames = ClassUtil.getClasses(file);

		Assert.assertEquals(expectedClassNames, actualClassNames);
	}

}