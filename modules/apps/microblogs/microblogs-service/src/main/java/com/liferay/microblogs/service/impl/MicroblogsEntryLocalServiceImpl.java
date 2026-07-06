/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.impl;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.microblogs.constants.MicroblogsEntryConstants;
import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.exception.UnsupportedMicroblogsEntryException;
import com.liferay.microblogs.internal.social.MicroblogsActivityKeys;
import com.liferay.microblogs.internal.util.MicroblogsUtil;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.base.MicroblogsEntryLocalServiceBaseImpl;
import com.liferay.microblogs.util.comparator.EntryCreateDateComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.social.kernel.service.SocialActivityLocalService;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan Lee
 */
@Component(
	property = "model.class.name=com.liferay.microblogs.model.MicroblogsEntry",
	service = AopService.class
)
public class MicroblogsEntryLocalServiceImpl
	extends MicroblogsEntryLocalServiceBaseImpl {

	@Override
	public MicroblogsEntry addMicroblogsEntry(
			long userId, long creatorClassNameId, long creatorClassPK,
			String content, int type, long parentMicroblogsEntryId,
			int socialRelationType, ServiceContext serviceContext)
		throws PortalException {

		// Microblogs entry

		User user = _userLocalService.getUser(userId);

		Date date = new Date();

		_validate(type, parentMicroblogsEntryId);

		long microblogsEntryId = counterLocalService.increment();

		if (parentMicroblogsEntryId == 0) {
			parentMicroblogsEntryId = microblogsEntryId;
		}

		MicroblogsEntry microblogsEntry = microblogsEntryPersistence.create(
			microblogsEntryId);

		microblogsEntry.setCompanyId(user.getCompanyId());
		microblogsEntry.setUserId(user.getUserId());
		microblogsEntry.setUserName(user.getFullName());
		microblogsEntry.setCreateDate(date);
		microblogsEntry.setModifiedDate(date);
		microblogsEntry.setCreatorClassNameId(creatorClassNameId);
		microblogsEntry.setCreatorClassPK(creatorClassPK);
		microblogsEntry.setContent(content);
		microblogsEntry.setType(type);
		microblogsEntry.setParentMicroblogsEntryId(parentMicroblogsEntryId);
		microblogsEntry.setSocialRelationType(socialRelationType);

		microblogsEntry = microblogsEntryPersistence.update(microblogsEntry);

		// Resources

		_resourceLocalService.addModelResources(
			microblogsEntry, serviceContext);

		// Asset

		updateAsset(
			microblogsEntry, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return microblogsEntry;
	}

	@Override
	public MicroblogsEntry addMicroblogsEntry(
			long userId, String content, int type, long parentMicroblogsEntryId,
			int socialRelationType, ServiceContext serviceContext)
		throws PortalException {

		// Microblogs entry

		User user = _userLocalService.getUser(userId);

		Date date = new Date();

		_validate(type, parentMicroblogsEntryId);

		long microblogsEntryId = counterLocalService.increment();

		if (parentMicroblogsEntryId == 0) {
			parentMicroblogsEntryId = microblogsEntryId;
		}

		MicroblogsEntry microblogsEntry = microblogsEntryPersistence.create(
			microblogsEntryId);

		microblogsEntry.setCompanyId(user.getCompanyId());
		microblogsEntry.setUserId(user.getUserId());
		microblogsEntry.setUserName(user.getFullName());
		microblogsEntry.setCreateDate(date);
		microblogsEntry.setModifiedDate(date);
		microblogsEntry.setCreatorClassNameId(
			_classNameLocalService.getClassNameId(User.class));
		microblogsEntry.setCreatorClassPK(user.getUserId());
		microblogsEntry.setContent(content);
		microblogsEntry.setType(type);
		microblogsEntry.setParentMicroblogsEntryId(parentMicroblogsEntryId);
		microblogsEntry.setSocialRelationType(socialRelationType);

		microblogsEntry = microblogsEntryPersistence.update(microblogsEntry);

		// Resources

		_resourceLocalService.addModelResources(
			microblogsEntry, serviceContext);

		// Asset

		updateAsset(
			microblogsEntry, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		// Social

		int activityKey = MicroblogsActivityKeys.ADD_ENTRY;

		if (type == MicroblogsEntryConstants.TYPE_REPLY) {
			activityKey = MicroblogsActivityKeys.REPLY_ENTRY;
		}
		else if (type == MicroblogsEntryConstants.TYPE_REPOST) {
			activityKey = MicroblogsActivityKeys.REPOST_ENTRY;
		}

		_socialActivityLocalService.addActivity(
			userId, 0, MicroblogsEntry.class.getName(), microblogsEntryId,
			activityKey,
			JSONUtil.put(
				"content", microblogsEntry.getContent()
			).put(
				"parentMicroblogsEntryId", parentMicroblogsEntryId
			).toString(),
			microblogsEntry.getParentMicroblogsEntryUserId());

		// Notification

		_subscribeUsers(microblogsEntry, serviceContext);

		_sendNotificationEvent(microblogsEntry, serviceContext);

		return microblogsEntry;
	}

	@Override
	public void deleteMicroblogsEntries(
			long creatorClassNameId, long creatorClassPK)
		throws PortalException {

		microblogsEntryPersistence.removeByCCNI_CCPK(
			creatorClassNameId, creatorClassPK);
	}

	@Override
	public MicroblogsEntry deleteMicroblogsEntry(long microblogsEntryId)
		throws PortalException {

		MicroblogsEntry microblogsEntry =
			microblogsEntryPersistence.findByPrimaryKey(microblogsEntryId);

		return deleteMicroblogsEntry(microblogsEntry);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public MicroblogsEntry deleteMicroblogsEntry(
			MicroblogsEntry microblogsEntry)
		throws PortalException {

		List<MicroblogsEntry> microblogsEntries = new ArrayList<>();

		microblogsEntries.add(microblogsEntry);

		microblogsEntries.addAll(
			_getAllRelatedMicroblogsEntries(
				microblogsEntry.getMicroblogsEntryId()));

		for (MicroblogsEntry curMicroblogsEntry : microblogsEntries) {

			// Microblogs entry

			microblogsEntryPersistence.remove(curMicroblogsEntry);

			// Resource

			_resourceLocalService.deleteResource(
				curMicroblogsEntry.getCompanyId(),
				MicroblogsEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				curMicroblogsEntry.getMicroblogsEntryId());

			// Asset

			_assetEntryLocalService.deleteEntry(
				MicroblogsEntry.class.getName(),
				curMicroblogsEntry.getMicroblogsEntryId());

			// Social

			_socialActivityLocalService.deleteActivities(
				MicroblogsEntry.class.getName(),
				curMicroblogsEntry.getMicroblogsEntryId());
		}

		return microblogsEntry;
	}

	@Override
	public void deleteUserMicroblogsEntries(long userId)
		throws PortalException {

		List<MicroblogsEntry> microblogsEntries =
			microblogsEntryPersistence.findByUserId(userId);

		for (MicroblogsEntry microblogsEntry : microblogsEntries) {
			deleteMicroblogsEntry(microblogsEntry);
		}
	}

	@Override
	public List<MicroblogsEntry> getCompanyMicroblogsEntries(
		long companyId, int start, int end) {

		return microblogsEntryPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public int getCompanyMicroblogsEntriesCount(long companyId) {
		return microblogsEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<MicroblogsEntry> getMicroblogsEntries(
		long companyId, long creatorClassNameId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return microblogsEntryPersistence.findByC_CCNI_T(
			companyId, creatorClassNameId, type, start, end, orderByComparator);
	}

	@Override
	public List<MicroblogsEntry> getMicroblogsEntries(
		long companyId, long creatorClassNameId, long creatorClassPK, int start,
		int end) {

		return microblogsEntryPersistence.findByC_CCNI_CCPK(
			companyId, creatorClassNameId, creatorClassPK, start, end);
	}

	@Override
	public List<MicroblogsEntry> getMicroblogsEntries(
		long companyId, long creatorClassNameId, long creatorClassPK, int type,
		int start, int end) {

		return microblogsEntryPersistence.findByC_CCNI_CCPK_T(
			companyId, creatorClassNameId, creatorClassPK, type, start, end);
	}

	@Override
	public List<MicroblogsEntry> getMicroblogsEntries(
		long companyId, long creatorClassNameId, long creatorClassPK,
		String assetTagName, boolean andOperator, int start, int end) {

		return microblogsEntryFinder.findByC_CCNI_CCPK_ATN(
			companyId, creatorClassNameId, creatorClassPK, assetTagName,
			andOperator, start, end);
	}

	@Override
	public int getMicroblogsEntriesCount(
		long companyId, long creatorClassNameId, long creatorClassPK) {

		return microblogsEntryPersistence.countByC_CCNI_CCPK(
			companyId, creatorClassNameId, creatorClassPK);
	}

	@Override
	public int getMicroblogsEntriesCount(
		long companyId, long creatorClassNameId, long creatorClassPK,
		int type) {

		return microblogsEntryPersistence.countByC_CCNI_CCPK_T(
			companyId, creatorClassNameId, creatorClassPK, type);
	}

	@Override
	public int getMicroblogsEntriesCount(
		long companyId, long creatorClassNameId, long creatorClassPK,
		String assetTagName, boolean andOperator) {

		return microblogsEntryFinder.countByC_CCNI_CCPK_ATN(
			companyId, creatorClassNameId, creatorClassPK, assetTagName,
			andOperator);
	}

	@Override
	public int getMicroblogsEntriesCount(
		long companyId, long creatorClassNameId, String assetTagName) {

		return microblogsEntryFinder.countByC_CCNI_ATN(
			companyId, creatorClassNameId, assetTagName);
	}

	@Override
	public MicroblogsEntry getMicroblogsEntry(long microblogsEntryId)
		throws PortalException {

		return microblogsEntryPersistence.findByPrimaryKey(microblogsEntryId);
	}

	@Override
	public List<MicroblogsEntry> getParentMicroblogsEntryMicroblogsEntries(
		int type, long parentMicroblogsEntryId, int start, int end) {

		return microblogsEntryPersistence.findByT_P(
			type, parentMicroblogsEntryId, start, end,
			EntryCreateDateComparator.getInstance(true));
	}

	@Override
	public List<MicroblogsEntry> getParentMicroblogsEntryMicroblogsEntries(
		int type, long parentMicroblogsEntryId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return microblogsEntryPersistence.findByT_P(
			type, parentMicroblogsEntryId, start, end, orderByComparator);
	}

	@Override
	public int getParentMicroblogsEntryMicroblogsEntriesCount(
		int type, long parentMicroblogsEntryId) {

		return microblogsEntryPersistence.countByT_P(
			type, parentMicroblogsEntryId);
	}

	@Override
	public List<MicroblogsEntry> getUserMicroblogsEntries(
		long userId, int start, int end) {

		return microblogsEntryPersistence.findByUserId(userId, start, end);
	}

	@Override
	public List<MicroblogsEntry> getUserMicroblogsEntries(
		long userId, int type, int start, int end) {

		return microblogsEntryPersistence.findByU_T(userId, type, start, end);
	}

	@Override
	public int getUserMicroblogsEntriesCount(long userId) {
		return microblogsEntryPersistence.countByUserId(userId);
	}

	@Override
	public int getUserMicroblogsEntriesCount(long userId, int type) {
		return microblogsEntryPersistence.countByU_T(userId, type);
	}

	@Override
	public void updateAsset(
			MicroblogsEntry microblogsEntry, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		Group group = _groupLocalService.getCompanyGroup(
			microblogsEntry.getCompanyId());

		_assetEntryLocalService.updateEntry(
			microblogsEntry.getUserId(), group.getGroupId(),
			MicroblogsEntry.class.getName(),
			microblogsEntry.getMicroblogsEntryId(), assetCategoryIds,
			assetTagNames);
	}

	@Override
	public MicroblogsEntry updateMicroblogsEntry(
			long microblogsEntryId, String content, int socialRelationType,
			ServiceContext serviceContext)
		throws PortalException {

		// Microblogs entry

		MicroblogsEntry microblogsEntry =
			microblogsEntryPersistence.findByPrimaryKey(microblogsEntryId);

		microblogsEntry.setModifiedDate(new Date());
		microblogsEntry.setContent(content);
		microblogsEntry.setSocialRelationType(socialRelationType);

		microblogsEntry = microblogsEntryPersistence.update(microblogsEntry);

		// Asset

		updateAsset(
			microblogsEntry, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return microblogsEntry;
	}

	private List<MicroblogsEntry> _getAllRelatedMicroblogsEntries(
		long microblogsEntryId) {

		List<MicroblogsEntry> microblogsEntries = new ArrayList<>();

		microblogsEntries.addAll(
			microblogsEntryPersistence.findByT_P(
				MicroblogsEntryConstants.TYPE_REPLY, microblogsEntryId));

		List<MicroblogsEntry> repostMicroblogsEntries =
			microblogsEntryPersistence.findByT_P(
				MicroblogsEntryConstants.TYPE_REPOST, microblogsEntryId);

		for (MicroblogsEntry microblogsEntry : repostMicroblogsEntries) {
			microblogsEntries.add(microblogsEntry);

			microblogsEntries.addAll(
				_getAllRelatedMicroblogsEntries(
					microblogsEntry.getMicroblogsEntryId()));
		}

		return microblogsEntries;
	}

	private long _getSubscriptionId(
		long userId, MicroblogsEntry microblogsEntry) {

		try {
			Subscription subscription =
				_subscriptionLocalService.getSubscription(
					microblogsEntry.getCompanyId(), userId,
					MicroblogsEntry.class.getName(),
					microblogsEntry.getParentMicroblogsEntryId());

			return subscription.getSubscriptionId();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return 0;
	}

	private void _sendNotificationEvent(
			final MicroblogsEntry microblogsEntry,
			ServiceContext serviceContext)
		throws PortalException {

		AssetRendererFactory<MicroblogsEntry> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				MicroblogsEntry.class);

		AssetRenderer<MicroblogsEntry> assetRenderer =
			assetRendererFactory.getAssetRenderer(
				microblogsEntry.getMicroblogsEntryId());

		String entryURL = StringPool.BLANK;

		try {
			entryURL = assetRenderer.getURLViewInContext(
				serviceContext.getLiferayPortletRequest(),
				serviceContext.getLiferayPortletResponse(), null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		final JSONObject notificationEventJSONObject = JSONUtil.put(
			"className", MicroblogsEntry.class.getName()
		).put(
			"classPK", microblogsEntry.getMicroblogsEntryId()
		).put(
			"entryTitle",
			MicroblogsUtil.getProcessedContent(
				StringUtil.shorten(microblogsEntry.getContent(), 50),
				serviceContext)
		).put(
			"entryURL", entryURL
		).put(
			"userId", microblogsEntry.getUserId()
		);

		final List<Long> receiverUserIds = MicroblogsUtil.getSubscriberUserIds(
			microblogsEntry);

		Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Message message = new Message();

				message.setPayload(
					new NotificationCallable(
						receiverUserIds, microblogsEntry,
						notificationEventJSONObject));

				_messageBus.sendMessage(
					DestinationNames.ASYNC_SERVICE, message);

				return null;
			}

		};

		TransactionCallbackUtil.registerCommitCallback(callable);
	}

	private void _subscribeUsers(
			MicroblogsEntry microblogsEntry, ServiceContext serviceContext)
		throws PortalException {

		long rootMicroblogsEntryId = MicroblogsUtil.getRootMicroblogsEntryId(
			microblogsEntry);

		_subscriptionLocalService.addSubscription(
			microblogsEntry.getUserId(), serviceContext.getScopeGroupId(),
			MicroblogsEntry.class.getName(), rootMicroblogsEntryId);

		List<String> screenNames = MicroblogsUtil.getScreenNames(
			microblogsEntry.getContent());

		for (String screenName : screenNames) {
			long userId = _userLocalService.getUserIdByScreenName(
				serviceContext.getCompanyId(), screenName);

			_subscriptionLocalService.addSubscription(
				userId, serviceContext.getScopeGroupId(),
				MicroblogsEntry.class.getName(), rootMicroblogsEntryId);
		}
	}

	private void _validate(int type, long parentMicroblogsEntryId)
		throws PortalException {

		if (parentMicroblogsEntryId == 0) {
			return;
		}

		MicroblogsEntry microblogsEntry =
			microblogsEntryPersistence.findByPrimaryKey(
				parentMicroblogsEntryId);

		if (microblogsEntry.getSocialRelationType() ==
				MicroblogsEntryConstants.TYPE_EVERYONE) {

			return;
		}

		if (type == MicroblogsEntryConstants.TYPE_REPOST) {
			throw new UnsupportedMicroblogsEntryException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MicroblogsEntryLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SocialActivityLocalService _socialActivityLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	private class NotificationCallable implements Callable<Serializable> {

		public NotificationCallable(
			List<Long> receiverUserIds, MicroblogsEntry microblogsEntry,
			JSONObject notificationEventJSONObject) {

			_receiverUserIds = receiverUserIds;
			_microblogsEntry = microblogsEntry;
			_notificationEventJSONObject = notificationEventJSONObject;
		}

		@Override
		public Serializable call() throws Exception {
			try {
				sendUserNotifications(
					_receiverUserIds, _microblogsEntry,
					_notificationEventJSONObject);
			}
			catch (Exception exception) {
				throw new Exception(exception);
			}

			return null;
		}

		protected void sendUserNotifications(
				List<Long> receiverUserIds, MicroblogsEntry microblogsEntry,
				JSONObject notificationEventJSONObject)
			throws PortalException {

			int count = receiverUserIds.size();

			int pages = count / Indexer.DEFAULT_INTERVAL;

			for (int i = 0; i <= pages; i++) {
				int start = i * Indexer.DEFAULT_INTERVAL;

				int end = start + Indexer.DEFAULT_INTERVAL;

				if (count < end) {
					end = count;
				}

				for (int j = start; j < end; j++) {
					notificationEventJSONObject.put(
						"subscriptionId",
						_getSubscriptionId(
							receiverUserIds.get(j), microblogsEntry));

					int notificationType = MicroblogsUtil.getNotificationType(
						microblogsEntry, receiverUserIds.get(j),
						UserNotificationDeliveryConstants.TYPE_PUSH);

					if (notificationType !=
							MicroblogsEntryConstants.
								NOTIFICATION_TYPE_UNKNOWN) {

						notificationEventJSONObject.put(
							"notificationType", notificationType);

						_userNotificationEventLocalService.
							sendUserNotificationEvents(
								receiverUserIds.get(j),
								MicroblogsPortletKeys.MICROBLOGS,
								UserNotificationDeliveryConstants.TYPE_PUSH,
								notificationEventJSONObject);
					}

					notificationType = MicroblogsUtil.getNotificationType(
						microblogsEntry, receiverUserIds.get(j),
						UserNotificationDeliveryConstants.TYPE_WEBSITE);

					if (notificationType !=
							MicroblogsEntryConstants.
								NOTIFICATION_TYPE_UNKNOWN) {

						notificationEventJSONObject.put(
							"notificationType", notificationType);

						_userNotificationEventLocalService.
							sendUserNotificationEvents(
								receiverUserIds.get(j),
								MicroblogsPortletKeys.MICROBLOGS,
								UserNotificationDeliveryConstants.TYPE_WEBSITE,
								notificationEventJSONObject);
					}
				}
			}
		}

		private static final long serialVersionUID = 1L;

		private final MicroblogsEntry _microblogsEntry;
		private final JSONObject _notificationEventJSONObject;
		private final List<Long> _receiverUserIds;

	}

}