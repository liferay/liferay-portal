import BasePage from 'shared/components/base-page';
import ClayLayout from '@clayui/layout';
import React from 'react';
import TopAssets from './TopAssets';
import TopCategoriesAndTags from './TopCategoriesAndTags';
import TopPagesCard from './TopPagesCard';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useQueryParams} from 'shared/hooks/useQueryParams';

interface IEngagementSummaryProps {
	channelId: string;
	children?: React.ReactNode;
	groupId: string;
	individualId: string;
	individualName?: string;
	loading?: boolean;
	showEmptyState?: boolean;
}

/**
 * Mirrors the Engagement Summary section of the account Overview tab, scoped
 * to a single individual. The context is provided here, and not around the
 * whole profile tab, so that the cards already on the tab keep reading the
 * default context.
 */

const EngagementSummary: React.FC<IEngagementSummaryProps> = ({
	channelId,
	children: emptyState,
	groupId,
	individualId,
	individualName,
	loading,
	showEmptyState,
}) => {
	const query = useQueryParams();

	const sectionContent = (
		<BasePage.Context.Provider
			value={{
				filters: {},
				individualId,
				individualName,
				router: {
					params: {channelId, groupId, id: individualId},
					query,
				},
			}}
		>
			<ClayLayout.Row>
				<ClayLayout.Col size={12}>
					<TopPagesCard className="top-pages-card-root" />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col className="d-flex flex-column" size={12} xl={6}>
					<TopAssets
						className="flex-grow-1"
						individualId={individualId}
						individualName={individualName}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="d-flex flex-column" size={12} xl={6}>
					<TopCategoriesAndTags
						className="flex-grow-1"
						individualId={individualId}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</BasePage.Context.Provider>
	);

	return (
		<>
			<SectionHeader
				className="mb-3 mt-2"
				icon="display-content"
				title={Liferay.Language.get('engagement-summary')}
			/>

			{showEmptyState && !loading ? emptyState : sectionContent}
		</>
	);
};

export default EngagementSummary;
