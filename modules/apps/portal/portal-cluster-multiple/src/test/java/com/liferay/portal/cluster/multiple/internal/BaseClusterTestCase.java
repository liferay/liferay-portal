/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.util.PortalImpl;

import org.junit.After;
import org.junit.Before;

/**
 * @author Tina Tian
 */
public abstract class BaseClusterTestCase {

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@After
	public void tearDown() {
		TestClusterChannel.reset();
	}

}