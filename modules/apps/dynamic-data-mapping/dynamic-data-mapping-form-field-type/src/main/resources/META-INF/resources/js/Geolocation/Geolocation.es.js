/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import 'leaflet/dist/leaflet.css';
import ClayIcon from '@clayui/icon';
import {useFormState} from 'data-engine-js-components-web';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React, {useCallback, useState} from 'react';

import {MAP_PROVIDER, useGeolocation} from './useGeolocation.es';

const geolocateTitle = Liferay.Language.get('geolocate');
const pathThemeImages = Liferay.ThemeDisplay.getPathThemeImages();

class NoRender extends React.Component {
	shouldComponentUpdate() {
		return false;
	}

	render() {
		return <div {...this.props} />;
	}
}

const Geolocation = ({
	disabled,
	googleMapsAPIKey,
	instanceId,
	mapProviderKey,
	name,
	onChange,
	value,
	viewMode,
	...otherProps
}) => {
	const [address, setAddress] = useState();
	const {editingLanguageId} = useFormState();

	const handleChange = useCallback(
		({newVal: {address, location}}) => {
			setAddress(address);
			onChange(JSON.stringify(location));
		},
		[onChange, setAddress]
	);

	useGeolocation({
		disabled,
		editingLanguageId,
		googleMapsAPIKey,
		instanceId,
		mapProviderKey,
		name,
		onChange: handleChange,
		value,
		viewMode,
	});

	return (
		<div {...otherProps} className="ddm-geolocation field-labels-inline">
			{!disabled || viewMode ? (
				<div>
					<div>
						<ClayIcon symbol="geolocation" />

						{address}
					</div>

					<dl>
						<dt className="text-capitalize"></dt>

						<dd>
							<NoRender
								className="lfr-map"
								id={`map_${instanceId}`}
								style={{height: '280px'}}
							/>

							<input
								id={`input_value_${instanceId}`}
								name={name}
								type="hidden"
							/>
						</dd>
					</dl>
				</div>
			) : (
				<img
					alt={Liferay.Language.get('geolocation')}
					className="w-100"
					src={`${pathThemeImages}/common/geolocation.png`}
					style={{maxWidth: '150px'}}
					title={geolocateTitle}
				/>
			)}
		</div>
	);
};

const Main = ({
	googleMapsAPIKey,
	instanceId,
	mapProviderKey = MAP_PROVIDER.openStreetMap,
	name,
	onChange,
	readOnly,
	value,
	viewMode,
	...otherProps
}) => (
	<FieldBase
		instanceId={instanceId}
		name={name}
		readOnly={readOnly}
		{...otherProps}
	>
		<Geolocation
			disabled={readOnly}
			googleMapsAPIKey={googleMapsAPIKey}
			instanceId={instanceId}
			mapProviderKey={mapProviderKey}
			name={name}
			onChange={(value) => {
				if (value !== '{"lat":0,"lng":0}') {
					onChange({}, value);
				}
			}}
			value={value}
			viewMode={viewMode}
		/>
	</FieldBase>
);

Main.displayName = 'Geolocation';

export default Main;
