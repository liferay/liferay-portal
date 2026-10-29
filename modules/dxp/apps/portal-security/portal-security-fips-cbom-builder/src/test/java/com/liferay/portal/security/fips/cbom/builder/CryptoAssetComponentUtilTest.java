/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.cyclonedx.Version;
import org.cyclonedx.generators.BomGeneratorFactory;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.component.crypto.enums.CryptoFunction;
import org.cyclonedx.model.component.crypto.enums.RelatedCryptoMaterialType;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Manuele Castro
 */
public class CryptoAssetComponentUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testToComponents() throws Exception {
		CryptoCall cipherCryptoCall = _createCryptoCall(
			"AES/GCM/NoPadding", "com.liferay.test.Class1",
			Arrays.asList(CryptoFunction.ENCRYPT, CryptoFunction.DECRYPT),
			"Cipher");

		cipherCryptoCall.setKeySize(256);

		CryptoCall keyGeneratorCryptoCall = _createCryptoCall(
			null, "com.liferay.test.Class2",
			Collections.singletonList(CryptoFunction.KEYGEN), "KeyGenerator");

		keyGeneratorCryptoCall.setConfiguredBy(
			Arrays.asList("test.encryption.algorithm", "test.key.size"));

		String featureName = _FEATURE_NAME + RandomTestUtil.randomString();

		keyGeneratorCryptoCall.setFeatureName(featureName);

		CryptoCall cipherConfigurableCryptoCall = _createCryptoCall(
			null, "com.liferay.test.Class2",
			Collections.singletonList(CryptoFunction.ENCRYPT), "Cipher");

		cipherConfigurableCryptoCall.setConfiguredBy(
			Arrays.asList("test.encryption.algorithm", "test.key.size"));

		CryptoCall keyFactoryCryptoCall = _createCryptoCall(
			"DSA", "com.liferay.test.Class3",
			Collections.singletonList(CryptoFunction.OTHER), "KeyFactory");

		keyFactoryCryptoCall.setRelatedCryptoMaterialTypes(
			Collections.singletonList(RelatedCryptoMaterialType.PUBLIC_KEY));

		CryptoCall unresolvedKeyStoreCryptoCall = _createCryptoCall(
			null, "com.liferay.test.Class10",
			Collections.singletonList(CryptoFunction.OTHER), "KeyStore");

		unresolvedKeyStoreCryptoCall.setUnresolvedReasons(
			Collections.singletonList(
				"the return value of " +
					"com.liferay.test.Class10.getKeyStoreType()"));

		CryptoCall unresolvedMessageDigestCryptoCall = _createCryptoCall(
			null, "com.liferay.test.Class9",
			Collections.singletonList(CryptoFunction.DIGEST), "MessageDigest");

		unresolvedMessageDigestCryptoCall.setUnresolvedReasons(
			Arrays.asList(
				"parameter 1 of com.liferay.test.Class9.digest",
				"the field com.liferay.test.Class9._algorithm"));

		Properties portalProperties = new Properties();

		String testEncryptionAlgorithm = RandomTestUtil.randomString();

		portalProperties.setProperty(
			"test.encryption.algorithm", testEncryptionAlgorithm);

