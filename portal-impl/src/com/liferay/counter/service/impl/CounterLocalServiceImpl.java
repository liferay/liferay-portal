/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.service.impl;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.counter.service.base.CounterLocalServiceBaseImpl;
import com.liferay.portal.kernel.aop.SkipAop;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Edward Han
 */
@SkipAop
public class CounterLocalServiceImpl
	extends CounterLocalServiceBaseImpl implements CounterLocalService {

	@Override
	public long getCurrentId(String name) {
		return counterFinder.getCurrentId(name);
	}

	@Override
	public List<String> getNames() {
		return counterFinder.getNames();
	}

	@Override
	public long increment() {
		return counterFinder.increment();
	}

	@Override
	public long increment(String name) {
		return counterFinder.increment(name);
	}

	@Override
	public long increment(String name, int size) {
		return counterFinder.increment(name, size);
	}

	@Override
	public void reset(String name) {
		counterFinder.reset(name);
	}

	@Override
	public void reset(String name, long size) {
		counterFinder.reset(name, size);
	}

}