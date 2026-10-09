/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.WebsiteURLException;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.url.validator.URLValidator;
import com.liferay.portal.service.base.WebsiteLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class WebsiteLocalServiceImpl extends WebsiteLocalServiceBaseImpl {

	@Override
	public Website addWebsite(
			String externalReferenceCode, long userId, String className,
			long classPK, String url, long listTypeId, boolean primary,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);
		long classNameId = _classNameLocalService.getClassNameId(className);

		validate(
			0, user.getCompanyId(), classNameId, classPK, url, listTypeId,
			primary);

		long websiteId = counterLocalService.increment();

		Website website = websitePersistence.create(websiteId);

		website.setUuid(serviceContext.getUuid());
		website.setExternalReferenceCode(externalReferenceCode);
		website.setCompanyId(user.getCompanyId());
		website.setUserId(user.getUserId());
		website.setUserName(user.getFullName());
		website.setClassNameId(classNameId);
		website.setClassPK(classPK);
		website.setUrl(url);
		website.setListTypeId(listTypeId);
		website.setPrimary(primary);

		return websitePersistence.update(website);
	}

	@Override
	public Website deleteWebsite(long websiteId) throws PortalException {
		Website website = websitePersistence.findByPrimaryKey(websiteId);

		return websiteLocalService.deleteWebsite(website);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public Website deleteWebsite(Website website) {
		websitePersistence.remove(website);

		return website;
	}

	@Override
	public void deleteWebsites(long companyId, String className, long classPK) {
		List<Website> websites = websitePersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);

		for (Website website : websites) {
			websiteLocalService.deleteWebsite(website);
		}
	}

	@Override
	public List<Website> getWebsites() {
		return websitePersistence.findAll();
	}

	@Override
	public List<Website> getWebsites(
		long companyId, String className, long classPK) {

		return websitePersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	@Override
	public Website updateWebsite(
			String externalReferenceCode, long websiteId, String url,
			long listTypeId, boolean primary)
		throws PortalException {

		validate(websiteId, 0, 0, 0, url, listTypeId, primary);

		Website website = websitePersistence.findByPrimaryKey(websiteId);

		website.setExternalReferenceCode(externalReferenceCode);
		website.setUrl(url);
		website.setListTypeId(listTypeId);
		website.setPrimary(primary);

		return websitePersistence.update(website);
	}

	protected void validate(
		long websiteId, long companyId, long classNameId, long classPK,
		boolean primary) {

		// Check to make sure there isn't another website with the same company
		// id, class name, and class pk that also has primary set to true

		if (primary) {
			List<Website> websites = websitePersistence.findByC_C_C_P(
				companyId, classNameId, classPK, primary);

			for (Website website : websites) {
				if ((websiteId > 0) && (website.getWebsiteId() == websiteId)) {
					continue;
				}

				website.setPrimary(false);

				websitePersistence.update(website);
			}
		}
	}

	protected void validate(
			long websiteId, long companyId, long classNameId, long classPK,
			String url, long listTypeId, boolean primary)
		throws PortalException {

		URLValidator urlValidator = _urlValidatorSnapshot.get();

		if (!urlValidator.isValid(url)) {
			throw new WebsiteURLException(url);
		}

		if (websiteId > 0) {
			Website website = websitePersistence.findByPrimaryKey(websiteId);

			companyId = website.getCompanyId();
			classNameId = website.getClassNameId();
			classPK = website.getClassPK();
		}

		_listTypeLocalService.validate(
			listTypeId, classNameId, ListTypeConstants.WEBSITE);

		validate(websiteId, companyId, classNameId, classPK, primary);
	}

	private static final Snapshot<URLValidator> _urlValidatorSnapshot =
		new Snapshot<>(WebsiteLocalServiceImpl.class, URLValidator.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ListTypeLocalService.class)
	private ListTypeLocalService _listTypeLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}