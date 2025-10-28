import BasePage from 'settings/components/BasePage';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import PreferenceMutation from '../queries/PreferenceMutation';
import PreferenceQuery from 'shared/queries/PreferenceQuery';
import React from 'react';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {
	convertMillisecondsToDays,
	convertMillisecondsToMonths
} from 'shared/util/date';
import {
	DATA_RETENTION_PERIOD_KEY,
	FaroEnv,
	ONE_DAY,
	ONE_MONTH,
	SEVEN_MONTHS,
	THIRTEEN_MONTHS,
	TWO_DAYS
} from 'shared/util/constants';
import {get} from 'lodash';
import {Option, Picker} from '@clayui/core';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useMutation, useQuery} from '@apollo/react-hooks';

let RETENTION_OPTIONS = [SEVEN_MONTHS, THIRTEEN_MONTHS];

if (FARO_ENV === FaroEnv.Local || FARO_ENV === FaroEnv.Staging) {
	RETENTION_OPTIONS = [ONE_DAY, TWO_DAYS, SEVEN_MONTHS, THIRTEEN_MONTHS];
}

const getRetentionLabel = (milliseconds: number): string => {
	if (milliseconds < parseInt(ONE_MONTH)) {
		return sub(Liferay.Language.get('x-days'), [
			convertMillisecondsToDays(milliseconds)
		]) as string;
	}

	return sub(Liferay.Language.get('x-months'), [
		convertMillisecondsToMonths(milliseconds)
	]) as string;
};

const fetchDownload = ({fromDate, groupId, toDate, type}) =>
	fetch(
		`/o/proxy/download/${type}/logs?projectGroupId=${groupId}&fromDate=${fromDate}&toDate=${toDate}`,
		{method: 'GET'}
	).then(response => {
		if (response.status === 200) {
			return response.text();
		}

		throw new Error('Request Error');
	});

interface IOverviewProps {
	close: () => void;
	groupId: string;
	open: (modalType: string, options: object) => void;
}

