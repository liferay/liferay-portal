/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.impl;

import com.liferay.commerce.notification.constants.CommerceNotificationActionKeys;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.base.CommerceNotificationTemplateServiceBaseImpl;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceNotificationTemplate"
	},
	service = AopService.class
)
@Deprecated
public class CommerceNotificationTemplateServiceImpl
	extends CommerceNotificationTemplateServiceBaseImpl {

	@Override
	public CommerceNotificationTemplate addCommerceNotificationTemplate(
			long groupId, String name, String description, String from,
			Map<Locale, String> fromNameMap, String to, String cc, String bcc,
			String type, boolean enabled, Map<Locale, String> subjectMap,
			Map<Locale, String> bodyMap, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceNotificationActionKeys.ADD_COMMERCE_NOTIFICATION_TEMPLATE);

		return commerceNotificationTemplateLocalService.
			addCommerceNotificationTemplate(
				getUserId(), groupId, name, description, from, fromNameMap, to,
				cc, bcc, type, enabled, subjectMap, bodyMap, serviceContext);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceNotificationTemplate addCommerceNotificationTemplate(
			String name, String description, String from,
			Map<Locale, String> fromNameMap, String to, String cc, String bcc,
			String type, boolean enabled, Map<Locale, String> subjectMap,
			Map<Locale, String> bodyMap, ServiceContext serviceContext)
		throws PortalException {

		return commerceNotificationTemplateService.
			addCommerceNotificationTemplate(
				serviceContext.getScopeGroupId(), name, description, from,
				fromNameMap, to, cc, bcc, type, enabled, subjectMap, bodyMap,
				serviceContext);
	}

	@Override
	public void deleteCommerceNotificationTemplate(
			long commerceNotificationTemplateId)
		throws PortalException {

		_commerceNotificationTemplateResourcePermission.check(
			getPermissionChecker(), commerceNotificationTemplateId,
			ActionKeys.DELETE);

		commerceNotificationTemplateLocalService.
			deleteCommerceNotificationTemplate(commerceNotificationTemplateId);
	}

	@Override
	public CommerceNotificationTemplate getCommerceNotificationTemplate(
			long commerceNotificationTemplateId)
		throws PortalException {

		_commerceNotificationTemplateResourcePermission.check(
			getPermissionChecker(), commerceNotificationTemplateId,
			ActionKeys.VIEW);

		return commerceNotificationTemplatePersistence.findByPrimaryKey(
			commerceNotificationTemplateId);
	}

	@Override
	public List<CommerceNotificationTemplate> getCommerceNotificationTemplates(
			long groupId, boolean enabled, int start, int end,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws PortalException {

		return commerceNotificationTemplatePersistence.filterFindByG_E(
			groupId, enabled, start, end, orderByComparator);
	}

	@Override
	public List<CommerceNotificationTemplate> getCommerceNotificationTemplates(
			long groupId, int start, int end,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws PortalException {

		return commerceNotificationTemplatePersistence.filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceNotificationTemplatesCount(long groupId)
		throws PortalException {

		return commerceNotificationTemplatePersistence.filterCountByGroupId(
			groupId);
	}

	@Override
	public int getCommerceNotificationTemplatesCount(
			long groupId, boolean enabled)
		throws PortalException {

		return commerceNotificationTemplatePersistence.filterCountByG_E(
			groupId, enabled);
	}

	@Override
	public CommerceNotificationTemplate updateCommerceNotificationTemplate(
			long commerceNotificationTemplateId, String name,
			String description, String from, Map<Locale, String> fromNameMap,
			String to, String cc, String bcc, String type, boolean enabled,
			Map<Locale, String> subjectMap, Map<Locale, String> bodyMap,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceNotificationTemplateResourcePermission.check(
			getPermissionChecker(), commerceNotificationTemplateId,
			ActionKeys.UPDATE);

		return commerceNotificationTemplateLocalService.
			updateCommerceNotificationTemplate(
				commerceNotificationTemplateId, name, description, from,
				fromNameMap, to, cc, bcc, type, enabled, subjectMap, bodyMap,
				serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.notification.model.CommerceNotificationTemplate)"
	)
	private ModelResourcePermission<CommerceNotificationTemplate>
		_commerceNotificationTemplateResourcePermission;

	@Reference(
		target = "(resource.name=" + CPConstants.RESOURCE_NAME_CHANNEL + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}