/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniela Zapata Riesco
 */
@Component(
	property = "jakarta.portlet.name=" + PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
	service = PortletDataHandler.class
)
public class PasswordPolicyPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "password_policies_admin";

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
			new StagedModelType(PasswordPolicy.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "password-policies", true, true, null,
				PasswordPolicy.class.getName()));
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				PasswordPolicyPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_passwordPolicyLocalService.deleteNondefaultPasswordPolicies(
			portletDataContext.getCompanyId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery actionableDynamicQuery =
			_getPasswordPolicyActionableDynamicQuery(portletDataContext, true);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(RESOURCE_NAME);

		Element passwordPoliciesElement =
			portletDataContext.getImportDataGroupElement(PasswordPolicy.class);

		List<Element> passwordPolicyElements =
			passwordPoliciesElement.elements();

		for (Element passwordPolicyElement : passwordPolicyElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, passwordPolicyElement);
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_getPasswordPolicyActionableDynamicQuery(portletDataContext, false);

		actionableDynamicQuery.performCount();
	}

	protected static final String RESOURCE_NAME =
		"com.liferay.portlet.passwordpoliciesadmin";

	private ActionableDynamicQuery _getPasswordPolicyActionableDynamicQuery(
		PortletDataContext portletDataContext, boolean export) {

		ActionableDynamicQuery actionableDynamicQuery =
			_passwordPolicyLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.setPerformActionMethod(
			(PasswordPolicy passwordPolicy) -> {
				if (!export) {
					return;
				}

				StagedModelDataHandlerUtil.exportStagedModel(
					portletDataContext, passwordPolicy);
			});

		return actionableDynamicQuery;
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

}