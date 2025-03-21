/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set;

import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.object.entries.frontend.data.set.data.model.RelatedModel;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.web.internal.object.entries.constants.ObjectEntriesFDSNames;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "fds.data.provider.key=" + ObjectEntriesFDSNames.RELATED_MODELS,
	service = FDSActionProvider.class
)
public class RelatedModelFDSActionProvider implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		if (ParamUtil.getBoolean(httpServletRequest, "readOnly")) {
			return null;
		}

		RelatedModel relatedModel = (RelatedModel)model;

		return DropdownItemListBuilder.add(
			() -> {
				ObjectEntry objectEntry =
					_objectEntryLocalService.getObjectEntry(
						relatedModel.getId());

				ModelResourcePermission<ObjectEntry> modelResourcePermission =
					_objectEntryService.getModelResourcePermission(
						objectEntry.getObjectDefinitionId());

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return modelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), objectEntry,
					ActionKeys.UPDATE);
			},
			dropdownItem -> {
				dropdownItem.setHref(
					_getDeleteURL(
						relatedModel.getClassName(), relatedModel.getId(),
						httpServletRequest));
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.DELETE));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("id", "view");
				dropdownItem.setHref(
					_getViewURL(relatedModel.getId(), httpServletRequest));
				dropdownItem.setIcon("view");
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.VIEW));
			}
		).build();
	}

	private PortletURL _getDeleteURL(
			String className, long id, HttpServletRequest httpServletRequest)
		throws PortalException {

		long objectEntryId = ParamUtil.getLong(
			httpServletRequest, "objectEntryId");

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, objectDefinition.getPortletId(),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/object_entries/edit_object_entry"
		).setCMD(
			() -> {
				if (objectEntry.getRootObjectEntryId() != 0) {
					return "deleteRelatedModels";
				}

				return "disassociateRelatedModels";
			}
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"className", className
		).setParameter(
			"objectEntryId", objectEntryId
		).setParameter(
			"objectRelationshipId",
			ParamUtil.getLong(httpServletRequest, "objectRelationshipId")
		).setParameter(
			"relatedModelId", id
		).buildPortletURL();
	}

	private PortletURL _getViewURL(
			long id, HttpServletRequest httpServletRequest)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(id);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, objectDefinition.getPortletId(),
				PortletRequest.ACTION_PHASE)
		).setMVCRenderCommandName(
			"/object_entries/edit_object_entry"
		).setBackURL(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).buildPortletURL();
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

}