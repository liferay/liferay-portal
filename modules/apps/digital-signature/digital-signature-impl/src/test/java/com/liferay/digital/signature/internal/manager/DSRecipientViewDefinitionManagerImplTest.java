/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.model.DSRecipientViewDefinition;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brian I. Kim
 */
public class DSRecipientViewDefinitionManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_digitalSignatureConfigurationUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isCompanyAdmin(Mockito.anyLong())
		).thenReturn(
			true
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	@Test
	public void testAddDSRecipientViewDefinition() throws Exception {
		String url = "https://" + RandomTestUtil.randomString();

		_mockDSHttp(JSONUtil.put("url", url));

		long companyId = RandomTestUtil.randomLong();
		String dsEnvelopeId = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			url,
			_dsRecipientViewDefinitionManagerImpl.addDSRecipientViewDefinition(
				companyId, groupId, dsEnvelopeId,
				new DSRecipientViewDefinition()));

		String message = RandomTestUtil.randomString();

		_mockDSHttp(
			JSONUtil.put(
				"errorCode", RandomTestUtil.randomString()
			).put(
				"message", message
			));

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"Unable to create the recipient view for digital signature ",
				"envelope ", dsEnvelopeId, ": ", message),
			() ->
				_dsRecipientViewDefinitionManagerImpl.
					addDSRecipientViewDefinition(
						companyId, groupId, dsEnvelopeId,
						new DSRecipientViewDefinition()));

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			Mockito.mock(DigitalSignatureConfiguration.class);

		Mockito.when(
			digitalSignatureConfiguration.environment()
		).thenReturn(
			"production"
		);

		_digitalSignatureConfigurationUtilMockedStatic.when(
			() ->
				DigitalSignatureConfigurationUtil.
					getDigitalSignatureConfiguration(companyId, groupId)
		).thenReturn(
			digitalSignatureConfiguration
		);

		long userId = RandomTestUtil.randomLong();

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.getUserId()
		).thenReturn(
			userId
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		DSHttp dsHttp = _mockDSHttp(JSONUtil.put("url", url));

		String emailAddress = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		DSRequestRecipient dsRequestRecipient = new DSRequestRecipient(
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", emailAddress
			).put(
				"name", name
			).put(
				"r_userToDSRequestRecipients_userId", userId
			).build());

		String portalURL = "https://" + RandomTestUtil.randomString();

		Assert.assertEquals(
			url,
			_dsRecipientViewDefinitionManagerImpl.addDSRecipientViewDefinition(
				companyId, groupId, dsEnvelopeId, dsRequestRecipient,
				portalURL));

		ArgumentCaptor<JSONObject> argumentCaptor = ArgumentCaptor.forClass(
			JSONObject.class);

		Mockito.verify(
			dsHttp
		).post(
			Mockito.eq(companyId), Mockito.eq(groupId),
			Mockito.eq("envelopes/" + dsEnvelopeId + "/views/recipient"),
			argumentCaptor.capture()
		);

		JSONObject jsonObject = argumentCaptor.getValue();

		Assert.assertEquals(
			"none", jsonObject.getString("authenticationMethod"));
		Assert.assertEquals(
			String.valueOf(userId), jsonObject.getString("clientUserId"));
		Assert.assertEquals(emailAddress, jsonObject.getString("email"));
		Assert.assertEquals(
			JSONUtil.putAll(
				"https://apps.docusign.com", portalURL
			).toString(),
			String.valueOf(jsonObject.getJSONArray("frameAncestors")));
		Assert.assertEquals(
			JSONUtil.putAll(
				"https://apps.docusign.com"
			).toString(),
			String.valueOf(jsonObject.getJSONArray("messageOrigins")));
		Assert.assertEquals(
			"https://apps.docusign.com", jsonObject.getString("returnUrl"));
		Assert.assertEquals(name, jsonObject.getString("userName"));

		Mockito.when(
			permissionChecker.getUserId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() ->
				_dsRecipientViewDefinitionManagerImpl.
					addDSRecipientViewDefinition(
						companyId, groupId, dsEnvelopeId, dsRequestRecipient,
						portalURL));

		Mockito.when(
			permissionChecker.isCompanyAdmin(companyId)
		).thenReturn(
			true
		);

		dsHttp = _mockDSHttp(JSONUtil.put("url", url));

		Assert.assertEquals(
			url,
			_dsRecipientViewDefinitionManagerImpl.addDSRecipientViewDefinition(
				companyId, groupId, dsEnvelopeId, dsRequestRecipient,
				portalURL));

		Mockito.verify(
			dsHttp
		).post(
			Mockito.eq(companyId), Mockito.eq(groupId),
			Mockito.eq("envelopes/" + dsEnvelopeId + "/views/recipient"),
			argumentCaptor.capture()
		);

		jsonObject = argumentCaptor.getValue();

		Assert.assertEquals(
			String.valueOf(userId), jsonObject.getString("clientUserId"));
	}

	private DSHttp _mockDSHttp(JSONObject jsonObject) {
		DSHttp dsHttp = Mockito.mock(DSHttp.class);

		Mockito.when(
			dsHttp.post(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.any(JSONObject.class))
		).thenReturn(
			jsonObject
		);

		ReflectionTestUtil.setFieldValue(
			_dsRecipientViewDefinitionManagerImpl, "_dsHttp", dsHttp);

		return dsHttp;
	}

	private static final MockedStatic<DigitalSignatureConfigurationUtil>
		_digitalSignatureConfigurationUtilMockedStatic = Mockito.mockStatic(
			DigitalSignatureConfigurationUtil.class);

	private final DSRecipientViewDefinitionManagerImpl
		_dsRecipientViewDefinitionManagerImpl =
			new DSRecipientViewDefinitionManagerImpl();

}