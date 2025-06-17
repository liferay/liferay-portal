/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.taglib.servlet.taglib;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		if (!_isShowArticle()) {
			return SKIP_PAGE;
		}

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		PortletRequestModel portletRequestModel = null;

		if ((portletRequest != null) && (portletResponse != null)) {
			portletRequestModel = new PortletRequestModel(
				portletRequest, portletResponse);
		}

		if (_article == null) {
			_article = JournalArticleLocalServiceUtil.fetchLatestArticle(
				_groupId, _articleId, WorkflowConstants.STATUS_APPROVED);
		}

		try {
			_articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(
				_article, _ddmTemplateKey,
				ParamUtil.getString(
					httpServletRequest, "p_l_mode", Constants.VIEW),
				getLanguageId(), 1, portletRequestModel, themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get journal article display", exception);
			}

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public JournalArticle getArticle() {
		return _article;
	}

	public String getArticleId() {
		return _articleId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getWrapperCssClass() {
		return _wrapperCssClass;
	}

	public boolean isDataAnalyticsTrackingEnabled() {
		return _dataAnalyticsTrackingEnabled;
	}

	public boolean isShowTitle() {
		return _showTitle;
	}

	public void setArticle(JournalArticle article) {
		_article = article;
	}

	public void setArticleId(String articleId) {
		_articleId = articleId;
	}

	public void setDataAnalyticsTrackingEnabled(
		boolean dataAnalyticsTrackingEnabled) {

		_dataAnalyticsTrackingEnabled = dataAnalyticsTrackingEnabled;
	}

	public void setDdmTemplateKey(String ddmTemplateKey) {
		_ddmTemplateKey = ddmTemplateKey;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowTitle(boolean showTitle) {
		_showTitle = showTitle;
	}

	public void setWrapperCssClass(String wrapperCssClass) {
		_wrapperCssClass = wrapperCssClass;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_article = null;
		_articleDisplay = null;
		_articleId = null;
		_dataAnalyticsTrackingEnabled = true;
		_ddmTemplateKey = null;
		_groupId = 0;
		_languageId = null;
		_showTitle = false;
		_wrapperCssClass = null;
	}

	protected String getDdmTemplateKey() {
		if (Validator.isNotNull(_ddmTemplateKey)) {
			return _ddmTemplateKey;
		}

		return _article.getDDMTemplateKey();
	}

	protected String getLanguageId() {
		if (Validator.isNotNull(_languageId)) {
			return _languageId;
		}

		return LanguageUtil.getLanguageId(getRequest());
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:article", _article);
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:articleDisplay", _articleDisplay);
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:dataAnalyticsTrackingEnabled",
			String.valueOf(_dataAnalyticsTrackingEnabled));
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:showTitle",
			String.valueOf(_showTitle));
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:wrapperCssClass",
			_wrapperCssClass);
	}

	private boolean _isShowArticle() {
		HttpServletRequest httpServletRequest = getRequest();

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		String mode = ParamUtil.getString(
			PortalUtil.getOriginalServletRequest(originalHttpServletRequest),
			"p_l_mode", Constants.VIEW);

		if (Objects.equals(Constants.EDIT, mode) ||
			Objects.equals(Constants.PREVIEW, mode)) {

			return true;
		}

		if ((_article == null) || (_articleDisplay == null) ||
			_article.isExpired()) {

			return false;
		}

		return true;
	}

	private static final String _PAGE = "/journal_article/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleTag.class);

	private JournalArticle _article;
	private JournalArticleDisplay _articleDisplay;
	private String _articleId;
	private boolean _dataAnalyticsTrackingEnabled = true;
	private String _ddmTemplateKey;
	private long _groupId;
	private String _languageId;
	private boolean _showTitle;
	private String _wrapperCssClass;

}