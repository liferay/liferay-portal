import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import React from 'react';

interface ISelectDataSourceProps extends React.HTMLAttributes<HTMLDivElement> {
	sections: {
		dataSources: {
			iconName: string;
			iconSize: string;
			name: string;
			onClick?: () => void;
			subtitle: string;
			url?: string;
		}[];
		title: string;
	}[];
}

const SelectDataSource: React.FC<ISelectDataSourceProps> = ({
	className,
	sections = [],
}) => (
	<div className={getCN('select-data-source-root', className)}>
		{sections.map(({dataSources, title}) => (
			<section key={title}>
				<div className="h4 text-uppercase section-title">{title}</div>

				<div className="section-items">
					{dataSources.map(
						({iconName, iconSize, name, onClick, subtitle, url}) =>
							url ? (
								<ClayLink
									button
									className="button-root data-source-item"
									displayType="unstyled"
									href={url}
									key={name}
								>
									<div className="image">
										<ClayIcon
											className="icon-root icon-size-xxxl"
											symbol={iconName}
										/>
									</div>

									<div className="details">
										<div className="title">
											<div
												className={getCN('h4', {
													'm-0': !subtitle,
												})}
											>
												{name}
											</div>
										</div>

										{subtitle && (
											<div className="subtitle">
												{subtitle}
											</div>
										)}
									</div>
								</ClayLink>
							) : (
								<ClayButton
									className="button-root data-source-item"
									displayType="unstyled"
									key={name}
									onClick={onClick}
								>
									<div className="image">
										<ClayIcon
											className={`icon-root icon-size-${iconSize}`}
											symbol={iconName}
										/>
									</div>

									<div className="details">
										<div className="title">
											<div
												className={getCN('h4', {
													'm-0': !subtitle,
												})}
											>
												{name}
											</div>
										</div>

										{subtitle && (
											<div className="subtitle">
												{subtitle}
											</div>
										)}
									</div>
								</ClayButton>
							)
					)}
				</div>
			</section>
		))}
	</div>
);

export default SelectDataSource;
