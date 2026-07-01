import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import CriteriaSidebarItem from './CriteriaSidebarItem';
import EmptyState from '@clayui/empty-state';
import React from 'react';
import {getDefaultValue} from './CriteriaSidebarCollapse';
import {List} from 'immutable';
import {Property} from 'shared/util/records';

export const filterPropertiesByLabel = (
	properties: List<Property>,
	searchValue: string
): List<Property> =>
	searchValue
		? (properties.filter((property) =>
				(property?.label ?? '')
					.toLowerCase()
					.includes(searchValue.toLowerCase())
			) as List<Property>)
		: properties;

interface IEmptyState {
	description: string;
	link?: {href: string; label: string};
	title: string;
}

export const renderProperties = (
	properties: List<Property>,
	searchValue = '',
	emptyState?: IEmptyState
) => {
	if (properties.isEmpty()) {
		const emptyStateProps: IEmptyState | undefined = searchValue
			? {
					description: Liferay.Language.get(
						'review-your-search-and-try-again'
					),
					title: Liferay.Language.get('no-results-found'),
				}
			: emptyState;

		if (emptyStateProps) {
			const {description, link, title} = emptyStateProps;

			return (
				<div className="align-items-center d-flex empty-message h-100 justify-content-center">
					<EmptyState
						className="text-center"
						description={description}
						title={title}
					>
						{link && (
							<ClayLink href={link.href} target="_blank">
								{link.label}

								<span className="inline-item inline-item-after">
									<ClayIcon fontSize={10} symbol="shortcut" />
								</span>
							</ClayLink>
						)}
					</EmptyState>
				</div>
			);
		}
	}

	return (
		<ul className="property-subgroups-list active">
			<li>
				<ul className="properties-list">
					{properties.toArray().map((property, i) => {
						const {label, name, propertyKey, type} = property;

						return (
							<CriteriaSidebarItem
								className={`color--${propertyKey}`}
								defaultValue={getDefaultValue(property)}
								key={`${name}-${i}`}
								label={label}
								name={name}
								property={property}
								propertyKey={propertyKey}
								type={type}
							/>
						);
					})}
				</ul>
			</li>
		</ul>
	);
};
