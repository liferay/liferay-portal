import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import DatePicker from './date-picker';
import getCN from 'classnames';
import Input from './Input';
import MaskedInput from './MaskedInput';
import moment from 'moment';
import React, {useRef, useState} from 'react';
import {
	applyTimeZone,
	DATE_MASK,
	DATE_TIME_MASK,
	DEFAULT_DATE_FORMAT,
	DEFAULT_TIMEZONE_ID,
} from 'shared/util/date';
import {DatePickerRetentionPeriodHeader} from './DatePickerRetentionPeriodHeader';
import {formatDateWithTimezone} from './dropdown-range-key/utils';
import {noop} from 'lodash';
import {useRetentionPeriod} from 'shared/hooks/useRetentionPeriod';

interface IDateInputProps extends React.HTMLAttributes<HTMLInputElement> {
	displayFormat?: string;
	format?: string;
	name?: string;
	onDateInputBlur?: (param: any) => void;
	onDateInputChange?: (param: any) => void;
	overlayAlignment?: OverlayAlignment;
	readOnly?: boolean;
	showRetentionPeriod?: boolean;
	showTimeSelector?: boolean;
	timeZoneId?: string;
	usePortal?: boolean;
	value: any;
}

export type OverlayAlignment = 'bottomLeft' | 'rightCenter';

const DateInput: React.FC<IDateInputProps> = ({
	className,
	displayFormat,
	format = DEFAULT_DATE_FORMAT,
	id,
	name,
	onDateInputBlur = noop,
	onDateInputChange = noop,
	readOnly = false,
	showRetentionPeriod = true,
	showTimeSelector = false,
	timeZoneId,
	value,
}) => {
	const [active, setActive] = useState(false);

	// Clay closes the picker from its own trigger through a callback it froze on
	// the first render, so reading the blur handler from that closure would call
	// a stale one. Route it through a ref instead.

	const onDateInputBlurRef = useRef(onDateInputBlur);

	onDateInputBlurRef.current = onDateInputBlur;

	const handleDateSelect = (value: any): void => {
		onDateInputChange(value.format(format));

		setActive(false);
	};

	const handleChange = ({target}: React.BaseSyntheticEvent<Event>): void => {
		const {value} = target;

		if (moment(value, format, true).isValid()) {
			onDateInputChange(value);
		}
	};

	const date = showTimeSelector
		? applyTimeZone(value, timeZoneId)
		: moment(value, format);

	// The displayed text and the calendar highlight read the same parse, so an
	// empty value leaves the mask placeholder visible on both instead of
	// rendering moment's "Invalid date" string.

	const getDateValue = (): string => {
		if (!displayFormat) {
			return value;
		}

		return date.isValid() ? date.format(displayFormat) : '';
	};

	const retentionPeriod = useRetentionPeriod();

	const minDate = formatDateWithTimezone(
		timeZoneId || DEFAULT_TIMEZONE_ID
	).clone();

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={Align.TopLeft}
			className="dropdown-range-key-root"
			menuElementAttrs={{
				className: getCN(className, 'dropdown-range-key-menu-root', {
					'show-date-picker': active,
				}),
			}}
			onActiveChange={(active) => {
				setActive(active);

				!active && onDateInputBlurRef.current();
			}}
			trigger={
				<div>
					<Input.Group>
						<Input.GroupItem>
							<MaskedInput
								autoComplete="off"
								data-testid="date-input"
								id={id}
								inset="after"
								keepCharPositions
								mask={
									showTimeSelector
										? DATE_TIME_MASK
										: DATE_MASK
								}
								name={name}
								onChange={handleChange}
								onClick={() => setActive(true)}
								placeholder={
									showTimeSelector
										? Liferay.Language.get(
												'yyyy-mm-dd-hh-mm-zz'
											)
										: Liferay.Language.get('yyyy-mm-dd')
								}
								readOnly={readOnly}
								showMask
								value={getDateValue()}
							/>

							<Input.Inset position="after">
								<ClayButton
									aria-label={Liferay.Language.get(
										'choose-a-date'
									)}
									className="button-root"
									displayType="unstyled"
									onClick={() => setActive(true)}
								>
									<ClayIcon
										className="icon-root"
										symbol="calendar"
									/>
								</ClayButton>
							</Input.Inset>
						</Input.GroupItem>
					</Input.Group>
				</div>
			}
		>
			<DatePicker
				date={date.isValid() ? date : null}
				header={
					showRetentionPeriod && retentionPeriod ? (
						<DatePickerRetentionPeriodHeader
							retentionPeriod={retentionPeriod!}
						/>
					) : null
				}
				maxDate={formatDateWithTimezone(
					timeZoneId || DEFAULT_TIMEZONE_ID
				)
					.clone()
					.subtract(1, 'days')}
				maxRange={365}
				minDate={
					showRetentionPeriod && retentionPeriod
						? minDate.subtract(retentionPeriod!, 'months')
						: minDate.subtract(100, 'years')
				}
				onSelect={handleDateSelect}
				showTimeSelector={showTimeSelector}
				timeZoneId={timeZoneId}
			/>
		</ClayDropDown>
	);
};

export default DateInput;
