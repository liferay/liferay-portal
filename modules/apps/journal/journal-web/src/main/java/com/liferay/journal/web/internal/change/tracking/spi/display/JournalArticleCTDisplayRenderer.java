/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.diff.exception.CompareVersionsException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = CTDisplayRenderer.class)
public class JournalArticleCTDisplayRenderer
	extends BaseCTDisplayRenderer<JournalArticle> {

	@Override
	public JournalArticle fetchLatestVersionedModel(
		JournalArticle journalArticle) {

		return _journalArticleLocalService.fetchLatestArticle(
			journalArticle.getResourcePrimKey());
	}

	@Override
	public String[] getAvailableLanguageIds(JournalArticle journalArticle) {
		return journalArticle.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(JournalArticle journalArticle) {
		return journalArticle.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			JournalArticle journalArticle)
		throws PortalException {

		Group group = _groupLocalService.getGroup(journalArticle.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, JournalPortletKeys.JOURNAL, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"articleId", journalArticle.getArticleId()
		).setParameter(
			"groupId", journalArticle.getGroupId()
		).setParameter(
			"version", journalArticle.getVersion()
		).buildString();
	}

	@Override
	public Class<JournalArticle> getModelClass() {
		return JournalArticle.class;
	}

	@Override
	public String getTitle(Locale locale, JournalArticle journalArticle) {
		return journalArticle.getTitle(locale);
	}

	@Override
	public String getVersionName(JournalArticle journalArticle) {
		return String.valueOf(journalArticle.getVersion());
	}

	@Override
	public String renderPreview(DisplayContext<JournalArticle> displayContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		PortletRequestModel portletRequestModel = new PortletRequestModel(
			portletRequest, portletResponse);

		JournalArticle journalArticle = displayContext.getModel();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_journalArticleLocalService.isRenderable(
				journalArticle, portletRequestModel, themeDisplay)) {

			throw new CompareVersionsException(journalArticle.getVersion());
		}

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				journalArticle, null, Constants.VIEW,
				_language.getLanguageId(displayContext.getLocale()), 1,
				portletRequestModel, themeDisplay);

		return journalArticleDisplay.getContent();
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(DisplayBuilder<JournalArticle> displayBuilder) {
		JournalArticle journalArticle = displayBuilder.getModel();

		displayBuilder.display(
			"name", journalArticle.getTitle(displayBuilder.getLocale())
		).display(
			"description",
			journalArticle.getDescription(displayBuilder.getLocale())
		).display(
			"created-by",
			() -> {
				String userName = journalArticle.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", journalArticle.getCreateDate()
		).display(
			"last-modified", journalArticle.getModifiedDate()
		).display(
			"version", journalArticle.getVersion()
		).display(
			"structure",
			() -> {
				DDMStructure ddmStructure = journalArticle.getDDMStructure();

				return ddmStructure.getName(displayBuilder.getLocale());
			}
		).display(
			"template",
			() -> {
				DDMTemplate ddmTemplate = journalArticle.getDDMTemplate();

				return ddmTemplate.getName(displayBuilder.getLocale());
			}
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}