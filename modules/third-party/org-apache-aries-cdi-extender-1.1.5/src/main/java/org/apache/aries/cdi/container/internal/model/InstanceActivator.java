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

package org.apache.aries.cdi.container.internal.model;

import java.lang.annotation.Annotation;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.aries.cdi.container.internal.container.ComponentContext.With;
import org.apache.aries.cdi.container.internal.container.ContainerState;
import org.apache.aries.cdi.container.internal.container.Op;
import org.apache.aries.cdi.container.internal.container.Phase;
import org.apache.aries.cdi.container.internal.util.Sets;
import org.apache.aries.cdi.container.internal.util.Throw;
import org.osgi.service.cdi.annotations.ComponentScoped;
import org.osgi.service.log.Logger;

public abstract class InstanceActivator extends Phase {

	public abstract static class Builder<T extends Builder<T>> {

		public Builder(ContainerState containerState, Phase next) {
			_containerState = containerState;
			_next = next;
		}

		public abstract InstanceActivator build();

		@SuppressWarnings("unchecked")
		public T setInstance(ExtendedComponentInstanceDTO instance) {
			_instance = instance;
			return (T)this;
		}

		private ContainerState _containerState;
		protected ExtendedComponentInstanceDTO _instance;
		private Phase _next;
	}

	protected InstanceActivator(Builder<?> builder) {
		super(builder._containerState, builder._next);

		_instance = builder._instance;
		_log = builder._containerState.ccrLogs().getLogger(InstanceActivator.class);
	}

	@Override
	public abstract Op closeOp();

	@Override
	public abstract Op openOp();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Entry<ExtendedActivationDTO, Object> activate(
		Bean<? extends Object> bean,
		ExtendedActivationTemplateDTO activationTemplate,
		BeanManager beanManager) {

		ExtendedActivationDTO activationDTO = new ExtendedActivationDTO();
		activationDTO.errors = new CopyOnWriteArrayList<>();
		activationDTO.template = activationTemplate;
		activationDTO.instance = _instance;

		_instance.activations.add(activationDTO);

		try (With with = new With(activationDTO)) {
			try {
				final Object object = containerState.componentContext().get(
					(Bean)bean,
					(CreationalContext)beanManager.createCreationalContext(bean));

				final Set<Annotation> qualifiers = bean.getQualifiers();

				Event<Object> event1 = beanManager.getEvent();

				event1 = event1.select(Sets.hashSet(qualifiers, Initialized.Literal.of(ComponentScoped.class)).toArray(new Annotation[0]));

				try {
					event1.fire(object);
				}
				catch (Throwable t) {
					_log.error(l -> l.error("CCR Error in activator event @Initialized for {} on {}", _instance, bundle(), t));
					activationDTO.errors.add(Throw.asString(t));
				}

				activationDTO.onClose = a -> {
					try (With with2 = new With(a)) {
						Event<Object> event2 = beanManager.getEvent();

						event2 = event2.select(Sets.hashSet(qualifiers, BeforeDestroyed.Literal.of(ComponentScoped.class)).toArray(new Annotation[0]));

						try {
							event2.fire(object);
						}
						catch (Throwable t) {
							_log.error(l -> l.error("CCR Error in activator event @BeforeDestroyed for {} on {}", _instance, bundle(), t));
							activationDTO.errors.add(Throw.asString(t));
						}

						containerState.componentContext().destroy();

						Event<Object> event3 = beanManager.getEvent();

						event3 = event3.select(Sets.hashSet(qualifiers, Destroyed.Literal.of(ComponentScoped.class)).toArray(new Annotation[0]));

						try {
							event3.fire(object);
						}
						catch (Throwable t) {
							_log.error(l -> l.error("CCR Error in activator event @Destroyed for {} on {}", _instance, bundle(), t));
							activationDTO.errors.add(Throw.asString(t));
						}

						_instance.activations.remove(a);
					}
				};

				return new AbstractMap.SimpleImmutableEntry<>(activationDTO, object);
			}
			catch (Throwable t) {
				_log.error(l -> l.error("CCR Error in activator create for {} on {}", _instance, bundle(), t));
				activationDTO.errors.add(Throw.asString(t));

				return new AbstractMap.SimpleImmutableEntry<>(activationDTO, null);
			}
		}
	}

	private final Logger _log;
	protected final ExtendedComponentInstanceDTO _instance;

}
/* @generated */