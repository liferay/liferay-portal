/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.item.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectEntryVersionLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryInfoItemUtil {

	public static String getInfoFieldNamespace(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		return StringBundler.concat(
			ObjectRelationship.class.getSimpleName(), StringPool.POUND,
			objectDefinition.getName(), StringPool.POUND,
			objectRelationship.getName());
	}

	public static ObjectEntry getObjectEntry(
		ObjectDefinition objectDefinition,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
		ThemeDisplay themeDisplay) {

		if ((serviceBuilderObjectEntry == null) || (themeDisplay == null)) {
			return null;
		}

		ObjectEntryManager objectEntryManager =
			objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getStorageType());

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null, themeDisplay.getLocale(), null,
				themeDisplay.getUser());

		ObjectEntryVersion objectEntryVersion =
			ObjectEntryVersionLocalServiceUtil.fetchObjectEntryVersion(
				serviceBuilderObjectEntry.getObjectEntryId(),
				serviceBuilderObjectEntry.getVersion());

		if (objectEntryVersion != null) {
			dtoConverterContext.setAttribute(
				"objectEntryVersion", objectEntryVersion);
		}

		try {
			return objectEntryManager.getObjectEntry(
				themeDisplay.getCompanyId(), dtoConverterContext,
				serviceBuilderObjectEntry.getExternalReferenceCode(),
				objectDefinition,
				getScopeKey(
					serviceBuilderObjectEntry.getGroupId(), objectDefinition,
					objectScopeProviderRegistry));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	public static String getScopeKey(
		long groupId, ObjectDefinition objectDefinition,
		ObjectScopeProviderRegistry objectScopeProviderRegistry) {

		ObjectScopeProvider objectScopeProvider =
			objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (!objectScopeProvider.isGroupAware()) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return group.getGroupKey();
	}

	public static ThemeDisplay getThemeDisplay() throws Exception {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay != null) {
			return themeDisplay;
		}

		return new ThemeDisplay() {
			{
				setCompany(
					CompanyLocalServiceUtil.getCompany(
						serviceContext.getCompanyId()));
				setLocale(
					LocaleUtil.fromLanguageId(serviceContext.getLanguageId()));
				setSiteGroupId(serviceContext.getScopeGroupId());
				setUser(getUser());
			}
		};
	}

	public static User getUser() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay != null) {
			return themeDisplay.getUser();
		}

		User user = UserLocalServiceUtil.fetchUser(serviceContext.getUserId());

		if (user == null) {
			user = UserLocalServiceUtil.fetchGuestUser(
				serviceContext.getCompanyId());
		}

		return user;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoItemUtil.class);

}