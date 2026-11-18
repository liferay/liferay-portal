/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.configuration.persistence.listener;

import com.liferay.multi.factor.authentication.timebased.otp.web.internal.util.MFATimeBasedOTPUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class MFATimeBasedOTPConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(
				Mockito.nullable(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_mfaTimeBasedOTPConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"algorithmKeySize",
				MFATimeBasedOTPUtil.
					MFA_TIMEBASED_OTP_FIPS_ALGORITHM_KEY_SIZE_MIN - 1
			).build());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_mfaTimeBasedOTPConfigurationModelListener.onBeforeSave(
				RandomTestUtil.randomString(),
				HashMapDictionaryBuilder.<String, Object>put(
					"algorithmKeySize",
					MFATimeBasedOTPUtil.
						MFA_TIMEBASED_OTP_FIPS_ALGORITHM_KEY_SIZE_MIN
				).build());

			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> _mfaTimeBasedOTPConfigurationModelListener.onBeforeSave(
					RandomTestUtil.randomString(),
					HashMapDictionaryBuilder.<String, Object>put(
						"algorithmKeySize",
						MFATimeBasedOTPUtil.
							MFA_TIMEBASED_OTP_FIPS_ALGORITHM_KEY_SIZE_MIN - 1
					).build()));
		}
	}

	private final MFATimeBasedOTPConfigurationModelListener
		_mfaTimeBasedOTPConfigurationModelListener =
			new MFATimeBasedOTPConfigurationModelListener();

}