/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Arrays;
import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherTicketHintUtil {

	public static String getPatcherTicketHintList(
			long productVersionId, String tickets, long projectVersionId)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(
				projectVersionId);

		PatcherTicketHint patcherTicketHint =
			fetchPatcherTicketHintByProductVersion(productVersionId);

		if (patcherTicketHint == null) {
			return "";
		}

		String result = String.valueOf(
			processPatcherLpsHintScript(
				patcherTicketHint.getScript(), tickets,
				patcherProjectVersion.getName()));

		return result;
	}

	protected static PatcherTicketHint fetchPatcherTicketHintByProductVersion(
			long patcherProductVersionId)
		throws Exception {

		AlloyServiceInvoker patcherTicketHintAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherTicketHint.class.getName());

		List<PatcherTicketHint> patcherTicketHints =
			patcherTicketHintAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"patcherProductVersionId", patcherProductVersionId
				});

		if (!patcherTicketHints.isEmpty()) {
			return patcherTicketHints.get(0);
		}

		return null;
	}

	protected static String processPatcherLpsHintScript(
			String script, String tickets, String projectVersionName)
		throws Exception {

		Binding binding = new Binding();

		binding.setVariable("projectVersion", projectVersionName);
		binding.setVariable("ticketsList", Arrays.asList(tickets.split(",")));

		GroovyShell groovyShell = new GroovyShell(binding);

		Object result = groovyShell.evaluate(script);

		if (result != null) {
			return result.toString();
		}

		return "";
	}

}