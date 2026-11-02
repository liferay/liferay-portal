/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceVersionLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.util.comparator.FormInstanceVersionVersionComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion",
	service = AopService.class
)
public class DDMFormInstanceVersionLocalServiceImpl
	extends DDMFormInstanceVersionLocalServiceBaseImpl {

	@Override
	public void deleteByFormInstanceId(long ddmFormInstanceId) {
		ddmFormInstanceVersionPersistence.removeByFormInstanceId(
			ddmFormInstanceId);
	}

	@Override
	public DDMFormInstanceVersion getFormInstanceVersion(
			long ddmFormInstanceId, String version)
		throws PortalException {

		return ddmFormInstanceVersionPersistence.findByF_V(
			ddmFormInstanceId, version);
	}

	@Override
	public List<DDMFormInstanceVersion> getFormInstanceVersions(
		long ddmFormInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceVersion> orderByComparator) {

		return ddmFormInstanceVersionPersistence.findByFormInstanceId(
			ddmFormInstanceId, start, end, orderByComparator);
	}

	@Override
	public DDMFormInstanceVersion getLatestFormInstanceVersion(
			long ddmFormInstanceId)
		throws PortalException {

		List<DDMFormInstanceVersion> ddmFormInstanceVersions =
			ddmFormInstanceVersionPersistence.findByFormInstanceId(
				ddmFormInstanceId);

		if (ddmFormInstanceVersions.isEmpty()) {
			throw new NoSuchFormInstanceVersionException(
				"No form instance versions found for form instance ID " +
					ddmFormInstanceId);
		}

		ddmFormInstanceVersions = ListUtil.copy(ddmFormInstanceVersions);

		Collections.sort(
			ddmFormInstanceVersions,
			new FormInstanceVersionVersionComparator());

		return ddmFormInstanceVersions.get(0);
	}

	@Override
	public DDMFormInstanceVersion getLatestFormInstanceVersion(
			long formInstanceId, int status)
		throws PortalException {

		List<DDMFormInstanceVersion> formInstanceVersions =
			ddmFormInstanceVersionPersistence.findByF_S(formInstanceId, status);

		if (formInstanceVersions.isEmpty()) {
			throw new NoSuchFormInstanceVersionException(
				StringBundler.concat(
					"No form instance versions found for form instance ID ",
					formInstanceId, " with status ", status));
		}

		formInstanceVersions = ListUtil.copy(formInstanceVersions);

		Collections.sort(
			formInstanceVersions, new FormInstanceVersionVersionComparator());

		return formInstanceVersions.get(0);
	}

}