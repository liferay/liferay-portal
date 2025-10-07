/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.unit;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

	public void registerBatchEngineUnit(
		long companyId, String featureFlagKey,
		UnsafeSupplier<CompletableFuture<Void>, Exception> unsafeSupplier) {

		_unsafeSuppliers.compute(
			_getTuple(companyId, featureFlagKey),
			(key, unsafeSuppliers) -> {
				if (unsafeSuppliers == null) {
					unsafeSuppliers = new ArrayList<>();
				}

				unsafeSuppliers.add(unsafeSupplier);

				return unsafeSuppliers;
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

	private Tuple _getTuple(long companyId, String featureFlagKey) {
		return new Tuple(companyId, featureFlagKey);
	}

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private ServiceRegistration<FeatureFlagListener> _serviceRegistration;
	private final Map
		<Tuple, List<UnsafeSupplier<CompletableFuture<Void>, Exception>>>
			_unsafeSuppliers = new ConcurrentHashMap<>();

	private class FeatureFlagListenerImpl implements FeatureFlagListener {

		@Override
		public void onValue(
			long companyId, String featureFlagKey, boolean enabled) {

			if (!enabled) {
				return;
			}

			Tuple tuple = _getTuple(companyId, featureFlagKey);

			if (!_unsafeSuppliers.containsKey(tuple)) {
				return;
			}

			synchronized (_unsafeSuppliers) {
				List<UnsafeSupplier<CompletableFuture<Void>, Exception>>
					unsafeSuppliers = _unsafeSuppliers.remove(tuple);

				ExecutorService executorService =
					_portalExecutorManager.getPortalExecutor(
						FeatureFlagListenerImpl.class.getName());

				executorService.submit(
					() -> {
						for (UnsafeSupplier<CompletableFuture<Void>, Exception>
								unsafeSupplier : unsafeSuppliers) {

							try {
								CompletableFuture<Void> completableFuture =
									unsafeSupplier.get();

								completableFuture.get();
							}
							catch (Exception exception) {
								throw new RuntimeException(exception);
							}
						}
					});
			}
		}

	}

}