		List<Component> components = CryptoAssetComponentUtil.toComponents(
			Arrays.asList(
				_createCryptoCall(
					"SHA-256", "com.liferay.test.Class4",
					Collections.singletonList(CryptoFunction.DIGEST),
					"MessageDigest"),
				keyGeneratorCryptoCall, cipherCryptoCall,
				_createCryptoCall(
					"SHA-256", "com.liferay.test.Class5",
					Collections.singletonList(CryptoFunction.DIGEST),
					"MessageDigest"),
				cipherConfigurableCryptoCall, keyFactoryCryptoCall,
				_createCryptoCall(
					"SHA1withDSA", "com.liferay.test.Class3",
					Collections.singletonList(CryptoFunction.VERIFY),
					"Signature"),
				_createCryptoCall(
					"HmacSHA1", "com.liferay.test.Class6",
					Collections.singletonList(CryptoFunction.TAG), "Mac"),
				_createCryptoCall(
					"RAW", "com.liferay.test.Class6",
					Collections.singletonList(CryptoFunction.OTHER),
					"SecretKeySpec"),
				_createCryptoCall(
					"PKCS12", "com.liferay.test.Class7",
					Collections.singletonList(CryptoFunction.OTHER),
					"KeyStore"),
				_createCryptoCall(
					"TLSv1.2", "com.liferay.test.Class8",
					Collections.singletonList(CryptoFunction.OTHER),
					"SSLContext"),
				unresolvedKeyStoreCryptoCall,
				unresolvedMessageDigestCryptoCall),
			portalProperties);

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				_toJSONObject(
					"crypto:Cipher:AES/GCM/NoPadding:256",
					JSONUtil.put(
						"algorithmProperties",
						JSONUtil.put(
							"cryptoFunctions",
							JSONUtil.putAll("encrypt", "decrypt")
						).put(
							"mode", "gcm"
						).put(
							"parameterSetIdentifier", "256"
						).put(
							"primitive", "ae"
						)
					).put(
						"assetType", "algorithm"
					),
					"AES-256-GCM",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "Cipher"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SunJCE")),
					"com.liferay.test.Class1"),
				_toJSONObject(
					"crypto:Cipher:test.encryption.algorithm",
					JSONUtil.put(
						"algorithmProperties",
						JSONUtil.put(
							"cryptoFunctions",
							JSONUtil.putAll("keygen", "encrypt")
						).put(
							"primitive", "unknown"
						)
					).put(
						"assetType", "algorithm"
					),
					"test.encryption.algorithm",
					JSONUtil.putAll(
						_toPropertyJSONObject(
							"configured-by", "test.encryption.algorithm"),
						_toPropertyJSONObject("configured-by", "test.key.size"),
						_toPropertyJSONObject(
							"default-value",
							"test.encryption.algorithm=" +
								testEncryptionAlgorithm),
						_toPropertyJSONObject(
							"default-value", "test.key.size="),
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("feature", featureName),
						_toPropertyJSONObject("jca-class", "Cipher"),
						_toPropertyJSONObject("jca-class", "KeyGenerator"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME)),
					"com.liferay.test.Class2"),
				_toJSONObject(
					"crypto:KeyFactory:DSA",
					JSONUtil.put(
						"assetType", "related-crypto-material"
					).put(
						"relatedCryptoMaterialProperties",
						JSONUtil.put(
							"algorithmRef", "crypto:Signature:SHA1withDSA"
						).put(
							"type", "public-key"
						)
					),
					"DSA",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "KeyFactory"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SUN")),
					"com.liferay.test.Class3"),
				_toJSONObject(
					"crypto:KeyStore:PKCS12",
					JSONUtil.put(
						"assetType", "related-crypto-material"
					).put(
						"relatedCryptoMaterialProperties",
						JSONUtil.put(
							"format", "PKCS12"
						).put(
							"type", "other"
						)
					),
					"PKCS12",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "KeyStore"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SUN")),
					"com.liferay.test.Class7"),
				_toJSONObject(
					"crypto:KeyStore:com.liferay.test.Class10",
					JSONUtil.put(
						"assetType", "related-crypto-material"
					).put(
						"relatedCryptoMaterialProperties",
						JSONUtil.put("type", "other")
					),
					"KeyStore",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "KeyStore"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject(
							"unresolved-reason",
							"the return value of " +
								"com.liferay.test.Class10.getKeyStoreType()")),
					"com.liferay.test.Class10"),
				_toJSONObject(
					"crypto:Mac:HmacSHA1",
					JSONUtil.put(
						"algorithmProperties",
						JSONUtil.put(
							"cryptoFunctions", JSONUtil.putAll("tag")
						).put(
							"primitive", "mac"
						)
					).put(
						"assetType", "algorithm"
					),
					"HMAC-SHA-1",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "Mac"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SunJCE")),
					"com.liferay.test.Class6"),
				_toJSONObject(
					"crypto:MessageDigest:SHA-256",
					JSONUtil.put(
						"algorithmProperties",
						JSONUtil.put(
							"cryptoFunctions", JSONUtil.putAll("digest")
						).put(
							"primitive", "hash"
						)
					).put(
						"assetType", "algorithm"
					),
					"SHA-256",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "MessageDigest"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SUN")),
					"com.liferay.test.Class4", "com.liferay.test.Class5"),
				_toJSONObject(
					"crypto:MessageDigest:com.liferay.test.Class9",
					JSONUtil.put(
						"algorithmProperties",
						JSONUtil.put(
							"cryptoFunctions", JSONUtil.putAll("digest")
						).put(
							"primitive", "hash"
						)
					).put(
						"assetType", "algorithm"
					),
					"MessageDigest",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "MessageDigest"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject(
							"unresolved-reason",
							"parameter 1 of com.liferay.test.Class9.digest"),
						_toPropertyJSONObject(
							"unresolved-reason",
							"the field com.liferay.test.Class9._algorithm")),
					"com.liferay.test.Class9"),
				_toJSONObject(
					"crypto:SSLContext:TLSv1.2",
					JSONUtil.put(
						"assetType", "protocol"
					).put(
						"protocolProperties",
						JSONUtil.put(
							"type", "tls"
						).put(
							"version", "1.2"
						)
					),
					"TLSv1.2",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "SSLContext"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SunJSSE")),
					"com.liferay.test.Class8"),
				_toJSONObject(
					"crypto:SecretKeySpec:RAW",
					JSONUtil.put(
						"assetType", "related-crypto-material"
					).put(
						"relatedCryptoMaterialProperties",
						JSONUtil.put(
							"algorithmRef", "crypto:Mac:HmacSHA1"
						).put(
							"type", "secret-key"
						)
					),
					"RAW",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "SecretKeySpec"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME)),
					"com.liferay.test.Class6"),
				_toJSONObject(
					"crypto:Signature:SHA1withDSA",
					JSONUtil.put(
						"algorithmProperties",
						JSONUtil.put(
							"cryptoFunctions", JSONUtil.putAll("verify")
						).put(
							"primitive", "signature"
						)
					).put(
						"assetType", "algorithm"
					),
					"DSA-SHA-1",
					JSONUtil.putAll(
						_toPropertyJSONObject("feature", _FEATURE_NAME),
						_toPropertyJSONObject("jca-class", "Signature"),
						_toPropertyJSONObject("module", _BUNDLE_SYMBOLIC_NAME),
						_toPropertyJSONObject("provider", "SUN")),
					"com.liferay.test.Class3")
			).toString(),
			_toJSON(components), JSONCompareMode.STRICT);
	}

	private CryptoCall _createCryptoCall(
		String algorithm, String className,
		List<CryptoFunction> cryptoFunctions, String jcaClassName) {

		CryptoCall cryptoCall = new CryptoCall(
			_BUNDLE_SYMBOLIC_NAME, className, _FEATURE_NAME, jcaClassName);

		cryptoCall.setAlgorithm(algorithm);
		cryptoCall.setCryptoFunctions(cryptoFunctions);

		return cryptoCall;
	}

	private String _toJSON(List<Component> components) throws Exception {
		Bom bom = new Bom();

		bom.setComponents(components);

		BomJsonGenerator bomJsonGenerator = BomGeneratorFactory.createJson(
			Version.VERSION_16, bom);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			bomJsonGenerator.toJsonString());

		return String.valueOf(jsonObject.getJSONArray("components"));
	}

	private JSONObject _toJSONObject(
			String bomRef, JSONObject cryptoPropertiesJSONObject, String name,
			JSONArray propertiesJSONArray, String... locations)
		throws Exception {

		JSONArray occurrencesJSONArray = JSONUtil.toJSONArray(
			Arrays.asList(locations),
			location -> JSONUtil.put("location", location));

		return JSONUtil.put(
			"bom-ref", bomRef
		).put(
			"cryptoProperties", cryptoPropertiesJSONObject
		).put(
			"evidence", JSONUtil.put("occurrences", occurrencesJSONArray)
		).put(
			"name", name
		).put(
			"properties", propertiesJSONArray
		).put(
			"type", "cryptographic-asset"
		);
	}

	private JSONObject _toPropertyJSONObject(String name, String value) {
		return JSONUtil.put(
			"name", "liferay:cbom:" + name
		).put(
			"value", value
		);
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		RandomTestUtil.randomString();

	private static final String _FEATURE_NAME = RandomTestUtil.randomString();

}