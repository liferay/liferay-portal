/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matyas Wollner
 */
@Component(service = StagedModelDataHandler.class)
public class ListTypeStagedModelDataHandler
	extends BaseStagedModelDataHandler<ListType> {

	public static final String[] CLASS_NAMES = {ListType.class.getName()};

	@Override
	public void deleteStagedModel(ListType listType) throws PortalException {
		_listTypeLocalService.deleteListType(listType.getListTypeId());
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		ListType listType = _listTypeLocalService.getListTypeByUuidAndCompanyId(
			uuid, group.getCompanyId());

		if (listType != null) {
			deleteStagedModel(listType);
		}
	}

	@Override
	public List<ListType> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_listTypeLocalService.fetchListTypeByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, ListType listType)
		throws Exception {

		Element listTypeElement = portletDataContext.getExportDataElement(
			listType);

		portletDataContext.addClassedModel(
			listTypeElement, ExportImportPathUtil.getModelPath(listType),
			listType);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, ListType listType)
		throws Exception {

		ListType existingListType = _listTypeLocalService.fetchListType(
			portletDataContext.getCompanyId(), listType.getName(),
			listType.getType());

		if (existingListType == null) {
			ServiceContext serviceContext =
				portletDataContext.createServiceContext(listType);

			serviceContext.setUuid(listType.getUuid());

			existingListType = _listTypeLocalService.addListType(
				portletDataContext.getCompanyId(), listType.getName(),
				listType.getType());
		}

		portletDataContext.importClassedModel(listType, existingListType);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

}