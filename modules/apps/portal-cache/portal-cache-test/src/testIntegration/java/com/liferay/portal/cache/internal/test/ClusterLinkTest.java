/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import java.net.InetAddress;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class ClusterLinkTest implements Serializable {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@ClassRule
	public static final TomcatClusterTestRule tomcatClusterTestRule =
		new TomcatClusterTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		TomcatCluster.Builder builder1 =
			tomcatClusterTestRule.buildTomcatNode();

		_tomcatNode1 = builder1.build();

		_tomcatNode1.start(true);

		TomcatCluster.Builder builder2 =
			tomcatClusterTestRule.buildTomcatNode();

		_tomcatNode2 = builder2.build();

		_tomcatNode2.start(true);
	}

	@Test
	public void testAutodetectClusteringAddress() throws Exception {
		try (Closeable closeable = _applyPortalExtPropertiesLines(
				true, _tomcatNode1, "cluster.link.autodetect.address=")) {

			InetAddress bindInetAddress = _tomcatNode1.syncExecute(
				ClusterLinkTest::_getControlChannelBindAddress);

			Assert.assertTrue(bindInetAddress.isLoopbackAddress());
		}
	}

	@Test
	public void testChannelProperties() throws Exception {
		_testChannelProperties(
			false, "TCP", ClusterLinkTest::_getControlChannelTransportName,
			PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL + "=tcp.xml");
		_testChannelProperties(
			true, "UDP", ClusterLinkTest::_getControlChannelTransportName,
			PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL + "=udp.xml");
		_testChannelProperties(
			false, "TCP", ClusterLinkTest::_getTransportChannelTransportName,
			"cluster.link.channel.properties.transport.0=tcp.xml");
		_testChannelProperties(
			true, "UDP", ClusterLinkTest::_getTransportChannelTransportName,
			"cluster.link.channel.properties.transport.0=udp.xml");
	}

	@Test
	public void testCustomClusteringAddress() throws Exception {
		String ipAddress = "127.0.0.1";

		try (Closeable closeable = _applyPortalExtPropertiesLines(
				true, _tomcatNode1, "cluster.link.autodetect.address=",
				"cluster.link.bind.addr[\"cluster-link-control\"]=" +
					ipAddress)) {

			InetAddress bindInetAddress = _tomcatNode1.syncExecute(
				ClusterLinkTest::_getControlChannelBindAddress);

			Assert.assertEquals(ipAddress, bindInetAddress.getHostAddress());
		}
	}

	@Test
	public void testCustomizeChannelNames() throws Exception {
		ClusterNode clusterNode1 = _tomcatNode1.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		ClusterNode clusterNode2 = _tomcatNode2.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		_testCustomizeChannelNames(true, clusterNode1, clusterNode2);

		_tomcatNode1.syncExecute(
			() -> {
				_logCapture = LoggerTestUtil.configureLog4JLogger(
					"org.jgroups.protocols.SYM_ENCRYPT", LoggerTestUtil.ERROR);

				return null;
			});

		try (Closeable closeable = _applyPortalExtPropertiesLines(
				true, _tomcatNode2,
				PropsKeys.CLUSTER_LINK_CHANNEL_NAME_CONTROL +
					"=different-control-channel",
				PropsKeys.CLUSTER_LINK_CHANNEL_NAME_TRANSPORT +
					".0=different-transport-channel")) {

			ClusterNode isolatedClusterNode2 = _tomcatNode2.syncExecute(
				ClusterExecutorUtil::getLocalClusterNode);

			_testCustomizeChannelNames(
				false, clusterNode1, isolatedClusterNode2);
		}

		List<String> messages = _tomcatNode1.syncExecute(
			() -> {
				try (LogCapture logCapture = _logCapture) {
					return new ArrayList<>(logCapture.getMessages());
				}
			});

		Assert.assertFalse(messages.isEmpty());

		for (String message : messages) {
			Assert.assertTrue(
				message,
				message.contains(
					"rejected decryption of multicast message from " +
						"non-member"));
		}

		ClusterNode reconnectedClusterNode2 = _tomcatNode2.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		_testCustomizeChannelNames(true, clusterNode1, reconnectedClusterNode2);
	}

	private static <S> String _getChannelTransportName(
		Class<S> clazz, Function<S, Object> clusterChannelExtractor) {

		return SystemBundleUtil.callService(
			clazz,
			service -> {
				Object clusterChannel = clusterChannelExtractor.apply(service);

				Object jChannel = ReflectionTestUtil.getFieldValue(
					clusterChannel, "_jChannel");

				Object protocolStack = ReflectionTestUtil.invoke(
					jChannel, "getProtocolStack", new Class<?>[0]);

				Object transport = ReflectionTestUtil.invoke(
					protocolStack, "getTransport", new Class<?>[0]);

				Class<?> transportClass = transport.getClass();

				return transportClass.getSimpleName();
			});
	}

	private static InetAddress _getControlChannelBindAddress() {
		return SystemBundleUtil.callService(
			ClusterExecutor.class,
			clusterExecutor -> {
				Object clusterChannel = ReflectionTestUtil.getFieldValue(
					clusterExecutor, "_clusterChannel");

				return ReflectionTestUtil.invoke(
					clusterChannel, "getBindInetAddress", new Class<?>[0]);
			});
	}

	private static String _getControlChannelTransportName() {
		return _getChannelTransportName(
			ClusterExecutor.class,
			clusterExecutor -> ReflectionTestUtil.getFieldValue(
				clusterExecutor, "_clusterChannel"));
	}

	private static String _getTransportChannelTransportName() {
		return _getChannelTransportName(
			ClusterLink.class,
			clusterLink -> {
				List<?> clusterChannels = ReflectionTestUtil.getFieldValue(
					clusterLink, "_clusterChannels");

				return clusterChannels.get(0);
			});
	}

	private Closeable _applyPortalExtPropertiesLines(
			boolean keepStarted, TomcatNode tomcatNode,
			String... portalExtPropertiesLines)
		throws Exception {

		tomcatNode.stop();

		Path path = tomcatNode.getPortalExtPropertiesPath();

		byte[] bytes = Files.readAllBytes(path);

		Files.write(
			path, Arrays.asList(portalExtPropertiesLines),
			StandardOpenOption.APPEND);

		tomcatNode.start(true);

		return () -> {
			try {
				tomcatNode.stop();

				Files.write(
					path, bytes, StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE);

				if (keepStarted) {
					tomcatNode.start(true);
				}
			}
			catch (Exception exception) {
				throw new IOException(exception);
			}
		};
	}

	private void _testChannelProperties(
			boolean keepStarted, String expectedTransportName,
			TomcatNode.ClusterExecutable<String> clusterExecutable,
			String... portalExtPropertiesLines)
		throws Exception {

		try (Closeable closeable = _applyPortalExtPropertiesLines(
				keepStarted, _tomcatNode1, portalExtPropertiesLines)) {

			// Assert portal-ext.properties lines are set correctly on node 1

			for (String portalExtLine : portalExtPropertiesLines) {
				List<String> parts = StringUtil.split(
					portalExtLine, CharPool.EQUAL);

				String key = parts.get(0);
				String expectedValue = parts.get(1);

				Assert.assertEquals(
					expectedValue,
					_tomcatNode1.syncExecute(() -> PropsUtil.get(key)));
			}

			// Assert node 1 can get its cluster node successfully

			Assert.assertNotNull(
				_tomcatNode1.syncExecute(
					ClusterExecutorUtil::getLocalClusterNode));

			Assert.assertEquals(
				expectedTransportName,
				_tomcatNode1.syncExecute(clusterExecutable));
		}
	}

	private void _testCustomizeChannelNames(
			boolean visible, ClusterNode clusterNode1, ClusterNode clusterNode2)
		throws Exception {

		List<ClusterNode> clusterNodes = _tomcatNode1.syncExecute(
			() -> new ArrayList<>(ClusterExecutorUtil.getClusterNodes()));

		Assert.assertEquals(visible, clusterNodes.contains(clusterNode2));

		clusterNodes = _tomcatNode2.syncExecute(
			() -> new ArrayList<>(ClusterExecutorUtil.getClusterNodes()));

		Assert.assertEquals(visible, clusterNodes.contains(clusterNode1));
	}

	private static transient LogCapture _logCapture;
	private static transient TomcatNode _tomcatNode1;
	private static transient TomcatNode _tomcatNode2;

}