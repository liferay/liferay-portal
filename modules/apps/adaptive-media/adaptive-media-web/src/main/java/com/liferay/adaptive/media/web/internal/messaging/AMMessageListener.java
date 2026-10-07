/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.messaging;

import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.adaptive.media.web.internal.constants.AMDestinationNames;
import com.liferay.adaptive.media.web.internal.processor.AMAsyncProcessorImpl;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "destination.name=" + AMDestinationNames.ADAPTIVE_MEDIA_PROCESSOR,
	service = MessageListener.class
)
public class AMMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext,
			(Class<AMProcessor<Object>>)(Class<?>)AMProcessor.class,
			"(model.class.name=*)",
			(serviceReference, emitter) -> emitter.emit(
				(String)serviceReference.getProperty("model.class.name")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		String className = message.getString("className");

		List<AMProcessor<Object>> amProcessors = _serviceTrackerMap.getService(
			className);

		if (amProcessors == null) {
			return;
		}

		AMProcessorCommand amProcessorCommand = (AMProcessorCommand)message.get(
			"command");

		Object model = message.get("model");
		String modelId = (String)message.get("modelId");

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					message.getLong("ctCollectionId"))) {

			for (AMProcessor<Object> amProcessor : amProcessors) {
				try {
					amProcessorCommand.execute(amProcessor, model, modelId);
				}
				catch (NoSuchFileEntryException noSuchFileEntryException) {
					if (_log.isInfoEnabled()) {
						_log.info(noSuchFileEntryException);
					}
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}
				}
			}
		}

		AMAsyncProcessorImpl.cleanQueue(amProcessorCommand, modelId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMMessageListener.class);

	private ServiceTrackerMap<String, List<AMProcessor<Object>>>
		_serviceTrackerMap;

}