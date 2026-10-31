/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateCollectionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"json.web.service.context.name=layout",
		"json.web.service.context.path=LayoutPageTemplateCollection"
	},
	service = AopService.class
)
public class LayoutPageTemplateCollectionServiceImpl
	extends LayoutPageTemplateCollectionServiceBaseImpl {

	@Override
	public LayoutPageTemplateCollection addLayoutPageTemplateCollection(
			String externalReferenceCode, long groupId,
			long parentLayoutPageTemplateCollectionId,
			String layoutPageTemplateCollectionKey, String name,
			String description, int type, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION);

		return layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				externalReferenceCode, getUserId(), groupId,
				parentLayoutPageTemplateCollectionId,
				layoutPageTemplateCollectionKey, name, description, type,
				serviceContext);
	}

	@Override
	public LayoutPageTemplateCollection copyLayoutPageTemplateCollection(
			long groupId, long sourceLayoutPageTemplateCollectionId,
			long layoutParentPageTemplateCollectionId, boolean copyPermissions,
			ServiceContext serviceContext)
		throws Exception {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION);

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), sourceLayoutPageTemplateCollectionId,
			ActionKeys.VIEW);

		return layoutPageTemplateCollectionLocalService.
			copyLayoutPageTemplateCollection(
				getUserId(), groupId, sourceLayoutPageTemplateCollectionId,
				layoutParentPageTemplateCollectionId, copyPermissions,
				serviceContext);
	}

	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId)
		throws PortalException {

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateCollectionId,
			ActionKeys.DELETE);

		return layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.findByERC_G(
				externalReferenceCode, groupId);

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateCollection,
			ActionKeys.DELETE);

		return layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	@Override
	public void deleteLayoutPageTemplateCollections(
			long[] layoutPageTemplateCollectionIds)
		throws PortalException {

		for (long layoutPageTemplateCollectionId :
				layoutPageTemplateCollectionIds) {

			_layoutPageTemplateCollectionModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateCollectionId,
				ActionKeys.DELETE);

			layoutPageTemplateCollectionLocalService.
				deleteLayoutPageTemplateCollection(
					layoutPageTemplateCollectionId);
		}
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollectionId);

		if (layoutPageTemplateCollection != null) {
			_layoutPageTemplateCollectionModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateCollection,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
			long groupId, String name,
			long parentLayoutPageTemplateCollectionId, int type)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.fetchByG_P_N_T(
				groupId, parentLayoutPageTemplateCollectionId, name, type);

		if (layoutPageTemplateCollection != null) {
			_layoutPageTemplateCollectionModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateCollection,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (layoutPageTemplateCollection != null) {
			_layoutPageTemplateCollectionModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateCollection,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection getLayoutPageTemplateCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.findByERC_G(
				externalReferenceCode, groupId);

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateCollection,
			ActionKeys.VIEW);

		return layoutPageTemplateCollection;
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int type) {

		return layoutPageTemplateCollectionPersistence.filterFindByG_T(
			groupId, type);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int type, int start, int end) {

		return layoutPageTemplateCollectionPersistence.filterFindByG_T(
			groupId, type, start, end);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return layoutPageTemplateCollectionPersistence.filterFindByG_T(
			groupId, type, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, long layoutPageTemplateCollectionId) {

		return layoutPageTemplateCollectionPersistence.filterFindByG_P(
			groupId, layoutPageTemplateCollectionId);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		if (Validator.isNull(name)) {
			return layoutPageTemplateCollectionPersistence.filterFindByG_T(
				groupId, type, start, end, orderByComparator);
		}

		return layoutPageTemplateCollectionPersistence.filterFindByG_LikeN_T(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			type, start, end, orderByComparator);
	}

	@Override
	public int getLayoutPageTemplateCollectionsCount(long groupId, int type) {
		return layoutPageTemplateCollectionPersistence.filterCountByG_T(
			groupId, type);
	}

	@Override
	public int getLayoutPageTemplateCollectionsCount(
		long groupId, String name, int type) {

		if (Validator.isNull(name)) {
			return layoutPageTemplateCollectionPersistence.filterCountByG_T(
				groupId, type);
		}

		return layoutPageTemplateCollectionPersistence.filterCountByG_LikeN_T(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			type);
	}

	@Override
	public LayoutPageTemplateCollection moveLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId,
			long targetLayoutPageTemplateCollectionId)
		throws PortalException {

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateCollectionId,
			ActionKeys.UPDATE);

		return layoutPageTemplateCollectionLocalService.
			moveLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId,
				targetLayoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId, String name)
		throws PortalException {

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateCollectionId,
			ActionKeys.UPDATE);

		return layoutPageTemplateCollectionLocalService.
			updateLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId, name);
	}

	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId, String name,
			String description)
		throws PortalException {

		_layoutPageTemplateCollectionModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateCollectionId,
			ActionKeys.UPDATE);

		return layoutPageTemplateCollectionLocalService.
			updateLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId, name, description);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection)"
	)
	private ModelResourcePermission<LayoutPageTemplateCollection>
		_layoutPageTemplateCollectionModelResourcePermission;

	@Reference(
		target = "(resource.name=" + LayoutPageTemplateConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}