import Card from './Card';
import classNames from 'classnames';
import ClayLink from '@clayui/link';
import Icon from '@clayui/icon';
import Loading from './Loading';
import React from 'react';
import Sticker from '@clayui/sticker';
import {Text} from '@clayui/core';

export type DataDrivenConfig = {
	columnClass: string;
	items: {
		className: string;
		icon?: string;
		key: string;
	}[];
	title: string;
}[];

const InfoItem = ({
	className,
	href,
	icon,
	label,
	value,
}: {
	className?: string;
	href?: string;
	icon?: string;
	label: string;
	value: string;
}) => (
	<div className={classNames(className, 'align-items-start d-flex mb-2')}>
		<Sticker
			className="flex-shrink-0 mr-3"
			displayType="unstyled"
			size="lg"
		>
			{icon && <Icon className="text-secondary" symbol={icon} />}
		</Sticker>

		<div className="d-flex flex-column mt-1">
			<Text color="secondary" size={3}>
				{label}
			</Text>

			{href ? (
				<ClayLink
					className="font-weight-semi-bold text-break text-dark"
					href={href}
					style={{wordBreak: 'break-all'}}
				>
					{value}
				</ClayLink>
			) : (
				<span
					className="font-weight-semi-bold text-break text-dark"
					style={{wordBreak: 'break-all'}}
				>
					{value}
				</span>
			)}
		</div>
	</div>
);

interface IGeneralInfoSectionProps {
	config: DataDrivenConfig;
	getHref?: (key: string) => string | undefined;
	getValue: (key: string) => string | undefined;
	languageMap: Record<string, string>;
	loading?: boolean;
}

export const GeneralInfoSection: React.FC<IGeneralInfoSectionProps> = ({
	config,
	getHref,
	getValue,
	languageMap,
	loading,
}) => (
	<div className="general-info mb-4">
		<div className="row g-3">
			{config.map((section) => (
				<div className={section.columnClass} key={section.title}>
					<Card className="h-100 p-2">
						<Card.Title className="pt-2 px-2 text-uppercase">
							<Text size={4}>{section.title}</Text>
						</Card.Title>
						<Card.Body className="pb-0 px-2">
							{loading ? (
								<Loading />
							) : (
								<div className="g-2 row">
									{section.items.map((item) => {
										const rawValue = getValue(item.key);

										const displayValue = rawValue || '-';

										return (
											<InfoItem
												className={item.className}
												href={
													rawValue
														? getHref?.(item.key)
														: undefined
												}
												icon={item.icon}
												key={item.key}
												label={languageMap[item.key]}
												value={displayValue}
											/>
										);
									})}
								</div>
							)}
						</Card.Body>
					</Card>
				</div>
			))}
		</div>
	</div>
);
