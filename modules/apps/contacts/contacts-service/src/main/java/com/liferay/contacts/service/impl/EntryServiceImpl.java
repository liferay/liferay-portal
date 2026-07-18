/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.service.impl;

import com.liferay.contacts.model.Entry;
import com.liferay.contacts.service.base.EntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"json.web.service.context.name=contact",
		"json.web.service.context.path=Entry"
	},
	service = AopService.class
)
public class EntryServiceImpl extends EntryServiceBaseImpl {

	@Override
	public Entry getEntry(long entryId) throws PortalException {
		_entryModelResourcePermission.check(
			getPermissionChecker(), entryId, ActionKeys.VIEW);

		return entryPersistence.findByPrimaryKey(entryId);
	}

	@Reference(target = "(model.class.name=com.liferay.contacts.model.Entry)")
	private ModelResourcePermission<Entry> _entryModelResourcePermission;

}