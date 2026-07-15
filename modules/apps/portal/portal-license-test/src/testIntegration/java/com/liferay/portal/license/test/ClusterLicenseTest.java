/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.util.LicenseUtil;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 * @author Jiefeng Wu
 */
@Ignore
@RunWith(Arquillian.class)
public class ClusterLicenseTest extends BaseLicenseTestCase {

	@ClassRule
	public static final TomcatClusterTestRule tomcatClusterTestRule =
		new TomcatClusterTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalSystemErrPrintStream = System.err;
		_originalSystemOutPrintStream = System.out;

		System.setErr(
			new TestPrintStream(System.err, _testConsoleMessageListener));
		System.setOut(
			new TestPrintStream(System.out, _testConsoleMessageListener));

		TomcatNode.ClusterExecutable<Serializable> clusterExecutable =
			() -> deployFreeTierPortalLicense(Time.HOUR);

		_backgroundTomcatNode1 = _startTomcatNode(true, clusterExecutable);
		_backgroundTomcatNode2 = _startTomcatNode(true, clusterExecutable);
	}

	@AfterClass
	public static void tearDownClass() {
		System.setErr(_originalSystemErrPrintStream);
		System.setOut(_originalSystemOutPrintStream);
	}

	@After
	public void tearDown() throws Exception {
		TomcatCluster tomcatCluster = tomcatClusterTestRule.getTomcatCluster();

		List<TomcatNode> tomcatNodes = tomcatCluster.getTomcatNodes();

		Iterator<TomcatNode> iterator = tomcatNodes.iterator();

		while (iterator.hasNext()) {
			TomcatNode tomcatNode = iterator.next();

			if ((tomcatNode == _backgroundTomcatNode1) ||
				(tomcatNode == _backgroundTomcatNode2)) {

				continue;
			}

			tomcatNode.stop();

			tomcatNode.destroy();

			iterator.remove();
		}
	}

	@Test
	public void testEnterpriseLicense() throws Exception {
		TomcatNode tomcatNode1 = _startTomcatNode(true);

		tomcatNode1.syncExecute(this::_testFreeTierLicense);

		Future<String> messageFuture1 = _testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_LICENSED_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		TomcatNode tomcatNode2 = _startTomcatNode(
			true,
			_getClusterExecutable(
				tomcatNode1.syncExecute(this::_getTimeStamp)));

		Future<String> messageFuture2 = _testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		tomcatNode2.syncExecute(this::_testFreeTierLicense);

		_testConsoleMessageListener.assertMessageListened(messageFuture1);
		_testConsoleMessageListener.assertMessageListened(messageFuture2);

		tomcatNode1.syncExecute(this::_deployEnterpriseLicense);
		tomcatNode2.syncExecute(this::_deployEnterpriseLicense);

		try {
			tomcatNode2.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof TimeoutException);
		}

		tomcatNode1.syncExecute(this::_assertPortalLicenseRegistered);
		tomcatNode2.syncExecute(this::_assertPortalLicenseRegistered);
	}

	@Test
	public void testFreeTierLicense() throws Exception {
		TomcatNode tomcatNode1 = _startTomcatNode(true);

		tomcatNode1.syncExecute(this::_testFreeTierLicense);

		Future<String> messageFuture1 = _testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_LICENSED_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		TomcatNode.ClusterExecutable<Serializable> clusterExecutable =
			_getClusterExecutable(tomcatNode1.syncExecute(this::_getTimeStamp));

		TomcatNode tomcatNode2 = _startTomcatNode(true, clusterExecutable);
		TomcatNode tomcatNode3 = _startTomcatNode(true, clusterExecutable);
		TomcatNode tomcatNode4 = _startTomcatNode(true, clusterExecutable);

		_testConsoleMessageListener.assertMessageListened(messageFuture1);

		messageFuture1 = _testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

		Future<String> messageFuture2 = _testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		tomcatNode2.syncExecute(this::_testFreeTierLicense);

		_testConsoleMessageListener.assertMessageListened(messageFuture2);

		Future<String> messageFuture3 = _testConsoleMessageListener.register(
			tomcatNode3.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		tomcatNode3.syncExecute(this::_testFreeTierLicense);

		_testConsoleMessageListener.assertMessageListened(messageFuture3);

		Future<String> messageFuture4 = _testConsoleMessageListener.register(
			tomcatNode4.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		tomcatNode4.syncExecute(this::_testFreeTierLicense);

		_testConsoleMessageListener.assertMessageListened(messageFuture4);

		TomcatNode tomcatNode5 = _startTomcatNode(true, clusterExecutable);

		Future<String> messageFuture5 = _testConsoleMessageListener.register(
			tomcatNode5.getNodeId(), _CONSOLE_KEY_BEYOND_TEMPORARY_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		tomcatNode5.syncExecute(
			() -> {
				assertLicensePropertiesNotExisted(getPortalProductId());

				assertPortalLicenseNotRegistered();

				deployFreeTierPortalLicense(Time.HOUR);

				String response = hitHomePage("localhost", getLocalPort());

				Assert.assertTrue(response.contains(_PAGE_KEY_EXCEEDED_LIMIT));

				return null;
			});

		_testConsoleMessageListener.assertMessageListened(messageFuture5);

		tomcatNode2.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);
		tomcatNode3.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);
		tomcatNode4.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);
		tomcatNode5.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

		_testConsoleMessageListener.assertMessageListened(messageFuture1);
	}

	@Test
	public void testLimitPreviouslyValidatedClusterNode() throws Exception {
		_testLimitPreviouslyValidatedClusterNode(true);
	}

	@Test
	public void testLimitPreviouslyValidatedClusterNodeWithManualRecovery()
		throws Exception {

		_testLimitPreviouslyValidatedClusterNode(false);
	}

	@Test
	public void testRecoverCluster() throws Exception {
		_testRecoverCluster(false);
	}

	@Test
	public void testRecoverClusterWithLicensedNodeShutdown() throws Exception {
		_testRecoverCluster(true);
	}

	@SafeVarargs
	private static void _restartTomcatNode(
			TomcatNode tomcatNode,
			TomcatNode.ClusterExecutable<Serializable>...
				additionalClusterExecutables)
		throws Exception {

		tomcatNode.start(true);

		String path = tomcatNode.getLiferayHome(
		).concat(
			"/data/license"
		);

		tomcatNode.syncExecute(
			() -> {
				disableValidateWithSafeCloseable();
				setVersionWithSafeCloseable("2026.Q1.0 LTS");

				ReflectionTestUtil.setFieldValue(
					LicenseUtil.class, "LICENSE_REPOSITORY_DIR", path);

				return null;
			});

		for (TomcatNode.ClusterExecutable<Serializable> clusterExecutable :
				additionalClusterExecutables) {

			tomcatNode.syncExecute(clusterExecutable);
		}
	}

	@SafeVarargs
	private static TomcatNode _startTomcatNode(
			boolean overloadNodeAutoShutdown,
			TomcatNode.ClusterExecutable<Serializable>...
				additionalClusterExecutables)
		throws Exception {

		TomcatCluster.Builder builder = tomcatClusterTestRule.buildTomcatNode();

		TomcatNode tomcatNode = builder.build();

		List<String> portalExtProperties = List.of(
			"configuration.override.com.liferay.captcha.configuration." +
				"CaptchaConfiguration_createAccountCaptchaEnabled=\"false\"",
			"configuration.override.com.liferay.captcha.configuration." +
				"CaptchaConfiguration_maxChallenges=I\"-1\"",
			"configuration.override.com.liferay.captcha.configuration." +
				"CaptchaConfiguration_sendPasswordCaptchaEnabled=\"false\"",
			"license.cluster.overload.node.auto.shutdown=" +
				overloadNodeAutoShutdown,
			"virtual.hosts.default.site.name=Guest");

		Files.write(
			tomcatNode.getPortalExtPropertiesPath(), portalExtProperties,
			StandardOpenOption.APPEND);

		_restartTomcatNode(tomcatNode, additionalClusterExecutables);

		return tomcatNode;
	}

	private Serializable _assertPortalLicenseRegistered() throws Exception {
		assertPortalLicenseRegistered();

		return null;
	}

	private Serializable _deployEnterpriseLicense() throws Exception {
		deployEnterprisePortalLicense(Time.HOUR);

		return null;
	}

	private String _encryptLicenseProperties(Map<String, String> properties)
		throws Exception {

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			getProperty("key.generator.class"));

		Method encryptMethod = null;

		for (Method method : clazz.getDeclaredMethods()) {
			Class<?>[] parameterTypes = method.getParameterTypes();

			if ((parameterTypes.length == 1) &&
				Map.class.isAssignableFrom(parameterTypes[0])) {

				method.setAccessible(true);

				encryptMethod = method;

				break;
			}
		}

		return (String)encryptMethod.invoke(null, properties);
	}

	private TomcatNode.ClusterExecutable<Serializable> _getClusterExecutable(
		long timestamp) {

		return () -> {
			Field field = findField("grace.period.end.field");

			field.setAccessible(true);

			field.setLong(
				null, timestamp + ((_NODE_SHUTDOWN_MINUTES - 1) * Time.MINUTE));

			return null;
		};
	}

	private long _getTimeStamp() throws Exception {
		Method method = findMethod("timestamp.method");

		return (long)method.invoke(null);
	}

	private Serializable _testFreeTierLicense() throws Exception {
		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		deployFreeTierPortalLicense(Time.HOUR);

		assertLicensePropertiesExisted(getPortalProductId());

		return _assertPortalLicenseRegistered();
	}

	private void _testLimitPreviouslyValidatedClusterNode(
			boolean overloadNodeAutoShutdown)
		throws Exception {

		TomcatNode tomcatNode1 = _startTomcatNode(overloadNodeAutoShutdown);

		tomcatNode1.syncExecute(this::_testFreeTierLicense);
		tomcatNode1.syncExecute(this::_writeBinaryLicense);

		tomcatNode1.stop();

		TomcatNode tomcatNode2 = _startTomcatNode(true);

		tomcatNode2.syncExecute(this::_testFreeTierLicense);

		Future<String> messageFuture2 = _testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_LICENSED_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		String temporaryNodeConsoleKey = _CONSOLE_KEY_TEMPORARY_NODE_MANUAL;

		if (overloadNodeAutoShutdown) {
			temporaryNodeConsoleKey = _CONSOLE_KEY_TEMPORARY_NODE;
		}

		Future<String> messageFuture1 = _testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), temporaryNodeConsoleKey,
			_CONSOLE_KEY_NODE_EXCEEDED);

		_restartTomcatNode(
			tomcatNode1,
			_getClusterExecutable(
				tomcatNode2.syncExecute(this::_getTimeStamp)));

		_testConsoleMessageListener.assertMessageListened(messageFuture1);
		_testConsoleMessageListener.assertMessageListened(messageFuture2);

		messageFuture2 = _testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

		if (overloadNodeAutoShutdown) {
			tomcatNode1.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);
		}
		else {
			try {
				tomcatNode1.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertTrue(exception instanceof TimeoutException);
			}

			tomcatNode1.stop();
		}

		_testConsoleMessageListener.assertMessageListened(messageFuture2);

		tomcatNode2.syncExecute(this::_assertPortalLicenseRegistered);
	}

	private void _testRecoverCluster(boolean shutdownLicensedNode)
		throws Exception {

		TomcatNode tomcatNode1 = _startTomcatNode(true);

		tomcatNode1.syncExecute(this::_testFreeTierLicense);

		Future<String> messageFuture1 = _testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_LICENSED_NODE,
			_CONSOLE_KEY_NODE_EXCEEDED);

		TomcatNode tomcatNode2 = _startTomcatNode(
			false,
			_getClusterExecutable(
				tomcatNode1.syncExecute(this::_getTimeStamp)));

		Future<String> messageFuture2 = _testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE_MANUAL,
			_CONSOLE_KEY_NODE_EXCEEDED);

		tomcatNode2.syncExecute(this::_testFreeTierLicense);

		_testConsoleMessageListener.assertMessageListened(messageFuture1);
		_testConsoleMessageListener.assertMessageListened(messageFuture2);

		try {
			tomcatNode2.wait(_NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof TimeoutException);
		}

		if (shutdownLicensedNode) {
			messageFuture2 = _testConsoleMessageListener.register(
				tomcatNode2.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

			tomcatNode1.stop();

			_testConsoleMessageListener.assertMessageListened(messageFuture2);

			tomcatNode2.syncExecute(this::_assertPortalLicenseRegistered);
		}
		else {
			tomcatNode2.syncExecute(
				() -> {
					String response = hitHomePage("localhost", getLocalPort());

					Assert.assertTrue(
						response.contains(_PAGE_KEY_EXCEEDED_LIMIT));

					return null;
				});

			messageFuture1 = _testConsoleMessageListener.register(
				tomcatNode1.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

			tomcatNode2.stop();

			_testConsoleMessageListener.assertMessageListened(messageFuture1);

			tomcatNode1.syncExecute(this::_assertPortalLicenseRegistered);
		}
	}

	private Serializable _writeBinaryLicense() throws Exception {
		Method getLicenseMethod = findMethod("get.license.method");

		Object license = getLicenseMethod.invoke(null, getPortalProductId());

		Method setKeyMethod = findMethod("set.key.method");

		setKeyMethod.invoke(
			license,
			_encryptLicenseProperties(
				LicenseManagerUtil.getLicenseProperties(getPortalProductId())));

		Method writeBinaryLicenseMethod = findMethod(
			"write.binary.license.method");

		writeBinaryLicenseMethod.invoke(null, license);

		return null;
	}

	private static final String _CONSOLE_KEY_BEYOND_TEMPORARY_NODE =
		"This current node is beyond the temporarily permitted node count " +
			"and is deactivatedand will automatically shut down after the " +
				"grace period expires";

	private static final String _CONSOLE_KEY_FINISHED_SHUTDOWN =
		"Finished shutting down overloaded nodes";

	private static final String _CONSOLE_KEY_LICENSED_NODE =
		"This current node is within the licensed node count and will not be " +
			"automatically deactivated nor shut down after the grace period " +
				"expires";

	private static final String _CONSOLE_KEY_NODE_EXCEEDED =
		"The maximum number of 3 nodes licensed for this cluster has been " +
			"exceeded";

	private static final String _CONSOLE_KEY_TEMPORARY_NODE =
		"This current node is within the temporarily permitted node count " +
			"and will be automatically deactivated and shut down after the " +
				"grace period expires";

	private static final String _CONSOLE_KEY_TEMPORARY_NODE_MANUAL =
		"This current node is within the temporarily permitted node count " +
			"and will be automatically deactivated after the grace period " +
				"expires";

	private static final long _NODE_SHUTDOWN_MINUTES = 6L;

	private static final String _PAGE_KEY_EXCEEDED_LIMIT =
		"You have exceeded the developer mode connection limit";

	private static TomcatNode _backgroundTomcatNode1;
	private static TomcatNode _backgroundTomcatNode2;
	private static PrintStream _originalSystemErrPrintStream;
	private static PrintStream _originalSystemOutPrintStream;
	private static final TestConsoleMessageListener
		_testConsoleMessageListener = new TestConsoleMessageListener();

	private static class TestConsoleMessageListener {

		public void assertMessageListened(Future<String> future)
			throws Exception {

			Assert.assertNotNull(future.get(3L, TimeUnit.MINUTES));
		}

		public void onMessage(String message) {
			if (!message.startsWith(_PREFIX)) {
				return;
			}

			int index = message.indexOf(CharPool.CLOSE_BRACKET);

			if (index == -1) {
				return;
			}

			String nodeName = message.substring(0, index + 1);

			Map<String[], DefaultNoticeableFuture<String>>
				defaultNoticeableFutures = _defaultNoticeableFuturesMap.get(
					nodeName);

			if (defaultNoticeableFutures == null) {
				return;
			}

			Set<Map.Entry<String[], DefaultNoticeableFuture<String>>> entries =
				defaultNoticeableFutures.entrySet();

			entries.removeIf(
				entry -> {
					if (_hasKeyword(entry.getKey(), message)) {
						DefaultNoticeableFuture<String>
							defaultNoticeableFuture = entry.getValue();

						defaultNoticeableFuture.set(message);

						return true;
					}

					return false;
				});
		}

		public Future<String> register(int nodeId, String... keywords) {
			DefaultNoticeableFuture<String> defaultNoticeableFuture =
				new DefaultNoticeableFuture<>();

			Map<String[], DefaultNoticeableFuture<String>>
				defaultNoticeableFutures =
					_defaultNoticeableFuturesMap.computeIfAbsent(
						_getKey(nodeId), key -> new ConcurrentHashMap<>());

			defaultNoticeableFutures.put(keywords, defaultNoticeableFuture);

			return defaultNoticeableFuture;
		}

		private String _getKey(int nodeId) {
			return _PREFIX + nodeId + StringPool.CLOSE_BRACKET;
		}

		private boolean _hasKeyword(String[] keywords, String message) {
			for (String keyword : keywords) {
				if (!message.contains(keyword)) {
					return false;
				}
			}

			return true;
		}

		private static final String _PREFIX = "[TomcatNode-";

		private final Map
			<String, Map<String[], DefaultNoticeableFuture<String>>>
				_defaultNoticeableFuturesMap = new ConcurrentHashMap<>();

	}

	private static class TestPrintStream extends PrintStream {

		public TestPrintStream(
			OutputStream outputStream,
			TestConsoleMessageListener testConsoleMessageListener) {

			super(outputStream);

			_testConsoleMessageListener = testConsoleMessageListener;
		}

		@Override
		public void print(String message) {
			super.print(message);

			for (String line : StringUtil.split(message, CharPool.NEW_LINE)) {
				_testConsoleMessageListener.onMessage(line);
			}
		}

		private final TestConsoleMessageListener _testConsoleMessageListener;

	}

}