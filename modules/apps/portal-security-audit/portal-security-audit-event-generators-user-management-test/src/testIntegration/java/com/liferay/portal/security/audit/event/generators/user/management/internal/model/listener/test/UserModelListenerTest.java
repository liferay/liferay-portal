/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class UserModelListenerTest extends BaseModelListenerTestCase {

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_user = UserTestUtil.addUser();

		_company = CompanyTestUtil.addCompany();

		Assert.assertFalse(_user.isAgreedToTermsOfUse());

		auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userLocalService.updateAgreedToTermsOfUse(_user.getUserId(), true);
		}

		AuditMessage agreedToTermsOfUseAuditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.AGREED_TO_TERMS_OF_USE);

		JSONObject additionalInfoJSONObject =
			agreedToTermsOfUseAuditMessage.getAdditionalInfo();

		Assert.assertTrue(
			additionalInfoJSONObject.has("termsOfUseJournalArticleGroupId"));
		Assert.assertTrue(
			additionalInfoJSONObject.has("termsOfUseJournalArticleId"));

		Assert.assertEquals(
			String.valueOf(_user.getUserId()),
			agreedToTermsOfUseAuditMessage.getClassPK());
		Assert.assertEquals(
			_user.getCompanyId(),
			agreedToTermsOfUseAuditMessage.getCompanyId());

		auditMessages.clear();

		_user = _userLocalService.getUser(_user.getUserId());

		_user.setComments(RandomTestUtil.randomString());

		_user = _userLocalService.updateUser(_user);

		for (AuditMessage auditMessage : auditMessages) {
			Assert.assertNotEquals(
				EventTypes.AGREED_TO_TERMS_OF_USE, auditMessage.getEventType());
		}
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}