/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.cache.CacheField;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutPageTemplateStructureRelElementVariationImpl
	extends LayoutPageTemplateStructureRelElementVariationBaseImpl {

	@Override
	public List<String> getAudienceEntryERCs() {
		if (_audienceEntryERCs == null) {
			_audienceEntryERCs = TransformUtil.transform(
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceUtil.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
						getExternalReferenceCode()),
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getAudienceEntryERC);
		}

		return _audienceEntryERCs;
	}

	@CacheField(permanent = true, propagateToInterface = true)
	private transient List<String> _audienceEntryERCs;

}
