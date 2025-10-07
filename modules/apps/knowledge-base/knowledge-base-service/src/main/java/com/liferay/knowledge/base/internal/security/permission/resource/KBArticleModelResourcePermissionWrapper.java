/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.security.permission.resource;

import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBArticle",
	service = ModelResourcePermission.class
)
public class KBArticleModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<KBArticle> {

	@Override
	protected ModelResourcePermission<KBArticle>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			KBArticle.class, KBArticle::getResourcePrimKey,
			classPK -> {
				KBArticle kbArticle =
					_kbArticleLocalService.fetchLatestKBArticle(
						classPK, WorkflowConstants.STATUS_ANY);

				if (kbArticle == null) {
					kbArticle = _kbArticleLocalService.getKBArticle(classPK);
				}

				return kbArticle;
			},
			_portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new KBArticleDynamicInheritanceModelResourcePermissionLogic(
							modelResourcePermission));
				}
			});
	}

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private KBFolderLocalService _kbFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBFolder)"
	)
	private ModelResourcePermission<KBFolder> _kbFolderModelResourcePermission;

	@Reference(
		target = "(resource.name=" + KBConstants.RESOURCE_NAME_ADMIN + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	private class KBArticleDynamicInheritanceModelResourcePermissionLogic
		implements ModelResourcePermissionLogic<KBArticle> {

		@Override
		public Boolean contains(
				PermissionChecker permissionChecker, String name,
				KBArticle kbArticle, String actionId)
			throws PortalException {

			if (!ActionKeys.VIEW.equals(actionId)) {
				return null;
			}

			long parentResourcePrimKey = kbArticle.getParentResourcePrimKey();

			if (parentResourcePrimKey ==
					KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				return null;
			}

			long parentResourceClassNameId =
				kbArticle.getParentResourceClassNameId();

			long kbFolderClassNameId = PortalUtil.getClassNameId(
				KBFolderConstants.getClassName());

			if (parentResourceClassNameId == kbFolderClassNameId) {
				KBFolder kbFolder = _kbFolderLocalService.fetchKBFolder(
					parentResourcePrimKey);

				if ((kbFolder != null) &&
					!_kbFolderModelResourcePermission.contains(
						permissionChecker, kbFolder, actionId)) {

					return false;
				}
			}
			else {
				KBArticle parentKBArticle =
					_kbArticleLocalService.fetchLatestKBArticle(
						parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

				if ((parentKBArticle != null) &&
					!_kbArticleModelResourcePermission.contains(
						permissionChecker, parentKBArticle, actionId)) {

					return false;
				}
			}

			return null;
		}

		private KBArticleDynamicInheritanceModelResourcePermissionLogic(
			ModelResourcePermission<KBArticle>
				kbArticleModelResourcePermission) {

			_kbArticleModelResourcePermission =
				kbArticleModelResourcePermission;
		}

		private final ModelResourcePermission<KBArticle>
			_kbArticleModelResourcePermission;

	}

}