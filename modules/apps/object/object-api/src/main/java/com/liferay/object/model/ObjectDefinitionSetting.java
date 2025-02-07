/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ObjectDefinitionSetting service. Represents a row in the &quot;ObjectDefinitionSetting&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectDefinitionSettingModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.object.model.impl.ObjectDefinitionSettingImpl"
)
@ProviderType
public interface ObjectDefinitionSetting
	extends ObjectDefinitionSettingModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectDefinitionSettingImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectDefinitionSetting, Long>
		OBJECT_DEFINITION_SETTING_ID_ACCESSOR =
			new Accessor<ObjectDefinitionSetting, Long>() {

				@Override
				public Long get(
					ObjectDefinitionSetting objectDefinitionSetting) {

					return objectDefinitionSetting.
						getObjectDefinitionSettingId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<ObjectDefinitionSetting> getTypeClass() {
					return ObjectDefinitionSetting.class;
				}

			};

}