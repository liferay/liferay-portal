/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.service;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutServiceWrapper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ServiceWrapper.class)
public class LayoutPageTemplateLayoutServiceWrapper
	extends LayoutServiceWrapper {

	@Override
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, long classNameId, long classPK,
			Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, boolean system, Map<Locale, String> friendlyURLMap,
			String masterLayoutPageTemplateEntryERC,
			ServiceContext serviceContext)
		throws PortalException {

		_checkLayoutPageTemplateEntryPermission(classNameId, classPK, groupId);

		return super.addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId,
			classNameId, classPK, localeNamesMap, localeTitlesMap,
			descriptionMap, keywordsMap, robotsMap, type, typeSettings, hidden,
			system, friendlyURLMap, masterLayoutPageTemplateEntryERC,
			serviceContext);
	}

	@Override
	public Layout convertEmptyLayout(
			long plid, Map<Locale, String> nameMap, String type,
			long classNameId, long classPK,
			String masterLayoutPageTemplateEntryERC,
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		_checkLayoutPageTemplateEntryPermission(
			classNameId, classPK, layout.getGroupId());

		return super.convertEmptyLayout(
			plid, nameMap, type, classNameId, classPK,
			masterLayoutPageTemplateEntryERC, serviceContext);
	}

	private void _checkLayoutPageTemplateEntryPermission(
			long classNameId, long classPK, long groupId)
		throws PortalException {

		if ((classNameId != _portal.getClassNameId(
				LayoutPageTemplateEntry.class)) ||
			(classPK <= 0)) {

			return;
		}

		PermissionChecker permissionChecker =
			GuestOrUserUtil.getPermissionChecker();

		_layoutPageTemplateEntryModelResourcePermission.check(
			permissionChecker, classPK, ActionKeys.VIEW);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				classPK);

		if ((layoutPageTemplateEntry.getGroupId() != groupId) &&
			!DesignLibraryUtil.isConnectedDesignLibraryGroupId(
				layoutPageTemplateEntry.getGroupId(), groupId)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutPageTemplateEntry.class.getName(),
				classPK, ActionKeys.VIEW);
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry)"
	)
	private ModelResourcePermission<LayoutPageTemplateEntry>
		_layoutPageTemplateEntryModelResourcePermission;

	@Reference
	private Portal _portal;

}