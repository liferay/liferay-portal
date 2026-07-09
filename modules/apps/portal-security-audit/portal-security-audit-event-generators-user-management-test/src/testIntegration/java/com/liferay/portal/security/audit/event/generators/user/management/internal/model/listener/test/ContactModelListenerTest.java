/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ContactLocalService;
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
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class ContactModelListenerTest extends BaseModelListenerTestCase {

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_user = UserTestUtil.addUser();

		_company = CompanyTestUtil.addCompany();

		auditMessages.clear();

		Contact contact = _user.getContact();

		contact.setFirstName(RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_contactLocalService.updateContact(contact);
		}

		AuditMessage auditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(_user.getCompanyId(), auditMessage.getCompanyId());
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private ContactLocalService _contactLocalService;

	@DeleteAfterTestRun
	private User _user;

}