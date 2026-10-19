/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.object.entry.contributor;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.object.entry.ObjectEntryContext;
import com.liferay.object.entry.contributor.ObjectEntryValuesContributor;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ObjectEntryValuesContributor.class)
public class LaunchEntryObjectEntryValuesContributor
	implements ObjectDefinitionScoped, ObjectEntryValuesContributor {

	@Override
	public void contribute(ObjectEntryContext objectEntryContext) {
		Map<String, Serializable> values = objectEntryContext.getValues();

		LaunchEntryManager launchEntryManager =
			_launchEntryManagerRegistry.getLaunchEntryManager(
				MapUtil.getString(values, "className"));

		if ((launchEntryManager == null) ||
			(MapUtil.getLong(values, "draftId") != 0) ||
			Validator.isNotNull(MapUtil.getString(values, "versionKey")) ||
			Validator.isNull(MapUtil.getString(values, "baseVersionKey"))) {

			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}
		else {
			serviceContext = (ServiceContext)serviceContext.clone();
		}

		serviceContext.setScopeGroupId(objectEntryContext.getGroupId());
		serviceContext.setUserId(objectEntryContext.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			LaunchEntryManager.Draft draft = launchEntryManager.addDraft(
				MapUtil.getLong(values, "classPK"),
				MapUtil.getLong(
					values, "r_launchSetToLaunchEntries_c_launchSetId"),
				MapUtil.getString(values, "baseVersionKey"));

			values.put("draftId", draft.getDraftId());
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return Collections.singletonList("LaunchEntry");
	}

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

}