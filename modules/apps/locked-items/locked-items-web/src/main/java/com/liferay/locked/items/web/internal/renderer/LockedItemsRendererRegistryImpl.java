/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.locked.items.web.internal.renderer;

import com.liferay.locked.items.renderer.LockedItemsRenderer;
import com.liferay.locked.items.renderer.LockedItemsRendererRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Marco Galluzzi
 */
@Component(service = LockedItemsRendererRegistry.class)
public class LockedItemsRendererRegistryImpl
	implements LockedItemsRendererRegistry {

	@Override
	public LockedItemsRenderer getLockedItemsRenderer(String key) {
		return _serviceTrackerMap.getService(key);
	}

	@Override
	public List<LockedItemsRenderer> getLockedItemsRenderers() {
		List<LockedItemsRenderer> lockedItemsRenderers = new ArrayList<>();

		for (LockedItemsRenderer lockedItemsRenderer :
				_serviceTrackerMap.values()) {

			if (!lockedItemsRenderer.isVisible()) {
				continue;
			}

			lockedItemsRenderers.add(lockedItemsRenderer);
		}

		return lockedItemsRenderers;
	}

	@Override
	public int getLockedItemsRenderersCount() {
		List<LockedItemsRenderer> lockedItemsRenderers =
			getLockedItemsRenderers();

		return lockedItemsRenderers.size();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LockedItemsRenderer.class, null,
			(serviceReference, emitter) -> {
				LockedItemsRenderer lockedItemsRenderer =
					bundleContext.getService(serviceReference);

				emitter.emit(lockedItemsRenderer.getKey());
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, LockedItemsRenderer> _serviceTrackerMap;

}