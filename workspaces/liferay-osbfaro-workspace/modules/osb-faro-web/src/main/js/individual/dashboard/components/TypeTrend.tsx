import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import InfoPopover from 'shared/components/InfoPopover';
import React from 'react';
import {isFinite} from 'lodash';
import {sub} from 'shared/util/lang';
import {formatPercent, toLocale} from 'shared/util/numbers';

interface ITrendItemProps {
	change: number;
	data: number[];
	id: string;
	info?: {content: string; title: string};
	title: string;
	total: number;
}

export const TrendItem: React.FC<ITrendItemProps> = ({
	change,
	info,
	title,
	total,
}) => {
	const finiteChange = isFinite(change);

	return (
		<div className="trend-item-root" key={title}>
			<div className="trend-item-title d-flex justify-content-between">
				<div className="card-title">{title}</div>

				{info && <InfoPopover {...info} />}
			</div>

			<div className="d-flex align-items-center flex-grow-1 justify-content-center">
				<div className="total">{toLocale(total)}</div>
			</div>

			{!!total && (
				<div className="change description">
					{sub(
						Liferay.Language.get('x-vs-last-30-days'),
						[
							<span
								className={getCN({
									decrease: change < 0 && finiteChange,
									increase: change > 0 && finiteChange,
								})}
								key="CHANGE"
							>
								{finiteChange && !!change && (
									<ClayIcon
										className="icon-root"
										symbol={
											change > 0
												? 'caret-top'
												: 'caret-bottom'
										}
									/>
								)}

								<b>
									{finiteChange
										? `${change > 0 ? '+' : ''}${formatPercent(
												change
											)}`
										: '--'}
								</b>
							</span>,
						],
						false
					)}
				</div>
			)}
		</div>
	);
};

const TypeTrend: React.FC<{items: ITrendItemProps[]}> = ({items}) => (
	<div className="type-trend-root">
		{items.map((item, i) => (
			<TrendItem {...item} key={i} />
		))}
	</div>
);

TypeTrend.defaultProps = {
	items: [],
};

export default TypeTrend;
