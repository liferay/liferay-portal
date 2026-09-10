/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_6_1;

import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
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
import com.liferay.portal.kernel.util.HttpComponentsUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		DB db = DBManagerUtil.getDB();

		try (SafeCloseable safeCloseable = db.addTemporaryIndex(
				connection, "DDMFieldAttribute", false, "ctCollectionId",
				"fieldId")) {

			String selectSQL = StringBundler.concat(
				"select DDMFieldAttribute.ctCollectionId, DDMFieldAttribute.",
				"fieldAttributeId, DDMFieldAttribute.companyId, ",
				"DDMFieldAttribute.largeAttributeValue, DDMFieldAttribute.",
				"smallAttributeValue from DDMStructure inner join ",
				"DDMStructureVersion on DDMStructure.ctCollectionId = ",
				"DDMStructureVersion.ctCollectionId and DDMStructure.",
				"structureId = DDMStructureVersion.structureId inner join ",
				"DDMField on DDMStructureVersion.ctCollectionId = DDMField.",
				"ctCollectionId and DDMStructureVersion.structureVersionId = ",
				"DDMField.structureVersionId inner join DDMFieldAttribute on ",
				"DDMField.ctCollectionId = DDMFieldAttribute.ctCollectionId ",
				"and DDMField.fieldId = DDMFieldAttribute.fieldId where ",
				"DDMStructure.classNameId = ? and DDMField.fieldType = ",
				"'rich_text'");

			String updateSQL =
				"update DDMFieldAttribute set largeAttributeValue = ?, " +
					"smallAttributeValue = ? where ctCollectionId = ? and " +
						"fieldAttributeId = ?";

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(selectSQL);
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection, updateSQL)) {

				preparedStatement1.setLong(
					1,
					_classNameLocalService.getClassNameId(
						"com.liferay.journal.model.JournalArticle"));

				try (ResultSet resultSet = preparedStatement1.executeQuery()) {
					while (resultSet.next()) {
						long companyId = resultSet.getLong("companyId");

						String largeAttributeValue = _transform(
							companyId,
							resultSet.getString("largeAttributeValue"));
						String smallAttributeValue = _transform(
							companyId,
							resultSet.getString("smallAttributeValue"));

						if ((smallAttributeValue != null) &&
							(smallAttributeValue.length() > 255)) {

							largeAttributeValue = smallAttributeValue;

							smallAttributeValue = null;
						}

						preparedStatement2.setString(1, largeAttributeValue);
						preparedStatement2.setString(2, smallAttributeValue);

						preparedStatement2.setLong(
							3, resultSet.getLong("ctCollectionId"));
						preparedStatement2.setLong(
							4, resultSet.getLong("fieldAttributeId"));

						preparedStatement2.addBatch();
					}
				}

				preparedStatement2.executeBatch();
			}
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
		String title = HttpComponentsUtil.decodeURL(matcher.group(4));

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

	private String _transform(long companyId, String html)
		throws PortalException {

		if ((html == null) || !html.contains("/documents/") ||
			!html.contains(_OPEN_TAG_TOKEN_IMG)) {

			return html;
		}

		StringBundler sb = new StringBundler();

		int lastIndex = 0;

		while (lastIndex < html.length()) {
			int pictureStart = html.indexOf(_OPEN_TAG_TOKEN_PICTURE, lastIndex);

			if (pictureStart == -1) {
				pictureStart = html.length();
			}

			_transformImgTags(companyId, html, lastIndex, pictureStart, sb);

			if (pictureStart >= html.length()) {
				lastIndex = pictureStart;

				continue;
			}

			int pictureEnd = html.indexOf(
				_CLOSE_TAG_TOKEN_PICTURE,
				pictureStart + _OPEN_TAG_TOKEN_PICTURE.length());

			if (pictureEnd == -1) {
				pictureEnd = html.length();
			}
			else {
				pictureEnd += _CLOSE_TAG_TOKEN_PICTURE.length();
			}

			sb.append(html.substring(pictureStart, pictureEnd));

			lastIndex = pictureEnd;
		}

		return sb.toString();
	}

	private String _transformImgTag(long companyId, String imgTag, String src)
		throws PortalException {

		if (imgTag.contains(
				AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID)) {

			return imgTag;
		}

		long fileEntryId = _getDLFileEntryId(companyId, src);

		if (fileEntryId == 0) {
			return imgTag;
		}

		return StringBundler.concat(
			_OPEN_TAG_TOKEN_IMG, StringPool.SPACE,
			AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID, "=\"",
			fileEntryId, "\"", imgTag.substring(_OPEN_TAG_TOKEN_IMG.length()));
	}

	private void _transformImgTags(
			long companyId, String html, int start, int end, StringBundler sb)
		throws PortalException {

		int lastIndex = start;

		while (lastIndex < end) {
			int imgStart = html.indexOf(_OPEN_TAG_TOKEN_IMG, lastIndex);

			if ((imgStart == -1) || (imgStart > end)) {
				sb.append(html.substring(lastIndex, end));

				return;
			}

			sb.append(html.substring(lastIndex, imgStart));

			int imgEnd = html.indexOf(CharPool.GREATER_THAN, imgStart) + 1;

			if (imgEnd == 0) {
				sb.append(html.substring(imgStart, end));

				return;
			}

			int attributeListPos = imgStart + _OPEN_TAG_TOKEN_IMG.length();

			int srcStart = html.indexOf(_ATTRIBUTE_TOKEN_SRC, attributeListPos);

			if ((srcStart == -1) || (srcStart > imgEnd)) {
				sb.append(html.substring(imgStart, imgEnd));

				lastIndex = imgEnd;

				continue;
			}

			int quotePos = srcStart + _ATTRIBUTE_TOKEN_SRC.length();

			int srcEnd = html.indexOf(html.charAt(quotePos), quotePos + 1);

			sb.append(
				_transformImgTag(
					companyId, html.substring(imgStart, imgEnd),
					html.substring(quotePos + 1, srcEnd)));

			lastIndex = imgEnd;
		}
	}

	private static final String _ATTRIBUTE_TOKEN_SRC = "src=";

	private static final String _CLOSE_TAG_TOKEN_PICTURE = "</picture>";

	private static final String _OPEN_TAG_TOKEN_IMG = "<img";

	private static final String _OPEN_TAG_TOKEN_PICTURE = "<picture";

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