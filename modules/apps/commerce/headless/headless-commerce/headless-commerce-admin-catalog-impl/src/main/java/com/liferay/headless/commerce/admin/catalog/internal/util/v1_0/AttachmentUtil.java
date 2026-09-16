/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.exception.CPAttachmentFileEntryProtocolException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.exception.NoSuchCPOptionException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentUrl;
import com.liferay.headless.commerce.admin.catalog.internal.util.DateConfigUtil;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.upload.UniqueFileNameProvider;

import java.io.File;

import java.net.URL;
import java.net.URLConnection;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 */
public class AttachmentUtil {

	public static FileEntry addFileEntry(
			Attachment attachment,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		if (Validator.isNotNull(attachment.getAttachment())) {
			String base64EncodedContent = attachment.getAttachment();

			File file = FileUtil.createTempFile(
				Base64.decode(base64EncodedContent));

			return _addFileEntry(
				file, attachment.getContentType(), uniqueFileNameProvider,
				serviceContext);
		}

		if (Validator.isNull(attachment.getSrc())) {
			return null;
		}

		URL url = new URL(attachment.getSrc());

		if (Objects.equals(url.getProtocol(), "file")) {
			throw new CPAttachmentFileEntryProtocolException(
				"Unsupported URL protocol");
		}

		URLConnection urlConnection = url.openConnection();

		urlConnection.connect();

		File file = FileUtil.createTempFile(urlConnection.getInputStream());

		return _addFileEntry(
			file, attachment.getContentType(), uniqueFileNameProvider,
			serviceContext);
	}

	public static FileEntry addFileEntry(
			AttachmentBase64 attachmentBase64,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		String base64EncodedContent = attachmentBase64.getAttachment();

		if (Validator.isNull(base64EncodedContent)) {
			return null;
		}

		File file = FileUtil.createTempFile(
			Base64.decode(base64EncodedContent));

		return _addFileEntry(
			file, attachmentBase64.getContentType(), uniqueFileNameProvider,
			serviceContext);
	}

	public static FileEntry addFileEntry(
			AttachmentUrl attachmentUrl,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		if (Validator.isNull(attachmentUrl.getSrc())) {
			return null;
		}

		File file = FileUtil.createTempFile(
			HttpUtil.URLtoInputStream(attachmentUrl.getSrc()));

		return _addFileEntry(
			file, attachmentUrl.getContentType(), uniqueFileNameProvider,
			serviceContext);
	}

	public static CPAttachmentFileEntry addOrUpdateCPAttachmentFileEntry(
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService,
			UniqueFileNameProvider uniqueFileNameProvider,
			AttachmentBase64 attachmentBase64, long classNameId, long classPK,
			int type, ServiceContext serviceContext)
		throws Exception {

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		if (attachmentBase64.getDisplayDate() != null) {
			displayCalendar = DateConfigUtil.convertDateToCalendar(
				attachmentBase64.getDisplayDate());
		}

		DateConfig displayDateConfig = new DateConfig(displayCalendar);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		expirationCalendar.add(Calendar.MONTH, 1);

		if (attachmentBase64.getExpirationDate() != null) {
			expirationCalendar = DateConfigUtil.convertDateToCalendar(
				attachmentBase64.getExpirationDate());
		}

		DateConfig expirationDateConfig = new DateConfig(expirationCalendar);

		long fileEntryId = 0;

		FileEntry fileEntry = addFileEntry(
			attachmentBase64, uniqueFileNameProvider, serviceContext);

		if (fileEntry != null) {
			fileEntryId = fileEntry.getFileEntryId();
		}

		long cpAttachmentFileEntryId = 0;

		if (Validator.isNull(attachmentBase64.getExternalReferenceCode())) {
			cpAttachmentFileEntryId = GetterUtil.getLong(
				attachmentBase64.getId());
		}

		return cpAttachmentFileEntryService.addOrUpdateCPAttachmentFileEntry(
			attachmentBase64.getExternalReferenceCode(),
			serviceContext.getScopeGroupId(), classNameId, classPK,
			cpAttachmentFileEntryId, fileEntryId, false, null,
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.get(attachmentBase64.getNeverExpire(), false),
			GetterUtil.get(attachmentBase64.getGalleryEnabled(), true),
			getTitleMap(null, attachmentBase64.getTitle()),
			_getJSON(
				null, cpDefinitionOptionRelService,
				cpDefinitionOptionValueRelService, cpOptionService,
				attachmentBase64.getOptions(), classPK,
				serviceContext.getCompanyId()),
			GetterUtil.getDouble(attachmentBase64.getPriority()), type,
			serviceContext);
	}

