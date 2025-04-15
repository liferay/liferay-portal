/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class EmailNotificationSettingsTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public String getBodyLabel() {
		return _bodyLabel;
	}

	public String getEmailBody() {
		return _emailBody;
	}

	public LocalizedValuesMap getEmailBodyLocalizedValuesMap() {
		return _emailBodyLocalizedValuesMap;
	}

	public Map<String, String> getEmailDefinitionTerms() {
		return _emailDefinitionTerms;
	}

	public String getEmailParam() {
		return _emailParam;
	}

	public String getEmailSubject() {
		return _emailSubject;
	}

	public LocalizedValuesMap getEmailSubjectLocalizedValuesMap() {
		return _emailSubjectLocalizedValuesMap;
	}

	public String getFieldPrefix() {
		return _fieldPrefix;
	}

	public String getFieldPrefixSeparator() {
		return _fieldPrefixSeparator;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public boolean isEmailEnabled() {
		return _emailEnabled;
	}

	public boolean isShowEmailEnabled() {
		return _showEmailEnabled;
	}

	public boolean isShowSubject() {
		return _showSubject;
	}

	public void setBodyLabel(String bodyLabel) {
		_bodyLabel = bodyLabel;
	}

	public void setEmailBody(String emailBody) {
		_emailBody = emailBody;
	}

	public void setEmailBodyLocalizedValuesMap(
		LocalizedValuesMap emailBodyLocalizedValuesMap) {

		_emailBodyLocalizedValuesMap = emailBodyLocalizedValuesMap;
	}

	public void setEmailDefinitionTerms(
		Map<String, String> emailDefinitionTerms) {

		_emailDefinitionTerms = emailDefinitionTerms;
	}

	public void setEmailEnabled(boolean emailEnabled) {
		_emailEnabled = emailEnabled;
	}

	public void setEmailParam(String emailParam) {
		_emailParam = emailParam;
	}

	public void setEmailSubject(String emailSubject) {
		_emailSubject = emailSubject;
	}

	public void setEmailSubjectLocalizedValuesMap(
		LocalizedValuesMap emailSubjectLocalizedValuesMap) {

		_emailSubjectLocalizedValuesMap = emailSubjectLocalizedValuesMap;
	}

	public void setFieldPrefix(String fieldPrefix) {
		_fieldPrefix = fieldPrefix;
	}

	public void setFieldPrefixSeparator(String fieldPrefixSeparator) {
		_fieldPrefixSeparator = fieldPrefixSeparator;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowEmailEnabled(boolean showEmailEnabled) {
		_showEmailEnabled = showEmailEnabled;
	}

	public void setShowSubject(boolean showSubject) {
		_showSubject = showSubject;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_bodyLabel = null;
		_emailBody = null;
		_emailBodyLocalizedValuesMap = null;
		_emailDefinitionTerms = null;
		_emailEnabled = false;
		_emailParam = null;
		_emailSubject = null;
		_emailSubjectLocalizedValuesMap = null;
		_fieldPrefix = null;
		_fieldPrefixSeparator = null;
		_helpMessage = null;
		_showEmailEnabled = true;
		_showSubject = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(_bodyLabel)) {
			_bodyLabel = "body";
		}

		if (Validator.isNull(_fieldPrefix)) {
			_fieldPrefix = "preferences";
		}

		if (Validator.isNull(_fieldPrefixSeparator)) {
			_fieldPrefixSeparator = "--";
		}

		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:bodyLabel",
			_bodyLabel);

		String emailBody = _emailBody;

		if (Validator.isNull(emailBody) &&
			(_emailBodyLocalizedValuesMap != null)) {

			emailBody = LocalizationUtil.getXml(
				_emailBodyLocalizedValuesMap, _emailParam + "Body");
		}

		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:emailBody",
			emailBody);

		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:emailDefinitionTerms",
			_emailDefinitionTerms);
		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:emailEnabled",
			String.valueOf(_emailEnabled));
		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:emailParam",
			_emailParam);

		String emailSubject = _emailSubject;

		if (Validator.isNull(emailSubject) &&
			(_emailSubjectLocalizedValuesMap != null)) {

			emailSubject = LocalizationUtil.getXml(
				_emailSubjectLocalizedValuesMap, _emailParam + "Subject");
		}

		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:emailSubject",
			emailSubject);

		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:fieldPrefix",
			_fieldPrefix);
		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:fieldPrefixSeparator",
			_fieldPrefixSeparator);
		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:helpMessage",
			_helpMessage);
		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:showEmailEnabled",
			_showEmailEnabled);
		httpServletRequest.setAttribute(
			"liferay-frontend:email-notification-settings:showSubject",
			_showSubject);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _PAGE = "/email_notification_settings/page.jsp";

	private String _bodyLabel;
	private String _emailBody;
	private LocalizedValuesMap _emailBodyLocalizedValuesMap;
	private Map<String, String> _emailDefinitionTerms;
	private boolean _emailEnabled;
	private String _emailParam;
	private String _emailSubject;
	private LocalizedValuesMap _emailSubjectLocalizedValuesMap;
	private String _fieldPrefix;
	private String _fieldPrefixSeparator;
	private String _helpMessage;
	private boolean _showEmailEnabled = true;
	private boolean _showSubject = true;

}