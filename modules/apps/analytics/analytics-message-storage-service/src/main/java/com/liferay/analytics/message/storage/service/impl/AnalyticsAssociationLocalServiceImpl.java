/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.impl;

import com.liferay.analytics.message.storage.model.AnalyticsAssociation;
import com.liferay.analytics.message.storage.service.base.AnalyticsAssociationLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.analytics.message.storage.model.AnalyticsAssociation",
	service = AopService.class
)
public class AnalyticsAssociationLocalServiceImpl
	extends AnalyticsAssociationLocalServiceBaseImpl {

	@Override
	public AnalyticsAssociation addAnalyticsAssociation(
		long companyId, Date createDate, long userId,
		String associationClassName, long associationClassPK, String className,
		long classPK) {

		AnalyticsAssociation analyticsAssociation =
			analyticsAssociationPersistence.create(
				counterLocalService.increment());

		analyticsAssociation.setCompanyId(companyId);
		analyticsAssociation.setCreateDate(createDate);
		analyticsAssociation.setModifiedDate(createDate);
		analyticsAssociation.setUserId(userId);
		analyticsAssociation.setAssociationClassName(associationClassName);
		analyticsAssociation.setAssociationClassPK(associationClassPK);
		analyticsAssociation.setClassName(className);
		analyticsAssociation.setClassPK(classPK);

		return analyticsAssociationPersistence.update(analyticsAssociation);
	}

	@Override
	public void deleteAnalyticsAssociations(long companyId) {
		analyticsAssociationPersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteAnalyticsAssociations(long companyId, Date modifiedDate) {
		analyticsAssociationPersistence.removeByC_LtM(companyId, modifiedDate);
	}

	@Override
	public void deleteAnalyticsAssociations(
		long companyId, String associationClassName, long associationClassPK) {

		analyticsAssociationPersistence.removeByC_A_A(
			companyId, associationClassName, associationClassPK);
	}

	@Override
	public List<AnalyticsAssociation> getAnalyticsAssociations(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end) {

		return analyticsAssociationPersistence.findByC_GtM_A(
			companyId, modifiedDate, associationClassName, start, end);
	}

	@Override
	public int getAnalyticsAssociationsCount(
		long companyId, Date modifiedDate, String associationClassName) {

		return analyticsAssociationPersistence.countByC_GtM_A(
			companyId, modifiedDate, associationClassName);
	}

}