/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MarkerBase} from '@liferay/map-common';
import L from 'leaflet';

import 'leaflet/dist/leaflet.css';

/**
 * OpenStreetMapMarker
 * @review
 */
export default class OpenStreetMapMarker extends MarkerBase {

	/**
	 * @inheritDoc
	 * @review
	 */
	_getNativeMarker(location, map) {
		if (!this._nativeMarker) {
			this._nativeMarker = L.marker(location, {
				draggable: true,
			}).addTo(map);

			this._nativeMarker.on(
				'click',
				this._getNativeEventFunction('click')
			);
			this._nativeMarker.on(
				'dblclick',
				this._getNativeEventFunction('dblclick')
			);
			this._nativeMarker.on('drag', this._getNativeEventFunction('drag'));
			this._nativeMarker.on(
				'dragend',
				this._getNativeEventFunction('dragend')
			);
			this._nativeMarker.on(
				'dragstart',
				this._getNativeEventFunction('dragstart')
			);
			this._nativeMarker.on(
				'mousedown',
				this._getNativeEventFunction('mousedown')
			);
			this._nativeMarker.on(
				'mouseout',
				this._getNativeEventFunction('mouseout')
			);
			this._nativeMarker.on(
				'mouseover',
				this._getNativeEventFunction('mouseover')
			);
		}

		return this._nativeMarker;
	}

	/**
	 * @inheritDoc
	 * @review
	 */
	_getNormalizedEventData(nativeEvent) {
		return {
			location: nativeEvent.target.getLatLng(),
		};
	}

	/**
	 * If a marked has been created, sets the marker location to the given one
	 * @param {Object} location Location to set the native marker in
	 * @review
	 */
	setPosition(location) {
		if (this._nativeMarker) {
			this._nativeMarker.setLatLng(location);
		}
	}
}
