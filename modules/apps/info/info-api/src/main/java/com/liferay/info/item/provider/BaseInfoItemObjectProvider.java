/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseInfoItemObjectProvider<T>
	implements InfoItemObjectProvider<T> {

	@Override
	public T getInfoItem(InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		return getInfoItem(_getGroupId(), infoItemIdentifier);
	}

	@Override
	public T getInfoItem(long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		return doGetInfoItem(
			_getGroupId(groupId, infoItemIdentifier), infoItemIdentifier);
	}

	protected abstract T doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException;

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
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

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
			throw new NoSuchInfoItemException(portalException);
		}
	}

}