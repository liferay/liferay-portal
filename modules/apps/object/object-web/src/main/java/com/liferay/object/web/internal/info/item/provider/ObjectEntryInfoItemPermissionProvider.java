/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.web.internal.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

/**
 * @author Lourdes Fernández Besada
 */
public class ObjectEntryInfoItemPermissionProvider
	implements InfoItemPermissionProvider<ObjectEntry> {

	public ObjectEntryInfoItemPermissionProvider(
		ObjectDefinition objectDefinition,
		ObjectEntryManager objectEntryManager,
		ObjectEntryService objectEntryService) {

		_objectDefinition = objectDefinition;
		_objectEntryManager = objectEntryManager;
		_objectEntryService = objectEntryService;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			return false;
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			return _hasPermission(
				actionId, classPKInfoItemIdentifier.getClassPK());
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext == null) {
				return false;
			}

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			if (themeDisplay == null) {
				return false;
			}

			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
				_objectEntryManager.getObjectEntry(
					themeDisplay.getCompanyId(),
					new DefaultDTOConverterContext(
						false, null, null, null, null, themeDisplay.getLocale(),
						null, themeDisplay.getUser()),
					ercInfoItemIdentifier.getExternalReferenceCode(),
					_objectDefinition, null);

			if (objectEntry != null) {
				hasPermission(
					permissionChecker,
					ObjectEntryUtil.toObjectEntry(
						_objectDefinition, objectEntry),
					actionId);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, ObjectEntry objectEntry,
			String actionId)
		throws InfoItemPermissionException {

		return _hasPermission(actionId, objectEntry.getObjectEntryId());
	}

	private boolean _hasPermission(String actionId, long objectEntryId) {
		try {
			return ObjectDefinitionResourcePermissionUtil.
				hasModelResourcePermission(
					_objectDefinition, objectEntryId, _objectEntryService,
					actionId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoItemPermissionProvider.class);

	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectEntryService _objectEntryService;

}