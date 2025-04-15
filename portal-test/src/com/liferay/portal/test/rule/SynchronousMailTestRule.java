/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.mail.MailServiceTestUtil;

import jakarta.portlet.PortletPreferences;

import org.junit.runner.Description;

/**
 * @author Manuel de la Peña
 * @author Roberto Díaz
 * @author Shuyang Zhou
 */
public class SynchronousMailTestRule extends SynchronousDestinationTestRule {

	public static final SynchronousMailTestRule INSTANCE =
		new SynchronousMailTestRule();

	@Override
	public void afterClass(Description description, SyncHandler syncHandler)
		throws Exception {

		_setCompanyAdminEmailFromAddress(
			TestPropsValues.getCompanyId(), _adminEmailFromAddress);

		MailServiceTestUtil.stop();
	}

	@Override
	public void afterMethod(
		Description description, SyncHandler syncHandler, Object target) {

		MailServiceTestUtil.clearMessages();
	}

	@Override
	public SyncHandler beforeClass(Description description) throws Throwable {
		MailServiceTestUtil.start();

		_adminEmailFromAddress = PrefsPropsUtil.getString(
			TestPropsValues.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		_setCompanyAdminEmailFromAddress(
			TestPropsValues.getCompanyId(), "integration-test@liferay.com");

		return null;
	}

	private SynchronousMailTestRule() {
	}

	private void _setCompanyAdminEmailFromAddress(
			long companyId, String adminEmailFromAddress)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		portletPreferences.setValue(
			PropsKeys.ADMIN_EMAIL_FROM_ADDRESS, adminEmailFromAddress);

		portletPreferences.store();
	}

	private String _adminEmailFromAddress;

}