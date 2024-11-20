/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.headless.delivery.dto.v1_0.DocumentMetadataSet;
import com.liferay.headless.delivery.resource.v1_0.DocumentMetadataSetResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/document-metadata-set.properties",
	scope = ServiceScope.PROTOTYPE, service = DocumentMetadataSetResource.class
)
public class DocumentMetadataSetResourceImpl
	extends BaseDocumentMetadataSetResourceImpl {

	@Override
	public void deleteAssetLibraryDocumentMetadataSetByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		deleteSiteDocumentMetadataSetByExternalReferenceCode(
			assetLibraryId, externalReferenceCode);
	}

	@Override
	public void deleteDocumentMetadataSet(Long documentMetadataSetId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		DataDefinitionResource.Builder builder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource = builder.user(
			contextUser
		).build();

		dataDefinitionResource.deleteDataDefinition(documentMetadataSetId);
	}

	@Override
	public void deleteSiteDocumentMetadataSetByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		DocumentMetadataSet documentMetadataSet =
			getSiteDocumentMetadataSetByExternalReferenceCode(
				siteId, externalReferenceCode);

		deleteDocumentMetadataSet(documentMetadataSet.getId());
	}

	@Override
	public DocumentMetadataSet
			getAssetLibraryDocumentMetadataSetByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		return getSiteDocumentMetadataSetByExternalReferenceCode(
			assetLibraryId, externalReferenceCode);
	}

	@Override
	public Page<DocumentMetadataSet> getAssetLibraryDocumentMetadataSetsPage(
			Long assetLibraryId, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		return _getPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postAssetLibraryDocumentMetadataSet",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE,
					"postAssetLibraryDocumentMetadataSetBatch",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getAssetLibraryDocumentMetadataSetsPage",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).build(),
			assetLibraryId, pagination);
	}

	@Override
	public DocumentMetadataSet getDocumentMetadataSet(
			Long documentMetadataSetId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		return _toDocumentMetadataSet(
			_ddmStructureService.getStructure(documentMetadataSetId));
	}

	@Override
	public DocumentMetadataSet
			getSiteDocumentMetadataSetByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		return _toDocumentMetadataSet(
			_ddmStructureService.getStructureByExternalReferenceCode(
				externalReferenceCode, siteId,
				_classNameLocalService.getClassNameId(
					DLFileEntryMetadata.class)));
	}

	@Override
	public Page<DocumentMetadataSet> getSiteDocumentMetadataSetsPage(
			Long siteId, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		return _getPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postSiteDocumentMetadataSet",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postSiteDocumentMetadataSetBatch",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteDocumentMetadataSetsPage",
					DLConstants.RESOURCE_NAME, siteId)
			).build(),
			siteId, pagination);
	}

	@Override
	public DocumentMetadataSet postAssetLibraryDocumentMetadataSet(
			Long assetLibraryId, DocumentMetadataSet documentMetadataSet)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		return postSiteDocumentMetadataSet(assetLibraryId, documentMetadataSet);
	}

	@Override
	public DocumentMetadataSet postSiteDocumentMetadataSet(
			Long siteId, DocumentMetadataSet documentMetadataSet)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32247")) {
			throw new UnsupportedOperationException();
		}

		return _addDocumentMetadataSet(siteId, documentMetadataSet);
	}

	@Override
	public DocumentMetadataSet
			putAssetLibraryDocumentMetadataSetByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return putSiteDocumentMetadataSetByExternalReferenceCode(
			assetLibraryId, externalReferenceCode, documentMetadataSet);
	}

	@Override
	public DocumentMetadataSet
			putSiteDocumentMetadataSetByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		DDMStructure ddmStructure =
			_ddmStructureService.fetchStructureByExternalReferenceCode(
				externalReferenceCode, siteId,
				_classNameLocalService.getClassNameId(
					DLFileEntryMetadata.class));

		if (ddmStructure != null) {
			return _updateDocumentMetadataSet(
				ddmStructure, documentMetadataSet);
		}

		return _addDocumentMetadataSet(siteId, documentMetadataSet);
	}

	private DocumentMetadataSet _addDocumentMetadataSet(
			long groupId, DocumentMetadataSet documentMetadataSet)
		throws Exception {

		DataDefinitionResource.Builder builder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource = builder.user(
			contextUser
		).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				groupId, "document-library",
				_getDataDefinition(documentMetadataSet));

		return _toDocumentMetadataSet(
			_ddmStructureService.getStructure(dataDefinition.getId()));
	}

	private DataDefinition _getDataDefinition(
		DocumentMetadataSet documentMetadataSet) {

		return new DataDefinition() {
			{
				setAvailableLanguageIds(
					documentMetadataSet::getAvailableLanguages);
				setDataDefinitionFields(
					documentMetadataSet::getDataDefinitionFields);
				setDefaultDataLayout(documentMetadataSet::getDataLayout);
				setDescription(
					() -> LocalizedValueUtil.toStringObjectMap(
						LocalizedMapUtil.getLocalizedMap(
							contextAcceptLanguage.getPreferredLocale(),
							documentMetadataSet.getDescription(),
							documentMetadataSet.getDescription_i18n())));
				setExternalReferenceCode(
					documentMetadataSet::getExternalReferenceCode);
				setName(
					() -> LocalizedValueUtil.toStringObjectMap(
						LocalizedMapUtil.getLocalizedMap(
							contextAcceptLanguage.getPreferredLocale(),
							documentMetadataSet.getName(),
							documentMetadataSet.getName_i18n())));
				setSiteId(() -> siteId);
				setUserId(contextUser::getUserId);
			}
		};
	}

	private Page<DocumentMetadataSet> _getPage(
			Map<String, Map<String, String>> actions, long groupId,
			Pagination pagination)
		throws Exception {

		Group group = groupLocalService.getGroup(groupId);
		long classNameId = _classNameLocalService.getClassNameId(
			DLFileEntryMetadata.class);

		return Page.of(
			actions,
			transform(
				_ddmStructureService.getStructures(
					group.getCompanyId(), new long[] {groupId}, classNameId,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				ddmStructure -> _toDocumentMetadataSet(ddmStructure)),
			pagination,
			_ddmStructureService.getStructuresCount(
				group.getCompanyId(), new long[] {groupId}, classNameId, null,
				WorkflowConstants.STATUS_ANY));
	}

	private DocumentMetadataSet _toDocumentMetadataSet(
			DDMStructure ddmStructure)
		throws Exception {

		return _documentMetadataSetDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"get",
					addAction(
						ActionKeys.VIEW, ddmStructure.getStructureId(),
						"getDocumentMetadataSet", ddmStructure.getUserId(),
						ResourceActionsUtil.getCompositeModelName(
							DLFileEntryMetadata.class.getName(),
							DDMStructure.class.getName()),
						ddmStructure.getGroupId())
				).build(),
				_dtoConverterRegistry, ddmStructure.getStructureId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private DocumentMetadataSet _updateDocumentMetadataSet(
			DDMStructure ddmStructure, DocumentMetadataSet documentMetadataSet)
		throws Exception {

		DataDefinitionResource.Builder builder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource = builder.user(
			contextUser
		).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.patchDataDefinition(
				ddmStructure.getStructureId(),
				_getDataDefinition(documentMetadataSet));

		return _toDocumentMetadataSet(
			_ddmStructureService.getStructure(dataDefinition.getId()));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.DocumentMetadataSetDTOConverter)"
	)
	private DTOConverter<DDMStructure, DocumentMetadataSet>
		_documentMetadataSetDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

}