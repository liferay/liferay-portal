/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.FDSVisualizationModeCET;
import com.liferay.client.extension.type.internal.FDSVisualizationModeCETImpl;
import com.liferay.petra.string.StringPool;
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
				RandomTestUtil.randomString(), RandomTestUtil.randomString()),
			null);

		_assertValidateFails(
			_createFDSVisualizationModeCET(
				RandomTestUtil.randomString(), StringPool.BLANK),
			"please-enter-at-least-one-javascript-url");
		_assertValidateFails(
			_createFDSVisualizationModeCET(
				StringPool.BLANK, RandomTestUtil.randomString()),
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
		String icon, String url) {

		return new FDSVisualizationModeCETImpl(
			StringPool.BLANK, 0, null, StringPool.BLANK, StringPool.BLANK, null,
			StringPool.BLANK, null, false, StringPool.BLANK, 0,
			UnicodePropertiesBuilder.put(
				"icon", icon
			).put(
				"url", url
			).build());
	}

	private final FDSVisualizationModeCETImplFactoryImpl
		_fdsVisualizationModeCETImplFactoryImpl =
			new FDSVisualizationModeCETImplFactoryImpl();

}