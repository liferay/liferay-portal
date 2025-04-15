/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.servlet.MultiSessionErrors;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.service.TranslationEntryLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class ImportTranslationDisplayContext {

	public ImportTranslationDisplayContext(
		long classNameId, long classPK, long companyId, long groupId,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, String title,
		TranslationEntryLocalService translationEntryLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		_classNameId = classNameId;
		_classPK = classPK;
		_companyId = companyId;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_title = title;
		_translationEntryLocalService = translationEntryLocalService;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
	}

	public String getErrorMessage() {
		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (!MultiSessionErrors.contains(
				portletRequest,
				XLIFFFileException.MustBeValid.class.getName())) {

			return null;
		}

		return LanguageUtil.get(
			_httpServletRequest,
			"please-enter-a-file-with-a-valid-xliff-file-extension");
	}

	public PortletURL getImportTranslationURL() throws PortalException {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/translation/import_translation"
		).setParameter(
			"classNameId", _classNameId
		).setParameter(
			"classPK", _classPK
		).setParameter(
			"groupId", _groupId
		).setParameter(
			"title", getTitle()
		).buildPortletURL();
	}

	public String getPublishButtonLabel() throws PortalException {
		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				_companyId, _groupId, TranslationEntry.class.getName())) {

			return "submit-for-workflow";
		}

		return "publish";
	}

	public String getRedirect() {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public String getSaveButtonLabel() {
		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				_companyId, _groupId, TranslationEntry.class.getName())) {

			return "save-as-draft";
		}

		return "save";
	}

	public String getTitle() throws PortalException {
		return _title;
	}

	public boolean isPending() throws PortalException {
		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				_companyId, _groupId, TranslationEntry.class.getName()) &&
			_isAnyTranslationPending()) {

			return true;
		}

		return false;
	}

	private boolean _isAnyTranslationPending() {
		if (_classPK == 0) {
			return false;
		}

		int translationEntriesCount =
			_translationEntryLocalService.getTranslationEntriesCount(
				PortalUtil.getClassName(_classNameId), _classPK,
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_DRAFT
				},
				true);

		if (translationEntriesCount > 0) {
			return true;
		}

		return false;
	}

	private final long _classNameId;
	private final long _classPK;
	private final long _companyId;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _redirect;
	private final String _title;
	private final TranslationEntryLocalService _translationEntryLocalService;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}