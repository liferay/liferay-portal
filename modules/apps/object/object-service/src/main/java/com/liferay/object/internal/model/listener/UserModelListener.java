/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterRemove(User user) throws ModelListenerException {
		try {
			User defaultServiceAccountUser =
				_userLocalService.fetchUserByScreenName(
					user.getCompanyId(), "default-service-account");

			if (defaultServiceAccountUser == null) {
				return;
			}

			_objectDefinitionLocalService.updateUserId(
				user.getCompanyId(), user.getUserId(),
				defaultServiceAccountUser.getUserId());
			_objectFieldLocalService.updateUserId(
				user.getCompanyId(), user.getUserId(),
				defaultServiceAccountUser.getUserId());
			_objectRelationshipLocalService.updateUserId(
				user.getCompanyId(), user.getUserId(),
				defaultServiceAccountUser.getUserId());
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onAfterUpdate(User originalUser, User user)
		throws ModelListenerException {

		if (!user.isGuestUser()) {
			return;
		}

		Locale locale = user.getLocale();

		if (Objects.equals(originalUser.getLocale(), locale)) {
			return;
		}

		for (ObjectDefinition systemObjectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(
					user.getCompanyId(), true, true,
					WorkflowConstants.STATUS_APPROVED)) {

			Map<Locale, String> labelMap = systemObjectDefinition.getLabelMap();

			if (labelMap.containsKey(locale)) {
				continue;
			}

			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						systemObjectDefinition.getName());

			if (systemObjectDefinitionManager == null) {
				continue;
			}

			Map<String, String> labelKeys =
				systemObjectDefinitionManager.getLabelKeys();

			Locale defaultLocale = LocaleUtil.fromLanguageId(
				systemObjectDefinition.getDefaultLanguageId(), false);

			systemObjectDefinition.setLabel(
				_language.get(locale, labelKeys.get("label")), locale,
				defaultLocale);
			systemObjectDefinition.setPluralLabel(
				_language.get(locale, labelKeys.get("pluralLabel")), locale,
				defaultLocale);

			_objectDefinitionLocalService.updateObjectDefinition(
				systemObjectDefinition);
		}
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}