/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutContentVersionPreview service. Represents a row in the &quot;LayoutContentVersionPreview&quot; database table, with each column mapped to a property of this class.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreviewModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.layout.content.model.impl.LayoutContentVersionPreviewImpl"
)
@ProviderType
public interface LayoutContentVersionPreview
	extends LayoutContentVersionPreviewModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<LayoutContentVersionPreview, Long>
		LAYOUT_CONTENT_VERSION_PREVIEW_ID_ACCESSOR =
			new Accessor<LayoutContentVersionPreview, Long>() {

				@Override
				public Long get(
					LayoutContentVersionPreview layoutContentVersionPreview) {

					return layoutContentVersionPreview.
						getLayoutContentVersionPreviewId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<LayoutContentVersionPreview> getTypeClass() {
					return LayoutContentVersionPreview.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-423190205