/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import Location from '../../js/Location/Location';

const mockMapInstances: any[] = [];

const mockPlaceSession = {
	fetchPlace: jest.fn(),
	fetchPlaceSuggestions: jest.fn(),
};

/**
 * Waits for the map the field creates once its map packages are imported.
 */
async function waitForMap() {
	await waitFor(() => expect(mockMapInstances).toHaveLength(1));

	return mockMapInstances[0];
}

const RECIFE = {
	address: '35 Alfândega Street, Recife, Pernambuco',
	location: {lat: -8.0619, lng: -34.8711},
};

const RECIFE_VALUE = JSON.stringify({
	address: RECIFE.address,
	coordinates: {
		latitude: RECIFE.location.lat,
		longitude: RECIFE.location.lng,
	},
});

const MADRID = {
	address: 'P.º de la Castellana, 280, 28046 Madrid, Spain',
	location: {lat: 40.4764291, lng: -3.6858155},
};

const MADRID_VALUE = JSON.stringify({
	address: MADRID.address,
	coordinates: {
		latitude: MADRID.location.lat,
		longitude: MADRID.location.lng,
	},
});

jest.mock('@clayui/autocomplete', () => {
	const React = require('react');

	return {
		__esModule: true,
		default: Object.assign(
			({
				active,
				children,
				disabled,
				items,
				onBlur,
				onChange,
				onFocus,
				value,
			}: any) =>
				React.createElement(
					React.Fragment,
					null,
					React.createElement('input', {
						'data-testid': 'addressInput',
						disabled,
						onBlur,
						'onChange': ({target}: any) => onChange(target.value),
						onFocus,
						value,
					}),
					active && items.map((item: any) => children(item))
				),
			{
				Item: ({children, onClick}: any) =>
					React.createElement(
						'button',
						{onClick, type: 'button'},
						children
					),
			}
		),
	};
});

jest.mock('@liferay/map-common', () => ({
	MapBase: {
		CONTROLS: {HOME: 'home', PAN: 'pan', TYPE: 'type', ZOOM: 'zoom'},
	},
}));

jest.mock('@liferay/map-google-maps', () => {
	class FakeEmitter {
		listeners: {[eventName: string]: Array<(payload: any) => void>} = {};

		dispose() {}

		emit(eventName: string, payload: any) {
			(this.listeners[eventName] ?? []).forEach((listener) =>
				listener(payload)
			);
		}

		on(eventName: string, listener: (payload: any) => void) {
			this.listeners[eventName] = [
				...(this.listeners[eventName] ?? []),
				listener,
			];
		}
	}

	class FakeMap extends FakeEmitter {
		config: any;
		private _position: any;

		constructor(config: any) {
			super();

			this.config = config;
			this._position = config.position;

			this._handleSearchButtonClicked =
				this._handleSearchButtonClicked.bind(this);

			mockMapInstances.push(this);
		}

		_handleSearchButtonClicked({position}: {position: any}) {
			this.position = position;
		}

		get position() {
			return this._position;
		}

		set position(position: any) {
			this.emit('positionChange', {
				newVal: {
					address: position.address,
					location: position.location,
				},
			});

			this._position = position;
		}
	}

	return {
		MapGoogleMaps: FakeMap,
		createPlaceAutocompleteSession: jest.fn(() => mockPlaceSession),
		loadGoogleMaps: jest.fn((_googleMapsAPIKey, callback) => {
			callback();

			return jest.fn();
		}),
	};
});

jest.mock('@liferay/map-openstreetmap', () => ({
	MapOpenStreetMap: jest.requireMock('@liferay/map-google-maps')
		.MapGoogleMaps,
}));

jest.mock('data-engine-js-components-web', () => ({
	useFormState: jest.fn(() => ({
		availableLocales: [
			{displayName: 'English (United States)', localeId: 'en_US'},
			{displayName: 'Portuguese (Brazil)', localeId: 'pt_BR'},
		],
		defaultLanguageId: 'en_US',
		editingLanguageId: 'pt_BR',
	})),
}));

