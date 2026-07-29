import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import HeaderDefault, {BaseCardHeaderDefaultIProps} from './HeaderDefault';
import React, {useContext, useState} from 'react';
import {INTERVAL_KEY_MAP} from 'shared/util/time';
import {RangeSelectors} from 'shared/types';
import {ReportContainer} from '../download-report/DownloadPDFReport';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

interface BaseCardIProps {
	className?: string;
	id?: string;
	children: (val: any) => React.ReactNode;
	description?: string;
	Header?: React.FC<BaseCardHeaderDefaultIProps>;
	headerProps?: {[key: string]: any};
	label: string;
	legacyDropdownRangeKey: boolean;
	minHeight?: number;
	reportContainer?: ReportContainer;
	showInterval?: boolean;
}

const BaseCard: React.FC<BaseCardIProps> = ({
	Header = HeaderDefault,
	children,
	className,
	description = '',
	headerProps = {},
	id,
	label,
	legacyDropdownRangeKey = true,
	minHeight,
	reportContainer,
	showInterval = false,
}) => {
	const context = useContext(BasePage.Context);

	const {
		accountId,
		experienceId,
		filters,
		rangeSelectors: contextRangeSelectors,
		router,
		segmentId,
	} = context;

	const [interval, setInterval] = useState(INTERVAL_KEY_MAP.day);

	const initialRangeSelectors = useQueryRangeSelectors();

	const [localRangeSelectors, setLocalRangeSelectors] =
		useState<RangeSelectors>(initialRangeSelectors);

	const currentRangeSelectors = contextRangeSelectors || localRangeSelectors;

	const isGlobal = !!contextRangeSelectors;

	const otherProps = {
		accountId,
		experienceId,
		filters,
		interval,
		onChangeInterval: setInterval,
		onRangeSelectorsChange: isGlobal ? undefined : setLocalRangeSelectors,
		rangeSelectors: currentRangeSelectors,
		router,
		segmentId,
	};

	return (
		<Card
			className={className}
			id={id}
			minHeight={minHeight}
			reportContainer={reportContainer}
		>
			<Header
				{...otherProps}
				{...headerProps}
				description={description}
				label={label}
				legacy={legacyDropdownRangeKey}
				showInterval={showInterval}
				showRangeKey={
					isGlobal ? false : headerProps.showRangeKey ?? true
				}
			/>

			{children({...otherProps})}
		</Card>
	);
};

export default BaseCard;
