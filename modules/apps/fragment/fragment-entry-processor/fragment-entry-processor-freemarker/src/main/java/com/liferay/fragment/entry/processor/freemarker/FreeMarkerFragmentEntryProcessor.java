/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.freemarker;

import com.liferay.fragment.entry.processor.freemarker.internal.configuration.FreeMarkerFragmentEntryProcessorConfiguration;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.helper.FragmentEntryLinkHelper;
import com.liferay.fragment.input.template.parser.FragmentEntryInputTemplateNodeContextHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=1",
	service = FragmentEntryProcessor.class
)
public class FreeMarkerFragmentEntryProcessor
	implements FragmentEntryProcessor {

	@Override
	public JSONObject getDefaultEditableValuesJSONObject(
		String html, String configuration) {

		return _fragmentEntryConfigurationParser.
			getConfigurationDefaultValuesJSONObject(configuration);
	}

	@Override
	public String processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, String html,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		if (Validator.isNull(html)) {
			return html;
		}

		FreeMarkerFragmentEntryProcessorConfiguration
			freeMarkerFragmentEntryProcessorConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FreeMarkerFragmentEntryProcessorConfiguration.class,
					fragmentEntryLink.getCompanyId());

		if (!freeMarkerFragmentEntryProcessorConfiguration.enable() &&
			Validator.isNull(fragmentEntryLink.getRendererKey()) &&
			!fragmentEntryLink.isSystem()) {

			return html;
		}

		if (fragmentEntryProcessorContext.getHttpServletRequest() == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"HTTP servlet request is not set in the fragment entry " +
						"processor context");
			}

			return html;
		}

		if (fragmentEntryProcessorContext.getHttpServletResponse() == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"HTTP servlet response is not set in the fragment entry " +
						"processor context");
			}

			return html;
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			new StringTemplateResource("template_id", "[#ftl] " + html), true);

		template.put(TemplateConstants.WRITER, unsyncStringWriter);

		JSONObject configurationValuesJSONObject =
			_fragmentEntryConfigurationParser.getConfigurationJSONObject(
				fragmentEntryLink.getConfiguration(),
				fragmentEntryLink.getEditableValues(),
				fragmentEntryProcessorContext.getLocale());

		template.putAll(
			HashMapBuilder.<String, Object>put(
				"configuration", configurationValuesJSONObject
			).put(
				"fragmentElementId",
				fragmentEntryProcessorContext.getFragmentElementId()
			).put(
				"fragmentEntryLinkNamespace", fragmentEntryLink.getNamespace()
			).put(
				"layoutMode",
				_getLayoutMode(
					fragmentEntryProcessorContext.getHttpServletRequest())
			).putAll(
				_fragmentEntryConfigurationParser.getContextObjects(
					configurationValuesJSONObject,
					fragmentEntryLink.getConfiguration(),
					_getInfoItem(
						fragmentEntryProcessorContext.
							getContextInfoItemReference()),
					fragmentEntryProcessorContext.getSegmentsEntryIds())
			).build());

		if (fragmentEntryLink.isTypeInput()) {
			template.put(
				"input",
				_fragmentEntryInputTemplateNodeContextHelper.
					toInputTemplateNode(
						fragmentEntryProcessorContext.getAttributes(),
						_fragmentEntryLinkHelper.getFragmentEntryName(
							fragmentEntryLink,
							fragmentEntryProcessorContext.getLocale()),
						fragmentEntryLink,
						fragmentEntryProcessorContext.getHttpServletRequest(),
						fragmentEntryProcessorContext.getInfoForm(),
						fragmentEntryProcessorContext.getLocale()));
		}

		template.prepareTaglib(
			fragmentEntryProcessorContext.getHttpServletRequest(),
			fragmentEntryProcessorContext.getHttpServletResponse());

		template.prepare(fragmentEntryProcessorContext.getHttpServletRequest());

		try {
			template.processTemplate(unsyncStringWriter);
		}
		catch (TemplateException templateException) {
			throw new FragmentEntryContentException(
				_getMessage(
					templateException,
					fragmentEntryProcessorContext.getLocale()),
				templateException);
		}

		return unsyncStringWriter.toString();
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

	private String _getLayoutMode(HttpServletRequest httpServletRequest) {
		return ParamUtil.getString(
			_portal.getOriginalServletRequest(httpServletRequest), "p_l_mode",
			Constants.VIEW);
	}

	private String _getMessage(
		TemplateException templateException, Locale locale) {

		String message = _language.get(locale, "freemarker-syntax-is-invalid");

		Throwable causeThrowable = templateException.getCause();

		String causeThrowableMessage = causeThrowable.getLocalizedMessage();

		if (Validator.isNotNull(causeThrowableMessage)) {
			message = message + "\n\n" + causeThrowableMessage;
		}

		return message;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FreeMarkerFragmentEntryProcessor.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryInputTemplateNodeContextHelper
		_fragmentEntryInputTemplateNodeContextHelper;

	@Reference
	private FragmentEntryLinkHelper _fragmentEntryLinkHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}