import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import CardTabs from 'shared/components/CardTabs';
import ClayLink from '@clayui/link';
import OperatingSystem from 'shared/components/OperatingSystem';
import React, {useCallback, useState} from 'react';
import WebBrowser from 'shared/components/WebBrowser';
import {compose} from 'redux';
import {HOC_CARD_PROPTYPES} from 'shared/util/proptypes';
import {PropTypes} from 'prop-types';
import {withEmpty, withError, withLoading} from 'shared/hoc';

const OPERATING_SYSTEM = Liferay.Language.get('devices');
const WEB_BROWSER = Liferay.Language.get('browsers');

const defaultProps = {
	browsers: [],
	devices: [],
	items: [],
	metricLabel: Liferay.Language.get('views')
};

const propTypes = {
	activeTab: PropTypes.string,
	browsers: PropTypes.array,
	devices: PropTypes.arrayOf(
		PropTypes.shape({
			data: PropTypes.array,
			id: PropTypes.string,
			label: PropTypes.string,
			percentageOfTotal: PropTypes.number,
			type: PropTypes.string
		})
	),
	items: PropTypes.array,
	metricLabel: PropTypes.string,
	onChange: PropTypes.func,
	total: PropTypes.number
};

const Tabs = ({
	activeTab,
	browsers,
	devices,
	items,
	metricLabel,
	onChange,
	total
}) => {
	/**
	 * Change Active Tab
	 * @param {object} activeTab
	 */
	const changeActiveTab = useCallback(
		newVal => onChange && onChange(newVal),
		[]
	);

	return (
		<div className='w-100'>
			<CardTabs
				activeTabId={activeTab}
				onChange={changeActiveTab}
				tabs={items.map(({label, ...otherParams}) => ({
					tabId: label,
					title: label,
					...otherParams
				}))}
			/>

			{activeTab === OPERATING_SYSTEM ? (
				<OperatingSystem devices={devices} metricLabel={metricLabel} />
			) : (
				<WebBrowser
					browsers={browsers}
					metricLabel={metricLabel}
					total={total}
				/>
			)}
		</div>
	);
};

Tabs.defaultProps = defaultProps;
Tabs.propTypes = propTypes;

/**
 * HOC
 * @description With Devices Card
 * @param {object} withDevices
 */
const withDevicesCard = (
	withDevices,
	{documentationTitle, documentationUrl, reportContainer, title} = {}
) => {
	const TabsWithDevices = compose(
		withDevices(),
		withLoading(),
		withError({page: false}),
		withEmpty({
			description: (
				<>
					<span className='mr-1'>
						{Liferay.Language.get(
							'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
						)}
					</span>

					<ClayLink
						href={documentationUrl}
						key='DOCUMENTATION'
						target='_blank'
					>
						{documentationTitle}
					</ClayLink>
				</>
			),
			title
		})
	)(Tabs);

	TabsWithDevices.propTypes = HOC_CARD_PROPTYPES;

	const defaultProps = {
		className: 'analytics-devices-card',
		metricLabel: Liferay.Language.get('views')
	};

	const propTypes = {
		metricLabel: PropTypes.string
	};

	const DevicesCard = ({
		className,
		label,
		legacyDropdownRangeKey,
		metricLabel
	}) => {
		const [activeTab, setActiveTab] = useState(OPERATING_SYSTEM);

		const handleActiveTabChange = useCallback(
			newVal => setActiveTab(newVal),
			[]
		);

		return (
			<BaseCard
				className={className}
				label={label}
				legacyDropdownRangeKey={legacyDropdownRangeKey}
				minHeight={536}
				reportContainer={reportContainer}
			>
				{({
					accountId,
					experienceId,
					filters,
					interval,
					rangeSelectors,
					router
				}) => (
					<Card.Body>
						<TabsWithDevices
							accountId={accountId}
							activeTab={activeTab}
							experienceId={experienceId}
							filters={filters}
							interval={interval}
							items={[
								{
									label: OPERATING_SYSTEM
								},
								{
									label: WEB_BROWSER
								}
							]}
							metricLabel={metricLabel}
							onChange={handleActiveTabChange}
							rangeSelectors={rangeSelectors}
							router={router}
						/>
					</Card.Body>
				)}
			</BaseCard>
		);
	};

	DevicesCard.defaultProps = defaultProps;
	DevicesCard.propTypes = propTypes;

	return DevicesCard;
};

export {withDevicesCard};
export default withDevicesCard;
