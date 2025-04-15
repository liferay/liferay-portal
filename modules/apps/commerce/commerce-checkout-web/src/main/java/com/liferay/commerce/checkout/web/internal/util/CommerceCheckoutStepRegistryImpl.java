/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util;

import com.liferay.commerce.checkout.web.internal.util.comparator.CommerceCheckoutStepServiceWrapperOrderComparator;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Marco Leo
 */
@Component(service = CommerceCheckoutStepRegistry.class)
public class CommerceCheckoutStepRegistryImpl
	implements CommerceCheckoutStepRegistry {

	@Override
	public CommerceCheckoutStep getCommerceCheckoutStep(
		String commerceCheckoutStepName) {

		if (Validator.isNull(commerceCheckoutStepName)) {
			return null;
		}

		ServiceTrackerMap<String, ServiceWrapper<CommerceCheckoutStep>>
			commerceCheckoutStepServiceTrackerMap =
				_getCommerceCheckoutStepServiceTrackerMap();

		ServiceWrapper<CommerceCheckoutStep>
			commerceCheckoutStepServiceWrapper =
				commerceCheckoutStepServiceTrackerMap.getService(
					commerceCheckoutStepName);

		if (commerceCheckoutStepServiceWrapper == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No commerce checkout step registered with name " +
						commerceCheckoutStepName);
			}

			return null;
		}

		return commerceCheckoutStepServiceWrapper.getService();
	}

	@Override
	public List<CommerceCheckoutStep> getCommerceCheckoutSteps(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean onlyActive)
		throws Exception {

		List<CommerceCheckoutStep> commerceCheckoutSteps = new ArrayList<>();

		ServiceTrackerMap<String, ServiceWrapper<CommerceCheckoutStep>>
			commerceCheckoutStepServiceTrackerMap =
				_getCommerceCheckoutStepServiceTrackerMap();

		List<ServiceWrapper<CommerceCheckoutStep>>
			commerceCheckoutStepServiceWrappers = ListUtil.fromCollection(
				commerceCheckoutStepServiceTrackerMap.values());

		Collections.sort(
			commerceCheckoutStepServiceWrappers,
			_commerceCheckoutStepServiceWrapperDisplayOrderComparator);

		for (ServiceWrapper<CommerceCheckoutStep>
				commerceCheckoutStepServiceWrapper :
					commerceCheckoutStepServiceWrappers) {

			CommerceCheckoutStep commerceCheckoutStep =
				commerceCheckoutStepServiceWrapper.getService();

			if (!onlyActive ||
				commerceCheckoutStep.isActive(
					httpServletRequest, httpServletResponse)) {

				commerceCheckoutSteps.add(commerceCheckoutStep);
			}
		}

		return Collections.unmodifiableList(commerceCheckoutSteps);
	}

	@Override
	public CommerceCheckoutStep getNextCommerceCheckoutStep(
			String commerceCheckoutStepName,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (Validator.isNull(commerceCheckoutStepName)) {
			return null;
		}

		List<CommerceCheckoutStep> commerceCheckoutSteps =
			getCommerceCheckoutSteps(
				httpServletRequest, httpServletResponse, false);

		CommerceCheckoutStep currentCommerceCheckoutStep =
			getCommerceCheckoutStep(commerceCheckoutStepName);

		for (int commerceCheckoutStepIndex = commerceCheckoutSteps.indexOf(
				currentCommerceCheckoutStep);
			 commerceCheckoutStepIndex < commerceCheckoutSteps.size();
			 commerceCheckoutStepIndex++) {

			if ((commerceCheckoutStepIndex >= 0) &&
				(commerceCheckoutStepIndex <
					(commerceCheckoutSteps.size() - 1))) {

				CommerceCheckoutStep commerceCheckoutStep =
					commerceCheckoutSteps.get(commerceCheckoutStepIndex + 1);

				if (commerceCheckoutStep.isActive(
						httpServletRequest, httpServletResponse)) {

					return commerceCheckoutStep;
				}
			}
		}

		return null;
	}

	@Override
	public CommerceCheckoutStep getPreviousCommerceCheckoutStep(
			String commerceCheckoutStepName,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (Validator.isNull(commerceCheckoutStepName)) {
			return null;
		}

		List<CommerceCheckoutStep> commerceCheckoutSteps =
			getCommerceCheckoutSteps(
				httpServletRequest, httpServletResponse, true);

		int commerceCheckoutStepIndex = commerceCheckoutSteps.indexOf(
			getCommerceCheckoutStep(commerceCheckoutStepName));

		if (commerceCheckoutStepIndex > 0) {
			return commerceCheckoutSteps.get(commerceCheckoutStepIndex - 1);
		}

		return null;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();
		}
	}

	private ServiceTrackerMap<String, ServiceWrapper<CommerceCheckoutStep>>
		_getCommerceCheckoutStepServiceTrackerMap() {

		if (_serviceTrackerMap == null) {
			_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, CommerceCheckoutStep.class,
				"commerce.checkout.step.name",
				ServiceTrackerCustomizerFactory.
					<CommerceCheckoutStep>serviceWrapper(_bundleContext));
		}

		return _serviceTrackerMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCheckoutStepRegistryImpl.class);

	private BundleContext _bundleContext;
	private final Comparator<ServiceWrapper<CommerceCheckoutStep>>
		_commerceCheckoutStepServiceWrapperDisplayOrderComparator =
			new CommerceCheckoutStepServiceWrapperOrderComparator();
	private ServiceTrackerMap<String, ServiceWrapper<CommerceCheckoutStep>>
		_serviceTrackerMap;

}