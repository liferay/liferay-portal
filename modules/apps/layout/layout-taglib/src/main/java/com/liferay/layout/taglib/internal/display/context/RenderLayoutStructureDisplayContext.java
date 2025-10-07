/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.display.context;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistryUtil;
import com.liferay.info.type.WebImage;
import com.liferay.info.type.WebURL;
import com.liferay.layout.helper.structure.LayoutStructureRulesHelper;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.layout.taglib.internal.util.SegmentsExperienceUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.layout.util.structure.RootLayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.layout.util.structure.StyledLayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Rubén Pulido
 */
public class RenderLayoutStructureDisplayContext {

	public RenderLayoutStructureDisplayContext(
		HttpServletRequest httpServletRequest, LayoutStructure layoutStructure,
		String mainItemId, String mode, boolean showPreview) {

		_httpServletRequest = httpServletRequest;
		_layoutStructure = layoutStructure;
		_mainItemId = mainItemId;
		_mode = mode;
		_showPreview = showPreview;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Theme theme = _themeDisplay.getTheme();

		String colorPalette = theme.getSetting("color-palette");

		_themeColorsCssClasses = SetUtil.fromArray(
			StringUtil.split(colorPalette));
	}

	public List<String> getCollectionStyledLayoutStructureItemIds() {
		List<String> collectionStyledLayoutStructureItemIds =
			(List<String>)_httpServletRequest.getAttribute(
				_COLLECTION_STYLED_LAYOUT_STRUCTURE_ITEM_IDS);

		if (collectionStyledLayoutStructureItemIds == null) {
			collectionStyledLayoutStructureItemIds = new ArrayList<>();

			_httpServletRequest.setAttribute(
				_COLLECTION_STYLED_LAYOUT_STRUCTURE_ITEM_IDS,
				collectionStyledLayoutStructureItemIds);
		}

		return collectionStyledLayoutStructureItemIds;
	}

	public String getColorCssClasses(
		StyledLayoutStructureItem styledLayoutStructureItem) {

		StringBundler sb = new StringBundler(4);

		JSONObject stylesJSONObject =
			styledLayoutStructureItem.getStylesJSONObject();

		String backgroundColorCssClass = stylesJSONObject.getString(
			"backgroundColor");

		if (_themeColorsCssClasses.contains(backgroundColorCssClass)) {
			sb.append("bg-");
			sb.append(backgroundColorCssClass);
		}

		String textColorCssClass = stylesJSONObject.getString("textColor");

		if (_themeColorsCssClasses.contains(textColorCssClass)) {
			sb.append(" text-");
			sb.append(textColorCssClass);
		}

		return sb.toString();
	}

	public String getContainerLinkHref(
			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem)
		throws PortalException {

		JSONObject linkJSONObject =
			containerStyledLayoutStructureItem.getLinkJSONObject();

		if (linkJSONObject == null) {
			return StringPool.BLANK;
		}

		JSONObject localizedJSONObject = linkJSONObject.getJSONObject(
			_themeDisplay.getLanguageId());

		if ((localizedJSONObject != null) &&
			(localizedJSONObject.length() > 0)) {

			linkJSONObject = localizedJSONObject;
		}

		String value = _getFieldValue(linkJSONObject);

		if (Validator.isNotNull(value)) {
			return value;
		}

		JSONObject layoutJSONObject = linkJSONObject.getJSONObject("layout");

		if (layoutJSONObject != null) {
			long groupId = layoutJSONObject.getLong("groupId");
			boolean privateLayout = layoutJSONObject.getBoolean(
				"privateLayout");
			long layoutId = layoutJSONObject.getLong("layoutId");

			Layout layout = LayoutLocalServiceUtil.fetchLayout(
				groupId, privateLayout, layoutId);

			if (layout == null) {
				return StringPool.POUND;
			}

			return PortalUtil.getLayoutFullURL(layout, _themeDisplay);
		}

		JSONObject hrefJSONObject = linkJSONObject.getJSONObject("href");

		if (hrefJSONObject != null) {
			return hrefJSONObject.getString(_themeDisplay.getLanguageId());
		}

		return StringPool.BLANK;
	}

