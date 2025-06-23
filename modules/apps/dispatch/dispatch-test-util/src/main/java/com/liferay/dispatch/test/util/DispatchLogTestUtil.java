/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.test.util;

import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.service.persistence.DispatchLogUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Date;

/**
 * @author Igor Beslic
 */
public class DispatchLogTestUtil {

	public static DispatchLog randomDispatchLog(
		User user, DispatchTaskStatus dispatchTaskStatus) {

		Date startDate = RandomTestUtil.nextDate();

		return _randomDispatchLog(
			user.getCompanyId(),
			new Date(
				startDate.getTime() + RandomTestUtil.randomInt(60000, 120000)),
			RandomTestUtil.randomString(2000),
			RandomTestUtil.randomString(3000), startDate,
			dispatchTaskStatus.getStatus(), user.getUserId());
	}

	private static DispatchLog _randomDispatchLog(
		long companyId, Date endDate, String error, String output,
		Date startDate, int status, long userId) {

		DispatchLog dispatchLog = DispatchLogUtil.create(
			RandomTestUtil.nextLong());

		dispatchLog.setCompanyId(companyId);
		dispatchLog.setUserId(userId);
		dispatchLog.setEndDate(endDate);
		dispatchLog.setError(error);
		dispatchLog.setOutput(output);
		dispatchLog.setStartDate(startDate);
		dispatchLog.setStatus(status);

		return dispatchLog;
	}

}