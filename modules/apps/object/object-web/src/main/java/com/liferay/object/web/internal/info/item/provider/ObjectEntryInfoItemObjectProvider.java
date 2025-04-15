/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryInfoItemObjectProvider
	implements InfoItemObjectProvider<ObjectEntry> {

	public ObjectEntryInfoItemObjectProvider(
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry) {

		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
	}

	@Override
	public ObjectEntry getInfoItem(InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				classPKInfoItemIdentifier.getClassPK());

			if (objectEntry == null) {
				throw new NoSuchInfoItemException(
					"Unable to get object entry " +
						classPKInfoItemIdentifier.getClassPK());
			}

			return objectEntry;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		Map<InfoItemIdentifier, ObjectEntry> objectEntries = _getObjectEntries(
			serviceContext.getRequest());

		if (objectEntries.containsKey(ercInfoItemIdentifier)) {
			return objectEntries.get(ercInfoItemIdentifier);
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		try {
			Group group = themeDisplay.getScopeGroup();

			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
				objectEntryManager.getObjectEntry(
					themeDisplay.getCompanyId(),
					new DefaultDTOConverterContext(
						false, null, null, null, null, themeDisplay.getLocale(),
						null, themeDisplay.getUser()),
					ercInfoItemIdentifier.getExternalReferenceCode(),
					_objectDefinition, group.getGroupKey());

			if (objectEntry != null) {
				ObjectEntry serviceBuilderObjectEntry =
					ObjectEntryUtil.toObjectEntry(
						_objectDefinition.getObjectDefinitionId(), objectEntry);

				objectEntries.put(
					ercInfoItemIdentifier, serviceBuilderObjectEntry);

				return serviceBuilderObjectEntry;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		throw new NoSuchInfoItemException(
			"Unable to get object entry " +
				ercInfoItemIdentifier.getExternalReferenceCode());
	}

	private Map<InfoItemIdentifier, ObjectEntry> _getObjectEntries(
		HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return new HashMap<>();
		}

		Map<InfoItemIdentifier, ObjectEntry> objectEntries =
			(Map<InfoItemIdentifier, ObjectEntry>)
				httpServletRequest.getAttribute(_OBJECT_ENTRIES);

		if (objectEntries == null) {
			objectEntries = new HashMap<>();

			httpServletRequest.setAttribute(_OBJECT_ENTRIES, objectEntries);
		}

		return objectEntries;
	}

	private static final String _OBJECT_ENTRIES =
		ObjectEntryInfoItemObjectProvider.class.getName() + "#OBJECT_ENTRIES";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoItemObjectProvider.class);

	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;

}