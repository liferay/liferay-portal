/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PortalInstances;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class StorePostUpgradeDataCleanupProcessTest
	extends BasePostUpgradeDataCleanupProcessTestCase {

	@Test
	public void testOrphanStoreFound() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		String dlStoreImpl = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				dlStoreImpl, LoggerTestUtil.WARN)) {

			test(
				null, () -> _companyLocalService.deleteCompany(company),
				() -> PortalInstances.removeCompany(company.getCompanyId()));

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					StringBundler.concat(
						"Manually remove unused store ", company.getCompanyId(),
						" that belongs to company ", company.getCompanyId(),
						" if it is no longer used anywhere else")));
		}
	}

	@Override
	protected Class<?>[] getPostUpgradeDataCleanupProcessArgumentTypes() {
		return new Class<?>[] {Store.class};
	}

	@Override
	protected Object[] getPostUpgradeDataCleanupProcessArguments() {
		return new Object[] {_store};
	}

	@Override
	protected String getPostUpgradeDataCleanupProcessClassName() {
		return "com.liferay.data.cleanup.internal.verify." +
			"StorePostUpgradeDataCleanupProcess";
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "(&(objectClass=com.liferay.document.library.kernel.store.Store)(default=true))"
	)
	private Store _store;

}