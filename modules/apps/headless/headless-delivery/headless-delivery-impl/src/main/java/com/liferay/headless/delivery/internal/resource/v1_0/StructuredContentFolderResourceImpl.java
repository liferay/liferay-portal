/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.StructuredContentFolder;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.StructuredContentFolderEntityModel;
import com.liferay.headless.delivery.resource.v1_0.StructuredContentFolderResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/structured-content-folder.properties",
	scope = ServiceScope.PROTOTYPE,
	service = StructuredContentFolderResource.class
)
public class StructuredContentFolderResourceImpl
	extends BaseStructuredContentFolderResourceImpl {

	@Override
	public void
			deleteAssetLibraryStructuredContentFolderByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		JournalFolder journalFolder =
			_journalFolderService.getFolderByExternalReferenceCode(
				assetLibraryId, externalReferenceCode);

		_journalFolderService.deleteFolder(journalFolder.getFolderId());
	}

	@Override
	public void deleteSiteStructuredContentFolderByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		JournalFolder journalFolder =
			_journalFolderService.getFolderByExternalReferenceCode(
				siteId, externalReferenceCode);

		_journalFolderService.deleteFolder(journalFolder.getFolderId());
	}

	@Override
	public void deleteStructuredContentFolder(Long structuredContentFolderId)
		throws Exception {

		_journalFolderService.deleteFolder(structuredContentFolderId);
	}

	@Override
	public StructuredContentFolder
			getAssetLibraryStructuredContentFolderByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		return _toStructuredContentFolder(
			_journalFolderService.getFolderByExternalReferenceCode(
				assetLibraryId, externalReferenceCode));
	}

	@Override
	public Page<StructuredContentFolder>
			getAssetLibraryStructuredContentFoldersPage(
				Long assetLibraryId, Boolean flatten, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		Long parentStructuredContentFolderId = null;

		if (!GetterUtil.getBoolean(flatten)) {
			parentStructuredContentFolderId =
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return _getStructuredContentFoldersPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE,
					"postAssetLibraryStructuredContentFolder",
					JournalConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE,
					"postAssetLibraryStructuredContentFolderBatch",
					JournalConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteStructuredContentFolderBatch",
					JournalConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW,
					"getAssetLibraryStructuredContentFoldersPage",
					JournalConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putStructuredContentFolderBatch",
					JournalConstants.RESOURCE_NAME, null)
			).build(),
			parentStructuredContentFolderId, assetLibraryId, search,
			aggregation, filter, pagination, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new StructuredContentFolderEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(JournalFolder.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public StructuredContentFolder
			getSiteStructuredContentFolderByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception {

		return _toStructuredContentFolder(
			_journalFolderService.getFolderByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public Page<StructuredContentFolder> getSiteStructuredContentFoldersPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Long parentStructuredContentFolderId = null;

		if (!GetterUtil.getBoolean(flatten)) {
			parentStructuredContentFolderId =
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return _getStructuredContentFoldersPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postSiteStructuredContentFolder",
					JournalConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postSiteStructuredContentFolderBatch",
					JournalConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteStructuredContentFolderBatch",
					JournalConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteStructuredContentFoldersPage",
					JournalConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putStructuredContentFolderBatch",
					JournalConstants.RESOURCE_NAME, null)
			).build(),
			parentStructuredContentFolderId, siteId, search, aggregation,
			filter, pagination, sorts);
	}

	@Override
	public StructuredContentFolder getStructuredContentFolder(
			Long structuredContentFolderId)
		throws Exception {

		return _toStructuredContentFolder(
			_journalFolderService.getFolder(structuredContentFolderId));
	}

	@Override
	public Page<StructuredContentFolder>
			getStructuredContentFolderStructuredContentFoldersPage(
				Long parentStructuredContentFolderId, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			parentStructuredContentFolderId);

		return _getStructuredContentFoldersPage(
			HashMapBuilder.put(
				"add-subfolder",
				addAction(
					ActionKeys.UPDATE, journalFolder,
					"postStructuredContentFolderStructuredContentFolder")
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, journalFolder,
					"getStructuredContentFolderStructuredContentFoldersPage")
			).put(
				"subscribe",
				addAction(
					ActionKeys.SUBSCRIBE, journalFolder,
					"putStructuredContentFolderSubscribe")
			).put(
				"unsubscribe",
				addAction(
					ActionKeys.SUBSCRIBE, journalFolder,
					"putStructuredContentFolderUnsubscribe")
			).build(),
			parentStructuredContentFolderId, journalFolder.getGroupId(), search,
			aggregation, filter, pagination, sorts);
	}

	@Override
	public StructuredContentFolder postAssetLibraryStructuredContentFolder(
			Long assetLibraryId,
			StructuredContentFolder structuredContentFolder)
		throws Exception {

		return postSiteStructuredContentFolder(
			assetLibraryId, structuredContentFolder);
	}

	@Override
	public StructuredContentFolder postSiteStructuredContentFolder(
			Long siteId, StructuredContentFolder structuredContentFolder)
		throws Exception {

		return _addStructuredContentFolder(
			structuredContentFolder.getExternalReferenceCode(), siteId,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			structuredContentFolder);
	}

	@Override
	public StructuredContentFolder
			postStructuredContentFolderStructuredContentFolder(
				Long parentStructuredContentFolderId,
				StructuredContentFolder structuredContentFolder)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			parentStructuredContentFolderId);

		return _addStructuredContentFolder(
			structuredContentFolder.getExternalReferenceCode(),
			journalFolder.getGroupId(), parentStructuredContentFolderId,
			structuredContentFolder);
	}

	@Override
	public StructuredContentFolder
			putAssetLibraryStructuredContentFolderByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				StructuredContentFolder structuredContentFolder)
		throws Exception {

		JournalFolder journalFolder =
			_journalFolderLocalService.
				fetchJournalFolderByExternalReferenceCode(
					externalReferenceCode, assetLibraryId);

		if (journalFolder != null) {
			return _updateStructuredContentFolder(
				assetLibraryId, journalFolder.getFolderId(),
				journalFolder.getParentFolderId(), structuredContentFolder);
		}

		return _addStructuredContentFolder(
			externalReferenceCode, assetLibraryId,
			structuredContentFolder.getParentStructuredContentFolderId(),
			structuredContentFolder);
	}

	@Override
	public StructuredContentFolder
			putSiteStructuredContentFolderByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				StructuredContentFolder structuredContentFolder)
		throws Exception {

		JournalFolder journalFolder =
			_journalFolderLocalService.
				fetchJournalFolderByExternalReferenceCode(
					externalReferenceCode, siteId);

		if (journalFolder != null) {
			return _updateStructuredContentFolder(
				siteId, journalFolder.getFolderId(),
				journalFolder.getParentFolderId(), structuredContentFolder);
		}

		return _addStructuredContentFolder(
			externalReferenceCode, siteId,
			structuredContentFolder.getParentStructuredContentFolderId(),
			structuredContentFolder);
	}

	@Override
	public StructuredContentFolder putStructuredContentFolder(
			Long structuredContentFolderId,
			StructuredContentFolder structuredContentFolder)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			structuredContentFolderId);

		return _toStructuredContentFolder(
			_journalFolderService.updateFolder(
				journalFolder.getGroupId(), structuredContentFolderId,
				journalFolder.getParentFolderId(),
				structuredContentFolder.getName(),
				structuredContentFolder.getDescription(), false,
				_createServiceContext(
					journalFolder.getGroupId(), structuredContentFolder)));
	}

	@Override
	public void putStructuredContentFolderSubscribe(
			Long structuredContentFolderId)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			structuredContentFolderId);

		_journalFolderService.subscribe(
			journalFolder.getGroupId(), journalFolder.getFolderId());
	}

	@Override
	public void putStructuredContentFolderUnsubscribe(
			Long structuredContentFolderId)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			structuredContentFolderId);

		_journalFolderService.unsubscribe(
			journalFolder.getGroupId(), journalFolder.getFolderId());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		JournalFolder journalFolder = _journalFolderService.getFolder((Long)id);

		return journalFolder.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return JournalConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return JournalFolder.class.getName();
	}

	private StructuredContentFolder _addStructuredContentFolder(
			String externalReferenceCode, Long siteId, Long parentFolderId,
			StructuredContentFolder structuredContentFolder)
		throws Exception {

		if (parentFolderId == null) {
			parentFolderId = JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return _toStructuredContentFolder(
			_journalFolderService.addFolder(
				externalReferenceCode, siteId, parentFolderId,
				structuredContentFolder.getName(),
				structuredContentFolder.getDescription(),
				_createServiceContext(siteId, structuredContentFolder)));
	}

	private ServiceContext _createServiceContext(
		long groupId, StructuredContentFolder structuredContentFolder) {

		return ServiceContextBuilder.create(
			groupId, contextHttpServletRequest,
			structuredContentFolder.getViewableByAsString()
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				JournalFolder.class.getName(), contextCompany.getCompanyId(),
				structuredContentFolder.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();
	}

	private Page<StructuredContentFolder> _getStructuredContentFoldersPage(
			Map<String, Map<String, String>> actions,
			Long parentStructuredContentFolderId, Long siteId, String keywords,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				if (parentStructuredContentFolderId != null) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(
							Field.FOLDER_ID,
							String.valueOf(parentStructuredContentFolderId)),
						BooleanClauseOccur.MUST);
				}
			},
			FilterUtil.processFilter(_ddmIndexer, filter),
			JournalFolder.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});

				SearchRequestBuilder searchRequestBuilder =
					_searchRequestBuilderFactory.builder(searchContext);

				AggregationUtil.processVulcanAggregation(
					_aggregations, _ddmIndexer, _queries, searchRequestBuilder,
					aggregation);

				SortUtil.processSorts(
					_ddmIndexer, searchRequestBuilder, searchContext.getSorts(),
					_queries, _sorts);
			},
			sorts,
			document -> _toStructuredContentFolder(
				_journalFolderService.getFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private StructuredContentFolder _toStructuredContentFolder(
			JournalFolder journalFolder)
		throws Exception {

		return _structuredContentFolderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"add-subfolder",
					addAction(
						ActionKeys.UPDATE, journalFolder,
						"postStructuredContentFolderStructuredContentFolder")
				).put(
					"delete",
					addAction(
						ActionKeys.DELETE, journalFolder,
						"deleteStructuredContentFolder")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, journalFolder,
						"getStructuredContentFolder")
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, journalFolder,
						"putStructuredContentFolder")
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, journalFolder,
						"putStructuredContentFolderSubscribe")
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, journalFolder,
						"putStructuredContentFolderUnsubscribe")
				).build(),
				_dtoConverterRegistry, journalFolder.getFolderId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private StructuredContentFolder _updateStructuredContentFolder(
			Long siteId, Long folderId, Long parentFolderId,
			StructuredContentFolder structuredContentFolder)
		throws Exception {

		return _toStructuredContentFolder(
			_journalFolderService.updateFolder(
				siteId, folderId, parentFolderId,
				structuredContentFolder.getName(),
				structuredContentFolder.getDescription(), false,
				_createServiceContext(siteId, structuredContentFolder)));
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference
	private JournalFolderService _journalFolderService;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.StructuredContentFolderDTOConverter)"
	)
	private DTOConverter<DLFolder, StructuredContentFolder>
		_structuredContentFolderDTOConverter;

}