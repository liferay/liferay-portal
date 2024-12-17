/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.FileReader;
import java.io.Reader;

import java.time.ZoneId;

import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class BenchmarksPropsValues {

	public static final String[] COMMERCE_LAYOUT_EXCLUDED_PORTLETS =
		StringUtil.split(
			PropertiesHolder._get(
				BenchmarksPropsKeys.COMMERCE_LAYOUT_EXCLUDED_PORTLETS));

	public static final DBType DB_TYPE = DBType.valueOf(
		StringUtil.toUpperCase(
			PropertiesHolder._get(BenchmarksPropsKeys.DB_TYPE)));

	public static final int MAX_ACCOUNT_ENTRY_COMMERCE_ORDER_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_ACCOUNT_ENTRY_COMMERCE_ORDER_COUNT));

	public static final int MAX_ACCOUNT_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_ACCOUNT_ENTRY_COUNT));

	public static final int MAX_ASSET_CATEGORY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_ASSET_CATEGORY_COUNT));

	public static final int MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT));

	public static final int MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT));

	public static final int MAX_ASSET_TAG_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_ASSET_TAG_COUNT));

	public static final int MAX_ASSET_VUCABULARY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_ASSET_VUCABULARY_COUNT));

	public static final int MAX_ASSETPUBLISHER_PAGE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_ASSETPUBLISHER_PAGE_COUNT));

	public static final int MAX_BLOGS_ENTRY_COMMENT_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_BLOGS_ENTRY_COMMENT_COUNT));

	public static final int MAX_BLOGS_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_BLOGS_ENTRY_COUNT));

	public static final int MAX_COMMERCE_CATALOG_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_COMMERCE_CATALOG_COUNT));

	public static final int MAX_COMMERCE_GROUP_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_COMMERCE_GROUP_COUNT));

	public static final int MAX_COMMERCE_INVENTORY_WAREHOUSE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_INVENTORY_WAREHOUSE_COUNT));

	public static final int MAX_COMMERCE_INVENTORY_WAREHOUSE_ITEM_QUANTITY =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.
					MAX_COMMERCE_INVENTORY_WAREHOUSE_ITEM_QUANTITY));

	public static final int MAX_COMMERCE_ORDER_STATUS_CANCELLED_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_ORDER_STATUS_CANCELLED_COUNT));

	public static final int MAX_COMMERCE_ORDER_STATUS_OPEN_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_ORDER_STATUS_OPEN_COUNT));

	public static final int MAX_COMMERCE_ORDER_STATUS_PENDING_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_ORDER_STATUS_PENDING_COUNT));

	public static final int MAX_COMMERCE_PRICE_LIST_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_PRICE_LIST_COUNT));

	public static final int MAX_COMMERCE_PRODUCT_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_COUNT));

	public static final int MAX_COMMERCE_PRODUCT_DEFINITION_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT));

	public static final int MAX_COMMERCE_PRODUCT_INSTANCE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_INSTANCE_COUNT));

	public static final int MAX_COMMERCE_PRODUCT_OPTION_CATEGORY_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.
					MAX_COMMERCE_PRODUCT_OPTION_CATEGORY_COUNT));

	public static final int MAX_COMPANY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_COMPANY_COUNT));

	public static final int MAX_COMPANY_USER_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_COMPANY_USER_COUNT));

	public static final int MAX_CONTENT_LAYOUT_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_CONTENT_LAYOUT_COUNT));

	public static final int MAX_CP_DEFINITION_ATTACHMENT_TYPE_IMAGE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.
					MAX_CP_DEFINITION_ATTACHMENT_TYPE_IMAGE_COUNT));

	public static final int MAX_CP_DEFINITION_ATTACHMENT_TYPE_PDF_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.
					MAX_CP_DEFINITION_ATTACHMENT_TYPE_PDF_COUNT));

	public static final int MAX_CP_DEFINITION_SPECIFICATION_OPTION_VALUE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.
					MAX_CP_DEFINITION_SPECIFICATION_OPTION_VALUE_COUNT));

	public static final int MAX_CP_SPECIFICATION_OPTION_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_CP_SPECIFICATION_OPTION_COUNT));

	public static final int MAX_DDL_CUSTOM_FIELD_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DDL_CUSTOM_FIELD_COUNT));

	public static final int MAX_DDL_RECORD_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DDL_RECORD_COUNT));

	public static final int MAX_DDL_RECORD_SET_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DDL_RECORD_SET_COUNT));

	public static final int MAX_DL_FILE_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DL_FILE_ENTRY_COUNT));

	public static final int MAX_DL_FILE_ENTRY_SIZE = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DL_FILE_ENTRY_SIZE));

	public static final int MAX_DL_FOLDER_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DL_FOLDER_COUNT));

	public static final int MAX_DL_FOLDER_DEPTH = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_DL_FOLDER_DEPTH));

	public static final int MAX_GROUP_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_GROUP_COUNT));

	public static final int MAX_JOURNAL_ARTICLE_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_COUNT));

	public static final int MAX_JOURNAL_ARTICLE_PAGE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_PAGE_COUNT));

	public static final int MAX_JOURNAL_ARTICLE_SIZE = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_SIZE));

	public static final int MAX_JOURNAL_ARTICLE_VERSION_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_VERSION_COUNT));

	public static final int MAX_LIST_TYPE_DEFINITION_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.MAX_LIST_TYPE_DEFINITION_COUNT));

	public static final int MAX_LIST_TYPE_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_LIST_TYPE_ENTRY_COUNT));

	public static final int MAX_MB_CATEGORY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_MB_CATEGORY_COUNT));

	public static final int MAX_MB_MESSAGE_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_MB_MESSAGE_COUNT));

	public static final int MAX_MB_THREAD_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_MB_THREAD_COUNT));

	public static final int MAX_OBJECT_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_OBJECT_ENTRY_COUNT));

	public static final int MAX_OBJECT_ENTRY_PAGE_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_OBJECT_ENTRY_PAGE_COUNT));

	public static final int MAX_SEGMENTS_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_SEGMENTS_ENTRY_COUNT));

	public static final int MAX_SEGMENTS_ENTRY_SEGMENTS_EXPERIENCE_COUNT =
		GetterUtil.getInteger(
			PropertiesHolder._get(
				BenchmarksPropsKeys.
					MAX_SEGMENTS_ENTRY_SEGMENTS_EXPERIENCE_COUNT));

	public static final int MAX_USER_TO_GROUP_COUNT = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.MAX_USER_TO_GROUP_COUNT));

	public static final int OPTIMIZE_BUFFER_SIZE = GetterUtil.getInteger(
		PropertiesHolder._get(BenchmarksPropsKeys.OPTIMIZE_BUFFER_SIZE));

	public static final String[] OUTPUT_CSV_FILE_NAMES = StringUtil.split(
		PropertiesHolder._get(BenchmarksPropsKeys.OUTPUT_CSV_FILE_NAMES));

	public static final boolean OUTPUT_MERGE = GetterUtil.getBoolean(
		PropertiesHolder._get(BenchmarksPropsKeys.OUTPUT_MERGE));

	public static final String SCRIPT = PropertiesHolder._get(
		BenchmarksPropsKeys.SCRIPT);

	public static final boolean SEARCH_BAR_ENABLED = GetterUtil.getBoolean(
		PropertiesHolder._get(BenchmarksPropsKeys.SEARCH_BAR_ENABLED));

	public static final String VIRTUAL_HOST_NAME = PropertiesHolder._get(
		BenchmarksPropsKeys.VIRTUAL_HOST_NAME);

	private static class PropertiesHolder {

		private static String _get(String key) {
			return _properties.getProperty(key);
		}

		private static final Properties _properties;

		static {
			Properties properties = new Properties();

			try (Reader reader = new FileReader(
					System.getProperty("sample-sql-properties"))) {

				properties.load(reader);

				TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("GMT")));
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}

			_properties = properties;
		}

	}

}