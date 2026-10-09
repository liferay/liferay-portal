/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Task;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * @author Rafael Praxedes
 */
public class TaskUtil {

	public static Task toTask(
		Document document, Language language, Locale locale, Portal portal,
		ResourceBundle resourceBundle, Function<Long, User> userFunction) {

		return new Task() {
			{
				setAssetTitle(
					() -> {
						Map<String, String> assetTitleMap =
							getAssetTitle_i18n();

						String assetTitle = assetTitleMap.get(
							locale.toLanguageTag());

						if (Validator.isNull(assetTitle)) {
							Locale defaultLocale =
								LocaleThreadLocal.getDefaultLocale();

							assetTitle = assetTitleMap.get(
								defaultLocale.toLanguageTag());
						}

						return assetTitle;
					});
				setAssetTitle_i18n(() -> _createMap(document, "assetTitle"));
				setAssetType(
					() -> {
						Map<String, String> assetTypeMap = getAssetType_i18n();

						String assetType = assetTypeMap.get(
							locale.toLanguageTag());

						if (Validator.isNull(assetType)) {
							Locale defaultLocale =
								LocaleThreadLocal.getDefaultLocale();

							assetType = assetTypeMap.get(
								defaultLocale.toLanguageTag());
						}

						return assetType;
					});
				setAssetType_i18n(() -> _createMap(document, "assetType"));
				setAssignee(
					() -> {
						String assigneeType = document.getString(
							"assigneeType");

						if (Objects.deepEquals(
								assigneeType, User.class.getName())) {

							return AssigneeUtil.toAssignee(
								language, portal, resourceBundle,
								document.getLong("assigneeIds"), userFunction);
						}

						return null;
					});
				setClassName(() -> document.getString("className"));
				setClassPK(() -> document.getLong("classPK"));
				setCompleted(() -> document.getBoolean("completed"));
				setCompletionUserId(() -> document.getLong("completionUserId"));
				setDateCompletion(
					() -> _parseDate(document.getDate("completionDate")));
				setDateCreated(
					() -> _parseDate(document.getDate("createDate")));
				setDateModified(
					() -> _parseDate(document.getDate("modifiedDate")));
				setDuration(() -> document.getLong("duration"));
				setId(() -> document.getLong("taskId"));
				setInstanceId(() -> document.getLong("instanceId"));
				setLabel(
					() -> language.get(
						resourceBundle, document.getString("name")));
				setName(() -> document.getString("name"));
				setNodeId(() -> document.getLong("nodeId"));
				setProcessId(() -> document.getLong("processId"));
				setProcessVersion(() -> document.getString("version"));
			}
		};
	}

