/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalInstances;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Jorge Avalos
 */
public class DBPartitionTestRule implements TestRule {

	public static final DBPartitionTestRule INSTANCE =
		new DBPartitionTestRule();

	@Override
	public Statement apply(Statement statement, Description description) {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return statement;
		}

		try {
			Company company = CompanyLocalServiceUtil.fetchCompanyByVirtualHost(
				TestPropsValues.COMPANY_WEB_ID);

			if (company != null) {
				return statement;
			}

			String companyWebId;

			if (TestPropsValues.DATABASE_PARTITION_COPY) {
				companyWebId = StringUtil.replaceLast(
					TestPropsValues.COMPANY_WEB_ID, CharPool.PERIOD, "-copy.");
			}
			else {
				companyWebId = TestPropsValues.COMPANY_WEB_ID;
			}

			PortalInstances.addCompany(
				"",
				() -> CompanyLocalServiceUtil.addCompany(
					null, companyWebId, companyWebId, companyWebId, 0, true,
					true, null, null, null, null, null, null));

			company = CompanyLocalServiceUtil.fetchCompanyByVirtualHost(
				companyWebId);

			if (TestPropsValues.DATABASE_PARTITION_COPY) {
				CompanyLocalServiceUtil.copyDBPartitionCompany(
					company.getCompanyId(), null,
					TestPropsValues.COMPANY_WEB_ID,
					TestPropsValues.COMPANY_WEB_ID,
					TestPropsValues.COMPANY_WEB_ID);
			}
			else if (TestPropsValues.DATABASE_PARTITION_EXPORT_AND_IMPORT) {
				CompanyLocalServiceUtil.exportCompany(company.getCompanyId());

				CompanyLocalServiceUtil.deleteCompany(company.getCompanyId());

				CompanyLocalServiceUtil.addDBPartitionCompany(
					company.getCompanyId(), TestPropsValues.COMPANY_WEB_ID,
					TestPropsValues.COMPANY_WEB_ID,
					TestPropsValues.COMPANY_WEB_ID);
			}
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		return statement;
	}

}