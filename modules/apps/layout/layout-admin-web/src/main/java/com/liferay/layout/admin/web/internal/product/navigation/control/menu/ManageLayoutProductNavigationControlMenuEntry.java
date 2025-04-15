/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.product.navigation.control.menu;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.taglib.aui.IconTag;
import com.liferay.taglib.servlet.PageContextFactoryUtil;
import com.liferay.taglib.ui.SuccessTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=100"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class ManageLayoutProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return null;
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isDraftLayout()) {
			layout = _layoutLocalService.fetchLayout(layout.getClassPK());
		}

		PortletURL editPageURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).buildPortletURL();

		String currentURL = _portal.getCurrentURL(httpServletRequest);

		editPageURL.setParameter("redirect", currentURL);
		editPageURL.setParameter("backURL", currentURL);

		if (layout.isSystem()) {
			editPageURL.setParameter(
				"portletResource",
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES);
		}

		editPageURL.setParameter(
			"groupId", String.valueOf(layout.getGroupId()));
		editPageURL.setParameter("selPlid", String.valueOf(layout.getPlid()));
		editPageURL.setParameter(
			"backURLTitle", layout.getName(themeDisplay.getLocale()));
		editPageURL.setParameter(
			"privateLayout", String.valueOf(layout.isPrivateLayout()));

		Map<String, String> values = HashMapBuilder.put(
			"configurePage",
			HtmlUtil.escape(
				_language.get(themeDisplay.getLocale(), "configure-page"))
		).put(
			"editPageURL", editPageURL.toString()
		).build();

		try {
			IconTag iconTag = new IconTag();

			iconTag.setCssClass("icon-monospaced");
			iconTag.setImage("cog");

			PageContext pageContext = PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse);

			values.put("iconCog", iconTag.doTagAsString(pageContext));

			SuccessTag successTag = new SuccessTag();

			successTag.setKey("layoutUpdated");
			successTag.setMessage(
				_language.get(
					themeDisplay.getLocale(),
					"changes-were-saved-successfully"));
			successTag.setTargetNode("#controlMenuAlertsContainer");

			values.put(
				"layoutUpdatedMessage", successTag.doTagAsString(pageContext));
		}
		catch (JspException jspException) {
			ReflectionUtil.throwException(jspException);
		}

		Writer writer = httpServletResponse.getWriter();

		writer.write(StringUtil.replace(_TMPL_CONTENT, "${", "}", values));

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(layout);

		if (layout.isEmbeddedPersonalApplication() ||
			layout.isTypeControlPanel() ||
			_isMasterLayout(layout, layoutPageTemplateEntry) ||
			!(themeDisplay.isShowLayoutTemplatesIcon() ||
			  themeDisplay.isShowPageSettingsIcon())) {

			return false;
		}

		String mode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if ((layout.isTypeAssetDisplay() || layout.isTypeContent()) &&
			Objects.equals(mode, Constants.EDIT)) {

			return false;
		}

		if (layout.isSystem() && layout.isTypeContent()) {
			return _layoutPermission.contains(
				themeDisplay.getPermissionChecker(),
				_layoutLocalService.getLayout(layout.getClassPK()),
				ActionKeys.UPDATE);
		}

		return super.isShow(httpServletRequest);
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry(Layout layout) {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());
		}

		return layoutPageTemplateEntry;
	}

	private boolean _isMasterLayout(
		Layout layout, LayoutPageTemplateEntry layoutPageTemplateEntry) {

		if ((layout.getMasterLayoutPlid() > 0) ||
			(layoutPageTemplateEntry == null) ||
			(layoutPageTemplateEntry.getType() !=
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return false;
		}

		return true;
	}

	private static final String _TMPL_CONTENT = StringUtil.read(
		ManageLayoutProductNavigationControlMenuEntry.class,
		"/META-INF/resources/control/menu" +
			"/edit_layout_control_menu_entry_icon.tmpl");

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

}