/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayChart from '@clayui/charts';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import classNames from 'classnames';
import {useEffect, useMemo, useRef, useState} from 'react';
import Loading from '~/components/Loading';
import {useCaseResultsChart} from '~/hooks/useCaseResultsChart';
import {safeJSONParse} from '~/util';

import JiraLink from '../../../../components/JiraLink';
import Container from '../../../../components/Layout/Container';
import QATable from '../../../../components/Table/QATable';
import {useTotalTestCasesByTestrayBuild} from '../../../../hooks/data/useCaseResultGroupBy';
import useIssuesFound from '../../../../hooks/data/useIssuesFound';
import i18n from '../../../../i18n';
import {TestrayBuild, TestrayTask} from '../../../../services/rest';
import {formatUTCDate} from '../../../../util/date';
import {getDonutLegend} from '../../../../util/graph';
import BuildAlertBar from './BuildAlertBar';

type BuildOverviewProps = {
	testrayBuild: TestrayBuild;
	testrayTask?: TestrayTask;
};

const BuildOverview: React.FC<BuildOverviewProps> = ({testrayBuild}) => {
	const totalTestCasesGroup = useTotalTestCasesByTestrayBuild(testrayBuild);
	const {chart, entity, loading} = useCaseResultsChart({
		buildId: testrayBuild.id,
	});

	const issues = useIssuesFound({buildId: testrayBuild.id});

	const playwrightReports = useMemo(
		() => safeJSONParse(testrayBuild.playwrightReports, []) as Object,
		[testrayBuild.playwrightReports]
	);

	const ref = useRef<any>();

	const [columnChartLoad, setColumnChartLoad] = useState(false);

	const [testrayTask] = testrayBuild?.tasks as TestrayTask[];

	useEffect(() => {
		setColumnChartLoad(false);
		setTimeout(() => {
			setColumnChartLoad(true);
		}, 100);
	}, [entity]);

	return (
		<>
			<BuildAlertBar
				testrayBuild={testrayBuild}
				testrayTask={testrayTask}
			/>

			<Container collapsable title={i18n.translate('details')}>
				<QATable
					items={[
						{
							title: i18n.translate('product-version'),
							value: testrayBuild.productVersion?.name,
						},
						{
							title: i18n.translate('description'),

							value: (
								<div
									dangerouslySetInnerHTML={{
										__html: testrayBuild?.description,
									}}
								/>
							),
						},
						{
							title: i18n.translate('git-hash'),
							value:
								testrayBuild?.gitHash === 'null' || ''
									? '-'
									: testrayBuild?.gitHash,
						},
						{
							title: i18n.translate('cpu-use-time'),
							value:
								testrayBuild?.cpuUseTime === 'null' || ''
									? '-'
									: testrayBuild?.cpuUseTime,
						},
						{
							title: i18n.translate('execution-date'),
							value: formatUTCDate(testrayBuild.dueDate),
						},
						{
							title: i18n.translate('created-by'),
							value: testrayBuild.creator.name,
						},
						{
							title: i18n.translate('all-issues-found'),
							value: <JiraLink issue={issues} />,
						},
					]}
				/>

				<>
					<ClayPanel
						collapsable
						displayTitle={
							<div className="tr-small-heading">
								{i18n.translate('playwright-reports')}
							</div>
						}
						displayType="default"
						showCollapseIcon
					>
						<ClayPanel.Body>
							<div className="d-flex flex-wrap mb-1">
								{Object.entries(playwrightReports)
									.sort(([_url1, name1], [_url2, name2]) =>
										name1.localeCompare(name2)
									)
									.map(([url, name], index) => (
										<a
											className="case-results-attachments-box mr-2 mt-2"
											href={url}
											key={index}
											rel="noopener noreferrer"
											target="_blank"
										>
											{name.substring(
												0,
												name.lastIndexOf('/')
											)}

											<ClayIcon
												className="ml-1"
												fontSize={12}
												symbol="shortcut"
											/>
										</a>
									))}
							</div>
						</ClayPanel.Body>
					</ClayPanel>
				</>

				<div className="d-flex mt-4">
					<dl>
						<dd>{i18n.sub('x-minutes', '0')}</dd>

						<dd className="tr-small-heading">
							{i18n.translate('total-estimated-time')}
						</dd>
					</dl>

					<dl className="ml-3">
						<dd>{i18n.sub('x-minutes', '0')}</dd>

						<dd className="tr-small-heading">
							{i18n.translate('total-estimated-time')}
						</dd>
					</dl>

					<dl className="ml-3">
						<dd>{issues?.length}</dd>

						<dd className="tr-small-heading">
							{i18n.translate('total-issues')}
						</dd>
					</dl>
				</div>
			</Container>

			<Container
				className="mt-4"
				collapsable
				title={i18n.translate('total-test-cases')}
			>
				<div className="d-flex justify-content-between row">
					<div
						className={classNames('align-items-center d-flex', {
							'col': !entity,
							'col-4': entity,
						})}
					>
						{totalTestCasesGroup.ready && (
							<div className="col-8">
								<ClayChart
									data={{
										colors: totalTestCasesGroup.colors,
										columns:
											totalTestCasesGroup.donut.columns,
										type: 'donut',
									}}
									donut={{
										expand: false,
										label: {
											show: false,
										},
										legend: {
											show: false,
										},
										title: totalTestCasesGroup.donut.total.toString(),
										width: 15,
									}}
									legend={{show: false}}
									onafterinit={() => {
										getDonutLegend(ref.current, {
											data: totalTestCasesGroup.donut.columns.map(
												([name]) => name
											),
											elementId:
												'testrayTotalMetricsGraphLegend',
											total: totalTestCasesGroup.donut
												.total as number,
										});
									}}
									ref={ref}
									size={{
										height: 200,
									}}
								/>
							</div>
						)}

						<div className="col-">
							<div id="testrayTotalMetricsGraphLegend" />
						</div>
					</div>

					{entity && (
						<div className="col-8">
							{loading ||
								(!columnChartLoad && (
									<Loading className="py-10" />
								))}

							{columnChartLoad && !loading && (
								<ClayChart
									axis={{
										x: {
											categories:
												!!chart.testrayRunNumber
													.length &&
												chart.testrayRunNumber,
											label: {
												position: 'outer-center',
												text: i18n
													.translate(`${entity}`)
													.toUpperCase(),
											},
											tick: {
												show: chart.testrayRunNumber
													.length
													? true
													: false,
												text: {
													show: chart.testrayRunNumber
														.length
														? true
														: false,
												},
											},
											type: 'category',
										},
										y: {
											label: {
												position: 'outer-middle',
												text: i18n
													.translate('tests')
													.toUpperCase(),
											},
										},
									}}
									bar={{
										width: {
											max: 30,
										},
									}}
									data={{
										colors: chart.colors,
										columns: chart.columns,
										groups: [chart.statuses],
										type: 'bar',
									}}
									legend={{
										inset: {
											anchor: 'top-right',
											step: 1,
											x: 10,
											y: -20,
										},
										position: 'inset',
									}}
									padding={{
										bottom: 5,
										top: 20,
									}}
									tooltip={{
										format: {
											title: (index: number) =>
												chart.columnNames[index],
										},
										order: '',
									}}
								/>
							)}
						</div>
					)}
				</div>
			</Container>
		</>
	);
};

export default BuildOverview;
