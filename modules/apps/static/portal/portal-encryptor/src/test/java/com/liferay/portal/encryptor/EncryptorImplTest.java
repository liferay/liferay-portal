/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.encryptor;

import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mika Koivisto
 */
public class EncryptorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testKeySerialization() throws Exception {
		Encryptor encryptor = new EncryptorImpl();

		Key key = encryptor.generateKey();

		String encryptedString = encryptor.encrypt(key, "Hello World!");

		String serializedKey = encryptor.serializeKey(key);

		key = encryptor.deserializeKey(serializedKey);

		Assert.assertEquals(
			"Hello World!", encryptor.decrypt(key, encryptedString));
	}

}