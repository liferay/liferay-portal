/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;

/**
 * @author Dante Wang
 * @author Jiefeng Wu
 */
public abstract class BaseClusterLicenseTestCase extends BaseLicenseTestCase {

	@ClassRule
	public static final TomcatClusterTestRule tomcatClusterTestRule =
		new TomcatClusterTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalSystemErrPrintStream = System.err;
		_originalSystemOutPrintStream = System.out;

		System.setErr(
			new TestPrintStream(System.err, testConsoleMessageListener));
		System.setOut(
			new TestPrintStream(System.out, testConsoleMessageListener));
	}

	@AfterClass
	public static void tearDownClass() {
		_backgroundTomcatNodes.clear();

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

			if (_backgroundTomcatNodes.contains(tomcatNode)) {
				continue;
			}

			tomcatNode.stop();

			tomcatNode.destroy();

			iterator.remove();
		}
	}

	protected static void registerBackgroundTomcatNode(TomcatNode tomcatNode) {
		_backgroundTomcatNodes.add(tomcatNode);
	}

	@SafeVarargs
	protected static TomcatNode startTomcatNode(
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

	protected void assertPortalLicenseRegistered(TomcatNode tomcatNode)
		throws Exception {

		tomcatNode.syncExecute(
			() -> {
				assertPortalLicenseRegistered();

				return null;
			});
	}

	protected Serializable deployAndAssertClusterLicense(
			TomcatNode.ClusterExecutable<Serializable>
				deployLicenseClusterExecutable)
		throws Throwable {

		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		deployLicenseClusterExecutable.execute();

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		return null;
	}

	protected TomcatNode.ClusterExecutable<Serializable> getClusterExecutable(
		long timestamp) {

		return () -> {
			Field field = findField("grace.period.end.field");

			field.setAccessible(true);

			field.setLong(
				null, timestamp + ((NODE_SHUTDOWN_MINUTES - 1) * Time.MINUTE));

			return null;
		};
	}

	protected String getNodeExceededConsoleKey(int maxClusterNodes) {
		return StringBundler.concat(
			"The maximum number of ", maxClusterNodes,
			" nodes licensed for this cluster has been exceeded");
	}

	protected long getTimeStamp() throws Exception {
		Method method = findMethod("timestamp.method");

		return (long)method.invoke(null);
	}

	protected void testClusterLicense(
			TomcatNode.ClusterExecutable<Serializable>
				deployLicenseClusterExecutable,
			String exceededLimitPageKey, int maxClusterNodes)
		throws Exception {

		TomcatNode tomcatNode1 = startTomcatNode(true);

		tomcatNode1.syncExecute(
			() -> deployAndAssertClusterLicense(
				deployLicenseClusterExecutable));

		Future<String> messageFuture1 = testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), CONSOLE_KEY_LICENSED_NODE,
			getNodeExceededConsoleKey(maxClusterNodes));

		List<TomcatNode> temporaryTomcatNodes = new ArrayList<>();

		TomcatNode.ClusterExecutable<Serializable> clusterExecutable =
			getClusterExecutable(tomcatNode1.syncExecute(this::getTimeStamp));

		for (int i = 0; i < maxClusterNodes; i++) {
			temporaryTomcatNodes.add(startTomcatNode(true, clusterExecutable));
		}

		testConsoleMessageListener.assertMessageListened(messageFuture1);

		messageFuture1 = testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

		for (TomcatNode temporaryTomcatNode : temporaryTomcatNodes) {
			Future<String> messageFuture = testConsoleMessageListener.register(
				temporaryTomcatNode.getNodeId(), CONSOLE_KEY_TEMPORARY_NODE,
				getNodeExceededConsoleKey(maxClusterNodes));

			temporaryTomcatNode.syncExecute(
				() -> deployAndAssertClusterLicense(
					deployLicenseClusterExecutable));

			testConsoleMessageListener.assertMessageListened(messageFuture);
		}

		TomcatNode beyondTomcatNode = startTomcatNode(true, clusterExecutable);

		Future<String> messageFuture2 = testConsoleMessageListener.register(
			beyondTomcatNode.getNodeId(), _CONSOLE_KEY_BEYOND_TEMPORARY_NODE,
			getNodeExceededConsoleKey(maxClusterNodes));

		beyondTomcatNode.syncExecute(
			() -> {
				assertLicensePropertiesNotExisted(getPortalProductId());

				assertPortalLicenseNotRegistered();

				deployLicenseClusterExecutable.execute();

				String response = hitHomePage("localhost", getLocalPort());

				Assert.assertTrue(response.contains(exceededLimitPageKey));

				return null;
			});

		testConsoleMessageListener.assertMessageListened(messageFuture2);

		for (TomcatNode temporaryTomcatNode : temporaryTomcatNodes) {
			temporaryTomcatNode.wait(NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);
		}

		beyondTomcatNode.wait(NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

		testConsoleMessageListener.assertMessageListened(messageFuture1);
	}

	protected void testLimitPreviouslyValidatedClusterNode(
			TomcatNode.ClusterExecutable<Serializable>
				deployLicenseClusterExecutable,
			int maxClusterNodes, boolean overloadNodeAutoShutdown)
		throws Exception {

		TomcatNode tomcatNode1 = startTomcatNode(overloadNodeAutoShutdown);

		tomcatNode1.syncExecute(
			() -> deployAndAssertClusterLicense(
				deployLicenseClusterExecutable));

		tomcatNode1.syncExecute(this::_writeBinaryLicense);

		tomcatNode1.stop();

		TomcatNode tomcatNode2 = startTomcatNode(true);

		tomcatNode2.syncExecute(
			() -> deployAndAssertClusterLicense(
				deployLicenseClusterExecutable));

		Future<String> messageFuture2 = testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), CONSOLE_KEY_LICENSED_NODE,
			getNodeExceededConsoleKey(maxClusterNodes));

		String temporaryNodeConsoleKey = _CONSOLE_KEY_TEMPORARY_NODE_MANUAL;

		if (overloadNodeAutoShutdown) {
			temporaryNodeConsoleKey = CONSOLE_KEY_TEMPORARY_NODE;
		}

		Future<String> messageFuture1 = testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), temporaryNodeConsoleKey,
			getNodeExceededConsoleKey(maxClusterNodes));

		_restartTomcatNode(
			tomcatNode1,
			getClusterExecutable(tomcatNode2.syncExecute(this::getTimeStamp)));

		testConsoleMessageListener.assertMessageListened(messageFuture1);
		testConsoleMessageListener.assertMessageListened(messageFuture2);

		messageFuture2 = testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

		if (overloadNodeAutoShutdown) {
			tomcatNode1.wait(NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);
		}
		else {
			try {
				tomcatNode1.wait(NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertTrue(exception instanceof TimeoutException);
			}

			tomcatNode1.stop();
		}

		testConsoleMessageListener.assertMessageListened(messageFuture2);

		assertPortalLicenseRegistered(tomcatNode2);
	}

	protected void testRecoverCluster(
			TomcatNode.ClusterExecutable<Serializable>
				deployLicenseClusterExecutable,
			String exceededLimitPageKey, int maxClusterNodes,
			boolean shutdownLicensedNode)
		throws Exception {

		TomcatNode tomcatNode1 = startTomcatNode(true);

		tomcatNode1.syncExecute(
			() -> deployAndAssertClusterLicense(
				deployLicenseClusterExecutable));

		Future<String> messageFuture1 = testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), CONSOLE_KEY_LICENSED_NODE,
			getNodeExceededConsoleKey(maxClusterNodes));

		TomcatNode tomcatNode2 = startTomcatNode(
			false,
			getClusterExecutable(tomcatNode1.syncExecute(this::getTimeStamp)));

		Future<String> messageFuture2 = testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE_MANUAL,
			getNodeExceededConsoleKey(maxClusterNodes));

		tomcatNode2.syncExecute(
			() -> deployAndAssertClusterLicense(
				deployLicenseClusterExecutable));

		testConsoleMessageListener.assertMessageListened(messageFuture1);
		testConsoleMessageListener.assertMessageListened(messageFuture2);

		try {
			tomcatNode2.wait(NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof TimeoutException);
		}

		if (shutdownLicensedNode) {
			messageFuture2 = testConsoleMessageListener.register(
				tomcatNode2.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

			tomcatNode1.stop();

			testConsoleMessageListener.assertMessageListened(messageFuture2);

			assertPortalLicenseRegistered(tomcatNode2);
		}
		else {
			tomcatNode2.syncExecute(
				() -> {
					String response = hitHomePage("localhost", getLocalPort());

					Assert.assertTrue(response.contains(exceededLimitPageKey));

					return null;
				});

			messageFuture1 = testConsoleMessageListener.register(
				tomcatNode1.getNodeId(), _CONSOLE_KEY_FINISHED_SHUTDOWN);

			tomcatNode2.stop();

			testConsoleMessageListener.assertMessageListened(messageFuture1);

			assertPortalLicenseRegistered(tomcatNode1);
		}
	}

	protected static final String CONSOLE_KEY_LICENSED_NODE =
		"This current node is within the licensed node count and will not be " +
			"automatically deactivated nor shut down after the grace period " +
				"expires";

	protected static final String CONSOLE_KEY_TEMPORARY_NODE =
		"This current node is within the temporarily permitted node count " +
			"and will be automatically deactivated and shut down after the " +
				"grace period expires";

	protected static final long NODE_SHUTDOWN_MINUTES = 6L;

	protected static final TestConsoleMessageListener
		testConsoleMessageListener = new TestConsoleMessageListener();

	protected static class TestConsoleMessageListener {

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

				ReflectionTestUtil.setFieldValue(
					LicenseUtil.class, "LICENSE_REPOSITORY_DIR", path);

				return null;
			});

		for (TomcatNode.ClusterExecutable<Serializable> clusterExecutable :
				additionalClusterExecutables) {

			tomcatNode.syncExecute(clusterExecutable);
		}
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

	private static final String _CONSOLE_KEY_TEMPORARY_NODE_MANUAL =
		"This current node is within the temporarily permitted node count " +
			"and will be automatically deactivated after the grace period " +
				"expires";

	private static final Set<TomcatNode> _backgroundTomcatNodes =
		new CopyOnWriteArraySet<>();
	private static PrintStream _originalSystemErrPrintStream;
	private static PrintStream _originalSystemOutPrintStream;

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