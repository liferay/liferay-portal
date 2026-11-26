/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.FDSVisualizationModeCET;
import com.liferay.client.extension.type.internal.FDSVisualizationModeCETImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Antonio Ortega
 */
public class FDSVisualizationModeCETImplFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidate() throws PortalException {
		_fdsVisualizationModeCETImplFactoryImpl.validate(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(),
				StringBundler.concat(
					"[{\"name\": \"", RandomTestUtil.randomString(),
					"\"}, {\"label\": \"", RandomTestUtil.randomString(),
					"\", \"name\": \"", RandomTestUtil.randomString(), "\"}]"),
				RandomTestUtil.randomString()),
			null);
		_fdsVisualizationModeCETImplFactoryImpl.validate(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString()),
			null);

		_assertValidateFails(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(), "[]",
				RandomTestUtil.randomString()),
			"declare-at-least-one-item-element");
		_assertValidateFails(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(), "[{}]",
				RandomTestUtil.randomString()),
			"every-item-element-must-have-a-name");
		_assertValidateFails(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(), "not json",
				RandomTestUtil.randomString()),
			"the-item-elements-are-not-valid-json");

		String name = RandomTestUtil.randomString();

		_assertValidateFails(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(),
				StringBundler.concat(
					"[{\"name\": \"", name, "\"}, {\"name\": \"", name, "\"}]"),
				RandomTestUtil.randomString()),
			"item-element-name-x-is-duplicated");

		_assertValidateFails(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(), StringPool.BLANK,
				StringPool.BLANK),
			"please-enter-at-least-one-javascript-url");
		_assertValidateFails(
			_createFDSVisualizationModeCET(
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString()),
			"please-select-an-icon");
	}

	private void _assertValidateFails(
			FDSVisualizationModeCET fdsVisualizationModeCET, String messageKey)
		throws PortalException {

		try {
			_fdsVisualizationModeCETImplFactoryImpl.validate(
				fdsVisualizationModeCET, null);

			Assert.fail();
		}
		catch (ClientExtensionEntryTypeSettingsException
					clientExtensionEntryTypeSettingsException) {

			Assert.assertEquals(
				messageKey,
				clientExtensionEntryTypeSettingsException.getMessageKey());
		}
	}

	private FDSVisualizationModeCET _createFDSVisualizationModeCET(
		String icon, String sections, String url) {

		return new FDSVisualizationModeCETImpl(
			StringPool.BLANK, 0, null, StringPool.BLANK, StringPool.BLANK, null,
			StringPool.BLANK, null, false, StringPool.BLANK, 0,
			UnicodePropertiesBuilder.put(
				"icon", icon
			).put(
				"sections", sections
			).put(
				"url", url
			).build());
	}

	private final FDSVisualizationModeCETImplFactoryImpl
		_fdsVisualizationModeCETImplFactoryImpl =
			new FDSVisualizationModeCETImplFactoryImpl(new JSONFactoryImpl());

}