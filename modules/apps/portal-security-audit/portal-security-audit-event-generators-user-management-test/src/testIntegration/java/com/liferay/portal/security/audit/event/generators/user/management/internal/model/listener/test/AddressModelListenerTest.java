/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
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
public class AddressModelListenerTest extends BaseModelListenerTestCase {

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_user = UserTestUtil.addUser();

		_company = CompanyTestUtil.addCompany();

		_address = _addressLocalService.addAddress(
			null, _user.getUserId(), User.class.getName(), _user.getUserId(), 0,
			0, 0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			null, ServiceContextTestUtil.getServiceContext());

		auditMessages.clear();

		_address.setStreet1(RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_addressLocalService.updateAddress(_address);
		}

		AuditMessage auditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(_user.getCompanyId(), auditMessage.getCompanyId());
	}

	@DeleteAfterTestRun
	private Address _address;

	@Inject
	private AddressLocalService _addressLocalService;

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private User _user;

}