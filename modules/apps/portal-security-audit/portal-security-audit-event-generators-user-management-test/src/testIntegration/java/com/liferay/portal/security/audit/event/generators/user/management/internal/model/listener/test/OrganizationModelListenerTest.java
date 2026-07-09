/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
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
public class OrganizationModelListenerTest extends BaseModelListenerTestCase {

	@Test
	public void testOnBeforeAddAssociation() throws Exception {
		_user = UserTestUtil.addUser();

		_organization = OrganizationTestUtil.addOrganization();

		_company = CompanyTestUtil.addCompany();

		auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userLocalService.addOrganizationUsers(
				_organization.getOrganizationId(),
				new long[] {_user.getUserId()});
		}

		AuditMessage auditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.ASSIGN);

		Assert.assertEquals(_user.getCompanyId(), auditMessage.getCompanyId());
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		_company = CompanyTestUtil.addCompany();

		auditMessages.clear();

		_organization.setName(RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_organization = _organizationLocalService.updateOrganization(
				_organization);
		}

		AuditMessage auditMessage = fetchAuditMessage(
			Organization.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			_organization.getCompanyId(), auditMessage.getCompanyId());
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private Organization _organization;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}