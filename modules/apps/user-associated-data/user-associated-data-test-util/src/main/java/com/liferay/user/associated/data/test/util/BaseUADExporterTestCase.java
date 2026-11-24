/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.test.util;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.user.associated.data.exporter.UADExporter;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pei-Jung Lan
 */
public abstract class BaseUADExporterTestCase<T extends BaseModel> {

	@Before
	public void setUp() throws Exception {
		uadExporter = getUADExporter();
		user = UserTestUtil.addUser();
	}

	@Test
	public void testExport() throws Exception {
		T baseModel = addBaseModel(user.getUserId());

		Document document = getExportDocument(baseModel);

		assertColumnValue(
			document, "userName", String.valueOf(user.getFullName()));
	}

	@Test
	public void testExportAll() throws Exception {
		addBaseModel(user.getUserId());

		File file = uadExporter.exportAll(user.getUserId());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			List<String> entries = zipReader.getEntries();

			Assert.assertEquals(entries.toString(), 1, entries.size());
		}
	}

	protected abstract T addBaseModel(long userId) throws Exception;

	protected void assertColumnValue(
		Document document, String columnName, String expectedColumnValue) {

		String nodeSelector =
			"/model/column[column-name='" + columnName + "']/column-value";

		Node columnNode = document.selectSingleNode(nodeSelector);

		Assert.assertEquals(expectedColumnValue, columnNode.getText());
	}

	protected Document getExportDocument(T baseModel) throws Exception {
		byte[] bytes = uadExporter.export(baseModel);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			bytes);

		return SAXReaderUtil.read(byteArrayInputStream);
	}

	protected abstract UADExporter<T> getUADExporter();

	protected UADExporter<T> uadExporter;

	@DeleteAfterTestRun
	protected User user;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}