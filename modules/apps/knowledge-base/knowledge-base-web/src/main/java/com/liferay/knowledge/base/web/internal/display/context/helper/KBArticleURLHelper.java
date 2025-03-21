/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context.helper;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Adolfo Pérez
 */
public class KBArticleURLHelper {

	public KBArticleURLHelper(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public PortletURL createViewURL(KBArticle kbArticle)
		throws PortalException {

		String portletId = PortalUtil.getPortletId(_renderRequest);

		if (portletId.startsWith(KBPortletKeys.KNOWLEDGE_BASE_ADMIN) ||
			portletId.startsWith(KBPortletKeys.KNOWLEDGE_BASE_SEARCH)) {

			return _createKBAdminViewURL(kbArticle);
		}

		if (portletId.startsWith(KBPortletKeys.KNOWLEDGE_BASE_SECTION)) {
			return _createKBSectionViewURL(kbArticle);
		}

		return _createKBDisplayViewURL(kbArticle);
	}

	public PortletURL createViewWithCommentsURL(KBArticle kbArticle)
		throws PortalException {

		return PortletURLBuilder.create(
			createViewURL(kbArticle)
		).setParameter(
			"expanded", true
		).buildPortletURL();
	}

	public PortletURL createViewWithRedirectURL(
			KBArticle kbArticle, String redirect)
		throws PortalException {

		if (Validator.isNull(redirect)) {
			return createViewURL(kbArticle);
		}

		return PortletURLBuilder.create(
			createViewURL(kbArticle)
		).setRedirect(
			redirect
		).buildPortletURL();
	}

	private PortletURL _createKBAdminViewURL(KBArticle kbArticle) {
		return PortletURLBuilder.create(
			_renderResponse.createRenderURL()
		).setMVCRenderCommandName(
			"/knowledge_base/view_kb_article"
		).setParameter(
			"resourceClassNameId", kbArticle.getClassNameId()
		).setParameter(
			"resourcePrimKey", kbArticle.getResourcePrimKey()
		).setParameter(
			"selectedItemId", kbArticle.getResourcePrimKey()
		).buildPortletURL();
	}

	private PortletURL _createKBDisplayViewURL(KBArticle kbArticle)
		throws PortalException {

		if (Validator.isNull(kbArticle.getUrlTitle())) {
			return PortletURLBuilder.create(
				_renderResponse.createRenderURL()
			).setParameter(
				"resourceClassNameId", kbArticle.getClassNameId()
			).setParameter(
				"resourcePrimKey", kbArticle.getResourcePrimKey()
			).buildPortletURL();
		}

		return PortletURLBuilder.create(
			_renderResponse.createRenderURL()
		).setParameter(
			"urlTitle", kbArticle.getUrlTitle()
		).setParameter(
			"kbFolderUrlTitle", _getKBFolderURLTitle(kbArticle), false
		).buildPortletURL();
	}

	private PortletURL _createKBSectionViewURL(KBArticle kbArticle)
		throws PortalException {

		if (Validator.isNull(kbArticle.getUrlTitle())) {
			return _createKBAdminViewURL(kbArticle);
		}

		return PortletURLBuilder.create(
			_renderResponse.createRenderURL()
		).setMVCRenderCommandName(
			"/knowledge_base/view_kb_article"
		).setParameter(
			"urlTitle", kbArticle.getUrlTitle()
		).setParameter(
			"kbFolderUrlTitle", _getKBFolderURLTitle(kbArticle), false
		).buildPortletURL();
	}

	private String _getKBFolderURLTitle(KBArticle kbArticle)
		throws PortalException {

		if (kbArticle.getKbFolderId() ==
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return null;
		}

		KBFolder kbFolder = KBFolderServiceUtil.getKBFolder(
			kbArticle.getKbFolderId());

		return kbFolder.getUrlTitle();
	}

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}