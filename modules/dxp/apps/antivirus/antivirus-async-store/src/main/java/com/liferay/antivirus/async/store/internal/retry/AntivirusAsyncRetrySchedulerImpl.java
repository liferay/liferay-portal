/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.retry;

import com.liferay.antivirus.async.store.AntivirusScannerHelper;
import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.retry.AntivirusAsyncRetryScheduler;
import com.liferay.antivirus.async.store.util.AntivirusAsyncUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TransientValue;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
	service = AntivirusAsyncRetryScheduler.class
)
public class AntivirusAsyncRetrySchedulerImpl
	implements AntivirusAsyncRetryScheduler {

	@Override
	public void schedule(Message message) {
		Map<String, Object> map = message.getValues();

		Set<Map.Entry<String, Object>> entrySet = map.entrySet();

		Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();

			if (!(entry.getValue() instanceof TransientValue)) {
				continue;
			}

			iterator.remove();
		}

		int retryCount = message.getInteger("retryCount") + 1;

		if (retryCount > _MAXIMUM_RETRIES) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Stop retrying the antivirus scan of ",
						AntivirusAsyncUtil.getFileIdentifier(message),
						" after ", retryCount - 1, " retries"));
			}

			return;
		}

		long delay = Math.min(
			(Time.MINUTE * 5) << Math.min(retryCount - 1, 9), Time.DAY);

		message.put("retryCount", retryCount);
		message.put("retryTime", System.currentTimeMillis() + delay);

		_messages.putIfAbsent(message.getString("jobName"), message);
	}

	@Override
	public void unschedule(Message message) {
		_messages.remove(message.getString("jobName"));
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		AntivirusAsyncConfiguration antivirusAsyncConfiguration =
			ConfigurableUtil.createConfigurable(
				AntivirusAsyncConfiguration.class, properties);

		TriggerConfiguration triggerConfiguration =
			TriggerConfiguration.createTriggerConfiguration(
				antivirusAsyncConfiguration.retryCronExpression());

		triggerConfiguration.setStartDate(
			new Date(System.currentTimeMillis() + (Time.SECOND * 10)));

		_serviceRegistration = bundleContext.registerService(
			SchedulerJobConfiguration.class,
			new AntivirusAsyncRetrySchedulerJobConfiguration(
				triggerConfiguration),
			null);
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private static final int _MAXIMUM_RETRIES = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		AntivirusAsyncRetrySchedulerImpl.class);

	@Reference
	private AntivirusScannerHelper _antivirusScannerHelper;

	private final Map<String, Message> _messages = new ConcurrentHashMap<>();
	private final AtomicBoolean _running = new AtomicBoolean();
	private ServiceRegistration<SchedulerJobConfiguration> _serviceRegistration;

	private class AntivirusAsyncRetrySchedulerJobConfiguration
		implements SchedulerJobConfiguration {

		@Override
		public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
			return () -> {
				if (!_running.compareAndSet(false, true)) {
					return;
				}

				try {
					for (Map.Entry<String, Message> entry :
							_messages.entrySet()) {

						Message message = entry.getValue();

						if ((message.getLong("retryTime") >
								System.currentTimeMillis()) ||
							!_messages.remove(entry.getKey(), message)) {

							continue;
						}

						_antivirusScannerHelper.processMessage(message);
					}
				}
				finally {
					_running.set(false);
				}
			};
		}

		@Override
		public TriggerConfiguration getTriggerConfiguration() {
			return _triggerConfiguration;
		}

		private AntivirusAsyncRetrySchedulerJobConfiguration(
			TriggerConfiguration triggerConfiguration) {

			_triggerConfiguration = triggerConfiguration;
		}

		private final TriggerConfiguration _triggerConfiguration;

	}

}