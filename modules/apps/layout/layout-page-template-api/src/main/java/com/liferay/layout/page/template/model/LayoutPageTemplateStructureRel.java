/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutPageTemplateStructureRel service. Represents a row in the &quot;LayoutPageTemplateStructureRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelImpl"
)
@ProviderType
public interface LayoutPageTemplateStructureRel
	extends LayoutPageTemplateStructureRelModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<LayoutPageTemplateStructureRel, Long>
		LAYOUT_PAGE_TEMPLATE_STRUCTURE_REL_ID_ACCESSOR =
			new Accessor<LayoutPageTemplateStructureRel, Long>() {

				@Override
				public Long get(
					LayoutPageTemplateStructureRel
						layoutPageTemplateStructureRel) {

					return layoutPageTemplateStructureRel.
						getLayoutPageTemplateStructureRelId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<LayoutPageTemplateStructureRel> getTypeClass() {
					return LayoutPageTemplateStructureRel.class;
				}

			};

	public com.liferay.portal.kernel.json.JSONObject getDataJSONObject();

}