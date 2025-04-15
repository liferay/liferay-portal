/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.MutableRenderParameters;

/**
 * @author Dante Wang
 */
public class MockMutableRenderParameters
	extends MockMutablePortletParameters implements MutableRenderParameters {

	@Override
	public void clearPrivate() {
	}

	@Override
	public void clearPublic() {
	}

	@Override
	public MutableRenderParameters clone() {
		return null;
	}

	@Override
	public boolean isPublic(String name) {
		return false;
	}

}