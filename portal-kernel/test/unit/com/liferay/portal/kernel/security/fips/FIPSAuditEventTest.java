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
	public void testPutThrows() throws Exception {
		_testPutThrows(Double.NaN);
		_testPutThrows(Double.POSITIVE_INFINITY);
		_testPutThrows(Float.NEGATIVE_INFINITY);
		_testPutThrows(Float.NaN);
		_testPutThrows(RandomTestUtil.randomBytes());
		_testPutThrows(null);

		String randomString = RandomTestUtil.randomString();

		char[] chars = randomString.toCharArray();

		_testPutThrows(chars);

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		_testPutThrows(keyPair.getPrivate());
		_testPutThrows(keyPair.getPublic());

		_testPutThrows(new PBEKeySpec(chars));
	}

	private void _testPutThrows(Object value) {
		String key = RandomTestUtil.randomString();

		_testPutThrows(key, Arrays.asList(value));
		_testPutThrows(key, Collections.singletonMap("nested", value));
		_testPutThrows(key, new Object[] {value});
		_testPutThrows(key, value);
	}

	private void _testPutThrows(String key, Object value) {
		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditEvent.Severity.INFO);

		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> fipsAuditEvent.put(key, value));

		Map<String, Object> fields = fipsAuditEvent.getFields();

		Assert.assertTrue(fields.isEmpty());
	}

}