import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React from 'react';
import {
	EMPTY_NODE_COLOR,
	getFill,
	MAIN_NODE_HEIGHT,
	MAIN_NODE_WIDTH,
	URL_COLOR,
} from './utils';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {getUrl} from 'shared/util/urls';
import {Layer, Rectangle} from 'recharts';
import {Link, useParams} from 'react-router-dom';
import {pickBy} from 'lodash';
import {Routes} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {TitleKey, Type} from './types';
import {toThousands} from 'shared/util/numbers';

function truncateText(text: string, limit: number) {
	if (text.length > limit) {
		return `${text.substring(0, limit)}...`;
	}

	return text;
}

function getRadius(payload: {main: boolean; type: Type}) {
	if (payload.main) {
		return 0;
	}

	if (payload.type === Type.Previous) {
		return [5, 0, 0, 5];
	}

	return [0, 5, 5, 0];
}

function showURL(url?: TitleKey) {
	if (
		url &&
		url !== TitleKey.Direct &&
		url !== TitleKey.DropOffs &&
		url !== TitleKey.Others
	) {
		return true;
	}

	return false;
}

function normalizeNumber(number: number) {
	return isNaN(number) ? 0 : number;
}

const Title = ({title, x, y}: {title: string; x: number; y: number}) => (
	<text
		className="analytics-sankey-node-title"
		data-testid="sankey-node-title"
		fontSize="16"
		fontWeight={600}
		textAnchor="start"
		x={x}
		y={y}
	>
		{truncateText(title, 15)}
	</text>
);

export const Node = ({
	emptyState,
	height: initialHeight,
	hovered,
	index,
	onNodeChange = () => {},
	payload,
	rangeSelectors,
	selectedNode,
	width: initialWidth,
	x: initialX,
	y: initialY,
}: any) => {
	const height = normalizeNumber(initialHeight);
	const width = normalizeNumber(initialWidth);
	const x = normalizeNumber(initialX);
	const y = normalizeNumber(initialY);

	const {channelId, groupId} = useParams();

	return (
		<Layer
			crossOrigin={undefined}
			fr={undefined}
			key={`CustomNode${index}`}
			onMouseEnter={() => onNodeChange(payload.id)}
			onMouseLeave={() => onNodeChange(null)}
			path={undefined}
		>
			{emptyState ? (
				<>
					<rect
						fill={EMPTY_NODE_COLOR}
						height={MAIN_NODE_HEIGHT}
						width={MAIN_NODE_WIDTH}
						y={y}
					/>

					<text
						x={MAIN_NODE_WIDTH / 2 - 2}
						y={MAIN_NODE_HEIGHT / 2 + 60}
					>
						{toThousands(payload.value)}
					</text>
				</>
			) : (
				<>
					<Rectangle
						fill={getFill({
							hovered,
							index: index - 1,
							payload,
							selectedNode,
						})}
						fillOpacity="1"
						height={height}
						radius={getRadius(payload) as number}
						width={width}
						x={x}
						y={y}
					/>

					<text
						textAnchor="middle"
						x={x + width / 2}
						y={y + height / 2 + 5}
					>
						{toThousands(payload.value)}
					</text>
				</>
			)}

			{showURL(payload.url) ? (
				payload.external ? (
					<Title title={payload.name} x={x} y={y - 32} />
				) : (
					<Link
						data-tooltip-align="right"
						title={Liferay.Language.get('go-to-dashboard-page')}
						to={getUrl(Routes.SITES_TOUCHPOINTS_OVERVIEW, {
							params: {
								channelId,
								groupId,
								title: payload.name,
								touchpoint: payload.url,
							},
							query: {
								...pickBy(rangeSelectors),
							},
						})}
					>
						<Title title={payload.name} x={x} y={y - 32} />
					</Link>
				)
			) : (
				<Title title={payload.name} x={x} y={y - 12} />
			)}

			{showURL(payload.url) && (
				<>
					<ClayIcon
						className="icon-root text-secondary"
						height={16}
						symbol="shortcut"
						width={16}
						x={x}
						y={y - 22}
					/>

					<ClayLink
						data-tooltip-align="right"
						href={payload.url}
						target="_blank"
						title={
							sub(Liferay.Language.get('visit-x'), [
								getSafeDecodedURIComponent(payload.url),
							]) as string
						}
					>
						<text
							className="analytics-sankey-node-url"
							fill={URL_COLOR}
							fontSize="12"
							fontWeight={400}
							textAnchor="start"
							x={x + 20}
							y={y - 10}
						>
							{truncateText(
								getSafeDecodedURIComponent(payload.url),
								18
							)}
						</text>
					</ClayLink>
				</>
			)}
		</Layer>
	);
};
