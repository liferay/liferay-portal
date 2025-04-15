/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.zip.processor;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.exception.DuplicateStyleBookEntryKeyException;
import com.liferay.style.book.exception.StyleBookEntryThemeIdException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessor;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessorImportResultEntry;

import jakarta.portlet.PortletException;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = StyleBookEntryZipProcessor.class)
public class StyleBookEntryZipProcessorImpl
	implements StyleBookEntryZipProcessor {

	@Override
	public File exportStyleBookEntries(List<StyleBookEntry> styleBookEntries)
		throws PortletException {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		try {
			for (StyleBookEntry styleBookEntry : styleBookEntries) {
				styleBookEntry.populateZipWriter(zipWriter, StringPool.BLANK);
			}

			return zipWriter.getFile();
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Override
	public List<StyleBookEntryZipProcessorImportResultEntry>
			importStyleBookEntries(
				long userId, long groupId, File file, boolean overwrite)
		throws Exception {

		_importResultEntries = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				if (zipEntry.isDirectory()) {
					continue;
				}

				String fileName = zipEntry.getName();

				if (!_isStyleBookEntry(fileName)) {
					continue;
				}

				_importStyleBookEntries(
					userId, groupId, zipFile, fileName, overwrite);
			}
		}

		return _importResultEntries;
	}

	private StyleBookEntry _addStyleBookEntry(
			long groupId, String frontendTokensValues, String name,
			boolean overwrite, String styleBookEntryKey, String themeId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		if (FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-30204") &&
			Validator.isBlank(themeId)) {

			throw new StyleBookEntryThemeIdException.MustNotBeNull();
		}

		StyleBookEntry styleBookEntry =
			_styleBookEntryEntryLocalService.fetchStyleBookEntry(
				groupId, styleBookEntryKey);

		if ((styleBookEntry != null) && !overwrite) {
			throw new DuplicateStyleBookEntryKeyException(styleBookEntryKey);
		}

		try {
			if (styleBookEntry == null) {
				styleBookEntry = _styleBookEntryEntryService.addStyleBookEntry(
					null, groupId, frontendTokensValues, name,
					styleBookEntryKey, themeId,
					ServiceContextThreadLocal.getServiceContext());
			}
			else {
				styleBookEntry =
					_styleBookEntryEntryService.updateStyleBookEntry(
						styleBookEntry.getStyleBookEntryId(),
						frontendTokensValues, name);
			}

			_importResultEntries.add(
				new StyleBookEntryZipProcessorImportResultEntry(
					name,
					StyleBookEntryZipProcessorImportResultEntry.Status.IMPORTED,
					styleBookEntry));

			return styleBookEntry;
		}
		catch (PortalException portalException) {
			_importResultEntries.add(
				new StyleBookEntryZipProcessorImportResultEntry(
					name,
					StyleBookEntryZipProcessorImportResultEntry.Status.INVALID,
					portalException.getMessage()));
		}

		return null;
	}

	private String _getContent(ZipFile zipFile, String fileName)
		throws Exception {

		ZipEntry zipEntry = zipFile.getEntry(fileName);

		if (zipEntry == null) {
			return StringPool.BLANK;
		}

		return StringUtil.read(zipFile.getInputStream(zipEntry));
	}

	private String _getFileName(String path) {
		int pos = path.lastIndexOf(CharPool.SLASH);

		if (pos > 0) {
			return path.substring(pos + 1);
		}

		return path;
	}

	private InputStream _getInputStream(ZipFile zipFile, String fileName)
		throws Exception {

		ZipEntry zipEntry = zipFile.getEntry(fileName);

		if (zipEntry == null) {
			return null;
		}

		return zipFile.getInputStream(zipEntry);
	}

	private String _getKey(ZipFile zipFile, long groupId, String fileName)
		throws Exception {

		String key = StringPool.BLANK;

		if (fileName.lastIndexOf(CharPool.SLASH) != -1) {
			String path = fileName.substring(
				0, fileName.lastIndexOf(CharPool.SLASH));

			key = path.substring(path.lastIndexOf(CharPool.SLASH) + 1);
		}
		else if (fileName.equals("style-book.json")) {
			JSONObject styleBookJSONObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					zipFile.getInputStream(zipFile.getEntry(fileName))));

			key = _styleBookEntryEntryLocalService.generateStyleBookEntryKey(
				groupId, styleBookJSONObject.getString("name"));
		}

		if (Validator.isNotNull(key)) {
			return key;
		}

		throw new IllegalArgumentException("Incorrect file name " + fileName);
	}

	private long _getPreviewFileEntryId(
			long userId, long groupId, ZipFile zipFile, String className,
			long classPK, String fileName, String contentPath)
		throws Exception {

		InputStream inputStream = _getStyleBookEntryInputStream(
			zipFile, fileName, contentPath);

		if (inputStream == null) {
			return 0;
		}

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				groupId, StyleBookPortletKeys.STYLE_BOOK);

		if (repository == null) {
			if (groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
				StyleBookEntry styleBookEntry =
					_styleBookEntryEntryLocalService.getStyleBookEntry(classPK);

				Company company = _companyLocalService.getCompany(
					styleBookEntry.getCompanyId());

				groupId = company.getGroupId();
			}

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				groupId, StyleBookPortletKeys.STYLE_BOOK, serviceContext);
		}

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, groupId, userId, className, classPK,
			StyleBookPortletKeys.STYLE_BOOK, repository.getDlFolderId(),
			inputStream,
			classPK + "_preview." + FileUtil.getExtension(contentPath),
			MimeTypesUtil.getContentType(contentPath), false);

		return fileEntry.getFileEntryId();
	}

	private String _getStyleBookEntryContent(
			ZipFile zipFile, String fileName, String contentPath)
		throws Exception {

		InputStream inputStream = _getStyleBookEntryInputStream(
			zipFile, fileName, contentPath);

		if (inputStream == null) {
			return StringPool.BLANK;
		}

		return StringUtil.read(inputStream);
	}

	private InputStream _getStyleBookEntryInputStream(
			ZipFile zipFile, String fileName, String contentPath)
		throws Exception {

		if (contentPath.startsWith(StringPool.SLASH)) {
			return _getInputStream(zipFile, contentPath.substring(1));
		}

		if (contentPath.startsWith("./")) {
			contentPath = contentPath.substring(2);
		}

		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.SLASH));

		return _getInputStream(zipFile, path + StringPool.SLASH + contentPath);
	}

	private void _importStyleBookEntries(
			long userId, long groupId, ZipFile zipFile, String fileName,
			boolean overwrite)
		throws Exception {

		boolean defaultStyleBookEntry = false;

		String styleBookEntryKey = _getKey(zipFile, groupId, fileName);

		String name = styleBookEntryKey;

		String frontendTokensValues = StringPool.BLANK;

		String styleBookEntryContent = _getContent(zipFile, fileName);

		String themeId = StringPool.BLANK;

		if (Validator.isNotNull(styleBookEntryContent)) {
			JSONObject styleBookEntryJSONObject = _jsonFactory.createJSONObject(
				styleBookEntryContent);

			defaultStyleBookEntry = styleBookEntryJSONObject.getBoolean(
				"defaultStyleBookEntry");
			frontendTokensValues = _getStyleBookEntryContent(
				zipFile, fileName,
				styleBookEntryJSONObject.getString("frontendTokensValuesPath"));
			name = styleBookEntryJSONObject.getString("name");
			themeId = styleBookEntryJSONObject.getString("themeId");
		}

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			groupId, frontendTokensValues, name, overwrite, styleBookEntryKey,
			themeId);

		if (styleBookEntry == null) {
			return;
		}

		if (defaultStyleBookEntry) {
			_styleBookEntryEntryService.updateDefaultStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(), true);
		}

		if (Validator.isNotNull(styleBookEntryContent)) {
			if (styleBookEntry.getPreviewFileEntryId() > 0) {
				PortletFileRepositoryUtil.deletePortletFileEntry(
					styleBookEntry.getPreviewFileEntryId());
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				styleBookEntryContent);

			String thumbnailPath = jsonObject.getString("thumbnailPath");

			if (Validator.isNotNull(thumbnailPath)) {
				_styleBookEntryEntryService.updatePreviewFileEntryId(
					styleBookEntry.getStyleBookEntryId(),
					_getPreviewFileEntryId(
						userId, groupId, zipFile,
						StyleBookEntry.class.getName(),
						styleBookEntry.getStyleBookEntryId(), fileName,
						thumbnailPath));
			}
		}
	}

	private boolean _isStyleBookEntry(String fileName) {
		return Objects.equals(_getFileName(fileName), "style-book.json");
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private List<StyleBookEntryZipProcessorImportResultEntry>
		_importResultEntries;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryEntryLocalService;

	@Reference
	private StyleBookEntryService _styleBookEntryEntryService;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}