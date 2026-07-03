/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MapOpenStreetMap} from '@liferay/map-openstreetmap';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
	ConfigProvider,
	FormProvider,
	PageProvider,
	languageReducer,
	useForm,
	useFormState,
} from 'data-engine-js-components-web';
import React from 'react';

import Geolocation from '../../../src/main/resources/META-INF/resources/js/Geolocation/Geolocation.es';

afterAll(() => {
	jest.clearAllMocks();
});

beforeAll(() => {
	window.Liferay = {
		Language: {get: (key) => key},
		MapBase: {
			CONTROLS: {},
			register: jest.fn(),
		},
		Maps: {},
		ThemeDisplay: {
			getPathThemeImages: () => '',
		},
		detach: jest.fn(),
		fire: jest.fn(),
		namespace: jest.fn((name) => {
			window.Liferay[name] = window.Liferay[name] || {};

			return window.Liferay[name];
		}),
		on: jest.fn(),
		once: jest.fn(),
	};
});

const mockReverse = jest.fn((location, callback) => {
	if (location.lat === 1 && location.lng === 1) {
		callback({data: {address: 'en_US Address'}});
	}
	else {
		callback({data: {address: 'pt_BR Address'}});
	}
});

jest.mock('@liferay/map-openstreetmap', () => ({
	MapOpenStreetMap: jest.fn().mockImplementation(() => {
		const mockInstance = {
			_geocoder: {
				reverse: mockReverse,
			},
			_listeners: {},
			dispose: jest.fn(),
			emit: jest.fn(function (eventName, payload) {
				if (this._listeners[eventName]) {
					this._listeners[eventName](payload);
				}
			}),
			on: jest.fn((eventName, callback) => {
				mockInstance._listeners[eventName] = callback;

				return {
					removeListener: jest.fn(() => {
						delete mockInstance._listeners[eventName];
					}),
				};
			}),
			setCenter: jest.fn(),
		};

		return mockInstance;
	}),
}));

const GeolocationWrapper = () => {
	const {editingLanguageId} = useFormState();

	const value =
		editingLanguageId === 'en_US' ? {lat: 1, lng: 1} : {lat: 2, lng: 2};

	return (
		<Geolocation
			instanceId="test"
			mapProviderKey="OpenStreetMap"
			name="geo"
			onChange={jest.fn()}
			value={value}
		/>
	);
};

const LanguageChangeButton = () => {
	const dispatch = useForm();

	return (
		<button
			onClick={() => {
				dispatch({
					payload: {
						editingLanguageId: 'pt_BR',
					},
					type: 'language_change',
				});
			}}
		>
			Language Change Button
		</button>
	);
};

describe('Geolocation', () => {
	it('updates address when editingLanguageId changes', async () => {
		const {getByRole} = render(
			<ConfigProvider value={{defaultLanguageId: 'en_US'}}>
				<FormProvider
					initialState={{
						editingLanguageId: 'en_US',
						pages: [],
					}}
					reducers={[languageReducer]}
				>
					<PageProvider value={{pageIndex: 0}}>
						<>
							<LanguageChangeButton />
							<GeolocationWrapper />
						</>
					</PageProvider>
				</FormProvider>
			</ConfigProvider>
		);

		expect(await screen.findAllByText('en_US Address')).toBeTruthy();

		userEvent.click(getByRole('button', {name: 'Language Change Button'}));

		expect(await screen.findAllByText('pt_BR Address')).toBeTruthy();
		expect(screen.queryAllByText('en_US Address')).toHaveLength(0);
	});

	it('removes only the listener held in useGeolocation and not any other listeners', () => {
		const renderGeolocation = () => (
			<ConfigProvider value={{defaultLanguageId: 'en_US'}}>
				<FormProvider
					initialState={{
						editingLanguageId: 'en_US',
						pages: [],
					}}
					reducers={[languageReducer]}
				>
					<PageProvider value={{pageIndex: 0}}>
						<GeolocationWrapper />
					</PageProvider>
				</FormProvider>
			</ConfigProvider>
		);

		const {rerender} = render(renderGeolocation());

		const {results} = MapOpenStreetMap.mock;

		const mapInstance = results[results.length - 1].value;

		const {results: onResults} = mapInstance.on.mock;

		const previousListener = onResults[onResults.length - 1].value;

		rerender(renderGeolocation());

		const currentListener = onResults[onResults.length - 1].value;

		expect(previousListener.removeListener).toHaveBeenCalledTimes(1);
		expect(currentListener.removeListener).not.toHaveBeenCalled();
	});
});

