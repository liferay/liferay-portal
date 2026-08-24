/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.exception.ObjectDefinitionObjectFolderIdException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class ObjectDefinitionModelListener
	extends BaseModelListener<ObjectDefinition> {

	@Override
	public void onBeforeCreate(ObjectDefinition objectDefinition)
		throws ModelListenerException {

		_validateCMSObjectDefinitionScope(objectDefinition);
	}

	@Override
	public void onBeforeRemove(ObjectDefinition objectDefinition)
		throws ModelListenerException {

		if (!objectDefinition.isCMS()) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(
			objectDefinition.getCompanyId(), GroupConstants.CMS);

		if (group == null) {
			return;
		}

		try {
			long classNameId = _portal.getClassNameId(
				objectDefinition.getClassName());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchDefaultLayoutPageTemplateEntry(
						group.getGroupId(), classNameId, 0);

			if (layoutPageTemplateEntry != null) {
				_layoutPageTemplateEntryLocalService.
					deleteLayoutPageTemplateEntry(layoutPageTemplateEntry);
			}

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						group.getGroupId(), "LFR_CMS_COMPARE_" + classNameId);

			if (layoutPageTemplateEntry != null) {
				_layoutPageTemplateEntryLocalService.
					deleteLayoutPageTemplateEntry(layoutPageTemplateEntry);
			}

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						group.getGroupId(),
						"LFR_CMS_TRANSLATION_" + classNameId);

			if (layoutPageTemplateEntry != null) {
				_layoutPageTemplateEntryLocalService.
					deleteLayoutPageTemplateEntry(layoutPageTemplateEntry);
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectDefinition originalObjectDefinition,
			ObjectDefinition objectDefinition)
		throws ModelListenerException {

		if (originalObjectDefinition.isCMS() &&
			!StringUtil.equals(
				originalObjectDefinition.getObjectFolderExternalReferenceCode(),
				objectDefinition.getObjectFolderExternalReferenceCode())) {

			throw new ModelListenerException(
				new ObjectDefinitionObjectFolderIdException(
					"CMS object definitions cannot change their folder " +
						"location"));
		}

		_validateCMSObjectDefinitionScope(objectDefinition);
	}

	private void _validateCMSObjectDefinitionScope(
		ObjectDefinition objectDefinition) {

		if (objectDefinition.isCMS() &&
			!StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			throw new ModelListenerException(
				new ObjectDefinitionScopeException(
					"CMS object definitions can only have scope \"depot\""));
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

}