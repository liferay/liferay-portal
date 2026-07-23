import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountDropdown from 'shared/components/AccountDropdown';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import DownloadCSVReport from 'shared/components/download-report/DownloadCSVReport';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useState} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {CSVType} from 'shared/components/download-report/utils';
import {getMatchedRoute, Routes, setUriQueryValues} from 'shared/util/router';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {pickBy} from 'lodash';
import {Router} from 'shared/types';
import {sub} from 'shared/util/lang';
import {Switch, useHistory} from 'react-router-dom';
import {useChannelContext} from 'shared/context/channel';
import {useDataSources} from 'shared/context/dataSources';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const Accounts = lazy(
	() => import(/* webpackChunkName: "BlogsAccounts" */ './Accounts')
);
const Overview = lazy(
	() => import(/* webpackChunkName: "BlogsOverview" */ './Overview')
);
const KnownIndividuals = lazy(
	() =>
		import(

			/* webpackChunkName: "BlogsKnownIndividuals" */ './KnownIndividuals'
		)
);

const Blog: React.FC<{
	className: string;
	router: Router;
}> = ({className, router}) => {
	const {
		params: {assetId, channelId, groupId, title, touchpoint, type},
		query: {accountId: accountIdFromURL, accountName: accountNameFromURL},
	} = router;

	const LDPEnabled = useLDPEnabled({groupId: groupId!});

	const NAV_ITEMS = [
		{
			exact: true,
			label: Liferay.Language.get('overview'),
			route: Routes.ASSETS_BLOGS_OVERVIEW,
		},
		...(LDPEnabled
			? [
					{
						exact: true,
						label: Liferay.Language.get('visitors'),
						route: Routes.ASSETS_BLOGS_ACCOUNTS,
					},
				]
			: [
					{
						exact: true,
						label: Liferay.Language.get('known-individuals'),
						route: Routes.ASSETS_BLOGS_KNOWN_INDIVIDUALS,
					},
				]),
	];

	const [filters] = useState({});
	const [selectedAccount, setSelectedAccount] = useState<{
		id: string;
		name: string;
	} | null>(
		accountIdFromURL
			? {
					id: accountIdFromURL,
					name: accountNameFromURL || accountIdFromURL,
				}
			: null
	);

	const dataSourceStates = useDataSources();

	const decodedTitle = getSafeDecodedURIComponent(title as string);
	const decodedType = getSafeDecodedURIComponent(type as string);

	const rangeSelectorsFromQuery = useQueryRangeSelectors();

	const {selectedChannel} = useChannelContext();

	const history = useHistory();

	const handleAccountFilterChange = (
		account: {id: string; name: string} | null
	) => {
		history.push(
			setUriQueryValues({
				accountId: account?.id ?? null,
				accountName: account?.name ?? null,
			})
		);

		setSelectedAccount(account);
	};

	return (
		<BasePage
			className={getCN(className)}
			documentTitle={Liferay.Language.get('assets')}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
					breadcrumbs.getAssets({
						channelId: channelId!,
						groupId: groupId!,
					}),
					breadcrumbs.getEntityName({label: decodedTitle}),
				]}
				groupId={groupId!}
			>
				{type && (
					<BasePage.Header.TitleSection
						label
						subtitle={decodedType}
						title={decodedTitle}
					/>
				)}

				<BasePage.Header.NavBar
					items={NAV_ITEMS}
					routeParams={{
						assetId,
						channelId,
						groupId,
						title,
						touchpoint,
						type,
					}}
					routeQueries={pickBy(rangeSelectorsFromQuery)}
				/>
			</BasePage.Header>

			{getMatchedRoute(NAV_ITEMS) === Routes.ASSETS_BLOGS_OVERVIEW && (
				<BasePage.SubHeader>
					{LDPEnabled && (
						<AccountDropdown
							assetType="blog"
							initialAccountId={accountIdFromURL}
							initialAccountName={accountNameFromURL}
							onFilterChange={handleAccountFilterChange}
						/>
					)}

					<div className="d-flex justify-content-end w-100">
						<DownloadPDFReport
							disabled={!!dataSourceStates.empty}
							subtitle={selectedChannel?.name}
							title={
								sub(Liferay.Language.get('x-dashboard'), [
									decodedTitle,
								]) as string
							}
						/>
					</div>
				</BasePage.SubHeader>
			)}

			{getMatchedRoute(NAV_ITEMS) ===
				Routes.ASSETS_BLOGS_KNOWN_INDIVIDUALS && (
				<BasePage.SubHeader>
					<div className="d-flex justify-content-end w-100">
						<DownloadCSVReport
							assetId={assetId}
							assetType="blog"
							disabled={!!dataSourceStates.empty}
							type={CSVType.Individual}
							typeLang={Liferay.Language.get('known-individuals')}
						/>
					</div>
				</BasePage.SubHeader>
			)}

			<BasePage.Context.Provider
				value={{
					accountId: selectedAccount?.id,
					filters,
					router,
				}}
			>
				<BasePage.Body>
					<Suspense fallback={<Loading center />}>
						<Switch>
							<BundleRouter
								data={Overview}
								destructured={false}
								exact
								path={Routes.ASSETS_BLOGS_OVERVIEW}
							/>

							<BundleRouter
								data={KnownIndividuals}
								destructured={false}
								exact
								path={Routes.ASSETS_BLOGS_KNOWN_INDIVIDUALS}
							/>

							<BundleRouter
								data={Accounts}
								destructured={false}
								exact
								path={Routes.ASSETS_BLOGS_ACCOUNTS}
							/>

							<RouteNotFound />
						</Switch>
					</Suspense>
				</BasePage.Body>
			</BasePage.Context.Provider>
		</BasePage>
	);
};

export default Blog;
