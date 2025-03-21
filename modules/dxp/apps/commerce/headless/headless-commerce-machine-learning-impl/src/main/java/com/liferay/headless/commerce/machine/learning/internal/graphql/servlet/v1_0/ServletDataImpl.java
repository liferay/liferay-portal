/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.machine.learning.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.machine.learning.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.machine.learning.internal.resource.v1_0.AccountCategoryForecastResourceImpl;
import com.liferay.headless.commerce.machine.learning.internal.resource.v1_0.AccountForecastResourceImpl;
import com.liferay.headless.commerce.machine.learning.internal.resource.v1_0.SkuForecastResourceImpl;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.AccountCategoryForecastResource;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.AccountForecastResource;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.SkuForecastResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Query.setAccountCategoryForecastResourceComponentServiceObjects(
			_accountCategoryForecastResourceComponentServiceObjects);
		Query.setAccountForecastResourceComponentServiceObjects(
			_accountForecastResourceComponentServiceObjects);
		Query.setSkuForecastResourceComponentServiceObjects(
			_skuForecastResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Machine.Learning";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-machine-learning-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"query#accountCategoryForecastsByMonthlyRevenue",
						new ObjectValuePair<>(
							AccountCategoryForecastResourceImpl.class,
							"getAccountCategoryForecastsByMonthlyRevenuePage"));
					put(
						"query#accountForecastsByMonthlyRevenue",
						new ObjectValuePair<>(
							AccountForecastResourceImpl.class,
							"getAccountForecastsByMonthlyRevenuePage"));
					put(
						"query#skuForecastsByMonthlyRevenue",
						new ObjectValuePair<>(
							SkuForecastResourceImpl.class,
							"getSkuForecastsByMonthlyRevenuePage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountCategoryForecastResource>
		_accountCategoryForecastResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountForecastResource>
		_accountForecastResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SkuForecastResource>
		_skuForecastResourceComponentServiceObjects;

}