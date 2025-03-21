/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.processor;

import com.liferay.fragment.constants.FragmentWebKeys;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.CSSFragmentEntryProcessor;
import com.liferay.fragment.processor.DefaultEditableValuesFragmentEntryProcessor;
import com.liferay.fragment.processor.DocumentFragmentEntryProcessor;
import com.liferay.fragment.processor.DocumentFragmentEntryValidator;
import com.liferay.fragment.processor.FragmentEntryAutocompleteContributor;
import com.liferay.fragment.processor.FragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.processor.FragmentEntryValidator;
import com.liferay.fragment.renderer.FragmentPortletRenderer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.impl.DefaultLayoutTypeAccessPolicyImpl;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = FragmentEntryProcessorRegistry.class)
public class FragmentEntryProcessorRegistryImpl
	implements FragmentEntryProcessorRegistry {

	@Override
	public JSONArray getAvailableTagsJSONArray() {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (FragmentEntryAutocompleteContributor
				fragmentEntryAutocompleteContributor :
					_fragmentEntryAutocompleteContributors) {

			JSONArray availableTagsJSONArray =
				fragmentEntryAutocompleteContributor.
					getAvailableTagsJSONArray();

			if (availableTagsJSONArray == null) {
				continue;
			}

			for (int i = 0; i < availableTagsJSONArray.length(); i++) {
				jsonArray.put(availableTagsJSONArray.getJSONObject(i));
			}
		}

		return jsonArray;
	}

	@Override
	public JSONArray getDataAttributesJSONArray() {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (FragmentEntryProcessor fragmentEntryProcessor :
				_fragmentEntryProcessors) {

			JSONArray dataAttributesJSONArray =
				fragmentEntryProcessor.getDataAttributesJSONArray();

			if (dataAttributesJSONArray == null) {
				continue;
			}

			for (int i = 0; i < dataAttributesJSONArray.length(); i++) {
				jsonArray.put(dataAttributesJSONArray.getString(i));
			}
		}

		return jsonArray;
	}

	@Override
	public JSONObject getDefaultEditableValuesJSONObject(
		String html, String configuration) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (FragmentEntryProcessor fragmentEntryProcessor :
				_fragmentEntryProcessors) {

			JSONObject defaultEditableValuesJSONObject =
				fragmentEntryProcessor.getDefaultEditableValuesJSONObject(
					html, configuration);

			if ((defaultEditableValuesJSONObject != null) &&
				(defaultEditableValuesJSONObject.length() > 0)) {

				Class<?> clazz = fragmentEntryProcessor.getClass();

				jsonObject.put(
					clazz.getName(), defaultEditableValuesJSONObject);
			}
		}

		Document document = _getDocument(html);

		for (DefaultEditableValuesFragmentEntryProcessor
				defaultEditableValuesFragmentEntryProcessor :
					_defaultEditableValuesFragmentEntryProcessors) {

			JSONObject defaultEditableValuesJSONObject =
				defaultEditableValuesFragmentEntryProcessor.
					getDefaultEditableValuesJSONObject(configuration, document);

			if ((defaultEditableValuesJSONObject != null) &&
				(defaultEditableValuesJSONObject.length() > 0)) {

				jsonObject.put(
					defaultEditableValuesFragmentEntryProcessor.getKey(),
					defaultEditableValuesJSONObject);
			}
		}

		return jsonObject;
	}

	@Override
	public String processFragmentEntryLinkCSS(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		String css = fragmentEntryLink.getCss();

		for (CSSFragmentEntryProcessor cssFragmentEntryProcessor :
				_cssFragmentEntryProcessors) {

			css = cssFragmentEntryProcessor.processFragmentEntryLinkCSS(
				fragmentEntryLink, css, fragmentEntryProcessorContext);
		}

		return css;
	}

	@Override
	public String processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		if (fragmentEntryLink.isTypePortlet()) {
			return _renderWidgetHTML(
				fragmentEntryLink, fragmentEntryProcessorContext);
		}

		String html = fragmentEntryLink.getHtml();

		for (FragmentEntryProcessor fragmentEntryProcessor :
				_fragmentEntryProcessors) {

			html = fragmentEntryProcessor.processFragmentEntryLinkHTML(
				fragmentEntryLink, html, fragmentEntryProcessorContext);
		}

		Document document = _getDocument(html);

		for (DocumentFragmentEntryProcessor documentFragmentEntryProcessor :
				_documentFragmentEntryProcessors) {

			documentFragmentEntryProcessor.processFragmentEntryLinkHTML(
				fragmentEntryLink, document, fragmentEntryProcessorContext);
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	@Override
	public void validateFragmentEntryHTML(String html, String configuration)
		throws PortalException {

		if (CompanyThreadLocal.isInitializingPortalInstance()) {
			return;
		}

		Set<String> validHTMLs = _validHTMLs.get();

		if (validHTMLs.contains(html)) {
			return;
		}

		for (FragmentEntryValidator fragmentEntryValidator :
				_fragmentEntryValidators) {

			fragmentEntryValidator.validateFragmentEntryHTML(
				html, configuration, LocaleUtil.getDefault());
		}

		Document document = _getDocument(html);

		for (DocumentFragmentEntryValidator documentFragmentEntryValidator :
				_documentFragmentEntryValidators) {

			documentFragmentEntryValidator.validateFragmentEntryHTML(
				document, configuration, LocaleUtil.getDefault());
		}

		validHTMLs.add(html);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_cssFragmentEntryProcessors = ServiceTrackerListFactory.open(
			bundleContext, CSSFragmentEntryProcessor.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"fragment.entry.processor.priority")));
		_defaultEditableValuesFragmentEntryProcessors =
			ServiceTrackerListFactory.open(
				bundleContext,
				DefaultEditableValuesFragmentEntryProcessor.class,
				Collections.reverseOrder(
					new PropertyServiceReferenceComparator<>(
						"fragment.entry.processor.priority")));
		_documentFragmentEntryProcessors = ServiceTrackerListFactory.open(
			bundleContext, DocumentFragmentEntryProcessor.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"fragment.entry.processor.priority")));
		_documentFragmentEntryValidators = ServiceTrackerListFactory.open(
			bundleContext, DocumentFragmentEntryValidator.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"fragment.entry.processor.priority")));
		_fragmentEntryAutocompleteContributors = ServiceTrackerListFactory.open(
			bundleContext, FragmentEntryAutocompleteContributor.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"fragment.entry.processor.priority")));
		_fragmentEntryProcessors = ServiceTrackerListFactory.open(
			bundleContext, FragmentEntryProcessor.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"fragment.entry.processor.priority")));
		_fragmentEntryValidators = ServiceTrackerListFactory.open(
			bundleContext, FragmentEntryValidator.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"fragment.entry.processor.priority")));
	}

	@Deactivate
	protected void deactivate() {
		_cssFragmentEntryProcessors.close();
		_defaultEditableValuesFragmentEntryProcessors.close();
		_documentFragmentEntryProcessors.close();
		_documentFragmentEntryValidators.close();
		_fragmentEntryAutocompleteContributors.close();
		_fragmentEntryProcessors.close();
		_fragmentEntryValidators.close();
	}

	private Document _getDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);

		document.outputSettings(outputSettings);

		return document;
	}

	private String _renderWidgetHTML(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		String portletId = jsonObject.getString("portletId");

		if (Validator.isNull(portletId)) {
			return StringPool.BLANK;
		}

		HttpServletRequest httpServletRequest =
			fragmentEntryProcessorContext.getHttpServletRequest();

		String instanceId = jsonObject.getString("instanceId");

		String encodedPortletId = PortletIdCodec.encode(portletId, instanceId);

		String html = _fragmentPortletRenderer.renderPortlet(
			fragmentEntryLink, httpServletRequest,
			fragmentEntryProcessorContext.getHttpServletResponse(), portletId,
			instanceId,
			PortletPreferencesFactoryUtil.toXML(
				PortletPreferencesFactoryUtil.getPortletPreferences(
					httpServletRequest, encodedPortletId)));

		String checkAccessAllowedToPortletCacheKey = StringBundler.concat(
			"LIFERAY_SHARED_",
			DefaultLayoutTypeAccessPolicyImpl.class.getName(), "#",
			ParamUtil.getLong(httpServletRequest, "p_l_id"), "#",
			encodedPortletId);

		httpServletRequest.setAttribute(
			FragmentWebKeys.ACCESS_ALLOWED_TO_FRAGMENT_ENTRY_LINK_ID +
				fragmentEntryLink.getFragmentEntryLinkId(),
			GetterUtil.getBoolean(
				httpServletRequest.getAttribute(
					checkAccessAllowedToPortletCacheKey),
				true));

		return html;
	}

	private static final ThreadLocal<Set<String>> _validHTMLs =
		new CentralizedThreadLocal(
			FragmentEntryProcessorRegistryImpl.class.getName() + "._validHTMLs",
			HashSet::new);

	private ServiceTrackerList<CSSFragmentEntryProcessor>
		_cssFragmentEntryProcessors;
	private ServiceTrackerList<DefaultEditableValuesFragmentEntryProcessor>
		_defaultEditableValuesFragmentEntryProcessors;
	private ServiceTrackerList<DocumentFragmentEntryProcessor>
		_documentFragmentEntryProcessors;
	private ServiceTrackerList<DocumentFragmentEntryValidator>
		_documentFragmentEntryValidators;
	private ServiceTrackerList<FragmentEntryAutocompleteContributor>
		_fragmentEntryAutocompleteContributors;
	private ServiceTrackerList<FragmentEntryProcessor> _fragmentEntryProcessors;
	private ServiceTrackerList<FragmentEntryValidator> _fragmentEntryValidators;

	@Reference
	private FragmentPortletRenderer _fragmentPortletRenderer;

	@Reference
	private JSONFactory _jsonFactory;

}