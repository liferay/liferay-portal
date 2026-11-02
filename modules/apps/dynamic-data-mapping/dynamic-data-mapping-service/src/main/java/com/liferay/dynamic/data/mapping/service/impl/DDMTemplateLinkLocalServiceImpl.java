/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.service.base.DDMTemplateLinkLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMTemplateLink",
	service = AopService.class
)
public class DDMTemplateLinkLocalServiceImpl
	extends DDMTemplateLinkLocalServiceBaseImpl {

	@Override
	public DDMTemplateLink addTemplateLink(
		long classNameId, long classPK, long templateId) {

		long templateLinkId = counterLocalService.increment();

		DDMTemplateLink templateLink = ddmTemplateLinkPersistence.create(
			templateLinkId);

		templateLink.setClassNameId(classNameId);
		templateLink.setClassPK(classPK);
		templateLink.setTemplateId(templateId);

		return ddmTemplateLinkPersistence.update(templateLink);
	}

	@Override
	public DDMTemplateLink deleteTemplateLink(DDMTemplateLink templateLink) {
		return ddmTemplateLinkPersistence.remove(templateLink);
	}

	@Override
	public DDMTemplateLink deleteTemplateLink(long templateLinkId)
		throws PortalException {

		DDMTemplateLink templateLink =
			ddmTemplateLinkPersistence.findByPrimaryKey(templateLinkId);

		return deleteDDMTemplateLink(templateLink);
	}

	@Override
	public DDMTemplateLink deleteTemplateLink(long classNameId, long classPK) {
		DDMTemplateLink templateLink = ddmTemplateLinkPersistence.fetchByC_C(
			classNameId, classPK);

		if (templateLink != null) {
			deleteDDMTemplateLink(templateLink);
		}

		return templateLink;
	}

	@Override
	public void deleteTemplateLinks(long templateId) {
		ddmTemplateLinkPersistence.removeByTemplateId(templateId);
	}

	@Override
	public DDMTemplateLink updateTemplateLink(
		long classNameId, long classPK, long templateId) {

		DDMTemplateLink templateLink = ddmTemplateLinkPersistence.fetchByC_C(
			classNameId, classPK);

		if (templateLink == null) {
			return addTemplateLink(classNameId, classPK, templateId);
		}

		templateLink.setTemplateId(templateId);

		return ddmTemplateLinkPersistence.update(templateLink);
	}

}