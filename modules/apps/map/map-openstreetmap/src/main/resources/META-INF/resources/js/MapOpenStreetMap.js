/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MapBase} from '@liferay/map-common';
import L from 'leaflet';

import 'leaflet/dist/leaflet.css';

import OpenStreetMapDialog from './OpenStreetMapDialog';
import OpenStreetMapGeoJSON from './OpenStreetMapGeoJSON';
import OpenStreetMapGeocoder from './OpenStreetMapGeocoder';
import OpenStreetMapMarker from './OpenStreetMapMarker';

const defaultTileURI = '//{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';

/**
 * MapOpenStreetMap
 * @review
 */
class MapOpenStreetMap extends MapBase {
	get tileURI() {
		return this._STATE_.tileURI;
	}

	set tileURI(tileURI) {
		this._STATE_.tileURI = tileURI;
	}

	/**
	 * Creates a new map using OpenStreetMap's API
	 * @param  {Array} args List of arguments to be passed to State
	 * @review
	 */
	constructor(args) {
		MapBase.DialogImpl = OpenStreetMapDialog;
		MapBase.GeocoderImpl = OpenStreetMapGeocoder;
		MapBase.GeoJSONImpl = OpenStreetMapGeoJSON;
		MapBase.MarkerImpl = OpenStreetMapMarker;
		MapBase.SearchImpl = null;

		super(args);

		const {tileURI = defaultTileURI} = args;

		this._STATE_ = {
			...this._STATE_,
			tileURI,
		};

		this._map = null;
	}

	/**
	 * @inheritDoc
	 * @review
	 */
	_createMap(location, controlsConfig) {
		const mapConfig = {
			center: location,
			layers: [
				L.tileLayer(this.tileURI, {
					attribution:
						'&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
				}),
			],
			zoom: this.zoom,
		};

		const map = L.map(
			document.querySelector(this.boundingBox),
			Object.assign(mapConfig, controlsConfig)
		);

		if (this.data?.features?.length) {
			const bounds = new L.LatLngBounds();

			this.data.features.forEach((feature) =>
				bounds.extend(
					new L.LatLng(
						feature.geometry.coordinates[1],
						feature.geometry.coordinates[0]
					)
				)
			);

			map.fitBounds(bounds);
		}

		return map;
	}

	/**
	 * @inheritDoc
	 * @review
	 */
	addControl(control, position) {
		const LeafLetControl = L.Control.extend({
			onAdd() {
				if (typeof control === 'string') {
					control = document.querySelector(control);
				}

				return control;
			},

			options: {
				position: MapOpenStreetMap.POSITION_MAP[position],
			},
		});

		this._map.addControl(new LeafLetControl());
	}

	/**
	 * @inheritDoc
	 * @review
	 */
	getBounds() {
		return this._map.getBounds();
	}

	/**
	 * @inheritDoc
	 * @review
	 */
	setCenter(location) {
		if (this._map) {
			this._map.panTo(location);
		}

		if (this._geolocationMarker) {
			this._geolocationMarker.setPosition(location);
		}
	}
}

MapOpenStreetMap.CONTROLS_MAP = {
	[MapBase.CONTROLS.ATTRIBUTION]: 'attributionControl',
	[MapBase.CONTROLS.ZOOM]: 'zoomControl',
};

MapOpenStreetMap.POSITION_MAP = {
	[MapBase.POSITION.BOTTOM]: 'bottomleft',
	[MapBase.POSITION.BOTTOM_CENTER]: 'bottomleft',
	[MapBase.POSITION.BOTTOM_LEFT]: 'bottomleft',
	[MapBase.POSITION.BOTTOM_RIGHT]: 'bottomright',
	[MapBase.POSITION.CENTER]: 'topleft',
	[MapBase.POSITION.LEFT]: 'topleft',
	[MapBase.POSITION.LEFT_BOTTOM]: 'bottomleft',
	[MapBase.POSITION.LEFT_CENTER]: 'topleft',
	[MapBase.POSITION.LEFT_TOP]: 'topleft',
	[MapBase.POSITION.RIGHT]: 'bottomright',
	[MapBase.POSITION.RIGHT_BOTTOM]: 'bottomright',
	[MapBase.POSITION.RIGHT_CENTER]: 'bottomright',
	[MapBase.POSITION.RIGHT_TOP]: 'topright',
	[MapBase.POSITION.TOP]: 'topright',
	[MapBase.POSITION.TOP_CENTER]: 'topright',
	[MapBase.POSITION.TOP_LEFT]: 'topleft',
	[MapBase.POSITION.TOP_RIGHT]: 'topright',
};

export default MapOpenStreetMap;
export {MapOpenStreetMap};
