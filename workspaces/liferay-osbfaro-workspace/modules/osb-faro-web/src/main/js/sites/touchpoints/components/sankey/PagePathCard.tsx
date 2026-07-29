import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import PagePathQuery from 'shared/queries/PagePathQuery';
import React, {useContext, useRef} from 'react';
import Sankey from './Sankey';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {EmptySankey} from './EmptySankey';
import {
	getSafeDecodedURIComponent,
	getSafeRangeSelectors,
	getSafeTouchpoint,
} from 'shared/util/util';
import {RangeSelectors} from 'shared/types';
import {SANKEY_WIDTH, SECONDARY_NODE_COLOR} from './utils';
import {TitleKey, Type} from './types';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';
import {useResize} from 'shared/hooks/useResize';
import {v4 as uuidv4} from 'uuid';

type pagePathNode = {
	external: boolean;
	views: number;
	canonicalUrl: string;
	title: TitleKey;
	followingPagePathNodes: pagePathNode[];
	previousPagePathNodes: pagePathNode[];
};

function getTitle(key: TitleKey, type: Type) {
	const langs = {
		[TitleKey.Direct]: Liferay.Language.get('direct-traffic'),
		[TitleKey.DropOffs]: Liferay.Language.get('drop-offs'),
		[TitleKey.Others]:
			type === Type.Previous
				? Liferay.Language.get('other-referrals')
				: Liferay.Language.get('other-pages'),
	};

	return langs[key] || key;
}

function getColor(key: TitleKey) {
	if (key === TitleKey.DropOffs || key === TitleKey.Others) {
		return SECONDARY_NODE_COLOR;
	}

	return null;
}

function formatData({pagePath}: {pagePath: pagePathNode}) {
	const formatNodes = (nodes: pagePathNode[], type: Type) =>
		nodes
			?.filter(({views}) => !!views)
			?.map(({canonicalUrl, external, title, views}) => ({
				color: getColor(title),
				external,
				id: uuidv4(),
				name: getTitle(title, type),
				type,
				url: canonicalUrl,
				views,
			}));

	const mainNode = {
		id: uuidv4(),
		main: true,
		name: pagePath.title,
		url: pagePath.canonicalUrl,
		views: pagePath.views,
	};

	const previousNodes = formatNodes(
		pagePath.previousPagePathNodes,
		Type.Previous
	);

	const followingNodes = formatNodes(
		pagePath.followingPagePathNodes,
		Type.Following
	);

	const links = [...previousNodes, ...followingNodes].map((link, index) => ({
		source: link.type === Type.Previous ? index + 1 : 0,
		target: link.type === Type.Previous ? 0 : index + 1,
		value: link.views,
	}));

	const nodes = [mainNode, ...previousNodes, ...followingNodes];

	return {
		links,
		nodes,
	};
}

interface IPagePathCardProps {
	rangeSelectors: RangeSelectors;
}

const PagePathCard: React.FC<IPagePathCardProps> = ({rangeSelectors}) => {
	const cardRef = useRef(null);
	const {accountId, segmentId} = useContext(BasePage.Context);
	const {channelId, title, touchpoint} = useParams<{
		channelId: string;
		title: string;
		touchpoint: string;
	}>();
	const {data, error, loading} = useQuery(PagePathQuery, {
		variables: {
			canonicalUrl: getSafeTouchpoint(touchpoint ?? ''),
			channelId,
			title: getSafeDecodedURIComponent(title ?? ''),
			...(accountId && {accountId}),
			...(segmentId && {segmentId}),
			...getSafeRangeSelectors(rangeSelectors),
		},
	});

	const formattedData = data ? formatData(data) : {links: [], nodes: []};
	const emptyState =
		!formattedData?.links.length && formattedData?.nodes.length === 1;

	const [width] = useResize(cardRef);

	const sankeyWidth = width - 60;

	return (
		<Card minHeight={600}>
			<Card.Header>
				<Card.Title>{Liferay.Language.get('path-analysis')}</Card.Title>
			</Card.Header>

			<div ref={cardRef}>
				<Card.Body className="d-flex align-items-center justify-content-center">
					<StatesRenderer
						empty={emptyState}
						error={!!error}
						loading={loading}
					>
						<StatesRenderer.Loading />

						<StatesRenderer.Error apolloError={error}>
							<ErrorDisplay />
						</StatesRenderer.Error>

						<StatesRenderer.Success>
							<Sankey
								data={formattedData}
								rangeSelectors={rangeSelectors}
								width={
									sankeyWidth > 0 ? sankeyWidth : SANKEY_WIDTH
								}
							/>
						</StatesRenderer.Success>

						<StatesRenderer.Empty>
							<>
								<EmptySankey
									data={formattedData}
									emptyState={emptyState}
									rangeSelectors={rangeSelectors}
								/>

								<NoResultsDisplay
									className="mt-4"
									description={
										<>
											{Liferay.Language.get(
												'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
											)}

											<ClayLink
												className="d-block mb-3"
												href={
													URLConstants.SitesDashboardPagesPath
												}
												key="DOCUMENTATION"
												target="_blank"
											>
												{Liferay.Language.get(
													'learn-more-about-path'
												)}
											</ClayLink>
										</>
									}
									flexGrow={false}
									title={Liferay.Language.get(
										'no-data-was-found'
									)}
								/>
							</>
						</StatesRenderer.Empty>
					</StatesRenderer>
				</Card.Body>
			</div>
		</Card>
	);
};

export default PagePathCard;
