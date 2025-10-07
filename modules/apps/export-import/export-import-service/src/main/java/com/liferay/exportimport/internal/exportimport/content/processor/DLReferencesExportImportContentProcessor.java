/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.exportimport.content.processor;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.exception.ExportImportContentProcessorException;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(
	property = "content.processor.type=DLReferences",
	service = ExportImportContentProcessor.class
)
public class DLReferencesExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content, boolean exportReferencedContent,
			boolean escapeContent)
		throws Exception {

		return _replaceExportDLReferences(
			portletDataContext, stagedModel, content, exportReferencedContent);
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		return _replaceImportDLReferences(
			portletDataContext, stagedModel, content);
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {

		if (_isValidateDLReferences()) {
			_validateDLReferences(groupId, content);
		}
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
		long groupId, String content, int beginPos, int endPos) {

		ObjectValuePair<String, Integer> dlReferenceEndPosObjectValuePair =
			_getDLReferenceEndPosObjectValuePair(content, beginPos, endPos);

		if (dlReferenceEndPosObjectValuePair == null) {
			return null;
		}

		boolean legacyURL = _isLegacyURL(content, beginPos);

		Map<String, String[]> map = new HashMap<>();

		String dlReference = dlReferenceEndPosObjectValuePair.getKey();

		endPos = dlReferenceEndPosObjectValuePair.getValue();

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

			String[] imageIds = null;

			if (map.containsKey("img_id")) {
				imageIds = map.get("img_id");
			}
			else if (map.containsKey("i_id")) {
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
					fileEntry = _dlAppLocalService.getFileEntryByUuidAndGroupId(
						uuid, groupId);
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to get file entry", portalException);
					}

					return _dlAppLocalService.
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
								_dlAppLocalService.getFileEntryByFileName(
									groupId, folderId, title);
						}
						catch (NoSuchFileEntryException
									noSuchFileEntryException) {

							if (_log.isDebugEnabled()) {
								_log.debug(noSuchFileEntryException);
							}

							fileEntry = _dlAppLocalService.getFileEntry(
								groupId, folderId, title);
						}
					}
					else {
						DLFileEntry dlFileEntry =
							_dlFileEntryLocalService.fetchFileEntryByName(
								groupId, folderId, name);

						if (dlFileEntry != null) {
							fileEntry = _dlAppLocalService.getFileEntry(
								dlFileEntry.getFileEntryId());
						}
					}
				}
				else if (map.containsKey("image_id")) {
					DLFileEntry dlFileEntry =
						_dlFileEntryLocalService.fetchFileEntryByAnyImageId(
							MapUtil.getLong(map, "image_id"));

					if (dlFileEntry != null) {
						fileEntry = _dlAppLocalService.getFileEntry(
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

	private FileEntry _getFileEntry(String content, int beginPos)
		throws PortalException {

		int jsonBeginPos = StringUtil.lastIndexOfAny(
			content, new String[] {"<![CDATA["}, beginPos);

		jsonBeginPos = StringUtil.indexOfAny(
			content, new char[] {'{'}, jsonBeginPos);

		int jsonEndPos = StringUtil.indexOfAny(
			content, new String[] {"]]>"}, jsonBeginPos);

		jsonEndPos = StringUtil.lastIndexOfAny(
			content, new char[] {'}'}, jsonEndPos);

		if ((jsonBeginPos == QueryUtil.ALL_POS) &&
			(jsonEndPos == QueryUtil.ALL_POS)) {

			return null;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			content.substring(jsonBeginPos, jsonEndPos + 1));

		try {
			return _dlAppLocalService.getFileEntryByUuidAndGroupId(
				jsonObject.getString("uuid"), jsonObject.getLong("groupId"));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	private Group _getGroup(String name) throws Exception {
		Group group = _groupLocalService.fetchFriendlyURLGroup(
			CompanyThreadLocal.getCompanyId(), StringPool.SLASH + name);

		if (group != null) {
			return group;
		}

		User user = _userLocalService.getUserByScreenName(
			CompanyThreadLocal.getCompanyId(), name);

		return user.getGroup();
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

		String portalURL = _portal.getPathContext();

		if (Validator.isNull(portalURL)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if ((serviceContext != null) &&
				(serviceContext.getThemeDisplay() != null)) {

				portalURL = _portal.getPortalURL(
					serviceContext.getThemeDisplay());
			}
		}

		Set<String> hostNames = new HashSet<>();

		hostNames.add(portalURL);

		Group group = _groupLocalService.getGroup(groupId);

		for (VirtualHost virtualHost :
				_virtualHostLocalService.getVirtualHosts(
					group.getCompanyId())) {

			String hostname = virtualHost.getHostname();

			hostNames.add(hostname);
			hostNames.add(Http.HTTP_WITH_SLASH + hostname);
			hostNames.add(Http.HTTPS_WITH_SLASH + hostname);
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

		for (String hostName : hostNames) {
			if (urlPort > 0) {
				int serverPort = _portal.getPortalServerPort(
					hostName.startsWith(Http.HTTPS_WITH_SLASH));

				if (urlPort != serverPort) {
					continue;
				}
			}

			int curBeginPos = beginPos - hostName.length() - colonPos;

			if (curBeginPos < 0) {
				continue;
			}

			String substring = content.substring(curBeginPos, endPos);

			if (substring.startsWith(hostName) &&
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

	private boolean _isValidateDLReferences() {
		try {
			ExportImportServiceConfiguration exportImportServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ExportImportServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return exportImportServiceConfiguration.
				validateFileEntryReferences();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	private String _replaceExportDLReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content, boolean exportReferencedContent)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			portletDataContext.getGroupId());

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		if (group.isStaged() && !group.isStagedRemotely() &&
			!group.isStagedPortlet(PortletKeys.DOCUMENT_LIBRARY) &&
			ExportImportThreadLocal.isStagingInProcess()) {

			return content;
		}

		StringBuilder sb = new StringBuilder(content);

		String contextPath = _portal.getPathContext();

		String[] patterns = {
			contextPath.concat("/c/document_library/get_file?"),
			contextPath.concat("/documents/"),
			contextPath.concat("/image/image_gallery?")
		};

		int beginPos = -1;
		int endPos = content.length();

		while (true) {
			beginPos = StringUtil.lastIndexOfAny(content, patterns, endPos);

			if (beginPos == -1) {
				break;
			}

			Map<String, String[]> dlReferenceParameters =
				_getDLReferenceParameters(
					portletDataContext.getScopeGroupId(), content,
					beginPos + contextPath.length(), endPos);

			FileEntry fileEntry = _getFileEntry(dlReferenceParameters);

			if ((fileEntry == null) ||
				_isExternalURL(
					portletDataContext.getScopeGroupId(), content, beginPos,
					endPos)) {

				endPos = beginPos - 1;

				continue;
			}

			endPos = MapUtil.getInteger(dlReferenceParameters, "endPos");

			try {
				if (exportReferencedContent && !fileEntry.isInTrash()) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, stagedModel, fileEntry,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
				}
				else {
					Element entityElement =
						portletDataContext.getExportDataElement(stagedModel);

					String referenceType =
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY;

					if (fileEntry.isInTrash()) {
						referenceType =
							PortletDataContext.
								REFERENCE_TYPE_DEPENDENCY_DISPOSABLE;
					}

					portletDataContext.addReferenceElement(
						stagedModel, entityElement, fileEntry, referenceType,
						true);
				}

				String path = ExportImportPathUtil.getModelPath(fileEntry);

				StringBundler exportedReferenceSB = new StringBundler(10);

				exportedReferenceSB.append("[$dl-reference=");
				exportedReferenceSB.append(path);

				if (dlReferenceParameters.containsKey("friendlyURL")) {
					exportedReferenceSB.append("$,$include-friendly-url=true");
				}
				else {
					exportedReferenceSB.append("$,$include-uuid=");
					exportedReferenceSB.append(
						dlReferenceParameters.containsKey("uuid"));
				}

				exportedReferenceSB.append("$]");

				if (fileEntry.isInTrash()) {
					String originalReference = sb.substring(beginPos, endPos);

					exportedReferenceSB.append("[#dl-reference=");
					exportedReferenceSB.append(originalReference);

					if (dlReferenceParameters.containsKey("friendlyURL")) {
						exportedReferenceSB.append(
							"#,#include-friendly-url=true");
					}
					else {
						exportedReferenceSB.append("#,#include-uuid=");
						exportedReferenceSB.append(
							dlReferenceParameters.containsKey("uuid"));
					}

					exportedReferenceSB.append("#]");
				}

				sb.replace(beginPos, endPos, exportedReferenceSB.toString());
			}
			catch (Exception exception) {
				StringBundler exceptionSB = new StringBundler(6);

				exceptionSB.append("Unable to process file entry ");
				exceptionSB.append(fileEntry.getFileEntryId());
				exceptionSB.append(" for staged model ");
				exceptionSB.append(stagedModel.getModelClassName());
				exceptionSB.append(" with primary key ");
				exceptionSB.append(stagedModel.getPrimaryKeyObj());

				ExportImportContentProcessorException
					exportImportContentProcessorException =
						new ExportImportContentProcessorException(
							exceptionSB.toString(), exception);

				if (_log.isDebugEnabled()) {
					_log.debug(
						exceptionSB.toString(),
						exportImportContentProcessorException);
				}
				else if (_log.isWarnEnabled()) {
					_log.warn(exceptionSB.toString());
				}
			}

			endPos = beginPos - 1;
		}

		return sb.toString();
	}

	private String _replaceImportDLReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		List<Element> referenceElements =
			portletDataContext.getReferenceElements(
				stagedModel, DLFileEntry.class);

		for (Element referenceElement : referenceElements) {
			Long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			Element referenceDataElement =
				portletDataContext.getReferenceDataElement(
					stagedModel, DLFileEntry.class, classPK);

			String path = null;

			if (referenceDataElement != null) {
				path = referenceDataElement.attributeValue("path");
			}

			if (Validator.isNull(path)) {
				long groupId = GetterUtil.getLong(
					referenceElement.attributeValue("group-id"));
				String className = referenceElement.attributeValue(
					"class-name");

				path = ExportImportPathUtil.getModelPath(
					groupId, className, classPK);
			}

			while (content.contains(
						"[$dl-reference=" + path +
							"$,$include-friendly-url=true$]") ||
				   content.contains(
					   "[$dl-reference=" + path + "$,$include-uuid=false$]") ||
				   content.contains(
					   "[$dl-reference=" + path + "$,$include-uuid=true$]")) {

				try {
					StagedModelDataHandlerUtil.importReferenceStagedModel(
						portletDataContext, stagedModel, DLFileEntry.class,
						classPK);
				}
				catch (Exception exception) {
					StringBundler exceptionSB = new StringBundler(6);

					exceptionSB.append("Unable to process file entry ");
					exceptionSB.append(classPK);
					exceptionSB.append(" for ");
					exceptionSB.append(stagedModel.getModelClassName());
					exceptionSB.append(" with primary key ");
					exceptionSB.append(stagedModel.getPrimaryKeyObj());

					ExportImportContentProcessorException
						exportImportContentProcessorException =
							new ExportImportContentProcessorException(
								exceptionSB.toString(), exception);

					if (_log.isDebugEnabled()) {
						_log.debug(
							exceptionSB.toString(),
							exportImportContentProcessorException);
					}
					else if (_log.isWarnEnabled()) {
						_log.warn(exceptionSB.toString());
					}
				}

				Map<Long, Long> dlFileEntryIds =
					(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
						DLFileEntry.class);

				long fileEntryId = MapUtil.getLong(
					dlFileEntryIds, classPK, classPK);

				int beginPos = content.indexOf("[$dl-reference=" + path);

				int endPos = content.indexOf("$]", beginPos) + 2;

				FileEntry importedFileEntry = null;

				try {
					importedFileEntry = _dlAppLocalService.getFileEntry(
						fileEntryId);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}

					if (content.startsWith("[#dl-reference=", endPos)) {
						int prefixPos = endPos + "[#dl-reference=".length();

						int postfixPos = content.indexOf("#]", prefixPos);

						String originalReference = content.substring(
							prefixPos, postfixPos);

						String exportedReference = content.substring(
							beginPos, postfixPos + 2);

						content = StringUtil.replace(
							content, exportedReference, originalReference);
					}
					else {
						throw portalException;
					}

					continue;
				}

				boolean appendVersion = false;

				if (!content.contains("$include-friendly-url=true$")) {
					appendVersion = true;
				}

				String url = _dlURLHelper.getPreviewURL(
					importedFileEntry, importedFileEntry.getFileVersion(), null,
					StringPool.BLANK, appendVersion, false);

				if (url.contains(StringPool.QUESTION)) {
					url = url.substring(
						0, url.lastIndexOf(StringPool.QUESTION));
				}

				String urlWithoutUUID = url.substring(
					0, url.lastIndexOf(StringPool.SLASH));

				String exportedReferenceFriendlyURL =
					"[$dl-reference=" + path + "$,$include-friendly-url=true$]";
				String exportedReferenceWithoutUUID =
					"[$dl-reference=" + path + "$,$include-uuid=false$]";
				String exportedReferenceWithUUID =
					"[$dl-reference=" + path + "$,$include-uuid=true$]";

				if (content.startsWith("[#dl-reference=", endPos)) {
					int friendlyURLPosition = content.indexOf(
						"#,#include-friendly-url=true", beginPos);

					if (friendlyURLPosition != -1) {
						endPos = friendlyURLPosition + 2;
					}
					else {
						endPos =
							content.indexOf("#,#include-uuid=", beginPos) + 2;
					}

					exportedReferenceFriendlyURL =
						content.substring(beginPos, endPos) +
							"#include-friendly-url=true#]";
					exportedReferenceWithoutUUID =
						content.substring(beginPos, endPos) +
							"#include-uuid=false#]";
					exportedReferenceWithUUID =
						content.substring(beginPos, endPos) +
							"#include-uuid=true#]";
				}

				content = StringUtil.replace(
					content, exportedReferenceFriendlyURL, url);
				content = StringUtil.replace(
					content, exportedReferenceWithUUID, url);
				content = StringUtil.replace(
					content, exportedReferenceWithoutUUID, urlWithoutUUID);
			}
		}

		return content;
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

	private void _validateDLReferences(long groupId, String content)
		throws PortalException {

		String pathContext = _portal.getPathContext();

		String[] patterns = {
			pathContext.concat("/c/document_library/get_file?"),
			pathContext.concat("/documents/"),
			pathContext.concat("/image/image_gallery?")
		};

		int beginPos = -1;
		int endPos = content.length();

		while (true) {
			beginPos = StringUtil.lastIndexOfAny(content, patterns, endPos);

			if (beginPos == -1) {
				break;
			}

			Map<String, String[]> dlReferenceParameters =
				_getDLReferenceParameters(
					groupId, content, beginPos + pathContext.length(), endPos);

			if (dlReferenceParameters == null) {
				endPos = beginPos - 1;

				continue;
			}

			FileEntry fileEntry = _getFileEntry(dlReferenceParameters);

			if (fileEntry == null) {
				fileEntry = _getFileEntry(content, beginPos);
			}

			if ((fileEntry == null) &&
				!_isExternalURL(groupId, content, beginPos, endPos)) {

				ExportImportContentValidationException
					exportImportContentValidationException =
						new ExportImportContentValidationException(
							DLReferencesExportImportContentProcessor.class.
								getName(),
							new NoSuchFileEntryException());

				exportImportContentValidationException.setDLReferenceParameters(
					dlReferenceParameters);

				ObjectValuePair<String, Integer>
					dlReferenceEndPosObjectValuePair =
						_getDLReferenceEndPosObjectValuePair(
							content, beginPos, endPos);

				exportImportContentValidationException.setDLReference(
					dlReferenceEndPosObjectValuePair.getKey());

				exportImportContentValidationException.setType(
					ExportImportContentValidationException.
						FILE_ENTRY_NOT_FOUND);

				throw exportImportContentValidationException;
			}

			endPos = beginPos - 1;
		}
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
		DLReferencesExportImportContentProcessor.class);

	private static final Pattern _jsonAttributePattern = Pattern.compile(
		"\\\"[^\"\\\\\\\\]*\\\"\\s*:\\s*\\\"");
	private static final Pattern _jsonLocalizedPattern = Pattern.compile(
		"\\\"[^\"\\\\]*\\\"\\s*:\\s*\\{\\\"[a-zA-Z_]+" +
			"\\\"\\s*:\\s*\\\"[^\"\\\\]*");
	private static final Pattern _uuidPattern = Pattern.compile(
		"[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-" +
			"[a-fA-F0-9]{12}(?=[&,?]|$)");

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}