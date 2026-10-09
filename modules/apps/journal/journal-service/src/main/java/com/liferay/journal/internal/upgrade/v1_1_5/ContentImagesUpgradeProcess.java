/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v1_1_5;

import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
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
 * @author Jürgen Kappler
 */
public class ContentImagesUpgradeProcess extends UpgradeProcess {

	public ContentImagesUpgradeProcess(
		JournalArticleImageUpgradeHelper journalArticleImageUpgradeHelper,
		PortletFileRepository portletFileRepository) {

		_journalArticleImageUpgradeHelper = journalArticleImageUpgradeHelper;
		_portletFileRepository = portletFileRepository;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateContentImages();
	}

	private String _convertTypeImageElements(
			long userId, long groupId, long companyId, String content,
			long resourcePrimKey)
		throws Exception {

		Document contentDocument = SAXReaderUtil.read(content);

		contentDocument = contentDocument.clone();

		XPath xPath = SAXReaderUtil.createXPath(
			"//dynamic-element[@type='image']");

		List<Node> imageNodes = xPath.selectNodes(contentDocument);

		for (Node imageNode : imageNodes) {
			Element imageElement = (Element)imageNode;

			List<Element> dynamicContentElements = imageElement.elements(
				"dynamic-content");

			for (Element dynamicContentElement : dynamicContentElements) {
				long fileEntryId = GetterUtil.getLong(
					dynamicContentElement.attributeValue("fileEntryId"));

				String id = dynamicContentElement.attributeValue("id");

				boolean emptyDynamicContentElement = false;
				FileEntry fileEntry = null;

				if (Validator.isNotNull(id)) {
					fileEntry = _getFileEntryById(
						userId, groupId, companyId, resourcePrimKey, id);
				}
				else if (fileEntryId > 0) {
					fileEntry = _getFileEntryByFileEntryId(fileEntryId);
				}
				else {
					String data = String.valueOf(
						dynamicContentElement.getData());

					if (Validator.isNull(data)) {
						emptyDynamicContentElement = true;
					}
					else {
						fileEntry =
							_journalArticleImageUpgradeHelper.
								getFileEntryFromURL(data);

						if (fileEntry == null) {
							try {
								JSONObject jsonObject =
									JSONFactoryUtil.createJSONObject(data);

								fileEntryId = GetterUtil.getLong(
									jsonObject.get("fileEntryId"));

								fileEntry = _getFileEntryByFileEntryId(
									fileEntryId);
							}
							catch (Exception exception) {
								if (_log.isWarnEnabled()) {
									_log.warn(
										"Unable to get file entry " +
											fileEntryId,
										exception);
								}
							}
						}
					}
				}

				dynamicContentElement.clearContent();

				if (fileEntry == null) {
					if (!emptyDynamicContentElement && _log.isWarnEnabled()) {
						_log.warn(
							"Deleted dynamic content because the file entry " +
								"does not exist");
					}

					continue;
				}

				dynamicContentElement.addCDATA(
					JSONUtil.put(
						"alt",
						GetterUtil.getString(
							dynamicContentElement.attributeValue("alt"))
					).put(
						"fileEntryId", fileEntry.getFileEntryId()
					).put(
						"groupId", fileEntry.getGroupId()
					).put(
						"name",
						dynamicContentElement.attributeValue(
							"name", fileEntry.getFileName())
					).put(
						"resourcePrimKey", resourcePrimKey
					).put(
						"title",
						dynamicContentElement.attributeValue(
							"title", fileEntry.getTitle())
					).put(
						"type", "journal"
					).put(
						"uuid", fileEntry.getUuid()
					).toString());

				if (fileEntryId > 0) {
					continue;
				}

				dynamicContentElement.addAttribute(
					"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));
			}
		}

		return contentDocument.formattedString();
	}

	private FileEntry _getFileEntryByFileEntryId(long fileEntryId) {
		FileEntry fileEntry = null;

		try {
			fileEntry = _portletFileRepository.getPortletFileEntry(fileEntryId);
		}
		catch (PortalException portalException) {
			String message = "Unable to get file entry " + fileEntryId;

			if (_log.isDebugEnabled()) {
				_log.debug(message, portalException);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(message);
			}
		}

		return fileEntry;
	}

	private FileEntry _getFileEntryById(
			long userId, long groupId, long companyId, long resourcePrimKey,
			String id)
		throws Exception {

		userId = PortalUtil.getValidUserId(companyId, userId);

		long folderId = _journalArticleImageUpgradeHelper.getFolderId(
			userId, groupId, resourcePrimKey);

		FileEntry fileEntry = null;

		try {
			fileEntry = _portletFileRepository.getPortletFileEntry(
				groupId, folderId, id);
		}
		catch (PortalException portalException) {
			String message = StringBundler.concat(
				"Unable to get file entry with group ID ", groupId,
				", folder ID ", folderId, ", and file name ", id,
				" for resourcePrimKey ", resourcePrimKey);

			if (_log.isDebugEnabled()) {
				_log.debug(message, portalException);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(message);
			}
		}

		return fileEntry;
	}

	private void _updateContentImages() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select id_, resourcePrimKey, groupId, companyId, userId, " +
					"content from JournalArticle where content like " +
						"'%type=\"image\"%'");

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update JournalArticle set content = ? where id_ = ?")) {

			while (resultSet.next()) {
				preparedStatement2.setString(
					1,
					_convertTypeImageElements(
						resultSet.getLong("userId"),
						resultSet.getLong("groupId"),
						resultSet.getLong("companyId"),
						resultSet.getString("content"),
						resultSet.getLong("resourcePrimKey")));
				preparedStatement2.setLong(2, resultSet.getLong("id_"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentImagesUpgradeProcess.class);

	private final JournalArticleImageUpgradeHelper
		_journalArticleImageUpgradeHelper;
	private final PortletFileRepository _portletFileRepository;

}