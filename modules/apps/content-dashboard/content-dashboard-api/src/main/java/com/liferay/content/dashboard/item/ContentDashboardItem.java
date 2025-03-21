/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.item;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Cristina González
 */
public interface ContentDashboardItem<T> {

	public List<AssetCategory> getAssetCategories();

	public List<AssetCategory> getAssetCategories(long vocabularyId);

	public List<AssetTag> getAssetTags();

	public List<Locale> getAvailableLocales();

	public List<ContentDashboardItemAction> getContentDashboardItemActions(
		HttpServletRequest httpServletRequest,
		ContentDashboardItemAction.Type... types);

	public ContentDashboardItemSubtype<T> getContentDashboardItemSubtype();

	public Date getCreateDate();

	public ContentDashboardItemAction getDefaultContentDashboardItemAction(
		HttpServletRequest httpServletRequest);

	public Locale getDefaultLocale();

	public String getDescription(Locale locale);

	public long getId();

	public InfoItemReference getInfoItemReference();

	public List<ContentDashboardItemVersion>
		getLatestContentDashboardItemVersions(Locale locale);

	public Date getModifiedDate();

	public default Date getReviewDate() {
		return null;
	}

	public String getScopeName(Locale locale);

	public List<SpecificInformation<?>> getSpecificInformationList(
		Locale locale);

	public String getTitle(Locale locale);

	public String getTypeLabel(Locale locale);

	public long getUserId();

	public String getUserName();

	public boolean isViewable(HttpServletRequest httpServletRequest);

	public static class SpecificInformation<T> {

		public SpecificInformation(
			String helpText, String key, Type type, T value) {

			_helpText = helpText;
			_key = key;
			_type = type;
			_value = value;
		}

		public SpecificInformation(String key, Type type, T value) {
			_key = key;
			_type = type;
			_value = value;

			_helpText = null;
		}

		public String getHelpText() {
			return _helpText;
		}

		public String getKey() {
			return _key;
		}

		public Type getType() {
			return _type;
		}

		public T getValue() {
			return _value;
		}

		public JSONObject toJSONObject(Language language, Locale locale) {
			return JSONUtil.put(
				"help", language.get(locale, getHelpText())
			).put(
				"title", language.get(locale, getKey())
			).put(
				"type", String.valueOf(getType())
			).put(
				"value", _toString(getValue())
			);
		}

		public enum Type {

			DATE {

				public String toString() {
					return "Date";
				}

			},
			STRING {

				public String toString() {
					return "String";
				}

			}, URL

		}

		private String _toString(Date date) {
			Instant instant = date.toInstant();

			ZonedDateTime zonedDateTime = instant.atZone(
				ZoneId.systemDefault());

			LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

			return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}

		private String _toString(Object object) {
			if (object == null) {
				return null;
			}

			if (object instanceof Date) {
				return _toString((Date)object);
			}

			return String.valueOf(object);
		}

		private final String _helpText;
		private final String _key;
		private final Type _type;
		private final T _value;

	}

}