import ClayButton from '@clayui/button';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {Sizes} from 'shared/util/constants';

interface IActivityStreamNoResultsProps {
	hasQuery: boolean;
	loading?: boolean;
	noData: React.ReactNode;
	onClearSearch: () => void;
}

/**
 * Empty state for the activity-stream session list. The loading and
 * search-with-no-matches states are identical across the account and
 * individual cards; only the no-data state (the `noData` node) differs per
 * page. Rendered through the timeline's `withEmpty` HOC, so it only shows when
 * there are no sessions.
 */
const ActivityStreamNoResults: React.FC<IActivityStreamNoResultsProps> = ({
	hasQuery,
	loading,
	noData,
	onClearSearch,
}) => {
	if (loading) {
		return (
			<NoResultsDisplay>
				<Loading key="LOADING" />
			</NoResultsDisplay>
		);
	}

	if (hasQuery) {
		return (
			<NoResultsDisplay
				description={Liferay.Language.get(
					'review-your-search-and-try-again'
				)}
				icon={{
					border: false,
					size: Sizes.XXXLarge,
					symbol: 'ac_no_results_found',
				}}
				spacer
				title={Liferay.Language.get('there-are-no-results-found')}
			>
				<ClayButton
					className="button-root"
					displayType="secondary"
					onClick={onClearSearch}
				>
					{Liferay.Language.get('clear-search')}
				</ClayButton>
			</NoResultsDisplay>
		);
	}

	return <>{noData}</>;
};

export default ActivityStreamNoResults;
