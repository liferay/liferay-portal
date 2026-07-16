/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.module.framework.ModuleFrameworkUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.LicenseUtil;

import java.io.File;
import java.io.Serializable;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

/**
 * @author Tina Tian
 */
public abstract class BaseLicenseTestCase implements Serializable {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	public static File deployFreeTierPortalLicense(long validityPeriod)
		throws Exception {

		return deployFreeTierPortalLicense(
			_FREE_TIER_DOMAIN, StringPool.BLANK, validityPeriod);
	}

	public static File deployFreeTierPortalLicense(
			String domain, long validityPeriod)
		throws Exception {

		return deployFreeTierPortalLicense(
			domain, StringPool.BLANK, validityPeriod);
	}

	public static File deployFreeTierPortalLicense(
			String domain, String key, long validityPeriod)
		throws Exception {

		StringBundler sb = new StringBundler(20);

		sb.append("<?xml version=\"1.0\"?><license><account-name>");
		sb.append(_FREE_TIER_ACCOUNT_NAME);
		sb.append("</account-name><product-id>");
		sb.append(getPortalProductId());
		sb.append("</product-id><product-name>");
		sb.append(_FREE_TIER_PRODUCT_NAME);
		sb.append("</product-name><product-version>2026.Q1</product-version>");
		sb.append("<license-type>");
		sb.append(_FREE_TIER_LICENSE_TYPE);
		sb.append("</license-type><license-version>6</license-version>");
		sb.append("<start-date>");

		long startTime = System.currentTimeMillis();

		sb.append(_DATE_FORMAT.format(new Date(startTime)));

		sb.append("</start-date><expiration-date>");
		sb.append(_DATE_FORMAT.format(new Date(startTime + validityPeriod)));
		sb.append("</expiration-date>");
		sb.append("<max-cluster-nodes>3</max-cluster-nodes><domains><domain>");
		sb.append(domain);
		sb.append("</domain><domain>localhost</domain></domains><key>");
		sb.append(key);
		sb.append("</key></license>");

		_registerLicense(sb.toString());

		return _buildBinaryFile(
			getPortalProductId(), _FREE_TIER_ACCOUNT_NAME,
			_FREE_TIER_PRODUCT_NAME, _FREE_TIER_LICENSE_TYPE);
	}

	public static SafeCloseable disableValidateWithSafeCloseable() {
		return setReturnValueWithSafeCloseable(
			ReflectionsHolder._validateMethod, true);
	}

	public static boolean isReleaseBundle() {
		try {
			if (ReflectionsHolder._licenseManagerHelperClass != null) {
				return true;
			}
		}
		catch (ExceptionInInitializerError exceptionInInitializerError) {
			if (_log.isDebugEnabled()) {
				_log.debug(exceptionInInitializerError);
			}
		}

		return false;
	}

	public static SafeCloseable setReturnValueWithSafeCloseable(
		Method method, Object result) {

		ResettableClassFileTransformer resettableClassFileTransformer =
			_transformMethod(method, result);

		return () -> resettableClassFileTransformer.reset(
			ReflectionsHolder._instrumentation,
			AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
	}

	public static SafeCloseable setVersionWithSafeCloseable(String version) {
		return setReturnValueWithSafeCloseable(
			ReflectionsHolder._versionMethod, version);
	}

	public void assertBundlesExisted(String... bundleNames) {
		Set<String> bundleSymbolicNames = _getBundleSymbolicNames();

		Assert.assertTrue(
			bundleSymbolicNames.containsAll(SetUtil.fromArray(bundleNames)));
	}

	public void assertBundlesNotExisted(String... bundleNames) {
		Set<String> bundleSymbolicNames = _getBundleSymbolicNames();

		Assert.assertFalse(
			bundleSymbolicNames.containsAll(SetUtil.fromArray(bundleNames)));
	}

	public void assertLicensePropertiesExisted(String productId) {
		Map<String, String> licenseProperties =
			LicenseManagerUtil.getLicenseProperties(productId);

		Assert.assertFalse(
			licenseProperties.toString(), licenseProperties.isEmpty());
	}

	public void assertLicensePropertiesNotExisted(String productId) {
		Map<String, String> licenseProperties =
			LicenseManagerUtil.getLicenseProperties(productId);

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());
	}

