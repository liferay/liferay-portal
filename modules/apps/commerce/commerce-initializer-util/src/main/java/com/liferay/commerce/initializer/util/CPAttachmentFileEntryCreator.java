/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URI;
import java.net.URLEncoder;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(service = CPAttachmentFileEntryCreator.class)
public class CPAttachmentFileEntryCreator {

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public CPAttachmentFileEntry addCPAttachmentFileEntry(
			ClassedModel classedModel, ClassLoader classLoader,
			String dependenciesPath, String fileName, double priority, int type,
			long scopeGroupId, long userId)
		throws Exception {

		User user = _userLocalService.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		Map<Locale, String> titleMap = HashMapBuilder.put(
			serviceContext.getLocale(), fileName
		).build();

		InputStream inputStream = null;

		String uriString =
			dependenciesPath + URLEncoder.encode(fileName, "UTF-8");

		URI uri = new URI(uriString);

		if (StringUtil.equalsIgnoreCase(uri.getScheme(), "file")) {
			File file = new File(uri.getPath());

			if (file.exists()) {
				inputStream = new FileInputStream(file);
			}
		}
		else {
			inputStream = classLoader.getResourceAsStream(
				dependenciesPath + fileName);
		}

		if (inputStream == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("resource not found: " + uri.toString());
			}

			return null;
		}

		File file = null;

		FileEntry fileEntry = null;

		try {
			fileEntry = _dlAppService.fetchFileEntry(
				serviceContext.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName);

			if (fileEntry == null) {
				Repository repository = _repositoryProvider.getRepository(
					serviceContext.getScopeGroupId());

				file = _file.createTempFile(inputStream);

				fileEntry = _dlAppService.addFileEntry(
					null, repository.getRepositoryId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
					_mimeTypes.getContentType(file), fileName, null, null, null,
					file, null, null, null, serviceContext);
			}
		}
		finally {
			if (file != null) {
				_file.delete(file);
			}

			inputStream.close();
		}

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		displayCalendar.add(Calendar.YEAR, -1);

		int displayDateMonth = displayCalendar.get(Calendar.MONTH);
		int displayDateDay = displayCalendar.get(Calendar.DAY_OF_MONTH);
		int displayDateYear = displayCalendar.get(Calendar.YEAR);
		int displayDateHour = displayCalendar.get(Calendar.HOUR);
		int displayDateMinute = displayCalendar.get(Calendar.MINUTE);
		int displayDateAmPm = displayCalendar.get(Calendar.AM_PM);

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		expirationCalendar.add(Calendar.MONTH, 1);

		int expirationDateMonth = expirationCalendar.get(Calendar.MONTH);
		int expirationDateDay = expirationCalendar.get(Calendar.DAY_OF_MONTH);
		int expirationDateYear = expirationCalendar.get(Calendar.YEAR);
		int expirationDateHour = expirationCalendar.get(Calendar.HOUR);
		int expirationDateMinute = expirationCalendar.get(Calendar.MINUTE);
		int expirationDateAmPm = expirationCalendar.get(Calendar.AM_PM);

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		long classPK = GetterUtil.getLong(classedModel.getPrimaryKeyObj());

		return _cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
			StringPool.BLANK, serviceContext.getUserId(),
			fileEntry.getGroupId(),
			_portal.getClassNameId(classedModel.getModelClass()), classPK,
			fileEntry.getFileEntryId(), false, null, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, true, true, titleMap,
			null, priority, type, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPAttachmentFileEntryCreator.class);

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryProvider _repositoryProvider;

	@Reference
	private UserLocalService _userLocalService;

}