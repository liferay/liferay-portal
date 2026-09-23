/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.internal.util.GroupExportImportParameterUtil;
import com.liferay.exportimport.internal.util.ManifestXmlFilePathUtil;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.lar.ExportImportGroup;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.List;
import java.util.Set;

/**
 * @author Petteri Karttunen
 */
public class GroupImporter {

	public GroupImporter(
		ClassNameLocalService classNameLocalService,
		ExportImportHelper exportImportHelper,
		ExportImportReportEntryLocalService exportImportReportEntryLocalService,
		GroupLocalService groupLocalService, GroupService groupService,
		PortletDataContextFactory portletDataContextFactory) {

		_classNameLocalService = classNameLocalService;
		_exportImportHelper = exportImportHelper;
		_exportImportReportEntryLocalService =
			exportImportReportEntryLocalService;
		_groupLocalService = groupLocalService;
		_groupService = groupService;
		_portletDataContextFactory = portletDataContextFactory;
	}

	public void importGroups(
			PortletDataContext portletDataContext,
			UnsafeBiConsumer<PortletDataContext, Long, Exception>
				unsafeBiConsumer,
			long userId)
		throws Exception {

		if (ExportImportThreadLocal.isStagingInProcess() ||
			!GroupExportImportParameterUtil.isGroupExportImportEnabled(
				portletDataContext.getCompanyId()) ||
			GroupExportImportParameterUtil.isGroupScoped(portletDataContext)) {

			return;
		}

		List<ExportImportGroup> exportImportGroups =
			_exportImportHelper.getExportImportGroups(portletDataContext);

		if (ListUtil.isEmpty(exportImportGroups)) {
			return;
		}

		String[] selectedGroupExternalReferenceCodes =
			GroupExportImportParameterUtil.
				getSelectedGroupExternalReferenceCodes(
					portletDataContext.getParameterMap());

		if (ArrayUtil.isEmpty(selectedGroupExternalReferenceCodes)) {
			return;
		}

		Set<String> missingGroupExternalReferenceCodes = SetUtil.fromArray(
			selectedGroupExternalReferenceCodes);

		for (ExportImportGroup exportImportGroup : exportImportGroups) {
			if (!missingGroupExternalReferenceCodes.remove(
					exportImportGroup.getExternalReferenceCode())) {

				continue;
			}

			Group group = _fetchGroup(portletDataContext, exportImportGroup);

			if (group == null) {
				continue;
			}

			_updateParentGroup(portletDataContext, group, exportImportGroup);

			unsafeBiConsumer.accept(
				_createPortletDataContext(
					portletDataContext, exportImportGroup, group),
				userId);
		}

		for (String missingGroupExternalReferenceCode :
				missingGroupExternalReferenceCodes) {

			_addReportEntry(
				portletDataContext, missingGroupExternalReferenceCode,
				"Group " + missingGroupExternalReferenceCode +
					" is missing in the LAR file",
				ExportImportReportEntryConstants.TYPE_ERROR);
		}
	}

	private void _addReportEntry(
		PortletDataContext portletDataContext, String externalReferenceCode,
		String message, int type) {

		_exportImportReportEntryLocalService.getOrAddExportImportReportEntry(
			0, portletDataContext.getCompanyId(),
			GetterUtil.getString(externalReferenceCode),
			_classNameLocalService.getClassNameId(Group.class.getName()), 0,
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			type, message, null, "groups");
	}

	private PortletDataContext _createPortletDataContext(
			PortletDataContext portletDataContext,
			ExportImportGroup exportImportGroup, Group group)
		throws Exception {

		PortletDataContext groupPortletDataContext =
			_portletDataContextFactory.createImportPortletDataContext(
				portletDataContext.getCompanyId(), group.getGroupId(),
				GroupExportImportParameterUtil.getGroupImportParameterMap(
					exportImportGroup.getExternalReferenceCode(),
					portletDataContext.getParameterMap()),
				portletDataContext.getUserIdStrategy(),
				portletDataContext.getZipReader());

		groupPortletDataContext.setExportImportProcessId(
			portletDataContext.getExportImportProcessId());

		Element rootElement = _getRootElement(
			groupPortletDataContext, exportImportGroup.getGroupId());

		groupPortletDataContext.setImportDataRootElement(rootElement);

		Element missingReferencesElement = rootElement.element(
			"missing-references");

		if (missingReferencesElement != null) {
			groupPortletDataContext.setMissingReferencesElement(
				missingReferencesElement);
		}

		groupPortletDataContext.setPrivateLayout(false);

		Element headerElement = rootElement.element("header");

		if (headerElement == null) {
			throw new LARFileException(LARFileException.TYPE_INVALID_MANIFEST);
		}

		groupPortletDataContext.setSourceCompanyId(
			GetterUtil.getLong(headerElement.attributeValue("company-id")));
		groupPortletDataContext.setSourceCompanyGroupId(
			GetterUtil.getLong(
				headerElement.attributeValue("company-group-id")));

		groupPortletDataContext.setSourceGroupId(
			exportImportGroup.getGroupId());
		groupPortletDataContext.setSourceUserPersonalSiteGroupId(
			GetterUtil.getLong(
				headerElement.attributeValue("user-personal-site-group-id")));

		return groupPortletDataContext;
	}

