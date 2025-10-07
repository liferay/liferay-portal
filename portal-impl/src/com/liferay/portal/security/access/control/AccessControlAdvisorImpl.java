/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.access.control;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlPolicy;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.util.PropsValues;

import java.lang.reflect.Method;

/**
 * @author Tomas Polesovsky
 * @author Igor Spasic
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class AccessControlAdvisorImpl implements AccessControlAdvisor {

	@Override
	public void accept(
			Method method, Object[] arguments,
			AccessControlled accessControlled)
		throws SecurityException {

		if (AccessControlThreadLocal.isRemoteAccess()) {
			try {
				for (AccessControlPolicy accessControlPolicy :
						_accessControlPolicies) {

					accessControlPolicy.onServiceRemoteAccess(
						method, arguments, accessControlled);
				}
			}
			catch (SecurityException securityException) {
				if (_log.isDebugEnabled()) {
					_log.debug(securityException);
				}

				if (PropsValues.ACCESS_CONTROL_SANITIZE_SECURITY_EXCEPTION) {
					throw new SecurityException();
				}

				throw securityException;
			}
		}
		else {
			for (AccessControlPolicy accessControlPolicy :
					_accessControlPolicies) {

				accessControlPolicy.onServiceAccess(
					method, arguments, accessControlled);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccessControlAdvisorImpl.class.getName());

	private static final ServiceTrackerList<AccessControlPolicy>
		_accessControlPolicies = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), AccessControlPolicy.class);

}