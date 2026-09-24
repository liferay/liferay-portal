/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.cache;

import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

		invalidationSequence.invalidate(
			_REGION_NAME_1, 0, false, Collections.emptyList());

		Assert.assertEquals(2, invalidationSequence.getSequence());

		invalidationSequence.invalidate(
			_REGION_NAME_2, 0, true, Arrays.asList(_KEY_1, _KEY_2));

		Assert.assertEquals(4, invalidationSequence.getSequence());
	}

	@Test
	public void testInvalidate() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		Assert.assertFalse(
			"Region should not have been written",
			invalidationSequence.invalidate(
				_REGION_NAME_1, 0, false, Collections.emptyList()));
		Assert.assertFalse(
			"Region should not have been written after sequence 1",
			invalidationSequence.invalidate(
				_REGION_NAME_1, 1, false, Collections.emptyList()));
		Assert.assertTrue(
			"Region should have been written after sequence 1",
			invalidationSequence.invalidate(
				_REGION_NAME_1, 1, false, Collections.emptyList()));
		Assert.assertEquals(6, invalidationSequence.getSequence());
	}

	@Test
	public void testInvalidateWithRemoveAll() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		List<String> actions = new ArrayList<>();

		invalidationSequence.invalidate(
			"Aa", 0, false, Collections.singletonList(_KEY_1));

		_assertPublishKeyDropped(
			invalidationSequence, "BB", _KEY_1, 0, actions);

		invalidationSequence.invalidate(
			"Aa", 2, true, Collections.singletonList(_KEY_1));

		_assertPublishKeyDropped(
			invalidationSequence, "Aa", _KEY_2, 2, actions);
		_assertPublishKey(invalidationSequence, "BB", _KEY_1, 2, actions);
	}

	@Test
	public void testPublish() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		List<String> actions = new ArrayList<>();

		_publish(invalidationSequence, _REGION_NAME_1, 0, actions);

		Assert.assertEquals(Collections.singletonList("publish"), actions);

		actions.clear();

		invalidationSequence.invalidate(
			_REGION_NAME_1, 0, false, Collections.emptyList());

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

				invalidationSequence.invalidate(
					_REGION_NAME_1, 1, false, Collections.emptyList());
			},
			() -> actions.add("withdraw"));

		Assert.assertEquals(Arrays.asList("publish", "withdraw"), actions);
	}

	@Test
	public void testPublishKey() {
		InvalidationSequence invalidationSequence = new InvalidationSequence();

		List<String> actions = new ArrayList<>();

		_assertPublishKey(
			invalidationSequence, _REGION_NAME_1, _KEY_1, 0, actions);

		invalidationSequence.invalidate(
			_REGION_NAME_1, 0, false, Collections.emptyList());

		_assertPublishKey(
			invalidationSequence, _REGION_NAME_1, _KEY_1, 0, actions);

		invalidationSequence.invalidate(
			_REGION_NAME_1, 2, false, Collections.singletonList(_KEY_1));

		_assertPublishKeyDropped(
			invalidationSequence, _REGION_NAME_1, _KEY_1, 3, actions);
		_assertPublishKey(
			invalidationSequence, _REGION_NAME_1, _KEY_1, 4, actions);
		_assertPublishKey(
			invalidationSequence, _REGION_NAME_1, _KEY_2, 0, actions);
		_assertPublishKey(
			invalidationSequence, _REGION_NAME_2, _KEY_1, 0, actions);

		invalidationSequence.invalidate(
			_REGION_NAME_1, 4, true, Collections.emptyList());

		_assertPublishKeyDropped(
			invalidationSequence, _REGION_NAME_1, _KEY_2, 5, actions);
		_assertPublishKey(
			invalidationSequence, _REGION_NAME_1, _KEY_2, 6, actions);
		_assertPublishKey(
			invalidationSequence, _REGION_NAME_2, _KEY_2, 0, actions);

		_assertPublishKeyWithdrawn(
			invalidationSequence, _REGION_NAME_1, _KEY_1, 6, false,
			Collections.singletonList(_KEY_1), actions);
		_assertPublishKeyWithdrawn(
			invalidationSequence, _REGION_NAME_1, _KEY_2, 8, true,
			Collections.emptyList(), actions);
	}

	private void _assertPublishKey(
		InvalidationSequence invalidationSequence, String regionName,
		String key, long sequence, List<String> actions) {

		boolean published = _publishKey(
			invalidationSequence, regionName, key, sequence, actions);

		Assert.assertTrue(actions.toString(), published);

		Assert.assertEquals(Collections.singletonList("publish"), actions);

		actions.clear();
	}

	private void _assertPublishKeyDropped(
		InvalidationSequence invalidationSequence, String regionName,
		String key, long sequence, List<String> actions) {

		boolean published = _publishKey(
			invalidationSequence, regionName, key, sequence, actions);

		Assert.assertFalse(actions.toString(), published);

		Assert.assertTrue(actions.toString(), actions.isEmpty());
	}

	private void _assertPublishKeyWithdrawn(
		InvalidationSequence invalidationSequence, String regionName,
		String key, long sequence, boolean removeAll,
		Collection<Serializable> keys, List<String> actions) {

		boolean published = invalidationSequence.publishKey(
			regionName, key, sequence,
			() -> {
				actions.add("publish");

				invalidationSequence.invalidate(
					regionName, sequence, removeAll, keys);
			},
			() -> actions.add("withdraw"));

		Assert.assertFalse(actions.toString(), published);

		Assert.assertEquals(Arrays.asList("publish", "withdraw"), actions);

		actions.clear();
	}

	private void _publish(
		InvalidationSequence invalidationSequence, String regionName,
		long sequence, List<String> actions) {

		invalidationSequence.publish(
			regionName, sequence, () -> actions.add("publish"),
			() -> actions.add("withdraw"));
	}

	private boolean _publishKey(
		InvalidationSequence invalidationSequence, String regionName,
		String key, long sequence, List<String> actions) {

		return invalidationSequence.publishKey(
			regionName, key, sequence, () -> actions.add("publish"),
			() -> actions.add("withdraw"));
	}

	private static final String _KEY_1 = "KEY_1";

	private static final String _KEY_2 = "KEY_2";

	private static final String _REGION_NAME_1 = "REGION_NAME_1";

	private static final String _REGION_NAME_2 = "REGION_NAME_2";

}