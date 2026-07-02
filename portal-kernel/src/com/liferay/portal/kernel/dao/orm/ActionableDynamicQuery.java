/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.BaseLocalService;

/**
 * @author Brian Wing Shun Chan
 */
public interface ActionableDynamicQuery {

	public AddCriteriaMethod getAddCriteriaMethod();

	public AddOrderCriteriaMethod getAddOrderCriteriaMethod();

	public PerformActionMethod<?> getPerformActionMethod();

	public PerformCountMethod getPerformCountMethod();

	public boolean isParallel();

	public void performActions() throws PortalException;

	public long performCount() throws PortalException;

	public void setAddCriteriaMethod(AddCriteriaMethod addCriteriaMethod);

	public void setAddOrderCriteriaMethod(
		AddOrderCriteriaMethod addOrderCriteriaMethod);

	public void setBaseLocalService(BaseLocalService baseLocalService);

	public void setClassLoader(ClassLoader classLoader);

	public void setCompanyId(long companyId);

	public void setGroupId(long groupId);

	public void setGroupIdPropertyName(String groupIdPropertyName);

	public void setInterval(int interval);

	public void setModelClass(Class<?> modelClass);

	public void setParallel(boolean parallel);

	public void setPerformActionMethod(
		PerformActionMethod<?> performActionMethod);

	public void setPerformCountMethod(PerformCountMethod performCountMethod);

	public void setPrimaryKeyPropertyName(String primaryKeyPropertyName);

	public interface AddCriteriaMethod {

		public void addCriteria(DynamicQuery dynamicQuery);

	}

	public interface AddOrderCriteriaMethod {

		public void addOrderCriteria(DynamicQuery dynamicQuery);

	}

	public interface PerformActionMethod<T> {

		public void performAction(T t) throws PortalException;

	}

	public interface PerformCountMethod {

		public long performCount() throws PortalException;

	}

}