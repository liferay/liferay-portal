/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalService;
import com.liferay.osb.patcher.service.PatcherTicketHintLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/get_ticket_suggestion_fields"
	},
	service = MVCResourceCommand.class
)
public class GetTicketSuggestionFieldsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long productVersionId = ParamUtil.getLong(
			resourceRequest, "productVersionId");
		String tickets = ParamUtil.getString(resourceRequest, "tickets");
		long projectVersionId = ParamUtil.getLong(
			resourceRequest, "projectVersionId");

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"troubleshooting",
				_getPatcherTicketHintList(
					productVersionId, tickets, projectVersionId)));
	}

	private String _getPatcherTicketHintList(
		long productVersionId, String tickets, long projectVersionId) {

		PatcherTicketHint patcherTicketHint =
			_patcherTicketHintLocalService.
				fetchPatcherTicketHintByProductVersionId(productVersionId);

		if (patcherTicketHint == null) {
			return StringPool.BLANK;
		}

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionLocalService.fetchPatcherProjectVersion(
				projectVersionId);

		return String.valueOf(
			_processPatcherLpsHintScript(
				patcherTicketHint.getScript(), tickets,
				patcherProjectVersion.getName()));
	}

	private String _processPatcherLpsHintScript(
		String script, String tickets, String projectVersionName) {

		Binding binding = new Binding();

		binding.setVariable("projectVersion", projectVersionName);
		binding.setVariable("ticketsList", Arrays.asList(tickets.split(",")));

		GroovyShell groovyShell = new GroovyShell(binding);

		Object result = groovyShell.evaluate(script);

		if (result != null) {
			return result.toString();
		}

		return StringPool.BLANK;
	}

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

	@Reference
	private PatcherTicketHintLocalService _patcherTicketHintLocalService;

}