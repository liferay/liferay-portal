/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.portlet;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.kernel.staging.StagingURLHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.LayoutBranchNameException;
import com.liferay.portal.kernel.exception.LayoutSetBranchNameException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.staging.bar.web.internal.portlet.constants.StagingBarPortletKeys;
import com.liferay.staging.constants.StagingProcessesWebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.net.ConnectException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Levente Hudák
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-staging-bar",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"com.liferay.portlet.show-portlet-inactive=false",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.display-name=Staging Bar",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + StagingBarPortletKeys.STAGING_BAR,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class StagingBarPortlet extends MVCPortlet {

	public void deleteLayoutRevision(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutRevisionId = ParamUtil.getLong(
			actionRequest, "layoutRevisionId");

		LayoutRevision layoutRevision =
			_layoutRevisionLocalService.getLayoutRevision(layoutRevisionId);

		_layoutRevisionLocalService.deleteLayoutRevision(layoutRevision);

		_deleteUnusedLayoutIconImage(layoutRevision);

		boolean updateRecentLayoutRevisionId = ParamUtil.getBoolean(
			actionRequest, "updateRecentLayoutRevisionId");

		if (updateRecentLayoutRevisionId) {
			_staging.setRecentLayoutRevisionId(
				_portal.getHttpServletRequest(actionRequest),
				layoutRevision.getLayoutSetBranchId(), layoutRevision.getPlid(),
				layoutRevision.getParentLayoutRevisionId());
		}

		_addLayoutRevisionSessionMessages(actionRequest, actionResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = _layoutLocalService.fetchLayout(themeDisplay.getPlid());

		Layout selLayout = layout;

		long selPlid = ParamUtil.getLong(httpServletRequest, "selPlid");

		if (selPlid > 0) {
			try {
				selLayout = _layoutLocalService.getLayout(selPlid);
			}
			catch (PortalException portalException) {
				throw new PortletException(portalException);
			}
		}

		Group group = null;
		boolean privateLayout = false;

		if (selLayout != null) {
			group = selLayout.getGroup();

			privateLayout = selLayout.isPrivateLayout();
		}

		boolean branchingEnabled = false;
		LayoutBranch layoutBranch = null;
		LayoutRevision layoutRevision = null;
		LayoutSetBranch layoutSetBranch = null;

		if (layout != null) {
			Layout layoutRevisionLayout = layout;

			if (layout.isDraftLayout()) {
				layoutRevisionLayout = _layoutLocalService.fetchLayout(
					layout.getClassPK());
			}

			layoutRevision = LayoutStagingUtil.getLayoutRevision(
				layoutRevisionLayout);

			if (layoutRevision != null) {
				branchingEnabled = true;

				try {
					layoutSetBranch =
						_layoutSetBranchLocalService.getLayoutSetBranch(
							layoutRevision.getLayoutSetBranchId());

					layoutBranch = layoutRevision.getLayoutBranch();
				}
				catch (PortalException portalException) {
					throw new PortletException(portalException);
				}
			}
		}

		List<LayoutSetBranch> layoutSetBranches = null;
		Group liveGroup = _staging.getLiveGroup(group.getGroupId());
		String liveURL = null;
		Layout liveLayout = null;
		Group stagingGroup = _staging.getStagingGroup(group.getGroupId());
		String remoteSiteURL = StringPool.BLANK;
		String remoteURL = null;
		String stagingURL = null;

		if (themeDisplay.isShowStagingIcon()) {
			long originalScopeGroupId = themeDisplay.getScopeGroupId();

			if (liveGroup != null) {
				liveLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					layout.getUuid(), liveGroup.getGroupId(),
					layout.isPrivateLayout());

				if (liveLayout != null) {
					try {
						if (liveLayout.isTypeAssetDisplay()) {
							themeDisplay.setScopeGroupId(
								liveGroup.getGroupId());

							_setScopedAssetEntry(
								themeDisplay.getRequest(),
								liveGroup.getGroupId());
						}

						liveURL = _portal.getLayoutURL(
							liveLayout, themeDisplay);
					}
					catch (PortalException portalException) {
						throw new PortletException(portalException);
					}
				}
				else if ((layout.isPrivateLayout() &&
						  (liveGroup.getPrivateLayoutsPageCount() > 0)) ||
						 (layout.isPublicLayout() &&
						  (liveGroup.getPublicLayoutsPageCount() > 0))) {

					liveURL = liveGroup.getDisplayURL(
						themeDisplay, layout.isPrivateLayout());
				}
			}

			if (stagingGroup != null) {
				Layout stagingLayout =
					_layoutLocalService.fetchLayoutByUuidAndGroupId(
						layout.getUuid(), stagingGroup.getGroupId(),
						layout.isPrivateLayout());

				if (stagingLayout != null) {
					try {
						if (stagingLayout.isTypeAssetDisplay()) {
							themeDisplay.setScopeGroupId(
								stagingGroup.getGroupId());

							_setScopedAssetEntry(
								themeDisplay.getRequest(),
								stagingGroup.getGroupId());
						}

						stagingURL = _portal.getLayoutURL(
							stagingLayout, themeDisplay);
					}
					catch (PortalException portalException) {
						throw new PortletException(portalException);
					}
				}
				else {
					stagingURL = stagingGroup.getDisplayURL(
						themeDisplay, layout.isPrivateLayout());
				}
			}

			Object originalAssetEntry = httpServletRequest.getAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY);

			httpServletRequest.setAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY, originalAssetEntry);

			if (originalAssetEntry instanceof AssetEntry) {
				AssetEntry assetEntry = (AssetEntry)originalAssetEntry;

				LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
					httpServletRequest, assetEntry.getEntryId());
			}

			themeDisplay.setScopeGroupId(originalScopeGroupId);

			if (group.isStagingGroup() || group.isStagedRemotely()) {
				layoutSetBranches =
					_layoutSetBranchLocalService.getLayoutSetBranches(
						stagingGroup.getGroupId(), layout.isPrivateLayout());
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			String remoteAddress = typeSettingsUnicodeProperties.getProperty(
				"remoteAddress");
			int remotePort = GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty("remotePort"));
			String remotePathContext =
				typeSettingsUnicodeProperties.getProperty("remotePathContext");
			boolean secureConnection = GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty("secureConnection"));

			remoteURL = _stagingURLHelper.buildRemoteURL(
				remoteAddress, remotePort, remotePathContext, secureConnection);

			if ((liveGroup != null) && group.isStagedRemotely()) {
				try {
					remoteSiteURL = _staging.getRemoteSiteURL(
						group, layout.isPrivateLayout());
				}
				catch (AuthException authException) {
					_log.error(authException);

					SessionErrors.add(renderRequest, AuthException.class);
				}
				catch (SystemException systemException) {
					Throwable throwable = systemException.getCause();

					if (!(throwable instanceof ConnectException)) {
						throw systemException;
					}

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to connect to remote live: " +
								throwable.getMessage());
					}

					SessionErrors.add(
						renderRequest, RemoteExportException.class);
				}
				catch (Exception exception) {
					_log.error(exception);

					SessionErrors.add(renderRequest, Exception.class);
				}
			}
		}

		renderRequest.setAttribute(
			StagingProcessesWebKeys.BRANCHING_ENABLED,
			String.valueOf(branchingEnabled));
		renderRequest.setAttribute(
			StagingProcessesWebKeys.LAYOUT_BRANCH, layoutBranch);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.LAYOUT_SET_BRANCH, layoutSetBranch);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.LAYOUT_SET_BRANCHES, layoutSetBranches);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.LIVE_GROUP, liveGroup);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.LIVE_LAYOUT, liveLayout);
		renderRequest.setAttribute(StagingProcessesWebKeys.LIVE_URL, liveURL);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.REMOTE_SITE_URL, remoteSiteURL);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.REMOTE_URL, remoteURL);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.STAGING_GROUP, stagingGroup);
		renderRequest.setAttribute(
			StagingProcessesWebKeys.STAGING_URL, stagingURL);
		renderRequest.setAttribute(WebKeys.GROUP, group);
		renderRequest.setAttribute(WebKeys.LAYOUT, layout);
		renderRequest.setAttribute(WebKeys.LAYOUT_REVISION, layoutRevision);
		renderRequest.setAttribute(
			WebKeys.PRIVATE_LAYOUT, String.valueOf(privateLayout));

		super.render(renderRequest, renderResponse);
	}

	public void updateLayoutRevision(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long layoutRevisionId = ParamUtil.getLong(
			actionRequest, "layoutRevisionId");

		LayoutRevision layoutRevision =
			_layoutRevisionLocalService.getLayoutRevision(layoutRevisionId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_updateParentLayoutsRevisions(layoutRevision, serviceContext);

		LayoutRevision enableLayoutRevision =
			_layoutRevisionLocalService.updateLayoutRevision(
				serviceContext.getUserId(), layoutRevisionId,
				layoutRevision.getLayoutBranchId(), layoutRevision.getName(),
				layoutRevision.getTitle(), layoutRevision.getDescription(),
				layoutRevision.getKeywords(), layoutRevision.getRobots(),
				layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
				layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
				layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
				serviceContext);

		if (layoutRevision.getStatus() != WorkflowConstants.STATUS_INCOMPLETE) {
			_staging.setRecentLayoutRevisionId(
				themeDisplay.getUser(), layoutRevision.getLayoutSetBranchId(),
				layoutRevision.getPlid(), layoutRevision.getLayoutRevisionId());

			_addLayoutRevisionSessionMessages(actionRequest, actionResponse);

			return;
		}

		LayoutRevision lastLayoutRevision =
			_layoutRevisionLocalService.fetchLastLayoutRevision(
				enableLayoutRevision.getPlid(), true);

		if (lastLayoutRevision != null) {
			LayoutRevision newLayoutRevision =
				_layoutRevisionLocalService.addLayoutRevision(
					serviceContext.getUserId(),
					layoutRevision.getLayoutSetBranchId(),
					layoutRevision.getLayoutBranchId(),
					enableLayoutRevision.getLayoutRevisionId(), false,
					layoutRevision.getPlid(),
					lastLayoutRevision.getLayoutRevisionId(),
					lastLayoutRevision.isPrivateLayout(),
					lastLayoutRevision.getName(), lastLayoutRevision.getTitle(),
					lastLayoutRevision.getDescription(),
					lastLayoutRevision.getKeywords(),
					lastLayoutRevision.getRobots(),
					lastLayoutRevision.getTypeSettings(),
					lastLayoutRevision.isIconImage(),
					lastLayoutRevision.getIconImageId(),
					lastLayoutRevision.getThemeId(),
					lastLayoutRevision.getColorSchemeId(),
					lastLayoutRevision.getCss(), serviceContext);

			_staging.setRecentLayoutRevisionId(
				themeDisplay.getUser(),
				newLayoutRevision.getLayoutSetBranchId(),
				newLayoutRevision.getPlid(),
				newLayoutRevision.getLayoutRevisionId());
		}

		_addLayoutRevisionSessionMessages(actionRequest, actionResponse);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchGroupException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses()) ||
			SessionErrors.contains(
				renderRequest, SystemException.class.getName())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof LayoutBranchNameException ||
			throwable instanceof LayoutSetBranchNameException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	private void _addLayoutRevisionSessionMessages(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		MultiSessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) + "requestProcessed");

		sendRedirect(actionRequest, actionResponse);
	}

	private void _deleteUnusedLayoutIconImage(LayoutRevision layoutRevision)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(
			layoutRevision.getPlid());

		if (layout == null) {
			return;
		}

		long layoutRevisionIconImageId = _beanProperties.getLong(
			layoutRevision, "iconImageId");

		if (layoutRevisionIconImageId == GetterUtil.DEFAULT_LONG) {
			layoutRevisionIconImageId = _beanProperties.getLong(
				layout, "iconImageId");
		}

		DynamicQuery layoutRevisionDynamicQuery =
			_layoutRevisionLocalService.dynamicQuery();

		layoutRevisionDynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"iconImageId", layoutRevisionIconImageId));

		long sameImageCount = _layoutRevisionLocalService.dynamicQueryCount(
			layoutRevisionDynamicQuery);

		DynamicQuery layoutDynamicQuery = _layoutLocalService.dynamicQuery();

		layoutDynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"iconImageId", layoutRevisionIconImageId));
		layoutDynamicQuery.add(
			RestrictionsFactoryUtil.ne("plid", layout.getPlid()));

		sameImageCount += _layoutLocalService.dynamicQueryCount(
			layoutDynamicQuery);

		if ((layoutRevisionIconImageId > 0) && (sameImageCount < 1)) {
			layout.setIconImageId(layoutRevisionIconImageId);

			_portal.updateImageId(layout, false, null, "iconImageId", 0, 0, 0);
		}
	}

	private void _setScopedAssetEntry(
		HttpServletRequest httpServletRequest, long groupId) {

		Object object = httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);

		if ((object != null) && (object instanceof AssetEntry)) {
			AssetEntry assetEntry = (AssetEntry)object;

			AssetEntry scopedAssetEntry = _assetEntryLocalService.fetchEntry(
				groupId, assetEntry.getClassUuid());

			httpServletRequest.setAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY, scopedAssetEntry);

			if (scopedAssetEntry != null) {
				LinkedAssetEntryIdsUtil.replaceLinkedAssetEntryId(
					httpServletRequest, assetEntry.getEntryId(),
					scopedAssetEntry.getEntryId());
			}
		}
	}

	private void _updateParentLayoutsRevisions(
			LayoutRevision layoutRevision, ServiceContext serviceContext)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(
			layoutRevision.getPlid());

		if (layout == null) {
			return;
		}

		long parentPlid = layout.getParentPlid();

		Layout parentLayout = null;

		while (true) {
			parentLayout = _layoutLocalService.fetchLayout(parentPlid);

			if (parentLayout == null) {
				break;
			}

			List<LayoutRevision> parentHeadLayoutRevisions =
				_layoutRevisionLocalService.getLayoutRevisions(
					layoutRevision.getLayoutSetBranchId(),
					parentLayout.getPlid(), true);

			if (parentHeadLayoutRevisions.isEmpty()) {
				LayoutRevision parentLayoutRevision =
					_layoutRevisionLocalService.fetchLatestLayoutRevision(
						layoutRevision.getLayoutSetBranchId(),
						parentLayout.getPlid());

				if (parentLayoutRevision != null) {
					_layoutRevisionLocalService.updateLayoutRevision(
						serviceContext.getUserId(),
						parentLayoutRevision.getLayoutRevisionId(),
						parentLayoutRevision.getLayoutBranchId(),
						parentLayoutRevision.getName(),
						parentLayoutRevision.getTitle(),
						parentLayoutRevision.getDescription(),
						parentLayoutRevision.getKeywords(),
						parentLayoutRevision.getRobots(),
						parentLayoutRevision.getTypeSettings(),
						parentLayoutRevision.getIconImage(),
						parentLayoutRevision.getIconImageId(),
						parentLayoutRevision.getThemeId(),
						parentLayoutRevision.getColorSchemeId(),
						parentLayoutRevision.getCss(), serviceContext);
				}
			}

			parentPlid = parentLayout.getParentPlid();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingBarPortlet.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.staging.bar.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private Staging _staging;

	@Reference
	private StagingURLHelper _stagingURLHelper;

}