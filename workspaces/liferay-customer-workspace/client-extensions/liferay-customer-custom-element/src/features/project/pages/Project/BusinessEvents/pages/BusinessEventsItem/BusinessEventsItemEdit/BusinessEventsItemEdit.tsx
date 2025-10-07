/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput, ClayRadio} from '@clayui/form';

import './BusinessEventsItemEdit.css';

import {Nav, useModal} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import NavigationBar from '@clayui/navigation-bar';
import {FieldArray, Formik} from 'formik';
import {useCallback, useEffect, useMemo, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {Button, DatePicker, Input, Select, TimePicker} from '~/components';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {useAppContext} from '~/features/project/context';
import useGetBusinessEventTypesList from '~/features/project/pages/Project/BusinessEvents/hooks/useGetBusinessEventTypesList';
import useGetLiferayVersions from '~/features/project/pages/Project/BusinessEvents/hooks/useGetLiferayVersions';
import useGetUTCTimeZonesList from '~/features/project/pages/Project/BusinessEvents/hooks/useGetUTCTimeZonesList';
import {Liferay} from '~/services/liferay';
import {updateBusinessEvent} from '~/services/liferay/graphql/queries';
import i18n from '~/utils/I18n';
import getKebabCase from '~/utils/getKebabCase';
import {IBusinessEvent, IOption, ITicket} from '~/utils/types';
import {isValidDate} from '~/utils/validations.form';

import AssociatedTicketsContainer from '../../../components/AssociatedTicketsContainer';
import useAccountsSyncBusinessEvents from '../../../hooks/useAccountsSyncBusinessEvents';
import useAccountsTickets from '../../../hooks/useAccountsTickets';
import useGetBusinessEvent from '../../../hooks/useGetBusinessEvent';
import useHasAllEventsPermissions from '../../../hooks/useHasAllEventsPermissions';
import {containsOption} from '../../../utils/containsOption';
import {getFormattedGoLiveDateTime} from '../../../utils/getFormattedGoLiveDate';
import useIsSaasOnly from '../../../utils/useIsSaasOnly';
import BusinessEventsConfirmationPage from './components/BusinessEventsConfirmationPage';

interface IProps {
	businessEvent: IBusinessEvent;
	errors?: Record<string, any>;
	originalBusinessEvent: IBusinessEvent;
	setFieldValue: (
		field: string,
		value: any,
		shouldValidate?: boolean
	) => void;
	values: any;
}

const NAVIGATION_YEARS_RANGE = 2;

const BusinessEventsItemEditPage: React.FC<IProps> = ({
	businessEvent,
	errors,
	originalBusinessEvent,
	setFieldValue,
	values,
}) => {
	const {client, featureFlags} = useAppPropertiesContext();

	const [{project, subscriptionGroups}] = useAppContext();

	const [baseButtonDisabled, setBaseButtonDisabled] = useState<boolean>(true);
	const [reason, setReason] = useState('');

	const {businessEventTypesList, loading: loadingBusinessEventTypesList} =
		useGetBusinessEventTypesList();

	const emptyOption = useMemo(
		() => ({
			disabled: true,
			label: i18n.translate('select-the-option'),
			value: '',
		}),
		[]
	);

	const {hasAllEventsPermissions} = useHasAllEventsPermissions();

	const [hasImpactingEvents, setHasImpactingEvents] = useState<string>('no');

	const isDescriptionRequired = useMemo(
		() => businessEvent.eventType?.key === 'otherEvent',
		[businessEvent.eventType]
	);

	const [isLoadingSubmitButton, setIsLoadingSubmitButton] =
		useState<boolean>(false);

	const [isModalOpen, setIsModalOpen] = useState(false);

	const isNewLiferayVersionRequired = useMemo(
		() => ['migration', 'upgrade'].includes(businessEvent.eventType?.key!),
		[businessEvent.eventType]
	);

	const {isSaasOnly} = useIsSaasOnly(subscriptionGroups);

	const {loading: loadingTickets, tickets} = useAccountsTickets(
		originalBusinessEvent,
		project?.accountKey || ''
	);

	const {loading: loadingUTCTimeZonesList, utcTimeZonesList} =
		useGetUTCTimeZonesList();

	const navigate = useNavigate();

	const now = new Date();

	const years = {
		end: now.getFullYear() + NAVIGATION_YEARS_RANGE,
		start: now.getFullYear(),
	};

	const {observer, onClose} = useModal({
		onClose: () => setIsModalOpen(false),
	});

	const {updateAccountBusinessEvents} = useAccountsSyncBusinessEvents(
		project?.accountKey || '',
		businessEvent,
		true,
		false
	);

	const {
		dxpMinorVersionsAndPortalMajorVersions,
		loading: loadingLiferayVersions,
	} = useGetLiferayVersions();

	const [newLiferayVersionOptions, setNewLiferayVersionOptions] = useState<
		IOption[]
	>([]);

	const [selectedTicketOptions, setSelectedTicketOptions] = useState<
		ITicket[]
	>([]);

	const [ticketOptions, setTicketOptions] = useState<ITicket[]>([]);

	const handleOptionChange = useCallback(
		(field: string, key: string, list: IOption[]) => {
			if (key) {
				setFieldValue(
					field,
					list.filter((option) => option.value === key)[0].label
				);
			}
		},
		[setFieldValue]
	);

	const handleRadioChange = (value: string) => {
		setHasImpactingEvents(value);
	};

	const handleRemove = useCallback((selectedTicket: ITicket) => {
		setTicketOptions((ticketOptions) => [
			...ticketOptions.map((ticket) => {
				return selectedTicket.ticketId === ticket.ticketId
					? {...ticket, selected: false}
					: {...ticket};
			}),
		]);

		setSelectedTicketOptions((selectedTicketOptions) => [
			...selectedTicketOptions.filter((ticket) => {
				return selectedTicket.ticketId !== ticket.ticketId;
			}),
		]);
	}, []);

	const handleSelect = useCallback((selectedTicket: ITicket) => {
		setTicketOptions((ticketOptions) => [
			...ticketOptions.map((ticket) => {
				return selectedTicket.ticketId === ticket.ticketId
					? {...ticket, selected: true}
					: {...ticket};
			}),
		]);

		setSelectedTicketOptions((selectedTicketOptions) => [
			...selectedTicketOptions,
			selectedTicket,
		]);
	}, []);

	const handleSubmit = async () => {
		const formattedBusinessEvent = {
			associatedTickets: businessEvent.associatedTickets,
			currentLiferayVersion: businessEvent.currentLiferayVersion?.key,
			description: businessEvent.description,
			eventStatus:
				new Date(businessEvent.targetGoLiveDateTime!) >= now
					? 'open'
					: originalBusinessEvent.eventStatus?.key,
			eventType: businessEvent.eventType?.key,
			lastComment: reason,
			name: businessEvent?.name,
			newLiferayVersion: businessEvent.newLiferayVersion?.key,
			r_accountEntryToBusinessEvents_accountEntryId:
				businessEvent.r_accountEntryToBusinessEvents_accountEntryId,
			targetGoLiveDateTime: businessEvent.targetGoLiveDateTime,
			timeZone: businessEvent.timeZone?.key,
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
					businessEventId: businessEvent.id,
				},
			});

			navigate(
				`/${project?.accountKey}/business-events/${businessEvent.id}`
			);

			Liferay.Util.openToast({
				message: i18n.translate('the-changes-were-saved-successfully'),
				type: 'success',
			});
		}
		catch (error) {
			setIsLoadingSubmitButton(false);

			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
			console.error('Error adding business event', error);
		}
	};

	const loading =
		loadingBusinessEventTypesList ||
		loadingLiferayVersions ||
		loadingTickets ||
		loadingUTCTimeZonesList;

	useEffect(() => {
		if (hasImpactingEvents === 'yes') {
			const selectedTickets = selectedTicketOptions.map((ticket) =>
				featureFlags.includes('LRSD-8280')
					? `"${ticket.ticketId}"`
					: ticket.ticketId
			);

			setFieldValue(
				'businessEvent.associatedTickets',
				`[${selectedTickets.length ? selectedTickets.join(', ') : ''}]`
			);
		}
		else {
			setFieldValue('businessEvent.associatedTickets', '[]');
		}
	}, [
		featureFlags,
		hasImpactingEvents,
		selectedTicketOptions,
		setFieldValue,
	]);

	useEffect(() => {
		setFieldValue(
			'businessEvent.targetGoLiveDateTime',
			getFormattedGoLiveDateTime(
				businessEvent.targetGoLiveDate,
				businessEvent.targetGoLiveTime
			)
		);
	}, [
		businessEvent.targetGoLiveDate,
		businessEvent.targetGoLiveTime,
		setFieldValue,
	]);

	useEffect(() => {
		if (!isDescriptionRequired) {
			setFieldValue('businessEvent.description', '');
		}
		else {
			originalBusinessEvent.description
				? setFieldValue(
						'businessEvent.description',
						originalBusinessEvent.description
					)
				: setFieldValue('businessEvent.description', '');
		}
	}, [
		isDescriptionRequired,
		originalBusinessEvent.description,
		setFieldValue,
	]);

	useEffect(() => {
		if (!isNewLiferayVersionRequired) {
			setFieldValue('businessEvent.newLiferayVersion.key', '');

			return;
		}

		if (
			businessEvent.newLiferayVersion?.key &&
			containsOption(
				newLiferayVersionOptions,
				businessEvent.newLiferayVersion
			)
		) {
			return;
		}

		if (
			originalBusinessEvent.newLiferayVersion &&
			containsOption(
				newLiferayVersionOptions,
				originalBusinessEvent.newLiferayVersion
			)
		) {
			setFieldValue(
				'businessEvent.newLiferayVersion.key',
				originalBusinessEvent.newLiferayVersion.key
			);

			setFieldValue(
				'businessEvent.newLiferayVersion.name',
				originalBusinessEvent.newLiferayVersion.name
			);

			return;
		}

		setFieldValue('businessEvent.newLiferayVersion.key', '');
	}, [
		businessEvent.newLiferayVersion,
		isNewLiferayVersionRequired,
		newLiferayVersionOptions,
		originalBusinessEvent.newLiferayVersion,
		setFieldValue,
	]);

	useEffect(() => {
		if (dxpMinorVersionsAndPortalMajorVersions?.length) {
			setNewLiferayVersionOptions([
				...dxpMinorVersionsAndPortalMajorVersions.filter(
					(version, index, versions) => {
						if (businessEvent.currentLiferayVersion?.key) {
							return (
								index <
								versions.findIndex((version) => {
									return (
										version.value ===
										businessEvent.currentLiferayVersion?.key
									);
								})
							);
						}

						return true;
					}
				),
			]);
		}
	}, [
		businessEvent.currentLiferayVersion?.key,
		dxpMinorVersionsAndPortalMajorVersions,
		emptyOption,
	]);

	useEffect(() => {
		if (originalBusinessEvent && tickets) {
			const associatedTickets = JSON.parse(
				originalBusinessEvent.associatedTickets!
			);

			setTicketOptions([
				...tickets
					?.filter((ticket) => {
						return featureFlags.includes('LRSD-8280')
							? true
							: ticket.status !== 'closed' &&
									ticket.status !== 'solved';
					})
					.map((ticket) =>
						associatedTickets.includes(ticket.ticketId)
							? {...ticket, selected: true}
							: {...ticket, selected: false}
					),
			]);

			setSelectedTicketOptions([
				...tickets.filter((ticket) => {
					return associatedTickets.includes(ticket.ticketId);
				}),
			]);

			if (associatedTickets.length) {
				handleRadioChange('yes');
			}
		}
	}, [featureFlags, originalBusinessEvent, tickets]);

	useEffect(() => {
		const hasCurrentLiferayVersion =
			values.businessEvent.currentLiferayVersion.key;

		const hasDescription = values.businessEvent.description;
		const hasError = errors && Object.keys(errors).length;
		const hasEventName = values.businessEvent.name;
		const hasEventType = values.businessEvent.eventType.key;
		const hasNewLiferayVersion = values.businessEvent.newLiferayVersion.key;
		const hasTargetGoLiveDate = values.businessEvent.targetGoLiveDate;

		let hasAllRequiredFieldsFilled =
			Boolean(hasEventName) &&
			Boolean(hasEventType) &&
			Boolean(hasTargetGoLiveDate);

		if (isDescriptionRequired) {
			hasAllRequiredFieldsFilled =
				hasAllRequiredFieldsFilled && hasDescription;
		}

		if (isNewLiferayVersionRequired) {
			hasAllRequiredFieldsFilled =
				hasAllRequiredFieldsFilled && hasNewLiferayVersion;
		}

		if (!isSaasOnly) {
			hasAllRequiredFieldsFilled =
				hasAllRequiredFieldsFilled && hasCurrentLiferayVersion;
		}

		setBaseButtonDisabled(!hasAllRequiredFieldsFilled || Boolean(hasError));
	}, [
		errors,
		isDescriptionRequired,
		isNewLiferayVersionRequired,
		isSaasOnly,
		values.businessEvent.currentLiferayVersion,
		values.businessEvent.description,
		values.businessEvent.eventType,
		values.businessEvent.name,
		values.businessEvent.newLiferayVersion,
		values.businessEvent.targetGoLiveDate,
	]);

	return !loading ? (
		tickets ? (
			hasAllEventsPermissions ? (
				<div className="be-edit-page">
					<div className="be-breadcrumbs font-weight-semi-bold mb-4">
						<span className="mx-2">
							<Link
								to={`/${project?.accountKey}/business-events/`}
							>
								<ClayIcon
									className="mr-1"
									symbol="order-arrow-left"
								/>

								{i18n.translate('back-to-business-events')}
							</Link>
						</span>
					</div>

					<div>
						<div
							className={`align-items-center font-weight-semi-bold be-status be-status-${businessEvent?.eventStatus?.key} mb-1 d-inline px-2 py-1`}
						>
							{i18n.translate(
								getKebabCase(
									businessEvent?.eventStatus?.key as string
								) as string
							)}
						</div>

						<div className="align-items-center d-flex justify-content-between mb-4 mt-2">
							<div className="font-weight-bold text-neutral-10">
								<h3>{businessEvent.name}</h3>
							</div>
							<div>
								<Button
									displayType="secondary"
									onClick={() => {
										navigate(
											`/${project?.accountKey}/business-events/${businessEvent.id}`
										);
									}}
								>
									{i18n.translate('cancel')}
								</Button>

								<Button
									className="ml-3"
									disabled={
										baseButtonDisabled ||
										isLoadingSubmitButton
									}
									displayType="primary"
									isLoading={isLoadingSubmitButton}
									onClick={() => {
										const newTargetGoLiveDateTime =
											getFormattedGoLiveDateTime(
												businessEvent.targetGoLiveDate,
												businessEvent.targetGoLiveTime
											);
										if (
											businessEvent.timeZone?.key !==
												originalBusinessEvent.timeZone
													?.key ||
											newTargetGoLiveDateTime !==
												originalBusinessEvent.targetGoLiveDateTime
										) {
											setIsModalOpen(true);
										}
										else {
											handleSubmit();
										}
									}}
								>
									{i18n.translate('save-changes')}
								</Button>
							</div>
						</div>
					</div>

					{isModalOpen && (
						<BusinessEventsConfirmationPage
							handleSubmit={handleSubmit}
							headerTitle={businessEvent.name!}
							isLoadingSubmitButton={isLoadingSubmitButton}
							message={i18n.translate(
								'we-understand-that-plans-change-please-let-us-know-why-the-target-go-live-date-for-this-event-is-being-updated'
							)}
							observer={observer}
							onClose={onClose}
							reason={reason}
							setReason={setReason}
						/>
					)}

					<div className="mb-4">
						<NavigationBar
							fluidSize={false}
							triggerLabel={i18n.translate('event-details')}
						>
							<Nav.Item>
								<Nav.Link
									active={true}
									aria-label={`Switch to ${i18n.translate(
										'event-details'
									)}`}
									className="be-nav-link text-neutral-10"
								>
									{i18n.translate('event-details')}
								</Nav.Link>
							</Nav.Item>
						</NavigationBar>
					</div>
					<div className="event-edit-container">
						<FieldArray
							name="businessEvent"
							render={() => (
								<>
									<div className="event-edit-field mb-4">
										<Input
											badgeClassName="mt-1 mx-3"
											label={i18n.translate('event-name')}
											name="businessEvent.name"
											required
											type="text"
										/>
									</div>

									<div className="event-edit-field mb-4">
										<Select
											className="mx-3"
											groupStyle="pb-1"
											label={i18n.translate('event-type')}
											link="https://support.liferay.com/w/business-events"
											name="businessEvent.eventType.key"
											onChange={(value: string) =>
												handleOptionChange(
													'businessEvent.eventType.name',
													value,
													businessEventTypesList
												)
											}
											options={businessEventTypesList}
											required
											showPopover
											text="to-learn-more-about-types-of-business-events-please-click-x-here-x"
										/>
									</div>

									{subscriptionGroups && !isSaasOnly && (
										<div className="event-edit-field mb-4">
											<Select
												className="mx-3"
												groupStyle="pb-1"
												label={i18n.translate(
													'your-current-liferay-version'
												)}
												name="businessEvent.currentLiferayVersion.key"
												onChange={(value: string) =>
													handleOptionChange(
														'businessEvent.currentLiferayVersion.name',
														value,
														dxpMinorVersionsAndPortalMajorVersions
													)
												}
												options={[
													emptyOption,
													...dxpMinorVersionsAndPortalMajorVersions,
												]}
												required
											/>
										</div>
									)}

									{isNewLiferayVersionRequired && (
										<div className="event-edit-field mb-4">
											<Select
												badgeClassName="mx-3"
												className="mx-3"
												groupStyle="pb-1"
												label={i18n.translate(
													'new-version'
												)}
												name="businessEvent.newLiferayVersion.key"
												onChange={(value: string) =>
													handleOptionChange(
														'businessEvent.newLiferayVersion.name',
														value,
														newLiferayVersionOptions
													)
												}
												options={[
													emptyOption,
													...newLiferayVersionOptions,
												]}
												required
											/>
										</div>
									)}

									{isDescriptionRequired && (
										<div className="event-edit-field mb-4">
											<Input
												badgeClassName="mx-3"
												component="textarea"
												groupStyle="pb-1"
												label={i18n.translate(
													'event-description'
												)}
												name="businessEvent.description"
												placeholder={i18n.translate(
													'event-description'
												)}
												required
												type="text"
											/>
										</div>
									)}

									<div className="event-edit-field mb-4">
										<ClayInput.Group className="m-0">
											<ClayInput.GroupItem className="m-0">
												<DatePicker
													badgeClassName="mx-3"
													className="mx-3"
													dateFormat="MM-dd-yyyy"
													groupStyle="pb-1"
													label={i18n.translate(
														'target-go-live-date'
													)}
													name="businessEvent.targetGoLiveDate"
													onChange={(
														value: string
													) => {
														setFieldValue(
															'businessEvent.targetGoLiveDate',
															value
														);
													}}
													placeholder={i18n.translate(
														'mm-dd-yyyy'
													)}
													required
													validations={[
														(value) =>
															isValidDate(
																value,
																years
															),
													]}
													years={years}
													yearsCheck
												/>
											</ClayInput.GroupItem>

											<ClayInput.GroupItem className="m-0">
												<Select
													groupStyle="pb-1"
													id="select-businessEvent.timeZone"
													label={i18n.translate(
														'time-zone'
													)}
													name="businessEvent.timeZone.key"
													onChange={(value: string) =>
														handleOptionChange(
															'businessEvent.timeZone.name',
															value,
															utcTimeZonesList
														)
													}
													options={[
														{
															...emptyOption,
															disabled: false,
														},
														...utcTimeZonesList,
													]}
													required
												/>
											</ClayInput.GroupItem>

											<ClayInput.GroupItem className="m-0">
												<TimePicker
													groupStyle="pb-1"
													label={i18n.translate(
														'time'
													)}
													name="businessEvent.targetGoLiveTime"
													onChange={(value) =>
														setFieldValue(
															'businessEvent.targetGoLiveTime',
															value
														)
													}
													required
													showPopover
													text="if-unsure-please-provide-an-estimated-time"
												/>
											</ClayInput.GroupItem>
										</ClayInput.Group>
									</div>

									<div className="event-edit-field mx-3 pb-3">
										<label>
											{i18n.translate(
												'are-there-any-support-tickets-impacting-this-event'
											)}
										</label>

										<div className="ml-1">
											<ClayRadio
												checked={
													hasImpactingEvents === 'no'
												}
												label={i18n.translate('no')}
												onChange={() =>
													handleRadioChange('no')
												}
												value="no"
											/>

											<ClayRadio
												checked={
													hasImpactingEvents === 'yes'
												}
												label={i18n.translate('yes')}
												onChange={() =>
													handleRadioChange('yes')
												}
												value="yes"
											/>
										</div>
									</div>

									{hasImpactingEvents === 'yes' && (
										<div className="event-edit-field mx-3 pb-3">
											{!!ticketOptions.length ||
											!!selectedTicketOptions.length ? (
												<>
													<label>
														{i18n.translate(
															'please-select-the-tickets-that-are-impacting-this-event'
														)}
													</label>

													<div className="mr-3">
														<AssociatedTicketsContainer
															editing
															handleRemove={
																handleRemove
															}
															handleSelect={
																handleSelect
															}
															selectedTickets={
																selectedTicketOptions
															}
															ticketOptions={
																ticketOptions
															}
														/>
													</div>
												</>
											) : (
												<div className="mx-3 pb-3">
													{i18n.translate(
														'there-are-currently-no-open-tickets-under-this-project'
													)}
												</div>
											)}
										</div>
									)}
								</>
							)}
						/>
					</div>
				</div>
			) : (
				<p>
					{i18n.translate(
						'make-sure-the-project-link-is-correct-and-that-you-have-access-to-this-project'
					)}
				</p>
			)
		) : (
			<p
				dangerouslySetInnerHTML={{
					__html: i18n.sub(
						'we-apologize-for-the-inconvenience-but-we-ve-detected-a-system-error-with-this-project',
						[
							'<a href="https://liferay.atlassian.net/servicedesk/customer/portals">',
							'</a>',
						]
					),
				}}
			/>
		)
	) : (
		<div className="w-25">
			<ClayLoadingIndicator size="sm" />
		</div>
	);
};

