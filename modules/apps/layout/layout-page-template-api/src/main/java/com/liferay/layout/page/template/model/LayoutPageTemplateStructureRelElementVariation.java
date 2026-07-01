/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutPageTemplateStructureRelElementVariation service. Represents a row in the &quot;LPTSRelElementVariation&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationImpl"
)
@ProviderType
public interface LayoutPageTemplateStructureRelElementVariation
	extends LayoutPageTemplateStructureRelElementVariationModel,
			PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor
		<LayoutPageTemplateStructureRelElementVariation, Long>
			LAYOUT_PAGE_TEMPLATE_STRUCTURE_REL_ELEMENT_VARIATION_ID_ACCESSOR =
				new Accessor
					<LayoutPageTemplateStructureRelElementVariation, Long>() {

					@Override
					public Long get(
						LayoutPageTemplateStructureRelElementVariation
							layoutPageTemplateStructureRelElementVariation) {

						return layoutPageTemplateStructureRelElementVariation.
							getLayoutPageTemplateStructureRelElementVariationId();
					}

					@Override
					public Class<Long> getAttributeClass() {
						return Long.class;
					}

					@Override
					public Class<LayoutPageTemplateStructureRelElementVariation>
						getTypeClass() {

						return LayoutPageTemplateStructureRelElementVariation.
							class;
					}

				};

	public java.util.List<String> getAudienceEntryERCs();

}
// LIFERAY-SERVICE-BUILDER-HASH:1213896382