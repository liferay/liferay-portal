/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 */
public class AccountDisplayTest {

	@Test
	public void testAccountDisplay() {
		Account account = new Account();

		List<Account.LifecycleStage> lifecycleStages = Arrays.asList(
			_getLifecycleStage(), _getLifecycleStage());

		account.setLifecycleStages(lifecycleStages);

		AccountDisplay accountDisplay = new AccountDisplay(account);

		Account.LifecycleStage lifecycleStage = lifecycleStages.get(0);

		Assert.assertEquals(
			lifecycleStage.getName(),
			ReflectionTestUtil.getFieldValue(
				accountDisplay, "_lifecycleStage"));

		Assert.assertEquals(
			lifecycleStages,
			ReflectionTestUtil.getFieldValue(
				accountDisplay, "_lifecycleStages"));
	}

	private Account.LifecycleStage _getLifecycleStage() {
		Account.LifecycleStage lifecycleStage = new Account.LifecycleStage();

		lifecycleStage.setLifecycleExternalReferenceCode(
			RandomTestUtil.randomString());
		lifecycleStage.setLifecycleId(RandomTestUtil.randomString());
		lifecycleStage.setLifecycleName(RandomTestUtil.randomString());
		lifecycleStage.setName(RandomTestUtil.randomString());
		lifecycleStage.setStageExternalReferenceCode(
			RandomTestUtil.randomString());

		return lifecycleStage;
	}

}