/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {Form, Formik, FormikValues} from 'formik';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import DataSelection from '../../components/DataSelection';
import Footer from '../../components/Footer';
import {PageTreeModalConfiguration} from '../../components/PageTreeModal';
import Setup from '../../components/Setup';
import {DateFilterValues, Range} from '../../components/date_filter';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {usePreview} from '../../hooks/usePreview';
import {deleteScheduledPublishProcess} from '../../services/deleteScheduledPublishProcess';
import {getScheduledPublishProcess} from '../../services/getScheduledPublishProcess';
import {postPublishProcess} from '../../services/postPublishProcess';
import {postScheduledPublishProcess} from '../../services/postScheduledPublishProcess';
import {PublishProcessRequest} from '../../types/exportImportProcess';
import {
	getSelectedDeletionCount,
	getSelectedItemsCount,
	toPortletDataHandlerSections,
	toProcessRequestFlags,
	withSelectedLayoutSetCount,
} from '../../utils/contentSelection';
import {toWallClockDateTime, toZonedDate} from '../../utils/dateTime';
import {getProcessFormErrors} from '../../utils/getProcessFormErrors';
import {toContentSelection} from '../../utils/toContentSelection';
import {toDateFilterValues} from '../../utils/toDateFilterValues';
import {toPortletDataHandlers} from '../../utils/toPortletDataHandlers';
import {FormikFieldPublishScheduler} from './components/scheduler/FormikFieldPublishScheduler';
import {
	fromCronExpression,
	toCronExpression,
} from './components/scheduler/cron';
import {ScheduleValues, TimeZoneOption} from './components/scheduler/types';
import {
	getInitialScheduleValues,
	getScheduleValuesErrors,
	hasEndDate,
} from './components/scheduler/utils';

type PublishFormValues = {
	contentSelection: ContentSelection | null | undefined;
	dateFilterValues: DateFilterValues;
	deletions: boolean;
	name: string;
	permissions: boolean;
	scheduleValues: ScheduleValues;
};