describe('Geolocation Google Maps loader', () => {
	const gmapsScriptSelector =
		'script[src*="maps.googleapis.com/maps/api/js"]';

	const renderGoogleMapsField = (instanceId, name) =>
		render(
			<ConfigProvider value={{defaultLanguageId: 'en_US'}}>
				<FormProvider
					initialState={{editingLanguageId: 'en_US', pages: []}}
					reducers={[languageReducer]}
				>
					<PageProvider value={{pageIndex: 0}}>
						<Geolocation
							instanceId={instanceId}
							mapProviderKey="GoogleMaps"
							name={name}
							onChange={jest.fn()}
							value={{lat: 1, lng: 1}}
						/>
					</PageProvider>
				</FormProvider>
			</ConfigProvider>
		);

	beforeEach(() => {
		delete window.google;

		window.Liferay.Maps = {};

		document
			.querySelectorAll(gmapsScriptSelector)
			.forEach((script) => script.remove());
	});

	afterEach(() => {
		document
			.querySelectorAll(gmapsScriptSelector)
			.forEach((script) => script.remove());
	});

	it('injects the Google Maps API script only once for two fields', () => {
		render(
			<ConfigProvider value={{defaultLanguageId: 'en_US'}}>
				<FormProvider
					initialState={{
						editingLanguageId: 'en_US',
						pages: [],
					}}
					reducers={[languageReducer]}
				>
					<PageProvider value={{pageIndex: 0}}>
						<>
							<Geolocation
								instanceId="first"
								mapProviderKey="GoogleMaps"
								name="geoFirst"
								onChange={jest.fn()}
								value={{lat: 1, lng: 1}}
							/>

							<Geolocation
								instanceId="second"
								mapProviderKey="GoogleMaps"
								name="geoSecond"
								onChange={jest.fn()}
								value={{lat: 2, lng: 2}}
							/>
						</>
					</PageProvider>
				</FormProvider>
			</ConfigProvider>
		);

		expect(document.querySelectorAll(gmapsScriptSelector)).toHaveLength(1);
	});

	it('re-injects the Google Maps API script after a failed load', () => {
		renderGoogleMapsField('first', 'geoFirst');

		const [script] = document.querySelectorAll(gmapsScriptSelector);

		expect(script).toBeTruthy();
		expect(window.Liferay.Maps.gmapsLoading).toBe(true);

		script.dispatchEvent(new Event('error'));

		expect(window.Liferay.Maps.gmapsLoading).toBe(false);
		expect(document.querySelectorAll(gmapsScriptSelector)).toHaveLength(0);

		renderGoogleMapsField('second', 'geoSecond');

		expect(document.querySelectorAll(gmapsScriptSelector)).toHaveLength(1);
	});
});

describe('Geolocation map configuration', () => {
	it('builds an isolated position for each field', () => {
		MapOpenStreetMap.mockClear();

		render(
			<ConfigProvider value={{defaultLanguageId: 'en_US'}}>
				<FormProvider
					initialState={{editingLanguageId: 'en_US', pages: []}}
					reducers={[languageReducer]}
				>
					<PageProvider value={{pageIndex: 0}}>
						<>
							<Geolocation
								instanceId="a"
								mapProviderKey="OpenStreetMap"
								name="geoA"
								onChange={jest.fn()}
								value={{lat: 1, lng: 1}}
							/>

							<Geolocation
								instanceId="b"
								mapProviderKey="OpenStreetMap"
								name="geoB"
								onChange={jest.fn()}
								value={{lat: 2, lng: 2}}
							/>
						</>
					</PageProvider>
				</FormProvider>
			</ConfigProvider>
		);

		const [firstConfig, secondConfig] = MapOpenStreetMap.mock.calls.map(
			([config]) => config
		);

		expect(firstConfig.position.location).toEqual({lat: 1, lng: 1});
		expect(secondConfig.position.location).toEqual({lat: 2, lng: 2});
		expect(firstConfig.position).not.toBe(secondConfig.position);
	});
});
