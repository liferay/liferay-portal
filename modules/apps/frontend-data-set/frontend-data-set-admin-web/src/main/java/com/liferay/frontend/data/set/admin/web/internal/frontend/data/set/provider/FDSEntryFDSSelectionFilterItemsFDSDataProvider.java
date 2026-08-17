/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.frontend.data.set.provider;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletFDSNames;
import com.liferay.frontend.data.set.admin.web.internal.model.FDSSelectionFilterItem;
import com.liferay.frontend.data.set.constants.FDSAdminPortletKeys;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Arroyo
 */
@Component(
	property = "fds.data.provider.key=" + FDSAdminPortletFDSNames.FDS_ENTRY_FDS_SELECTION_FILTER_ITEMS,
	service = FDSDataProvider.class
)
public class FDSEntryFDSSelectionFilterItemsFDSDataProvider
	implements FDSDataProvider<FDSSelectionFilterItem> {

	@Override
	public List<FDSSelectionFilterItem> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_checkPermissions(themeDisplay);

		List<FDSSelectionFilterItem> fdsEntryFDSSelectionFilterItems =
			_getFDSEntryFDSSelectionFilterItems(
				themeDisplay.getCompanyId(), fdsKeywords);

		int start = Math.max(fdsPagination.getStartPosition(), 0);
		int end = Math.min(
			fdsPagination.getEndPosition(),
			fdsEntryFDSSelectionFilterItems.size());

		if ((start >= fdsEntryFDSSelectionFilterItems.size()) ||
			(start >= end)) {

			return Collections.emptyList();
		}

		ListUtil.sort(
			fdsEntryFDSSelectionFilterItems,
			Comparator.comparing(
				FDSSelectionFilterItem::getItemLabel,
				String::compareToIgnoreCase));

		return fdsEntryFDSSelectionFilterItems.subList(start, end);
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_checkPermissions(themeDisplay);

		return _getFDSEntryFDSSelectionFilterItems(
			themeDisplay.getCompanyId(), fdsKeywords
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

	private List<FDSSelectionFilterItem> _getFDSEntryFDSSelectionFilterItems(
			long companyId, FDSKeywords fdsKeywords)
		throws PortalException {

		List<SystemFDSEntry> systemFDSEntries =
			FDSDataProviderUtil.getSystemFDSEntries(
				fdsKeywords.getKeywords(), _systemFDSEntryRegistry);

		List<FDSSelectionFilterItem> fdsSelectionFilterItems =
			TransformUtil.transform(
				systemFDSEntries,
				(SystemFDSEntry systemFDSEntry) -> {
					String label = systemFDSEntry.getTitle();

					if (Validator.isNull(label)) {
						label = systemFDSEntry.getName();
					}

					return new FDSSelectionFilterItem(
						systemFDSEntry.getName(), label);
				});

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", companyId);

		if (objectDefinition == null) {
			return fdsSelectionFilterItems;
		}

		ObjectEntryManager objectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType()));

		try {
			Page<ObjectEntry> objectEntriesPage =
				objectEntryManager.getObjectEntries(
					companyId, objectDefinition, null, null,
					new DefaultDTOConverterContext(
						false, null, null, null, null,
						LocaleUtil.getMostRelevantLocale(), null, null),
					null, null, fdsKeywords.getKeywords(), null);

			fdsSelectionFilterItems.addAll(
				TransformUtil.transform(
					ListUtil.filter(
						ListUtil.fromCollection(objectEntriesPage.getItems()),
						(ObjectEntry objectEntry) -> !ListUtil.exists(
							systemFDSEntries,
							systemFDSEntry -> Objects.equals(
								systemFDSEntry.getName(),
								objectEntry.getExternalReferenceCode()))),
					(ObjectEntry objectEntry) -> {
						String externalReferenceCode =
							objectEntry.getExternalReferenceCode();

						Map<String, Object> properties =
							objectEntry.getProperties();

						String label = GetterUtil.getString(
							properties.get("label"));

						if (Validator.isNull(label)) {
							label = externalReferenceCode;
						}

						return new FDSSelectionFilterItem(
							externalReferenceCode, label);
					}));
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		return fdsSelectionFilterItems;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference(
		target = "(jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN + ")"
	)
	private Portlet _portlet;

	@Reference
	private SystemFDSEntryRegistry _systemFDSEntryRegistry;

}