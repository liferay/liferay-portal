/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalPreviewArticleContentTemplateDisplayContext {

	public JournalPreviewArticleContentTemplateDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public JournalArticleDisplay getArticleDisplay() throws Exception {
		if (_articleDisplay != null) {
			return _articleDisplay;
		}

		JournalArticle article = JournalArticleLocalServiceUtil.fetchArticle(
			getGroupId(), getArticleId(), getVersion());

		if (article == null) {
			return _articleDisplay;
		}

		String ddmTemplateKey = StringPool.BLANK;

		DDMTemplate ddmTemplate = getDDMTemplate();

		if (ddmTemplate != null) {
			ddmTemplateKey = ddmTemplate.getTemplateKey();
		}

		int page = ParamUtil.getInteger(_renderRequest, "page");

		_articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(
			article, ddmTemplateKey, null, getLanguageId(), page,
			new PortletRequestModel(_renderRequest, _renderResponse),
			_themeDisplay);

		return _articleDisplay;
	}

	public String getArticleId() {
		if (_articleId != null) {
			return _articleId;
		}

		_articleId = ParamUtil.getString(_renderRequest, "articleId");

		return _articleId;
	}

	public DDMTemplate getDDMTemplate() {
		if (_ddmTemplate != null) {
			return _ddmTemplate;
		}

		_ddmTemplate = DDMTemplateLocalServiceUtil.fetchDDMTemplate(
			getDDMTemplateId());

		return _ddmTemplate;
	}

	public long getDDMTemplateId() {
		if (_ddmTemplateId != null) {
			return _ddmTemplateId;
		}

		_ddmTemplateId = ParamUtil.getLong(_renderRequest, "ddmTemplateId");

		return _ddmTemplateId;
	}

	public String getDDMTemplateJSON() {
		if (getDDMTemplateId() <= 0) {
			return null;
		}

		DDMTemplate ddmTemplate = getDDMTemplate();

		return JSONUtil.put(
			"ddmtemplateid", ddmTemplate.getTemplateId()
		).put(
			"ddmtemplatekey", ddmTemplate.getTemplateKey()
		).put(
			"name",
			HtmlUtil.escape(ddmTemplate.getName(_themeDisplay.getLocale()))
		).toString();
	}

	public List<DDMTemplate> getDDMTemplates() throws PortalException {
		JournalArticle article = JournalArticleLocalServiceUtil.fetchArticle(
			getGroupId(), getArticleId(), getVersion());

		if (article == null) {
			return Collections.emptyList();
		}

		DDMStructure ddmStructure = article.getDDMStructure();

		if (ddmStructure == null) {
			return Collections.emptyList();
		}

		return DDMTemplateLocalServiceUtil.getTemplates(
			article.getGroupId(), PortalUtil.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(), true);
	}

	public String getEventName() {
		if (_eventName != null) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_renderRequest, "eventName",
			_renderResponse.getNamespace() + "preview");

		return _eventName;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(_renderRequest, "groupId");

		return _groupId;
	}

	public String getLanguageId() {
		if (_languageId != null) {
			return _languageId;
		}

		_languageId = ParamUtil.getString(
			_renderRequest, "languageId", _themeDisplay.getLanguageId());

		return _languageId;
	}

	public PortletURL getPageIteratorPortletURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/preview_article_content_template.jsp"
		).setParameter(
			"articleId", getArticleId()
		).setParameter(
			"ddmTemplateId", getDDMTemplateId()
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"version", getVersion()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();
	}

	public PortletURL getPortletURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/preview_article_content_template.jsp"
		).setParameter(
			"articleId", getArticleId()
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"version", getVersion()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();
	}

	public Double getVersion() {
		if (_version != null) {
			return _version;
		}

		_version = ParamUtil.getDouble(_renderRequest, "version");

		return _version;
	}

	private JournalArticleDisplay _articleDisplay;
	private String _articleId;
	private DDMTemplate _ddmTemplate;
	private Long _ddmTemplateId;
	private String _eventName;
	private Long _groupId;
	private String _languageId;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private Double _version;

}