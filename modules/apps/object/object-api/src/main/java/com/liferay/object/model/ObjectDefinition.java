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
 * The extended model interface for the ObjectDefinition service. Represents a row in the &quot;ObjectDefinition&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectDefinitionModel
 * @generated
 */
@ImplementationClassName("com.liferay.object.model.impl.ObjectDefinitionImpl")
@ProviderType
public interface ObjectDefinition
	extends ObjectDefinitionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectDefinitionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectDefinition, Long>
		OBJECT_DEFINITION_ID_ACCESSOR = new Accessor<ObjectDefinition, Long>() {

			@Override
			public Long get(ObjectDefinition objectDefinition) {
				return objectDefinition.getObjectDefinitionId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ObjectDefinition> getTypeClass() {
				return ObjectDefinition.class;
			}

		};

	public java.util.Locale getDefaultLocale();

	public String getDestinationName();

	public String getExtensionDBTableName();

	public String getLocalizationDBTableName();

	public java.util.List<ObjectDefinitionSetting>
		getObjectDefinitionSettings();

	public String getObjectFolderExternalReferenceCode();

	public String getOSGiJaxRsName();

	public String getOSGiJaxRsName(String className);

	public String getPortletId();

	public String getPreviousRESTContextPath();

	public String getResourceName();

	public String getRESTContextPath();

	public String getRootObjectDefinitionExternalReferenceCode();

	public String getShortName();

	public boolean isApproved();

	public boolean isDefaultStorageType();

	public boolean isLinkedToObjectFolder(long objectFolderId);

	public boolean isModifiableAndSystem();

	public boolean isNodeCandidate();

	public boolean isRootDescendantNode();

	public boolean isRootNode();

	public boolean isUnmodifiableSystemObject();

	public void setObjectDefinitionSettings(
		java.util.List<ObjectDefinitionSetting> objectDefinitionSettings);

	public void setPreviousRESTContextPath(String previousRESTContextPath);

}