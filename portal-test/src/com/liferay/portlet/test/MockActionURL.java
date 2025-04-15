/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;

import jakarta.portlet.ActionURL;
import jakarta.portlet.MutableActionParameters;

/**
 * @author David Arques
 * @see    com.liferay.portlet.internal.ActionURLImpl
 */
public class MockActionURL extends MockLiferayPortletURL implements ActionURL {

	@Override
	public MutableActionParameters getActionParameters() {
		return null;
	}

}