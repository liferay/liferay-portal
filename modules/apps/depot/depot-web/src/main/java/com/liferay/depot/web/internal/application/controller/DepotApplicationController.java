/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application.controller;

import com.liferay.depot.application.DepotApplication;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotAppCustomizationLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.workflow.WorkflowHandlerVisibleFilter;
import com.liferay.trash.constants.TrashPortletKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DepotApplicationController.class)
public class DepotApplicationController {

	public Collection<DepotApplication> getCustomizableDepotApplications(
		long groupId) {

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			return Collections.emptyList();
		}

		Collection<DepotApplication> depotApplications = new ArrayList<>();

		for (DepotApplication depotApplication : _serviceTrackerMap.values()) {
			if (!depotApplication.isCustomizable() ||
				!depotApplication.isDepotTypeSupported(depotEntry.getType())) {

				continue;
			}

			depotApplications.add(depotApplication);
		}

		return depotApplications;
	}

	public boolean isClassNameEnabled(String className, long groupId) {
		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			return false;
		}

		for (DepotApplication depotApplication : _serviceTrackerMap.values()) {
			List<String> classNames = depotApplication.getClassNames();

			if (!classNames.contains(className)) {
				continue;
			}

			if (!depotApplication.isDepotTypeSupported(depotEntry.getType())) {
				return false;
			}

			if (!depotApplication.isCustomizable()) {
				return true;
			}

			DepotAppCustomization depotAppCustomization =
				_depotAppCustomizationLocalService.fetchDepotAppCustomization(
					depotEntry.getDepotEntryId(),
					depotApplication.getPortletId());

			if (depotAppCustomization == null) {
				return true;
			}

			return depotAppCustomization.isEnabled();
		}

		return false;
	}

	public boolean isEnabled(String portletId) {
		DepotApplication depotApplication = _serviceTrackerMap.getService(
			portletId);

		if (depotApplication == null) {
			return false;
		}

		return true;
	}

	public boolean isEnabled(String portletId, long groupId) {
		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			return false;
		}

		DepotApplication depotApplication = _serviceTrackerMap.getService(
			portletId);

		if ((depotApplication == null) ||
			!depotApplication.isDepotTypeSupported(depotEntry.getType())) {

			return false;
		}

		if (!depotApplication.isCustomizable()) {
			if (Objects.equals(portletId, TrashPortletKeys.TRASH)) {
				return _isTrashEnabled(depotEntry.getDepotEntryId());
			}

			return true;
		}

		DepotAppCustomization depotApplicationCustomization =
			_depotAppCustomizationLocalService.fetchDepotAppCustomization(
				depotEntry.getDepotEntryId(), depotApplication.getPortletId());

		if (depotApplicationCustomization == null) {
			return true;
		}

		return depotApplicationCustomization.isEnabled();
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DepotApplication.class, null,
			(serviceReference, emitter) -> {
				DepotApplication depotApplication = bundleContext.getService(
					serviceReference);

				emitter.emit(depotApplication.getPortletId());

				bundleContext.ungetService(serviceReference);
			});

		_serviceRegistration = bundleContext.registerService(
			WorkflowHandlerVisibleFilter.class,
			(workflowHandler, group) -> {
				if (!group.isDepot() ||
					isClassNameEnabled(
						workflowHandler.getClassName(), group.getGroupId())) {

					return workflowHandler.isVisible(group);
				}

				return false;
			},
			null);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		_serviceTrackerMap.close();
	}

	private boolean _isTrashEnabled(long depotEntryId) {
		int enabledDepotAppCustomizationsCount =
			_depotAppCustomizationLocalService.getDepotAppCustomizationsCount(
				depotEntryId, true);
		int disabledDepotAppCustomizationsCount =
			_depotAppCustomizationLocalService.getDepotAppCustomizationsCount(
				depotEntryId, false);

		if ((enabledDepotAppCustomizationsCount == 0) &&
			(disabledDepotAppCustomizationsCount > 0)) {

			return false;
		}

		return true;
	}

	@Reference
	private DepotAppCustomizationLocalService
		_depotAppCustomizationLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	private ServiceRegistration<?> _serviceRegistration;
	private ServiceTrackerMap<String, DepotApplication> _serviceTrackerMap;

}