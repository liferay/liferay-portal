/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.LayoutObjectReferenceUtil;
import com.liferay.layout.list.retriever.DefaultLayoutListRetrieverContext;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Verónica González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_collection_item_count"
	},
	service = MVCResourceCommand.class
)
public class GetCollectionItemCountMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String layoutObjectReference = ParamUtil.getString(
			resourceRequest, "layoutObjectReference");

		try {
			jsonObject = _getCollectionItemCountJSONObject(
				_portal.getHttpServletRequest(resourceRequest),
				layoutObjectReference);
		}
		catch (Exception exception) {
			_log.error("Unable to get collection item count", exception);

			jsonObject.put(
				"error",
				_language.get(
					themeDisplay.getRequest(), "an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	private JSONObject _getCollectionItemCountJSONObject(
			HttpServletRequest httpServletRequest, String layoutObjectReference)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		JSONObject layoutObjectReferenceJSONObject =
			_jsonFactory.createJSONObject(layoutObjectReference);

		String type = layoutObjectReferenceJSONObject.getString("type");

		LayoutListRetriever<?, ListObjectReference> layoutListRetriever =
			(LayoutListRetriever<?, ListObjectReference>)
				_layoutListRetrieverRegistry.getLayoutListRetriever(type);

		if (layoutListRetriever != null) {
			ListObjectReferenceFactory<?> listObjectReferenceFactory =
				_listObjectReferenceFactoryRegistry.getListObjectReference(
					type);

			if (listObjectReferenceFactory != null) {
				DefaultLayoutListRetrieverContext
					defaultLayoutListRetrieverContext =
						new DefaultLayoutListRetrieverContext();

				defaultLayoutListRetrieverContext.setConfiguration(
					LayoutObjectReferenceUtil.getConfiguration(
						layoutObjectReferenceJSONObject));

				Object infoItem = _getInfoItem(httpServletRequest);

				if (infoItem != null) {
					defaultLayoutListRetrieverContext.setContextObject(
						infoItem);
				}

				InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
					listObjectReferenceFactory.getListObjectReference(
						layoutObjectReferenceJSONObject),
					defaultLayoutListRetrieverContext);

				jsonObject.put("totalNumberOfItems", infoPage.getTotalCount());
			}
		}

		return jsonObject;
	}

	private Object _getInfoItem(HttpServletRequest httpServletRequest) {
		String className = _portal.fetchClassName(
			ParamUtil.getLong(httpServletRequest, "classNameId"));
		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

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

	private static final Log _log = LogFactoryUtil.getLog(
		GetCollectionItemCountMVCResourceCommand.class);

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