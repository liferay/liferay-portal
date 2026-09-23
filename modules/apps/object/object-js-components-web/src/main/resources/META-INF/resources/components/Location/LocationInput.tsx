/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import {ClayInput} from '@clayui/form';
import {openToast} from 'frontend-js-components-web';
import {cancelDebounce, debounce} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {
	LocationValue,
	MAP_PROVIDER,
	MapProviderKey,
	parseLocationValue,
} from './locationUtil';
import {useLocationMap} from './useLocationMap';

import './LocationInput.scss';

interface LocationInputProps {
	children?: React.ReactNode;
	disabled?: boolean;
	googleMapsAPIKey?: string;
	id: string;
	mapProviderKey?: MapProviderKey;
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onLocationChange: (locationValue: LocationValue | null) => void;
	serializedValue?: string;
}

/**
 * Renders the map with its draggable pin. On Google Maps, an address input
 * below the map offers Places suggestions, with `children` placed next to the
 * input. OpenStreetMap shows only the map, since Nominatim forbids
 * autocomplete, so `children` are placed next to the map.
 */
export function LocationInput({
	children,
	disabled,
	googleMapsAPIKey,
	id,
	mapProviderKey = MAP_PROVIDER.openStreetMap,
	onBlur,
	onFocus,
	onLocationChange,
	serializedValue,
}: LocationInputProps) {
	const locationValue = useMemo(
		() => parseLocationValue(serializedValue),
		[serializedValue]
	);

	const [address, setAddress] = useState(locationValue?.address ?? '');

	// DDM field ids contain "$", which is not valid in a CSS id selector, and
	// the map looks its container up with querySelector. The map element id is
	// taken from the `id` prop once on mount and never updates, so the map is
	// not destroyed and recreated when the prop changes with the editing
	// language. The address input keeps using the current `id` prop.

	const [mapElementId] = useState(() => `${id}_map`.replace(/[^\w-]/g, '_'));
	const [suggestions, setSuggestions] = useState<any[]>([]);

	const placeSessionRef = useRef<any>(null);
	const typedAddressRef = useRef('');

	/**
	 * Each address input keeps its own autocomplete session, so two Location
	 * fields on the same form never share a session token.
	 */
	const getPlaceSession = useCallback(async () => {
		if (!placeSessionRef.current) {
			const {createPlaceAutocompleteSession} = await import(
				'@liferay/map-google-maps'
			);

			placeSessionRef.current = createPlaceAutocompleteSession();
		}

		return placeSessionRef.current;
	}, []);

	const debouncedFetchPlaceSuggestions = useMemo(
		() =>
			debounce(async (input: string) => {
				try {
					const placeSession = await getPlaceSession();

					const placePredictions =
						await placeSession.fetchPlaceSuggestions(input);

					// Drop suggestions for text the input no longer shows,
					// such as after it is cleared or loses focus.

					if (input === typedAddressRef.current) {
						setSuggestions(placePredictions);
					}
				}
				catch {
					setSuggestions([]);
				}
			}, 300),
		[getPlaceSession]
	);

	// Update the input when the value changes outside it, such as when the
	// editing language changes.

	useEffect(() => {
		setAddress(locationValue?.address ?? '');
	}, [locationValue]);

	// Cancel the pending suggestions request when the field unmounts.

	useEffect(
		() => () => cancelDebounce(debouncedFetchPlaceSuggestions),
		[debouncedFetchPlaceSuggestions]
	);

	const handleAddressChange = (newAddress: string) => {
		setAddress(newAddress);

		typedAddressRef.current = newAddress;

		if (!newAddress) {
			cancelDebounce(debouncedFetchPlaceSuggestions);

			setSuggestions([]);

			onLocationChange(null);

			return;
		}

		debouncedFetchPlaceSuggestions(newAddress);
	};

	/**
	 * Show the stored address when the input is blurred. The actual value is
	 * only stored when a suggestion is picked. Clay's `allowsCustomValue` keeps
	 * Clay from clearing the input when the suggestions close.
	 */
	const handleBlur = (event: React.FocusEvent<HTMLInputElement>) => {
		cancelDebounce(debouncedFetchPlaceSuggestions);

		typedAddressRef.current = '';

		setAddress(locationValue?.address ?? '');

		onBlur?.(event);
	};

	const handlePositionChange = (newLocationValue: LocationValue) => {
		setAddress(newLocationValue.address);

		onLocationChange(newLocationValue);
	};

	const {setPosition} = useLocationMap({
		disabled,
		googleMapsAPIKey,
		mapElementId,
		mapProviderKey,
		onPositionChange: handlePositionChange,
		value: locationValue,
	});

	const handleSuggestionClick = async (
		event: React.MouseEvent,
		placePrediction: any
	) => {

		// Keeps Clay from writing the suggestion text into the input, which
		// would fetch suggestions for it again. The address comes from the
		// picked place instead.

		event.preventDefault();

		setSuggestions([]);

		try {
			const placeSession = await getPlaceSession();

			setPosition(await placeSession.fetchPlace(placePrediction));
		}
		catch {
			setAddress(locationValue?.address ?? '');

			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	const hasAddressInput = mapProviderKey === MAP_PROVIDER.googleMaps;

	return (
		<>
			<ClayInput.Group className="align-items-start">
				<ClayInput.GroupItem>
					<div
						className="object-field__location-map"
						id={mapElementId}
					/>
				</ClayInput.GroupItem>

				{!hasAddressInput && children}
			</ClayInput.Group>

			{hasAddressInput && (
				<ClayInput.Group className="mt-3">
					<ClayInput.GroupItem>
						<ClayAutocomplete
							active={!!suggestions.length}
							allowsCustomValue
							disabled={disabled}
							id={id}
							items={suggestions}
							onActiveChange={(active: boolean) => {
								if (!active) {
									setSuggestions([]);
								}
							}}
							onBlur={handleBlur}
							onChange={handleAddressChange}
							onFocus={onFocus}
							onItemsChange={(newSuggestions: any[] | null) =>
								setSuggestions(newSuggestions ?? [])
							}
							value={address}
						>
							{(placePrediction: any) => (
								<ClayAutocomplete.Item
									key={placePrediction.placeId}
									onClick={(event: React.MouseEvent) =>
										handleSuggestionClick(
											event,
											placePrediction
										)
									}
								>
									{String(placePrediction.text)}
								</ClayAutocomplete.Item>
							)}
						</ClayAutocomplete>
					</ClayInput.GroupItem>

					{children}
				</ClayInput.Group>
			)}
		</>
	);
}
