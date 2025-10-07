/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.service.persistence.impl;

import com.liferay.counter.kernel.service.persistence.CounterFinder;
import com.liferay.portal.dao.orm.hibernate.SessionFactoryImpl;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;

import javax.sql.DataSource;

/**
 * @author Shuyang Zhou
 */
public class CounterFinderFactory {

	public static CounterFinder createCounterFinder(
		DataSource dataSource, SessionFactory sessionFactory) {

		if (sessionFactory instanceof SessionFactoryImpl) {
			SessionFactoryImpl sessionFactoryImpl =
				(SessionFactoryImpl)sessionFactory;

			sessionFactoryImpl.setSessionFactoryClassLoader(
				PortalClassLoaderUtil.getClassLoader());
		}

		CounterFinderImpl counterFinderImpl = null;

		if (PropsValues.COUNTER_DATA_CENTER_COUNT > 1) {
			counterFinderImpl = new MultiDataCenterCounterFinderImpl(
				PropsValues.COUNTER_DATA_CENTER_COUNT,
				PropsValues.COUNTER_DATA_CENTER_DEPLOYMENT_ID);
		}
		else {
			counterFinderImpl = new CounterFinderImpl();
		}

		counterFinderImpl.setDataSource(dataSource);
		counterFinderImpl.setSessionFactory(sessionFactory);

		return counterFinderImpl;
	}

}