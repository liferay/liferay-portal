/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import org.junit.runner.Description;

/**
 * @author Cristina González
 */
public class CompanyProviderClassTestRule extends ClassTestRule<SafeCloseable> {

	public static final CompanyProviderClassTestRule INSTANCE =
		new CompanyProviderClassTestRule();

	@Override
	protected void afterClass(
		Description description, SafeCloseable safeCloseable) {

		safeCloseable.close();
	}

	@Override
	protected SafeCloseable beforeClass(Description description)
		throws PortalException {

		return CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			TestPropsValues.getCompanyId());
	}

	private CompanyProviderClassTestRule() {
	}

}