/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.exception.NoSuchCommentException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.portal.kernel.exception.NoSuchSubscriptionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=knowledge-base-portlet knowledge-base-portlet-section",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.header-portlet-css=/admin/css/common.css",
		"com.liferay.portlet.header-portlet-css=/section/css/main.css",
		"com.liferay.portlet.icon=/icons/section.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.struts-path=knowledge_base",
		"jakarta.portlet.display-name=Knowledge Base Section",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/knowledge_base/view",
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=categoryId",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SectionPortlet extends BaseKBPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchArticleException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchCommentException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchSubscriptionException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/admin/common/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected void doRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE,
				_getKBArticle(renderRequest));
			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_STATUS,
				WorkflowConstants.STATUS_APPROVED);
		}
		catch (NoSuchArticleException | PrincipalException exception) {
			SessionErrors.add(renderRequest, exception.getClass());
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private KBArticle _getKBArticle(RenderRequest renderRequest)
		throws PortalException {

		long resourcePrimKey = ParamUtil.getLong(
			renderRequest, "resourcePrimKey");

		if (resourcePrimKey > 0) {
			return kbArticleService.getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_APPROVED);
		}

		String urlTitle = ParamUtil.getString(renderRequest, "urlTitle");

		if (Validator.isNull(urlTitle)) {
			return null;
		}

		long groupId = portal.getScopeGroupId(renderRequest);

		String kbFolderUrlTitle = ParamUtil.getString(
			renderRequest, "kbFolderUrlTitle");

		if (Validator.isNotNull(kbFolderUrlTitle)) {
			return _kbArticleLocalService.getKBArticleByUrlTitle(
				groupId, kbFolderUrlTitle, urlTitle);
		}

		return _kbArticleLocalService.getKBArticleByUrlTitle(
			groupId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, urlTitle);
	}

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.knowledge.base.web)(&(release.schema.version>=1.2.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}