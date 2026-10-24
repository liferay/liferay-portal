/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.registry;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.component.UADComponent;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.display.UADHierarchyDeclaration;
import com.liferay.user.associated.data.exporter.UADExporter;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author William Newbury
 */
@Component(service = UADRegistry.class)
public class UADRegistry {

	public List<UADAnonymizer<?>> getApplicationUADAnonymizers(
		String applicationKey) {

		return _bundleUADAnonymizerServiceTrackerMap.getService(applicationKey);
	}

	public Set<String> getApplicationUADAnonymizersKeySet() {
		return _bundleUADAnonymizerServiceTrackerMap.keySet();
	}

	public List<UADDisplay<?>> getApplicationUADDisplays(
		String applicationKey) {

		List<UADDisplay<?>> uadDisplayList =
			_bundleUADDisplayServiceTrackerMap.getService(applicationKey);

		if (uadDisplayList == null) {
			return Collections.emptyList();
		}

		return uadDisplayList;
	}

	public Set<String> getApplicationUADDisplaysKeySet() {
		return _bundleUADDisplayServiceTrackerMap.keySet();
	}

	public List<UADAnonymizer<?>> getNonreviewableApplicationUADAnonymizers(
		String applicationKey) {

		return new ArrayList<>(
			_getNonreviewableUADAnonymizers(
				getApplicationUADAnonymizers(applicationKey),
				getApplicationUADDisplays(applicationKey)));
	}

	public Collection<UADAnonymizer<?>> getNonreviewableUADAnonymizers() {
		return _getNonreviewableUADAnonymizers(
			getUADAnonymizers(), getUADDisplays());
	}

	public UADAnonymizer<?> getUADAnonymizer(String key) {
		return _uadAnonymizerServiceTrackerMap.getService(key);
	}

	public Collection<UADAnonymizer<?>> getUADAnonymizers() {
		return _uadAnonymizerServiceTrackerMap.values();
	}

	public UADDisplay<?> getUADDisplay(String key) {
		return _uadDisplayServiceTrackerMap.getService(key);
	}

	public UADDisplay<?> getUADDisplayByObject(Object object) {
		for (UADDisplay<?> uadDisplay : getUADDisplays()) {
			if (uadDisplay.isTypeEntity(object)) {
				return uadDisplay;
			}
		}

		return null;
	}

	public Collection<UADDisplay<?>> getUADDisplays() {
		return _uadDisplayServiceTrackerMap.values();
	}

	public UADExporter<?> getUADExporter(String key) {
		return _uadExporterServiceTrackerMap.getService(key);
	}

	public UADHierarchyDisplay getUADHierarchyDisplay(String applicationKey) {
		UADHierarchyDeclaration uadHierarchyDeclaration =
			_getUADHierarchyDeclaration(applicationKey);

		if (uadHierarchyDeclaration == null) {
			return null;
		}

		return new UADHierarchyDisplay(uadHierarchyDeclaration, this);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleUADAnonymizerServiceTrackerMap = _getMultiValueServiceTrackerMap(
			bundleContext,
			(Class<UADAnonymizer<?>>)(Class<?>)UADAnonymizer.class);
		_bundleUADDisplayServiceTrackerMap = _getMultiValueServiceTrackerMap(
			bundleContext, (Class<UADDisplay<?>>)(Class<?>)UADDisplay.class);
		_bundleUADHierarchyDeclarationServiceTrackerMap =
			_getUADHierachyDeclarationServiceTrackerMap(
				bundleContext, UADHierarchyDeclaration.class);
		_uadAnonymizerServiceTrackerMap = _getSingleValueServiceTrackerMap(
			bundleContext,
			(Class<UADAnonymizer<?>>)(Class<?>)UADAnonymizer.class);
		_uadDisplayServiceTrackerMap = _getSingleValueServiceTrackerMap(
			bundleContext, (Class<UADDisplay<?>>)(Class<?>)UADDisplay.class);
		_uadExporterServiceTrackerMap = _getSingleValueServiceTrackerMap(
			bundleContext, (Class<UADExporter<?>>)(Class<?>)UADExporter.class);
	}

	@Deactivate
	protected void deactivate() {
		_bundleUADAnonymizerServiceTrackerMap.close();
		_bundleUADDisplayServiceTrackerMap.close();
		_bundleUADHierarchyDeclarationServiceTrackerMap.close();
		_uadAnonymizerServiceTrackerMap.close();
		_uadDisplayServiceTrackerMap.close();
		_uadExporterServiceTrackerMap.close();
	}

	private <T extends UADComponent> ServiceTrackerMap<String, List<T>>
		_getMultiValueServiceTrackerMap(
			BundleContext bundleContext, Class<T> clazz) {

		return ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, clazz, null,
			(serviceReference, emitter) -> {
				Bundle bundle = serviceReference.getBundle();

				emitter.emit(bundle.getSymbolicName());
			});
	}

	private Collection<UADAnonymizer<?>> _getNonreviewableUADAnonymizers(
		Collection<UADAnonymizer<?>> uadAnonymizers,
		Collection<UADDisplay<?>> uadDisplayList) {

		List<String> uadDisplayTypeKeys = new ArrayList<>();

		for (UADDisplay<?> uadDisplay : uadDisplayList) {
			uadDisplayTypeKeys.add(uadDisplay.getTypeKey());
		}

		List<UADAnonymizer<?>> nonreviewableUADAnonymizers = new ArrayList<>(
			uadAnonymizers);

		for (UADAnonymizer<?> uadAnonymizer : uadAnonymizers) {
			if (!uadDisplayTypeKeys.contains(uadAnonymizer.getTypeKey())) {
				continue;
			}

			nonreviewableUADAnonymizers.remove(uadAnonymizer);
		}

		return nonreviewableUADAnonymizers;
	}

	private <T extends UADComponent> ServiceTrackerMap<String, T>
		_getSingleValueServiceTrackerMap(
			BundleContext bundleContext, Class<T> clazz) {

		return ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, clazz, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(uadComponent, emitter) -> emitter.emit(
					uadComponent.getTypeKey())));
	}

	private <T> ServiceTrackerMap<String, T>
		_getUADHierachyDeclarationServiceTrackerMap(
			BundleContext bundleContext, Class<T> clazz) {

		return ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, clazz, null,
			(serviceReference, emitter) -> {
				Bundle bundle = serviceReference.getBundle();

				emitter.emit(bundle.getSymbolicName());
			});
	}

	private UADHierarchyDeclaration _getUADHierarchyDeclaration(
		String applicationKey) {

		return _bundleUADHierarchyDeclarationServiceTrackerMap.getService(
			applicationKey);
	}

	private ServiceTrackerMap<String, List<UADAnonymizer<?>>>
		_bundleUADAnonymizerServiceTrackerMap;
	private ServiceTrackerMap<String, List<UADDisplay<?>>>
		_bundleUADDisplayServiceTrackerMap;
	private ServiceTrackerMap<String, UADHierarchyDeclaration>
		_bundleUADHierarchyDeclarationServiceTrackerMap;
	private ServiceTrackerMap<String, UADAnonymizer<?>>
		_uadAnonymizerServiceTrackerMap;
	private ServiceTrackerMap<String, UADDisplay<?>>
		_uadDisplayServiceTrackerMap;
	private ServiceTrackerMap<String, UADExporter<?>>
		_uadExporterServiceTrackerMap;

}