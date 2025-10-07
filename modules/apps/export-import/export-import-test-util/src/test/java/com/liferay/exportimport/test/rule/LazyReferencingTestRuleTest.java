/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.rule;

import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carlos Correa
 */
public class LazyReferencingTestRuleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			LazyReferencingTestRule.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@LazyReferencing(enabled = false)
	@Test
	public void testLazyReferencingDisabled() throws Exception {
		Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());
		Assert.assertFalse(_run(LazyReferencingThreadLocal::isEnabled));
	}

	@LazyReferencing
	@Test
	public void testLazyReferencingEnabled() throws Exception {
		Assert.assertTrue(LazyReferencingThreadLocal.isEnabled());
		Assert.assertTrue(_run(LazyReferencingThreadLocal::isEnabled));
	}

	@Test
	public void testLazyReferencingMissing() throws Exception {
		Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());
		Assert.assertFalse(_run(LazyReferencingThreadLocal::isEnabled));
	}

	private Boolean _run(Supplier<Boolean> supplier) throws Exception {
		AtomicReference<Boolean> atomicReference = new AtomicReference<>();

		Thread thread = new Thread(() -> atomicReference.set(supplier.get()));

		thread.start();

		thread.join();

		return atomicReference.get();
	}

}