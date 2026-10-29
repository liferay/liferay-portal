/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.cyclonedx.Version;
import org.cyclonedx.exception.ParseException;
import org.cyclonedx.parsers.JsonParser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Manuele Castro
 */
public class CBOMBuilderTest {

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
	public void testBuild() throws Exception {
		CryptoCallScanner cryptoCallScanner = new CryptoCallScanner(false);

		cryptoCallScanner.scan(_getJarFile(DigesterUtil.class));
		cryptoCallScanner.scan(_getJarFile(JSONFactoryImpl.class));

		List<CryptoCall> scannedCryptoCalls =
			cryptoCallScanner.getCryptoCalls();

		Set<String> messages = new TreeSet<>();

		List<CryptoCall> cryptoCalls = CBOMBuilder.getCryptoCalls(
			scannedCryptoCalls, messages);

		Assert.assertEquals(
			Arrays.asList(
				"crypto-features.json maps company.encryption.algorithm to " +
					"Company encryption, but no crypto call matches it",
				"crypto-features.json maps company.encryption.key.size to " +
					"Company encryption, but no crypto call matches it",
				"crypto-features.json maps object.encryption.algorithm to " +
					"Encrypted object fields, but no crypto call matches it",
				"crypto-features.json maps passwords.encryption.algorithm to " +
					"Password hashing, but no crypto call matches it",
				"crypto-features.json maps " +
					"passwords.encryption.algorithm.legacy to Legacy " +
						"password verification, but no crypto call matches it"),
			new ArrayList<>(messages));

		String configurationName = RandomTestUtil.randomString();

		String cbomJSON = CBOMBuilder.build(configurationName, cryptoCalls);

		Assert.assertEquals(
			cbomJSON, CBOMBuilder.build(configurationName, cryptoCalls));

		JsonParser jsonParser = new JsonParser();

		List<ParseException> parseExceptions = jsonParser.validate(
			cbomJSON.getBytes(StandardCharsets.UTF_8), Version.VERSION_16);

		Assert.assertEquals(
			parseExceptions.toString(), 0, parseExceptions.size());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(cbomJSON);

		JSONObject metadataJSONObject = jsonObject.getJSONObject("metadata");

		JSONObject componentJSONObject = metadataJSONObject.getJSONObject(
			"component");

		Assert.assertEquals(
			ReleaseInfo.getName(), componentJSONObject.getString("name"));
		Assert.assertEquals(
			ReleaseInfo.getVersionDisplayName(),
			componentJSONObject.getString("version"));

		Assert.assertFalse(metadataJSONObject.has("timestamp"));

		JSONArray lifecyclesJSONArray = metadataJSONObject.getJSONArray(
			"lifecycles");

		JSONObject lifecycleJSONObject = lifecyclesJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"post-build", lifecycleJSONObject.getString("phase"));

		Assert.assertFalse(jsonObject.has("dependencies"));

		JSONArray componentsJSONArray = jsonObject.getJSONArray("components");

		List<String> bomRefs = new ArrayList<>();
		Map<String, JSONArray> propertiesJSONArrays = new HashMap<>();

		for (int i = 0; i < componentsJSONArray.length(); i++) {
			componentJSONObject = componentsJSONArray.getJSONObject(i);

			String bomRef = componentJSONObject.getString("bom-ref");

			bomRefs.add(bomRef);
			propertiesJSONArrays.put(
				bomRef, componentJSONObject.getJSONArray("properties"));
		}

