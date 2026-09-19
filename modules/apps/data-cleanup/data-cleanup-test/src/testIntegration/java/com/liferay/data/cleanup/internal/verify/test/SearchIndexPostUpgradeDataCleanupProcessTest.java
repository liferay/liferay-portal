/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
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
public class SearchIndexPostUpgradeDataCleanupProcessTest
	extends BasePostUpgradeDataCleanupProcessTestCase {

	@Test
	public void testOrphanSearchIndexFound() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Index ", _indexNameBuilder.getIndexNamePrefix(),
							company.getCompanyId(),
							" belongs to deleted company ",
							company.getCompanyId(),
							". Remove it if it is not used anywhere else.")));
			},
			() -> _companyLocalService.deleteCompany(company),
			() -> PortalInstances.removeCompany(company.getCompanyId()));
	}

	@Override
	protected Class<?>[] getPostUpgradeDataCleanupProcessArgumentTypes() {
		return new Class<?>[] {IndexInformation.class, IndexNameBuilder.class};
	}

	@Override
	protected Object[] getPostUpgradeDataCleanupProcessArguments() {
		return new Object[] {_indexInformation, _indexNameBuilder};
	}

	@Override
	protected String getPostUpgradeDataCleanupProcessClassName() {
		return "com.liferay.data.cleanup.internal.verify." +
			"SearchIndexPostUpgradeDataCleanupProcess";
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private IndexInformation _indexInformation;

	@Inject
	private IndexNameBuilder _indexNameBuilder;

}