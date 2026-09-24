/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.cache;

import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	public void testGetSequence() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		Assert.assertEquals(0, invalidationSequence.getSequence());

		invalidationSequence.invalidate(_REGION_NAME_1, 0);

		Assert.assertEquals(1, invalidationSequence.getSequence());

		invalidationSequence.invalidate(_REGION_NAME_2, 0);

		Assert.assertEquals(2, invalidationSequence.getSequence());
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
		Assert.assertEquals(3, invalidationSequence.getSequence());
	}

	@Test
	public void testPublish() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		List<String> actions = new ArrayList<>();

		_publish(invalidationSequence, _REGION_NAME_1, 0, actions);

		Assert.assertEquals(Collections.singletonList("publish"), actions);

		actions.clear();

		invalidationSequence.invalidate(_REGION_NAME_1, 0);

		_publish(invalidationSequence, _REGION_NAME_1, 0, actions);

		Assert.assertTrue(actions.toString(), actions.isEmpty());

		_publish(invalidationSequence, _REGION_NAME_1, 1, actions);

		Assert.assertEquals(Collections.singletonList("publish"), actions);

		actions.clear();

		_publish(invalidationSequence, _REGION_NAME_2, 0, actions);

		Assert.assertEquals(Collections.singletonList("publish"), actions);

		actions.clear();

		invalidationSequence.publish(
			_REGION_NAME_1, 1,
			() -> {
				actions.add("publish");

				invalidationSequence.invalidate(_REGION_NAME_1, 1);
			},
			() -> actions.add("withdraw"));

		Assert.assertEquals(Arrays.asList("publish", "withdraw"), actions);
	}

	private void _publish(
		InvalidationSequence invalidationSequence, String regionName,
		long sequence, List<String> actions) {

		invalidationSequence.publish(
			regionName, sequence, () -> actions.add("publish"),
			() -> actions.add("withdraw"));
	}

	private static final String _REGION_NAME_1 = "REGION_NAME_1";

	private static final String _REGION_NAME_2 = "REGION_NAME_2";

}