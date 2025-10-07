/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.internal;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.documentlibrary.store.DLStoreImpl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Shuyang Zhou
 */
@Component(service = {})
public class CTStoreRegistrator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, Store.class,
			new ServiceTrackerCustomizer<Store, ServiceRegistration<?>>() {

				@Override
				public ServiceRegistration<?> addingService(
					ServiceReference<Store> serviceReference) {

					if (GetterUtil.getBoolean(
							serviceReference.getProperty("republished"))) {

						return null;
					}

					String storeType = String.valueOf(
						serviceReference.getProperty("store.type"));

					if (GetterUtil.getBoolean(
							serviceReference.getProperty("ct.aware"))) {

						if (!StringUtil.equals(
								storeType, PropsValues.DL_STORE_IMPL)) {

							return null;
						}

						Store ctStore = bundleContext.getService(
							serviceReference);

						DLStoreImpl.setStore(ctStore);

						return bundleContext.registerService(
							Store.class, ctStore,
							HashMapDictionaryBuilder.<String, Object>put(
								"default", true
							).put(
								"republished", true
							).build());
					}

					Store ctStore = _ctStoreFactory.createCTStore(
						bundleContext.getService(serviceReference), storeType);

					if (StringUtil.equals(
							storeType, PropsValues.DL_STORE_IMPL)) {

						DLStoreImpl.setStore(ctStore);
					}

					return bundleContext.registerService(
						Store.class, ctStore,
						HashMapDictionaryBuilder.<String, Object>put(
							"ct.aware", true
						).put(
							"default",
							() -> {
								if (StringUtil.equals(
										storeType, PropsValues.DL_STORE_IMPL)) {

									return true;
								}

								return null;
							}
						).put(
							"republished", true
						).put(
							"store.type", storeType
						).build());
				}

				@Override
				public void modifiedService(
					ServiceReference<Store> serviceReference,
					ServiceRegistration<?> serviceRegistration) {
				}

				@Override
				public void removedService(
					ServiceReference<Store> serviceReference,
					ServiceRegistration<?> serviceRegistration) {

					serviceRegistration.unregister();

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	@Reference
	private CTStoreFactory _ctStoreFactory;

	private ServiceTracker<Store, ServiceRegistration<?>> _serviceTracker;

}