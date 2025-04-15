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
import com.liferay.headless.delivery.dto.v1_0.MessageBoardSection;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.MessageBoardSectionEntityModel;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardSectionResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryService;
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
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/message-board-section.properties",
	scope = ServiceScope.PROTOTYPE, service = MessageBoardSectionResource.class
)
public class MessageBoardSectionResourceImpl
	extends BaseMessageBoardSectionResourceImpl {

	@Override
	public void deleteMessageBoardSection(Long messageBoardSectionId)
		throws Exception {

		_mbCategoryService.deleteCategory(messageBoardSectionId, true);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new MessageBoardSectionEntityModel(
			new ArrayList<>(
				EntityFieldsUtil.getEntityFields(
					_portal.getClassNameId(MBCategory.class.getName()),
					contextCompany.getCompanyId(), _expandoBridgeIndexer,
					_expandoColumnLocalService, _expandoTableLocalService)));
	}

	@Override
	public MessageBoardSection getMessageBoardSection(
			Long messageBoardSectionId)
		throws Exception {

		return _toMessageBoardSection(
			_mbCategoryService.getCategory(messageBoardSectionId));
	}

	@Override
	public Page<MessageBoardSection>
			getMessageBoardSectionMessageBoardSectionsPage(
				Long parentMessageBoardSectionId, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		MBCategory mbCategory = _mbCategoryService.getCategory(
			parentMessageBoardSectionId);

		return _getMessageBoardSectionsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_CATEGORY,
					"postMessageBoardSectionMessageBoardSection",
					MBConstants.RESOURCE_NAME, mbCategory.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW,
					"getMessageBoardSectionMessageBoardSectionsPage",
					MBConstants.RESOURCE_NAME, mbCategory.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						Field.ASSET_PARENT_CATEGORY_ID,
						String.valueOf(mbCategory.getCategoryId())),
					BooleanClauseOccur.MUST);
			},
			mbCategory.getGroupId(), search, aggregation, filter, pagination,
			sorts);
	}

	@Override
	public MessageBoardSection getSiteMessageBoardSectionByFriendlyUrlPath(
			Long siteId, String friendlyUrlPath)
		throws Exception {

		return _toMessageBoardSection(
			_mbCategoryService.getMBCategory(siteId, friendlyUrlPath));
	}

	@Override
	public Page<MessageBoardSection> getSiteMessageBoardSectionsPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getMessageBoardSectionsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_CATEGORY, "postSiteMessageBoardSection",
					MBConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_CATEGORY, "postSiteMessageBoardSectionBatch",
					MBConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteMessageBoardSectionBatch",
					MBConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteMessageBoardSectionsPage",
					MBConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putMessageBoardSectionBatch",
					MBConstants.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
				if (!GetterUtil.getBoolean(flatten)) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(Field.ASSET_PARENT_CATEGORY_ID, "0"),
						BooleanClauseOccur.MUST);
				}
			},
			siteId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public MessageBoardSection postMessageBoardSectionMessageBoardSection(
			Long parentMessageBoardSectionId,
			MessageBoardSection messageBoardSection)
		throws Exception {

		MBCategory mbCategory = _mbCategoryService.getCategory(
			parentMessageBoardSectionId);

		return _addMessageBoardSection(
			mbCategory.getGroupId(), parentMessageBoardSectionId,
			messageBoardSection);
	}

	@Override
	public MessageBoardSection postSiteMessageBoardSection(
			Long siteId, MessageBoardSection messageBoardSection)
		throws Exception {

		return _addMessageBoardSection(siteId, 0L, messageBoardSection);
	}

	@Override
	public MessageBoardSection putMessageBoardSection(
			Long messageBoardSectionId, MessageBoardSection messageBoardSection)
		throws Exception {

		MBCategory mbCategory = _mbCategoryService.getCategory(
			messageBoardSectionId);

		return _toMessageBoardSection(
			_mbCategoryService.updateCategory(
				messageBoardSectionId, mbCategory.getParentCategoryId(),
				messageBoardSection.getTitle(),
				messageBoardSection.getDescription(),
				mbCategory.getDisplayStyle(), "", "", "", 0, false, "", "", 0,
				"", false, "", 0, false, "", "", false, false, false,
				_createServiceContext(
					mbCategory.getGroupId(), messageBoardSection, null)));
	}

	@Override
	public void putMessageBoardSectionSubscribe(Long messageBoardSectionId)
		throws Exception {

		MBCategory mbCategory = _mbCategoryService.getCategory(
			messageBoardSectionId);

		_mbCategoryService.subscribeCategory(
			mbCategory.getGroupId(), mbCategory.getCategoryId());
	}

	@Override
	public void putMessageBoardSectionUnsubscribe(Long messageBoardSectionId)
		throws Exception {

		MBCategory mbCategory = _mbCategoryService.getCategory(
			messageBoardSectionId);

		_mbCategoryService.unsubscribeCategory(
			mbCategory.getGroupId(), mbCategory.getCategoryId());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		MBCategory mbCategory = _mbCategoryService.getCategory((Long)id);

		return mbCategory.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return MBConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return MBCategory.class.getName();
	}

	private MessageBoardSection _addMessageBoardSection(
			long siteId, Long parentMessageBoardSectionId,
			MessageBoardSection messageBoardSection)
		throws Exception {

		return _toMessageBoardSection(
			_mbCategoryService.addCategory(
				null, contextUser.getUserId(), parentMessageBoardSectionId,
				messageBoardSection.getTitle(),
				messageBoardSection.getDescription(),
				_createServiceContext(
					siteId, messageBoardSection,
					messageBoardSection.getViewableByAsString())));
	}

	private ServiceContext _createServiceContext(
		long groupId, MessageBoardSection messageBoardSection,
		String viewableBy) {

		return ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, viewableBy
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				MBCategory.class.getName(), contextCompany.getCompanyId(),
				messageBoardSection.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();
	}

	private Page<MessageBoardSection> _getMessageBoardSectionsPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Long siteId, String keywords, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer,
			FilterUtil.processFilter(_ddmIndexer, filter),
			MBCategory.class.getName(), keywords, pagination,
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
			document -> _toMessageBoardSection(
				_mbCategoryService.getCategory(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private MessageBoardSection _toMessageBoardSection(MBCategory mbCategory)
		throws Exception {

		return _messageBoardSectionDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"add-subcategory",
					addAction(
						ActionKeys.ADD_SUBCATEGORY, mbCategory,
						"postMessageBoardSectionMessageBoardSection")
				).put(
					"add-thread",
					ActionUtil.addAction(
						ActionKeys.ADD_MESSAGE,
						MessageBoardThreadResourceImpl.class,
						mbCategory.getCategoryId(),
						"postMessageBoardSectionMessageBoardThread",
						contextScopeChecker, mbCategory.getUserId(),
						MBCategory.class.getName(), mbCategory.getGroupId(),
						contextUriInfo)
				).put(
					"delete",
					addAction(
						ActionKeys.DELETE, mbCategory,
						"deleteMessageBoardSection")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, mbCategory, "getMessageBoardSection")
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, mbCategory, "putMessageBoardSection")
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, mbCategory,
						"putMessageBoardSectionSubscribe")
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, mbCategory,
						"putMessageBoardSectionUnsubscribe")
				).build(),
				_dtoConverterRegistry, mbCategory.getCategoryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
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
	private MBCategoryService _mbCategoryService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.MessageBoardSectionDTOConverter)"
	)
	private DTOConverter<MBCategory, MessageBoardSection>
		_messageBoardSectionDTOConverter;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

}