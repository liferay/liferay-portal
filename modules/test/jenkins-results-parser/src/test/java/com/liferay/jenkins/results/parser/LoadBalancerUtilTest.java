/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Calum Ragan
 */
public class LoadBalancerUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsMasterTestUtil.resetCaches();

		JenkinsResultsParserUtil.setBuildProperties(
			(Hashtable<Object, Object>)null);
	}

	@Test
	public void testGetAvailableJenkinsMasters() {
		Properties properties = JenkinsMasterTestUtil.stageFleet("test-9", 5);

		List<String> masterNames = _getMasterNames(
			LoadBalancerUtil.getAvailableJenkinsMasters(
				"TEST-9-2", "test-9", properties, false));

		Assert.assertEquals(masterNames.toString(), 4, masterNames.size());

		Assert.assertFalse(masterNames.contains("test-9-2"));

		properties.setProperty(
			"jenkins.load.balancer.blacklist", "test-9-3, test-9-4");

		masterNames = _getMasterNames(
			LoadBalancerUtil.getAvailableJenkinsMasters(
				null, "test-9", properties, false));

		Assert.assertEquals(masterNames.toString(), 3, masterNames.size());

		Assert.assertFalse(masterNames.contains("test-9-3"));
		Assert.assertFalse(masterNames.contains("test-9-4"));
	}

	@Test
	public void testGetMostAvailableMasterURL() {
		Properties properties = JenkinsMasterTestUtil.stageFleet("test-9", 5);

		properties.setProperty("blacklist", "test-9-5");

		Map<String, Integer> selectionCounts = new HashMap<>();

		for (int i = 0; i < 12; i++) {
			String masterURL = LoadBalancerUtil.getMostAvailableMasterURL(
				properties, false);

			Integer selectionCount = selectionCounts.get(masterURL);

			if (selectionCount == null) {
				selectionCount = 0;
			}

			selectionCounts.put(masterURL, selectionCount + 1);
		}

		Assert.assertEquals(
			selectionCounts.toString(), 4, selectionCounts.size());

		for (int i = 1; i <= 4; i++) {
			String masterURL = "http://test-9-" + i;

			Assert.assertEquals(
				masterURL, Integer.valueOf(3), selectionCounts.get(masterURL));
		}

		Assert.assertNull(selectionCounts.get("http://test-9-5"));
	}

	private List<String> _getMasterNames(List<JenkinsMaster> jenkinsMasters) {
		List<String> masterNames = new ArrayList<>();

		for (JenkinsMaster jenkinsMaster : jenkinsMasters) {
			masterNames.add(jenkinsMaster.getName());
		}

		return masterNames;
	}

}