/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.test.util;

import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.Collections;

/**
 * @author Carolina Barbosa
 */
public class CMPTestUtil {

	public static ObjectEntry addCMPProjectLinkObjectEntry(
			ObjectEntry cmpProjectObjectEntry)
		throws PortalException {

		return _addObjectEntry(
			null, "L_CMP_PROJECT_LINK", cmpProjectObjectEntry,
			"r_cmpProjectToCMPProjectLinks_c_cmpProjectId");
	}

	public static ObjectEntry addCMPProjectLinkObjectEntry(
			ObjectEntry cmpProjectObjectEntry, ObjectEntry linkedObjectEntry)
		throws PortalException {

		return _addObjectEntry(
			linkedObjectEntry, "L_CMP_PROJECT_LINK", cmpProjectObjectEntry,
			"r_cmpProjectToCMPProjectLinks_c_cmpProjectId");
	}

	public static ObjectEntry addCMPProjectObjectEntry()
		throws PortalException {

		return addCMPProjectObjectEntry(WorkflowConstants.ACTION_SAVE_DRAFT);
	}

	public static ObjectEntry addCMPProjectObjectEntry(int workflowAction)
		throws PortalException {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_PROJECT,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(workflowAction);

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	public static ObjectEntry addCMPTaskLinkObjectEntry(
			ObjectEntry cmpTaskObjectEntry)
		throws PortalException {

		return _addObjectEntry(
			null, "L_CMP_TASK_LINK", cmpTaskObjectEntry,
			"r_cmpTaskToCMPTaskLinks_c_cmpTaskId");
	}

	public static ObjectEntry addCMPTaskLinkObjectEntry(
			ObjectEntry cmpTaskObjectEntry, ObjectEntry linkedObjectEntry)
		throws PortalException {

		return _addObjectEntry(
			linkedObjectEntry, "L_CMP_TASK_LINK", cmpTaskObjectEntry,
			"r_cmpTaskToCMPTaskLinks_c_cmpTaskId");
	}

	public static ObjectEntry addCMPTaskObjectEntry() throws PortalException {
		return addCMPTaskObjectEntry(addCMPProjectObjectEntry());
	}

	public static ObjectEntry addCMPTaskObjectEntry(
			ObjectEntry cmpProjectObjectEntry)
		throws PortalException {

		ObjectDefinition cmpTaskObjectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			cmpProjectObjectEntry.getGroupId(),
			cmpProjectObjectEntry.getUserId(),
			cmpTaskObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"r_cmpProjectToCMPTasks_c_cmpProjectId",
				cmpProjectObjectEntry.getObjectEntryId()
			).put(
				"title", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	public static ObjectEntry addCMSBasicWebContentObjectEntry(
			DepotEntry depotEntry, String title)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", depotEntry.getCompanyId());
		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", title
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId()));
	}

	public static Group getOrAddGroup(Class<?> clazz) throws Exception {
		Group group = GroupLocalServiceUtil.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());

		if (objectDefinition == null) {
			_initialize(true, clazz, group);

			objectDefinition =
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_CMP_PROJECT", TestPropsValues.getCompanyId());
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					group.getGroupId(),
					PortalUtil.getClassNameId(objectDefinition.getClassName()),
					0);

		if (layoutPageTemplateEntry == null) {
			_initialize(false, clazz, group);
		}

		return group;
	}

	private static ObjectEntry _addObjectEntry(
			ObjectEntry linkedObjectEntry,
			String objectDefinitionExternalReferenceCode,
			ObjectEntry objectEntry, String relationshipObjectFieldName)
		throws PortalException {

		String classExternalReferenceCode = RandomTestUtil.randomString();
		String className = RandomTestUtil.randomString();
		String groupExternalReferenceCode = RandomTestUtil.randomString();

		if (linkedObjectEntry != null) {
			classExternalReferenceCode =
				linkedObjectEntry.getExternalReferenceCode();
			className = linkedObjectEntry.getModelClassName();

			Group group = GroupLocalServiceUtil.getGroup(
				linkedObjectEntry.getGroupId());

			groupExternalReferenceCode = group.getExternalReferenceCode();
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			objectEntry.getGroupId(), objectEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				relationshipObjectFieldName, objectEntry.getObjectEntryId()
			).put(
				"classExternalReferenceCode", classExternalReferenceCode
			).put(
				"className", className
			).put(
				"groupExternalReferenceCode", groupExternalReferenceCode
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private static void _initialize(
			boolean processBatchEngine, Class<?> clazz, Group group)
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

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

				if (processBatchEngine) {
					BatchEngineTestUtil.processBatchEngineUnits(
						_BUNDLE_SYMBOLIC_NAME, clazz,
						new String[] {
							"." + _BUNDLE_SYMBOLIC_NAME +
								".internal.batch.00.list.type.definition",
							"." + _BUNDLE_SYMBOLIC_NAME +
								".internal.batch.01.notification.template",
							"." + _BUNDLE_SYMBOLIC_NAME +
								".internal.batch.02.object.folder",
							"." + _BUNDLE_SYMBOLIC_NAME +
								".internal.batch.03.object.definition"
						});
				}
			}
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			PrincipalThreadLocal.setName(originalName);

			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.site.initializer.cmp";

	private static final Snapshot<SiteInitializerRegistry>
		_siteInitializerRegistrySnapshot = new Snapshot<>(
			CMPTestUtil.class, SiteInitializerRegistry.class);

}