/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.security.permission.resource;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourcePermissionCheckerUtil;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermissionUtil;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.sharing.security.permission.resource.SharingModelResourcePermissionConfigurator;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = ModelResourcePermission.class
)
public class DLFileEntryModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<DLFileEntry> {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext,
			ModelResourcePermissionFactory.ModelResourcePermissionConfigurator.
				class,
			"(model.class.name=" +
				"com.liferay.document.library.kernel.model.DLFileEntry)");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Override
	protected ModelResourcePermission<DLFileEntry>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			DLFileEntry.class, DLFileEntry::getFileEntryId,
			_dlFileEntryLocalService::getDLFileEntry,
			_portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				_serviceTrackerList.forEach(
					modelResourcePermissionConfigurator ->
						modelResourcePermissionConfigurator.
							configureModelResourcePermissionLogics(
								modelResourcePermission, consumer));

				consumer.accept(
					new StagedModelPermissionLogic<>(
						_stagingPermission, DLPortletKeys.DOCUMENT_LIBRARY,
						DLFileEntry::getFileEntryId));
				consumer.accept(
					new DLFileEntryWorkflowedModelResourcePermissionLogic(
						modelResourcePermission));

				_sharingModelResourcePermissionConfigurator.configure(
					modelResourcePermission, consumer);

				consumer.accept(
					(permissionChecker, name, fileEntry, actionId) -> {
						String className = fileEntry.getClassName();
						long classPK = fileEntry.getClassPK();

						if (Validator.isNull(className) || (classPK <= 0)) {
							return null;
						}

						String relatedModelActionId = _getRelatedModelActionId(
							actionId);

						Boolean hasResourcePermission =
							ResourcePermissionCheckerUtil.
								containsResourcePermission(
									permissionChecker, className, classPK,
									relatedModelActionId);

						if ((hasResourcePermission != null) &&
							!hasResourcePermission) {

							return false;
						}

						Boolean hasBaseModelPermission =
							ModelResourcePermissionUtil.contains(
								permissionChecker, fileEntry.getGroupId(),
								className, classPK, relatedModelActionId);

						if ((hasBaseModelPermission != null) &&
							!hasBaseModelPermission) {

							return false;
						}

						return null;
					});

				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					DynamicInheritancePermissionLogic<DLFileEntry, DLFolder>
						dynamicInheritancePermissionLogic =
							new DynamicInheritancePermissionLogic<>(
								_dlFolderModelResourcePermission,
								_getFetchParentFunction(), true);

					consumer.accept(
						(permissionChecker, name, model, actionId) -> {
							if (actionId.equals(ActionKeys.DOWNLOAD)) {
								actionId = ActionKeys.VIEW;
							}

							return dynamicInheritancePermissionLogic.contains(
								permissionChecker, name, model, actionId);
						});
				}
			});
	}

	private UnsafeFunction<DLFileEntry, DLFolder, PortalException>
		_getFetchParentFunction() {

		return fileEntry -> {
			long folderId = fileEntry.getFolderId();

			if (DLFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (fileEntry.isInTrash()) {
				return _dlFolderLocalService.fetchDLFolder(folderId);
			}

			return _dlFolderLocalService.getFolder(folderId);
		};
	}

	private String _getRelatedModelActionId(String actionId) {
		if (actionId.equals(ActionKeys.DOWNLOAD)) {
			return ActionKeys.VIEW;
		}

		return actionId;
	}

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFolder)"
	)
	private ModelResourcePermission<DLFolder> _dlFolderModelResourcePermission;

	@Reference(target = "(resource.name=" + DLConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	private ServiceTrackerList
		<ModelResourcePermissionFactory.ModelResourcePermissionConfigurator>
			_serviceTrackerList;

	@Reference
	private SharingModelResourcePermissionConfigurator
		_sharingModelResourcePermissionConfigurator;

	@Reference
	private StagingPermission _stagingPermission;

	private class DLFileEntryWorkflowedModelResourcePermissionLogic
		implements ModelResourcePermissionLogic<DLFileEntry> {

		@Override
		public Boolean contains(
				PermissionChecker permissionChecker, String name,
				DLFileEntry dlFileEntry, String actionId)
			throws PortalException {

			DLFileVersion fileVersion = dlFileEntry.getFileVersion();

			if (fileVersion.isDraft() || fileVersion.isScheduled()) {
				if (actionId.equals(ActionKeys.VIEW) &&
					!_modelResourcePermission.contains(
						permissionChecker, dlFileEntry, ActionKeys.UPDATE)) {

					return false;
				}
			}
			else if (fileVersion.isPending()) {
				Boolean hasPermission = WorkflowPermissionUtil.hasPermission(
					permissionChecker, fileVersion.getGroupId(), name,
					fileVersion.getFileVersionId(), actionId);

				if (hasPermission != null) {
					return hasPermission;
				}

				return false;
			}

			return null;
		}

		private DLFileEntryWorkflowedModelResourcePermissionLogic(
			ModelResourcePermission<DLFileEntry> modelResourcePermission) {

			_modelResourcePermission = modelResourcePermission;
		}

		private final ModelResourcePermission<DLFileEntry>
			_modelResourcePermission;

	}

}