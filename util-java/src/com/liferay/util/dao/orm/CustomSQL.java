/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.dao.orm;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Raymond Augé
 * @see    com.liferay.portal.dao.orm.custom.sql.CustomSQL
 */
public class CustomSQL {

	public static final String DB2_FUNCTION_IS_NOT_NULL =
		"CAST(? AS VARCHAR(32672)) IS NOT NULL";

	public static final String DB2_FUNCTION_IS_NULL =
		"CAST(? AS VARCHAR(32672)) IS NULL";

	public static final String INFORMIX_FUNCTION_IS_NOT_NULL =
		"NOT lportal.isnull(?)";

	public static final String INFORMIX_FUNCTION_IS_NULL = "lportal.isnull(?)";

	public static final String MYSQL_FUNCTION_IS_NOT_NULL =
		"IFNULL(?, '1') = '0'";

	public static final String MYSQL_FUNCTION_IS_NULL = "IFNULL(?, '1') = '1'";

	public CustomSQL() throws SQLException {
		reloadCustomSQL();
	}

	public String appendCriteria(String sql, String criteria) {
		if (Validator.isNull(criteria)) {
			return sql;
		}

		if (!criteria.startsWith(StringPool.SPACE)) {
			criteria = StringPool.SPACE.concat(criteria);
		}

		if (!criteria.endsWith(StringPool.SPACE)) {
			criteria = criteria.concat(StringPool.SPACE);
		}

		int pos = sql.indexOf(_GROUP_BY_CLAUSE);

		if (pos != -1) {
			return StringBundler.concat(
				sql.substring(0, pos + 1), criteria, sql.substring(pos + 1));
		}

		pos = sql.indexOf(_ORDER_BY_CLAUSE);

		if (pos != -1) {
			return StringBundler.concat(
				sql.substring(0, pos + 1), criteria, sql.substring(pos + 1));
		}

		return sql.concat(criteria);
	}

	public String get(String id) {
		return _sqlPool.get(id);
	}

	public String get(String id, QueryDefinition<?> queryDefinition) {
		return get(id, queryDefinition, StringPool.BLANK);
	}

	public String get(
		String id, QueryDefinition<?> queryDefinition, String tableName) {

		String sql = get(id);

		if (!Validator.isBlank(tableName) &&
			!tableName.endsWith(StringPool.PERIOD)) {

			tableName = tableName.concat(StringPool.PERIOD);
		}

		if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
			sql = StringUtil.replace(
				sql, _STATUS_KEYWORD, _STATUS_CONDITION_EMPTY);
		}
		else {
			if (queryDefinition.isExcludeStatus()) {
				sql = StringUtil.replace(
					sql, _STATUS_KEYWORD,
					tableName.concat(_STATUS_CONDITION_INVERSE));
			}
			else {
				sql = StringUtil.replace(
					sql, _STATUS_KEYWORD,
					tableName.concat(_STATUS_CONDITION_DEFAULT));
			}
		}

		if (queryDefinition.getOwnerUserId() > 0) {
			if (queryDefinition.isIncludeOwner()) {
				sql = StringUtil.replace(
					sql,
					new String[] {
						_OWNER_USER_ID_KEYWORD, _OWNER_USER_ID_AND_OR_CONNECTOR
					},
					new String[] {
						StringBundler.concat(
							StringPool.OPEN_PARENTHESIS, tableName,
							_OWNER_USER_ID_CONDITION_DEFAULT, " AND ",
							tableName, _STATUS_CONDITION_INVERSE,
							StringPool.CLOSE_PARENTHESIS),
						" OR "
					});
			}
			else {
				sql = StringUtil.replace(
					sql,
					new String[] {
						_OWNER_USER_ID_KEYWORD, _OWNER_USER_ID_AND_OR_CONNECTOR
					},
					new String[] {
						tableName.concat(_OWNER_USER_ID_CONDITION_DEFAULT),
						" AND "
					});
			}
		}
		else {
			sql = StringUtil.removeSubstrings(
				sql, _OWNER_USER_ID_KEYWORD, _OWNER_USER_ID_AND_OR_CONNECTOR);
		}