	public static CPAttachmentFileEntry addOrUpdateCPAttachmentFileEntry(
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService,
			UniqueFileNameProvider uniqueFileNameProvider,
			AttachmentUrl attachmentUrl, long classNameId, long classPK,
			int type, ServiceContext serviceContext)
		throws Exception {

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		if (attachmentUrl.getDisplayDate() != null) {
			displayCalendar = DateConfigUtil.convertDateToCalendar(
				attachmentUrl.getDisplayDate());
		}

		DateConfig displayDateConfig = new DateConfig(displayCalendar);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		expirationCalendar.add(Calendar.MONTH, 1);

		if (attachmentUrl.getExpirationDate() != null) {
			expirationCalendar = DateConfigUtil.convertDateToCalendar(
				attachmentUrl.getExpirationDate());
		}

		DateConfig expirationDateConfig = new DateConfig(expirationCalendar);

		long fileEntryId = 0;

		FileEntry fileEntry = addFileEntry(
			attachmentUrl, uniqueFileNameProvider, serviceContext);

		if (fileEntry != null) {
			fileEntryId = fileEntry.getFileEntryId();
		}

		long cpAttachmentFileEntryId = 0;

		if (Validator.isNull(attachmentUrl.getExternalReferenceCode())) {
			cpAttachmentFileEntryId = GetterUtil.getLong(attachmentUrl.getId());
		}

		return cpAttachmentFileEntryService.addOrUpdateCPAttachmentFileEntry(
			attachmentUrl.getExternalReferenceCode(),
			serviceContext.getScopeGroupId(), classNameId, classPK,
			cpAttachmentFileEntryId, fileEntryId, false, null,
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.get(attachmentUrl.getNeverExpire(), false),
			GetterUtil.get(attachmentUrl.getGalleryEnabled(), true),
			getTitleMap(null, attachmentUrl.getTitle()),
			_getJSON(
				null, cpDefinitionOptionRelService,
				cpDefinitionOptionValueRelService, cpOptionService,
				attachmentUrl.getOptions(), classPK,
				serviceContext.getCompanyId()),
			GetterUtil.getDouble(attachmentUrl.getPriority()), type,
			serviceContext);
	}

	public static CPAttachmentFileEntry addOrUpdateCPAttachmentFileEntry(
			long groupId,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService,
			DLAppLocalService dlAppLocalService,
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission,
			GroupLocalService groupLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			Attachment attachment, long classNameId, long classPK, int type,
			ServiceContext serviceContext)
		throws Exception {

		ServiceContext dlFileEntryCloneServiceContext =
			(ServiceContext)serviceContext.clone();

		dlFileEntryCloneServiceContext.setExpandoBridgeAttributes(
			new LinkedHashMap<>());

		dlFileEntryCloneServiceContext.setWorkflowAction(
			WorkflowConstants.ACTION_PUBLISH);

		long fileEntryId = GetterUtil.getLong(attachment.getFileEntryId());

		if (fileEntryId > 0) {
			FileEntry fileEntry = dlAppLocalService.fetchFileEntry(fileEntryId);

			if ((fileEntry == null) ||
				(fileEntry.getCompanyId() != serviceContext.getCompanyId())) {

				fileEntryId = 0;
			}
		}

		if (fileEntryId == 0) {
			String fileEntryExternalReferenceCode = GetterUtil.getString(
				attachment.getFileEntryExternalReferenceCode());

			if (Validator.isNull(fileEntryExternalReferenceCode)) {
				FileEntry fileEntry = addFileEntry(
					attachment, uniqueFileNameProvider,
					dlFileEntryCloneServiceContext);

				if (fileEntry != null) {
					fileEntryId = fileEntry.getFileEntryId();
				}
			}
			else {
				FileEntry fileEntry = null;

				Group group =
					groupLocalService.fetchGroupByExternalReferenceCode(
						attachment.getFileEntryGroupExternalReferenceCode(),
						serviceContext.getCompanyId());

				if (group != null) {
					fileEntry =
						dlAppLocalService.fetchFileEntryByExternalReferenceCode(
							group.getGroupId(), fileEntryExternalReferenceCode);
				}

				if ((fileEntry == null) &&
					LazyReferencingThreadLocal.isEnabled()) {

					fileEntry = addFileEntry(
						attachment, uniqueFileNameProvider,
						dlFileEntryCloneServiceContext);
				}

				if (fileEntry != null) {
					fileEntryId = fileEntry.getFileEntryId();
				}
				else if (group == null) {
					String fileEntryGroupExternalReferenceCode =
						attachment.getFileEntryGroupExternalReferenceCode();

					throw new NoSuchGroupException(
						"Unable to find group with external reference code " +
							fileEntryGroupExternalReferenceCode);
				}
			}
		}

		if (fileEntryId > 0) {
			dlFileEntryModelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(), fileEntryId,
				ActionKeys.VIEW);
		}

