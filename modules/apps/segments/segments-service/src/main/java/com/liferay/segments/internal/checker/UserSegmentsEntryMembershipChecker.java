/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.checker;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.syntax.Types;

/**
 * @author Marcos Martins
 */
public class UserSegmentsEntryMembershipChecker {

	public static boolean isMember(
		String filterString, Map<String, Object> userAttributes) {

		String template = _buildGroovyTemplate(filterString);

		Class<?> clazz = _cachedScriptClasses.get(template);

		if (clazz == null) {
			Object lock = _locks.computeIfAbsent(template, o -> new Object());

			synchronized (lock) {
				clazz = _cachedScriptClasses.computeIfAbsent(
					template, _groovyShell.getClassLoader()::parseClass);
			}
		}

		Script script = null;

		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();

			script = (Script)constructor.newInstance();
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to evaluate filter: " + filterString, exception);
		}

		Binding binding = new Binding();

		binding.setVariable(
			"CLASS_PK", String.valueOf(userAttributes.get("classPK")));
		binding.setVariable("user", _getFilteredUserAttributes(userAttributes));

		script.setBinding(binding);

		return (boolean)script.invokeMethod("evaluate", null);
	}

	private static String _buildGroovyTemplate(String filterString) {
		String parsedFilterString = filterString;

		parsedFilterString = _processContainsOperations(parsedFilterString);
		parsedFilterString = _processLogicalOperations(parsedFilterString);
		parsedFilterString = _processNotOperations(parsedFilterString);
		parsedFilterString = _processOperations(parsedFilterString);

		return "def evaluate() {return " + parsedFilterString + " }";
	}

	private static String _getDateValueString(String input) throws Exception {
		Matcher matcher = _dateTimePattern.matcher(input);

		if (matcher.find()) {
			String group = matcher.group();

			return _dateTimeFormat.format(
				DateUtils.parseDate(group, _DATE_PATTERNS));
		}

		return null;
	}

	private static String _getFieldName(String key) {
		String fieldName = _fieldNames.get(StringUtil.trim(key));

		if (fieldName != null) {
			return fieldName;
		}

		return key;
	}

	private static Object _getFieldValue(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Boolean || value instanceof Date) {
			return value;
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			List<Object> list = new ArrayList<>();

			for (int i = 0; i < Array.getLength(value); i++) {
				list.add(_getFieldValue(Array.get(value, i)));
			}

			return list.toArray();
		}

		if (Validator.isBlank(value.toString())) {
			return null;
		}

		return StringUtil.lowerCase(value.toString());
	}

	private static Map<String, Object> _getFilteredUserAttributes(
		Map<String, Object> userAttributes) {

		Map<String, Object> filteredUserAttributes = new HashMap<>();

		userAttributes.forEach(
			(key, value) -> {
				Object fieldValue = _getFieldValue(value);

				if (Validator.isNotNull(fieldValue)) {
					filteredUserAttributes.put(key, fieldValue);
				}
			});

		return filteredUserAttributes;
	}

	private static String _getValue(String input) {
		Matcher matcher = _valuePattern.matcher(input);

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	private static String _processContainsOperations(String filterString) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _containsOperationPattern.matcher(filterString);

		while (matcher.find()) {
			String fieldName = matcher.group(1);
			String value = StringUtil.lowerCase(matcher.group(2));

			String replacement = StringBundler.concat(
				"((user['", _getFieldName(fieldName),
				"']?.toLowerCase().indexOf('", value, "') != null ? user['",
				_getFieldName(fieldName), "'].toLowerCase().indexOf('", value,
				"') : -1) >= 0)");

			matcher.appendReplacement(sb, replacement);
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static String _processLogicalOperations(String filterString) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _logicalOperationPattern.matcher(filterString);

		while (matcher.find()) {
			String group = matcher.group();

			matcher.appendReplacement(
				sb,
				StringUtil.quote(
					_operators.get(StringUtil.trim(group)), StringPool.SPACE));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static String _processNotOperations(String filterString) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _notOperationPattern.matcher(filterString);

		while (matcher.find()) {
			matcher.appendReplacement(sb, "!");
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static String _processOperations(String filterString) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _operationPattern.matcher(filterString);

		while (matcher.find()) {
			String fieldName = matcher.group(1);
			String operator = matcher.group(2);

			String value = _getValue(String.valueOf(matcher.group(3)));

			if ((value == null) || Validator.isBlank(operator) ||
				Validator.isBlank(value)) {

				continue;
			}

			try {
				if (_getDateValueString(value) != null) {
					value = StringBundler.concat(
						"Date.parse(\"yyyy-MM-dd'T'HH:mm:ss.SSSX\", \"", value,
						"\")");
				}
				else if (!StringUtil.equals(value, "CLASS_PK")) {
					value = StringUtil.lowerCase(value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			matcher.appendReplacement(
				sb,
				StringBundler.concat(
					"(user['", _getFieldName(fieldName),
					"'] instanceof Object[] ? ", value, " in user['",
					_getFieldName(fieldName), "'] : user['",
					_getFieldName(fieldName), "'] ",
					_operators.getOrDefault(operator, operator), " ", value,
					")"));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final String[] _DATE_PATTERNS = {
		"yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
		"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
	};

	private static final Map<String, Class<?>> _cachedScriptClasses =
		new ConcurrentHashMap<>();
	private static final Pattern _containsOperationPattern = Pattern.compile(
		"contains\\(((?:customField/)?\\w*), '([^')]*)'\\)");
	private static final DateFormat _dateTimeFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final Pattern _dateTimePattern = Pattern.compile(
		"\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}:\\d{2}.\\d{3}){0,1}((Z)|" +
			"((\\+|-)(\\d*))){0,1}");
	private static final Map<String, String> _fieldNames = HashMapBuilder.put(
		"dateModified", "modifiedDate"
	).build();
	private static final GroovyShell _groovyShell = new GroovyShell(
		new CompilerConfiguration() {
			{
				addCompilationCustomizers(
					new SecureASTCustomizer() {
						{
							setImportsWhitelist(Collections.emptyList());
							setReceiversWhiteList(
								List.of(
									Date.class.getName(),
									Object.class.getName(),
									String.class.getName()));
							setTokensWhitelist(
								List.of(
									Types.COMPARE_EQUAL,
									Types.COMPARE_GREATER_THAN,
									Types.COMPARE_GREATER_THAN_EQUAL,
									Types.COMPARE_LESS_THAN,
									Types.COMPARE_LESS_THAN_EQUAL,
									Types.COMPARE_NOT_EQUAL, Types.KEYWORD_DEF,
									Types.KEYWORD_FALSE, Types.KEYWORD_IN,
									Types.KEYWORD_INSTANCEOF,
									Types.KEYWORD_TRUE, Types.LEFT_PARENTHESIS,
									Types.LEFT_SQUARE_BRACKET,
									Types.LOGICAL_AND, Types.LOGICAL_OR,
									Types.NOT, Types.RIGHT_PARENTHESIS,
									Types.RIGHT_SQUARE_BRACKET));
						}
					});
			}
		});
	private static final Map<String, Object> _locks = new ConcurrentHashMap<>();
	private static final Pattern _logicalOperationPattern = Pattern.compile(
		"\\s+(and|or)\\s+");
	private static final Pattern _notOperationPattern = Pattern.compile(
		"not(?=\\s*\\()");
	private static final Pattern _operationPattern = Pattern.compile(
		StringBundler.concat(
			"((?:customField/)?\\w*)\\s+(eq|ge|gt|in|le|lt)\\s+",
			"('([^')]*)'|\\('([^')]*)'\\)|false|true|CLASS_PK|",
			"\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}:\\d{2}.\\d{3}){0,1}((Z)|",
			"((\\+|\\-)(\\d*))){0,1})"));
	private static final Map<String, String> _operators = HashMapBuilder.put(
		"and", "&&"
	).put(
		"eq", "=="
	).put(
		"ge", ">="
	).put(
		"gt", ">"
	).put(
		"le", "<="
	).put(
		"lt", "<"
	).put(
		"not", "!"
	).put(
		"or", "||"
	).build();
	private static final Pattern _valuePattern = Pattern.compile(
		"'([^')]*)'|false|true|CLASS_PK|" +
			"'{0,1}\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}:\\d{2}.\\d{3})" +
				"{0,1}((Z)|((\\+|-)(\\d*))){0,1}'{0,1}");

}