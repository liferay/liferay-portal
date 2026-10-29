/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import javax.net.ssl.TrustManagerFactory;

import org.cyclonedx.model.component.crypto.enums.CryptoFunction;
import org.cyclonedx.model.component.crypto.enums.RelatedCryptoMaterialType;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Manuele Castro
 */
public class CryptoCallScannerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testScan() throws Exception {
		_testScan(
			ConstantTestClass.class, false, "MessageDigest SHA-256 [digest]");
		_testScan(
			ConstructorParameterTestClass.class, false,
			"MessageDigest unresolved: parameter 1 of the constructor of " +
				ConstructorParameterTestClass.class.getName() + " [digest]");
		_testScan(
			new Class<?>[] {CallSiteTestClass.class}, false,
			"MessageDigest SHA-256 [digest]");
		_testScan(
			new Class<?>[] {ComputedTestClass.class}, false,
			"Mac configured by [test.digest] [tag]",
			"MessageDigest unresolved: a value the code computes [digest]");
		_testScan(
			DiamondTestClass.class, false, "MessageDigest SHA-256 [digest]");
		_testScan(
			DigesterUtilDefaultTestClass.class, false,
			"MessageDigest SHA-1 [digest]");
		_testScan(
			DigesterUtilDefaultTestClass.class, true,
			"MessageDigest SHA-256 [digest]");
		_testScan(
			DigesterUtilTestClass.class, false, "MessageDigest MD5 [digest]");
		_testScan(
			FallbackTestClass.class, false,
			"MessageDigest SHA-256 selected by [test.algorithm] [digest]",
			"MessageDigest configured by [test.algorithm] [digest]");
		_testScan(
			FIPSEnabledTestClass.class, false, "MessageDigest MD5 [digest]");
		_testScan(
			FIPSEnabledTestClass.class, true, "MessageDigest SHA-256 [digest]");
		_testScan(
			InstanceFieldTestClass.class, false,
			"SecretKeySpec configured by [test.algorithm] [other]");
		_testScan(
			new Class<?>[] {
				DispatchCallerTestClass.class, DispatchTestClass.class,
				DispatchTestInterface.class
			},
			false, "MessageDigest SHA-384 [digest]");
		_testScan(
			new Class<?>[] {LambdaTestClass.class}, false,
			"Cipher configured by [test.algorithm] [encrypt]",
			"SecretKeySpec configured by [test.algorithm] [other]");
		_testScan(
			new Class<?>[] {KeyTestClass.class}, false,
			"Cipher configured by [test.algorithm] [encrypt]",
			"SecretKeySpec configured by [test.algorithm] [other]");
		_testScan(
			KeyFactoryTestClass.class, false,
			"KeyFactory RSA [other] [private-key]",
			"KeyFactory RSA [other] [public-key]");
		_testScan(
			KeyGeneratorTestClass.class, false,
			"KeyGenerator AES [keygen] 256");
		_testScan(
			MacTestClass.class, false, "Mac HmacSHA256 [tag]",
			"SecretKeySpec HmacSHA256 [other]");
		_testScan(
			MultipleValuesTestClass.class, false, "MessageDigest MD5 [digest]",
			"MessageDigest SHA-256 [digest]");
		_testScan(
			ParameterTestClass.class, false,
			"MessageDigest unresolved: parameter 1 of " +
				ParameterTestClass.class.getName() + ".digest [digest]");
		_testScan(
			PartialTestClass.class, false,
			"MessageDigest configured by [test.algorithm] [digest]",
			"MessageDigest unresolved: the return value of " +
				"java.util.Map.get() [digest]");
		_testScan(
			PrivateMethodTestClass.class, false,
			"Cipher AES/CBC/PKCS5Padding [encrypt]");
		_testScan(
			PropsUtilTestClass.class, false,
			"MessageDigest configured by [test.algorithm] [digest]");
		_testScan(
			new Class<?>[] {
				SelectedCallerTestClass.class, SelectedTestClass.class,
				SelectedTestInterface.class
			},
			false,
			"MessageDigest SHA-512 selected by [test.algorithm] [digest]");

