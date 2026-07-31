import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountDropdown from 'shared/components/AccountDropdown';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import ClayLink from '@clayui/link';
import DownloadCSVReport from 'shared/components/download-report/DownloadCSVReport';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import ExperienceDropdown from '../components/ExperienceDropdown';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useEffect, useState} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import SegmentDropdown from 'shared/components/SegmentDropdown';
import TextTruncate from 'shared/components/TextTruncate';
import {CSVType} from 'shared/components/download-report/utils';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {getMatchedRoute, Routes} from 'shared/util/router';
import {getSafeDecodedURIComponent, getSafeTouchpoint} from 'shared/util/util';
import {pickBy} from 'lodash';
import {PropTypes} from 'prop-types';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/router';
import {Switch, useHistory} from 'react-router-dom';
import {useAccountFilter} from 'shared/hooks/useAccountFilter';
import {useChannelContext} from 'shared/context/channel';
import {useDataSources} from 'shared/context/dataSources';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useSegmentFilter} from 'shared/hooks/useSegmentFilter';

const Accounts = lazy(() =>
	import(/* webpackChunkName: "TouchpointAccountsPage" */ './Accounts')
);
const KnownIndividuals = lazy(() =>
	import(
		/* webpackChunkName: "TouchpointKnownIndividualsPage" */ './KnownIndividuals'
	)
);
const TouchpointOverviewPage = lazy(() =>
	import(
		/* webpackChunkName: "TouchpointOverviewPage" */ './TouchpointOverviewPage'
	)
);
const TouchpointPathPage = lazy(() =>
	import(/* webpackChunkName: "TouchpointPathPage" */ './PagePath')
);

