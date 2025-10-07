/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Roberto Díaz
 */
public class RoleMembershipPolicyFactoryUtil {

	public static RoleMembershipPolicy getRoleMembershipPolicy() {
		ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
			serviceTracker = _serviceTrackerDCLSingleton.getSingleton(
				RoleMembershipPolicyFactoryUtil::_createServiceTracker);

		return serviceTracker.getService();
	}

	private static ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
		_createServiceTracker() {

		ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
			serviceTracker = new ServiceTracker<>(
				_bundleContext, RoleMembershipPolicy.class,
				new RoleMembershipPolicyTrackerCustomizer());

		serviceTracker.open();

		return serviceTracker;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RoleMembershipPolicyFactoryUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final DCLSingleton
		<ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>>
			_serviceTrackerDCLSingleton = new DCLSingleton<>();

	private static class RoleMembershipPolicyTrackerCustomizer
		implements ServiceTrackerCustomizer
			<RoleMembershipPolicy, RoleMembershipPolicy> {

		@Override
		public RoleMembershipPolicy addingService(
			ServiceReference<RoleMembershipPolicy> serviceReference) {

			RoleMembershipPolicy roleMembershipPolicy =
				_bundleContext.getService(serviceReference);

			if (PropsValues.MEMBERSHIP_POLICY_AUTO_VERIFY) {
				try {
					roleMembershipPolicy.verifyPolicy();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}

			return roleMembershipPolicy;
		}

		@Override
		public void modifiedService(
			ServiceReference<RoleMembershipPolicy> serviceReference,
			RoleMembershipPolicy roleMembershipPolicy) {
		}

		@Override
		public void removedService(
			ServiceReference<RoleMembershipPolicy> serviceReference,
			RoleMembershipPolicy roleMembershipPolicy) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}