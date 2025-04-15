/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Application;

import java.io.Closeable;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.MethodInvocationInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfoStack;
import org.apache.cxf.jaxrs.model.ProviderInfo;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.transport.Session;

/**
 * @author Cristina González
 */
public class MockMessage implements Message {

	public MockMessage(HttpServletRequest httpServletRequest) {
		this(httpServletRequest, null, null);
	}

	public MockMessage(
		HttpServletRequest httpServletRequest, Method method, Object resource) {

		_httpServletRequest = httpServletRequest;
		_resource = resource;

		if (resource != null) {
			Class<?> clazz = resource.getClass();

			_operationResourceInfo = new OperationResourceInfo(
				method,
				new ClassResourceInfo(
					clazz, clazz, true, true, BusFactory.getDefaultBus(true)));
		}
		else {
			_operationResourceInfo = null;
		}
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return null;
	}

	@Override
	public <T> T get(Class<T> clazz) {
		if (!Objects.equals(OperationResourceInfoStack.class, clazz)) {
			return null;
		}

		OperationResourceInfoStack operationResourceInfoStack =
			new OperationResourceInfoStack();

		ClassResourceInfo classResourceInfo =
			_operationResourceInfo.getClassResourceInfo();

		operationResourceInfoStack.add(
			new MethodInvocationInfo(
				_operationResourceInfo, classResourceInfo.getResourceClass(),
				Collections.emptyList()));

		return (T)operationResourceInfoStack;
	}

	@Override
	public Object get(Object key) {
		return null;
	}

	@Override
	public Collection<Attachment> getAttachments() {
		return null;
	}

	@Override
	public <T> T getContent(Class<T> clazz) {
		return null;
	}

	@Override
	public Set<Class<?>> getContentFormats() {
		return null;
	}

	@Override
	public Object getContextualProperty(String contextProperty) {
		if (Objects.equals(contextProperty, "HTTP.REQUEST")) {
			return _httpServletRequest;
		}
		else if (Objects.equals(
					contextProperty,
					"org.apache.cxf.jaxrs.resource.context.provider")) {

			return _resource;
		}

		return null;
	}

	@Override
	public Set<String> getContextualPropertyKeys() {
		return null;
	}

	@Override
	public Destination getDestination() {
		return null;
	}

