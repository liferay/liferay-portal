/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

/**
 * Provides a wrapper for {@link LayoutRevisionService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutRevisionService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutRevisionServiceWrapper
	implements LayoutRevisionService, ServiceWrapper<LayoutRevisionService> {

	public LayoutRevisionServiceWrapper() {
		this(null);
	}

	public LayoutRevisionServiceWrapper(
		LayoutRevisionService layoutRevisionService) {

		_layoutRevisionService = layoutRevisionService;
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutRevision addLayoutRevision(
			long userId, long layoutSetBranchId, long layoutBranchId,
			long parentLayoutRevisionId, boolean head, long plid,
			long portletPreferencesPlid, boolean privateLayout,
			java.lang.String name, java.lang.String title,
			java.lang.String description, java.lang.String keywords,
			java.lang.String robots, java.lang.String typeSettings,
			boolean iconImage, long iconImageId, java.lang.String themeId,
			java.lang.String colorSchemeId, java.lang.String css,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutRevisionService.addLayoutRevision(
			userId, layoutSetBranchId, layoutBranchId, parentLayoutRevisionId,
			head, plid, portletPreferencesPlid, privateLayout, name, title,
			description, keywords, robots, typeSettings, iconImage, iconImageId,
			themeId, colorSchemeId, css, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _layoutRevisionService.getOSGiServiceIdentifier();
	}

	@Override
	public LayoutRevisionService getWrappedService() {
		return _layoutRevisionService;
	}

	@Override
	public void setWrappedService(LayoutRevisionService layoutRevisionService) {
		_layoutRevisionService = layoutRevisionService;
	}

	private LayoutRevisionService _layoutRevisionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1664871768