/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Díaz
 */
@RunWith(Arquillian.class)
public class PLOOverrideResourceBundleManagerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetOverrideResourceBundleWithUnregisteredCompany()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into PLOEntry (ploEntryId, companyId, key_, " +
					"languageId, value) values (?, ?, ?, ?, ?)");

			SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			preparedStatement.setLong(1, RandomTestUtil.randomLong());
			preparedStatement.setLong(2, companyId);

			String key = RandomTestUtil.randomString();

			preparedStatement.setString(3, key);

			Locale locale = LocaleUtil.getDefault();

			preparedStatement.setString(4, LanguageUtil.getLanguageId(locale));

			String value = RandomTestUtil.randomString();

			preparedStatement.setString(5, value);

			preparedStatement.executeUpdate();

			Assert.assertEquals(value, LanguageUtil.get(locale, key));
		}
		finally {
			try (Connection connection = DataAccess.getConnection();

				PreparedStatement preparedStatement =
					connection.prepareStatement(
						"delete from PLOEntry where companyId = ?")) {

				preparedStatement.setLong(1, companyId);

				preparedStatement.executeUpdate();
			}
		}
	}

}