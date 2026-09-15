/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.security.Key;

import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class ClusterSymEncryptTest implements Serializable {

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
	public void test() throws Exception {
		_assertChannelProperties(_tomcatNode1);
		_assertChannelProperties(_tomcatNode2);

		byte[] controlChannelSecretKey = _tomcatNode1.syncExecute(
			ClusterSymEncryptTest::_getControlChannelSecretKey);
		byte[] transportChannelSecretKey = _tomcatNode1.syncExecute(
			ClusterSymEncryptTest::_getTransportChannelSecretKey);

		Assert.assertArrayEquals(
			_digest(
				_tomcatNode1.syncExecute(
					() -> PropsUtil.get(
						PropsKeys.CLUSTER_LINK_CHANNEL_NAME_CONTROL))),
			controlChannelSecretKey);
		Assert.assertArrayEquals(
			_digest(
				_tomcatNode1.syncExecute(
					() -> PropsUtil.get(
						PropsKeys.CLUSTER_LINK_CHANNEL_NAME_TRANSPORT + ".0"))),
			transportChannelSecretKey);
		Assert.assertArrayEquals(
			controlChannelSecretKey,
			_tomcatNode2.syncExecute(
				ClusterSymEncryptTest::_getControlChannelSecretKey));
		Assert.assertArrayEquals(
			transportChannelSecretKey,
			_tomcatNode2.syncExecute(
				ClusterSymEncryptTest::_getTransportChannelSecretKey));
	}

	private static byte[] _getControlChannelSecretKey() {
		return _getSecretKey(
			ClusterExecutor.class,
			clusterExecutor -> ReflectionTestUtil.getFieldValue(
				clusterExecutor, "_clusterChannel"));
	}

	private static <S> byte[] _getSecretKey(
		Class<S> clazz, Function<S, Object> clusterChannelFunction) {

		return SystemBundleUtil.callService(
			clazz,
			service -> {
				Object clusterChannel = clusterChannelFunction.apply(service);

				Object jChannel = ReflectionTestUtil.getFieldValue(
					clusterChannel, "_jChannel");

				Object protocolStack = ReflectionTestUtil.invoke(
					jChannel, "getProtocolStack", new Class<?>[0]);

				Object symEncrypt = ReflectionTestUtil.invoke(
					protocolStack, "findProtocol",
					new Class<?>[] {String.class}, "SYM_ENCRYPT");

				Key secretKey = ReflectionTestUtil.invoke(
					symEncrypt, "secretKey", new Class<?>[0]);

				return secretKey.getEncoded();
			});
	}

	private static byte[] _getTransportChannelSecretKey() {
		return _getSecretKey(
			ClusterLink.class,
			clusterLink -> {
				List<?> clusterChannels = ReflectionTestUtil.getFieldValue(
					clusterLink, "_clusterChannels");

				return clusterChannels.get(0);
			});
	}

	private void _assertChannelProperties(TomcatNode tomcatNode)
		throws Exception {

		Assert.assertEquals(
			"jgroups/secure/sym_encrypt/udp_control.xml",
			tomcatNode.syncExecute(
				() -> PropsUtil.get(
					PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL)));
		Assert.assertEquals(
			"jgroups/secure/sym_encrypt/udp_transport.xml",
			tomcatNode.syncExecute(
				() -> PropsUtil.get(
					PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT +
						".0")));
	}

	private byte[] _digest(String channelName) {
		return DigesterUtil.digestRaw(DigesterUtil.SHA_256, channelName);
	}

	private static transient TomcatNode _tomcatNode1;
	private static transient TomcatNode _tomcatNode2;

}