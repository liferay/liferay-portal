/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.model.impl;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalServiceUtil;
import com.liferay.dynamic.data.lists.service.DDLRecordSetVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DDLRecordSetImpl extends DDLRecordSetBaseImpl {

	@Override
	public DDMStructure getDDMStructure() throws PortalException {
		return DDMStructureLocalServiceUtil.getStructure(getDDMStructureId());
	}

	@Override
	public DDMStructure getDDMStructure(long formDDMTemplateId)
		throws PortalException {

		DDMStructure ddmStructure = getDDMStructure();

		if (formDDMTemplateId > 0) {
			DDMTemplate ddmTemplate =
				DDMTemplateLocalServiceUtil.fetchDDMTemplate(formDDMTemplateId);

			if (ddmTemplate != null) {

				// Clone ddmStructure to make sure changes are never persisted

				ddmStructure = (DDMStructure)ddmStructure.clone();

				ddmStructure.setDefinition(ddmTemplate.getScript());

				// Purposely lower MVCC version to avoid entity cache corruption
				// when DDMStructureImpl#getDDMForm is invoked for
				// reconstructing the DDMForm cache field

				ddmStructure.setMvccVersion(ddmStructure.getMvccVersion() - 1);
			}
		}

		return ddmStructure;
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion() throws PortalException {
		return getRecordSetVersion(getVersion());
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion(String version)
		throws PortalException {

		return DDLRecordSetVersionLocalServiceUtil.getRecordSetVersion(
			getRecordSetId(), version);
	}

	@Override
	public List<DDLRecord> getRecords() {
		return DDLRecordLocalServiceUtil.getRecords(getRecordSetId());
	}

}