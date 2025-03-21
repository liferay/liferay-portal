/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.threaddump;

import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public class ThreadDumpFilter
	extends BasePortalFilter implements TryFinallyFilter {

	@Override
	public void doFilterFinally(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Object object) {

		ScheduledFuture<?> scheduledFuture = (ScheduledFuture<?>)object;

		scheduledFuture.cancel(true);
	}

	@Override
	public Object doFilterTry(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _scheduledExecutorService.schedule(
			_threadDumper, PropsValues.THREAD_DUMP_SPEED_THRESHOLD,
			TimeUnit.SECONDS);
	}

	private static final int _MAX_THREAD_DUMPERS = 5;

	private static final ScheduledExecutorService _scheduledExecutorService =
		Executors.newScheduledThreadPool(_MAX_THREAD_DUMPERS);
	private static final ThreadDumper _threadDumper = new ThreadDumper();

}