/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.internal.model.listener;

import com.liferay.audiences.service.AudiencesEntryGroupRelLocalService;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onBeforeRemove(Group group) {
		_audiencesEntryGroupRelLocalService.
			deleteAudiencesEntryGroupRelsByGroupERC(
				group.getCompanyId(), group.getExternalReferenceCode());
	}

	@Reference
	private AudiencesEntryGroupRelLocalService
		_audiencesEntryGroupRelLocalService;

}