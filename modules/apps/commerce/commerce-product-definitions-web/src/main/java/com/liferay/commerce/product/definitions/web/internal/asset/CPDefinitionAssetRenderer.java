/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.asset;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.util.AssetHelper;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionAssetRenderer
	extends BaseJSPAssetRenderer<CPDefinition> {

	public CPDefinitionAssetRenderer(
		CPDefinition cpDefinition, CPDefinitionHelper cpDefinitionHelper,
		Language language,
		ModelResourcePermission<CommerceCatalog> modelResourcePermission,
		Portal portal) {

		_cpDefinition = cpDefinition;
		_cpDefinitionHelper = cpDefinitionHelper;
		_language = language;
		_modelResourcePermission = modelResourcePermission;
		_portal = portal;
	}

	@Override
	public CPDefinition getAssetObject() {
		return _cpDefinition;
	}

	@Override
	public String getClassName() {
		return CPDefinition.class.getName();
	}

	@Override
	public long getClassPK() {
		return _cpDefinition.getCPDefinitionId();
	}

	@Override
	public long getGroupId() {
		return _cpDefinition.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public int getStatus() {
		return _cpDefinition.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		int abstractLength = AssetHelper.ASSET_ENTRY_ABSTRACT_LENGTH;

		if (portletRequest != null) {
			abstractLength = GetterUtil.getInteger(
				portletRequest.getAttribute(
					WebKeys.ASSET_ENTRY_ABSTRACT_LENGTH),
				AssetHelper.ASSET_ENTRY_ABSTRACT_LENGTH);
		}

		String summary = _cpDefinition.getDescription();

		if (portletRequest != null) {
			summary = _cpDefinition.getDescription(
				_language.getLanguageId(_portal.getLocale(portletRequest)));
		}

		if (Validator.isNull(summary)) {
			summary = StringUtil.shorten(
				_cpDefinition.getDescriptionMapAsXML(), abstractLength);
		}

		return HtmlUtil.stripHtml(summary);
	}

	@Override
	public String getTitle(Locale locale) {
		return _cpDefinition.getName(_language.getLanguageId(locale));
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest,
				GroupLocalServiceUtil.fetchGroup(_cpDefinition.getGroupId()),
				CPPortletKeys.CP_DEFINITIONS, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"cpDefinitionId", _cpDefinition.getCPDefinitionId()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<CPDefinition> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"cpDefinitionId", _cpDefinition.getCPDefinitionId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		try {
			if (!_cpDefinition.isApproved() || !_cpDefinition.isPublished()) {
				return null;
			}

			return _cpDefinitionHelper.getFriendlyURL(
				_cpDefinition.getCPDefinitionId(), themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return noSuchEntryRedirect;
		}
	}

	@Override
	public long getUserId() {
		return _cpDefinition.getUserId();
	}

	@Override
	public String getUserName() {
		return _cpDefinition.getUserName();
	}

	@Override
	public String getUuid() {
		return _cpDefinition.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, _cpDefinition.getCommerceCatalog(),
			ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, _cpDefinition.getCommerceCatalog(),
			ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(CPWebKeys.CP_DEFINITION, _cpDefinition);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionAssetRenderer.class);

	private final CPDefinition _cpDefinition;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final Language _language;
	private final ModelResourcePermission<CommerceCatalog>
		_modelResourcePermission;
	private final Portal _portal;

}