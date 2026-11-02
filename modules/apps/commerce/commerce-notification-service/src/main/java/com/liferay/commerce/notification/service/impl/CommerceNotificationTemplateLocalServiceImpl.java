/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.impl;

import com.liferay.commerce.notification.exception.CommerceNotificationTemplateFromException;
import com.liferay.commerce.notification.exception.CommerceNotificationTemplateNameException;
import com.liferay.commerce.notification.exception.CommerceNotificationTemplateTypeException;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateCommerceAccountGroupRelLocalService;
import com.liferay.commerce.notification.service.base.CommerceNotificationTemplateLocalServiceBaseImpl;
import com.liferay.commerce.notification.type.CommerceNotificationType;
import com.liferay.commerce.notification.type.CommerceNotificationTypeRegistry;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Validator;

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
	property = "model.class.name=com.liferay.commerce.notification.model.CommerceNotificationTemplate",
	service = AopService.class
)
@Deprecated
public class CommerceNotificationTemplateLocalServiceImpl
	extends CommerceNotificationTemplateLocalServiceBaseImpl {

	@Override
	public CommerceNotificationTemplate addCommerceNotificationTemplate(
			long userId, long groupId, String name, String description,
			String from, Map<Locale, String> fromNameMap, String to, String cc,
			String bcc, String type, boolean enabled,
			Map<Locale, String> subjectMap, Map<Locale, String> bodyMap,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce notification template

		User user = _userLocalService.getUser(userId);

		_validate(name, from, type);

		long commerceNotificationTemplateId = counterLocalService.increment();

		CommerceNotificationTemplate commerceNotificationTemplate =
			commerceNotificationTemplatePersistence.create(
				commerceNotificationTemplateId);

		commerceNotificationTemplate.setGroupId(groupId);
		commerceNotificationTemplate.setCompanyId(user.getCompanyId());
		commerceNotificationTemplate.setUserId(user.getUserId());
		commerceNotificationTemplate.setUserName(user.getFullName());
		commerceNotificationTemplate.setName(name);
		commerceNotificationTemplate.setDescription(description);
		commerceNotificationTemplate.setFrom(from);
		commerceNotificationTemplate.setFromNameMap(fromNameMap);
		commerceNotificationTemplate.setTo(to);
		commerceNotificationTemplate.setCc(cc);
		commerceNotificationTemplate.setBcc(bcc);
		commerceNotificationTemplate.setType(type);
		commerceNotificationTemplate.setEnabled(enabled);
		commerceNotificationTemplate.setSubjectMap(subjectMap);
		commerceNotificationTemplate.setBodyMap(bodyMap);
		commerceNotificationTemplate.setExpandoBridgeAttributes(serviceContext);

		commerceNotificationTemplate =
			commerceNotificationTemplatePersistence.update(
				commerceNotificationTemplate);

		// Resources

		_resourceLocalService.addModelResources(
			commerceNotificationTemplate, serviceContext);

		return commerceNotificationTemplate;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceNotificationTemplate deleteCommerceNotificationTemplate(
			CommerceNotificationTemplate commerceNotificationTemplate)
		throws PortalException {

		// Commerce notification queues

		_commerceNotificationQueueEntryLocalService.
			updateCommerceNotificationQueueEntriesTemplateIds(
				commerceNotificationTemplate.
					getCommerceNotificationTemplateId());

		// Commerce notification template account groups rels

		_commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deleteCNTemplateCommerceAccountGroupRelsByCommerceNotificationTemplateId(
				commerceNotificationTemplate.
					getCommerceNotificationTemplateId());

		// Commerce notification template

		commerceNotificationTemplatePersistence.remove(
			commerceNotificationTemplate);

		// Resources

		_resourceLocalService.deleteResource(
			commerceNotificationTemplate.getCompanyId(),
			CommerceNotificationTemplate.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			commerceNotificationTemplate.getCommerceNotificationTemplateId());

		// Expando

		_expandoRowLocalService.deleteRows(
			commerceNotificationTemplate.getCommerceNotificationTemplateId());

		return commerceNotificationTemplate;
	}

	@Override
	public CommerceNotificationTemplate deleteCommerceNotificationTemplate(
			long commerceNotificationTemplateId)
		throws PortalException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			commerceNotificationTemplatePersistence.findByPrimaryKey(
				commerceNotificationTemplateId);

		return commerceNotificationTemplateLocalService.
			deleteCommerceNotificationTemplate(commerceNotificationTemplate);
	}

	@Override
	public void deleteCommerceNotificationTemplates(long groupId)
		throws PortalException {

		List<CommerceNotificationTemplate> commerceNotificationTemplates =
			commerceNotificationTemplatePersistence.findByGroupId(groupId);

		for (CommerceNotificationTemplate commerceNotificationTemplate :
				commerceNotificationTemplates) {

			commerceNotificationTemplateLocalService.
				deleteCommerceNotificationTemplate(
					commerceNotificationTemplate);
		}
	}

	@Override
	public List<CommerceNotificationTemplate> getCommerceNotificationTemplates(
		long groupId, String type, boolean enabled) {

		return commerceNotificationTemplatePersistence.findByG_T_E(
			groupId, type, enabled);
	}

	@Override
	public CommerceNotificationTemplate updateCommerceNotificationTemplate(
			long commerceNotificationTemplateId, String name,
			String description, String from, Map<Locale, String> fromNameMap,
			String to, String cc, String bcc, String type, boolean enabled,
			Map<Locale, String> subjectMap, Map<Locale, String> bodyMap,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			commerceNotificationTemplatePersistence.findByPrimaryKey(
				commerceNotificationTemplateId);

		_validate(name, from, type);

		commerceNotificationTemplate.setName(name);
		commerceNotificationTemplate.setDescription(description);
		commerceNotificationTemplate.setFrom(from);
		commerceNotificationTemplate.setFromNameMap(fromNameMap);
		commerceNotificationTemplate.setTo(to);
		commerceNotificationTemplate.setCc(cc);
		commerceNotificationTemplate.setBcc(bcc);
		commerceNotificationTemplate.setType(type);
		commerceNotificationTemplate.setEnabled(enabled);
		commerceNotificationTemplate.setSubjectMap(subjectMap);
		commerceNotificationTemplate.setBodyMap(bodyMap);
		commerceNotificationTemplate.setExpandoBridgeAttributes(serviceContext);

		return commerceNotificationTemplatePersistence.update(
			commerceNotificationTemplate);
	}

	private void _validate(String name, String from, String type)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new CommerceNotificationTemplateNameException();
		}

		if (Validator.isNull(from)) {
			throw new CommerceNotificationTemplateFromException();
		}

		CommerceNotificationType commerceNotificationType =
			_commerceNotificationTypeRegistry.getCommerceNotificationType(type);

		if (commerceNotificationType == null) {
			throw new CommerceNotificationTemplateTypeException();
		}
	}

	@Reference
	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

	@Reference
	private CommerceNotificationTemplateCommerceAccountGroupRelLocalService
		_commerceNotificationTemplateCommerceAccountGroupRelLocalService;

	@Reference
	private CommerceNotificationTypeRegistry _commerceNotificationTypeRegistry;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}