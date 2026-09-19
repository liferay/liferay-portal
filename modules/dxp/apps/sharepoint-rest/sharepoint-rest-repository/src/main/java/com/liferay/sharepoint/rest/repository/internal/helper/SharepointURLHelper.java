/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.helper;

import com.liferay.document.library.repository.external.ExtRepositoryFileEntry;
import com.liferay.document.library.repository.external.ExtRepositoryObject;
import com.liferay.document.library.repository.external.ExtRepositoryObjectType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;

/**
 * @author Adolfo Pérez
 */
public class SharepointURLHelper {

	public SharepointURLHelper(String siteAbsoluteURL, String resultsSourceId) {
		_siteAbsoluteURL = siteAbsoluteURL;
		_resultsSourceId = resultsSourceId;
	}

	public String getAddFileURL(String extRepositoryFolderKey, String name) {
		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')/Files" +
				"/Add(overwrite=false,url='%s')",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFolderKey),
			HttpComponentsUtil.encodePath(name));
	}

	public String getAddFolderURL(String extRepositoryFolderKey) {
		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
				"/Folders",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFolderKey));
	}

	public String getCancelCheckedOutFileURL(String extRepositoryFileEntryKey) {
		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
				"/UndoCheckOut",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFileEntryKey));
	}

	public String getCheckInFileURL(
		String extRepositoryFileEntryKey, boolean createMajorVersion,
		String changeLog) {

		int checkInType = 0;

		if (createMajorVersion) {
			checkInType = 1;
		}

		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
				"/CheckIn(comment='%s',checkintype=%d)",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFileEntryKey), changeLog,
			checkInType);
	}

	public String getCheckOutFileURL(String extRepositoryFileEntryKey) {
		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')/CheckOut",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFileEntryKey));
	}

	public String getCopyFileURL(
		String extRepositoryFileEntryKey, String newExtRepositoryFolderKey,
		String newTitle) {

		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
				"/CopyTo(strnewurl='%s',boverwrite=false)",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFileEntryKey),
			HttpComponentsUtil.encodePath(newExtRepositoryFolderKey) +
				StringPool.SLASH + HttpComponentsUtil.encodePath(newTitle));
	}

	public <T extends ExtRepositoryObject> String getDeleteObjectURL(
		ExtRepositoryObjectType<T> extRepositoryObjectType,
		String extRepositoryObjectKey) {

		if (extRepositoryObjectType == ExtRepositoryObjectType.FILE) {
			return String.format(
				"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')",
				_siteAbsoluteURL,
				HttpComponentsUtil.encodePath(extRepositoryObjectKey));
		}

		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryObjectKey));
	}

	public String getFileEntryContentURL(
		ExtRepositoryFileEntry extRepositoryFileEntry) {

		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
				"/OpenBinaryStream",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(
				extRepositoryFileEntry.getExtRepositoryModelKey()));
	}

	public String getFileVersionContentURL(
		ExtRepositoryFileEntry extRepositoryFileEntry, String versionId) {

		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
				"/Versions(%s)",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(
				extRepositoryFileEntry.getExtRepositoryModelKey()),
			versionId);
	}

	public String getFileVersionsURL(
		ExtRepositoryFileEntry extRepositoryFileEntry) {

		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')/Versions",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(
				extRepositoryFileEntry.getExtRepositoryModelKey()));
	}

	public String getFilesURL(String extRepositoryFolderKey) {
		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
				"/Files?$select=%s&$expand=%s",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFolderKey),
			_FIELDS_SELECTED_FILE, _FIELDS_EXPANDED_FILE);
	}

	public String getFoldersURL(String extRepositoryFolderKey) {
		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
				"/Folders?$select=%s&$expand=%s",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFolderKey),
			_FIELDS_SELECTED_FOLDER, _FIELDS_EXPANDED_FOLDER);
	}

	public String getMoveFileURL(
		String extRepositoryFileEntryKey, String extRepositoryFolderKey,
		String title) {

		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
				"/MoveTo(newurl='%s',flags=1)",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFileEntryKey),
			HttpComponentsUtil.encodePath(extRepositoryFolderKey) +
				StringPool.SLASH + HttpComponentsUtil.encodePath(title));
	}

	public <T extends ExtRepositoryObject> String getObjectURL(
		ExtRepositoryObjectType<T> extRepositoryObjectType,
		String extRepositoryObjectKey) {

		if (extRepositoryObjectType == ExtRepositoryObjectType.FILE) {
			return String.format(
				"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')" +
					"?$select=%s&$expand=%s",
				_siteAbsoluteURL,
				HttpComponentsUtil.encodePath(extRepositoryObjectKey),
				_FIELDS_SELECTED_FILE, _FIELDS_EXPANDED_FILE);
		}

		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
				"?$select=%s&$expand=%s",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryObjectKey),
			_FIELDS_SELECTED_FOLDER, _FIELDS_EXPANDED_FOLDER);
	}

	public <T extends ExtRepositoryObject> String getObjectsCountURL(
		ExtRepositoryObjectType<T> extRepositoryObjectType,
		String extRepositoryFolderKey) {

		if (extRepositoryObjectType == ExtRepositoryObjectType.OBJECT) {
			return String.format(
				"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
					"/ItemCount",
				_siteAbsoluteURL,
				HttpComponentsUtil.encodePath(extRepositoryFolderKey));
		}

		if (extRepositoryObjectType == ExtRepositoryObjectType.FOLDER) {
			return String.format(
				"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
					"/Folders?$select=ItemCount",
				_siteAbsoluteURL,
				HttpComponentsUtil.encodePath(extRepositoryFolderKey));
		}

		return String.format(
			"%s/_api/web/GetFolderByServerRelativePath(decodedUrl='%s')" +
				"/Files?$select=Level",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFolderKey));
	}

	public String getSearchURL(String queryText, int start, int end) {
		return String.format(
			"%s/_api/search/query?QueryText='%s'&SourceID='%s'&StartRow=%d&" +
				"RowsPerPage=%d",
			_siteAbsoluteURL, HtmlUtil.escapeURL(queryText), _resultsSourceId,
			start, end - start);
	}

	public String getUpdateFileURL(String extRepositoryFileEntryKey) {
		return String.format(
			"%s/_api/web/GetFileByServerRelativePath(decodedUrl='%s')/$value",
			_siteAbsoluteURL,
			HttpComponentsUtil.encodePath(extRepositoryFileEntryKey));
	}

	private static final String _FIELDS_EXPANDED_FILE = StringUtil.merge(
		Arrays.asList("Author", "CheckedOutByUser", "ListItemAllFields"));

	private static final String _FIELDS_EXPANDED_FOLDER = "ListItemAllFields";

	private static final String _FIELDS_SELECTED_FILE = StringUtil.merge(
		Arrays.asList(
			"Author/Title", "CheckedOutByUser/Title", "Length",
			"ListItemAllFields/EffectiveBasePermissions", "Name",
			"ServerRelativeUrl", "TimeCreated", "TimeLastModified", "Title",
			"UIVersion", "UIVersionLabel"));

	private static final String _FIELDS_SELECTED_FOLDER = StringUtil.merge(
		Arrays.asList(
			"Name", "ListItemAllFields/EffectiveBasePermissions",
			"ServerRelativeUrl", "TimeCreated", "TimeLastModified"));

	private final String _resultsSourceId;
	private final String _siteAbsoluteURL;

}