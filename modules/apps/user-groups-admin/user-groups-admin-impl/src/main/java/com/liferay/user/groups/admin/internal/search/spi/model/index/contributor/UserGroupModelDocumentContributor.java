/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.internal.search.spi.model.index.contributor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.UserGroup",
	service = ModelDocumentContributor.class
)
public class UserGroupModelDocumentContributor
	implements ModelDocumentContributor<UserGroup> {

	@Override
	public void contribute(Document document, UserGroup userGroup) {
		document.addKeyword(Field.COMPANY_ID, userGroup.getCompanyId());
		document.addText(Field.DESCRIPTION, userGroup.getDescription());
		document.addKeyword(
			"groupIds",
			TransformUtil.transformToLongArray(
				_groupLocalService.getUserGroupGroups(
					userGroup.getUserGroupId()),
				group -> group.getGroupId()));
		document.addText(Field.NAME, userGroup.getName());
		document.addKeyword(Field.USER_GROUP_ID, userGroup.getUserGroupId());
		document.addKeyword(
			"userIds",
			_userGroupLocalService.getUserPrimaryKeys(
				userGroup.getUserGroupId()));
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}