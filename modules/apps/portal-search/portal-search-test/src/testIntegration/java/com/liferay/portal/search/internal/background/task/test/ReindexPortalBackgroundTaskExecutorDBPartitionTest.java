/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.background.task.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.Inject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class ReindexPortalBackgroundTaskExecutorDBPartitionTest
	extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();
	}

	@Test
	public void testReindex() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try {
			ReflectionTestUtil.invoke(
				_backgroundTaskExecutor, "reindex",
				new Class<?>[] {String.class, long[].class, String.class}, null,
				new long[] {company.getCompanyId()}, "concurrent");

			String[] indexNames = _getIndexNames(
				company.getCompanyId(),
				getPartitionName(company.getCompanyId()));

			GetIndexIndexResponse getIndexIndexResponse =
				_searchEngineAdapter.execute(
					new GetIndexIndexRequest(
						_indexNameBuilder.getIndexName(
							company.getCompanyId())));

			Assert.assertArrayEquals(
				new String[] {indexNames[0]},
				getIndexIndexResponse.getIndexNames());

			Assert.assertNull(indexNames[1]);
			Assert.assertNull(
				_getIndexNames(company.getCompanyId(), defaultPartitionName));
		}
		finally {
			companyLocalService.forEachCompanyId(
				_searchEngineHelper::removeCompany,
				new long[] {company.getCompanyId()});

			companyLocalService.deleteCompany(company);
		}
	}

	private String[] _getIndexNames(long companyId, String partitionName)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId());
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select indexNameCurrent, indexNameNext from ",
					partitionName, ".CompanyInfo where companyId = ?"))) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}

				return new String[] {
					resultSet.getString("indexNameCurrent"),
					resultSet.getString("indexNameNext")
				};
			}
		}
	}

	@Inject(
		filter = "background.task.executor.class.name=com.liferay.portal.search.internal.background.task.ReindexPortalBackgroundTaskExecutor"
	)
	private BackgroundTaskExecutor _backgroundTaskExecutor;

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

	@Inject
	private SearchEngineHelper _searchEngineHelper;

}