/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.engine;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Tina Tian
 */
public abstract class BaseTemplate implements Template {

	public BaseTemplate(
		TemplateResource templateResource, Map<String, Object> context,
		TemplateContextHelper templateContextHelper, boolean restricted) {

		if (templateResource == null) {
			throw new IllegalArgumentException("Template resource is null");
		}

		if (templateContextHelper == null) {
			throw new IllegalArgumentException(
				"Template context helper is null");
		}

		_templateResource = templateResource;

		this.context = new ChainedMap(context);

		_templateContextHelper = templateContextHelper;
		_restricted = restricted;
	}

	@Override
	public void clear() {
		context.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return context.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return context.containsValue(value);
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return context.entrySet();
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			return null;
		}

		return context.get(key);
	}

	@Override
	public boolean isEmpty() {
		return context.isEmpty();
	}

	public boolean isRestricted() {
		return _restricted;
	}

	@Override
	public Set<String> keySet() {
		return context.keySet();
	}

	@Override
	public void prepare(HttpServletRequest httpServletRequest) {
		ChainedMap chainedMap = (ChainedMap)context;

		chainedMap._setRequestContext(
			_templateContextHelper.createLazyContext(
				this, httpServletRequest, _restricted));

		_templateContextHelper.prepare(this, httpServletRequest);
	}

	@Override
	public void prepareTaglib(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {
	}

	@Override
	public void processTemplate(Writer writer) throws TemplateException {
		try {
			processTemplate(_templateResource, writer);
		}
		catch (Exception exception) {
			throw new TemplateException(
				"Unable to process template " +
					_templateResource.getTemplateId(),
				exception);
		}
	}

	@Override
	public void processTemplate(
			Writer writer,
			Supplier<TemplateResource> errorTemplateResourceSupplier)
		throws TemplateException {

		if (errorTemplateResourceSupplier == null) {
			processTemplate(writer);

			return;
		}

		Writer oldWriter = (Writer)get(TemplateConstants.WRITER);

		try {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			put(TemplateConstants.WRITER, unsyncStringWriter);

			processTemplate(_templateResource, unsyncStringWriter);

			StringBundler sb = unsyncStringWriter.getStringBundler();

			sb.writeTo(writer);
		}
		catch (Exception exception) {
			TemplateResource errorTemplateResource =
				errorTemplateResourceSupplier.get();

			if (errorTemplateResource == null) {
				throw new TemplateException(
					"Unable to process template " +
						_templateResource.getTemplateId(),
					exception);
			}

			put(TemplateConstants.WRITER, writer);

			handleException(
				_templateResource, errorTemplateResource, exception, writer);
		}
		finally {
			put(TemplateConstants.WRITER, oldWriter);
		}
	}

	@Override
	public Object put(String key, Object value) {
		if ((key == null) || (value == null)) {
			return null;
		}

		if (_restricted) {
			Set<String> restrictedVariables =
				_templateContextHelper.getRestrictedVariables();

			if (restrictedVariables.contains(key)) {
				return null;
			}
		}

		if (value instanceof Class) {
			return putClass(key, (Class<?>)value);
		}

		return context.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		map.forEach(this::put);
	}

	@Override
	public Object remove(Object key) {
		return context.remove(key);
	}

	@Override
	public int size() {
		return context.size();
	}

	@Override
	public Collection<Object> values() {
		return context.values();
	}

	protected void cacheTemplateResource(
		TemplateResourceCache templateResourceCache,
		TemplateResource templateResource) {

		if (templateResource instanceof StringTemplateResource) {
			return;
		}

		TemplateResource cachedTemplateResource =
			templateResourceCache.getTemplateResource(
				templateResource.getTemplateId());

		if ((cachedTemplateResource == null) ||
			!templateResource.equals(cachedTemplateResource)) {

			templateResourceCache.put(
				templateResource.getTemplateId(), templateResource);
		}
	}

	protected String getTemplateResourceUUID(
		TemplateResource templateResource) {

		return StringBundler.concat(
			TemplateConstants.TEMPLATE_RESOURCE_UUID_PREFIX, StringPool.POUND,
			templateResource.getTemplateId());
	}

	protected abstract void handleException(
			TemplateResource templateResource,
			TemplateResource errorTemplateResource, Exception exception,
			Writer writer)
		throws TemplateException;

	protected abstract void processTemplate(
			TemplateResource templateResource, Writer writer)
		throws Exception;

	protected Object putClass(String key, Class<?> clazz) {
		return context.put(key, clazz);
	}

	protected void setTaglibContext(Map<String, ?> taglibContext) {
		ChainedMap chainedMap = (ChainedMap)context;

		chainedMap._setTaglibContext(taglibContext);
	}

	protected Map<String, Object> context;

	private final boolean _restricted;
	private final TemplateContextHelper _templateContextHelper;
	private final TemplateResource _templateResource;

	private static class ChainedMap extends AbstractMap<String, Object> {

		@Override
		public void clear() {
			_map.clear();
			_removedKeys.clear();

			_requestContext = null;
			_sharedContext = null;
			_taglibContext = null;
		}

		@Override
		public boolean containsKey(Object key) {
			if (_map.containsKey(key)) {
				return true;
			}

			if (_removedKeys.contains(key)) {
				return false;
			}

			if (_getChainedValue(key) != null) {
				return true;
			}

			return false;
		}

		@Override
		public Set<Entry<String, Object>> entrySet() {
			return new AbstractSet<>() {

				@Override
				public Iterator<Entry<String, Object>> iterator() {
					List<Entry<String, Object>> entries =
						Collections.unmodifiableList(_getEntries());

					return entries.iterator();
				}

				@Override
				public int size() {
					List<Entry<String, Object>> entries = _getEntries();

					return entries.size();
				}

			};
		}

		@Override
		public Object get(Object key) {
			Object value = _map.get(key);

			if ((value != null) || _map.containsKey(key)) {
				return value;
			}

			if (_removedKeys.contains(key)) {
				return null;
			}

			return _getChainedValue(key);
		}

		@Override
		public Object put(String key, Object value) {
			Object previousValue = _map.put(key, value);

			if (!_removedKeys.isEmpty()) {
				_removedKeys.remove(key);
			}

			return previousValue;
		}

		@Override
		public Object remove(Object key) {
			Object value = get(key);

			_map.remove(key);

			if (_getChainedValue(key) != null) {
				_removedKeys.add((String)key);
			}

			return value;
		}

		private ChainedMap(Map<String, ?> sharedContext) {
			_sharedContext = sharedContext;
		}

		private void _addEntries(
			Map<String, ?> chainedContext, List<Entry<String, Object>> entries,
			Set<String> keys) {

			if (chainedContext == null) {
				return;
			}

			for (Entry<String, ?> entry : chainedContext.entrySet()) {
				if ((entry.getValue() == null) || !keys.add(entry.getKey())) {
					continue;
				}

				entries.add(new SimpleImmutableEntry<>(entry));
			}
		}

		private Object _getChainedValue(Object key) {
			Object value = _getValue(_requestContext, key);

			if (value != null) {
				return value;
			}

			value = _getValue(_taglibContext, key);

			if (value != null) {
				return value;
			}

			return _getValue(_sharedContext, key);
		}

		private List<Entry<String, Object>> _getEntries() {
			List<Entry<String, Object>> entries = new ArrayList<>(
				_map.entrySet());

			Set<String> keys = new HashSet<>(_map.keySet());

			keys.addAll(_removedKeys);

			_addEntries(_requestContext, entries, keys);
			_addEntries(_taglibContext, entries, keys);
			_addEntries(_sharedContext, entries, keys);

			return entries;
		}

		private Object _getValue(Map<String, ?> chainedContext, Object key) {
			if (chainedContext == null) {
				return null;
			}

			return chainedContext.get(key);
		}

		private void _removeKeys(Map<String, ?> chainedContext) {
			Set<String> keys = _map.keySet();

			keys.removeIf(key -> chainedContext.get(key) != null);

			_removedKeys.removeIf(key -> chainedContext.get(key) != null);
		}

		private void _setRequestContext(Map<String, ?> requestContext) {
			_removeKeys(requestContext);

			_requestContext = requestContext;
		}

		private void _setTaglibContext(Map<String, ?> taglibContext) {
			_removeKeys(taglibContext);

			_taglibContext = taglibContext;
		}

		private final Map<String, Object> _map = new HashMap<>();
		private final Set<String> _removedKeys = new HashSet<>();
		private Map<String, ?> _requestContext;
		private Map<String, ?> _sharedContext;
		private Map<String, ?> _taglibContext;

	}

}