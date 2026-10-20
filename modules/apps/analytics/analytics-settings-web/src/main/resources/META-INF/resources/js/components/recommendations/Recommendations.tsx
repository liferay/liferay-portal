/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DisplayType} from '@clayui/alert';
import {Body, Cell, Head, Row, Table, Text} from '@clayui/core';
import {ClayToggle} from '@clayui/form';
import ClayLabel from '@clayui/label';
import {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {
	JobStatus,
	TExtendedRecommendationConfiguration,
	TRecommendationConfiguration,
	fetchRecommendationConfiguration,
	updateRecommendationConfiguration,
} from '../../utils/api';
import {REFETCH_JOB_INTERVAL} from '../../utils/constants';
import {useRequest} from '../../utils/useRequest';
import StateRenderer from '../StateRenderer';
import DisableJobModal from './DisableJobModal';
import {Job, jobs as initialJobs} from './jobs';

const header: {
	displayText?: boolean;
	key: string;
	value?: string;
	width?: string;
}[] = [
	{
		key: 'modelName',
		value: Liferay.Language.get('model-name'),
	},
	{
		key: 'description',
		value: Liferay.Language.get('description'),
	},
	{
		key: 'type',
		value: Liferay.Language.get('type'),
		width: '104px',
	},
	{
		key: 'status',
		value: Liferay.Language.get('status'),
		width: '165px',
	},
	{
		displayText: false,
		key: 'toggle',
		value: Liferay.Language.get('toggle[noun]'),
		width: '80px',
	},
];

const statusMap: {
	[key in JobStatus]: {
		label: string;
		value: DisplayType;
	};
} = {
	[JobStatus.Disabled]: {
		label: Liferay.Language.get('disabled'),
		value: 'secondary',
	},
	[JobStatus.Enabled]: {
		label: Liferay.Language.get('enabled'),
		value: 'success',
	},
	[JobStatus.Configuring]: {
		label: Liferay.Language.get('configuring'),
		value: 'info',
	},
};

interface IRecommendationsContentProps {
	jobs: Job[];
	onJobChange: (updatedJobs: Job[]) => void;
}

const RecommendationsContent: React.FC<IRecommendationsContentProps> = ({
	jobs,
	onJobChange,
}) => {
	const [selectedJobIndex, setSelectedJobIndex] = useState<number>(-1);
	const {observer, onOpenChange, open} = useModal();

	const handleChangeJob = async (index: number, message: string) => {
		const updatedJobs = [...jobs];

		updatedJobs[index] = {
			...updatedJobs[index],
			enabled: !updatedJobs[index].enabled,
			status: updatedJobs[index].enabled
				? JobStatus.Disabled
				: JobStatus.Configuring,
		};

		const recomendationConfiguration = updatedJobs.reduce((acc, cur) => {
			return {
				...acc,
				[cur.id]: {
					enabled: cur.enabled,
				},
			};
		}, {}) as TRecommendationConfiguration;

		const {ok} = await updateRecommendationConfiguration(
			recomendationConfiguration
		);

		if (ok) {
			Liferay.Util.openToast({
				message: sub(message, [updatedJobs[index].title]),
			});

			onJobChange(updatedJobs);
		}
	};

	return (
		<>
			<Table className="table-bordered" columnsVisibility={false}>
				<Head>
					{header.map(({displayText = true, key, value, width}) => (
						<Cell key={key} textValue={value} width={width}>
							{displayText ? value : ''}
						</Cell>
					))}
				</Head>

				<Body>
					{jobs.map(
						({description, enabled, id, title, type}, index) => (
							<Row data-testid={id} key={id}>
								<Cell>
									<Text size={3} weight="bold">
										{title}
									</Text>
								</Cell>

								<Cell>
									<Text size={3}>{description}</Text>
								</Cell>

								<Cell>
									<Text size={3}>{type}</Text>
								</Cell>

								<Cell>
									<ClayLabel
										displayType={
											statusMap[jobs[index].status].value
										}
									>
										{statusMap[jobs[index].status].label}
									</ClayLabel>
								</Cell>

								<Cell id="toggle" textValue={title}>
									<ClayToggle
										disabled={
											jobs[index].enabled &&
											jobs[index].status ===
												JobStatus.Configuring
										}
										onToggle={async () => {
											if (jobs[index].enabled) {
												onOpenChange(true);
												setSelectedJobIndex(index);

												return;
											}
											else {
												handleChangeJob(
													index,
													Liferay.Language.get(
														'x-was-updated-successfully'
													)
												);

												return;
											}
										}}
										toggled={enabled}
									/>
								</Cell>
							</Row>
						)
					)}
				</Body>
			</Table>

			{open && (
				<DisableJobModal
					observer={observer}
					onCancel={() => {
						setSelectedJobIndex(-1);
						onOpenChange(false);
					}}
					onDisable={() => {
						handleChangeJob(
							selectedJobIndex,
							Liferay.Language.get('x-was-disabled-successfully')
						);

						setSelectedJobIndex(-1);
						onOpenChange(false);
					}}
					title={jobs?.[selectedJobIndex]?.modalTitle ?? ''}
				/>
			)}
		</>
	);
};

const Recommendations: React.FC = () => {
	const [jobs, setJobs] = useState<Job[]>(initialJobs);
	const [loaded, setLoaded] = useState(false);
	const {data, error, refetch} =
		useRequest<TExtendedRecommendationConfiguration>(
			fetchRecommendationConfiguration
		);

	useEffect(() => {
		let refetchJobs: NodeJS.Timeout | null = null;

		if (data) {
			setJobs(
				initialJobs.map((job: Job) => ({
					...job,
					...data[job.id],
				}))
			);

			refetchJobs = setTimeout(() => {
				const configuring = Object.values(data).some(
					({status}) => status === JobStatus.Configuring
				);

				if (configuring) {
					clearTimeout(refetchJobs as NodeJS.Timeout);

					refetch();
				}
			}, REFETCH_JOB_INTERVAL);

			setLoaded(true);
		}

		return () => clearTimeout(refetchJobs as NodeJS.Timeout);
	}, [data, refetch]);

	return (
		<StateRenderer empty={!data} error={error} loading={!loaded}>
			<StateRenderer.Success>
				<RecommendationsContent
					jobs={jobs}

					// setTimeout is a hack to trigger fetch again at the
					// end of javascript execution to make an updated request.

					onJobChange={(updatedJobs) => {
						setJobs(updatedJobs);

						setTimeout(refetch, REFETCH_JOB_INTERVAL);
					}}
				/>
			</StateRenderer.Success>
		</StateRenderer>
	);
};

export default Recommendations;
