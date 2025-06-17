/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the KaleoDefinition service. Represents a row in the &quot;KaleoDefinition&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionImpl"
)
@ProviderType
public interface KaleoDefinition extends KaleoDefinitionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<KaleoDefinition, Long>
		KALEO_DEFINITION_ID_ACCESSOR = new Accessor<KaleoDefinition, Long>() {

			@Override
			public Long get(KaleoDefinition kaleoDefinition) {
				return kaleoDefinition.getKaleoDefinitionId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<KaleoDefinition> getTypeClass() {
				return KaleoDefinition.class;
			}

		};

	public String getContentAsXML();

	public java.util.List<KaleoDefinitionVersion> getKaleoDefinitionVersions()
		throws com.liferay.portal.kernel.exception.PortalException;

	public void setContentAsXML(String contentAsXML);

}