	public String getContainerLinkTarget(
		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem) {

		JSONObject linkJSONObject =
			containerStyledLayoutStructureItem.getLinkJSONObject();

		if (linkJSONObject == null) {
			return StringPool.BLANK;
		}

		JSONObject localizedJSONObject = linkJSONObject.getJSONObject(
			_themeDisplay.getLanguageId());

		if ((localizedJSONObject != null) &&
			(localizedJSONObject.length() > 0)) {

			linkJSONObject = localizedJSONObject;
		}

		return linkJSONObject.getString("target");
	}

	public DefaultFragmentRendererContext getDefaultFragmentRendererContext(
		FragmentEntryLink fragmentEntryLink, InfoForm infoForm, String itemId) {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		InfoItemReference infoItemReference =
			(InfoItemReference)_httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (infoItemReference == null) {
			InfoItemDetails infoItemDetails =
				(InfoItemDetails)_httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_DETAILS);

			if (infoItemDetails != null) {
				infoItemReference = infoItemDetails.getInfoItemReference();
			}
		}

		defaultFragmentRendererContext.setContextInfoItemReference(
			infoItemReference);

		defaultFragmentRendererContext.setLocale(_themeDisplay.getLocale());

		Layout layout = _themeDisplay.getLayout();

		if (infoForm == null) {
			infoForm = (InfoForm)_httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_FORM);
		}

		defaultFragmentRendererContext.setInfoForm(infoForm);

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			defaultFragmentRendererContext.setMode(_mode);
			defaultFragmentRendererContext.setPreviewClassNameId(
				_getPreviewClassNameId());
			defaultFragmentRendererContext.setPreviewClassPK(
				_getPreviewClassPK());
			defaultFragmentRendererContext.setPreviewType(_getPreviewType());
			defaultFragmentRendererContext.setPreviewVersion(
				_getPreviewVersion());
			defaultFragmentRendererContext.setSegmentsEntryIds(
				_getSegmentsEntryIds());
		}

		if (LayoutStructureItemUtil.hasAncestor(
				itemId, LayoutDataItemTypeConstants.TYPE_COLLECTION_ITEM,
				_layoutStructure)) {

			defaultFragmentRendererContext.setUseCachedContent(false);
		}

		return defaultFragmentRendererContext;
	}

	public Set<String> getDisabledItemIds() {
		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult = getLayoutStructureRulesResult();

		return layoutStructureRulesResult.getDisabledItemIds();
	}

	public Set<String> getDisplayedItemIds() {
		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult = getLayoutStructureRulesResult();

		return layoutStructureRulesResult.getDisplayedItemIds();
	}

	public String getEditInfoItemActionURL() {
		StringBundler sb = new StringBundler(3);

		sb.append(PortalUtil.getPortalURL(_httpServletRequest));
		sb.append(_themeDisplay.getPathMain());
		sb.append("/portal/edit_info_item");

		return PortalUtil.addPreservedParameters(_themeDisplay, sb.toString());
	}

	public Set<String> getEnabledItemIds() {
		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult = getLayoutStructureRulesResult();

		return layoutStructureRulesResult.getEnabledItemIds();
	}

	public String getErrorMessage(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		InfoForm infoForm) {

		InfoFormException infoFormException =
			(InfoFormException)SessionErrors.get(
				_httpServletRequest, formStyledLayoutStructureItem.getItemId());

		if (!(infoFormException instanceof InfoFormValidationException)) {
			return infoFormException.getLocalizedMessage(
				_themeDisplay.getLocale());
		}

		InfoFormValidationException infoFormValidationException =
			(InfoFormValidationException)infoFormException;

		if (Validator.isNull(
				infoFormValidationException.getInfoFieldUniqueId())) {

			return infoFormException.getLocalizedMessage(
				_themeDisplay.getLocale());
		}

		String formInputLabel = _getFormInputLabel(
			infoFormValidationException.getInfoFieldUniqueId());

		if (Validator.isNotNull(formInputLabel)) {
			return infoFormValidationException.getLocalizedMessage(
				formInputLabel, _themeDisplay.getLocale());
		}

		InfoField<?> infoField = infoForm.getInfoField(
			infoFormValidationException.getInfoFieldUniqueId());

		formInputLabel = infoField.getLabel(_themeDisplay.getLocale());

		return infoFormValidationException.getLocalizedMessage(
			formInputLabel, _themeDisplay.getLocale());
	}

	public String getFormStyledLayoutStructureItemRedirect(
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws Exception {

		JSONObject successMessageJSONObject =
			formStyledLayoutStructureItem.getSuccessMessageJSONObject();

		if (successMessageJSONObject == null) {
			return StringPool.BLANK;
		}

		String redirect = StringPool.BLANK;

		if (successMessageJSONObject.has("url")) {
			redirect = _getFormStyledLayoutStructureItemURLRedirect(
				successMessageJSONObject);
		}
		else if (successMessageJSONObject.has("layout")) {
			redirect = _getFormStyledLayoutStructureItemLayoutRedirect(
				successMessageJSONObject);
		}

		return redirect;
	}

	public String getFormStyledLayoutStructureItemSuccessMessageDisplayPage(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		JSONObject successMessageJSONObject =
			formStyledLayoutStructureItem.getSuccessMessageJSONObject();

		if ((successMessageJSONObject == null) ||
			!successMessageJSONObject.has("displayPage")) {

			return StringPool.BLANK;
		}

		return successMessageJSONObject.getString("displayPage");
	}

	public Set<String> getHiddenItemIds() {
		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult = getLayoutStructureRulesResult();

		return layoutStructureRulesResult.getHiddenItemIds();
	}

	public InfoForm getInfoForm(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem) {

		long classNameId = formStyledLayoutStructureItem.getClassNameId();

		if (classNameId <= 0) {
			return null;
		}

		String className = formStyledLayoutStructureItem.getClassName();

		if (Validator.isNull(className)) {
			return null;
		}

		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		InfoItemFormProvider<Object> infoItemFormProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, className);

		if (infoItemFormProvider == null) {
			return null;
		}

		try {
			return infoItemFormProvider.getInfoForm(
				String.valueOf(formStyledLayoutStructureItem.getClassTypeId()),
				_themeDisplay.getScopeGroupId());
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormVariationException);
			}

			return null;
		}
	}

	public Map<String, Object> getInfoItemActionComponentContext() {
		return HashMapBuilder.<String, Object>put(
			"executeInfoItemActionURL",
			() -> {
				StringBundler sb = new StringBundler(6);

				sb.append(PortalUtil.getPortalURL(_httpServletRequest));
				sb.append(_themeDisplay.getPathMain());
				sb.append("/portal/execute_info_item_action?p_l_mode=");
				sb.append(getLayoutMode());
				sb.append("&plid=");

				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				sb.append(themeDisplay.getPlid());

				return sb.toString();
			}
		).build();
	}

	public String getLayoutMode() {
		return ParamUtil.getString(
			_httpServletRequest, "p_l_mode", Constants.VIEW);
	}

	public LayoutStructure getLayoutStructure() {
		return _layoutStructure;
	}

	public LayoutStructureRulesHelper.LayoutStructureRulesResult
		getLayoutStructureRulesResult() {

		if (_layoutStructureRulesResult != null) {
			return _layoutStructureRulesResult;
		}

		LayoutStructureRulesHelper layoutStructureRulesHelper =
			ServletContextUtil.getLayoutStructureRulesHelper();

		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult =
				layoutStructureRulesHelper.processLayoutStructureRules(
					_themeDisplay.getScopeGroupId(), _layoutStructure,
					_themeDisplay.getPermissionChecker(),
					_getSegmentsEntryIds());

		_layoutStructureRulesResult = layoutStructureRulesResult;

		return _layoutStructureRulesResult;
	}

	public List<String> getMainChildrenItemIds() {
		LayoutStructure layoutStructure = getLayoutStructure();

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(_getMainItemId());

		return layoutStructureItem.getChildrenItemIds();
	}

	public String getNotificationText(
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws Exception {

		JSONObject successMessageJSONObject =
			formStyledLayoutStructureItem.getSuccessMessageJSONObject();

		if ((successMessageJSONObject == null) ||
			!GetterUtil.getBoolean(
				successMessageJSONObject.getBoolean("showNotification"))) {

			return StringPool.BLANK;
		}

		JSONObject textJSONObject = successMessageJSONObject.getJSONObject(
			"notificationText");

		if (textJSONObject == null) {
			return LanguageUtil.get(
				_themeDisplay.getLocale(),
				"your-information-was-successfully-received");
		}

		String notificationText = textJSONObject.getString(
			_themeDisplay.getLanguageId());

		if (Validator.isNull(notificationText)) {
			String siteDefaultLanguageId = LanguageUtil.getLanguageId(
				PortalUtil.getSiteDefaultLocale(
					_themeDisplay.getScopeGroupId()));

			notificationText = textJSONObject.getString(siteDefaultLanguageId);
		}

		if (Validator.isNotNull(notificationText)) {
			return notificationText;
		}

		return LanguageUtil.get(
			_themeDisplay.getLocale(),
			"your-information-was-successfully-received");
	}

	public Map<String, Object> getRulesHandlerComponentContext() {
		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult = getLayoutStructureRulesResult();

		return HashMapBuilder.<String, Object>put(
			"evaluateRulesURL",
			() -> {
				StringBundler sb = new StringBundler(6);

				sb.append(PortalUtil.getPortalURL(_httpServletRequest));
				sb.append(_themeDisplay.getPathMain());
				sb.append("/portal/evaluate_layout_structure_rules?plid=");

				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				sb.append(themeDisplay.getPlid());

				sb.append("&segmentsExperienceId=");
				sb.append(
					SegmentsExperienceUtil.getSegmentsExperienceId(
						_httpServletRequest));

				return sb.toString();
			}
		).put(
			"itemIdsByRuleId",
			layoutStructureRulesResult.getLayoutStructureRuleIdsMap()
		).put(
			"ruleIdsByItemId", layoutStructureRulesResult.getItemIdsMap()
		).build();
	}

	public String getStyle(StyledLayoutStructureItem styledLayoutStructureItem)
		throws Exception {

		StringBundler sb = new StringBundler(9);

		JSONObject backgroundImageJSONObject =
			styledLayoutStructureItem.getBackgroundImageJSONObject();

		long fileEntryId = 0;

		if (backgroundImageJSONObject.has("fileEntryId")) {
			fileEntryId = backgroundImageJSONObject.getLong("fileEntryId");
		}
		else if (backgroundImageJSONObject.has("classNameId") &&
				 backgroundImageJSONObject.has("classPK") &&
				 backgroundImageJSONObject.has("fieldId")) {

			FragmentEntryProcessorHelper fragmentEntryProcessorHelper =
				ServletContextUtil.getFragmentEntryProcessorHelper();

			fileEntryId = fragmentEntryProcessorHelper.getFileEntryId(
				backgroundImageJSONObject.getLong("classNameId"),
				backgroundImageJSONObject.getLong("classPK"),
				backgroundImageJSONObject.getString("fieldId"),
				_themeDisplay.getLocale());
		}
		else if (backgroundImageJSONObject.has("collectionFieldId")) {
			FragmentEntryProcessorHelper fragmentEntryProcessorHelper =
				ServletContextUtil.getFragmentEntryProcessorHelper();

			fileEntryId = fragmentEntryProcessorHelper.getFileEntryId(
				(InfoItemReference)_httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE),
				backgroundImageJSONObject.getString("collectionFieldId"),
				_themeDisplay.getLocale());
		}
		else if (backgroundImageJSONObject.has("mappedField")) {
			fileEntryId = _getFileEntryId(
				backgroundImageJSONObject.getString("mappedField"));
		}

		if (fileEntryId != 0) {
			sb.append("--background-image-file-entry-id:");
			sb.append(fileEntryId);
			sb.append(StringPool.SEMICOLON);
		}

		String backgroundImageURL = _getBackgroundImage(
			backgroundImageJSONObject);

		if (Validator.isNotNull(backgroundImageURL)) {
			sb.append("--lfr-background-image-");
			sb.append(styledLayoutStructureItem.getItemId());
			sb.append(": url(");
			sb.append(backgroundImageURL);
			sb.append(");");
		}

		Set<String> displayedItemIds = getDisplayedItemIds();

		if (displayedItemIds.contains(styledLayoutStructureItem.getItemId())) {
			sb.append("display: block !important;");
		}

		return sb.toString();
	}

	public String getSuccessMessage(
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws Exception {

		String successMessage = null;

		JSONObject successMessageJSONObject =
			formStyledLayoutStructureItem.getSuccessMessageJSONObject();

		if ((successMessageJSONObject != null) &&
			successMessageJSONObject.has("message")) {

			JSONObject messageJSONObject =
				successMessageJSONObject.getJSONObject("message");

			successMessage = HtmlUtil.escape(
				messageJSONObject.getString(_themeDisplay.getLanguageId()));

			if (Validator.isNull(successMessage)) {
				String siteDefaultLanguageId = LanguageUtil.getLanguageId(
					PortalUtil.getSiteDefaultLocale(
						_themeDisplay.getScopeGroupId()));

				successMessage = HtmlUtil.escape(
					messageJSONObject.getString(siteDefaultLanguageId));
			}
		}

		if (Validator.isNull(successMessage)) {
			successMessage = LanguageUtil.get(
				_themeDisplay.getLocale(),
				"thank-you.-your-information-was-successfully-received");
		}

		return successMessage;
	}

	public boolean includeCommonStyles(FragmentEntryLink fragmentEntryLink)
		throws Exception {

		String editableValues = fragmentEntryLink.getEditableValues();

		if (!editableValues.contains(
				FragmentEntryProcessorConstants.
					KEY_STYLES_FRAGMENT_ENTRY_PROCESSOR)) {

			return false;
		}

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject stylesFragmentEntryEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_STYLES_FRAGMENT_ENTRY_PROCESSOR);

		if (stylesFragmentEntryEntryProcessorJSONObject == null) {
			return false;
		}

		return stylesFragmentEntryEntryProcessorJSONObject.getBoolean(
			"hasCommonStyles");
	}

	public boolean isIncludeContainer(
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem) {

		LayoutStructureItem parentLayoutStructureItem =
			_layoutStructure.getLayoutStructureItem(
				rowStyledLayoutStructureItem.getParentItemId());

		if (!(parentLayoutStructureItem instanceof RootLayoutStructureItem)) {
			return false;
		}

		Layout layout = _themeDisplay.getLayout();

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			return true;
		}

		LayoutStructureItem rootParentLayoutStructureItem =
			_layoutStructure.getLayoutStructureItem(
				parentLayoutStructureItem.getParentItemId());

		if (rootParentLayoutStructureItem == null) {
			return true;
		}

		if (rootParentLayoutStructureItem instanceof
				DropZoneLayoutStructureItem) {

			LayoutStructureItem dropZoneParentLayoutStructureItem =
				_layoutStructure.getLayoutStructureItem(
					rootParentLayoutStructureItem.getParentItemId());

			if (dropZoneParentLayoutStructureItem instanceof
					RootLayoutStructureItem) {

				return true;
			}
		}

		return false;
	}

	private String _getBackgroundImage(JSONObject jsonObject) {
		if (jsonObject == null) {
			return StringPool.BLANK;
		}

		String value = _getFieldValue(jsonObject);

		if (Validator.isNotNull(value)) {
			return value;
		}

		String backgroundImageURL = jsonObject.getString("url");

		if (Validator.isNotNull(backgroundImageURL)) {
			return backgroundImageURL;
		}

		return StringPool.BLANK;
	}

	private String _getFieldValue(JSONObject jsonObject) {
		String collectionFieldId = jsonObject.getString("collectionFieldId");

		if (Validator.isNotNull(collectionFieldId)) {
			String value = _getValue(
				collectionFieldId,
				(InfoItemReference)_httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE));

			if (Validator.isNotNull(value)) {
				return value;
			}
		}

		String mappedField = jsonObject.getString("mappedField");

		if (Validator.isNotNull(mappedField)) {
			Object infoItem = _httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

			InfoItemDetails infoItemDetails =
				(InfoItemDetails)_httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_DETAILS);

			if ((infoItem != null) && (infoItemDetails != null)) {
				InfoItemServiceRegistry infoItemServiceRegistry =
					ServletContextUtil.getInfoItemServiceRegistry();

				InfoItemFieldValuesProvider<Object>
					infoItemFieldValuesProvider =
						infoItemServiceRegistry.getFirstInfoItemService(
							InfoItemFieldValuesProvider.class,
							infoItemDetails.getClassName());

				if (infoItemFieldValuesProvider != null) {
					String value = _parseInfoFieldValue(
						infoItemFieldValuesProvider.getInfoFieldValue(
							infoItem, mappedField));

					if (Validator.isNotNull(value)) {
						return value;
					}
				}
			}
		}

		String fieldId = jsonObject.getString("fieldId");

		if (Validator.isNotNull(fieldId)) {
			long classNameId = jsonObject.getLong("classNameId");
			long classPK = jsonObject.getLong("classPK");

			if ((classNameId > 0) && (classPK > 0)) {
				InfoItemReference infoItemReference = new InfoItemReference(
					PortalUtil.getClassName(classNameId),
					new ClassPKInfoItemIdentifier(classPK));

				String value = _getValue(fieldId, infoItemReference);

				if (Validator.isNotNull(value)) {
					return value;
				}
			}
		}

		return StringPool.BLANK;
	}

	private long _getFileEntryId(String fieldId) throws Exception {
		InfoItemDetails infoItemDetails =
			(InfoItemDetails)_httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if (infoItemDetails == null) {
			return 0;
		}

		InfoItemReference infoItemReference =
			infoItemDetails.getInfoItemReference();

		if (infoItemReference == null) {
			return 0;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return 0;
		}

		FragmentEntryProcessorHelper fragmentEntryProcessorHelper =
			ServletContextUtil.getFragmentEntryProcessorHelper();

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		return fragmentEntryProcessorHelper.getFileEntryId(
			PortalUtil.getClassNameId(infoItemReference.getClassName()),
			classPKInfoItemIdentifier.getClassPK(), fieldId,
			_themeDisplay.getLocale());
	}

	private String _getFormInputLabel(String infoFieldUniqueId) {
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			ServletContextUtil.getFragmentEntryConfigurationParser();

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			_layoutStructure.getFragmentLayoutStructureItems();

		for (LayoutStructureItem layoutStructureItem :
				fragmentLayoutStructureItems.values()) {

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			if (fragmentStyledLayoutStructureItem.getFragmentEntryLinkId() <=
					0) {

				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if ((fragmentEntryLink == null) ||
				Validator.isNull(fragmentEntryLink.getEditableValues())) {

				continue;
			}

			String inputFieldId = GetterUtil.getString(
				fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValuesJSONObject(),
					new FragmentConfigurationField(
						"inputFieldId", "string", StringPool.BLANK, false,
						"text"),
					_themeDisplay.getLocale()));

			if (!Objects.equals(inputFieldId, infoFieldUniqueId)) {
				continue;
			}

			return GetterUtil.getString(
				fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValuesJSONObject(),
					new FragmentConfigurationField(
						"inputLabel", "string", StringPool.BLANK, true, "text"),
					_themeDisplay.getLocale()));
		}

		return StringPool.BLANK;
	}

	private String _getFormStyledLayoutStructureItemLayoutRedirect(
			JSONObject successMessageJSONObject)
		throws Exception {

		JSONObject layoutJSONObject = successMessageJSONObject.getJSONObject(
			"layout");

		if (layoutJSONObject == null) {
			return StringPool.BLANK;
		}

		String layoutUuid = layoutJSONObject.getString("layoutUuid");
		long groupId = layoutJSONObject.getLong("groupId");
		boolean privateLayout = layoutJSONObject.getBoolean("privateLayout");

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			layoutUuid, groupId, privateLayout);

		if (layout != null) {
			return PortalUtil.getLayoutURL(layout, _themeDisplay);
		}

		return StringPool.BLANK;
	}

	private String _getFormStyledLayoutStructureItemURLRedirect(
			JSONObject successMessageJSONObject)
		throws Exception {

		JSONObject urlJSONObject = successMessageJSONObject.getJSONObject(
			"url");

		if (urlJSONObject == null) {
			return StringPool.BLANK;
		}

		String redirect = urlJSONObject.getString(
			_themeDisplay.getLanguageId());

		if (Validator.isNull(redirect)) {
			String siteDefaultLanguageId = LanguageUtil.getLanguageId(
				PortalUtil.getSiteDefaultLocale(
					_themeDisplay.getScopeGroupId()));

			redirect = urlJSONObject.getString(siteDefaultLanguageId);
		}

		return redirect;
	}

	private Object _getInfoItem(InfoItemReference infoItemReference) {
		if (infoItemReference == null) {
			return null;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, infoItemReference.getClassName(),
				infoItemIdentifier.getInfoItemServiceFilter());

		try {
			return infoItemObjectProvider.getInfoItem(infoItemIdentifier);
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}
		}

		return null;
	}

	private String _getMainItemId() {
		if (Validator.isNotNull(_mainItemId)) {
			return _mainItemId;
		}

		return _layoutStructure.getMainItemId();
	}

	private long _getPreviewClassNameId() {
		if (_previewClassNameId != null) {
			return _previewClassNameId;
		}

		if (!_showPreview) {
			return 0;
		}

		_previewClassNameId = ParamUtil.getLong(
			_httpServletRequest, "previewClassNameId");

		return _previewClassNameId;
	}

	private long _getPreviewClassPK() {
		if (_previewClassPK != null) {
			return _previewClassPK;
		}

		if (!_showPreview) {
			return 0;
		}

		_previewClassPK = ParamUtil.getLong(
			_httpServletRequest, "previewClassPK");

		return _previewClassPK;
	}

	private int _getPreviewType() {
		if (_previewType != null) {
			return _previewType;
		}

		if (!_showPreview) {
			return 0;
		}

		_previewType = ParamUtil.getInteger(_httpServletRequest, "previewType");

		return _previewType;
	}

	private String _getPreviewVersion() {
		if (_previewVersion != null) {
			return _previewVersion;
		}

		if (!_showPreview) {
			return null;
		}

		_previewVersion = ParamUtil.getString(
			_httpServletRequest, "previewVersion");

		return _previewVersion;
	}

	private long[] _getSegmentsEntryIds() {
		if (_segmentsEntryIds != null) {
			return _segmentsEntryIds;
		}

		long[] segmentEntryIds = (long[])_httpServletRequest.getAttribute(
			SegmentsWebKeys.SEGMENTS_ENTRY_IDS);

		if (segmentEntryIds != null) {
			_segmentsEntryIds = segmentEntryIds;
		}
		else {
			SegmentsEntryRetriever segmentsEntryRetriever =
				ServletContextUtil.getSegmentsEntryRetriever();

			RequestContextMapper requestContextMapper =
				ServletContextUtil.getRequestContextMapper();

			_segmentsEntryIds = segmentsEntryRetriever.getSegmentsEntryIds(
				_themeDisplay.getScopeGroupId(), _themeDisplay.getUserId(),
				requestContextMapper.map(_httpServletRequest), new long[0]);
		}

		return _segmentsEntryIds;
	}

	private String _getValue(
		String fieldId, InfoItemReference infoItemReference) {

		String className = InfoSearchClassMapperRegistryUtil.getClassName(
			infoItemReference.getClassName());

		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		if (infoItemFieldValuesProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get info item field values provider for class " +
						className);
			}

			return StringPool.BLANK;
		}

		Object infoItemObject = _getInfoItem(infoItemReference);

		if (infoItemObject == null) {
			return StringPool.BLANK;
		}

		return _parseInfoFieldValue(
			infoItemFieldValuesProvider.getInfoFieldValue(
				infoItemObject, fieldId));
	}

	private String _parseInfoFieldValue(InfoFieldValue<?> infoFieldValue) {
		if (infoFieldValue == null) {
			return StringPool.BLANK;
		}

		Object value = infoFieldValue.getValue(_themeDisplay.getLocale());

		if (value instanceof String) {
			return (String)value;
		}

		if (value instanceof WebImage) {
			WebImage webImage = (WebImage)value;

			String url = webImage.getURL();

			if (Validator.isNotNull(url)) {
				return url;
			}
		}

		if (value instanceof WebURL) {
			WebURL webURL = (WebURL)value;

			String url = webURL.getURL();

			if (Validator.isNotNull(url)) {
				return url;
			}
		}

		return StringPool.BLANK;
	}

	private static final String _COLLECTION_STYLED_LAYOUT_STRUCTURE_ITEM_IDS =
		"COLLECTION_STYLED_LAYOUT_STRUCTURE_ITEM_IDS";

	private static final Log _log = LogFactoryUtil.getLog(
		RenderLayoutStructureDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final LayoutStructure _layoutStructure;
	private LayoutStructureRulesHelper.LayoutStructureRulesResult
		_layoutStructureRulesResult;
	private final String _mainItemId;
	private final String _mode;
	private Long _previewClassNameId;
	private Long _previewClassPK;
	private Integer _previewType;
	private String _previewVersion;
	private long[] _segmentsEntryIds;
	private final boolean _showPreview;
	private final Set<String> _themeColorsCssClasses;
	private final ThemeDisplay _themeDisplay;

}