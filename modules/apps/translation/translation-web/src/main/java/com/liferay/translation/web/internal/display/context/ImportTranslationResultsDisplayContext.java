/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.util.JS;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * @author Alicia García
 */
public class ImportTranslationResultsDisplayContext implements Serializable {

	public ImportTranslationResultsDisplayContext(
		long classNameId, long classPK, long companyId, long groupId,
		List<Map<String, String>> failureMessages, String fileName,
		List<String> successMessages, String title, int workflowAction,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		_classNameId = classNameId;
		_classPK = classPK;
		_companyId = companyId;
		_groupId = groupId;
		_failureMessages = failureMessages;
		_fileName = fileName;
		_successMessages = successMessages;
		_title = title;
		_workflowAction = workflowAction;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
	}

	public String getFailureMessageKey() {
		String pattern = "x-files-could-not-be-published";

		if (_workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			if (getFailureMessagesCount() == 1) {
				pattern = "x-file-could-not-be-published";
			}
		}
		else {
			pattern = "x-files-could-not-be-saved";

			if (getFailureMessagesCount() == 1) {
				pattern = "x-file-could-not-be-saved";
			}
		}

		return pattern;
	}

	public List<Map<String, String>> getFailureMessages() {
		return _failureMessages;
	}

	public int getFailureMessagesCount() {
		return _failureMessages.size();
	}

	public String getFailureMessagesCSVDataURL(Locale locale)
		throws IOException {

		StringWriter stringWriter = new StringWriter();

		CSVPrinter csvPrinter = new CSVPrinter(
			stringWriter,
			CSVFormat.DEFAULT.withHeader(
				LanguageUtil.get(locale, "file-name"),
				LanguageUtil.get(locale, "error-message"),
				LanguageUtil.get(locale, "container")));

		for (Map<String, String> failureMessage : _failureMessages) {
			csvPrinter.printRecord(
				failureMessage.get("fileName"),
				failureMessage.get("errorMessage"),
				failureMessage.get("container"));
		}

		return "data:text/csv;charset=utf-8," +
			JS.encodeURIComponent(stringWriter.toString());
	}

	public String getFileName() {
		return _fileName;
	}

	public String getImportTranslationURL(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/translation/import_translation"
		).setRedirect(
			getRedirect(httpServletRequest)
		).setParameter(
			"classNameId", _classNameId
		).setParameter(
			"classPK", _classPK
		).setParameter(
			"groupId", _groupId
		).buildString();
	}

	public String getRedirect(HttpServletRequest httpServletRequest) {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(httpServletRequest, "redirect");

		return _redirect;
	}

	public String getSuccessMessageLabel(Locale locale) {
		if (Objects.equals(
				Layout.class.getName(),
				PortalUtil.getClassName(_classNameId))) {

			return _getLayoutSuccessMessageLabel(locale);
		}

		boolean workflowEnabled =
			_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				_companyId, _groupId, TranslationEntry.class.getName());

		if (workflowEnabled) {
			return _getWorkflowSuccessMessageLabel(locale);
		}

		if ((getSuccessMessagesCount() > 1) &&
			(getFailureMessagesCount() == 0)) {

			return LanguageUtil.get(locale, "all-files-published");
		}

		String pattern = "x-files-published";

		if (getSuccessMessagesCount() == 1) {
			pattern = "x-file-published";
		}

		return LanguageUtil.format(locale, pattern, getSuccessMessagesCount());
	}

	public List<String> getSuccessMessages() {
		return _successMessages;
	}

	public int getSuccessMessagesCount() {
		return _successMessages.size();
	}

	public String getTitle() {
		return _title;
	}

	private String _getLayoutSuccessMessageLabel(Locale locale) {
		if ((getSuccessMessagesCount() > 1) &&
			(getFailureMessagesCount() == 0)) {

			return LanguageUtil.get(locale, "all-files-saved");
		}

		String pattern = "x-files-saved";

		if (getSuccessMessagesCount() == 1) {
			pattern = "x-file-saved";
		}

		return LanguageUtil.format(locale, pattern, getSuccessMessagesCount());
	}

	private String _getWorkflowSuccessMessageLabel(Locale locale) {
		if ((getSuccessMessagesCount() > 1) &&
			(getFailureMessagesCount() == 0)) {

			if (_workflowAction == WorkflowConstants.ACTION_PUBLISH) {
				return LanguageUtil.get(
					locale, "all-files-sent-for-publication");
			}

			return LanguageUtil.get(locale, "all-files-saved-as-draft");
		}

		String pattern = "x-files-saved-as-draft";

		if (_workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			pattern = "x-files-sent-for-publication";
		}

		if (getSuccessMessagesCount() == 1) {
			pattern = "x-file-saved-as-draft";

			if (_workflowAction == WorkflowConstants.ACTION_PUBLISH) {
				pattern = "x-file-sent-for-publication";
			}
		}

		return LanguageUtil.format(locale, pattern, getSuccessMessagesCount());
	}

	private final long _classNameId;
	private final long _classPK;
	private final long _companyId;
	private final List<Map<String, String>> _failureMessages;
	private final String _fileName;
	private final long _groupId;
	private String _redirect;
	private final List<String> _successMessages;
	private final String _title;
	private final int _workflowAction;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}