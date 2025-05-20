/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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