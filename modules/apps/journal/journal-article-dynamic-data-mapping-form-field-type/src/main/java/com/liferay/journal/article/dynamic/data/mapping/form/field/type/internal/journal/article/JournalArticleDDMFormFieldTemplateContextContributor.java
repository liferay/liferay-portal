/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.article.dynamic.data.mapping.form.field.type.internal.journal.article;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.JournalArticleItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "ddm.form.field.type.name=" + JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE,
	service = DDMFormFieldTemplateContextContributor.class
)
public class JournalArticleDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		return HashMapBuilder.<String, Object>put(
			"itemSelectorURL",
			_getItemSelectorURL(
				ddmFormFieldRenderingContext,
				ddmFormFieldRenderingContext.getHttpServletRequest())
		).put(
			"message",
			_getMessage(
				ddmFormFieldRenderingContext.getLocale(),
				ddmFormFieldRenderingContext.getValue())
		).put(
			"portletNamespace",
			ddmFormFieldRenderingContext.getPortletNamespace()
		).put(
			"predefinedValue",
			() -> {
				LocalizedValue localizedValue =
					(LocalizedValue)ddmFormField.getProperty("predefinedValue");

				if (localizedValue == null) {
					return StringPool.BLANK;
				}

				String predefinedValue = GetterUtil.getString(
					localizedValue.getString(
						ddmFormFieldRenderingContext.getLocale()));

				return _getValue(predefinedValue);
			}
		).put(
			"value",
			_getValue(
				GetterUtil.getString(
					ddmFormFieldRenderingContext.getProperty("value")))
		).build();
	}

	private String _getItemSelectorURL(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		HttpServletRequest httpServletRequest) {

		if (_itemSelector == null) {
			return null;
		}

		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		infoItemItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new JournalArticleItemSelectorReturnType());
		infoItemItemSelectorCriterion.setRefererClassPK(
			_getRefererClassPK(httpServletRequest));
		infoItemItemSelectorCriterion.setStatus(
			WorkflowConstants.STATUS_APPROVED);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				ddmFormFieldRenderingContext.getPortletNamespace() +
					"selectJournalArticle",
				infoItemItemSelectorCriterion));
	}

	private String _getMessage(Locale defaultLocale, String value) {
		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(value);

			long classPK = jsonObject.getLong("classPK");

			if (classPK <= 0) {
				return StringPool.BLANK;
			}

			JournalArticle article =
				_journalArticleLocalService.fetchLatestArticle(classPK);

			if (article != null) {
				if (article.isInTrash()) {
					return _language.get(
						defaultLocale,
						"the-selected-web-content-was-moved-to-the-recycle-" +
							"bin");
				}

				return StringPool.BLANK;
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get article for  " + classPK);
			}

			return _language.get(
				defaultLocale, "the-selected-web-content-was-deleted");
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return StringPool.BLANK;
		}
	}

	private long _getRefererClassPK(HttpServletRequest httpServletRequest) {
		String articleId = ParamUtil.getString(httpServletRequest, "articleId");

		if (Validator.isNull(articleId)) {
			return 0;
		}

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(groupId, articleId);

		if (journalArticle == null) {
			return 0;
		}

		return journalArticle.getResourcePrimKey();
	}

	private String _getValue(String value) {
		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(value);

			long classPK = jsonObject.getLong("classPK");

			if (classPK <= 0) {
				return StringPool.BLANK;
			}

			if (!jsonObject.has("classNameId")) {
				String className = jsonObject.getString("className");

				jsonObject.put(
					"classNameId", _portal.getClassNameId(className));
			}

			if (!jsonObject.has("title") || !jsonObject.has("titleMap")) {
				JournalArticle journalArticle =
					_journalArticleLocalService.fetchLatestArticle(
						jsonObject.getLong("classPK"));

				jsonObject.put(
					"title", journalArticle.getTitle()
				).put(
					"titleMap",
					_jsonFactory.createJSONObject(journalArticle.getTitleMap())
				);
			}

			return jsonObject.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return StringPool.BLANK;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleDDMFormFieldTemplateContextContributor.class);

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}