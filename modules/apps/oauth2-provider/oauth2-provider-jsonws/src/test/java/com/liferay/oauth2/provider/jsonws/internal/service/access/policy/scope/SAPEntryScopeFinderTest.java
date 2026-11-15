/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope;

import com.liferay.portal.security.service.access.policy.model.SAPEntryWrapper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class SAPEntryScopeFinderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFindScopes() {
		AtomicInteger atomicInteger = new AtomicInteger();

		SAPEntryScopeFinder sapEntryScopeFinder = new SAPEntryScopeFinder(
			() -> {
				atomicInteger.incrementAndGet();

				return Collections.singletonList(
					new SAPEntryScope(
						new SAPEntryWrapper(null) {

							@Override
							public boolean isEnabled() {
								return true;
							}

						},
						"everything.read"));
			});

		Assert.assertEquals(
			Collections.singleton("everything.read"),
			sapEntryScopeFinder.findScopes());
		Assert.assertEquals(
			Collections.singleton("everything.read"),
			sapEntryScopeFinder.findScopes());

		Assert.assertEquals(1, atomicInteger.get());
	}

}