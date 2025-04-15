/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.web.internal.security.permission.resource.DDLRecordSetPermission;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class DDLRecordSetCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDLRecordSet> {

	@Override
	public String[] getAvailableLanguageIds(DDLRecordSet ddlRecordSet) {
		return ddlRecordSet.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(DDLRecordSet ddlRecordSet) {
		return ddlRecordSet.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, DDLRecordSet ddlRecordSet)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!DDLRecordSetPermission.contains(
				themeDisplay.getPermissionChecker(), ddlRecordSet,
				ActionKeys.UPDATE)) {

			return null;
		}

		Group group = _groupLocalService.getGroup(ddlRecordSet.getGroupId());

		if (group.isCompany()) {
			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DDLPortletKeys.DYNAMIC_DATA_LISTS, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_record_set.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"groupId", ddlRecordSet.getGroupId()
		).setParameter(
			"recordSetId", ddlRecordSet.getRecordSetId()
		).setParameter(
			"version", ddlRecordSet.getVersion()
		).buildString();
	}

	@Override
	public Class<DDLRecordSet> getModelClass() {
		return DDLRecordSet.class;
	}

	@Override
	public String getTitle(Locale locale, DDLRecordSet ddlRecordSet) {
		return ddlRecordSet.getName(locale);
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DDLRecordSet> displayBuilder) {
		DDLRecordSet ddlRecordSet = displayBuilder.getModel();

		Locale locale = displayBuilder.getLocale();

		displayBuilder.display(
			"name", ddlRecordSet.getName(locale)
		).display(
			"description", ddlRecordSet.getDescription(locale)
		).display(
			"data-definition",
			() -> {
				DDMStructure ddmStructure =
					_ddmStructureLocalService.fetchDDMStructure(
						ddlRecordSet.getDDMStructureId());

				if (ddmStructure != null) {
					return ddmStructure.getName(locale);
				}

				return StringPool.BLANK;
			}
		).display(
			"workflow",
			() -> {
				WorkflowDefinitionLink workflowDefinitionLink =
					_workflowDefinitionLinkLocalService.
						fetchWorkflowDefinitionLink(
							ddlRecordSet.getCompanyId(),
							ddlRecordSet.getGroupId(),
							DDLRecordSet.class.getName(),
							ddlRecordSet.getRecordSetId(), 0, true);

				if (workflowDefinitionLink != null) {
					return workflowDefinitionLink.getWorkflowDefinitionName();
				}

				return _language.get(locale, "no-workflow");
			}
		);
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}