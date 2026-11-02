/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.service.base.WebDAVPropsLocalServiceBaseImpl;

import java.util.Date;

/**
 * @author Alexander Chow
 */
public class WebDAVPropsLocalServiceImpl
	extends WebDAVPropsLocalServiceBaseImpl {

	@Override
	public WebDAVProps getWebDAVProps(
		long companyId, String className, long classPK) {

		long classNameId = _classNameLocalService.getClassNameId(className);

		WebDAVProps webDAVProps = webDAVPropsPersistence.fetchByC_C(
			classNameId, classPK);

		if (webDAVProps == null) {
			webDAVProps = webDAVPropsPersistence.create(
				counterLocalService.increment());

			Date date = new Date();

			webDAVProps.setCompanyId(companyId);
			webDAVProps.setCreateDate(date);
			webDAVProps.setModifiedDate(date);
			webDAVProps.setClassNameId(classNameId);
			webDAVProps.setClassPK(classPK);

			webDAVProps = webDAVPropsLocalService.updateWebDAVProps(
				webDAVProps);
		}

		return webDAVProps;
	}

	@Override
	public void storeWebDAVProps(WebDAVProps webDAVProps)
		throws PortalException {

		try {
			webDAVProps.store();
		}
		catch (Exception exception) {
			throw new WebDAVException(
				"Problem trying to store WebDAVProps", exception);
		}

		webDAVPropsPersistence.update(webDAVProps);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}