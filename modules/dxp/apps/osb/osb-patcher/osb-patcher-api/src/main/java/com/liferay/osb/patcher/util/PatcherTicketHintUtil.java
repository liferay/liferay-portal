/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherTicketHintLocalServiceUtil;
import com.liferay.petra.string.StringPool;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Arrays;

/**
 * @author Zsolt Balogh
 */
public class PatcherTicketHintUtil {

	public static String getPatcherTicketHintList(
		long productVersionId, String tickets, long projectVersionId) {

		PatcherTicketHint patcherTicketHint =
			PatcherTicketHintLocalServiceUtil.
				fetchPatcherTicketHintByProductVersionId(productVersionId);

		if (patcherTicketHint == null) {
			return StringPool.BLANK;
		}

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(
				projectVersionId);

		return String.valueOf(
			_processPatcherLpsHintScript(
				patcherTicketHint.getScript(), tickets,
				patcherProjectVersion.getName()));
	}

	private static String _processPatcherLpsHintScript(
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

}