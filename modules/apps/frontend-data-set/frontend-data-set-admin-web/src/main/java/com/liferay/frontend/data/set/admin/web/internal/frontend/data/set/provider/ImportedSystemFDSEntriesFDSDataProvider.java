/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.frontend.data.set.provider;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletFDSNames;
import com.liferay.frontend.data.set.admin.web.internal.model.ImportedSystemFDSEntry;
import com.liferay.frontend.data.set.constants.FDSAdminPortletKeys;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Arroyo
 */
@Component(
	property = "fds.data.provider.key=" + FDSAdminPortletFDSNames.IMPORTED_SYSTEM_FDS_ENTRIES,
	service = FDSDataProvider.class
)
public class ImportedSystemFDSEntriesFDSDataProvider
	implements FDSDataProvider<ImportedSystemFDSEntry> {

	@Override
	public List<ImportedSystemFDSEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_checkPermissions(themeDisplay);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", themeDisplay.getCompanyId());

		if (objectDefinition == null) {
			return Collections.emptyList();
		}

		List<SystemFDSEntry> systemFDSEntries =
			FDSDataProviderUtil.getSystemFDSEntries(
				fdsKeywords.getKeywords(), _systemFDSEntryRegistry);

		int start = Math.max(fdsPagination.getStartPosition(), 0);
		int end = Math.min(
			fdsPagination.getEndPosition(), systemFDSEntries.size());

		if ((start >= systemFDSEntries.size()) || (start >= end)) {
			return Collections.emptyList();
		}

		ListUtil.sort(
			systemFDSEntries,
			Comparator.comparing(
				systemFDSEntry -> {
					String title = systemFDSEntry.getTitle();

					if (title != null) {
						return title;
					}

					return StringPool.BLANK;
				},
				String::compareToIgnoreCase));

		return TransformUtil.transform(
			systemFDSEntries.subList(start, end),
			(SystemFDSEntry systemFDSEntry) -> _toImportedSystemFDSEntry(
				httpServletRequest, objectDefinition, systemFDSEntry));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_checkPermissions(themeDisplay);

		return FDSDataProviderUtil.getSystemFDSEntries(
			fdsKeywords.getKeywords(), _systemFDSEntryRegistry
		).size();
	}

	private void _checkPermissions(ThemeDisplay themeDisplay)
		throws PortalException {

		if (!PortletPermissionUtil.hasControlPanelAccessPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), _portlet)) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getPermissionChecker(), _portlet.getPortletClass(),
				_portlet.getPortletId(), ActionKeys.ACCESS_IN_CONTROL_PANEL);
		}
	}

	private ImportedSystemFDSEntry _toImportedSystemFDSEntry(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition, SystemFDSEntry systemFDSEntry) {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			systemFDSEntry.getName(),
			ObjectDefinitionConstants.GROUP_ID_DEFAULT,
			objectDefinition.getObjectDefinitionId());

		return new ImportedSystemFDSEntry(
			systemFDSEntry.getAdditionalAPIURLParameters(httpServletRequest),
			systemFDSEntry.getDefaultItemsPerPage(),
			systemFDSEntry.getDescription(), objectEntry != null,
			systemFDSEntry.getName(), systemFDSEntry.getRESTApplication(),
			systemFDSEntry.getRESTEndpoint(), systemFDSEntry.getRESTSchema(),
			systemFDSEntry.getSymbol(), systemFDSEntry.getTitle());
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(
		target = "(jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN + ")"
	)
	private Portlet _portlet;

	@Reference
	private SystemFDSEntryRegistry _systemFDSEntryRegistry;

}