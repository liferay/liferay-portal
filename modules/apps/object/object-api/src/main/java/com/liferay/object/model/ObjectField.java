/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ObjectField service. Represents a row in the &quot;ObjectField&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectFieldModel
 * @generated
 */
@ImplementationClassName("com.liferay.object.model.impl.ObjectFieldImpl")
@ProviderType
public interface ObjectField extends ObjectFieldModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectFieldImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectField, Long> OBJECT_FIELD_ID_ACCESSOR =
		new Accessor<ObjectField, Long>() {

			@Override
			public Long get(ObjectField objectField) {
				return objectField.getObjectFieldId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ObjectField> getTypeClass() {
				return ObjectField.class;
			}

		};

	public boolean compareBusinessType(String businessType);

	public String[] getDBColumnNames();

	public String getI18nObjectFieldName();

	public ObjectDefinition getObjectDefinition()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<ObjectFieldSetting> getObjectFieldSettings();

	public ObjectRelationship getObjectRelationship();

	public String getSortableDBColumnName();

	public boolean hasInsertValues();

	public boolean hasUniqueValues();

	public boolean hasUpdateValues();

	public boolean isDeletionAllowed()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isMetadata();

	public void setObjectFieldSettings(
		java.util.List<ObjectFieldSetting> objectFieldSettings);

}