/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.messaging;

import com.liferay.antivirus.async.store.AntivirusScannerHelper;
import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.retry.AntivirusAsyncRetryScheduler;
import com.liferay.antivirus.async.store.util.AntivirusAsyncUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageRunnable;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "destination.name=" + AntivirusAsyncDestinationNames.ANTIVIRUS,
	service = MessageListener.class
)
public class AntivirusAsyncMessageListener implements MessageListener {

	@Override
	public void receive(Message message) {
		_antivirusScannerHelper.processMessage(message);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		AntivirusAsyncConfiguration antivirusAsyncConfiguration =
			ConfigurableUtil.createConfigurable(
				AntivirusAsyncConfiguration.class, properties);

		DestinationConfiguration destinationConfiguration =
			DestinationConfiguration.createSerialDestinationConfiguration(
				AntivirusAsyncDestinationNames.ANTIVIRUS);

		int maximumQueueSize = antivirusAsyncConfiguration.maximumQueueSize();

		if (maximumQueueSize == 0) {
			maximumQueueSize = Integer.MAX_VALUE;
		}

		destinationConfiguration.setMaximumQueueSize(maximumQueueSize);

		destinationConfiguration.setRejectedExecutionHandler(
			(runnable, threadPoolExecutor) -> {
				MessageRunnable messageRunnable = (MessageRunnable)runnable;

				Message message = messageRunnable.getMessage();

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Schedule ",
							AntivirusAsyncUtil.getFileIdentifier(message),
							" for retry because the async antivirus queue is ",
							"overflowing: ", message.getValues()));
				}

				message.put("overflow", true);

				_antivirusAsyncRetryScheduler.schedule(message);
			});

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		_destinationServiceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		if (_destinationServiceRegistration != null) {
			_destinationServiceRegistration.unregister();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AntivirusAsyncMessageListener.class);

	@Reference
	private AntivirusAsyncRetryScheduler _antivirusAsyncRetryScheduler;

	@Reference
	private AntivirusScannerHelper _antivirusScannerHelper;

	@Reference
	private DestinationFactory _destinationFactory;

	private ServiceRegistration<Destination> _destinationServiceRegistration;

}