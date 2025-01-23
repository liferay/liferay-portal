/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.aries.cdi.container.internal.container;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;

import org.apache.aries.cdi.container.internal.Activator;
import org.apache.aries.cdi.spi.configuration.Configuration;

public class ConfigurationExtension  extends AbstractMap<String, Object> implements Configuration, Extension {

	protected ConfigurationExtension() { // proxy
		_containerState = null;
		_configuration = null;
	}

	public ConfigurationExtension(ContainerState containerState) {
		_containerState = containerState;

		Map<String, Object> configuration = new TreeMap<>();

		if (_containerState.containerDTO().components != null &&
			!_containerState.containerDTO().components.isEmpty() &&
			_containerState.containerDTO().components.get(0).instances != null &&
			!_containerState.containerDTO().components.get(0).instances.isEmpty() &&
			_containerState.containerDTO().components.get(0).instances.get(0).configurations != null &&
			!_containerState.containerDTO().components.get(0).instances.get(0).configurations.isEmpty()) {

			configuration = new TreeMap<>(_containerState.containerDTO().components.get(0).instances.get(0).configurations.get(0).properties);
		}

		configuration.put(HTTP_WHITEBOARD_CONTEXT_SELECT, getSelectedContext(configuration));
		configuration.put(JAX_RS_APPLICATION_SELECT, getSelectedApplication(configuration));

		_configuration = Collections.unmodifiableMap(configuration);
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return _configuration.entrySet();
	}

	void init(
		@Priority(Interceptor.Priority.PLATFORM_BEFORE)
		@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {

		Activator.put(_containerState.bundle(), beanManager);

		Event<Object> event = beanManager.getEvent();

		event.fire(this);
	}

	void destroy(
		@Priority(Integer.MAX_VALUE - 1000)
		@Observes BeforeShutdown bs) {

		Activator.remove(_containerState.bundle());
	}

	String getSelectedApplication(Map<String, Object> configuration) {
		if (configuration.containsKey(JAX_RS_APPLICATION_SELECT)) {
			return String.valueOf(_configuration.get(JAX_RS_APPLICATION_SELECT));
		}

		Map<String, Object> attributes = _containerState.cdiAttributes();

		if (attributes.containsKey(JAX_RS_APPLICATION_SELECT)) {
			return String.valueOf(attributes.get(JAX_RS_APPLICATION_SELECT));
		}

		return DEFAULT_APPLICATION_FILTER;
	}

	String getSelectedContext(Map<String, Object> configuration) {
		if (configuration.containsKey(HTTP_WHITEBOARD_CONTEXT_SELECT)) {
			return String.valueOf(_configuration.get(HTTP_WHITEBOARD_CONTEXT_SELECT));
		}

		Map<String, Object> attributes = _containerState.cdiAttributes();

		if (attributes.containsKey(HTTP_WHITEBOARD_CONTEXT_SELECT)) {
			return String.valueOf(attributes.get(HTTP_WHITEBOARD_CONTEXT_SELECT));
		}

		Dictionary<String,String> headers = _containerState.bundle().getHeaders();

		if (headers != null && headers.get(WEB_CONTEXT_PATH) != null) {
			return CONTEXT_PATH_PREFIX + headers.get(WEB_CONTEXT_PATH) + ')';
		}

		return DEFAULT_CONTEXT_FILTER;
	}

	private static final String CONTEXT_PATH_PREFIX = "(osgi.http.whiteboard.context.path=";
	private static final String DEFAULT_APPLICATION_FILTER = "(osgi.jaxrs.name=.default)";
	private static final String DEFAULT_CONTEXT_FILTER = "(osgi.http.whiteboard.context.name=default)";
	private static final String WEB_CONTEXT_PATH = "Web-ContextPath";

	private final ContainerState _containerState;
	private final Map<String, Object> _configuration;

}
/* @generated */