/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import MapBase from '../../src/main/resources/META-INF/resources/js/MapBase';

describe('MapBase', () => {
	const getLocation = () => ({lat: Math.random(), lng: Math.random()});

	let mapImpl;
	let bounds;

	class MapImpl extends MapBase {
		_handlePositionChanged(data) {
			const geolocation = (data && data.location) || getLocation();

			return super._handlePositionChanged({
				newVal: {location: geolocation},
			});
		}

		_createMap(location, controlsConfig) {
			return {controlsConfig, location, name: 'map'};
		}

		getBounds() {
			return bounds;
		}

		setCenter() {}
	}

	const MarkerImpl = jest.fn().mockImplementation(function (location) {
		this.location = location;

		this.on = jest.fn();

		this.setPosition = function (location) {
			this.location = location;
		};
	});

	const DialogImpl = jest.fn().mockImplementation(() => {});

	const geocoderImpl = {
		reverse(location, callback) {
			callback({data: {location, name: 'data'}});
		},
	};

	const GeocoderImpl = jest.fn().mockImplementation(() => geocoderImpl);

	const geoJSONImpl = {
		on: jest.fn(),
	};

	const GeoJSONImpl = jest.fn().mockImplementation(() => geoJSONImpl);

	beforeEach(() => {
		MapImpl.MarkerImpl = MarkerImpl;
		MapImpl.DialogImpl = DialogImpl;
		MapImpl.GeocoderImpl = GeocoderImpl;
		MapImpl.GeoJSONImpl = GeoJSONImpl;

		mapImpl = new MapImpl();

		jest.spyOn(mapImpl, '_handlePositionChanged');
		jest.spyOn(mapImpl, 'addMarker');
		jest.spyOn(mapImpl, 'emit');
		jest.spyOn(mapImpl, 'getBounds');
		jest.spyOn(mapImpl, 'setCenter');

		bounds = {
			extend(location) {
				this.locations.push(location);
			},
			locations: [],
		};
	});

	describe('addMarker()', () => {
		it('passes the given location object to the constructor', () => {
			const location = getLocation();

			mapImpl._map = 'map';

			mapImpl.addMarker(location);

			expect(MapImpl.MarkerImpl.mock.calls[0][0].location).toBe(location);
		});

		it('passes the existing _map to the constructor', () => {
			const location = getLocation();

			mapImpl._map = 'map';

			mapImpl.addMarker(location);

			expect(MapImpl.MarkerImpl.mock.calls[0][0].map).toBe('map');
		});

		it('does nothing if MarkerImpl is not implemented', () => {
			MapImpl.MarkerImpl = null;

			const result = mapImpl.addMarker();

			expect(mapImpl.addMarker).toHaveBeenCalledTimes(1);
			expect(result).toBe(undefined);
		});
	});

	describe('constructor()', () => {
		it('calls _initializeMap as a fallback', () => {
			jest.spyOn(MapImpl.prototype, '_initializeMap');

			const mapImpl = new MapImpl();

			expect(mapImpl._initializeMap.mock.calls[0][0].location).toEqual({
				lat: 0,
				lng: 0,
			});
			expect(mapImpl.zoom).toBe(2);
		});

		it('requests the browser geolocation with a timeout', () => {
			const getCurrentPosition = jest.fn();

			navigator.geolocation = {getCurrentPosition};

			try {
				new MapImpl();

				expect(getCurrentPosition).toHaveBeenCalledWith(
					expect.any(Function),
					expect.any(Function),
					{timeout: 10000}
				);
			}
			finally {
				delete navigator.geolocation;
			}
		});
	});

	describe('destructor()', () => {
		it('disposes existing _geoJSONLayer', () => {
			const layer = {dispose: jest.fn()};

			mapImpl._geoJSONLayer = layer;

			expect(mapImpl._geoJSONLayer).toBe(layer);

			mapImpl.destructor();

			expect(mapImpl._geoJSONLayer).toBe(null);
			expect(layer.dispose).toHaveBeenCalledTimes(1);
		});

		it('disposes existing search custom control', () => {
			const control = {dispose: jest.fn()};

			mapImpl._geoJSONLayer = {dispose: jest.fn()};

			mapImpl._customControls = {[MapImpl.CONTROLS.SEARCH]: control};

			expect(mapImpl._customControls[MapImpl.CONTROLS.SEARCH]).toBe(
				control
			);

			mapImpl.destructor();

			expect(mapImpl._customControls[MapImpl.CONTROLS.SEARCH]).toBe(null);
			expect(control.dispose).toHaveBeenCalledTimes(1);
		});
	});

	describe('getNativeMap()', () => {
		it('returns the _map property', () => {
			mapImpl._map = {name: 'map'};

			expect(mapImpl.getNativeMap()).toBe(mapImpl._map);
		});
	});

	describe('openDialog()', () => {
		it('calls _getDialog() method', () => {
			mapImpl._getDialog = jest.fn(() => null);

			mapImpl.openDialog();

			expect(mapImpl._getDialog).toHaveBeenCalledTimes(1);
		});

		it('calls dialog.open() with the given dialog config', () => {
			const dialog = {open: jest.fn()};
			const dialogConfig = {opt1: 'asd'};

			mapImpl._getDialog = () => dialog;

			mapImpl.openDialog(dialogConfig);

			expect(dialog.open).toHaveBeenCalledTimes(1);
			expect(dialog.open.mock.calls[0][0]).toBe(dialogConfig);
		});
	});

	describe('_bindUIMB()', () => {
		it('binds handlers to featuresAdded and featureClick events on geoJSONLayer', () => {
			mapImpl._handleGeoJSONLayerFeatureClicked = jest.fn();
			mapImpl._handleGeoJSONLayerFeaturesAdded = jest.fn();

			mapImpl._geoJSONLayer = {on: jest.fn()};

			mapImpl._bindUIMB();

			expect(mapImpl._geoJSONLayer.on.mock.calls[0]).toEqual([
				'featuresAdded',
				mapImpl._handleGeoJSONLayerFeaturesAdded,
			]);
		});

		it('binds handlers to dragend event on geolocationMarker', () => {
			mapImpl._geolocationMarker = {on: jest.fn()};
			mapImpl._handleGeoLocationMarkerDragended = jest.fn();

			mapImpl._bindUIMB();

			expect(mapImpl._geolocationMarker.on.mock.calls[0]).toEqual([
				'dragend',
				mapImpl._handleGeoLocationMarkerDragended,
			]);
		});

		it('binds handlers to click event on customControls[HOME]', () => {
			mapImpl._handleHomeButtonClicked = jest.fn();

			mapImpl._customControls = {
				[MapImpl.CONTROLS.HOME]: {addEventListener: jest.fn()},
			};

			mapImpl._bindUIMB();

			expect(
				mapImpl._customControls[MapImpl.CONTROLS.HOME].addEventListener
					.mock.calls[0]
			).toEqual(['click', mapImpl._handleHomeButtonClicked]);
		});

		it('binds handlers to search event on customControls[SEARCH]', () => {
			mapImpl._handleSearchButtonClicked = jest.fn();
			mapImpl._customControls = {
				[MapImpl.CONTROLS.SEARCH]: {on: jest.fn()},
			};

			mapImpl._bindUIMB();

			expect(
				mapImpl._customControls[MapImpl.CONTROLS.SEARCH].on.mock
					.calls[0]
			).toEqual(['search', mapImpl._handleSearchButtonClicked]);
		});
	});

	describe('_getDialog()', () => {
		it('does nothing if there is no MapBase.DialogImpl', () => {
			MapImpl.DialogImpl = null;

			expect(mapImpl._getDialog()).toBe(null);
		});

		it('passes the existing map to the dialog constructor', () => {
			mapImpl._map = Math.random();

			mapImpl._getDialog();

			expect(MapImpl.DialogImpl.mock.calls[0][0].map).toBe(mapImpl._map);
		});

		it('reuses the existing dialog instance', () => {
			const dialog1 = mapImpl._getDialog();
			const dialog2 = mapImpl._getDialog();

			expect(dialog1).toBe(dialog2);
		});
	});

	describe('_getGeoCoder()', () => {
		it('does nothing if there is no MapBase.GeocoderImpl', () => {
			MapImpl.GeocoderImpl = null;

			mapImpl = new MapImpl();

			expect(mapImpl._getGeocoder()).toBe(null);
		});

		it('reuses the existing geocoder instance', () => {
			const geocoder1 = mapImpl._getGeocoder();
			const geocoder2 = mapImpl._getGeocoder();

			expect(geocoder1).toBe(geocoder2);
		});
	});

	describe('_initializeGeoJSONData()', () => {
		it('does nothing if there is no data attribute', () => {
			mapImpl._geoJSONLayer = {addData: jest.fn()};

			mapImpl.data = null;

			mapImpl._initializeGeoJSONData();

			expect(mapImpl._geoJSONLayer.addData).not.toHaveBeenCalled();
		});

		it('calls geoJSONLayer.addData with data attribute', () => {
			mapImpl._geoJSONLayer = {addData: jest.fn()};

			mapImpl.data = {name: 'more data'};

			mapImpl._initializeGeoJSONData();

			expect(mapImpl._geoJSONLayer.addData.mock.calls[0][0]).toBe(
				mapImpl.data
			);
		});
	});

	describe('_initializeLocation()', () => {
		it('directly calls initializeMap with the given location if geolocation is false', () => {
			const location = getLocation();

			mapImpl._initializeMap = jest.fn();

			mapImpl.geolocation = false;

			mapImpl._initializeLocation(location);

			expect(mapImpl._initializeMap.mock.calls[0][0]).toEqual({location});
		});
	});

	describe('_initializeMap()', () => {
		let position;

		beforeEach(() => {
			position = {location: getLocation()};
		});

		it('stores the given position as position', () => {
			mapImpl._initializeMap(position);

			expect(position).toBe(mapImpl.position);
		});

		it('stores the given position as originalPosition', () => {
			mapImpl._initializeMap(position);

			expect(position).toBe(mapImpl._originalPosition);
		});

		it('creates a new map and store it as _map', () => {
			mapImpl._initializeMap(position);

			expect(mapImpl._map.name).toBe('map');
			expect(mapImpl._map.location).toBe(position.location);
		});

		it('calls _getControlsConfig()', () => {
			jest.spyOn(mapImpl, '_getControlsConfig');

			mapImpl._initializeMap(position);

			expect(mapImpl._getControlsConfig).toHaveBeenCalledTimes(1);
		});

		it('calls _createCustomControls()', () => {
			jest.spyOn(mapImpl, '_createCustomControls');

			mapImpl._initializeMap(position);

			expect(mapImpl._createCustomControls).toHaveBeenCalledTimes(1);
		});

		it('calls _bindUIMB()', () => {
			jest.spyOn(mapImpl, '_bindUIMB');

			mapImpl._initializeMap(position);

			expect(mapImpl._bindUIMB).toHaveBeenCalledTimes(1);
		});

		it('calls _initializeGeoJSONData()', () => {
			jest.spyOn(mapImpl, '_initializeGeoJSONData');

			mapImpl._initializeMap(position);

			expect(mapImpl._initializeGeoJSONData).toHaveBeenCalledTimes(1);
		});
	});

	describe('_handleGeoJSONLayerFeaturesAdded()', () => {
		class Geometry {
			constructor() {
				this.location = getLocation();

				jest.spyOn(this, 'get');
			}

			get() {
				return this.location;
			}
		}

		class Feature {
			constructor() {
				this.geometry = new Geometry();

				jest.spyOn(this, 'getGeometry');
			}

			getGeometry() {
				return this.geometry;
			}
		}

		it('gets the map bounds using getBounds()', () => {
			mapImpl._handleGeoJSONLayerFeaturesAdded({features: []});

			expect(mapImpl.getBounds).toHaveBeenCalledTimes(1);
		});

		it('gets the feature geometry for each feature', () => {
			const feature = new Feature();

			mapImpl._handleGeoJSONLayerFeaturesAdded({features: [feature]});

			expect(feature.getGeometry).toHaveBeenCalledTimes(1);
			expect(feature.geometry.get).toHaveBeenCalledTimes(1);
		});

		it('updates the map position when there is a single feature', () => {
			const feature = new Feature();

			mapImpl._handleGeoJSONLayerFeaturesAdded({features: [feature]});

			expect(mapImpl.position.location).toBe(feature.geometry.location);
		});

		it('adds the features locations to the map bounds when there are multiple features', () => {
			const featureA = new Feature();
			const featureB = new Feature();

			mapImpl._handleGeoJSONLayerFeaturesAdded({
				features: [featureA, featureB],
			});

			expect(mapImpl.getBounds().locations).toEqual([
				featureA.geometry.location,
				featureB.geometry.location,
			]);
		});
	});

	describe('_handleGeoJSONLayerFeatureClicked()', () => {
		it('emits a featureClick event', () => {
			mapImpl._handleGeoJSONLayerFeatureClicked({});

			expect(mapImpl.emit.mock.calls[0][0]).toBe('featureClick');
		});

		it('sends the given feature as event data', () => {
			mapImpl._handleGeoJSONLayerFeatureClicked({feature: 'feature'});

			expect(mapImpl.emit.mock.calls[0][1]).toEqual({feature: 'feature'});
		});
	});

	describe('_handleGeoLocationMarkerDragended()', () => {
		it('gets the location position with geocoder.reverse()', () => {
			const location = getLocation();

			jest.spyOn(mapImpl._getGeocoder(), 'reverse');

			mapImpl._handleGeoLocationMarkerDragended({location});

			expect(mapImpl._getGeocoder().reverse.mock.calls[0][0]).toBe(
				location
			);
		});

		it('updates the instance position', () => {
			const location = getLocation();

			mapImpl._handleGeoLocationMarkerDragended({location});

			expect(mapImpl.position).toEqual({location, name: 'data'});
		});
	});

	describe('_handleHomeButtonClicked()', () => {
		it('calls preventDefault on the given event', () => {
			const event = {preventDefault: jest.fn()};
			const position = {location: getLocation()};

			mapImpl._originalPosition = position;

			mapImpl._handleHomeButtonClicked(event);

			expect(event.preventDefault).toHaveBeenCalledTimes(1);
		});

		it('sets the instance position to _originalPosition', () => {
			const event = {preventDefault: jest.fn()};
			const position = {location: getLocation()};

			mapImpl._originalPosition = position;

			mapImpl._handleHomeButtonClicked(event);

			expect(mapImpl.position).toBe(position);
		});
	});

	describe('_handlePositionChanged()', () => {
		it('calls setCenter with the given location', () => {
			const location = getLocation();

			mapImpl._handlePositionChanged({location});

			expect(mapImpl.setCenter.mock.calls[0]).toEqual([location]);
		});

		it('updates the geolocationMarker position if present', () => {
			const locationA = getLocation();
			const locationB = getLocation();

			mapImpl._geolocationMarker = mapImpl.addMarker(locationA);

			expect(mapImpl._geolocationMarker.location.location).toEqual(
				locationA
			);

			mapImpl._handlePositionChanged({location: locationB});

			expect(mapImpl._geolocationMarker.location).toEqual(locationB);
		});
	});

	describe('_handleSearchButtonClicked()', () => {
		it('updates the instance position', () => {
			const position = {location: getLocation()};

			mapImpl._handleSearchButtonClicked({position});

			expect(mapImpl.position).toBe(position);
		});
	});

	describe('getBounds()', () => {
		it('throws a not implemented Error', () => {
			expect(() => {
				new MapBase().getBounds();
			}).toThrow();
		});
	});

	describe('setCenter()', () => {
		it('throws a not implemented Error', () => {
			expect(() => {
				new MapBase().setCenter();
			}).toThrow();
		});
	});

	describe('_createMap()', () => {
		it('throws a not implemented Error', () => {
			expect(() => {
				new MapBase()._createMap();
			}).toThrow();
		});
	});
});
