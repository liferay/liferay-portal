/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal.jmx;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerRegistry;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Dictionary;
import java.util.List;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jmx.objectname=com.liferay.portal.messaging:classification=message_bus,name=MessageBusManager",
		"jmx.objectname.cache.key=MessageBusManager"
	},
	service = DynamicMBean.class
)
public class MessageBusManager
	extends StandardMBean implements MessageBusManagerMBean {

	public MessageBusManager() throws NotCompliantMBeanException {
		super(MessageBusManagerMBean.class);
	}

	@Override
	public int getDestinationCount() {
		return _serviceTracker.size();
	}

	@Override
	public int getMessageListenerCount(String destinationName) {
		List<MessageListener> messageListeners =
			_messageListenerRegistry.getMessageListeners(destinationName);

		return messageListeners.size();
	}

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		_serviceTracker = new ServiceTracker<>(
			bundleContext,
			bundleContext.createFilter(
				"(&(destination.name=*)(objectClass=" +
					Destination.class.getName() + "))"),
			new ServiceTrackerCustomizer
				<Destination, ServiceRegistration<DynamicMBean>>() {

				@Override
				public ServiceRegistration<DynamicMBean> addingService(
					ServiceReference<Destination> serviceReference) {

					ServiceRegistration<DynamicMBean> serviceRegistration =
						null;

					Destination destination = bundleContext.getService(
						serviceReference);

					try {
						DestinationStatisticsManager
							destinationStatisticsManager =
								new DestinationStatisticsManager(destination);

						Dictionary<String, Object> mBeanProperties =
							HashMapDictionaryBuilder.<String, Object>put(
								"jmx.objectname",
								destinationStatisticsManager.getObjectName()
							).put(
								"jmx.objectname.cache.key",
								destinationStatisticsManager.
									getObjectNameCacheKey()
							).build();

						serviceRegistration = bundleContext.registerService(
							DynamicMBean.class, destinationStatisticsManager,
							mBeanProperties);
					}
					catch (NotCompliantMBeanException
								notCompliantMBeanException) {

						if (_log.isInfoEnabled()) {
							_log.info(
								"Unable to register destination mbean",
								notCompliantMBeanException);
						}
					}

					return serviceRegistration;
				}

				@Override
				public void modifiedService(
					ServiceReference<Destination> serviceReference,
					ServiceRegistration<DynamicMBean> serviceRegistration) {
				}

				@Override
				public void removedService(
					ServiceReference<Destination> serviceReference,
					ServiceRegistration<DynamicMBean> serviceRegistration) {

					bundleContext.ungetService(serviceReference);

					serviceRegistration.unregister();
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MessageBusManager.class);

	@Reference
	private MessageListenerRegistry _messageListenerRegistry;

	private ServiceTracker<Destination, ServiceRegistration<DynamicMBean>>
		_serviceTracker;

}