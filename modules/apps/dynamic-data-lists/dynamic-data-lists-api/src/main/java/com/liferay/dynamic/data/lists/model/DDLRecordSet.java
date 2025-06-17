/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the DDLRecordSet service. Represents a row in the &quot;DDLRecordSet&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordSetModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.dynamic.data.lists.model.impl.DDLRecordSetImpl"
)
@ProviderType
public interface DDLRecordSet extends DDLRecordSetModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<DDLRecordSet, Long> RECORD_SET_ID_ACCESSOR =
		new Accessor<DDLRecordSet, Long>() {

			@Override
			public Long get(DDLRecordSet ddlRecordSet) {
				return ddlRecordSet.getRecordSetId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<DDLRecordSet> getTypeClass() {
				return DDLRecordSet.class;
			}

		};

	public com.liferay.dynamic.data.mapping.model.DDMStructure getDDMStructure()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.dynamic.data.mapping.model.DDMStructure getDDMStructure(
			long formDDMTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<DDLRecord> getRecords();

	public DDLRecordSetVersion getRecordSetVersion()
		throws com.liferay.portal.kernel.exception.PortalException;

	public DDLRecordSetVersion getRecordSetVersion(String version)
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getSettingsDDMFormValues();

	public DDLRecordSetSettings getSettingsModel()
		throws com.liferay.portal.kernel.exception.PortalException;

	public void setSettingsDDMFormValues(
		com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues);

}