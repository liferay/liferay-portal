/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.graph;

import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carlos Sierra Andrés
 * @author Miguel Pastor
 */
public class ReleaseGraphManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAutoUpgradePath() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4));

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList("0.0.0");

		Assert.assertEquals(
			upgradeInfosList.toString(), 1, upgradeInfosList.size());
		Assert.assertEquals(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4),
			upgradeInfosList.get(0));
	}

	@Test
	public void testGetAutoUpgradePathWhenInEndNode() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");
		UpgradeInfo upgradeInfo5 = _createUpgradeInfo("0.1.0", "0.1.0.1");
		UpgradeInfo upgradeInfo6 = _createUpgradeInfo("0.1.0.1", "0.1.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4,
				upgradeInfo5, upgradeInfo6));

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList("0.1.0.1");

		Assert.assertEquals(
			upgradeInfosList.toString(), 1, upgradeInfosList.size());
		Assert.assertEquals(
			Arrays.asList(
				upgradeInfo6, upgradeInfo2, upgradeInfo3, upgradeInfo4),
			upgradeInfosList.get(0));
	}

	@Test
	public void testGetAutoUpgradePathWhenInEndNodeAndMultipleSinkNodes() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");
		UpgradeInfo upgradeInfo5 = _createUpgradeInfo("0.1.0", "0.1.0.1");
		UpgradeInfo upgradeInfo6 = _createUpgradeInfo("0.1.0.1", "0.1.0");
		UpgradeInfo upgradeInfo7 = _createUpgradeInfo("0.1.0.1", "0.1.0.2");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4,
				upgradeInfo5, upgradeInfo6, upgradeInfo7));

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList("0.1.0.1");

		Assert.assertEquals(
			upgradeInfosList.toString(), 2, upgradeInfosList.size());
	}

	@Test
	public void testGetAutoUpgradePathWhithoutEndNodes() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.0.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("0.2.0", "0.1.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4));

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList("0.0.0");

		Assert.assertEquals(
			upgradeInfosList.toString(), 0, upgradeInfosList.size());
	}

	@Test
	public void testGetSinkNodes() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4));

		List<String> sinkNodes = releaseGraphManager.getEndVertices();

		Assert.assertTrue(sinkNodes.toString(), sinkNodes.contains("2.0.0"));
	}

	@Test
	public void testGetUpgradePath() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4));

		List<UpgradeInfo> upgradePath = releaseGraphManager.getUpgradeInfos(
			"0.0.0", "2.0.0");

		Assert.assertEquals(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4),
			upgradePath);
	}

	@Test
	public void testGetUpgradePathNotInOrder() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo4, upgradeInfo2, upgradeInfo1, upgradeInfo3));

		List<UpgradeInfo> upgradePath = releaseGraphManager.getUpgradeInfos(
			"0.0.0", "2.0.0");

		Assert.assertEquals(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4),
			upgradePath);
	}

	@Test
	public void testGetUpgradePathWithCyclesReturnsShortestPath() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");
		UpgradeInfo upgradeInfo5 = _createUpgradeInfo("0.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4,
				upgradeInfo5));

		List<UpgradeInfo> upgradePath = releaseGraphManager.getUpgradeInfos(
			"0.0.0", "2.0.0");

		Assert.assertEquals(Arrays.asList(upgradeInfo5), upgradePath);
	}

	@Test
	public void testGetUpgradePathWithCyclesReturnsShortestPathWhenNotZero() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");
		UpgradeInfo upgradeInfo5 = _createUpgradeInfo("0.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4,
				upgradeInfo5));

		List<UpgradeInfo> upgradePath = releaseGraphManager.getUpgradeInfos(
			"0.1.0", "2.0.0");

		Assert.assertEquals(
			Arrays.asList(upgradeInfo2, upgradeInfo3, upgradeInfo4),
			upgradePath);
	}

	@Test
	public void testGetUpgradePathWithIllegalArguments() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo4, upgradeInfo2, upgradeInfo1, upgradeInfo3));

		List<UpgradeInfo> upgradeInfos = releaseGraphManager.getUpgradeInfos(
			"0.0.0", "2.0.1");

		Assert.assertEquals(upgradeInfos.toString(), 0, upgradeInfos.size());
	}

	@Test
	public void testgetSinkNodesWithMultipleEndNodes() {
		UpgradeInfo upgradeInfo1 = _createUpgradeInfo("0.0.0", "0.1.0");
		UpgradeInfo upgradeInfo2 = _createUpgradeInfo("0.1.0", "0.2.0");
		UpgradeInfo upgradeInfo3 = _createUpgradeInfo("0.2.0", "1.0.0");
		UpgradeInfo upgradeInfo4 = _createUpgradeInfo("1.0.0", "2.0.0");
		UpgradeInfo upgradeInfo5 = _createUpgradeInfo("1.0.0", "2.2.0");

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(
				upgradeInfo1, upgradeInfo2, upgradeInfo3, upgradeInfo4,
				upgradeInfo5));

		List<String> sinkNodes = releaseGraphManager.getEndVertices();

		Assert.assertTrue(sinkNodes.toString(), sinkNodes.contains("2.0.0"));
		Assert.assertTrue(sinkNodes.toString(), sinkNodes.contains("2.2.0"));
	}

	private UpgradeInfo _createUpgradeInfo(String from, String to) {
		return new UpgradeInfo(
			from, to, new TestUpgradeStep(from + " -> " + to));
	}

	private static class TestUpgradeStep implements UpgradeStep {

		public TestUpgradeStep(String name) {
			_name = name;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof TestUpgradeStep)) {
				return false;
			}

			TestUpgradeStep testUpgradeStep = (TestUpgradeStep)object;

			return _name.equals(testUpgradeStep._name);
		}

		@Override
		public int hashCode() {
			return _name.hashCode();
		}

		@Override
		public void upgrade() {
		}

		private final String _name;

	}

}