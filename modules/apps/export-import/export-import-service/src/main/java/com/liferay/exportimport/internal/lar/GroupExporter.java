/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.internal.util.GroupExportImportParameterUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.Locale;

/**
 * @author Petteri Karttunen
 */
public class GroupExporter {

	public GroupExporter(
		ExportImportHelper exportImportHelper, GroupService groupService,
		PortletDataContextFactory portletDataContextFactory) {

		_exportImportHelper = exportImportHelper;
		_groupService = groupService;
		_portletDataContextFactory = portletDataContextFactory;
	}

	public void addGroupsElement(
			PortletDataContext portletDataContext, Element element)
		throws PortalException {

		if (ExportImportThreadLocal.isStagingInProcess() ||
			!GroupExportImportParameterUtil.isGroupExportImportEnabled(
				portletDataContext.getCompanyId()) ||
			GroupExportImportParameterUtil.isGroupScoped(portletDataContext)) {

			return;
		}

		String[] selectedGroupExternalReferenceCodes =
			GroupExportImportParameterUtil.
				getSelectedGroupExternalReferenceCodes(
					portletDataContext.getParameterMap());

		if (ArrayUtil.isEmpty(selectedGroupExternalReferenceCodes)) {
			return;
		}

		Element groupsElement = element.addElement("groups");

		for (String selectedGroupExternalReferenceCode :
				selectedGroupExternalReferenceCodes) {

			Group group = _fetchGroup(
				portletDataContext, selectedGroupExternalReferenceCode);

			if (group == null) {
				continue;
			}

			Element groupElement = groupsElement.addElement("group");

			Locale locale = LocaleUtil.fromLanguageId(
				group.getDefaultLanguageId());

			groupElement.addAttribute(
				"child-groups-count",
				String.valueOf(_exportImportHelper.getChildGroupsCount(group)));
			groupElement.addAttribute(
				"descriptive-name", group.getDescriptiveName(locale));
			groupElement.addAttribute(
				"external-reference-code", group.getExternalReferenceCode());
			groupElement.addAttribute(
				"group-id", String.valueOf(group.getGroupId()));
			groupElement.addAttribute(
				"path", _exportImportHelper.getGroupPath(group, locale));

			Group parentGroup = group.getParentGroup();

			if (parentGroup != null) {
				groupElement.addAttribute(
					"parent-group-external-reference-code",
					parentGroup.getExternalReferenceCode());
			}
		}
	}

	public void exportGroups(
			PortletDataContext portletDataContext,
			UnsafeConsumer<PortletDataContext, Exception> unsafeConsumer)
		throws Exception {

		if (ExportImportThreadLocal.isStagingInProcess() ||
			!GroupExportImportParameterUtil.isGroupExportImportEnabled(
				portletDataContext.getCompanyId()) ||
			GroupExportImportParameterUtil.isGroupScoped(portletDataContext)) {

			return;
		}

		String[] selectedGroupExternalReferenceCodes =
			GroupExportImportParameterUtil.
				getSelectedGroupExternalReferenceCodes(
					portletDataContext.getParameterMap());

		for (String selectedGroupExternalReferenceCode :
				selectedGroupExternalReferenceCodes) {

			Group group = _fetchGroup(
				portletDataContext, selectedGroupExternalReferenceCode);

			if (group == null) {
				continue;
			}

			unsafeConsumer.accept(
				_createPortletDataContext(portletDataContext, group));
		}
	}

	private PortletDataContext _createPortletDataContext(
			PortletDataContext portletDataContext, Group group)
		throws Exception {

		PortletDataContext groupPortletDataContext =
			_portletDataContextFactory.createExportPortletDataContext(
				portletDataContext.getCompanyId(), group.getGroupId(),
				GroupExportImportParameterUtil.getGroupExportParameterMap(
					group.getExternalReferenceCode(),
					portletDataContext.getParameterMap()),
				portletDataContext.getStartDate(),
				portletDataContext.getEndDate(),
				portletDataContext.getZipWriter());

		groupPortletDataContext.setExportImportProcessId(
			portletDataContext.getExportImportProcessId());
		groupPortletDataContext.setLayoutIds(
			_exportImportHelper.getAllLayoutIds(group.getGroupId(), false));
		groupPortletDataContext.setPrivateLayout(false);

		return groupPortletDataContext;
	}

	private Group _fetchGroup(
		PortletDataContext portletDataContext, String externalReferenceCode) {

		try {
			Group group = _groupService.fetchGroupByExternalReferenceCode(
				externalReferenceCode, portletDataContext.getCompanyId());

			if (group == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Group " + externalReferenceCode +
							" is missing in the instance");
				}

				return null;
			}

			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				group.getGroupId(), ActionKeys.EXPORT_IMPORT_LAYOUTS);

			if (!_exportImportHelper.isGroupSupported(group)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Exporting group " + externalReferenceCode +
							" not supported");
				}

				return null;
			}

			return group;
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to export group " + externalReferenceCode,
				portalException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(GroupExporter.class);

	private final ExportImportHelper _exportImportHelper;
	private final GroupService _groupService;
	private final PortletDataContextFactory _portletDataContextFactory;

}