/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.production.readiness;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;

import java.io.File;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * @author Lily Chi
 */
public class ProductionReadinessCheckUtil {

	public static Collection<ProductionReadinessResult> check() {
		return TransformUtil.transform(
			_productionReadinessResultSuppliers, Supplier::get);
	}

	private static ProductionReadinessResult _checkCounterIncrement() {
		int counterIncrement = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.COUNTER_INCREMENT));

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION, "counter-increment"
			).currentValue(
				String.valueOf(counterIncrement)
			).messageParameters(
				PropsKeys.COUNTER_INCREMENT
			).recommendedValue(
				PropsKeys.COUNTER_INCREMENT + ">=" + _MIN_COUNTER_INCREMENT
			);

		if (counterIncrement >= _MIN_COUNTER_INCREMENT) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkDLImagePreviewDPI() {
		int dpi = PropsValues.DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI;

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION,
				"dl-image-preview-dpi"
			).currentValue(
				String.valueOf(dpi)
			).recommendedValue(
				String.valueOf(_MAX_DL_IMAGE_PREVIEW_DPI)
			);

		if (dpi <= _MAX_DL_IMAGE_PREVIEW_DPI) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkDLPreviewForking() {
		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION, "dl-preview-forking"
			).currentValue(
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=" +
					PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
			).messageParameters(
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
			).recommendedValue(
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=true"
			);

		if (PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkExplicitGCDisabled() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean disabled = inputArguments.contains("-XX:+DisableExplicitGC");

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"explicit-gc-disabled"
			).currentValue(
				disabled ? "-XX:+DisableExplicitGC" : StringPool.BLANK
			).recommendedValue(
				"-XX:+DisableExplicitGC"
			);

		if (disabled) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkFileStoreImplementation() {
		String dlStoreImpl = PropsValues.DL_STORE_IMPL;

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION,
				"file-store-implementation"
			).currentValue(
				dlStoreImpl
			).recommendedValue(
				StringUtil.merge(_recommendedDLStoreImplClassNames)
			);

		if (_recommendedDLStoreImplClassNames.contains(dlStoreImpl)) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkGarbageCollectorType() {
		List<GarbageCollectorMXBean> garbageCollectorMXBeans =
			ManagementFactory.getGarbageCollectorMXBeans();

		List<String> gcNames = new ArrayList<>();

		boolean pass = false;

		for (GarbageCollectorMXBean garbageCollectorMXBean :
				garbageCollectorMXBeans) {

			String name = garbageCollectorMXBean.getName();

			gcNames.add(name);

			if (name.contains("G1") || name.contains("Shenandoah") ||
				name.contains("ZGC")) {

				pass = true;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"garbage-collector-type"
			).currentValue(
				StringUtil.merge(gcNames, ", ")
			).recommendedValue(
				"G1, Shenandoah, ZGC"
			);

		if (pass) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkHeapAllocationConsistency() {
		MemoryUsage heapMemoryUsage = _getHeapMemoryUsage();

		long xmsBytes = heapMemoryUsage.getInit();
		long xmxBytes = heapMemoryUsage.getMax();

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"heap-allocation-consistency"
			).currentValue(
				StringBundler.concat(
					"Xms=", _toUnit(xmsBytes, _BYTES_PER_MEGABYTE), "MB, Xmx=",
					_toUnit(xmxBytes, _BYTES_PER_MEGABYTE), "MB")
			);

		if ((xmsBytes > 0) && (xmsBytes == xmxBytes)) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkHeapSizeUpperLimit() {
		MemoryUsage heapMemoryUsage = _getHeapMemoryUsage();

		long xmxBytes = heapMemoryUsage.getMax();

		double maxMemoryGB = _toUnit(xmxBytes, _BYTES_PER_GIGABYTE);

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"heap-size-upper-limit"
			).currentValue(
				maxMemoryGB + "GB"
			);

		if (maxMemoryGB <= 32.0) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkHugePagesConfiguration() {
		MemoryUsage heapMemoryUsage = _getHeapMemoryUsage();

		long xmxBytes = heapMemoryUsage.getMax();

		double maxMemoryGB = _toUnit(xmxBytes, _BYTES_PER_GIGABYTE);

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"huge-pages-configuration");

		if (maxMemoryGB <= 4.0) {
			return builder.messageKeySuffix(
				"heap-under-4gb"
			).pass();
		}

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		String largePageSizeArgument = null;
		boolean useLargePages = false;

		for (String inputArgument : inputArguments) {
			if (inputArgument.equals("-XX:+UseLargePages")) {
				useLargePages = true;
			}
			else if (inputArgument.startsWith(
						_PREFIX_LARGE_PAGE_SIZE_IN_BYTES)) {

				largePageSizeArgument = inputArgument.substring(
					_PREFIX_LARGE_PAGE_SIZE_IN_BYTES.length());
			}
		}

		if (!useLargePages) {
			return builder.messageKeySuffix(
				"no-large-pages"
			).recommendedValue(
				"-XX:+UseLargePages"
			).fail();
		}

		if (largePageSizeArgument == null) {
			return builder.messageKeySuffix(
				"missing-large-page-size"
			).fail();
		}

		long osHugePageSize = _getOSHugePageSize();

		if (osHugePageSize > 0) {
			long configLargePageSize = _parseSize(largePageSizeArgument);

			if (configLargePageSize != osHugePageSize) {
				return builder.currentValue(
					StringBundler.concat(
						"-XX:LargePageSizeInBytes=", largePageSizeArgument,
						", OS HugePageSize=",
						_toUnit(osHugePageSize, _BYTES_PER_KILOBYTE), "KB")
				).messageKeySuffix(
					"size-mismatch"
				).fail();
			}
		}

		return builder.messageKeySuffix(
			"configured"
		).pass();
	}

	private static ProductionReadinessResult _checkJMXConfigurationDisabled() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		String enabledArgument = StringPool.BLANK;

		for (String inputArgument : runtimeMXBean.getInputArguments()) {
			if (inputArgument.startsWith("-Dcom.sun.management.jmxremote")) {
				enabledArgument = inputArgument;

				break;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"jmx-configuration-disabled"
			).currentValue(
				enabledArgument
			);

		if (Validator.isNull(enabledArgument)) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkJSPEngineSettings() {
		if (!ServerDetector.isTomcat()) {
			return null;
		}

		String catalinaBase = System.getProperty("catalina.base");

		if (Validator.isNull(catalinaBase)) {
			catalinaBase = System.getProperty("catalina.home");
		}

		if (Validator.isNull(catalinaBase)) {
			return null;
		}

		File webXmlFile = new File(catalinaBase, "conf/web.xml");

		if (!webXmlFile.exists()) {
			return null;
		}

		try {
			String content = FileUtil.read(webXmlFile);

			Document document = SAXReaderUtil.read(content);

			Element rootElement = document.getRootElement();

			Boolean development = null;
			Boolean mappedFile = null;

			for (Element element : rootElement.elements("servlet")) {
				String servletName = element.elementText("servlet-name");

				if (!servletName.equals("jsp")) {
					continue;
				}

				for (Element initParamElement :
						element.elements("init-param")) {

					String paramName = initParamElement.elementText(
						"param-name");
					String paramValue = initParamElement.elementText(
						"param-value");

					if (paramName.equals("development")) {
						development = GetterUtil.getBoolean(paramValue);
					}
					else if (paramName.equals("mappedFile")) {
						mappedFile = GetterUtil.getBoolean(paramValue);
					}
				}
			}

			boolean developmentEnabled = GetterUtil.getBoolean(
				development, true);

			boolean mappedFileEnabled = GetterUtil.getBoolean(mappedFile, true);

			ProductionReadinessResult.Builder builder =
				ProductionReadinessResult.builder(
					_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
					"jsp-engine-settings"
				).currentValue(
					StringBundler.concat(
						"development=", developmentEnabled, ", mappedfile=",
						mappedFileEnabled)
				).recommendedValue(
					"development=false, mappedfile=false"
				);

			if (!developmentEnabled && !mappedFileEnabled) {
				return builder.pass();
			}

			return builder.fail();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	private static ProductionReadinessResult _checkJSPReloading() {
		boolean directServletContextReload = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD));

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION, "jsp-reloading"
			).currentValue(
				PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD + "=" +
					directServletContextReload
			).messageParameters(
				PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD
			).recommendedValue(
				PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD + "=false"
			);

		if (!directServletContextReload) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkLocalesBeta() {
		List<String> betaLocales = List.of(PropsValues.LOCALES_BETA);

		List<String> enabledBetaLocales = ListUtil.filter(
			List.of(PropsValues.LOCALES_ENABLED), betaLocales::contains);

		String enabledBetaLocalesString = StringUtil.merge(enabledBetaLocales);

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION, "locales-beta"
			).currentValue(
				enabledBetaLocalesString
			).messageParameters(
				enabledBetaLocalesString
			);

		if (enabledBetaLocales.isEmpty()) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkLocalesUnused() {
		List<String> enabledLocales = List.of(PropsValues.LOCALES_ENABLED);

		List<String> unusedLocales = ListUtil.filter(
			List.of(PropsValues.LOCALES),
			locale -> !enabledLocales.contains(locale));

		String unusedLocalesString = StringUtil.merge(unusedLocales);

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION, "locales-unused"
			).currentValue(
				unusedLocalesString
			).messageParameters(
				unusedLocalesString
			);

		if (unusedLocales.isEmpty()) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkPasswordEncryption() {
		String algorithm = PropsUtil.get(
			PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM);

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION, "password-encryption"
			).currentValue(
				algorithm
			).recommendedValue(
				"PBKDF2WithHmacSHA1/160/" + _MIN_PBKDF2_ROUNDS +
					", BCRYPT, SCRYPT"
			);

		if (_isStrongerAlgorithm(algorithm)) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkPoolVsThreadSize() {
		int jdbcMaxPoolSize = GetterUtil.getInteger(
			PropsUtil.get("jdbc.default.maximumPoolSize"));

		int tomcatMaxThreads = _getTomcatMaxThreads();

		if ((jdbcMaxPoolSize <= 0) || (tomcatMaxThreads <= 0)) {
			return null;
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				"database-configuration", "pool-vs-thread-size"
			).currentValue(
				StringBundler.concat(
					"jdbc.default.maximumPoolSize=", jdbcMaxPoolSize,
					", Tomcat maxThreads=", tomcatMaxThreads)
			).recommendedValue(
				"jdbc.default.maximumPoolSize>=" + tomcatMaxThreads
			);

		if (jdbcMaxPoolSize >= tomcatMaxThreads) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkPortalDeveloperProperties() {
		boolean included = ArrayUtil.contains(
			PropsUtil.getArray("include-and-override"),
			"portal-developer.properties");

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_PORTAL_PROPERTIES_CONFIGURATION,
				"portal-developer-properties"
			).currentValue(
				included ? "portal-developer.properties" : StringPool.BLANK
			);

		if (!included) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkPreventDiagnosticOverhead() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean unlocked = inputArguments.contains(
			"-XX:+UnlockDiagnosticVMOptions");

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				_CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION,
				"prevent-diagnostic-overhead"
			).currentValue(
				unlocked ? "-XX:+UnlockDiagnosticVMOptions" : StringPool.BLANK
			);

		if (!unlocked) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkSidecarDetection() {
		SearchEngineInformation searchEngineInformation =
			_searchEngineInformationSnapshot.get();

		String vendorString = searchEngineInformation.getVendorString();

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				"search-engine-connectivity-validation", "sidecar-detection");

		if (vendorString.contains("Sidecar")) {
			return builder.fail();
		}

		return builder.pass();
	}

	private static MemoryUsage _getHeapMemoryUsage() {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		return memoryMXBean.getHeapMemoryUsage();
	}

	private static long _getOSHugePageSize() {
		File file = new File("/proc/meminfo");

		if (!file.exists()) {
			return -1;
		}

		try {
			String content = FileUtil.read(file);

			for (String line : StringUtil.splitLines(content)) {
				if (line.startsWith(_PREFIX_HUGEPAGESIZE)) {
					String sizeString = line.substring(
						_PREFIX_HUGEPAGESIZE.length());

					sizeString = sizeString.trim();

					return _parseSize(
						StringUtil.removeSubstring(sizeString, " "));
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return -1;
	}

	private static int _getTomcatMaxThreads() {
		try {
			MBeanServer mBeanServer =
				ManagementFactory.getPlatformMBeanServer();

			ObjectName objectName = new ObjectName(
				"Catalina:type=ThreadPool,name=*");

			Set<ObjectName> objectNames = mBeanServer.queryNames(
				objectName, null);

			int maxThreads = 0;

			for (ObjectName name : objectNames) {
				int threads = GetterUtil.getInteger(
					mBeanServer.getAttribute(name, "maxThreads"));

				if (threads > maxThreads) {
					maxThreads = threads;
				}
			}

			return maxThreads;
		}
		catch (AttributeNotFoundException | InstanceNotFoundException |
			   MalformedObjectNameException | MBeanException |
			   ReflectionException exception) {

			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return -1;
		}
	}

	private static boolean _isStrongerAlgorithm(String algorithm) {
		if (algorithm == null) {
			return false;
		}

		if (algorithm.equals("BCRYPT") || algorithm.startsWith("BCRYPT/") ||
			algorithm.equals("SCRYPT")) {

			return true;
		}

		if (algorithm.startsWith("PBKDF2WithHmacSHA1/")) {
			String[] parts = algorithm.split("/");

			if (parts.length >= 3) {
				int rounds = GetterUtil.getInteger(parts[2]);

				if (rounds >= _MIN_PBKDF2_ROUNDS) {
					return true;
				}
			}
		}

		return false;
	}

	private static long _parseSize(String sizeString) {
		if (sizeString == null) {
			return -1;
		}

		sizeString = StringUtil.toLowerCase(sizeString.trim());

		long multiplier = 1;

		if (sizeString.endsWith("k") || sizeString.endsWith("kb")) {
			multiplier = _BYTES_PER_KILOBYTE;
		}
		else if (sizeString.endsWith("m") || sizeString.endsWith("mb")) {
			multiplier = _BYTES_PER_MEGABYTE;
		}
		else if (sizeString.endsWith("g") || sizeString.endsWith("gb")) {
			multiplier = _BYTES_PER_GIGABYTE;
		}

		if (multiplier > 1) {
			sizeString = StringUtil.extractDigits(sizeString);
		}

		return GetterUtil.getLong(sizeString) * multiplier;
	}

	private static double _toUnit(long bytes, long bytesPerUnit) {
		return (double)bytes / bytesPerUnit;
	}

	private static final long _BYTES_PER_GIGABYTE = 1024 * 1024 * 1024;

	private static final long _BYTES_PER_KILOBYTE = 1024;

	private static final long _BYTES_PER_MEGABYTE = 1024 * 1024;

	private static final String _CATEGORY_JVM_AND_INFRASTRUCTURE_VALIDATION =
		"jvm-and-infrastructure-validation";

	private static final String _CATEGORY_PORTAL_PROPERTIES_CONFIGURATION =
		"portal-properties-configuration";

	private static final int _MAX_DL_IMAGE_PREVIEW_DPI = 75;

	private static final int _MIN_COUNTER_INCREMENT = 2000;

	private static final int _MIN_PBKDF2_ROUNDS = 1300000;

	private static final String _PREFIX_HUGEPAGESIZE = "Hugepagesize:";

	private static final String _PREFIX_LARGE_PAGE_SIZE_IN_BYTES =
		"-XX:LargePageSizeInBytes=";

	private static final Log _log = LogFactoryUtil.getLog(
		ProductionReadinessCheckUtil.class);

	private static final List<Supplier<ProductionReadinessResult>>
		_productionReadinessResultSuppliers = List.of(
			ProductionReadinessCheckUtil::_checkCounterIncrement,
			ProductionReadinessCheckUtil::_checkDLImagePreviewDPI,
			ProductionReadinessCheckUtil::_checkDLPreviewForking,
			ProductionReadinessCheckUtil::_checkExplicitGCDisabled,
			ProductionReadinessCheckUtil::_checkFileStoreImplementation,
			ProductionReadinessCheckUtil::_checkGarbageCollectorType,
			ProductionReadinessCheckUtil::_checkHeapAllocationConsistency,
			ProductionReadinessCheckUtil::_checkHeapSizeUpperLimit,
			ProductionReadinessCheckUtil::_checkHugePagesConfiguration,
			ProductionReadinessCheckUtil::_checkJMXConfigurationDisabled,
			ProductionReadinessCheckUtil::_checkJSPEngineSettings,
			ProductionReadinessCheckUtil::_checkJSPReloading,
			ProductionReadinessCheckUtil::_checkLocalesBeta,
			ProductionReadinessCheckUtil::_checkLocalesUnused,
			ProductionReadinessCheckUtil::_checkPasswordEncryption,
			ProductionReadinessCheckUtil::_checkPoolVsThreadSize,
			ProductionReadinessCheckUtil::_checkPortalDeveloperProperties,
			ProductionReadinessCheckUtil::_checkPreventDiagnosticOverhead,
			ProductionReadinessCheckUtil::_checkSidecarDetection);
	private static final List<String> _recommendedDLStoreImplClassNames =
		List.of(
			"com.liferay.portal.store.azure.AzureStore",
			"com.liferay.portal.store.file.system.AdvancedFileSystemStore",
			"com.liferay.portal.store.gcs.GCSStore",
			"com.liferay.portal.store.s3.IBMS3Store",
			"com.liferay.portal.store.s3.S3Store");
	private static final Snapshot<SearchEngineInformation>
		_searchEngineInformationSnapshot = new Snapshot<>(
			ProductionReadinessCheckUtil.class, SearchEngineInformation.class,
			null, true);

}