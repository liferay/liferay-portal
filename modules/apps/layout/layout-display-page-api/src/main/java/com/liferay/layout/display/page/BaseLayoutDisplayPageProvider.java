/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.display.page;

import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Mikel Lorza
 */
public abstract class BaseLayoutDisplayPageProvider<T>
	implements LayoutDisplayPageProvider<T> {

	@Override
	public LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(
			InfoItemReference infoItemReference) {

		return getLayoutDisplayPageObjectProvider(
			_getGroupId(), infoItemReference);
	}

	@Override
	public LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		return doGetLayoutDisplayPageObjectProvider(
			_getGroupId(groupId, infoItemReference.getInfoItemIdentifier()),
			infoItemReference);
	}

	@Override
	public String getURLSeparator() {
		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolverByDefaultURLSeparator(
					getDefaultURLSeparator());

		if (friendlyURLResolver == null) {
			return getDefaultURLSeparator();
		}

		return friendlyURLResolver.getURLSeparator();
	}

	protected abstract LayoutDisplayPageObjectProvider<T>
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference);

	private long _getCompanyId() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getCompanyId();
		}

		Long companyId = CompanyThreadLocal.getCompanyId();

		if (companyId != null) {
			return companyId;
		}

		throw new IllegalStateException(
			"Neither company thread local nor service context thread local " +
				"are initialized");
	}

	private long _getGroupId() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getScopeGroupId();
		}

		Long groupId = GroupThreadLocal.getGroupId();

		if (groupId != null) {
			return groupId;
		}

		throw new IllegalStateException(
			"Neither service context thread local nor group thread local are " +
				"initialized");
	}

	private long _getGroupId(
		long groupId, InfoItemIdentifier infoItemIdentifier) {

		try {
			if (!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {
				return groupId;
			}

			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			if (Validator.isNull(
					ercInfoItemIdentifier.getScopeExternalReferenceCode())) {

				return groupId;
			}

			Group group = GroupLocalServiceUtil.getGroupByExternalReferenceCode(
				ercInfoItemIdentifier.getScopeExternalReferenceCode(),
				_getCompanyId());

			return group.getGroupId();
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

}