jest.mock('dynamic-data-mapping-form-field-type', () => {
	const React = require('react');

	return {
		LocalesDropdown: () =>
			React.createElement('div', {'data-testid': 'localesDropdown'}),
	};
});

jest.mock('dynamic-data-mapping-form-field-type/api', () => {
	const React = require('react');

	return {
		ReactFieldBase: ({children}: any) =>
			React.createElement('div', null, children),
	};
});

describe('Location', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockMapInstances.length = 0;
	});

	it('clears the value when the address is deleted', () => {
		const onChange = jest.fn();

		render(
			<Location
				fieldName="address"
				googleMapsAPIKey="key"
				mapProviderKey="GoogleMaps"
				name="address"
				onChange={onChange}
				value={RECIFE_VALUE}
			/>
		);

		fireEvent.change(screen.getByTestId('addressInput'), {
			target: {value: ''},
		});

		expect(onChange).toHaveBeenCalledWith({target: {value: ''}});
	});

	it('does not show an address input on OpenStreetMap', () => {
		render(
			<Location
				fieldName="address"
				mapProviderKey="OpenStreetMap"
				name="address"
				onChange={jest.fn()}
			/>
		);

		expect(screen.queryByTestId('addressInput')).not.toBeInTheDocument();
	});

	it('emits the address and coordinates when the pin moves', async () => {
		const onChange = jest.fn();

		render(
			<Location
				fieldName="address"
				googleMapsAPIKey="key"
				mapProviderKey="GoogleMaps"
				name="address"
				onChange={onChange}
			/>
		);

		const map = await waitForMap();

		act(() => {
			map.position = {address: 'Somewhere', location: {lat: 0, lng: 0}};
		});

		onChange.mockClear();

		act(() => {
			map.position = RECIFE;
		});

		expect(onChange).toHaveBeenCalledWith({
			target: {value: RECIFE_VALUE},
		});
		expect(screen.getByTestId('addressInput')).toHaveValue(RECIFE.address);
	});

	it('fixes the pin on the same map when the field becomes disabled', async () => {
		const props = {
			fieldName: 'address',
			mapProviderKey: 'OpenStreetMap' as const,
			name: 'address',
			onChange: jest.fn(),
			value: RECIFE_VALUE,
		};

		const {rerender} = render(<Location {...props} />);

		await waitForMap();

		rerender(<Location {...props} readOnly />);

		expect(mockMapInstances).toHaveLength(1);
		expect(mockMapInstances[0].draggablePin).toBe(false);
	});

	it('ignores pin moves while the field is disabled', async () => {
		const onChange = jest.fn();

		render(
			<Location
				fieldName="address"
				mapProviderKey="OpenStreetMap"
				name="address"
				onChange={onChange}
				readOnly
				value={RECIFE_VALUE}
			/>
		);

		const map = await waitForMap();

		act(() => {
			map.position = {address: 'Somewhere', location: {lat: 0, lng: 0}};
		});

		act(() => {
			map.position = MADRID;
		});

		expect(onChange).not.toHaveBeenCalled();
	});

	it('ignores the position the map reports while it initializes', async () => {
		const onChange = jest.fn();

		render(
			<Location
				fieldName="address"
				googleMapsAPIKey="key"
				mapProviderKey="GoogleMaps"
				name="address"
				onChange={onChange}
				value={RECIFE_VALUE}
			/>
		);

		const map = await waitForMap();

		expect(map.config.draggablePin).toBe(true);
		expect(map.config.position).toEqual(RECIFE);

		act(() => {
			map.position = {
				address: 'Reverse geocoded address',
				location: RECIFE.location,
			};
		});

		expect(onChange).not.toHaveBeenCalled();
		expect(screen.getByTestId('addressInput')).toHaveValue(RECIFE.address);
	});

	it('lets the user type an address on Google Maps', async () => {
		jest.useFakeTimers();

		mockPlaceSession.fetchPlaceSuggestions.mockResolvedValue([
			{placeId: 'madrid', text: MADRID.address},
		]);

		render(
			<Location
				fieldName="address"
				googleMapsAPIKey="key"
				mapProviderKey="GoogleMaps"
				name="address"
				onChange={jest.fn()}
			/>
		);

		const input = screen.getByTestId('addressInput');

		fireEvent.change(input, {target: {value: 'Paseo de la Castellana'}});

		expect(input).toHaveValue('Paseo de la Castellana');

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(mockPlaceSession.fetchPlaceSuggestions).toHaveBeenCalledWith(
			'Paseo de la Castellana'
		);
		expect(screen.getByText(MADRID.address)).toBeInTheDocument();

		jest.useRealTimers();
	});

	it('moves the pin to the picked Google suggestion', async () => {
		jest.useFakeTimers();

		const placePrediction = {placeId: 'madrid', text: MADRID.address};

		mockPlaceSession.fetchPlace.mockResolvedValue({
			...MADRID,
			viewport: null,
		});
		mockPlaceSession.fetchPlaceSuggestions.mockResolvedValue([
			placePrediction,
		]);

		const onChange = jest.fn();

		render(
			<Location
				fieldName="address"
				googleMapsAPIKey="key"
				mapProviderKey="GoogleMaps"
				name="address"
				onChange={onChange}
			/>
		);

		const map = await waitForMap();

		act(() => {
			map.position = {address: 'Somewhere', location: {lat: 0, lng: 0}};
		});

		fireEvent.change(screen.getByTestId('addressInput'), {
			target: {value: 'Paseo de la Castellana'},
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		fireEvent.click(screen.getByText(MADRID.address));

		await act(async () => {});

		expect(
			jest.requireMock('@liferay/map-google-maps')
				.createPlaceAutocompleteSession
		).toHaveBeenCalledTimes(1);
		expect(mockPlaceSession.fetchPlace).toHaveBeenCalledWith(
			placePrediction
		);
		expect(onChange).toHaveBeenCalledWith({
			target: {value: MADRID_VALUE},
		});
		expect(screen.getByTestId('addressInput')).toHaveValue(MADRID.address);

		jest.useRealTimers();
	});

	it('shows the map when the field is disabled', async () => {
		render(
			<Location
				fieldName="address"
				mapProviderKey="OpenStreetMap"
				name="address"
				onChange={jest.fn()}
				readOnly
				value={RECIFE_VALUE}
			/>
		);

		const map = await waitForMap();

		expect(map.config.draggablePin).toBe(false);
		expect(map.config.position).toEqual(RECIFE);
		expect(
			document.querySelector(map.config.boundingBox)
		).toBeInTheDocument();
	});

	it('shows the stored address', () => {
		render(
			<Location
				fieldName="address"
				googleMapsAPIKey="key"
				mapProviderKey="GoogleMaps"
				name="address"
				onChange={jest.fn()}
				value={RECIFE_VALUE}
			/>
		);

		expect(screen.getByTestId('addressInput')).toHaveValue(RECIFE.address);
	});

	it('stores the value under the editing language when localized', async () => {
		const onChange = jest.fn();

		render(
			<Location
				fieldName="address"
				localizedObjectField
				mapProviderKey="OpenStreetMap"
				name="address"
				onChange={onChange}
				value={{en_US: RECIFE_VALUE}}
			/>
		);

		expect(screen.getByTestId('localesDropdown')).toBeInTheDocument();

		const map = await waitForMap();

		act(() => {
			map.position = {address: 'Somewhere', location: {lat: 0, lng: 0}};
		});

		act(() => {
			map.position = MADRID;
		});

		expect(onChange).toHaveBeenCalledWith({
			target: {
				value: {
					en_US: RECIFE_VALUE,
					pt_BR: MADRID_VALUE,
				},
			},
		});
	});

	it('uses a CSS-safe id for the map container', async () => {
		const name = '_ns_ddm$$address$xEpVTXMf$0$$en_US';

		render(
			<Location
				fieldName="address"
				mapProviderKey="OpenStreetMap"
				name={name}
				onChange={jest.fn()}
			/>
		);

		const map = await waitForMap();

		expect(map.config.boundingBox).not.toContain('$');
		expect(
			document.querySelector(map.config.boundingBox)
		).toBeInTheDocument();
	});
});
