/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.display;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Writer;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrew Betts
 */
public abstract class BaseBackgroundTaskDisplay
	implements BackgroundTaskDisplay {

	public BaseBackgroundTaskDisplay(BackgroundTask backgroundTask) {
		this.backgroundTask = backgroundTask;

		backgroundTaskStatus =
			BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(
				backgroundTask.getBackgroundTaskId());
	}

	@Override
	public String getDisplayName(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(backgroundTask.getName())) {
			return LanguageUtil.get(httpServletRequest, "untitled");
		}

		return backgroundTask.getName();
	}

	@Override
	public abstract int getPercentage();

	@Override
	public int getStatus() {
		return backgroundTask.getStatus();
	}

	@Override
	public String getStatusLabel() {
		return getStatusLabel(LocaleUtil.getDefault());
	}

	@Override
	public String getStatusLabel(Locale locale) {
		return LanguageUtil.get(locale, backgroundTask.getStatusLabel());
	}

	@Override
	public boolean hasPercentage() {
		if (getPercentage() >= PERCENTAGE_MIN) {
			return true;
		}

		return false;
	}

	@Override
	public String renderDisplayTemplate() {
		return renderDisplayTemplate(LocaleUtil.getDefault());
	}

	@Override
	public String renderDisplayTemplate(Locale locale) {
		TemplateResource templateResource = getTemplateResource();

		if (templateResource == null) {
			return StringPool.BLANK;
		}

		TemplateManager templateManager =
			TemplateManagerUtil.getTemplateManager(
				TemplateConstants.LANG_TYPE_FTL);

		Template template = templateManager.getTemplate(templateResource, true);

		template.put("backgroundTask", backgroundTask);
		template.put("backgroundTaskDisplay", this);
		template.put("backgroundTaskStatus", backgroundTaskStatus);
		template.put(
			"statusMessageJSONObject", getStatusMessageJSONObject(locale));

		Map<String, Object> templateVars = getTemplateVars();

		if (templateVars != null) {
			template.putAll(getTemplateVars());
		}

		Writer writer = new UnsyncStringWriter();

		try {
			template.processTemplate(writer);
		}
		catch (TemplateException templateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(templateException);
			}

			return StringPool.BLANK;
		}

		return writer.toString();
	}

	protected long getBackgroundTaskStatusAttributeLong(String attributeKey) {
		if (!hasBackgroundTaskStatus()) {
			return 0;
		}

		return GetterUtil.getLong(
			backgroundTaskStatus.getAttribute(attributeKey));
	}

	protected String getBackgroundTaskStatusAttributeString(
		String attributeKey) {

		if (!hasBackgroundTaskStatus()) {
			return StringPool.BLANK;
		}

		return GetterUtil.getString(
			backgroundTaskStatus.getAttribute(attributeKey));
	}

	protected JSONObject getStatusMessageJSONObject(Locale locale) {
		JSONObject jsonObject = null;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(
				backgroundTask.getStatusMessage());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return translateJSON(jsonObject, locale);
	}

	protected abstract TemplateResource getTemplateResource();

	protected abstract Map<String, Object> getTemplateVars();

	protected boolean hasBackgroundTaskStatus() {
		if (backgroundTaskStatus != null) {
			return true;
		}

		return false;
	}

	protected JSONArray translateJSON(JSONArray jsonArray, Locale locale) {
		JSONArray translatedJSONArray = JSONFactoryUtil.createJSONArray();

		for (Object object : jsonArray) {
			if (object instanceof JSONObject) {
				translatedJSONArray.put(
					translateJSON((JSONObject)object, locale));
			}
			else if (object instanceof JSONArray) {
				translatedJSONArray.put(
					translateJSON((JSONArray)object, locale));
			}
			else if (object instanceof String) {
				translatedJSONArray.put(
					LanguageUtil.get(locale, (String)object));
			}
			else {
				translatedJSONArray.put(object);
			}
		}

		return translatedJSONArray;
	}

	protected JSONObject translateJSON(JSONObject jsonObject, Locale locale) {
		if (locale == null) {
			return jsonObject;
		}

		JSONObject translatedJSONObject = JSONFactoryUtil.createJSONObject();

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			Object object = jsonObject.get(key);

			if (object instanceof JSONObject) {
				translatedJSONObject.put(
					key, translateJSON((JSONObject)object, locale));
			}
			else if (object instanceof JSONArray) {
				translatedJSONObject.put(
					key, translateJSON((JSONArray)object, locale));
			}
			else if (object instanceof String) {
				translatedJSONObject.put(
					key, LanguageUtil.get(locale, (String)object));
			}
			else {
				translatedJSONObject.put(key, object);
			}
		}

		return translatedJSONObject;
	}

	protected static final int PERCENTAGE_MAX = 100;

	protected static final int PERCENTAGE_MIN = 0;

	protected static final int PERCENTAGE_NONE = -1;

	protected final BackgroundTask backgroundTask;
	protected final BackgroundTaskStatus backgroundTaskStatus;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseBackgroundTaskDisplay.class);

}