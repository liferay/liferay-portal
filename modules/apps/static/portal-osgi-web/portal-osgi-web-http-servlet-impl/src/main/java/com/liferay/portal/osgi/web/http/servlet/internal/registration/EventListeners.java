/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import java.util.AbstractList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Dante Wang
 */
public class EventListeners {

	public void clear() {
		_eventListenerRegistrationsMap.clear();
	}

	public <E extends EventListener> List<E> get(Class<E> clazz) {
		Objects.requireNonNull(clazz);

		List<EventListenerRegistration> eventListenerRegistrations =
			_eventListenerRegistrationsMap.get(clazz);

		if (eventListenerRegistrations == null) {
			return Collections.emptyList();
		}

		return new ListenerList<>(eventListenerRegistrations);
	}

	public <E extends EventListener> void put(
		Class<E> clazz, EventListenerRegistration eventListenerRegistration) {

		Objects.requireNonNull(clazz);

		List<EventListenerRegistration> eventListenerRegistrations =
			_eventListenerRegistrationsMap.computeIfAbsent(
				clazz, key -> new CopyOnWriteArrayList<>());

		eventListenerRegistrations.add(eventListenerRegistration);
	}

	public void put(
		List<Class<? extends EventListener>> classes,
		EventListenerRegistration eventListenerRegistration) {

		for (Class<? extends EventListener> clazz : classes) {
			put(clazz, eventListenerRegistration);
		}
	}

	public <E extends EventListener> void remove(
		Class<E> clazz, EventListenerRegistration eventListenerRegistration) {

		Objects.requireNonNull(clazz);

		List<EventListenerRegistration> eventListenerRegistrations =
			_eventListenerRegistrationsMap.get(clazz);

		if (eventListenerRegistrations != null) {
			eventListenerRegistrations.remove(eventListenerRegistration);
		}
	}

	public void remove(
		List<Class<? extends EventListener>> classes,
		EventListenerRegistration eventListenerRegistration) {

		for (Class<? extends EventListener> clazz : classes) {
			remove(clazz, eventListenerRegistration);
		}
	}

	private final ConcurrentMap
		<Class<? extends EventListener>, List<EventListenerRegistration>>
			_eventListenerRegistrationsMap = new ConcurrentHashMap<>();

	private static class ListenerList<R extends EventListener>
		extends AbstractList<R> {

		public ListenerList(
			List<EventListenerRegistration> eventListenerRegistrations) {

			_eventListenerRegistrations = eventListenerRegistrations;
		}

		public R get(int index) {
			EventListenerRegistration eventListenerRegistration =
				_eventListenerRegistrations.get(index);

			return (R)eventListenerRegistration.getService();
		}

		public int size() {
			return _eventListenerRegistrations.size();
		}

		private final List<EventListenerRegistration>
			_eventListenerRegistrations;

	}

}