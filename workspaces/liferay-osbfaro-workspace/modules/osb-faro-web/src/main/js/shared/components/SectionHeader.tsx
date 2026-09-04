import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React, {ReactNode} from 'react';
import {Text} from '@clayui/core';

interface ISectionHeader {
	className?: string;
	icon: string;
	rightContent?: ReactNode;
	title: string;
}

const SectionHeader: React.FC<ISectionHeader> = ({
	className = 'mb-3',
	icon,
	rightContent,
	title,
}) => {
	const heading = (
		<>
			<span className="mr-2">
				<Text color="secondary" size={4}>
					<ClayIcon symbol={icon} />
				</Text>
			</span>

			<Text color="secondary" size={4} weight="semi-bold">
				{title.toUpperCase()}
			</Text>
		</>
	);

	// Only a header carrying right content becomes a flex row with the heading
	// in a wrapper of its own. Without it the markup stays exactly as it was,
	// so none of the existing callers shift.

	if (!rightContent) {
		return <div className={className}>{heading}</div>;
	}

	return (
		<div
			className={getCN(
				className,
				'align-items-center d-flex justify-content-between'
			)}
		>
			<div>{heading}</div>

			<div>{rightContent}</div>
		</div>
	);
};

export {SectionHeader};
