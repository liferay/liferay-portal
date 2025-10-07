/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DisplayType as AlertDisplayType} from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import {useOutletContext, useParams} from 'react-router-dom';
import Container from '~/components/Layout/Container';
import ListView from '~/components/ListView';
import ProgressBar from '~/components/ProgressBar';
import i18n from '~/i18n';
import {TestrayBuild, TestrayRoutine} from '~/services/rest';
import {testrayBuildAlertProperties} from '~/util/constants';
import dayjs from '~/util/date';
import {filterStatuses} from '~/util/statuses';

import BuildHistoryChart from './Builds/BuildHistoryChart';
import useBuildActions from './Builds/useBuildActions';

type OutletContext = {
	testrayRoutine: TestrayRoutine;
};

const Routine = () => {
	const {actions, formModal} = useBuildActions();
	const {routineId} = useParams();
	const {testrayRoutine}: OutletContext = useOutletContext();

	const filter = testrayRoutine.r_teamToRoutines_c_teamId
		? `?filter=%7B"testrayTeamIds"%3A%5B${testrayRoutine.r_teamToRoutines_c_teamId}%5D%7D&filterSchema=buildResults`
		: '';

	return (
		<Container>
			<ListView
				forceRefetch={formModal.forceRefetch}
				initialContext={{
					columns: {
						in_progress: false,
						passed: false,
						total: false,
						untested: false,
					},
				}}
				managementToolbarProps={{
					applyFilters: true,
					filterSchema: 'builds',
					title: i18n.translate('build-history'),
				}}
				resource={`/testray-status-metrics/by-testray-routineId/${routineId}/testray-builds-metrics`}
				tableProps={{
					actions,
					columns: [
						{
							key: 'status',
							render: (
								_,
								{
									testrayBuildPromoted,
									testrayBuildTaskStatus,
								}: TestrayBuild
							) => (
								<>
									{testrayBuildPromoted && (
										<span
											title={i18n.translate('promoted')}
										>
											<ClayIcon
												className="mr-3"
												color="darkblue"
												symbol="star"
											/>
										</span>
									)}

									{testrayBuildTaskStatus && (
										<span
											title={
												filterStatuses[
													testrayBuildTaskStatus
												]
											}
										>
											<ClayIcon
												className={classNames(
													'label-chart symbol',
													{
														[testrayBuildTaskStatus.toLowerCase()]:
															testrayBuildTaskStatus,
													}
												)}
												symbol="circle"
											/>
										</span>
									)}
								</>
							),
							value: i18n.translate('build-status'),
						},
						{
							clickable: true,
							key: 'testrayBuildDueDate',
							render: (testrayBuildDueDate) =>
								dayjs(testrayBuildDueDate).format('lll'),
							size: 'sm',
							value: i18n.translate('execution-date'),
						},
						{
							clickable: true,
							key: 'testrayBuildCPUUseTime',
							render: (testrayBuildCPUUseTime) =>
								testrayBuildCPUUseTime === 'null' || ''
									? '-'
									: testrayBuildCPUUseTime,
							value: i18n.translate('cpu-use-time'),
						},
						{
							clickable: true,
							key: 'testrayBuildGitHash',
							render: (testrayBuildGitHash) =>
								testrayBuildGitHash === 'null' || ''
									? '-'
									: testrayBuildGitHash,
							value: i18n.translate('git-hash'),
						},
						{
							clickable: true,
							key: 'testrayBuildProductVersion',
							value: i18n.translate('product-version'),
						},
						{
							key: 'testrayBuildName',
							selectable: true,
							value: i18n.translate('build'),
						},
						{
							key: 'testrayBuildImportStatus',
							render: (
								_,
								{testrayBuildImportStatus}: TestrayBuild
							) => (
								<>
									{testrayBuildImportStatus && (
										<>
											<ClayLabel
												displayType={
													testrayBuildAlertProperties[
														testrayBuildImportStatus
													]
														.displayType as AlertDisplayType
												}
											>
												{
													testrayBuildAlertProperties[
														testrayBuildImportStatus
													].label
												}
											</ClayLabel>
										</>
									)}
								</>
							),
							value: i18n.translate('import-status'),
						},
						{
							clickable: true,
							key: 'failed',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.failed,
							value: i18n.translate('failed'),
						},
						{
							clickable: true,
							key: 'blocked',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.blocked,
							value: i18n.translate('blocked'),
						},
						{
							clickable: true,
							key: 'untested',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.untested,
							value: i18n.translate('untested'),
						},
						{
							clickable: true,
							key: 'in-progress',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.inProgress,
							value: i18n.translate('in-progress'),
						},
						{
							clickable: true,
							key: 'passed',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.passed,
							value: i18n.translate('passed'),
						},
						{
							clickable: true,
							key: 'test-fix',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.testfix,
							value: i18n.translate('test-fix'),
						},
						{
							clickable: true,
							key: 'total',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.total,
							value: i18n.translate('total'),
						},
						{
							clickable: true,
							key: 'testrayStatusMetric',
							render: (testrayStatusMetric) => (
								<ProgressBar
									chartOrder={[
										'passed',
										'failed',
										'blocked',
										'test_fix',
										'incomplete',
									]}
									items={{
										blocked: testrayStatusMetric?.blocked,
										failed: testrayStatusMetric?.failed,
										incomplete:
											testrayStatusMetric?.incomplete +
											testrayStatusMetric?.untested,
										passed: testrayStatusMetric?.passed,
										test_fix: testrayStatusMetric?.testfix,
									}}
								/>
							),
							size: 'xl',
							value: i18n.translate('metrics'),
						},
					],
					navigateTo: ({testrayBuildId}) =>
						`build/${testrayBuildId}${filter}`,
				}}
			>
				{({items, totalCount}) =>
					totalCount > 0 && <BuildHistoryChart builds={items} />
				}
			</ListView>
		</Container>
	);
};

export default Routine;
