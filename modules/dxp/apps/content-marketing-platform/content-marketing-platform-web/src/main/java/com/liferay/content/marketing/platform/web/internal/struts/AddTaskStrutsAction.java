/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.struts;

import com.liferay.content.marketing.platform.web.internal.util.ActionUtil;
import com.liferay.content.marketing.platform.web.internal.util.CMPHttpComponentsUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(property = "path=/cms/add_task", service = StrutsAction.class)
public class AddTaskStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				ParamUtil.getLong(httpServletRequest, "objectDefinitionId"));

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_TASK")) {

			return null;
		}

		long parentId = ParamUtil.getLong(httpServletRequest, "parentId");
		String projectGroupId = ParamUtil.getString(
			httpServletRequest, "projectGroupId");
		Map<String, Object> values = HashMapBuilder.<String, Object>put(
			"r_cmpProjectToCMPTasks_c_cmpProjectId",
			ParamUtil.getLong(httpServletRequest, "projectId")
		).build();

		if ((parentId > 0) &&
			FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-107678")) {

			com.liferay.object.model.ObjectEntry
				parentServiceBuilderObjectEntry =
					_objectEntryService.getObjectEntry(parentId);

			values.put(
				"r_cmpProjectToCMPTasks_c_cmpProjectId",
				MapUtil.getLong(
					parentServiceBuilderObjectEntry.getValues(),
					"r_cmpProjectToCMPTasks_c_cmpProjectId"));

			projectGroupId = String.valueOf(
				parentServiceBuilderObjectEntry.getGroupId());

			values.put("r_cmpTaskToCMPTasks_c_cmpTaskId", parentId);
		}

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getCompanyId(),
				objectDefinition.getStorageType());
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				themeDisplay.getSiteDefaultLocale(), null,
				themeDisplay.getUser()),
			objectDefinition,
			new ObjectEntry() {
				{
					setObjectEntryFolderExternalReferenceCode(
						() -> ParamUtil.getString(
							httpServletRequest,
							"objectEntryFolderExternalReferenceCode"));
					setProperties(() -> values);
					setStatus(
						() -> new Status() {
							{
								setCode(() -> WorkflowConstants.STATUS_DRAFT);
							}
						});
				}
			},
			projectGroupId);

		String editTaskURL =
			ActionUtil.getBaseEditTaskURL(objectDefinition, themeDisplay) +
				objectEntry.getId();

		editTaskURL = CMPHttpComponentsUtil.addParameter(
			httpServletRequest, "action", editTaskURL);
		editTaskURL = CMPHttpComponentsUtil.addParameter(
			httpServletRequest, "redirect", editTaskURL);

		httpServletResponse.sendRedirect(editTaskURL);

		return null;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryService _objectEntryService;

}