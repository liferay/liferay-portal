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
 * The extended model interface for the DDMStructureLayout service. Represents a row in the &quot;DDMStructureLayout&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLayoutModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutImpl"
)
@ProviderType
public interface DDMStructureLayout
	extends DDMStructureLayoutModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<DDMStructureLayout, Long>
		STRUCTURE_LAYOUT_ID_ACCESSOR =
			new Accessor<DDMStructureLayout, Long>() {

				@Override
				public Long get(DDMStructureLayout ddmStructureLayout) {
					return ddmStructureLayout.getStructureLayoutId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<DDMStructureLayout> getTypeClass() {
					return DDMStructureLayout.class;
				}

			};

	public DDMFormLayout getDDMFormLayout();

	public DDMStructure getDDMStructure()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long getDDMStructureId()
		throws com.liferay.portal.kernel.exception.PortalException;

	public void setDDMFormLayout(DDMFormLayout ddmFormLayout);

}