	public void assertPortalLicenseExpired() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(response.contains(_EXPIRED_LICENSE_KEY));
	}

	public void assertPortalLicenseInvalid(String failMessage)
		throws Exception {

		try {
			hitHomePage("localhost", getLocalPort());

			Assert.fail("Unable to see LogEntriesException");
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(failMessage, throwable.getMessage());

			String payload = logEntriesException.getPayload();

			Assert.assertTrue(payload.contains(_INVALID_LICENSE_KEY));
		}
	}

	public void assertPortalLicenseNotRegistered() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(response.contains(_NOT_REGISTERED_LICENSE_KEY));
	}

	public void assertPortalLicenseRegistered() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertFalse(response.contains(_LICENSE_PAGE_KEY));
	}

	public File deployAppLicense(App app, long validityPeriod)
		throws Exception {

		return deployAppLicense(
			app, System.currentTimeMillis(), validityPeriod);
	}

	public File deployAppLicense(App app, long startTime, long validityPeriod)
		throws Exception {

		StringBundler sb = new StringBundler(19);

		sb.append("<?xml version=\"1.0\"?><license><product-id>");
		sb.append(getProductId(app));
		sb.append("</product-id><product-name>");
		sb.append(app);
		sb.append("</product-name><product-version>2026.Q1</product-version>");
		sb.append("<license-type>");
		sb.append(_APP_LICENSE_TYPE);
		sb.append("</license-type><license-version>3</license-version>");
		sb.append("<start-date>");
		sb.append(_DATE_FORMAT.format(new Date(startTime)));
		sb.append("</start-date><expiration-date>");
		sb.append(_DATE_FORMAT.format(new Date(startTime + validityPeriod)));
		sb.append("</expiration-date><host-names>");
		sb.append("<host-name>localhost</host-name>");
		sb.append("</host-names><ip-addresses>");

		for (String localIpAddress : LicenseUtil.getIpAddresses()) {
			sb.append("<ip-address>");
			sb.append(localIpAddress);
			sb.append("</ip-address>");
		}

		sb.append("</ip-addresses><mac-addresses>");

		for (String localMacAddress : LicenseUtil.getMacAddresses()) {
			sb.append("<mac-address>");
			sb.append(localMacAddress);
			sb.append("</mac-address>");
		}

		sb.append("</mac-addresses><key></key></license>");

		_registerLicense(sb.toString());

		return _buildBinaryFile(
			getProductId(app), StringPool.BLANK, app.toString(),
			_APP_LICENSE_TYPE);
	}

	public File deployEnterprisePortalLicense(long validityPeriod)
		throws Exception {

		long currentTimeMillis = System.currentTimeMillis();

		StringBundler sb = new StringBundler(19);

		sb.append("<?xml version=\"1.0\"?><license><account-name>");
		sb.append(_ENTERPRISE_ACCOUNT_NAME);
		sb.append("</account-name><product-id>");
		sb.append(getPortalProductId());
		sb.append("</product-id><product-name>");
		sb.append(_ENTERPRISE_PRODUCT_NAME);
		sb.append("</product-name><product-version>2026.Q1</product-version>");
		sb.append("<license-type>");
		sb.append(_ENTERPRISE_LICENSE_TYPE);
		sb.append("</license-type><license-version>6</license-version>");
		sb.append("<start-date>");
		sb.append(_DATE_FORMAT.format(new Date(currentTimeMillis)));
		sb.append("</start-date><expiration-date>");
		sb.append(
			_DATE_FORMAT.format(new Date(currentTimeMillis + validityPeriod)));
		sb.append("</expiration-date>");
		sb.append("<domains><domain>");
		sb.append(_ENTERPRISE_DOMAIN);
		sb.append("</domain><domain>localhost</domain></domains>");
		sb.append("<key></key></license>");

		_registerLicense(sb.toString());

		return _buildBinaryFile(
			getPortalProductId(), _ENTERPRISE_ACCOUNT_NAME,
			_ENTERPRISE_PRODUCT_NAME, _ENTERPRISE_LICENSE_TYPE);
	}

	public void resetCheckInterval() throws Exception {
		for (Field field : _lifecycleActionClass.getDeclaredFields()) {
			field.setAccessible(true);

			if (!Modifier.isFinal(field.getModifiers()) &&
				Objects.equals(field.getType(), long.class)) {

				field.set(_lifecycleAction, 0L);
			}
			else if (Objects.equals(field.getType(), Map.class)) {
				Map<?, ?> map = (Map<?, ?>)field.get(_lifecycleAction);

				for (Object object : map.values()) {
					if (Long.class.isAssignableFrom(object.getClass())) {
						map.clear();

						break;
					}
				}
			}
		}
	}

	public SafeCloseable resetLicenseDataWithSafeCloseble() throws Exception {
		File dir = new File(LicenseUtil.LICENSE_REPOSITORY_DIR);

		File tmpDir = FileUtil.createTempFolder();

		if (dir.exists()) {
			FileUtil.copyDirectory(dir, tmpDir);

			FileUtil.deltree(dir);
		}

		checkLicense(getPortalProductId());

		for (App app : App.values()) {
			checkLicense(getProductId(app));
		}

		resetLifecycleAction();

		return () -> {
			if (dir.exists()) {
				FileUtil.deltree(dir);
			}

			if (tmpDir.exists()) {
				try {
					FileUtil.copyDirectory(tmpDir, dir);
				}
				catch (Exception exception) {
					throw new IllegalStateException(exception);
				}

				FileUtil.deltree(tmpDir);
			}

			try {
				checkLicense(getPortalProductId());

				for (App app : App.values()) {
					checkLicense(getProductId(app));
				}

				resetLifecycleAction();
			}
			catch (Exception exception) {
				throw new IllegalStateException(exception);
			}
		};
	}

	public void resetLifecycleAction() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_BUNDLE_START_STOP_LOGGER, LoggerTestUtil.ALL)) {

			for (Method method : _lifecycleActionClass.getDeclaredMethods()) {
				if (Arrays.equals(
						method.getParameterTypes(),
						new Class<?>[] {
							BundleContext.class, Map.class, Framework.class
						})) {

					method.setAccessible(true);

					for (Field field :
							_lifecycleActionClass.getDeclaredFields()) {

						if (Map.class.isAssignableFrom(field.getType())) {
							field.setAccessible(true);

							if (!Modifier.isFinal(field.getModifiers())) {
								Object bundleData = field.get(_lifecycleAction);

								if (bundleData != null) {
									method.invoke(
										_lifecycleAction,
										SystemBundleUtil.getBundleContext(),
										bundleData,
										ModuleFrameworkUtil.getFramework());
								}
							}
							else {
								Map<?, ?> map = (Map<?, ?>)field.get(
									_lifecycleAction);

								for (Object object : map.values()) {
									if (Map.class.isAssignableFrom(
											object.getClass())) {

										method.invoke(
											_lifecycleAction,
											SystemBundleUtil.getBundleContext(),
											object,
											ModuleFrameworkUtil.getFramework());
									}
								}
							}
						}
					}

					break;
				}
			}

			for (Field field : _lifecycleActionClass.getDeclaredFields()) {
				field.setAccessible(true);

				if (!Modifier.isFinal(field.getModifiers())) {
					if (Objects.equals(field.getType(), long.class)) {
						field.set(_lifecycleAction, 0L);
					}
					else if (Objects.equals(field.getType(), boolean.class)) {
						field.set(_lifecycleAction, false);
					}
					else {
						field.set(_lifecycleAction, null);
					}
				}
				else if (Objects.equals(field.getType(), Map.class)) {
					Map<?, ?> map = (Map<?, ?>)field.get(_lifecycleAction);

					map.clear();
				}
			}

			_throwLogEntriesException(logCapture, null, LoggerTestUtil.ERROR);
		}
	}

	protected static Field findField(String propertyKey) throws Exception {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		String field = getProperty(propertyKey);

		return ReflectionTestUtil.getField(
			classLoader.loadClass(
				field.substring(0, field.lastIndexOf(StringPool.PERIOD))),
			field.substring(field.lastIndexOf(StringPool.PERIOD) + 1));
	}

	protected static Method findMethod(String propertyKey) throws Exception {
		String method = getProperty(propertyKey);

		String methodName = method.substring(
			0, method.indexOf(StringPool.OPEN_PARENTHESIS));

		String className = methodName.substring(
			0, methodName.lastIndexOf(StringPool.PERIOD));
		String methodSimpleName = methodName.substring(
			methodName.lastIndexOf(StringPool.PERIOD) + 1);

		String parameterString = method.substring(
			method.indexOf(StringPool.OPEN_PARENTHESIS) + 1,
			method.length() - 1);

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		if (Validator.isNull(parameterString)) {
			return ReflectionTestUtil.getMethod(
				classLoader.loadClass(className), methodSimpleName);
		}

		String[] parameterNames = parameterString.split(StringPool.COMMA);

		Class<?>[] parameterTypes = new Class<?>[parameterNames.length];

		for (int i = 0; i < parameterNames.length; i++) {
			parameterTypes[i] = classLoader.loadClass(parameterNames[i]);
		}

		return ReflectionTestUtil.getMethod(
			classLoader.loadClass(className), methodSimpleName, parameterTypes);
	}

	protected static String getPortalProductId() {
		return getProperty("product.id.portal");
	}

	protected static String getProperty(String propertyKey) {
		String value = _licenseTestProperties.getProperty(propertyKey);

		if (Validator.isNull(value)) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Property ", _PROPERTY_PREFIX, propertyKey, " is not set"));
		}

		return value;
	}

	protected static Class<?> getValidateClass() {
		return ReflectionsHolder._validateClass;
	}

	protected void checkLicense(String productId) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_licensePackageName, LoggerTestUtil.ALL)) {

			LicenseManagerUtil.checkLicense(productId);

			_throwLogEntriesException(logCapture, null, LoggerTestUtil.ERROR);
		}
	}

	protected String getCurrentVersion() throws Exception {
		Method versionMethod = ReflectionsHolder._versionMethod;

		return (String)versionMethod.invoke(null);
	}

	protected int getLocalPort() {
		int localPort = PortalUtil.getPortalLocalPort(false);

		if (localPort == -1) {
			localPort = 8080;
		}

		return localPort;
	}

	protected String getProductId(App app) {
		return getProperty(
			"product.id." + StringUtil.toLowerCase(app.toString()));
	}

	protected String hitHomePage(String host, int port) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_lifecycleActionClass.getName(), LoggerTestUtil.ALL)) {

			Http.Options options = new Http.Options();

			options.setCookieSpec(Http.CookieSpec.IGNORE_COOKIES);
			options.setLocation(String.format("http://%s:%d/", host, port));
			options.setMethod(Http.Method.GET);

			String response = HttpUtil.URLtoString(options);

			_throwLogEntriesException(
				logCapture, response, LoggerTestUtil.DEBUG);

			return response;
		}
	}

	private static File _buildBinaryFile(
		String productId, String accountName, String productEntryName,
		String licenseType) {

		StringBundler sb = new StringBundler(6);

		if (productId.equals(getPortalProductId())) {
			sb.append(StringUtil.extractChars(accountName));
			sb.append("_");
		}

		sb.append(StringUtil.extractChars(productEntryName));
		sb.append("_");
		sb.append(StringUtil.extractChars(licenseType));
		sb.append(".li");

		return new File(LicenseUtil.LICENSE_REPOSITORY_DIR, sb.toString());
	}

	private static void _registerLicense(String licenseXML) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_licensePackageName, LoggerTestUtil.ALL)) {

			LicenseManagerUtil.registerLicense(
				JSONUtil.put("licenseXML", licenseXML));

			_throwLogEntriesException(logCapture, null, LoggerTestUtil.ERROR);
		}
	}

	private static void _throwLogEntriesException(
			LogCapture logCapture, String payload, String priority)
		throws LogEntriesException {

		List<LogEntry> logEntries = new ArrayList<>();

		for (LogEntry logEntry : logCapture.getLogEntries()) {
			if (Objects.equals(priority, logEntry.getPriority())) {
				logEntries.add(logEntry);
			}
		}

		if (!logEntries.isEmpty()) {
			throw new LogEntriesException(logEntries, payload);
		}
	}

	private static ResettableClassFileTransformer _transformMethod(
		Method method, Object returnValue) {

		return new AgentBuilder.Default(
		).disableClassFormatChanges(
		).with(
			AgentBuilder.RedefinitionStrategy.RETRANSFORMATION
		).type(
			ElementMatchers.is(method.getDeclaringClass())
		).transform(
			(builder, typeDescription, classLoader, module, protectionDomain) ->
				builder.method(
					ElementMatchers.is(method)
				).intercept(
					FixedValue.value(returnValue)
				)
		).installOn(
			ReflectionsHolder._instrumentation
		);
	}

	private Set<String> _getBundleSymbolicNames() {
		Set<String> bundleSymbolicNames = new HashSet<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			bundleSymbolicNames.add(bundle.getSymbolicName());
		}

		return bundleSymbolicNames;
	}

	private static final String _APP_LICENSE_TYPE = "production";

	private static final String _BUNDLE_START_STOP_LOGGER =
		"com.liferay.portal.bootstrap.log.BundleStartStopLogger";

	private static final DateFormat _DATE_FORMAT = new SimpleDateFormat(
		"EEEE, MMMM d, yyyy hh:mm:ss a z", LocaleUtil.US);

	private static final String _ENTERPRISE_ACCOUNT_NAME = "Enterprise Account";

	private static final String _ENTERPRISE_DOMAIN = "enterprise.com";

	private static final String _ENTERPRISE_LICENSE_TYPE = "enterprise";

	private static final String _ENTERPRISE_PRODUCT_NAME = "DXP Enterprise";

	private static final String _EXPIRED_LICENSE_KEY =
		"This instance is expired.";

	private static final String _FREE_TIER_ACCOUNT_NAME = "Free Account";

	private static final String _FREE_TIER_DOMAIN = "free.tier.com";

	private static final String _FREE_TIER_LICENSE_TYPE = "free";

	private static final String _FREE_TIER_PRODUCT_NAME = "DXP Production";

	private static final String _INVALID_LICENSE_KEY =
		"This instance is invalid.";

	private static final String _LICENSE_PAGE_KEY =
		"<strong class=\"lead\">Error:</strong>";

	private static final String _NOT_REGISTERED_LICENSE_KEY =
		"This instance is not registered.";

	private static final String _PROPERTY_PREFIX = "license.test.";

	private static final Log _log = LogFactoryUtil.getLog(
		BaseLicenseTestCase.class);

	private static String _licensePackageName;
	private static Properties _licenseTestProperties;
	private static Object _lifecycleAction;
	private static Class<?> _lifecycleActionClass;

	private static class ReflectionsHolder {

		private static Instrumentation _instrumentation;
		private static Class<?> _licenseManagerHelperClass;
		private static Class<?> _validateClass;
		private static Method _validateMethod;
		private static Method _versionMethod;

		static {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			try {
				_licenseManagerHelperClass = classLoader.loadClass(
					"com.liferay.portal.ee.license.util.LicenseManagerHelper");
			}
			catch (ClassNotFoundException classNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(classNotFoundException);
				}
			}

			if (_licenseManagerHelperClass != null) {
				_licenseTestProperties = PropsUtil.getProperties(
					_PROPERTY_PREFIX, true);

				if (_licenseTestProperties.isEmpty()) {
					throw new IllegalArgumentException(
						"Missing license test properties");
				}

				try {
					_validateClass = classLoader.loadClass(
						getProperty("validate.class"));

					for (Method method : _validateClass.getDeclaredMethods()) {
						if (method.getReturnType() == boolean.class) {
							_validateMethod = method;

							break;
						}
					}

					_versionMethod = findMethod("version.method");

					Field field = findField("lifecycle.action.field");

					_lifecycleAction = field.get(null);

					_lifecycleActionClass = _lifecycleAction.getClass();

					_licensePackageName =
						_lifecycleActionClass.getPackageName();

					ByteBuddyAgent.install();

					_instrumentation = ByteBuddyAgent.getInstrumentation();
				}
				catch (Exception exception) {
					throw new ExceptionInInitializerError(exception);
				}
			}
		}

	}

}