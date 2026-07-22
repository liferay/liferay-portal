import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import DownloadCSVReport from 'shared/components/download-report/DownloadCSVReport';
import ErrorPage from 'shared/pages/ErrorPage';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useContext} from 'react';
import {buildHeaderSubtitle, IndividualHeaderData} from './utils/utils';
import {ChannelContext} from 'shared/context/channel';
import {compose, withIndividual} from 'shared/hoc';
import {CSVType} from 'shared/components/download-report/utils';
import {getMatchedRoute, Routes} from 'shared/util/router';
import {Route, Routes as RouterRoutes} from 'react-router-dom';
import {useDataSources} from 'shared/context/dataSources';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useRequest} from 'shared/hooks/useRequest';

const AssociatedSegments = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualAssociatedSegments" */ './AssociatedSegments'
		)
);

const InterestDetails = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualInterestDetails" */ './InterestDetails'
		)
);
const Interests = lazy(
	() => import(/* webpackChunkName: "IndividualInterests" */ './Interests')
);

const OverviewCDP = lazy(
	() => import(/* webpackChunkName: "IndividualOverview" */ './OverviewCDP')
);

const IndividualProfileCDP = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualProfileCDP" */ './IndividualProfileCDP'
		)
);

const NAV_ITEMS_CDP = [
	{
		exact: true,
		label: Liferay.Language.get('activities'),
		route: Routes.CONTACTS_INDIVIDUAL,
	},
	{
		exact: true,
		label: Liferay.Language.get('profile'),
		route: Routes.CONTACTS_INDIVIDUAL_DETAILS,
	},
	{
		exact: false,
		label: Liferay.Language.get('interests'),
		route: Routes.CONTACTS_INDIVIDUAL_INTERESTS,
	},
	{
		exact: true,
		label: Liferay.Language.get('segments'),
		route: Routes.CONTACTS_INDIVIDUAL_SEGMENTS,
	},
];

interface IIndividualProfileRoutesCDPProps {
	channelId: string;
	className?: string;
	groupId: string;
	id: string;
	individual: {
		id: string;
		name?: string;
		toJS: () => IndividualHeaderData;
	};
}

export const IndividualProfileRoutesCDP = ({
	channelId,
	className,
	groupId,
	id,
	individual,
}: IIndividualProfileRoutesCDPProps) => {
	const dataSourceStates = useDataSources();

	const LDPEnabled = useLDPEnabled({groupId});

	const {selectedChannel} = useContext(ChannelContext);

	const matchedRoute = getMatchedRoute(NAV_ITEMS_CDP);

	const componentProps = {individual};

	const entityName = individual.name || Liferay.Language.get('unknown');

	const {data: dataSourceData} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta: 1,
			groupId,
		},
	});

	return (
		<BasePage
			className={
				matchedRoute === Routes.CONTACTS_INDIVIDUAL
					? getCN('overview-root', className)
					: className
			}
			documentTitle={`${entityName} - ${Liferay.Language.get(
				'individuals'
			)}`}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name,
					}),
					breadcrumbs.getIndividuals({
						channelId,
						groupId,
						LDPEnabled,
					}),
					breadcrumbs.getEntityName({label: entityName}),
				]}
				groupId={groupId}
			>
				<BasePage.Header.TitleSection
					subtitle={buildHeaderSubtitle(individual.toJS(), {
						channelId,
						groupId,
					})}
					title={entityName}
				/>

				<BasePage.Header.NavBar
					items={NAV_ITEMS_CDP}
					routeParams={{channelId, groupId, id}}
				/>
			</BasePage.Header>

			{getMatchedRoute(NAV_ITEMS_CDP) === Routes.CONTACTS_INDIVIDUAL &&
				dataSourceData?.total > 0 && (
					<BasePage.SubHeader>
						<div className="d-flex justify-content-end w-100">
							<DownloadCSVReport
								disabled={!!dataSourceStates.empty}
								individualId={individual.id}
								type={CSVType.Event}
								typeLang={Liferay.Language.get('events')}
							/>
						</div>
					</BasePage.SubHeader>
				)}

			<BasePage.Body>
				<Suspense fallback={<Loading />}>
					<RouterRoutes>
						<Route
							element={
								<BundleRouter
									componentProps={componentProps}
									data={AssociatedSegments}
								/>
							}
							path="segments"
						/>

						<Route
							element={
								<BundleRouter
									componentProps={componentProps}
									data={IndividualProfileCDP}
								/>
							}
							path="details"
						/>

						<Route
							element={
								<BundleRouter
									componentProps={componentProps}
									data={InterestDetails}
								/>
							}
							path="interests/:interestId"
						/>

						<Route
							element={
								<BundleRouter
									componentProps={componentProps}
									data={Interests}
								/>
							}
							path="interests"
						/>

						<Route
							element={
								<BundleRouter
									componentProps={componentProps}
									data={OverviewCDP}
								/>
							}
							index
						/>

						<Route element={<ErrorPage />} path="*" />
					</RouterRoutes>
				</Suspense>
			</BasePage.Body>
		</BasePage>
	);
};

export default compose(withIndividual)(IndividualProfileRoutesCDP);
