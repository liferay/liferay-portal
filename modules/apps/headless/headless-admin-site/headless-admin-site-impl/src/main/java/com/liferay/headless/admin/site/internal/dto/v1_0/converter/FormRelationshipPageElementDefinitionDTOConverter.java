/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.FormRelationshipConfig;
import com.liferay.headless.admin.site.dto.v1_0.FormRelationshipPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ImageValueUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem"
	},
	service = DTOConverter.class
)
public class FormRelationshipPageElementDefinitionDTOConverter
	implements DTOConverter
		<FormRelationshipStyledLayoutStructureItem,
		 FormRelationshipPageElementDefinition> {

	@Override
	public String getContentType() {
		return FormRelationshipPageElementDefinition.class.getSimpleName();
	}

	@Override
	public FormRelationshipPageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			FormRelationshipStyledLayoutStructureItem
				formRelationshipStyledLayoutStructureItem)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		return new FormRelationshipPageElementDefinition() {
			{
				setBackgroundImageValue(
					() -> ImageValueUtil.toBackgroundImageValue(
						companyId, dtoConverterContext,
						_infoItemServiceRegistry,
						formRelationshipStyledLayoutStructureItem.
							getBackgroundImageJSONObject(),
						scopeGroupId));
				setCssClasses(
					() -> {
						if (SetUtil.isEmpty(
								formRelationshipStyledLayoutStructureItem.
									getCssClasses())) {

							return null;
						}

						return ArrayUtil.toStringArray(
							formRelationshipStyledLayoutStructureItem.
								getCssClasses());
					});
				setFormRelationshipConfig(
					() -> _toFormRelationshipConfig(
						formRelationshipStyledLayoutStructureItem));
				setFragmentViewports(
					() -> FragmentViewportUtil.toFragmentViewports(
						formRelationshipStyledLayoutStructureItem.
							getItemConfigJSONObject()));
				setIndexed(
					formRelationshipStyledLayoutStructureItem::isIndexed);
				setName(formRelationshipStyledLayoutStructureItem::getName);
				setRepeatable(
					formRelationshipStyledLayoutStructureItem::isRepeatable);
				setType(() -> PageElementDefinition.Type.FORM_RELATIONSHIP);
			}
		};
	}

	private FormRelationshipConfig _toFormRelationshipConfig(
		FormRelationshipStyledLayoutStructureItem
			formRelationshipStyledLayoutStructureItem) {

		FragmentInlineValue buttonLabelFragmentInlineValue =
			_toFragmentInlineValue(
				formRelationshipStyledLayoutStructureItem.
					getButtonLabelJSONObject());
		String contentType =
			formRelationshipStyledLayoutStructureItem.getContentType();

		if ((buttonLabelFragmentInlineValue == null) &&
			Validator.isBlank(contentType)) {

			return null;
		}

		FormRelationshipConfig formRelationshipConfig =
			new FormRelationshipConfig();

		formRelationshipConfig.setButtonLabelFragmentInlineValue(
			() -> buttonLabelFragmentInlineValue);
		formRelationshipConfig.setContentType(
			() -> {
				if (Validator.isBlank(contentType)) {
					return null;
				}

				return contentType;
			});

		return formRelationshipConfig;
	}

	private FragmentInlineValue _toFragmentInlineValue(JSONObject jsonObject) {
		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		FragmentInlineValue fragmentInlineValue = new FragmentInlineValue();

		fragmentInlineValue.setValue_i18n(
			() -> LocalizedValueUtil.toLocalizedValues(jsonObject));

		return fragmentInlineValue;
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}