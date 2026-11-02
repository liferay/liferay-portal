/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateVersionException;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMTemplateVersionLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.util.comparator.TemplateVersionVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMTemplateVersion",
	service = AopService.class
)
public class DDMTemplateVersionLocalServiceImpl
	extends DDMTemplateVersionLocalServiceBaseImpl {

	@Override
	public void deleteTemplateVersions(long templateId) {
		ddmTemplateVersionPersistence.removeByTemplateId(templateId);
	}

	@Override
	public DDMTemplateVersion getLatestTemplateVersion(long templateId)
		throws PortalException {

		List<DDMTemplateVersion> templateVersions =
			ddmTemplateVersionPersistence.findByTemplateId(templateId);

		if (templateVersions.isEmpty()) {
			throw new NoSuchTemplateVersionException(
				"No template versions found for template ID " + templateId);
		}

		templateVersions = ListUtil.copy(templateVersions);

		Collections.sort(
			templateVersions, new TemplateVersionVersionComparator());

		return templateVersions.get(0);
	}

	@Override
	public DDMTemplateVersion getTemplateVersion(
			long templateId, String version)
		throws PortalException {

		return ddmTemplateVersionPersistence.findByT_V(templateId, version);
	}

	@Override
	public List<DDMTemplateVersion> getTemplateVersions(long templateId) {
		return ddmTemplateVersionPersistence.findByTemplateId(templateId);
	}

}