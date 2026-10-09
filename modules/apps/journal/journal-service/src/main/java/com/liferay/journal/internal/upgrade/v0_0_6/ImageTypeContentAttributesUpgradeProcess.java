/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v0_0_6;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Alberto Chaparro
 */
public class ImageTypeContentAttributesUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateContentImages();
	}

	private String _addImageContentAttributes(long id, String content)
		throws Exception {

		Document document = null;

		try {
			document = SAXReaderUtil.read(content);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat("ID: ", id, "\nContent: ", content));

			throw exception;
		}

		document = document.clone();

		XPath xPath = SAXReaderUtil.createXPath(
			"//dynamic-element[@type='image']");

		List<Node> imageNodes = xPath.selectNodes(document);

		for (Node imageNode : imageNodes) {
			Element imageElement = (Element)imageNode;

			List<Element> dynamicContentElements = imageElement.elements(
				"dynamic-content");

			String articleImageId = null;

			for (Element dynamicContentElement : dynamicContentElements) {
				articleImageId = dynamicContentElement.attributeValue("id");

				dynamicContentElement.addAttribute("alt", StringPool.BLANK);
				dynamicContentElement.addAttribute("name", articleImageId);
				dynamicContentElement.addAttribute("title", articleImageId);
				dynamicContentElement.addAttribute("type", "journal");
			}

			if (Validator.isNull(articleImageId)) {
				continue;
			}

			imageElement.addAttribute(
				"instance-id", _getImageInstanceId(articleImageId));
		}

		return document.formattedString();
	}

	private String _getImageInstanceId(String articleImageId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select elInstanceId from JournalArticleImage where " +
					"articleImageId = ?")) {

			preparedStatement.setLong(1, Long.valueOf(articleImageId));

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("elInstanceId");
			}

			return StringPool.BLANK;
		}
	}

	private void _updateContentImages() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select id_, content from JournalArticle where content like " +
					"'%type=\"image\"%'");

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update JournalArticle set content = ? where id_ = ?")) {

			while (resultSet.next()) {
				long id = resultSet.getLong("id_");

				preparedStatement2.setString(
					1,
					_addImageContentAttributes(
						id, resultSet.getString("content")));
				preparedStatement2.setLong(2, id);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImageTypeContentAttributesUpgradeProcess.class);

}