export const Overview: React.FC<IOverviewProps> = ({close, groupId, open}) => {
	const [updatePreference] = useMutation(PreferenceMutation);

	const {data} = useQuery(PreferenceQuery, {
		variables: {key: DATA_RETENTION_PERIOD_KEY}
	});

	const currentUser = useCurrentUser();

	const handleDateRetentionPeriodChange = value => {
		const curVal = parseInt(data.preference.value);
		const newVal = parseInt(value);

		const updateDateRetentionPeriod = () =>
			updatePreference({
				update: (cache, {data}) => {
					cache.writeQuery({
						data,
						query: PreferenceQuery,
						variables: {key: DATA_RETENTION_PERIOD_KEY}
					});
				},
				variables: {
					key: DATA_RETENTION_PERIOD_KEY,
					value
				}
			});

		if (curVal > newVal) {
			open(modalTypes.CONFIRMATION_MODAL, {
				message: (
					<div>
						<p className='text-secondary'>
							{sub(
								Liferay.Language.get(
									'are-you-sure-you-want-to-change-the-retention-period-to-x'
								),
								[getRetentionLabel(newVal).toLowerCase()]
							)}
						</p>

						<div className='h5'>
							{sub(
								Liferay.Language.get(
									'you-will-permanently-lose-analytics-data-that-has-been-recorded-over-x-ago.-you-will-not-be-able-to-undo-this-operation'
								),
								[getRetentionLabel(newVal).toLowerCase()]
							)}
						</div>
					</div>
				),
				modalVariant: 'modal-warning',
				onClose: close,
				onSubmit: updateDateRetentionPeriod,
				submitButtonDisplay: 'warning',
				submitMessage: Liferay.Language.get('change-period'),
				title: Liferay.Language.get('changing-retention-period'),
				titleIcon: 'warning-full'
			});
		} else {
			updateDateRetentionPeriod();
		}
	};

	const handleOpenRequestModal = () =>
		open(modalTypes.EXPORT_LOG_MODAL, {
			description: Liferay.Language.get(
				'select-a-date-range-to-export-your-request-log.-your-download-may-take-a-couple-minutes-to-process'
			),
			fileName: 'request-log.csv',
			groupId,
			onClose: close,
			onSubmit: ({fromDate, toDate}) =>
				fetchDownload({
					fromDate,
					groupId,
					toDate,
					type: 'data-control-tasks'
				}),
			title: Liferay.Language.get('export-request-log')
		});

	const handleOpenSuppressionModal = () =>
		open(modalTypes.EXPORT_LOG_MODAL, {
			description: Liferay.Language.get(
				'select-a-date-range-to-export-your-suppression-list.-your-download-may-take-a-couple-minutes-to-process'
			),
			fileName: 'suppression-list.csv',
			groupId,
			onClose: close,
			onSubmit: ({fromDate, toDate}) =>
				fetchDownload({
					fromDate,
					groupId,
					toDate,
					type: 'suppressions'
				}),
			title: Liferay.Language.get('export-suppression-list')
		});

	return (
		<BasePage
			className='data-privacy-overview-root'
			pageTitle={Liferay.Language.get('data-control-&-privacy')}
		>
			<div className='row'>
				<div className='col-xl-8'>
					<Card>
						<Card.Body>
							<div className='container'>
								<div className='row justify-content-between'>
									<div className='col-lg-8'>
										<div className='h4'>
											{Liferay.Language.get(
												'retention-period'
											)}
										</div>

										<p className='text-secondary'>
											{Liferay.Language.get(
												'analytics-cloud-stores-event-data-and-inactive-anonymous-individuals-for-the-period-specified.-known-profile-data-will-be-stored-indefinitely-unless-it-is-removed-from-the-source-or-requested-to-be-deleted.-contact-sales-to-customize-retention-period'
											)}
										</p>
									</div>

									<div className='col-lg-auto align-self-center'>
										<Picker
											data-testid='data-retention-period-select-input'
											disabled={!currentUser.isAdmin()}
											items={RETENTION_OPTIONS}
											onSelectionChange={
												handleDateRetentionPeriodChange
											}
											selectedKey={get(data, [
												'preference',
												'value'
											])}
										>
											{item => (
												<Option key={item}>
													{getRetentionLabel(
														parseInt(item)
													)}
												</Option>
											)}
										</Picker>
									</div>
								</div>

								<hr />

								<div className='row mt-3 justify-content-between'>
									<div className='col-lg-8'>
										<div className='h4'>
											{Liferay.Language.get(
												'request-log'
											)}
										</div>

										<p className='text-secondary'>
											{Liferay.Language.get(
												'data-subjects-and-your-organization-can-request-access,-deletion,-and-suppression-of-their-data-in-analytics-cloud.-some-requests-may-take-up-to-7-days-to-complete.-we-will-notify-the-requestor-by-email-once-the-download-is-ready'
											)}
										</p>
									</div>

									<div className='col-lg-auto'>
										<ClayLink
											block
											button
											className='button-root mb-2'
											displayType='secondary'
											href={toRoute(
												Routes.SETTINGS_DATA_PRIVACY_REQUEST_LOG,
												{groupId}
											)}
										>
											{Liferay.Language.get('manage')}
										</ClayLink>

										<ClayButton
											block
											className='button-root'
											displayType='secondary'
											onClick={handleOpenRequestModal}
										>
											{Liferay.Language.get('export-log')}
										</ClayButton>
									</div>
								</div>

								<hr />

								<div className='row mt-3 justify-content-between'>
									<div className='col-lg-8'>
										<div className='h4'>
											{Liferay.Language.get(
												'suppressed-users'
											)}
										</div>

										<p className='text-secondary'>
											{Liferay.Language.get(
												'suppressed-data-subjects-will-be-excluded-in-further-identity-resolution-activity.-deleted-data-subjects-will-automatically-be-suppressed-by-their-user-id-and-their-identity-will-not-be-resolvable'
											)}
										</p>
									</div>

									<div className='col-lg-auto'>
										<ClayLink
											block
											button
											className='button-root mb-2'
											displayType='secondary'
											href={toRoute(
												Routes.SETTINGS_DATA_PRIVACY_SUPPRESSED_USERS,
												{groupId}
											)}
										>
											{Liferay.Language.get('manage')}
										</ClayLink>

										<ClayButton
											block
											className='button-root'
											data-testid='export-suppressed-user-button'
											disabled={!currentUser.isAdmin()}
											displayType='secondary'
											onClick={handleOpenSuppressionModal}
										>
											{Liferay.Language.get(
												'export-list'
											)}
										</ClayButton>
									</div>
								</div>
							</div>
						</Card.Body>
					</Card>
				</div>
			</div>
		</BasePage>
	);
};

export default compose<any>(connect(null, {close, open}))(Overview);
