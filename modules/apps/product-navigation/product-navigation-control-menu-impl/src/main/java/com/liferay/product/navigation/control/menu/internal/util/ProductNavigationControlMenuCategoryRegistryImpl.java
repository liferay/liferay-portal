/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.internal.util;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuCategory;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuCategoryRegistry;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuEntryRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(service = ProductNavigationControlMenuCategoryRegistry.class)
public class ProductNavigationControlMenuCategoryRegistryImpl
	implements ProductNavigationControlMenuCategoryRegistry {

	@Override
	public List<ProductNavigationControlMenuCategory>
		getProductNavigationControlMenuCategories(
			String productNavigationControlMenuCategoryKey) {

		List<ProductNavigationControlMenuCategory>
			productNavigationControlMenuCategories =
				_serviceTrackerMap.getService(
					productNavigationControlMenuCategoryKey);

		if (productNavigationControlMenuCategories == null) {
			return Collections.emptyList();
		}

		return new ArrayList<>(productNavigationControlMenuCategories);
	}

	@Override
	public List<ProductNavigationControlMenuCategory>
		getProductNavigationControlMenuCategories(
			String productNavigationControlMenuCategoryKey,
			HttpServletRequest httpServletRequest) {

		List<ProductNavigationControlMenuCategory>
			productNavigationControlMenuCategories =
				getProductNavigationControlMenuCategories(
					productNavigationControlMenuCategoryKey);

		if (productNavigationControlMenuCategories.isEmpty()) {
			return productNavigationControlMenuCategories;
		}

		return ListUtil.filter(
			productNavigationControlMenuCategories,
			productNavigationControlMenuCategory -> {
				try {
					if (!productNavigationControlMenuCategory.
							hasAccessPermission(httpServletRequest)) {

						return false;
					}

					List<ProductNavigationControlMenuEntry>
						productNavigationControlMenuEntries =
							_productNavigationControlMenuEntryRegistry.
								getProductNavigationControlMenuEntries(
									productNavigationControlMenuCategory,
									httpServletRequest);

					return !productNavigationControlMenuEntries.isEmpty();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				return false;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, ProductNavigationControlMenuCategory.class, null,
			new PropertyServiceReferenceMapper<>(
				"product.navigation.control.menu.category.key"),
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"product.navigation.control.menu.category.order")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductNavigationControlMenuCategoryRegistryImpl.class);

	@Reference
	private ProductNavigationControlMenuEntryRegistry
		_productNavigationControlMenuEntryRegistry;

	private ServiceTrackerMap
		<String, List<ProductNavigationControlMenuCategory>> _serviceTrackerMap;

}