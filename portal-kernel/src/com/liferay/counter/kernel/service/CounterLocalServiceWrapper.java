/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CounterLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CounterLocalService
 * @generated
 */
public class CounterLocalServiceWrapper
	implements CounterLocalService, ServiceWrapper<CounterLocalService> {

	public CounterLocalServiceWrapper() {
		this(null);
	}

	public CounterLocalServiceWrapper(CounterLocalService counterLocalService) {
		_counterLocalService = counterLocalService;
	}

	@Override
	public long getCurrentId(String name) {
		return _counterLocalService.getCurrentId(name);
	}

	@Override
	public java.util.List<String> getNames() {
		return _counterLocalService.getNames();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _counterLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public long increment() {
		return _counterLocalService.increment();
	}

	@Override
	public long increment(String name) {
		return _counterLocalService.increment(name);
	}

	@Override
	public long increment(String name, int size) {
		return _counterLocalService.increment(name, size);
	}

	@Override
	public void reset(String name) {
		_counterLocalService.reset(name);
	}

	@Override
	public void reset(String name, long size) {
		_counterLocalService.reset(name, size);
	}

	@Override
	public CounterLocalService getWrappedService() {
		return _counterLocalService;
	}

	@Override
	public void setWrappedService(CounterLocalService counterLocalService) {
		_counterLocalService = counterLocalService;
	}

	private CounterLocalService _counterLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-838549041