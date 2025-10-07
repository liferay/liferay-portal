/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.messaging;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.internal.score.CTScoreCalculator;
import com.liferay.change.tracking.service.CTScoreLocalService;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.MapUtil;

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
		if (message.getBoolean("increment")) {
			_ctScoreLocalService.incrementScore(
				message.getLong("ctCollectionId"),
				_ctScoreCalculator.calculate(
					message.getLong("modelClassNameId")));
		}
		else {
			_ctScoreLocalService.decrementScore(
				message.getLong("ctCollectionId"),
				_ctScoreCalculator.calculate(
					message.getLong("modelClassNameId")));
		}
	}

	@Reference
	private CTScoreCalculator _ctScoreCalculator;

	@Reference
	private CTScoreLocalService _ctScoreLocalService;

	@Reference
	private DestinationFactory _destinationFactory;

	private ServiceRegistration<Destination> _serviceRegistration;

}