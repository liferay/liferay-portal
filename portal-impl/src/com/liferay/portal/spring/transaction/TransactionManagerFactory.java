/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.spring.hibernate.PortalTransactionManager;

import java.util.Enumeration;
import java.util.Properties;

import javax.sql.DataSource;

import jodd.bean.BeanUtil;

import org.hibernate.SessionFactory;

import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * @author Brian Wing Shun Chan
 */
public class TransactionManagerFactory {

	public static AbstractPlatformTransactionManager createTransactionManager(
		DataSource dataSource, SessionFactory sessionFactory) {

		AbstractPlatformTransactionManager abstractPlatformTransactionManager =
			new PortalTransactionManager(dataSource, sessionFactory);

		Properties properties = PropsUtil.getProperties(
			"transaction.manager.property.", true);

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			String value = properties.getProperty(key);

			BeanUtil.pojo.setProperty(
				abstractPlatformTransactionManager, key, value);
		}

		return abstractPlatformTransactionManager;
	}

}