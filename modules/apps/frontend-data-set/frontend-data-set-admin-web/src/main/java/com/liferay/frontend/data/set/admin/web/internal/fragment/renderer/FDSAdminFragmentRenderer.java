/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.fragment.renderer;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.data.set.renderer.FDSRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 * @author Marko Cikos
 */
@Component(service = FragmentRenderer.class)
public class FDSAdminFragmentRenderer implements FragmentRenderer {

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
							"label", "data-set-view"
						).put(
							"name", "itemSelector"
						).put(
							"type", "itemSelector"
						).put(
							"typeOptions", JSONUtil.put("itemType", "FDSView")
						)))));
	}

	@Override
	public String getIcon() {
		return "table";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "data-set");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled(
			_portal.getCompanyId(httpServletRequest), "LPS-164563");
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			PrintWriter printWriter = httpServletResponse.getWriter();

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			JSONObject jsonObject =
				(JSONObject)_fragmentEntryConfigurationParser.getFieldValue(
					getConfigurationJSONObject(fragmentRendererContext),
					fragmentEntryLink.getEditableValuesJSONObject(),
					fragmentRendererContext.getLocale(), "itemSelector");

			String externalReferenceCode = jsonObject.getString(
				"externalReferenceCode");

			ObjectEntry dataSetObjectEntry = null;

			if (Validator.isNotNull(externalReferenceCode)) {
				try {
					ObjectDefinition dataSetObjectDefinition =
						_dataSetObjectDefinitionLocalService.
							fetchObjectDefinition(
								fragmentEntryLink.getCompanyId(), "DataSet");

					DefaultObjectEntryManager defaultObjectEntryManager =
						DefaultObjectEntryManagerProvider.provide(
							_dataSetObjectEntryManagerRegistry.
								getObjectEntryManager(
									dataSetObjectDefinition.getStorageType()));

					dataSetObjectEntry =
						defaultObjectEntryManager.getObjectEntry(
							fragmentEntryLink.getCompanyId(),
							new DefaultDTOConverterContext(
								false, null, null, null, null,
								LocaleUtil.getMostRelevantLocale(), null, null),
							externalReferenceCode, dataSetObjectDefinition,
							null);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get frontend data set view with " +
								"external reference code " +
									externalReferenceCode,
							exception);
					}
				}
			}

			if ((dataSetObjectEntry == null) &&
				fragmentRendererContext.isEditMode()) {

				printWriter.write(
					StringBundler.concat(
						"<div class=\"portlet-msg-info\">",
						_language.get(
							httpServletRequest, "select-a-data-set-view"),
						"</div>"));
			}

			if (dataSetObjectEntry == null) {
				return;
			}

			if (!FeatureFlagManagerUtil.isEnabled(
					_portal.getCompanyId(httpServletRequest), "LPD-38564")) {

				_fdsRenderer.render(
					HashMapBuilder.<String, Object>put(
						"namespace",
						fragmentRendererContext.getFragmentElementId()
					).put(
						"style", "fluid"
					).build(),
					fragmentRendererContext.getFragmentElementId(),
					externalReferenceCode, httpServletRequest,
					httpServletResponse, true, null, printWriter);

				return;
			}

			DefaultFragmentEntryProcessorContext
				defaultFragmentEntryProcessorContext =
					_getDefaultFragmentEntryProcessorContext(
						fragmentRendererContext, httpServletRequest,
						httpServletResponse);

			boolean hasTokens = _hasTokens(
				externalReferenceCode, httpServletRequest);

			if (fragmentRendererContext.isEditMode() && hasTokens) {
				_renderMappingUI(
					defaultFragmentEntryProcessorContext, externalReferenceCode,
					fragmentEntryLink, httpServletRequest);

				printWriter.write(
					_processFragmentEntryLinkHTML(
						defaultFragmentEntryProcessorContext,
						fragmentRendererContext));
			}

			if (_isResolved(
					defaultFragmentEntryProcessorContext, externalReferenceCode,
					fragmentEntryLink, httpServletRequest)) {

				printWriter.write("<div>");

				_fdsRenderer.render(
					HashMapBuilder.<String, Object>put(
						"namespace",
						fragmentRendererContext.getFragmentElementId()
					).put(
						"style", "fluid"
					).put(
						"tokenResolutions",
						() -> {
							if (hasTokens) {
								return _getTokenResolutionsJSONObject(
									defaultFragmentEntryProcessorContext,
									externalReferenceCode, fragmentEntryLink,
									httpServletRequest);
							}

							return null;
						}
					).build(),
					fragmentRendererContext.getFragmentElementId(),
					externalReferenceCode, httpServletRequest,
					httpServletResponse, true, null, printWriter);

				printWriter.write("</div>");
			}
		}
		catch (Exception exception) {
			_log.error("Unable to render frontend data set view", exception);

			throw new IOException(exception);
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}
	}

	private DefaultFragmentEntryProcessorContext
		_getDefaultFragmentEntryProcessorContext(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					httpServletRequest, httpServletResponse,
					fragmentRendererContext.getMode(),
					fragmentRendererContext.getLocale());

		defaultFragmentEntryProcessorContext.setAttributes(
			fragmentRendererContext.getAttributes());
		defaultFragmentEntryProcessorContext.setContextInfoItemReference(
			fragmentRendererContext.getContextInfoItemReference());
		defaultFragmentEntryProcessorContext.setFragmentElementId(
			fragmentRendererContext.getFragmentElementId());
		defaultFragmentEntryProcessorContext.setInfoForm(
			fragmentRendererContext.getInfoForm());
		defaultFragmentEntryProcessorContext.setPreviewClassNameId(
			fragmentRendererContext.getPreviewClassNameId());
		defaultFragmentEntryProcessorContext.setPreviewClassPK(
			fragmentRendererContext.getPreviewClassPK());
		defaultFragmentEntryProcessorContext.setPreviewType(
			fragmentRendererContext.getPreviewType());
		defaultFragmentEntryProcessorContext.setPreviewVersion(
			fragmentRendererContext.getPreviewVersion());
		defaultFragmentEntryProcessorContext.setSegmentsEntryIds(
			fragmentRendererContext.getSegmentsEntryIds());

		return defaultFragmentEntryProcessorContext;
	}

	private Set<String> _getTokenNames(
		String externalReferenceCode, HttpServletRequest httpServletRequest) {

		Set<String> tokenNames = new HashSet<>();

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, false, null));

		while (matcher.find()) {
			tokenNames.add(matcher.group(1));
		}

		return tokenNames;
	}

	private JSONObject _getTokenResolutionsJSONObject(
		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext,
		String externalReferenceCode, FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest) {

		Set<String> tokenNames = _getTokenNames(
			externalReferenceCode, httpServletRequest);

		JSONObject tokenResolutionsJSONObject = _jsonFactory.createJSONObject();

		for (String tokenName : tokenNames) {
			String tokenValue = _getTokenValue(
				defaultFragmentEntryProcessorContext, fragmentEntryLink,
				httpServletRequest, tokenName);

			if (Validator.isNotNull(tokenValue)) {
				tokenResolutionsJSONObject.put(tokenName, tokenValue);
			}
		}

		return tokenResolutionsJSONObject;
	}

	private String _getTokenValue(
		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext,
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest, String tokenName) {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		if (editableValuesJSONObject == null) {
			return null;
		}

		JSONObject tokenValuesJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		if (tokenValuesJSONObject == null) {
			return null;
		}

		JSONObject tokenValueJSONObject = tokenValuesJSONObject.getJSONObject(
			tokenName);

		if (tokenValueJSONObject == null) {
			return null;
		}

		String tokenValue = tokenValueJSONObject.getString(
			LanguageUtil.getLanguageId(httpServletRequest));

		if (Validator.isNull(tokenValue)) {
			try {
				tokenValue = String.valueOf(
					_fragmentEntryProcessorHelper.getFieldValue(
						tokenValueJSONObject, new HashMap<>(),
						defaultFragmentEntryProcessorContext));
			}
			catch (PortalException portalException) {
				_log.error("Unable to get token value", portalException);
			}
		}

		if (Validator.isNull(tokenValue) &&
			tokenName.equals("externalReferenceCode")) {

			tokenValue = tokenValueJSONObject.getString(
				"externalReferenceCode");
		}

		if (Validator.isNull(tokenValue)) {
			tokenValue = tokenValueJSONObject.getString("classPK");
		}

		if (Validator.isNull(tokenValue)) {
			InfoItemReference infoItemReference =
				(InfoItemReference)httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

			if (infoItemReference == null) {
				return null;
			}

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
				return null;
			}

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			tokenValue = String.valueOf(classPKInfoItemIdentifier.getClassPK());
		}

		return tokenValue;
	}

	private boolean _hasTokens(
		String externalReferenceCode, HttpServletRequest httpServletRequest) {

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, false, null));

		return matcher.find();
	}

	private boolean _isResolved(
		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext,
		String externalReferenceCode, FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest) {

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, true,
				_getTokenResolutionsJSONObject(
					defaultFragmentEntryProcessorContext, externalReferenceCode,
					fragmentEntryLink, httpServletRequest)));

		return !matcher.find();
	}

	private boolean _isResolvedToken(String url, String tokenName) {
		return !url.contains(
			StringPool.OPEN_CURLY_BRACE + tokenName +
				StringPool.CLOSE_CURLY_BRACE);
	}

	private String _processFragmentEntryLinkHTML(
			DefaultFragmentEntryProcessorContext
				defaultFragmentEntryProcessorContext,
			FragmentRendererContext fragmentRendererContext)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		String html = StringPool.BLANK;

		if (Validator.isNotNull(fragmentEntryLink.getHtml()) ||
			Validator.isNotNull(fragmentEntryLink.getEditableValues())) {

			html = _fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext);
		}

		return html;
	}

	private void _renderMappingUI(
		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext,
		String externalReferenceCode, FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest) {

		StringBundler htmlSB = new StringBundler();

		htmlSB.append("<div class=\"p-2\" data-fragment-namespace=");
		htmlSB.append("\"${fragmentEntryLinkNamespace}\">");

		for (String tokenName :
				_getTokenNames(externalReferenceCode, httpServletRequest)) {

			htmlSB.append("<div><span><strong>");
			htmlSB.append(tokenName);

			if (!_isResolvedToken(
					_fdsRenderer.getFDSAPIURL(
						externalReferenceCode, httpServletRequest, true, null),
					tokenName)) {

				htmlSB.append(" (*) ");
			}

			htmlSB.append(": </strong></span>");
			htmlSB.append("<span class=\"navbar-text-truncate\"");
			htmlSB.append("data-lfr-editable-id=\"");
			htmlSB.append(tokenName);
			htmlSB.append("\" data-lfr-editable-type=\"text\">\n\t{");
			htmlSB.append(tokenName);
			htmlSB.append("}\n</span>");
			htmlSB.append("</div>");
		}

		htmlSB.append("<span class=\"workflow-status\"><strong class=\"label ");

		if (_isResolved(
				defaultFragmentEntryProcessorContext, externalReferenceCode,
				fragmentEntryLink, httpServletRequest)) {

			htmlSB.append("label-success\">Resolved");
		}
		else {
			htmlSB.append("label-info\">Unresolved");
		}

		htmlSB.append("</strong></span> Data Set API URL: ");

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, true,
				_getTokenResolutionsJSONObject(
					defaultFragmentEntryProcessorContext, externalReferenceCode,
					fragmentEntryLink, httpServletRequest)));

		htmlSB.append(
			matcher.replaceAll(
				match -> {
					String tokenName = match.group(1);

					String tokenValue = _getTokenValue(
						defaultFragmentEntryProcessorContext, fragmentEntryLink,
						httpServletRequest, tokenName);

					if (Validator.isNull(tokenValue)) {
						tokenValue = "{" + tokenName + "}";
					}

					String editableMarkup =
						"<span><strong>" + tokenValue + "</strong></span>";

					return Matcher.quoteReplacement(editableMarkup);
				}));

		htmlSB.append("</div>");

		fragmentEntryLink.setHtml(htmlSB.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSAdminFragmentRenderer.class);

	private static final Pattern _pattern = Pattern.compile("\\{(.*?)\\}");

	@Reference
	private ObjectDefinitionLocalService _dataSetObjectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _dataSetObjectEntryManagerRegistry;

	@Reference
	private FDSRenderer _fdsRenderer;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}