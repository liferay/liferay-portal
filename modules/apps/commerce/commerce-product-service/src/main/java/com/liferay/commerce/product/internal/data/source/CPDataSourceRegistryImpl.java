/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.data.source;

import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceRegistry;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Marco Leo
 */
@Component(service = CPDataSourceRegistry.class)
public class CPDataSourceRegistryImpl implements CPDataSourceRegistry {

	@Override
	public CPDataSource getCPDataSource(String key) {
		if (Validator.isNull(key)) {
			return null;
		}

		for (CPDataSource cpDataSource : _serviceTrackerList) {
			if (key.equals(cpDataSource.getName())) {
				return cpDataSource;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"No commerce product data source registered with key " + key);
		}

		return null;
	}

	@Override
	public List<CPDataSource> getCPDataSources() {
		List<CPDataSource> cpDataSources = new ArrayList<>();

		for (CPDataSource cpDataSource : _serviceTrackerList) {
			if (Validator.isNull(cpDataSource.getName())) {
				continue;
			}

			cpDataSources.add(cpDataSource);
		}

		return Collections.unmodifiableList(cpDataSources);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, CPDataSource.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDataSourceRegistryImpl.class);

	private ServiceTrackerList<CPDataSource> _serviceTrackerList;

}