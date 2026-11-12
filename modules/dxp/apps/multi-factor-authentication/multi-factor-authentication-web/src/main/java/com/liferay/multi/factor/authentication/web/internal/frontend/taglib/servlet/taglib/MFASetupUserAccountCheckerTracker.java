/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.multi.factor.authentication.spi.checker.setup.SetupMFAChecker;
import com.liferay.multi.factor.authentication.web.internal.policy.MFAPolicy;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import jakarta.servlet.ServletContext;

import java.util.Dictionary;

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
 * @author Tomas Polesovsky
 */
@Component(service = {})
public class MFASetupUserAccountCheckerTracker {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, SetupMFAChecker.class,
			new MFACheckerSetupServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private BundleContext _bundleContext;

	@Reference
	private MFAPolicy _mfaPolicy;

	private ServiceTracker
		<SetupMFAChecker, ServiceRegistration<ScreenNavigationEntry<User>>>
			_serviceTracker;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.web)"
	)
	private ServletContext _servletContext;

	private class MFACheckerSetupServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<SetupMFAChecker,
			 ServiceRegistration<ScreenNavigationEntry<User>>> {

		@Override
		public ServiceRegistration<ScreenNavigationEntry<User>> addingService(
			ServiceReference<SetupMFAChecker> serviceReference) {

			SetupMFAChecker setupMFAChecker = _bundleContext.getService(
				serviceReference);

			if (setupMFAChecker == null) {
				return null;
			}

			return (ServiceRegistration)_bundleContext.registerService(
				ScreenNavigationEntry.class,
				new MFASetupUserAccountScreenNavigationEntry(
					_mfaPolicy, serviceReference, _servletContext,
					setupMFAChecker),
				_buildProperties(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference<SetupMFAChecker> serviceReference,
			ServiceRegistration<ScreenNavigationEntry<User>>
				serviceRegistration) {

			serviceRegistration.setProperties(
				_buildProperties(serviceReference));
		}

		@Override
		public void removedService(
			ServiceReference<SetupMFAChecker> serviceReference,
			ServiceRegistration<ScreenNavigationEntry<User>>
				serviceRegistration) {

			serviceRegistration.unregister();

			_bundleContext.ungetService(serviceReference);
		}

		private Dictionary<String, Object> _buildProperties(
			ServiceReference<SetupMFAChecker> serviceReference) {

			return HashMapDictionaryBuilder.<String, Object>put(
				"screen.navigation.entry.order",
				GetterUtil.getInteger(
					serviceReference.getProperty(
						"user.account.screen.navigation.entry.order"),
					Integer.MAX_VALUE)
			).build();
		}

	}

}