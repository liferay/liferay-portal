import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React, {createRef} from 'react';
import ScrollableSection from 'shared/components/ScrollableSection';
import {DistributionTab} from 'shared/util/records';
import {List} from 'immutable';

interface ITabItemProps {
	active: boolean;
	disabled: boolean;
	id: string;
	onDelete: (id: string) => void;
	onSelect: (id: string) => void;
	title: string;
}

export type ITab = {
	context: string;
	id: string;
	numberOfBins: number;
	propertyId: string;
	propertyType: string;
	title: string;
};

const TabItem: React.FC<ITabItemProps> = ({
	active,
	disabled,
	id,
	onDelete,
	onSelect,
	title,
}) => (
	<li className={getCN('tab-item d-flex align-items-baseline', {active})}>
		{!active && (
			<ClayButton
				className="button-root tab-title"
				disabled={disabled}
				displayType="unstyled"
				onClick={() => onSelect(id)}
			>
				{title}
			</ClayButton>
		)}

		{active && (
			<div className="tab-title active">
				{title}

				<ClayButton
					aria-label={Liferay.Language.get('close')}
					className="button-root remove-tab"
					displayType="unstyled"
					onClick={() => onDelete(id)}
					size="sm"
				>
					<ClayIcon
						className="icon-root icon-size-sm"
						symbol="times"
					/>
				</ClayButton>
			</div>
		)}
	</li>
);

interface ITabsProps {
	itemsIList: List<DistributionTab>;
	onAdd: () => void;
	onDelete: (id: string) => void;
	onSelect: (id: string) => void;
	selectedTabIndex: number;
	showAddProperty: boolean;
}

export default class Tabs extends React.Component<ITabsProps> {
	static defaultProps = {
		itemsIList: List(),
	};

	componentDidUpdate(prevProps: ITabsProps) {
		const {itemsIList} = this.props;

		const count = itemsIList.size;
		const prevCount = prevProps.itemsIList.size;

		if (this._scrollableSectionRef.current && prevCount) {
			const {scrollToBeg, scrollToEnd} =
				this._scrollableSectionRef.current;

			if (count > prevCount) {
				scrollToEnd();
			}
			else if (count < prevCount) {
				scrollToBeg();
			}
		}
	}

	private _scrollableSectionRef = createRef<ScrollableSection>();

	render() {
		const {
			itemsIList,
			onAdd,
			onDelete,
			onSelect,
			selectedTabIndex,
			showAddProperty,
		} = this.props;

		return (
			<div className="tabs-root d-flex align-items-center justify-content-between">
				<ScrollableSection ref={this._scrollableSectionRef}>
					<ul className="d-flex">
						{itemsIList.toArray().map(({id, title}, i) => (
							<TabItem
								active={
									selectedTabIndex === i && !showAddProperty
								}
								disabled={showAddProperty}
								id={id}
								key={title}
								onDelete={onDelete}
								onSelect={onSelect}
								title={title}
							/>
						))}
					</ul>
				</ScrollableSection>

				<ClayButton
					aria-label={Liferay.Language.get('add')}
					borderless
					className={getCN('button-root add-tab', {
						active: showAddProperty,
					})}
					displayType="secondary"
					monospaced
					onClick={onAdd}
					size="sm"
				>
					<ClayIcon symbol="plus" />
				</ClayButton>
			</div>
		);
	}
}
