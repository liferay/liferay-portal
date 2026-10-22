/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.exception.DSEnvelopeException;
import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.manager.DSCustomFieldManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Brian I. Kim
 */
public class DSEnvelopeManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testAddDSEnvelope() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_dsEnvelopeManagerImpl, "_dsCustomFieldManager",
			Mockito.mock(DSCustomFieldManager.class));

		DSHttp dsHttp = _mockDSHttp(JSONFactoryUtil.createJSONObject());

		Mockito.when(
			dsHttp.get(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			JSONUtil.put(
				"errorCode", "ENVELOPE_DOES_NOT_EXIST"
			).put(
				"message", RandomTestUtil.randomString()
			)
		);

		String dsEnvelopeId = RandomTestUtil.randomString();

		Mockito.when(
			dsHttp.post(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.eq("envelopes"),
				Mockito.any(JSONObject.class))
		).thenReturn(
			JSONUtil.put("envelopeId", dsEnvelopeId)
		);

		long companyId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		SystemException systemException = Assert.assertThrows(
			SystemException.class,
			() -> _dsEnvelopeManagerImpl.addDSEnvelope(
				companyId, groupId, new DSEnvelope()));

		Assert.assertEquals(
			"Unable to read digital signature envelope " + dsEnvelopeId,
			systemException.getMessage());

		Mockito.verify(
			dsHttp
		).put(
			Mockito.eq(companyId), Mockito.eq(groupId),
			Mockito.eq("envelopes/" + dsEnvelopeId),
			Mockito.any(JSONObject.class)
		);

		Mockito.when(
			dsHttp.put(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.any(JSONObject.class))
		).thenReturn(
			JSONUtil.put(
				"errorCode", "ENVELOPE_CANNOT_VOID_INVALID_STATE"
			).put(
				"message", RandomTestUtil.randomString()
			)
		);

		systemException = Assert.assertThrows(
			SystemException.class,
			() -> _dsEnvelopeManagerImpl.addDSEnvelope(
				companyId, groupId, new DSEnvelope()));

		Throwable[] suppressedThrowables = systemException.getSuppressed();

		Assert.assertEquals(
			DSEnvelopeException.MustBeVoidable.class,
			suppressedThrowables[0].getClass());
		Assert.assertEquals(
			Arrays.toString(suppressedThrowables), 1,
			suppressedThrowables.length);
	}

	@Test
	public void testVoidDSEnvelope() throws Exception {
		DSHttp dsHttp = _mockDSHttp(JSONFactoryUtil.createJSONObject());

		long companyId = RandomTestUtil.randomLong();
		String dsEnvelopeId = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();
		String voidedReason = RandomTestUtil.randomString();

		_dsEnvelopeManagerImpl.voidDSEnvelope(
			companyId, dsEnvelopeId, groupId, voidedReason);

		ArgumentCaptor<JSONObject> argumentCaptor = ArgumentCaptor.forClass(
			JSONObject.class);

		Mockito.verify(
			dsHttp
		).put(
			Mockito.eq(companyId), Mockito.eq(groupId),
			Mockito.eq("envelopes/" + dsEnvelopeId), argumentCaptor.capture()
		);

		JSONObject jsonObject = argumentCaptor.getValue();

		Assert.assertEquals("voided", jsonObject.getString("status"));
		Assert.assertEquals(voidedReason, jsonObject.getString("voidedReason"));

		_mockDSHttp(
			JSONUtil.put(
				"errorCode", "ENVELOPE_CANNOT_VOID_INVALID_STATE"
			).put(
				"message", "Only envelopes in the sent state may be voided."
			));

		DSEnvelopeException.MustBeVoidable mustBeVoidable = Assert.assertThrows(
			DSEnvelopeException.MustBeVoidable.class,
			() -> _dsEnvelopeManagerImpl.voidDSEnvelope(
				companyId, dsEnvelopeId, groupId, voidedReason));

		Assert.assertEquals(
			StringBundler.concat(
				"Unable to void digital signature envelope ", dsEnvelopeId,
				": Only envelopes in the sent state may be voided."),
			mustBeVoidable.getMessage());

		_mockDSHttp(
			JSONUtil.put(
				"errorCode", "USER_AUTHENTICATION_FAILED"
			).put(
				"message", "One or both of Username and Password are invalid."
			));

		PortalException portalException = Assert.assertThrows(
			PortalException.class,
			() -> _dsEnvelopeManagerImpl.voidDSEnvelope(
				companyId, dsEnvelopeId, groupId, voidedReason));

		Assert.assertFalse(
			portalException instanceof DSEnvelopeException.MustBeVoidable);
		Assert.assertEquals(
			StringBundler.concat(
				"Unable to void digital signature envelope ", dsEnvelopeId,
				": One or both of Username and Password are invalid."),
			portalException.getMessage());
	}

	private DSHttp _mockDSHttp(JSONObject jsonObject) {
		DSHttp dsHttp = Mockito.mock(DSHttp.class);

		Mockito.when(
			dsHttp.put(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.any(JSONObject.class))
		).thenReturn(
			jsonObject
		);

		ReflectionTestUtil.setFieldValue(
			_dsEnvelopeManagerImpl, "_dsHttp", dsHttp);

		return dsHttp;
	}

	private final DSEnvelopeManagerImpl _dsEnvelopeManagerImpl =
		new DSEnvelopeManagerImpl();

}