	private Group _fetchGroup(
		PortletDataContext portletDataContext,
		ExportImportGroup exportImportGroup) {

		try {
			Group group = _groupService.fetchGroupByExternalReferenceCode(
				exportImportGroup.getExternalReferenceCode(),
				portletDataContext.getCompanyId());

			if (group == null) {
				String externalReferenceCode =
					exportImportGroup.getExternalReferenceCode();

				_addReportEntry(
					portletDataContext, externalReferenceCode,
					StringBundler.concat(
						"The group ", externalReferenceCode,
						" does not exist in the target instance. Create the ",
						"group before importing it."),
					ExportImportReportEntryConstants.TYPE_ERROR);

				return null;
			}

			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				group.getGroupId(), ActionKeys.EXPORT_IMPORT_LAYOUTS);

			if (!_exportImportHelper.isGroupSupported(group)) {
				String externalReferenceCode = group.getExternalReferenceCode();

				_addReportEntry(
					portletDataContext, externalReferenceCode,
					"Importing group " + externalReferenceCode +
						" is not supported",
					ExportImportReportEntryConstants.TYPE_ERROR);

				return null;
			}

			return group;
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to import group " +
					exportImportGroup.getExternalReferenceCode(),
				portalException);

			return null;
		}
	}

	private Element _getRootElement(
			PortletDataContext portletDataContext, long groupId)
		throws Exception {

		String xml = portletDataContext.getZipEntryAsString(
			ManifestXmlFilePathUtil.getImportManifestXmlFilePath(groupId));

		if (Validator.isNull(xml)) {
			throw new LARFileException(LARFileException.TYPE_MISSING_MANIFEST);
		}

		try {
			Document document = SAXReaderUtil.read(xml);

			return document.getRootElement();
		}
		catch (Exception exception) {
			throw new LARFileException(
				LARFileException.TYPE_INVALID_MANIFEST, exception);
		}
	}

	private void _updateParentGroup(
		PortletDataContext portletDataContext, Group group,
		ExportImportGroup exportImportGroup) {

		String parentGroupExternalReferenceCode =
			exportImportGroup.getParentGroupExternalReferenceCode();

		if (Validator.isNull(parentGroupExternalReferenceCode)) {
			return;
		}

		Group parentGroup =
			_groupLocalService.fetchGroupByExternalReferenceCode(
				parentGroupExternalReferenceCode,
				portletDataContext.getCompanyId());

		if (parentGroup == null) {
			_addReportEntry(
				portletDataContext,
				exportImportGroup.getExternalReferenceCode(),
				StringBundler.concat(
					"The parent group ",
					exportImportGroup.getParentGroupExternalReferenceCode(),
					" for ", exportImportGroup.getDescriptiveName(),
					" does not exist in the target instance or in the LAR ",
					"file."),
				ExportImportReportEntryConstants.TYPE_WARNING);

			return;
		}

		if (group.getParentGroupId() == parentGroup.getGroupId()) {
			return;
		}

		try {
			_groupLocalService.updateGroup(
				group.getGroupId(), parentGroup.getGroupId(),
				group.getNameMap(), group.getDescriptionMap(), group.getType(),
				group.getTypeSettings(), group.isManualMembership(),
				group.getMembershipRestriction(), group.getFriendlyURL(),
				group.isInheritContent(), group.isActive(), null);
		}
		catch (GroupParentException groupParentException) {
			if (_log.isWarnEnabled()) {
				_log.warn(groupParentException);
			}

			_addReportEntry(
				portletDataContext,
				exportImportGroup.getExternalReferenceCode(),
				StringBundler.concat(
					"The parent group ",
					exportImportGroup.getParentGroupExternalReferenceCode(),
					" for ", exportImportGroup.getDescriptiveName(),
					" is below it in the target instance. Leaving the group ",
					"where it is"),
				ExportImportReportEntryConstants.TYPE_WARNING);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			_addReportEntry(
				portletDataContext,
				exportImportGroup.getExternalReferenceCode(),
				StringBundler.concat(
					"Unable to move the group ",
					exportImportGroup.getDescriptiveName(),
					" under its parent group ",
					exportImportGroup.getParentGroupExternalReferenceCode(),
					"."),
				ExportImportReportEntryConstants.TYPE_WARNING);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(GroupImporter.class);

	private final ClassNameLocalService _classNameLocalService;
	private final ExportImportHelper _exportImportHelper;
	private final ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final GroupService _groupService;
	private final PortletDataContextFactory _portletDataContextFactory;

}