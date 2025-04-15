/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.organization.web.internal.display.context;

import com.liferay.commerce.organization.web.internal.configuration.CommerceOrganizationPortletInstanceConfiguration;
import com.liferay.commerce.organization.web.internal.display.context.helper.CommerceOrganizationRequestHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrganizationDisplayContext {

	public CommerceOrganizationDisplayContext(
			ConfigurationProvider configurationProvider,
			HttpServletRequest httpServletRequest,
			OrganizationLocalService organizationLocalService,
			OrganizationService organizationService, Portal portal,
			UserLocalService userLocalService)
		throws PortalException {

		_organizationLocalService = organizationLocalService;
		_organizationService = organizationService;
		_portal = portal;
		_userLocalService = userLocalService;

		_commerceOrganizationRequestHelper =
			new CommerceOrganizationRequestHelper(httpServletRequest);

		_commerceOrganizationPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CommerceOrganizationPortletInstanceConfiguration.class,
				_commerceOrganizationRequestHelper.getThemeDisplay());
	}

	public String getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_commerceOrganizationRequestHelper.getRequest());

		_keywords = ParamUtil.getString(httpServletRequest, "q", null);

		if (_keywords == null) {
			return StringPool.BLANK;
		}

		return _keywords;
	}

	public String getLogo(Organization organization) {
		return organization.getLogoURL();
	}

	public Organization getOrganization() throws PortalException {
		long organizationId = ParamUtil.getLong(
			_commerceOrganizationRequestHelper.getRequest(), "organizationId");

		if (organizationId > 0) {
			return _organizationService.getOrganization(organizationId);
		}

		return null;
	}

	public long getOrganizationId() throws PortalException {
		Organization organization = getOrganization();

		if (organization != null) {
			return organization.getOrganizationId();
		}

		return OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID;
	}

	public PortletURL getPortletURL() throws PortalException {
		LiferayPortletResponse liferayPortletResponse =
			_commerceOrganizationRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_commerceOrganizationRequestHelper.getRequest());

		String backURL = ParamUtil.getString(
			httpServletRequest,
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL");

		if (Validator.isNotNull(backURL)) {
			portletURL.setParameter(
				PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
				backURL);
		}

		String redirect = ParamUtil.getString(
			_commerceOrganizationRequestHelper.getRequest(), "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String delta = ParamUtil.getString(
			_commerceOrganizationRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			_commerceOrganizationRequestHelper.getRequest(), "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		Organization organization = getOrganization();

		if (organization != null) {
			portletURL.setParameter(
				"organizationId",
				String.valueOf(organization.getOrganizationId()));
		}

		return portletURL;
	}

	public Organization getRootOrganization() throws PortalException {
		String rootOrganizationIdString = getRootOrganizationId();

		if (rootOrganizationIdString.isEmpty()) {
			return null;
		}

		return _organizationService.fetchOrganization(
			GetterUtil.getLong(rootOrganizationIdString));
	}

	public String getRootOrganizationId() throws PortalException {
		Organization organization = null;

		String rootOrganizationExternalReferenceCode =
			_commerceOrganizationPortletInstanceConfiguration.
				rootOrganizationExternalReferenceCode();

		if (Validator.isNotNull(rootOrganizationExternalReferenceCode)) {
			organization =
				_organizationLocalService.
					fetchOrganizationByExternalReferenceCode(
						rootOrganizationExternalReferenceCode,
						_commerceOrganizationRequestHelper.getCompanyId());
		}

		if ((organization != null) &&
			OrganizationPermissionUtil.contains(
				_commerceOrganizationRequestHelper.getPermissionChecker(),
				organization, ActionKeys.VIEW)) {

			return String.valueOf(organization.getOrganizationId());
		}

		return StringPool.BLANK;
	}

	public User getSelectedUser() throws PortalException {
		return _userLocalService.getUser(getSelectedUserId());
	}

	public long getSelectedUserId() {
		long userId = ParamUtil.getLong(
			_commerceOrganizationRequestHelper.getRequest(), "userId");

		if (userId > 0) {
			return userId;
		}

		return _commerceOrganizationRequestHelper.getUserId();
	}

	public String getSelectLogoURL() {
		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(
				_commerceOrganizationRequestHelper.getLiferayPortletResponse()),
			PortletKeys.IMAGE_UPLOADER
		).setMVCRenderCommandName(
			"/image_uploader/upload_image"
		).setParameter(
			"aspectRatio", 1
		).setParameter(
			"currentLogoURL", "[$CURRENT_LOGO_URL$]"
		).setParameter(
			"preserveRatio", true
		).setParameter(
			"randomNamespace",
			_portal.generateRandomKey(
				_commerceOrganizationRequestHelper.getRequest(),
				"commerce-organization-web")
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public boolean hasAddOrganizationPermissions() throws PortalException {
		if (getOrganizationId() ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			return PortalPermissionUtil.contains(
				_commerceOrganizationRequestHelper.getPermissionChecker(),
				ActionKeys.ADD_ORGANIZATION);
		}

		return OrganizationPermissionUtil.contains(
			_commerceOrganizationRequestHelper.getPermissionChecker(),
			getOrganizationId(), ActionKeys.ADD_ORGANIZATION);
	}

	public boolean isAdminPortlet() {
		return _adminPortlet;
	}

	public void setAdminPortlet(boolean adminPortlet) {
		_adminPortlet = adminPortlet;
	}

	private boolean _adminPortlet;
	private final CommerceOrganizationPortletInstanceConfiguration
		_commerceOrganizationPortletInstanceConfiguration;
	private final CommerceOrganizationRequestHelper
		_commerceOrganizationRequestHelper;
	private String _keywords;
	private final OrganizationLocalService _organizationLocalService;
	private final OrganizationService _organizationService;
	private final Portal _portal;
	private final UserLocalService _userLocalService;

}