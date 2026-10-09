/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.expression.internal;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Leonardo Barros
 */
@Component(service = DDMExpressionFunctionRegistry.class)
public class DDMExpressionFunctionRegistryImpl
	implements DDMExpressionFunctionRegistry {

	@Override
	public Map<String, DDMExpressionFunction>
		getCustomDDMExpressionFunctions() {

		Map<String, DDMExpressionFunction> customDDMExpressionFunctions =
			new HashMap<>();

		for (DDMExpressionFunctionFactory ddmExpressionFunctionFactory :
				_serviceTrackerMap.values()) {

			DDMExpressionFunction ddmExpressionFunction =
				ddmExpressionFunctionFactory.create();

			if (!ddmExpressionFunction.isCustomDDMExpressionFunction()) {
				continue;
			}

			customDDMExpressionFunctions.put(
				ddmExpressionFunction.getName(), ddmExpressionFunction);
		}

		return customDDMExpressionFunctions;
	}

	@Override
	public Map<String, DDMExpressionFunctionFactory>
		getDDMExpressionFunctionFactories(Set<String> functionNames) {

		Map<String, DDMExpressionFunctionFactory>
			ddmExpressionFunctionFactories = new HashMap<>();

		for (String functionName : functionNames) {
			DDMExpressionFunctionFactory ddmExpressionFunctionFactory =
				_serviceTrackerMap.getService(functionName);

			if (ddmExpressionFunctionFactory == null) {
				continue;
			}

			ddmExpressionFunctionFactories.put(
				functionName, ddmExpressionFunctionFactory);
		}

		return ddmExpressionFunctionFactories;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public Map<String, DDMExpressionFunction> getDDMExpressionFunctions(
		Set<String> functionNames) {

		return Collections.emptyMap();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void ungetDDMExpressionFunctions(
		Map<String, DDMExpressionFunction> ddmExpressionFunctionsMap) {
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMExpressionFunctionFactory.class, "name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, DDMExpressionFunctionFactory>
		_serviceTrackerMap;

}