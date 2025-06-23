/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.mapper;

import com.liferay.headless.delivery.dto.v1_0.ClassTypeReference;
import com.liferay.headless.delivery.dto.v1_0.ContextReference;
import com.liferay.headless.delivery.dto.v1_0.FormConfig;
import com.liferay.headless.delivery.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.delivery.dto.v1_0.Layout;
import com.liferay.headless.delivery.dto.v1_0.LocalizationConfig;
import com.liferay.headless.delivery.dto.v1_0.MessageFormSubmissionResult;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.PageFormDefinition;
import com.liferay.headless.delivery.dto.v1_0.SitePageFormSubmissionResult;
import com.liferay.headless.delivery.dto.v1_0.URLFormSubmissionResult;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.util.FragmentMappedValueUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.util.LocalizedValueUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.util.StyledLayoutStructureItemUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.converter.AlignConverter;
import com.liferay.layout.converter.ContentDisplayConverter;
import com.liferay.layout.converter.FlexWrapConverter;
import com.liferay.layout.converter.JustifyConverter;
import com.liferay.layout.util.constants.StyledLayoutStructureConstants;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class FormLayoutStructureItemMapper
	extends BaseStyledLayoutStructureItemMapper {

	public FormLayoutStructureItemMapper(
		InfoItemServiceRegistry infoItemServiceRegistry, Portal portal) {

		super(infoItemServiceRegistry, portal);
	}

	@Override
	public PageElement getPageElement(
		long groupId, LayoutStructureItem layoutStructureItem,
		boolean saveInlineContent, boolean saveMappingConfiguration) {

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)layoutStructureItem;

		return new PageElement() {
			{
				setDefinition(
					() -> new PageFormDefinition() {
						{
							setCssClasses(
								() ->
									StyledLayoutStructureItemUtil.getCssClasses(
										formStyledLayoutStructureItem));
							setCustomCSS(
								() ->
									StyledLayoutStructureItemUtil.getCustomCSS(
										formStyledLayoutStructureItem));
							setCustomCSSViewports(
								() ->
									StyledLayoutStructureItemUtil.
										getCustomCSSViewports(
											formStyledLayoutStructureItem));
							setFormConfig(
								() -> new FormConfig() {
									{
										setFormReference(
											() -> _toFormReference(
												formStyledLayoutStructureItem));
										setFormSuccessSubmissionResult(
											() ->
												_toFormSuccessSubmissionResult(
													saveInlineContent,
													saveMappingConfiguration,
													formStyledLayoutStructureItem));
										setFormType(
											() -> _toFormType(
												formStyledLayoutStructureItem));
										setLocalizationConfig(
											() -> _toLocalizationConfig(
												formStyledLayoutStructureItem));
										setNumberOfSteps(
											formStyledLayoutStructureItem::
												getNumberOfSteps);
									}
								});
							setFragmentStyle(
								() -> {
									JSONObject itemConfigJSONObject =
										formStyledLayoutStructureItem.
											getItemConfigJSONObject();

									return toFragmentStyle(
										itemConfigJSONObject.getJSONObject(
											"styles"),
										saveMappingConfiguration);
								});
							setFragmentViewports(
								() -> getFragmentViewPorts(
									formStyledLayoutStructureItem.
										getItemConfigJSONObject()));
							setIndexed(
								formStyledLayoutStructureItem::isIndexed);
							setLayout(
								() -> _toLayout(formStyledLayoutStructureItem));
							setName(formStyledLayoutStructureItem::getName);
						}
					});
				setId(layoutStructureItem::getItemId);
				setType(() -> Type.FORM);
			}
		};
	}

	private Object _toFormReference(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		if (formStyledLayoutStructureItem.getFormConfig() ==
				FormStyledLayoutStructureItem.FORM_CONFIG_OTHER_ITEM_TYPE) {

			return new ClassTypeReference() {
				{
					setClassName(formStyledLayoutStructureItem::getClassName);
					setClassType(formStyledLayoutStructureItem::getClassTypeId);
				}
			};
		}

		return new ContextReference() {
			{
				setContextSource(() -> ContextSource.DISPLAY_PAGE_ITEM);
			}
		};
	}

	private Object _toFormSuccessSubmissionResult(
		boolean saveInlineContent, boolean saveMappingConfiguration,
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		JSONObject successMessageJSONObject =
			formStyledLayoutStructureItem.getSuccessMessageJSONObject();

		if ((!saveInlineContent && !saveMappingConfiguration) ||
			(successMessageJSONObject == null)) {

			return null;
		}

		if (saveInlineContent && successMessageJSONObject.has("message")) {
			return new MessageFormSubmissionResult() {
				{
					setMessage(
						() -> _toFragmentInlineValue(
							successMessageJSONObject.getJSONObject("message")));
					setMessageType(() -> MessageType.EMBEDDED);
				}
			};
		}

		String type = successMessageJSONObject.getString("type");

		if (saveInlineContent && Objects.equals(type, "none")) {
			return new MessageFormSubmissionResult() {
				{
					setMessage(
						() -> {
							if (!successMessageJSONObject.has(
									"notificationText")) {

								return null;
							}

							return _toFragmentInlineValue(
								successMessageJSONObject.getJSONObject(
									"notificationText"));
						});
					setMessageType(() -> MessageType.NONE);
					setShowNotification(
						() -> {
							if (!successMessageJSONObject.has(
									"showNotification")) {

								return null;
							}

							return successMessageJSONObject.getBoolean(
								"showNotification");
						});
				}
			};
		}

		if (saveInlineContent && successMessageJSONObject.has("url")) {
			return new URLFormSubmissionResult() {
				{
					setUrl(
						() -> _toFragmentInlineValue(
							successMessageJSONObject.getJSONObject("url")));
				}
			};
		}

		if (!saveMappingConfiguration ||
			!successMessageJSONObject.has("layout")) {

			return null;
		}

		JSONObject layoutJSONObject = successMessageJSONObject.getJSONObject(
			"layout");

		return new SitePageFormSubmissionResult() {
			{
				setItemReference(
					() -> FragmentMappedValueUtil.toLayoutClassFieldsReference(
						layoutJSONObject));
			}
		};
	}

	private FormConfig.FormType _toFormType(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		if (Objects.equals(
				formStyledLayoutStructureItem.getFormType(), "multistep")) {

			return FormConfig.FormType.MULTISTEP;
		}

		return FormConfig.FormType.SIMPLE;
	}

	private FragmentInlineValue _toFragmentInlineValue(JSONObject jsonObject) {
		return new FragmentInlineValue() {
			{
				setValue_i18n(
					() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
			}
		};
	}

	private Layout _toLayout(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		return new Layout() {
			{
				setAlign(
					() -> {
						String align = formStyledLayoutStructureItem.getAlign();

						if (Validator.isNull(align)) {
							return null;
						}

						return Align.create(
							AlignConverter.convertToExternalValue(align));
					});
				setContentDisplay(
					() -> {
						String contentDisplay =
							formStyledLayoutStructureItem.getContentDisplay();

						if (Validator.isNull(contentDisplay)) {
							return null;
						}

						return ContentDisplay.create(
							ContentDisplayConverter.convertToExternalValue(
								contentDisplay));
					});
				setFlexWrap(
					() -> {
						String flexWrap =
							formStyledLayoutStructureItem.getFlexWrap();

						if (Validator.isNull(flexWrap)) {
							return null;
						}

						return FlexWrap.create(
							FlexWrapConverter.convertToExternalValue(flexWrap));
					});
				setJustify(
					() -> {
						String justify =
							formStyledLayoutStructureItem.getJustify();

						if (Validator.isNull(justify)) {
							return null;
						}

						return Justify.create(
							JustifyConverter.convertToExternalValue(justify));
					});
				setWidthType(
					() -> {
						String widthType =
							formStyledLayoutStructureItem.getWidthType();

						if (Validator.isNull(widthType) ||
							Objects.equals(
								widthType,
								StyledLayoutStructureConstants.WIDTH_TYPE)) {

							return null;
						}

						return WidthType.create(
							StringUtil.upperCaseFirstLetter(widthType));
					});
			}
		};
	}

	private LocalizationConfig _toLocalizationConfig(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		JSONObject localizationConfigJSONObject =
			formStyledLayoutStructureItem.getLocalizationConfigJSONObject();

		if (localizationConfigJSONObject == null) {
			return null;
		}

		return new LocalizationConfig() {
			{
				if (localizationConfigJSONObject.has(
						"unlocalizedFieldsMessage")) {

					setUnlocalizedFieldsMessage(
						() -> _toFragmentInlineValue(
							localizationConfigJSONObject.getJSONObject(
								"unlocalizedFieldsMessage")));
				}

				if (localizationConfigJSONObject.has(
						"unlocalizedFieldsState")) {

					setUnlocalizedFieldsState(
						() -> {
							if (Objects.equals(
									localizationConfigJSONObject.getString(
										"unlocalizedFieldsState"),
									"disabled")) {

								return LocalizationConfig.
									UnlocalizedFieldsState.DISABLED;
							}

							return LocalizationConfig.UnlocalizedFieldsState.
								READ_ONLY;
						});
				}
			}
		};
	}

}