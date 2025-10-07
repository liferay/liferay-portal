/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.change.tracking.spi.display;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vy Bui
 */
@Component(service = CTDisplayRenderer.class)
public class KBArticleCTDisplayRenderer
	extends BaseCTDisplayRenderer<KBArticle> {

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest, KBArticle kbArticle) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/knowledge_base/view_kb_article"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"kbArticleId", kbArticle.getKbArticleId()
		).buildString();
	}

	@Override
	public Class<KBArticle> getModelClass() {
		return KBArticle.class;
	}

	@Override
	public String getTitle(Locale locale, KBArticle kbArticle)
		throws PortalException {

		return kbArticle.getTitle();
	}

	@Override
	public String renderPreview(DisplayContext<KBArticle> displayContext)
		throws Exception {

		return renderDisplayPagePreview(
			_assetDisplayPageFriendlyURLProvider, displayContext);
	}

	@Override
	protected void buildDisplay(DisplayBuilder<KBArticle> displayBuilder) {
		KBArticle kbArticle = displayBuilder.getModel();

		displayBuilder.display(
			"name", kbArticle.getTitle()
		).display(
			"content", kbArticle.getContent()
		).display(
			"description", kbArticle.getDescription()
		).display(
			"created-by",
			() -> {
				String userName = kbArticle.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", kbArticle.getCreateDate()
		).display(
			"last-modified", kbArticle.getModifiedDate()
		);
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private Portal _portal;

}