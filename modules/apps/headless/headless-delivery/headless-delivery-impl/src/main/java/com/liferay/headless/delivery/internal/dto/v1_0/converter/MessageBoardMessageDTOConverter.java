/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.CreatorStatisticsUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RelatedContentUtil;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.service.MBStatsUserLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.message.boards.model.MBMessage"
	},
	service = DTOConverter.class
)
public class MessageBoardMessageDTOConverter
	implements DTOConverter<MBMessage, MessageBoardMessage> {

	@Override
	public String getContentType() {
		return MessageBoardMessage.class.getSimpleName();
	}

	@Override
	public MessageBoardMessage toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		MBMessage mbMessage = _mbMessageService.getMessage(
			(Long)dtoConverterContext.getId());

		Company company = _companyLocalService.getCompany(
			mbMessage.getCompanyId());
		User user = _userLocalService.getUser(mbMessage.getUserId());

		return new MessageBoardMessage() {
			{
				setActions(dtoConverterContext::getActions);
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							MBMessage.class.getName(),
							mbMessage.getMessageId())));
				setAnonymous(mbMessage::isAnonymous);
				setArticleBody(mbMessage::getBody);
				setCreator(
					() -> {
						if (mbMessage.isAnonymous()) {
							return null;
						}

						return CreatorUtil.toCreator(
							dtoConverterContext, _portal, user);
					});
				setCreatorStatistics(
					() -> {
						if (mbMessage.isAnonymous() || (user == null) ||
							user.isGuestUser()) {

							return null;
						}

						return CreatorStatisticsUtil.toCreatorStatistics(
							mbMessage.getGroupId(),
							String.valueOf(dtoConverterContext.getLocale()),
							_mbStatsUserLocalService,
							dtoConverterContext.getUriInfo(), user);
					});
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						MBMessage.class.getName(), mbMessage.getMessageId(),
						mbMessage.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(mbMessage::getCreateDate);
				setDateModified(mbMessage::getModifiedDate);
				setEncodingFormat(mbMessage::getFormat);
				setExternalReferenceCode(mbMessage::getExternalReferenceCode);
				setFriendlyUrlPath(mbMessage::getUrlSubject);
				setHasCompanyMx(
					() -> company.hasCompanyMx(user.getEmailAddress()));
				setHeadline(mbMessage::getSubject);
				setId(mbMessage::getMessageId);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							MBMessage.class.getName(),
							mbMessage.getMessageId()),
						AssetTag.NAME_ACCESSOR));
				setMessageBoardSectionId(mbMessage::getCategoryId);
				setMessageBoardThreadId(mbMessage::getThreadId);
				setModified(() -> mbMessage.getMvccVersion() > 1);
				setNumberOfMessageBoardAttachments(
					mbMessage::getAttachmentsFileEntriesCount);
				setNumberOfMessageBoardMessages(
					() -> _mbMessageLocalService.getChildMessagesCount(
						mbMessage.getMessageId(),
						WorkflowConstants.STATUS_APPROVED));
				setParentMessageBoardMessageId(
					() -> {
						if (mbMessage.getParentMessageId() == 0L) {
							return null;
						}

						return mbMessage.getParentMessageId();
					});
				setRelatedContents(
					() -> RelatedContentUtil.toRelatedContents(
						_assetEntryLocalService, _assetLinkLocalService,
						dtoConverterContext.getDTOConverterRegistry(),
						mbMessage.getModelClassName(), mbMessage.getMessageId(),
						dtoConverterContext.getLocale()));
				setShowAsAnswer(mbMessage::isAnswer);
				setSiteId(mbMessage::getGroupId);
				setStatus(
					() -> WorkflowConstants.getStatusLabel(
						mbMessage.getStatus()));
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						mbMessage.getCompanyId(),
						dtoConverterContext.getUserId(),
						MBThread.class.getName(), mbMessage.getThreadId()));
			}
		};
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private MBStatsUserLocalService _mbStatsUserLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}