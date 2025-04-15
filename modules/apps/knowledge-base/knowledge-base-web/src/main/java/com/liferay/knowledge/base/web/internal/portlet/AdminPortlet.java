/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet;

import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.KBArticleImportException;
import com.liferay.knowledge.base.exception.KBTemplateContentException;
import com.liferay.knowledge.base.exception.KBTemplateTitleException;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.exception.NoSuchCommentException;
import com.liferay.knowledge.base.exception.NoSuchFolderException;
import com.liferay.knowledge.base.exception.NoSuchTemplateException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.portal.kernel.exception.NoSuchSubscriptionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.util.TrashWebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 * @author Eric Min
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=knowledge-base-portlet knowledge-base-portlet-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/admin/css/common.css",
		"com.liferay.portlet.header-portlet-css=/admin/css/main.css",
		"com.liferay.portlet.icon=/icons/admin.png",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"jakarta.portlet.display-name=Knowledge Base",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.portlet-title-based-navigation=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/knowledge_base/view",
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=categoryId",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AdminPortlet extends BaseKBPortlet {

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		String resourceID = GetterUtil.getString(
			resourceRequest.getResourceID());

		if (resourceID.equals("infoPanel")) {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(resourceRequest);

			try {
				resourceRequest.setAttribute(
					KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLES,
					getKBArticles(httpServletRequest));
				resourceRequest.setAttribute(
					KBWebKeys.KNOWLEDGE_BASE_KB_FOLDERS,
					_getKBFolders(httpServletRequest));
				resourceRequest.setAttribute(
					TrashWebKeys.TRASH_HELPER, _trashHelper);

				PortletSession portletSession =
					resourceRequest.getPortletSession();

				PortletContext portletContext =
					portletSession.getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher(
						"/admin/info_panel.jsp");

				portletRequestDispatcher.include(
					resourceRequest, resourceResponse);
			}
			catch (PortalException portalException) {
				throw new PortletException(portalException);
			}
		}
		else {
			super.serveResource(resourceRequest, resourceResponse);
		}
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
				renderRequest, NoSuchTemplateException.class.getName()) ||
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

			long kbArticleClassNameId = _portal.getClassNameId(
				KBArticleConstants.getClassName());

			long resourceClassNameId = ParamUtil.getLong(
				renderRequest, "resourceClassNameId", kbArticleClassNameId);

			long resourcePrimKey = ParamUtil.getLong(
				renderRequest, "resourcePrimKey");
			int status = WorkflowConstants.STATUS_ANY;

			if ((resourcePrimKey > 0) &&
				(resourceClassNameId == kbArticleClassNameId)) {

				kbArticle = kbArticleService.getLatestKBArticle(
					resourcePrimKey, status);

				CTTimelineUtil.setCTTimelineKeys(
					renderRequest, KBArticle.class, kbArticle.getKbArticleId());
			}

			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE, kbArticle);

			KBArticle parentKBArticle = null;
			KBFolder parentKBFolder = null;

			long kbFolderClassNameId = _portal.getClassNameId(
				KBFolderConstants.getClassName());

			long parentResourceClassNameId = ParamUtil.getLong(
				renderRequest, "parentResourceClassNameId",
				kbFolderClassNameId);

			long parentResourcePrimKey = ParamUtil.getLong(
				renderRequest, "parentResourcePrimKey");

			if (parentResourcePrimKey > 0) {
				if (parentResourceClassNameId == kbFolderClassNameId) {
					parentKBFolder = kbFolderService.getKBFolder(
						parentResourcePrimKey);

					CTTimelineUtil.setCTTimelineKeys(
						renderRequest, KBFolder.class,
						parentKBFolder.getKbFolderId());
				}
				else {
					parentKBArticle = kbArticleService.getLatestKBArticle(
						parentResourcePrimKey, status);
				}
			}

			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_PARENT_KB_ARTICLE, parentKBArticle);
			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_PARENT_KB_FOLDER, parentKBFolder);

			KBTemplate kbTemplate = null;

			long kbTemplateId = ParamUtil.getLong(
				renderRequest, "kbTemplateId");

			if (kbTemplateId > 0) {
				kbTemplate = kbTemplateService.getKBTemplate(kbTemplateId);

				CTTimelineUtil.setCTTimelineKeys(
					renderRequest, KBTemplate.class,
					kbTemplate.getKbTemplateId());
			}

			renderRequest.setAttribute(
				KBWebKeys.KNOWLEDGE_BASE_KB_TEMPLATE, kbTemplate);

			renderRequest.setAttribute(KBWebKeys.KNOWLEDGE_BASE_STATUS, status);
		}
		catch (NoSuchArticleException | NoSuchFolderException |
			   NoSuchTemplateException | PrincipalException exception) {

			SessionErrors.add(renderRequest, exception.getClass());
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	protected List<KBArticle> getKBArticles(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<KBArticle> kbArticles = new ArrayList<>();

		long[] kbArticleResourcePrimKeys = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsKBArticle");

		for (long kbArticleResourcePrimKey : kbArticleResourcePrimKeys) {
			KBArticle kbArticle = kbArticleService.getLatestKBArticle(
				kbArticleResourcePrimKey, WorkflowConstants.STATUS_ANY);

			kbArticles.add(kbArticle);
		}

		return kbArticles;
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof KBArticleImportException ||
			throwable instanceof KBTemplateContentException ||
			throwable instanceof KBTemplateTitleException ||
			throwable instanceof NoSuchTemplateException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	private List<KBFolder> _getKBFolders(HttpServletRequest httpServletRequest)
		throws PortalException {

		List<KBFolder> kbFolders = new ArrayList<>();

		long[] kbFolderIds = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsKBFolder");

		for (long kbFolderId : kbFolderIds) {
			kbFolders.add(kbFolderService.getKBFolder(kbFolderId));
		}

		return kbFolders;
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.knowledge.base.web)(&(release.schema.version>=1.2.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private TrashHelper _trashHelper;

}