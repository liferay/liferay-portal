/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Mendez Gonzalez
 */
@Component(service = StagedModelDataHandler.class)
public class UserGroupStagedModelDataHandler
	extends BaseStagedModelDataHandler<UserGroup> {

	public static final String[] CLASS_NAMES = {UserGroup.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		UserGroup userGroup = fetchStagedModelByUuidAndGroupId(
			uuid, group.getCompanyId());

		if (userGroup != null) {
			deleteStagedModel(userGroup);
		}
	}

	@Override
	public void deleteStagedModel(UserGroup userGroup) throws PortalException {
		_userGroupLocalService.deleteUserGroup(userGroup);
	}

	@Override
	public List<UserGroup> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_userGroupLocalService.fetchUserGroupByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(UserGroup userGroup) {
		return userGroup.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, UserGroup userGroup)
		throws Exception {

		Element userGroupElement = portletDataContext.getExportDataElement(
			userGroup);

		portletDataContext.addClassedModel(
			userGroupElement, ExportImportPathUtil.getModelPath(userGroup),
			userGroup);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long userGroupId)
		throws Exception {

		UserGroup existingUserGroup = fetchMissingReference(uuid, groupId);

		if (existingUserGroup == null) {
			return;
		}

		Map<Long, Long> userGroupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				UserGroup.class);

		userGroupIds.put(userGroupId, existingUserGroup.getUserGroupId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, UserGroup userGroup)
		throws Exception {

		ScopeUtil.validateInstanceScoped(portletDataContext);

		long userId = portletDataContext.getUserId(userGroup.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			userGroup);

		UserGroup existingUserGroup = fetchStagedModelByUuidAndGroupId(
			userGroup.getUuid(), portletDataContext.getGroupId());

		if (existingUserGroup == null) {
			existingUserGroup = _userGroupLocalService.fetchUserGroup(
				portletDataContext.getCompanyId(), userGroup.getName());
		}

		UserGroup importedUserGroup = null;

		if (existingUserGroup == null) {
			serviceContext.setUuid(userGroup.getUuid());

			importedUserGroup = _userGroupLocalService.addUserGroup(
				userGroup.getExternalReferenceCode(), userId,
				portletDataContext.getCompanyId(), userGroup.getName(),
				userGroup.getDescription(), serviceContext);
		}
		else {
			importedUserGroup = _userGroupLocalService.updateUserGroup(
				userGroup.getExternalReferenceCode(),
				portletDataContext.getCompanyId(),
				existingUserGroup.getUserGroupId(), userGroup.getName(),
				userGroup.getDescription(), serviceContext);
		}

		portletDataContext.importClassedModel(userGroup, importedUserGroup);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}