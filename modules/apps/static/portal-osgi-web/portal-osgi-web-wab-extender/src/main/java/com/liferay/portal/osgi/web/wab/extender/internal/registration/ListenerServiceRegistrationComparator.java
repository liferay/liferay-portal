/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.registration;

import jakarta.servlet.ServletContextListener;

import java.util.Comparator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Juan González
 */
public class ListenerServiceRegistrationComparator
	implements Comparator<ServiceRegistration<?>> {

	@Override
	public int compare(
		ServiceRegistration<?> serviceRegistration1,
		ServiceRegistration<?> serviceRegistration2) {

		Integer servletContextListenerCount1 = 0;

		ServiceReference<?> serviceReference1 =
			serviceRegistration1.getReference();

		String[] objectClassNames1 = (String[])serviceReference1.getProperty(
			Constants.OBJECTCLASS);

		for (String objectClassName : objectClassNames1) {
			if (objectClassName.equals(
					ServletContextListener.class.getName())) {

				servletContextListenerCount1++;
			}
		}

		Integer servletContextListenerCount2 = 0;

		ServiceReference<?> serviceReference2 =
			serviceRegistration2.getReference();

		String[] objectClassNames2 = (String[])serviceReference2.getProperty(
			Constants.OBJECTCLASS);

		for (String objectClassName : objectClassNames2) {
			if (objectClassName.equals(
					ServletContextListener.class.getName())) {

				servletContextListenerCount2++;
			}
		}

		if (servletContextListenerCount1.equals(servletContextListenerCount2)) {
			servletContextListenerCount2++;
		}

		return servletContextListenerCount1.compareTo(
			servletContextListenerCount2);
	}

}