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
 * The extended model interface for the KaleoDefinitionVersion service. Represents a row in the &quot;KaleoDefinitionVersion&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionVersionModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionImpl"
)
@ProviderType
public interface KaleoDefinitionVersion
	extends KaleoDefinitionVersionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<KaleoDefinitionVersion, Long>
		KALEO_DEFINITION_VERSION_ID_ACCESSOR =
			new Accessor<KaleoDefinitionVersion, Long>() {

				@Override
				public Long get(KaleoDefinitionVersion kaleoDefinitionVersion) {
					return kaleoDefinitionVersion.getKaleoDefinitionVersionId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<KaleoDefinitionVersion> getTypeClass() {
					return KaleoDefinitionVersion.class;
				}

			};

	public String getContentAsXML();

	public KaleoDefinition getKaleoDefinition()
		throws com.liferay.portal.kernel.exception.PortalException;

	public KaleoNode getKaleoStartNode()
		throws com.liferay.portal.kernel.exception.PortalException;

	public void setContentAsXML(String contentAsXML);

}