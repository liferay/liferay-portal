/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.option;

import com.liferay.commerce.product.internal.option.comparator.CommerceOptionTypeServiceWrapperDisplayOrderComparator;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceOptionTypeRegistry.class)
public class CommerceOptionTypeRegistryImpl
	implements CommerceOptionTypeRegistry {

	@Override
	public CommerceOptionType getCommerceOptionType(String key) {
		if (Validator.isNull(key)) {
			return null;
		}

		ServiceWrapper<CommerceOptionType> commerceOptionTypeServiceWrapper =
			_serviceTrackerMap.getService(key);

		if (commerceOptionTypeServiceWrapper == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No commerce option type registered with key " + key);
			}

			return null;
		}

		CommerceOptionType commerceOptionType =
			commerceOptionTypeServiceWrapper.getService();

		if (commerceOptionType.isActive()) {
			return commerceOptionType;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce option type registered with key " + key +
					" is not active.");
		}

		return null;
	}

	@Override
	public List<CommerceOptionType> getCommerceOptionTypes() {
		List<CommerceOptionType> commerceOptionTypes = new ArrayList<>();

		List<ServiceWrapper<CommerceOptionType>>
			commerceOptionTypeServiceWrappers = ListUtil.fromCollection(
				_serviceTrackerMap.values());

		Collections.sort(
			commerceOptionTypeServiceWrappers,
			_commerceOptionTypeServiceWrapperDisplayOrderComparator);

		for (ServiceWrapper<CommerceOptionType>
				commerceOptionTypeServiceWrapper :
					commerceOptionTypeServiceWrappers) {

			CommerceOptionType commerceOptionType =
				commerceOptionTypeServiceWrapper.getService();

			if (!commerceOptionType.isActive()) {
				continue;
			}

			commerceOptionTypes.add(commerceOptionType);
		}

		return Collections.unmodifiableList(commerceOptionTypes);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CommerceOptionType.class, "commerce.option.type.key",
			ServiceTrackerCustomizerFactory.<CommerceOptionType>serviceWrapper(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOptionTypeRegistryImpl.class);

	private static final Comparator<ServiceWrapper<CommerceOptionType>>
		_commerceOptionTypeServiceWrapperDisplayOrderComparator =
			new CommerceOptionTypeServiceWrapperDisplayOrderComparator();

	private ServiceTrackerMap<String, ServiceWrapper<CommerceOptionType>>
		_serviceTrackerMap;

}