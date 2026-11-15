/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler;

import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.adaptive.media.image.scaler.AMImageScalerRegistry;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Sergio González
 */
@Component(service = AMImageScalerRegistry.class)
public class AMImageScalerRegistryImpl implements AMImageScalerRegistry {

	@Override
	public AMImageScaler getAMImageScaler(String mimeType) {
		List<AMImageScaler> amImageScalers = _serviceTrackerMap.getService(
			mimeType);

		if (ListUtil.isNotEmpty(amImageScalers)) {
			for (AMImageScaler amImageScaler : amImageScalers) {
				if (amImageScaler.isEnabled()) {
					return amImageScaler;
				}
			}
		}

		return _amDefaultImageScaler;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, AMImageScaler.class, "(mimeTypes=*)",
			new PropertyServiceReferenceMapper<>("mimeTypes"),
			new PropertyServiceReferenceComparator<>("service.ranking"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private final AMImageScaler _amDefaultImageScaler =
		new AMDefaultImageScaler();
	private ServiceTrackerMap<String, List<AMImageScaler>> _serviceTrackerMap;

}