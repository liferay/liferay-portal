/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.internal.util.FileEntryUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.upload.UniqueFileNameProvider;

import jakarta.ws.rs.BadRequestException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Danny Situ
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/sku-virtual-settings-file-entry.properties",
	scope = ServiceScope.PROTOTYPE,
	service = SkuVirtualSettingsFileEntryResource.class
)
public class SkuVirtualSettingsFileEntryResourceImpl
	extends BaseSkuVirtualSettingsFileEntryResourceImpl {

	@Override
	public void deleteSkuVirtualSettingsFileEntry(Long id) throws Exception {
		_cpdVirtualSettingFileEntryService.deleteCPDVirtualSettingFileEntry(id);
	}

	@Override
	public Page<SkuVirtualSettingsFileEntry>
			getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
				Long id, Pagination pagination)
		throws Exception {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				id);

		return Page.of(
			transform(
				cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries(
					pagination.getStartPosition(), pagination.getEndPosition()),
				this::_toSkuVirtualSettingsFileEntry),
			pagination,
			cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntriesCount());
	}

	@Override
	public SkuVirtualSettingsFileEntry getSkuVirtualSettingsFileEntry(Long id)
		throws Exception {

		return _toSkuVirtualSettingsFileEntry(
			_cpdVirtualSettingFileEntryService.getCPDVirtualSettingFileEntry(
				id));
	}

	@Override
	public SkuVirtualSettingsFileEntry patchSkuVirtualSettingsFileEntry(
			Long id, MultipartBody multipartBody)
		throws Exception {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryService.getCPDVirtualSettingFileEntry(
				id);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinitionVirtualSetting.getClassPK());

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry =
			multipartBody.getValueAsNullableInstance(
				"skuVirtualSettingsFileEntry",
				SkuVirtualSettingsFileEntry.class);

		long fileEntryId = cpdVirtualSettingFileEntry.getFileEntryId();

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				binaryFile, cpInstance.getGroupId(),
				_cpdVirtualSettingFileEntryService, _dlAppService,
				_repositoryLocalService, _uniqueFileNameProvider);
		}
		else if (skuVirtualSettingsFileEntry.getAttachment() != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				skuVirtualSettingsFileEntry.getAttachment(),
				skuVirtualSettingsFileEntry.getUrl(), cpInstance.getGroupId(),
				_cpdVirtualSettingFileEntryService, _dlAppService,
				_repositoryLocalService, _uniqueFileNameProvider,
				_serviceContextHelper.getServiceContext(
					cpInstance.getGroupId()));
		}

		return _toSkuVirtualSettingsFileEntry(
			_cpdVirtualSettingFileEntryService.updateCPDefinitionVirtualSetting(
				id, fileEntryId,
				GetterUtil.get(
					skuVirtualSettingsFileEntry.getUrl(),
					cpdVirtualSettingFileEntry.getUrl()),
				GetterUtil.get(
					skuVirtualSettingsFileEntry.getVersion(),
					cpdVirtualSettingFileEntry.getVersion())));
	}

	@Override
	public SkuVirtualSettingsFileEntry
			postSkuVirtualSettingIdSkuVirtualSettingsFileEntry(
				Long id, MultipartBody multipartBody)
		throws Exception {

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry =
			multipartBody.getValueAsNullableInstance(
				"skuVirtualSettingsFileEntry",
				SkuVirtualSettingsFileEntry.class);

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if ((binaryFile == null) &&
			(skuVirtualSettingsFileEntry.getAttachment() == null)) {

			throw new BadRequestException("No file found in body");
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				id);

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinitionVirtualSetting.getClassPK());

		long fileEntryId = 0;

		if (binaryFile != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				binaryFile, cpInstance.getGroupId(),
				_cpdVirtualSettingFileEntryService, _dlAppService,
				_repositoryLocalService, _uniqueFileNameProvider);
		}
		else if (skuVirtualSettingsFileEntry.getAttachment() != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				skuVirtualSettingsFileEntry.getAttachment(),
				skuVirtualSettingsFileEntry.getUrl(), cpInstance.getGroupId(),
				_cpdVirtualSettingFileEntryService, _dlAppService,
				_repositoryLocalService, _uniqueFileNameProvider,
				_serviceContextHelper.getServiceContext(
					cpInstance.getGroupId()));
		}

		return _toSkuVirtualSettingsFileEntry(
			_cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getGroupId(),
				CPInstance.class.getName(),
				cpDefinitionVirtualSetting.getClassPK(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				fileEntryId,
				GetterUtil.get(
					skuVirtualSettingsFileEntry.getUrl(), StringPool.BLANK),
				GetterUtil.get(
					skuVirtualSettingsFileEntry.getVersion(),
					StringPool.BLANK)));
	}

	private Map<String, Map<String, String>> _getActions(
			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry)
		throws Exception {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				cpdVirtualSettingFileEntry.getCPDefinitionVirtualSettingId());

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinitionVirtualSetting.getClassPK());

		long cpDefinitionId = cpInstance.getCPDefinitionId();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			ActionUtil.addAction(
				ActionKeys.UPDATE, getClass(), cpDefinitionId,
				"deleteSkuVirtualSettingsFileEntry",
				_cpDefinitionModelResourcePermission,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextUriInfo)
		).put(
			"get",
			ActionUtil.addAction(
				ActionKeys.VIEW, getClass(), cpDefinitionId,
				"getSkuVirtualSettingsFileEntry",
				_cpDefinitionModelResourcePermission,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextUriInfo)
		).put(
			"patch",
			ActionUtil.addAction(
				ActionKeys.UPDATE, getClass(), cpDefinitionId,
				"patchSkuVirtualSettingsFileEntry",
				_cpDefinitionModelResourcePermission,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextUriInfo)
		).build();
	}

	private SkuVirtualSettingsFileEntry _toSkuVirtualSettingsFileEntry(
			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry)
		throws Exception {

		return _skuVirtualSettingsFileEntryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(cpdVirtualSettingFileEntry), _dtoConverterRegistry,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

	@Reference
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

	@Reference
	private CPDVirtualSettingFileEntryService
		_cpdVirtualSettingFileEntryService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.SkuVirtualSettingsFileEntryDTOConverter)"
	)
	private DTOConverter<CPDefinition, SkuVirtualSettingsFileEntry>
		_skuVirtualSettingsFileEntryDTOConverter;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

}