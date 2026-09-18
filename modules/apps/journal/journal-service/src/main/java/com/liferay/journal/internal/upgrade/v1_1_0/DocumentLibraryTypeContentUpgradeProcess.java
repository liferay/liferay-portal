/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v1_1_0;

import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class DocumentLibraryTypeContentUpgradeProcess extends UpgradeProcess {

	public DocumentLibraryTypeContentUpgradeProcess(
		JournalArticleImageUpgradeHelper journalArticleImageUpgradeHelper) {

		_journalArticleImageUpgradeHelper = journalArticleImageUpgradeHelper;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateContent();
	}

	private String _convertContent(String content) throws Exception {
		Document contentDocument = SAXReaderUtil.read(content);

		contentDocument = contentDocument.clone();

		XPath xPath = SAXReaderUtil.createXPath(
			"//dynamic-element[@type='document_library' or " +
				"@type='image_gallery']");

		List<Node> imageNodes = xPath.selectNodes(contentDocument);

		if (imageNodes.isEmpty()) {
			return null;
		}

		for (Node imageNode : imageNodes) {
			Element imageElement = (Element)imageNode;

			List<Element> dynamicContentElements = imageElement.elements(
				"dynamic-content");

			for (Element dynamicContentElement : dynamicContentElements) {
				String data =
					_journalArticleImageUpgradeHelper.getDocumentLibraryValue(
						dynamicContentElement.getText());

				dynamicContentElement.clearContent();

				dynamicContentElement.addCDATA(data);
			}

			imageElement.addAttribute("type", "document_library");
		}

		return contentDocument.formattedString();
	}

	private void _updateContent() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select content, id_ from JournalArticle where content like " +
					"'%type=\"document_library\"%' or content like " +
						"'%type=\"image_gallery\"%'");

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update JournalArticle set content = ? where id_ = ?")) {

			while (resultSet.next()) {
				String content = _convertContent(
					resultSet.getString("content"));

				if (content == null) {
					continue;
				}

				preparedStatement2.setString(1, content);
				preparedStatement2.setLong(2, resultSet.getLong("id_"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private final JournalArticleImageUpgradeHelper
		_journalArticleImageUpgradeHelper;

}