const BusinessEventsItemEdit: React.FC = () => {
	const {id} = useParams<{id: string}>();

	const {businessEvent, loading} = useGetBusinessEvent(id || '');

	if (loading) {
		return (
			<div className="mx-auto">
				<ClayLoadingIndicator size="sm" />
			</div>
		);
	}

	if (
		['canceled', 'completed'].includes(
			businessEvent?.eventStatus?.key || ''
		)
	) {
		return (
			<div className="h6 mt-4">
				{i18n.translate('cannot-edit-canceled-or-completed-events')}
			</div>
		);
	}

	if (!businessEvent) {
		return <div>{i18n.translate('no-data-found')}</div>;
	}

	const targetGoLiveTime = businessEvent.targetGoLiveDateTime
		?.split('T')[1]
		.substring(0, 5);

	const [year, month, day] = businessEvent
		.targetGoLiveDateTime!.split('T')[0]
		.split('-');

	return (
		<Formik
			initialValues={{
				businessEvent: {
					...businessEvent,
					currentLiferayVersion:
						businessEvent.currentLiferayVersion || {key: ''},
					newLiferayVersion: businessEvent.newLiferayVersion || {
						key: '',
					},
					targetGoLiveDate: `${month}-${day}-${year}`,
					targetGoLiveTime: {
						hours: targetGoLiveTime?.split(':')[0],
						minutes: targetGoLiveTime?.split(':')[1],
					},
					timeZone: businessEvent.timeZone || {key: ''},
				},
			}}
			onSubmit={() => {}}
			validateOnChange
		>
			{(formikProps) => (
				<BusinessEventsItemEditPage
					businessEvent={
						formikProps.values
							.businessEvent as unknown as IBusinessEvent
					}
					errors={formikProps.errors}
					originalBusinessEvent={businessEvent}
					setFieldValue={formikProps.setFieldValue}
					values={formikProps.values}
				/>
			)}
		</Formik>
	);
};

export default BusinessEventsItemEdit;
