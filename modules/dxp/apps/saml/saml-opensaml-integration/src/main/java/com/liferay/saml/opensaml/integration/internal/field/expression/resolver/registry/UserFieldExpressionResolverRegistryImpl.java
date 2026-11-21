/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.field.expression.resolver.registry;

import com.liferay.saml.opensaml.integration.field.expression.resolver.UserFieldExpressionResolver;
import com.liferay.saml.opensaml.integration.field.expression.resolver.registry.UserFieldExpressionResolverRegistry;
import com.liferay.saml.opensaml.integration.internal.service.tracker.collections.OrderedServiceTrackerMap;

import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stian Sigvartsen
 */
@Component(service = UserFieldExpressionResolverRegistry.class)
public class UserFieldExpressionResolverRegistryImpl
	implements UserFieldExpressionResolverRegistry {

	@Override
	public String getDefaultUserFieldExpressionResolverKey() {
		return _orderedServiceTrackerMap.getDefaultServiceKey();
	}

	@Override
	public List<Map.Entry<String, UserFieldExpressionResolver>>
		getOrderedUserFieldExpressionResolvers() {

		return _orderedServiceTrackerMap.getOrderedServices();
	}

	@Override
	public UserFieldExpressionResolver getUserFieldExpressionResolver(
		String key) {

		return _orderedServiceTrackerMap.getService(key);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_orderedServiceTrackerMap = new OrderedServiceTrackerMap<>(
			bundleContext, UserFieldExpressionResolver.class, "key");
	}

	@Deactivate
	protected void deactivate() {
		_orderedServiceTrackerMap.close();
	}

	private OrderedServiceTrackerMap<UserFieldExpressionResolver>
		_orderedServiceTrackerMap;

}