function TouchpointRoutes({className, router}) {
	const dataSourceStates = useDataSources();
	const rangeSelectors = useQueryRangeSelectors();
	const {
		channelId,
		experienceId: experienceIdfromURL,
		groupId,
		title,
		touchpoint
	} = router.params;
	const {accountId, accountName, setAccount} = useAccountFilter();
	const {segmentId, segmentName, setSegment} = useSegmentFilter();
	const LDPEnabled = useLDPEnabled({groupId});
	const NAV_ITEMS = [
		{
			exact: true,
			label: Liferay.Language.get('overview'),
			route: Routes.SITES_TOUCHPOINTS_OVERVIEW
		},
		{
			exact: true,
			label: Liferay.Language.get('path'),
			route: Routes.SITES_TOUCHPOINTS_PATH
		},
		LDPEnabled && {
			exact: true,
			label: Liferay.Language.get('visitors'),
			route: Routes.SITES_TOUCHPOINTS_ACCOUNTS
		},
		!LDPEnabled && {
			exact: true,
			label: Liferay.Language.get('known-individuals'),
			route: Routes.SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS
		}
	].filter(Boolean);
	const [pathRangeSelectors, setPathRangeSelectors] =
		useState(rangeSelectors);
	const {selectedChannel} = useChannelContext();
	const matchedRoute = getMatchedRoute(NAV_ITEMS);
	const decodedTitle = getSafeDecodedURIComponent(title);
	const decodedTouchpoint = getSafeDecodedURIComponent(touchpoint);
	const [experienceId, setExperienceId] = useState(experienceIdfromURL);
	const history = useHistory();

	const accountDropdown = LDPEnabled && (
		<AccountDropdown
			assetType='page'
			className='mr-2'
			initialAccountId={accountId}
			initialAccountName={accountName}
			onFilterChange={setAccount}
		/>
	);

	const segmentDropdown = LDPEnabled && (
		<SegmentDropdown
			className='mr-2'
			initialSegmentId={segmentId}
			initialSegmentName={segmentName}
			onFilterChange={setSegment}
		/>
	);

	useEffect(() => {
		setPathRangeSelectors(rangeSelectors);
	}, [matchedRoute]);

	return (
		<BasePage
			className={getCN(className)}
			documentTitle={Liferay.Language.get('pages')}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name
					}),
					breadcrumbs.getSites({channelId, groupId}),
					breadcrumbs.getPages({channelId, groupId}),
					breadcrumbs.getEntityName({label: decodedTitle})
				]}
				groupId={groupId}
			>
				<BasePage.Header.TitleSection
					subtitle={
						<TextTruncate title={decodedTouchpoint}>
							<ClayLink href={decodedTouchpoint} target='_blank'>
								{/* It should have double decode for cases when there are special characters */}

								{getSafeDecodedURIComponent(decodedTouchpoint)}
							</ClayLink>
						</TextTruncate>
					}
					title={decodedTitle}
				/>

				<BasePage.Header.NavBar
					items={NAV_ITEMS}
					routeParams={{
						channelId,
						groupId,
						title,
						touchpoint
					}}
					routeQueries={pickBy({
						...rangeSelectors,
						accountId,
						accountName,
						segmentId,
						segmentName
					})}
				/>
			</BasePage.Header>

			{matchedRoute === Routes.SITES_TOUCHPOINTS_OVERVIEW && (
				<BasePage.SubHeader>
					{accountDropdown}

					{segmentDropdown}

					{LDPEnabled && (
						<ExperienceDropdown
							groupId={groupId}
							onChange={experienceId => {
								history.push(setUriQueryValues({experienceId}));

								setExperienceId(experienceId);
							}}
						/>
					)}

					<div className='d-flex justify-content-end w-100'>
						<DropdownRangeKey
							legacy={false}
							onRangeSelectorChange={rangeSelectors => {
								history.push(
									setUriQueryValues(
										pickBy({
											...rangeSelectors
										}),
										removeUriQueryParam(
											window.location.href,
											'rangeEnd',
											'rangeStart'
										)
									)
								);

								setPathRangeSelectors(rangeSelectors);
							}}
							rangeSelectors={pathRangeSelectors}
						/>

						<DownloadPDFReport
							disabled={dataSourceStates.empty}
							subtitle={`${
								selectedChannel.name
							} | ${Liferay.Language.get('page-dashboard')}`}
							title={decodedTitle}
							url={decodedTouchpoint}
						/>
					</div>
				</BasePage.SubHeader>
			)}

			{matchedRoute === Routes.SITES_TOUCHPOINTS_ACCOUNTS && (
				<BasePage.SubHeader>
					{segmentDropdown}
				</BasePage.SubHeader>
			)}

			{matchedRoute === Routes.SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS && (
				<BasePage.SubHeader>
					<div className='d-flex justify-content-end w-100'>
						<DownloadCSVReport
							assetId={getSafeTouchpoint(touchpoint)}
							assetType='page'
							disabled={dataSourceStates.empty}
							type={CSVType.Individual}
							typeLang={Liferay.Language.get('known-individuals')}
						/>
					</div>
				</BasePage.SubHeader>
			)}

			<BasePage.Context.Provider
				value={{
					accountId,
					experienceId,
					filters: {},
					rangeSelectors: pathRangeSelectors,
					router,
					segmentId,
					segmentName
				}}
			>
				{matchedRoute === Routes.SITES_TOUCHPOINTS_PATH && (
					<BasePage.SubHeader>
						{accountDropdown}

						{segmentDropdown}

						<div className='d-flex justify-content-end w-100'>
							<DropdownRangeKey
								legacy={false}
								onRangeSelectorChange={setPathRangeSelectors}
								rangeSelectors={pathRangeSelectors}
							/>
						</div>
					</BasePage.SubHeader>
				)}

				<BasePage.Body>
					<Suspense fallback={<Loading />}>
						<Switch>
							<BundleRouter
								data={TouchpointOverviewPage}
								destructured={false}
								exact
								path={Routes.SITES_TOUCHPOINTS_OVERVIEW}
							/>

							<BundleRouter
								data={KnownIndividuals}
								destructured={false}
								exact
								path={
									Routes.SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS
								}
							/>

							<BundleRouter
								componentProps={{
									rangeSelectors: pathRangeSelectors
								}}
								data={TouchpointPathPage}
								destructured={false}
								exact
								path={Routes.SITES_TOUCHPOINTS_PATH}
							/>

							<BundleRouter
								data={Accounts}
								destructured={false}
								exact
								path={Routes.SITES_TOUCHPOINTS_ACCOUNTS}
							/>

							<RouteNotFound />
						</Switch>
					</Suspense>
				</BasePage.Body>
			</BasePage.Context.Provider>
		</BasePage>
	);
}

TouchpointRoutes.propTypes = {
	/**
	 * @type {object}
	 * @default undefined
	 */
	router: PropTypes.object,

	/**
	 * @type {string}
	 * @default undefined
	 */
	title: PropTypes.string
};

export default TouchpointRoutes;
