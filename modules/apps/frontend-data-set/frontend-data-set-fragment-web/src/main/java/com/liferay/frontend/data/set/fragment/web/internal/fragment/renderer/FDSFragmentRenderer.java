/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.fragment.web.internal.fragment.renderer;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.data.set.renderer.FDSRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
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
public class FDSFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
	}

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(getClass(), "fds/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
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

			JSONObject configurationJSONObject = getConfigurationJSONObject(
				fragmentRendererContext);

			JSONObject itemSelectorJSONObject =
				(JSONObject)_fragmentEntryConfigurationParser.getFieldValue(
					configurationJSONObject,
					fragmentEntryLink.getEditableValuesJSONObject(),
					fragmentRendererContext.getLocale(), "itemSelector");

			String externalReferenceCode = itemSelectorJSONObject.getString(
				"externalReferenceCode");

			ObjectEntry dataSetObjectEntry = null;

			if (Validator.isNotNull(externalReferenceCode)) {
				try {
					ObjectDefinition dataSetObjectDefinition =
						_dataSetObjectDefinitionLocalService.
							fetchObjectDefinitionByExternalReferenceCode(
								"L_DATA_SET", fragmentEntryLink.getCompanyId());

					DefaultObjectEntryManager defaultObjectEntryManager =
						DefaultObjectEntryManagerProvider.provide(
							_dataSetObjectEntryManagerRegistry.
								getObjectEntryManager(
									dataSetObjectDefinition.getCompanyId(),
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
							"Unable to get frontend data set with external " +
								"reference code " + externalReferenceCode,
							exception);
					}
				}
			}

			if ((dataSetObjectEntry == null) &&
				fragmentRendererContext.isEditMode()) {

				printWriter.write(
					StringBundler.concat(
						"<div class=\"portlet-msg-info\">",
						_language.get(httpServletRequest, "select-a-data-set"),
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

			String componentId = externalReferenceCode;

			boolean hasTokens = _hasTokens(
				externalReferenceCode, httpServletRequest);

			JSONObject apiURLTokenMappingsJSONObject =
				_getAPIURLTokenMappingsJSONObject(
					(String)_fragmentEntryConfigurationParser.getFieldValue(
						configurationJSONObject,
						fragmentEntryLink.getEditableValuesJSONObject(),
						fragmentRendererContext.getLocale(),
						"apiURLTokenMappings"));

			JSONObject tokenResolutionsJSONObject =
				_getTokenResolutionsJSONObject(
					apiURLTokenMappingsJSONObject, externalReferenceCode,
					httpServletRequest);

			Set<String> unresolvedTokenNames = _getUnresolvedTokenNames(
				externalReferenceCode, httpServletRequest,
				tokenResolutionsJSONObject);

			boolean resolved = unresolvedTokenNames.isEmpty();

			if (fragmentRendererContext.isEditMode()) {
				if (hasTokens) {
					_writeAutoResolvedTokenNames(
						externalReferenceCode, fragmentEntryLink,
						httpServletRequest);
				}

				componentId = StringBundler.concat(
					componentId, StringPool.DASH,
					fragmentEntryLink.getFragmentEntryLinkId());

				_writeDestroyPreviousComponentScript(
					componentId, fragmentEntryLink, httpServletRequest,
					printWriter);

				if (hasTokens && !resolved) {
					Set<String> unresolvedContextTokenNames =
						_getUnresolvedContextTokenNames(
							apiURLTokenMappingsJSONObject, httpServletRequest,
							unresolvedTokenNames);

					_reactRenderer.renderReact(
						new ComponentDescriptor(
							"{UnresolvedDataSetPreview} from " +
								"frontend-data-set-fragment-web",
							componentId, null, true),
						HashMapBuilder.<String, Object>put(
							"apiURL",
							_fdsRenderer.getFDSAPIURL(
								externalReferenceCode, httpServletRequest, true,
								tokenResolutionsJSONObject)
						).put(
							"hasUnmappedTokens",
							unresolvedTokenNames.size() >
								unresolvedContextTokenNames.size()
						).put(
							"hasUnresolvedContextTokens",
							!unresolvedContextTokenNames.isEmpty()
						).build(),
						httpServletRequest, printWriter);
				}
			}

			if (resolved) {
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
								return tokenResolutionsJSONObject;
							}

							return null;
						}
					).build(),
					componentId, externalReferenceCode, httpServletRequest,
					httpServletResponse, true, null, printWriter);

				printWriter.write("</div>");
			}
		}
		catch (Exception exception) {
			_log.error("Unable to render frontend data set", exception);

			throw new IOException(exception);
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}
	}

	private JSONObject _getAPIURLTokenMappingsJSONObject(String value) {
		if (Validator.isNull(value)) {
			return _jsonFactory.createJSONObject();
		}

		try {
			return _jsonFactory.createJSONObject(value);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to create JSON object from: " + value,
					jsonException);
			}
		}

		return _jsonFactory.createJSONObject();
	}

	private Set<String> _getAutoResolvedTokenNames(
		String externalReferenceCode, HttpServletRequest httpServletRequest) {

		Set<String> tokenNames = _getTokenNames(
			externalReferenceCode, httpServletRequest);

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, true, null));

		while (matcher.find()) {
			tokenNames.remove(matcher.group(1));
		}

		return tokenNames;
	}

	private String _getExternalReferenceCode(InfoItemDetails infoItemDetails) {
		if (infoItemDetails == null) {
			return null;
		}

		InfoItemReference infoItemReference =
			infoItemDetails.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {
			return null;
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		return ercInfoItemIdentifier.getExternalReferenceCode();
	}

	private InfoItemDetails _getInfoItemDetails(
		HttpServletRequest httpServletRequest,
		InfoItemReference infoItemReference) {

		String className = infoItemReference.getClassName();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				infoItemIdentifier.getInfoItemServiceFilter());

		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className);

		if ((infoItemObjectProvider == null) ||
			(infoItemDetailsProvider == null)) {

			return null;
		}

		try {
			return infoItemDetailsProvider.getInfoItemDetails(
				_portal.getScopeGroupId(httpServletRequest),
				ERCInfoItemIdentifier.class,
				infoItemObjectProvider.getInfoItem(infoItemIdentifier));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
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
		JSONObject apiURLTokenMappingsJSONObject, String externalReferenceCode,
		HttpServletRequest httpServletRequest) {

		JSONObject tokenResolutionsJSONObject = _jsonFactory.createJSONObject();

		Set<String> autoResolvedTokenNames = _getAutoResolvedTokenNames(
			externalReferenceCode, httpServletRequest);
		Set<String> tokenNames = _getTokenNames(
			externalReferenceCode, httpServletRequest);

		for (String tokenName : tokenNames) {
			String tokenValue = _getTokenValue(
				apiURLTokenMappingsJSONObject, httpServletRequest, tokenName);

			if (Validator.isNotNull(tokenValue)) {
				tokenResolutionsJSONObject.put(
					tokenName, HtmlUtil.escape(tokenValue));
			}
			else if (autoResolvedTokenNames.contains(tokenName) &&
					 _hasManualMapping(
						 apiURLTokenMappingsJSONObject, tokenName)) {

				// The user picked a manual mapping and left it empty. Mark the
				// token with an empty JSON object so the URL builder leaves it
				// unresolved instead of applying the automatic resolution.

				tokenResolutionsJSONObject.put(
					tokenName, _jsonFactory.createJSONObject());
			}
		}

		return tokenResolutionsJSONObject;
	}

	private String _getTokenValue(
		JSONObject apiURLTokenMappingsJSONObject,
		HttpServletRequest httpServletRequest, String tokenName) {

		JSONObject mappingJSONObject =
			apiURLTokenMappingsJSONObject.getJSONObject(tokenName);

		if (mappingJSONObject == null) {
			return apiURLTokenMappingsJSONObject.getString(tokenName);
		}

		String mappingMode = mappingJSONObject.getString("mappingMode");

		if (Objects.equals(mappingMode, "autoResolved")) {
			return null;
		}

		String fieldId = mappingJSONObject.getString("fieldId");

		if (Validator.isNull(fieldId)) {
			return null;
		}

		if (Objects.equals(mappingMode, "context")) {
			InfoItemReference infoItemReference =
				(InfoItemReference)httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

			if (infoItemReference == null) {
				return null;
			}

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (Objects.equals(fieldId, "externalReferenceCode")) {
				if (infoItemIdentifier instanceof ERCInfoItemIdentifier) {
					ERCInfoItemIdentifier ercInfoItemIdentifier =
						(ERCInfoItemIdentifier)infoItemIdentifier;

					return ercInfoItemIdentifier.getExternalReferenceCode();
				}

				return _getExternalReferenceCode(
					_getInfoItemDetails(httpServletRequest, infoItemReference));
			}

			if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)infoItemIdentifier;

				return String.valueOf(classPKInfoItemIdentifier.getClassPK());
			}

			return null;
		}

		if (Objects.equals(fieldId, "externalReferenceCode")) {
			return mappingJSONObject.getString("externalReferenceCode");
		}

		return mappingJSONObject.getString("classPK");
	}

	private Set<String> _getUnresolvedContextTokenNames(
		JSONObject apiURLTokenMappingsJSONObject,
		HttpServletRequest httpServletRequest,
		Set<String> unresolvedTokenNames) {

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (infoItemReference != null) {
			return Collections.emptySet();
		}

		Set<String> unresolvedContextTokenNames = new HashSet<>();

		for (String unresolvedTokenName : unresolvedTokenNames) {
			JSONObject mappingJSONObject =
				apiURLTokenMappingsJSONObject.getJSONObject(
					unresolvedTokenName);

			if (mappingJSONObject == null) {
				continue;
			}

			if (Objects.equals(
					mappingJSONObject.getString("mappingMode"), "context") &&
				Validator.isNotNull(mappingJSONObject.getString("fieldId"))) {

				unresolvedContextTokenNames.add(unresolvedTokenName);
			}
		}

		return unresolvedContextTokenNames;
	}

	private Set<String> _getUnresolvedTokenNames(
		String externalReferenceCode, HttpServletRequest httpServletRequest,
		JSONObject tokenResolutionsJSONObject) {

		Set<String> unresolvedTokenNames = new HashSet<>();

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, true,
				tokenResolutionsJSONObject));

		while (matcher.find()) {
			unresolvedTokenNames.add(matcher.group(1));
		}

		return unresolvedTokenNames;
	}

	private boolean _hasManualMapping(
		JSONObject apiURLTokenMappingsJSONObject, String tokenName) {

		if (!apiURLTokenMappingsJSONObject.has(tokenName)) {
			return false;
		}

		JSONObject mappingJSONObject =
			apiURLTokenMappingsJSONObject.getJSONObject(tokenName);

		if (mappingJSONObject == null) {
			return true;
		}

		return !Objects.equals(
			mappingJSONObject.getString("mappingMode"), "autoResolved");
	}

	private boolean _hasTokens(
		String externalReferenceCode, HttpServletRequest httpServletRequest) {

		Matcher matcher = _pattern.matcher(
			_fdsRenderer.getFDSAPIURL(
				externalReferenceCode, httpServletRequest, false, null));

		return matcher.find();
	}

	private void _writeAutoResolvedTokenNames(
		String externalReferenceCode, FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest) {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		if (editableValuesJSONObject == null) {
			editableValuesJSONObject = _jsonFactory.createJSONObject();

			fragmentEntryLink.setEditableValues(
				editableValuesJSONObject.toString());
		}

		JSONObject configurationJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (configurationJSONObject == null) {
			configurationJSONObject = _jsonFactory.createJSONObject();

			editableValuesJSONObject.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				configurationJSONObject);
		}

		try {
			JSONArray jsonArray = JSONUtil.toJSONArray(
				_getAutoResolvedTokenNames(
					externalReferenceCode, httpServletRequest),
				autoResolvedTokenName -> autoResolvedTokenName);

			configurationJSONObject.put(
				"autoResolvedTokenNames", jsonArray.toString());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to write auto resolved token names", exception);
			}
		}
	}

	private void _writeDestroyPreviousComponentScript(
			String componentId, FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest, PrintWriter printWriter)
		throws IOException {

		ScriptData scriptData = new ScriptData();

		scriptData.append(
			_portal.getPortletId(httpServletRequest),
			StringUtil.replace(
				StringUtil.read(
					getClass(), "dependencies/destroy_previous_component.js"),
				new String[] {"[$COMPONENT_ID$]", "[$FRAGMENT_ENTRY_LINK_ID$]"},
				new String[] {
					HtmlUtil.escapeJS(componentId),
					String.valueOf(fragmentEntryLink.getFragmentEntryLinkId())
				}),
			null, ScriptData.ModulesType.ES6);

		scriptData.writeTo(printWriter);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSFragmentRenderer.class);

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
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

}