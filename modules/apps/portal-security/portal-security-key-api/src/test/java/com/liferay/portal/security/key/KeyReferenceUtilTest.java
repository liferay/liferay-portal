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
 * @author Pedro Victor Silvestre
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
	public void testIsParsableKeyReferenceWithInvalidKeyReference() {
		Assert.assertFalse(KeyReferenceUtil.isParsableKeyReference(null));
		Assert.assertFalse(KeyReferenceUtil.isParsableKeyReference(""));
		Assert.assertFalse(KeyReferenceUtil.isParsableKeyReference("abc"));
		Assert.assertFalse(KeyReferenceUtil.isParsableKeyReference("${}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference("${secretRef}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference("${secretRef:}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference("${secretRef:"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${secretRef::identifier}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference("${secretRef:provider}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference("${secretRef:provider:}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${secretRef:provider:   }"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${secretRef:provider:identifier"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${secretRef:provider:identifier}trailing"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${secretRef:provider:null}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${secretRef:pro}vider:identifier}"));
		Assert.assertFalse(
			KeyReferenceUtil.isParsableKeyReference(
				"${SecretRef:provider:identifier}"));
	}

	@Test
	public void testToKeyReference() {
		_assertKeyReference(
			"identifier", "${keyRef:provider:identifier}", "provider",
			KeyReference.Type.CRYPTO);
		_assertKeyReference(
			"identifier", "${secretRef:*:identifier}", "*",
			KeyReference.Type.SECRET);
		_assertKeyReference(
			"arn:aws:kms:us-east-1:123:key/abc",
			"${secretRef:aws-kms:arn:aws:kms:us-east-1:123:key/abc}", "aws-kms",
			KeyReference.Type.SECRET);
		_assertKeyReference(
			"identifier", "${secretRef:provider:identifier}", "provider",
			KeyReference.Type.SECRET);
		_assertKeyReference(
			"identi}fier", "${secretRef:provider:identi}fier}", "provider",
			KeyReference.Type.SECRET);

		for (KeyReference.Type type : KeyReference.Type.values()) {
			KeyReference keyReference = new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				type);

			Assert.assertEquals(
				keyReference,
				KeyReferenceUtil.toKeyReference(
					KeyReferenceUtil.toKeyReferenceString(keyReference)));
		}
	}

	@Test
	public void testToKeyReferenceWithInvalidKeyReference() {
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(null));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(""));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("abc"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef:}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef:"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef::identifier}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef:provider}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef:provider:}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference("${secretRef:provider:   }"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(
				"${secretRef:provider:identifier"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(
				"${secretRef:provider:identifier}trailing"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(
				"${secretRef:provider:null}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(
				"${secretRef:pro}vider:identifier}"));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> KeyReferenceUtil.toKeyReference(
				"${SecretRef:provider:identifier}"));
	}

	private void _assertKeyReference(
		String identifier, String keyReferenceString, String providerId,
		KeyReference.Type type) {

		Assert.assertTrue(
			keyReferenceString,
			KeyReferenceUtil.isParsableKeyReference(keyReferenceString));

		KeyReference keyReference = KeyReferenceUtil.toKeyReference(
			keyReferenceString);

		Assert.assertEquals(identifier, keyReference.getIdentifier());
		Assert.assertEquals(
			keyReferenceString,
			KeyReferenceUtil.toKeyReferenceString(keyReference));
		Assert.assertEquals(providerId, keyReference.getProviderId());
		Assert.assertEquals(type, keyReference.getType());
	}

}