	@Override
	public Exchange getExchange() {
		return new Exchange() {

			@Override
			public void clear() {
			}

			@Override
			public boolean containsKey(Object key) {
				return false;
			}

			@Override
			public boolean containsValue(Object value) {
				return false;
			}

			@Override
			public Set<Entry<String, Object>> entrySet() {
				return null;
			}

			@Override
			public <T> T get(Class<T> clazz) {
				if (Objects.equals(OperationResourceInfo.class, clazz)) {
					return (T)_operationResourceInfo;
				}

				return null;
			}

			@Override
			public Object get(Object key) {
				return null;
			}

			@Override
			public Binding getBinding() {
				return null;
			}

			@Override
			public BindingOperationInfo getBindingOperationInfo() {
				return null;
			}

			@Override
			public Bus getBus() {
				return null;
			}

			@Override
			public Conduit getConduit(Message message) {
				return null;
			}

			@Override
			public Destination getDestination() {
				return null;
			}

			@Override
			public Endpoint getEndpoint() {
				return new Endpoint() {

					@Override
					public void addCleanupHook(Closeable closeable) {
					}

					@Override
					public void clear() {
					}

					@Override
					public boolean containsKey(Object key) {
						return false;
					}

					@Override
					public boolean containsValue(Object value) {
						return false;
					}

					@Override
					public Set<Entry<String, Object>> entrySet() {
						return null;
					}

					@Override
					public Object get(Object key) {
						if (!Objects.equals(
								key, "jakarta.ws.rs.core.Application")) {

							return null;
						}

						return new ProviderInfo(new Application(), null, true);
					}

					@Override
					public List<Feature> getActiveFeatures() {
						return null;
					}

					@Override
					public Binding getBinding() {
						return null;
					}

					@Override
					public List<Closeable> getCleanupHooks() {
						return null;
					}

					@Override
					public EndpointInfo getEndpointInfo() {
						return null;
					}

					@Override
					public Executor getExecutor() {
						return null;
					}

					@Override
					public List<Interceptor<? extends Message>>
						getInFaultInterceptors() {

						return null;
					}

					@Override
					public MessageObserver getInFaultObserver() {
						return null;
					}

					@Override
					public List<Interceptor<? extends Message>>
						getInInterceptors() {

						return null;
					}

					@Override
					public List<Interceptor<? extends Message>>
						getOutFaultInterceptors() {

						return null;
					}

					@Override
					public MessageObserver getOutFaultObserver() {
						return null;
					}

					@Override
					public List<Interceptor<? extends Message>>
						getOutInterceptors() {

						return null;
					}

					@Override
					public Service getService() {
						return null;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Set<String> keySet() {
						return null;
					}

					@Override
					public Object put(String key, Object value) {
						return null;
					}

					@Override
					public void putAll(Map<? extends String, ?> map) {
					}

					@Override
					public Object remove(Object key) {
						return null;
					}

					@Override
					public void setExecutor(Executor executor) {
					}

					@Override
					public void setInFaultObserver(
						MessageObserver messageObserver) {
					}

					@Override
					public void setOutFaultObserver(
						MessageObserver messageObserver) {
					}

					@Override
					public int size() {
						return 0;
					}

					@Override
					public Collection<Object> values() {
						return null;
					}

				};
			}

			@Override
			public Message getInFaultMessage() {
				return null;
			}

			@Override
			public Message getInMessage() {
				return null;
			}

			@Override
			public Message getOutFaultMessage() {
				return null;
			}

			@Override
			public Message getOutMessage() {
				return null;
			}

			@Override
			public Service getService() {
				return null;
			}

			@Override
			public Session getSession() {
				return null;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean isOneWay() {
				return false;
			}

			@Override
			public boolean isSynchronous() {
				return false;
			}

			@Override
			public Set<String> keySet() {
				return null;
			}

			@Override
			public <T> void put(Class<T> clazz, T t) {
			}

			@Override
			public Object put(String key, Object value) {
				return null;
			}

			@Override
			public void putAll(Map<? extends String, ?> map) {
			}

			@Override
			public <T> T remove(Class<T> clazz) {
				return null;
			}

			@Override
			public Object remove(Object key) {
				return null;
			}

			@Override
			public void setConduit(Conduit conduit) {
			}

			@Override
			public void setDestination(Destination destination) {
			}

			@Override
			public void setInFaultMessage(Message message) {
			}

			@Override
			public void setInMessage(Message message) {
			}

			@Override
			public void setOneWay(boolean oneWay) {
			}

			@Override
			public void setOutFaultMessage(Message message) {
			}

			@Override
			public void setOutMessage(Message message) {
			}

			@Override
			public void setSynchronous(boolean synchronous) {
			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public Collection<Object> values() {
				return null;
			}

		};
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public InterceptorChain getInterceptorChain() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<String> keySet() {
		return null;
	}

	@Override
	public <T> void put(Class<T> clazz, T t) {
	}

	@Override
	public Object put(String key, Object value) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ?> map) {
	}

	@Override
	public <T> T remove(Class<T> clazz) {
		return null;
	}

	@Override
	public Object remove(Object key) {
		return null;
	}

	@Override
	public <T> void removeContent(Class<T> clazz) {
	}

	@Override
	public void resetContextCache() {
	}

	@Override
	public void setAttachments(Collection<Attachment> collection) {
	}

	@Override
	public <T> void setContent(Class<T> clazz, Object object) {
	}

	@Override
	public void setExchange(Exchange exchange) {
	}

	@Override
	public void setId(String id) {
	}

	@Override
	public void setInterceptorChain(InterceptorChain interceptorChain) {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Collection<Object> values() {
		return null;
	}

	private final HttpServletRequest _httpServletRequest;
	private final OperationResourceInfo _operationResourceInfo;
	private final Object _resource;

}