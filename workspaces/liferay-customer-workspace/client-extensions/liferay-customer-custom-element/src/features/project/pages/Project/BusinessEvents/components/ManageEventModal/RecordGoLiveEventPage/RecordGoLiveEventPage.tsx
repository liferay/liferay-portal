/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApolloClient} from '@apollo/client/core/ApolloClient';
import {ClayInput} from '@clayui/form';
import {useEffect, useMemo, useState} from 'react';
import {Badge, Select} from '~/components';
import DatePicker from '~/components/DatePicker/DatePicker';
import TimePicker from '~/components/TimePicker/TimePicker';
import {Liferay} from '~/services/liferay';
import {updateBusinessEvent} from '~/services/liferay/graphql/queries';
import i18n from '~/utils/I18n';
import {IBusinessEvent, IOption} from '~/utils/types';

import './RecordGoLiveEventPage.css';

import {Observer} from '@clayui/modal/lib/types';
import classNames from 'classnames';
import {isValidDate} from '~/utils/validations.form';

import useAccountsSyncBusinessEvents from '../../../hooks/useAccountsSyncBusinessEvents';
import useGetUTCTimeZonesList from '../../../hooks/useGetUTCTimeZonesList';
import {getFormattedGoLiveDateTime} from '../../../utils/getFormattedGoLiveDate';
import BusinessEventsModal from '../../BusinessEventsModal/BusinessEventsModal';

interface IProps {
	accountExternalReferenceCode: string;
	businessEvent: IBusinessEvent;
	client: ApolloClient<any>;
	closeFunction?: (value: boolean) => void;
	errors?: Record<string, any>;
	modalType: string;
	observer: Observer;
	onCompleted: () => void;
	setFieldValue: (
		field: string,
		value: any,
		shouldValidate?: boolean
	) => void;
	touched?: any;
	values?: any;
}

