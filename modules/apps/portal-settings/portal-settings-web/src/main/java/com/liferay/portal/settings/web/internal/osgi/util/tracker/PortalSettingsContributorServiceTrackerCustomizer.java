/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.osgi.util.tracker;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;
import com.liferay.portal.settings.web.internal.portlet.action.DeletePortalSettingsFormMVCActionCommand;
import com.liferay.portal.settings.web.internal.portlet.action.SavePortalSettingsFormMVCActionCommand;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(service = {})
public class PortalSettingsContributorServiceTrackerCustomizer
	implements ServiceTrackerCustomizer
		<PortalSettingsFormContributor, PortalSettingsFormContributor> {

	@Override
	public PortalSettingsFormContributor addingService(
		ServiceReference<PortalSettingsFormContributor> serviceReference) {

		PortalSettingsFormContributor portalSettingsFormContributor =
			_bundleContext.getService(serviceReference);

		MVCActionCommandServiceRegistrationHolder
			mvcActionCommandServiceRegistrationHolder =
				new MVCActionCommandServiceRegistrationHolder();

		DeletePortalSettingsFormMVCActionCommand
			deletePortalSettingsFormMVCActionCommand =
				new DeletePortalSettingsFormMVCActionCommand(
					_portletPreferencesLocalService,
					portalSettingsFormContributor);

		mvcActionCommandServiceRegistrationHolder.
			_deleteMVCActionCommandServiceRegistration =
				_registerMVCActionCommand(
					portalSettingsFormContributor.
						getDeleteMVCActionCommandName(),
					deletePortalSettingsFormMVCActionCommand);

		SavePortalSettingsFormMVCActionCommand
			savePortalSettingsFormMVCActionCommand =
				new SavePortalSettingsFormMVCActionCommand(
					portalSettingsFormContributor);

		mvcActionCommandServiceRegistrationHolder.
			_saveMVCActionCommandServiceRegistration =
				_registerMVCActionCommand(
					portalSettingsFormContributor.getSaveMVCActionCommandName(),
					savePortalSettingsFormMVCActionCommand);

		_serviceRegistrationHolders.put(
			portalSettingsFormContributor.getSettingsId(),
			mvcActionCommandServiceRegistrationHolder);

		return portalSettingsFormContributor;
	}

	@Override
	public void modifiedService(
		ServiceReference<PortalSettingsFormContributor> serviceReference,
		PortalSettingsFormContributor service) {

		_unregister(service);

		addingService(serviceReference);
	}

	@Override
	public void removedService(
		ServiceReference<PortalSettingsFormContributor> serviceReference,
		PortalSettingsFormContributor service) {

		_unregister(service);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, PortalSettingsFormContributor.class, this);
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext = null;

		_serviceTracker.close();
	}

	private ServiceRegistration<MVCActionCommand> _registerMVCActionCommand(
		String mvcActionCommandName, MVCActionCommand mvcActionCommand) {

		return _bundleContext.registerService(
			MVCActionCommand.class, mvcActionCommand,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.name",
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS
			).put(
				"mvc.command.name", mvcActionCommandName
			).build());
	}

	private void _unregister(
		PortalSettingsFormContributor portalSettingsFormContributor) {

		MVCActionCommandServiceRegistrationHolder
			mvcActionCommandServiceRegistrationHolder =
				_serviceRegistrationHolders.get(
					portalSettingsFormContributor.getSettingsId());

		if (mvcActionCommandServiceRegistrationHolder == null) {
			return;
		}

		if (mvcActionCommandServiceRegistrationHolder.
				_deleteMVCActionCommandServiceRegistration != null) {

			mvcActionCommandServiceRegistrationHolder.
				_deleteMVCActionCommandServiceRegistration.unregister();
		}

		if (mvcActionCommandServiceRegistrationHolder.
				_saveMVCActionCommandServiceRegistration != null) {

			mvcActionCommandServiceRegistrationHolder.
				_saveMVCActionCommandServiceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private final Map<String, MVCActionCommandServiceRegistrationHolder>
		_serviceRegistrationHolders = new Hashtable<>();
	private ServiceTracker
		<PortalSettingsFormContributor, PortalSettingsFormContributor>
			_serviceTracker;

	private class MVCActionCommandServiceRegistrationHolder {

		private ServiceRegistration<MVCActionCommand>
			_deleteMVCActionCommandServiceRegistration;
		private ServiceRegistration<MVCActionCommand>
			_saveMVCActionCommandServiceRegistration;

	}

}