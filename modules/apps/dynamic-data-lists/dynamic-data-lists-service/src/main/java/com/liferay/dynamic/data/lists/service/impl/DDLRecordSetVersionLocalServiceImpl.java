/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.impl;

import com.liferay.dynamic.data.lists.exception.NoSuchRecordSetVersionException;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.service.base.DDLRecordSetVersionLocalServiceBaseImpl;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetVersionVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * Provides the local service for accessing, adding, deleting, and updating
 * dynamic data list (DDL) record set versions.
 *
 * @author Leonardo Barros
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.lists.model.DDLRecordSetVersion",
	service = AopService.class
)
public class DDLRecordSetVersionLocalServiceImpl
	extends DDLRecordSetVersionLocalServiceBaseImpl {

	@Override
	public void deleteByRecordSetId(long recordSetId) {
		ddlRecordSetVersionPersistence.removeByRecordSetId(recordSetId);
	}

	@Override
	public DDLRecordSetVersion getLatestRecordSetVersion(long recordSetId)
		throws PortalException {

		List<DDLRecordSetVersion> ddlRecordSetVersions =
			ddlRecordSetVersionPersistence.findByRecordSetId(recordSetId);

		if (ddlRecordSetVersions.isEmpty()) {
			throw new NoSuchRecordSetVersionException(
				"No record set versions found for record set ID " +
					recordSetId);
		}

		ddlRecordSetVersions = ListUtil.copy(ddlRecordSetVersions);

		Collections.sort(
			ddlRecordSetVersions, new DDLRecordSetVersionVersionComparator());

		return ddlRecordSetVersions.get(0);
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion(
			long recordSetId, String version)
		throws PortalException {

		return ddlRecordSetVersionPersistence.findByRS_V(recordSetId, version);
	}

}