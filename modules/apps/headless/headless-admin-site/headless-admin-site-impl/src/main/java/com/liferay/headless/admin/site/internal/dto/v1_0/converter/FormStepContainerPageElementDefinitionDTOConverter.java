/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ImageValueUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem"
	},
	service = DTOConverter.class
)
public class FormStepContainerPageElementDefinitionDTOConverter
	implements DTOConverter
		<FormStepContainerStyledLayoutStructureItem,
		 FormStepContainerPageElementDefinition> {

	@Override
	public String getContentType() {
		return FormStepContainerPageElementDefinition.class.getSimpleName();
	}

	@Override
	public FormStepContainerPageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			FormStepContainerStyledLayoutStructureItem
				formStepContainerStyledLayoutStructureItem)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		return new FormStepContainerPageElementDefinition() {
			{
				setBackgroundImageValue(
					() -> ImageValueUtil.toBackgroundImageValue(
						companyId, dtoConverterContext,
						_infoItemServiceRegistry,
						formStepContainerStyledLayoutStructureItem.
							getBackgroundImageJSONObject(),
						scopeGroupId));
				setCssClasses(
					() -> {
						if (SetUtil.isEmpty(
								formStepContainerStyledLayoutStructureItem.
									getCssClasses())) {

							return null;
						}

						return ArrayUtil.toStringArray(
							formStepContainerStyledLayoutStructureItem.
								getCssClasses());
					});
				setFragmentViewports(
					() -> FragmentViewportUtil.toFragmentViewports(
						formStepContainerStyledLayoutStructureItem.
							getItemConfigJSONObject()));
				setType(PageElementDefinition.Type.FORM_STEP_CONTAINER);
			}
		};
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}