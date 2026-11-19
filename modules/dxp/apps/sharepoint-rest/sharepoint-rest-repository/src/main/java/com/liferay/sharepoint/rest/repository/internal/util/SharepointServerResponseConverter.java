/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.util;

import com.liferay.document.library.repository.external.ExtRepository;
import com.liferay.document.library.repository.external.ExtRepositoryFileEntry;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersion;
import com.liferay.document.library.repository.external.ExtRepositoryFolder;
import com.liferay.document.library.repository.external.ExtRepositoryObject;
import com.liferay.document.library.repository.external.ExtRepositoryObjectType;
import com.liferay.document.library.repository.external.ExtRepositorySearchResult;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model.SharepointFileEntry;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model.SharepointFileVersion;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model.SharepointFolder;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model.SharepointModel;
import com.liferay.sharepoint.rest.repository.internal.helper.SharepointURLHelper;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class SharepointServerResponseConverter {

	public SharepointServerResponseConverter(
		SharepointURLHelper sharepointURLHelper, ExtRepository extRepository,
		String siteAbsoluteURL, String libraryPath) {

		_sharepointURLHelper = sharepointURLHelper;
		_extRepository = extRepository;
		_siteAbsoluteURL = siteAbsoluteURL;
		_libraryPath = libraryPath;

		_rootDocumentPath = _join(
			StringPool.SLASH, siteAbsoluteURL, libraryPath);
	}

	public <T extends ExtRepositoryObject> List<T> getExtRepositoryFileEntries(
		JSONObject jsonObject) {

		List<T> extRepositoryFileEntries = new ArrayList<>();

		JSONObject dJSONObject = jsonObject.getJSONObject("d");

		JSONArray resultsJSONArray = dJSONObject.getJSONArray("results");

		for (int i = 0; i < resultsJSONArray.length(); i++) {
			extRepositoryFileEntries.add(
				(T)_createFileEntry(resultsJSONArray.getJSONObject(i)));
		}

		return extRepositoryFileEntries;
	}

	public <T extends ExtRepositoryFileEntry & ExtRepositoryObject> T
		getExtRepositoryFileEntry(JSONObject jsonObject) {

		return (T)_createFileEntry(jsonObject.getJSONObject("d"));
	}

	public <T extends ExtRepositoryFileEntry & SharepointModel>
		List<ExtRepositoryFileVersion> getExtRepositoryFileVersions(
			T extRepositoryFileEntry, JSONObject jsonObject) {

		List<ExtRepositoryFileVersion> extRepositoryObjects = new ArrayList<>();

		JSONObject dJSONObject = jsonObject.getJSONObject("d");

		JSONArray resultsJSONArray = dJSONObject.getJSONArray("results");

		extRepositoryObjects.add(extRepositoryFileEntry.asFileVersion());

		for (int i = 0; i < resultsJSONArray.length(); i++) {
			extRepositoryObjects.add(
				_createFileVersion(
					extRepositoryFileEntry, resultsJSONArray.getJSONObject(i)));
		}

		return extRepositoryObjects;
	}

	public <T extends ExtRepositoryFolder & ExtRepositoryObject> T
		getExtRepositoryFolder(JSONObject jsonObject) {

		return _createFolder(jsonObject.getJSONObject("d"));
	}

	public <T extends ExtRepositoryObject> List<T> getExtRepositoryFolders(
		JSONObject jsonObject) {

		List<T> extRepositoryFolders = new ArrayList<>();

		JSONObject dJSONObject = jsonObject.getJSONObject("d");

		JSONArray resultsJSONArray = dJSONObject.getJSONArray("results");

		for (int i = 0; i < resultsJSONArray.length(); i++) {
			extRepositoryFolders.add(
				_createFolder(resultsJSONArray.getJSONObject(i)));
		}

		return extRepositoryFolders;
	}

	public <T extends ExtRepositoryObject> T getExtRepositoryObject(
		ExtRepositoryObjectType<?> extRepositoryObjectType,
		JSONObject jsonObject) {

		if (extRepositoryObjectType == ExtRepositoryObjectType.FILE) {
			return getExtRepositoryFileEntry(jsonObject);
		}

		return getExtRepositoryFolder(jsonObject);
	}

	public int getExtRepositoryObjectsCount(JSONObject jsonObject) {
		if (jsonObject.has("value")) {
			return jsonObject.getInt("value");
		}

		JSONObject dJSONObject = jsonObject.getJSONObject("d");

		JSONArray resultsJSONArray = dJSONObject.getJSONArray("results");

		return resultsJSONArray.length();
	}

	public <T extends ExtRepositoryObject> List<ExtRepositorySearchResult<T>>
			getSearchResults(JSONObject jsonObject)
		throws PortalException {

		List<ExtRepositorySearchResult<T>> searchResults = new ArrayList<>();

		JSONObject dJSONObject = jsonObject.getJSONObject("d");

		JSONObject queryJSONObject = dJSONObject.getJSONObject("query");

		JSONObject primaryQueryResultJSONObject = queryJSONObject.getJSONObject(
			"PrimaryQueryResult");

		JSONObject relevantResultsJSONObject =
			primaryQueryResultJSONObject.getJSONObject("RelevantResults");

		JSONObject tableJSONObject = relevantResultsJSONObject.getJSONObject(
			"Table");

		JSONObject rowsJSONObject = tableJSONObject.getJSONObject("Rows");

		JSONArray rowsResultsJSONArray = rowsJSONObject.getJSONArray("results");

		for (int i = 0; i < rowsResultsJSONArray.length(); i++) {
			JSONObject rowResultJSONObject = rowsResultsJSONArray.getJSONObject(
				i);

			JSONObject cellsJSONObject = rowResultJSONObject.getJSONObject(
				"Cells");

			JSONArray cellsResultsJSONArray = cellsJSONObject.getJSONArray(
				"results");

			ExtRepositoryObjectType<? extends ExtRepositoryObject>
				extRepositoryObjectType = ExtRepositoryObjectType.OBJECT;

			String extension = null;
			String parentLink = null;
			double score = 0;
			String snippet = null;
			String title = null;

			for (int j = 0; j < cellsResultsJSONArray.length(); j++) {
				JSONObject cellsResultJSONObject =
					cellsResultsJSONArray.getJSONObject(j);

				String key = cellsResultJSONObject.getString("Key");

				if (key.equals("Description")) {
					snippet = GetterUtil.getString(
						cellsResultJSONObject.getString("Value"));
				}
				else if (key.equals("IsContainer")) {
					if (cellsResultJSONObject.getBoolean("Value")) {
						extRepositoryObjectType =
							ExtRepositoryObjectType.FOLDER;
					}
					else {
						extRepositoryObjectType = ExtRepositoryObjectType.FILE;
					}
				}
				else if (key.equals("ParentLink")) {
					parentLink = cellsResultJSONObject.getString("Value");

					if (parentLink.endsWith(_SHAREPOINT_ALL_ITEMS_LIST_PATH)) {
						parentLink = parentLink.substring(
							0,
							parentLink.length() -
								_SHAREPOINT_ALL_ITEMS_LIST_PATH.length());
					}
				}
				else if (key.equals("Rank")) {
					score = cellsResultJSONObject.getDouble("Value");
				}
				else if (key.equals("Title")) {
					title = cellsResultJSONObject.getString("Value");
				}
				else if (key.equals("SecondaryFileExtension")) {
					extension = cellsResultJSONObject.getString("Value");
				}
			}

			if (Validator.isNull(parentLink) ||
				!parentLink.startsWith(_rootDocumentPath) ||
				(extRepositoryObjectType == ExtRepositoryObjectType.OBJECT)) {

				continue;
			}

			ExtRepositoryObject extRepositoryObject =
				_extRepository.getExtRepositoryObject(
					extRepositoryObjectType,
					parentLink.substring(_siteAbsoluteURL.length()),
					_getFileName(title, extension));

			searchResults.add(
				new ExtRepositorySearchResult(
					extRepositoryObject, (float)score, snippet));
		}

		return searchResults;
	}

	public boolean isMinorVersionEnabled(JSONObject jsonObject) {
		JSONObject dJSONObject = jsonObject.getJSONObject("d");

		return dJSONObject.getBoolean("EnableMinorVersions");
	}

	private SharepointFileEntry _createFileEntry(JSONObject jsonObject) {
		String extRepositoryModelKey = jsonObject.getString(
			"ServerRelativeUrl");

		String name = jsonObject.getString("Name");

		String title = name;

		Date createDate = _parseDate(jsonObject.getString("TimeCreated"));
		Date modifiedDate = _parseDate(
			jsonObject.getString("TimeLastModified"));
		long size = jsonObject.getLong("Length");
		String fileVersionExtRepositoryModelKey =
			extRepositoryModelKey + StringPool.COLON +
				jsonObject.getString("UIVersion");
		String version = jsonObject.getString("UIVersionLabel");

		JSONObject authorJSONObject = jsonObject.getJSONObject("Author");

		String owner = authorJSONObject.getString("Title");

		String checkedOutBy = StringPool.BLANK;

		JSONObject checkedOutByUserJSONObject = jsonObject.getJSONObject(
			"CheckedOutByUser");

		if (!checkedOutByUserJSONObject.has("__deferred")) {
			checkedOutBy = checkedOutByUserJSONObject.getString("Title");
		}

		long effectiveBasePermissionsBits = _getEffectiveBasePermissionsBits(
			jsonObject);

		return new SharepointFileEntry(
			extRepositoryModelKey, name, title, createDate, modifiedDate, size,
			fileVersionExtRepositoryModelKey, version, owner, checkedOutBy,
			effectiveBasePermissionsBits, _sharepointURLHelper);
	}

	private <T extends ExtRepositoryFileEntry & SharepointModel>
		SharepointFileVersion _createFileVersion(
			T extRepositoryFileEntry, JSONObject jsonObject) {

		String id = jsonObject.getString("ID");

		String extRepositoryModelKey =
			extRepositoryFileEntry.getExtRepositoryModelKey() +
				StringPool.COLON + id;

		String version = jsonObject.getString("VersionLabel");
		String contentURL = _sharepointURLHelper.getFileVersionContentURL(
			extRepositoryFileEntry, id);
		Date createDate = _parseDate(jsonObject.getString("Created"));
		String changeLog = jsonObject.getString("CheckInComment");
		String mimeType = MimeTypesUtil.getContentType(
			jsonObject.getString("Url"));
		long size = jsonObject.getLong("Size");

		return new SharepointFileVersion(
			extRepositoryModelKey, version, contentURL, createDate, changeLog,
			mimeType, size, extRepositoryFileEntry);
	}

	private <T extends ExtRepositoryFolder> T _createFolder(
		JSONObject jsonObject) {

		String extRepositoryModelKey = jsonObject.getString(
			"ServerRelativeUrl");
		String name = jsonObject.getString("Name");
		Date createDate = _parseDate(jsonObject.getString("TimeCreated"));
		Date modifiedDate = _parseDate(
			jsonObject.getString("TimeLastModified"));
		long effectiveBasePermissionsBits =
			_getFolderEffectiveBasePermissionsBits(jsonObject);

		return (T)new SharepointFolder(
			extRepositoryModelKey, name, createDate, modifiedDate,
			effectiveBasePermissionsBits);
	}

	private long _getEffectiveBasePermissionsBits(JSONObject jsonObject) {
		JSONObject listAllItemFieldsJSONObject = jsonObject.getJSONObject(
			"ListItemAllFields");

		if (listAllItemFieldsJSONObject.has("__deferred")) {
			return 0;
		}

		JSONObject effectiveBasePermissionsJSONObject =
			listAllItemFieldsJSONObject.getJSONObject(
				"EffectiveBasePermissions");

		long low = effectiveBasePermissionsJSONObject.getLong("Low");
		long high = effectiveBasePermissionsJSONObject.getLong("High");

		return low | (high << 32);
	}

	private String _getFileName(String title, String extension) {
		if (Validator.isNull(extension)) {
			return title;
		}

		return title + StringPool.PERIOD + extension;
	}

	private long _getFolderEffectiveBasePermissionsBits(JSONObject jsonObject) {
		JSONObject listAllItemFieldsJSONObject = jsonObject.getJSONObject(
			"ListItemAllFields");

		if (!listAllItemFieldsJSONObject.has("__deferred")) {
			return _getEffectiveBasePermissionsBits(jsonObject);
		}

		String extRepositoryModelKey = jsonObject.getString(
			"ServerRelativeUrl");

		if (extRepositoryModelKey.equals(StringPool.SLASH + _libraryPath)) {
			return _PERMISSION_BITS_ALL;
		}

		return _PERMISSION_BITS_NONE;
	}

	private String _join(String delimiter, String... strings) {
		if (strings.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((strings.length * 2) - 1);

		sb.append(strings[0]);

		for (int i = 1; i < strings.length; i++) {
			if (!strings[i - 1].endsWith(delimiter)) {
				sb.append(delimiter);
			}

			sb.append(strings[i]);
		}

		return sb.toString();
	}

	private Date _parseDate(String dateString) {
		try {
			DateFormat simpleDateFormat =
				DateFormatFactoryUtil.getSimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");

			return simpleDateFormat.parse(dateString);
		}
		catch (ParseException parseException) {
			throw new RuntimeException(parseException);
		}
	}

	private static final long _PERMISSION_BITS_ALL = 0x7FFFFFFFFFFFFFFFL;

	private static final long _PERMISSION_BITS_NONE = 0;

	private static final String _SHAREPOINT_ALL_ITEMS_LIST_PATH =
		"/Forms/AllItems.aspx";

	private final ExtRepository _extRepository;
	private final String _libraryPath;
	private final String _rootDocumentPath;
	private final SharepointURLHelper _sharepointURLHelper;
	private final String _siteAbsoluteURL;

}