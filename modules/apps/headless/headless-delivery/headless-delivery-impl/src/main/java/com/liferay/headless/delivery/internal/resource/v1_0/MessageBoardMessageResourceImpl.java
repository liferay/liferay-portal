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
import com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RatingUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.MessageBoardMessageEntityModel;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardMessageResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.message.boards.util.comparator.MessageCreateDateComparator;
import com.liferay.message.boards.util.comparator.MessageModifiedDateComparator;
import com.liferay.message.boards.util.comparator.MessageSubjectComparator;
import com.liferay.message.boards.util.comparator.MessageURLSubjectComparator;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
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
import com.liferay.portal.vulcan.util.UriInfoUtil;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/message-board-message.properties",
	scope = ServiceScope.PROTOTYPE, service = MessageBoardMessageResource.class
)
public class MessageBoardMessageResourceImpl
	extends BaseMessageBoardMessageResourceImpl {

	@Override
	public void deleteMessageBoardMessage(Long messageBoardMessageId)
		throws Exception {

		_mbMessageService.deleteMessage(messageBoardMessageId);
	}

	@Override
	public void deleteMessageBoardMessageMyRating(Long messageBoardMessageId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		spiRatingResource.deleteRating(messageBoardMessageId);
	}

	@Override
	public void deleteSiteMessageBoardMessageByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		MBMessage mbMessage =
			_mbMessageLocalService.getMBMessageByExternalReferenceCode(
				externalReferenceCode, siteId);

		_mbMessageService.deleteMessage(mbMessage.getMessageId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new MessageBoardMessageEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(MBMessage.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public MessageBoardMessage getMessageBoardMessage(
			Long messageBoardMessageId)
		throws Exception {

		return _toMessageBoardMessage(
			_mbMessageService.getMessage(messageBoardMessageId));
	}

	@Override
	public Page<MessageBoardMessage>
			getMessageBoardMessageMessageBoardMessagesPage(
				Long parentMessageBoardMessageId, Boolean flatten,
				String search, Aggregation aggregation, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		flatten = GetterUtil.getBoolean(flatten);

		MBMessage mbMessage = _mbMessageService.getMessage(
			parentMessageBoardMessageId);

		Map<String, Map<String, String>> actions =
			HashMapBuilder.<String, Map<String, String>>put(
				"get-child-messages",
				addAction(
					ActionKeys.VIEW, mbMessage.getMessageId(),
					"getMessageBoardMessageMessageBoardMessagesPage",
					mbMessage.getUserId(), MBConstants.RESOURCE_NAME,
					mbMessage.getGroupId())
			).put(
				"reply-to-message",
				addAction(
					ActionKeys.REPLY_TO_MESSAGE, mbMessage.getMessageId(),
					"postMessageBoardMessageMessageBoardMessage",
					mbMessage.getUserId(), MBConstants.RESOURCE_NAME,
					mbMessage.getGroupId())
			).build();

		if ((search == null) && (filter == null) && !flatten) {
			int status = WorkflowConstants.STATUS_APPROVED;

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker.isContentReviewer(
					contextCompany.getCompanyId(), mbMessage.getGroupId())) {

				status = WorkflowConstants.STATUS_ANY;
			}

			OrderByComparator<MBMessage> orderByComparator =
				_getMBMessageOrderByComparator(sorts);

			return Page.of(
				actions,
				transform(
					_mbMessageService.getChildMessages(
						mbMessage.getMessageId(), flatten,
						new QueryDefinition<>(
							status, contextUser.getUserId(), true,
							pagination.getStartPosition(),
							pagination.getEndPosition(), orderByComparator)),
					this::_toMessageBoardMessage),
				pagination,
				_mbMessageService.getChildMessagesCount(
					mbMessage.getMessageId(), flatten,
					new QueryDefinition<>(
						status, contextUser.getUserId(), true,
						pagination.getStartPosition(),
						pagination.getEndPosition(), orderByComparator)));
		}

		return _getMessageBoardMessagesPage(
			actions, parentMessageBoardMessageId, null, flatten, search,
			aggregation, filter, pagination, sorts);
	}

	@Override
	public Rating getMessageBoardMessageMyRating(Long messageBoardMessageId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.getRating(messageBoardMessageId);
	}

	@Override
	public Page<MessageBoardMessage>
			getMessageBoardThreadMessageBoardMessagesPage(
				Long messageBoardThreadId, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		MBThread mbThread = _mbThreadLocalService.getMBThread(
			messageBoardThreadId);

		Map<String, Map<String, String>> actions =
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				addAction(
					ActionKeys.ADD_MESSAGE, mbThread.getThreadId(),
					"postMessageBoardThreadMessageBoardMessage",
					mbThread.getUserId(), MBConstants.RESOURCE_NAME,
					mbThread.getGroupId())
			).<String, Map<String, String>>put(
				"createBatch",
				addAction(
					ActionKeys.ADD_MESSAGE, mbThread.getThreadId(),
					"postMessageBoardThreadMessageBoardMessageBatch",
					mbThread.getUserId(), MBConstants.RESOURCE_NAME,
					mbThread.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, mbThread.getThreadId(),
					"getMessageBoardThreadMessageBoardMessagesPage",
					mbThread.getUserId(), MBConstants.RESOURCE_NAME,
					mbThread.getGroupId())
			).build();

		if ((search == null) && (filter == null)) {
			OrderByComparator<MBMessage> orderByComparator =
				_getMBMessageOrderByComparator(sorts);

			int status = WorkflowConstants.STATUS_APPROVED;

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker.isContentReviewer(
					contextCompany.getCompanyId(), mbThread.getGroupId())) {

				status = WorkflowConstants.STATUS_ANY;
			}

			return Page.of(
				actions,
				transform(
					_mbMessageService.getChildMessages(
						mbThread.getRootMessageId(), false,
						new QueryDefinition<>(
							status, contextUser.getUserId(), true,
							pagination.getStartPosition(),
							pagination.getEndPosition(), orderByComparator)),
					this::_toMessageBoardMessage),
				pagination,
				_mbMessageService.getChildMessagesCount(
					mbThread.getRootMessageId(), false,
					new QueryDefinition<>(
						status, contextUser.getUserId(), true,
						pagination.getStartPosition(),
						pagination.getEndPosition(), orderByComparator)));
		}

		return _getMessageBoardMessagesPage(
			actions, mbThread.getRootMessageId(), null, false, search,
			aggregation, filter, pagination, sorts);
	}

	@Override
	public MessageBoardMessage
			getSiteMessageBoardMessageByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception {

		return _toMessageBoardMessage(
			_mbMessageLocalService.getMBMessageByExternalReferenceCode(
				externalReferenceCode, siteId));
	}

	@Override
	public MessageBoardMessage getSiteMessageBoardMessageByFriendlyUrlPath(
			Long siteId, String friendlyUrlPath)
		throws Exception {

		MBMessage mbMessage = _mbMessageService.fetchMBMessageByUrlSubject(
			siteId, friendlyUrlPath);

		if (mbMessage == null) {
			throw new NoSuchMessageException(
				"No message exists with friendly URL path " + friendlyUrlPath);
		}

		return _toMessageBoardMessage(mbMessage);
	}

	@Override
	public Page<MessageBoardMessage> getSiteMessageBoardMessagesPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getMessageBoardMessagesPage(
			HashMapBuilder.put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteMessageBoardMessageBatch",
					MBConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteMessageBoardMessagesPage",
					MBConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putMessageBoardMessageBatch",
					MBConstants.RESOURCE_NAME, null)
			).build(),
			null, siteId, flatten, search, aggregation, filter, pagination,
			sorts);
	}

	@Override
	public Page<MessageBoardMessage>
			getSiteUserMessageBoardMessagesActivityPage(
				Long siteId, Long userId, Pagination pagination)
		throws Exception {

		int start = QueryUtil.ALL_POS;
		int end = QueryUtil.ALL_POS;

		if (pagination != null) {
			start = pagination.getStartPosition();
			end = pagination.getEndPosition();
		}

		return Page.of(
			transform(
				_mbMessageService.getGroupUserMessageBoardMessagesActivity(
					siteId, userId, start, end),
				this::_toMessageBoardMessage),
			pagination,
			_mbMessageService.getGroupUserMessageBoardMessagesActivityCount(
				siteId, userId));
	}

	@Override
	public MessageBoardMessage postMessageBoardMessageMessageBoardMessage(
			Long parentMessageBoardMessageId,
			MessageBoardMessage messageBoardMessage)
		throws Exception {

		MBMessage mbMessage = _mbMessageLocalService.getMBMessage(
			parentMessageBoardMessageId);

		return _addMessageBoardMessage(
			messageBoardMessage.getExternalReferenceCode(),
			mbMessage.getGroupId(), mbMessage.getMessageId(),
			messageBoardMessage);
	}

	@Override
	public Rating postMessageBoardMessageMyRating(
			Long messageBoardMessageId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), messageBoardMessageId);
	}

	@Override
	public MessageBoardMessage postMessageBoardThreadMessageBoardMessage(
			Long messageBoardThreadId, MessageBoardMessage messageBoardMessage)
		throws Exception {

		MBThread mbThread = _mbThreadLocalService.getMBThread(
			messageBoardThreadId);

		return _addMessageBoardMessage(
			messageBoardMessage.getExternalReferenceCode(),
			mbThread.getGroupId(), mbThread.getRootMessageId(),
			messageBoardMessage);
	}

	@Override
	public MessageBoardMessage putMessageBoardMessage(
			Long messageBoardMessageId, MessageBoardMessage messageBoardMessage)
		throws Exception {

		MBMessage mbMessage = _mbMessageService.getMessage(
			messageBoardMessageId);

		return _updateMessageBoardMessage(mbMessage, messageBoardMessage);
	}

	@Override
	public void putMessageBoardMessageMarkAsAnswer(Long messageBoardMessageId)
		throws Exception {

		_mbMessageService.updateAnswer(messageBoardMessageId, true, false);
	}

	@Override
	public Rating putMessageBoardMessageMyRating(
			Long messageBoardMessageId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), messageBoardMessageId);
	}

	@Override
	public void putMessageBoardMessageSubscribe(Long messageBoardMessageId)
		throws Exception {

		_mbMessageService.subscribeMessage(messageBoardMessageId);
	}

	@Override
	public void putMessageBoardMessageUnmarkAsAnswer(Long messageBoardMessageId)
		throws Exception {

		_mbMessageService.updateAnswer(messageBoardMessageId, false, false);
	}

	@Override
	public void putMessageBoardMessageUnsubscribe(Long messageBoardMessageId)
		throws Exception {

		_mbMessageService.unsubscribeMessage(messageBoardMessageId);
	}

	@Override
	public MessageBoardMessage
			putSiteMessageBoardMessageByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		MBMessage mbMessage =
			_mbMessageLocalService.fetchMBMessageByExternalReferenceCode(
				externalReferenceCode, siteId);

		if (mbMessage != null) {
			return _updateMessageBoardMessage(mbMessage, messageBoardMessage);
		}

		return _addMessageBoardMessage(
			externalReferenceCode, siteId,
			messageBoardMessage.getParentMessageBoardMessageId(),
			messageBoardMessage);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		MBMessage mbMessage = _mbMessageService.getMessage((Long)id);

		return mbMessage.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return MBConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return MBMessage.class.getName();
	}

	private MessageBoardMessage _addMessageBoardMessage(
			String externalReferenceCode, Long groupId, Long parentMessageId,
			MessageBoardMessage messageBoardMessage)
		throws Exception {

		if (parentMessageId == null) {
			throw new BadRequestException("Parent message board ID is null");
		}

		String headline = messageBoardMessage.getHeadline();

		if (headline == null) {
			MBMessage parentMBMessage = _mbMessageService.getMessage(
				parentMessageId);

			headline =
				MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE +
					parentMBMessage.getSubject();
		}

		String encodingFormat = messageBoardMessage.getEncodingFormat();

		if (encodingFormat == null) {
			encodingFormat = MBMessageConstants.DEFAULT_FORMAT;
		}

		MBMessage mbMessage = _mbMessageService.addMessage(
			externalReferenceCode, parentMessageId, headline,
			messageBoardMessage.getArticleBody(), encodingFormat,
			Collections.emptyList(),
			GetterUtil.getBoolean(messageBoardMessage.getAnonymous()), 0.0,
			false, _createServiceContext(groupId, messageBoardMessage));

		_updateAnswer(mbMessage, messageBoardMessage);

		return _toMessageBoardMessage(mbMessage);
	}

	private ServiceContext _createServiceContext(
		long groupId, MessageBoardMessage messageBoardMessage) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest,
			messageBoardMessage.getViewableByAsString()
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				MBMessage.class.getName(), contextCompany.getCompanyId(),
				messageBoardMessage.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();

		String link = contextHttpServletRequest.getHeader("Link");

		if (link == null) {
			UriBuilder uriBuilder = UriInfoUtil.getBaseUriBuilder(
				contextUriInfo);

			link = String.valueOf(
				uriBuilder.replacePath(
					"/"
				).build());
		}

		serviceContext.setAttribute("entryURL", link);

		if (messageBoardMessage.getId() == null) {
			serviceContext.setCommand("add");
		}
		else {
			serviceContext.setCommand("update");
		}

		return serviceContext;
	}

	private OrderByComparator<MBMessage> _getMBMessageOrderByComparator(
		Sort[] sorts) {

		OrderByComparator<MBMessage> orderByComparator = null;

		if ((sorts != null) && (sorts.length == 1)) {
			Sort sort = sorts[0];

			String fieldName = sort.getFieldName();

			if (Objects.equals(fieldName, "createDate_sortable")) {
				orderByComparator = MessageCreateDateComparator.getInstance(
					!sort.isReverse());
			}
			else if (Objects.equals(fieldName, "modified_sortable")) {
				orderByComparator = MessageModifiedDateComparator.getInstance(
					!sort.isReverse());
			}
			else if (fieldName.contains("title")) {
				orderByComparator = MessageSubjectComparator.getInstance(
					!sort.isReverse());
			}
			else if (fieldName.contains("urlSubject")) {
				orderByComparator = MessageURLSubjectComparator.getInstance(
					!sort.isReverse());
			}
		}

		return orderByComparator;
	}

	private Page<MessageBoardMessage> _getMessageBoardMessagesPage(
			Map<String, Map<String, String>> actions,
			Long messageBoardMessageId, Long siteId, Boolean flatten,
			String keywords, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (messageBoardMessageId != null) {
			MBMessage mbMessage = _mbMessageService.getMessage(
				messageBoardMessageId);

			siteId = mbMessage.getGroupId();
		}

		long messageBoardMessageSiteId = siteId;

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				if (messageBoardMessageId != null) {
					booleanFilter.add(
						new TermFilter(
							Field.ENTRY_CLASS_PK,
							String.valueOf(messageBoardMessageId)),
						BooleanClauseOccur.MUST_NOT);

					String field = "parentMessageId";

					if (GetterUtil.getBoolean(flatten)) {
						field = Field.TREE_PATH;
					}

					booleanFilter.add(
						new TermFilter(
							field, String.valueOf(messageBoardMessageId)),
						BooleanClauseOccur.MUST);
				}
				else {
					if (!GetterUtil.getBoolean(flatten)) {
						booleanFilter.add(
							new TermFilter(Field.CATEGORY_ID, "0"),
							BooleanClauseOccur.MUST);
					}

					booleanFilter.add(
						new TermFilter(
							Field.GROUP_ID,
							String.valueOf(messageBoardMessageSiteId)),
						BooleanClauseOccur.MUST);
				}
			},
			FilterUtil.processFilter(_ddmIndexer, filter),
			MBMessage.class.getName(), keywords, pagination,
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
			document -> _toMessageBoardMessage(
				_mbMessageService.getMessage(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private SPIRatingResource<Rating> _getSPIRatingResource() {
		return new SPIRatingResource<>(
			MBMessage.class.getName(), _ratingsEntryLocalService,
			ratingsEntry -> {
				MBMessage mbMessage = _mbMessageService.getMessage(
					ratingsEntry.getClassPK());

				return RatingUtil.toRating(
					HashMapBuilder.put(
						"create",
						addAction(
							ActionKeys.VIEW, mbMessage,
							"postMessageBoardMessageMyRating")
					).put(
						"delete",
						addAction(
							ActionKeys.VIEW, mbMessage,
							"deleteMessageBoardMessageMyRating")
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, mbMessage,
							"getMessageBoardMessageMyRating")
					).put(
						"replace",
						addAction(
							ActionKeys.VIEW, mbMessage,
							"putMessageBoardMessageMyRating")
					).build(),
					_portal, ratingsEntry, _userLocalService);
			},
			contextUser);
	}

	private MessageBoardMessage _toMessageBoardMessage(MBMessage mbMessage)
		throws Exception {

		return _messageBoardMessageDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false,
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, mbMessage,
						"deleteMessageBoardMessage")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, mbMessage, "getMessageBoardMessage")
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, mbMessage, "putMessageBoardMessage")
				).put(
					"reply-to-message",
					addAction(
						ActionKeys.REPLY_TO_MESSAGE, mbMessage.getMessageId(),
						"postMessageBoardMessageMessageBoardMessage",
						mbMessage.getUserId(), MBConstants.RESOURCE_NAME,
						mbMessage.getGroupId())
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE, mbMessage,
						"putMessageBoardMessageSubscribe")
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE, mbMessage,
						"putMessageBoardMessageSubscribe")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, mbMessage,
						"patchMessageBoardMessage")
				).build(),
				_dtoConverterRegistry, mbMessage.getPrimaryKey(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private void _updateAnswer(
			MBMessage mbMessage, MessageBoardMessage messageBoardMessage)
		throws Exception {

		Boolean showAsAnswer = messageBoardMessage.getShowAsAnswer();

		if ((showAsAnswer != null) && (showAsAnswer != mbMessage.isAnswer())) {
			_mbMessageService.updateAnswer(
				mbMessage.getMessageId(), showAsAnswer, false);
		}
	}

	private MessageBoardMessage _updateMessageBoardMessage(
			MBMessage mbMessage, MessageBoardMessage messageBoardMessage)
		throws Exception {

		if ((messageBoardMessage.getArticleBody() == null) &&
			(messageBoardMessage.getHeadline() == null)) {

			throw new BadRequestException(
				"Article body and headline are both null");
		}

		String headline = messageBoardMessage.getHeadline();

		if (headline == null) {
			MBMessage parentMBMessage = _mbMessageService.getMessage(
				mbMessage.getParentMessageId());

			headline =
				MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE +
					parentMBMessage.getSubject();
		}

		mbMessage = _mbMessageService.updateDiscussionMessage(
			mbMessage.getClassName(), mbMessage.getClassPK(),
			mbMessage.getMessageId(), headline,
			messageBoardMessage.getArticleBody(),
			_createServiceContext(mbMessage.getGroupId(), messageBoardMessage));

		_updateAnswer(mbMessage, messageBoardMessage);

		return _toMessageBoardMessage(mbMessage);
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
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.MessageBoardMessageDTOConverter)"
	)
	private DTOConverter<MBMessage, MessageBoardMessage>
		_messageBoardMessageDTOConverter;

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