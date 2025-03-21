/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.internal.util.FileEntryUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductVirtualSettingsFileEntryResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/product-virtual-settings-file-entry.properties",
	scope = ServiceScope.PROTOTYPE,
	service = ProductVirtualSettingsFileEntryResource.class
)
public class ProductVirtualSettingsFileEntryResourceImpl
	extends BaseProductVirtualSettingsFileEntryResourceImpl {

	@Override
	public void deleteProductVirtualSettingsFileEntry(Long id)
		throws Exception {

		_cpdVirtualSettingFileEntryService.deleteCPDVirtualSettingFileEntry(id);
	}

	@Override
	public Page<ProductVirtualSettingsFileEntry>
			getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
				Long id, Pagination pagination)
		throws Exception {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				id);

		return Page.of(
			transform(
				cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries(
					pagination.getStartPosition(), pagination.getEndPosition()),
				this::_toProductVirtualSettingsFileEntry),
			pagination,
			cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntriesCount());
	}

	@Override
	public ProductVirtualSettingsFileEntry getProductVirtualSettingsFileEntry(
			Long id)
		throws Exception {

		return _toProductVirtualSettingsFileEntry(
			_cpdVirtualSettingFileEntryService.getCPDVirtualSettingFileEntry(
				id));
	}

	@Override
	public ProductVirtualSettingsFileEntry patchProductVirtualSettingsFileEntry(
			Long id, MultipartBody multipartBody)
		throws Exception {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryService.getCPDVirtualSettingFileEntry(
				id);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				cpdVirtualSettingFileEntry.getCPDefinitionVirtualSettingId());

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionVirtualSetting.getClassPK());

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			multipartBody.getValueAsNullableInstance(
				"productVirtualSettingsFileEntry",
				ProductVirtualSettingsFileEntry.class);

		long fileEntryId = cpdVirtualSettingFileEntry.getFileEntryId();

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				binaryFile, cpDefinition.getGroupId(),
				_cpdVirtualSettingFileEntryService, _dlAppService,
				_repositoryLocalService, _uniqueFileNameProvider);
		}
		else if (productVirtualSettingsFileEntry.getAttachment() != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				productVirtualSettingsFileEntry.getAttachment(),
				productVirtualSettingsFileEntry.getUrl(),
				cpDefinition.getGroupId(), _cpdVirtualSettingFileEntryService,
				_dlAppService, _repositoryLocalService, _uniqueFileNameProvider,
				_serviceContextHelper.getServiceContext(
					cpDefinition.getGroupId()));
		}

		return _toProductVirtualSettingsFileEntry(
			_cpdVirtualSettingFileEntryService.updateCPDefinitionVirtualSetting(
				id, fileEntryId,
				GetterUtil.get(
					productVirtualSettingsFileEntry.getUrl(),
					cpdVirtualSettingFileEntry.getUrl()),
				GetterUtil.get(
					productVirtualSettingsFileEntry.getVersion(),
					cpdVirtualSettingFileEntry.getVersion())));
	}

	@Override
	public ProductVirtualSettingsFileEntry
			postProductVirtualSettingIdProductVirtualSettingsFileEntry(
				Long id, MultipartBody multipartBody)
		throws Exception {

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			multipartBody.getValueAsNullableInstance(
				"productVirtualSettingsFileEntry",
				ProductVirtualSettingsFileEntry.class);

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if ((binaryFile == null) &&
			(productVirtualSettingsFileEntry.getAttachment() == null)) {

			throw new BadRequestException("No file found in body");
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				id);

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionVirtualSetting.getClassPK());

		long fileEntryId = 0;

		if (binaryFile != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				binaryFile, cpDefinition.getGroupId(),
				_cpdVirtualSettingFileEntryService, _dlAppService,
				_repositoryLocalService, _uniqueFileNameProvider);
		}
		else if (productVirtualSettingsFileEntry.getAttachment() != null) {
			fileEntryId = FileEntryUtil.getFileEntryId(
				productVirtualSettingsFileEntry.getAttachment(),
				productVirtualSettingsFileEntry.getUrl(),
				cpDefinition.getGroupId(), _cpdVirtualSettingFileEntryService,
				_dlAppService, _repositoryLocalService, _uniqueFileNameProvider,
				_serviceContextHelper.getServiceContext(
					cpDefinition.getGroupId()));
		}

		return _toProductVirtualSettingsFileEntry(
			_cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getGroupId(),
				CPDefinition.class.getName(),
				cpDefinitionVirtualSetting.getClassPK(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				fileEntryId,
				GetterUtil.get(
					productVirtualSettingsFileEntry.getUrl(), StringPool.BLANK),
				GetterUtil.get(
					productVirtualSettingsFileEntry.getVersion(),
					StringPool.BLANK)));
	}

	private Map<String, Map<String, String>> _getActions(
			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry)
		throws Exception {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.getCPDefinitionVirtualSetting(
				cpdVirtualSettingFileEntry.getCPDefinitionVirtualSettingId());

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			ActionUtil.addAction(
				ActionKeys.UPDATE, getClass(),
				cpDefinitionVirtualSetting.getClassPK(),
				"deleteProductVirtualSettingsFileEntry",
				_cpDefinitionModelResourcePermission,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextUriInfo)
		).put(
			"get",
			ActionUtil.addAction(
				ActionKeys.VIEW, getClass(),
				cpDefinitionVirtualSetting.getClassPK(),
				"getProductVirtualSettingsFileEntry",
				_cpDefinitionModelResourcePermission,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextUriInfo)
		).put(
			"patch",
			ActionUtil.addAction(
				ActionKeys.UPDATE, getClass(),
				cpDefinitionVirtualSetting.getClassPK(),
				"patchProductVirtualSettingsFileEntry",
				_cpDefinitionModelResourcePermission,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextUriInfo)
		).build();
	}

	private ProductVirtualSettingsFileEntry _toProductVirtualSettingsFileEntry(
			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry)
		throws Exception {

		return _productVirtualSettingsFileEntryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(cpdVirtualSettingFileEntry), _dtoConverterRegistry,
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

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
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductVirtualSettingsFileEntryDTOConverter)"
	)
	private DTOConverter<CPDefinition, ProductVirtualSettingsFileEntry>
		_productVirtualSettingsFileEntryDTOConverter;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

}