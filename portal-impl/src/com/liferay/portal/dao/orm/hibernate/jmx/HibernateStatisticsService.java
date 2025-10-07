/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate.jmx;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.Statistics;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class HibernateStatisticsService {

	public void afterPropertiesSet() throws NotCompliantMBeanException {
		if (PropsValues.HIBERNATE_GENERATE_STATISTICS) {
			SessionFactoryImplementor sessionFactoryImplementor =
				(SessionFactoryImplementor)
					InfrastructureUtil.getSessionFactory();

			Statistics statistics = sessionFactoryImplementor.getStatistics();

			statistics.setStatisticsEnabled(true);

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			_serviceRegistration = bundleContext.registerService(
				DynamicMBean.class,
				new StandardMBean(statistics, Statistics.class),
				MapUtil.singletonDictionary(
					"jmx.objectname", "Hibernate:name=statistics"));
		}
	}

	public void destroy() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private ServiceRegistration<?> _serviceRegistration;

}