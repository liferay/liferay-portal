/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.top.head.extender.internal.osgi.util.tracker;

import com.liferay.frontend.js.top.head.extender.TopHeadResources;
import com.liferay.frontend.js.top.head.extender.internal.TopHeadResourcesImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = {})
public class TopHeadExtender
	implements ServiceTrackerCustomizer
		<ServletContext, ServiceRegistration<TopHeadResources>> {

	@Override
	public ServiceRegistration<TopHeadResources> addingService(
		ServiceReference<ServletContext> serviceReference) {

		Bundle bundle = serviceReference.getBundle();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		Map.Entry<List<String>, List<String>> entry = _scanTopHeadResources(
			headers);

		if (entry == null) {
			return null;
		}

		ServletContext servletContext = _bundleContext.getService(
			serviceReference);

		return _bundleContext.registerService(
			TopHeadResources.class,
			new TopHeadResourcesImpl(
				servletContext.getContextPath(), entry.getKey(),
				entry.getValue()),
			MapUtil.singletonDictionary(
				"service.ranking",
				GetterUtil.getInteger(headers.get("Liferay-Top-Head-Weight"))));
	}

	@Override
	public void modifiedService(
		ServiceReference<ServletContext> serviceReference,
		ServiceRegistration<TopHeadResources> serviceRegistration) {
	}

	@Override
	public void removedService(
		ServiceReference<ServletContext> serviceReference,
		ServiceRegistration<TopHeadResources> serviceRegistration) {

		serviceRegistration.unregister();

		_bundleContext.ungetService(serviceReference);
	}

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		_bundleContext = bundleContext;

		_serviceTracker = new ServiceTracker<>(
			bundleContext,
			bundleContext.createFilter(
				StringBundler.concat(
					"(&(objectClass=", ServletContext.class.getName(),
					")(osgi.web.symbolicname=*))")),
			this);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Map.Entry<List<String>, List<String>> _scanTopHeadResources(
		Dictionary<String, String> headers) {

		String liferayJsResourcesTopHead = headers.get(
			"Liferay-JS-Resources-Top-Head");

		String liferayJsResourcesTopHeadAuthenticated = headers.get(
			"Liferay-JS-Resources-Top-Head-Authenticated");

		if (Validator.isBlank(liferayJsResourcesTopHead) &&
			Validator.isBlank(liferayJsResourcesTopHeadAuthenticated)) {

			return null;
		}

		List<String> jsResourcePaths = null;

		if (Validator.isNull(liferayJsResourcesTopHead)) {
			jsResourcePaths = Collections.emptyList();
		}
		else {
			jsResourcePaths = Arrays.asList(
				liferayJsResourcesTopHead.split(StringPool.COMMA));
		}

		List<String> authenticatedJsResourcePaths = null;

		if (Validator.isNull(liferayJsResourcesTopHeadAuthenticated)) {
			authenticatedJsResourcePaths = Collections.emptyList();
		}
		else {
			authenticatedJsResourcePaths = Arrays.asList(
				liferayJsResourcesTopHeadAuthenticated.split(StringPool.COMMA));
		}

		return new AbstractMap.SimpleImmutableEntry<>(
			jsResourcePaths, authenticatedJsResourcePaths);
	}

	private BundleContext _bundleContext;
	private ServiceTracker
		<ServletContext, ServiceRegistration<TopHeadResources>> _serviceTracker;

}