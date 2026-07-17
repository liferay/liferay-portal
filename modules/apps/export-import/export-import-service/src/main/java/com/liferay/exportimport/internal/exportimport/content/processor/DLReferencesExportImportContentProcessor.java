/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.exportimport.content.processor;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.exception.ExportImportContentProcessorException;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.staging.StagingGroupHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 * @author Carlos Correa
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

		return _replaceExportDLReferences(
			content, exportReferencedContent, portletDataContext, stagedModel);
	}

	@Override
	public String replaceExportContentReferences(
		String content, PortletDataContext portletDataContext) {

		StringBuilder sb = new StringBuilder(content);

		DLReferencesReverseIterator dlReferencesReverseIterator =
			new DLReferencesReverseIterator(
				content, _fileEntryFriendlyURLResolver,
				portletDataContext.getScopeGroupId());

		while (dlReferencesReverseIterator.hasNext()) {
			DLReferencesReverseIterator.DLReference dlReference =
				dlReferencesReverseIterator.next();

			FileEntry fileEntry = dlReference.getFileEntry();

			String externalReferenceCode = null;
			String uuid = null;

			if (fileEntry != null) {
				externalReferenceCode = fileEntry.getExternalReferenceCode();
				uuid = fileEntry.getUuid();
			}
			else if (Validator.isBlank(
						dlReference.getParameter("friendlyURL"))) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"The file entry does not exist and the friendly URL " +
							"parameter was not parsed");
				}

				continue;
			}
			else if (_log.isDebugEnabled()) {
				_log.debug(
					"The file entry with friendly URL " +
						dlReference.getParameter("friendlyURL") +
							" does not exist");
			}

			Group group = dlReference.getGroup();

			if (group == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get group for document library reference " +
							dlReference.getReference());
				}

				continue;
			}

			DocumentLibraryReference documentLibraryReference =
				new DocumentLibraryReference(
					externalReferenceCode,
					dlReference.getParameter("friendlyURL"),
					group.getFriendlyURL(), group.getGroupId(), uuid);

			sb.replace(
				dlReference.getBeginPos(), dlReference.getEndPos(),
				documentLibraryReference.toString());
		}

		return sb.toString();
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		return _replaceImportDLReferences(
			content, portletDataContext, stagedModel);
	}

	@Override
	public String replaceImportContentReferences(
			String content, PortletDataContext portletDataContext)
		throws Exception {

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		for (DocumentLibraryReference documentLibraryReference :
				DocumentLibraryReference.parse(content)) {

			Group group = _getGroup(
				portletDataContext.getCompanyId(), documentLibraryReference,
				groupIds);

			long groupId = (group != null) ? group.getGroupId() : 0L;

			FileEntry fileEntry =
				_fileEntryFriendlyURLResolver.resolveFriendlyURL(
					groupId,
					documentLibraryReference.getFileEntryFriendlyURL());

			if (!_isSameFileEntry(documentLibraryReference, fileEntry)) {
				if (_log.isDebugEnabled()) {
					if (fileEntry == null) {
						_log.debug(
							StringBundler.concat(
								"The file entry with friendly URL ",
								documentLibraryReference.
									getFileEntryFriendlyURL(),
								" and group friendly URL ",
								documentLibraryReference.getGroupFriendlyURL(),
								" does not exist"));
					}
					else {
						_log.debug(
							StringBundler.concat(
								"The file entry with friendly URL ",
								documentLibraryReference.
									getFileEntryFriendlyURL(),
								" and group with friendly URL ",
								documentLibraryReference.getGroupFriendlyURL(),
								" exists but it may contain incorrect data"));
					}
				}

				boolean companyGroup = false;

				if (group != null) {
					companyGroup = _stagingGroupHelper.isCompanyGroup(group);
				}

				long classNameId = _classNameLocalService.getClassNameId(
					FileEntry.class);
				long exportImportConfigurationId = GetterUtil.getLong(
					ExportImportThreadLocal.getExportImportConfigurationId());

				_exportImportReportEntryLocalService.
					getOrAddExportImportReportEntry(
						companyGroup ? 0L : groupId,
						portletDataContext.getCompanyId(),
						documentLibraryReference.getExternalReferenceCode(),
						classNameId, 0, exportImportConfigurationId,
						ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
						FileEntry.class.getName());

				_exportImportProcessCallbackRegistry.registerCallback(
					portletDataContext.getExportImportProcessId(),
					() -> {
						FileEntry afterImportFileEntry =
							_fileEntryFriendlyURLResolver.resolveFriendlyURL(
								groupId,
								documentLibraryReference.
									getFileEntryFriendlyURL());

						if (_isSameFileEntry(
								documentLibraryReference,
								afterImportFileEntry)) {

							_exportImportReportEntryLocalService.
								resolveEmptyExportImportReportEntries(
									groupId, portletDataContext.getCompanyId(),
									documentLibraryReference.
										getExternalReferenceCode(),
									classNameId);
						}

						return null;
					});
			}

			content = StringUtil.replaceLast(
				content, documentLibraryReference.getReferenceString(),
				_getUrl(documentLibraryReference, fileEntry, group));
		}

		return content;
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {

		if (_isValidateDLReferences()) {
			_validateDLReferences(content, groupId);
		}
	}

	private Group _getGroup(
		long companyId, DocumentLibraryReference documentLibraryReference,
		Map<Long, Long> groupIds) {

		try {
			Long liveGroupId = groupIds.get(
				documentLibraryReference.getGroupId());

			if (liveGroupId != null) {
				return _groupLocalService.getGroup(liveGroupId);
			}

			return _groupLocalService.fetchFriendlyURLGroup(
				companyId, documentLibraryReference.getGroupFriendlyURL());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private String _getUrl(
			DocumentLibraryReference documentLibraryReference,
			FileEntry fileEntry, Group group)
		throws Exception {

		if (fileEntry == null) {
			return _dlURLHelper.getPreviewURL(
				documentLibraryReference.getFileEntryFriendlyURL(),
				(group != null) ? group.getFriendlyURL() :
					documentLibraryReference.getGroupFriendlyURL());
		}

		return _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, false);
	}

	private boolean _isSameFileEntry(
		DocumentLibraryReference documentLibraryReference,
		FileEntry fileEntry) {

		if ((fileEntry == null) ||
			!StringUtil.equals(
				documentLibraryReference.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()) ||
			!StringUtil.equals(
				documentLibraryReference.getUuid(), fileEntry.getUuid())) {

			return false;
		}

		return true;
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
			String content, boolean exportReferencedContent,
			PortletDataContext portletDataContext, StagedModel stagedModel)
		throws Exception {

		StringBuilder sb = new StringBuilder(content);

		DLReferencesReverseIterator dlReferencesReverseIterator =
			new DLReferencesReverseIterator(
				content, _fileEntryFriendlyURLResolver,
				portletDataContext.getScopeGroupId());

		while (dlReferencesReverseIterator.hasNext()) {
			DLReferencesReverseIterator.DLReference dlReference =
				dlReferencesReverseIterator.next();

			FileEntry fileEntry = dlReference.getFileEntry();

			if (fileEntry == null) {
				continue;
			}

			if (exportReferencedContent && !fileEntry.isInTrash()) {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, stagedModel, fileEntry,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
			else {
				Element entityElement = portletDataContext.getExportDataElement(
					stagedModel);

				String referenceType =
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY;

				if (fileEntry.isInTrash()) {
					referenceType =
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY_DISPOSABLE;
				}

				portletDataContext.addReferenceElement(
					stagedModel, entityElement, fileEntry, referenceType, true);
			}

			String path = ExportImportPathUtil.getModelPath(fileEntry);

			StringBundler exportedReferenceSB = new StringBundler(10);

			exportedReferenceSB.append("[$dl-reference=");
			exportedReferenceSB.append(path);

			if (dlReference.containsParameter("friendlyURL")) {
				exportedReferenceSB.append("$,$include-friendly-url=true");
			}
			else {
				exportedReferenceSB.append("$,$include-uuid=");
				exportedReferenceSB.append(
					dlReference.containsParameter("uuid"));
			}

			exportedReferenceSB.append("$]");

			if (fileEntry.isInTrash()) {
				String originalReference = sb.substring(
					dlReference.getBeginPos(), dlReference.getEndPos());

				exportedReferenceSB.append("[#dl-reference=");
				exportedReferenceSB.append(originalReference);

				if (dlReference.containsParameter("friendlyURL")) {
					exportedReferenceSB.append("#,#include-friendly-url=true");
				}
				else {
					exportedReferenceSB.append("#,#include-uuid=");
					exportedReferenceSB.append(
						dlReference.containsParameter("uuid"));
				}

				exportedReferenceSB.append("#]");
			}

			sb.replace(
				dlReference.getBeginPos(), dlReference.getEndPos(),
				exportedReferenceSB.toString());
		}

		return sb.toString();
	}

	private String _replaceImportDLReferences(
			String content, PortletDataContext portletDataContext,
			StagedModel stagedModel)
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

	private void _validateDLReferences(String content, long groupId)
		throws PortalException {

		DLReferencesReverseIterator dlReferencesReverseIterator =
			new DLReferencesReverseIterator(
				content, _fileEntryFriendlyURLResolver, groupId);

		while (dlReferencesReverseIterator.hasNext()) {
			DLReferencesReverseIterator.DLReference dlReference =
				dlReferencesReverseIterator.next();

			if (dlReference.getFileEntry() == null) {
				ExportImportContentValidationException
					exportImportContentValidationException =
						new ExportImportContentValidationException(
							DLReferencesExportImportContentProcessor.class.
								getName(),
							new NoSuchFileEntryException());

				exportImportContentValidationException.setDLReferenceParameters(
					dlReference.getParameters());

				exportImportContentValidationException.setDLReference(
					dlReference.getReference());

				exportImportContentValidationException.setType(
					ExportImportContentValidationException.
						FILE_ENTRY_NOT_FOUND);

				throw exportImportContentValidationException;
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLReferencesExportImportContentProcessor.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	private static class DocumentLibraryReference {

		public static List<DocumentLibraryReference> parse(String value) {
			List<DocumentLibraryReference> documentLibraryReferences =
				new ArrayList<>();

			int startIndex = -1;

			while ((startIndex = value.indexOf(_BEGIN)) != -1) {
				int endIndex = value.indexOf(_END, startIndex);

				String reference = value.substring(
					startIndex, endIndex + _END.length());

				String externalReferenceCode = _decode(
					_getArgumentValue(_EXTERNAL_REFERENCE_CODE, reference));
				String fileEntryFriendlyURL = _getArgumentValue(
					_FILE_ENTRY_FRIENDLY_URL, reference);
				String groupFriendlyURL = _getArgumentValue(
					_GROUP_FRIENDLY_URL, reference);
				long groupId = GetterUtil.getLong(
					_getArgumentValue(_GROUP_ID, reference));
				String uuid = _getArgumentValue(_UUID, reference);

				documentLibraryReferences.add(
					new DocumentLibraryReference(
						externalReferenceCode, fileEntryFriendlyURL,
						groupFriendlyURL, groupId, reference, uuid));

				value = value.substring(endIndex + _END.length());
			}

			return documentLibraryReferences;
		}

		public String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		public String getFileEntryFriendlyURL() {
			return _fileEntryFriendlyURL;
		}

		public String getGroupFriendlyURL() {
			return _groupFriendlyURL;
		}

		public long getGroupId() {
			return _groupId;
		}

		public String getReferenceString() {
			return _referenceString;
		}

		public String getUuid() {
			return _uuid;
		}

		@Override
		public String toString() {
			return StringBundler.concat(
				_BEGIN, " $", _EXTERNAL_REFERENCE_CODE, "=",
				_encode(_externalReferenceCode), "$,$",
				_FILE_ENTRY_FRIENDLY_URL, "=", _fileEntryFriendlyURL, "$,$",
				_GROUP_FRIENDLY_URL, "=", _groupFriendlyURL, "$,$", _GROUP_ID,
				"=", _groupId, "$,$", _UUID, "=", _uuid, _END);
		}

		private static String _decode(String text) {
			return StringUtil.replace(
				text, new String[] {"\\$\\", "\\[\\", "\\]\\"},
				new String[] {"$", "[", "]"});
		}

		private static String _getArgumentValue(String name, String reference) {
			String key = "$" + name + "=";

			int beginIndex = reference.indexOf(key);

			int endIndex = StringUtil.indexOfAny(
				reference, new String[] {"$,$", _END}, beginIndex,
				reference.length());

			return reference.substring(beginIndex + key.length(), endIndex);
		}

		private DocumentLibraryReference(
			String externalReferenceCode, String fileEntryFriendlyURL,
			String groupFriendlyURL, long groupId, String uuid) {

			this(
				externalReferenceCode, fileEntryFriendlyURL, groupFriendlyURL,
				groupId, null, uuid);
		}

		private DocumentLibraryReference(
			String externalReferenceCode, String fileEntryFriendlyURL,
			String groupFriendlyURL, long groupId, String referenceString,
			String uuid) {

			_externalReferenceCode = GetterUtil.getString(
				externalReferenceCode);
			_fileEntryFriendlyURL = fileEntryFriendlyURL;
			_groupFriendlyURL = groupFriendlyURL;
			_groupId = groupId;
			_referenceString = referenceString;
			_uuid = GetterUtil.getString(uuid);
		}

		private String _encode(String text) {
			return StringUtil.replace(
				text, new String[] {"$", "[", "]"},
				new String[] {"\\$\\", "\\[\\", "\\]\\"});
		}

		private static final String _BEGIN = "[$dl-reference$";

		private static final String _END = "$]";

		private static final String _EXTERNAL_REFERENCE_CODE =
			"external-reference-code";

		private static final String _FILE_ENTRY_FRIENDLY_URL =
			"file-entry-friendly-url";

		private static final String _GROUP_FRIENDLY_URL = "group-friendly-url";

		private static final String _GROUP_ID = "group-id";

		private static final String _UUID = "uuid";

		private final String _externalReferenceCode;
		private final String _fileEntryFriendlyURL;
		private final String _groupFriendlyURL;
		private final long _groupId;
		private String _referenceString;
		private final String _uuid;

	}

}