export function NewPublish({
	backURL,
	commentsAndRatingsEnabled = false,
	defaultScheduled = false,
	lastPublishDate,
	pageTreeModalConfiguration,
	processesBackURL,
	publishPreviewAPIURL,
	publishProcessAPIURL,
	scheduledBackURL,
	scheduledPublishProcessAPIURL,
	scheduledPublishProcessId,
	timeZoneId,
	timeZones,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	defaultScheduled?: boolean;
	lastPublishDate?: string;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	processesBackURL: string;
	publishPreviewAPIURL: string;
	publishProcessAPIURL: string;
	scheduledBackURL: string;
	scheduledPublishProcessAPIURL: string;
	scheduledPublishProcessId?: number;
	timeZoneId: string;
	timeZones: TimeZoneOption[];
}) {
	const {
		appliedDateFilterRef,
		error,
		handleApplyFilter,
		loading,
		preview,
		setError,
	} = usePreview(publishPreviewAPIURL, timeZoneId);
	const [initialFormValues, setInitialFormValues] =
		useState<PublishFormValues>(() => ({
			contentSelection: undefined,
			dateFilterValues: {range: Range.All},
			deletions: false,
			name: '',
			permissions: false,
			scheduleValues: getInitialScheduleValues(
				timeZoneId,
				defaultScheduled
			),
		}));
	const editedScheduledPublishProcessId = scheduledPublishProcessId ?? 0;

	const [seeding, setSeeding] = useState(!!editedScheduledPublishProcessId);
	const seededRef = useRef(false);

	useEffect(() => {
		if (!editedScheduledPublishProcessId || !preview || seededRef.current) {
			return;
		}

		seededRef.current = true;

		getScheduledPublishProcess({
			url: `${scheduledPublishProcessAPIURL}/${editedScheduledPublishProcessId}`,
		}).then((scheduledPublishProcessResponse) => {
			if (scheduledPublishProcessResponse.error !== null) {
				setError(scheduledPublishProcessResponse.error);

				return;
			}

			const scheduledPublishProcess =
				scheduledPublishProcessResponse.data;

			const scheduledPublishProcessRequest =
				scheduledPublishProcess.scheduledPublishProcessRequest;

			const publishProcessRequest =
				scheduledPublishProcessRequest?.publishProcessRequest ?? {
					name: '',
				};

			const dateFilterValues = toDateFilterValues(
				publishProcessRequest.dateFilter,
				timeZoneId
			);

			handleApplyFilter(dateFilterValues);

			const scheduledTimeZoneId =
				scheduledPublishProcessRequest?.timeZoneId ?? timeZoneId;

			const scheduleStartDate = scheduledPublishProcess.scheduleStartDate;

			const scheduleStartDateTime = scheduleStartDate
				? toWallClockDateTime(scheduleStartDate, scheduledTimeZoneId)
				: '';

			setInitialFormValues((currentInitialFormValues) => ({
				contentSelection: toContentSelection(
					toPortletDataHandlerSections(
						preview.portletDataHandlers ?? []
					),
					publishProcessRequest,
					{commentsAndRatingsEnabled}
				),
				dateFilterValues,
				deletions: !!publishProcessRequest.deletions,
				name: scheduledPublishProcess.name ?? '',
				permissions: !!publishProcessRequest.permissions,
				scheduleValues: {
					...currentInitialFormValues.scheduleValues,
					enabled: true,
					endDateTime: scheduledPublishProcess.scheduleEndDate
						? toWallClockDateTime(
								scheduledPublishProcess.scheduleEndDate,
								scheduledTimeZoneId
							)
						: '',
					neverEnd: !scheduledPublishProcess.scheduleEndDate,
					startDateTime: scheduleStartDateTime,
					timeZoneId: scheduledTimeZoneId,
					...(scheduledPublishProcessRequest?.cronExpression
						? fromCronExpression(
								scheduledPublishProcessRequest.cronExpression,
								scheduleStartDateTime
							)
						: {}),
				},
			}));

			setSeeding(false);
		});
	}, [
		commentsAndRatingsEnabled,
		editedScheduledPublishProcessId,
		handleApplyFilter,
		preview,
		scheduledPublishProcessAPIURL,
		setError,
		timeZoneId,
	]);

	if (error) {
		return <ClayAlert displayType="danger">{error}</ClayAlert>;
	}

	if (seeding) {
		return (
			<div className="sheet">
				<span
					aria-hidden="true"
					className="loading-animation mb-9 mt-8"
				></span>
			</div>
		);
	}

	const portletDataHandlerSections = toPortletDataHandlerSections(
		preview?.portletDataHandlers ?? []
	);

	return (
		<Formik
			initialValues={initialFormValues}
			onSubmit={async (values) => {
				const scheduleValues = values.scheduleValues;

				const scheduled = scheduleValues.enabled;

				const publishProcessRequest: PublishProcessRequest = {
					...toProcessRequestFlags(values.contentSelection),
					dateFilter: appliedDateFilterRef.current,
					deletions: values.deletions,
					name: values.name,
					permissions: values.permissions,
					portletDataHandlers: toPortletDataHandlers(
						portletDataHandlerSections,
						values.contentSelection
					),
				};

				const result = scheduled
					? await postScheduledPublishProcess({
							scheduledPublishProcessRequest: {
								cronExpression:
									toCronExpression(scheduleValues),
								publishProcessRequest,
								scheduleEndDate: hasEndDate(scheduleValues)
									? toZonedDate(
											scheduleValues.endDateTime,
											scheduleValues.timeZoneId
										).toISOString()
									: undefined,
								scheduleStartDate: toZonedDate(
									scheduleValues.startDateTime,
									scheduleValues.timeZoneId
								).toISOString(),
								timeZoneId: scheduleValues.timeZoneId,
							},
							url: scheduledPublishProcessAPIURL,
						})
					: await postPublishProcess({
							publishProcessRequest,
							url: publishProcessAPIURL,
						});

				if (result.error) {
					Liferay.Util.openToast({
						message: result.error,
						type: 'danger',
					});

					return;
				}

				if (editedScheduledPublishProcessId) {
					const deleteResult = await deleteScheduledPublishProcess({
						url: `${scheduledPublishProcessAPIURL}/${editedScheduledPublishProcessId}`,
					});

					if (deleteResult.error && deleteResult.status !== '404') {
						Liferay.Util.openToast({
							message: deleteResult.error,
							type: 'danger',
						});
					}
				}

				Liferay.Util.navigate(
					scheduled ? scheduledBackURL : processesBackURL
				);
			}}
			validate={(values: FormikValues) => {
				const errors = getProcessFormErrors(values);

				const {
					cronExpression,
					endDateTime,
					repeatOnTime,
					startDateTime,
				} = getScheduleValuesErrors(values.scheduleValues);

				const scheduleValuesError =
					startDateTime ??
					cronExpression ??
					repeatOnTime ??
					endDateTime;

				if (scheduleValuesError) {
					errors.scheduleValues = scheduleValuesError;
				}

				return errors;
			}}
			validateOnMount
		>
			{(formik) => {
				const contentSelection = formik.values.contentSelection;

				return (
					<Form noValidate>
						<Setup
							placeholder={Liferay.Language.get(
								'process-name-placeholder'
							)}
							title={sub(
								Liferay.Language.get('x-details'),
								Liferay.Language.get('publish')
							)}
						/>

						<FormikFieldPublishScheduler
							name="scheduleValues"
							timeZones={timeZones}
						/>

						<DataSelection
							commentsAndRatingsEnabled={
								commentsAndRatingsEnabled
							}
							deletionCount={getSelectedDeletionCount(
								preview?.deletionCount,
								portletDataHandlerSections,
								contentSelection
							)}
							deletionsDescription={Liferay.Language.get(
								'deletions-help'
							)}
							deletionsLabel={Liferay.Language.get(
								'replicate-individual-deletions'
							)}
							itemsCount={getSelectedItemsCount(
								preview?.additionCount,
								portletDataHandlerSections,
								contentSelection
							)}
							lastPublishDate={lastPublishDate}
							loading={loading}
							onApplyFilter={handleApplyFilter}
							pageTreeModalConfiguration={{
								...pageTreeModalConfiguration,
								title: Liferay.Language.get('pages-to-publish'),
							}}
							permissionsDescription={Liferay.Language.get(
								'publish-permissions-help'
							)}
							permissionsLabel={Liferay.Language.get(
								'publish-permissions'
							)}
							portletDataHandlerSections={withSelectedLayoutSetCount(
								portletDataHandlerSections,
								contentSelection
							)}
							process="publish"
							subtitle={Liferay.Language.get(
								'select-and-filter-the-data-you-want-to-publish'
							)}
							timeZoneId={timeZoneId}
						/>

						<Footer
							actionButton={
								<ClayButton
									disabled={
										formik.isSubmitting || !formik.isValid
									}
									type="submit"
								>
									<span className="inline-item inline-item-before">
										<ClayIcon
											className="mr-1"
											symbol="change"
										/>
									</span>

									{formik.values.scheduleValues.enabled
										? Liferay.Language.get(
												'schedule-publication-to-live'
											)
										: Liferay.Language.get(
												'publish-to-live'
											)}
								</ClayButton>
							}
							backURL={backURL}
						/>
					</Form>
				);
			}}
		</Formik>
	);
}
