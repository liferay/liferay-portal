/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.impl;

import com.liferay.commerce.tax.exception.CommerceTaxMethodEngineKeyException;
import com.liferay.commerce.tax.exception.CommerceTaxMethodNameException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.base.CommerceTaxMethodLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.tax.model.CommerceTaxMethod",
	service = AopService.class
)
public class CommerceTaxMethodLocalServiceImpl
	extends CommerceTaxMethodLocalServiceBaseImpl {

	@Override
	public CommerceTaxMethod addCommerceTaxMethod(
			long userId, long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String engineKey,
			boolean percentage, boolean active)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(nameMap, engineKey);

		long commerceTaxMethodId = counterLocalService.increment();

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.create(commerceTaxMethodId);

		commerceTaxMethod.setGroupId(groupId);
		commerceTaxMethod.setCompanyId(user.getCompanyId());
		commerceTaxMethod.setUserId(user.getUserId());
		commerceTaxMethod.setUserName(user.getFullName());
		commerceTaxMethod.setNameMap(nameMap);
		commerceTaxMethod.setDescriptionMap(descriptionMap);
		commerceTaxMethod.setEngineKey(engineKey);
		commerceTaxMethod.setPercentage(percentage);
		commerceTaxMethod.setActive(active);

		return commerceTaxMethodPersistence.update(commerceTaxMethod);
	}

	@Override
	public void deleteCommerceTaxMethods(long groupId) throws PortalException {
		List<CommerceTaxMethod> commerceTaxMethods =
			commerceTaxMethodPersistence.findByGroupId(groupId);

		for (CommerceTaxMethod commerceTaxMethod : commerceTaxMethods) {
			commerceTaxMethodLocalService.deleteCommerceTaxMethod(
				commerceTaxMethod);
		}
	}

	@Override
	public CommerceTaxMethod fetchCommerceTaxMethod(
		long groupId, String engineKey) {

		return commerceTaxMethodPersistence.fetchByG_E(groupId, engineKey);
	}

	@Override
	public List<CommerceTaxMethod> getCommerceTaxMethods(
		long groupId, boolean active) {

		return commerceTaxMethodPersistence.findByG_A(groupId, active);
	}

	@Override
	public CommerceTaxMethod setActive(long commerceTaxMethodId, boolean active)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.findByPrimaryKey(commerceTaxMethodId);

		commerceTaxMethod.setActive(active);

		return commerceTaxMethodPersistence.update(commerceTaxMethod);
	}

	@Override
	public CommerceTaxMethod updateCommerceTaxMethod(
			long commerceTaxMethodId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean percentage,
			boolean active)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			commerceTaxMethodPersistence.findByPrimaryKey(commerceTaxMethodId);

		_validate(nameMap, commerceTaxMethod.getEngineKey());

		commerceTaxMethod.setNameMap(nameMap);
		commerceTaxMethod.setDescriptionMap(descriptionMap);
		commerceTaxMethod.setPercentage(percentage);
		commerceTaxMethod.setActive(active);

		return commerceTaxMethodPersistence.update(commerceTaxMethod);
	}

	private void _validate(Map<Locale, String> nameMap, String engineKey)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		String name = nameMap.get(locale);

		if (Validator.isNull(name)) {
			throw new CommerceTaxMethodNameException();
		}

		if (Validator.isNull(engineKey)) {
			throw new CommerceTaxMethodEngineKeyException();
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}