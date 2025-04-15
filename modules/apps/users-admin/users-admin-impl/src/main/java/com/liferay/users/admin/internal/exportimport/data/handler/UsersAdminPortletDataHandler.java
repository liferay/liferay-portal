/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author David Gonzalez
 */
@Component(
	property = "jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
	service = PortletDataHandler.class
)
public class UsersAdminPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "users_admin";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean isSupportsDataStrategyCopyAsNew() {
		return false;
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTAL);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(Organization.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "organizations", true, true, null,
				Organization.class.getName()));
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		List<Organization> organizations =
			_organizationLocalService.getOrganizations(
				portletDataContext.getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID);

		for (Organization organization : organizations) {
			_organizationLocalService.deleteOrganization(organization);
		}

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortalPermissions();

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery actionableDynamicQuery =
			_organizationLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortalPermissions();

		Element organizationsElement =
			portletDataContext.getImportDataGroupElement(Organization.class);

		List<Element> organizationElements = organizationsElement.elements();

		for (Element organizationElement : organizationElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, organizationElement);
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_organizationLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.performCount();
	}

	@Reference
	private OrganizationLocalService _organizationLocalService;

}