		ServiceContext cloneServiceContext =
			(ServiceContext)serviceContext.clone();

		cloneServiceContext.setTimeZone(serviceContext.getTimeZone());
		cloneServiceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			cloneServiceContext.getTimeZone());

		if (attachment.getDisplayDate() != null) {
			displayCalendar = DateConfigUtil.convertDateToCalendar(
				attachment.getDisplayDate());
		}

		DateConfig displayDateConfig = new DateConfig(displayCalendar);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			cloneServiceContext.getTimeZone());

		expirationCalendar.add(Calendar.MONTH, 1);

		if (attachment.getExpirationDate() != null) {
			expirationCalendar = DateConfigUtil.convertDateToCalendar(
				attachment.getExpirationDate());
		}

		DateConfig expirationDateConfig = new DateConfig(expirationCalendar);

		long cpAttachmentFileEntryId = 0;

		if (Validator.isNull(attachment.getExternalReferenceCode())) {
			cpAttachmentFileEntryId = GetterUtil.getLong(attachment.getId());
		}

		return cpAttachmentFileEntryService.addOrUpdateCPAttachmentFileEntry(
			attachment.getExternalReferenceCode(), groupId, classNameId,
			classPK, cpAttachmentFileEntryId, fileEntryId,
			GetterUtil.get(attachment.getCdnEnabled(), false),
			GetterUtil.getString(attachment.getCdnURL()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.get(attachment.getNeverExpire(), false),
			GetterUtil.get(attachment.getGalleryEnabled(), true),
			getTitleMap(null, attachment.getTitle()),
			_getJSON(
				null, cpDefinitionOptionRelService,
				cpDefinitionOptionValueRelService, cpOptionService,
				attachment.getOptions(), classPK,
				serviceContext.getCompanyId()),
			GetterUtil.getDouble(attachment.getPriority()), type,
			cloneServiceContext);
	}

	public static Map<Locale, String> getTitleMap(
			CPAttachmentFileEntry cpAttachmentFileEntry,
			Map<String, String> titleMap)
		throws PortalException {

		if (titleMap != null) {
			return LanguageUtils.getLocalizedMap(titleMap);
		}

		if (cpAttachmentFileEntry == null) {
			return null;
		}

		return cpAttachmentFileEntry.getTitleMap();
	}

	public static CPAttachmentFileEntry updateCPAttachmentFileEntry(
			CPAttachmentFileEntry cpAttachmentFileEntry,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService,
			DLAppLocalService dlAppLocalService,
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission,
			GroupLocalService groupLocalService, Attachment attachment,
			long classPK, int type, ServiceContext serviceContext)
		throws Exception {

		long fileEntryId = GetterUtil.getLong(attachment.getFileEntryId());

		if (fileEntryId > 0) {
			FileEntry fileEntry = dlAppLocalService.fetchFileEntry(fileEntryId);

			if ((fileEntry == null) ||
				(fileEntry.getCompanyId() != serviceContext.getCompanyId())) {

				fileEntryId = 0;
			}
		}

		if (fileEntryId == 0) {
			String fileEntryExternalReferenceCode = GetterUtil.getString(
				attachment.getFileEntryExternalReferenceCode());

			if (Validator.isNull(fileEntryExternalReferenceCode)) {
				fileEntryId = cpAttachmentFileEntry.getFileEntryId();
			}
			else {
				FileEntry fileEntry = null;

				Group group =
					groupLocalService.fetchGroupByExternalReferenceCode(
						attachment.getFileEntryGroupExternalReferenceCode(),
						serviceContext.getCompanyId());

				if (group != null) {
					fileEntry =
						dlAppLocalService.fetchFileEntryByExternalReferenceCode(
							group.getGroupId(), fileEntryExternalReferenceCode);
				}

				if (fileEntry != null) {
					fileEntryId = fileEntry.getFileEntryId();
				}
				else {
					fileEntryId = cpAttachmentFileEntry.getFileEntryId();
				}
			}
		}

		if (fileEntryId > 0) {
			dlFileEntryModelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(), fileEntryId,
				ActionKeys.VIEW);
		}

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		if (attachment.getDisplayDate() != null) {
			displayCalendar = DateConfigUtil.convertDateToCalendar(
				attachment.getDisplayDate());
		}

		DateConfig displayDateConfig = new DateConfig(displayCalendar);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		expirationCalendar.add(Calendar.MONTH, 1);

		if (attachment.getExpirationDate() != null) {
			expirationCalendar = DateConfigUtil.convertDateToCalendar(
				attachment.getExpirationDate());
		}

		DateConfig expirationDateConfig = new DateConfig(expirationCalendar);

		String options = _getJSON(
			cpAttachmentFileEntry, cpDefinitionOptionRelService,
			cpDefinitionOptionValueRelService, cpOptionService,
			attachment.getOptions(), classPK, serviceContext.getCompanyId());

		return cpAttachmentFileEntryService.updateCPAttachmentFileEntry(
			GetterUtil.getLong(
				attachment.getId(),
				cpAttachmentFileEntry.getCPAttachmentFileEntryId()),
			fileEntryId, GetterUtil.get(attachment.getCdnEnabled(), false),
			GetterUtil.getString(
				attachment.getCdnURL(), cpAttachmentFileEntry.getCDNURL()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.get(attachment.getNeverExpire(), false),
			GetterUtil.get(attachment.getGalleryEnabled(), true),
			getTitleMap(cpAttachmentFileEntry, attachment.getTitle()), options,
			GetterUtil.getDouble(
				attachment.getPriority(), cpAttachmentFileEntry.getPriority()),
			type, serviceContext);
	}

	private static FileEntry _addFileEntry(
			File file, String contentType,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		String uniqueFileName = uniqueFileNameProvider.provide(
			file.getName(),
			curFileName -> _exists(
				serviceContext.getScopeGroupId(), serviceContext.getUserId(),
				curFileName));

		if (Validator.isNull(contentType)) {
			contentType = MimeTypesUtil.getContentType(file);
		}

		uniqueFileName = _appendExtension(contentType, uniqueFileName);

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, uniqueFileName,
			contentType, uniqueFileName, StringPool.BLANK, null,
			StringPool.BLANK, file, null, null, null, serviceContext);

		FileUtil.delete(file);

		return fileEntry;
	}

	private static String _appendExtension(
		String contentType, String uniqueFileName) {

		String extension = StringPool.BLANK;

		Set<String> extensions = MimeTypesUtil.getExtensions(contentType);

		if (!extensions.isEmpty()) {
			Iterator<String> iterator = extensions.iterator();

			if (iterator.hasNext()) {
				extension = iterator.next();
			}
		}

		return uniqueFileName.concat(extension);
	}

	private static boolean _exists(
		long groupId, long userId, String curFileName) {

		try {
			FileEntry fileEntry = TempFileEntryUtil.getTempFileEntry(
				groupId, userId, _TEMP_FILE_NAME, curFileName);

			if (fileEntry != null) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static String _getJSON(
		CPAttachmentFileEntry cpAttachmentFileEntry,
		CPDefinitionOptionRelService cpDefinitionOptionRelService,
		CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
		CPOptionService cpOptionService, Map<String, String> options,
		long classPK, long companyId) {

		if (options == null) {
			if (cpAttachmentFileEntry != null) {
				return GetterUtil.getString(cpAttachmentFileEntry.getJson());
			}

			return StringPool.BLANK;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Map.Entry<String, String> entry : options.entrySet()) {
			jsonArray.put(
				() -> {
					CPOption cpOption = cpOptionService.fetchCPOption(
						companyId, entry.getKey());

					if (cpOption == null) {
						throw new NoSuchCPOptionException();
					}

					CPDefinitionOptionRel cpDefinitionOptionRel =
						cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
							classPK, cpOption.getCPOptionId());

					if ((cpDefinitionOptionRel != null) &&
						(cpDefinitionOptionRel.getCPDefinitionId() ==
							classPK)) {

						CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
							cpDefinitionOptionValueRelService.
								fetchCPDefinitionOptionValueRel(
									cpDefinitionOptionRel.
										getCPDefinitionOptionRelId(),
									entry.getValue());

						if (cpDefinitionOptionValueRel == null) {
							throw new NoSuchCPDefinitionOptionValueRelException();
						}

						return JSONUtil.put(
							"key", cpDefinitionOptionRel.getKey()
						).put(
							"value",
							JSONUtil.put(cpDefinitionOptionValueRel.getKey())
						);
					}

					throw new NoSuchCPDefinitionOptionRelException();
				});
		}

		return jsonArray.toString();
	}

	private static final String _TEMP_FILE_NAME =
		AttachmentUtil.class.getName();

	private static final Log _log = LogFactoryUtil.getLog(AttachmentUtil.class);

}