/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.helper;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.type.WebImage;
import com.liferay.layout.helper.LayoutWarningMessageHelper;
import com.liferay.layout.list.retriever.DefaultLayoutListRetrieverContext;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Molina
 */
@Component(service = LayoutWarningMessageHelper.class)
public class LayoutWarningMessageHelperImpl
	implements LayoutWarningMessageHelper {

	@Override
	public JSONObject getCollectionWarningMessageJSONObject(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem,
			HttpServletRequest httpServletRequest)
		throws Exception {

		int totalCount = _getTotalCount(collectionStyledLayoutStructureItem);

		if (!Objects.equals(
				collectionStyledLayoutStructureItem.getPaginationType(),
				CollectionPaginationUtil.PAGINATION_TYPE_NONE) ||
			(!collectionStyledLayoutStructureItem.isDisplayAllItems() &&
			 (totalCount <= PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA))) {

			return _jsonFactory.createJSONObject();
		}

		String mode = ParamUtil.getString(
			_portal.getOriginalServletRequest(httpServletRequest), "p_l_mode",
			Constants.VIEW);

		if (Objects.equals(mode, Constants.VIEW)) {
			return JSONUtil.put(
				"description",
				_language.format(
					httpServletRequest,
					StringBundler.concat(
						"this-setting-can-affect-page-performance-severely-if-",
						"the-number-of-collection-items-is-above-x.-we-",
						"strongly-recommend-using-pagination-instead"),
					PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA)
			).put(
				"title",
				_language.get(httpServletRequest, "pagination-is-disabled")
			);
		}

		return JSONUtil.put(
			"description",
			_language.format(
				httpServletRequest,
				"this-setting-can-affect-page-performance-severely-if-the-" +
					"number-of-collection-items-is-above-x.-we-strongly-" +
						"recommend-using-pagination-instead",
				PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA));
	}

	@Override
	public JSONObject getFragmentWarningMessageJsonObject(
		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			return _jsonFactory.createJSONObject();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (_showWarningMessage(
					httpServletRequest, fragmentStyledLayoutStructureItem,
					themeDisplay) ||
				_showWarningMessage(
					httpServletRequest, httpServletResponse, fragmentEntryLink,
					themeDisplay)) {

				return JSONUtil.put(
					"description",
					_language.get(
						themeDisplay.getLocale(),
						"please-consider-configuring-adaptive-media-lazy-" +
							"loading-or-reducing-the-image-size")
				).put(
					"title",
					_language.get(
						httpServletRequest, "big-image-file-size-used")
				);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONUtil.put(
				"description",
				_language.get(
					themeDisplay.getLocale(), "an-unexpected-error-occurred"));
		}

		return _jsonFactory.createJSONObject();
	}

	private boolean _exceedsFileSize(long fileEntryId) throws Exception {
		if (fileEntryId <= 0) {
			return false;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		long size = fileEntry.getSize();

		if (size < _MAX_SIZE) {
			return false;
		}

		return true;
	}

	private Map<String, String[]> _getConfiguration(
		JSONObject layoutObjectReferenceJSONObject) {

		JSONObject configurationJSONObject =
			layoutObjectReferenceJSONObject.getJSONObject("config");

		if (configurationJSONObject == null) {
			return null;
		}

		Map<String, String[]> configuration = new HashMap<>();

		for (String key : configurationJSONObject.keySet()) {
			List<String> values = new ArrayList<>();

			Object object = configurationJSONObject.get(key);

			if (object instanceof JSONArray) {
				JSONArray jsonArray = configurationJSONObject.getJSONArray(key);

				for (int i = 0; i < jsonArray.length(); i++) {
					values.add(jsonArray.getString(i));
				}
			}
			else {
				values.add(String.valueOf(object));
			}

			configuration.put(key, values.toArray(new String[0]));
		}

		return configuration;
	}

	private long _getFileEntryId(
			JSONObject editableValueJSONObject,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Locale locale)
		throws Exception {

		if (_fragmentEntryProcessorHelper.isMapped(editableValueJSONObject) ||
			_fragmentEntryProcessorHelper.isMappedCollection(
				editableValueJSONObject) ||
			_fragmentEntryProcessorHelper.isMappedDisplayPage(
				editableValueJSONObject)) {

			Object fieldValue = _fragmentEntryProcessorHelper.getFieldValue(
				editableValueJSONObject, new HashMap<>(),
				new DefaultFragmentEntryProcessorContext(
					httpServletRequest, httpServletResponse,
					FragmentEntryLinkConstants.VIEW, locale));

			if (fieldValue == null) {
				return 0;
			}

			if (fieldValue instanceof JSONObject) {
				JSONObject fieldValueJSONObject = (JSONObject)fieldValue;

				if (fieldValueJSONObject.has("className") &&
					fieldValueJSONObject.has("classPK")) {

					return _fragmentEntryProcessorHelper.getFileEntryId(
						fieldValueJSONObject.getString("className"),
						fieldValueJSONObject.getLong("classPK"));
				}
				else if (fieldValueJSONObject.has("fileEntryId")) {
					return fieldValueJSONObject.getLong("fileEntryId");
				}
			}
			else if (fieldValue instanceof WebImage) {
				WebImage webImage = (WebImage)fieldValue;

				return _fragmentEntryProcessorHelper.getFileEntryId(webImage);
			}

			return 0;
		}

		String value = _fragmentEntryProcessorHelper.getEditableValue(
			editableValueJSONObject, locale);

		if (JSONUtil.isJSONObject(value)) {
			try {
				JSONObject imageJSONObject = _jsonFactory.createJSONObject(
					value);

				return imageJSONObject.getLong("fileEntryId");
			}
			catch (JSONException jsonException) {
				_log.error(
					"Unable to parse JSON value " + value, jsonException);
			}
		}

		return 0;
	}

	private long _getFileEntryId(
			String fieldId, InfoItemDetails infoItemDetails,
			ThemeDisplay themeDisplay)
		throws Exception {

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

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		return _fragmentEntryProcessorHelper.getFileEntryId(
			_portal.getClassNameId(infoItemReference.getClassName()),
			classPKInfoItemIdentifier.getClassPK(), fieldId,
			themeDisplay.getLocale());
	}

	private Object _getInfoItem(JSONObject layoutObjectReferenceJSONObject) {
		String className = _portal.fetchClassName(
			layoutObjectReferenceJSONObject.getLong("classNameId"));
		long classPK = layoutObjectReferenceJSONObject.getLong("classPK");

		if (Validator.isNull(className) && (classPK <= 0)) {
			return null;
		}

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			(InfoItemObjectProvider<Object>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, className,
					ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		if (infoItemObjectProvider == null) {
			return null;
		}

		try {
			return infoItemObjectProvider.getInfoItem(
				new ClassPKInfoItemIdentifier(classPK));
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}
		}

		return null;
	}

	private int _getTotalCount(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		JSONObject layoutObjectReferenceJSONObject =
			collectionStyledLayoutStructureItem.getCollectionJSONObject();

		String type = layoutObjectReferenceJSONObject.getString("type");

		LayoutListRetriever<?, ListObjectReference> layoutListRetriever =
			(LayoutListRetriever<?, ListObjectReference>)
				_layoutListRetrieverRegistry.getLayoutListRetriever(type);

		if (layoutListRetriever == null) {
			throw new Exception(
				"Layout list retriever is null for type " + type);
		}

		ListObjectReferenceFactory<?> listObjectReferenceFactory =
			_listObjectReferenceFactoryRegistry.getListObjectReference(type);

		if (listObjectReferenceFactory == null) {
			throw new Exception(
				"List object reference factory is null for type " + type);
		}

		DefaultLayoutListRetrieverContext defaultLayoutListRetrieverContext =
			new DefaultLayoutListRetrieverContext();

		defaultLayoutListRetrieverContext.setConfiguration(
			_getConfiguration(layoutObjectReferenceJSONObject));

		Object infoItem = _getInfoItem(layoutObjectReferenceJSONObject);

		if (infoItem != null) {
			defaultLayoutListRetrieverContext.setContextObject(infoItem);
		}

		InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
			listObjectReferenceFactory.getListObjectReference(
				layoutObjectReferenceJSONObject),
			defaultLayoutListRetrieverContext);

		return Math.min(
			collectionStyledLayoutStructureItem.getNumberOfItems(),
			infoPage.getTotalCount());
	}

	private boolean _showWarningMessage(
			HttpServletRequest httpServletRequest,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONObject backgroundImageJSONObject =
			fragmentStyledLayoutStructureItem.getBackgroundImageJSONObject();

		long fileEntryId = 0;

		if (backgroundImageJSONObject.has("fileEntryId")) {
			fileEntryId = backgroundImageJSONObject.getLong("fileEntryId");
		}
		else if (backgroundImageJSONObject.has("classNameId") &&
				 backgroundImageJSONObject.has("classPK") &&
				 backgroundImageJSONObject.has("fieldId")) {

			fileEntryId = _fragmentEntryProcessorHelper.getFileEntryId(
				backgroundImageJSONObject.getLong("classNameId"),
				backgroundImageJSONObject.getLong("classPK"),
				backgroundImageJSONObject.getString("fieldId"),
				themeDisplay.getLocale());
		}
		else if (backgroundImageJSONObject.has("collectionFieldId")) {
			fileEntryId = _fragmentEntryProcessorHelper.getFileEntryId(
				(InfoItemReference)httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE),
				backgroundImageJSONObject.getString("collectionFieldId"),
				themeDisplay.getLocale());
		}
		else if (backgroundImageJSONObject.has("mappedField")) {
			fileEntryId = _getFileEntryId(
				backgroundImageJSONObject.getString("mappedField"),
				(InfoItemDetails)httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_DETAILS),
				themeDisplay);
		}

		return _exceedsFileSize(fileEntryId);
	}

	private boolean _showWarningMessage(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			FragmentEntryLink fragmentEntryLink, ThemeDisplay themeDisplay)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		for (String fragmentEntryProcessorKey :
				_FRAGMENT_ENTRY_PROCESSOR_KEYS) {

			JSONObject editableValuesJSONObject = jsonObject.getJSONObject(
				fragmentEntryProcessorKey);

			if (editableValuesJSONObject == null) {
				continue;
			}

			Iterator<String> editableKeysIterator =
				editableValuesJSONObject.keys();

			while (editableKeysIterator.hasNext()) {
				JSONObject editableValueJSONObject =
					editableValuesJSONObject.getJSONObject(
						editableKeysIterator.next());

				JSONObject configJSONObject =
					editableValueJSONObject.getJSONObject("config");

				if (configJSONObject.getBoolean("lazyLoading")) {
					continue;
				}

				if (_exceedsFileSize(
						_getFileEntryId(
							editableValueJSONObject, httpServletRequest,
							httpServletResponse, themeDisplay.getLocale()))) {

					return true;
				}
			}
		}

		return false;
	}

	private static final String[] _FRAGMENT_ENTRY_PROCESSOR_KEYS = {
		FragmentEntryProcessorConstants.
			KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	};

	private static final int _MAX_SIZE = 500 * 1024;

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutWarningMessageHelperImpl.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutListRetrieverRegistry _layoutListRetrieverRegistry;

	@Reference
	private ListObjectReferenceFactoryRegistry
		_listObjectReferenceFactoryRegistry;

	@Reference
	private Portal _portal;

}