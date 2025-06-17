/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.service.persistence.IndexEntryPersistence;

import java.lang.reflect.Field;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class IndexEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		List<String> missingIndexForFinderNames = new ArrayList<>();

		try (Connection connection = DataAccess.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			DB db = DBManagerUtil.getDB();

			List<IndexMetadata> indexMetadatas = db.getIndexMetadatas(
				connection, "IndexEntry", null, false);

			for (Field field :
					ReflectionUtil.getDeclaredFields(
						_indexEntryPersistence.getClass())) {

				String fieldName = field.getName();

				String finderName = null;
				boolean unique = false;

				if (fieldName.startsWith("_finderPathWithPaginationFindBy")) {
					finderName = fieldName.substring(31);
				}
				else if (fieldName.startsWith("_finderPathFetchBy")) {
					finderName = fieldName.substring(18);
					unique = true;
				}

				if ((finderName != null) &&
					!_hasIndex(
						databaseMetaData,
						(FinderPath)field.get(_indexEntryPersistence),
						indexMetadatas, unique)) {

					missingIndexForFinderNames.add(finderName);
				}
			}
		}

		Assert.assertTrue(
			missingIndexForFinderNames.toString(),
			missingIndexForFinderNames.isEmpty());
	}

	private boolean _hasIndex(
			DatabaseMetaData databaseMetaData, FinderPath finderPath,
			List<IndexMetadata> indexMetadatas, boolean unique)
		throws SQLException {

		for (IndexMetadata indexMetadata : indexMetadatas) {
			String[] expectedIndexColumnNames = finderPath.getColumnNames();

			if (unique) {
				expectedIndexColumnNames = ArrayUtil.append(
					expectedIndexColumnNames, "ctCollectionId");
			}

			for (int i = 0; i < expectedIndexColumnNames.length; i++) {
				if (databaseMetaData.storesLowerCaseIdentifiers()) {
					expectedIndexColumnNames[i] = StringUtil.toLowerCase(
						expectedIndexColumnNames[i]);
				}
				else if (databaseMetaData.storesUpperCaseIdentifiers()) {
					expectedIndexColumnNames[i] = StringUtil.toUpperCase(
						expectedIndexColumnNames[i]);
				}
			}

			if (unique) {
				if (indexMetadata.isUnique() &&
					ArrayUtil.containsAll(
						indexMetadata.getColumnNames(),
						expectedIndexColumnNames) &&
					ArrayUtil.containsAll(
						expectedIndexColumnNames,
						indexMetadata.getColumnNames())) {

					return true;
				}
			}
			else if (ArrayUtil.containsAll(
						indexMetadata.getColumnNames(),
						finderPath.getColumnNames())) {

				return true;
			}
		}

		return false;
	}

	@Inject
	private IndexEntryPersistence _indexEntryPersistence;

}