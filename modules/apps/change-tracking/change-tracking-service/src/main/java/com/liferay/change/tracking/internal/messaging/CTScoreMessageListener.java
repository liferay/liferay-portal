/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.messaging;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.internal.notification.CTUserNotificationSender;
import com.liferay.change.tracking.internal.score.CTScoreCache;
import com.liferay.change.tracking.internal.score.CTScoreCalculator;
import com.liferay.change.tracking.internal.score.CTScoreSizeClassificationCache;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "destination.name=" + CTDestinationNames.CT_SCORE,
	service = MessageListener.class
)
public class CTScoreMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SERIAL,
				CTDestinationNames.CT_SCORE);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		_serviceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		long ctCollectionId = message.getLong("ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if ((ctCollection == null) || ctCollection.isReadOnly()) {
			return;
		}

		int delta = _ctScoreCalculator.calculate(
			message.getLong("modelClassNameId"));

		if (!message.getBoolean("increment")) {
			delta = -1 * delta;
		}

		int score = _ctScoreCache.updateScore(ctCollectionId, delta);

		String originalSizeClassification =
			_ctScoreCalculator.getScoreSizeClassification(score - delta);
		String sizeClassification =
			_ctScoreCalculator.getScoreSizeClassification(score);

		if (Objects.equals(originalSizeClassification, sizeClassification)) {
			return;
		}

		_ctScoreSizeClassificationCache.put(ctCollectionId, sizeClassification);

		try {
			Set<Long> userIds = SetUtil.fromArray(
				_ctUserNotificationSender.getPublicationRoleUserIds(
					ctCollection, true, PublicationRoleConstants.NAME_ADMIN,
					PublicationRoleConstants.NAME_PUBLISHER));

			_ctUserNotificationSender.sendUserNotificationEvents(
				ctCollection,
				JSONUtil.put(
					"ctCollectionId", ctCollectionId
				).put(
					"notificationType",
					UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY
				).put(
					"originalSizeClassification", originalSizeClassification
				).put(
					"sizeClassification", sizeClassification
				),
				ArrayUtil.toLongArray(userIds));
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to send user notification events", portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTScoreMessageListener.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTScoreCache _ctScoreCache;

	@Reference
	private CTScoreCalculator _ctScoreCalculator;

	@Reference
	private CTScoreSizeClassificationCache _ctScoreSizeClassificationCache;

	@Reference
	private CTUserNotificationSender _ctUserNotificationSender;

	@Reference
	private DestinationFactory _destinationFactory;

	private ServiceRegistration<Destination> _serviceRegistration;

}