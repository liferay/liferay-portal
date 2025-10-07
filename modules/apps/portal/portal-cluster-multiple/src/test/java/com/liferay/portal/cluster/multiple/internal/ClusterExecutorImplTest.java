/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterNodeResponses;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class ClusterExecutorImplTest extends BaseClusterTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeactivate() {
		ClusterExecutorImpl clusterExecutorImpl = _getClusterExecutorImpl();

		List<TestClusterChannel> clusterChannels =
			TestClusterChannel.getClusterChannels();

		Assert.assertEquals(
			clusterChannels.toString(), 1, clusterChannels.size());

		TestClusterChannel clusterChannel = clusterChannels.get(0);

		ExecutorService executorService =
			clusterExecutorImpl.getExecutorService();

		Assert.assertFalse(executorService.isShutdown());

		Assert.assertFalse(clusterChannel.isClosed());

		clusterExecutorImpl.deactivate();

		Assert.assertTrue(clusterChannel.isClosed());
		Assert.assertTrue(executorService.isShutdown());
	}

	@Test
	public void testExecute() throws Exception {

		// Test 1, execute multicast request and not skip local

		ClusterExecutorImpl clusterExecutorImpl = _getClusterExecutorImpl();

		TestClusterChannel.clearAllMessages();

		List<Serializable> multicastMessages =
			TestClusterChannel.getMulticastMessages();
		List<ObjectValuePair<Serializable, Address>> unicastMessages =
			TestClusterChannel.getUnicastMessages();

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			StringPool.BLANK);

		FutureClusterResponses futureClusterResponses =
			clusterExecutorImpl.execute(clusterRequest);

		Assert.assertEquals(
			multicastMessages.toString(), 1, multicastMessages.size());
		Assert.assertTrue(
			multicastMessages.toString(),
			multicastMessages.contains(clusterRequest));
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		ClusterNodeResponses clusterNodeResponses =
			futureClusterResponses.get();

		Assert.assertEquals(1, clusterNodeResponses.size());

		// Test 2, execute multicast request and skip local

		TestClusterChannel.clearAllMessages();

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		clusterRequest = ClusterRequest.createMulticastRequest(
			StringPool.BLANK, true);

		futureClusterResponses = clusterExecutorImpl.execute(clusterRequest);

		Assert.assertEquals(
			multicastMessages.toString(), 1, multicastMessages.size());
		Assert.assertTrue(
			multicastMessages.toString(),
			multicastMessages.contains(clusterRequest));
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		clusterNodeResponses = futureClusterResponses.get();

		Assert.assertEquals(0, clusterNodeResponses.size());

		// Test 3, execute unicast request to local address

		TestClusterChannel.clearAllMessages();

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		ClusterNode localClusterNode =
			clusterExecutorImpl.getLocalClusterNode();

		clusterRequest = ClusterRequest.createUnicastRequest(
			clusterRequest, localClusterNode.getClusterNodeId());

		futureClusterResponses = clusterExecutorImpl.execute(clusterRequest);

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		clusterNodeResponses = futureClusterResponses.get();

		Assert.assertEquals(1, clusterNodeResponses.size());

		// Test 4, execute unicast request to other address

		TestClusterChannel.clearAllMessages();

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		ClusterExecutorImpl newClusterExecutorImpl = _getClusterExecutorImpl();

		Assert.assertEquals(
			multicastMessages.toString(), 1, multicastMessages.size());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		Serializable serializable = multicastMessages.get(0);

		clusterExecutorImpl.handleReceivedClusterRequest(
			(ClusterRequest)serializable);

		TestClusterChannel.clearAllMessages();

		ClusterNode newClusterNode =
			newClusterExecutorImpl.getLocalClusterNode();

		clusterRequest = ClusterRequest.createUnicastRequest(
			StringPool.BLANK, newClusterNode.getClusterNodeId());

		clusterExecutorImpl.execute(clusterRequest);

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertEquals(
			unicastMessages.toString(), 1, unicastMessages.size());

		ObjectValuePair<Serializable, Address> receivedMessage =
			unicastMessages.get(0);

		Assert.assertEquals(clusterRequest, receivedMessage.getKey());
	}

	@Test
	public void testExecuteClusterRequest() throws Exception {
		ClusterExecutorImpl clusterExecutorImpl = _getClusterExecutorImpl();

		// Test 1, payload is not method handler

		ClusterNodeResponse clusterNodeResponse =
			clusterExecutorImpl.executeClusterRequest(
				ClusterRequest.createMulticastRequest(StringPool.BLANK));

		Exception exception1 = clusterNodeResponse.getException();

		Assert.assertEquals(
			"Payload is not of type " + MethodHandler.class.getName(),
			exception1.getMessage());

		// Test 2, invoke with exception1

		String timestamp = String.valueOf(System.currentTimeMillis());

		clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(
			ClusterRequest.createMulticastRequest(
				new MethodHandler(
					new MethodKey(TestBean.class, "testMethod3", String.class),
					timestamp)));

		try {
			clusterNodeResponse.getResult();

			Assert.fail();
		}
		catch (Exception exception2) {
			Throwable throwable = exception2.getCause();

			Assert.assertEquals(timestamp, throwable.getMessage());
		}

		// Test 3, invoke without exception1

		timestamp = String.valueOf(System.currentTimeMillis());

		clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(
			ClusterRequest.createMulticastRequest(
				new MethodHandler(
					new MethodKey(TestBean.class, "testMethod1", String.class),
					timestamp)));

		Assert.assertEquals(timestamp, clusterNodeResponse.getResult());

		// Test 4, thread local

		Assert.assertTrue(ClusterInvokeThreadLocal.isEnabled());

		clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(
			ClusterRequest.createMulticastRequest(
				new MethodHandler(
					new MethodKey(TestBean.class, "testMethod5"))));

		Assert.assertFalse((Boolean)clusterNodeResponse.getResult());

		Assert.assertTrue(ClusterInvokeThreadLocal.isEnabled());
	}

	private ClusterExecutorImpl _getClusterExecutorImpl() {
		ClusterExecutorImpl clusterExecutorImpl = new ClusterExecutorImpl() {

			@Override
			protected void modified(Map<String, Object> properies) {
				clusterExecutorConfiguration =
					ConfigurableUtil.createConfigurable(
						ClusterExecutorConfiguration.class, properies);

				ReflectionTestUtil.setFieldValue(
					this, "_clusterChannelFactory",
					new TestClusterChannelFactory());
			}

		};

		ReflectionTestUtil.setFieldValue(
			clusterExecutorImpl, "_portalExecutorManager",
			new MockPortalExecutorManager());

		PropsUtil.set(
			PropsKeys.CLUSTER_LINK_CHANNEL_NAME_CONTROL,
			"test-channel-name-control");
		PropsUtil.set(
			PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL,
			"test-channel-properties-control");

		clusterExecutorImpl.activate(
			SystemBundleUtil.getBundleContext(), Collections.emptyMap());

		return clusterExecutorImpl;
	}

}