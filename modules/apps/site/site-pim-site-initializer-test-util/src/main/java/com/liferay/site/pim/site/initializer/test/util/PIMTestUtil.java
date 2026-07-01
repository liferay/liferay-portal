/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.test.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.Collections;

/**
 * @author Stefano Motta
 */
public class PIMTestUtil {

	public static ObjectEntry addCatalogObjectEntry() throws PortalException {
		DepotEntry depotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_CATALOG,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_PIM_CATALOG", TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	public static Group getOrAddGroup(Class<?> clazz) throws Exception {
		Group group = CMSTestUtil.getOrAddGroup(clazz);

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_PIM_CATALOG", TestPropsValues.getCompanyId());

		if (objectDefinition != null) {
			return group;
		}

		_initialize(group);

		return group;
	}

	private static void _initialize(Group group) throws Exception {
		try {
			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						group.getCompanyId())) {

				SiteInitializerRegistry siteInitializerRegistry =
					_siteInitializerRegistrySnapshot.get();

				SiteInitializer siteInitializer =
					siteInitializerRegistry.getSiteInitializer(
						_BUNDLE_SYMBOLIC_NAME);

				siteInitializer.initialize(group.getGroupId());
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.site.initializer.pim";

	private static final Snapshot<SiteInitializerRegistry>
		_siteInitializerRegistrySnapshot = new Snapshot<>(
			PIMTestUtil.class, SiteInitializerRegistry.class);

}