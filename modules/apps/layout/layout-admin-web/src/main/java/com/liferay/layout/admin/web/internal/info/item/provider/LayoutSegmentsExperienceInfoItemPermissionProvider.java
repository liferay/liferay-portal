/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.provider;

import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemPermissionProviderHelper;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = InfoItemPermissionProvider.class)
public class LayoutSegmentsExperienceInfoItemPermissionProvider
	implements InfoItemPermissionProvider<SegmentsExperience> {

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		return _layoutInfoItemPermissionProviderHelper.hasPermission(
			permissionChecker,
			_getInfoItemReference(classPKInfoItemIdentifier.getClassPK()),
			actionId);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			SegmentsExperience segmentsExperience, String actionId)
		throws InfoItemPermissionException {

		return _layoutInfoItemPermissionProviderHelper.hasPermission(
			permissionChecker, _getLayout(segmentsExperience), actionId);
	}

	@Activate
	protected void activate() {
		_layoutInfoItemPermissionProviderHelper =
			new LayoutInfoItemPermissionProviderHelper(
				_layoutModelResourcePermission);
	}

	private InfoItemReference _getInfoItemReference(long segmentsExperienceId) {
		try {
			Layout layout = _getLayout(
				_segmentsExperienceLocalService.getSegmentsExperience(
					segmentsExperienceId));

			return new InfoItemReference(
				Layout.class.getName(),
				new ClassPKInfoItemIdentifier(layout.getPlid()));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private Layout _getLayout(SegmentsExperience segmentsExperience) {
		try {
			Layout layout = _layoutLocalService.getLayout(
				segmentsExperience.getPlid());

			if (layout.isDraftLayout()) {
				return layout;
			}

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout != null) {
				return draftLayout;
			}

			return layout;
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private volatile LayoutInfoItemPermissionProviderHelper
		_layoutInfoItemPermissionProviderHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private ModelResourcePermission<Layout> _layoutModelResourcePermission;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}