/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet;

import com.liferay.knowledge.base.constants.KBActionKeys;
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
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
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
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=knowledge-base-portlet knowledge-base-portlet-article",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.header-portlet-css=/admin/css/common.css",
		"com.liferay.portlet.header-portlet-css=/article/css/main.css",
		"com.liferay.portlet.icon=/icons/article.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.struts-path=knowledge_base",
		"jakarta.portlet.display-name=Knowledge Base Article",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/knowledge_base/view",
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=categoryId",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ArticlePortlet extends BaseKBPortlet {

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String actionName = ParamUtil.getString(
			actionRequest, ActionRequest.ACTION_NAME);

		if (actionName.equals("/knowledge_base/delete_kb_article")) {
			return;
		}

		super.addSuccessMessage(actionRequest, actionResponse);
	}

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
			KBArticle kbArticle = null;

			long resourcePrimKey = getResourcePrimKey(renderRequest);

			int status = ParamUtil.getInteger(
				renderRequest, "status", WorkflowConstants.STATUS_APPROVED);

			if (resourcePrimKey > 0) {
				kbArticle = kbArticleService.getLatestKBArticle(
					resourcePrimKey, status);
			}

			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE, kbArticle);
			renderRequest.setAttribute(KBWebKeys.KNOWLEDGE_BASE_STATUS, status);
		}
		catch (NoSuchArticleException | PrincipalException exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			SessionMessages.add(
				renderRequest,
				portal.getPortletId(renderRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	protected long getResourcePrimKey(RenderRequest renderRequest)
		throws PortalException {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		long defaultValue = GetterUtil.getLong(
			portletPreferences.getValue("resourcePrimKey", null));

		KBArticle defaultKBArticle = kbArticleService.fetchLatestKBArticle(
			defaultValue, WorkflowConstants.STATUS_ANY);

		if (defaultKBArticle == null) {
			defaultValue = 0;
		}

		String mvcPath = ParamUtil.getString(renderRequest, "mvcPath");

		if ((defaultValue == 0) && mvcPath.equals(viewTemplate)) {
			return 0;
		}

		long resourcePrimKey = _getResourcePrimKeyFromUrlTitle(renderRequest);

		if (resourcePrimKey == 0) {
			resourcePrimKey = ParamUtil.getLong(
				renderRequest, "resourcePrimKey", defaultValue);
		}

		if ((resourcePrimKey == 0) || (resourcePrimKey != defaultValue)) {
			return resourcePrimKey;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			KBWebKeys.THEME_DISPLAY);

		if (!_kbArticleModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), defaultValue,
				KBActionKeys.VIEW)) {

			return 0;
		}

		return defaultValue;
	}

	private long _getResourcePrimKeyFromUrlTitle(RenderRequest renderRequest)
		throws PortalException {

		String urlTitle = ParamUtil.getString(renderRequest, "urlTitle");

		if (Validator.isNull(urlTitle)) {
			return 0;
		}

		String kbFolderUrlTitle = ParamUtil.getString(
			renderRequest, "kbFolderUrlTitle");

		KBArticle kbArticle = null;

		if (Validator.isNull(kbFolderUrlTitle)) {
			kbArticle = _kbArticleLocalService.fetchKBArticleByUrlTitle(
				_portal.getScopeGroupId(renderRequest),
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, urlTitle);
		}
		else {
			kbArticle = _kbArticleLocalService.fetchKBArticleByUrlTitle(
				_portal.getScopeGroupId(renderRequest), kbFolderUrlTitle,
				urlTitle);
		}

		if (kbArticle != null) {
			return kbArticle.getResourcePrimKey();
		}

		return 0;
	}

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.knowledge.base.web)(&(release.schema.version>=1.2.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}