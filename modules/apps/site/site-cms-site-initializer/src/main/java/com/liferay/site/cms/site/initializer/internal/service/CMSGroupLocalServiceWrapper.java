/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceWrapper;
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
public class CMSGroupLocalServiceWrapper extends GroupLocalServiceWrapper {

	@Override
	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws PortalException {

		Group group = super.addGroup(
			userId, parentGroupId, className, classPK, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, inheritContent, active, serviceContext);

		if (group.isDepot()) {
			ObjectEntryFolderUtil.addObjectEntryFolders(group.getGroupId());
		}

		return group;
	}

}