/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.model.impl;

import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public class ExpandoValueImpl extends ExpandoValueBaseImpl {

	@Override
	public List<Locale> getAvailableLocales() throws PortalException {
		if (!isColumnLocalized()) {
			return null;
		}

		List<Locale> locales = new ArrayList<>();

		for (String languageId :
				LocalizationUtil.getAvailableLanguageIds(getData())) {

			locales.add(LocaleUtil.fromLanguageId(languageId));
		}

		return locales;
	}

	@Override
	public boolean getBoolean() throws PortalException {
		validate(ExpandoColumnConstants.BOOLEAN);

		return GetterUtil.getBoolean(getData());
	}

	@Override
	public boolean[] getBooleanArray() throws PortalException {
		validate(ExpandoColumnConstants.BOOLEAN_ARRAY);

		return GetterUtil.getBooleanValues(StringUtil.split(getData()));
	}

	@Override
	public ExpandoColumn getColumn() throws PortalException {
		if (_column != null) {
			return _column;
		}

		long columnId = getColumnId();

		if (columnId <= 0) {
			return null;
		}

		return ExpandoColumnLocalServiceUtil.getColumn(columnId);
	}

	@Override
	public Date getDate() throws PortalException {
		validate(ExpandoColumnConstants.DATE);

		return new Date(GetterUtil.getLong(getData()));
	}

	@Override
	public Date[] getDateArray() throws PortalException {
		validate(ExpandoColumnConstants.DATE_ARRAY);

		String[] data = StringUtil.split(getData());

		Date[] dateArray = new Date[data.length];

		for (int i = 0; i < data.length; i++) {
			dateArray[i] = new Date(GetterUtil.getLong(data[i]));
		}

		return dateArray;
	}

	@Override
	public Locale getDefaultLocale() throws PortalException {
		if (!isColumnLocalized()) {
			return null;
		}

		return LocaleUtil.fromLanguageId(
			LocalizationUtil.getDefaultLanguageId(getData()));
	}

	@Override
	public double getDouble() throws PortalException {
		validate(ExpandoColumnConstants.DOUBLE);

		return GetterUtil.getDouble(getData());
	}

	@Override
	public double[] getDoubleArray() throws PortalException {
		validate(ExpandoColumnConstants.DOUBLE_ARRAY);

		return GetterUtil.getDoubleValues(StringUtil.split(getData()));
	}

	@Override
	public float getFloat() throws PortalException {
		validate(ExpandoColumnConstants.FLOAT);

		return GetterUtil.getFloat(getData());
	}

	@Override
	public float[] getFloatArray() throws PortalException {
		validate(ExpandoColumnConstants.FLOAT_ARRAY);

		return GetterUtil.getFloatValues(StringUtil.split(getData()));
	}

	@Override
	public JSONObject getGeolocationJSONObject() throws PortalException {
		validate(ExpandoColumnConstants.GEOLOCATION);

		return JSONFactoryUtil.createJSONObject(getData());
	}

	@Override
	public int getInteger() throws PortalException {
		validate(ExpandoColumnConstants.INTEGER);

		return GetterUtil.getInteger(getData());
	}

	@Override
	public int[] getIntegerArray() throws PortalException {
		validate(ExpandoColumnConstants.INTEGER_ARRAY);

		return GetterUtil.getIntegerValues(StringUtil.split(getData()));
	}

	@Override
	public long getLong() throws PortalException {
		validate(ExpandoColumnConstants.LONG);

		return GetterUtil.getLong(getData());
	}

	@Override
	public long[] getLongArray() throws PortalException {
		validate(ExpandoColumnConstants.LONG_ARRAY);

		return GetterUtil.getLongValues(StringUtil.split(getData()));
	}

	@Override
	public Number getNumber() throws PortalException {
		validate(ExpandoColumnConstants.NUMBER);

		return GetterUtil.getNumber(getData());
	}

	@Override
	public Number[] getNumberArray() throws PortalException {
		validate(ExpandoColumnConstants.NUMBER_ARRAY);

		return GetterUtil.getNumberValues(StringUtil.split(getData()));
	}

	@Override
	public Serializable getSerializable() throws PortalException {
		ExpandoColumn column = getColumn();

		int type = column.getType();

		if (type == ExpandoColumnConstants.BOOLEAN) {
			return getBoolean();
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			return getBooleanArray();
		}
		else if (type == ExpandoColumnConstants.DATE) {
			return getDate();
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			return getDateArray();
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			return getDouble();
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			return getDoubleArray();
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			return getFloat();
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			return getFloatArray();
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			return getGeolocationJSONObject();
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			return getInteger();
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			return getIntegerArray();
		}
		else if (type == ExpandoColumnConstants.LONG) {
			return getLong();
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			return getLongArray();
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			return getNumber();
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			return getNumberArray();
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			return getShort();
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			return getShortArray();
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			return getStringArray();
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
			return (Serializable)getStringArrayMap();
		}
		else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
			return (Serializable)getStringMap();
		}

		return getData();
	}

	@Override
	public short getShort() throws PortalException {
		validate(ExpandoColumnConstants.SHORT);

		return GetterUtil.getShort(getData());
	}

	@Override
	public short[] getShortArray() throws PortalException {
		validate(ExpandoColumnConstants.SHORT_ARRAY);

		return GetterUtil.getShortValues(StringUtil.split(getData()));
	}

	@Override
	public String getString() throws PortalException {
		validate(ExpandoColumnConstants.STRING);

		return getData();
	}

	@Override
	public String getString(Locale locale) throws PortalException {
		validate(ExpandoColumnConstants.STRING_LOCALIZED);

		String languageId = LocaleUtil.toLanguageId(locale);

		return getData(languageId);
	}

	@Override
	public String[] getStringArray() throws PortalException {
		validate(ExpandoColumnConstants.STRING_ARRAY);

		return split(getData());
	}

	@Override
	public String[] getStringArray(Locale locale) throws PortalException {
		validate(ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		String languageId = LocaleUtil.toLanguageId(locale);

		return split(getData(languageId));
	}

	@Override
	public Map<Locale, String[]> getStringArrayMap() throws PortalException {
		validate(ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		Map<Locale, String> stringMap = LocalizationUtil.getLocalizationMap(
			getData());

		Map<Locale, String[]> stringArrayMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : stringMap.entrySet()) {
			stringArrayMap.put(entry.getKey(), split(entry.getValue()));
		}

		return stringArrayMap;
	}

	@Override
	public Map<Locale, String> getStringMap() throws PortalException {
		validate(ExpandoColumnConstants.STRING_LOCALIZED);

		return LocalizationUtil.getLocalizationMap(getData());
	}

	@Override
	public void setBoolean(boolean data) throws PortalException {
		validate(ExpandoColumnConstants.BOOLEAN);

		setData(String.valueOf(data));
	}

	@Override
	public void setBooleanArray(boolean[] data) throws PortalException {
		validate(ExpandoColumnConstants.BOOLEAN_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setColumn(ExpandoColumn column) {
		_column = column;

		setColumnId(_column.getColumnId());
	}

	@Override
	public void setDate(Date data) throws PortalException {
		validate(ExpandoColumnConstants.DATE);

		setData(String.valueOf(data.getTime()));
	}

	@Override
	public void setDateArray(Date[] data) throws PortalException {
		validate(ExpandoColumnConstants.DATE_ARRAY);

		if (data.length > 0) {
			StringBundler sb = new StringBundler(data.length * 2);

			for (Date date : data) {
				sb.append(date.getTime());
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);

			setData(sb.toString());
		}
		else {
			setData(StringPool.BLANK);
		}
	}

	@Override
	public void setDouble(double data) throws PortalException {
		validate(ExpandoColumnConstants.DOUBLE);

		setData(String.valueOf(data));
	}

	@Override
	public void setDoubleArray(double[] data) throws PortalException {
		validate(ExpandoColumnConstants.DOUBLE_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setFloat(float data) throws PortalException {
		validate(ExpandoColumnConstants.FLOAT);

		setData(String.valueOf(data));
	}

	@Override
	public void setFloatArray(float[] data) throws PortalException {
		validate(ExpandoColumnConstants.FLOAT_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setGeolocationJSONObject(JSONObject dataJSONObject)
		throws PortalException {

		validate(ExpandoColumnConstants.GEOLOCATION);

		setData(dataJSONObject.toString());
	}

	@Override
	public void setInteger(int data) throws PortalException {
		validate(ExpandoColumnConstants.INTEGER);

		setData(String.valueOf(data));
	}

	@Override
	public void setIntegerArray(int[] data) throws PortalException {
		validate(ExpandoColumnConstants.INTEGER_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setLong(long data) throws PortalException {
		validate(ExpandoColumnConstants.LONG);

		setData(String.valueOf(data));
	}

	@Override
	public void setLongArray(long[] data) throws PortalException {
		validate(ExpandoColumnConstants.LONG_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setNumber(Number data) throws PortalException {
		validate(ExpandoColumnConstants.NUMBER);

		setData(String.valueOf(data));
	}

	@Override
	public void setNumberArray(Number[] data) throws PortalException {
		validate(ExpandoColumnConstants.NUMBER_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setShort(short data) throws PortalException {
		validate(ExpandoColumnConstants.SHORT);

		setData(String.valueOf(data));
	}

	@Override
	public void setShortArray(short[] data) throws PortalException {
		validate(ExpandoColumnConstants.SHORT_ARRAY);

		setData(StringUtil.merge(data));
	}

	@Override
	public void setString(String data) throws PortalException {
		validate(ExpandoColumnConstants.STRING);

		setData(data);
	}

	@Override
	public void setString(String data, Locale locale, Locale defaultLocale)
		throws PortalException {

		validate(ExpandoColumnConstants.STRING_LOCALIZED);

		doSetString(data, locale, defaultLocale);
	}

	@Override
	public void setStringArray(String[] data) throws PortalException {
		validate(ExpandoColumnConstants.STRING_ARRAY);

		setData(merge(data));
	}

	@Override
	public void setStringArray(
			String[] data, Locale locale, Locale defaultLocale)
		throws PortalException {

		validate(ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		doSetString(merge(data), locale, defaultLocale);
	}

	@Override
	public void setStringArrayMap(
			Map<Locale, String[]> dataMap, Locale defaultLocale)
		throws PortalException {

		validate(ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		Map<Locale, String> stringMap = new HashMap<>();

		for (Map.Entry<Locale, String[]> entry : dataMap.entrySet()) {
			stringMap.put(entry.getKey(), merge(entry.getValue()));
		}

		doSetStringMap(stringMap, defaultLocale);
	}

	@Override
	public void setStringMap(Map<Locale, String> dataMap, Locale defaultLocale)
		throws PortalException {

		validate(ExpandoColumnConstants.STRING_LOCALIZED);

		doSetStringMap(dataMap, defaultLocale);
	}

	protected void doSetString(
		String data, Locale locale, Locale defaultLocale) {

		String languageId = LocaleUtil.toLanguageId(locale);

		if (Validator.isNotNull(data)) {
			String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

			data = LocalizationUtil.updateLocalization(
				getData(), "Data", data, languageId, defaultLanguageId);
		}
		else {
			data = LocalizationUtil.removeLocalization(
				getData(), "Data", languageId);
		}

		setData(data);
	}

	protected void doSetStringMap(
		Map<Locale, String> dataMap, Locale defaultLocale) {

		if (dataMap == null) {
			return;
		}

		setData(
			LocalizationUtil.updateLocalization(
				dataMap, getData(), "Data",
				LocaleUtil.toLanguageId(defaultLocale)));
	}

	protected String getData(String languageId) {
		return LocalizationUtil.getLocalization(getData(), languageId);
	}

	protected boolean isColumnLocalized() throws PortalException {
		ExpandoColumn column = getColumn();

		if (column == null) {
			return false;
		}

		if ((column.getType() ==
				ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) ||
			(column.getType() == ExpandoColumnConstants.STRING_LOCALIZED)) {

			return true;
		}

		return false;
	}

	protected String merge(String[] data) {
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				data[i] = StringUtil.replace(
					data[i], CharPool.COMMA, _EXPANDO_COMMA);
			}
		}

		return StringUtil.merge(data);
	}

	protected String[] split(String data) {
		String[] dataArray = StringUtil.split(data);

		for (int i = 0; i < dataArray.length; i++) {
			dataArray[i] = StringUtil.replace(
				dataArray[i], _EXPANDO_COMMA, StringPool.COMMA);
		}

		return dataArray;
	}

	protected void validate(int type) throws PortalException {
		ExpandoColumn column = getColumn();

		if ((column == null) || (column.getType() == type)) {
			return;
		}

		throw new ValueDataException.MismatchColumnType(
			getColumnId(),
			ExpandoColumnConstants.getTypeLabel(column.getType()),
			ExpandoColumnConstants.getTypeLabel(type));
	}

	private static final String _EXPANDO_COMMA = "[$LIFERAY_EXPANDO_COMMA$]";

	private transient ExpandoColumn _column;

}