/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class KeyReferenceUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsKeyReference() {
		Assert.assertFalse(KeyReferenceUtil.isKeyReference(null));
		Assert.assertFalse(
			KeyReferenceUtil.isKeyReference(RandomTestUtil.randomString()));
		Assert.assertTrue(
			KeyReferenceUtil.isKeyReference("${keyRef:provider:identifier}"));
		Assert.assertTrue(
			KeyReferenceUtil.isKeyReference(
				"${secretRef:provider:identifier}"));
	}

	@Test
	public void testToKeyReference() {
		_testToKeyReference("${keyRef:", KeyReference.Type.CRYPTO);
		_testToKeyReference("${secretRef:", KeyReference.Type.SECRET);
	}

	private void _testToKeyReference(String prefix, KeyReference.Type type) {
		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), type);

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			keyReference);

		Assert.assertEquals(
			keyReference, KeyReferenceUtil.toKeyReference(keyReferenceString));
		Assert.assertTrue(
			keyReferenceString, keyReferenceString.startsWith(prefix));
	}

}