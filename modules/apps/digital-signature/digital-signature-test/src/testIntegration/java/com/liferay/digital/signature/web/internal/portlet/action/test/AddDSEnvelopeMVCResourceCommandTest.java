/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class AddDSEnvelopeMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoTransactionalCommand() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		int days = RandomTestUtil.randomInt();

		mockLiferayResourceRequest.setParameter(
			"expireAfter", String.valueOf(days));
		mockLiferayResourceRequest.setParameter(
			"expireWarn", String.valueOf(days));

		mockLiferayResourceRequest.setParameter("recipients", "[]");

		AssertUtils.assertFailure(
			PortalException.class, "Expire warn must be less than expire after",
			() -> ReflectionTestUtil.invoke(
				_mvcResourceCommand, "doTransactionalCommand",
				new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
				mockLiferayResourceRequest, new MockLiferayResourceResponse()));

		mockLiferayResourceRequest.setParameter(
			"expireWarn", String.valueOf(days - 1));

		String dsEnvelopeId = RandomTestUtil.randomString();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						DSEnvelope dsEnvelope = (DSEnvelope)arguments[2];

						Assert.assertEquals(days, dsEnvelope.getExpireAfter());
						Assert.assertEquals(
							days - 1, dsEnvelope.getExpireWarn());

						dsEnvelope.setDSEnvelopeId(dsEnvelopeId);

						return dsEnvelope;
					}));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		try {
			ReflectionTestUtil.invoke(
				_mvcResourceCommand, "doTransactionalCommand",
				new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
				mockLiferayResourceRequest, mockLiferayResourceResponse);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			byteArrayOutputStream.toString());

		Assert.assertEquals(dsEnvelopeId, jsonObject.getString("dsEnvelopeId"));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(filter = "mvc.command.name=/digital_signature/add_ds_envelope")
	private MVCResourceCommand _mvcResourceCommand;

}