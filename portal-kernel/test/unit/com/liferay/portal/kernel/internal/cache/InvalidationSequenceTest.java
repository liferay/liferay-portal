/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.cache;

import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class InvalidationSequenceTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testGetInvalidationSequence() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		Assert.assertEquals(
			0, invalidationSequence.getInvalidationSequence(_REGION_NAME_1));

		invalidationSequence.invalidate(_REGION_NAME_1, 0);

		Assert.assertEquals(
			1, invalidationSequence.getInvalidationSequence(_REGION_NAME_1));
		Assert.assertEquals(
			0, invalidationSequence.getInvalidationSequence(_REGION_NAME_2));

		invalidationSequence.invalidate(_REGION_NAME_2, 0);

		Assert.assertEquals(
			1, invalidationSequence.getInvalidationSequence(_REGION_NAME_1));
		Assert.assertEquals(
			2, invalidationSequence.getInvalidationSequence(_REGION_NAME_2));
	}

	@Test
	public void testInvalidate() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		Assert.assertFalse(
			"Region should not have been invalidated",
			invalidationSequence.invalidate(_REGION_NAME_1, 0));
		Assert.assertFalse(
			"Region should not have been invalidated after sequence 1",
			invalidationSequence.invalidate(_REGION_NAME_1, 1));
		Assert.assertTrue(
			"Region should have been invalidated after sequence 1",
			invalidationSequence.invalidate(_REGION_NAME_1, 1));
		Assert.assertEquals(
			3, invalidationSequence.getInvalidationSequence(_REGION_NAME_1));
	}

	@Test
	public void testIsInvalidatedAfter() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		Assert.assertFalse(
			"Region should not have been invalidated",
			invalidationSequence.isInvalidatedAfter(_REGION_NAME_1, 0));

		invalidationSequence.invalidate(_REGION_NAME_1, 0);

		Assert.assertTrue(
			"Region should have been invalidated after sequence 0",
			invalidationSequence.isInvalidatedAfter(_REGION_NAME_1, 0));
		Assert.assertFalse(
			"Region should not have been invalidated after sequence 1",
			invalidationSequence.isInvalidatedAfter(_REGION_NAME_1, 1));
		Assert.assertFalse(
			"Region should not have been invalidated",
			invalidationSequence.isInvalidatedAfter(_REGION_NAME_2, 0));
	}

	private static final String _REGION_NAME_1 = "REGION_NAME_1";

	private static final String _REGION_NAME_2 = "REGION_NAME_2";

}