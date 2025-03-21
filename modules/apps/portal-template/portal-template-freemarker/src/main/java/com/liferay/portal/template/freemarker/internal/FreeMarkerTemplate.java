/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.template.TemplateResourceThreadLocal;
import com.liferay.portal.template.engine.BaseTemplate;
import com.liferay.portal.template.engine.TemplateContextHelper;

import freemarker.core.ParseException;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.WrapperTemplateModel;

import freemarker.template.AdapterTemplateModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mika Koivisto
 * @author Tina Tian
 */
public class FreeMarkerTemplate extends BaseTemplate {

	public FreeMarkerTemplate(
		TemplateResource templateResource, Map<String, Object> context,
		Configuration configuration,
		TemplateContextHelper templateContextHelper,
		TemplateResourceCache templateResourceCache, boolean restricted,
		BeansWrapper beansWrapper, FreeMarkerManager freeMarkerManager) {

		super(templateResource, context, templateContextHelper, restricted);

		_configuration = configuration;
		_templateResourceCache = templateResourceCache;
		_beansWrapper = beansWrapper;
		_freeMarkerManager = freeMarkerManager;

		if (templateResourceCache.isEnabled()) {
			cacheTemplateResource(templateResourceCache, templateResource);
		}
	}

	@Override
	public void prepareTaglib(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		_freeMarkerManager.addTaglibSupport(
			context, httpServletRequest, httpServletResponse, _beansWrapper);
	}

	@Override
	protected void handleException(
			TemplateResource templateResource,
			TemplateResource errorTemplateResource, Exception exception1,
			Writer writer)
		throws TemplateException {

		if (_templateResourceCache.isEnabled()) {
			cacheTemplateResource(
				_templateResourceCache, errorTemplateResource);
		}

		if (exception1 instanceof freemarker.template.TemplateException ||
			exception1 instanceof ParseException) {

			put("exception", exception1.getMessage());

			if (templateResource instanceof StringTemplateResource) {
				StringTemplateResource stringTemplateResource =
					(StringTemplateResource)templateResource;

				put("script", stringTemplateResource.getContent());
			}

			if (exception1 instanceof ParseException) {
				ParseException parseException = (ParseException)exception1;

				put("column", parseException.getColumnNumber());
				put("line", parseException.getLineNumber());
			}

			try {
				processTemplate(errorTemplateResource, writer);
			}
			catch (Exception exception2) {
				throw new TemplateException(
					"Unable to process FreeMarker template " +
						errorTemplateResource.getTemplateId(),
					exception2);
			}
		}
		else {
			throw new TemplateException(
				"Unable to process FreeMarker template " +
					templateResource.getTemplateId(),
				exception1);
		}
	}

	@Override
	protected void processTemplate(
			TemplateResource templateResource, Writer writer)
		throws Exception {

		_freeMarkerManager.render(
			templateResource.getTemplateId(), writer, isRestricted(),
			() -> {
				TemplateResourceThreadLocal.setTemplateResource(
					TemplateConstants.LANG_TYPE_FTL, templateResource);

				try {
					Template template = _configuration.getTemplate(
						getTemplateResourceUUID(templateResource),
						TemplateConstants.DEFAUT_ENCODING);

					template.setObjectWrapper(_beansWrapper);

					template.process(
						new CachableDefaultMapAdapter(context, _beansWrapper),
						writer);
				}
				finally {
					TemplateResourceThreadLocal.setTemplateResource(
						TemplateConstants.LANG_TYPE_FTL, null);
				}

				return null;
			});
	}

	@Override
	protected Object putClass(String key, Class<?> clazz) {
		try {
			TemplateHashModel templateHashModel =
				_beansWrapper.getStaticModels();

			return context.put(key, templateHashModel.get(clazz.getName()));
		}
		catch (TemplateModelException templateModelException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Variable " + key + " registration fail",
					templateModelException);
			}

			return null;
		}
	}

	private static final TemplateModel _NULL_TEMPLATE_MODEL =
		new TemplateModel() {
		};

	private static final Log _log = LogFactoryUtil.getLog(
		FreeMarkerTemplate.class);

	private final BeansWrapper _beansWrapper;
	private final Configuration _configuration;
	private final FreeMarkerManager _freeMarkerManager;
	private final TemplateResourceCache _templateResourceCache;

	private class CachableDefaultMapAdapter
		extends WrappingTemplateModel
		implements AdapterTemplateModel, Serializable, TemplateHashModelEx,
				   TemplateModelWithAPISupport, WrapperTemplateModel {

		@Override
		public TemplateModel get(String key) throws TemplateModelException {
			TemplateModel templateModel = _wrappedValueMap.get(key);

			if (templateModel == _NULL_TEMPLATE_MODEL) {
				return null;
			}

			if (templateModel != null) {
				return templateModel;
			}

			Object value = _map.get(key);

			if (value instanceof UnsafeSupplierValue) {
				UnsafeSupplierValue<?, RuntimeException> unsafeSupplierValue =
					(UnsafeSupplierValue<?, RuntimeException>)value;

				value = unsafeSupplierValue.getValue();
			}

			if (value == null) {
				_wrappedValueMap.put(key, _NULL_TEMPLATE_MODEL);

				return null;
			}

			templateModel = _objectWrapper.wrap(value);

			_wrappedValueMap.put(key, templateModel);

			return templateModel;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Object getAdaptedObject(Class hint) {
			return _map;
		}

		@Override
		public TemplateModel getAPI() throws TemplateModelException {
			ObjectWrapperWithAPISupport objectWrapperWithAPISupport =
				(ObjectWrapperWithAPISupport)_objectWrapper;

			return objectWrapperWithAPISupport.wrapAsAPI(_map);
		}

		@Override
		public Object getWrappedObject() {
			return _map;
		}

		@Override
		public boolean isEmpty() {
			return _map.isEmpty();
		}

		@Override
		public TemplateCollectionModel keys() {
			return new SimpleCollection(_map.keySet(), _objectWrapper);
		}

		@Override
		public int size() {
			return _map.size();
		}

		@Override
		public TemplateCollectionModel values() {
			return new SimpleCollection(_map.values(), _objectWrapper);
		}

		private CachableDefaultMapAdapter(
			Map<String, Object> map, ObjectWrapper objectWrapper) {

			super(objectWrapper);

			_map = map;
			_objectWrapper = objectWrapper;
		}

		private final Map<String, Object> _map;
		private final ObjectWrapper _objectWrapper;
		private final Map<String, TemplateModel> _wrappedValueMap =
			new HashMap<>();

	}

}