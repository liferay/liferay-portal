/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the DDMFormInstance service. Represents a row in the &quot;DDMFormInstance&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl"
)
@ProviderType
public interface DDMFormInstance extends DDMFormInstanceModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<DDMFormInstance, Long>
		FORM_INSTANCE_ID_ACCESSOR = new Accessor<DDMFormInstance, Long>() {

			@Override
			public Long get(DDMFormInstance ddmFormInstance) {
				return ddmFormInstance.getFormInstanceId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<DDMFormInstance> getTypeClass() {
				return DDMFormInstance.class;
			}

		};

	public DDMForm getDDMForm()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<DDMFormInstanceRecord> getFormInstanceRecords();

	public DDMFormInstanceVersion getFormInstanceVersion(String version)
		throws com.liferay.portal.kernel.exception.PortalException;

	public long getObjectDefinitionId()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getSettingsDDMFormValues();

	public DDMFormInstanceSettings getSettingsModel()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getStorageType()
		throws com.liferay.portal.kernel.exception.PortalException;

	public DDMStructure getStructure()
		throws com.liferay.portal.kernel.exception.PortalException;

	public void setSettingsDDMFormValues(
		com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues);

}