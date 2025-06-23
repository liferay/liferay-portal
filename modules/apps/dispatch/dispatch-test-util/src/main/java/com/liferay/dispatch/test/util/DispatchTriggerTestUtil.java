/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.test.util;

import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.persistence.DispatchTriggerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Objects;

/**
 * @author Igor Beslic
 */
public class DispatchTriggerTestUtil {

	public static DispatchTrigger randomDispatchTrigger(
		DispatchTrigger dispatchTrigger, int nameSalt) {

		Objects.requireNonNull(dispatchTrigger);

		return _randomDispatchTrigger(
			RandomTestUtil.randomBoolean(), dispatchTrigger.getCompanyId(),
			CronExpressionTestUtil.getCronExpression(),
			_randomTaskClusterMode(),
			dispatchTrigger.getDispatchTaskExecutorType(),
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties(),
			_randomName(nameSalt), dispatchTrigger.isSystem(),
			dispatchTrigger.getUserId());
	}

	public static DispatchTrigger randomDispatchTrigger(
		User user, String dispatchTaskExecutorType, int nameSalt) {

		Objects.requireNonNull(user);

		return _randomDispatchTrigger(
			RandomTestUtil.randomBoolean(), user.getCompanyId(),
			CronExpressionTestUtil.getCronExpression(), 0,
			dispatchTaskExecutorType,
			RandomTestUtil.randomUnicodeProperties(
				RandomTestUtil.randomInt(10, 30), 32, 64),
			_randomName(nameSalt), RandomTestUtil.randomBoolean(),
			user.getUserId());
	}

	private static DispatchTrigger _randomDispatchTrigger(
		boolean active, long companyId, String cronExpression,
		int dispatchTaskClusterMode, String dispatchTaskExecutorType,
		UnicodeProperties unicodeProperties, String name, boolean system,
		long userId) {

		DispatchTrigger dispatchTrigger = DispatchTriggerUtil.create(
			RandomTestUtil.nextLong());

		dispatchTrigger.setCompanyId(companyId);
		dispatchTrigger.setUserId(userId);
		dispatchTrigger.setActive(active);
		dispatchTrigger.setCronExpression(cronExpression);
		dispatchTrigger.setDispatchTaskClusterMode(dispatchTaskClusterMode);
		dispatchTrigger.setDispatchTaskExecutorType(dispatchTaskExecutorType);
		dispatchTrigger.setDispatchTaskSettingsUnicodeProperties(
			unicodeProperties);
		dispatchTrigger.setName(name);
		dispatchTrigger.setSystem(system);

		return dispatchTrigger;
	}

	private static String _randomName(int nameSalt) {
		if (nameSalt < 0) {
			return null;
		}

		return String.format("TEST-TRIGGER-%06d", nameSalt);
	}

	private static int _randomTaskClusterMode() {
		DispatchTaskClusterMode[] dispatchTaskClusterModes =
			DispatchTaskClusterMode.values();

		DispatchTaskClusterMode dispatchTaskClusterMode =
			dispatchTaskClusterModes
				[RandomTestUtil.randomInt(
					0, dispatchTaskClusterModes.length - 1)];

		return dispatchTaskClusterMode.getMode();
	}

}