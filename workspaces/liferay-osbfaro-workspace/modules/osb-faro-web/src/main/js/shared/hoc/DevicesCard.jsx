import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import ClayTabs from '@clayui/tabs';
import OperatingSystem from 'shared/components/OperatingSystem';
import React, {useCallback, useState} from 'react';
import WebBrowser from 'shared/components/WebBrowser';
import {compose} from 'redux';
import {HOC_CARD_PROPTYPES} from 'shared/util/proptypes';
import {PropTypes} from 'prop-types';
import {withEmpty, withError, withLoading} from 'shared/hoc';

const OPERATING_SYSTEM = Liferay.Language.get('devices');
const WEB_BROWSER = Liferay.Language.get('browsers');

const TAB_LABELS = [OPERATING_SYSTEM, WEB_BROWSER];

const defaultProps = {
	browsers: [],
	devices: [],
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
	metricLabel: PropTypes.string,
	total: PropTypes.number
};

const TabContent = ({activeTab, browsers, devices, metricLabel, total}) =>
	activeTab === OPERATING_SYSTEM ? (
		<OperatingSystem devices={devices} metricLabel={metricLabel} />
	) : (
		<WebBrowser
			browsers={browsers}
			metricLabel={metricLabel}
			total={total}
		/>
	);

TabContent.defaultProps = defaultProps;
TabContent.propTypes = propTypes;

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
	)(TabContent);

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
			index => setActiveTab(TAB_LABELS[Number(index)]),
			[]
		);

		const activeIndex = Math.max(TAB_LABELS.indexOf(activeTab), 0);

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
					<Card.Body
						className='w-100 d-flex flex-column flex-grow-1'
						noPadding
					>
						<ClayTabs
							active={activeIndex}
							className='mb-3'
							onActiveChange={handleActiveTabChange}
						>
							{TAB_LABELS.map(tabLabel => (
								<ClayTabs.Item key={tabLabel}>
									{tabLabel}
								</ClayTabs.Item>
							))}
						</ClayTabs>

						<div className='px-4 pb-4'>
							<TabsWithDevices
								accountId={accountId}
								activeTab={activeTab}
								experienceId={experienceId}
								filters={filters}
								interval={interval}
								metricLabel={metricLabel}
								rangeSelectors={rangeSelectors}
								router={router}
							/>
						</div>
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
