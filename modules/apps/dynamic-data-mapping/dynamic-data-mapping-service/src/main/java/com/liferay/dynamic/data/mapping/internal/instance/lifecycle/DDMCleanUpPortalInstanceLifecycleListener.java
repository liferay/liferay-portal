/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.instance.lifecycle;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class DDMCleanUpPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		for (DDMTemplate ddmTemplate :
				_ddmTemplateLocalService.getTemplatesByGroupId(
					company.getGroupId())) {

			_deleteDDMTemplate(ddmTemplate);
		}

		Queue<DDMStructure> queue = new LinkedList<>(
			_ddmStructureLocalService.getStructures(company.getGroupId()));

		Deque<DDMStructure> deque = new LinkedList<>();

		DDMStructure currentDDMStructure = null;

		while ((currentDDMStructure = queue.poll()) != null) {
			deque.push(currentDDMStructure);

			queue.addAll(
				_ddmStructureLocalService.getChildrenStructures(
					currentDDMStructure.getStructureId()));
		}

		for (DDMStructure ddmStructure : deque) {
			long structureId = ddmStructure.getStructureId();

			_ddmStructureLinkLocalService.deleteStructureStructureLinks(
				structureId);

			for (DDMTemplate ddmTemplate :
					_ddmTemplateLocalService.getTemplates(structureId)) {

				_deleteDDMTemplate(ddmTemplate);
			}

			_ddmStorageEngineManager.deleteByClass(
				ddmStructure.getStructureId());

			_ddmStructureLocalService.deleteStructure(ddmStructure);
		}
	}

	private void _deleteDDMTemplate(DDMTemplate ddmTemplate) throws Exception {
		_ddmTemplateVersionLocalService.deleteTemplateVersions(
			ddmTemplate.getTemplateId());

		_ddmTemplateLinkLocalService.deleteTemplateLinks(
			ddmTemplate.getTemplateId());

		_ddmTemplateLocalService.deleteTemplate(ddmTemplate);
	}

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMTemplateVersionLocalService _ddmTemplateVersionLocalService;

}