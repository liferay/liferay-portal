/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.search.experiences.blueprint.parameter.SXPParameter;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributor;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributorDefinition;
import com.liferay.search.experiences.internal.blueprint.parameter.BooleanSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.DateSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.DoubleSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.FloatSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.IntegerArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.IntegerSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.LongArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.LongSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.internal.blueprint.parameter.StringArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.StringSXPParameter;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.Parameter;
import com.liferay.search.experiences.rest.dto.v1_0.ParameterConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;

import java.beans.ExceptionListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * @author Petteri Karttunen
 */
public class SXPParameterDataCreatorUtil {

	public static SXPParameterData create(
		ExceptionListener exceptionListener, SearchContext searchContext,
		SXPBlueprint sxpBlueprint,
		SXPParameterContributor[] sxpParameterContributors) {

		Map<String, SXPParameter> sxpParameters = new LinkedHashMap<>();

		String keywords = _addKeywordsSXPParameters(
			searchContext, sxpParameters);

		Configuration configuration = sxpBlueprint.getConfiguration();

		if (configuration != null) {
			_addSXPParameters(
				configuration.getParameterConfiguration(), searchContext,
				sxpParameters);
		}

		_contribute(
			exceptionListener, searchContext, sxpParameters,
			sxpParameterContributors);

		return new SXPParameterData(keywords, sxpParameters);
	}

	private static void _add(
		SXPParameter sxpParameter, Map<String, SXPParameter> sxpParameters) {

		sxpParameters.put(sxpParameter.getName(), sxpParameter);
	}

	private static String _addKeywordsSXPParameters(
		SearchContext searchContext, Map<String, SXPParameter> sxpParameters) {

		String keywords = GetterUtil.getString(searchContext.getKeywords());

		if ((StringUtil.count(keywords, CharPool.QUOTE) % 2) != 0) {
			keywords = StringUtil.replace(
				keywords, CharPool.QUOTE, StringPool.BLANK);
		}

		keywords = keywords.replaceAll("/", "&#8725;");
		keywords = keywords.replaceAll("\"", "&#34;");
		keywords = keywords.replaceAll("\\$", "&#36;");
		keywords = keywords.replaceAll("\\[", "&#91;");
		keywords = keywords.replaceAll("\\\\", "&#92;");
		keywords = keywords.replaceAll("\\]", "&#93;");

		_add(new StringSXPParameter("keywords", true, keywords), sxpParameters);

		return keywords;
	}

	private static void _addSXPParameter(
		SearchContext searchContext,
		SXPParameterContributorDefinition sxpParameterContributorDefinition,
		Map<String, SXPParameter> sxpParameters) {

		String name = StringUtils.substringBetween(
			sxpParameterContributorDefinition.getTemplateVariable(),
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE,
			StringPool.CLOSE_CURLY_BRACE);

		Object object = searchContext.getAttribute(name);

		if (object == null) {
			return;
		}

		SXPParameter sxpParameter = _getSXPParameter(
			name, object, new Parameter(), searchContext,
			_getType(sxpParameterContributorDefinition));

		if (sxpParameter == null) {
			return;
		}

		_add(sxpParameter, sxpParameters);
	}

	private static void _addSXPParameter(
		String name, Parameter parameter, SearchContext searchContext,
		Map<String, SXPParameter> sxpParameters) {

		Object object = searchContext.getAttribute(name);

		if (object == null) {
			return;
		}

		SXPParameter sxpParameter = _getSXPParameter(
			name, object, parameter, searchContext, parameter.getType());

		if (sxpParameter == null) {
			return;
		}

		_add(sxpParameter, sxpParameters);
	}

	private static void _addSXPParameters(
		ParameterConfiguration parameterConfiguration,
		SearchContext searchContext, Map<String, SXPParameter> sxpParameters) {

		if (parameterConfiguration == null) {
			return;
		}

		MapUtil.isNotEmptyForEach(
			parameterConfiguration.getParameters(),
			(name, parameter) -> _addSXPParameter(
				name, parameter, searchContext, sxpParameters));
	}

