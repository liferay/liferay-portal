/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.script.management.groovy.script.use.GroovyScriptUse;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/system_settings/get_groovy_script_uses"
	},
	service = MVCResourceCommand.class
)
public class GetGroovyScriptUsesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, GroovyScriptUsesFactory.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<GroovyScriptUse> groovyScriptUses = new ArrayList<>();

		if (DBPartition.isPartitionEnabled()) {
			_companyLocalService.forEachCompanyId(
				companyId -> _addGroovyScriptUses(
					groovyScriptUses, resourceRequest));
		}
		else {
			_addGroovyScriptUses(groovyScriptUses, resourceRequest);
		}

		Comparator<GroovyScriptUse> comparator = Comparator.comparing(
			GroovyScriptUse::getCompanyWebId);

		groovyScriptUses.sort(
			comparator.thenComparing(
				groovyScriptUse -> StringUtil.lowerCase(
					groovyScriptUse.getSourceName())));

		for (GroovyScriptUse groovyScriptUse : groovyScriptUses) {
			jsonArray.put(
				JSONUtil.put(
					"companyWebId", groovyScriptUse.getCompanyWebId()
				).put(
					"sourceName", groovyScriptUse.getSourceName()
				).put(
					"sourceURL", groovyScriptUse.getSourceURL()
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	private void _addGroovyScriptUses(
			List<GroovyScriptUse> groovyScriptUses,
			ResourceRequest resourceRequest)
		throws Exception {

		Iterator<GroovyScriptUsesFactory> iterator =
			_serviceTrackerList.iterator();

		while (iterator.hasNext()) {
			GroovyScriptUsesFactory groovyScriptUsesFactory = iterator.next();

			groovyScriptUses.addAll(
				groovyScriptUsesFactory.create(resourceRequest));
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	private ServiceTrackerList<GroovyScriptUsesFactory> _serviceTrackerList;

}