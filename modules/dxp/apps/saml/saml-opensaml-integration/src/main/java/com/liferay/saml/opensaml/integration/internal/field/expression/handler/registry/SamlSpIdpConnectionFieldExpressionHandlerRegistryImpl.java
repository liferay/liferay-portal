/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.field.expression.handler.registry;

import com.liferay.saml.opensaml.integration.field.expression.handler.SamlSpIdpConnectionFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.SamlSpIdpConnectionFieldExpressionHandlerRegistry;
import com.liferay.saml.opensaml.integration.internal.service.tracker.collections.OrderedServiceTrackerMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stian Sigvartsen
 */
@Component(service = SamlSpIdpConnectionFieldExpressionHandlerRegistry.class)
public class SamlSpIdpConnectionFieldExpressionHandlerRegistryImpl
	implements SamlSpIdpConnectionFieldExpressionHandlerRegistry {

	@Override
	public SamlSpIdpConnectionFieldExpressionHandler getFieldExpressionHandler(
		String prefix) {

		return _orderedServiceTrackerMap.getService(prefix);
	}

	@Override
	public Set<String> getFieldExpressionHandlerPrefixes() {
		return _orderedServiceTrackerMap.getServicesKeys();
	}

	@Override
	public List<String> getOrderedFieldExpressionHandlerPrefixes() {
		return _orderedServiceTrackerMap.getOrderedServicesKeys();
	}

	@Override
	public List<Map.Entry<String, SamlSpIdpConnectionFieldExpressionHandler>>
		getOrderedFieldExpressionHandlers() {

		return _orderedServiceTrackerMap.getOrderedServices();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_orderedServiceTrackerMap = new OrderedServiceTrackerMap<>(
			bundleContext, SamlSpIdpConnectionFieldExpressionHandler.class,
			"prefix");
	}

	@Deactivate
	protected void deactivate() {
		_orderedServiceTrackerMap.close();
	}

	private OrderedServiceTrackerMap<SamlSpIdpConnectionFieldExpressionHandler>
		_orderedServiceTrackerMap;

}