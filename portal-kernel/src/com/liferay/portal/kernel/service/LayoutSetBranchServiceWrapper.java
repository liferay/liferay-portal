/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

/**
 * Provides a wrapper for {@link LayoutSetBranchService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutSetBranchServiceWrapper
	implements LayoutSetBranchService, ServiceWrapper<LayoutSetBranchService> {

	public LayoutSetBranchServiceWrapper() {
		this(null);
	}

	public LayoutSetBranchServiceWrapper(
		LayoutSetBranchService layoutSetBranchService) {

		_layoutSetBranchService = layoutSetBranchService;
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch addLayoutSetBranch(
			long groupId, boolean privateLayout, java.lang.String name,
			java.lang.String description, boolean master,
			long copyLayoutSetBranchId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchService.addLayoutSetBranch(
			groupId, privateLayout, name, description, master,
			copyLayoutSetBranchId, serviceContext);
	}

	@Override
	public void deleteLayoutSetBranch(long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetBranchService.deleteLayoutSetBranch(layoutSetBranchId);
	}

	@Override
	public void deleteLayoutSetBranch(
			long currentLayoutPlid, long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetBranchService.deleteLayoutSetBranch(
			currentLayoutPlid, layoutSetBranchId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutSetBranch>
		getLayoutSetBranches(long groupId, boolean privateLayout) {

		return _layoutSetBranchService.getLayoutSetBranches(
			groupId, privateLayout);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _layoutSetBranchService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch mergeLayoutSetBranch(
			long layoutSetBranchId, long mergeLayoutSetBranchId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchService.mergeLayoutSetBranch(
			layoutSetBranchId, mergeLayoutSetBranchId, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			updateLayoutSetBranch(
				long groupId, long layoutSetBranchId, java.lang.String name,
				java.lang.String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchService.updateLayoutSetBranch(
			groupId, layoutSetBranchId, name, description, serviceContext);
	}

	@Override
	public LayoutSetBranchService getWrappedService() {
		return _layoutSetBranchService;
	}

	@Override
	public void setWrappedService(
		LayoutSetBranchService layoutSetBranchService) {

		_layoutSetBranchService = layoutSetBranchService;
	}

	private LayoutSetBranchService _layoutSetBranchService;

}
// LIFERAY-SERVICE-BUILDER-HASH:246094601