/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.mapper;

import com.liferay.headless.delivery.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.PageFormRelationshipDefinition;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.util.LocalizedValueUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.util.StyledLayoutStructureItemUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Portal;

/**
 * @author Víctor Galán
 */
public class FormRelationshipLayoutStructureItemMapper
	extends BaseStyledLayoutStructureItemMapper {

	public FormRelationshipLayoutStructureItemMapper(
		InfoItemServiceRegistry infoItemServiceRegistry, Portal portal) {

		super(infoItemServiceRegistry, portal);
	}

	@Override
	public PageElement getPageElement(
		long groupId, LayoutStructureItem layoutStructureItem,
		boolean saveInlineContent, boolean saveMappingConfiguration) {

		FormRelationshipStyledLayoutStructureItem
			formRelationshipStyledLayoutStructureItem =
				(FormRelationshipStyledLayoutStructureItem)layoutStructureItem;

		return new PageElement() {
			{
				setDefinition(
					() -> new PageFormRelationshipDefinition() {
						{
							setButtonLabel(
								() -> {
									JSONObject buttonLabelJSONObject =
										formRelationshipStyledLayoutStructureItem.
											getButtonLabelJSONObject();

									if (buttonLabelJSONObject == null) {
										return null;
									}

									return new FragmentInlineValue() {
										{
											setValue_i18n(
												() ->
													LocalizedValueUtil.
														toLocalizedValues(
															buttonLabelJSONObject));
										}
									};
								});
							setContentType(
								formRelationshipStyledLayoutStructureItem::
									getContentType);
							setCssClasses(
								() ->
									StyledLayoutStructureItemUtil.getCssClasses(
										formRelationshipStyledLayoutStructureItem));
							setCustomCSS(
								() ->
									StyledLayoutStructureItemUtil.getCustomCSS(
										formRelationshipStyledLayoutStructureItem));
							setCustomCSSViewports(
								() ->
									StyledLayoutStructureItemUtil.
										getCustomCSSViewports(
											formRelationshipStyledLayoutStructureItem));
							setFragmentStyle(
								() -> {
									JSONObject itemConfigJSONObject =
										formRelationshipStyledLayoutStructureItem.
											getItemConfigJSONObject();

									return toFragmentStyle(
										itemConfigJSONObject.getJSONObject(
											"styles"),
										saveMappingConfiguration);
								});
							setFragmentViewports(
								() -> getFragmentViewPorts(
									formRelationshipStyledLayoutStructureItem.
										getItemConfigJSONObject()));
							setName(
								formRelationshipStyledLayoutStructureItem::
									getName);
						}
					});
				setId(layoutStructureItem::getItemId);
				setType(() -> Type.FORM_RELATIONSHIP);
			}
		};
	}

}