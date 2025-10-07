/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.site.cms.site.initializer.internal.util.ObjectEntryFolderUtil;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 */
@Component(service = ServiceWrapper.class)
public class CMSObjectEntryFolderDepotEntryLocalServiceWrapper
	extends DepotEntryLocalServiceWrapper {

	@Override
	public DepotEntry addDepotEntry(Group group, ServiceContext serviceContext)
		throws PortalException {

		DepotEntry depotEntry = super.addDepotEntry(group, serviceContext);

		ObjectEntryFolderUtil.addObjectEntryFolders(depotEntry.getGroupId());

		return depotEntry;
	}

	@Override
	public DepotEntry addDepotEntry(
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, ServiceContext serviceContext)
		throws PortalException {

		DepotEntry depotEntry = super.addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);

		ObjectEntryFolderUtil.addObjectEntryFolders(depotEntry.getGroupId());

		return depotEntry;
	}

}