/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.crypto.spec.PBEKeySpec;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventTest {

	@Test
	public void testPut() {
		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditEvent.Severity.INFO);

		String reason = RandomTestUtil.randomString();

		fipsAuditEvent.put("reason", reason);

		Map<String, Object> fields = fipsAuditEvent.getFields();

		Assert.assertEquals(reason, fields.get("reason"));
	}

	@Test
	public void testPutRejectsANonfiniteNumber() {
		_testPutRejects(Double.NaN);
		_testPutRejects(Double.POSITIVE_INFINITY);
		_testPutRejects(Float.NEGATIVE_INFINITY);
		_testPutRejects(Float.NaN);
	}

	@Test
	public void testPutRejectsANullValue() {
		_testPutRejects(null);
	}

	@Test
	public void testPutRejectsASensitiveSecurityParameter() throws Exception {
		_testPutRejects(RandomTestUtil.randomBytes());

		String randomString = RandomTestUtil.randomString();

		char[] chars = randomString.toCharArray();

		_testPutRejects(chars);

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		_testPutRejects(keyPair.getPrivate());
		_testPutRejects(keyPair.getPublic());

		_testPutRejects(new PBEKeySpec(chars));
	}

	private void _assertPutThrows(Object value) {
		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditEvent.Severity.INFO);

		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> fipsAuditEvent.put(RandomTestUtil.randomString(), value));

		Map<String, Object> fields = fipsAuditEvent.getFields();

		Assert.assertTrue(fields.isEmpty());
	}

	private void _testPutRejects(Object value) {
		_assertPutThrows(Arrays.asList(value));
		_assertPutThrows(Collections.singletonMap("nested", value));
		_assertPutThrows(new Object[] {value});
		_assertPutThrows(value);
	}

}