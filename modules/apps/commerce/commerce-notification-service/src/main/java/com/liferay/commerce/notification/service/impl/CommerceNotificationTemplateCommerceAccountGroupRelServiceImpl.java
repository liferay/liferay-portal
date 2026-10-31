/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.impl;

import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.model.CommerceNotificationTemplateCommerceAccountGroupRel;
import com.liferay.commerce.notification.service.base.CommerceNotificationTemplateCommerceAccountGroupRelServiceBaseImpl;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplatePersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceNotificationTemplateCommerceAccountGroupRel"
	},
	service = AopService.class
)
@Deprecated
public class CommerceNotificationTemplateCommerceAccountGroupRelServiceImpl
	extends CommerceNotificationTemplateCommerceAccountGroupRelServiceBaseImpl {

	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateId,
				long commerceAccountGroupId, ServiceContext serviceContext)
		throws PortalException {

		_commerceNotificationTemplateResourcePermission.check(
			getPermissionChecker(), commerceNotificationTemplateId,
			ActionKeys.UPDATE);

		return commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateId, commerceAccountGroupId,
				serviceContext);
	}

	@Override
	public void deleteCommerceNotificationTemplateCommerceAccountGroupRel(
			long commerceNotificationTemplateCommerceAccountGroupRelId)
		throws PortalException {

		CommerceNotificationTemplateCommerceAccountGroupRel
			commerceNotificationTemplateCommerceAccountGroupRel =
				commerceNotificationTemplateCommerceAccountGroupRelPersistence.
					findByPrimaryKey(
						commerceNotificationTemplateCommerceAccountGroupRelId);

		_commerceNotificationTemplateResourcePermission.check(
			getPermissionChecker(),
			commerceNotificationTemplateCommerceAccountGroupRel.
				getCommerceNotificationTemplateId(),
			ActionKeys.UPDATE);

		commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
			fetchCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateId,
				long commerceAccountGroupId)
		throws PortalException {

		_commerceNotificationTemplateResourcePermission.check(
			getPermissionChecker(), commerceNotificationTemplateId,
			ActionKeys.VIEW);

		return commerceNotificationTemplateCommerceAccountGroupRelPersistence.
			fetchByC_C(commerceNotificationTemplateId, commerceAccountGroupId);
	}

	@Override
	public List<CommerceNotificationTemplateCommerceAccountGroupRel>
			getCommerceNotificationTemplateCommerceAccountGroupRels(
				long commerceNotificationTemplateId, int start, int end,
				OrderByComparator
					<CommerceNotificationTemplateCommerceAccountGroupRel>
						orderByComparator)
		throws PortalException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			_commerceNotificationTemplatePersistence.fetchByPrimaryKey(
				commerceNotificationTemplateId);

		if (commerceNotificationTemplate != null) {
			_commerceNotificationTemplateResourcePermission.check(
				getPermissionChecker(), commerceNotificationTemplateId,
				ActionKeys.UPDATE);
		}

		return commerceNotificationTemplateCommerceAccountGroupRelPersistence.
			findByCommerceNotificationTemplateId(
				commerceNotificationTemplateId, start, end, orderByComparator);
	}

	@Reference
	private CommerceNotificationTemplatePersistence
		_commerceNotificationTemplatePersistence;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.notification.model.CommerceNotificationTemplate)"
	)
	private ModelResourcePermission<CommerceNotificationTemplate>
		_commerceNotificationTemplateResourcePermission;

}