import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountDropdown from 'shared/components/AccountDropdown';
import BasePage from 'shared/components/base-page';
import ClayLink from '@clayui/link';
import DownloadCSVReport from 'shared/components/download-report/DownloadCSVReport';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import ExperienceDropdown from '../components/ExperienceDropdown';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import PropTypes from 'prop-types';
import React, {Suspense, useEffect, useState} from 'react';
import SegmentDropdown from 'shared/components/SegmentDropdown';
import TextTruncate from 'shared/components/TextTruncate';
import {CSVType} from 'shared/components/download-report/utils';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {getSafeDecodedURIComponent, getSafeTouchpoint} from 'shared/util/util';
import {pickBy} from 'lodash';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/url';
import {useAccountFilter} from 'shared/hooks/useAccountFilter';
import {useChannelContext} from 'shared/context/channel';
import {useDataSources} from 'shared/context/dataSources';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {getAPIRangeParams} from 'shared/hooks/useRangeQueryValues';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useSegmentFilter} from 'shared/hooks/useSegmentFilter';
import {config} from 'shared/util/config';

function TouchpointProfile({className, renderContent, router}) {
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
	const [pathRangeSelectors, setPathRangeSelectors] =
		useState(rangeSelectors);
	const {selectedChannel} = useChannelContext();
	const friendlyURL = config.friendlyURL;
	const decodedTitle = getSafeDecodedURIComponent(title);
	const decodedTouchpoint = getSafeDecodedURIComponent(touchpoint);
	const [experienceId, setExperienceId] = useState(experienceIdfromURL);

	const accountDropdown = LDPEnabled && (
		<AccountDropdown
			assetType='page'
			className='mr-3'
			initialAccountId={accountId}
			initialAccountName={accountName}
			onFilterChange={setAccount}
			rangeQueryValues={getAPIRangeParams(pathRangeSelectors)}
		/>
	);

	const segmentDropdown = LDPEnabled && (
		<SegmentDropdown
			className='mr-3'
			initialSegmentId={segmentId}
			initialSegmentName={segmentName}
			onFilterChange={setSegment}
		/>
	);

	useEffect(() => {
		setPathRangeSelectors(rangeSelectors);
	}, [friendlyURL]);

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
					breadcrumbs.getSites(),
					breadcrumbs.getPages(),
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

				<BasePage.Header.NavBar />
			</BasePage.Header>

			{friendlyURL === '/page-overview' && (
				<BasePage.SubHeader>
					{accountDropdown}

					{segmentDropdown}

					{LDPEnabled && (
						<ExperienceDropdown
							groupId={groupId}
							onChange={experienceId => {
								window.location.assign(setUriQueryValues({experienceId}));

								setExperienceId(experienceId);
							}}
						/>
					)}

					<div className='d-flex justify-content-end w-100'>
						<DropdownRangeKey
							legacy={false}
							onRangeSelectorChange={rangeSelectors => {
								window.location.assign(
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

			{friendlyURL === '/page-accounts' && (
				<BasePage.SubHeader>
					{segmentDropdown}
				</BasePage.SubHeader>
			)}

			{friendlyURL === '/page-known-individuals' && (
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
				{friendlyURL === '/page-path' && (
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
						{renderContent({pathRangeSelectors})}
					</Suspense>
				</BasePage.Body>
			</BasePage.Context.Provider>
		</BasePage>
	);
}

TouchpointProfile.propTypes = {
	renderContent: PropTypes.func.isRequired,
	router: PropTypes.object,
	title: PropTypes.string
};

export default TouchpointProfile;
