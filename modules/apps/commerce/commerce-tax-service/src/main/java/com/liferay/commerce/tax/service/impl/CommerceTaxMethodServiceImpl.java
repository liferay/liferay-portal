/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.impl;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.base.CommerceTaxMethodServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceTaxMethod"
	},
	service = AopService.class
)
public class CommerceTaxMethodServiceImpl
	extends CommerceTaxMethodServiceBaseImpl {

	@Override
	public CommerceTaxMethod addCommerceTaxMethod(
			long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String engineKey,
			boolean percentage, boolean active)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxMethodLocalService.addCommerceTaxMethod(
			getUserId(), groupId, nameMap, descriptionMap, engineKey,
			percentage, active);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceTaxMethod addCommerceTaxMethod(
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String engineKey, boolean percentage, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceTaxMethodService.addCommerceTaxMethod(
			serviceContext.getScopeGroupId(), nameMap, descriptionMap,
			engineKey, percentage, active);
	}

	@Override
	public CommerceTaxMethod createCommerceTaxMethod(
			long groupId, long commerceTaxMethodId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxMethodLocalService.createCommerceTaxMethod(
			commerceTaxMethodId);
	}

	@Override
	public void deleteCommerceTaxMethod(long commerceTaxMethodId)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.findByPrimaryKey(commerceTaxMethodId);

		_checkCommerceChannel(commerceTaxMethod.getGroupId());

		commerceTaxMethodLocalService.deleteCommerceTaxMethod(
			commerceTaxMethod);
	}

	@Override
	public CommerceTaxMethod fetchCommerceTaxMethod(
			long groupId, String engineKey)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxMethodPersistence.fetchByG_E(groupId, engineKey);
	}

	@Override
	public CommerceTaxMethod getCommerceTaxMethod(long commerceTaxMethodId)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.findByPrimaryKey(commerceTaxMethodId);

		_checkCommerceChannel(commerceTaxMethod.getGroupId());

		return commerceTaxMethodPersistence.findByPrimaryKey(
			commerceTaxMethodId);
	}

	@Override
	public List<CommerceTaxMethod> getCommerceTaxMethods(long groupId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxMethodPersistence.findByGroupId(groupId);
	}

	@Override
	public List<CommerceTaxMethod> getCommerceTaxMethods(
			long groupId, boolean active)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxMethodLocalService.getCommerceTaxMethods(
			groupId, active);
	}

	@Override
	public CommerceTaxMethod setActive(long commerceTaxMethodId, boolean active)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.fetchByPrimaryKey(commerceTaxMethodId);

		if (commerceTaxMethod != null) {
			_checkCommerceChannel(commerceTaxMethod.getGroupId());
		}

		return commerceTaxMethodLocalService.setActive(
			commerceTaxMethodId, active);
	}

	@Override
	public CommerceTaxMethod updateCommerceTaxMethod(
			CommerceTaxMethod commerceTaxMethod)
		throws PortalException {

		_checkCommerceChannel(commerceTaxMethod.getGroupId());

		return commerceTaxMethodLocalService.updateCommerceTaxMethod(
			commerceTaxMethod);
	}

	@Override
	public CommerceTaxMethod updateCommerceTaxMethod(
			long commerceTaxMethodId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean percentage,
			boolean active)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.findByPrimaryKey(commerceTaxMethodId);

		_checkCommerceChannel(commerceTaxMethod.getGroupId());

		return commerceTaxMethodLocalService.updateCommerceTaxMethod(
			commerceTaxMethod.getCommerceTaxMethodId(), nameMap, descriptionMap,
			percentage, active);
	}

	private void _checkCommerceChannel(long groupId) throws PortalException {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(groupId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannel, ActionKeys.UPDATE);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

}