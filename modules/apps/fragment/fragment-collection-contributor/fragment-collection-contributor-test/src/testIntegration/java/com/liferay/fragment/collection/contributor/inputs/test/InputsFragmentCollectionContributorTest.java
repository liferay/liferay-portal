/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.contributor.inputs.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class InputsFragmentCollectionContributorTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInputFragmentsAvailable() {
		for (String fragmentEntryKey : _FRAGMENT_ENTRY_KEYS) {
			Assert.assertNotNull(
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryKey));
		}
	}

	private static final String[] _FRAGMENT_ENTRY_KEYS = {
		"INPUTS-assignee-input", "INPUTS-captcha", "INPUTS-checkbox",
		"INPUTS-date-input", "INPUTS-date-time-input", "INPUTS-file-upload",
		"INPUTS-multiselect-list", "INPUTS-numeric-input",
		"INPUTS-rich-text-input", "INPUTS-select-from-list", "INPUTS-stepper",
		"INPUTS-submit-button", "INPUTS-text-input", "INPUTS-textarea"
	};

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

}