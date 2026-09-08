/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardThread;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.CreatorStatisticsUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RelatedContentUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBStatsUserLocalService;
import com.liferay.message.boards.service.MBThreadFlagLocalService;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.message.boards.model.MBThread"
	},
	service = DTOConverter.class
)
public class MessageBoardThreadDTOConverter
	implements DTOConverter<MBThread, MessageBoardThread> {

	@Override
	public String getContentType() {
		return MessageBoardThread.class.getSimpleName();
	}

	@Override
	public MessageBoardThread toDTO(
			DTOConverterContext dtoConverterContext, MBThread mbThread)
		throws Exception {

		String languageId = LocaleUtil.toLanguageId(
			dtoConverterContext.getLocale());
		MBMessage mbMessage = _mbMessageLocalService.getMessage(
			mbThread.getRootMessageId());
		User user = _userLocalService.fetchUser(mbThread.getUserId());

		return new MessageBoardThread() {
			{
				setActions(dtoConverterContext::getActions);
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							MBMessage.class.getName(),
							mbMessage.getMessageId())));
				setArticleBody(mbMessage::getBody);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal, user));
				setCreatorStatistics(
					() -> {
						if (mbMessage.isAnonymous() || (user == null) ||
							user.isGuestUser()) {

							return null;
						}

						return CreatorStatisticsUtil.toCreatorStatistics(
							mbMessage.getGroupId(), languageId,
							_mbStatsUserLocalService,
							dtoConverterContext.getUriInfo(), user);
					});
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						MBMessage.class.getName(), mbMessage.getMessageId(),
						mbThread.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(mbMessage::getCreateDate);
				setDateModified(mbMessage::getModifiedDate);
				setEncodingFormat(mbMessage::getFormat);
				setFriendlyUrlPath(mbMessage::getUrlSubject);
				setHasValidAnswer(
					() -> ListUtil.exists(
						_mbMessageLocalService.getChildMessages(
							mbMessage.getMessageId(),
							WorkflowConstants.STATUS_APPROVED),
						MBMessage::isAnswer));
				setHeadline(mbMessage::getSubject);
				setId(mbThread::getThreadId);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							MBMessage.class.getName(),
							mbMessage.getMessageId()),
						AssetTag.NAME_ACCESSOR));
				setLastPostDate(mbThread::getLastPostDate);
				setLocked(mbThread::isLocked);
				setMessageBoardRootMessageId(mbThread::getRootMessageId);
				setMessageBoardSectionId(mbMessage::getCategoryId);
				setNumberOfMessageBoardAttachments(
					mbMessage::getAttachmentsFileEntriesCount);
				setNumberOfMessageBoardMessages(
					() -> _mbMessageLocalService.getChildMessagesCount(
						mbMessage.getMessageId(),
						WorkflowConstants.STATUS_APPROVED));
				setRelatedContents(
					() -> RelatedContentUtil.toRelatedContents(
						_assetEntryLocalService, _assetLinkLocalService,
						dtoConverterContext.getDTOConverterRegistry(),
						mbMessage.getModelClassName(), mbMessage.getMessageId(),
						dtoConverterContext.getLocale()));
				setSeen(
					() -> _mbThreadFlagLocalService.hasThreadFlag(
						dtoConverterContext.getUserId(), mbThread));
				setShowAsQuestion(mbThread::isQuestion);
				setSiteId(mbThread::getGroupId);
				setStatus(
					() -> WorkflowConstants.getStatusLabel(
						mbThread.getStatus()));
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						mbMessage.getCompanyId(),
						dtoConverterContext.getUserId(),
						MBThread.class.getName(), mbMessage.getThreadId()));
				setTaxonomyCategoryBriefs(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							MBMessage.class.getName(),
							mbThread.getRootMessageId()),
						assetCategory ->
							TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
								assetCategory, dtoConverterContext),
						TaxonomyCategoryBrief.class));
				setThreadType(
					() -> _toThreadType(
						languageId, mbThread.getGroupId(),
						mbThread.getPriority()));
				setViewCount(mbThread::getViewCount);
			}
		};
	}

	private String _toThreadType(
			String languageId, Long siteId, double priority)
		throws Exception {

		MBGroupServiceSettings mbGroupServiceSettings =
			MBGroupServiceSettings.getInstance(siteId);

		for (String priorityString :
				mbGroupServiceSettings.getPriorities(languageId)) {

			String[] parts = StringUtil.split(priorityString, StringPool.PIPE);

			if (priority == GetterUtil.getDouble(parts[2])) {
				return parts[0];
			}
		}

		return null;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBStatsUserLocalService _mbStatsUserLocalService;

	@Reference
	private MBThreadFlagLocalService _mbThreadFlagLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}