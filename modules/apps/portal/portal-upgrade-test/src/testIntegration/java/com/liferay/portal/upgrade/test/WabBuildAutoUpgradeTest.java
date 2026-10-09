/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.net.URL;

import java.sql.Types;

import java.util.jar.JarOutputStream;

import org.junit.After;
import org.junit.Before;

import org.osgi.framework.Constants;

/**
 * @author Preston Crary
 */
public class WabBuildAutoUpgradeTest extends BaseBuildAutoUpgradeTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_warFiles[0] = _createWabBundleFile(
			new Object[][] {{"id_", Types.BIGINT}, {"data_", Types.VARCHAR}},
			1);

		_warFiles[1] = _createWabBundleFile(
			new Object[][] {
				{"id_", Types.BIGINT}, {"data_", Types.VARCHAR},
				{"data2", Types.VARCHAR}
			},
			2);

		_warFiles[2] = _createWabBundleFile(
			new Object[][] {{"id_", Types.BIGINT}, {"data2", Types.VARCHAR}},
			3);

		_warFiles[3] = _createWabBundleFile(
			new Object[][] {{"id_", Types.BIGINT}, {"data_", Types.VARCHAR}},
			4);

		for (int i = 0; i < _warURLs.length; i++) {
			URI uri = _warFiles[i].toURI();

			URL url = uri.toURL();

			_warURLs[i] = new URL(
				"webbundle", null,
				StringBundler.concat(
					url.getPath(), "?", Constants.BUNDLE_SYMBOLICNAME,
					"=BuildAutoUpgradeTest&Web-ContextPath=",
					"/BuildAutoUpgradeTest&protocol=file"));
		}
	}

	@After
	@Override
	public void tearDown() throws Throwable {
		super.tearDown();

		for (File file : _warFiles) {
			if (file == null) {
				continue;
			}

			file.delete();
		}
	}

	@Override
	protected InputStream getBundleInputStream(int version) throws IOException {
		return _warURLs[version - 1].openStream();
	}

	private File _createWabBundleFile(Object[][] tableColumns, int version)
		throws Exception {

		File file = FileUtil.createTempFile("war");

		try (OutputStream outputStream = new FileOutputStream(file);

			JarOutputStream jarOutputStream = new JarOutputStream(
				outputStream)) {

			String createSQL = toCreateSQL(tableColumns);

			addClass(
				"WEB-INF/classes/" + ENTITY_PATH, jarOutputStream, tableColumns,
				createSQL);

			addResource(
				"dependencies/portlet.xml", "WEB-INF/portlet.xml",
				jarOutputStream);
			addResource(
				"dependencies/service/META-INF/portlet-model-hints.xml",
				"WEB-INF/classes/META-INF/portlet-model-hints.xml",
				jarOutputStream);
			addResource(
				"dependencies/service/META-INF/spring/module-spring.xml",
				"WEB-INF/classes/META-INF/portlet-spring.xml", jarOutputStream);

			addEmptyResource("WEB-INF/sql/indexes.sql", jarOutputStream);
			addEmptyResource("WEB-INF/sql/sequences.sql", jarOutputStream);

			addResource(
				"WEB-INF/sql/tables.sql", createSQL.getBytes(StringPool.UTF8),
				jarOutputStream);

			addServiceProperties(
				version, "WEB-INF/classes/service.properties", jarOutputStream);

			jarOutputStream.finish();

			return file;
		}
	}

	private final File[] _warFiles = new File[4];
	private final URL[] _warURLs = new URL[4];

}