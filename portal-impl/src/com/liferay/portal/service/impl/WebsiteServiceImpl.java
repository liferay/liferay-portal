/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.service.base.WebsiteServiceBaseImpl;
import com.liferay.portal.service.permission.CommonPermissionUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class WebsiteServiceImpl extends WebsiteServiceBaseImpl {

	@Override
	public Website addWebsite(
			String externalReferenceCode, String className, long classPK,
			String url, long typeId, boolean primary,
			ServiceContext serviceContext)
		throws PortalException {

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				className, "com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, actionId);

		return websiteLocalService.addWebsite(
			externalReferenceCode, getUserId(), className, classPK, url, typeId,
			primary, serviceContext);
	}

	@Override
	public void deleteWebsite(long websiteId) throws PortalException {
		Website website = websitePersistence.findByPrimaryKey(websiteId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				website.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), website.getClassNameId(),
			website.getClassPK(), actionId);

		websiteLocalService.deleteWebsite(website);
	}

	@Override
	public Website fetchWebsiteByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Website website = websitePersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (website != null) {
			CommonPermissionUtil.check(
				getPermissionChecker(), website.getClassNameId(),
				website.getClassPK(), ActionKeys.VIEW);
		}

		return website;
	}

	@Override
	public Website getWebsite(long websiteId) throws PortalException {
		Website website = websitePersistence.findByPrimaryKey(websiteId);

		CommonPermissionUtil.check(
			getPermissionChecker(), website.getClassNameId(),
			website.getClassPK(), ActionKeys.VIEW);

		return website;
	}

	@Override
	public List<Website> getWebsites(String className, long classPK)
		throws PortalException {

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, ActionKeys.VIEW);

		User user = getUser();

		return websitePersistence.findByC_C_C(
			user.getCompanyId(),
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public Website updateWebsite(
			String externalReferenceCode, long websiteId, String url,
			long typeId, boolean primary)
		throws PortalException {

		Website website = websitePersistence.findByPrimaryKey(websiteId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				website.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), website.getClassNameId(),
			website.getClassPK(), actionId);

		return websiteLocalService.updateWebsite(
			externalReferenceCode, websiteId, url, typeId, primary);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}