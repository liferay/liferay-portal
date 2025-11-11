/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.DisplayPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.dto.v1_0.EmbeddedMessageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.dto.v1_0.FormContainerClassSubtypeReference;
import com.liferay.headless.admin.site.dto.v1_0.FormContainerConfig;
import com.liferay.headless.admin.site.dto.v1_0.FormContainerContextReference;
import com.liferay.headless.admin.site.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormContainerReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.SitePageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.dto.v1_0.StayInPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.dto.v1_0.SuccessFormContainerSubmissionResult;
import com.liferay.headless.admin.site.dto.v1_0.SuccessNotificationMessage;
import com.liferay.headless.admin.site.dto.v1_0.URLFormContainerSubmissionResult;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ContainerLayoutUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.FormStyledLayoutStructureItem",
	service = DTOConverter.class
)
public class FormContainerPageElementDefinitionDTOConverter
	implements DTOConverter
		<FormStyledLayoutStructureItem, FormContainerPageElementDefinition> {

	@Override
	public String getContentType() {
		return FormContainerPageElementDefinition.class.getSimpleName();
	}

	@Override
	public FormContainerPageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		FormContainerPageElementDefinition formContainerPageElementDefinition =
			new FormContainerPageElementDefinition();

		formContainerPageElementDefinition.setCssClasses(
			() -> {
				if (SetUtil.isEmpty(
						formStyledLayoutStructureItem.getCssClasses())) {

					return null;
				}

				return ArrayUtil.toStringArray(
					formStyledLayoutStructureItem.getCssClasses());
			});
		formContainerPageElementDefinition.setCustomCSS(
			() -> {
				String customCSS = formStyledLayoutStructureItem.getCustomCSS();

				if (Validator.isNotNull(customCSS)) {
					return customCSS;
				}

				return null;
			});
		formContainerPageElementDefinition.setFormContainerConfig(
			() -> _toFormContainerConfig(
				companyId, formStyledLayoutStructureItem, scopeGroupId));
		formContainerPageElementDefinition.setFragmentViewports(
			() -> FragmentViewportUtil.toFragmentViewports(
				formStyledLayoutStructureItem.getItemConfigJSONObject()));
		formContainerPageElementDefinition.setIndexed(
			formStyledLayoutStructureItem::isIndexed);
		formContainerPageElementDefinition.setLayout(
			() -> ContainerLayoutUtil.toLayout(
				formStyledLayoutStructureItem.getItemConfigJSONObject()));
		formContainerPageElementDefinition.setName(
			formStyledLayoutStructureItem::getName);

		return formContainerPageElementDefinition;
	}

	private FormContainerConfig _toFormContainerConfig(
		long companyId,
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		long scopeGroupId) {

		if ((formStyledLayoutStructureItem.getFormConfig() !=
				FormStyledLayoutStructureItem.
					FORM_CONFIG_DISPLAY_PAGE_ITEM_TYPE) &&
			(formStyledLayoutStructureItem.getFormConfig() !=
				FormStyledLayoutStructureItem.FORM_CONFIG_OTHER_ITEM_TYPE)) {

			return null;
		}

		return new FormContainerConfig() {
			{
				setFormContainerReference(
					() -> _toFormContainerReference(
						formStyledLayoutStructureItem));
				setFormContainerType(
					() -> {
						if (Objects.equals(
								formStyledLayoutStructureItem.getFormType(),
								"simple")) {

							return FormContainerType.SIMPLE;
						}

						return FormContainerType.MULTISTEP;
					});
				setNumberOfSteps(
					formStyledLayoutStructureItem::getNumberOfSteps);
				setSuccessFormContainerSubmissionResult(
					() -> _toSuccessFormContainerSubmissionResult(
						companyId,
						formStyledLayoutStructureItem.
							getSuccessMessageJSONObject(),
						scopeGroupId));
			}
		};
	}

	private FormContainerReference _toFormContainerReference(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		if (formStyledLayoutStructureItem.getFormConfig() ==
				FormStyledLayoutStructureItem.FORM_CONFIG_OTHER_ITEM_TYPE) {

			FormContainerClassSubtypeReference
				formContainerClassSubtypeReference =
					new FormContainerClassSubtypeReference();

			formContainerClassSubtypeReference.setClassName(
				formStyledLayoutStructureItem::getClassName);

			return formContainerClassSubtypeReference;
		}

		FormContainerContextReference formContainerContextReference =
			new FormContainerContextReference();

		formContainerContextReference.setContextSource(
			() ->
				FormContainerContextReference.ContextSource.DISPLAY_PAGE_ITEM);

		return formContainerContextReference;
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

	private ItemExternalReference _toLayoutItemExternalReference(
		long companyId, JSONObject layoutJSONObject, long scopeGroupId) {

		if (JSONUtil.isEmpty(layoutJSONObject)) {
			return null;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutJSONObject.getLong("groupId"),
			layoutJSONObject.getBoolean("privateLayout"),
			layoutJSONObject.getLong("layoutId"));

		String externalReferenceCode;

		if (layout != null) {
			externalReferenceCode = layout.getExternalReferenceCode();
		}
		else {
			externalReferenceCode = layoutJSONObject.getString(
				"externalReferenceCode");
		}

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(Layout.class::getName);
		itemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		itemExternalReference.setScope(
			() -> {
				if (layout != null) {
					return ItemScopeUtil.getItemScope(
						layout.getGroupId(), scopeGroupId);
				}

				return ItemScopeUtil.getItemScope(
					companyId,
					layoutJSONObject.getString("scopeExternalReferenceCode"),
					scopeGroupId);
			});

		return itemExternalReference;
	}

	private ItemExternalReference
		_toLayoutPageTemplateEntryItemExternalReference(
			long companyId, String displayPage, JSONObject jsonObject,
			long scopeGroupId) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				GetterUtil.getInteger(
					StringUtil.removeSubstring(
						displayPage,
						LayoutPageTemplateEntry.class.getSimpleName() +
							StringPool.UNDERLINE)));

		String layoutPageTemplateEntryExternalReferenceCode;

		if (layoutPageTemplateEntry != null) {
			layoutPageTemplateEntryExternalReferenceCode =
				layoutPageTemplateEntry.getExternalReferenceCode();
		}
		else {
			layoutPageTemplateEntryExternalReferenceCode = jsonObject.getString(
				"layoutPageTemplateEntryExternalReferenceCode");
		}

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(
			LayoutPageTemplateEntry.class::getName);
		itemExternalReference.setExternalReferenceCode(
			() -> layoutPageTemplateEntryExternalReferenceCode);
		itemExternalReference.setScope(
			() -> {
				if (layoutPageTemplateEntry != null) {
					return ItemScopeUtil.getItemScope(
						layoutPageTemplateEntry.getGroupId(), scopeGroupId);
				}

				return ItemScopeUtil.getItemScope(
					companyId,
					jsonObject.getString(
						"layoutPageTemplateEntryScopeExternalReferenceCode"),
					scopeGroupId);
			});

		return itemExternalReference;
	}

	private SuccessFormContainerSubmissionResult
		_toSuccessFormContainerSubmissionResult(
			long companyId, JSONObject jsonObject, long scopeGroupId) {

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		String type = jsonObject.getString("type");

		if (Validator.isNull(type)) {
			return null;
		}

		if (StringUtil.equals(type, "displayPage")) {
			DisplayPageFormContainerSubmissionResult
				displayPageFormContainerSubmissionResult =
					new DisplayPageFormContainerSubmissionResult();

			String displayPage = jsonObject.getString("displayPage");

			if (StringUtil.startsWith(
					displayPage,
					LayoutPageTemplateEntry.class.getSimpleName() +
						StringPool.UNDERLINE)) {

				displayPageFormContainerSubmissionResult.
					setItemExternalReference(
						() -> _toLayoutPageTemplateEntryItemExternalReference(
							companyId, displayPage, jsonObject, scopeGroupId));
			}
			else {
				displayPageFormContainerSubmissionResult.setDefaultDisplayPage(
					() -> Boolean.TRUE);
			}

			displayPageFormContainerSubmissionResult.
				setSuccessNotificationMessage(
					() -> _toSuccessNotificationMessage(jsonObject));

			return displayPageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(type, "embedded")) {
			EmbeddedMessageFormContainerSubmissionResult
				embeddedMessageFormContainerSubmissionResult =
					new EmbeddedMessageFormContainerSubmissionResult();

			embeddedMessageFormContainerSubmissionResult.setMessage(
				() -> _toFragmentInlineValue(
					jsonObject.getJSONObject("message")));
			embeddedMessageFormContainerSubmissionResult.
				setSuccessNotificationMessage(
					() -> _toSuccessNotificationMessage(jsonObject));

			return embeddedMessageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(type, "none")) {
			StayInPageFormContainerSubmissionResult
				stayInPageFormContainerSubmissionResult =
					new StayInPageFormContainerSubmissionResult();

			stayInPageFormContainerSubmissionResult.
				setSuccessNotificationMessage(
					() -> _toSuccessNotificationMessage(jsonObject));

			return stayInPageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(type, "page")) {
			SitePageFormContainerSubmissionResult
				sitePageFormContainerSubmissionResult =
					new SitePageFormContainerSubmissionResult();

			sitePageFormContainerSubmissionResult.setItemExternalReference(
				() -> _toLayoutItemExternalReference(
					companyId, jsonObject.getJSONObject("layout"),
					scopeGroupId));
			sitePageFormContainerSubmissionResult.setSuccessNotificationMessage(
				() -> _toSuccessNotificationMessage(jsonObject));

			return sitePageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(type, "url")) {
			URLFormContainerSubmissionResult urlFormContainerSubmissionResult =
				new URLFormContainerSubmissionResult();

			urlFormContainerSubmissionResult.setUrl(
				() -> _toFragmentInlineValue(jsonObject.getJSONObject("url")));

			return urlFormContainerSubmissionResult;
		}

		return null;
	}

	private SuccessNotificationMessage _toSuccessNotificationMessage(
		JSONObject jsonObject) {

		if (!jsonObject.has("notificationText") &&
			!jsonObject.has("showNotification")) {

			return null;
		}

		SuccessNotificationMessage successNotificationMessage =
			new SuccessNotificationMessage();

		successNotificationMessage.setMessage(
			() -> _toFragmentInlineValue(
				jsonObject.getJSONObject("notificationText")));
		successNotificationMessage.setShowNotification(
			() -> GetterUtil.getBoolean(
				jsonObject.getBoolean("showNotification")));

		return successNotificationMessage;
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}