		String defaultSecureRandomAlgorithm =
			CryptoProviderUtil.getDefaultSecureRandomAlgorithm();

		_testScan(
			SecureRandomTestClass.class, false,
			StringBundler.concat(
				"SecureRandom ", defaultSecureRandomAlgorithm, " [generate]"),
			StringBundler.concat(
				"SecureRandom ", defaultSecureRandomAlgorithm, " [generate]"),
			"SecureRandom SHA1PRNG [generate]");

		_testScan(
			SignatureTestClass.class, false,
			"Signature SHA256withRSA [verify]");
		_testScan(
			StaticFieldTestClass.class, false,
			"KeyGenerator configured by [test.algorithm, test.key.size] " +
				"[keygen]");
		_testScan(
			TrustManagerFactoryTestClass.class, false,
			"TrustManagerFactory configured by " +
				"[ssl.TrustManagerFactory.algorithm] [other]");
	}

	@Test
	public void testScanFile() throws Exception {
		String bundleName = RandomTestUtil.randomString();
		String bundleSymbolicName = RandomTestUtil.randomString();

		File bundleJarFile = temporaryFolder.newFile("bundle.jar");

		_writeJarFile(
			bundleName, bundleSymbolicName + ";singleton:=true", bundleJarFile);

		_testScanFile(bundleSymbolicName, bundleName, bundleJarFile);

		File libraryJarFile = temporaryFolder.newFile("library.jar");

		_writeJarFile(null, null, libraryJarFile);

		_testScanFile("library", "library", libraryJarFile);

		File lpkgFile = temporaryFolder.newFile("package.lpkg");

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(lpkgFile))) {

			zipOutputStream.putNextEntry(new ZipEntry("bundle.jar"));

			zipOutputStream.write(Files.readAllBytes(bundleJarFile.toPath()));

			zipOutputStream.closeEntry();
		}

		_testScanFile(bundleSymbolicName, bundleName, lpkgFile);

		CryptoCallScanner bundleCryptoCallScanner = new CryptoCallScanner(
			Collections.singleton(RandomTestUtil.randomString()), false);

		bundleCryptoCallScanner.scan(bundleJarFile);

		Assert.assertEquals(
			Collections.emptyList(), bundleCryptoCallScanner.getCryptoCalls());

		File springJarFile = temporaryFolder.newFile("spring.jar");

		try (JarOutputStream jarOutputStream = new JarOutputStream(
				new FileOutputStream(springJarFile))) {

			jarOutputStream.putNextEntry(
				new JarEntry(_getClassFileName(SpringBeanTestClass.class)));

			jarOutputStream.write(_read(SpringBeanTestClass.class));

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(
				new JarEntry("META-INF/test-spring.xml"));

			String xml = StringUtil.read(
				CryptoCallScannerTest.class, "dependencies/test-spring.xml");

			jarOutputStream.write(xml.getBytes(StandardCharsets.UTF_8));

			jarOutputStream.closeEntry();
		}

		CryptoCallScanner cryptoCallScanner = new CryptoCallScanner(false);

		cryptoCallScanner.scan(springJarFile);

		Assert.assertEquals(
			Collections.singletonList("MessageDigest SHA-256 [digest]"),
			TransformUtil.transform(
				cryptoCallScanner.getCryptoCalls(), this::_getDescription));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	public static class CallSiteTestClass {

		public void digest() throws Exception {
			_identity("MD5");

			MessageDigest.getInstance(_identity("SHA-256"));
		}

		private String _identity(String value) {
			return value;
		}

	}

	public static class ComputedTestClass {

		public void digest() throws Exception {
			MessageDigest.getInstance("SHA-" + _size);
		}

		public void tag() throws Exception {
			String digest = StringUtil.replaceFirst(
				PropsUtil.get("test.digest"), "SHA-", "SHA");

			Mac.getInstance("Hmac" + digest);
		}

		private int _size = 256;

	}

	public static class ConstantTestClass {

		public void digest() throws Exception {
			String algorithm = "SHA-256";

			MessageDigest.getInstance(algorithm);
		}

	}

	public static class ConstructorParameterTestClass {

		public ConstructorParameterTestClass(String algorithm) {
			_algorithm = algorithm;
		}

		public void digest() throws Exception {
			MessageDigest.getInstance(_algorithm);
		}

		private final String _algorithm;

	}

	public static class DiamondTestClass {

		public DiamondTestClass() {
			_algorithm1 = _defaultAlgorithm;
			_algorithm2 = _defaultAlgorithm;
		}

		public void digest(boolean flag) throws Exception {
			MessageDigest.getInstance(flag ? _algorithm1 : _algorithm2);
		}

		private static String _defaultAlgorithm = "SHA-256";

		private final String _algorithm1;
		private final String _algorithm2;

	}

	public static class DigesterUtilDefaultTestClass {

		public String digest() {
			return DigesterUtil.digest("test");
		}

	}

	public static class DigesterUtilTestClass {

		public String digest() {
			return DigesterUtil.digestHex(DigesterUtil.MD5, "test");
		}

	}

	public static class DispatchCallerTestClass {

		public void digest(DispatchTestInterface dispatchTestInterface)
			throws Exception {

			dispatchTestInterface.digest("SHA-384");
		}

	}

	public static class DispatchTestClass implements DispatchTestInterface {

		@Override
		public void digest(String algorithm) throws Exception {
			MessageDigest.getInstance(algorithm);
		}

	}

	public static class FIPSEnabledTestClass {

		public void digest() throws Exception {
			MessageDigest.getInstance(
				PropsValues.FIPS_ENABLED ? DigesterUtil.SHA_256 :
					DigesterUtil.MD5);
		}

	}

	public static class FallbackTestClass {

		public void digest(boolean flag) throws Exception {
			MessageDigest.getInstance(
				flag ? "SHA-256" : PropsUtil.get("test.algorithm"));
		}

	}

	public static class InstanceFieldTestClass {

		public Key getKey(byte[] bytes) {
			return new SecretKeySpec(bytes, _getAlgorithm());
		}

		private String _getAlgorithm() {
			String algorithm = _algorithm;

			if (algorithm == null) {
				algorithm = PropsUtil.get("test.algorithm");

				_algorithm = algorithm;
			}

			return algorithm;
		}

		private String _algorithm;

	}

	public static class KeyFactoryTestClass {

		public PrivateKey getPrivateKey(byte[] bytes) throws Exception {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
		}

		public PublicKey getPublicKey(byte[] bytes) throws Exception {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			return keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
		}

	}

	public static class KeyGeneratorTestClass {

		public void generateKey() throws Exception {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

			keyGenerator.init(256);
		}

	}

	public static class KeyTestClass {

		public void encrypt() throws Exception {
			_encrypt(_getKey());
		}

		private void _encrypt(Key key) throws Exception {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());

			cipher.init(Cipher.ENCRYPT_MODE, key);
		}

		private Key _getKey() {
			return new SecretKeySpec(
				new byte[16], PropsUtil.get("test.algorithm"));
		}

	}

	public static class LambdaTestClass {

		public void encrypt() {
			Key key = _getKey();

			Consumer<String> consumer = value -> _encrypt(key, value);

			consumer.accept("test");
		}

		private void _encrypt(Key key, String value) {
			try {
				Cipher cipher = Cipher.getInstance(key.getAlgorithm());

				cipher.init(Cipher.ENCRYPT_MODE, key);
			}
			catch (Exception exception) {
				throw new RuntimeException(value, exception);
			}
		}

		private Key _getKey() {
			return new SecretKeySpec(
				new byte[16], PropsUtil.get("test.algorithm"));
		}

	}

	public static class MacTestClass {

		public Key getKey(byte[] bytes) throws Exception {
			Mac mac = Mac.getInstance("HmacSHA256");

			return new SecretKeySpec(bytes, mac.getAlgorithm());
		}

	}

	public static class MultipleValuesTestClass {

		public void digest(boolean flag) throws Exception {
			MessageDigest.getInstance(flag ? "SHA-256" : "MD5");
		}

	}

	public static class ParameterTestClass {

		public void digest(String algorithm) throws Exception {
			MessageDigest.getInstance(algorithm);
		}

	}

	public static class PartialTestClass {

		public void digest(boolean flag, String key) throws Exception {
			MessageDigest.getInstance(
				flag ? PropsUtil.get("test.algorithm") : _map.get(key));
		}

		private final Map<String, String> _map = new HashMap<>();

	}

	public static class PrivateMethodTestClass {

		public void encrypt(Key key) throws Exception {
			_encrypt(key, "AES/CBC/PKCS5Padding");
		}

		private void _encrypt(Key key, String transformation) throws Exception {
			Cipher cipher = Cipher.getInstance(transformation);

			cipher.init(Cipher.ENCRYPT_MODE, key);
		}

	}

	public static class PropsUtilTestClass {

		public void digest() throws Exception {
			MessageDigest.getInstance(PropsUtil.get("test.algorithm"));
		}

	}

	public static class SecureRandomTestClass {

		public void generate() throws Exception {
			SecureRandom.getInstance("SHA1PRNG");

			new SecureRandom();

			SecureRandomUtil.nextLong();
		}

	}

	public static class SelectedCallerTestClass {

		public void digest(SelectedTestInterface selectedTestInterface)
			throws Exception {

			selectedTestInterface.digest(PropsUtil.get("test.algorithm"));
		}

	}

	public static class SelectedTestClass implements SelectedTestInterface {

		@Override
		public void digest(String algorithm) throws Exception {
			MessageDigest.getInstance("SHA-512");
		}

	}

	public static class SignatureTestClass {

		public void verify(PublicKey publicKey) throws Exception {
			Signature signature = Signature.getInstance("SHA256withRSA");

			signature.initVerify(publicKey);
		}

	}

	public static class SpringBeanTestClass {

		public SpringBeanTestClass(String algorithm) {
			this(algorithm, StringPool.UTF8);
		}

		public SpringBeanTestClass(String algorithm, String charsetName) {
			_algorithm = algorithm;
			_charsetName = charsetName;
		}

		public SpringBeanTestClass copy() {
			return new SpringBeanTestClass(_algorithm, _charsetName);
		}

		public void digest() throws Exception {
			MessageDigest.getInstance(_algorithm);
		}

		private final String _algorithm;
		private final String _charsetName;

	}

	public static class StaticFieldTestClass {

		public void generateKey() throws Exception {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(_ALGORITHM);

			keyGenerator.init(_KEY_SIZE);
		}

		private static final String _ALGORITHM = StringUtil.toUpperCase(
			GetterUtil.getString(PropsUtil.get("test.algorithm")));

		private static final int _KEY_SIZE = GetterUtil.getInteger(
			PropsUtil.get("test.key.size"));

	}

	public static class TrustManagerFactoryTestClass {

		public void getTrustManagerFactory() throws Exception {
			TrustManagerFactory.getInstance(
				TrustManagerFactory.getDefaultAlgorithm());
		}

	}

	public interface DispatchTestInterface {

		public void digest(String algorithm) throws Exception;

	}

	public interface SelectedTestInterface {

		public void digest(String algorithm) throws Exception;

	}

	private String _getClassFileName(Class<?> clazz) {
		String name = StringUtil.replace(
			clazz.getName(), CharPool.PERIOD, CharPool.SLASH);

		return name + ".class";
	}

	private String _getDescription(CryptoCall cryptoCall) {
		StringBundler sb = new StringBundler(11);

		sb.append(cryptoCall.getJCAClassName());
		sb.append(StringPool.SPACE);

		if (cryptoCall.getAlgorithm() != null) {
			sb.append(cryptoCall.getAlgorithm());

			if (ListUtil.isNotEmpty(cryptoCall.getSelectedBy())) {
				sb.append(" selected by ");
				sb.append(String.valueOf(cryptoCall.getSelectedBy()));
			}
		}
		else if (ListUtil.isNotEmpty(cryptoCall.getConfiguredBy())) {
			sb.append("configured by ");
			sb.append(String.valueOf(cryptoCall.getConfiguredBy()));
		}
		else {
			sb.append("unresolved: ");
			sb.append(
				StringUtil.merge(cryptoCall.getUnresolvedReasons(), ", "));
		}

		sb.append(StringPool.SPACE);
		sb.append(
			String.valueOf(
				TransformUtil.transform(
					cryptoCall.getCryptoFunctions(), CryptoFunction::getName)));

		if (cryptoCall.getKeySize() > 0) {
			sb.append(StringPool.SPACE);
			sb.append(String.valueOf(cryptoCall.getKeySize()));
		}

		if (ListUtil.isNotEmpty(cryptoCall.getRelatedCryptoMaterialTypes())) {
			sb.append(StringPool.SPACE);
			sb.append(
				String.valueOf(
					TransformUtil.transform(
						cryptoCall.getRelatedCryptoMaterialTypes(),
						RelatedCryptoMaterialType::getName)));
		}

		return sb.toString();
	}

	private byte[] _read(Class<?> clazz) throws Exception {
		ClassLoader classLoader = clazz.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				_getClassFileName(clazz))) {

			return inputStream.readAllBytes();
		}
	}

	private void _testScan(
			Class<?> clazz, boolean fipsEnabled, String... expectedDescriptions)
		throws Exception {

		_testScan(new Class<?>[] {clazz}, fipsEnabled, expectedDescriptions);
	}

	private void _testScan(
			Class<?>[] classes, boolean fipsEnabled,
			String... expectedDescriptions)
		throws Exception {

		CryptoCallScanner cryptoCallScanner = new CryptoCallScanner(
			fipsEnabled);

		String bundleSymbolicName = RandomTestUtil.randomString();
		String featureName = RandomTestUtil.randomString();

		List<Class<?>> scannedClasses = new ArrayList<>();

		scannedClasses.add(DigesterUtil.class);

		Collections.addAll(scannedClasses, classes);

		for (Class<?> curClass : scannedClasses) {
			try (InputStream inputStream = new ByteArrayInputStream(
					_read(curClass))) {

				cryptoCallScanner.scan(
					bundleSymbolicName, featureName, inputStream);
			}
		}

		List<CryptoCall> cryptoCalls = cryptoCallScanner.getCryptoCalls();

		for (CryptoCall cryptoCall : cryptoCalls) {
			Assert.assertEquals(
				bundleSymbolicName, cryptoCall.getBundleSymbolicName());
			Assert.assertEquals(
				CryptoCallScannerTest.class.getName(),
				cryptoCall.getClassName());
			Assert.assertEquals(featureName, cryptoCall.getFeatureName());
		}

		List<String> descriptions = TransformUtil.transform(
			cryptoCalls, this::_getDescription);

		descriptions.sort(null);

		Assert.assertEquals(Arrays.asList(expectedDescriptions), descriptions);
	}

	private void _testScanFile(
			String expectedBundleSymbolicName, String expectedFeatureName,
			File file)
		throws Exception {

		CryptoCallScanner cryptoCallScanner = new CryptoCallScanner(false);

		cryptoCallScanner.scan(file);

		List<CryptoCall> cryptoCalls = cryptoCallScanner.getCryptoCalls();

		Assert.assertEquals(cryptoCalls.toString(), 1, cryptoCalls.size());

		CryptoCall cryptoCall = cryptoCalls.get(0);

		Assert.assertEquals(
			expectedBundleSymbolicName, cryptoCall.getBundleSymbolicName());
		Assert.assertEquals(
			CryptoCallScannerTest.class.getName(), cryptoCall.getClassName());
		Assert.assertEquals(expectedFeatureName, cryptoCall.getFeatureName());
	}

	private void _writeJarFile(
			String bundleName, String bundleSymbolicName, File file)
		throws Exception {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");

		if (bundleName != null) {
			attributes.putValue("Bundle-Name", bundleName);
		}

		if (bundleSymbolicName != null) {
			attributes.putValue("Bundle-SymbolicName", bundleSymbolicName);
		}

		try (JarOutputStream jarOutputStream = new JarOutputStream(
				new FileOutputStream(file), manifest)) {

			jarOutputStream.putNextEntry(
				new JarEntry(_getClassFileName(ConstantTestClass.class)));

			jarOutputStream.write(_read(ConstantTestClass.class));

			jarOutputStream.closeEntry();
		}
	}

}