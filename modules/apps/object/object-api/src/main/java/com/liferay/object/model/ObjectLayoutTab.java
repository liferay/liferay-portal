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
 * The extended model interface for the ObjectLayoutTab service. Represents a row in the &quot;ObjectLayoutTab&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectLayoutTabModel
 * @generated
 */
@ImplementationClassName("com.liferay.object.model.impl.ObjectLayoutTabImpl")
@ProviderType
public interface ObjectLayoutTab extends ObjectLayoutTabModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectLayoutTabImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectLayoutTab, Long>
		OBJECT_LAYOUT_TAB_ID_ACCESSOR = new Accessor<ObjectLayoutTab, Long>() {

			@Override
			public Long get(ObjectLayoutTab objectLayoutTab) {
				return objectLayoutTab.getObjectLayoutTabId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ObjectLayoutTab> getTypeClass() {
				return ObjectLayoutTab.class;
			}

		};

	public java.util.List<ObjectLayoutBox> getObjectLayoutBoxes();

	public ObjectRelationship getObjectRelationship()
		throws com.liferay.portal.kernel.exception.PortalException;

	public void setObjectLayoutBoxes(
		java.util.List<ObjectLayoutBox> objectLayoutBoxes);

}