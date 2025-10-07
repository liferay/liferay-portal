/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';

import SVFilter from '../../components/SVFilter';
import SVPanel from '../../components/SVPanel';
import SVSearch from '../../components/SVSearch';
import SVTable from '../../components/SVTable';

import './SecurityVulnerabilitiesList.css';

import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar/lib/PaginationBarWithBasicItems';
import {useMemo} from 'react';
import {SVWaves} from '~/assets/SVWaves';
import {FILTER_OPTIONS} from '~/features/security-vulnerabilities/utils/constants/filterOptions';
import {JiraEnum} from '~/features/security-vulnerabilities/utils/constants/jiraEnum';
import {
	paginationDeltas,
	paginationLabels,
} from '~/features/security-vulnerabilities/utils/constants/paginationOptions';
import {SORT_OPTIONS} from '~/features/security-vulnerabilities/utils/constants/sortOptions';
import {getFormattedDate} from '~/utils/getFormattedDate';

import {IRow} from '../../components/SVTable/SVTable';
import SVAffectedVersions from '../../components/SVTable/components/SVAffectedVersions';
import {IJiraIssue} from '../../hooks/useJiraIssue';
import useJiraSearch, {IProps as IJiraSearch} from '../../hooks/useJiraSearch';
import useJiraVersions from '../../hooks/useJiraVersions';

const SecurityVulnerabilitiesList = () => {
	const defaultParams: IJiraSearch = useMemo(
		() => ({
			[JiraEnum.PAGE]: 1,
			[JiraEnum.PAGE_SIZE]: 15,
		}),
		[]
	);

	const {jiraSearch, loading, searchParams, updateSearchParams} =
		useJiraSearch(defaultParams);

	const {jiraVersions} = useJiraVersions();

	const setPage = (page: number) => {
		updateSearchParams({
			[JiraEnum.PAGE]: page,
		});
	};

	const setPageSize = (pageSize: number) => {
		updateSearchParams({
			[JiraEnum.PAGE]: 1,
			[JiraEnum.PAGE_SIZE]: pageSize,
		});
	};

	const columns = [
		{
			className: 'sv-priority-summary-column',
			columnKey: 'prioritySummary',
			label: i18n.translate('priority-summary'),
		},
		{
			columnKey: 'category',
			label: i18n.translate('category'),
		},
		{
			columnKey: 'issueClassification',
			label: i18n.translate('classification'),
		},
		{
			columnKey: 'affectedVersion',
			label: i18n.translate('affected-version'),
		},
		{
			columnKey: 'published',
			label: i18n.translate('published'),
		},
	];

	const rows = useMemo(() => {
		if (jiraSearch?.[JiraEnum.ISSUES]) {
			return jiraSearch?.[JiraEnum.ISSUES].map((issue: IJiraIssue) => ({
				affectedVersion: (
					<div>
						<SVAffectedVersions
							affectedVersions={
								issue[JiraEnum.FIELDS]?.[
									JiraEnum.AFFECTED_VERSIONS
								]
							}
						/>
					</div>
				),
				category: issue[JiraEnum.FIELDS]?.[JiraEnum.CATEGORIES]
					?.map(String)
					.join(', '),
				issueClassification:
					issue[JiraEnum.FIELDS]?.[JiraEnum.ISSUE_CLASSIFICATION],
				link: `/${issue?.[JiraEnum.KEY]}`,
				prioritySummary: (
					<div>
						<div className="align-items-center d-flex">
							<div
								className={`mr-2 px-2 sv-severity sv-severity-${issue[JiraEnum.FIELDS]?.[JiraEnum.SEVERITY]?.toLowerCase()} text-center`}
							>
								{issue[JiraEnum.FIELDS]?.[JiraEnum.SEVERITY]}
							</div>

							<div className="font-weight-bold sv-name sv-wrap-text">
								{issue[JiraEnum.FIELDS]?.[JiraEnum.CVE_IDS]}
							</div>
						</div>

						<div className="sv-summary sv-wrap-text text-neutral-8">
							{issue[JiraEnum.FIELDS]?.[JiraEnum.SUMMARY]}
						</div>
					</div>
				),
				published: getFormattedDate(
					issue[JiraEnum.FIELDS]?.[JiraEnum.PUBLISHED_DATE],
					'day2DMonthSYearN'
				),
			}));
		}
		else {
			return undefined;
		}
	}, [jiraSearch]);

	return (
		<>
			<div className="sv-list">
				<div className="align-items-center d-flex flex-column mt-3 sv-list-header">
					<div className="align-items-center d-flex flex-column justify-content-center my-5 sv-search text-center">
						<h1 className="my-4 text-neutral-0">
							{i18n.translate('liferay-security-reports')}
						</h1>

						<SVSearch
							keywords={searchParams.get(JiraEnum.KEYWORDS) || ''}
							onChange={(keywords) =>
								updateSearchParams({
									[JiraEnum.KEYWORDS]: keywords,
									[JiraEnum.PAGE]: 1,
								})
							}
						/>
					</div>

					<div className="align-items-end d-flex justify-content-end position-absolute sv-gradient">
						<SVWaves />
					</div>
				</div>

				<div className="container-fluid container-fluid-max-xl">
					<div className="row sv-table-content">
						<div className="col-12 col-md-3">
							<SVFilter
								filterOptions={{
									...FILTER_OPTIONS,
									[JiraEnum.AFFECTED_VERSIONS]: jiraVersions,
								}}
								onChange={(params) =>
									updateSearchParams({
										...params,
										[JiraEnum.PAGE]: 1,
									})
								}
								params={searchParams}
								sortOptions={SORT_OPTIONS}
							/>

							<SVPanel
								link="https://www.subscribepage.com/liferay"
								linkText="subscribe-here"
								text="for-the-latest-support-announcements-on-critical-security-vulnerabilities-x"
							/>
						</div>

						<div className="col-12 col-md-9">
							{loading ? (
								<span className="cp-spinner ml-2 spinner-border spinner-border-sm"></span>
							) : rows?.length ? (
								<>
									<SVTable
										columns={columns}
										rows={rows as unknown as IRow[]}
									/>

									<ClayPaginationBarWithBasicItems
										active={jiraSearch?.[JiraEnum.PAGE]}
										activeDelta={
											jiraSearch?.[JiraEnum.PAGE_SIZE]
										}
										deltas={paginationDeltas}
										labels={paginationLabels}
										onActiveChange={(value: number) =>
											setPage(value)
										}
										onDeltaChange={(value: number) =>
											setPageSize(value)
										}
										totalItems={
											jiraSearch?.[JiraEnum.TOTAL]!
										}
									/>
								</>
							) : (
								<div className="py-2">
									{i18n.translate(
										'the-requested-search-does-not-exist-in-our-database-please-try-again-with-different-criteria'
									)}
								</div>
							)}
						</div>
					</div>
				</div>
			</div>
		</>
	);
};

export default SecurityVulnerabilitiesList;
