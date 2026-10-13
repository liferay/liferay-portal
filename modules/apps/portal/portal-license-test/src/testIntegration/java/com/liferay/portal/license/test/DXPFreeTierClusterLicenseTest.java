/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;

import java.io.Serializable;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 * @author Jiefeng Wu
 */
@Ignore
@RunWith(Arquillian.class)
public class DXPFreeTierClusterLicenseTest extends BaseClusterLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseClusterLicenseTestCase.setUpClass();

		TomcatNode.ClusterExecutable<Serializable> clusterExecutable =
			DXPFreeTierClusterLicenseTest::_deployFreeTierLicense;

		registerBackgroundTomcatNode(startTomcatNode(true, clusterExecutable));
		registerBackgroundTomcatNode(startTomcatNode(true, clusterExecutable));
	}

	@Test
	public void testClusterLicense() throws Exception {
		testClusterLicense(
			DXPFreeTierClusterLicenseTest::_deployFreeTierLicense,
			_PAGE_KEY_EXCEEDED_LIMIT_FREE_TIER, _MAX_CLUSTER_NODES_FREE_TIER);
	}

	@Test
	public void testEnterpriseLicense() throws Exception {
		TomcatNode tomcatNode1 = startTomcatNode(true);

		tomcatNode1.syncExecute(
			() -> deployAndAssertClusterLicense(
				DXPFreeTierClusterLicenseTest::_deployFreeTierLicense));

		Future<String> messageFuture1 = testConsoleMessageListener.register(
			tomcatNode1.getNodeId(), CONSOLE_KEY_LICENSED_NODE,
			getNodeExceededConsoleKey(_MAX_CLUSTER_NODES_FREE_TIER));

		TomcatNode tomcatNode2 = startTomcatNode(
			true,
			getClusterExecutable(tomcatNode1.syncExecute(this::getTimeStamp)));

		Future<String> messageFuture2 = testConsoleMessageListener.register(
			tomcatNode2.getNodeId(), CONSOLE_KEY_TEMPORARY_NODE,
			getNodeExceededConsoleKey(_MAX_CLUSTER_NODES_FREE_TIER));

		tomcatNode2.syncExecute(
			() -> deployAndAssertClusterLicense(
				DXPFreeTierClusterLicenseTest::_deployFreeTierLicense));

		testConsoleMessageListener.assertMessageListened(messageFuture1);
		testConsoleMessageListener.assertMessageListened(messageFuture2);

		tomcatNode1.syncExecute(this::_deployEnterpriseLicense);
		tomcatNode2.syncExecute(this::_deployEnterpriseLicense);

		try {
			tomcatNode2.wait(NODE_SHUTDOWN_MINUTES, TimeUnit.MINUTES);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof TimeoutException);
		}

		assertPortalLicenseRegistered(tomcatNode1);
		assertPortalLicenseRegistered(tomcatNode2);
	}

	@Test
	public void testLimitPreviouslyValidatedClusterNode() throws Exception {
		testLimitPreviouslyValidatedClusterNode(
			DXPFreeTierClusterLicenseTest::_deployFreeTierLicense,
			_MAX_CLUSTER_NODES_FREE_TIER, true);
	}

	@Test
	public void testLimitPreviouslyValidatedClusterNodeWithManualRecovery()
		throws Exception {

		testLimitPreviouslyValidatedClusterNode(
			DXPFreeTierClusterLicenseTest::_deployFreeTierLicense,
			_MAX_CLUSTER_NODES_FREE_TIER, false);
	}

	@Test
	public void testRecoverCluster() throws Exception {
		testRecoverCluster(
			DXPFreeTierClusterLicenseTest::_deployFreeTierLicense,
			_PAGE_KEY_EXCEEDED_LIMIT_FREE_TIER, _MAX_CLUSTER_NODES_FREE_TIER,
			false);
	}

	@Test
	public void testRecoverClusterWithLicensedNodeShutdown() throws Exception {
		testRecoverCluster(
			DXPFreeTierClusterLicenseTest::_deployFreeTierLicense,
			_PAGE_KEY_EXCEEDED_LIMIT_FREE_TIER, _MAX_CLUSTER_NODES_FREE_TIER,
			true);
	}

	private static Serializable _deployFreeTierLicense() throws Exception {
		deployFreeTierPortalLicense(Time.HOUR);

		return null;
	}

	private Serializable _deployEnterpriseLicense() throws Exception {
		deployEnterprisePortalLicense(Time.HOUR);

		return null;
	}

	private static final int _MAX_CLUSTER_NODES_FREE_TIER = 3;

	private static final String _PAGE_KEY_EXCEEDED_LIMIT_FREE_TIER =
		"You have exceeded the developer mode connection limit";

}