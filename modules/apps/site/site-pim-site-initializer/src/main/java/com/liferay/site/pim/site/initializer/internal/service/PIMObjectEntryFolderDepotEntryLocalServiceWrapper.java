/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.service;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceWrapper;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectEntryFolderConstants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ServiceWrapper.class)
public class PIMObjectEntryFolderDepotEntryLocalServiceWrapper
	extends DepotEntryLocalServiceWrapper {

	@Override
	public DepotEntry addDepotEntry(Group group, ServiceContext serviceContext)
		throws PortalException {

		DepotEntry depotEntry = super.addDepotEntry(group, serviceContext);

		_addObjectEntryFolder(depotEntry);

		return depotEntry;
	}

	@Override
	public DepotEntry addDepotEntry(
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, ServiceContext serviceContext)
		throws PortalException {

		DepotEntry depotEntry = super.addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);

		_addObjectEntryFolder(depotEntry);

		return depotEntry;
	}

	@Override
	public DepotEntry deleteDepotEntry(DepotEntry depotEntry)
		throws PortalException {

		_deleteObjectEntryFolder(depotEntry);

		return super.deleteDepotEntry(depotEntry);
	}

	@Override
	public DepotEntry deleteDepotEntry(long depotEntryId)
		throws PortalException {

		_deleteObjectEntryFolder(getDepotEntry(depotEntryId));

		return super.deleteDepotEntry(depotEntryId);
	}

	private void _addObjectEntryFolder(DepotEntry depotEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				depotEntry.getCompanyId(), "LPD-96666") ||
			(depotEntry.getType() != DepotConstants.TYPE_SPACE)) {

			return;
		}

		Group group = depotEntry.getGroup();

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					group.getGroupId(), group.getCompanyId());

		if (objectEntryFolder != null) {
			return;
		}

		Map<Locale, String> labelMap = new HashMap<>();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(group.getGroupId())) {

			labelMap.put(
				locale, LanguageUtil.get(locale, "products", "Products"));
		}

		_objectEntryFolderLocalService.addObjectEntryFolder(
			PIMObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
			group.getGroupId(), group.getCreatorUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			"", labelMap, "Products",
			ServiceContextThreadLocal.getServiceContext());
	}

	private void _deleteObjectEntryFolder(DepotEntry depotEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				depotEntry.getCompanyId(), "LPD-96666") ||
			(depotEntry.getType() != DepotConstants.TYPE_SPACE)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());
		}
	}

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}