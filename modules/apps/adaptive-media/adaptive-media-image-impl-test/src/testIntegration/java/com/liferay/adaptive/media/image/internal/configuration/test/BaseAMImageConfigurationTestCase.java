/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.configuration.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Alejandro Hernández
 */
public abstract class BaseAMImageConfigurationTestCase {

	@Before
	public void setUp() throws Exception {
		_initialAMImageConfigurationEntryUuids =
			_getAMImageConfigurationEntryUuids();
	}

	@After
	public void tearDown() throws Exception {
		if (_initialAMImageConfigurationEntryUuids == null) {
			return;
		}

		AMImageConfigurationHelper amImageConfigurationHelper =
			getAMImageConfigurationHelper();

		for (String uuid : _getAMImageConfigurationEntryUuids()) {
			if (!_initialAMImageConfigurationEntryUuids.contains(uuid)) {
				amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
					TestPropsValues.getCompanyId(), uuid);
			}
		}
	}

	protected void assertContains(
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries,
		String uuid) {

		Assert.assertTrue(
			amImageConfigurationEntries.toString(),
			_contains(amImageConfigurationEntries, uuid));
	}

	protected void assertDisabled(
		AMImageConfigurationEntry amImageConfigurationEntry) {

		Assert.assertNotNull(amImageConfigurationEntry);
		Assert.assertFalse(amImageConfigurationEntry.isEnabled());
	}

	protected void assertEnabled(
		AMImageConfigurationEntry amImageConfigurationEntry) {

		Assert.assertNotNull(amImageConfigurationEntry);
		Assert.assertTrue(amImageConfigurationEntry.isEnabled());
	}

	protected void assertNotContains(
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries,
		String uuid) {

		Assert.assertFalse(
			amImageConfigurationEntries.toString(),
			_contains(amImageConfigurationEntries, uuid));
	}

	protected abstract AMImageConfigurationHelper
		getAMImageConfigurationHelper();

	@FunctionalInterface
	protected interface CheckedRunnable {

		public void run() throws Exception;

	}

	private boolean _contains(
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries,
		String uuid) {

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			if (uuid.equals(amImageConfigurationEntry.getUUID())) {
				return true;
			}
		}

		return false;
	}

	private Set<String> _getAMImageConfigurationEntryUuids() throws Exception {
		Set<String> uuids = new HashSet<>();

		AMImageConfigurationHelper amImageConfigurationHelper =
			getAMImageConfigurationHelper();

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			amImageConfigurationHelper.getAMImageConfigurationEntries(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry -> true);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			uuids.add(amImageConfigurationEntry.getUUID());
		}

		return uuids;
	}

	private Set<String> _initialAMImageConfigurationEntryUuids;

	@Inject
	private MessageBus _messageBus;

}