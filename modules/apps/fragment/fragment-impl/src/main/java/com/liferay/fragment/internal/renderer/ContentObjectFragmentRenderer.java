/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectVariationProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.item.renderer.InfoItemTemplatedRenderer;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = FragmentRenderer.class)
public class ContentObjectFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
	}

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		return JSONUtil.put(
			"fieldSets",
			JSONUtil.putAll(
				JSONUtil.put(
					"fields",
					JSONUtil.putAll(
						JSONUtil.put(
							"label", "item"
						).put(
							"name", "itemSelector"
						).put(
							"type", "itemSelector"
						).put(
							"typeOptions",
							JSONUtil.put("enableSelectTemplate", true)
						))
				).put(
					"label",
					_language.format(
						fragmentRendererContext.getLocale(), "x-options",
						"content-display", true)
				)));
	}

	@Override
	public String getIcon() {
		return "web-content";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "content-display");
	}

	@Override
	public boolean hasViewPermission(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		JSONObject jsonObject = _getFieldValueJSONObject(
			fragmentRendererContext);

		InfoItemReference infoItemReference =
			fragmentRendererContext.getContextInfoItemReference();

		if ((infoItemReference == null) &&
			((jsonObject == null) || (jsonObject.length() == 0))) {

			return true;
		}

		String className = StringPool.BLANK;
		Object displayObject = null;

		if (jsonObject != null) {
			className = jsonObject.getString("className");

			displayObject = _getDisplayObject(
				httpServletRequest, infoItemReference, jsonObject);
		}
		else {
			displayObject = _getInfoItem(infoItemReference);
		}

		if (displayObject == null) {
			return true;
		}

		if (Validator.isNull(className) && (infoItemReference != null)) {
			className = infoItemReference.getClassName();
		}

		Tuple tuple = _getTuple(
			className, displayObject.getClass(), fragmentRendererContext);

		if ((tuple == null) || (tuple.getObject(0) == null) ||
			_hasPermission(httpServletRequest, className, displayObject)) {

			return true;
		}

		return false;
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		JSONObject jsonObject = _getFieldValueJSONObject(
			fragmentRendererContext);

		InfoItemReference infoItemReference =
			fragmentRendererContext.getContextInfoItemReference();

		if ((infoItemReference == null) &&
			((jsonObject == null) || (jsonObject.length() == 0))) {

			if (fragmentRendererContext.isEditMode()) {
				FragmentRendererUtil.printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-selected-content-will-be-shown-here");
			}

			return;
		}

		Object displayObject = null;

		if (jsonObject != null) {
			displayObject = _getDisplayObject(
				httpServletRequest, infoItemReference, jsonObject);
		}
		else {
			displayObject = _getInfoItem(infoItemReference);
		}

		if (displayObject == null) {
			if (fragmentRendererContext.isEditMode()) {
				FragmentRendererUtil.printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-selected-content-is-no-longer-available.-please-" +
						"select-another");
			}

			return;
		}

		String className = StringPool.BLANK;

		if (jsonObject != null) {
			className = jsonObject.getString("className");
		}

		if (Validator.isNull(className) && (infoItemReference != null)) {
			className = infoItemReference.getClassName();
		}

		Tuple tuple = _getTuple(
			className, displayObject.getClass(), fragmentRendererContext);

		if ((tuple == null) || (tuple.getObject(0) == null)) {
			if (fragmentRendererContext.isEditMode()) {
				FragmentRendererUtil.printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"there-are-no-available-renderers-for-the-selected-" +
						"content");
			}

			return;
		}

		if (!_hasPermission(httpServletRequest, className, displayObject)) {
			if (fragmentRendererContext.isEditMode()) {
				FragmentRendererUtil.printRestrictedContentMessage(
					httpServletRequest, httpServletResponse);
			}

			return;
		}

		long classPK = _getClassPK(infoItemReference, jsonObject);
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-39437") ||
			!fragmentRendererContext.isViewMode() || (classPK <= 0)) {

			_render(
				displayObject, httpServletRequest, httpServletResponse,
				(InfoItemRenderer<Object>)tuple.getObject(0), tuple);

			return;
		}

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			StringBundler sb = new StringBundler(10);

			sb.append("<div data-analytics-asset-action=\"view\" ");
			sb.append("data-analytics-asset-id=\"");
			sb.append(classPK);
			sb.append("\" data-analytics-asset-subtype=\"");
			sb.append(_getAnalyticsAssetSubtype(className, displayObject));
			sb.append("\" data-analytics-asset-title=\"");
			sb.append(
				_getAnalyticsAssetTitle(
					className, fragmentRendererContext.getLocale(),
					displayObject));
			sb.append("\" data-analytics-asset-type=\"");
			sb.append(className);
			sb.append("\">");

			printWriter.write(sb.toString());

			_render(
				displayObject, httpServletRequest, httpServletResponse,
				(InfoItemRenderer<Object>)tuple.getObject(0), tuple);

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private String _getAnalyticsAssetSubtype(String className, Object object) {
		InfoItemObjectVariationProvider infoItemObjectVariationProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectVariationProvider.class, className);

		if (infoItemObjectVariationProvider == null) {
			return StringPool.BLANK;
		}

		return infoItemObjectVariationProvider.getInfoItemFormVariationKey(
			object);
	}

	private String _getAnalyticsAssetTitle(
		String className, Locale locale, Object object) {

		InfoItemFieldValuesProvider infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(object);

		if (infoItemFieldValues == null) {
			return StringPool.BLANK;
		}

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("title");

		if (infoFieldValue == null) {
			return StringPool.BLANK;
		}

		return String.valueOf(infoFieldValue.getValue(locale));
	}

	private long _getClassPK(
		InfoItemReference infoItemReference, JSONObject jsonObject) {

		if (jsonObject != null) {
			long classPK = jsonObject.getLong("classPK");

			if (classPK > 0) {
				return classPK;
			}
		}

		if (infoItemReference != null) {
			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)infoItemIdentifier;

				return classPKInfoItemIdentifier.getClassPK();
			}
		}

		return 0;
	}

	private Object _getDisplayObject(
		HttpServletRequest httpServletRequest,
		InfoItemReference infoItemReference, JSONObject jsonObject) {

		long classPK = jsonObject.getLong("classPK");

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if ((classPK <= 0) && (infoItemDetails != null) &&
			(infoItemReference != null) &&
			infoItemReference.equals(infoItemDetails.getInfoItemReference())) {

			Object infoItem = httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

			if (infoItem != null) {
				return infoItem;
			}
		}

		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		if ((classPK <= 0) && Validator.isNull(externalReferenceCode)) {
			return _getInfoItem(infoItemReference);
		}

		String className = jsonObject.getString("className");
		InfoItemIdentifier infoItemIdentifier = null;
		InfoItemObjectProvider<?> infoItemObjectProvider = null;

		if (classPK > 0) {
			infoItemIdentifier = new ClassPKInfoItemIdentifier(classPK);
			infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, className,
					ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);
		}
		else {
			infoItemIdentifier = new ERCInfoItemIdentifier(
				externalReferenceCode,
				jsonObject.getString("scopeExternalReferenceCode", null));
			infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, className,
					ERCInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);
		}

		if (infoItemObjectProvider == null) {
			return null;
		}

		try {
			return infoItemObjectProvider.getInfoItem(infoItemIdentifier);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private JSONObject _getFieldValueJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		return (JSONObject)_fragmentEntryConfigurationParser.getFieldValue(
			getConfigurationJSONObject(fragmentRendererContext),
			fragmentEntryLink.getEditableValuesJSONObject(),
			fragmentRendererContext.getLocale(), "itemSelector");
	}

	private Object _getInfoItem(InfoItemReference infoItemReference) {
		if (infoItemReference == null) {
			return null;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, infoItemReference.getClassName(),
				infoItemIdentifier.getInfoItemServiceFilter());

		if (infoItemObjectProvider == null) {
			return null;
		}

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

	private Tuple _getTuple(
		String className, Class<?> displayObjectClass,
		FragmentRendererContext fragmentRendererContext) {

		List<InfoItemRenderer<?>> infoItemRenderers =
			FragmentRendererUtil.getInfoItemRenderers(
				className, displayObjectClass, _infoItemRendererRegistry);

		if (infoItemRenderers == null) {
			return null;
		}

		InfoItemRenderer<Object> defaultInfoItemRenderer =
			(InfoItemRenderer<Object>)infoItemRenderers.get(0);

		JSONObject jsonObject = _getFieldValueJSONObject(
			fragmentRendererContext);

		if (jsonObject == null) {
			return new Tuple(defaultInfoItemRenderer);
		}

		JSONObject templateJSONObject = jsonObject.getJSONObject("template");

		if (templateJSONObject == null) {
			return new Tuple(defaultInfoItemRenderer);
		}

		String infoItemRendererKey = templateJSONObject.getString(
			"infoItemRendererKey");

		InfoItemRenderer<Object> infoItemRenderer =
			(InfoItemRenderer<Object>)
				_infoItemRendererRegistry.getInfoItemRenderer(
					infoItemRendererKey);

		if (infoItemRenderer != null) {
			return new Tuple(
				infoItemRenderer, templateJSONObject.getString("templateKey"));
		}

		return new Tuple(defaultInfoItemRenderer);
	}

	private boolean _hasPermission(
		HttpServletRequest httpServletRequest, String className,
		Object displayObject) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (Validator.isNull(className) &&
			Validator.isNotNull(infoItemReference.getClassName())) {

			className = infoItemReference.getClassName();
		}

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			(LayoutDisplayPageProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER);

		if (Validator.isNull(className) &&
			(layoutDisplayPageProvider != null)) {

			className = layoutDisplayPageProvider.getClassName();
		}

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if (Validator.isNull(className) && (infoItemDetails != null)) {
			className = infoItemDetails.getClassName();
		}

		try {
			InfoItemPermissionProvider infoItemPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemPermissionProvider.class, className);

			if ((infoItemPermissionProvider != null) &&
				!infoItemPermissionProvider.hasPermission(
					themeDisplay.getPermissionChecker(), displayObject,
					ActionKeys.VIEW)) {

				return false;
			}
		}
		catch (Exception exception) {
			_log.error("Unable to check display object permissions", exception);

			return false;
		}

		return true;
	}

	private void _render(
		Object displayObject, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		InfoItemRenderer<Object> infoItemRenderer, Tuple tuple) {

		if (infoItemRenderer instanceof InfoItemTemplatedRenderer) {
			InfoItemTemplatedRenderer<Object> infoItemTemplatedRenderer =
				(InfoItemTemplatedRenderer<Object>)infoItemRenderer;

			if (tuple.getSize() > 1) {
				infoItemTemplatedRenderer.render(
					displayObject, (String)tuple.getObject(1),
					httpServletRequest, httpServletResponse);
			}
			else {
				infoItemTemplatedRenderer.render(
					displayObject, httpServletRequest, httpServletResponse);
			}
		}
		else {
			infoItemRenderer.render(
				displayObject, httpServletRequest, httpServletResponse);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentObjectFragmentRenderer.class);

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private InfoItemRendererRegistry _infoItemRendererRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

}