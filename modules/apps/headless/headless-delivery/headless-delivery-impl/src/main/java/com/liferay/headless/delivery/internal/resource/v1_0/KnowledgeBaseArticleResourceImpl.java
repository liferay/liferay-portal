/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.resource.SPIRatingResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RatingUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.KnowledgeBaseArticleEntityModel;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseArticleResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.service.KBFolderService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
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
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/knowledge-base-article.properties",
	scope = ServiceScope.PROTOTYPE, service = KnowledgeBaseArticleResource.class
)
public class KnowledgeBaseArticleResourceImpl
	extends BaseKnowledgeBaseArticleResourceImpl {

	@Override
	public void deleteKnowledgeBaseArticle(Long knowledgeBaseArticleId)
		throws Exception {

		_kbArticleService.deleteKBArticle(knowledgeBaseArticleId);
	}

	@Override
	public void deleteKnowledgeBaseArticleMyRating(Long knowledgeBaseArticleId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		spiRatingResource.deleteRating(knowledgeBaseArticleId);
	}

	@Override
	public void deleteSiteKnowledgeBaseArticleByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		KBArticle kbArticle =
			_kbArticleLocalService.getLatestKBArticleByExternalReferenceCode(
				siteId, externalReferenceCode);

		_kbArticleService.deleteKBArticle(kbArticle.getResourcePrimKey());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new KnowledgeBaseArticleEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(KBArticle.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public KnowledgeBaseArticle getKnowledgeBaseArticle(
			Long knowledgeBaseArticleId)
		throws Exception {

		return _toKnowledgeBaseArticle(
			_kbArticleService.getLatestKBArticle(
				knowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED));
	}

	@Override
	public Page<KnowledgeBaseArticle>
			getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
				Long parentKnowledgeBaseArticleId, Boolean flatten,
				String search, Aggregation aggregation, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			parentKnowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED);

		return _getKnowledgeBaseArticlesPage(
			HashMapBuilder.put(
				"create",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE,
					"postKnowledgeBaseArticleKnowledgeBaseArticle",
					KBConstants.RESOURCE_NAME_ADMIN, kbArticle.getGroupId())
			).put(
				"createBatch",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE,
					"postSiteKnowledgeBaseArticleBatch",
					KBConstants.RESOURCE_NAME_ADMIN, kbArticle.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW,
					"getKnowledgeBaseArticleKnowledgeBaseArticlesPage",
					KBConstants.RESOURCE_NAME_ADMIN, kbArticle.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"parentMessageId",
						String.valueOf(kbArticle.getResourcePrimKey())),
					BooleanClauseOccur.MUST);
			},
			kbArticle.getGroupId(), search, aggregation, filter, pagination,
			sorts);
	}

	@Override
	public Rating getKnowledgeBaseArticleMyRating(Long knowledgeBaseArticleId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.getRating(knowledgeBaseArticleId);
	}

	@Override
	public Page<KnowledgeBaseArticle>
			getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
				Long knowledgeBaseFolderId, Boolean flatten, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		KBFolder kbFolder = _kbFolderService.getKBFolder(knowledgeBaseFolderId);

		return _getKnowledgeBaseArticlesPage(
			HashMapBuilder.put(
				"create",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE, kbFolder.getKbFolderId(),
					"postKnowledgeBaseFolderKnowledgeBaseArticle",
					kbFolder.getUserId(), KBConstants.RESOURCE_NAME_ADMIN,
					kbFolder.getGroupId())
			).put(
				"createBatch",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE, kbFolder.getKbFolderId(),
					"postKnowledgeBaseFolderKnowledgeBaseArticleBatch",
					kbFolder.getUserId(), KBConstants.RESOURCE_NAME_ADMIN,
					kbFolder.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, kbFolder.getKbFolderId(),
					"getKnowledgeBaseFolderKnowledgeBaseArticlesPage",
					kbFolder.getUserId(), KBConstants.RESOURCE_NAME_ADMIN,
					kbFolder.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				if (GetterUtil.getBoolean(flatten)) {
					booleanFilter.add(
						new TermFilter(
							Field.TREE_PATH,
							String.valueOf(kbFolder.getKbFolderId())),
						BooleanClauseOccur.MUST);
				}
				else {
					booleanFilter.add(
						new TermFilter(
							Field.FOLDER_ID,
							String.valueOf(kbFolder.getKbFolderId())),
						BooleanClauseOccur.MUST);
					booleanFilter.add(
						new TermFilter(
							"parentMessageId",
							String.valueOf(kbFolder.getKbFolderId())),
						BooleanClauseOccur.MUST);
				}
			},
			kbFolder.getGroupId(), search, aggregation, filter, pagination,
			sorts);
	}

	@Override
	public KnowledgeBaseArticle
			getSiteKnowledgeBaseArticleByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception {

		return _toKnowledgeBaseArticle(
			_kbArticleService.getLatestKBArticleByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public Page<KnowledgeBaseArticle> getSiteKnowledgeBaseArticlesPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getKnowledgeBaseArticlesPage(
			HashMapBuilder.put(
				"create",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE, "postSiteKnowledgeBaseArticle",
					KBConstants.RESOURCE_NAME_ADMIN, siteId)
			).put(
				"createBatch",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE,
					"postSiteKnowledgeBaseArticleBatch",
					KBConstants.RESOURCE_NAME_ADMIN, siteId)
			).put(
				"deleteBatch",
				addAction(
					KBActionKeys.DELETE_KB_ARTICLES,
					"deleteKnowledgeBaseArticleBatch",
					KBConstants.RESOURCE_NAME_ADMIN, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteKnowledgeBaseArticlesPage",
					KBConstants.RESOURCE_NAME_ADMIN, siteId)
			).put(
				"subscribe",
				addAction(
					ActionKeys.SUBSCRIBE,
					"putSiteKnowledgeBaseArticleSubscribe",
					KBConstants.RESOURCE_NAME_ADMIN, siteId)
			).put(
				"unsubscribe",
				addAction(
					ActionKeys.SUBSCRIBE,
					"putSiteKnowledgeBaseArticleUnsubscribe",
					KBConstants.RESOURCE_NAME_ADMIN, siteId)
			).put(
				"updateBatch",
				addAction(
					KBActionKeys.UPDATE_KB_ARTICLES_PRIORITIES,
					"putKnowledgeBaseArticleBatch",
					KBConstants.RESOURCE_NAME_ADMIN, null)
			).build(),
			booleanQuery -> {
				if (!GetterUtil.getBoolean(flatten)) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(Field.FOLDER_ID, "0"),
						BooleanClauseOccur.MUST);
					booleanFilter.add(
						new TermFilter("parentMessageId", "0"),
						BooleanClauseOccur.MUST);
				}
			},
			siteId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public KnowledgeBaseArticle postKnowledgeBaseArticleKnowledgeBaseArticle(
			Long parentKnowledgeBaseArticleId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			parentKnowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED);

		return _addKnowledgeBaseArticle(
			knowledgeBaseArticle.getExternalReferenceCode(),
			kbArticle.getGroupId(),
			_portal.getClassNameId(KBArticle.class.getName()),
			parentKnowledgeBaseArticleId, knowledgeBaseArticle);
	}

	@Override
	public Rating postKnowledgeBaseArticleMyRating(
			Long knowledgeBaseArticleId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), knowledgeBaseArticleId);
	}

	@Override
	public KnowledgeBaseArticle postKnowledgeBaseFolderKnowledgeBaseArticle(
			Long knowledgeBaseFolderId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		KBFolder kbFolder = _kbFolderService.getKBFolder(knowledgeBaseFolderId);

		return _addKnowledgeBaseArticle(
			knowledgeBaseArticle.getExternalReferenceCode(),
			kbFolder.getGroupId(),
			_portal.getClassNameId(KBFolder.class.getName()),
			knowledgeBaseFolderId, knowledgeBaseArticle);
	}

	@Override
	public KnowledgeBaseArticle postSiteKnowledgeBaseArticle(
			Long siteId, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return _addKnowledgeBaseArticle(
			knowledgeBaseArticle.getExternalReferenceCode(), siteId,
			_portal.getClassNameId(KBFolder.class.getName()), null,
			knowledgeBaseArticle);
	}

	@Override
	public KnowledgeBaseArticle putKnowledgeBaseArticle(
			Long knowledgeBaseArticleId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
			knowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED);

		return _updateKnowledgeBaseArticle(kbArticle, knowledgeBaseArticle);
	}

	@Override
	public Rating putKnowledgeBaseArticleMyRating(
			Long knowledgeBaseArticleId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), knowledgeBaseArticleId);
	}

	@Override
	public void putKnowledgeBaseArticleSubscribe(Long knowledgeBaseArticleId)
		throws Exception {

		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			knowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED);

		_kbArticleService.subscribeKBArticle(
			kbArticle.getGroupId(), kbArticle.getResourcePrimKey());
	}

	@Override
	public void putKnowledgeBaseArticleUnsubscribe(Long knowledgeBaseArticleId)
		throws Exception {

		_kbArticleService.unsubscribeKBArticle(knowledgeBaseArticleId);
	}

	@Override
	public KnowledgeBaseArticle
			putSiteKnowledgeBaseArticleByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		KBArticle kbArticle =
			_kbArticleLocalService.fetchLatestKBArticleByExternalReferenceCode(
				siteId, externalReferenceCode);

		if (kbArticle != null) {
			return _updateKnowledgeBaseArticle(kbArticle, knowledgeBaseArticle);
		}

		long parentResourceClassNameId = _portal.getClassNameId(
			KBFolderConstants.getClassName());
		Long parentResourcePrimaryKey =
			knowledgeBaseArticle.getParentKnowledgeBaseFolderId();

		if ((knowledgeBaseArticle.getParentKnowledgeBaseArticleId() != null) &&
			(knowledgeBaseArticle.getParentKnowledgeBaseArticleId() !=
				KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY)) {

			parentResourceClassNameId = _portal.getClassNameId(
				KBArticleConstants.getClassName());
			parentResourcePrimaryKey =
				knowledgeBaseArticle.getParentKnowledgeBaseArticleId();
		}

		return _addKnowledgeBaseArticle(
			externalReferenceCode, siteId, parentResourceClassNameId,
			parentResourcePrimaryKey, knowledgeBaseArticle);
	}

	@Override
	public void putSiteKnowledgeBaseArticleSubscribe(Long siteId)
		throws Exception {

		_kbArticleService.subscribeGroupKBArticles(
			siteId, KBPortletKeys.KNOWLEDGE_BASE_DISPLAY);
	}

	@Override
	public void putSiteKnowledgeBaseArticleUnsubscribe(Long siteId)
		throws Exception {

		_kbArticleService.unsubscribeGroupKBArticles(
			siteId, KBPortletKeys.KNOWLEDGE_BASE_DISPLAY);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			(Long)id, WorkflowConstants.STATUS_APPROVED);

		return kbArticle.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return KBConstants.RESOURCE_NAME_ADMIN;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return KBArticle.class.getName();
	}

	private KnowledgeBaseArticle _addKnowledgeBaseArticle(
			String externalReferenceCode, Long groupId,
			Long parentResourceClassNameId, Long parentResourcePrimaryKey,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		if (parentResourcePrimaryKey == null) {
			parentResourceClassNameId = _portal.getClassNameId(
				KBFolderConstants.getClassName());
			parentResourcePrimaryKey =
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		Date datePublished = knowledgeBaseArticle.getDatePublished();

		if (datePublished == null) {
			datePublished = new Date();
		}

		return _toKnowledgeBaseArticle(
			_kbArticleService.addKBArticle(
				externalReferenceCode, KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
				parentResourceClassNameId, parentResourcePrimaryKey,
				knowledgeBaseArticle.getTitle(),
				knowledgeBaseArticle.getFriendlyUrlPath(),
				knowledgeBaseArticle.getArticleBody(),
				knowledgeBaseArticle.getDescription(), null, null,
				datePublished, null, null, null,
				_createServiceContext(
					knowledgeBaseArticle.getTaxonomyCategoryIds(),
					knowledgeBaseArticle.getKeywords(), groupId,
					knowledgeBaseArticle)));
	}

	private ServiceContext _createServiceContext(
		Long[] assetCategoryIds, String[] assetTagNames, long groupId,
		KnowledgeBaseArticle knowledgeBaseArticle) {

		return ServiceContextBuilder.create(
			groupId, contextHttpServletRequest,
			knowledgeBaseArticle.getViewableByAsString()
		).assetCategoryIds(
			assetCategoryIds
		).assetTagNames(
			assetTagNames
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				KBArticle.class.getName(), contextCompany.getCompanyId(),
				knowledgeBaseArticle.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();
	}

	private Page<KnowledgeBaseArticle> _getKnowledgeBaseArticlesPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Long siteId, String keywords, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer,
			FilterUtil.processFilter(_ddmIndexer, filter),
			KBArticle.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_APPROVED);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});

				if (keywords == null) {
					searchContext.setKeywords("");
				}

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
			document -> _toKnowledgeBaseArticle(
				_kbArticleService.getLatestKBArticle(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)),
					WorkflowConstants.STATUS_APPROVED)));
	}

	private SPIRatingResource<Rating> _getSPIRatingResource() {
		return new SPIRatingResource<>(
			KBArticle.class.getName(), _ratingsEntryLocalService,
			ratingsEntry -> {
				KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
					ratingsEntry.getClassPK(),
					WorkflowConstants.STATUS_APPROVED);

				return RatingUtil.toRating(
					HashMapBuilder.put(
						"create",
						addAction(
							ActionKeys.VIEW, kbArticle.getResourcePrimKey(),
							"postKnowledgeBaseArticleMyRating",
							kbArticle.getUserId(), KBArticle.class.getName(),
							kbArticle.getGroupId())
					).put(
						"delete",
						addAction(
							ActionKeys.VIEW, kbArticle.getResourcePrimKey(),
							"deleteKnowledgeBaseArticleMyRating",
							kbArticle.getUserId(), KBArticle.class.getName(),
							kbArticle.getGroupId())
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, kbArticle.getResourcePrimKey(),
							"getKnowledgeBaseArticleMyRating",
							kbArticle.getUserId(), KBArticle.class.getName(),
							kbArticle.getGroupId())
					).put(
						"replace",
						addAction(
							ActionKeys.VIEW, kbArticle.getResourcePrimKey(),
							"putKnowledgeBaseArticleMyRating",
							kbArticle.getUserId(), KBArticle.class.getName(),
							kbArticle.getGroupId())
					).build(),
					_portal, ratingsEntry, _userLocalService);
			},
			contextUser);
	}

	private KnowledgeBaseArticle _toKnowledgeBaseArticle(KBArticle kbArticle)
		throws Exception {

		if (kbArticle == null) {
			return null;
		}

		return _knowledgeBaseArticleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, kbArticle.getResourcePrimKey(),
						"deleteKnowledgeBaseArticle", kbArticle.getUserId(),
						KBArticle.class.getName(), kbArticle.getGroupId())
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, kbArticle.getResourcePrimKey(),
						"getKnowledgeBaseArticle", kbArticle.getUserId(),
						KBArticle.class.getName(), kbArticle.getGroupId())
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, kbArticle.getResourcePrimKey(),
						"putKnowledgeBaseArticle", kbArticle.getUserId(),
						KBArticle.class.getName(), kbArticle.getGroupId())
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, kbArticle.getResourcePrimKey(),
						"putKnowledgeBaseArticleSubscribe",
						kbArticle.getUserId(), KBArticle.class.getName(),
						kbArticle.getGroupId())
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, kbArticle.getResourcePrimKey(),
						"putKnowledgeBaseArticleUnsubscribe",
						kbArticle.getUserId(), KBArticle.class.getName(),
						kbArticle.getGroupId())
				).build(),
				_dtoConverterRegistry, kbArticle.getResourcePrimKey(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private KnowledgeBaseArticle _updateKnowledgeBaseArticle(
			KBArticle kbArticle, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		Long[] taxonomyCategoryIds =
			knowledgeBaseArticle.getTaxonomyCategoryIds();

		if (taxonomyCategoryIds == null) {
			taxonomyCategoryIds = new Long[0];
		}

		return _toKnowledgeBaseArticle(
			_kbArticleService.updateKBArticle(
				kbArticle.getResourcePrimKey(), knowledgeBaseArticle.getTitle(),
				knowledgeBaseArticle.getArticleBody(),
				knowledgeBaseArticle.getDescription(), null, null,
				knowledgeBaseArticle.getDatePublished(), null, null, null, null,
				_createServiceContext(
					taxonomyCategoryIds,
					GetterUtil.getStringValues(
						knowledgeBaseArticle.getKeywords()),
					kbArticle.getGroupId(), knowledgeBaseArticle)));
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
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private KBFolderService _kbFolderService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.KnowledgeBaseArticleDTOConverter)"
	)
	private DTOConverter<KBArticle, KnowledgeBaseArticle>
		_knowledgeBaseArticleDTOConverter;

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