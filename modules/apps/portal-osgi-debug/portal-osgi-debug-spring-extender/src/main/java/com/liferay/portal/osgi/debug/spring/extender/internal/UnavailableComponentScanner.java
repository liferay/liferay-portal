/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.debug.spring.extender.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.osgi.debug.spring.extender.internal.configuration.UnavailableComponentScannerConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Matthew Tambara
 */
@Component(
	configurationPid = "com.liferay.portal.osgi.debug.spring.extender.internal.configuration.UnavailableComponentScannerConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class UnavailableComponentScanner {

	@Activate
	protected void activate(Map<String, Object> properties) {
		UnavailableComponentScannerConfiguration
			unavailableComponentScannerConfiguration =
				ConfigurableUtil.createConfigurable(
					UnavailableComponentScannerConfiguration.class, properties);

		long scanningInterval =
			unavailableComponentScannerConfiguration.
				unavailableComponentScanningInterval();

		if (scanningInterval > 0) {
			_unavailableComponentScanningThread =
				new UnavailableComponentScanningThread(
					scanningInterval * Time.SECOND);

			_unavailableComponentScanningThread.start();
		}
	}

	@Deactivate
	protected void deactivate() throws InterruptedException {
		if (_unavailableComponentScanningThread != null) {
			_unavailableComponentScanningThread.interrupt();

			_unavailableComponentScanningThread.join();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnavailableComponentScanner.class);

	private Thread _unavailableComponentScanningThread;

	private static class UnavailableComponentScanningThread extends Thread {

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(_scanningInterval);

					String scanResult =
						UnavailableComponentUtil.scanUnavailableComponents();

					if (scanResult.isEmpty()) {
						if (_log.isInfoEnabled()) {
							_log.info(
								"All Spring extender dependency manager " +
									"components are registered");
						}
					}
					else {
						if (_log.isWarnEnabled()) {
							_log.warn(scanResult);
						}
					}
				}
			}
			catch (InterruptedException interruptedException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Stopped scanning for unavailable components",
						interruptedException);
				}
			}
		}

		private UnavailableComponentScanningThread(long scanningInterval) {
			_scanningInterval = scanningInterval;

			setDaemon(true);
			setName("Spring Extender Unavailable Component Scanner");
		}

		private final long _scanningInterval;

	}

}