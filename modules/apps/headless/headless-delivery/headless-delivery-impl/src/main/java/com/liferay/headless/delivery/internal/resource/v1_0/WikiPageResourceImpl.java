/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.WikiPage;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.WikiPageEntityModel;
import com.liferay.headless.delivery.resource.v1_0.WikiPageResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Constants;
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
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeService;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageService;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/wiki-page.properties",
	scope = ServiceScope.PROTOTYPE, service = WikiPageResource.class
)
public class WikiPageResourceImpl extends BaseWikiPageResourceImpl {

	@Override
	public void deleteSiteWikiPageByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		com.liferay.wiki.model.WikiPage wikiPage =
			_wikiPageService.getLatestPageByExternalReferenceCode(
				siteId, externalReferenceCode);

		_wikiPageService.deletePage(wikiPage.getNodeId(), wikiPage.getTitle());
	}

	@Override
	public void deleteWikiPage(Long wikiPageId) throws Exception {
		com.liferay.wiki.model.WikiPage wikiPage =
			_wikiPageLocalService.getPage(wikiPageId);

		_wikiPageService.deletePage(wikiPage.getNodeId(), wikiPage.getTitle());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new WikiPageEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(
					com.liferay.wiki.model.WikiPage.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public WikiPage getSiteWikiPageByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _toWikiPage(
			_wikiPageService.getLatestPageByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public Page<WikiPage> getWikiNodeWikiPagesPage(
			Long wikiNodeId, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		WikiNode wikiNode = _wikiNodeService.getNode(wikiNodeId);

		return SearchUtil.search(
			HashMapBuilder.put(
				"add-page",
				addAction(ActionKeys.ADD_PAGE, wikiNode, "postWikiNodeWikiPage")
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_PAGE, wikiNode, "postWikiNodeWikiPageBatch")
			).put(
				"get",
				addAction(ActionKeys.VIEW, wikiNode, "getWikiNodeWikiPagesPage")
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(Field.NODE_ID, String.valueOf(wikiNodeId)),
					BooleanClauseOccur.MUST);
			},
			FilterUtil.processFilter(_ddmIndexer, filter),
			com.liferay.wiki.model.WikiPage.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());

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
			document -> _toWikiPage(
				_wikiPageService.getPage(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public WikiPage getWikiPage(Long wikiPageId) throws Exception {
		return _toWikiPage(_wikiPageService.getPage(wikiPageId));
	}

	@Override
	public Page<WikiPage> getWikiPageWikiPagesPage(Long parentWikiPageId)
		throws Exception {

		com.liferay.wiki.model.WikiPage wikiPage = _wikiPageService.getPage(
			parentWikiPageId);

		return Page.of(
			HashMapBuilder.put(
				"add-page",
				addAction(
					ActionKeys.UPDATE, wikiPage.getResourcePrimKey(),
					"postWikiPageWikiPage", wikiPage.getUserId(),
					WikiPage.class.getName(), wikiPage.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, wikiPage.getResourcePrimKey(),
					"getWikiPageWikiPagesPage", wikiPage.getUserId(),
					WikiPage.class.getName(), wikiPage.getGroupId())
			).build(),
			transform(
				_wikiPageService.getChildren(
					wikiPage.getGroupId(), wikiPage.getNodeId(), true,
					wikiPage.getTitle()),
				this::_toWikiPage));
	}

	@Override
	public WikiPage postWikiNodeWikiPage(Long wikiNodeId, WikiPage wikiPage)
		throws Exception {

		WikiNode wikiNode = _wikiNodeService.getNode(wikiNodeId);

		ServiceContext serviceContext = _createServiceContext(
			Constants.ADD, wikiNode.getGroupId(), wikiPage);

		return _toWikiPage(
			_wikiPageService.addPage(
				wikiPage.getExternalReferenceCode(), wikiNodeId,
				wikiPage.getHeadline(), wikiPage.getContent(),
				wikiPage.getHeadline(), true,
				_toFormat(wikiPage.getEncodingFormat()), null, null,
				serviceContext));
	}

	@Override
	public WikiPage postWikiPageWikiPage(
			Long parentWikiPageId, WikiPage wikiPage)
		throws Exception {

		com.liferay.wiki.model.WikiPage parentWikiPage =
			_wikiPageService.getPage(parentWikiPageId);

		ServiceContext serviceContext = _createServiceContext(
			Constants.ADD, parentWikiPage.getGroupId(), wikiPage);

		return _toWikiPage(
			_wikiPageService.addPage(
				wikiPage.getExternalReferenceCode(), parentWikiPage.getNodeId(),
				wikiPage.getHeadline(), WikiPageConstants.VERSION_DEFAULT,
				wikiPage.getContent(), wikiPage.getHeadline(), false,
				_toFormat(wikiPage.getEncodingFormat()), false,
				parentWikiPage.getTitle(), null, serviceContext));
	}

	@Override
	public WikiPage putSiteWikiPageByExternalReferenceCode(
			Long siteId, String externalReferenceCode, WikiPage wikiPage)
		throws Exception {

		com.liferay.wiki.model.WikiPage serviceBuilderWikiPage =
			_wikiPageLocalService.fetchLatestPageByExternalReferenceCode(
				siteId, externalReferenceCode);

		if (serviceBuilderWikiPage != null) {
			return _updateWikiPage(serviceBuilderWikiPage, wikiPage);
		}

		if (wikiPage.getWikiNodeId() == null) {
			throw new BadRequestException("WikiNode ID is null");
		}

		return _toWikiPage(
			_wikiPageService.addPage(
				externalReferenceCode, wikiPage.getWikiNodeId(),
				wikiPage.getHeadline(), wikiPage.getContent(),
				wikiPage.getDescription(), false,
				_toFormat(wikiPage.getEncodingFormat()), null, null,
				_createServiceContext(Constants.ADD, siteId, wikiPage)));
	}

	@Override
	public WikiPage putWikiPage(Long wikiPageId, WikiPage wikiPage)
		throws Exception {

		com.liferay.wiki.model.WikiPage serviceBuilderWikiPage =
			_wikiPageService.getPage(wikiPageId);

		return _updateWikiPage(serviceBuilderWikiPage, wikiPage);
	}

	@Override
	public void putWikiPageSubscribe(Long wikiPageId) throws Exception {
		com.liferay.wiki.model.WikiPage wikiPage =
			_wikiPageLocalService.getPage(wikiPageId);

		_wikiPageService.subscribePage(
			wikiPage.getNodeId(), wikiPage.getTitle());
	}

	@Override
	public void putWikiPageUnsubscribe(Long wikiPageId) throws Exception {
		com.liferay.wiki.model.WikiPage wikiPage =
			_wikiPageLocalService.getPage(wikiPageId);

		_wikiPageService.unsubscribePage(
			wikiPage.getNodeId(), wikiPage.getTitle());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		com.liferay.wiki.model.WikiPage wikiPage =
			_wikiPageLocalService.getPage((Long)id);

		return wikiPage.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return WikiConstants.RESOURCE_NAME;
	}

	@Override
	protected Long getPermissionCheckerResourceId(Object id) throws Exception {
		com.liferay.wiki.model.WikiPage wikiPage =
			_wikiPageLocalService.getPage((Long)id);

		return wikiPage.getResourcePrimKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return com.liferay.wiki.model.WikiPage.class.getName();
	}

	private ServiceContext _createServiceContext(
		String command, Long groupId, WikiPage wikiPage) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, wikiPage.getViewableByAsString()
		).assetCategoryIds(
			wikiPage.getTaxonomyCategoryIds()
		).assetTagNames(
			wikiPage.getKeywords()
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				com.liferay.wiki.model.WikiPage.class.getName(),
				contextCompany.getCompanyId(), wikiPage.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();

		serviceContext.setCommand(command);

		return serviceContext;
	}

	private String _toFormat(String encodingFormat) {
		if (encodingFormat.equals("text/x-wiki")) {
			return "creole";
		}
		else if (encodingFormat.equals("text/html")) {
			return "html";
		}
		else if (encodingFormat.equals("text/plain")) {
			return "plain_text";
		}

		return encodingFormat;
	}

	private WikiPage _toWikiPage(com.liferay.wiki.model.WikiPage wikiPage)
		throws Exception {

		return _wikiPageDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"add-page",
					addAction(
						ActionKeys.UPDATE, wikiPage.getResourcePrimKey(),
						"postWikiPageWikiPage", wikiPage.getUserId(),
						WikiPage.class.getName(), wikiPage.getGroupId())
				).put(
					"delete",
					addAction(
						ActionKeys.DELETE, wikiPage.getResourcePrimKey(),
						"deleteWikiPage", wikiPage.getUserId(),
						WikiPage.class.getName(), wikiPage.getGroupId())
				).put(
					"deleteBatch",
					addAction(
						ActionKeys.DELETE, "deleteWikiPageBatch",
						WikiPage.class.getName(), null)
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, wikiPage.getResourcePrimKey(),
						"getWikiPage", wikiPage.getUserId(),
						WikiPage.class.getName(), wikiPage.getGroupId())
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, wikiPage.getResourcePrimKey(),
						"putWikiPage", wikiPage.getUserId(),
						WikiPage.class.getName(), wikiPage.getGroupId())
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, wikiPage.getResourcePrimKey(),
						"putWikiPageSubscribe", wikiPage.getUserId(),
						WikiPage.class.getName(), wikiPage.getGroupId())
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, wikiPage.getResourcePrimKey(),
						"putWikiPageUnsubscribe", wikiPage.getUserId(),
						WikiPage.class.getName(), wikiPage.getGroupId())
				).put(
					"updateBatch",
					addAction(
						ActionKeys.UPDATE, "putWikiPageBatch",
						WikiPage.class.getName(), null)
				).build(),
				_dtoConverterRegistry, wikiPage.getResourcePrimKey(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private WikiPage _updateWikiPage(
			com.liferay.wiki.model.WikiPage serviceBuilderWikiPage,
			WikiPage wikiPage)
		throws Exception {

		ServiceContext serviceContext = _createServiceContext(
			Constants.UPDATE, serviceBuilderWikiPage.getGroupId(), wikiPage);

		return _toWikiPage(
			_wikiPageService.updatePage(
				serviceBuilderWikiPage.getNodeId(), wikiPage.getHeadline(),
				serviceBuilderWikiPage.getVersion(), wikiPage.getContent(),
				wikiPage.getDescription(), true,
				_toFormat(wikiPage.getEncodingFormat()),
				serviceBuilderWikiPage.getParentTitle(),
				serviceBuilderWikiPage.getRedirectTitle(), serviceContext));
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
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference
	private WikiNodeService _wikiNodeService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.WikiPageDTOConverter)"
	)
	private DTOConverter<com.liferay.wiki.model.WikiPage, WikiPage>
		_wikiPageDTOConverter;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference
	private WikiPageService _wikiPageService;

}