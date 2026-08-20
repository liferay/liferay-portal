import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import DatePicker from './date-picker';
import getCN from 'classnames';
import Input from './Input';
import moment from 'moment';
import React, {useRef, useState} from 'react';
import {DatePickerRetentionPeriodHeader} from './DatePickerRetentionPeriodHeader';
import {DEFAULT_DATE_FORMAT} from 'shared/util/date';
import {formatDateWithTimezone} from './dropdown-range-key/utils';
import {isNil, noop} from 'lodash';
import {sub} from 'shared/util/lang';
import {useRetentionPeriod} from 'shared/hooks/useRetentionPeriod';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const convertToMoment = (
	value: string,
	format: string
): moment.Moment | null => {
	const date = moment(value, format);

	return date.isValid() ? date : null;
};

const formatMoment = (value: moment.Moment | null, format: string): string =>
	isNil(value) ? '' : value.format(format);

export type DateRange = {
	end: string;
	start: string;
};

export type MomentDateRange = {
	end: moment.Moment | null;
	start: moment.Moment | null;
};

interface IDateInputProps {
	className?: string;
	displayFormat?: string;
	format?: string;
	groupId?: string;
	limitEndDate?: boolean;
	id?: string;
	name?: string;
	onBlur?: (event?: FocusEvent) => void;
	onChange: (range: DateRange) => void;
	overlayAlignment?: string;
	maxRange?: number;
	showRetentionPeriod?: boolean;
	usePortal?: boolean;
	value: DateRange;
}

const DateInput: React.FC<IDateInputProps> = ({
	className,
	displayFormat,
	format = DEFAULT_DATE_FORMAT,
	groupId,
	limitEndDate = true,
	maxRange = 365,
	onBlur = noop,
	onChange = noop,
	showRetentionPeriod = true,
	value,
}) => {
	const [active, setActive] = useState(false);

	const {timeZoneId} = useTimeZone(groupId);
	const retentionPeriod = useRetentionPeriod();

	// Clay closes the picker from its own trigger through a callback it froze on
	// the first render, so reading the blur handler from that closure would call
	// a stale one. Route it through a ref instead.

	const onBlurRef = useRef(onBlur);

	onBlurRef.current = onBlur;

	// The emitted range is read back with `format`, so it is written with
	// `format`; `displayFormat` is locale aware and only ever reaches the text
	// the trigger shows.

	const handleDateSelect = ({end, start}: MomentDateRange) => {
		onChange({
			end: formatMoment(end, format),
			start: formatMoment(start, format),
		});
	};

	const getDateRangeDisplay = ({end, start}: MomentDateRange): string => {
		if (end || start) {
			return sub(Liferay.Language.get('x-to-x'), [
				formatMoment(start, displayFormat || format),
				formatMoment(end, displayFormat || format),
			]) as string;
		}

		return '';
	};

	const momentDateRange = {
		end: convertToMoment(value.end, format),
		start: convertToMoment(value.start, format),
	};

	const minDate = formatDateWithTimezone(timeZoneId).clone();

	if (maxRange === -1) {
		maxRange = Number.MAX_SAFE_INTEGER;
	}

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={Align.TopLeft}
			className={getCN(className, 'dropdown-range-key-root')}
			menuElementAttrs={{
				className: getCN('dropdown-range-key-menu-root', {
					'show-date-picker': active,
				}),
				style: {
					zIndex: 1060,
				},
			}}
			onActiveChange={(active) => {
				setActive(active);

				!active && onBlurRef.current();
			}}
			trigger={
				<div>
					<Input.Group>
						<Input.GroupItem>
							<Input
								autoComplete="off"
								data-testid="date-range-input"
								inset="after"
								onClick={() => setActive(true)}
								placeholder={sub(
									Liferay.Language.get('x-to-x'),
									[
										Liferay.Language.get('yyyy-mm-dd'),
										Liferay.Language.get('yyyy-mm-dd'),
									]
								)}
								readOnly
								value={getDateRangeDisplay(momentDateRange)}
							/>

							<Input.Inset position="after">
								<ClayButton
									aria-label={Liferay.Language.get(
										'choose-date-range'
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
				date={momentDateRange}
				header={
					showRetentionPeriod && retentionPeriod ? (
						<DatePickerRetentionPeriodHeader
							retentionPeriod={retentionPeriod!}
						/>
					) : null
				}
				maxDate={
					limitEndDate
						? formatDateWithTimezone(timeZoneId)
								.clone()
								.subtract(1, 'days')
						: undefined
				}
				maxRange={maxRange}
				minDate={
					showRetentionPeriod && retentionPeriod
						? minDate.subtract(retentionPeriod!, 'months')
						: minDate.subtract(100, 'years')
				}
				onSelect={handleDateSelect}
				timeZoneId={timeZoneId}
			/>
		</ClayDropDown>
	);
};

export default DateInput;
