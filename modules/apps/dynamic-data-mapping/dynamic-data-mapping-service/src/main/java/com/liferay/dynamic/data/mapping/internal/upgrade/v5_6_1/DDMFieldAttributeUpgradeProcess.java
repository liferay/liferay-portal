/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_6_1;

import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Adolfo Pérez
 */
public class DDMFieldAttributeUpgradeProcess extends UpgradeProcess {

	public DDMFieldAttributeUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		DLFileEntryLocalService dlFileEntryLocalService,
		FileEntryFriendlyURLResolver fileEntryFriendlyURLResolver,
		GroupLocalService groupLocalService,
		UserLocalService userLocalService) {

		_classNameLocalService = classNameLocalService;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_fileEntryFriendlyURLResolver = fileEntryFriendlyURLResolver;
		_groupLocalService = groupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(
			"com.liferay.journal.model.JournalArticle");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DDMFieldAttribute.companyId, ",
					"DDMFieldAttribute.ctCollectionId, ",
					"DDMFieldAttribute.fieldAttributeId, ",
					"DDMFieldAttribute.smallAttributeValue, ",
					"DDMFieldAttribute.largeAttributeValue from DDMStructure ",
					"inner join DDMStructureVersion on ",
					"DDMStructure.ctCollectionId = ",
					"DDMStructureVersion.ctCollectionId and ",
					"DDMStructure.structureId = ",
					"DDMStructureVersion.structureId inner join DDMField on ",
					"DDMStructureVersion.ctCollectionId = ",
					"DDMField.ctCollectionId and ",
					"DDMStructureVersion.structureVersionId = ",
					"DDMField.structureVersionId inner join DDMFieldAttribute ",
					"on DDMField.ctCollectionId = ",
					"DDMFieldAttribute.ctCollectionId and DDMField.fieldId = ",
					"DDMFieldAttribute.fieldId where DDMStructure.classNameId ",
					"= ? and DDMField.fieldType = 'rich_text'"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update DDMFieldAttribute set smallAttributeValue = ?, " +
						"largeAttributeValue = ? where ctCollectionId = ? " +
							"and fieldAttributeId = ?")) {

			preparedStatement1.setLong(1, classNameId);

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				long companyId = resultSet.getLong(1);

				preparedStatement2.setString(
					1, _transform(companyId, resultSet.getString(4)));
				preparedStatement2.setString(
					2, _transform(companyId, resultSet.getString(5)));

				preparedStatement2.setLong(3, resultSet.getLong(2));
				preparedStatement2.setLong(4, resultSet.getLong(3));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private DLFileEntry _getDLFileEntry(long companyId, Matcher matcher)
		throws PortalException {

		if (Objects.equals(
				FriendlyURLResolverConstants.URL_SEPARATOR_Y_FILE_ENTRY,
				matcher.group(7))) {

			String groupName = matcher.group(8);

			Group group = _getGroup(companyId, groupName);

			String friendlyURL = matcher.group(9);

			FileEntry fileEntry =
				_fileEntryFriendlyURLResolver.resolveFriendlyURL(
					group.getGroupId(), friendlyURL);

			if (fileEntry == null) {
				return null;
			}

			return (DLFileEntry)fileEntry.getModel();
		}

		if (matcher.group(5) != null) {
			long groupId = GetterUtil.getLong(matcher.group(2));

			String uuid = matcher.group(5);

			return _dlFileEntryLocalService.getFileEntryByUuidAndGroupId(
				uuid, groupId);
		}

		long groupId = GetterUtil.getLong(matcher.group(2));
		long folderId = GetterUtil.getLong(matcher.group(3));
		String title = matcher.group(4);

		try {
			return _dlFileEntryLocalService.getFileEntry(
				groupId, folderId, title);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			return _dlFileEntryLocalService.getFileEntryByFileName(
				groupId, folderId, title);
		}
	}

	private long _getDLFileEntryId(long companyId, String src)
		throws PortalException {

		// Check if the src starts with "data:image/" first because "data:image"
		// indicates a Base64 URL which can potentially be millions of
		// characters. So it is faster to run startsWith first to return early
		// on these strings first so that we do not have to call "contains" over
		// a very long string.

		if (src.startsWith("data:image/")) {
			return 0;
		}

		// If we got past the above check, we have a URL. Now we can do a quick
		// check if the URL contains "/documents" as a crude way of bypassing
		// most non-Liferay URLs before we have to get into the less performant
		// regex logic.

		if (!src.contains("/documents")) {
			return 0;
		}

		Matcher matcher = _pattern.matcher(src);

		if (matcher.find()) {
			try {
				DLFileEntry dlFileEntry = _getDLFileEntry(companyId, matcher);

				if (dlFileEntry == null) {
					if (_log.isWarnEnabled()) {
						_log.warn("Missing file entry for URL " + src);
					}

					return 0;
				}

				return dlFileEntry.getFileEntryId();
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Missing file entry for URL " + src, portalException);
				}

				return 0;
			}
		}

		return 0;
	}

	private Group _getGroup(long companyId, String name)
		throws PortalException {

		Group group = _groupLocalService.fetchFriendlyURLGroup(
			companyId, StringPool.SLASH + name);

		if (group != null) {
			return group;
		}

		User user = _userLocalService.getUserByScreenName(companyId, name);

		return user.getGroup();
	}

	private Document _parseDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);
		outputSettings.syntax(Document.OutputSettings.Syntax.xml);

		document.outputSettings(outputSettings);

		return document;
	}

	private String _transform(long companyId, String html)
		throws PortalException {

		if ((html == null) || !html.contains("/documents/") ||
			!html.contains("<img")) {

			return html;
		}

		Document document = _parseDocument(html);

		for (Element imgElement : document.select("img:not(picture > img)")) {
			if (!imgElement.hasAttr(
					AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID)) {

				long fileEntryId = _getDLFileEntryId(
					companyId, imgElement.attr("src"));

				if (fileEntryId != 0) {
					imgElement.attr(
						AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID,
						String.valueOf(fileEntryId));
				}
			}
		}

		if (html.contains("<html>") || html.contains("<head>")) {
			return document.html();
		}

		Element body = document.body();

		return body.html();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldAttributeUpgradeProcess.class);

	private static final Pattern _pattern = Pattern.compile(
		"((?:/?[^\\s]*)/documents/(\\d+)/(\\d+)/([^/?]+)(?:/([-0-9a-fA-F]+))?" +
			"(?:\\?.*$)?)|((?:/?[^\\s]*)/documents/(d)/(.*)/" +
				"([_A-Za-z0-9-]+)?(?:\\?.*$)?)");

	private final ClassNameLocalService _classNameLocalService;
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;
	private final GroupLocalService _groupLocalService;
	private final UserLocalService _userLocalService;

}