	public static Task toTask(
		Language language, Locale locale, Portal portal,
		ResourceBundle resourceBundle, Map<String, Object> sourcesMap,
		Function<Long, User> userFunction) {

		return new Task() {
			{
				setAssetTitle(
					() -> {
						Map<String, String> assetTitleMap =
							getAssetTitle_i18n();

						String assetTitle = assetTitleMap.get(
							locale.toLanguageTag());

						if (Validator.isNull(assetTitle)) {
							Locale defaultLocale =
								LocaleThreadLocal.getDefaultLocale();

							assetTitle = assetTitleMap.get(
								defaultLocale.toLanguageTag());
						}

						return assetTitle;
					});
				setAssetTitle_i18n(() -> _createMap("assetTitle", sourcesMap));
				setAssetType(
					() -> {
						Map<String, String> assetTypeMap = getAssetType_i18n();

						String assetType = assetTypeMap.get(
							locale.toLanguageTag());

						if (Validator.isNull(assetType)) {
							Locale defaultLocale =
								LocaleThreadLocal.getDefaultLocale();

							assetType = assetTypeMap.get(
								defaultLocale.toLanguageTag());
						}

						return assetType;
					});
				setAssetType_i18n(() -> _createMap("assetType", sourcesMap));
				setAssignee(
					() -> {
						String assigneeType = GetterUtil.getString(
							sourcesMap.get("assigneeType"));

						if (Objects.deepEquals(
								assigneeType, User.class.getName())) {

							return AssigneeUtil.toAssignee(
								language, portal, resourceBundle,
								GetterUtil.getLong(
									sourcesMap.get("assigneeIds")),
								userFunction);
						}

						return null;
					});
				setClassName(
					() -> GetterUtil.getString(sourcesMap.get("className")));
				setClassPK(() -> GetterUtil.getLong(sourcesMap.get("classPK")));
				setCompleted(
					() -> GetterUtil.getBoolean(sourcesMap.get("completed")));
				setCompletionUserId(
					() -> GetterUtil.getLong(
						sourcesMap.get("completionUserId")));
				setDateCompletion(
					() -> _parseDate(
						GetterUtil.getString(
							sourcesMap.get("completionDate"))));
				setDateCreated(
					() -> _parseDate(
						GetterUtil.getString(sourcesMap.get("createDate"))));
				setDateModified(
					() -> _parseDate(
						GetterUtil.getString(sourcesMap.get("modifiedDate"))));
				setDuration(
					() -> GetterUtil.getLong(sourcesMap.get("duration")));
				setId(() -> GetterUtil.getLong(sourcesMap.get("taskId")));
				setInstanceId(
					() -> GetterUtil.getLong(sourcesMap.get("instanceId")));
				setLabel(
					() -> language.get(
						resourceBundle,
						GetterUtil.getString(sourcesMap.get("name"))));
				setName(() -> GetterUtil.getString(sourcesMap.get("name")));
				setNodeId(() -> GetterUtil.getLong(sourcesMap.get("nodeId")));
				setProcessId(
					() -> GetterUtil.getLong(sourcesMap.get("processId")));
				setProcessVersion(
					() -> GetterUtil.getString(sourcesMap.get("version")));
			}
		};
	}

	public static Task toTask(
		Language language, String taskName, ResourceBundle resourceBundle) {

		return new Task() {
			{
				setLabel(() -> language.get(resourceBundle, taskName));
				setName(() -> taskName);
			}
		};
	}

	private static Map<String, String> _createMap(
		Document document, String fieldName) {

		Map<String, String> map = new HashMap<>();

		Map<String, Field> fields = document.getFields();

		for (Map.Entry<String, Field> entry : fields.entrySet()) {
			String key = entry.getKey();

			if (!StringUtil.startsWith(key, fieldName + StringPool.UNDERLINE) ||
				StringUtil.endsWith(key, "_sortable")) {

				continue;
			}

			Field field = entry.getValue();

			map.put(
				StringUtil.removeSubstring(
					key, fieldName + StringPool.UNDERLINE),
				String.valueOf(field.getValue()));
		}

		return map;
	}

	private static Map<String, String> _createMap(
		String fieldName, Map<String, Object> sourcesMap) {

		Map<String, String> map = new HashMap<>();

		for (Map.Entry<String, Object> entry : sourcesMap.entrySet()) {
			if (!StringUtil.startsWith(
					entry.getKey(), fieldName + StringPool.UNDERLINE) ||
				StringUtil.endsWith(entry.getKey(), "_sortable")) {

				continue;
			}

			map.put(
				_toLanguageTag(
					StringUtil.removeSubstring(
						entry.getKey(), fieldName + StringPool.UNDERLINE)),
				GetterUtil.getString(entry.getValue()));
		}

		return map;
	}

	private static Date _parseDate(String dateString) {
		try {
			return DateUtil.parseDate(
				"yyyyMMddHHmmss", dateString, LocaleUtil.getDefault());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private static String _toLanguageTag(String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return locale.toLanguageTag();
	}

	private static final Log _log = LogFactoryUtil.getLog(TaskUtil.class);

}