const RecordGoLiveEventPage: React.FC<IProps> = ({
	accountExternalReferenceCode,
	businessEvent,
	client,
	closeFunction = () => {},
	errors,
	modalType,
	observer,
	onCompleted,
	setFieldValue,
	touched,
	values,
}) => {
	const [baseButtonDisabled, setBaseButtonDisabled] = useState<boolean>(true);
	const [isLoadingSubmitButton, setIsLoadingSubmitButton] =
		useState<boolean>(false);
	const [isValidRecordDate, setIsValidRecordDate] = useState<boolean>(false);

	const emptyOption = useMemo(
		() => ({
			disabled: true,
			label: i18n.translate('select-the-option'),
			value: '',
		}),
		[]
	);

	const {updateAccountBusinessEvents} = useAccountsSyncBusinessEvents(
		accountExternalReferenceCode,
		businessEvent,
		false,
		true
	);

	const {utcTimeZonesList} = useGetUTCTimeZonesList();
	const [utcTimeZonesOptions, setUTCTimeZonesOptions] = useState<IOption[]>(
		[]
	);

	const handleInputChange = (event: {target: {value: string}}) => {
		setFieldValue('businessEvent.lastComment', event.target.value);
	};

	useEffect(() => {
		if (utcTimeZonesList?.length) {
			setUTCTimeZonesOptions([
				{...emptyOption, disabled: false},
				...utcTimeZonesList,
			]);
		}
	}, [emptyOption, utcTimeZonesList]);

	useEffect(() => {
		const hasError = errors && Object.keys(errors).length;
		const hasActualGoLiveDate = values.businessEvent?.actualGoLiveDate;
		const hasActualGoLiveTime = values.businessEvent?.actualGoLiveTime;
		const hasTimeZone = values.businessEvent?.timeZone?.key;

		const isValidDate = (date: string) => new Date(date) <= new Date();
		const isActualGoLiveDateValid = isValidDate(hasActualGoLiveDate);

		setIsValidRecordDate(isActualGoLiveDateValid);

		const hasAllRequiredFieldsFilled =
			Boolean(hasActualGoLiveDate) &&
			Boolean(hasActualGoLiveTime) &&
			Boolean(hasTimeZone);

		setBaseButtonDisabled(
			!hasAllRequiredFieldsFilled ||
				Boolean(hasError) ||
				!isActualGoLiveDateValid
		);
	}, [
		errors,
		touched,
		values.businessEvent?.actualGoLiveDate,
		values.businessEvent?.actualGoLiveTime,
		values.businessEvent?.timeZone?.key,
	]);

	const handleSubmit = async () => {
		const businessEventId = businessEvent.id;

		const updatedBusinessEvent = {...values?.businessEvent};
		const formattedBusinessEvent = {
			actualGoLiveDateTime: getFormattedGoLiveDateTime(
				updatedBusinessEvent.actualGoLiveDate,
				updatedBusinessEvent.actualGoLiveTime
			),
			eventStatus: 'completed',
			lastComment: updatedBusinessEvent?.lastComment,
			r_accountEntryToBusinessEvents_accountEntryId:
				businessEvent.r_accountEntryToBusinessEvents_accountEntryId,
			targetGoLiveDateTime: businessEvent.targetGoLiveDateTime,
			timeZone: updatedBusinessEvent.timeZone?.key,
		};

		try {
			setIsLoadingSubmitButton(true);

			await updateAccountBusinessEvents();

			await client.mutate<{
				updateBusinessEvent: IBusinessEvent;
			}>({
				context: {
					displaySuccess: false,
					type: 'liferay-rest',
				},
				mutation: updateBusinessEvent,
				variables: {
					businessEvent: formattedBusinessEvent,
					businessEventId,
				},
			});

			closeFunction(false);

			onCompleted();
		}
		catch (error) {
			setIsLoadingSubmitButton(false);

			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
			console.error('Error canceling business event', error);
		}
	};

	const isEditable = ['open', 'overdue'].includes(
		businessEvent.eventStatus?.key!
	);

	return (
		<BusinessEventsModal
			baseButtonDisabled={baseButtonDisabled}
			handleSubmit={handleSubmit}
			headerTitle={businessEvent.name!}
			isLoadingSubmitButton={isLoadingSubmitButton}
			modalType={modalType}
			observer={observer}
			onClose={() => closeFunction(false)}
			submitButton={i18n.translate('record-actual-go-live')}
			title={i18n.translate('record-actual-go-live')}
		>
			{isEditable ? (
				<div>
					<ClayInput.Group className="business-date-container m-0">
						<ClayInput.GroupItem
							className={classNames('m-0', {
								'be-record-container': !isValidRecordDate,
							})}
						>
							<DatePicker
								badgeClassName="mr-4"
								dateFormat="MM-dd-yyyy"
								groupStyle="pb-1"
								label={i18n.translate('actual-go-live-date')}
								name="businessEvent.actualGoLiveDate"
								onChange={(value) =>
									setFieldValue(
										'businessEvent.actualGoLiveDate',
										value
									)
								}
								placeholder={i18n.translate('mm-dd-yyyy')}
								required
								validations={[isValidDate]}
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem className="m-0">
							<Select
								groupStyle="pb-1"
								id="select-businessEvent.timeZone"
								label={i18n.translate('time-zone')}
								name="businessEvent.timeZone.key"
								options={utcTimeZonesOptions}
								required
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem className="m-0">
							<TimePicker
								groupStyle="pb-1"
								label={i18n.translate('time')}
								name="businessEvent.actualGoLiveTime"
								onChange={(value) =>
									setFieldValue(
										'businessEvent.actualGoLiveTime',
										value
									)
								}
								required
							/>
						</ClayInput.GroupItem>
					</ClayInput.Group>

					<div className="font-weight-bold mb-3">
						{i18n.translate(
							'please-let-us-know-if-you-have-any-feedback-on-the-support-you-received-during-this-time'
						)}
					</div>

					<ClayInput
						component="textarea"
						onChange={handleInputChange}
						required
						type="text"
						value={values?.lastComment}
					/>

					<Badge alertType="info" badgeClassName="mt-3">
						<span className="pl-1 text-paragraph">
							{i18n.translate(
								'entering-an-actual-go-live-date-will-close-this-business-event-no-further-edits-will-be-possible'
							)}
						</span>
					</Badge>

					{values.businessEvent?.actualGoLiveDate! &&
						!isValidRecordDate && (
							<Badge>
								<span className="pl-1">
									{i18n.translate(
										'please-select-an-actual-go-live-date-that-has-already-occurred-or-is-today'
									)}
								</span>
							</Badge>
						)}
				</div>
			) : (
				<div className="h6 my-4">
					{i18n.translate('cannot-edit-canceled-or-completed-events')}
				</div>
			)}
		</BusinessEventsModal>
	);
};

export default RecordGoLiveEventPage;
