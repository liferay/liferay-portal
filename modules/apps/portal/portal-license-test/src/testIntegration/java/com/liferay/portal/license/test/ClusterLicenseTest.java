/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Time;

import java.io.Serializable;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 * @author Jiefeng Wu
 * @author Tina Tian
 */
@Ignore
@RunWith(Arquillian.class)
public class ClusterLicenseTest extends BaseClusterLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseClusterLicenseTestCase.setUpClass();

		registerBackgroundTomcatNode(
			startTomcatNode(
				true, ClusterLicenseTest::_deployVirtualClusterLicense));
	}

	@Test
	public void testClusterLicense() throws Exception {
		testClusterLicense(
			ClusterLicenseTest::_deployVirtualClusterLicense,
			_PAGE_KEY_EXCEEDED_LIMIT_VIRTUAL_CLUSTER,
			_MAX_CLUSTER_NODES_VIRTUAL_CLUSTER);
	}

	@Test
	public void testLimitPreviouslyValidatedClusterNode() throws Exception {
		testLimitPreviouslyValidatedClusterNode(
			ClusterLicenseTest::_deployVirtualClusterLicense,
			_MAX_CLUSTER_NODES_VIRTUAL_CLUSTER, true);
	}

	@Test
	public void testLimitPreviouslyValidatedClusterNodeWithManualRecovery()
		throws Exception {

		testLimitPreviouslyValidatedClusterNode(
			ClusterLicenseTest::_deployVirtualClusterLicense,
			_MAX_CLUSTER_NODES_VIRTUAL_CLUSTER, false);
	}

	@Test
	public void testRecoverCluster() throws Exception {
		testRecoverCluster(
			ClusterLicenseTest::_deployVirtualClusterLicense,
			_PAGE_KEY_EXCEEDED_LIMIT_VIRTUAL_CLUSTER,
			_MAX_CLUSTER_NODES_VIRTUAL_CLUSTER, false);
	}

	@Test
	public void testRecoverClusterWithLicensedNodeShutdown() throws Exception {
		testRecoverCluster(
			ClusterLicenseTest::_deployVirtualClusterLicense,
			_PAGE_KEY_EXCEEDED_LIMIT_VIRTUAL_CLUSTER,
			_MAX_CLUSTER_NODES_VIRTUAL_CLUSTER, true);
	}

	private static Serializable _deployVirtualClusterLicense()
		throws Exception {

		deployEnterprisePortalLicense(
			StringPool.BLANK, _MAX_CLUSTER_NODES_VIRTUAL_CLUSTER, Time.HOUR);

		return null;
	}

	private static final int _MAX_CLUSTER_NODES_VIRTUAL_CLUSTER = 2;

	private static final String _PAGE_KEY_EXCEEDED_LIMIT_VIRTUAL_CLUSTER =
		"You have exceeded the maximum number of cluster nodes in cluster";

}