	private static void _contribute(
		ExceptionListener exceptionListener, SearchContext searchContext,
		Map<String, SXPParameter> sxpParameters,
		SXPParameterContributor[] sxpParameterContributors) {

		if (ArrayUtil.isEmpty(sxpParameterContributors)) {
			return;
		}

		for (SXPParameterContributor sxpParameterContributor :
				sxpParameterContributors) {

			Set<SXPParameter> set = new LinkedHashSet<>();

			sxpParameterContributor.contribute(
				exceptionListener, searchContext, set);

			for (SXPParameter sxpParameter : set) {
				_add(sxpParameter, sxpParameters);
			}

			List<SXPParameterContributorDefinition>
				sxpParameterContributorDefinitions =
					sxpParameterContributor.
						getSXPParameterContributorDefinitions(
							searchContext.getCompanyId(),
							searchContext.getLocale());

			if (ListUtil.isNotEmpty(sxpParameterContributorDefinitions)) {
				for (SXPParameterContributorDefinition
						sxpParameterContributorDefinition :
							sxpParameterContributorDefinitions) {

					_addSXPParameter(
						searchContext, sxpParameterContributorDefinition,
						sxpParameters);
				}
			}
		}
	}

	private static Double _fit(Double maxValue, Double minValue, Double value) {
		if ((minValue != null) && (value < minValue)) {
			return minValue;
		}

		if ((maxValue != null) && (value > maxValue)) {
			return maxValue;
		}

		return value;
	}

	private static Float _fit(Float maxValue, Float minValue, Float value) {
		if ((minValue != null) && (value < minValue)) {
			return minValue;
		}

		if ((maxValue != null) && (value > maxValue)) {
			return maxValue;
		}

		return value;
	}

	private static Integer _fit(
		Integer maxValue, Integer minValue, Integer value) {

		if ((minValue != null) && (value < minValue)) {
			return minValue;
		}

		if ((maxValue != null) && (value > maxValue)) {
			return maxValue;
		}

		return value;
	}

	private static Long _fit(Long maxValue, Long minValue, Long value) {
		if ((minValue != null) && (value < minValue)) {
			return minValue;
		}

		if ((maxValue != null) && (value > maxValue)) {
			return maxValue;
		}

		return value;
	}

