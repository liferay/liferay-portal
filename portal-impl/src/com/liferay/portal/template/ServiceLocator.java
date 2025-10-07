/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Brian Wing Shun Chan
 */
public class ServiceLocator {

	public static ServiceLocator getInstance() {
		return _serviceLocator;
	}

	public Object findService(String serviceName) {
		Object object = SystemBundleUtil.callService(serviceName, obj -> obj);

		if (PropsValues.TEMPLATE_ENGINE_SERVICE_LOCATOR_RESTRICT &&
			!(object instanceof BaseLocalService) &&
			!(object instanceof BaseService)) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Denied access to service \"", serviceName,
						"\" because it is not a Service Builder generated ",
						"service"));
			}

			object = null;
		}

		return object;
	}

	private ServiceLocator() {
	}

	private static final Log _log = LogFactoryUtil.getLog(ServiceLocator.class);

	private static final ServiceLocator _serviceLocator = new ServiceLocator();

}