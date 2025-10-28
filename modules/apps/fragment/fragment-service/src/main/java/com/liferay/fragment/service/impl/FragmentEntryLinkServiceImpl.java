/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.base.FragmentEntryLinkServiceBaseImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"json.web.service.context.name=fragment",
		"json.web.service.context.path=FragmentEntryLink"
	},
	service = AopService.class
)
public class FragmentEntryLinkServiceImpl
	extends FragmentEntryLinkServiceBaseImpl {

	@Override
	public FragmentEntryLink addFragmentEntryLink(
			String externalReferenceCode, long groupId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long segmentsExperienceId, long plid,
			String css, String html, String js, String configuration,
			String editableValues, String namespace, int position,
			String rendererKey, int type, ServiceContext serviceContext)
		throws PortalException {

		_checkPermission(groupId, plid, false, true);

		return fragmentEntryLinkLocalService.addFragmentEntryLink(
			externalReferenceCode, getUserId(), groupId,
			originalFragmentEntryLinkERC, fragmentEntryERC,
			fragmentEntryScopeERC, segmentsExperienceId, plid, css, html, js,
			configuration, editableValues, namespace, position, rendererKey,
			type, serviceContext);
	}

	@Override
	public FragmentEntryLink deleteFragmentEntryLink(long fragmentEntryLinkId)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		_checkPermission(
			fragmentEntryLink.getGroupId(), fragmentEntryLink.getPlid(), false,
			false);

		return fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLinkId);
	}

	@Override
	public FragmentEntryLink deleteFragmentEntryLink(
			String externalReferenceCode, long groupId)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByERC_G(
				externalReferenceCode, groupId);

		_checkPermission(
			fragmentEntryLink.getGroupId(), fragmentEntryLink.getPlid(), false,
			false);

		return fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);
	}

	@Override
	public FragmentEntryLink getFragmentEntryLinkByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return fragmentEntryLinkLocalService.
			getFragmentEntryLinkByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public FragmentEntryLink updateDeleted(
			long fragmentEntryLinkId, boolean deleted)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		_checkPermission(
			fragmentEntryLink.getGroupId(), fragmentEntryLink.getPlid(), true,
			true);

		return fragmentEntryLinkLocalService.updateDeleted(
			getUserId(), fragmentEntryLinkId, deleted);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long fragmentEntryLinkId, String editableValues)
		throws PortalException {

		return updateFragmentEntryLink(
			fragmentEntryLinkId, editableValues, true);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		_checkPermission(
			fragmentEntryLink.getGroupId(), fragmentEntryLink.getPlid(), true,
			true);

		return fragmentEntryLinkLocalService.updateFragmentEntryLink(
			getUserId(), fragmentEntryLinkId, editableValues,
			updateClassedModel);
	}

	private void _checkPermission(
			long groupId, long plid, boolean checkUpdateLayoutContentPermission,
			boolean checkLayoutRestrictedUpdatePermission)
		throws PortalException {

		String className = Layout.class.getName();
		long classPK = plid;

		long layoutPageTemplateEntryPlid = plid;

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout.isDraftLayout()) {
			layoutPageTemplateEntryPlid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layoutPageTemplateEntryPlid);

		if (layoutPageTemplateEntry != null) {
			className = LayoutPageTemplateEntry.class.getName();
			classPK = layoutPageTemplateEntry.getLayoutPageTemplateEntryId();
		}

		if (GetterUtil.getBoolean(
				ModelResourcePermissionUtil.contains(
					getPermissionChecker(), groupId, className, classPK,
					ActionKeys.UPDATE))) {

			return;
		}

		if (!Objects.equals(className, Layout.class.getName()) ||
			(!checkUpdateLayoutContentPermission &&
			 !checkLayoutRestrictedUpdatePermission)) {

			throw new PrincipalException.MustHavePermission(
				getUserId(), className, classPK, ActionKeys.UPDATE);
		}

		if (_layoutPermission.contains(
				getPermissionChecker(), classPK, ActionKeys.UPDATE) ||
			(checkUpdateLayoutContentPermission &&
			 _layoutPermission.contains(
				 getPermissionChecker(), classPK,
				 ActionKeys.UPDATE_LAYOUT_CONTENT))) {

			return;
		}

		if (checkLayoutRestrictedUpdatePermission &&
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				getPermissionChecker(), classPK)) {

			return;
		}

		throw new PrincipalException.MustHavePermission(
			getUserId(), className, classPK, ActionKeys.UPDATE);
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

}