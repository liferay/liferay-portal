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

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;

import org.osgi.framework.BundleContext;

public class BundleContextExtension implements Extension {

	protected BundleContextExtension() { // for proxy
		this(null);
	}

	public BundleContextExtension(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	void init(
		@Priority(Interceptor.Priority.PLATFORM_BEFORE)
		@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {

		Event<Object> event = beanManager.getEvent();

		event.fire(_bundleContext);
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		abd.addBean()
			.addType(BundleContext.class)
			.createWith(c -> _bundleContext);
	}

	private final BundleContext _bundleContext;

}
/* @generated */