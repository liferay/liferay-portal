/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.resource.SPIRatingResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RatingUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.DocumentFolderEntityModel;
import com.liferay.headless.delivery.resource.v1_0.DocumentFolderResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
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
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/document-folder.properties",
	scope = ServiceScope.PROTOTYPE, service = DocumentFolderResource.class
)
public class DocumentFolderResourceImpl extends BaseDocumentFolderResourceImpl {

	@Override
	public void deleteDocumentFolder(Long documentFolderId) throws Exception {
		_dlAppService.deleteFolder(documentFolderId);
	}

	@Override
	public void deleteDocumentFolderMyRating(Long documentFolderId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		spiRatingResource.deleteRating(documentFolderId);
	}

	@Override
	public void deleteSiteDocumentsFolderByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		Folder folder = _dlAppService.getFolderByExternalReferenceCode(
			externalReferenceCode, siteId);

		_dlAppService.deleteFolder(folder.getFolderId());
	}

	@Override
	public Page<DocumentFolder> getAssetLibraryDocumentFoldersPage(
			Long assetLibraryId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Long documentFolderId = null;

		if (!GetterUtil.getBoolean(flatten)) {
			documentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return _getDocumentFoldersPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_FOLDER, "postAssetLibraryDocumentFolder",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_FOLDER,
					"postAssetLibraryDocumentFolderBatch",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteDocumentFolderBatch",
					DLConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getAssetLibraryDocumentFoldersPage",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putDocumentFolderBatch",
					DLConstants.RESOURCE_NAME, null)
			).build(),
			documentFolderId, assetLibraryId, flatten, search, aggregation,
			filter, pagination, sorts);
	}

	@Override
	public Page<DocumentFolder> getAssetLibraryDocumentFoldersRatedByMePage(
			Long assetLibraryId, Pagination pagination)
		throws Exception {

		return _getGroupDocumentFoldersRatedByMePage(
			assetLibraryId, pagination);
	}

	@Override
	public DocumentFolder getDocumentFolder(Long documentFolderId)
		throws Exception {

		return _toDocumentFolder(_dlAppService.getFolder(documentFolderId));
	}

	@Override
	public Page<DocumentFolder> getDocumentFolderDocumentFoldersPage(
			Long parentDocumentFolderId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Folder folder = _dlAppService.getFolder(parentDocumentFolderId);

		return _getDocumentFoldersPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_SUBFOLDER, folder.getFolderId(),
					"postDocumentFolderDocumentFolder", folder.getUserId(),
					DLConstants.RESOURCE_NAME, folder.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, folder.getFolderId(),
					"getDocumentFolderDocumentFoldersPage", folder.getUserId(),
					DLConstants.RESOURCE_NAME, folder.getGroupId())
			).build(),
			folder.getFolderId(), folder.getGroupId(), flatten, search,
			aggregation, filter, pagination, sorts);
	}

	@Override
	public Rating getDocumentFolderMyRating(Long documentFolderId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.getRating(documentFolderId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new DocumentFolderEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(DLFolder.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public Page<DocumentFolder> getSiteDocumentFoldersPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Long documentFolderId = null;

		if (!GetterUtil.getBoolean(flatten)) {
			documentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return _getDocumentFoldersPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_FOLDER, "postSiteDocumentFolder",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_FOLDER, "postSiteDocumentFolderBatch",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteDocumentFolderBatch",
					DLConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteDocumentFoldersPage",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putDocumentFolderBatch",
					DLConstants.RESOURCE_NAME, null)
			).build(),
			documentFolderId, siteId, flatten, search, aggregation, filter,
			pagination, sorts);
	}

	@Override
	public Page<DocumentFolder> getSiteDocumentFoldersRatedByMePage(
			Long siteId, Pagination pagination)
		throws Exception {

		return _getGroupDocumentFoldersRatedByMePage(siteId, pagination);
	}

	@Override
	public DocumentFolder getSiteDocumentsFolderByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		Folder folder = _dlAppService.getFolderByExternalReferenceCode(
			externalReferenceCode, siteId);

		return _toDocumentFolder(folder);
	}

	@Override
	public DocumentFolder postAssetLibraryDocumentFolder(
			Long assetLibraryId, DocumentFolder documentFolder)
		throws Exception {

		return postSiteDocumentFolder(assetLibraryId, documentFolder);
	}

	@Override
	public DocumentFolder postDocumentFolderDocumentFolder(
			Long parentDocumentFolderId, DocumentFolder documentFolder)
		throws Exception {

		Folder folder = _dlAppService.getFolder(parentDocumentFolderId);

		return _addDocumentFolder(
			documentFolder.getExternalReferenceCode(), folder.getGroupId(),
			folder.getFolderId(), documentFolder);
	}

	@Override
	public Rating postDocumentFolderMyRating(
			Long documentFolderId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), documentFolderId);
	}

	@Override
	public DocumentFolder postSiteDocumentFolder(
			Long siteId, DocumentFolder documentFolder)
		throws Exception {

		return _addDocumentFolder(
			documentFolder.getExternalReferenceCode(), siteId, 0L,
			documentFolder);
	}

	@Override
	public DocumentFolder putDocumentFolder(
			Long documentFolderId, DocumentFolder documentFolder)
		throws Exception {

		return _updateDocumentFolder(
			_dlAppService.getFolder(documentFolderId), documentFolder);
	}

	@Override
	public Rating putDocumentFolderMyRating(
			Long documentFolderId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), documentFolderId);
	}

	@Override
	public void putDocumentFolderSubscribe(Long documentFolderId)
		throws Exception {

		Folder folder = _dlAppService.getFolder(documentFolderId);

		_dlAppService.subscribeFolder(
			folder.getGroupId(), folder.getFolderId());
	}

	@Override
	public void putDocumentFolderUnsubscribe(Long documentFolderId)
		throws Exception {

		Folder folder = _dlAppService.getFolder(documentFolderId);

		_dlAppService.unsubscribeFolder(
			folder.getGroupId(), folder.getFolderId());
	}

	@Override
	public DocumentFolder putSiteDocumentsFolderByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			DocumentFolder documentFolder)
		throws Exception {

		Folder folder = _dlAppLocalService.fetchFolderByExternalReferenceCode(
			externalReferenceCode, siteId);

		if (folder != null) {
			return _updateDocumentFolder(folder, documentFolder);
		}

		return _addDocumentFolder(
			externalReferenceCode, siteId, 0L, documentFolder);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		Folder folder = _dlAppService.getFolder((Long)id);

		return folder.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return DLConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return DLFolder.class.getName();
	}

	private DocumentFolder _addDocumentFolder(
			String externalReferenceCode, Long groupId, Long parentFolderId,
			DocumentFolder documentFolder)
		throws Exception {

		return _toDocumentFolder(
			_dlAppService.addFolder(
				externalReferenceCode, groupId, parentFolderId,
				documentFolder.getName(), documentFolder.getDescription(),
				_createServiceContext(
					groupId, documentFolder,
					documentFolder.getViewableByAsString())));
	}

	private ServiceContext _createServiceContext(
		long groupId, DocumentFolder documentFolder, String viewableBy) {

		return ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, viewableBy
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				DLFolder.class.getName(), contextCompany.getCompanyId(),
				documentFolder.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();
	}

	private Page<DocumentFolder> _getDocumentFoldersPage(
			Map<String, Map<String, String>> actions,
			Long parentDocumentFolderId, Long groupId, Boolean flatten,
			String keywords, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				if (parentDocumentFolderId != null) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					String field = Field.FOLDER_ID;

					if (GetterUtil.getBoolean(flatten)) {
						booleanFilter.add(
							new TermFilter(
								field, String.valueOf(parentDocumentFolderId)),
							BooleanClauseOccur.MUST_NOT);
						field = Field.TREE_PATH;
					}

					booleanFilter.add(
						new TermFilter(
							field, String.valueOf(parentDocumentFolderId)),
						BooleanClauseOccur.MUST);
				}
			},
			FilterUtil.processFilter(_ddmIndexer, filter), _dlFolderIndexer,
			keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});

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
			document -> _toDocumentFolder(
				_dlAppService.getFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private Page<DocumentFolder> _getGroupDocumentFoldersRatedByMePage(
			long groupId, Pagination pagination)
		throws Exception {

		return Page.of(
			_toDocumentFolders(
				_dlFolderService.getFolders(
					groupId, 0.1, pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination, _dlFolderService.getFoldersCount(groupId, 0.1));
	}

	private SPIRatingResource<Rating> _getSPIRatingResource() {
		return new SPIRatingResource<>(
			DLFolder.class.getName(), _ratingsEntryLocalService,
			ratingsEntry -> {
				Folder folder = _dlAppService.getFolder(
					ratingsEntry.getClassPK());

				return RatingUtil.toRating(
					HashMapBuilder.put(
						"create",
						addAction(
							ActionKeys.VIEW, folder.getPrimaryKey(),
							"postDocumentFolderMyRating", folder.getUserId(),
							DLFolder.class.getName(), folder.getGroupId())
					).put(
						"delete",
						addAction(
							ActionKeys.VIEW, folder.getPrimaryKey(),
							"deleteDocumentFolderMyRating", folder.getUserId(),
							DLFolder.class.getName(), folder.getGroupId())
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, folder.getPrimaryKey(),
							"getDocumentFolderMyRating", folder.getUserId(),
							DLFolder.class.getName(), folder.getGroupId())
					).put(
						"replace",
						addAction(
							ActionKeys.VIEW, folder.getPrimaryKey(),
							"putDocumentFolderMyRating", folder.getUserId(),
							DLFolder.class.getName(), folder.getGroupId())
					).build(),
					_portal, ratingsEntry, _userLocalService);
			},
			contextUser);
	}

	private DocumentFolder _toDocumentFolder(Folder folder) throws Exception {
		return _documentFolderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, folder.getFolderId(),
						"deleteDocumentFolder", folder.getUserId(),
						DLFolder.class.getName(), folder.getGroupId())
				).put(
					"get",
					addAction(
						ActionKeys.ACCESS, folder.getFolderId(),
						"getDocumentFolder", folder.getUserId(),
						DLFolder.class.getName(), folder.getGroupId())
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, folder.getFolderId(),
						"putDocumentFolder", folder.getUserId(),
						DLFolder.class.getName(), folder.getGroupId())
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, folder.getFolderId(),
						"putDocumentFolderSubscribe", folder.getUserId(),
						DLFolder.class.getName(), folder.getGroupId())
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, folder.getFolderId(),
						"putDocumentFolderUnsubscribe", folder.getUserId(),
						DLFolder.class.getName(), folder.getGroupId())
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, folder.getFolderId(),
						"patchDocumentFolder", folder.getUserId(),
						DLFolder.class.getName(), folder.getGroupId())
				).build(),
				_dtoConverterRegistry, folder.getFolderId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private List<DocumentFolder> _toDocumentFolders(List<DLFolder> dlFolders)
		throws Exception {

		return transform(
			dlFolders,
			dlFolder -> _toDocumentFolder(new LiferayFolder(dlFolder)));
	}

	private DocumentFolder _updateDocumentFolder(
			Folder folder, DocumentFolder documentFolder)
		throws Exception {

		return _toDocumentFolder(
			_dlAppService.updateFolder(
				folder.getFolderId(), documentFolder.getName(),
				documentFolder.getDescription(),
				_createServiceContext(0, documentFolder, null)));
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference(
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFolder)"
	)
	private Indexer<?> _dlFolderIndexer;

	@Reference
	private DLFolderService _dlFolderService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.DocumentFolderDTOConverter)"
	)
	private DTOConverter<DLFolder, DocumentFolder> _documentFolderDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

}