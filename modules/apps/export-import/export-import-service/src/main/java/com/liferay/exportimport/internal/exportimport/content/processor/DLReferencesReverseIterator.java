/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.exportimport.content.processor;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author Carlos Correa
 */
public class DLReferencesReverseIterator
	implements Iterator<DLReferencesReverseIterator.DLReference> {

	public DLReferencesReverseIterator(
		String content,
		FileEntryFriendlyURLResolver fileEntryFriendlyURLResolver,
		long groupId) {

		_content = content;
		_fileEntryFriendlyURLResolver = fileEntryFriendlyURLResolver;
		_groupId = groupId;

		_endPos = content.length();

		_pathContext = PortalUtil.getPathContext();

		_patterns = _getPatterns(_pathContext);
	}

	@Override
	public boolean hasNext() {
		if (_hasNext != null) {
			return _hasNext;
		}

		_nextDLReference = next();

		_hasNext = _nextDLReference != null;

		return _hasNext;
	}

	@Override
	public DLReference next() {
		if (_nextDLReference != null) {
			DLReference dlReference = _nextDLReference;

			_hasNext = null;
			_nextDLReference = null;

			return dlReference;
		}

		int beginPos = -1;

		while (true) {
			beginPos = StringUtil.lastIndexOfAny(_content, _patterns, _endPos);

			if (beginPos == -1) {
				return null;
			}

			ObjectValuePair<String, Integer> dlReferenceEndPosObjectValuePair =
				_getDLReferenceEndPosObjectValuePair(
					_content, beginPos + _pathContext.length(), _endPos);

			if (dlReferenceEndPosObjectValuePair == null) {
				_endPos = beginPos - 1;

				continue;
			}

			String dlReference = dlReferenceEndPosObjectValuePair.getKey();

			Map<String, String[]> dlReferenceParameters =
				_getDLReferenceParameters(
					_groupId, _content, beginPos + _pathContext.length(),
					dlReferenceEndPosObjectValuePair.getValue(), dlReference);

			if (MapUtil.isEmpty(dlReferenceParameters)) {
				_endPos = beginPos - 1;

				continue;
			}

			FileEntry fileEntry = _getFileEntry(dlReferenceParameters);

			if (fileEntry == null) {
				fileEntry = _getFileEntry(
					_content, beginPos + _pathContext.length());
			}

			try {
				if (_isExternalURL(_groupId, _content, beginPos, _endPos)) {
					_endPos = beginPos - 1;

					continue;
				}
			}
			catch (Exception exception) {
				_log.error(exception);

				_endPos = beginPos - 1;

				continue;
			}

			_endPos = beginPos - 1;

			return new DLReference(
				beginPos, MapUtil.getInteger(dlReferenceParameters, "endPos"),
				fileEntry, dlReferenceParameters, dlReference);
		}
	}

	public class DLReference {

		public DLReference(
			int beginPos, int endPos, FileEntry fileEntry,
			Map<String, String[]> parameters, String reference) {

			_beginPos = beginPos;
			_endPos = endPos;
			_fileEntry = fileEntry;
			_parameters = parameters;
			_reference = reference;

			_group = _getGroup(fileEntry, parameters);
		}

		public boolean containsParameter(String name) {
			return _parameters.containsKey(name);
		}

		public int getBeginPos() {
			return _beginPos;
		}

		public int getEndPos() {
			return _endPos;
		}

		public FileEntry getFileEntry() {
			return _fileEntry;
		}

		public Group getGroup() {
			return _group;
		}

		public String getParameter(String name) {
			return MapUtil.getString(_parameters, name);
		}

		public Map<String, String[]> getParameters() {
			return _parameters;
		}

		public String getReference() {
			return _reference;
		}

		private final int _beginPos;
		private final int _endPos;
		private final FileEntry _fileEntry;
		private final Group _group;
		private final Map<String, String[]> _parameters;
		private final String _reference;

	}

	private ObjectValuePair<String, Integer>
		_getDLReferenceEndPosObjectValuePair(
			String content, int beginPos, int endPos) {

		String[] stopStrings = _DL_REFERENCE_LEGACY_STOP_STRINGS;

		if (!_isLegacyURL(content, beginPos)) {
			stopStrings = _DL_REFERENCE_STOP_STRINGS;
		}

		int urlPatternPos = StringUtil.indexOfAny(
			content, stopStrings, beginPos, endPos);

		if (urlPatternPos == -1) {
			if (endPos != content.length()) {
				return null;
			}

			urlPatternPos = endPos;
		}

		return new ObjectValuePair<>(
			content.substring(beginPos, urlPatternPos), urlPatternPos);
	}

	private Map<String, String[]> _getDLReferenceParameters(
		long groupId, String content, int beginPos, int endPos,
		String dlReference) {

		boolean legacyURL = _isLegacyURL(content, beginPos);

		Map<String, String[]> map = new HashMap<>();

		while (dlReference.contains(StringPool.AMPERSAND_ENCODED)) {
			dlReference = StringUtil.replace(
				dlReference, StringPool.AMPERSAND_ENCODED,
				StringPool.AMPERSAND);
		}

		if (!legacyURL) {
			String[] pathArray = dlReference.split(StringPool.SLASH);

			if (pathArray.length < 3) {
				return map;
			}

			if (Objects.equals(
					pathArray[2],
					FriendlyURLResolverConstants.URL_SEPARATOR_Y_FILE_ENTRY)) {

				if (pathArray.length >= 5) {
					map.put(
						"friendlyURL",
						new String[] {
							StringUtils.substringBefore(
								HttpComponentsUtil.decodeURL(pathArray[4]),
								StringPool.POUND)
						});
				}

				if (pathArray.length >= 4) {
					map.put("groupName", new String[] {pathArray[3]});
				}
			}
			else if (Objects.equals(pathArray[2], "portlet_file_entry")) {
				if (pathArray.length >= 4) {
					map.put("groupId", new String[] {pathArray[3]});
				}

				if (pathArray.length >= 5) {
					map.put(
						"title",
						new String[] {
							StringUtils.substringBefore(
								HttpComponentsUtil.decodeURL(pathArray[4]),
								StringPool.POUND)
						});
				}
			}
			else {
				map.put("groupId", new String[] {pathArray[2]});

				if (pathArray.length == 5) {
					map.put("folderId", new String[] {pathArray[3]});
					map.put(
						"title",
						new String[] {
							StringUtils.substringBefore(
								HttpComponentsUtil.decodeURL(pathArray[4]),
								StringPool.POUND)
						});
				}
			}

			String uuid = _getUuid(dlReference);

			if (Validator.isNotNull(uuid)) {
				map.put("uuid", new String[] {uuid});
			}
		}
		else {
			dlReference = dlReference.substring(
				dlReference.indexOf(CharPool.QUESTION) + 1);

			map = HttpComponentsUtil.parameterMapFromString(dlReference);

			String[] imageIds = map.get("img_id");

			if (imageIds == null) {
				imageIds = map.get("i_id");
			}

			imageIds = ArrayUtil.filter(imageIds, Validator::isNotNull);

			if (ArrayUtil.isNotEmpty(imageIds)) {
				map.put("image_id", imageIds);
			}
		}

		map.put("endPos", new String[] {String.valueOf(endPos)});

		String groupIdString = MapUtil.getString(map, "groupId");

		if (groupIdString.equals("@group_id@")) {
			groupIdString = String.valueOf(groupId);

			map.put("groupId", new String[] {groupIdString});
		}

		return map;
	}

	private FileEntry _getFileEntry(Map<String, String[]> map) {
		if (MapUtil.isEmpty(map)) {
			return null;
		}

		FileEntry fileEntry = null;

		try {
			String uuid = MapUtil.getString(map, "uuid");
			long groupId = MapUtil.getLong(map, "groupId");

			if (Validator.isNotNull(uuid)) {
				try {
					fileEntry =
						DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
							uuid, groupId);
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to get file entry", portalException);
					}

					return DLAppLocalServiceUtil.
						getFileEntryByExternalReferenceCode(uuid, groupId);
				}
			}
			else {
				if (map.containsKey("friendlyURL")) {
					String friendlyURL = MapUtil.getString(map, "friendlyURL");

					fileEntry = _resolveFileEntry(
						MapUtil.getString(map, "groupName"), friendlyURL);

					if (fileEntry == null) {
						throw new NoSuchFileEntryException(
							"No file entry found for friendly URL " +
								friendlyURL);
					}
				}
				else if (map.containsKey("folderId")) {
					long folderId = MapUtil.getLong(map, "folderId");
					String name = MapUtil.getString(map, "name");
					String title = MapUtil.getString(map, "title");

					if (Validator.isNotNull(title)) {
						try {
							fileEntry =
								DLAppLocalServiceUtil.getFileEntryByFileName(
									groupId, folderId, title);
						}
						catch (NoSuchFileEntryException
									noSuchFileEntryException) {

							if (_log.isDebugEnabled()) {
								_log.debug(noSuchFileEntryException);
							}

							fileEntry = DLAppLocalServiceUtil.getFileEntry(
								groupId, folderId, title);
						}
					}
					else {
						DLFileEntry dlFileEntry =
							DLFileEntryLocalServiceUtil.fetchFileEntryByName(
								groupId, folderId, name);

						if (dlFileEntry != null) {
							fileEntry = DLAppLocalServiceUtil.getFileEntry(
								dlFileEntry.getFileEntryId());
						}
					}
				}
				else if (map.containsKey("image_id")) {
					DLFileEntry dlFileEntry =
						DLFileEntryLocalServiceUtil.fetchFileEntryByAnyImageId(
							MapUtil.getLong(map, "image_id"));

					if (dlFileEntry != null) {
						fileEntry = DLAppLocalServiceUtil.getFileEntry(
							dlFileEntry.getFileEntryId());
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return fileEntry;
	}

	private FileEntry _getFileEntry(String content, int beginPos) {
		int cdataBeginPos = StringUtil.lastIndexOfAny(
			content, new String[] {"<![CDATA["}, beginPos);

		if (cdataBeginPos == QueryUtil.ALL_POS) {
			return null;
		}

		int cdataEndPos = StringUtil.indexOfAny(
			content, new String[] {"]]>"}, cdataBeginPos);

		if (cdataEndPos == QueryUtil.ALL_POS) {
			return null;
		}

		int jsonBeginPos = StringUtil.indexOfAny(
			content, new char[] {'{'}, cdataBeginPos, cdataEndPos);

		int jsonEndPos = StringUtil.lastIndexOfAny(
			content, new char[] {'}'}, cdataBeginPos, cdataEndPos);

		if ((jsonBeginPos == QueryUtil.ALL_POS) ||
			(jsonEndPos == QueryUtil.ALL_POS)) {

			return null;
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				content.substring(jsonBeginPos, jsonEndPos + 1));

			return DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
				jsonObject.getString("uuid"), jsonObject.getLong("groupId"));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return null;
	}

	private Group _getGroup(
		FileEntry fileEntry, Map<String, String[]> parameteres) {

		try {
			if (fileEntry != null) {
				return GroupLocalServiceUtil.getGroup(fileEntry.getGroupId());
			}
			else if (parameteres.containsKey("groupId")) {
				return GroupLocalServiceUtil.getGroup(
					MapUtil.getLong(parameteres, "groupId"));
			}
			else if (parameteres.containsKey("groupName")) {
				return _getGroup(MapUtil.getString(parameteres, "groupName"));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private Group _getGroup(String name) throws Exception {
		Group group = GroupLocalServiceUtil.fetchFriendlyURLGroup(
			CompanyThreadLocal.getCompanyId(), StringPool.SLASH + name);

		if (group != null) {
			return group;
		}

		User user = UserLocalServiceUtil.getUserByScreenName(
			CompanyThreadLocal.getCompanyId(), name);

		return user.getGroup();
	}

	private String[] _getPatterns(String contextPath) {
		return new String[] {
			contextPath.concat("/c/document_library/get_file?"),
			contextPath.concat("/documents/"),
			contextPath.concat("/image/image_gallery?")
		};
	}

	private String _getUuid(String s) {
		Matcher matcher = _uuidPattern.matcher(s);

		String uuid = StringPool.BLANK;

		while (matcher.find()) {
			uuid = matcher.group(0);
		}

		return uuid;
	}

	private boolean _isCreoleReference(String content, int beginPos) {
		if (content.regionMatches(
				true, beginPos - 2, StringPool.DOUBLE_OPEN_BRACKET, 0, 2) ||
			content.regionMatches(
				true, beginPos - 2, StringPool.DOUBLE_OPEN_CURLY_BRACE, 0, 2)) {

			return true;
		}

		return false;
	}

	private boolean _isExternalURL(
			long groupId, String content, int beginPos, int endPos)
		throws PortalException {

		if (((beginPos == 0) && (endPos == content.length())) ||
			_isCreoleReference(content, beginPos) ||
			_isHTMLReference(content, beginPos) ||
			_isJSONReference(content, beginPos) ||
			_isStyleReference(content, beginPos)) {

			return false;
		}

		String portalURL = _pathContext;

		if (Validator.isNull(portalURL)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if ((serviceContext != null) &&
				(serviceContext.getThemeDisplay() != null)) {

				portalURL = PortalUtil.getPortalURL(
					serviceContext.getThemeDisplay());
			}
		}

		Set<String> hostnames = new HashSet<>();

		hostnames.add(portalURL);

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		for (VirtualHost virtualHost :
				VirtualHostLocalServiceUtil.getVirtualHosts(
					group.getCompanyId())) {

			String hostname = virtualHost.getHostname();

			hostnames.add(hostname);
			hostnames.add(Http.HTTP_WITH_SLASH + hostname);
			hostnames.add(Http.HTTPS_WITH_SLASH + hostname);
		}

		int colonPos = 0;

		for (int i = 1; i <= _OFFSET_COLON_PORT; i++) {
			if (i > beginPos) {
				break;
			}

			if (content.charAt(beginPos - i) == CharPool.COLON) {
				colonPos = i;

				break;
			}
		}

		long urlPort = 0;

		if (colonPos > 0) {
			urlPort = GetterUtil.getLong(
				content.substring(beginPos - colonPos + 1, beginPos));
		}

		for (String hostname : hostnames) {
			if (urlPort > 0) {
				int serverPort = PortalUtil.getPortalServerPort(
					hostname.startsWith(Http.HTTPS_WITH_SLASH));

				if (urlPort != serverPort) {
					continue;
				}
			}

			int curBeginPos = beginPos - hostname.length() - colonPos;

			if (curBeginPos < 0) {
				continue;
			}

			String substring = content.substring(curBeginPos, endPos);

			if (substring.startsWith(hostname) &&
				(((curBeginPos == 0) && (endPos == content.length())) ||
				 _isCreoleReference(content, curBeginPos) ||
				 _isHTMLReference(content, curBeginPos) ||
				 _isJSONReference(content, curBeginPos) ||
				 _isStyleReference(content, curBeginPos))) {

				return false;
			}
		}

		return true;
	}

	private boolean _isHTMLReference(String content, int beginPos) {
		if (content.regionMatches(beginPos - 1, StringPool.APOSTROPHE, 0, 1) ||
			content.regionMatches(beginPos - 1, StringPool.QUOTE, 0, 1)) {

			beginPos = beginPos - 1;
		}

		if (content.regionMatches(
				true, beginPos - 1, StringPool.BACK_SLASH, 0, 1)) {

			beginPos = beginPos - 1;
		}

		String[] attributes = {"href=", "src="};

		for (String attribute : attributes) {
			if (content.regionMatches(
					true, beginPos - attribute.length(), attribute, 0,
					attribute.length())) {

				return true;
			}
		}

		return false;
	}

	private boolean _isJSONReference(String content, int beginPos) {
		String[] jsonAttributes = {"\"href\"", "\"url\""};

		int position = StringUtil.lastIndexOfAny(
			content, jsonAttributes, beginPos);

		if (position == -1) {
			return false;
		}

		if (_jsonAttributePattern.matcher(
				content.substring(position, beginPos)
			).matches() ||
			_jsonLocalizedPattern.matcher(
				content.substring(position, beginPos)
			).matches()) {

			return true;
		}

		return false;
	}

	private boolean _isLegacyURL(String content, int beginPos) {
		return !content.startsWith("/documents/", beginPos);
	}

	private boolean _isStyleReference(String content, int beginPos) {
		beginPos = _skipWhiteSpacePos(content, beginPos);

		if (content.regionMatches(beginPos - 1, StringPool.APOSTROPHE, 0, 1) ||
			content.regionMatches(beginPos - 1, StringPool.QUOTE, 0, 1)) {

			beginPos = beginPos - 1;
		}

		beginPos = _skipWhiteSpacePos(content, beginPos);

		String url = "url(";

		return content.regionMatches(true, beginPos - url.length(), url, 0, 2);
	}

	private FileEntry _resolveFileEntry(String groupName, String friendlyURL)
		throws Exception {

		if (_fileEntryFriendlyURLResolver == null) {
			return null;
		}

		Group group = _getGroup(groupName);

		return _fileEntryFriendlyURLResolver.resolveFriendlyURL(
			group.getGroupId(), friendlyURL);
	}

	private int _skipWhiteSpacePos(String content, int beginPos) {
		while (content.regionMatches(beginPos - 1, StringPool.NEW_LINE, 0, 1) ||
			   content.regionMatches(beginPos - 1, StringPool.RETURN, 0, 1) ||
			   content.regionMatches(beginPos - 1, StringPool.SPACE, 0, 1) ||
			   content.regionMatches(beginPos - 1, StringPool.TAB, 0, 1)) {

			beginPos = beginPos - 1;
		}

		return beginPos;
	}

	private static final String[] _DL_REFERENCE_LEGACY_STOP_STRINGS = {
		StringPool.APOSTROPHE, StringPool.APOSTROPHE_ENCODED,
		StringPool.BACK_SLASH + StringPool.APOSTROPHE,
		StringPool.BACK_SLASH + StringPool.QUOTE, StringPool.CLOSE_BRACKET,
		StringPool.CLOSE_CURLY_BRACE, StringPool.CLOSE_PARENTHESIS,
		StringPool.GREATER_THAN, StringPool.LESS_THAN, StringPool.PIPE,
		StringPool.POUND, StringPool.QUOTE, StringPool.QUOTE_ENCODED,
		StringPool.SPACE
	};

	private static final String[] _DL_REFERENCE_STOP_STRINGS = {
		StringPool.APOSTROPHE, StringPool.APOSTROPHE_ENCODED,
		StringPool.BACK_SLASH + StringPool.APOSTROPHE,
		StringPool.BACK_SLASH + StringPool.QUOTE, StringPool.CLOSE_BRACKET,
		StringPool.CLOSE_CURLY_BRACE, StringPool.CLOSE_PARENTHESIS,
		StringPool.GREATER_THAN, StringPool.LESS_THAN, StringPool.NEW_LINE,
		StringPool.PIPE, StringPool.POUND, StringPool.QUESTION,
		StringPool.QUOTE, StringPool.QUOTE_ENCODED, StringPool.SPACE
	};

	private static final int _OFFSET_COLON_PORT = 6;

	private static final Log _log = LogFactoryUtil.getLog(
		DLReferencesReverseIterator.class);

	private static final Pattern _jsonAttributePattern = Pattern.compile(
		"\\\"[^\"\\\\\\\\]*\\\"\\s*:\\s*\\\"");
	private static final Pattern _jsonLocalizedPattern = Pattern.compile(
		"\\\"[^\"\\\\]*\\\"\\s*:\\s*\\{\\\"[a-zA-Z_]+" +
			"\\\"\\s*:\\s*\\\"[^\"\\\\]*");
	private static final Pattern _uuidPattern = Pattern.compile(
		"[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-" +
			"[a-fA-F0-9]{12}(?=[&,?]|$)");

	private final String _content;
	private int _endPos;
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;
	private final long _groupId;
	private Boolean _hasNext;
	private DLReference _nextDLReference;
	private final String _pathContext;
	private final String[] _patterns;

}