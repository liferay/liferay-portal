/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.TreeModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ObjectEntry service. Represents a row in the &quot;ObjectEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectEntryModel
 * @generated
 */
@ImplementationClassName("com.liferay.object.model.impl.ObjectEntryImpl")
@ProviderType
public interface ObjectEntry
	extends ObjectEntryModel, PersistedModel, TreeModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectEntry, Long> OBJECT_ENTRY_ID_ACCESSOR =
		new Accessor<ObjectEntry, Long>() {

			@Override
			public Long get(ObjectEntry objectEntry) {
				return objectEntry.getObjectEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ObjectEntry> getTypeClass() {
				return ObjectEntry.class;
			}

		};

	@Override
	public String buildTreePath()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getModelClassName();

	public long getNonzeroGroupId()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.Map<java.util.Locale, String> getTitleMap()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getTitleValue()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getTitleValue(String languageId)
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getTitleValue(String languageId, boolean useDefault)
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getURLTitle(java.util.Locale locale);

	public java.util.Map<String, String> getURLTitleMap();

	public java.util.Map<String, java.io.Serializable> getValues();

	public boolean isHead();

	public boolean isRootDescendantNode();

	public void setTransientValues(
		java.util.Map<String, java.io.Serializable> values);

	public void setValues(java.util.Map<String, java.io.Serializable> values);

}