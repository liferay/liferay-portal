/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.rest.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PreviewPortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ImportPreviewResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.ws.rs.BadRequestException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/import-preview.properties",
	scope = ServiceScope.PROTOTYPE, service = ImportPreviewResource.class
)
public class ImportPreviewResourceImpl extends BaseImportPreviewResourceImpl {

	@Override
	public ImportPreview postAssetLibraryImportPreview(
			String assetLibraryExternalReferenceCode, Long plid,
			String portletId, MultipartBody multipartBody)
		throws Exception {

		return _getImportPreview(
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			multipartBody, GetterUtil.getLong(plid), portletId);
	}

	@Override
	public ImportPreview postImportPreview(
			Long plid, String portletId, MultipartBody multipartBody)
		throws Exception {

		return _getImportPreview(
			GroupUtil.getCompanyGroup(contextCompany.getCompanyId()),
			multipartBody, GetterUtil.getLong(plid), portletId);
	}

	@Override
	public ImportPreview postSiteImportPreview(
			String siteExternalReferenceCode, Long plid, String portletId,
			MultipartBody multipartBody)
		throws Exception {

		return _getImportPreview(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			multipartBody, GetterUtil.getLong(plid), portletId);
	}

	private ExportImportConfiguration _addExportImportConfiguration(
			long groupId, long plid, String portletId)
		throws Exception {

		if (Validator.isBlank(portletId)) {
			return _exportImportConfigurationLocalService.
				addExportImportConfiguration(
					contextUser.getUserId(), groupId, null, null,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							contextUser.getUserId(), groupId, false, null,
							new HashMap<>(),
							contextAcceptLanguage.getPreferredLocale(),
							contextUser.getTimeZone()),
					new ServiceContext());
		}

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				contextUser.getUserId(), groupId, null, null,
				ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportPortletSettingsMap(
						contextUser.getUserId(), plid, groupId, portletId,
						new HashMap<>(),
						contextAcceptLanguage.getPreferredLocale(),
						contextUser.getTimeZone()),
				new ServiceContext());
	}

	private FileEntry _addTempFileEntry(
			long groupId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		String folderName = ImportPreviewResource.class.getName();

		String[] tempFileNames = _layoutService.getTempFileNames(
			groupId, folderName);

		for (String tempFileEntryName : tempFileNames) {
			_layoutService.deleteTempFileEntry(
				groupId, folderName, tempFileEntryName);
		}

		return _layoutService.addTempFileEntry(
			groupId, folderName, binaryFile.getFileName(),
			binaryFile.getInputStream(), binaryFile.getContentType());
	}

	private ImportPreview _getImportPreview(
			Group group, MultipartBody multipartBody, long plid,
			String portletId)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), groupId);

		FileEntry fileEntry = _addTempFileEntry(groupId, multipartBody);

		_validateImportFile(fileEntry, groupId, plid, portletId);

		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap = new LinkedHashMap<>();

		Locale locale = contextAcceptLanguage.getPreferredLocale();

		ManifestSummary manifestSummary =
			_exportImportHelper.getManifestSummary(
				contextUser.getUserId(), groupId, new HashMap<>(), fileEntry);

		boolean portletScoped = !Validator.isBlank(portletId);

		for (Portlet portlet : manifestSummary.getDataPortlets()) {
			if (portletScoped &&
				!StringUtil.equals(portlet.getPortletId(), portletId)) {

				continue;
			}

			PreviewPortletDataHandlerUtil.addPreviewPortletDataHandler(
				contextCompany.getCompanyId(), locale, manifestSummary, portlet,
				portlet.getPortletDataHandlerInstance(),
				PortletDataHandler::getImportPortletDataHandlerControls,
				portletScoped, previewPortletDataHandlersMap);
		}

		if (portletScoped) {
			Portlet portlet = _portletLocalService.getPortletById(
				contextCompany.getCompanyId(), portletId);

			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			if (portletDataHandler != null) {
				PreviewPortletDataHandlerUtil.
					addConfigurationPreviewPortletDataHandler(
						locale, portlet,
						portletDataHandler.
							getImportConfigurationPortletDataHandlerControls(
								portlet, manifestSummary),
						previewPortletDataHandlersMap);
			}
		}

		return new ImportPreview() {
			{
				setAdditionCount(
					() -> PreviewPortletDataHandlerUtil.getAdditionCount(
						previewPortletDataHandlersMap));
				setAuthor(fileEntry::getUserName);
				setDeletionCount(
					() -> PreviewPortletDataHandlerUtil.getDeletionCount(
						previewPortletDataHandlersMap));
				setExportDate(manifestSummary::getExportDate);
				setFileName(fileEntry::getFileName);
				setFileSize(fileEntry::getSize);
				setPreviewPortletDataHandlerSections(
					() ->
						PreviewPortletDataHandlerUtil.
							toPreviewPortletDataHandlerSections(
								locale, previewPortletDataHandlersMap));
			}
		};
	}

	private void _validateImportFile(
			FileEntry fileEntry, long groupId, long plid, String portletId)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_addExportImportConfiguration(groupId, plid, portletId);

		try {
			if (Validator.isBlank(portletId)) {
				_exportImportLocalService.validateImportLayoutsFile(
					exportImportConfiguration, fileEntry.getContentStream());
			}
			else {
				_exportImportLocalService.validateImportPortletInfo(
					exportImportConfiguration, fileEntry.getContentStream());
			}
		}
		catch (PortalException portalException) {
			_layoutService.deleteTempFileEntry(
				groupId, ImportPreviewResource.class.getName(),
				fileEntry.getFileName());

			JSONObject jsonObject = _staging.getExceptionMessagesJSONObject(
				contextAcceptLanguage.getPreferredLocale(), portalException,
				exportImportConfiguration);

			throw new BadRequestException(jsonObject.getString("message"));
		}
		finally {
			_exportImportConfigurationLocalService.
				deleteExportImportConfiguration(exportImportConfiguration);
		}
	}

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private Staging _staging;

}