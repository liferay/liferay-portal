/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterUpdate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _onAfterCreate(ObjectEntry objectEntry) {
		_updateGroup(objectEntry);
	}

	private void _onAfterUpdate(ObjectEntry objectEntry) {
		_updateGroup(objectEntry);
	}

	private void _updateGroup(ObjectEntry objectEntry) {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666")) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if ((objectDefinition == null) ||
			!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_PIM_CATALOG")) {

			return;
		}

		String name = MapUtil.getString(objectEntry.getValues(), "name");

		if (Validator.isNull(name)) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		if (group == null) {
			return;
		}

		Locale locale = LocaleUtil.fromLanguageId(group.getDefaultLanguageId());

		if (StringUtil.equals(group.getName(locale), name)) {
			return;
		}

		group.setName(name, locale);

		_groupLocalService.updateGroup(group);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}