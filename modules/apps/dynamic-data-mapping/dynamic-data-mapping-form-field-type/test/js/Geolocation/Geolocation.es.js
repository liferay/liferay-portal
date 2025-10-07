/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

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
		ThemeDisplay: {
			getPathThemeImages: () => '',
		},
		detach: jest.fn(),
		on: jest.fn(),
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
			}),
			removeAllListeners: jest.fn(),
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
});
