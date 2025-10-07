/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.LogAssertionTestRule;
import com.liferay.portal.tools.ToolDependencies;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class SampleSQLBuilderTest {

	@ClassRule
	public static final LogAssertionTestRule logAssertionTestRule =
		LogAssertionTestRule.INSTANCE;

	@Test
	public void testFreeMarkerTemplateContent() throws Exception {
		Class<?> clazz = getClass();

		URL url = clazz.getResource(
			"/com/liferay/portal/tools/sample/sql/builder/dependencies" +
				"/sample.ftl");

		String fileContent = new String(
			Files.readAllBytes(Paths.get(url.toURI())), StringPool.UTF8);

		Assert.assertTrue(
			"sample.ftl must end with " + _SAMPLE_FTL_END,
			fileContent.endsWith(_SAMPLE_FTL_END));
	}

	@Test
	public void testGenerateAndInsertSampleSQL() throws Exception {
		ToolDependencies.wireBasic();

		DBManagerUtil.setDB(DBType.HYPERSONIC, null);

		Properties properties = new Properties();

		File tempDir = new File(
			SystemProperties.get(SystemProperties.TMP_DIR),
			String.valueOf(System.currentTimeMillis()));

		_initProperties(properties);

		File tempPropertiesFile = File.createTempFile("test", ".properties");

		try (Writer writer = new FileWriter(tempPropertiesFile)) {
			properties.store(writer, null);

			System.setProperty(
				"sample-sql-properties", tempPropertiesFile.getAbsolutePath());
			System.setProperty("user.dir", tempDir.getAbsolutePath());

			new SampleSQLBuilder();

			_loadHypersonic(tempDir.getAbsolutePath());
		}
		finally {
			FileUtil.deltree(tempDir);
		}
	}

	private void _initProperties(Properties properties) {
		properties.put(
			BenchmarksPropsKeys.COMMERCE_LAYOUT_EXCLUDED_PORTLETS,
			StringPool.BLANK);
		properties.put(BenchmarksPropsKeys.DB_TYPE, "hypersonic");
		properties.put(
			BenchmarksPropsKeys.MAX_ACCOUNT_ENTRY_COMMERCE_ORDER_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_ACCOUNT_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_ASSET_CATEGORY_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_ASSET_TAG_COUNT, "2");
		properties.put(BenchmarksPropsKeys.MAX_ASSET_VUCABULARY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_ASSETPUBLISHER_PAGE_COUNT, "2");
		properties.put(BenchmarksPropsKeys.MAX_BLOGS_ENTRY_COMMENT_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_BLOGS_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_COMMERCE_CATALOG_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_COMMERCE_GROUP_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_INVENTORY_WAREHOUSE_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_INVENTORY_WAREHOUSE_ITEM_QUANTITY,
			"1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_ORDER_STATUS_CANCELLED_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_ORDER_STATUS_OPEN_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_ORDER_STATUS_PENDING_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_COMMERCE_PRICE_LIST_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_INSTANCE_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_OPTION_CATEGORY_COUNT,
			"1");
		properties.put(BenchmarksPropsKeys.MAX_COMPANY_COUNT, "2");
		properties.put(BenchmarksPropsKeys.MAX_COMPANY_USER_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_CONTENT_LAYOUT_COUNT, "6");
		properties.put(
			BenchmarksPropsKeys.MAX_CP_DEFINITION_ATTACHMENT_TYPE_IMAGE_COUNT,
			"1");
		properties.put(
			BenchmarksPropsKeys.MAX_CP_DEFINITION_ATTACHMENT_TYPE_PDF_COUNT,
			"1");
		properties.put(
			BenchmarksPropsKeys.
				MAX_CP_DEFINITION_SPECIFICATION_OPTION_VALUE_COUNT,
			"1");
		properties.put(
			BenchmarksPropsKeys.MAX_CP_SPECIFICATION_OPTION_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DDL_CUSTOM_FIELD_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DDL_RECORD_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DDL_RECORD_SET_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FILE_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FILE_ENTRY_SIZE, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FOLDER_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FOLDER_DEPTH, "1");
		properties.put(BenchmarksPropsKeys.MAX_FRAGMENT_ENTRY_LINK_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_GROUP_COUNT, "2");
		properties.put(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_PAGE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_SIZE, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_VERSION_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_LIST_TYPE_DEFINITION_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_LIST_TYPE_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_MB_CATEGORY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_MB_MESSAGE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_MB_THREAD_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_OBJECT_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_OBJECT_ENTRY_PAGE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_SEGMENTS_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_USER_TO_GROUP_COUNT, "1");
		properties.put(BenchmarksPropsKeys.OPTIMIZE_BUFFER_SIZE, "8192");
		properties.put(
			BenchmarksPropsKeys.OUTPUT_CSV_FILE_NAMES,
			StringBundler.concat(
				"assetPublisher,blog,commerceDeliveryAPI,",
				"commerceInventoryWarehouseItem,commerceOrder,commerceProduct,",
				"company,cpDefinition,documentLibrary,dynamicDataList,",
				"fragment,layout,mbCategory,mbThread,objectDefinition,",
				"portalPreferenceValue,repository,user"));
		properties.put(BenchmarksPropsKeys.OUTPUT_MERGE, "true");
		properties.put(
			BenchmarksPropsKeys.SCRIPT,
			"com/liferay/portal/tools/sample/sql/builder/dependencies" +
				"/sample.ftl");
		properties.put(BenchmarksPropsKeys.SEARCH_BAR_ENABLED, "true");
		properties.put(
			BenchmarksPropsKeys.VIRTUAL_HOSTNAME_ADMIN_INSTANCE, "localhost");
		properties.put(BenchmarksPropsKeys.VIRTUAL_HOSTNAME_PREFIX, "liferay");
	}

	private void _loadHypersonic(Connection connection, String fileName)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		List<String> lines = Files.readAllLines(
			Paths.get(fileName), StandardCharsets.UTF_8);

		StringBundler sb = new StringBundler(lines.size() * 2);

		for (String line : lines) {
			if (line.isEmpty() || line.startsWith(StringPool.DOUBLE_SLASH)) {
				continue;
			}

			sb.append(line);
			sb.append(StringPool.NEW_LINE);
		}

		db.runSQLTemplate(connection, sb.toString(), true);
	}

	private void _loadHypersonic(String outputDir) throws Exception {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:hsqldb:mem:testSampleSQLBuilderDB;shutdown=true", "sa",
				"")) {

			_loadHypersonic(connection, outputDir + "/sample-hypersonic.sql");

			try (Statement statement = connection.createStatement()) {
				statement.execute("SHUTDOWN COMPACT");
			}
		}
	}

	private static final String _SAMPLE_FTL_END =
		"<#include \"counters.ftl\">\n\nCOMMIT_TRANSACTION";

}