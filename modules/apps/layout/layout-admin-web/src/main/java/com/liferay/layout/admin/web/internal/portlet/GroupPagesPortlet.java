/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet;

import com.liferay.application.list.GroupProvider;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.configuration.LayoutUtilityPageThumbnailConfiguration;
import com.liferay.layout.admin.web.internal.constants.LayoutAdminWebKeys;
import com.liferay.layout.admin.web.internal.display.context.LayoutUtilityPageEntryDisplayContext;
import com.liferay.layout.admin.web.internal.display.context.LayoutsAdminDisplayContext;
import com.liferay.layout.admin.web.internal.display.context.MillerColumnsDisplayContext;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.admin.web.internal.servlet.taglib.util.LayoutActionDropdownItemsProvider;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.GroupInheritContentException;
import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLsException;
import com.liferay.portal.kernel.exception.LayoutJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.exception.LayoutParentLayoutIdException;
import com.liferay.portal.kernel.exception.LayoutSetJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutSetVirtualHostException;
import com.liferay.portal.kernel.exception.LayoutTypeException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.RequiredLayoutException;
import com.liferay.portal.kernel.exception.RequiredLayoutPrototypeException;
import com.liferay.portal.kernel.exception.SitemapChangeFrequencyException;
import com.liferay.portal.kernel.exception.SitemapIncludeException;
import com.liferay.portal.kernel.exception.SitemapPagePriorityException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.translation.security.permission.TranslationPermission;
import com.liferay.translation.url.provider.TranslationURLProvider;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	configurationPid = "com.liferay.layout.admin.web.internal.configuration.LayoutUtilityPageThumbnailConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-layouts-admin",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/group_pages.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Group Pages",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.supported-public-render-parameter=layoutSetBranchId",
		"jakarta.portlet.supported-public-render-parameter=selPlid",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class GroupPagesPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		super.processAction(actionRequest, actionResponse);

		if (!SessionErrors.isEmpty(actionRequest)) {
			MutableRenderParameters renderParameters =
				actionResponse.getRenderParameters();

			renderParameters.setValue("checkboxNames", StringPool.BLANK);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_layoutUtilityPageThumbnailConfiguration =
			ConfigurableUtil.createConfigurable(
				LayoutUtilityPageThumbnailConfiguration.class, properties);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		Group group = _groupProvider.getGroup(
			_portal.getHttpServletRequest(renderRequest));

		if (group.isCompany()) {
			throw new PortletException();
		}

		renderRequest.setAttribute(WebKeys.GROUP, group);

		if (SessionErrors.contains(
				renderRequest, NoSuchGroupException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			try {
				List<LayoutPrototype> layoutPrototypes =
					_layoutPrototypeLocalService.getLayoutPrototypes(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				for (LayoutPrototype layoutPrototype : layoutPrototypes) {
					LayoutPageTemplateEntry layoutPageTemplateEntry =
						_layoutPageTemplateEntryLocalService.
							fetchFirstLayoutPageTemplateEntry(
								layoutPrototype.getLayoutPrototypeId());

					if (layoutPageTemplateEntry == null) {
						try (SafeCloseable safeCloseable =
								CTCollectionThreadLocal.
									setCTCollectionIdWithSafeCloseable(
										group.getCtCollectionId())) {

							_layoutPageTemplateEntryLocalService.
								addGlobalLayoutPageTemplateEntry(
									layoutPrototype);
						}
					}
				}

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(renderRequest);

				ServiceContextThreadLocal.pushServiceContext(serviceContext);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}

			renderRequest.setAttribute(CETManager.class.getName(), _cetManager);
			renderRequest.setAttribute(
				ItemSelector.class.getName(), _itemSelector);
			renderRequest.setAttribute(
				LayoutUtilityPageThumbnailConfiguration.class.getName(),
				_layoutUtilityPageThumbnailConfiguration);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
				_layoutConverterRegistry, themeDisplay, _translationPermission);

			LayoutsAdminDisplayContext layoutsAdminDisplayContext =
				new LayoutsAdminDisplayContext(
					_itemSelector, layoutActionsHelper, _layoutLocalService,
					_layoutSetPrototypeHelper,
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse));

			LayoutActionDropdownItemsProvider
				layoutActionDropdownItemsProvider =
					new LayoutActionDropdownItemsProvider(
						_portal.getHttpServletRequest(renderRequest),
						layoutActionsHelper, layoutsAdminDisplayContext,
						_portal.getLiferayPortletResponse(renderResponse),
						_translationURLProvider);

			renderRequest.setAttribute(
				LayoutAdminWebKeys.LAYOUT_ACTION_DROPDOWN_ITEMS_PROVIDER,
				layoutActionDropdownItemsProvider);

			renderRequest.setAttribute(
				LayoutAdminWebKeys.LAYOUT_PAGE_LAYOUT_ADMIN_DISPLAY_CONTEXT,
				layoutsAdminDisplayContext);
			renderRequest.setAttribute(
				LayoutAdminWebKeys.MILLER_COLUMNS_DISPLAY_CONTEXT,
				new MillerColumnsDisplayContext(
					_layoutSetPrototypeHelper, layoutsAdminDisplayContext,
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse)));
			renderRequest.setAttribute(
				LayoutUtilityPageEntryDisplayContext.class.getName(),
				new LayoutUtilityPageEntryDisplayContext(
					renderRequest, renderResponse));
			renderRequest.setAttribute(
				TranslationURLProvider.class.getName(),
				_translationURLProvider);

			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isAlwaysSendRedirect() {
		return true;
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetCategoryException ||
			throwable instanceof AssetTagException ||
			throwable instanceof DDMFormValuesValidationException ||
			throwable instanceof DuplicateFriendlyURLEntryException ||
			throwable instanceof
				DuplicateLayoutPageTemplateCollectionException ||
			throwable instanceof GroupInheritContentException ||
			throwable instanceof ImageTypeException ||
			throwable instanceof LayoutFriendlyURLException ||
			throwable instanceof LayoutFriendlyURLsException ||
			throwable instanceof LayoutJavaScriptException ||
			throwable instanceof LayoutNameException ||
			throwable instanceof LayoutPageTemplateCollectionNameException ||
			throwable instanceof LayoutParentLayoutIdException ||
			throwable instanceof LayoutSetJavaScriptException ||
			throwable instanceof LayoutSetVirtualHostException ||
			throwable instanceof LayoutTypeException ||
			throwable instanceof NoSuchGroupException ||
			throwable instanceof PrincipalException ||
			throwable instanceof RequiredLayoutException ||
			throwable instanceof RequiredLayoutPrototypeException ||
			throwable instanceof SitemapChangeFrequencyException ||
			throwable instanceof SitemapIncludeException ||
			throwable instanceof SitemapPagePriorityException ||
			throwable instanceof UploadException) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupPagesPortlet.class);

	@Reference
	private CETManager _cetManager;

	@Reference
	private GroupProvider _groupProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private LayoutConverterRegistry _layoutConverterRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	private volatile LayoutUtilityPageThumbnailConfiguration
		_layoutUtilityPageThumbnailConfiguration;

	@Reference
	private Portal _portal;

	@Reference
	private TranslationPermission _translationPermission;

	@Reference
	private TranslationURLProvider _translationURLProvider;

}