/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.counter.test.rule.HypersonicServerClassTestRule;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cache.key.SimpleCacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PortalClassPathUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.lang.management.ManagementFactory;

import java.net.URL;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CounterLocalServiceProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			false, new LiferayIntegrationTestRule(),
			new ClassTestRule<Void>() {

				@Override
				public void afterClass(Description description, Void v) {
					CounterLocalServiceUtil.reset(_COUNTER_NAME);
				}

				@Override
				public Void beforeClass(Description description)
					throws Exception {

					CounterLocalServiceUtil.reset(_COUNTER_NAME);

					CounterLocalServiceUtil.reset(_COUNTER_NAME, 0);

					MBeanServer mBeanServer =
						ManagementFactory.getPlatformMBeanServer();

					// HikariCP

					for (ObjectName objectName :
							mBeanServer.queryNames(
								null,
								new ObjectName(
									"com.zaxxer.hikari:type=Pool (*"))) {

						mBeanServer.invoke(
							objectName, "softEvictConnections", null, null);
					}

					return null;
				}

			},
			HypersonicServerClassTestRule.INSTANCE);

	@Test
	public void testConcurrentIncrement() throws Exception {
		List<String> arguments = new ArrayList<>();

		arguments.add("-Dsun.zip.disableMemoryMapping=true");

		for (String property :
				HypersonicServerClassTestRule.INSTANCE.
					getTestServerJdbcProperties()) {

			arguments.add("-D" + property);
		}

		arguments.add("-Xmx1024m");
		arguments.add("-XX:MaxMetaspaceSize=200m");

		ProcessConfig portalProcessConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(arguments);
		builder.setBootstrapClassPath(
			portalProcessConfig.getBootstrapClassPath());
		builder.setReactClassLoader(PortalClassLoaderUtil.getClassLoader());
		builder.setRuntimeClassPath(
			_prependClassPath(
				portalProcessConfig.getRuntimeClassPath(),
				CounterLocalServiceProcessTest.class,
				LiferayIntegrationTestRule.class));

		ProcessConfig processConfig = builder.build();

		List<Future<Long[]>> futures = new ArrayList<>();

		for (int i = 0; i < _PROCESS_COUNT; i++) {
			ProcessCallable<Long[]> processCallable =
				new IncrementProcessCallable(
					"Increment Process-" + i,
					SystemProperties.get("catalina.base"), _COUNTER_NAME,
					_INCREMENT_COUNT);

			ProcessChannel<Long[]> processChannel = _processExecutor.execute(
				processConfig, processCallable);

			futures.add(processChannel.getProcessNoticeableFuture());
		}

		int total = _PROCESS_COUNT * _INCREMENT_COUNT;

		List<Long> ids = new ArrayList<>(total);

		for (Future<Long[]> future : futures) {
			Collections.addAll(ids, future.get());
		}

		Assert.assertEquals(ids.toString(), total, ids.size());

		Collections.sort(ids);

		for (int i = 0; i < total; i++) {
			Long id = ids.get(i);

			Assert.assertEquals(
				i + 1 + PropsValues.COUNTER_INCREMENT, id.intValue());
		}
	}

	private String _prependClassPath(String baseClassPath, Class<?>... classes)
		throws Exception {

		StringBundler sb = new StringBundler((classes.length * 2) + 1);

		for (Class<?> clazz : classes) {
			ProtectionDomain protectionDomain = clazz.getProtectionDomain();

			CodeSource codeSource = protectionDomain.getCodeSource();

			URL url = codeSource.getLocation();

			File file = new File(url.toURI());

			sb.append(file.getPath());

			sb.append(File.pathSeparator);
		}

		sb.append(baseClassPath);

		return sb.toString();
	}

	private static final String _COUNTER_NAME =
		CounterLocalServiceProcessTest.class.getName();

	private static final int _INCREMENT_COUNT = 10000;

	private static final int _PROCESS_COUNT = 4;

	@Inject
	private static final ProcessExecutor _processExecutor = null;

	private static class IncrementProcessCallable
		implements ProcessCallable<Long[]> {

		public IncrementProcessCallable(
			String processName, String catalinaBase, String counterName,
			int incrementCount) {

			_processName = processName;
			_catalinaBase = catalinaBase;
			_counterName = counterName;
			_incrementCount = incrementCount;
		}

		@Override
		public Long[] call() throws ProcessException {
			System.setProperty(
				PropsKeys.COUNTER_INCREMENT + "." + _counterName, "1");
			System.setProperty("catalina.base", _catalinaBase);
			System.setProperty("portal:hibernate.hbm.jaxb.cache", "false");

			// HikariCP

			System.setProperty("portal:jdbc.default.maximumPoolSize", "1");
			System.setProperty("portal:jdbc.default.minimumIdle", "0");

			CacheKeyGeneratorUtil cacheKeyGeneratorUtil =
				new CacheKeyGeneratorUtil();

			cacheKeyGeneratorUtil.setDefaultCacheKeyGenerator(
				new SimpleCacheKeyGenerator());

			InitUtil.initWithSpring(
				Arrays.asList(
					"META-INF/base-spring.xml", "META-INF/counter-spring.xml",
					"META-INF/infrastructure-spring.xml",
					"META-INF/hibernate-spring.xml"),
				false, false, null);

			List<Long> ids = new ArrayList<>();

			try {
				for (int i = 0; i < _incrementCount; i++) {
					ids.add(CounterLocalServiceUtil.increment(_counterName));
				}
			}
			catch (SystemException systemException) {
				throw new ProcessException(systemException);
			}

			return ids.toArray(new Long[0]);
		}

		@Override
		public String toString() {
			return _processName;
		}

		private static final long serialVersionUID = 1L;

		private final String _catalinaBase;
		private final String _counterName;
		private final int _incrementCount;
		private final String _processName;

	}

}