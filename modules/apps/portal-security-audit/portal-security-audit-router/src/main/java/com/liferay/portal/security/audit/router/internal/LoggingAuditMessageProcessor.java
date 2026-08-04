/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.formatter.LogMessageFormatter;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "eventTypes=*", service = AuditMessageProcessor.class
)
public class LoggingAuditMessageProcessor implements AuditMessageProcessor {

	@Override
	public void process(AuditMessage auditMessage) {
		try {
			_process(auditMessage);
		}
		catch (Exception exception) {
			_log.fatal(
				"Unable to process audit message " + auditMessage, exception);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_loggingAuditMessageProcessorConfiguration =
			ConfigurableUtil.createConfigurable(
				LoggingAuditMessageProcessorConfiguration.class, properties);
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LogMessageFormatter.class, null,
			(serviceReference, emitter) -> {
				String format = (String)serviceReference.getProperty("format");

				if (Validator.isNull(format)) {
					throw new IllegalArgumentException(
						"The property \"format\" is null");
				}

				emitter.emit(format);
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _process(AuditMessage auditMessage) throws Exception {
		if (_loggingAuditMessageProcessorConfiguration.enabled() &&
			(_log.isInfoEnabled() ||
			 _loggingAuditMessageProcessorConfiguration.outputToConsole())) {

			LogMessageFormatter logMessageFormatter =
				_serviceTrackerMap.getService(
					_loggingAuditMessageProcessorConfiguration.
						logMessageFormat());

			if (logMessageFormatter == null) {
				if (_log.isWarnEnabled()) {
					String logMessageFormat =
						_loggingAuditMessageProcessorConfiguration.
							logMessageFormat();

					_log.warn(
						"No log message formatter found for log message " +
							"format " + logMessageFormat);
				}

				return;
			}

			String logMessage = logMessageFormatter.format(auditMessage);

			if (_log.isInfoEnabled()) {
				_log.info(logMessage);
			}

			if (_loggingAuditMessageProcessorConfiguration.outputToConsole()) {
				System.out.println(
					"LoggingAuditMessageProcessor: " + logMessage);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoggingAuditMessageProcessor.class);

	private volatile LoggingAuditMessageProcessorConfiguration
		_loggingAuditMessageProcessorConfiguration;
	private volatile ServiceTrackerMap<String, LogMessageFormatter>
		_serviceTrackerMap;

}