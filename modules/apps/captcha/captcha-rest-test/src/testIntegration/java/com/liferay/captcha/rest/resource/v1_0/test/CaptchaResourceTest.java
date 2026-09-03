/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.rest.client.dto.v1_0.Captcha;
import com.liferay.captcha.rest.client.http.HttpInvoker;
import com.liferay.captcha.rest.client.resource.v1_0.CaptchaResource;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Loc Pham
 */
@RunWith(Arquillian.class)
public class CaptchaResourceTest extends BaseCaptchaResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_captchaResource = CaptchaResource.builder(
		).endpoint(
			company.getVirtualHostname(), PortalUtil.getPortalServerPort(false),
			"http"
		).build();
	}

	@Override
	@Test
	public void testGetCaptchaChallenge() throws Exception {
		Captcha captcha = _captchaResource.getCaptchaChallenge();

		Assert.assertNotNull(captcha.getToken());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			EncryptorUtil.decryptAuthenticated(
				testCompany.getKeyObj(), captcha.getToken()));

		Assert.assertNotNull(jsonObject.get("answer"));

		Assert.assertTrue(
			(GetterUtil.getLong(jsonObject.get("expiryTime")) - 1000L) >
				System.currentTimeMillis());

		String image = captcha.getImage();
		String prefix = "data:image/png;base64,";

		Assert.assertEquals(prefix, image.substring(0, prefix.length()));
		Assert.assertTrue(
			Base64.decode(image.substring(prefix.length())).length > 0);
	}

	@Override
	@Test
	public void testPostCaptchaResponse() throws Exception {

		// Answer is invalid

		_assertStatus(_getToken(), RandomTestUtil.randomString(), 400);

		// Captcha is expired

		// TODO

		// Nonce

		String token = _getToken();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			EncryptorUtil.decryptAuthenticated(testCompany.getKeyObj(), token));

		_assertStatus(token, jsonObject.getString("answer"), 204);

		_assertStatus(token, jsonObject.getString("answer"), 400);
	}

	private void _assertStatus(String token, String answer, int statusCode)
		throws Exception {

		Captcha captcha = new Captcha();

		captcha.setAnswer(answer);
		captcha.setToken(token);

		HttpInvoker.HttpResponse httpResponse =
			_captchaResource.postCaptchaResponseHttpResponse(captcha);

		Assert.assertEquals(statusCode, httpResponse.getStatusCode());
	}

	private String _getToken() throws Exception {
		Captcha captcha = _captchaResource.getCaptchaChallenge();

		return captcha.getToken();
	}

	private CaptchaResource _captchaResource;

	@Inject
	private CompanyLocalService _companyLocalService;

}