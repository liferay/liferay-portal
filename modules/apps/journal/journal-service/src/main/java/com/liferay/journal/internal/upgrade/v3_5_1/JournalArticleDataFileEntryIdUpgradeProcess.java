/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v3_5_1;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alejandro Tardín
 */
public class JournalArticleDataFileEntryIdUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select id_, content from JournalArticle");

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update JournalArticle set content = ? where id_ = ?")) {

			while (resultSet.next()) {
				String content = resultSet.getString("content");

				String upgradedContent = _upgradeContent(content);

				if (Objects.equals(content, upgradedContent)) {
					continue;
				}

				preparedStatement2.setString(1, upgradedContent);
				preparedStatement2.setLong(2, resultSet.getLong("id_"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private String _upgradeContent(String content) throws DocumentException {
		Document document = SAXReaderUtil.read(content);

		XPath xPath = SAXReaderUtil.createXPath(
			"//dynamic-element[@type='text_area']");

		List<Node> ddmJournalArticleNodes = xPath.selectNodes(document);

		for (Node ddmJournalArticleNode : ddmJournalArticleNodes) {
			Element ddmJournalArticleElement = (Element)ddmJournalArticleNode;

			List<Element> dynamicContentElements =
				ddmJournalArticleElement.elements("dynamic-content");

			for (Element dynamicContentElement : dynamicContentElements) {
				String stringValue = dynamicContentElement.getStringValue();

				Matcher matcher = _dataFileEntryIdPattern.matcher(stringValue);

				String upgradedStringValue = matcher.replaceAll(
					"data-fileentryid=");

				if (upgradedStringValue.equals(stringValue)) {
					continue;
				}

				dynamicContentElement.clearContent();

				dynamicContentElement.addCDATA(upgradedStringValue);
			}
		}

		return document.asXML();
	}

	private static final Pattern _dataFileEntryIdPattern = Pattern.compile(
		"data-fileEntryId=", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}