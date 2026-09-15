/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(service = ModelListener.class)
public class ObjectDefinitionSettingModelListener
	extends BaseModelListener<ObjectDefinitionSetting> {

	@Override
	public void onAfterCreate(ObjectDefinitionSetting objectDefinitionSetting)
		throws ModelListenerException {

		if (!StringUtil.equals(
				objectDefinitionSetting.getName(),
				ObjectDefinitionSettingConstants.NAME_OLD_CLASS_NAME_ID)) {

			return;
		}

		long oldClassNameId = GetterUtil.getLong(
			objectDefinitionSetting.getValue());

		if (oldClassNameId == 0) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionSetting.getObjectDefinitionId());

		if (objectDefinition == null) {
			return;
		}

		long newClassNameId = _classNameLocalService.getClassNameId(
			objectDefinition.getClassName());

		if (oldClassNameId == newClassNameId) {
			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_layoutPageTemplateEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"companyId", objectDefinitionSetting.getCompanyId())
			).add(
				RestrictionsFactoryUtil.eq("classNameId", oldClassNameId)
			));
		actionableDynamicQuery.setPerformActionMethod(
			(LayoutPageTemplateEntry layoutPageTemplateEntry) -> {
				layoutPageTemplateEntry.setClassNameId(newClassNameId);

				_layoutPageTemplateEntryLocalService.
					updateLayoutPageTemplateEntry(layoutPageTemplateEntry);
			});

		try {
			actionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}