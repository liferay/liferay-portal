import * as API from 'shared/api';
import Form, {validateRequired} from 'shared/components/form';
import React, {useState} from 'react';
import {TimeZone} from 'shared/util/records';
import {useRequest} from 'shared/hooks/useRequest';

interface ITimeZonePicker {
	disabled?: boolean;
	fieldName: string;
	initialTimeZone?: TimeZone;
	setFieldTouched: Function;
	setFieldValue: Function;
	onCountryChange?: Function;
}

const TimeZonePicker: React.FC<ITimeZonePicker> = ({
	disabled = false,
	fieldName,
	initialTimeZone,
	onCountryChange,
	setFieldTouched,
	setFieldValue,
}) => {
	const {data: timezonesAvailable, loading} = useRequest({
		dataSourceFn: API.projects.fetchAvailableTimeZones,
		variables: {},
	});

	const [selectedCountry, setSelectedCountry] = useState<string>(
		(initialTimeZone && initialTimeZone.country) || ''
	);

	const getCountries = (): Array<string> =>
		timezonesAvailable
			? Array.from(
					new Set(
						timezonesAvailable
							.map((timeZone: TimeZone) => timeZone.country)
							.sort()
					)
				)
			: [];

	const getTimeZones = (): Array<TimeZone> =>
		timezonesAvailable
			? timezonesAvailable.filter(
					({country}: TimeZone) =>
						!selectedCountry || country === selectedCountry
				)
			: [];

	const handleSelectCountry = (
		event:
			| React.ChangeEvent<HTMLSelectElement>
			| React.FocusEvent<HTMLSelectElement>
	): void => {
		setSelectedCountry(event.target.value);
		setFieldTouched(fieldName, 'UTC');
		setFieldValue(fieldName, 'UTC');

		if (onCountryChange) {
			onCountryChange(event.target.value);
		}
	};

	return (
		<Form.Group autoFit className="time-zone-picker-root">
			<Form.GroupItem className="col-3 p-0">
				<select
					className="form-control select-root"
					disabled={disabled || loading}
					onBlur={handleSelectCountry}
					onChange={handleSelectCountry}
					value={selectedCountry}
				>
					<option value="">{Liferay.Language.get('country')}</option>

					{getCountries().map((country) => (
						<Form.Select.Item key={country} value={country}>
							{country}
						</Form.Select.Item>
					))}
				</select>
			</Form.GroupItem>

			<Form.GroupItem className="col-9 p-0">
				<Form.Select
					disabled={disabled || loading || !selectedCountry}
					name={fieldName}
					showBlankOption
					validate={validateRequired}
				>
					{getTimeZones().map(({displayTimeZone, timeZoneId}) => (
						<Form.Select.Item key={timeZoneId} value={timeZoneId}>
							{displayTimeZone}
						</Form.Select.Item>
					))}
				</Form.Select>
			</Form.GroupItem>
		</Form.Group>
	);
};

export default TimeZonePicker;