		return sql;
	}

	/**
	 * Returns <code>true</code> if Hibernate is connecting to a DB2 database.
	 *
	 * @return <code>true</code> if Hibernate is connecting to a DB2 database
	 */
	public boolean isVendorDB2() {
		return _vendorDB2;
	}

	/**
	 * Returns <code>true</code> if Hibernate is connecting to a Hypersonic
	 * database.
	 *
	 * @return <code>true</code> if Hibernate is connecting to a Hypersonic
	 *         database
	 */
	public boolean isVendorHSQL() {
		return _vendorHSQL;
	}

	/**
	 * Returns <code>true</code> if Hibernate is connecting to an Informix
	 * database.
	 *
	 * @return <code>true</code> if Hibernate is connecting to an Informix
	 *         database
	 */
	public boolean isVendorInformix() {
		return _vendorInformix;
	}

	/**
	 * Returns <code>true</code> if Hibernate is connecting to a MySQL database.
	 *
	 * @return <code>true</code> if Hibernate is connecting to a MySQL database
	 */
	public boolean isVendorMySQL() {
		return _vendorMySQL;
	}

	/**
	 * Returns <code>true</code> if Hibernate is connecting to an Oracle
	 * database. Oracle has a nasty bug where it treats '' as a
	 * <code>NULL</code> value. See
	 * http://thedailywtf.com/forums/thread/26879.aspx for more information on
	 * this nasty bug.
	 *
	 * @return <code>true</code> if Hibernate is connecting to an Oracle
	 *         database
	 */
	public boolean isVendorOracle() {
		return _vendorOracle;
	}

	/**
	 * Returns <code>true</code> if Hibernate is connecting to a PostgreSQL
	 * database.
	 *
	 * @return <code>true</code> if Hibernate is connecting to a PostgreSQL
	 *         database
	 */
	public boolean isVendorPostgreSQL() {
		return _vendorPostgreSQL;
	}

	public String[] keywords(String keywords) {
		return keywords(keywords, true, WildcardMode.SURROUND);
	}

	public String[] keywords(String keywords, boolean lowerCase) {
		return keywords(keywords, lowerCase, WildcardMode.SURROUND);
	}

	public String[] keywords(
		String keywords, boolean lowerCase, WildcardMode wildcardMode) {

		if (Validator.isNull(keywords)) {
			return new String[] {null};
		}

		if (_CUSTOM_SQL_AUTO_ESCAPE_WILDCARDS_ENABLED) {
			keywords = _escapeWildCards(keywords);
		}

		if (lowerCase) {
			keywords = StringUtil.toLowerCase(keywords);
		}

		keywords = keywords.trim();

		List<String> keywordsList = new ArrayList<>();

		for (int i = 0; i < keywords.length(); i++) {
			char c = keywords.charAt(i);

			if (c == CharPool.QUOTE) {
				int pos = i + 1;

				i = keywords.indexOf(CharPool.QUOTE, pos);

				if (i == -1) {
					i = keywords.length();
				}

				if (i > pos) {
					String keyword = keywords.substring(pos, i);

					keywordsList.add(insertWildcard(keyword, wildcardMode));
				}
			}
			else {
				while (Character.isWhitespace(c)) {
					i++;

					c = keywords.charAt(i);
				}

				int pos = i;

				while (!Character.isWhitespace(c)) {
					i++;

					if (i == keywords.length()) {
						break;
					}

					c = keywords.charAt(i);
				}

				String keyword = keywords.substring(pos, i);

				keywordsList.add(insertWildcard(keyword, wildcardMode));
			}
		}

		return keywordsList.toArray(new String[0]);
	}

	public String[] keywords(String keywords, WildcardMode wildcardMode) {
		return keywords(keywords, true, wildcardMode);
	}

	public String[] keywords(String[] keywordsArray) {
		return keywords(keywordsArray, true);
	}

	public String[] keywords(String[] keywordsArray, boolean lowerCase) {
		if (ArrayUtil.isEmpty(keywordsArray)) {
			return new String[] {null};
		}

		if (lowerCase) {
			for (int i = 0; i < keywordsArray.length; i++) {
				keywordsArray[i] = StringUtil.lowerCase(keywordsArray[i]);
			}
		}

		return keywordsArray;
	}

	public void reloadCustomSQL() throws SQLException {
		String functionIsNull = PortalUtil.getCustomSQLFunctionIsNull();
		String functionIsNotNull = PortalUtil.getCustomSQLFunctionIsNotNull();

		try (Connection connection = DataAccess.getConnection()) {
			if (Validator.isNotNull(functionIsNull) &&
				Validator.isNotNull(functionIsNotNull)) {

				_functionIsNull = functionIsNull;
				_functionIsNotNull = functionIsNotNull;

				if (_log.isDebugEnabled()) {
					_log.debug(
						"functionIsNull is manually set to " + functionIsNull);
					_log.debug(
						"functionIsNotNull is manually set to " +
							functionIsNotNull);
				}
			}
			else if (connection != null) {
				DatabaseMetaData metaData = connection.getMetaData();

				String dbName = GetterUtil.getString(
					metaData.getDatabaseProductName());

				if (_log.isInfoEnabled()) {
					_log.info("Database name " + dbName);
				}

				if (dbName.startsWith("DB2")) {
					_vendorDB2 = true;
					_functionIsNull = DB2_FUNCTION_IS_NULL;
					_functionIsNotNull = DB2_FUNCTION_IS_NOT_NULL;

					if (_log.isInfoEnabled()) {
						_log.info("Detected DB2 with database name " + dbName);
					}
				}
				else if (dbName.startsWith("HSQL")) {
					_vendorHSQL = true;

					if (_log.isInfoEnabled()) {
						_log.info("Detected HSQL with database name " + dbName);
					}
				}
				else if (dbName.startsWith("Informix")) {
					_vendorInformix = true;
					_functionIsNull = INFORMIX_FUNCTION_IS_NULL;
					_functionIsNotNull = INFORMIX_FUNCTION_IS_NOT_NULL;

					if (_log.isInfoEnabled()) {
						_log.info(
							"Detected Informix with database name " + dbName);
					}
				}
				else if (dbName.startsWith("MySQL")) {
					_vendorMySQL = true;
					//_functionIsNull = MYSQL_FUNCTION_IS_NULL;
					//_functionIsNotNull = MYSQL_FUNCTION_IS_NOT_NULL;

					if (_log.isInfoEnabled()) {
						_log.info(
							"Detected MySQL with database name " + dbName);
					}
				}
				else if (dbName.startsWith("Oracle")) {
					_vendorOracle = true;

					if (_log.isInfoEnabled()) {
						_log.info(
							"Detected Oracle with database name " + dbName);
					}
				}
				else if (dbName.startsWith("PostgreSQL")) {
					_vendorPostgreSQL = true;

					if (_log.isInfoEnabled()) {
						_log.info(
							"Detected PostgreSQL with database name " + dbName);
					}
				}
				else {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to detect database with name " + dbName);
					}
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		try {
			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			String[] configs = getConfigs();

			Map<String, String> sqlPool = new HashMap<>();

			for (String config : configs) {
				_read(classLoader, config, sqlPool);
			}

			_sqlPool = sqlPool;
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	public String removeGroupBy(String sql) {
		int x = sql.indexOf(_GROUP_BY_CLAUSE);

		if (x != -1) {
			int y = sql.indexOf(_ORDER_BY_CLAUSE);

			if (y == -1) {
				sql = sql.substring(0, x);
			}
			else {
				sql = sql.substring(0, x) + sql.substring(y);
			}
		}

		return sql;
	}

	public String removeOrderBy(String sql) {
		int pos = sql.indexOf(_ORDER_BY_CLAUSE);

		if (pos != -1) {
			sql = sql.substring(0, pos);
		}

		return sql;
	}

	public String replaceAndOperator(String sql, boolean andOperator) {
		String andOrConnector = "OR";
		String andOrNullCheck = "AND ? IS NOT NULL";

		if (andOperator) {
			andOrConnector = "AND";
			andOrNullCheck = "OR ? IS NULL";
		}

		sql = StringUtil.replace(
			sql, new String[] {"[$AND_OR_CONNECTOR$]", "[$AND_OR_NULL_CHECK$]"},
			new String[] {andOrConnector, andOrNullCheck});

		if (_vendorPostgreSQL) {
			sql = StringUtil.replace(
				sql,
				new String[] {
					"Date >= ? AND ? IS NOT NULL",
					"Date <= ? AND ? IS NOT NULL", "Date >= ? OR ? IS NULL",
					"Date <= ? OR ? IS NULL"
				},
				new String[] {
					"Date >= ? AND CAST(? AS TIMESTAMP) IS NOT NULL",
					"Date <= ? AND CAST(? AS TIMESTAMP) IS NOT NULL",
					"Date >= ? OR CAST(? AS TIMESTAMP) IS NULL",
					"Date <= ? OR CAST(? AS TIMESTAMP) IS NULL"
				});
		}

		return replaceIsNull(sql);
	}

	public String replaceGroupBy(String sql, String groupBy) {
		if (groupBy == null) {
			return sql;
		}

		int x = sql.indexOf(_GROUP_BY_CLAUSE);

		if (x != -1) {
			int y = sql.indexOf(_ORDER_BY_CLAUSE);

			if (y == -1) {
				sql = sql.substring(0, x + _GROUP_BY_CLAUSE.length());

				sql = sql.concat(groupBy);
			}
			else {
				sql = StringBundler.concat(
					sql.substring(0, x + _GROUP_BY_CLAUSE.length()), groupBy,
					sql.substring(y));
			}
		}
		else {
			int y = sql.indexOf(_ORDER_BY_CLAUSE);

			if (y == -1) {
				sql = StringBundler.concat(sql, _GROUP_BY_CLAUSE, groupBy);
			}
			else {
				sql = StringBundler.concat(
					sql.substring(0, y), _GROUP_BY_CLAUSE, groupBy,
					sql.substring(y));
			}
		}

		return sql;
	}

	public String replaceIsNull(String sql) {
		if (Validator.isNotNull(_functionIsNull)) {
			sql = StringUtil.replace(
				sql, new String[] {"? IS NULL", "? IS NOT NULL"},
				new String[] {_functionIsNull, _functionIsNotNull});
		}

		return sql;
	}

	public String replaceKeywords(
		String sql, String field, boolean last, int[] values) {

		if ((values != null) && (values.length == 1)) {
			return sql;
		}

		StringBundler oldSqlSB = new StringBundler(4);

		oldSqlSB.append(StringPool.OPEN_PARENTHESIS);
		oldSqlSB.append(field);
		oldSqlSB.append(" = ?)");

		if (!last) {
			oldSqlSB.append(" [$AND_OR_CONNECTOR$]");
		}

		if (ArrayUtil.isEmpty(values)) {
			return StringUtil.removeSubstring(sql, oldSqlSB.toString());
		}

		StringBundler newSqlSB = new StringBundler((values.length * 4) + 3);

		newSqlSB.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				newSqlSB.append(" OR ");
			}

			newSqlSB.append(StringPool.OPEN_PARENTHESIS);
			newSqlSB.append(field);
			newSqlSB.append(" = ?)");
		}

		newSqlSB.append(StringPool.CLOSE_PARENTHESIS);

		if (!last) {
			newSqlSB.append(" [$AND_OR_CONNECTOR$]");
		}

		return StringUtil.replace(
			sql, oldSqlSB.toString(), newSqlSB.toString());
	}

	public String replaceKeywords(
		String sql, String field, boolean last, long[] values) {

		if ((values != null) && (values.length == 1)) {
			return sql;
		}

		StringBundler oldSqlSB = new StringBundler(4);

		oldSqlSB.append(StringPool.OPEN_PARENTHESIS);
		oldSqlSB.append(field);
		oldSqlSB.append(" = ?)");

		if (!last) {
			oldSqlSB.append(" [$AND_OR_CONNECTOR$]");
		}

		if (ArrayUtil.isEmpty(values)) {
			return StringUtil.removeSubstring(sql, oldSqlSB.toString());
		}

		StringBundler newSqlSB = new StringBundler((values.length * 4) + 3);

		newSqlSB.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				newSqlSB.append(" OR ");
			}

			newSqlSB.append(StringPool.OPEN_PARENTHESIS);
			newSqlSB.append(field);
			newSqlSB.append(" = ?)");
		}

		newSqlSB.append(StringPool.CLOSE_PARENTHESIS);

		if (!last) {
			newSqlSB.append(" [$AND_OR_CONNECTOR$]");
		}

		return StringUtil.replace(
			sql, oldSqlSB.toString(), newSqlSB.toString());
	}

	public String replaceKeywords(
		String sql, String field, String operator, boolean last,
		String[] values) {

		if ((values != null) && (values.length <= 1)) {
			return sql;
		}

		StringBundler oldSqlSB = new StringBundler(6);

		oldSqlSB.append(StringPool.OPEN_PARENTHESIS);
		oldSqlSB.append(field);
		oldSqlSB.append(" ");
		oldSqlSB.append(operator);
		oldSqlSB.append(" ? [$AND_OR_NULL_CHECK$])");

		if (!last) {
			oldSqlSB.append(" [$AND_OR_CONNECTOR$]");
		}

		StringBundler newSqlSB = new StringBundler((values.length * 6) + 2);

		newSqlSB.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				newSqlSB.append(" OR ");
			}

			newSqlSB.append(StringPool.OPEN_PARENTHESIS);
			newSqlSB.append(field);
			newSqlSB.append(" ");
			newSqlSB.append(operator);
			newSqlSB.append(" ? [$AND_OR_NULL_CHECK$])");
		}

		newSqlSB.append(StringPool.CLOSE_PARENTHESIS);

		if (!last) {
			newSqlSB.append(" [$AND_OR_CONNECTOR$]");
		}

		return StringUtil.replace(
			sql, oldSqlSB.toString(), newSqlSB.toString());
	}

	public String replaceOrderBy(
		String sql, OrderByComparator<?> orderByComparator) {

		if (orderByComparator == null) {
			return sql;
		}

		String orderBy = orderByComparator.getOrderBy();

		int pos = sql.indexOf(_ORDER_BY_CLAUSE);

		if ((pos != -1) && (pos < sql.length())) {
			sql = sql.substring(0, pos + _ORDER_BY_CLAUSE.length());

			sql = sql.concat(orderBy);
		}
		else {
			sql = StringBundler.concat(sql, _ORDER_BY_CLAUSE, orderBy);
		}

		return sql;
	}

	protected String[] getConfigs() {
		ClassLoader classLoader = CustomSQL.class.getClassLoader();

		if (PortalClassLoaderUtil.isPortalClassLoader(classLoader)) {
			Properties propsUtil = PortalUtil.getPortalProperties();

			return StringUtil.split(
				propsUtil.getProperty("custom.sql.configs"));
		}

		if (classLoader.getResource("portlet.properties") != null) {
			Configuration configuration =
				ConfigurationFactoryUtil.getConfiguration(
					classLoader, "portlet");

			return ArrayUtil.append(
				StringUtil.split(configuration.get("custom.sql.configs")),
				new String[] {
					"custom-sql/default.xml", "META-INF/custom-sql/default.xml"
				});
		}

		return new String[] {
			"custom-sql/default.xml", "META-INF/custom-sql/default.xml"
		};
	}

	protected String insertWildcard(String keyword, WildcardMode wildcardMode) {
		if (wildcardMode == WildcardMode.LEADING) {
			return StringPool.PERCENT.concat(keyword);
		}
		else if (wildcardMode == WildcardMode.SURROUND) {
			return StringUtil.quote(keyword, StringPool.PERCENT);
		}
		else if (wildcardMode == WildcardMode.TRAILING) {
			return keyword.concat(StringPool.PERCENT);
		}

		throw new IllegalArgumentException(
			"Invalid wildcard mode " + wildcardMode);
	}

	protected String transform(String sql) {
		sql = PortalUtil.transformCustomSQL(sql);

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(sql))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith(StringPool.CLOSE_PARENTHESIS)) {
					sb.setIndex(sb.index() - 1);
				}

				sb.append(line);

				if (line.endsWith(StringPool.OPEN_PARENTHESIS)) {
					continue;
				}

				sb.append(StringPool.SPACE);
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return sql;
		}

		return sb.toString();
	}

	private String _escapeWildCards(String keywords) {
		if (!isVendorMySQL() && !isVendorOracle()) {
			return keywords;
		}

		StringBuilder sb = new StringBuilder(keywords);

		for (int i = 0; i < sb.length(); ++i) {
			char c = sb.charAt(i);

			if (c == CharPool.BACK_SLASH) {
				i++;

				continue;
			}

			if ((c != CharPool.UNDERLINE) && (c != CharPool.PERCENT)) {
				continue;
			}

			sb.insert(i, CharPool.BACK_SLASH);

			i++;
		}

		return sb.toString();
	}

	private void _read(
			ClassLoader classLoader, String source, Map<String, String> sqlPool)
		throws Exception {

		try (InputStream inputStream = classLoader.getResourceAsStream(
				source)) {

			if (inputStream == null) {
				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Loading " + source);
			}

			Document document = UnsecureSAXReaderUtil.read(inputStream);

			Element rootElement = document.getRootElement();

			for (Element sqlElement : rootElement.elements("sql")) {
				String file = sqlElement.attributeValue("file");

				if (Validator.isNotNull(file)) {
					_read(classLoader, file, sqlPool);
				}
				else {
					String id = sqlElement.attributeValue("id");

					String content = transform(sqlElement.getText());

					content = replaceIsNull(content);

					sqlPool.put(id, content);
				}
			}
		}
	}

	private static final boolean _CUSTOM_SQL_AUTO_ESCAPE_WILDCARDS_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.CUSTOM_SQL_AUTO_ESCAPE_WILDCARDS_ENABLED));

	private static final String _GROUP_BY_CLAUSE = " GROUP BY ";

	private static final String _ORDER_BY_CLAUSE = " ORDER BY ";

	private static final String _OWNER_USER_ID_AND_OR_CONNECTOR =
		"[$OWNER_USER_ID_AND_OR_CONNECTOR$]";

	private static final String _OWNER_USER_ID_CONDITION_DEFAULT = "userId = ?";

	private static final String _OWNER_USER_ID_KEYWORD = "[$OWNER_USER_ID$]";

	private static final String _STATUS_CONDITION_DEFAULT = "status = ?";

	private static final String _STATUS_CONDITION_EMPTY =
		WorkflowConstants.STATUS_ANY + " = ?";

	private static final String _STATUS_CONDITION_INVERSE = "status != ?";

	private static final String _STATUS_KEYWORD = "[$STATUS$]";

	private static final Log _log = LogFactoryUtil.getLog(CustomSQL.class);

	private String _functionIsNotNull;
	private String _functionIsNull;
	private volatile Map<String, String> _sqlPool = Collections.emptyMap();
	private boolean _vendorDB2;
	private boolean _vendorHSQL;
	private boolean _vendorInformix;
	private boolean _vendorMySQL;
	private boolean _vendorOracle;
	private boolean _vendorPostgreSQL;

}