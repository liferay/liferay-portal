/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.configuration.BlogsFileUploadsConfiguration;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.settings.BlogsGroupServiceSettings;
import com.liferay.blogs.web.internal.display.context.BlogsEditEntryDisplayContext;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.blogs.configuration.BlogsFileUploadsConfiguration",
	property = {
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_AGGREGATOR,
		"mvc.command.name=/blogs/edit_entry"
	},
	service = MVCRenderCommand.class
)
public class EditEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			BlogsEntry entry = ActionUtil.getEntry(renderRequest);

			if (entry != null) {
				_blogsEntryModelResourcePermission.check(
					themeDisplay.getPermissionChecker(), entry,
					ActionKeys.UPDATE);

				CTTimelineUtil.setCTTimelineKeys(
					renderRequest, BlogsEntry.class, entry.getPrimaryKey());
			}

			httpServletRequest.setAttribute(
				AssetVocabularyLocalService.class.getName(),
				_assetVocabularyLocalService);
			httpServletRequest.setAttribute(
				ItemSelector.class.getName(), _itemSelector);
			httpServletRequest.setAttribute(
				SiteConnectedGroupGroupProvider.class.getName(),
				_siteConnectedGroupGroupProvider);

			renderRequest.setAttribute(
				BlogsEditEntryDisplayContext.class.getName(),
				new BlogsEditEntryDisplayContext(
					_getAssetAutoTaggerConfiguration(renderRequest),
					_assetVocabularyLocalService, entry,
					_blogsFileUploadsConfiguration,
					BlogsGroupServiceSettings.getInstance(
						themeDisplay.getScopeGroupId()),
					httpServletRequest, _itemSelector,
					_portal.getLiferayPortletResponse(renderResponse),
					_siteConnectedGroupGroupProvider));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/blogs/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/blogs/edit_entry.jsp";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_blogsFileUploadsConfiguration = ConfigurableUtil.createConfigurable(
			BlogsFileUploadsConfiguration.class, properties);
	}

	private AssetAutoTaggerConfiguration _getAssetAutoTaggerConfiguration(
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _assetAutoTaggerConfigurationFactory.
			getGroupAssetAutoTaggerConfiguration(themeDisplay.getSiteGroup());
	}

	@Reference
	private AssetAutoTaggerConfigurationFactory
		_assetAutoTaggerConfigurationFactory;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference(target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)")
	private volatile ModelResourcePermission<BlogsEntry>
		_blogsEntryModelResourcePermission;

	private volatile BlogsFileUploadsConfiguration
		_blogsFileUploadsConfiguration;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}