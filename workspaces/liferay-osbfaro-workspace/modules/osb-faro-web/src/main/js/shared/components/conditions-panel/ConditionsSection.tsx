import getCN from 'classnames';
import React, {useId} from 'react';
import {Heading, Text} from '@clayui/core';

interface IConditionsSectionProps {
	action?: React.ReactNode;
	children?: React.ReactNode;
	className?: string;
	title: string;
}

const ConditionsSection = React.forwardRef<
	HTMLElement,
	IConditionsSectionProps
>(({action, children, className, title}, ref) => {
	const titleId = useId();

	return (
		<section
			aria-labelledby={titleId}
			className={getCN(className, 'px-4 py-3')}
			ref={ref}
			tabIndex={-1}
		>
			<div className="align-items-center d-flex justify-content-between">
				<Heading level={3} weight="normal">
					<Text id={titleId} size={4}>
						{title}
					</Text>
				</Heading>

				{action}
			</div>

			{children}
		</section>
	);
});

export default ConditionsSection;
