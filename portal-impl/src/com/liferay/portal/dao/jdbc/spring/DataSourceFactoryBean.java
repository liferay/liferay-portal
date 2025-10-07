/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.spring;

import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author Brian Wing Shun Chan
 */
public class DataSourceFactoryBean extends AbstractFactoryBean<DataSource> {

	@Override
	public DataSource createInstance() throws Exception {
		if (_dataSource != null) {
			return _dataSource;
		}

		Properties properties = _properties;

		if (properties == null) {
			properties = PropsUtil.getProperties(_propertyPrefix, true);
		}
		else {
			properties = PropertiesUtil.getProperties(
				properties, _propertyPrefix, true);
		}

		return DataSourceFactoryUtil.initDataSource(properties);
	}

	@Override
	public void destroyInstance(DataSource dataSource) throws Exception {
		DataSourceFactoryUtil.destroyDataSource(dataSource);
	}

	@Override
	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

	public void setDataSource(DataSource dataSource) {
		_dataSource = dataSource;
	}

	public void setProperties(Properties properties) {
		_properties = properties;
	}

	public void setPropertyPrefix(String propertyPrefix) {
		_propertyPrefix = propertyPrefix;
	}

	public void setPropertyPrefixLookup(String propertyPrefixLookup) {
		_propertyPrefix = PropsUtil.get(propertyPrefixLookup);
	}

	private DataSource _dataSource;
	private Properties _properties;
	private String _propertyPrefix;

}