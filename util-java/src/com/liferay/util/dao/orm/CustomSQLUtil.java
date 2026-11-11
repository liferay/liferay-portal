/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.dao.orm;

import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Raymond Augé
 * @see    com.liferay.portal.dao.orm.custom.sql.CustomSQLUtil
 */
public class CustomSQLUtil {

	public static String appendCriteria(String sql, String criteria) {
		return _customSQLUtil._customSQL.appendCriteria(sql, criteria);
	}

	public static String get(String id) {
		return _customSQLUtil._customSQL.get(id);
	}

	public static String get(String id, QueryDefinition<?> queryDefinition) {
		return _customSQLUtil._customSQL.get(id, queryDefinition);
	}

	public static String get(
		String id, QueryDefinition<?> queryDefinition, String tableName) {

		return _customSQLUtil._customSQL.get(id, queryDefinition, tableName);
	}

	public static boolean isVendorDB2() {
		return _customSQLUtil._customSQL.isVendorDB2();
	}

	public static boolean isVendorHSQL() {
		return _customSQLUtil._customSQL.isVendorHSQL();
	}

	public static boolean isVendorInformix() {
		return _customSQLUtil._customSQL.isVendorInformix();
	}

	public static boolean isVendorMySQL() {
		return _customSQLUtil._customSQL.isVendorMySQL();
	}

	public static boolean isVendorOracle() {
		return _customSQLUtil._customSQL.isVendorOracle();
	}

	public static String[] keywords(String keywords) {
		return _customSQLUtil._customSQL.keywords(keywords);
	}

	public static String[] keywords(String keywords, boolean lowerCase) {
		return _customSQLUtil._customSQL.keywords(keywords, lowerCase);
	}

	public static String[] keywords(
		String keywords, boolean lowerCase, WildcardMode wildcardMode) {

		return _customSQLUtil._customSQL.keywords(
			keywords, lowerCase, wildcardMode);
	}

	public static String[] keywords(
		String keywords, WildcardMode wildcardMode) {

		return _customSQLUtil._customSQL.keywords(keywords, wildcardMode);
	}

	public static String[] keywords(String[] keywordsArray) {
		return _customSQLUtil._customSQL.keywords(keywordsArray);
	}

	public static String[] keywords(String[] keywordsArray, boolean lowerCase) {
		return _customSQLUtil._customSQL.keywords(keywordsArray, lowerCase);
	}

	public static void reloadCustomSQL() {
		_customSQLUtil._customSQL.reloadCustomSQL();
	}

	public static String removeGroupBy(String sql) {
		return _customSQLUtil._customSQL.removeGroupBy(sql);
	}

	public static String removeOrderBy(String sql) {
		return _customSQLUtil._customSQL.removeOrderBy(sql);
	}

	public static String replaceAndOperator(String sql, boolean andOperator) {
		return _customSQLUtil._customSQL.replaceAndOperator(sql, andOperator);
	}

	public static String replaceGroupBy(String sql, String groupBy) {
		return _customSQLUtil._customSQL.replaceGroupBy(sql, groupBy);
	}

	public static String replaceIsNull(String sql) {
		return _customSQLUtil._customSQL.replaceIsNull(sql);
	}

	public static String replaceKeywords(
		String sql, String field, boolean last, int[] values) {

		return _customSQLUtil._customSQL.replaceKeywords(
			sql, field, last, values);
	}

	public static String replaceKeywords(
		String sql, String field, boolean last, long[] values) {

		return _customSQLUtil._customSQL.replaceKeywords(
			sql, field, last, values);
	}

	public static String replaceKeywords(
		String sql, String field, String operator, boolean last,
		String[] values) {

		return _customSQLUtil._customSQL.replaceKeywords(
			sql, field, operator, last, values);
	}

	public static String replaceOrderBy(
		String sql, OrderByComparator<?> orderByComparator) {

		return _customSQLUtil._customSQL.replaceOrderBy(sql, orderByComparator);
	}

	private CustomSQLUtil() {
		CustomSQL customSQL = null;

		try {
			customSQL = new CustomSQL();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_customSQL = customSQL;
	}

	private static final Log _log = LogFactoryUtil.getLog(CustomSQLUtil.class);

	private static final CustomSQLUtil _customSQLUtil = new CustomSQLUtil();

	private final CustomSQL _customSQL;

}