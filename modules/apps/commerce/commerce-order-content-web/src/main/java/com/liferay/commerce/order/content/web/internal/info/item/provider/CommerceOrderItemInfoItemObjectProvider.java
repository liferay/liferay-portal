/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info.item.provider;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"item.class.name=com.liferay.commerce.model.CommerceOrderItem",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class CommerceOrderItemInfoItemObjectProvider
	implements InfoItemObjectProvider<CommerceOrderItem> {

	public CommerceOrderItem getInfoItem(InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return getInfoItem(
			serviceContext.getScopeGroupId(), infoItemIdentifier);
	}

	@Override
	public CommerceOrderItem getInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemLocalService.fetchCommerceOrderItem(
					classPKInfoItemIdentifier.getClassPK());

			if (commerceOrderItem == null) {
				throw new NoSuchInfoItemException(
					"Unable to get commerce order item " +
						classPKInfoItemIdentifier.getClassPK());
			}

			return commerceOrderItem;
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		Group group = null;

		if (Validator.isNull(
				ercInfoItemIdentifier.getScopeExternalReferenceCode())) {

			group = _groupLocalService.fetchGroup(groupId);

			if (group == null) {
				throw new NoSuchInfoItemException(
					"No group found with group ID " + groupId);
			}
		}
		else {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				ercInfoItemIdentifier.getScopeExternalReferenceCode(),
				serviceContext.getCompanyId());

			if (group == null) {
				throw new NoSuchInfoItemException(
					StringBundler.concat(
						"No group found with company ID ",
						serviceContext.getCompanyId(),
						" and external reference code ",
						ercInfoItemIdentifier.getScopeExternalReferenceCode()));
			}
		}

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.
				fetchCommerceOrderItemByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(),
					group.getCompanyId());

		if (commerceOrderItem == null) {
			throw new NoSuchInfoItemException(
				StringBundler.concat(
					"No commerce order item found with company ID ",
					group.getCompanyId(), " and external reference code ",
					ercInfoItemIdentifier.getExternalReferenceCode()));
		}

		return commerceOrderItem;
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}