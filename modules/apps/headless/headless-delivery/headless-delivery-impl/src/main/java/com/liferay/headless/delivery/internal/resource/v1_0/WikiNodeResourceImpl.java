/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.WikiNode;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.WikiNodeEntityModel;
import com.liferay.headless.delivery.resource.v1_0.WikiNodeResource;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiNodeService;

import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/wiki-node.properties",
	scope = ServiceScope.PROTOTYPE, service = WikiNodeResource.class
)
public class WikiNodeResourceImpl extends BaseWikiNodeResourceImpl {

	@Override
	public void deleteSiteWikiNodeByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		com.liferay.wiki.model.WikiNode wikiNode =
			_wikiNodeLocalService.getWikiNodeByExternalReferenceCode(
				externalReferenceCode, siteId);

		_wikiNodeService.deleteNode(wikiNode.getNodeId());
	}

	@Override
	public void deleteWikiNode(Long wikiNodeId) throws Exception {
		_wikiNodeService.deleteNode(wikiNodeId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public WikiNode getSiteWikiNodeByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _toWikiNode(
			_wikiNodeService.getWikiNodeByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public Page<WikiNode> getSiteWikiNodesPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_NODE, "postSiteWikiNode",
					WikiConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_NODE, "postSiteWikiNodeBatch",
					WikiConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteWikiNodeBatch",
					WikiConstants.RESOURCE_NAME, null)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putWikiNodeBatch",
					WikiConstants.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(Field.GROUP_ID, String.valueOf(siteId)),
					BooleanClauseOccur.MUST);
			},
			filter, com.liferay.wiki.model.WikiNode.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toWikiNode(
				_wikiNodeService.getNode(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public WikiNode getWikiNode(Long wikiNodeId) throws Exception {
		return _toWikiNode(_wikiNodeService.getNode(wikiNodeId));
	}

	@Override
	public WikiNode postSiteWikiNode(Long siteId, WikiNode wikiNode)
		throws Exception {

		return _addWikiNode(
			wikiNode.getExternalReferenceCode(), siteId, wikiNode);
	}

	@Override
	public WikiNode putSiteWikiNodeByExternalReferenceCode(
			Long siteId, String externalReferenceCode, WikiNode wikiNode)
		throws Exception {

		com.liferay.wiki.model.WikiNode serviceBuilderWikiNode =
			_wikiNodeLocalService.fetchWikiNodeByExternalReferenceCode(
				externalReferenceCode, siteId);

		if (serviceBuilderWikiNode != null) {
			return _updateWikiNode(serviceBuilderWikiNode, wikiNode);
		}

		return _addWikiNode(externalReferenceCode, siteId, wikiNode);
	}

	@Override
	public WikiNode putWikiNode(Long wikiNodeId, WikiNode wikiNode)
		throws Exception {

		com.liferay.wiki.model.WikiNode serviceBuilderWikiNode =
			_wikiNodeService.getNode(wikiNodeId);

		return _updateWikiNode(serviceBuilderWikiNode, wikiNode);
	}

	@Override
	public void putWikiNodeSubscribe(Long wikiNodeId) throws Exception {
		_wikiNodeService.subscribeNode(wikiNodeId);
	}

	@Override
	public void putWikiNodeUnsubscribe(Long wikiNodeId) throws Exception {
		_wikiNodeService.unsubscribeNode(wikiNodeId);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		com.liferay.wiki.model.WikiNode wikiNode = _wikiNodeService.getNode(
			(Long)id);

		return wikiNode.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return WikiConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return com.liferay.wiki.model.WikiNode.class.getName();
	}

	private WikiNode _addWikiNode(
			String externalReferenceCode, Long groupId, WikiNode wikiNode)
		throws Exception {

		return _toWikiNode(
			_wikiNodeService.addNode(
				externalReferenceCode, wikiNode.getName(),
				wikiNode.getDescription(),
				ServiceContextBuilder.create(
					groupId, contextHttpServletRequest,
					wikiNode.getViewableByAsString()
				).build()));
	}

	private WikiNode _toWikiNode(com.liferay.wiki.model.WikiNode wikiNode)
		throws Exception {

		return _wikiNodeDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(ActionKeys.DELETE, wikiNode, "deleteWikiNode")
				).put(
					"get", addAction(ActionKeys.VIEW, wikiNode, "getWikiNode")
				).put(
					"replace",
					addAction(ActionKeys.UPDATE, wikiNode, "putWikiNode")
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, wikiNode, "putWikiNodeSubscribe")
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, wikiNode,
						"putWikiNodeUnsubscribe")
				).build(),
				_dtoConverterRegistry, wikiNode.getNodeId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			wikiNode);
	}

	private WikiNode _updateWikiNode(
			com.liferay.wiki.model.WikiNode serviceBuilderWikiNode,
			WikiNode wikiNode)
		throws Exception {

		return _toWikiNode(
			_wikiNodeService.updateNode(
				serviceBuilderWikiNode.getNodeId(), wikiNode.getName(),
				wikiNode.getDescription(),
				ServiceContextBuilder.create(
					serviceBuilderWikiNode.getGroupId(),
					contextHttpServletRequest, wikiNode.getViewableByAsString()
				).build()));
	}

	private static final EntityModel _entityModel = new WikiNodeEntityModel();

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.WikiNodeDTOConverter)"
	)
	private DTOConverter<com.liferay.wiki.model.WikiNode, WikiNode>
		_wikiNodeDTOConverter;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference
	private WikiNodeService _wikiNodeService;

}