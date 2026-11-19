/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán Grande
 */
@Component(
	property = "path=/cms/get_object_definition_deletion_info",
	service = StrutsAction.class
)
public class GetObjectDefinitionDeleteInfoStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long objectDefinitionId = ParamUtil.getLong(
			httpServletRequest, "objectDefinitionId");

		_objectDefinitionModelResourcePermission.check(
			themeDisplay.getPermissionChecker(), objectDefinitionId,
			ActionKeys.DELETE);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.toString(
				JSONUtil.put(
					"hasObjectRelationship",
					() -> {
						List<ObjectRelationship> objectRelationships =
							_objectRelationshipLocalService.
								getObjectRelationshipsByObjectDefinitionId2(
									objectDefinitionId);

						for (ObjectRelationship objectRelationship :
								objectRelationships) {

							if (Objects.equals(
									objectRelationship.getDeletionType(),
									ObjectRelationshipConstants.
										DELETION_TYPE_CASCADE)) {

								return true;
							}
						}

						return false;
					}
				).put(
					"objectEntriesCount",
					_objectEntryLocalService.getObjectEntriesCount(
						objectDefinitionId)
				).put(
					"status", objectDefinition.getStatus()
				)));

		return null;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}