		Assert.assertEquals(
			Arrays.asList(
				"crypto:MessageDigest:MD5", "crypto:MessageDigest:SHA-1",
				"crypto:MessageDigest:SHA-256",
				"crypto:MessageDigest:auth.mac.algorithm",
				"crypto:SecretKeySpec:tunneling.servlet.encryption.algorithm",
				"crypto:SecureRandom:NativePRNG"),
			bomRefs);

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				_toPropertyJSONObject("configured-by", "auth.mac.algorithm"),
				_toPropertyJSONObject(
					"default-value", "auth.mac.algorithm=MD5"),
				_toPropertyJSONObject("feature", "Authentication MAC"),
				_toPropertyJSONObject("jca-class", "MessageDigest"),
				_toPropertyJSONObject("module", "com.liferay.portal.impl")
			).toString(),
			String.valueOf(
				propertiesJSONArrays.get(
					"crypto:MessageDigest:auth.mac.algorithm")),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.putAll(
				_toPropertyJSONObject("feature", "CSRF token whitelist"),
				_toPropertyJSONObject("feature", "Image URL tokens"),
				_toPropertyJSONObject(
					"feature", "JDBC driver download verification"),
				_toPropertyJSONObject(
					"feature", "Portlet preferences cache keys"),
				_toPropertyJSONObject("feature", "Translated response ETags"),
				_toPropertyJSONObject("jca-class", "MessageDigest"),
				_toPropertyJSONObject("module", "com.liferay.portal.impl"),
				_toPropertyJSONObject("module", "com.liferay.portal.kernel"),
				_toPropertyJSONObject("provider", "SUN")
			).toString(),
			String.valueOf(
				propertiesJSONArrays.get("crypto:MessageDigest:SHA-1")),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testGetCryptoCalls() throws Exception {
		CryptoCall cryptoCall1 = _createCryptoCall(
			"MD5", "com.liferay.portal.kernel",
			"com.liferay.portal.util.FileImpl");
		CryptoCall cryptoCall2 = _createCryptoCall(
			"MD5", "com.liferay.portal.impl", RandomTestUtil.randomString());

		CryptoCall cryptoCall3 = _createCryptoCall(
			null, "com.liferay.portal.impl", RandomTestUtil.randomString());

		cryptoCall3.setUnresolvedReasons(
			Collections.singletonList(RandomTestUtil.randomString()));

		CryptoCall cryptoCall4 = _createCryptoCall(
			"MD5", "com.liferay.portal.encryptor",
			RandomTestUtil.randomString());

		CryptoCall cryptoCall5 = _createCryptoCall(
			null, "com.liferay.portal.encryptor",
			RandomTestUtil.randomString());

		cryptoCall5.setUnresolvedReasons(
			Collections.singletonList(RandomTestUtil.randomString()));

		CryptoCall cryptoCall6 = _createCryptoCall(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		cryptoCall6.setConfiguredBy(
			Collections.singletonList("company.encryption.algorithm"));

		CryptoCall cryptoCall7 = _createCryptoCall(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		cryptoCall7.setConfiguredBy(
			Collections.singletonList(RandomTestUtil.randomString()));

		CryptoCall cryptoCall8 = _createCryptoCall(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		cryptoCall8.setUnresolvedReasons(
			Collections.singletonList(RandomTestUtil.randomString()));

		CryptoCall cryptoCall9 = _createCryptoCall(
			null, "com.liferay.portal.security.password.encryptor.impl",
			RandomTestUtil.randomString());

		cryptoCall9.setConfiguredBy(
			Arrays.asList(
				"passwords.encryption.algorithm",
				"passwords.encryption.algorithm.legacy",
				RandomTestUtil.randomString()));

		CryptoCall cryptoCall10 = _createCryptoCall(
			"SHA-1", "com.liferay.portal.security.password.encryptor.impl",
			RandomTestUtil.randomString());

		cryptoCall10.setSelectedBy(
			Arrays.asList(
				"passwords.encryption.algorithm",
				"passwords.encryption.algorithm.legacy",
				RandomTestUtil.randomString()));

		String bundleName = cryptoCall2.getFeatureName();

		Set<String> messages = new TreeSet<>();

		List<CryptoCall> cryptoCalls = CBOMBuilder.getCryptoCalls(
			Arrays.asList(
				cryptoCall1, cryptoCall2, cryptoCall3, cryptoCall4, cryptoCall5,
				cryptoCall6, cryptoCall7, cryptoCall8, cryptoCall9,
				cryptoCall10),
			messages);

		Assert.assertEquals(
			Arrays.asList(cryptoCall1, cryptoCall2, cryptoCall3, cryptoCall5),
			cryptoCalls.subList(0, 4));
		Assert.assertSame(cryptoCall8, cryptoCalls.get(5));

		List<CryptoCall> partCryptoCalls = new ArrayList<>(
			cryptoCalls.subList(4, cryptoCalls.size()));

		partCryptoCalls.remove(cryptoCall8);

		Assert.assertEquals(
			Arrays.asList(
				"Company encryption [company.encryption.algorithm] " +
					"MessageDigest",
				"Password hashing [passwords.encryption.algorithm] " +
					"MessageDigest",
				"Legacy password verification " +
					"[passwords.encryption.algorithm.legacy] MessageDigest",
				"Password hashing SHA-1 MessageDigest",
				"Legacy password verification SHA-1 MessageDigest"),
			TransformUtil.transform(
				partCryptoCalls,
				cryptoCall -> {
					if (cryptoCall.getAlgorithm() != null) {
						return StringBundler.concat(
							cryptoCall.getFeatureName(), StringPool.SPACE,
							cryptoCall.getAlgorithm(), StringPool.SPACE,
							cryptoCall.getJCAClassName());
					}

					return StringBundler.concat(
						cryptoCall.getFeatureName(), StringPool.SPACE,
						cryptoCall.getConfiguredBy(), StringPool.SPACE,
						cryptoCall.getJCAClassName());
				}));

		Assert.assertEquals("File checksums", cryptoCall1.getFeatureName());
		Assert.assertEquals(bundleName, cryptoCall2.getFeatureName());

		Assert.assertTrue(
			messages.contains(
				StringBundler.concat(
					"crypto-features.json has no feature for MD5 in ",
					cryptoCall2.getClassName(),
					", so the CBOM uses the bundle name ", bundleName)));

		for (CryptoCall cryptoCall :
				Arrays.asList(cryptoCall3, cryptoCall5, cryptoCall8)) {

			List<String> unresolvedReasons = cryptoCall.getUnresolvedReasons();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"The scan cannot read the algorithm of the ",
						"MessageDigest call in ", cryptoCall.getClassName(),
						", which comes from ", unresolvedReasons.get(0))));
		}

		Assert.assertFalse(
			messages.contains(
				"crypto-features.json maps MD5 in " +
					"com.liferay.portal.util.FileImpl to File checksums, but " +
						"no crypto call matches it"));
		Assert.assertFalse(
			messages.contains(
				"crypto-features.json maps company.encryption.algorithm to " +
					"Company encryption, but no crypto call matches it"));
		Assert.assertTrue(
			messages.contains(
				"crypto-features.json maps company.encryption.key.size to " +
					"Company encryption, but no crypto call matches it"));
		Assert.assertTrue(
			messages.contains(
				"crypto-features.json maps auth.mac.algorithm to " +
					"Authentication MAC, but no crypto call matches it"));
	}

	@Test
	public void testMain() throws Exception {
		File configurationDir = temporaryFolder.newFolder();

		File portalExtPropertiesFile = new File(
			configurationDir, "portal-ext.properties");

		Files.write(
			portalExtPropertiesFile.toPath(),
			"fips.enabled=true".getBytes(StandardCharsets.UTF_8));

		try {
			CBOMBuilder.main(new String[] {configurationDir.getPath()});

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				"Unable to build the CBOM of a FIPS mode configuration",
				unsupportedOperationException.getMessage());
		}
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private CryptoCall _createCryptoCall(
		String algorithm, String bundleSymbolicName, String className) {

		CryptoCall cryptoCall = new CryptoCall(
			bundleSymbolicName, className, RandomTestUtil.randomString(),
			"MessageDigest");

		cryptoCall.setAlgorithm(algorithm);

		return cryptoCall;
	}

	private File _getJarFile(Class<?> clazz) throws Exception {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		return new File(url.toURI());
	}

	private JSONObject _toPropertyJSONObject(String name, String value) {
		return JSONUtil.put(
			"name", "liferay:cbom:" + name
		).put(
			"value", value
		);
	}

}