/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.unit;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = FeatureFlagBatchEngineUnitProcessor.class)
public class FeatureFlagBatchEngineUnitProcessor {

	public void registerBatchEngineUnits(
		long companyId, String featureFlagKey,
		UnsafeRunnable<Exception> unsafeRunnable) {

		_unsafeRunnables.compute(
			Map.entry(companyId, featureFlagKey),
			(key, unsafeRunnables) -> {
				if (unsafeRunnables == null) {
					unsafeRunnables = new ArrayList<>();
				}

				unsafeRunnables.add(unsafeRunnable);

				return unsafeRunnables;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class, new FeatureFlagListenerImpl(),
			MapUtil.singletonDictionary("feature.flag.key", "*"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FeatureFlagBatchEngineUnitProcessor.class);

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private ServiceRegistration<FeatureFlagListener> _serviceRegistration;
	private final Map<Map.Entry<Long, String>, List<UnsafeRunnable<Exception>>>
		_unsafeRunnables = new ConcurrentHashMap<>();

	private class FeatureFlagListenerImpl implements FeatureFlagListener {

		@Override
		public void onValue(
			long companyId, String featureFlagKey, boolean enabled) {

			if (!enabled) {
				return;
			}

			List<UnsafeRunnable<Exception>> unsafeRunnables =
				_unsafeRunnables.remove(Map.entry(companyId, featureFlagKey));

			if (unsafeRunnables == null) {
				return;
			}

			ExecutorService executorService =
				_portalExecutorManager.getPortalExecutor(
					FeatureFlagListenerImpl.class.getName());

			executorService.submit(
				() -> {
					for (UnsafeRunnable<Exception> unsafeRunnable :
							unsafeRunnables) {

						try {
							unsafeRunnable.run();
						}
						catch (Throwable throwable) {
							_log.error(
								StringBundler.concat(
									"Unable to process batch engine units ",
									"deferred for feature flag ",
									featureFlagKey, " in company ", companyId),
								throwable);

							return;
						}
					}
				});
		}

	}

}