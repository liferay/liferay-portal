/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;

import org.junit.Assume;
import org.junit.runner.Description;

/**
 * @author Shuyang Zhou
 */
public class TomcatClusterTestRule extends ClassTestRule<Void> {

	public TomcatCluster.Builder buildTomcatNode() {
		return _tomcatCluster.buildTomcatNode();
	}

	public TomcatCluster getTomcatCluster() {
		return _tomcatCluster;
	}

	@Override
	protected void afterClass(Description description, Void v)
		throws Throwable {

		for (TomcatNode tomcatNode : _tomcatCluster.getTomcatNodes()) {
			tomcatNode.stop();

			tomcatNode.destroy();
		}
	}

	@Override
	protected Void beforeClass(Description description) throws Throwable {
		Assume.assumeTrue(
			"Unsupported server type " + ServerDetector.getServerId(),
			ServerDetector.isTomcat());

		Assume.assumeTrue(
			"System property \"catalina.home\" is undefined",
			System.getProperty("catalina.home") != null);

		DB db = DBManagerUtil.getDB();

		Assume.assumeFalse(
			"Unsupported DB type " + db.getDBType(),
			db.getDBType() == DBType.HYPERSONIC);

		_tomcatCluster = new TomcatCluster(description.getTestClass());

		return null;
	}

	private TomcatCluster _tomcatCluster;

}