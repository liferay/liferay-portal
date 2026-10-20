/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemPermissionProvider.class)
public class CPDefinitionInfoItemPermissionProvider
	implements InfoItemPermissionProvider<CPDefinition> {

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, CPDefinition cpDefinition,
			String actionId)
		throws InfoItemPermissionException {

		CommerceContext commerceContext = _getCommerceContext();

		if (commerceContext == null) {
			return false;
		}

		try {
			return _commerceProductViewPermission.contains(
				permissionChecker,
				CommerceUtil.getCommerceAccountId(commerceContext),
				commerceContext.getCommerceChannelGroupId(),
				cpDefinition.getCPDefinitionId());
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(
				cpDefinition.getCPDefinitionId(), portalException);
		}
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		CommerceContext commerceContext = _getCommerceContext();

		if (commerceContext == null) {
			return false;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return false;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		try {
			return _commerceProductViewPermission.contains(
				permissionChecker,
				CommerceUtil.getCommerceAccountId(commerceContext),
				commerceContext.getCommerceChannelGroupId(),
				classPKInfoItemIdentifier.getClassPK());
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(
				classPKInfoItemIdentifier.getClassPK(), portalException);
		}
	}

	private CommerceContext _getCommerceContext() {
		CommerceContext commerceContext = CommerceContextThreadLocal.get();

		if (commerceContext != null) {
			return commerceContext;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || (serviceContext.getRequest() == null)) {
			return null;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		commerceContext = (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext != null) {
			return commerceContext;
		}

		commerceContext = _commerceContextFactory.create(httpServletRequest);

		try {
			if (commerceContext.getCommerceChannelId() == 0) {
				return null;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}

		httpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);

		return commerceContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionInfoItemPermissionProvider.class);

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

}