	private static Boolean _getBoolean(Boolean defaultValue, Object object) {
		if (object != null) {
			return GetterUtil.getBoolean(object);
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static SXPParameter _getBooleanSXPParameter(
		String name, Object object, Parameter parameter) {

		Boolean value = _getBoolean(
			(Boolean)parameter.getDefaultValue(), object);

		if (value == null) {
			return null;
		}

		return new BooleanSXPParameter(name, true, value);
	}

	private static SXPParameter _getDateSXPParameter(
		String name, Object object, TimeZone timeZone, Parameter parameter) {

		String value = _getString(null, object);

		if (value == null) {
			return null;
		}

		LocalDate localDate = LocalDate.parse(
			value, DateTimeFormatter.ofPattern(parameter.getFormat()));

		Calendar calendar = GregorianCalendar.from(
			localDate.atStartOfDay(timeZone.toZoneId()));

		Date date = calendar.getTime();

		if (date == null) {
			return null;
		}

		return new DateSXPParameter(name, true, date);
	}

	private static Double _getDouble(Double defaultValue, Object object) {
		if (object != null) {
			return GetterUtil.getDouble(object);
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static SXPParameter _getDoubleSXPParameter(
		String name, Object object, Parameter parameter) {

		Double value = _getDouble((Double)parameter.getDefaultValue(), object);

		if (value == null) {
			return null;
		}

		return new DoubleSXPParameter(
			name, true,
			_fit(
				(Double)parameter.getMax(), (Double)parameter.getMin(), value));
	}

	private static Float _getFloat(Float defaultValue, Object object) {
		if (object != null) {
			return GetterUtil.getFloat(object);
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static SXPParameter _getFloatSXPParameter(
		String name, Object object, Parameter parameter) {

		Float value = _getFloat((Float)parameter.getDefaultValue(), object);

		if (value == null) {
			return null;
		}

		return new FloatSXPParameter(
			name, true,
			_fit((Float)parameter.getMax(), (Float)parameter.getMin(), value));
	}

	private static Integer _getInteger(Integer defaultValue, Object object) {
		if (object != null) {
			return GetterUtil.getInteger(object);
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static Integer[] _getIntegerArray(
		Integer[] defaultValue, Object object) {

		if (object instanceof String) {
			return ArrayUtil.toArray(
				GetterUtil.getIntegerValues(StringUtil.split((String)object)));
		}

		if (object != null) {
			int[] fromArray = GetterUtil.getIntegerValues(object);

			if (ArrayUtil.isNotEmpty(fromArray)) {
				return ArrayUtil.toArray(fromArray);
			}
			else if (object instanceof List<?> list) {
				if (list.stream().allMatch(e -> e instanceof Integer)) {
					return list.toArray(new Integer[0]);
				}
			}
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static SXPParameter _getIntegerArraySXPParameter(
		String name, Object object, Parameter parameter) {

		Integer[] value = _getIntegerArray(
			(Integer[])parameter.getDefaultValue(), object);

		if (ArrayUtil.isEmpty(value)) {
			return null;
		}

		return new IntegerArraySXPParameter(name, true, value);
	}

	private static SXPParameter _getIntegerSXPParameter(
		String name, Object object, Parameter parameter) {

		Integer value = _getInteger(
			(Integer)parameter.getDefaultValue(), object);

		if (value == null) {
			return null;
		}

		return new IntegerSXPParameter(
			name, true,
			_fit(
				(Integer)parameter.getMax(), (Integer)parameter.getMin(),
				value));
	}

	private static Long _getLong(Long defaultValue, Object object) {
		if (object != null) {
			return GetterUtil.getLong(object);
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static Long[] _getLongArray(Long[] defaultValue, Object object) {
		if (object instanceof String) {
			return ArrayUtil.toArray(
				GetterUtil.getLongValues(StringUtil.split((String)object)));
		}

		if (object != null) {
			long[] fromArray = GetterUtil.getLongValues(object);

			if (ArrayUtil.isNotEmpty(fromArray)) {
				return ArrayUtil.toArray(fromArray);
			}
			else if (object instanceof List<?> list) {
				if (list.stream().allMatch(e -> e instanceof Long)) {
					return list.toArray(new Long[0]);
				}
			}
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static SXPParameter _getLongArraySXPParameter(
		String name, Object object, Parameter parameter) {

		Long[] value = _getLongArray(
			(Long[])parameter.getDefaultValue(), object);

		if (ArrayUtil.isEmpty(value)) {
			return null;
		}

		return new LongArraySXPParameter(name, true, value);
	}

	private static SXPParameter _getLongSXPParameter(
		String name, Object object, Parameter parameter) {

		Long value = _getLong((Long)parameter.getDefaultValue(), object);

		if (value == null) {
			return null;
		}

		return new LongSXPParameter(
			name, true,
			_fit((Long)parameter.getMax(), (Long)parameter.getMin(), value));
	}

	private static String _getString(String defaultValue, Object object) {
		if (object != null) {
			return GetterUtil.getString(object);
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static String[] _getStringArray(
		String[] defaultValue, Object object) {

		if (object != null) {
			String[] fromArray = GetterUtil.getStringValues(object);

			if (ArrayUtil.isNotEmpty(fromArray)) {
				return fromArray;
			}
			else if (object instanceof List<?> list) {
				if (list.stream().allMatch(e -> e instanceof String)) {
					return list.toArray(new String[0]);
				}
			}
		}

		if (defaultValue != null) {
			return defaultValue;
		}

		return null;
	}

	private static SXPParameter _getStringArraySXPParameter(
		String name, Object object, Parameter parameter) {

		String[] value = _getStringArray(
			(String[])parameter.getDefaultValue(), object);

		if (ArrayUtil.isEmpty(value)) {
			return null;
		}

		return new StringArraySXPParameter(name, true, value);
	}

	private static SXPParameter _getStringSXPParameter(
		String name, Object object, Parameter parameter) {

		String value = _getString((String)parameter.getDefaultValue(), object);

		if (value == null) {
			return null;
		}

		return new StringSXPParameter(name, true, value);
	}

	private static SXPParameter _getSXPParameter(
		String name, Object object, Parameter parameter,
		SearchContext searchContext, Parameter.Type type) {

		if (type.equals(Parameter.Type.BOOLEAN)) {
			return _getBooleanSXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.DATE)) {
			return _getDateSXPParameter(
				name, object, searchContext.getTimeZone(), parameter);
		}
		else if (type.equals(Parameter.Type.DOUBLE)) {
			return _getDoubleSXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.FLOAT)) {
			return _getFloatSXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.INTEGER)) {
			return _getIntegerSXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.INTEGER_ARRAY)) {
			return _getIntegerArraySXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.LONG)) {
			return _getLongSXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.LONG_ARRAY)) {
			return _getLongArraySXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.STRING)) {
			return _getStringSXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.STRING_ARRAY)) {
			return _getStringArraySXPParameter(name, object, parameter);
		}
		else if (type.equals(Parameter.Type.TIME_RANGE)) {
			return _getTimeRangeSXPParameter(name, object);
		}

		throw new IllegalArgumentException();
	}

	private static SXPParameter _getTimeRangeSXPParameter(
		String name, Object object) {

		String value = _getString(null, object);

		if (value == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();

		if (value.equals("last-day")) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		else if (value.equals("last-hour")) {
			calendar.add(Calendar.HOUR_OF_DAY, -1);
		}
		else if (value.equals("last-month")) {
			calendar.add(Calendar.MONTH, -1);
		}
		else if (value.equals("last-week")) {
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
		}
		else if (value.equals("last-year")) {
			calendar.add(Calendar.YEAR, -1);
		}

		return new DateSXPParameter(name, true, calendar.getTime());
	}

	private static Parameter.Type _getType(
		SXPParameterContributorDefinition sxpParameterContributorDefinition) {

		String className = sxpParameterContributorDefinition.getClassName();

		if (className.equals(BooleanSXPParameter.class.getName())) {
			return Parameter.Type.BOOLEAN;
		}
		else if (className.equals(DateSXPParameter.class.getName())) {
			return Parameter.Type.DATE;
		}
		else if (className.equals(DoubleSXPParameter.class.getName())) {
			return Parameter.Type.DOUBLE;
		}
		else if (className.equals(FloatSXPParameter.class.getName())) {
			return Parameter.Type.FLOAT;
		}
		else if (className.equals(IntegerSXPParameter.class.getName())) {
			return Parameter.Type.INTEGER;
		}
		else if (className.equals(IntegerArraySXPParameter.class.getName())) {
			return Parameter.Type.INTEGER_ARRAY;
		}
		else if (className.equals(LongSXPParameter.class.getName())) {
			return Parameter.Type.LONG;
		}
		else if (className.equals(LongArraySXPParameter.class.getName())) {
			return Parameter.Type.LONG_ARRAY;
		}
		else if (className.equals(StringSXPParameter.class.getName())) {
			return Parameter.Type.STRING;
		}
		else if (className.equals(StringArraySXPParameter.class.getName())) {
			return Parameter.Type.STRING_ARRAY;
		}

		throw new IllegalArgumentException();
	}

}