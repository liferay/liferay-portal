/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.resource.v1_0.DocumentDataDefinitionTypeResource;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/document-data-definition-type.properties",
	scope = ServiceScope.PROTOTYPE,
	service = DocumentDataDefinitionTypeResource.class
)
public class DocumentDataDefinitionTypeResourceImpl
	extends BaseDocumentDataDefinitionTypeResourceImpl {

	@Override
	public void deleteDocumentDataDefinitionType(
			Long documentDataDefinitionTypeId)
		throws Exception {

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				contextUser
			).build();

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeService.getFileEntryType(
				documentDataDefinitionTypeId);

		dataDefinitionResource.deleteDataDefinition(
			dlFileEntryType.getDataDefinitionId());

		_dlFileEntryTypeService.deleteFileEntryType(
			documentDataDefinitionTypeId);
	}

	@Override
	public Page<DocumentDataDefinitionType>
			getAssetLibraryDocumentDataDefinitionTypesPage(
				Long assetLibraryId, String search, Aggregation aggregation,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getDocumentDataDefinitionTypePage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_DOCUMENT_TYPE,
					"postAssetLibraryDocumentDataDefinitionType",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DOCUMENT_TYPE,
					"postAssetLibraryDocumentDataDefinitionTypeBatch",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).build(),
			assetLibraryId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public DocumentDataDefinitionType getDocumentDataDefinitionType(
			Long documentDataDefinitionTypeId)
		throws Exception {

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeService.getFileEntryType(
				documentDataDefinitionTypeId);

		return _toDocumentDataDefinitionType(dlFileEntryType);
	}

	@Override
	public Page<DocumentDataDefinitionType>
			getSiteDocumentDataDefinitionTypesPage(
				Long siteId, String search, Aggregation aggregation,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getDocumentDataDefinitionTypePage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_DOCUMENT_TYPE,
					"postSiteDocumentDataDefinitionType",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DOCUMENT_TYPE,
					"postSiteDocumentDataDefinitionTypeBatch",
					DLConstants.RESOURCE_NAME, siteId)
			).build(),
			siteId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public DocumentDataDefinitionType
			postAssetLibraryDocumentDataDefinitionType(
				Long assetLibraryId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return postSiteDocumentDataDefinitionType(
			assetLibraryId, documentDataDefinitionType);
	}

	@Override
	public DocumentDataDefinitionType postSiteDocumentDataDefinitionType(
			Long siteId, DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		DataDefinitionResource.Builder builder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource = builder.user(
			contextUser
		).build();

		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			documentDataDefinitionType.getDescription(),
			documentDataDefinitionType.getDescription_i18n());
		Map<Locale, String> nameMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			documentDataDefinitionType.getName(),
			documentDataDefinitionType.getName_i18n());

		DataDefinition dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				siteId, "document-library",
				new DataDefinition() {
					{
						setAvailableLanguageIds(
							documentDataDefinitionType::getAvailableLanguages);
						setDataDefinitionFields(
							documentDataDefinitionType::
								getDataDefinitionFields);
						setDefaultDataLayout(
							documentDataDefinitionType::getDataLayout);
						setDescription(
							() -> LocalizedValueUtil.toStringObjectMap(
								descriptionMap));
						setName(
							() -> LocalizedValueUtil.toStringObjectMap(
								nameMap));
						setUserId(contextUser::getUserId);
					}
				});

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeService.addFileEntryType(
				documentDataDefinitionType.getExternalReferenceCode(), siteId,
				dataDefinition.getId(), null, nameMap, descriptionMap,
				ServiceContextBuilder.create(
					siteId, contextHttpServletRequest,
					documentDataDefinitionType.getViewableByAsString()
				).build());

		if (ArrayUtil.isNotEmpty(
				documentDataDefinitionType.getDocumentMetadataSetIds())) {

			_dlFileEntryTypeLocalService.addDDMStructureLinks(
				dlFileEntryType.getFileEntryTypeId(),
				SetUtil.fromArray(
					documentDataDefinitionType.getDocumentMetadataSetIds()));
		}

		return _toDocumentDataDefinitionType(dlFileEntryType);
	}

	private Page<DocumentDataDefinitionType> _getDocumentDataDefinitionTypePage(
			Map<String, Map<String, String>> actions, Long siteId,
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions,
			booleanQuery -> {
			},
			filter, DLFileEntryType.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});
			},
			sorts,
			document -> _toDocumentDataDefinitionType(
				_dlFileEntryTypeService.getFileEntryType(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private DocumentDataDefinitionType _toDocumentDataDefinitionType(
			DLFileEntryType dlFileEntryType)
		throws Exception {

		return _documentDataDefinitionTypeDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, dlFileEntryType.getFileEntryTypeId(),
						"deleteDocumentDataDefinitionType",
						dlFileEntryType.getUserId(),
						DLFileEntryType.class.getName(),
						dlFileEntryType.getGroupId())
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, dlFileEntryType.getFileEntryTypeId(),
						"getDocumentDataDefinitionType",
						dlFileEntryType.getUserId(),
						DLFileEntryType.class.getName(),
						dlFileEntryType.getGroupId())
				).build(),
				_dtoConverterRegistry, dlFileEntryType.getFileEntryTypeId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			dlFileEntryType);
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.DocumentDataDefinitionTypeDTOConverter)"
	)
	private DTOConverter<DLFileEntryType, DocumentDataDefinitionType>
		_documentDataDefinitionTypeDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

}