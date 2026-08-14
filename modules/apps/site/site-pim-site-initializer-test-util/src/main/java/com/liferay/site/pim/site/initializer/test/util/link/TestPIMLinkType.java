/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.test.util.link;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;

import java.util.Locale;

/**
 * @author Stefano Motta
 */
public class TestPIMLinkType implements PIMLinkType {

	public static final String TYPE = RandomTestUtil.randomString();

	@Override
	public String getLabel(Locale locale) {
		return TYPE;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isClustered() {
		return true;
	}

}