/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.function.captcha.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.test.util.CaptchaTestUtil;
import com.liferay.client.extension.type.CustomElementCET;
import com.liferay.client.extension.type.configuration.CETConfiguration;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class FunctionCaptchaImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		String externalReferenceCode = "LXC:" + RandomTestUtil.randomString();

		CustomElementCET customElementCET =
			(CustomElementCET)_cetManager.addCET(
				ConfigurableUtil.createConfigurable(
					CETConfiguration.class,
					HashMapBuilder.<String, Object>put(
						"baseURL", "${portalURL}/o/test_" + _VIRTUAL_HOSTNAME
					).put(
						"name", "Test " + _VIRTUAL_HOSTNAME
					).put(
						"type", "customElement"
					).put(
						"typeSettings", new String[] {"htmlElementName=test"}
					).build()),
				TestPropsValues.getCompanyId(), externalReferenceCode);

		String pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.captcha.internal.configuration." +
				"FunctionCaptchaImplConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"captchaName", RandomTestUtil.randomString()
			).put(
				"captchaResponseParameterName", RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"customElementExternalReferenceCode", externalReferenceCode
			).put(
				"oAuth2ApplicationExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"resourcePath", RandomTestUtil.randomString()
			).build());

		String servicePid = StringUtil.extractLast(pid, StringPool.TILDE);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CaptchaConfiguration.class.getName(),
						new HashMapDictionaryBuilder(
						).<String, Object>put(
							"captchaEngine",
							"com.liferay.captcha.internal.function.captcha." +
								"FunctionCaptchaImpl#" + servicePid
						).build())) {

			Assert.assertTrue(
				CaptchaTestUtil.isCaptchaRendered(
					StringPool.LESS_THAN +
						customElementCET.getHTMLElementName()));
		}

		_cetManager.deleteCET(customElementCET);

		ConfigurationTestUtil.deleteConfiguration(pid);
	}

	private static final String _VIRTUAL_HOSTNAME =
		RandomTestUtil.randomString() + ".localtest.me";

	@Inject
	private static CETManager _cetManager;

}