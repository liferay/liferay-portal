/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Brian I. Kim
 */
public class DSRequestTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetStatus() {
		Date pastDate = new Date(System.currentTimeMillis() - Time.DAY);

		DSRequest dsRequest = _createDSRequest(
			pastDate, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_SENT, RandomTestUtil.randomLong());

		Assert.assertEquals(
			DSRequestConstants.STATUS_EXPIRED, dsRequest.getStatus());

		dsRequest = _createDSRequest(
			new Date(System.currentTimeMillis() + Time.DAY),
			DSRequestConstants.STATUS_SENT, DSRequestConstants.STATUS_SENT,
			RandomTestUtil.randomLong());

		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, dsRequest.getStatus());

		dsRequest = _createDSRequest(
			pastDate, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_VOIDED, RandomTestUtil.randomLong());

		Assert.assertEquals(
			DSRequestConstants.STATUS_VOIDED, dsRequest.getStatus());
	}

	@Test
	public void testGetStatusDate() {
		Date expirationDate = new Date(System.currentTimeMillis() - Time.DAY);
		Date statusDate = new Date(System.currentTimeMillis() - (Time.DAY * 2));

		DSRequest dsRequest = new DSRequest(
			RandomTestUtil.randomLong(), new Date(),
			RandomTestUtil.randomLong(), Collections.emptyList(),
			RandomTestUtil.randomString(), Collections.emptyList(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			HashMapBuilder.<String, Serializable>put(
				"requestExpirationDate", expirationDate
			).put(
				"requestStatus", DSRequestConstants.STATUS_SENT
			).put(
				"requestStatusDate", statusDate
			).build());

		Assert.assertEquals(expirationDate, dsRequest.getStatusDate());

		dsRequest = new DSRequest(
			RandomTestUtil.randomLong(), new Date(),
			RandomTestUtil.randomLong(), Collections.emptyList(),
			RandomTestUtil.randomString(), Collections.emptyList(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			HashMapBuilder.<String, Serializable>put(
				"requestExpirationDate", expirationDate
			).put(
				"requestStatus", DSRequestConstants.STATUS_VOIDED
			).put(
				"requestStatusDate", statusDate
			).build());

		Assert.assertEquals(statusDate, dsRequest.getStatusDate());
	}

	@Test
	public void testIsReplaceable() {
		Assert.assertFalse(_isReplaceable(DSRequestConstants.STATUS_COMPLETED));
		Assert.assertFalse(_isReplaceable(DSRequestConstants.STATUS_CREATED));
		Assert.assertTrue(_isReplaceable(DSRequestConstants.STATUS_DECLINED));
		Assert.assertTrue(_isReplaceable(DSRequestConstants.STATUS_EXPIRED));
		Assert.assertFalse(_isReplaceable(DSRequestConstants.STATUS_SENT));
		Assert.assertTrue(_isReplaceable(DSRequestConstants.STATUS_VOIDED));
	}

	@Test
	public void testIsSignatureRequired() {
		long userId = RandomTestUtil.randomLong();

		DSRequest dsRequest = _createDSRequest(
			null, DSRequestConstants.STATUS_CREATED,
			DSRequestConstants.STATUS_SENT, userId);

		Assert.assertFalse(dsRequest.isSignatureRequired(userId));

		dsRequest = _createDSRequest(
			null, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_COMPLETED, userId);

		Assert.assertFalse(dsRequest.isSignatureRequired(userId));

		dsRequest = _createDSRequest(
			new Date(System.currentTimeMillis() - Time.DAY),
			DSRequestConstants.STATUS_SENT, DSRequestConstants.STATUS_SENT,
			userId);

		Assert.assertFalse(dsRequest.isSignatureRequired(userId));

		dsRequest = _createDSRequest(
			null, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_SENT, 0);

		Assert.assertFalse(dsRequest.isSignatureRequired(0));

		dsRequest = _createDSRequest(
			null, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_SENT, userId);

		Assert.assertTrue(dsRequest.isSignatureRequired(userId));
		Assert.assertFalse(
			dsRequest.isSignatureRequired(RandomTestUtil.randomLong()));
	}

	@Test
	public void testIsSyncable() {
		Date pastDate = new Date(System.currentTimeMillis() - Time.DAY);

		DSRequest dsRequest = _createDSRequest(
			pastDate, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_SENT, RandomTestUtil.randomLong());

		Assert.assertTrue(dsRequest.isSyncable());
		Assert.assertTrue(dsRequest.isTerminal());

		dsRequest = _createDSRequest(
			pastDate, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_VOIDED, RandomTestUtil.randomLong());

		Assert.assertFalse(dsRequest.isSyncable());
		Assert.assertTrue(dsRequest.isTerminal());
	}

	private DSRequest _createDSRequest(
		Date expirationDate, String requestRecipientStatus,
		String requestStatus, long userId) {

		return new DSRequest(
			RandomTestUtil.randomLong(), new Date(),
			RandomTestUtil.randomLong(),
			Collections.singletonList(
				new DSRequestRecipient(
					HashMapBuilder.<String, Serializable>put(
						"r_userToDSRequestRecipients_userId", userId
					).put(
						"requestRecipientStatus", requestRecipientStatus
					).build())),
			RandomTestUtil.randomString(), Collections.emptyList(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			HashMapBuilder.<String, Serializable>put(
				"requestExpirationDate", expirationDate
			).put(
				"requestStatus", requestStatus
			).build());
	}

	private boolean _isReplaceable(String requestStatus) {
		DSRequest dsRequest = _createDSRequest(
			null, DSRequestConstants.STATUS_SENT, requestStatus,
			RandomTestUtil.randomLong());

		return dsRequest.isReplaceable();
	}

}